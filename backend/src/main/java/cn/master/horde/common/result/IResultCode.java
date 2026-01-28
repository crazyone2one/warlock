package cn.master.horde.common.result;

import cn.master.horde.util.Translator;

/**
 * @author : 11's papa
 * @since : 2026/1/15, 星期四
 **/
public interface IResultCode {
    /**
     * 返回状态码
     */
    int getCode();

    /**
     * 返回状态码信息
     */
    String getMessage();

    default String getTranslationMessage(String message) {
        return Translator.get(message, message);
    }
}
