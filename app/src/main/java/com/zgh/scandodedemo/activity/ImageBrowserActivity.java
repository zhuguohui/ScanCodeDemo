package com.zgh.scandodedemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.zgh.scandodedemo.R;

import java.util.ArrayList;

/**
 * Created by Vincent Woo
 * Date: 2016/7/4
 * Time: 16:46
 */
public class ImageBrowserActivity extends AppCompatActivity {
    public static String IMAGE_BROWSER_LIST = "ImageBrowserList";
    public static String IMAGE_BROWSER_INIT_SRC = "ImageBrowserInitSrc";


    private TextView mImgDescription;
    private boolean isLightOff = false;
    private int initIndex = 0;

    private ImageView mBack;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_browser);


        Intent intent = getIntent();

        String initSrc = intent.getStringExtra(IMAGE_BROWSER_INIT_SRC);
        imageList = intent.getStringArrayListExtra(IMAGE_BROWSER_LIST);
        if (imageList == null) {
            imageList = new ArrayList<>();
        }
        initIndex = imageList.indexOf(initSrc);
        initView();

    }


    @SuppressWarnings("unchecked")
    protected <T extends View> T $(int id) {
        return (T) super.findViewById(id);
    }

    private void initView() {

        mBack = $(R.id.iv_img_browser_back);
        final ViewPager mViewPager = $(R.id.view_pager);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mViewPager.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));
        mViewPager.setAdapter(new ImageBrowserAdapter());
        mViewPager.setCurrentItem(initIndex, false);
    }

    private ArrayList<String> imageList = new ArrayList<>();

    private class ImageBrowserAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView view = new PhotoView(ImageBrowserActivity.this);
            view.enable();
            view.setScaleType(ImageView.ScaleType.FIT_CENTER);

            Glide.with(ImageBrowserActivity.this).load(imageList.get(position)).into(view);

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return imageList == null ? 0 : imageList.size();
        }
    }


}
