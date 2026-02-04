package cn.master.horde;

import cn.master.horde.entity.SystemProject;
import cn.master.horde.entity.SystemUser;
import cn.master.horde.mapper.SystemProjectMapper;
import cn.master.horde.mapper.SystemUserMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author : 11's papa
 * @since : 2026/1/14, 星期三
 **/
@SpringBootTest
public class UserTest {
    @Resource
    PasswordEncoder passwordEncoder;
    @Resource
    SystemUserMapper systemUserMapper;
    @Resource
    SystemProjectMapper systemProjectMapper;

    @Test
    void addUserTest() {
        SystemUser user = SystemUser.builder().name("admin").nickName("管理员")
                .password(passwordEncoder.encode("admin")).enable(true)
                .email("admin@example.com")
                .createUser("admin").updateUser("admin")
                .build();
        systemUserMapper.insertSelective(user);
    }
    @Test
    void addProjectTest() {
        SystemProject project = SystemProject.builder().name("演示项目").num("1001")
                .description("演示项目").enable(true)
                .createUser("admin").updateUser("admin")
                .build();
        systemProjectMapper.insertSelective(project);
    }
}
