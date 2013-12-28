package com.warsong.android.learn.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 心形mask
 * 
 * @author zhanqu
 * @date 2013-12-21 下午2:03:27
 */
public class HeartMaskView extends View {

    Paint paint = new Paint();

    Bitmap mask;

    Xfermode xFermode = new PorterDuffXfermode(PorterDuff.Mode.SRC);

    private int topMargin = 50;
    private int leftMargin = 50;

    public HeartMaskView(Context context) {
        super(context);
    }

    public HeartMaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeartMaskView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mask != null) {
            canvas.drawBitmap(mask, 0.0f, 0.0f, null);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int w = getWidth();//getMeasuredWidth();//getWidth();
        int h = getHeight();//getMeasuredHeight();//getHeight();
        Log.i("maskview", "onMeasure w,h: " + w + "," + h);
        if (w > 0 && h > 0) {
            if (mask == null) {
                mask = convertToAlphaMask(w, h);
            }
        }
    }

    private Bitmap convertToAlphaMask(int w, int h) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setXfermode(xFermode);
        p.setColor(0x99000000);

        //只取透明度
        Bitmap a = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8);
        Canvas c = new Canvas(a);
        c.drawRect(new Rect(0, 0, w, h), p);

        p.setColor(0x11000000);
        drawHeart3(c, p, w, h, topMargin, leftMargin);
        //drawHeart2(c, p, w, h, topMargin, leftMargin);
        //drawHeart(c, p);
        return a;
    }

    private void drawHeart3(Canvas c, Paint p, int w, int h, int topMargin, int leftMargin) {
        p.setStyle(Paint.Style.FILL);
        Path path = new Path();
        path.reset();

        //曲线方程：http://mathworld.wolfram.com/HeartCurve.html
        // x = [-1,1]
        // y = x + sqrt(1-x2)
        double scale = w / 2;
        double x = 0, y = 0;
        double range = 1;
        int i = 0;
        for (double t = -range; t <= range; t += 0.04) {
            x = transformX2(t, scale);
            y = transformY2(Math.abs(t) - Math.sqrt(1 - Math.pow(t, 2)), scale);
            if (i == 0) {
                path.moveTo((float) x, (float) y);
                i = 1;
            } else {
                path.lineTo((float) x, (float) y);
            }
        }
        //path.close();
        //c.drawPath(path, p);
        
        //Path p2 = new Path();
        //i = 0;
        //p2.reset();
        for (double t = range; t >= -range; t -= 0.01) {
            x = transformX2(t, scale);
            y = transformY2(Math.abs(t) + Math.sqrt(1 - Math.pow(t, 2)), scale);
            //if (i == 0) {
            //    path.moveTo((float) x, (float) y);
            //    i = 1;
            //} else {
                path.lineTo((float) x, (float) y);
            //}
        }
        //p2.close();
        c.drawPath(path, p);
    }

    //坐标变换
    private double transformX2(double x, double scale) {
        return x * scale + scale;
    }

    private double transformY2(double y, double scale) {
        return -scale * y + 1.5 * scale;
    }

    private void drawHeart2(Canvas c, Paint p, int w, int h, int topMargin, int leftMargin) {
        int hw = w - 2 * leftMargin;
        float p2 = (float) Math.pow(2, 0.5);
        float r = (float) (hw / (2 + Math.pow(2, 0.5)));
        float br = p2 * r;
        float mr = (float) (r / p2);

        p.setColor(0x11000000);
        c.drawCircle(r + leftMargin, r + topMargin, r, p);
        c.drawCircle(r + leftMargin + br, r + topMargin, r, p);

        //旋转拒行？
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(leftMargin + r - mr, topMargin + r + mr);
        path.lineTo(leftMargin + r + mr, topMargin + r - mr);
        path.lineTo(leftMargin + r + br + mr, topMargin + r + mr);
        path.lineTo(leftMargin + r + mr, topMargin + r + mr + br);
        path.close();
        c.drawPath(path, p);
    }

    /**
     * 绘制心形线条
     * 
     * @param canvas
     * @param p
     */
    private void drawHeart(Canvas canvas, Paint p) {
        Path path = new Path();
        path.reset();

        //曲线方程：http://mathworld.wolfram.com/HeartCurve.html
        // x = 16sin3(t) [立方]
        // y = 13cos(t) - 5cos(2t) - 2cos(3t) - cos(4t)
        double scale = getWidth() / 32;
        double x = 0, y = 0;
        double range = 3;
        int i = 0;
        for (double t = -range; t <= range; t += 0.08) {
            x = transformX(16 * Math.pow(Math.sin(t), 3), scale);
            y = transformY(
                13 * Math.cos(t) - 5 * Math.cos(t * 2) - 2 * Math.cos(3 * t) - Math.cos(4 * t),
                scale);
            if (i == 0) {
                path.moveTo((float) x, (float) y);
                i = 1;
            } else {
                path.lineTo((float) x, (float) y);
            }
        }

        p.setStyle(Paint.Style.FILL);
        path.close();
        canvas.drawPath(path, p);
    }

    //    private void drawHeart3(Canvas canvas, Paint p) {
    //        float mid = getWidth() / 2;
    //        float min = Math.min(getWidth(), getHeight());
    //        float fat = min / 17;
    //        float half = min / 2;
    //        float rad = half - fat;
    //        mid = mid - half;
    //
    //        paint.setStrokeWidth(fat);
    //        paint.setStyle(Paint.Style.STROKE);
    //
    //        canvas.drawCircle(mid + half, half, rad, p);
    //
    //        Path path = new Path();
    //        path.reset();
    //
    //        paint.setStyle(Paint.Style.FILL);
    //
    //        // top left
    //        path.moveTo(mid + half * 0.5f, half * 0.84f);
    //        // top right
    //        path.lineTo(mid + half * 1.5f, half * 0.84f);
    //        // bottom left
    //        path.lineTo(mid + half * 0.68f, half * 1.45f);
    //        // top tip
    //        path.lineTo(mid + half * 1.0f, half * 0.5f);
    //        // bottom right
    //        path.lineTo(mid + half * 1.32f, half * 1.45f);
    //        // top left
    //        path.lineTo(mid + half * 0.5f, half * 0.84f);
    //
    //        path.close();
    //        canvas.drawPath(path, paint);
    //    }

    //坐标变换
    private double transformX(double x, double scale) {
        return x * scale + scale * 16;
    }

    private double transformY(double y, double scale) {
        return -scale * y + scale * 16;
    }

}
