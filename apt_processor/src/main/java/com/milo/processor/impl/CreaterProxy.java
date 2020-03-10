package com.milo.processor.impl;

import com.squareup.javapoet.ClassName;

import java.io.IOException;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * 标题：
 * 功能：
 * 备注：
 * <p>
 * Created by Milo  2020/3/10
 * E-Mail : 303767416@qq.com
 */
public interface CreaterProxy<T> {

    String getPackageName();

    String getClassName();

    String getMethodName();

    Elements getElements();

    TypeElement getTypeElement();

    void putElement(Integer value, Element element);

    T getJavaFile();

    void process(ProcessingEnvironment processingEnv) throws IOException;

}
