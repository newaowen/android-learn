package com.warsong.android.learn.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.warsong.android.learn.R;

/**
 * 视图拦截器测试
 * 
 * @author newaowen@gmail.com
 * @date 2013-11-24 上午11:19:21
 */
public class ViewInterceptActivity extends Activity {

	private final static String TAG = "ViewInterceptActivity";

	private View btn;
	private TextView text;

	private GestureDetector gestureDetector;

	private static int clickTime = 0;
	private static int interTime = 0;

	private boolean longPressed = false;
	private boolean doubleTaped = false;
	private boolean interrupted = false;

	private TextView logTV;

	private View clearBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_intercept);

		logTV = (TextView) findViewById(R.id.log);
		clearBtn = findViewById(R.id.clear);
		clearBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				logTV.setText("");
			}
		});
		
		initView();
		injectAction();
	}

	private void initView() {
		btn = findViewById(R.id.btn);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				log("exec onClick");
				// text.setText("btn clicked" + (clickTime++));
			}
		});
		btn.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				log("exec onLongClick");
				return false;
			}
		});

		text = (TextView) findViewById(R.id.text);
		gestureDetector = new GestureDetector(this, new SimpleOnGestureListener() {

			public boolean onDown(MotionEvent e) {
				longPressed = false;
				return false;
			}

			/**
			 * 可以屏蔽onclick, 但有个问题：按钮进入按下状态，但无法返回up状态
			 */
			public boolean onSingleTapUp(MotionEvent e) {
				// longpress bugfix
				if (interrupted) {
					unpress(btn);
				}
				trigger();
				return interrupted;
			}

			/**
			 * 双击的按下之后触发ouDoubleTap，并在onDoubleTapEvent之前执行
			 */
			public boolean onDoubleTap(MotionEvent e) {
				log("onDoubleTap");
				doubleTaped = true;
				if (interrupted) {
					unpress(btn);
				}
				trigger();
				return interrupted;
			}

			public boolean onDoubleTapEvent(MotionEvent e) {
				log("onDoubleTapEvent");
				return false;
			}

			/**
			 * 长按（按下状态超过一段时间后触发)
			 */
			public void onLongPress(MotionEvent e) {
				log("longPressed: " + e.getAction());
				if (interrupted) {
					unpress(btn);
				}
				trigger();
				longPressed = true;
			}

			/**
			 * 在singeTap执行后
			 */
			public boolean onSingleTapConfirmed(MotionEvent e) {
				log("onSingleTapConfirmed");
				return false;
			}
		});
	}

	private void trigger() {
		log("exec intercept");
		// Toast.makeText(ViewInterceptActivity.this, "intercept toast " +
		// (interTime++),
		// Toast.LENGTH_SHORT).show();
	}

	private void injectAction() {
		btn.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
//				if (doubleTaped) {
//					log("doubleTapped, onTouch -> return false");
//					doubleTaped = false;
//					unpress(v);
//					return false;
//				}
//
//				if (longPressed && event.getAction() == MotionEvent.ACTION_UP) {
//					log("onTouch to long pressed");
//					longPressed = false;
//					if (interrupted) {
//						unpress(v);
//					}
//					trigger();
//					return interrupted;
//				} else {
//					log("onTouch " + event.getAction());
//					return gestureDetector.onTouchEvent(event);
//				}
			}
		});
	}

	private void log(String text) {
		logTV.setText(logTV.getText() + "\n" + text);
	}

	private void unpress(View v) {
		if (v.isClickable()) {
			v.setPressed(false);
		}
	}

}
