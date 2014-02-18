package com.warsong.android.learn.ui;

import com.warsong.android.learn.R;

import android.app.Activity;
import android.os.Bundle;

/**
 * view生命周期试
 * @author newaowen@gmail.com
 */
public class LinearTextViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linear_textview);
    }

}
