package com.warsong.android.learn.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.warsong.android.learn.R;
import com.warsong.android.learn.util.GeneralUtil;

/**
 * 下拉刷新View
 * 
 * ViewGroup默认clickable为false,因此只有down事件，
 */
public class PullRefreshView extends FrameLayout implements OnGestureListener {
	private static final String TAG = "PullRefreshView";

	private static final byte STATE_CLOSE = 0;
	private static final byte STATE_OPEN = STATE_CLOSE + 1;
	private static final byte STATE_OVER = STATE_OPEN + 1; //下拉超出top高度
	private static final byte STATE_OPEN_RELEASE = STATE_OVER + 1;  //开始释放
	private static final byte STATE_OVER_RELEASE = STATE_OPEN_RELEASE + 1; //释放结束
	private static final byte STATE_REFRESH = STATE_OVER_RELEASE + 1; //开始刷新
	private static final byte STATE_REFRESH_RELEASE = STATE_REFRESH + 1; //??
	private byte mState;

	private GestureDetector mGestureDetector;
	private Flinger mFlinger;

	private RefreshListener mRefreshListener;
	/**
	 * Overlay视图
	 */
	protected TopView mOverView;

	private int mLastY;
	// private int mMargin;
	protected int mMaxMargin;

	private boolean mEnablePull = true;
	
	private int moveDownDetail = 0;

	public PullRefreshView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PullRefreshView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public PullRefreshView(Context context) {
		super(context);
		init();
	}

	@SuppressWarnings("deprecation")
	private void init() {
		mGestureDetector = new GestureDetector(this);
		mFlinger = new Flinger();

		Log.i(TAG, "clicable: " + isClickable());
	}

	/**
	 * 设置刷新接口
	 */
	public void setRefreshListener(RefreshListener refreshListener) {
		if (mOverView != null) {
			removeView(mOverView);
		}
		mRefreshListener = refreshListener;
		initListener();
	}

	@SuppressWarnings("deprecation")
	private void initListener() {
		// 从回调中获取TopView??
		mOverView = mRefreshListener.getTopView();
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		addView(mOverView, 0, params);

		getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						int height = mOverView.findViewById(R.id.framework_pullrefresh_loading)
								.getMeasuredHeight();
						mMaxMargin = height;
						Log.w(TAG, "onGlobalLayout getMaxMargin: " + mMaxMargin);

						getViewTreeObserver().removeGlobalOnLayoutListener(this);
					}
				});
	}

	@Override
	public boolean onDown(MotionEvent evn) {
		Log.i(TAG, "GestureDetector onDown");
		// 必须返回true才行
		return true;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
		Log.i(TAG, "GestureDetector onFling");
		// onFling返回false的含义是？？
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		Log.i(TAG, "GestureDetector onLongPress");
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		Log.i(TAG, "GestureDetector onShowPress");
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float disX, float disY) {
		Log.i(TAG, "GestureDetector onScroll");
		Log.i("pullrefreshview", "onScroll (disX, disY): " + disX + ", " + disY);
		if (Math.abs(disX) > Math.abs(disY))
			return false;
		if (null != mRefreshListener && !mRefreshListener.canRefresh()) {
			return false;
		}
		View head = getChildAt(0);
		View child = getChildAt(1);
		// if (!(child instanceof AdapterView<?>)) {//
		// 对账单做兼容,账单中adapterView需要使用framelayout包裹,第一个控件不是AdapterView
		// child = ((ViewGroup) getChildAt(1)).getChildAt(0);
		// }

		if (child instanceof AdapterView<?>) {
			if (((AdapterView<?>) child).getFirstVisiblePosition() != 0
					|| (((AdapterView<?>) child).getFirstVisiblePosition() == 0
							&& ((AdapterView<?>) child).getChildAt(0) != null && ((AdapterView<?>) child)
							.getChildAt(0).getTop() < 0))
				return false;
		}
		if ((mState == STATE_REFRESH && head.getTop() > 0 && disY > 0)
				|| (child.getTop() <= 0 && disY > 0)) {
			return false;
		}
		if (mState == STATE_OPEN_RELEASE || mState == STATE_OVER_RELEASE
				|| mState == STATE_REFRESH_RELEASE)
			return false;
		int speed = mLastY;
		//当head完全展示后　减慢下拉速度
		if (head.getTop() >= 0)
			speed = mLastY / 5;

		Log.i(TAG, "onScroll to moveDown(speed,lastY): " + speed + ", " + mLastY);
		boolean bool = moveDown(speed, true);
		mLastY = (int) -disY;
		return bool;
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		// 调用View的onTouchEvent, 为什么只能接收到down, 没有up???
		boolean r = mGestureDetector.onTouchEvent(e);
		// Log.i(TAG, "onTouchEvent MotionEvent e: " + e.getAction() +
		// ", result: " + r);
		
		View head = getChildAt(0);
		if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_CANCEL
				|| e.getAction() == MotionEvent.ACTION_POINTER_2_UP
				|| e.getAction() == MotionEvent.ACTION_POINTER_3_UP) {
			if (head.getBottom() > 0) {
				if (mState == STATE_REFRESH && head.getBottom() > mMaxMargin) { //head刚好拉到完全展开时释放
					Log.w(TAG, "state == STATE_REFRESH, head.getBottom()=" + head.getBottom());
					release(head.getBottom() - mMaxMargin);
					return false;
				} else if (mState != STATE_REFRESH) { // 释放操作
					Log.w(TAG, "(onTouchEvent)state != STATE_REFRESH");
					release(head.getBottom());
					return false;
				}
			}
		}
		
		return r;
	}

	/**
	 * 执行释放，dis为需要释放的像素距离(delta)
	 * 
	 * @param dis
	 */
	private void release(int dis) {
		if (mRefreshListener != null && dis > mMaxMargin) { //header从超过完全展开的位置开始释放
			mState = STATE_OVER_RELEASE;
			//只释放dis-maxMargin,使topView停在刚好完全展开的情况
			mFlinger.recover(dis - mMaxMargin);
			Log.w(TAG, "(release)header从超过完全展开的位置开始释放: " + (dis - mMaxMargin));
		} else { //header从小于或等于完全展开的位置开始释放 
			mState = STATE_OPEN_RELEASE;
			//释放到header完全隐藏
			mFlinger.recover(dis);
			Log.w(TAG, "(release)header从小于或等于完全展开的位置开始释放: " + dis);
		}
	}

	public boolean dispatchTouchEvent(MotionEvent ev) {
		Log.i(TAG, "dispatchTouchEvent...");

		if (!mEnablePull)
			return super.dispatchTouchEvent(ev);
		if (!mFlinger.isFinished())
			return false;

		View head = getChildAt(0);
		if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL
				|| ev.getAction() == MotionEvent.ACTION_POINTER_2_UP
				|| ev.getAction() == MotionEvent.ACTION_POINTER_3_UP) {
			if (head.getBottom() > 0) {
				if (mState == STATE_REFRESH && head.getBottom() > mMaxMargin) {
					Log.i("pullrefreshview",
							"state == STATE_REFRESH, head.getBottom()=" + head.getBottom());
					release(head.getBottom() - mMaxMargin);
					return false;
				} else if (mState != STATE_REFRESH) {
					Log.i("pullrefreshview", "state != STATE_REFRESH");
					release(head.getBottom());
					return false;
				}
			}
		}

		boolean bool = mGestureDetector.onTouchEvent(ev);
		Log.i(TAG, "mGestureDetector mstate: " + mState);
		Log.i(TAG,
				"mGestureDetector onTouchEvent result: " + bool + ",head bottom: "
						+ head.getBottom());
		if ((bool || (mState != STATE_CLOSE && mState != STATE_REFRESH)) && head.getBottom() != 0) {
			if (bool) {
				Log.i("pullrefreshview", "gesture consumed");
			} else {
				Log.i("pullrefreshview", "not state close, not state refresh, cancel event");
			}
			ev.setAction(MotionEvent.ACTION_CANCEL);
			return super.dispatchTouchEvent(ev);
		}

		if (bool) {
			return true;
		} else {
			Log.i(TAG, "super dispatchTouchEvent");
			return super.dispatchTouchEvent(ev);
		}
	}

	/**
	 * 自动滚动
	 */
	private class Flinger implements Runnable {
		private Scroller mScroller;
		private int mLastY;
		private boolean mIsFinished;

		public Flinger() {
			mScroller = new Scroller(getContext());
			mIsFinished = true;
		}

		@Override
		public void run() {
			boolean b = mScroller.computeScrollOffset();
			if (b) {
				moveDown(mLastY - mScroller.getCurrY(), false);
				mLastY = mScroller.getCurrY();
				post(this);
			} else {
				mIsFinished = true;
				removeCallbacks(this);
			}
		}

		public void recover(int dis) {
			if (dis <= 0) {
				return;
			}
			removeCallbacks(this);
			mLastY = 0;
			mIsFinished = false;
			Log.w(TAG, "(flinger)recover: " + dis);
			mScroller.startScroll(0, 0, 0, dis, 300);
			post(this);
		}

		public boolean isFinished() {
			return mIsFinished;
		}
	}

	/**
	 * onLayout是有作用的
	 */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		View head = getChildAt(0);
		View child = getChildAt(1);
		int y;
		if (child != null) {
			y = child.getTop();
		} else {
			y = 0;
		}
 		
		if (moveDownDetail != 0) {
			head.offsetTopAndBottom(moveDownDetail);
			child.offsetTopAndBottom(moveDownDetail);
		} else {
			
		}

		if (mState == STATE_REFRESH) {
			//refresh状态下，保持位置不变（如果不在onLayout中写这句代码，refresh状态下head,layout都没法显示!!!
			head.layout(0, mMaxMargin - head.getMeasuredHeight(), right, head.getMeasuredHeight());
			if (child != null) {
				child.layout(0, mMaxMargin, right, mMaxMargin + child.getMeasuredHeight());
			}
		} else {
			//以child当前位置调整head
			head.layout(0, y - head.getMeasuredHeight(), right, y);
			if (child != null) {
				child.layout(0, y, right, y + child.getMeasuredHeight());
			}
		}

		View other = null;
		for (int i = 2; i < getChildCount(); ++i) {
			other = getChildAt(i);
			other.layout(0, top, right, bottom);
		}
	}

	private boolean moveDown(int dis, boolean changeState) {
		View head = getChildAt(0);
		View child = getChildAt(1);

		//moveDownDetail = 0;
		//Log.w(TAG, "(moveDown)dis,childTop: " + dis + ", " + child.getTop());
		//这个加法很奇怪啊
		// dis是要向下移动的相对距离，top为list的top
		//　child的目标top
		int childNextTop = child.getTop() + dis;
		Log.i(TAG, " moveDown  childTop: " + childNextTop);
		if (childNextTop <= 0) { // refresh完后的自动回收分支
			Log.i(TAG, "(recover)moveDown dis,childTop" + dis + ", " + childNextTop);
			if (childNextTop < 0) {
				dis = -child.getTop();
			}
			head.offsetTopAndBottom(dis);
			child.offsetTopAndBottom(dis);
			if (mState != STATE_REFRESH) {
				Log.i(TAG, "set state to STATE_CLOSE");
				mState = STATE_CLOSE;
			}
		} else if (childNextTop <= mMaxMargin) { 
			// 1.从顶部下拉出header到header完全显示 2.release后header自动向上滚到顶部, 3.刷新完后header滚动消失
			Log.w(TAG, "(moveDown)[下拉或释放到顶部]dis,childTop: " + dis + ", " + child.getTop());
			
			if (mOverView.getState() != TopView.STATE_OPEN) {
				mOverView.onOpen();
				mOverView.setState(TopView.STATE_OPEN);
			}

			head.offsetTopAndBottom(dis);
			child.offsetTopAndBottom(dis);
			if (changeState && mState != STATE_REFRESH) { // 下拉出header
				mState = STATE_OPEN;
				Log.i(TAG, "set state to STATE_OPEN");
			} else if (childNextTop <= mMaxMargin && mState == STATE_OVER_RELEASE) { // topview处于 反弹到刚好完全展开的状态
				Log.w(TAG, "(moveDown)已经反弹到刚好完全展开的状态,开始刷新");
				refresh();
			}
			
		} else if (mState != STATE_REFRESH) { //header下拉到超出本身高度的分支 
			Log.w(TAG, "(moveDown)topview已经完全展开, 继续下拉");
			if (mOverView.getState() != TopView.STATE_OVER) { // 设置topview为完成下拉展开状态
				  mOverView.onOver();
				 mOverView.setState(TopView.STATE_OVER);
			}
			
			moveDownDetail = dis;
			head.offsetTopAndBottom(dis);
			child.offsetTopAndBottom(dis);
			if (changeState) {
				mState = STATE_OVER;
			}
		} else { //保存当前位置？
			
		}
		
		invalidate();
		//requestLayout();
		return true;
	}

	/**
	 * 刷新
	 */
	private void refresh() {
		if (mRefreshListener != null) {
			Log.w(TAG, "refresh!");
			mState = STATE_REFRESH;
			mOverView.onLoad();
			mOverView.setState(TopView.STATE_LOAD);
			mRefreshListener.onRefresh();
		}
	}

	/**
	 * 刷新完成
	 * 
	 * 必须在主线程调用，且需要在refresh被调用后的下个tick才能调用
	 */
	public void refreshFinished() {
		View head = getChildAt(0);
		mOverView.onFinish();
		mOverView.setState(TopView.STATE_FINISH);
		if (head.getBottom() > 0) {
			mState = STATE_REFRESH_RELEASE;
			release(head.getBottom());
		} else {
			mState = STATE_CLOSE;
		}
	}

	/**
	 * 设置是否可下拉刷新
	 * 
	 * @param enablePull
	 *            是否可下拉刷新
	 */
	public void setEnablePull(boolean enablePull) {
		mEnablePull = enablePull;
	}

	public void autoRefresh() {
		mState = STATE_REFRESH;
		mOverView.onLoad();
		mOverView.setState(TopView.STATE_LOAD);
		requestLayout();
	}

	/**
	 * 刷新接口
	 */
	public interface RefreshListener {
		/**
		 * 刷新
		 */
		public void onRefresh();

		/**
		 * 获取OverView
		 * 
		 * @return OverView
		 */
		public TopView getTopView();

		/**
		 * 是否可刷新
		 * 
		 * @return
		 */
		public boolean canRefresh();
	}

	/**
	 * 下拉刷新的Overlay视图,可以重载这个类来定义自己的Overlay
	 * 
	 * @author sanping.li
	 * 
	 */
	public static abstract class TopView extends FrameLayout {
		public static final byte STATE_CLOSE = 0;
		public static final byte STATE_OPEN = STATE_CLOSE + 1;
		public static final byte STATE_OVER = STATE_OPEN + 1;
		public static final byte STATE_LOAD = STATE_OVER + 1;
		public static final byte STATE_FINISH = STATE_LOAD + 1;

		protected byte mState = STATE_CLOSE;

		public TopView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			init();
		}

		public TopView(Context context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}

		public TopView(Context context) {
			super(context);
			init();
		}

		/**
		 * 初始化
		 */
		public abstract void init();

		/**
		 * 下拉开始时显示top
		 */
		protected abstract void onOpen();

		/**
		 * 下拉超过topview本身高度后释放, 进入释放状态
		 */
		public abstract void onOver();

		/**
		 * 释放完成后进入开始refresh装
		 */
		public abstract void onLoad();

		/**
		 * refresh完成
		 */
		public abstract void onFinish();

		/**
		 * 设置状态
		 * 
		 * @param state
		 *            状态
		 */
		public void setState(byte state) {
			mState = state;
		}

		/**
		 * 获取状态
		 * 
		 * @return 状态
		 */
		public byte getState() {
			return mState;
		}
	}
}