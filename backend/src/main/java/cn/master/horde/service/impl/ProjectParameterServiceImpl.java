package cn.master.horde.service.impl;

import cn.master.horde.common.result.BizException;
import cn.master.horde.common.result.ResultCode;
import cn.master.horde.common.service.CurrentUserService;
import cn.master.horde.entity.ProjectParameter;
import cn.master.horde.mapper.ProjectParameterMapper;
import cn.master.horde.service.ProjectParameterService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import static cn.master.horde.entity.table.ProjectParameterTableDef.PROJECT_PARAMETER;

/**
 * 项目级参数 服务层实现。
 *
 * @author 11's papa
 * @since 2026-01-19
 */
@Service
public class ProjectParameterServiceImpl extends ServiceImpl<ProjectParameterMapper, ProjectParameter> implements ProjectParameterService {

    @Override
    public void saveParameter(ProjectParameter projectParameter) {
        checkExistByProjectIdAndParameterType(projectParameter);
        String userId = CurrentUserService.getCurrentUserId();
        projectParameter.setCreateUser(userId);
        projectParameter.setUpdateUser(userId);
        mapper.insertSelective(projectParameter);
    }

    private void checkExistByProjectIdAndParameterType(ProjectParameter projectParameter) {
        boolean exists = queryChain()
                .where(PROJECT_PARAMETER.PROJECT_ID.eq(projectParameter.getProjectId())
                        .and(PROJECT_PARAMETER.PARAMETER_TYPE.eq(projectParameter.getParameterType()))).exists();
        if (exists) {
            throw new BizException(ResultCode.VALIDATE_FAILED,
                    projectParameter.getProjectId() + "已存在" + projectParameter.getParameterType() + "参数");
        }
    }
}
