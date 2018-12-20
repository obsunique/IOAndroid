package com.example.cc.iocontrolapplication.information;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.cc.iocontrolapplication.R;

/**
 * Created by cc on 2018/12/21.
 */

public class OpenInformationPage extends Activity{

    private WebView webview;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_openwebpage);
        Intent intent=new Intent();
        String url=intent.getStringExtra("webname");
        webview=(WebView)findViewById(R.id.webview);
        webview.loadUrl(url);
        setWebViewSettings();
        setWebClient();
    }
    private void setWebViewSettings() {
        //获取一个webviewsetting对象
        WebSettings setting = webview.getSettings();
        //支持JavaScript
        setting.setJavaScriptEnabled(true);
        //显示缩放控制工具
        setting.setDisplayZoomControls(true);
        //设置webview支持缩放
        setting.setSupportZoom(true);
        setting.setBuiltInZoomControls(true);
        //设置加载进来的页面自适应手机屏幕
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);
    }
    private void setWebClient() {

        webview.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return true;
            }

        });

    }
}
