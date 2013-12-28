package com.warsong.android.learn.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class OvalMaskView extends View {

    Paint paint = new Paint();

    Bitmap mask;

    Xfermode xFermode = new PorterDuffXfermode(PorterDuff.Mode.SRC);

    public OvalMaskView(Context context) {
        super(context);
    }

    public OvalMaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OvalMaskView(Context context, AttributeSet attrs, int defStyle) {
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

        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        Log.i("maskview", "onMeasure w,h: " + w + "," + h);
        if (w > 0 && h > 0) {
            if (mask == null) {
                mask = convertToAlphaMask(w, h);
                //Shader targetShader = createShader(mask);
                //paint.setShader(targetShader);
            }
            //Bitmap b = circleAlphaMask(w, h);
            //Shader t2 = createShader(b);

            //RadialGradient r = getRadialShader(w, h);
            //paint.setShader (new ComposeShader (targetShader, t2, PorterDuff.Mode.SRC));
        }
    }

    //    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    //        super.onLayout(changed, left, top, right, bottom);
    //        if (changed) {
    //            int w = right - left;
    //            int h = bottom - top;
    //            Log.i("maskview", "onLayout w,h: " + w + "," + h);
    //            if (w > 0 && h > 0) {
    //                mask = convertToAlphaMask(w, h);
    //                Shader targetShader = createShader(mask);
    //
    //                Bitmap b = circleAlphaMask(w, h);
    //                Shader t2 = createShader(b);
    //
    //                paint.setShader(t2);
    //
    //                //RadialGradient r = getRadialShader(w, h);
    //                //paint.setShader (new ComposeShader (targetShader, t2, PorterDuff.Mode.SRC));
    //            }
    //        }
    //    }

    private Bitmap convertToAlphaMask(int w, int h) {
        Paint p = new Paint();
         p.setAntiAlias(true);
         p.setXfermode(xFermode);
        //只取透明度
        Bitmap a = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8);
        Canvas c = new Canvas(a);

        //绘制矩形
        p.setColor(0x77000000);
        RectF r = new RectF(0, 0, w, h);
        c.drawRect(r, p);
        //增加blur效果 
        p.setColor(0x11000000);
        p.setMaskFilter(new BlurMaskFilter(70, BlurMaskFilter.Blur.NORMAL));
        RectF oval = new RectF(0,0,w,h);
        c.drawOval(oval, p);
        return a;
    }

//    private Bitmap circleAlphaMask(int w, int h) {
//        Paint p = new Paint();
//        p.setAntiAlias(true);
//        p.setXfermode(xFermode);
//
//        //只取透明度
//        Bitmap a = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8);
//        Canvas c = new Canvas(a);
//        c.setDensity(320);
//
//        p.setColor(0xee000000);
//        c.drawCircle(w / 2, h / 2, w / 2, p);
//        return a;
//    }
//
//    private RadialGradient getRadialShader(int w, int h) {
//        int[] colors = new int[] { 0, 0, 0xff000000 };
//        float[] pos = new float[] { 0.0f, 0.8f, 1.0f };
//        int cx = w / 2, cy = h / 2;
//        float aspect = h * 1.0f / w;
//        RadialGradient vignette = new RadialGradient(cx, 1.0f * cy / aspect, cx, colors, pos,
//            Shader.TileMode.CLAMP);
//        Matrix oval = new Matrix();
//        oval.setScale(1.0f, aspect);
//        vignette.setLocalMatrix(oval);
//        return vignette;
//    }
//
//    private static Shader createShader(Bitmap b) {
//        return new BitmapShader(b, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
//    }

}
