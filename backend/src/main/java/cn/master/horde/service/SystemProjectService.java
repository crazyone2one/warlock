package cn.master.horde.service;

import cn.master.horde.model.dto.BasePageRequest;
import cn.master.horde.model.dto.ProjectSwitchRequest;
import cn.master.horde.model.dto.request.UpdateProjectNameRequest;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import cn.master.horde.model.entity.SystemProject;

/**
 * 项目 服务层。
 *
 * @author 11's papa
 * @since 2026-01-15
 */
public interface SystemProjectService extends IService<SystemProject> {

    boolean saveProject(SystemProject systemProject);

    Page<SystemProject> getProjectPage(BasePageRequest page);

    boolean updateProject(SystemProject systemProject);

    void removeProject(String id);

    void enable(String id, String currentUserId);

    void disable(String id, String currentUserId);

    void switchProject(ProjectSwitchRequest request, String currentUserId);

    void rename(UpdateProjectNameRequest request, String username);
}
