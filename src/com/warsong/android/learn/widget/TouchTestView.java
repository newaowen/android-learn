package com.warsong.android.learn.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class TouchTestView extends View {

	private static final String TAG = "TouchTestView";

	public TouchTestView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TouchTestView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public TouchTestView(Context context) {
		super(context);
		init();
	}

	private void init() {
//		setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//			}
//		});
	}

    public boolean dispatchTouchEvent(MotionEvent e) {
    	Log.i(TAG, "dispatchTouchEvent MotionEvent e: " + e.getAction());
    	return super.dispatchTouchEvent(e);
    }
	
	/**
	 * why??
	 * 默认只能捕获touch down
	 * 当设置clickable时可捕获到最基本的touch　down, up, move事件
	 */
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		Log.i(TAG, "onTouchEvent MotionEvent e: " + e.getAction());
		return super.onTouchEvent(e);
	}

}
