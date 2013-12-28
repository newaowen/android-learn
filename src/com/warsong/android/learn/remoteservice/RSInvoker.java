package com.warsong.android.learn.remoteservice;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import com.warsong.android.learn.actionintercept.annotation.ActionIntercept;

public class RSInvoker {

    /**
     * RPC工厂
     */
    private RSFactory mFactory;

    public RSInvoker(RSFactory factory) {
        mFactory = factory;
//        mRandom = new Random();
        //rpcSequence = new AtomicInteger();
    }
    
    /**
     * @param clazz 类
     * @param method 方法
     * @param args 参数
     * @return 数据
     * @throws RpcException
     */
    public Object invoke(RSInvocationHandler handler, Object proxy, Class<?> clazz, Method method,
                         Object[] args) {
        //if (ThreadUtil.checkMainThread())
        //    throw new IllegalThreadStateException("can't in main thread call rpc .");
        //ViewActionInject operationType = method.getAnnotation(ViewActionInject.class);
        //Type retType = method.getGenericReturnType();
        Annotation[] annotations = method.getAnnotations();
        //RETURN_VALUE.set(null);//初始化返回值

        //if (operationType == null) {
        //    throw new IllegalStateException("OperationType must be set.");
        //}
        //String operationTypeValue = operationType.value();
        //        int id = mRandom.nextInt(Integer.MAX_VALUE);
        //int id = rpcSequence.incrementAndGet();
        preHandle(proxy, clazz, method, args, annotations);//前置拦截
        
        String data = singleCall(handler, method, args);

       // PerformanceLog.getInstance().log("RPC start: operationTypeValue=" + operationTypeValue);
//        try {
//            if (mMode == MODE_DEFAULT) {
//                String data = singleCall(handler, method, args, operationTypeValue, id);
//                Deserializer deserializer = handler.getDeserializer(retType, data);
//                Object object = deserializer.parser();
//                if (retType != Void.TYPE) {//非void
//                    RETURN_VALUE.set(object);
//                }
//            } else {
//                //TODO 批量调用
//            }
//
//        } catch (RpcException exception) {
//            exception.setOperationType(operationTypeValue);//异常设置OperationType
//            exceptionHandle(proxy, clazz, method, args, annotations, exception);//异常拦截
//        }

        postHandle(proxy, clazz, method, args, annotations);//后置拦截
       // PerformanceLog.getInstance().log("RPC finish: operationTypeValue=" + operationTypeValue);
        return data;
    }
    
    /**
     * 前置处理
     * 
     * @param proxy 调用对象
     * @param clazz
     * @param method
     * @param args
     * @param annotations
     * @throws RpcException
     */
    private void preHandle(final Object proxy, final Class<?> clazz, final Method method,
                           final Object[] args, Annotation[] annotations)   {
        handleAnnotations(annotations, new Handle() {
            @Override
            public boolean handle(RSInterceptor rpcInterceptor, Annotation annotation)  {
                if (!rpcInterceptor.preHandle(proxy,  clazz, method, args, annotation)) {
                  //  throw new RpcException(RpcException.ErrorCode.CLIENT_HANDLE_ERROR,
                  //      rpcInterceptor + "preHandle stop this call.");
                }
                return true;
            }
        });
    }
    
    /**
     * 后置处理
     * 
     * @param proxy 调用对象
     * @param clazz
     * @param method
     * @param args
     * @param annotations
     * @throws RpcException
     */
    private void postHandle(final Object proxy, final Class<?> clazz, final Method method,
                            final Object[] args, Annotation[] annotations)   {
        handleAnnotations(annotations, new Handle() {
            @Override
            public boolean handle(RSInterceptor rpcInterceptor, Annotation annotation)  {
                if (!rpcInterceptor.postHandle(proxy, clazz, method, args, annotation)) {
                   // throw new RpcException(RpcException.ErrorCode.CLIENT_HANDLE_ERROR,
                    //    rpcInterceptor + "postHandle stop this call.");
                }
                return true;
            }
        });
    }
    
    /**
     * 处理所有拦截
     * 
     * @param annotations
     * @param handle
     * @throws RpcException
     */
    private boolean handleAnnotations(Annotation[] annotations, Handle handle) {
        boolean ret = true;
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> c = annotation.annotationType();
            RSInterceptor rpcInterceptor = mFactory.findInterceptor(c);
            if (rpcInterceptor == null) {
//                throw new RpcException(RpcException.ErrorCode.CLIENT_HANDLE_ERROR,
//                    "can not find Interceptor :" + c);
            }
            ret = handle.handle(rpcInterceptor, annotation);
            if (!ret) {
                break;
            }
        }
        return ret;
    }
    
    private String singleCall(RSInvocationHandler handler, Method method, Object[] args) {
//        String data = null;
//        //robin
//        if(operationTypeValue.contains("alipay.user.login")) {
//            LogCatLog.e("RpcInvoker", "alipay.user.login");
//        }
//        Serializer serializer = handler.getSerializer(id, operationTypeValue, args);//数据格式协议
//        //LogCatLog.v("RpcInvoker", "operationType ["+operationTypeValue + serializer.packet()+"]");
//        
//        RpcCaller caller = handler.getTransport(method, serializer.packet());//传输
//        data = (String) caller.call();//同步
//        
//        //LogCatLog.v("RpcInvoker", "responseData ["+ data +"]");
        return "";
    }
    
    private interface Handle {
        public boolean handle(RSInterceptor interceptor, Annotation annotation);
    }

}
