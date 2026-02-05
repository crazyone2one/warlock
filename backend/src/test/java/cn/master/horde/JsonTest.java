package cn.master.horde;

import cn.master.horde.model.dto.permission.PermissionDefinitionItem;
import cn.master.horde.common.util.JsonHelper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.core.type.TypeReference;

import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/1/15, 星期四
 **/
@SpringBootTest
public class JsonTest {
    @Test
    public void testJson() {
        String json = "[\n" +
                "  {\n" +
                "    \"id\": \"SYSTEM\",\n" +
                "    \"name\": \"permission.system.name\",\n" +
                "    \"type\": \"SYSTEM\",\n" +
                "    \"children\": [\n" +
                "      {\n" +
                "        \"id\": \"SYSTEM_USER\",\n" +
                "        \"name\": \"permission.system_user.name\",\n" +
                "        \"permissions\": [\n" +
                "          {\n" +
                "            \"id\": \"SYSTEM_USER:READ\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"id\": \"SYSTEM_USER:READ+ADD\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"id\": \"SYSTEM_USER:READ+IMPORT\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"id\": \"SYSTEM_USER:READ+UPDATE\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"id\": \"SYSTEM_USER:READ+DELETE\"\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    ],\n" +
                "    \"order\": 1\n" +
                "  }\n" +
                "]\n";
        List<PermissionDefinitionItem> temp = JsonHelper.toObject(json, new TypeReference<>() {
        });
        System.out.println(temp);
    }
}
