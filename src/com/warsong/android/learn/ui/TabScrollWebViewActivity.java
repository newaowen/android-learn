package com.warsong.android.learn.ui;

import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;

import com.warsong.android.learn.R;

/**
 * view生命周期试
 * @author newaowen@gmail.com
 */
public class TabScrollWebViewActivity extends Activity {

    private View btnOne;
    private View btnTwo;

    private View webviewBox;

    private WebView webviewOne;

    private WebView webviewTwo;

    private String oneUrl = "http://www.baidxxxxu.com";
    private String twoUrl = "http://www.taobao.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_scroll_webview);
        initView();
    }

    private void initView() {
        btnOne = findViewById(R.id.btnOne);
        btnOne.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                webviewOne.setVisibility(View.VISIBLE);
                webviewOne.loadUrl(oneUrl);
                webviewTwo.setVisibility(View.GONE);
            }
        });

        btnTwo = findViewById(R.id.btnTwo);
        btnTwo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                webviewTwo.setVisibility(View.VISIBLE);
                webviewTwo.loadUrl(twoUrl);
                webviewOne.setVisibility(View.GONE);
            }
        });
        webviewBox = findViewById(R.id.webviewBox);
        webviewOne = (WebView) findViewById(R.id.webviewOne);
        webviewTwo = (WebView) findViewById(R.id.webviewTwo);

        initWebView(webviewOne);
        initWebView(webviewTwo);

        webviewOne.setVisibility(View.VISIBLE);
        webviewOne.loadUrl(oneUrl);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView(WebView webView) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVerticalScrollbarOverlay(true);
        webView.getSettings().setPluginState(PluginState.ON);

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
    }

}
