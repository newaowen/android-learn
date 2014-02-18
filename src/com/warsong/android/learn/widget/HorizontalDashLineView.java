package com.warsong.android.learn.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class HorizontalDashLineView extends View {

    private Paint paint;
    private DashPathEffect effect;
    private Path path;

    public HorizontalDashLineView(Context context) {
        super(context);
        init();
    }

    public HorizontalDashLineView(Context context, AttributeSet as) {
        super(context, as);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        
        //set your own color
        paint.setColor(0xffffffff);
        path = new Path();
        //array is ON and OFF distances in px (4px line then 2px space)
        effect = new DashPathEffect(new float[] { 2, 2 }, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        paint.setPathEffect(effect);
        int h = getHeight();
        int w = getWidth();
        if (h <= w) {
            // horizontal
            path.moveTo(0, 0);
            path.lineTo(w, 0);
            canvas.drawPath(path, paint);
        } else {
            // vertical
            path.moveTo(0, 0);
            path.lineTo(0, h);
            canvas.drawPath(path, paint);
        }
    }

}
