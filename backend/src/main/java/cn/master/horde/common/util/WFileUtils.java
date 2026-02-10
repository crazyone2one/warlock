package cn.master.horde.common.util;

import cn.master.horde.common.result.BizException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import java.io.File;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
public class WFileUtils {
    public static void validateFileName(String... fileNames) {
        if (fileNames != null) {
            for (String fileName : fileNames) {
                if (StringUtils.isNotBlank(fileName) && Strings.CS.contains(fileName, "." + File.separator)) {
                    throw new BizException(Translator.get("invalid_parameter"));
                }
            }
        }
    }
}
