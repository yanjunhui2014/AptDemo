package com.milo.processor;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

/**
 * 标题：
 * 功能：
 * 备注：
 * <p>
 * Created by Milo  2020/3/8
 * E-Mail : 303767416@qq.com
 */
//public class BindingSet {
//
//    JavaFile brewJava(int sdk, boolean debuggable) {
//        TypeSpec bindingConfiguration = createType(sdk, debuggable);
//        return JavaFile.builder(bindingClassName.packageName(), bindingConfiguration)
//                .addFileComment("Generated code from Butter Knife. Do not modify!")
//                .build();
//        return null;
//    }
//
//    private TypeSpec createType(int sdk, boolean debuggable) {
//        TypeSpec.Builder result = TypeSpec.classBuilder(bindingClassName.simpleName())
//                .addModifiers(PUBLIC)
//                .addOriginatingElement(enclosingElement);
//        if (isFinal) {
//            result.addModifiers(FINAL);
//        }
//
//        if (parentBinding != null) {
//            result.superclass(parentBinding.getBindingClassName());
//        } else {
//            result.addSuperinterface(UNBINDER);
//        }
//
//        if (hasTargetField()) {
//            result.addField(targetTypeName, "target", PRIVATE);
//        }
//
//        if (isView) {
//            result.addMethod(createBindingConstructorForView());
//        } else if (isActivity) {
//            result.addMethod(createBindingConstructorForActivity());
//        } else if (isDialog) {
//            result.addMethod(createBindingConstructorForDialog());
//        }
//        if (!constructorNeedsView()) {
//            // Add a delegating constructor with a target type + view signature for reflective use.
//            result.addMethod(createBindingViewDelegateConstructor());
//        }
//        result.addMethod(createBindingConstructor(sdk, debuggable));
//
//        if (hasViewBindings() || parentBinding == null) {
//            result.addMethod(createBindingUnbindMethod(result));
//        }

//        return result.build();
//        return null;
//    }
//
//}
