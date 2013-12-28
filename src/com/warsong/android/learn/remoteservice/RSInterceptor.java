package com.warsong.android.learn.remoteservice;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * RS调用拦截器
 * 
 * @param <T>
 */
public interface RSInterceptor {
    /**
     * 前置处理
     * 
     * @param proxy 调用对象
     * @param retValue 返回值
     * @param clazz 类名
     * @param method 方法
     * @param args 参数
     * @return 是否需要被下一步处理
     * @throws RpcException
     */
    public boolean preHandle(Object proxy, Class<?> clazz, Method method, Object[] args,
                             Annotation annotation);

    /**
     * 后置处理
     * 
     * @param proxy 调用对象
     * @param retValue 返回值
     * @param clazz 类名
     * @param method 方法
     * @param args 参数
     * @return 是否需要被下一步处理
     * @throws RpcException
     */
    public boolean postHandle(Object proxy, Class<?> clazz, Method method, Object[] args,
                              Annotation annotation);

    /**
     * 异常处理
     * 
     * @param proxy 调用对象
     * @param retValue 返回值
     * @param clazz 类名
     * @param method 方法
     * @param args 参数
     * @param exception 异常
     * @return 是否需要被下一步处理
     * @throws RpcException
     */
    public boolean exceptionHandle(Object proxy, Class<?> clazz, Method method, Object[] args,
                                   Annotation annotation);
}
