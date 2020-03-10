package com.milo.processor;

import com.google.auto.service.AutoService;
import com.milo.annotation.BindView;
import com.milo.processor.impl.CreaterProxy;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
public class AptDemoProcessor extends AbstractProcessor {
    protected static final String VIEW_TYPE     = "android.view.View";
    protected static final String ACTIVITY_TYPE = "android.app.Activity";
    protected static final String DIALOG_TYPE   = "android.app.Dialog";
    protected static final String FRAGMENT_TYPE = "androidx.fragment.app.Fragment";

    private final Map<String, CreaterProxy<?>> mProxyMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(BindView.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment env) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "process start ...");
        mProxyMap.clear();

        Set<? extends Element> elements = env.getElementsAnnotatedWith(BindView.class);
        for (Element element : elements) {
            if (!(element instanceof VariableElement)) {
                throw new IllegalArgumentException("被绑定的Field必须是变量");
            }

            VariableElement varElement = (VariableElement) element;
            TypeElement classElement = (TypeElement) varElement.getEnclosingElement();

            final String proxyKey = classElement.getQualifiedName().toString();

            CreaterProxy proxy = mProxyMap.get(proxyKey);
            if (proxy == null) {
                synchronized (mProxyMap) {
                    proxy = mProxyMap.get(proxyKey);
                    if (proxy == null) {
                        //使用StringBuilder创建文件
//                        proxy = new StringCreaterProxy(processingEnv.getElementUtils(), classElement);
                        //使用javapoet创建文件
                        proxy = new PoetCreaterProxy(processingEnv.getElementUtils(), classElement);
                        mProxyMap.put(proxyKey, proxy);
                    }
                }
            }

            BindView bindAnnotation = varElement.getAnnotation(BindView.class);
            proxy.putElement(bindAnnotation.value(), varElement);
        }

        for (String key : mProxyMap.keySet()) {
            CreaterProxy proxy = mProxyMap.get(key);
            try {
                proxy.process(processingEnv);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "process end ...");
        return true;
    }

}
