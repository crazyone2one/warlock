package cn.master.horde.service.impl;

import cn.master.horde.common.constants.*;
import cn.master.horde.common.file.FileCenter;
import cn.master.horde.common.file.FileRequest;
import cn.master.horde.common.result.BizException;
import cn.master.horde.common.service.ProjectServiceInvoker;
import cn.master.horde.common.util.JsonHelper;
import cn.master.horde.common.util.LogUtils;
import cn.master.horde.common.util.SessionUtils;
import cn.master.horde.common.util.Translator;
import cn.master.horde.model.dto.BasePageRequest;
import cn.master.horde.model.dto.LogDTO;
import cn.master.horde.model.dto.ProjectSwitchRequest;
import cn.master.horde.model.dto.request.UpdateProjectNameRequest;
import cn.master.horde.model.entity.SystemProject;
import cn.master.horde.model.entity.SystemSchedule;
import cn.master.horde.model.entity.SystemUser;
import cn.master.horde.model.entity.UserRole;
import cn.master.horde.model.mapper.SystemProjectMapper;
import cn.master.horde.model.mapper.UserRoleMapper;
import cn.master.horde.model.mapper.UserRolePermissionMapper;
import cn.master.horde.model.mapper.UserRoleRelationMapper;
import cn.master.horde.service.OperationLogService;
import cn.master.horde.service.ProjectParameterService;
import cn.master.horde.service.SystemProjectService;
import cn.master.horde.service.SystemScheduleService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static cn.master.horde.model.entity.table.SystemProjectTableDef.SYSTEM_PROJECT;
import static cn.master.horde.model.entity.table.SystemUserTableDef.SYSTEM_USER;
import static cn.master.horde.model.entity.table.UserRolePermissionTableDef.USER_ROLE_PERMISSION;
import static cn.master.horde.model.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;
import static cn.master.horde.model.entity.table.UserRoleTableDef.USER_ROLE;


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
    private final ProjectServiceInvoker projectServiceInvoker;
    private final UserRoleRelationMapper userRoleRelationMapper;
    private final UserRoleMapper userRoleMapper;
    private final UserRolePermissionMapper userRolePermissionMapper;
    private final OperationLogService operationLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveProject(SystemProject systemProject) {
        checkProjectExistByNameAndNum(systemProject);
        systemProject.setCreateUser(SessionUtils.getCurrentUsername());
        systemProject.setUpdateUser(SessionUtils.getCurrentUsername());
        boolean save = save(systemProject);
        projectServiceInvoker.invokeCreateServices(systemProject.getId());
        return save;
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
        SystemProject project = queryChain().where(SYSTEM_PROJECT.ID.eq(id)).oneOpt()
                .orElseThrow(() -> new BizException(Translator.get("project_is_not_exist")));
        mapper.deleteById(id);
        // 删除项目参数
        projectParameterService.deleteParameterByProjectId(id);
        // 删除项目任务
        List<SystemSchedule> taskList = systemScheduleService.getTaskByProjectId(id);
        if (CollectionUtils.isNotEmpty(taskList)) {
            taskList.forEach(task -> systemScheduleService.deleteTask(task.getId()));
        }
        projectServiceInvoker.invokeServices(id);
        deleteProjectUserGroup(id);
        // 删除项目目录，避免资源删除有遗漏
        String projectDir = DefaultRepositoryDir.getProjectDir(id);
        FileRequest request = new FileRequest();
        request.setFolder(projectDir);
        try {
            FileCenter.getDefaultRepository().deleteFolder(request);
        } catch (Exception e) {
            LogUtils.error(e);
        }
        List<LogDTO> logDTOList = new ArrayList<>();
        LogDTO logDTO = new LogDTO(OperationLogConstants.SYSTEM, id, Translator.get("scheduled_tasks"), OperationLogType.DELETE.name(), OperationLogModule.SETTING_ORGANIZATION_PROJECT, Translator.get("delete") + Translator.get("project") + ": " + project.getName());
        setLog(logDTO, StringUtils.EMPTY, StringUtils.EMPTY, logDTOList);

        operationLogService.batchAdd(logDTOList);
    }

    private void setLog(LogDTO dto, String path, String method, List<LogDTO> logDTOList) {
        dto.setPath(path);
        dto.setMethod(method);
        dto.setOriginalValue(JsonHelper.toJSONBytes(StringUtils.EMPTY));
        logDTOList.add(dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enable(String id, String currentUserId) {
        queryChain().where(SYSTEM_PROJECT.ID.eq(id)).oneOpt()
                .orElseThrow(() -> new BizException(Translator.get("project_is_not_exist")));
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
                .orElseThrow(() -> new BizException(Translator.get("project_is_not_exist")));
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
            throw new BizException(Translator.get("not_authorized"));
        }
        queryChain().where(SYSTEM_PROJECT.ID.eq(request.projectId())).oneOpt()
                .orElseThrow(() -> new BizException(Translator.get("project_not_exist")));
        UpdateChain.of(SystemUser.class).set(SYSTEM_USER.LAST_PROJECT_ID, request.projectId())
                .where(SYSTEM_USER.ID.eq(request.userId())).update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rename(UpdateProjectNameRequest request, String username) {
        SystemProject systemProject = checkProjectNotExist(request.id());
        SystemProject project = new SystemProject();
        project.setId(request.id());
        project.setName(request.name());
        project.setNum(systemProject.getNum());
        checkProjectExistByNameAndNum(project);
        project.setUpdateUser(username);
        mapper.update(project);
    }

    private SystemProject checkProjectNotExist(String id) {
        return queryChain().where(SYSTEM_PROJECT.ID.eq(id)).oneOpt().orElseThrow(() -> new BizException(Translator.get("project_is_not_exist")));
    }

    private void checkProjectExistByNameAndNum(SystemProject project) {
        boolean exists = QueryChain.of(SystemProject.class).where(SYSTEM_PROJECT.NAME.eq(project.getName())
                .and(SYSTEM_PROJECT.ID.ne(project.getId()))
                .and(SYSTEM_PROJECT.NUM.eq(project.getNum()))).exists();
        if (exists) {
            throw new BizException(Translator.get("project_name_already_exists"));
        }
    }

    private void deleteProjectUserGroup(String projectId) {
        QueryWrapper queryWrapper = QueryWrapper.create().where(USER_ROLE_RELATION.SOURCE_ID.eq(projectId));
        userRoleRelationMapper.deleteByQuery(queryWrapper);

        QueryWrapper userRoleWrapper = QueryWrapper.create().where(USER_ROLE.SCOPE_ID.eq(projectId)
                .and(USER_ROLE.TYPE.eq(UserRoleType.PROJECT.name())));
        List<UserRole> userRoles = userRoleMapper.selectListByQuery(userRoleWrapper);
        if (CollectionUtils.isNotEmpty(userRoles)) {
            List<String> roleIds = userRoles.stream().map(UserRole::getId).toList();
            QueryWrapper rolePermissionWrapper = QueryWrapper.create().where(USER_ROLE_PERMISSION.ROLE_ID.in(roleIds));
            userRoleMapper.deleteByQuery(userRoleWrapper);
            userRolePermissionMapper.deleteByQuery(rolePermissionWrapper);
        }
    }
}
