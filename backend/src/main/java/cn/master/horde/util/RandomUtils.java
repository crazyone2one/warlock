package cn.master.horde.util;

import java.util.Random;

/**
 * @author : 11's papa
 * @since : 2026/1/21, 星期三
 **/
public class RandomUtils {
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random random = new Random();
    public static int generateRandomIntegerByLength(int length) {
        if (length <= 0 || length > 9) {
            throw new IllegalArgumentException("长度必须在1到9之间");
        }

        if (length == 1) {
            return random.nextInt(9) + 1; // 1-9
        } else {
            int min = (int) Math.pow(10, length - 1); // 最小值，如长度为4则是1000
            int max = (int) Math.pow(10, length) - 1; // 最大值，如长度为4则是9999
            return min + random.nextInt(max - min + 1);
        }
    }
}
