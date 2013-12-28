package com.warsong.android.learn.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class AutoFixView extends View {
	
	public MeasuareCallback measureCallback;

	public AutoFixView(Context context) {
		super(context);
	}
	
	public AutoFixView(Context context, AttributeSet as) {
		super(context, as);
	}

	public MeasuareCallback getMeasureCallback() {
		return measureCallback;
	}

	public void setMeasureCallback(MeasuareCallback measureCallback) {
		this.measureCallback = measureCallback;
	}
	
	public void setMD(int width, int height) {
		setMeasuredDimension(width, height);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (measureCallback != null) {
			measureCallback.run();
		}
	}
	
	public interface MeasuareCallback {
		public void run();
	}
}
