package cn.master.horde.service.impl;

import cn.master.horde.model.entity.ProjectApplication;
import cn.master.horde.model.mapper.ProjectApplicationMapper;
import cn.master.horde.service.ProjectApplicationService;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import static cn.master.horde.model.entity.table.ProjectApplicationTableDef.PROJECT_APPLICATION;

/**
 * 项目应用 服务层实现。
 *
 * @author 11's papa
 * @since 2026-02-09
 */
@Service
public class ProjectApplicationServiceImpl extends ServiceImpl<ProjectApplicationMapper, ProjectApplication> implements ProjectApplicationService {

    @Override
    public void update(ProjectApplication application, String currentUser) {
        createOrUpdateConfig(application);
    }

    private void createOrUpdateConfig(ProjectApplication application) {
        String type = application.getType();
        String projectId = application.getProjectId();
        QueryChain<ProjectApplication> queryChain = queryChain().where(PROJECT_APPLICATION.TYPE.eq(type).and(PROJECT_APPLICATION.PROJECT_ID.eq(projectId)));
        if (queryChain.count() > 0) {
            mapper.updateByQuery(application, queryChain);
        } else {
            mapper.insertSelective(application);
        }
    }
}
