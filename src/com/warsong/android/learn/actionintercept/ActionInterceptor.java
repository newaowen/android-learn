package com.warsong.android.learn.actionintercept;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;

/**
 * 抽象接口 视图拦截器
 * 同一个拦截器支持注入到多个view中, 同时也支持一次解除多个view的绑定
 * 
 * @author zhanqu
 * @date 2013-12-13 下午2:17:26
 */
public abstract class ActionInterceptor {
    
    protected Context context;
    
    protected ActionInterceptDesc desc;
    
    protected List<View> targetView;

    public ActionInterceptor(Context context, ActionInterceptDesc desc) {
        this.context = context;
        this.desc = desc;
        targetView = new ArrayList<View>();
    }
    
    /**
     * 添加拦截目标
     * @param v
     */
    public void add(View v) {
        if (v != null) {
            targetView.add(v);
            inject(v);
        }
    }
    
    /**
     * 删除拦截目标
     */
    public void remove(View v) {
//        if (targetView.contains(v)) {
//            targetView.remove(i);
//        }
    }
    
    public void removeAll() {
        for (View v : targetView) {
            uninject(v);
        }
        targetView.clear();
    }
    
    protected void inject(View v) {
        
    }
    
    protected void uninject(View v) {
        
    }
    
    /**
     * 是否有效的
     * @return
     */
    public boolean isAlive() {
        return false;
    }
    
    protected void preExec() {
        
    }
    
    protected void postExec() {
        
    }
    
}
