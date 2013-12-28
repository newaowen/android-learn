package com.warsong.android.learn.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.warsong.android.learn.R;
import com.warsong.android.learn.misc.ResizeAnimation;
import com.warsong.android.learn.widget.AutoFixView;

public class AutoFixViewActivity extends Activity {

	private View content;
	AutoFixView afv;
	View top;
	View bottom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.auto_fix_view);

		content = findViewById(R.id.content);

		//非动画情况下不需要
//		content.getViewTreeObserver().addOnGlobalLayoutListener(
//				new ViewTreeObserver.OnGlobalLayoutListener() {
//
//					@Override
//					public void onGlobalLayout() {
//						resizeInner();
//					}
//				});

		top = (View) findViewById(R.id.top);
		bottom = (View) findViewById(R.id.bottom);
		afv = (AutoFixView) findViewById(R.id.afv);
		afv.setMeasureCallback(new AutoFixView.MeasuareCallback() {

			@Override
			public void run() {
				resizeInner();
			}
		});

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				runAnimate();
			}
		}, 500);
	}

	protected void resizeInner() {
		int cw = content.getMeasuredWidth();
		int ch = content.getMeasuredHeight();
		int childH = afv.getHeight() + top.getHeight() + bottom.getHeight();
		int minusHeight = ch - top.getHeight() - bottom.getHeight();
		if (childH > ch) { // 父容器空间不够，将inner view调小，并清空margin
			int d = Math.min(cw, minusHeight);
			((MarginLayoutParams) afv.getLayoutParams()).setMargins(0, 0, 0, 0);
			afv.setMD(d, d);
		} else if (childH < ch) {
			// 父容器空间足够, 将inner view剧中显示
			int marginTop = (ch - afv.getHeight() - bottom.getHeight()) / 2;
			((MarginLayoutParams) afv.getLayoutParams()).setMargins(0, marginTop, 0, 0);
		}
	}

	protected void runAnimate() {
		// 播放resize动画
		ResizeAnimation anim = new ResizeAnimation(content, content.getWidth(),
				content.getHeight(), content.getWidth(), content.getHeight() / 2);
		anim.setDuration(2000);
		//content.startAnimation(anim);
	}

}
