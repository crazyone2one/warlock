package cn.master.horde;

import cn.master.horde.common.config.MinioProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@MapperScan("cn.master.horde.model.mapper")
@EnableConfigurationProperties({
        MinioProperties.class
})
public class WarlockApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarlockApplication.class, args);
    }

}
