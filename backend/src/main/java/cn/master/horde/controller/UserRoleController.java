package cn.master.horde.controller;

import cn.master.horde.common.log.annotation.Loggable;
import cn.master.horde.common.constants.Created;
import cn.master.horde.common.constants.Updated;
import cn.master.horde.model.dto.permission.PermissionDefinitionItem;
import cn.master.horde.model.dto.request.PermissionSettingUpdateRequest;
import cn.master.horde.model.dto.request.UserRoleUpdateRequest;
import cn.master.horde.model.entity.UserRole;
import cn.master.horde.service.UserRoleService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户角色表 控制层。
 *
 * @author 11's papa
 * @since 2026-01-14
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/role")
public class UserRoleController {

    private final UserRoleService userRoleService;

    @PostMapping("save")
    @Loggable("保存自定义用户组")
    @PreAuthorize("hasPermission('SYSTEM_USER_ROLE','READ+ADD')")
    public UserRole save(@Validated({Created.class}) @RequestBody UserRoleUpdateRequest request) {
        return userRoleService.add(request);
    }

    @GetMapping("remove/{id}")
    @Operation(summary = "删除自定义全用户组")
    @PreAuthorize("hasPermission('SYSTEM_USER_ROLE','READ+DELETE')")
    public void remove(@PathVariable String id) {
        userRoleService.delete(id);
    }

    @PostMapping("update")
    @Loggable("更新自定义全局用户组")
    @PreAuthorize("hasPermission('SYSTEM_USER_ROLE','READ+UPDATE')")
    @Operation(summary = "更新自定义用户组")
    public UserRole update(@Validated({Updated.class}) @RequestBody UserRoleUpdateRequest request) {
        return userRoleService.updateUserRole(request);
    }

    @GetMapping("list")
    @Operation(summary = "查询所有用户角色表")
    @PreAuthorize("hasPermission('SYSTEM_USER_ROLE','READ')")
    public List<UserRole> list() {
        return userRoleService.list();
    }

    @GetMapping("getInfo/{id}")
    @Operation(summary = "根据主键获取用户角色表")
    @PreAuthorize("hasPermission('SYSTEM_USER_ROLE','READ')")
    public UserRole getInfo(@PathVariable String id) {
        return userRoleService.getById(id);
    }

    /**
     * 分页查询用户角色表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @PreAuthorize("hasPermission('SYSTEM_USER_ROLE','READ')")
    public Page<UserRole> page(Page<UserRole> page) {
        return userRoleService.page(page);
    }

    @GetMapping("/permission/setting/{id}")
    @Operation(summary = "获取用户组对应的权限配置")
    @PreAuthorize("hasPermission('SYSTEM_USER_ROLE','READ')")
    public List<PermissionDefinitionItem> getPermissionSetting(@PathVariable String id) {
        return userRoleService.getPermissionSetting(id);
    }

    @Loggable("更新用户角色权限配置")
    @PostMapping("/permission/update")
    @Operation(summary = "更新用户组对应的权限配置")
    @PreAuthorize("hasPermission('SYSTEM_USER_ROLE','READ+UPDATE')")
    public void updatePermissionSetting(@Validated @RequestBody PermissionSettingUpdateRequest request) {
        userRoleService.updatePermissionSetting(request);
    }
}
