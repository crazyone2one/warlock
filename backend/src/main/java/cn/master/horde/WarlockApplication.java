package cn.master.horde;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.master.horde.mapper")
public class WarlockApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarlockApplication.class, args);
    }

}
