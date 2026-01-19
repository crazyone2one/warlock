package cn.master.horde;

import cn.master.horde.dao.ScheduleConfigParameter;
import cn.master.horde.util.JsonHelper;
import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : 11's papa
 * @since : 2026/1/15, 星期四
 **/
@SpringBootTest
public class JsonTest {
    @Test
    public void testJson() {
        try{
            DataSourceKey.use("ds-slave150622007792");
            List<Row> rows = Db.selectAll("system_users");
            System.out.println(rows);
        }finally{
            DataSourceKey.clear();
        }
    }
}
