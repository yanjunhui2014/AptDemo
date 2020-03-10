package com.milo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import androidx.annotation.IdRes;

/**
 * 标题：绑定视图注解
 * 功能：
 * 备注：
 * <p>
 * Created by Milo  2020/3/7
 * E-Mail : 303767416@qq.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BindView {

    @IdRes
    int value();

}
