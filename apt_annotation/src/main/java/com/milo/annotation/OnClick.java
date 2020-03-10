package com.milo.annotation;

import androidx.annotation.IdRes;

import com.milo.annotation.internal.Constants;

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
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ListenerClass(
        targetType = "android.view.View",
        setter = "setOnClickListener",
        type = "butterknife.internal.DebouncingOnClickListener",
        method = @ListenerMethod(
                name = "doClick",
                parameters = "android.view.View"
        )
)
public @interface OnClick {
    @IdRes int[] value() default (Constants.NO_ID);
}

