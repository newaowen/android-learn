package com.warsong.android.learn.remoteservice.test;

import com.warsong.android.learn.actionintercept.annotation.ActionIntercept;

/**
 * 测试用的rs
 *
 * @author zhanqu
 * @date 2013-12-14 下午1:11:22
 */
public interface TestRS {

    @ActionIntercept(id="mock",type="click", viewId="btn", uri="tel:10086")
    public String doSomething();
    //    return "dosomething";
    //}
    
}
