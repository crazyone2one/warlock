package cn.master.horde.controller;

import cn.idev.excel.FastExcel;
import cn.idev.excel.util.MapUtils;
import cn.master.horde.common.log.annotation.Loggable;
import cn.master.horde.common.constants.Created;
import cn.master.horde.common.constants.Updated;
import cn.master.horde.common.util.SessionUtils;
import cn.master.horde.security.security.CustomUserDetails;
import cn.master.horde.model.dto.*;
import cn.master.horde.model.dto.request.UserBatchCreateRequest;
import cn.master.horde.model.dto.request.UserChangeEnableRequest;
import cn.master.horde.model.dto.request.UserEditRequest;
import cn.master.horde.model.entity.SystemUser;
import cn.master.horde.service.SystemUserService;
import cn.master.horde.service.UserRoleService;
import cn.master.horde.common.util.JsonHelper;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 用户 控制层。
 *
 * @author 11's papa
 * @since 2026-01-14
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/user")
public class SystemUserController {

    private final SystemUserService systemUserService;
    private final UserRoleService userRoleService;

    @Loggable("添加用户")
    @PreAuthorize("hasPermission('SYSTEM_USER','READ+ADD')")
    @PostMapping("save")
    public UserBatchCreateResponse save(@Validated({Created.class}) @RequestBody UserBatchCreateRequest request) {
        return systemUserService.saveUser(request);
    }

    /**
     * 根据主键删除用户。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @PreAuthorize("hasPermission('SYSTEM_USER','READ+DELETE')")
    public boolean remove(@PathVariable String id) {
        return systemUserService.removeById(id);
    }

    @Loggable("修改用户")
    @PreAuthorize("hasPermission('SYSTEM_USER','READ+UPDATE')")
    @PostMapping("update")
    public UserEditRequest update(@Validated({Updated.class}) @RequestBody UserEditRequest request) {
        return systemUserService.updateUser(request);
    }

    @Loggable("启用/禁用用户")
    @PreAuthorize("hasPermission('SYSTEM_USER','READ+UPDATE')")
    @PostMapping("/update/enable")
    public TableBatchProcessResponse update(@Validated @RequestBody UserChangeEnableRequest request) {
        return systemUserService.updateUserEnable(request, SessionUtils.getCurrentUserId(), SessionUtils.getCurrentUsername());
    }

    @Loggable("删除用户")
    @PreAuthorize("hasPermission('SYSTEM_USER','READ+DELETE')")
    @PostMapping("/delete")
    public TableBatchProcessResponse delete(@Validated @RequestBody UserChangeEnableRequest request) {
        return systemUserService.deleteUser(request, SessionUtils.getCurrentUserId(), SessionUtils.getCurrentUsername());
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


    @GetMapping("/get/{keyword}")
    @Operation(summary = "通过email或id查找用户")
    @PreAuthorize("hasPermission('SYSTEM_USER','READ')")
    public UserDTO getInfo(@PathVariable String keyword) {
        return systemUserService.getUserDTOByKeyword(keyword);
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

    @PostMapping("page")
    @Operation(summary = "系统设置-用户-分页查找用户")
    @PreAuthorize("hasPermission('SYSTEM_USER','READ')")
    public Page<UserTableResponse> page(@Validated @RequestBody BasePageRequest request) {
        return systemUserService.pageUser(request);
    }

    @PostMapping("/reset/password")
    @Operation(summary = "系统设置-系统-用户-重置用户密码")
    @PreAuthorize("hasPermission('SYSTEM_USER','READ+UPDATE')")
    public TableBatchProcessResponse resetPassword(@Validated @RequestBody TableBatchProcessDTO request) {
        return systemUserService.resetPassword(request, SessionUtils.getCurrentUserId());
    }

    @PostMapping(value = "/import", consumes = {"multipart/form-data"})
    @Operation(summary = "系统设置-系统-用户-导入用户")
    @PreAuthorize("hasPermission('SYSTEM_USER','READ+IMPORT')")
    public UserImportResponse importUser(@RequestPart(value = "file", required = false) MultipartFile excelFile) {
        return systemUserService.importByExcel(excelFile, SessionUtils.getCurrentUserId());
    }

    @GetMapping("/get/system/role")
    @Operation(summary = "系统设置-系统-用户-查找系统级用户组")
    @PreAuthorize("hasPermission('SYSTEM_USER','READ')")
    public List<UserSelectOption> getGlobalSystemRole() {
        return userRoleService.getSystemRoleList();
    }

    @GetMapping("/get/import/{type}")
    @Operation(summary = "获取导入模板文件")
    public void getTemplate(HttpServletResponse response, @PathVariable String type) throws IOException {
        try {
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("user-import-template", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename*=utf-8''" + fileName + ".xlsx");
            List<UserExcel> list = List.of(new UserExcel());
            if (!"template".equalsIgnoreCase(type)) {
                // todo 获取用户列表
            }
            // 这里需要设置不关闭流
            FastExcel.write(response.getOutputStream(), UserExcel.class).autoCloseStream(Boolean.FALSE).sheet("模板")
                    .doWrite(list);
        } catch (Exception e) {
            // 重置response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Map<String, String> map = MapUtils.newHashMap();
            map.put("status", "failure");
            map.put("message", "下载文件失败" + e.getMessage());
            response.getWriter().println(JsonHelper.objectToString(map));
        }
    }
}
