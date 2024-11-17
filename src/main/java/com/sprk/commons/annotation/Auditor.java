package com.sprk.commons.annotation;

import com.sprk.commons.tag.Action;
import com.sprk.commons.tag.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditor {
    View[] allowedViews() default {};
    Action[] allowedActions() default {};
    boolean log() default true;
    boolean auditJwt() default true;
    boolean enableStrictMode() default false;
}
