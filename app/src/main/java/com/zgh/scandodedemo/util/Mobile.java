package com.zgh.scandodedemo.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.uuzuche.lib_zxing.activity.CodeUtils.AnalyzeCallback;
import com.zgh.scandodedemo.activity.ImageBrowserActivity;
import com.zgh.scandodedemo.util.CodeUtils;

import java.util.ArrayList;


/**
 * Created by zhuguohui on 2018/10/9.
 */

public class Mobile {
    Context context;
    Handler handler = new Handler(Looper.getMainLooper());
    ArrayList<String> imageURLList = new ArrayList<>();

    public Mobile(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void scanCode(final String imageUrl) {
        if (scanListener != null) {
            scanListener.onScanStart();
        }
        if (TextUtils.isEmpty(imageUrl)) {
            if (scanListener != null) {
                scanListener.onScanFailed("图片地址为空");
            }
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    getImage(imageUrl);
                }
            });


        }
    }

    @JavascriptInterface
    public void addImageUrl(String url) {
        imageURLList.add(url);
    }

    @JavascriptInterface
    public void showImage(String url) {
        Intent intent = new Intent(context, ImageBrowserActivity.class);
        intent.putStringArrayListExtra(ImageBrowserActivity.IMAGE_BROWSER_LIST, imageURLList);
        intent.putExtra(ImageBrowserActivity.IMAGE_BROWSER_INIT_SRC, url);
        context.startActivity(intent);
    }

    private void getImage(String imageUrl) {
        Glide.with(context.getApplicationContext()).load(imageUrl)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                        CodeUtils.analyzeBitmap(resource, new AnalyzeCallback() {
                            @Override
                            public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                                if (scanListener != null) {
                                    scanListener.onScanSuccess(result);
                                }
                            }

                            @Override
                            public void onAnalyzeFailed() {
                                if (scanListener != null) {
                                    scanListener.onScanFailed("未识别到二维码");
                                }
                            }
                        });

                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        if (scanListener != null) {
                            scanListener.onScanFailed("加载图片失败[" + e.getMessage() + "]");
                        }
                    }
                });
    }

    public interface ScanListener {
        void onScanStart();

        void onScanFailed(String info);

        void onScanSuccess(String result);
    }

    private ScanListener scanListener;

    public void setScanListener(ScanListener scanListener) {
        this.scanListener = scanListener;
    }
}
