package com.warsong.android.learn.remoteservice;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RSInvocationHandler implements InvocationHandler {

    /**
     * 接口类
     */
    private Class<?> mClazz;
    
    private RSInvoker rsInvoker;
    
    /**
     * @param clazz 类
     * @param rpcInvoker RpcInvoker
     */
    public RSInvocationHandler(Class<?> clazz, RSInvoker rsInvoker) {
        mClazz = clazz;
        this.rsInvoker = rsInvoker;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return rsInvoker.invoke(this, proxy, mClazz, method, args);
    }

}
