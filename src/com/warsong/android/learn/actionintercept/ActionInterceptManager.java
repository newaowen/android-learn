package com.warsong.android.learn.actionintercept;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.view.View;

/**
 * 拦截器管理
 *
 * @author zhanqu
 * @date 2013-12-13 下午2:31:31
 */
public class ActionInterceptManager {

    private static ActionInterceptManager instance;

    //全局拦截描述符
    private Map<String, ActionInterceptDesc> descs;
    
    //已生成的拦截器(descId -> ViewInterceptor)
    private Map<String, ActionInterceptor> interceptors;

    public static ActionInterceptManager getInstance() {
        if (instance == null) {
            //synchronized (instance) {
                instance = new ActionInterceptManager();
            //}
        }
        return instance;
    }
    
    protected ActionInterceptManager() {
        descs = new HashMap<String, ActionInterceptDesc>();
        interceptors = new HashMap<String, ActionInterceptor>();
    }

    public void addInterceptDesc(String id, ActionInterceptDesc desc) {
        descs.put(id, desc);
    }

    public void removeInterceptDesc(String id) {
        descs.remove(id);
    }

    public ActionInterceptDesc findInterceptDesc(String descId) {
        return null;
    }

    /**
     * 针对当前页面自动生成拦截
     */
    public void autoIntercept(Context context, View parent) {
        //each
        if (context != null && parent != null && descs != null) {
            for (ActionInterceptDesc desc : descs.values()) {
                View v = parent.findViewWithTag(desc.viewId);
                if (v != null) {
                    //自动生成拦截器
                    ActionInterceptor interceptor = ActionInterceptorFactory.create(context, desc);
                    interceptor.inject(v);
                    interceptors.put(desc.id, interceptor);
                }
            }
        }
    }
    
    /**
     * 自动解除view树内的拦截器
     * @param context
     * @param parent
     */
    public void autoUnIntercept(Context context, View parent) {
        //each
        if (context != null && parent != null && interceptors != null) {
            for (ActionInterceptDesc desc : descs.values()) {
                View v = parent.findViewWithTag(desc.viewId);
                if (v != null) {
                    ActionInterceptor interceptor = interceptors.get(desc.id);
                    if (interceptor != null && interceptor.isAlive()) {
                        interceptor.removeAll();
                    }
                }
            }
        }
    }

}
