package cn.master.horde;

import cn.master.horde.dao.ScheduleConfigParameter;
import cn.master.horde.util.JsonHelper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : 11's papa
 * @since : 2026/1/15, 星期四
 **/
@SpringBootTest
public class JsonTest {
    @Test
    public void testJson() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "papa");
        map.put("sensorId", "123");
        System.out.println(JsonHelper.objectToType(ScheduleConfigParameter.class).apply(map));
    }
}
