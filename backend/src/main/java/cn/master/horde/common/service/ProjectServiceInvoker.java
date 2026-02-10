package cn.master.horde.common.service;

import cn.master.horde.service.CleanupProjectResourceService;
import cn.master.horde.service.CreateProjectResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
@Component
@RequiredArgsConstructor
public class ProjectServiceInvoker {
    private final List<CreateProjectResourceService> createProjectResourceServices;
    private final List<CleanupProjectResourceService> cleanupProjectResourceServices;

    public void invokeCreateServices(String projectId) {
        for (CreateProjectResourceService service : createProjectResourceServices) {
            service.createResources(projectId);
        }
    }

    public void invokeServices(String projectId) {
        for (CleanupProjectResourceService service : cleanupProjectResourceServices) {
            service.deleteResources(projectId);
        }
    }
}
