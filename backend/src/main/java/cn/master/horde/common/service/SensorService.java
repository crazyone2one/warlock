package cn.master.horde.common.service;


import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : 11's papa
 * @since : 2026/1/15, 星期四
 **/
@Service
@RequiredArgsConstructor
public class SensorService {
    private final RedisService redisService;
    private final ObjectMapper objectMapper;
    private static final Long TIMEOUT = 60 * 60 * 24L;

    public List<Row> getSensorFromRedis(String projectNum, String key, String tableName) {
        String dataInRedis = redisService.getSensor(projectNum, key);
        if (StringUtils.isNotBlank(dataInRedis)) {
            return objectMapper.readValue(dataInRedis, objectMapper.getTypeFactory().constructCollectionType(List.class, Row.class));
        } else {
            List<Row> sensorList = getAllDataFromSlaveDatabase(projectNum, tableName);
            redisService.storeSensor(projectNum, key, sensorList, TIMEOUT);
            return sensorList;
        }
    }

    public List<Row> getAllDataFromSlaveDatabase(String projectNum, String tableName) {
        List<Row> rows;
        try {
            DataSourceKey.use("ds-slave" + projectNum);
            Map<String, Object> map = new LinkedHashMap<>();
            rows = Db.selectListByMap(tableName, map);
        } finally {
            DataSourceKey.clear();
        }
        return rows;
    }
}
