package com.warsong.android.learn.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.warsong.android.learn.R;
import com.warsong.android.learn.widget.PullRefreshView.TopView;

/**
 * 默认下拉刷新View的OverView
 * 
 * @author sanping.li@alipay.com
 * 
 */
public class DefaultPullRefreshTopView extends TopView {

	private View mNormalView;
	private View mLoadingView;

	private ProgressBar mProgressBar;

	private TextView mPrompt;
	private ImageView mIndicator;

	// private int mTextColor;
	private Drawable mIndicatorUpDrawable;
	private Drawable mIndicatorDownDrawable;
	private Drawable mProgressDrawable;

	private Animation mOpenAnimation;
	private Animation mCloseAnimation;
	private AnimationListener mAnimationListener;

	public DefaultPullRefreshTopView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttrs(context, attrs, defStyle);
	}

	public DefaultPullRefreshTopView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttrs(context, attrs, 0);
	}

	public DefaultPullRefreshTopView(Context context) {
		super(context);
	}

	private void initAttrs(Context context, AttributeSet attrs, int defStyle) {
		// TypedArray a = context.obtainStyledAttributes(attrs,
		// R.styleable.framework_pullrefresh_overview, defStyle,
		// R.style.framework_pullrefresh_overview);
		//
		// mIndicatorUpDrawable = a
		// .getDrawable(R.styleable.framework_pullrefresh_overview_framework_pullrefresh_indicator_upDrawable);
		// mIndicatorDownDrawable = a
		// .getDrawable(R.styleable.framework_pullrefresh_overview_framework_pullrefresh_indicator_downDrawable);
		// // mTextColor = R.color.colorBlack;
		// mProgressDrawable = a
		// .getDrawable(R.styleable.framework_pullrefresh_overview_framework_pullrefresh_progressDrawable);
		// a.recycle();
	}

	@Override
	public void init() {
		mAnimationListener = new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (animation == mCloseAnimation) {
					if (mIndicatorUpDrawable != null) {
						mIndicator.setImageDrawable(mIndicatorUpDrawable);
					}
				} else {
					if (mIndicatorDownDrawable != null) {
						mIndicator.setImageDrawable(mIndicatorDownDrawable);
						mIndicatorDownDrawable.setLevel(10000);
					}
				}
			}
		};
		// Load all of the animations we need in code rather than through XML
		mOpenAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mOpenAnimation.setInterpolator(new LinearInterpolator());
		mOpenAnimation.setDuration(250);
		mOpenAnimation.setAnimationListener(mAnimationListener);
		mCloseAnimation = new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mCloseAnimation.setInterpolator(new LinearInterpolator());
		mCloseAnimation.setDuration(250);
		mCloseAnimation.setAnimationListener(mAnimationListener);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (getId() == NO_ID) {
			throw new RuntimeException("must set id");
		}

		mNormalView = findViewById(R.id.framework_pullrefresh_normal);
		mLoadingView = findViewById(R.id.framework_pullrefresh_loading);
		mProgressBar = (ProgressBar) findViewById(R.id.framework_pullrefresh_progress);
		mPrompt = (TextView) findViewById(R.id.framework_pullrefresh_prompt);
		mIndicator = (ImageView) findViewById(R.id.framework_pullrefresh_indicator);

		if (mIndicatorUpDrawable != null) {
			mIndicator.setImageDrawable(mIndicatorUpDrawable);
		}
		if (mProgressDrawable != null) {
			mProgressBar.setIndeterminateDrawable(mProgressDrawable);
		}
	}

	@Override
	public void onOpen() {
	//	mIndicator.clearAnimation();
		mPrompt.setText("下拉");
	//	mIndicator.startAnimation(mOpenAnimation);
	}

	@Override
	public void onOver() {
	//	mIndicator.clearAnimation();
		  mPrompt.setText("释放");
	//	mIndicator.startAnimation(mCloseAnimation);
	}

	@Override
	public void onLoad() {
		mNormalView.setVisibility(GONE);
		mLoadingView.setVisibility(VISIBLE);
	}

	@Override
	public void onFinish() {
		mPrompt.setText("刷新中");
		mNormalView.setVisibility(VISIBLE);
		mLoadingView.setVisibility(GONE);
	}

	public View getNormalView() {
		return mNormalView;
	}
}