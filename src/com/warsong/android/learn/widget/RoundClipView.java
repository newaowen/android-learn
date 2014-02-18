package com.warsong.android.learn.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class RoundClipView extends RelativeLayout {

    public RoundClipView(Context context) {
        super(context);
        init(context, null);
    }

    public RoundClipView(Context context, AttributeSet as) {
        super(context, as);
        init(context, as);
    }

    public RoundClipView(Context context, AttributeSet as, int defStyle) {
        super(context, as, defStyle);
        init(context, as);
    }

    private void init(Context context, AttributeSet attrs) {

    }

    /**
     * 仅支持scaleType为fitXY方式，不支持scroll, padding, 
     */
    @Override
    protected void onDraw(Canvas canvas) {
        Path clipPath = new Path();
        float radius = 30;
        float padding = radius / 2;
        int w = this.getWidth();
        int h = this.getHeight();
        clipPath.addRoundRect(new RectF(padding, padding, w - padding, h - padding), radius,
            radius, Path.Direction.CW);
        canvas.clipPath(clipPath);
        //super.onDraw(canvas);
    }

}
