package com.xlfc.common.annotion;

import java.lang.annotation.*;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface Service {
    String interfaceName() default "";

    String version() default "";

    String group() default "";
}
