package cn.master.horde.controller;

import cn.master.horde.common.constants.Created;
import cn.master.horde.common.service.SessionUtils;
import cn.master.horde.dto.UserExcludeOptionDTO;
import cn.master.horde.dto.UserRoleRelationUserDTO;
import cn.master.horde.dto.request.UserRoleRelationQueryRequest;
import cn.master.horde.dto.request.UserRoleRelationUpdateRequest;
import cn.master.horde.entity.UserRoleRelation;
import cn.master.horde.service.UserRoleRelationService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户-角色关联表 控制层。
 *
 * @author 11's papa
 * @since 2026-01-14
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/role/relation")
public class UserRoleRelationController {

    private final UserRoleRelationService userRoleRelationService;

    @PostMapping("save")
    @PreAuthorize("hasPermission('SYSTEM_USER_ROLE','READ+UPDATE')")
    @Operation(summary = "系统设置-系统-用户组-用户关联关系-创建全局用户组和用户的关联关系")
    public void save(@Validated({Created.class}) @RequestBody UserRoleRelationUpdateRequest request) {
        request.setCreateUser(SessionUtils.getCurrentUsername());
        userRoleRelationService.add(request);
    }

    @GetMapping("remove/{id}")
    @PreAuthorize("hasPermission('SYSTEM_USER_ROLE','READ+UPDATE')")
    @Operation(summary = "系统设置-系统-用户组-用户关联关系-删除全局用户组和用户的关联关系")
    public void remove(@PathVariable String id) {
        userRoleRelationService.delete(id);
    }

    /**
     * 根据主键更新用户-角色关联表。
     *
     * @param userRoleRelation 用户-角色关联表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody UserRoleRelation userRoleRelation) {
        return userRoleRelationService.updateById(userRoleRelation);
    }

    /**
     * 查询所有用户-角色关联表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<UserRoleRelation> list() {
        return userRoleRelationService.list();
    }

    /**
     * 根据主键获取用户-角色关联表。
     *
     * @param id 用户-角色关联表主键
     * @return 用户-角色关联表详情
     */
    @GetMapping("getInfo/{id}")
    public UserRoleRelation getInfo(@PathVariable String id) {
        return userRoleRelationService.getById(id);
    }

    @PostMapping("page")
    @PreAuthorize("hasPermission('SYSTEM_USER_ROLE','READ')")
    @Operation(summary = "系统设置-系统-用户组-用户关联关系-获取全局用户组对应的用户列表")
    public Page<UserRoleRelationUserDTO> page(@Validated @RequestBody UserRoleRelationQueryRequest request) {
        return userRoleRelationService.pageDTO(request);
    }

    @GetMapping("/user/option/{roleId}")
    @Operation(summary = "系统设置-系统-用户组-用户关联关系-获取需要关联的用户选项")
    @PreAuthorize("hasPermission('SYSTEM_USER_ROLE','READ')")
    public List<UserExcludeOptionDTO> getSelectOption(@Schema(description = "用户组ID", requiredMode = Schema.RequiredMode.REQUIRED)
                                                      @PathVariable String roleId,
                                                      @Schema(description = "查询关键字，根据邮箱和用户名查询", requiredMode = Schema.RequiredMode.REQUIRED)
                                                      @RequestParam(value = "keyword", required = false) String keyword) {
        return userRoleRelationService.getExcludeSelectOption(roleId, keyword);
    }
}
