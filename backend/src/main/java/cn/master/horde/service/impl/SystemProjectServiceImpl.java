package cn.master.horde.service.impl;

import cn.master.horde.common.result.BizException;
import cn.master.horde.common.result.ResultCode;
import cn.master.horde.common.service.CurrentUserService;
import cn.master.horde.dao.BasePageRequest;
import cn.master.horde.entity.SystemProject;
import cn.master.horde.entity.SystemSchedule;
import cn.master.horde.mapper.SystemProjectMapper;
import cn.master.horde.service.SystemProjectService;
import cn.master.horde.service.SystemScheduleService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.master.horde.entity.table.SystemProjectTableDef.SYSTEM_PROJECT;

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

    @Override
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
    public boolean updateProject(SystemProject systemProject) {
        checkProjectExistByNameAndNum(systemProject);
        return updateById(systemProject);
    }

    @Override
    public void removeProject(String id) {
        queryChain().where(SYSTEM_PROJECT.ID.eq(id)).oneOpt()
                .orElseThrow(() -> new BizException(ResultCode.VALIDATE_FAILED, "项目不存在"));
        mapper.deleteById(id);
        List<SystemSchedule> taskList = systemScheduleService.getTaskByProjectId(id);
        if (CollectionUtils.isNotEmpty(taskList)) {
            taskList.forEach(task -> systemScheduleService.deleteTask(task.getId()));
        }
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
