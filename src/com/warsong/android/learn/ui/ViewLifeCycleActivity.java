package com.warsong.android.learn.ui;

import com.warsong.android.learn.R;

import android.app.Activity;
import android.os.Bundle;

/**
 * shape绘图测试
 * @author newaowen@gmail.com
 */
public class ViewLifeCycleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shape);
    }

}
