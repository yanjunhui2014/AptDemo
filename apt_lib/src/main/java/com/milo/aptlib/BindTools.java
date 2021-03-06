package com.milo.aptlib;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

import androidx.fragment.app.Fragment;

import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 标题：
 * 功能：
 * 备注：
 * <p>
 * Created by Milo  2020/3/8
 * E-Mail : 303767416@qq.com
 */
public final class BindTools {

    public static void bindStrMode(Activity activity) {
        if (activity == null) {
            return;
        }

        Class clazz = activity.getClass();
        Class bindClazz = null;
        try {
            bindClazz = activity.getClassLoader().loadClass(clazz.getName() + "$String$ViewBind");
            Method method = bindClazz.getMethod("bindView", activity.getClass());
            method.invoke(bindClazz.newInstance(), activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static <T extends Activity> void bind(T activity) {
        if (activity == null) {
            return;
        }

        Class clazz = activity.getClass();
        Class bindClazz = null;
        try {
            bindClazz = activity.getClassLoader().loadClass(clazz.getName() + "$Poet$ViewBind");
            Constructor<?> constructor = bindClazz.getConstructor(activity.getClass());
            constructor.newInstance(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T extends Fragment> void bind(T fragment, View view) {
        if (fragment == null) {
            return;
        }

        Class clazz = fragment.getClass();
        Class bindClazz = null;
        try {
            bindClazz = Class.forName(clazz.getName() + "$Poet$ViewBind");
            Constructor<?> constructor = bindClazz.getConstructor(fragment.getClass(), View.class);
            constructor.newInstance(fragment, view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void bind(Dialog dialog) {

    }

}
