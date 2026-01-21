package cn.master.horde.service.impl;

import cn.master.horde.common.result.BizException;
import cn.master.horde.common.result.ResultCode;
import cn.master.horde.common.service.CurrentUserService;
import cn.master.horde.dao.BasePageRequest;
import cn.master.horde.dao.ProjectSwitchRequest;
import cn.master.horde.entity.SystemProject;
import cn.master.horde.entity.SystemSchedule;
import cn.master.horde.entity.SystemUser;
import cn.master.horde.mapper.SystemProjectMapper;
import cn.master.horde.service.ProjectParameterService;
import cn.master.horde.service.SystemProjectService;
import cn.master.horde.service.SystemScheduleService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cn.master.horde.entity.table.SystemProjectTableDef.SYSTEM_PROJECT;
import static cn.master.horde.entity.table.SystemUserTableDef.SYSTEM_USER;

/**
 * 项目 服务层实现。
 *
 * @author 11's papa
 * @since 2026-01-15
 */
@Service
@RequiredArgsConstructor
public class SystemProjectServiceImpl extends ServiceImpl<SystemProjectMapper, SystemProject> implements SystemProjectService {
    private final SystemScheduleService systemScheduleService;
    private final ProjectParameterService projectParameterService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveProject(SystemProject systemProject) {
        checkProjectExistByNameAndNum(systemProject);
        systemProject.setCreateUser(CurrentUserService.getCurrentUsername());
        systemProject.setUpdateUser(CurrentUserService.getCurrentUsername());
        return save(systemProject);
    }

    @Override
    public Page<SystemProject> getProjectPage(BasePageRequest page) {
        return queryChain()
                .where(SYSTEM_PROJECT.NAME.like(page.getKeyword())
                        .or(SYSTEM_PROJECT.NUM.like(page.getKeyword())))
                .page(new Page<>(page.getPage(), page.getPageSize()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateProject(SystemProject systemProject) {
        checkProjectExistByNameAndNum(systemProject);
        return updateById(systemProject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeProject(String id) {
        queryChain().where(SYSTEM_PROJECT.ID.eq(id)).oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.VALIDATE_FAILED, "项目不存在"));
        mapper.deleteById(id);
        // 删除项目参数
        projectParameterService.deleteParameterByProjectId(id);
        // 删除项目任务
        List<SystemSchedule> taskList = systemScheduleService.getTaskByProjectId(id);
        if (CollectionUtils.isNotEmpty(taskList)) {
            taskList.forEach(task -> systemScheduleService.deleteTask(task.getId()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enable(String id, String currentUserId) {
        queryChain().where(SYSTEM_PROJECT.ID.eq(id)).oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.VALIDATE_FAILED, "项目不存在"));
        updateChain().set(SYSTEM_PROJECT.ENABLE, true).set(SYSTEM_PROJECT.UPDATE_USER, currentUserId)
                .where(SYSTEM_PROJECT.ID.eq(id)).update();
        List<SystemSchedule> taskList = systemScheduleService.getTaskByProjectId(id);
        if (CollectionUtils.isNotEmpty(taskList)) {
            taskList.forEach(task -> systemScheduleService.resumeTask(task.getId()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disable(String id, String currentUserId) {
        queryChain().where(SYSTEM_PROJECT.ID.eq(id)).oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.VALIDATE_FAILED, "项目不存在"));
        updateChain().set(SYSTEM_PROJECT.ENABLE, false).set(SYSTEM_PROJECT.UPDATE_USER, currentUserId)
                .where(SYSTEM_PROJECT.ID.eq(id)).update();
        List<SystemSchedule> taskList = systemScheduleService.getTaskByProjectId(id);
        if (CollectionUtils.isNotEmpty(taskList)) {
            taskList.forEach(task -> systemScheduleService.pauseTask(task.getId()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void switchProject(ProjectSwitchRequest request, String currentUserId) {
        if (!Strings.CS.equals(request.userId(), currentUserId)) {
            throw new BizException(ResultCode.VALIDATE_FAILED, "无权限");
        }
        queryChain().where(SYSTEM_PROJECT.ID.eq(request.projectId())).oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.VALIDATE_FAILED, "项目不存在"));
        UpdateChain.of(SystemUser.class).set(SYSTEM_USER.LAST_PROJECT_ID, request.projectId())
                .where(SYSTEM_USER.ID.eq(request.userId())).update();
    }

    private void checkProjectExistByNameAndNum(SystemProject project) {
        boolean exists = QueryChain.of(SystemProject.class).where(SYSTEM_PROJECT.NAME.eq(project.getName())
                .and(SYSTEM_PROJECT.ID.ne(project.getId()))
                .and(SYSTEM_PROJECT.NUM.eq(project.getNum()))).exists();
        if (exists) {
            throw new BizException(ResultCode.VALIDATE_FAILED, "项目名称或编号已存在");
        }
    }
}
