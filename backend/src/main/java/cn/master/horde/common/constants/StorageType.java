package cn.master.horde.common.constants;

import org.apache.commons.lang3.Strings;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
public enum StorageType {
    MINIO, GIT, LOCAL;

    public static boolean isGit(String storage) {
        return Strings.CS.equals(GIT.name(), storage);
    }
}
