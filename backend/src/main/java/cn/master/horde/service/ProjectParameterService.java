package cn.master.horde.service;

import cn.master.horde.entity.ProjectParameter;
import com.mybatisflex.core.service.IService;

/**
 * 项目级参数 服务层。
 *
 * @author 11's papa
 * @since 2026-01-19
 */
public interface ProjectParameterService extends IService<ProjectParameter> {

    void saveParameter(ProjectParameter projectParameter);

    void deleteParameterByProjectId(String projectId);

    ProjectParameter getParameterByProjectIdAndType(String projectId, String type);

    void updateParameter(ProjectParameter projectParameter);
}
