package cn.master.horde.service.impl;

import cn.master.horde.common.result.BizException;
import cn.master.horde.common.result.ResultCode;
import cn.master.horde.common.service.CurrentUserService;
import cn.master.horde.dao.SlaveParameter;
import cn.master.horde.entity.ProjectParameter;
import cn.master.horde.mapper.ProjectParameterMapper;
import cn.master.horde.service.ProjectParameterService;
import cn.master.horde.util.JsonHelper;
import com.github.benmanes.caffeine.cache.Cache;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.master.horde.entity.table.ProjectParameterTableDef.PROJECT_PARAMETER;

/**
 * 项目级参数 服务层实现。
 *
 * @author 11's papa
 * @since 2026-01-19
 */
@Service
@RequiredArgsConstructor
public class ProjectParameterServiceImpl extends ServiceImpl<ProjectParameterMapper, ProjectParameter> implements ProjectParameterService {
    private final Cache<String, SlaveParameter> slaveCache;

    @Override
    public void saveParameter(ProjectParameter projectParameter) {
        checkExistByProjectIdAndParameterType(projectParameter);
        String userId = CurrentUserService.getCurrentUserId();
        projectParameter.setCreateUser(userId);
        projectParameter.setUpdateUser(userId);
        mapper.insertSelective(projectParameter);
        slaveCache.put(projectParameter.getProjectId(),
                JsonHelper.objectToType(SlaveParameter.class).apply(projectParameter.getParameters()));
    }

    @Override
    public void deleteParameterByProjectId(String projectId) {
        List<ProjectParameter> list = queryChain().where(PROJECT_PARAMETER.PROJECT_ID.eq(projectId)).list();
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(parameter -> mapper.deleteById(parameter.getId()));
            slaveCache.invalidate(projectId);
        }
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
