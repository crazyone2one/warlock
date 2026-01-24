package cn.master.horde.service.impl;

import cn.master.horde.common.config.DataSourceParameter;
import cn.master.horde.common.result.BizException;
import cn.master.horde.common.result.ResultCode;
import cn.master.horde.common.service.CurrentUserService;
import cn.master.horde.dto.SlaveParameter;
import cn.master.horde.entity.ProjectParameter;
import cn.master.horde.mapper.ProjectParameterMapper;
import cn.master.horde.service.ProjectParameterService;
import cn.master.horde.util.JsonHelper;
import com.github.benmanes.caffeine.cache.Cache;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.datasource.FlexDataSource;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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
    @Transactional(rollbackFor = Exception.class)
    public void saveParameter(ProjectParameter projectParameter) {
        checkExistByProjectIdAndParameterType(projectParameter);
        String userId = CurrentUserService.getCurrentUserId();
        projectParameter.setCreateUser(userId);
        projectParameter.setUpdateUser(userId);
        mapper.insertSelective(projectParameter);
        setDataSource(projectParameter.getParameters());
        slaveCache.put(projectParameter.getProjectId(),
                JsonHelper.objectToType(SlaveParameter.class).apply(projectParameter.getParameters()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteParameterByProjectId(String projectId) {
        List<ProjectParameter> list = queryChain().where(PROJECT_PARAMETER.PROJECT_ID.eq(projectId)).list();
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(parameter -> mapper.deleteById(parameter.getId()));
            slaveCache.invalidate(projectId);
        }
    }

    @Override
    public ProjectParameter getParameterByProjectIdAndType(String projectId, String type) {
        return queryChain().where(PROJECT_PARAMETER.PROJECT_ID.eq(projectId)
                .and(PROJECT_PARAMETER.PARAMETER_TYPE.eq(type))).one();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateParameter(ProjectParameter projectParameter) {
        updateById(projectParameter);
        setDataSource(projectParameter.getParameters());
    }

    private void setDataSource(Map<String, String> parameters) {
        FlexDataSource dataSource = FlexGlobalConfig.getDefaultConfig().getDataSource();
        DataSourceParameter parameter = JsonHelper.objectToType(DataSourceParameter.class).apply(parameters);
        java.util.Properties properties = new java.util.Properties();
        properties.put("jdbcUrl", parameter.getUrl());
        properties.put("username", parameter.getUsername());
        properties.put("password", parameter.getPassword());
        HikariConfig hikariConfig = new HikariConfig(properties);
        dataSource.addDataSource(parameter.getName(), new HikariDataSource(hikariConfig));
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
