package cn.master.horde.core.constant;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author : 11's papa
 * @since : 2026/1/15, 星期四
 **/
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private long accessTokenValidity;
    private long refreshTokenValidity;
    private String secret;
}
