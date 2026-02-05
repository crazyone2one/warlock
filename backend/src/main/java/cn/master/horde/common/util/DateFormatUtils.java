package cn.master.horde.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author : 11's papa
 * @since : 2026/1/21, 星期三
 **/
public class DateFormatUtils {
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter UTC_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    /**
     * 返回yyyyMMddHHmmss格式的时间
     *
     * @param localDateTime 需要转换的时间
     * @return java.lang.String
     */
    public static String localDateTimeToString(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return localDateTime.format(formatter);
    }

    /**
     * 将LocalDateTime对象转换为指定时间格式(yyyy-MM-dd HH:mm:ss)的字符串
     *
     * @param localDateTime 要转换的LocalDateTime对象
     * @return 格式化后的时间字符串
     */
    public static String toTimePatternString(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_PATTERN);
        return localDateTime.format(formatter);
    }
}
