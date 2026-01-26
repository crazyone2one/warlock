package cn.master.horde.dto.permission;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : 11's papa
 * @since : 2026/1/26, 星期一
 **/
@Data
public class UserRoleResource implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
}
