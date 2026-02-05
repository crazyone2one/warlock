package cn.master.horde.common.config;

import cn.master.horde.model.dto.permission.PermissionCache;
import cn.master.horde.model.dto.permission.PermissionDefinitionItem;
import cn.master.horde.common.util.JsonHelper;
import io.micrometer.core.instrument.util.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/1/23, 星期五
 **/
@Configuration
public class PermissionConfig {
    @Bean
    public PermissionCache permissionCache() throws Exception {

        List<PermissionDefinitionItem> permissionDefinition = null;
        Enumeration<URL> urls = this.getClass().getClassLoader().getResources("permission.json");
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            String content = IOUtils.toString(url.openStream(), StandardCharsets.UTF_8);
            if (StringUtils.isBlank(content)) {
                continue;
            }

            List<PermissionDefinitionItem> temp = JsonHelper.parseArray(content, PermissionDefinitionItem.class);
            if (permissionDefinition == null) {
                permissionDefinition = temp;
            } else {
                permissionDefinition.addAll(temp);
            }
        }
        PermissionCache permissionCache = new PermissionCache();
        permissionCache.setPermissionDefinition(permissionDefinition);
        return permissionCache;
    }
}
