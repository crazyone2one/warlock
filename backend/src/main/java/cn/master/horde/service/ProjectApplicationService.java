package cn.master.horde.service;

import com.mybatisflex.core.service.IService;
import cn.master.horde.model.entity.ProjectApplication;

/**
 * 项目应用 服务层。
 *
 * @author 11's papa
 * @since 2026-02-09
 */
public interface ProjectApplicationService extends IService<ProjectApplication> {
    void update(ProjectApplication application, String currentUser);
}
