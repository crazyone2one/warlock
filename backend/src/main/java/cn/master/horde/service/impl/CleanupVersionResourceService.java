package cn.master.horde.service.impl;

import cn.master.horde.common.constants.ProjectApplicationType;
import cn.master.horde.model.mapper.ProjectApplicationMapper;
import cn.master.horde.model.mapper.ProjectVersionMapper;
import cn.master.horde.service.CleanupProjectResourceService;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static cn.master.horde.model.entity.table.ProjectApplicationTableDef.PROJECT_APPLICATION;
import static cn.master.horde.model.entity.table.ProjectVersionTableDef.PROJECT_VERSION;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
@Component
@RequiredArgsConstructor
public class CleanupVersionResourceService implements CleanupProjectResourceService {
    private final ProjectVersionMapper projectVersionMapper;
    private final ProjectApplicationMapper projectApplicationMapper;

    @Override
    public void deleteResources(String projectId) {
        // 删除所有项目版本
        QueryWrapper queryWrapper = QueryWrapper.create().where(PROJECT_VERSION.PROJECT_ID.eq(projectId));
        projectVersionMapper.deleteByQuery(queryWrapper);
        // 删除项目版本配置项
        QueryWrapper query = QueryWrapper.create().where(PROJECT_APPLICATION.PROJECT_ID.eq(projectId)
                .and(PROJECT_APPLICATION.TYPE.eq(ProjectApplicationType.VERSION.VERSION_ENABLE.name())));
        projectApplicationMapper.deleteByQuery(query);
    }
}
