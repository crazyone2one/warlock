package cn.master.horde.common.config;

import cn.master.horde.common.util.LogUtils;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketLifecycleArgs;
import io.minio.messages.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
@Configuration
public class MinioConfig {
    public static final String BUCKET = "warlock";

    @Bean
    public MinioClient minioClient(MinioProperties minioProperties) throws Exception {
        // Create client with credentials.
        MinioClient minioClient = MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
        // 检查并创建存储桶
        BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(BUCKET).build();
        boolean exist;
        try {
            exist = minioClient.bucketExists(bucketExistsArgs);
        } catch (Exception e) {
            exist = false;
        }
        if (!exist) {
            LogUtils.info("创建存储桶: {}", BUCKET);
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET).build());
            LogUtils.info("存储桶创建成功: {}", BUCKET);
        }

        // 再次验证存储桶
        exist = minioClient.bucketExists(bucketExistsArgs);
        LogUtils.info("存储桶 {} 最终状态: {}", BUCKET, exist ? "存在" : "不存在");

        // 设置临时目录下文件的过期时间
        setBucketLifecycle(minioClient);
        setBucketLifecycleByExcel(minioClient);

        return minioClient;
    }

    private void setBucketLifecycleByExcel(MinioClient minioClient) {
        List<LifecycleRule> rules = new LinkedList<>();
        rules.add(
                new LifecycleRule(
                        Status.ENABLED,
                        null,
                        new Expiration((ZonedDateTime) null, 1, null),
                        new RuleFilter("system/export/excel"),
                        "excel-file",
                        null,
                        null,
                        null));
        rules.add(
                new LifecycleRule(
                        Status.ENABLED,
                        null,
                        new Expiration((ZonedDateTime) null, 1, null),
                        new RuleFilter("system/export/api"),
                        "api-file",
                        null,
                        null,
                        null));
        LifecycleConfiguration config = new LifecycleConfiguration(rules);
        try {
            minioClient.setBucketLifecycle(
                    SetBucketLifecycleArgs.builder()
                            .bucket(BUCKET)
                            .config(config)
                            .build());
        } catch (Exception e) {
            LogUtils.error(e);
        }
    }

    private void setBucketLifecycle(MinioClient minioClient) {
        List<LifecycleRule> rules = new LinkedList<>();
        rules.add(
                new LifecycleRule(
                        Status.ENABLED,
                        null,
                        new Expiration((ZonedDateTime) null, 7, null),
                        new RuleFilter("system/temp/"),
                        "temp-file",
                        null,
                        null,
                        null));
        LifecycleConfiguration config = new LifecycleConfiguration(rules);
        try {
            minioClient.setBucketLifecycle(
                    SetBucketLifecycleArgs.builder()
                            .bucket(BUCKET)
                            .config(config)
                            .build());
        } catch (Exception e) {
            LogUtils.error(e);
        }
    }
}