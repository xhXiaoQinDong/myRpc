package com.xlfc.common.annotion;

import java.lang.annotation.*;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface Reference {
    String interfaceName() default "";

    String version() default "";

    String group() default "";
}
