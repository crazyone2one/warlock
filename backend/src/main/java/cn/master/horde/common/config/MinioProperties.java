package cn.master.horde.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
@Getter
@Setter
@ConfigurationProperties(prefix = MinioProperties.MINIO_PREFIX)
public class MinioProperties {
    public static final String MINIO_PREFIX = "minio";

    private String endpoint;
    private String accessKey;
    private String secretKey;
}
