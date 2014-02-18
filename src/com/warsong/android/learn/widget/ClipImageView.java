package com.warsong.android.learn.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.warsong.android.learn.R;

public class ClipImageView extends ImageView {

    private float percentage = 0.3f;

    private int direction = DIRECTION_HORIZONTAL;

    public static int DIRECTION_HORIZONTAL = 1;
    public static int DIRECTION_VERTIAL = 2;

    public ClipImageView(Context context) {
        super(context);
        init(context, null);
    }

    public ClipImageView(Context context, AttributeSet as) {
        super(context, as);
        init(context, as);
    }

    public ClipImageView(Context context, AttributeSet as, int defStyle) {
        super(context, as, defStyle);
        init(context, as);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.clip_image_view);
            percentage = a.getFloat(R.styleable.clip_image_view_percentage, percentage);
            direction = a.getInt(R.styleable.clip_image_view_direction, direction);
            a.recycle();
        }
    }

    /**
     * 仅支持scaleType为fitXY方式，不支持scroll, padding, 
     */
    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            super.onDraw(canvas);
        }

        // canvas.save();
        if (direction == DIRECTION_HORIZONTAL) {
            canvas.clipRect(0, 0, (int) (getWidth() * percentage), getHeight());
        } else {
            canvas.clipRect(0, 0, getWidth(), (int) (getHeight() * percentage));
        }
        drawable.draw(canvas);
        //canvas.restore();

        //Paint paint = new Paint();
        //paint.setColor(Color.BLUE);
        // 设置样式-填充   
        //paint.setStyle(Style.FILL);   
        //Rect rect = new Rect(0,0,getWidth(),getHeight());
        //canvas.drawRect(rect, paint);
        //canvas.restore();
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

}
