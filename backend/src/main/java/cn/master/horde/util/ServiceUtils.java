package cn.master.horde.util;

import cn.master.horde.common.result.BizException;

import static cn.master.horde.common.result.ResultCode.NOT_FOUND;

/**
 * @author : 11's papa
 * @since : 2026/1/26, 星期一
 **/
public class ServiceUtils {
    //用于排序的pos
    public static final int POS_STEP = 4096;

    /**
     * 保存资源名称，在处理 NOT_FOUND 异常时，拼接资源名称
     */
    private static final ThreadLocal<String> resourceName = new ThreadLocal<>();
    public static <T> T checkResourceExist(T resource, String name) {
        if (resource == null) {
            resourceName.set(name);
            throw new BizException(NOT_FOUND);
        }
        return resource;
    }

    public static String getResourceName() {
        return resourceName.get();
    }

    public static void clearResourceName() {
        resourceName.remove();
    }
}
