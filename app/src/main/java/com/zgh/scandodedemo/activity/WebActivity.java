package com.zgh.scandodedemo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.zgh.scandodedemo.R;

public class WebActivity extends AppCompatActivity {

    public static final String KEY_WEB_URL = "key_web_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        WebView webView = findViewById(R.id.webview);
        String url = getIntent().getStringExtra(KEY_WEB_URL);
        webView.loadUrl(url);
    }
}
