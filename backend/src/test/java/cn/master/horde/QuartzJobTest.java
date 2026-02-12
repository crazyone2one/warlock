package cn.master.horde;

import cn.master.horde.scheduler.job.MyBusinessJob;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * ApiDefinitionModuleService 单元测试
 *
 * @author 11's papa
 * @since 2026-02-10
 */
@SpringBootTest
class QuartzJobTest {

    @Resource
    private MyBusinessJob businessJob;

    @Test
    void add_ShouldCreateModuleSuccessfully() {
        businessJob.cleanTempFiles();
    }

}