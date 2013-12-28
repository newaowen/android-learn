package com.warsong.android.learn.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

public class GeneralUtil {

    private static AlertDialog loadingDialog;

    public static boolean isIntentExist(Context context, Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, 0);
        if (list.size() > 0) {
            return true;
        }
        return false;
    }

    public static void alert(Activity context, String title, String content,
                             DialogInterface.OnClickListener positiveListener,
                             DialogInterface.OnClickListener cancelListener) {
        if (!isActivityValid(context)) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(content);
        if (positiveListener != null) {
            builder.setPositiveButton("确定", positiveListener);
        }
        if (cancelListener != null) {
            builder.setNegativeButton("取消", cancelListener);
        }

        builder.show();
    }

    public static void toast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    //	public static void showLoading(Activity context) {
    //		showLoading(context, "", null);
    //	}

    //	public static void showLoading(Activity context, String msg,
    //			DialogInterface.OnCancelListener cancelListener) {
    //		if (!isActivityValid(context)) {
    //			return;
    //		}
    //
    //		if (loadingDialog != null && loadingDialog.isShowing()) {
    //			return;
    //		}
    //
    //		loadingDialog = new GenericProgressDialog(context);
    //		loadingDialog.setMessage(msg);
    //		((GenericProgressDialog) loadingDialog).setProgressVisiable(true);
    //		loadingDialog.setCancelable(true);
    //		loadingDialog.setOnCancelListener(cancelListener);
    //		loadingDialog.setCanceledOnTouchOutside(false);
    //		loadingDialog.show();
    //	}
    //
    //	public static void dismissLoading() {
    //		if (loadingDialog != null && loadingDialog.isShowing()) {
    //			loadingDialog.dismiss();
    //			loadingDialog = null;
    //		}
    //	}

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dpToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int pxToDp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 避免在更新背景时忽略掉padding
     * 
     * @param v
     * @param resId
     */
    public static void updateViewBackground(View v, int resId) {
        int pL = v.getPaddingLeft();
        int pT = v.getPaddingTop();
        int pR = v.getPaddingRight();
        int pB = v.getPaddingBottom();

        v.setBackgroundResource(resId);
        v.setPadding(pL, pT, pR, pB);
    }

    public static byte[] streamToByte(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();

        return buffer.toByteArray();
    }

    public static boolean isActivityValid(Activity act) {
        return !(act == null || act.isFinishing());
    }

    /**
     * 获取本机手机号（注意：只有sim卡中保存了才有效)
     * 
     * @param ctx
     * @return
     */
    public static String getLocalPhoneNo(Context ctx) {
        TelephonyManager phoneMgr = (TelephonyManager) ctx
            .getSystemService(Context.TELEPHONY_SERVICE);
        return phoneMgr.getLine1Number();
    }

    public static String getConfigFromManifest(Context ctx, String key) {
        String result = "";
        try {
            ApplicationInfo ai = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(),
                PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            result = bundle.getString(key);
        } catch (NameNotFoundException e) {
            Log.e("getValueFromManifest",
                "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e("getValueFromManifest",
                "Failed to load meta-data, NullPointer: " + e.getMessage());
        }

        return result;
    }

    public static boolean isDebug(Context ctx) {
        return (ctx.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    public static String addUrlLastBS(String url) {
        if (!"/".equals(url.substring(url.length() - 1))) {
            url += "/";
        }
        return url;
    }

    public static int parseInt(String string) {
        int ret = -1;
        try {
            ret = Integer.parseInt(string);
        } catch (NumberFormatException e) {
        }

        return ret;
    }

    public static Long parseLong(String string) {
        Long ret = 0L;
        try {
            ret = Long.valueOf(string);
        } catch (NumberFormatException e) {
        }

        return ret;
    }

    public static String fomatMoneyFloat(String str) {
        String ret = str;
        try {
            ret = String.format("%1$.2f", Float.parseFloat(str));
        } catch (Exception e) {
        }
        return ret;
    }

    public static int parseInt(String string, int defaultRet) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
        }
        return defaultRet;
    }

    public static Integer valueOf(String string) {
        int ret = -1;
        try {
            ret = Integer.valueOf(string);
        } catch (NumberFormatException e) {

        }

        return ret;
    }

    /**
     * 全角转半角
     * @param input String.
     * @return 半角字符串
     */
    public static String toDBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);
            }
        }
        String returnString = new String(c);
        return returnString;
    }

    public static String handleChinese(String str) {
        if (!isEmpty(str)) {
            str = str.replaceAll("【", "[").replaceAll("】", "]").replaceAll("！", "!");// 替换中文标号
            String regEx = "[『』]"; // 清除掉特殊字符
            try {
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(str);
                str = m.replaceAll("").trim();
            } catch (PatternSyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return str;

    }

    public static String getString(Intent intent, String key) {
        if (null != intent) {
            Bundle bundle = intent.getExtras();
            if (null != bundle) {
                return bundle.getString(key);
            }
        }

        return null;
    }

    public static String catString(String... params) {
        StringBuilder strBuilder = new StringBuilder();
        for (String str : params) {
            if (null == str) {
                continue;
            }

            strBuilder.append(str);
        }
        return strBuilder.toString();
    }

    public static String catStringWithSeparator(String separator, String... params) {
        StringBuilder strBuilder = new StringBuilder();
        if (null != params) {
            String item = null;
            for (int i = 0; i < params.length; i++) {
                item = params[i];
                if (isEmpty(item)) {
                    continue;
                } else {
                    strBuilder.append(isEmpty(strBuilder.toString()) ? item : separator + item);
                }
            }
        }

        return strBuilder.toString();
    }

    public static void showToast(Activity context, String memo) {
        Toast toast = Toast.makeText(context.getApplicationContext(), memo, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static boolean showInputMethod(View v, boolean checkCurrentViewFocus) {
        if (null == v || (checkCurrentViewFocus ? !v.isFocused() : false)) {
            return false;
        }

        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(
            Context.INPUT_METHOD_SERVICE);

        //imm.toggleSoftInput(0, 0);
        return imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT); //or 0
    }

    public static boolean isSystemPatternEnable(ContentResolver resolver) {
        String enableString = android.provider.Settings.Secure.getString(resolver,
            "lock_pattern_autolock");//android.provider.Settings.Secure.LOCK_PATTERN_ENABLED
        return "1".equals(enableString);
    }

    public static boolean isEmpty(String string) {
        return (null == string || "".equals(string)) ? true : false;
    }

    public static boolean isZero(String string) {
        if (isEmpty(string)) {
            return true;
        } else {
            for (int i = 0; i < string.length(); i++) {
                char ch = string.charAt(i);
                if ('.' != ch && '0' != ch) {
                    return false;
                }
            }

            return true;
        }
    }

    public static void adjustTextSize(TextView tv) {

        float size = tv.getPaint().getTextSize();
        TextPaint textPaint = tv.getPaint();
        float stringWidth = textPaint.measureText(tv.getText().toString());
        int textViewWidth = tv.getWidth();
        if (textViewWidth > stringWidth) {

            float percent = (textViewWidth / stringWidth);
            textPaint.setTextSize(size * percent);
        }
    }

    public static String adjustTextLength(TextView tv, String textSource, String appendText) {
        if (null == tv || null == textSource) {
            return textSource;
        }

        TextPaint textPaint = tv.getPaint();
        float stringWidth = textPaint.measureText(textSource);
        int width = tv.getWidth();
        int count = textSource.length();
        while (stringWidth > width && count > 0) {
            stringWidth = textPaint.measureText(textSource.substring(0, --count) + appendText);
        }

        if (count > 0 && count < textSource.length()) {
            return textSource.substring(0, count) + appendText;
        }

        return textSource;
    }

    /**
     * 重置repeat
     * 
     * @param view
     */
    public static void fixBackgroundRepeat(View view) {
        Drawable bg = view.getBackground();
        if (bg != null) {
            if (bg instanceof BitmapDrawable) {
                BitmapDrawable bmp = (BitmapDrawable) bg;
                bmp.mutate(); // make sure that we aren't sharing state anymore
                bmp.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
            }
        }
    }

    public static boolean equalsAtBit(int num, int bit) {
        return 0 != (num & bit);
    }

    public static int getFirstBit(int num) {
        int count = 0;
        while (0 != num) {
            if (1 == (num & 1)) {
                return 1 << count;
            }
        }

        return 0;
    }

    public static int countBit(int num) {
        int count = 0;
        while (0 != num) {
            if (1 == (num & 1)) {
                count++;
            }

            num = num >> 1;
        }

        return count;
    }

    /**
     * 获取指定样式的当前时间
     * @param timeStyle
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getCurrentFomatTime(String timeStyle) {
        Calendar calendar = Calendar.getInstance();
        long time = calendar.getTimeInMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeStyle);
        return simpleDateFormat.format(time);
    }

    /***
     * 取得千分符的金额
     * 
     * @param s
     * @return
     */
    public static String formatMoney(String s) {
        try {
            if (s == null || s.length() < 1) {
                return "0.00";
            }
            final NumberFormat numberFormat = new DecimalFormat("###,###,##0.00");
            return numberFormat.format(Double.parseDouble(s));
        } catch (NumberFormatException e) {
            Log.e("Utilz", "money format error", e);
        }
        return "0.00";
    }

    /***
     * 年月转换成calendar对象
     * 
     * @param year
     * @param month
     * @return
     */
    public static Calendar yearMonthToCalendar(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c;
    }

    public static byte[] bitmap2Bytes(Bitmap bm) {
        byte[] result = null;
        if (bm != null) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
                result = baos.toByteArray();
            } catch (Error e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 转换成jpeg格式
     * (注：大图片建议采用这种方式，转换成png可能非常耗时)
     * @param bm
     * @return
     */
    public static byte[] bitmap2JPEGBytes(Bitmap bm, int quality) {
        byte[] result = null;
        if (bm != null) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                result = baos.toByteArray();
            } catch (Error e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static Bitmap captureView(View v) {
        v.setDrawingCacheEnabled(true);
        //需要创建新图像，否则会导致use recycled bitmap异常
        Bitmap b = null;
        try {
            b = Bitmap.createBitmap(v.getDrawingCache());
        } catch (Error e) { //防OOM
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        v.setDrawingCacheEnabled(false);
        v.destroyDrawingCache();
        return b;
    }

    /**
     * 旋转图片
     * @param bitmap
     * @param degree
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap src, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap b = null;
        try {
            b = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        } catch (Error e) { //防OOM
            e.printStackTrace();
        }
        return b;
    }

    /**
     * 旋转图片，并自动尝试回收旧图片 
     * @param bitmap
     * @param degree
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap src, float degree, boolean autoRecycle) {
        Bitmap b = rotateBitmap(src, degree);
        if (autoRecycle) {
            recycleBitmap(src);
        }
        return b;
    }

    /**
     * 先翻转再旋转图片(相机)
     * @param src
     * @param degree
     * @param autoRecycle
     * @return
     */
    public static Bitmap flipAndRotateBitmap(Bitmap src, float degree, boolean autoRecycle) {
        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1);
        matrix.postRotate(degree);
        Bitmap b = null;
        try {
            b = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
            if (autoRecycle) {
                recycleBitmap(src);
            }
        } catch (Error e) { //防OOM
            e.printStackTrace();
        }
        return b;
    }

    /**
     * 先旋转再翻转图片(相机)
     * @param src
     * @param degree
     * @param autoRecycle
     * @return
     */
    public static Bitmap rotateAndFlipBitmap(Bitmap src, float degree, boolean autoRecycle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        matrix.postScale(-1, 1);
        Bitmap b = null;
        try {
            b = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
            if (autoRecycle) {
                recycleBitmap(src);
            }
        } catch (Error e) { //防OOM
            e.printStackTrace();
        }
        return b;
    }

    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    public static RectF scaleCenterInsideRect(int srcW, int srcH, int destW, int destH) {
        float xScale = (float) destW / srcW;
        float yScale = (float) destH / srcH;
        float scale = Math.min(xScale, yScale);
        // Now get the size of the source bitmap when scaled
        float sw = scale * srcW;
        float sh = scale * srcH;
        //RectF result = new RectF();
        float left = (destW - sw) / 2;
        float top = (destH - sh) / 2;
        //改变变化快的边使其等于dest的尺寸
        RectF result = new RectF();
        result.left = left;
        result.right = left + sw;
        result.top = top;
        result.bottom = top + sh;
        return result;
    }

    public static RectF scaleCenterCropRect(int srcW, int srcH, int destW, int destH) {
        float xScale = (float) destW / srcW;
        float yScale = (float) destH / srcH;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float sw = scale * srcW;
        float sh = scale * srcH;

        float left = (destW - sw) / 2;
        float top = (destH - sh) / 2;
        RectF result = new RectF();
        result.left = left;
        result.right = left + sw;
        result.top = top;
        result.bottom = top + sh;
        return result;
    }

    public static Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger 
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);
        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap result = null;
        try {
            Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
            Canvas canvas = new Canvas(dest);
            canvas.drawBitmap(source, null, targetRect, null);
            //原图回收  
            //recycleBitmap(source);
        } catch (Error e) {
            e.printStackTrace();
        }
        if (result == null) {
            result = source;
        }
        return result;
    }

    public static Bitmap decodeImageBytes(byte[] bytes, int width, int height) {
        Bitmap bmp = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new ByteArrayInputStream(bytes), null, options);
            options.inSampleSize = calculateInSampleSize(options, width, height);
            options.inJustDecodeBounds = false;
            bmp = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes), null, options);
        } catch (Error e) { //防OOM
            e.printStackTrace();
        }
        return bmp;
    }

    /**
     * 从文件中解析图片，并缩放到widthxheight
     * @param filePath
     * @param width
     * @param height
     * @return
     */
    public static Bitmap decodeFile(String filePath, int width, int height) {
        Bitmap bmp = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            options.inSampleSize = calculateInSampleSize(options, width, height);
            options.inJustDecodeBounds = false;
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (Exception e) { //防OOM
            e.printStackTrace();
        }
        return bmp;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
                                            int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * 手动解析preview数据时需要
     * @param rgba
     * @param yuv420sp
     * @param width
     * @param height
     */
    static public void decodeYUV420SP(int[] rgba, byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;

        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0)
                    y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }

                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0)
                    r = 0;
                else if (r > 262143)
                    r = 262143;
                if (g < 0)
                    g = 0;
                else if (g > 262143)
                    g = 262143;
                if (b < 0)
                    b = 0;
                else if (b > 262143)
                    b = 262143;
                rgba[yp] = ((r << 14) & 0xff000000) | ((g << 6) & 0xff0000) | ((b >> 2) | 0xff00);
            }
        }
    }

}
