package com.warsong.android.learn.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.warsong.android.learn.R;

/**
 * 容器大小测试
 * padding不会影响容器大小, margin要根据情况影响
 * 1. 当父不为scrollview时，当前view width/height为fill_parent时，margin会影响容器的实际大小
 * 2. 当父不为scrollview时，当前view width/height为具体高度值时，当w/h+margin小于父容器尺寸时，w/h不变;
 *    当w/h+margin大于父容器尺寸时，则调整容器尺寸不超过父容器大小
 * 3. 当父为scrollView时，margin不影响容器的实际大小
 * @author newaowen@gmail.com
 */
public class LayoutSizeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_size);
        
        final ViewGroup v = (ViewGroup)findViewById(R.id.container);
        v.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
			
			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
					int oldTop, int oldRight, int oldBottom) {
				Log.i("LayoutSizeActivity", "container layoutchange: " + (right-left) +"," + (bottom-top));
				Log.i("LayoutSizeActivity", "container size: " + v.getWidth() +"," + v.getHeight());
			}
		});
        
        final View t = findViewById(R.id.target);
        t.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
			
			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
					int oldTop, int oldRight, int oldBottom) {
				Log.i("LayoutSizeActivity", "target layoutchange: " + (right-left) +"," + (bottom-top));
				Log.i("LayoutSizeActivity", "target size: " + t.getWidth() +"," + t.getHeight());
				Log.i("LayoutSizeActivity", "target size: " + t.getMeasuredWidth() +"," + t.getMeasuredHeight());
			}
		});
    }

}
