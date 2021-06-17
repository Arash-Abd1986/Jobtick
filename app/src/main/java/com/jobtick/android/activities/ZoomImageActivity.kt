package com.jobtick.android.activities;


import android.os.Bundle;
import android.view.WindowManager;

import androidx.viewpager.widget.ViewPager;
import com.jobtick.android.R;
import android.annotation.SuppressLint;

import com.jobtick.android.adapers.ImagePagerAdapter;
import com.jobtick.android.models.AttachmentModel;
import com.jobtick.android.utils.TouchImageView;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ZoomImageActivity extends ActivityBase {


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.ivZoom)
    TouchImageView ivZoom;


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.vpBanner)
    ViewPager vpBanner;

    ArrayList<AttachmentModel> dataListModel = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_img);


        ButterKnife.bind(this);


        dataListModel = getIntent().getParcelableArrayListExtra("url");
        int pos = getIntent().getIntExtra("pos", 0);

        ImagePagerAdapter pagerAdapter = new ImagePagerAdapter
                (this, dataListModel);
        vpBanner.setAdapter(pagerAdapter);
        //ciBanner.setViewPager(vpBanner);
        vpBanner.setCurrentItem(pos);


        vpBanner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @OnClick(R.id.ivBack)
    void onBack() {
        onBackPressed();
    }


}
