package com.warsong.android.learn.remoteservice;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.warsong.android.learn.actionintercept.annotation.ActionIntercept;

public class RSFactory {

    /**
     * 配置
     */
    //private Config mConfig;

    private static RSFactory instance;
    /**
     * RPC调用器
     */
    private RSInvoker rsInvoker;

    /**
     * 拦截器
     */
    private Map<Class<? extends Annotation>, RSInterceptor> mInterceptors;

    public static RSFactory getInstance() {
        if (instance == null) {
            instance = new RSFactory();
        }
        return instance;
    }

    protected RSFactory() {
        mInterceptors = new HashMap<Class<? extends Annotation>, RSInterceptor>();
        rsInvoker = new RSInvoker(this);

        //mock 自动添加ViewActionInterceptor
        ActionRSInterceptor v = new ActionRSInterceptor();
        addInterceptor(ActionIntercept.class, v);
    }

    @SuppressWarnings("unchecked")
    public <T> T getRpcProxy(Class<T> clazz) {
        //        typeChecker.isValidInterface(clazz);
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz },
            new RSInvocationHandler(clazz, rsInvoker));
    }

    /**
     * 添加拦截器
     * 
     * @param clazz 对应的注解类
     * @param rpcInterceptor 拦截器
     */
    public void addInterceptor(Class<? extends Annotation> clazz, RSInterceptor interceptor) {
        mInterceptors.put(clazz, interceptor);
    }

    /**
     * 通过注解查找拦截器
     * 
     * @param clazz 注解类型
     * @return 拦截器
     */
    public RSInterceptor findInterceptor(Class<? extends Annotation> clazz) {
        return mInterceptors.get(clazz);
    }

}
