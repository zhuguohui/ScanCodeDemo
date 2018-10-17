package com.zgh.scandodedemo.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.afollestad.materialdialogs.MaterialDialog;
import com.zgh.scandodedemo.util.Mobile;
import com.zgh.scandodedemo.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements Mobile.ScanListener {

    WebView webView;
    String initJs;
    ProgressDialog scanDialog;
    private static final String TEST_URL = "https://appapp.snxw.com/sy/tj_3696/201809/t20180930_376601.html";

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webview);
        //加载js
        loadJSFromRaw();
        //设置webView
        setWebView();
        //加载测试网页
        webView.loadUrl(TEST_URL);
    }

    private void setWebView() {
        //设置webView
        webView.setWebContentsDebuggingEnabled(true);
        WebSettings settings = webView.getSettings();
        //支持javascript
        settings.setJavaScriptEnabled(true);
        // 设置可以支持缩放
        settings.setSupportZoom(false);
        // 设置出现缩放工具
        settings.setBuiltInZoomControls(true);
        //扩大比例的缩放
        settings.setUseWideViewPort(true);
        //自适应屏幕
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        //注入mobile对象
        Mobile mobile = new Mobile(this);
        mobile.setScanListener(this);
        webView.addJavascriptInterface(mobile, "mobile");

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return false;
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(final WebView view, String url) {
                view.evaluateJavascript("javascript:" + initJs, null);
            }
        });
    }


    private void loadJSFromRaw() {
        InputStream inputStream = this.getResources().openRawResource(R.raw.init);
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        try {
            while ((len = inputStream.read(buffer)) >= 0) {
                bos.write(buffer, 0, len);
            }
            inputStream.close();
            initJs = bos.toString();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onScanStart() {
        if (scanDialog == null) {
            scanDialog = new ProgressDialog(this);
            scanDialog.setMessage("正在识别");
            scanDialog.setCancelable(false);

        }
        scanDialog.show();

    }

    @Override
    public void onScanFailed(String info) {
        if (scanDialog != null) {
            scanDialog.dismiss();
        }
        new MaterialDialog.Builder(this)
                .title("识别失败")
                .content(info)
                .positiveText("确定")
                .onPositive((dialog, which) -> {
                    dialog.dismiss();
                })
                .show();


    }

    @Override
    public void onScanSuccess(String result) {
        if (scanDialog != null) {
            scanDialog.dismiss();
        }

        boolean isHttpLink = false;
        String info = result;
        if (!TextUtils.isEmpty(result)) {
            String lowerCase = result.toLowerCase();
            if (lowerCase.startsWith("http")) {
                isHttpLink = true;
                info = "是否跳转到链接:" + info;
            }
        }
        boolean finalIsHttpLink = isHttpLink;
        new MaterialDialog.Builder(this)
                .title("识别成功")
                .content(info)
                .positiveText(isHttpLink ? "立即跳转" : "确定")
                .negativeText("取消")
                .onPositive((dialog, which) -> {
                    dialog.dismiss();
                    if (finalIsHttpLink) {
                        Intent intent = new Intent(MainActivity.this, WebActivity.class);
                        intent.putExtra(WebActivity.KEY_WEB_URL, result);
                        MainActivity.this.startActivity(intent);
                    }
                })
                .onNegative((dialog, which) -> {
                    dialog.dismiss();
                }).show();


    }
}
