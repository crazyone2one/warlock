package cn.master.horde.common.file;

import cn.master.horde.model.dto.FileMetadataRepositoryDTO;
import cn.master.horde.model.dto.FileModuleRepositoryDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
@Data
@NoArgsConstructor
public class FileRequest {
    private String folder;

    // 存储类型
    private String storage;

    // 文件名称
    private String fileName;
    private GitFileRequest gitFileRequest;

    public void setGitFileRequest(FileModuleRepositoryDTO repository, FileMetadataRepositoryDTO file) {
        gitFileRequest = new GitFileRequest(repository.getUrl(), repository.getToken(), repository.getUserName(), file.getBranch(), file.getCommitId());
    }

    public FileRequest(String folder, String storage, String fileName) {
        this.folder = folder;
        this.storage = storage;
        this.fileName = fileName;
    }
}

@Data
@AllArgsConstructor
class GitFileRequest {
    private String url;
    private String token;
    private String userName;
    private String branch;
    private String commitId;
}