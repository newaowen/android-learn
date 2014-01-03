package com.warsong.android.learn.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.warsong.android.learn.R;

public class PrintTextView extends TextView {

	public PrintTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PrintTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public PrintTextView(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.border));
	}
	
	public void print(String str) {
		setText(getText() + "\n" + str);
	}
	
	public void clear() {
		setText("");
	}

}
