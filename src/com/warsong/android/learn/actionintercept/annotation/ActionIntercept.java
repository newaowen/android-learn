package com.warsong.android.learn.actionintercept.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 视图动作拦截注解
 *
 * @author zhanqu
 * @date 2013-12-13 下午4:40:10
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionIntercept {
    
    public String id();
    
    public String type();
    
    public String viewId();
    
    public String uri();
    
}

