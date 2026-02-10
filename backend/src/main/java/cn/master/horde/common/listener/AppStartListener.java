package cn.master.horde.common.listener;

import cn.master.horde.common.constants.StorageType;
import cn.master.horde.common.file.FileCenter;
import cn.master.horde.common.file.MinioRepository;
import cn.master.horde.common.uid.DefaultUidGenerator;
import cn.master.horde.common.util.LogUtils;
import io.minio.MinioClient;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
@Component
@RequiredArgsConstructor
public class AppStartListener implements ApplicationRunner {
    @Resource
    private MinioClient minioClient;
    @Resource
    private DefaultUidGenerator defaultUidGenerator;
    @Override
    public void run(@NotNull ApplicationArguments args) {
        LogUtils.info("================= 应用启动 =================");
        defaultUidGenerator.init();
        // 初始化MinIO配置
        ((MinioRepository) FileCenter.getRepository(StorageType.MINIO)).init(minioClient);
    }
}
