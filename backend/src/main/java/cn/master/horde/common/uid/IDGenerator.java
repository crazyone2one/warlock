package cn.master.horde.common.uid;

import cn.master.horde.common.util.CommonBeanFactory;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
public class IDGenerator {
    private static final DefaultUidGenerator DEFAULT_UID_GENERATOR;

    static {
        DEFAULT_UID_GENERATOR = CommonBeanFactory.getBean(DefaultUidGenerator.class);
    }

    public static Long nextNum() {
        return DEFAULT_UID_GENERATOR.getUID();
    }

    public static String nextStr() {
        return String.valueOf(DEFAULT_UID_GENERATOR.getUID());
    }
}
