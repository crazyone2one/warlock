package cn.master.horde.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
@Data
public class FileModuleRepositoryDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String fileModuleId;

    private String platform;

    private String url;

    private String token;

    private String userName;
}
