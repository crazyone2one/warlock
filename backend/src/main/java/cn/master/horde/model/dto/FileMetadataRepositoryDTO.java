package cn.master.horde.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
@Data
public class FileMetadataRepositoryDTO implements Serializable {
    private String fileMetadataId;
    private String branch;
    private String commitId;

    private String commitMessage;

    @Serial
    private static final long serialVersionUID = 1L;
}
