package cn.master.horde.controller;

import cn.master.horde.core.security.CustomUserDetails;
import cn.master.horde.dto.UserDTO;
import cn.master.horde.entity.SystemUser;
import cn.master.horde.service.SystemUserService;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户 控制层。
 *
 * @author 11's papa
 * @since 2026-01-14
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/system-user")
public class SystemUserController {

    private final SystemUserService systemUserService;

    /**
     * 保存用户。
     *
     * @param systemUser 用户
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody SystemUser systemUser) {
        return systemUserService.save(systemUser);
    }

    /**
     * 根据主键删除用户。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable String id) {
        return systemUserService.removeById(id);
    }

    /**
     * 根据主键更新用户。
     *
     * @param systemUser 用户
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody SystemUser systemUser) {
        return systemUserService.updateById(systemUser);
    }

    /**
     * 查询所有用户。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<SystemUser> list() {
        return systemUserService.list();
    }

    /**
     * 根据主键获取用户。
     *
     * @param id 用户主键
     * @return 用户详情
     */
    @GetMapping("getInfo/{id}")
    public SystemUser getInfo(@PathVariable String id) {
        return systemUserService.getById(id);
    }

    @GetMapping("get-user-info")
    public UserDTO getInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        assert customUserDetails != null;
        String userId = customUserDetails.getUserId();
        return systemUserService.getUserInfoById(userId);
    }

    /**
     * 分页查询用户。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<SystemUser> page(Page<SystemUser> page) {
        return systemUserService.page(page);
    }

}
