package com.warsong.android.learn.ui;

import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.warsong.android.learn.R;

/**
 * webview滚动测试
 * @author newaowen@gmail.com
 */
@SuppressLint("NewApi")
public class ScrollWebViewActivity extends Activity {

    private static String TAG = "ScrollWebViewActivity";

    private View upBox;
    private View contentBox;
    private View btn;
    private WebView webView;
    private View mainCntent;

    private View down;

    private GestureDetector gestureDetector;

    private boolean mIsScrolling = true;

    private int scrollTop;
    private int originPos = 0;

    private int statusBarHeight;

    ViewTreeObserver.OnGlobalLayoutListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scroll_web_view);

        statusBarHeight = getStatusBarHeight();
        Log.i(TAG, "statusHeight: " + statusBarHeight);
        init();
    }

    private int getStatusBarHeight() {
        // 第一种方法(可能获取为0)
        Rect rectgle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
        int StatusBarHeight = rectgle.top;
        int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentViewTop - StatusBarHeight;
        if (titleBarHeight == 0) { //第二种方法
            int result = 0;
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = getResources().getDimensionPixelSize(resourceId);
            }
            return result;
        }
        return titleBarHeight;
    }

    private void init() {
        mainCntent = findViewById(R.id.main_content);
        upBox = findViewById(R.id.up_box);
        contentBox = findViewById(R.id.content_box);
        btn = findViewById(R.id.btn);
        down = findViewById(R.id.down);
         
        
        //        webView = (WebView)findViewById(R.id.wv);
        //        initWebClient(webView);
        //        webView.loadUrl("http://www.baidu.com");

        listener = new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                int pos[] = new int[2];
                contentBox.getLocationInWindow(pos);
                originPos = pos[1];
                mainCntent.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                // originPos去掉status bar高度
                Log.i(TAG, "statusHeight: " + statusBarHeight);
                scrollTop = originPos - statusBarHeight;
                
                //upBox.setClipBounds(clipBounds)
                //upBox.setClipBounds(new Rect(0, 0, upBox.getWidth(), upBox.getHeight()));
                //scrollTop = originPos;
            }
        };

        mainCntent.getViewTreeObserver().addOnGlobalLayoutListener(listener);

        gestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent arg0) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent arg0) {
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float arg2, float arg3) {
                // 滚动事件处理
                //return false;
                Log.i(TAG, "onScroll from to: " + e1.getY() + ", " + e2.getY());
                mIsScrolling = true;
                handleScroll(e1.getY(), e2.getY());
                return true;
            }

            @Override
            public void onLongPress(MotionEvent arg0) {
            }

            @Override
            public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
                Log.i(TAG, "onFling ");
                return false;
            }

            @Override
            public boolean onDown(MotionEvent arg0) {
                return true;
            }
        });

        contentBox.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    private boolean isFixUp = false;
    private boolean isInOrigin = true;

    private void handleScroll(float y1, float y2) {
        int v = (int) (y2 - y1);
        int newY = mainCntent.getScrollY() - v;
        Log.i(TAG, "handle scroll newY" + newY);
        if (v <= 0) { //向上滚动
            if (newY > scrollTop) {
                newY = scrollTop;
            }

            if (!isFixUp()) {
                mainCntent.scrollTo(0, newY);
            } else {
                //TODO 切换到头部滚动
                contentBox.setOnTouchListener(null);
                btn.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return gestureDetector.onTouchEvent(event);
                    }
                });
            }
        } else { //向下滚动
            // 修正滚动值
            if (newY < 0) {
                newY = 0;
            }
            if (!isInOrigin()) {
                mainCntent.scrollTo(0, newY);
            } else {
                // TODO 切换到下部分滚动
                btn.setOnTouchListener(null);
                contentBox.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return gestureDetector.onTouchEvent(event);
                    }
                });
            }
        }
    }

    private boolean isFixUp() {
        int pos[] = new int[2];
        contentBox.getLocationInWindow(pos);
        Log.i(TAG, "isFixUp: " + pos[1]);
        if (pos[1] <= statusBarHeight) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isInOrigin() {
        int pos[] = new int[2];
        contentBox.getLocationInWindow(pos);
        if (pos[1] >= originPos) {
            return true;
        } else {
            return false;
        }
    }

    private int getCurrentY() {
        int pos[] = new int[2];
        contentBox.getLocationInWindow(pos);
        return pos[1];
    }

    private void initWebClient(WebView webView) {
        //webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setJavaScriptEnabled(true);
        //webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setVerticalScrollbarOverlay(true);

        try {
            Method method = webView.getSettings().getClass()
                .getMethod("setDomStorageEnabled", boolean.class);
            if (method != null) {
                method.invoke(webView.getSettings(), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        WebViewClient client = new WebViewClient();
        webView.setWebViewClient(client);
        //mock js alert
        //        webView.setWebChromeClient(new WebChromeClient() {
        //
        //            @Override
        //            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        //                return true;
        //            }
        //        });
    }

}
