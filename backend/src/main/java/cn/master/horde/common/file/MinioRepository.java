package cn.master.horde.common.file;

import cn.master.horde.common.result.BizException;
import io.minio.*;
import io.minio.messages.Item;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
@Component
public class MinioRepository implements FileRepository {
    // 缓冲区大小
    private static final int BUFFER_SIZE = 8192;
    public static final String BUCKET = "warlock";
    public static final String ENDPOINT = "endpoint";
    public static final String ACCESS_KEY = "accessKey";
    public static final String SECRET_KEY = "secretKey";
    private MinioClient client;

    public void init(MinioClient client) {
        if (this.client == null) {
            this.client = client;
        }
    }

    // public void init(Map<String, Object> minioConfig) {
    //     if (minioConfig == null || minioConfig.isEmpty()) {
    //         LogUtils.info("MinIO初始化失败，参数[minioConfig]为空");
    //         return;
    //     }
    //
    //     try {
    //         String endpoint = minioConfig.get(ENDPOINT).toString();
    //         String accessKey = minioConfig.get(ACCESS_KEY).toString();
    //         String secretKey = minioConfig.get(SECRET_KEY).toString();
    //
    //         LogUtils.info("开始初始化 MinIO Repository: endpoint={}", endpoint);
    //
    //         if (ObjectUtils.isNotEmpty(endpoint)) {
    //             // 创建 MinioClient 客户端
    //             client = MinioClient.builder()
    //                     .endpoint(endpoint)
    //                     .credentials(accessKey, secretKey)
    //                     .build();
    //
    //             // 测试连接
    //             LogUtils.info("测试 MinIO Repository 连接...");
    //             List<Bucket> buckets = client.listBuckets();
    //             LogUtils.info("MinIO Repository 连接成功，存储桶数量: {}", buckets.size());
    //
    //             // 检查并创建存储桶
    //             boolean exist = client.bucketExists(BucketExistsArgs.builder().bucket(BUCKET).build());
    //             LogUtils.info("存储桶 {} 是否存在: {}", BUCKET, exist);
    //
    //             if (!exist) {
    //                 LogUtils.info("创建存储桶: {}", BUCKET);
    //                 client.makeBucket(MakeBucketArgs.builder().bucket(BUCKET).build());
    //                 LogUtils.info("存储桶创建成功: {}", BUCKET);
    //             }
    //         }
    //     } catch (Exception e) {
    //         LogUtils.info("MinIOClient初始化失败！endpoint={}, error={}", minioConfig.get(ENDPOINT), e.getMessage(), e);
    //     }
    // }

    boolean bucketExists(String bucketName) throws Exception {
        return client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    void makeBucket(String bucketName) throws Exception {
        client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
    }

    @Override
    public String saveFile(MultipartFile file, FileRequest request) throws Exception {
        // 文件存储路径
        String filePath = getPath(request);
        client.putObject(PutObjectArgs.builder()
                .bucket(BUCKET)
                .object(filePath)
                .stream(file.getInputStream(), file.getSize(), -1) // 文件内容
                .build());
        return filePath;
    }

    private String getPath(FileRequest request) {
        String folder = request.getFolder();
        if (!Strings.CS.startsWithAny(folder, "system", "project", "organization")) {
            throw new BizException("folder.error");
        }
        return StringUtils.join(folder, File.separator, request.getFileName());
    }

    @Override
    public String saveFile(byte[] bytes, FileRequest request) throws Exception {
        String filePath = getPath(request);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            client.putObject(PutObjectArgs.builder()
                    .bucket(BUCKET)
                    .object(filePath)
                    .stream(inputStream, bytes.length, -1)
                    .build());
        }
        return request.getFileName();
    }

    @Override
    public String saveFile(InputStream inputStream, FileRequest request) throws Exception {
        String filePath = getPath(request);
        client.putObject(PutObjectArgs.builder()
                .bucket(BUCKET)
                .object(filePath)
                .stream(inputStream, -1, 5242880) // 文件内容
                .build());
        return filePath;
    }

    @Override
    public void delete(FileRequest request) throws Exception {
        String filePath = getPath(request);
        // 删除单个文件
        removeObject(BUCKET, filePath);
    }

    private void removeObject(String bucketName, String objectName) throws Exception {
        client.removeObject(RemoveObjectArgs.builder()
                .bucket(bucketName) // 存储桶
                .object(objectName) // 文件名
                .build());
    }

    @Override
    public void deleteFolder(FileRequest request) throws Exception {
        String filePath = getPath(request);
        // 删除文件夹
        removeObjects(BUCKET, filePath);
    }

    private void removeObjects(String bucketName, String objectName) throws Exception {
        List<String> objects = listObjects(bucketName, objectName);
        for (String object : objects) {
            removeObject(bucketName, object);
        }
    }

    @Override
    public byte[] getFile(FileRequest request) throws Exception {
        return getFileAsStream(request).readAllBytes();
    }

    @Override
    public InputStream getFileAsStream(FileRequest request) throws Exception {
        String fileName = getPath(request);
        return client.getObject(GetObjectArgs.builder()
                .bucket(BUCKET) // 存储桶
                .object(fileName) // 文件名
                .build());
    }

    @Override
    public void downloadFile(FileRequest request, String fullPath) throws Exception {
        String fileName = getPath(request);
        // 下载对象到本地文件
        try (InputStream inputStream = client.getObject(
                GetObjectArgs.builder()
                        .bucket(BUCKET)
                        .object(fileName)
                        .build());
             BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fullPath))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    @Override
    public List<String> getFolderFileNames(FileRequest request) throws Exception {
        return listObjects(BUCKET, getPath(request));
    }

    private List<String> listObjects(String bucketName, String objectName) throws Exception {
        List<String> list = new ArrayList<>(12);
        Iterable<Result<Item>> results = client.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(objectName)
                        .build());
        for (Result<Item> result : results) {
            Item item = result.get();
            if (item.isDir()) {
                List<String> files = listObjects(bucketName, item.objectName());
                list.addAll(files);
            } else {
                list.add(item.objectName());
            }
        }
        return list;
    }

    @Override
    public void copyFile(FileCopyRequest request) throws Exception {
        String sourcePath = StringUtils.join(request.getCopyFolder(), File.separator, request.getCopyfileName());
        String targetPath = getPath(request);
        client.copyObject(CopyObjectArgs.builder()
                .bucket(BUCKET)
                .object(targetPath)
                .source(CopySource.builder()
                        .bucket(BUCKET)
                        .object(sourcePath)
                        .build())
                .build());
    }

    @Override
    public long getFileSize(FileRequest request) throws Exception {
        String fileName = getPath(request);
        return client.statObject(StatObjectArgs.builder()
                .bucket(BUCKET) // 存储桶
                .object(fileName) // 文件名
                .build()).size();
    }
}
