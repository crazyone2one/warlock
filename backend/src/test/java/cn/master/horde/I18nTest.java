package cn.master.horde;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * @author : 11's papa
 * @since : 2026/1/26, 星期一
 **/
@SpringBootTest
public class I18nTest {
    @Resource
    MessageSource messageSource;
    @Test
    void contextLoads() {
        String message = messageSource.getMessage("error_lang_invalid", null, Locale.forLanguageTag("zh-CN"));
        System.out.println(message);
    }
}
