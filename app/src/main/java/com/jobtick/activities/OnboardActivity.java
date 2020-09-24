package com.jobtick.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.jobtick.R;
import com.jobtick.TextView.TextViewMedium;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.models.AttachmentModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.droidsonroids.gif.GifImageView;

public class OnboardActivity extends ActivityBase {


    int pos;
    AdapterImageSlider adapterImageSlider;
    ArrayList<Integer> gifList;

    @BindView(R.id.pager)
    ViewPager viewPager;


    @BindView(R.id.layout_dots)
    LinearLayout layoutDots;

    @BindView(R.id.txtNextButton)
    TextViewRegular txtMextButton;

    @BindView(R.id.lyt_btn_next)
    LinearLayout lytBtnNext;

    @BindView(R.id.txt_skip)
    TextViewMedium txtSkip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);
        ButterKnife.bind(this);
        gifList = new ArrayList<>();
        init();

        txtSkip.setOnClickListener(v -> {
            Intent intent = new Intent(OnboardActivity.this, DashboardActivity.class);
            startActivity(intent);
        });
    }

    public void init() {


        if (getIntent().hasExtra("as")) {
            if (getIntent().getExtras().getString("as").equals("poster")) {

                gifList.add(R.drawable.ic_poster1);
                gifList.add(R.drawable.ic_poster2);
                gifList.add(R.drawable.ic_poster3);
                gifList.add(R.drawable.ic_poster4);
            }
            if (getIntent().getExtras().getString("as").equals("worker")) {

                gifList.add(R.drawable.ic_worker1);
                gifList.add(R.drawable.ic_poster2);
                gifList.add(R.drawable.ic_poster3);
                gifList.add(R.drawable.ic_worker4);

            }
        }


        adapterImageSlider = new AdapterImageSlider(OnboardActivity.this, gifList);
        viewPager.setAdapter(adapterImageSlider);
        addBottomDots(layoutDots, adapterImageSlider.getCount(), 0);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int pos) {
                //   ((TextView) findViewById(R.id.title)).setText(items.get(pos).name);
                //   ((TextView) findViewById(R.id.brief)).setText(items.get(pos).brief);
                addBottomDots(layoutDots, adapterImageSlider.getCount(), pos);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        lytBtnNext.setOnClickListener(v ->
                {
                    if (viewPager.getCurrentItem() == gifList.size()) {
                        Intent main = new Intent(OnboardActivity.this, DashboardActivity.class);
                        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(main);

                    } else {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);

                    }

                }
        );

        txtSkip.setOnClickListener(v -> {
            Intent main = new Intent(OnboardActivity.this, DashboardActivity.class);
            main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(main);


        });

    }

    private static class AdapterImageSlider extends PagerAdapter {
        private Activity act;
        private ArrayList<Integer> items;
        private AdapterImageSlider.OnItemClickListener onItemClickListener;

        private interface OnItemClickListener {
            void onItemClick(View view, AttachmentModel obj);
        }

        public void setOnItemClickListener(AdapterImageSlider.OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        // constructor
        private AdapterImageSlider(Activity activity, ArrayList<Integer> items) {
            this.act = activity;
            this.items = items;
        }


        @Override
        public int getCount() {
            return this.items.size();
        }

        public int getItem(int pos) {
            return items.get(pos);
        }

        public void setItems(ArrayList<Integer> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((RelativeLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final int attachment = items.get(position);
            LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.item_slider_image, container, false);

            ImageView image = (ImageView) v.findViewById(R.id.image);
            image.setVisibility(View.GONE);
            GifImageView ivGIFImageView = v.findViewById(R.id.ivGIFImageView);
            ivGIFImageView.setVisibility(View.VISIBLE);
            ivGIFImageView.setImageResource(attachment);
            ((ViewPager) container).addView(v);

            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((RelativeLayout) object);

        }

    }

    /*  Intent intent = new Intent(CompleteRegistrationActivity.this, DashboardActivity.class);
                            openActivity(intent);*/


    private void addBottomDots(LinearLayout layout_dots, int size, int current) {
        ImageView[] dots = new ImageView[size];
        layout_dots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            int width_height = 30;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(width_height, width_height));
            params.setMargins(10, 10, 10, 10);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(R.drawable.shape_circle_gray);
            layout_dots.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[current].setImageResource(R.drawable.shape_circle_blue);
        }
        if (current == size) {
            txtMextButton.setText("GET STARTED");
        }
    }


}
