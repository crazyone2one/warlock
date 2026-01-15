package cn.master.horde.common.config;

import cn.master.horde.dao.SlaveParameter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : 11's papa
 * @since : 2026/1/15, 星期四
 **/
@Component
@ConfigurationProperties(prefix = "transfer")
public class FileTransferConfiguration {
    @Getter
    @Setter
    private List<Map<String, SlaveParameter>> slave;
    public SlaveParameter getSlaveConfigByResourceId(String resourceId) {
        Map<String, SlaveParameter> slaveMap = slave.stream()
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> replacement // 如果key重复，使用新的值
                ));
        return slaveMap.get(resourceId);
    }
}
