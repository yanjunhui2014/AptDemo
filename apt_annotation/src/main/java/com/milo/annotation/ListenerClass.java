package com.milo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标题：
 * 功能：
 * 备注：
 * <p>
 * Created by Milo  2020/3/8
 * E-Mail : 303767416@qq.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ListenerClass {

    String targetType();

    String setter();

    String remover() default "";

    String type();

    Class<? extends Enum<?>> callbacks() default ListenerClass.NONE.class;

    ListenerMethod[] method() default {};

    enum NONE{}
}


