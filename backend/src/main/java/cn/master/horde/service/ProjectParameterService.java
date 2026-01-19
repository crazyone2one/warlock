package cn.master.horde.service;

import com.mybatisflex.core.service.IService;
import cn.master.horde.entity.ProjectParameter;

/**
 * 项目级参数 服务层。
 *
 * @author 11's papa
 * @since 2026-01-19
 */
public interface ProjectParameterService extends IService<ProjectParameter> {

    void saveParameter(ProjectParameter projectParameter);
}
