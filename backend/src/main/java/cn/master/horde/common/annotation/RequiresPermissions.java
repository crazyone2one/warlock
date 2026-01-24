package cn.master.horde.common.annotation;

import cn.master.horde.core.constant.Logical;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : 11's papa
 * @since : 2026/1/23, 星期五
 **/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermissions {
    String[] value();

    Logical logical() default Logical.AND;
}
