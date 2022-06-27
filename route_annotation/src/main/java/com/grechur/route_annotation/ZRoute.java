package com.grechur.route_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *ElementType.TYPE 使用在接口、类、枚举、注解之上
 * ElementType.FIELD 字段、枚举的常量
 * ElementType.METHOD 方法
 * ElementType.PARAMETER 方法参数
 * ElementType.CONSTRUCTOR 构造方法
 * ElementType.LOCAL_VARIABLE 局部变量
 * ElementType.ANNOTATION_TYPE 注解
 * ElementType.PACKAGE 包
 */
@Target(ElementType.TYPE)
/**
 *注解的生命周期
 * //RetentionPolicy.SOURCE  源码阶段
 * //RetentionPolicy.CLASS   编译阶段
 * //RetentionPolicy.RUNTIME 运行阶段
 */
@Retention(RetentionPolicy.CLASS)
public @interface ZRoute {
    String path();

    String group() default "";
}
