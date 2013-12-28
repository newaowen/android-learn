package com.warsong.android.learn.ui;

import com.warsong.android.learn.R;

import android.app.Activity;
import android.os.Bundle;

/**
 * 图片延迟加载测试
 * @author newaowen@gmail.com
 */
public class ImageLazyLoadActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_lazy_load);
    }

}
