package cn.master.horde.controller;

import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import cn.master.horde.entity.UserRoleRelation;
import cn.master.horde.service.UserRoleRelationService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 用户-角色关联表 控制层。
 *
 * @author 11's papa
 * @since 2026-01-14
 */
@RestController
@RequestMapping("/userRoleRelation")
public class UserRoleRelationController {

    @Autowired
    private UserRoleRelationService userRoleRelationService;

    /**
     * 保存用户-角色关联表。
     *
     * @param userRoleRelation 用户-角色关联表
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody UserRoleRelation userRoleRelation) {
        return userRoleRelationService.save(userRoleRelation);
    }

    /**
     * 根据主键删除用户-角色关联表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable String id) {
        return userRoleRelationService.removeById(id);
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

    /**
     * 分页查询用户-角色关联表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<UserRoleRelation> page(Page<UserRoleRelation> page) {
        return userRoleRelationService.page(page);
    }

}
