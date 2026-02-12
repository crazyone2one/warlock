package cn.master.horde.scheduler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : 11's papa
 * @since : 2026/2/12, 星期四
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface QuartzJob {
    String cron(); // 必须提供 cron 表达式

    String group() default "DEFAULT"; // 可选分组

    boolean autoStart() default false; // false 表示默认暂停

    String paramsJson() default "{}";
}
