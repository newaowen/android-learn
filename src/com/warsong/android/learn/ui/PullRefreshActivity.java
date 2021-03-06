package com.warsong.android.learn.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;

import com.warsong.android.learn.R;
import com.warsong.android.learn.widget.DefaultPullRefreshTopView;
import com.warsong.android.learn.widget.PullRefreshView;
import com.warsong.android.learn.widget.PullRefreshView.TopView;

/**
 * 如果Activity内所有view group都不响应touch down事件，最终将回传到Activity.onTouchEvent,
 * 注意后续的touch up, touch move事件直接在Activity.dispatchTouchEvent中调用DetorView.superDispatchTouchEvent
 * 即ViewGroup的dispatchTouchEvent（必定返回false)，最后调用Activity.onTouchEvent
 * 
 * @author newaowen@gmail.com
 * @date 2013-11-11 上午11:00:44
 */
public class PullRefreshActivity extends Activity {

	private static final String TAG = "PullRefreshActivity";
	
	private PullRefreshView mPullRefreshView;
	private DefaultPullRefreshTopView topView;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.pullrefresh);
		
		init();
		
		//Uri uri = Uri.parse("alipays://platformapi/startapp?appId=20000003&actionType=toBillList");
		Uri uri = Uri.parse("alipays://platformapi/startapp?appId=20000001&actionType=20000003");
		Intent i = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(i);
	}
	
	public boolean dispatchTouchEvent(MotionEvent e) {
		Log.i(TAG, "dispatchTouchEvent MotionEvent e: " + e.getAction());
		boolean result = super.dispatchTouchEvent(e);
		return result;
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		boolean result = super.onTouchEvent(event);
		Log.i(TAG, "onTouchEvent MotionEvent e: " + event.getAction());
		return result;
	}
	
	private void init() {
		 mPullRefreshView = (PullRefreshView)findViewById(R.id.PullRefreshView);
		topView = (DefaultPullRefreshTopView) LayoutInflater.from(this).inflate(
				R.layout.default_overview, null);

		mPullRefreshView.setEnablePull(true);
		mPullRefreshView.setRefreshListener(new PullRefreshView.RefreshListener() {
			// 下拉完成之后的操作，刷新数据等
			@Override
			public void onRefresh() {
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						mPullRefreshView.refreshFinished();
					}
				}, 1000);
			}

			@Override
			public TopView getTopView() {
				return topView;
			}

			@Override
			public boolean canRefresh() {
				return true;
			}
		});
	}

}
