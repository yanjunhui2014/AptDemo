package com.milo.processor;

import com.milo.processor.impl.CreaterProxy;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

/**
 * 标题：
 * 功能：
 * 备注：
 * <p>
 * Created by Milo  2020/3/10
 * E-Mail : 303767416@qq.com
 */
public class PoetCreaterProxy implements CreaterProxy<JavaFile> {

    private Elements    mElements;
    private TypeElement mTypeElement;

    private       String mPackageName;
    private       String mClassName;
    private final String mMethodName;

    private final boolean  isView;
    private final boolean  isActivity;
    private final boolean  isFragment;
    private final boolean  isDialog;
    private final TypeName targetTypeName;

    private Map<Integer, Element> mElementsMap = new HashMap<>();

    private final ClassName UI_THREAD      = ClassName.get("androidx.annotation", "UiThread");
    private final ClassName CONTEXT_COMPAT = ClassName.get("androidx.core.content", "ContextCompat");

    private static final ClassName VIEW    = ClassName.get("android.view", "View");
    private static final ClassName CONTEXT = ClassName.get("android.content", "Context");

    public PoetCreaterProxy(Elements elements, TypeElement classElement) {
        this.mElements = elements;
        this.mTypeElement = classElement;

        this.mPackageName = mElements.getPackageOf(classElement).getQualifiedName().toString();
        this.mClassName = classElement.getSimpleName() + "$Poet$ViewBind";
        this.mMethodName = "bindView";

        TypeMirror typeMirror = classElement.asType();
        System.out.println("typeMirror == " + typeMirror.toString());

        isView = isSubtypeOfType(typeMirror, AptDemoProcessor.VIEW_TYPE);
        isActivity = isSubtypeOfType(typeMirror, AptDemoProcessor.ACTIVITY_TYPE);
        isFragment = isSubtypeOfType(typeMirror, AptDemoProcessor.FRAGMENT_TYPE);
        isDialog = isSubtypeOfType(typeMirror, AptDemoProcessor.DIALOG_TYPE);
        boolean isFinal = classElement.getModifiers().contains(Modifier.FINAL);

        System.out.println(classElement.getSimpleName() + " -- isView == " + isView + ", isActivity == " + isActivity + ", isFragment == " + isFragment + ", isDialog == " + isDialog);

        TypeName targetType = TypeName.get(typeMirror);
        if (targetType instanceof ParameterizedTypeName) {
            targetType = ((ParameterizedTypeName) targetType).rawType;
        }
        targetTypeName = targetType;
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
    public JavaFile getJavaFile() {
//        return JavaFile.builder(mPackageName, generateCode()).build();
        TypeSpec.Builder result = TypeSpec.classBuilder(getClassName())
                .addModifiers(Modifier.PUBLIC)
                .addOriginatingElement(mTypeElement);       //设置注解处理器的源元素
        //todo 实现继承父类的判断
        result.addSuperinterface(ClassName.get("com.milo.aptlib.impl", "Unbinder"))//添加接口
                .addField(targetTypeName, "target");
        if (isView) {
            result.addMethod(createBindingConstructorForView());
        } else if (isActivity) {
            result.addMethod(createBindingConstructorForActivity());
        } else if (isDialog) {
            result.addMethod(createBindingConstructorForDialog());
        }

        result.addMethod(createBindingConstructor());//添加绑定构造

        result.addMethod(createBindingUnbindMethod());//添加接触绑定方法

        return JavaFile.builder(mPackageName, result.build()).build();
    }

    @Override
    public void process(ProcessingEnvironment processingEnv) throws IOException {
        JavaFile javaFile = getJavaFile();
        javaFile.writeTo(processingEnv.getFiler());
    }

    private TypeSpec generateCode() {
        TypeSpec bindingClass = TypeSpec.classBuilder(mClassName)
                .addJavadoc("Created by apt, don`t edit this file")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(generateMethods())
                .build();
        return bindingClass;
    }

    private MethodSpec generateMethods() {
        ClassName host = ClassName.bestGuess(mTypeElement.getQualifiedName().toString());
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(mMethodName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(UI_THREAD)
                .returns(void.class)
                .addParameter(host, "host");

        for (int id : mElementsMap.keySet()) {
            Element element = mElementsMap.get(id);
            String name = element.getSimpleName().toString();
            String type = element.asType().toString();
            methodBuilder.addCode("host." + name + " = " + "(" + type + ")(((android.app.Activity)host).findViewById( " + id + "));");
        }
        return methodBuilder.build();
    }

    private MethodSpec createBindingConstructorForView() {
        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addAnnotation(UI_THREAD)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(targetTypeName, "target");
        if (constructorNeedsView()) {
            builder.addStatement("this(target, target)");
        } else {
            builder.addStatement("this(target, target.getContext())");
        }

        return builder.build();
    }

    private MethodSpec createBindingConstructorForActivity() {
        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addAnnotation(UI_THREAD)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(targetTypeName, "target");
        if (constructorNeedsView()) {
            builder.addStatement("this(target, target.getWindow().getDecorView())");
        } else {
            builder.addStatement("this(target, target)");
        }
        return builder.build();
    }

    private MethodSpec createBindingConstructorForDialog() {
        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addAnnotation(UI_THREAD)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(targetTypeName, "target");
        if (constructorNeedsView()) {
            builder.addStatement("this(target, target.getWindow().getDecorView())");
        } else {
            builder.addStatement("this(target, target.getContext())");
        }
        return builder.build();
    }

    private MethodSpec createBindingConstructor() {
        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addAnnotation(UI_THREAD)
                .addModifiers(Modifier.PUBLIC);

        constructor.addParameter(targetTypeName, "target", Modifier.FINAL);

        if (constructorNeedsView()) {
            constructor.addParameter(VIEW, "source");
        } else {
            constructor.addParameter(CONTEXT, "context");
        }

//        if (parentBinding != null) {
//            if (parentBinding.constructorNeedsView()) {
//                constructor.addStatement("super(target, source)");
//            } else if (constructorNeedsView()) {
//                constructor.addStatement("super(target, source.getContext())");
//            } else {
//                constructor.addStatement("super(target, context)");
//            }
//            constructor.addCode("\n");
//        }

//        if (hasTargetField()) {
        constructor.addStatement("this.target = target");
        constructor.addCode("\n");

        for (int id : mElementsMap.keySet()) {
            Element element = mElementsMap.get(id);
            addFieldBinding(constructor, element, id, false);
        }

        return constructor.build();
    }


    /**
     * 创建解绑的方法
     *
     * @return MethodSpec
     */
    private MethodSpec createBindingUnbindMethod() {
        MethodSpec.Builder result = MethodSpec.methodBuilder("unbind")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);
        result.addStatement("$T target = this.target", targetTypeName);
        result.addStatement("if (target == null) throw new $T($S)", IllegalStateException.class,
                "Bindings already cleared.");
        result.addStatement("$N = null", "this.target");
        result.addCode("\n");
        for (int id : mElementsMap.keySet()) {
            Element element = mElementsMap.get(id);
            result.addStatement("target.$L = null", element.getSimpleName());
        }

        return result.build();
    }

    private void addFieldBinding(MethodSpec.Builder result, Element element, int id, boolean requiresCast) {
        if (element == null) {
            return;
        }

        if (isView) {
            if (requiresCast) {
                result.addStatement("this.target.$L = ($T)(target.findViewById($L))", element.getSimpleName().toString(), element.asType(), id);
            } else {
                result.addStatement("this.target.$L = target.findViewById($L)", element.getSimpleName().toString(), id);
            }
        } else if (isActivity) {
            if (requiresCast) {
                result.addStatement("target.$L = ($T)(target.findViewById($L))", element.getSimpleName().toString(), element.asType(), id);
            } else {
                result.addStatement("target.$L = target.findViewById($L)", element.getSimpleName().toString(), id);
            }
        } else if (isFragment) {
            if (requiresCast) {
                result.addStatement("target.$L = ($T)(source.findViewById($L))", element.getSimpleName().toString(), element.asType(), id);
            } else {
                result.addStatement("target.$L = source.findViewById($L)", element.getSimpleName().toString(), id);
            }
        } else if (isDialog) {

        }
    }

    public boolean constructorNeedsView() {
        if (isFragment) {
            return true;
        }
        return false;
    }

//    public boolean constructorNeedsView() {
//        return hasViewBindings() || (parentBinding != null && parentBinding.constructorNeedsView());
//    }

    static boolean isSubtypeOfType(TypeMirror typeMirror, String otherType) {
        if (isTypeEqual(typeMirror, otherType)) {
            return true;
        }
        if (typeMirror.getKind() != TypeKind.DECLARED) {
            return false;
        }
        DeclaredType declaredType = (DeclaredType) typeMirror;
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        if (typeArguments.size() > 0) {
            StringBuilder typeString = new StringBuilder(declaredType.asElement().toString());
            typeString.append('<');
            for (int i = 0; i < typeArguments.size(); i++) {
                if (i > 0) {
                    typeString.append(',');
                }
                typeString.append('?');
            }
            typeString.append('>');
            if (typeString.toString().equals(otherType)) {
                return true;
            }
        }
        Element element = declaredType.asElement();
        if (!(element instanceof TypeElement)) {
            return false;
        }
        TypeElement typeElement = (TypeElement) element;
        TypeMirror superType = typeElement.getSuperclass();
        if (isSubtypeOfType(superType, otherType)) {
            return true;
        }
        for (TypeMirror interfaceType : typeElement.getInterfaces()) {
            if (isSubtypeOfType(interfaceType, otherType)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isTypeEqual(TypeMirror typeMirror, String otherType) {
        return otherType.equals(typeMirror.toString());
    }

}
