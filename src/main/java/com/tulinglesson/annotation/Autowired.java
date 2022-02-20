package com.tulinglesson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author bahsk
 * @createTime 2022-02-10 0:34
 * @description
 * @program: lightframework
 *
 * ElementType.FIELD :Field declaration (includes enum constants)
 * RetentionPolicy.RUNTIME:  Annotations are to be recorded in the class file by the compiler
 *      and retained by the VM at run time,
 *      so they may be read reflectively.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Autowired {
    String value() default "";

}
