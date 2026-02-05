package cn.master.horde.common.listener;

import cn.master.horde.common.config.DataSourceParameter;
import cn.master.horde.model.entity.ProjectParameter;
import cn.master.horde.common.util.JsonHelper;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.datasource.FlexDataSource;
import com.mybatisflex.core.query.QueryChain;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jspecify.annotations.NullMarked;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

import static cn.master.horde.model.entity.table.ProjectParameterTableDef.PROJECT_PARAMETER;


/**
 * @author : 11's papa
 * @since : 2026/1/19, 星期一
 **/
@NullMarked
@Component
public class DataSourceInitListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        List<ProjectParameter> projectDataSourceParameters = QueryChain.of(ProjectParameter.class)
                .select(PROJECT_PARAMETER.PROJECT_ID, PROJECT_PARAMETER.PARAMETERS)
                .where(PROJECT_PARAMETER.PARAMETER_TYPE.eq("datasource")).list();
        if (!projectDataSourceParameters.isEmpty()) {
            FlexDataSource dataSource = FlexGlobalConfig.getDefaultConfig().getDataSource();
            projectDataSourceParameters.forEach(ds -> {
                DataSourceParameter parameter = JsonHelper.objectToType(DataSourceParameter.class).apply(ds.getParameters());
                java.util.Properties properties = new java.util.Properties();
                properties.put("jdbcUrl", parameter.getUrl());
                properties.put("username", parameter.getUsername());
                properties.put("password", parameter.getPassword());
                HikariConfig hikariConfig = new HikariConfig(properties);
                dataSource.addDataSource(parameter.getName(), new HikariDataSource(hikariConfig));
            });
        }
    }
}
