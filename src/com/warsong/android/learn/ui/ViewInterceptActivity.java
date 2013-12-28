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
 * @author newaowen@gmail.com
 * @date 2013-11-24 上午11:19:21
 */
public class ViewInterceptActivity extends Activity {
    
    private final static String TAG = "ViewInterceptActivity";
    
    private View btn;
    private TextView text;
    
    private GestureDetector gestureDetector;
    
    private static int time = 0;
    
    private boolean longPressed = false;
    private boolean doubleTaped  = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_intercept);
        
        initView();
        injectAction();
    }
    
    private void initView() {
        btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick");
                text.setText("btn clicked" + (time ++));
            }
        });
        
        text = (TextView) findViewById(R.id.text);
        gestureDetector = new GestureDetector(this, new SimpleOnGestureListener() {
            
            /**
             * 可以屏蔽onclick, 但有个问题：按钮进入按下状态，但无法返回up状态
             */
            public boolean onSingleTapUp(MotionEvent e) {
                if (btn.isClickable()) {
                    btn.setPressed(false);
                }
                Log.i(TAG, "onSingleTapUp");
                Toast.makeText(ViewInterceptActivity.this, "intercept toast " + time, Toast.LENGTH_SHORT).show();
                return true;
            }
            
            /**
             * 双击的第一次按下之后触发ouDoubleTap，并在up之前执行
             */ 
            public boolean onDoubleTap(MotionEvent e) {
                Log.i(TAG, "onDoubleTap");
                doubleTaped = true;
                return false;
            }
            
            public boolean onDoubleTapEvent(MotionEvent e) {
            	return true;
            }
            
            /**
             * 长按up?
             */
            public void onLongPress(MotionEvent e) {
                longPressed = true;
            }
            
            /**
             * 在singeTap执行后
             */
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.i(TAG, "onSingleTapConfirmed");
                return false;
            }
        });
    }
    
    private void injectAction() {
        btn.setOnTouchListener(new View.OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ((longPressed == true || doubleTaped == true) && event.getAction() == MotionEvent.ACTION_UP) {
                    if (longPressed) {
                        Log.i(TAG, "longpressed up");
                        longPressed = false;
                        if (btn.isClickable()) {
                            btn.setPressed(false);
                        }
                        btn.performClick();
                    } else if (doubleTaped) {
                        Log.i(TAG, "doubleTap up");
                        doubleTaped = false;
                        if (btn.isClickable()) {
                            btn.setPressed(false);
                        }
                    }
                    return true;
                } else {
                    Log.i(TAG, "onTouch: " + event.getAction());
                    return gestureDetector.onTouchEvent(event);
                }
            }
        });
    }

}
