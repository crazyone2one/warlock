package cn.master.horde.dto;

import lombok.Data;

import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/1/23, 星期五
 **/
@Data
public class PermissionCache {
    private List<PermissionDefinitionItem> permissionDefinition;
}
