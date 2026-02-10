package cn.master.horde.service.impl;

import cn.master.horde.common.constants.ProjectApplicationType;
import cn.master.horde.model.entity.ProjectApplication;
import cn.master.horde.model.entity.ProjectVersion;
import cn.master.horde.model.mapper.ProjectVersionMapper;
import cn.master.horde.service.CreateProjectResourceService;
import cn.master.horde.service.ProjectApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
@Component
@RequiredArgsConstructor
public class CreateVersionResourceService implements CreateProjectResourceService {
    private final ProjectVersionMapper projectVersionMapper;
    private final ProjectApplicationService projectApplicationService;
    public static final String DEFAULT_VERSION = "v1.0";
    public static final String DEFAULT_VERSION_STATUS = "open";

    @Override
    public void createResources(String projectId) {
        ProjectVersion defaultVersion = new ProjectVersion();
        defaultVersion.setProjectId(projectId);
        defaultVersion.setName(DEFAULT_VERSION);
        defaultVersion.setStatus(DEFAULT_VERSION_STATUS);
        defaultVersion.setLatest(true);
        projectVersionMapper.insert(defaultVersion);
        ProjectApplication projectApplication = new ProjectApplication();
        projectApplication.setProjectId(projectId);
        projectApplication.setType(ProjectApplicationType.VERSION.VERSION_ENABLE.name());
        projectApplication.setTypeValue("FALSE");
        projectApplicationService.update(projectApplication, "");

    }
}
