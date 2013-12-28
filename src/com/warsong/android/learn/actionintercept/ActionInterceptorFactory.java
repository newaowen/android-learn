package com.warsong.android.learn.actionintercept;

import android.content.Context;

import com.warsong.android.learn.actionintercept.click.ClickInterceptor;

public class ActionInterceptorFactory {

    public static ActionInterceptor create(Context context, ActionInterceptDesc desc) {
        if (desc != null) {
            String type = desc.type;
            if (type.equals("click")) {
                return new ClickInterceptor(context, desc);
            }
        }
        
        return null;
    }
    
}

