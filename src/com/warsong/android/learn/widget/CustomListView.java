package com.warsong.android.learn.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListView;

public class CustomListView extends ListView {

	private final String TAG = "CustomListView";
	
	public CustomListView(Context context) {
		super(context);
	}
	
	public CustomListView(Context context, AttributeSet as) {
		super(context, as);
	}
	
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		int mw = getMeasuredWidth();
//		int mh = getMeasuredHeight();
		//setMeasuredDimension(mw, mh - 300);
	}
	
	
	/**
	 * 布局
	 */
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		//bottom = bottom  - 500;
		super.onLayout(changed, left, top, right, bottom);
		Log.i(TAG, "onLayout: " + (right - left) + "," + bottom);
	}
	

}
