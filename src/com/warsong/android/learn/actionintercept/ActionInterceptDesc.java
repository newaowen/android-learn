package com.warsong.android.learn.actionintercept;

import android.os.Bundle;

/**
 * 拦截器描述
 *
 * @author zhanqu
 * @date 2013-12-13 下午2:18:07
 */
public class ActionInterceptDesc {

    //拦截器id(可用对应的具体业务标记)
    public String id;
    
    public String viewId;
    
    //拦截器类型
    public String type;
    
    //跳转uri
    public String uri;
    
    //附加参数
    public Bundle extra;
    
}
