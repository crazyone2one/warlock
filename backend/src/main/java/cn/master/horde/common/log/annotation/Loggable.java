package cn.master.horde.common.log.annotation;

import cn.master.horde.common.constants.OperationLogType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : 11's papa
 * @since : 2026/1/22, 星期四
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
    String value() default "业务操作";

    OperationLogType type() default OperationLogType.SELECT;

    String expression();

    Class[] wClass() default {};
}
