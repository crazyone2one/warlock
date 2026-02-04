package cn.master.horde.controller;

import cn.master.horde.common.constants.Updated;
import cn.master.horde.common.service.SessionUtils;
import cn.master.horde.dto.BasePageRequest;
import cn.master.horde.dto.ProjectSwitchRequest;
import cn.master.horde.dto.request.UpdateProjectNameRequest;
import cn.master.horde.entity.SystemProject;
import cn.master.horde.service.SystemProjectService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目 控制层。
 *
 * @author 11's papa
 * @since 2026-01-15
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/project")
public class SystemProjectController {

    private final SystemProjectService systemProjectService;

    @PostMapping("save")
    @Operation(description = "保存项目")
    @PreAuthorize("hasPermission('SYSTEM_PROJECT','READ+ADD')")
    public boolean save(@RequestBody SystemProject systemProject) {
        return systemProjectService.saveProject(systemProject);
    }


    @DeleteMapping("remove/{id}")
    @Operation(description = "根据主键删除项目")
    @PreAuthorize("hasPermission('SYSTEM_PROJECT','READ+DELETE')")
    public void remove(@PathVariable String id) {
        systemProjectService.removeProject(id);
    }

    @Operation(description = "更新项目")
    @PostMapping("update")
    @PreAuthorize("hasPermission('SYSTEM_PROJECT','READ+UPDATE')")
    public boolean update(@RequestBody SystemProject systemProject) {
        return systemProjectService.updateProject(systemProject);
    }


    @GetMapping("list")
    @Operation(description = "查询所有项目")
    @PreAuthorize("hasPermission('SYSTEM_PROJECT','READ')")
    public List<SystemProject> list() {
        return systemProjectService.list();
    }


    @GetMapping("getInfo/{id}")
    @Operation(description = "根据主键获取项目")
    @PreAuthorize("hasPermission('SYSTEM_PROJECT','READ')")
    public SystemProject getInfo(@PathVariable String id) {
        return systemProjectService.getById(id);
    }

    @Operation(description = "分页查询项目")
    @PostMapping("page")
    @PreAuthorize("hasPermission('SYSTEM_PROJECT','READ')")
    public Page<SystemProject> page(@Validated @RequestBody BasePageRequest page) {
        return systemProjectService.getProjectPage(page);
    }

    @GetMapping("/enable/{id}")
    @PreAuthorize("hasPermission('SYSTEM_PROJECT','READ+UPDATE')")
    public void enable(@PathVariable String id) {
        systemProjectService.enable(id, SessionUtils.getCurrentUsername());
    }

    @GetMapping("/disable/{id}")
    @PreAuthorize("hasPermission('SYSTEM_PROJECT','READ+UPDATE')")
    public void disable(@PathVariable String id) {
        systemProjectService.disable(id, SessionUtils.getCurrentUsername());
    }

    @PostMapping("/switch")
    public void switchProject(@RequestBody ProjectSwitchRequest request) {
        systemProjectService.switchProject(request, SessionUtils.getCurrentUserId());
    }

    @PostMapping("/rename")
    @Operation(summary = "系统设置-系统-组织与项目-项目-修改项目名称")
    @PreAuthorize("hasPermission('SYSTEM_PROJECT','READ+UPDATE')")
    public void rename(@RequestBody @Validated({Updated.class}) UpdateProjectNameRequest request) {
        systemProjectService.rename(request, SessionUtils.getCurrentUsername());
    }
}
