package com.milo.processor;

import com.milo.processor.impl.CreaterProxy;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

/**
 * 标题：
 * 功能：
 * 备注：
 * <p>
 * Created by Milo  2020/3/10
 * E-Mail : 303767416@qq.com
 */
public class StringCreaterProxy implements CreaterProxy<String> {

    private Elements    mElements;
    private TypeElement mTypeElement;

    private       String mPackageName;
    private       String mClassName;
    private final String mMethodName;

    private Map<Integer, Element> mElementsMap = new HashMap<>();

    private StringCreaterProxy() {
        this.mMethodName = "bindView";
    }

    public StringCreaterProxy(Elements elements, TypeElement classElement) {
        this.mElements = elements;
        this.mTypeElement = classElement;

        this.mPackageName = mElements.getPackageOf(classElement).getQualifiedName().toString();
        this.mClassName = classElement.getSimpleName() + "$String$ViewBind";
        this.mMethodName = "bindView";
    }

    @Override
    public String getPackageName() {
        return mPackageName;
    }

    @Override
    public String getClassName() {
        return mClassName;
    }

    @Override
    public String getMethodName() {
        return mMethodName;
    }

    @Override
    public Elements getElements() {
        return mElements;
    }

    @Override
    public TypeElement getTypeElement() {
        return mTypeElement;
    }

    @Override
    public void putElement(Integer value, Element element) {
        mElementsMap.put(value, element);
    }

    @Override
    public String getJavaFile() {
        return generateCode();
    }

    @Override
    public void process(ProcessingEnvironment processingEnv) throws IOException {
        JavaFileObject jfo = processingEnv.getFiler().createSourceFile(getClassName(), getTypeElement());
        Writer writer = jfo.openWriter();
        writer.write(getJavaFile());
        writer.flush();
        writer.close();
    }

    private String generateCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("package ").append(mPackageName).append(";\n\n")
                .append('\n')
                .append("/**Created by apt, don`t edit this file*/\n")
                .append("public class " + mClassName + " {\n");

        generateMethods(builder);
        builder.append('\n');
        builder.append("}\n");
        return builder.toString();
    }

    private void generateMethods(StringBuilder builder) {
        builder.append("\n public void " + mMethodName + "(" + mTypeElement.getQualifiedName() + " host ) {\n");
        for (int id : mElementsMap.keySet()) {
            Element element = mElementsMap.get(id);
            String name = element.getSimpleName().toString();
            String type = element.asType().toString();
            builder.append("   host." + name).append(" = ");
            builder.append("(" + type + ")(((android.app.Activity)host).findViewById(" + id + "));\n");
        }
        builder.append(" }\n");
    }

}
