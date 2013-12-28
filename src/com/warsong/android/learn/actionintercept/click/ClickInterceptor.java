package com.warsong.android.learn.actionintercept.click;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.warsong.android.learn.actionintercept.ActionInterceptDesc;
import com.warsong.android.learn.actionintercept.ActionInterceptor;

public class ClickInterceptor extends ActionInterceptor {

    private final static String TAG = "ViewClickInterceptor";

    public ClickInterceptor(Context context, ActionInterceptDesc desc) {
        super(context, desc);
    }

    /**
     * TODO desc类型校验
     */
    protected boolean checkDescType() {
        return true;
    }

    @Override
    protected void preExec() {
        if (desc != null) {
            Intent i = new Intent(Intent.ACTION_DEFAULT, Uri.parse(desc.uri));
            context.startActivity(i);
        }
    }

    @Override
    protected void postExec() {
        //ignore
    }

    protected void inject(View v) {
        final CustomOnGestureListener listener = new CustomOnGestureListener(v);
        final GestureDetector gestureDetector = new GestureDetector(context, listener);

        v.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ((listener.longPressed == true || listener.doubleTaped == true)
                    && event.getAction() == MotionEvent.ACTION_UP) {
                    if (listener.longPressed) {
                        Log.i(TAG, "longpressed up");
                        listener.longPressed = false;
                        unPressView(v);
                    } else if (listener.doubleTaped) {
                        Log.i(TAG, "doubleTap up");
                        listener.doubleTaped = false;
                        unPressView(v);
                    }
                    return false;
                } else {
                    Log.i(TAG, "onTouch: " + event.getAction());
                    return gestureDetector.onTouchEvent(event);
                }
            }
        });
    }

    @Override
    protected void uninject(View v) {
        v.setOnTouchListener(null);
    }

    public class CustomOnGestureListener extends SimpleOnGestureListener {
        public View v;

        public boolean longPressed = false;
        public boolean doubleTaped = false;

        public CustomOnGestureListener(View v) {
            this.v = v;
        }

        /**
         * 可以屏蔽onclick, 但有个问题：按钮进入按下状态，但无法返回up状态
         */
        public boolean onSingleTapUp(MotionEvent e) {
            unPressView(v);
            Log.i(TAG, "onSingleTapUp");
            preExec();
            Toast.makeText(context, "intercept toast ", Toast.LENGTH_SHORT).show();
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
    }

    private void unPressView(View v) {
        if (v.isClickable()) {
            v.setPressed(false);
        }
    }

}
