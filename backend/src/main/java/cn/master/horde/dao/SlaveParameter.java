package cn.master.horde.dao;

import lombok.Data;

/**
 * @author : 11's papa
 * @since : 2026/1/15, 星期四
 **/
@Data
public class SlaveParameter {
    private String host;
    private Integer port;
    private String username;
    private String password;
    private String localPath;
    private String remotePath;
}
