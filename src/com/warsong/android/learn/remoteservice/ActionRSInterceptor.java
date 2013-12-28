package com.warsong.android.learn.remoteservice;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.warsong.android.learn.actionintercept.ActionInterceptDesc;
import com.warsong.android.learn.actionintercept.ActionInterceptManager;
import com.warsong.android.learn.actionintercept.annotation.ActionIntercept;

/**
 * view action 的rs拦截器
 *
 * @author zhanqu
 * @date 2013-12-14 下午12:06:40
 */
public class ActionRSInterceptor implements RSInterceptor {

    @Override
    public boolean preHandle(Object proxy, Class<?> clazz, Method method, Object[] args,
                             Annotation annotation) {
        return false;
    }

    @Override
    public boolean postHandle(Object proxy, Class<?> clazz, Method method, Object[] args,
                              Annotation annotation) {
        //从annotation定义中获取数据
        ActionIntercept a = (ActionIntercept)annotation;
        String id= a.id();
        ActionInterceptDesc desc = new ActionInterceptDesc();
        desc.id = a.id();
        desc.type = a.type();
        desc.uri = a.uri();
        desc.viewId = a.viewId();
        ActionInterceptManager.getInstance().addInterceptDesc(id, desc);
        return false;
    }

    @Override
    public boolean exceptionHandle(Object proxy, Class<?> clazz, Method method, Object[] args,
                                   Annotation annotation) {
        return false;
    }

}
