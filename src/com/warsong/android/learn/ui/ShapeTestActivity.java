package com.warsong.android.learn.ui;

import com.warsong.android.learn.R;

import android.app.Activity;
import android.os.Bundle;

/**
 * 视图组件的生命周期
 * @author newaowen@gmail.com
 * @date 2013-11-24 上午11:19:21
 */
public class ShapeTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shape);
    }

}
