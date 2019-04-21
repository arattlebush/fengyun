package net.fengyun.italker.italker.activities;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.fengyun.italker.italker.R;

public class WebActivity extends Activity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        WebView webView=(WebView)findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        Intent intent=getIntent();
        String data=intent.getStringExtra("url");
        webView.loadUrl(data);

    }


//    //进度
//    private ProgressBar mProgressBar;
//    //网页
//    private WebView mWebView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_web);
//
//        initView();
//    }
//
//    //初始化View
//    private void initView() {
//
//        mProgressBar = (ProgressBar) findViewById(R.id.mProgressBar);
//        mWebView = (WebView) findViewById(R.id.mWebView);
//
//        Intent intent = getIntent();
//        String title = intent.getStringExtra("title");
//        final String url = intent.getStringExtra("url");
//        //L.i("url:" + url);
//
//        //设置标题
//        getSupportActionBar().setTitle(title);
//
//        //进行加载网页的逻辑
//
//        //支持JS
//        mWebView.getSettings().setJavaScriptEnabled(true);
//        //支持缩放
//        mWebView.getSettings().setSupportZoom(true);
//        mWebView.getSettings().setBuiltInZoomControls(true);
//        //接口回调
//        mWebView.setWebChromeClient(new WebViewClient());
//        //加载网页
//        mWebView.loadUrl(url);
//
//        //本地显示
//        mWebView.setWebViewClient(new android.webkit.WebViewClient(){
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                view.loadUrl(url);
//                //我接受这个事件
//                return true;
//            }
//        });
//    }
//
//    public class WebViewClient extends WebChromeClient {
//
//        //进度变化的监听
//        @Override
//        public void onProgressChanged(WebView view, int newProgress) {
//            if(newProgress == 100){
//                mProgressBar.setVisibility(View.GONE);
//            }
//            super.onProgressChanged(view, newProgress);
//        }
//    }
}
