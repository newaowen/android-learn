package com.warsong.android.learn.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * view生命周期test
 * 
 * 结果：onFinishInflate -> onAttachedToWindow -> onMeasure -> onLayout -> onDraw
 * 
 * @author newaowen@gmail.com
 * @date 2013-11-8 下午8:48:03
 */
public class LifeCycleView extends View {

	private final static String TAG = "LifeCycleView";
	
	public LifeCycleView(Context context) {
		super(context);
	}
	
	public LifeCycleView(Context context, AttributeSet as) {
		super(context, as);
	}
	
	protected void onFinishInflate() {
		super.onFinishInflate();
		Log.i(TAG, "onFinishInflate");
	}
	
	//requestLayout, 可能会触发父容器重新布局
	/**
	 * 测量大小（可能会调用子组件的measure)
	 * @see android.view.View#onMeasure(int, int)
	 */
	protected void onMeasure(int widthSpec, int heightSpec) {
		super.onMeasure(widthSpec, heightSpec);
		Log.i(TAG, "onMeasure");
	}
	
	/**
	 * 布局
	 */
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		Log.i(TAG, "onLayout");
	}
	
	/**
	 * 只负责绘制
	 */
	protected void onDraw(Canvas cvs) {
		super.onDraw(cvs);
		Log.i(TAG, "onDraw");
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		Log.i(TAG, "onAttachedToWindow");
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		Log.i(TAG, "onDetachedFromWindow");
	}
}
