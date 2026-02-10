package cn.master.horde.common.file;

import cn.master.horde.common.constants.StorageType;
import cn.master.horde.common.util.CommonBeanFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
public class FileCenter {
    private FileCenter() {
    }

    public static FileRepository getRepository(StorageType storageType) {
        return switch (storageType) {
            case MINIO -> CommonBeanFactory.getBean(MinioRepository.class);
            case LOCAL -> CommonBeanFactory.getBean(LocalFileRepository.class);
            // case GIT -> CommonBeanFactory.getBean(GitRepository.class);
            default -> getDefaultRepository();
        };
    }

    public static FileRepository getRepository(String storage) {
        Map<String, StorageType> storageTypeMap = new HashMap<>() {{
            put(StorageType.MINIO.name(), StorageType.MINIO);
            put(StorageType.LOCAL.name(), StorageType.LOCAL);
            put(StorageType.GIT.name(), StorageType.GIT);
        }};

        return getRepository(storageTypeMap.get(storage.toUpperCase()));
    }

    public static FileRepository getDefaultRepository() {
        return CommonBeanFactory.getBean(MinioRepository.class);
    }

}
