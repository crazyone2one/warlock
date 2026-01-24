package cn.master.horde.util;

import cn.master.horde.dto.SlaveParameter;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/1/15, 星期四
 **/
@Slf4j
@NullMarked
@Component
public class FileHelper {
    @Value("${spring.profiles.active:default}")
    private String activeProfile;
    private final ScpUtils scpUtils;

    public FileHelper(ScpUtils scpUtils) {
        this.scpUtils = scpUtils;
    }

    /**
     * 构建完整的文件路径
     *
     * @param filePath      基础文件路径
     * @param projectNum    项目编号
     * @param directoryName 目录名称
     * @param fileName      文件名
     * @return 组合后的完整文件路径
     */
    public String filePath(String filePath, String projectNum, String directoryName, String fileName) {
        if (filePath.endsWith(File.separator)) {
            return filePath + projectNum + File.separator + directoryName + File.separator + fileName;
        }
        return filePath + File.separator + projectNum + File.separator + directoryName + File.separator + fileName;
    }

    /**
     * 上传文件到远程服务器
     *
     * @param slaveConfig 从节点配置参数，包含远程服务器连接信息
     * @param localPath   本地文件路径
     * @param targetPath  目标服务器上的目标路径
     */
    public void uploadFile(SlaveParameter slaveConfig, String localPath, String targetPath) {
        if (activeProfile.equals("dev")) {
            return;
        }
        File file = new File(localPath);
        if (!file.exists()) {
            log.warn("file not exists: {}", localPath);
            return;
        }
        try {
            // 上传文件
            scpUtils.uploadFile(slaveConfig, localPath, targetPath);
            log.info("file transfer successfully");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            scpUtils.close();
        }
    }

    /**
     * 根据指定路径和内容生成文件
     * 在开发环境下仅输出内容到日志而不创建文件
     *
     * @param filePath 文件的完整路径
     * @param content  要写入文件的内容
     */
    public void generateFile(String filePath, String content) {
        if (activeProfile.equals("dev")) {
            log.info("{}", content);
            return;
        }
        try {
            File file = new File(filePath);
            // 检查父目录是否存在，不存在则创建
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean dirCreated = parentDir.mkdirs();
                if (!dirCreated) {
                    log.error("Failed to create directory: {}", parentDir.getAbsolutePath());
                    return;
                }
            }
            if (!file.exists()) {
                boolean newFile = file.createNewFile();
                if (!newFile) {
                    log.error("Failed to create file: {}", filePath);
                    return;
                }
            }
            try (FileWriter fw = new FileWriter(filePath)) {
                fw.write(content);
                log.info("{} created successfully", file.getName());
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    /**
     * 删除指定根目录下所有叶子目录中的文件
     * 叶子目录是指不包含任何子目录的目录，该方法会删除这些叶子目录中的所有文件，但保留目录结构本身
     *
     * @param rootPath 根目录路径，必须是一个存在的目录
     * @throws IOException 当文件系统操作发生错误时抛出
     */
    public void deleteFilesInLeafDirectories(String rootPath) throws IOException {
        Path root = Paths.get(rootPath);
        if (!Files.exists(root) || !Files.isDirectory(root)) {
            throw new IllegalArgumentException("路径不存在或不是目录: " + rootPath);
        }
        // 收集所有叶子目录（即不包含任何子目录的目录）
        List<Path> leafDirs = new ArrayList<>();
        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            private boolean hasSubDir = false;

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                // 重置标志
                hasSubDir = false;
                // 检查当前目录是否有子目录
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                    for (Path entry : stream) {
                        if (Files.isDirectory(entry)) {
                            hasSubDir = true;
                            break;
                        }
                    }
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                // 文件不影响是否为叶子目录
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                // 如果该目录没有子目录，则是叶子目录
                if (!hasSubDir) {
                    leafDirs.add(dir);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                System.err.println("访问文件失败: " + file);
                return FileVisitResult.CONTINUE;
            }
        });
        // 删除每个叶子目录中的文件（不删目录）
        for (Path leafDir : leafDirs) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(leafDir)) {
                for (Path file : stream) {
                    if (Files.isRegularFile(file)) {
                        log.info("  删除文件: {}", file);
                        Files.delete(file);
                    }
                    // 忽略子目录（理论上叶子目录不应有子目录，但双重保险）
                }
            }
        }
    }
}
