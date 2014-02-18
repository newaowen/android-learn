package com.warsong.android.learn.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * 解决ScrollView嵌套ViewPager出现的滑动冲突问题
 * @author wei.dongww
 * @version $Id: ScrollViewExtend.java, v 0.1 2013-6-18 下午6:28:34 wei.dongww Exp $
 */
public class ScrollViewExtend extends ScrollView {

    // 标记位，防止在持续slide时角度大于某值时触发滚动条
    private boolean canScroll;

    private GestureDetector mGestureDetector;

    View.OnTouchListener mGestureListener;

    // 滚动(Y轴)和内部view滑动(X轴)的比值，当Y轴为x*ratio时，才触发滚动条事件
    public float deltaRatio = 4.0f;

    /**
     * @param context
     * @param attrs
     */
    public ScrollViewExtend(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(new YScrollDetector());
        canScroll = true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 当slide结束时才重新开启滚动
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            canScroll = true;
        }
        return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
    }

    class YScrollDetector extends SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (canScroll) {
                if (Math.abs(distanceY) >= deltaRatio * Math.abs(distanceX)) {
                    canScroll = true;
                } else {
                    canScroll = false;
                }
            }
            return canScroll;
        }
    }

}
