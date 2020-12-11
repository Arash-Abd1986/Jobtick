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

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.jobtick.R;
import com.jobtick.text_view.TextViewMedium;
import com.jobtick.text_view.TextViewRegular;
import com.jobtick.models.AttachmentModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OnboardActivity extends ActivityBase {


    int pos;
    AdapterImageSlider adapterImageSlider;
    ArrayList<Integer> lottieAnimList;
    ArrayList<Integer> descriptionList;

    @BindView(R.id.pager)
    ViewPager viewPager;


    @BindView(R.id.layout_dots)
    LinearLayout layoutDots;


    @BindView(R.id.lyt_btn_next)
    MaterialButton lytBtnNext;

    @BindView(R.id.txt_skip)
    TextViewMedium txtSkip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);
        ButterKnife.bind(this);
        lottieAnimList = new ArrayList<>();
        descriptionList = new ArrayList<>();
        init();

        txtSkip.setOnClickListener(v -> {
            Intent intent = new Intent(OnboardActivity.this, DashboardActivity.class);
            startActivity(intent);
        });
    }

    public void init() {


        if (getIntent().hasExtra("as")) {
            if (getIntent().getExtras().getString("as").equals("poster")) {

                lottieAnimList.add(R.raw.slide1);
                lottieAnimList.add(R.raw.slide5);
                lottieAnimList.add(R.raw.slide2);
                lottieAnimList.add(R.raw.slide3);

                descriptionList.add(R.string.poster_page1);
                descriptionList.add(R.string.poster_page2);
                descriptionList.add(R.string.poster_page3);
                descriptionList.add(R.string.poster_page4);
            }
            if (getIntent().getExtras().getString("as").equals("worker")) {

                lottieAnimList.add(R.raw.slide4);
                lottieAnimList.add(R.raw.slide5);
                lottieAnimList.add(R.raw.slide2);
                lottieAnimList.add(R.raw.slide6);

                descriptionList.add(R.string.worker_page1);
                descriptionList.add(R.string.worker_page2);
                descriptionList.add(R.string.worker_page3);
                descriptionList.add(R.string.worker_page4);

            }
        }


        adapterImageSlider = new AdapterImageSlider(OnboardActivity.this, lottieAnimList, descriptionList);
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
                    if (viewPager.getCurrentItem() == lottieAnimList.size() - 1) {
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
        private final Activity act;
        private final ArrayList<Integer> animItems;
        private final ArrayList<Integer> descItems;
        private AdapterImageSlider.OnItemClickListener onItemClickListener;

        private interface OnItemClickListener {
            void onItemClick(View view, AttachmentModel obj);
        }

        public void setOnItemClickListener(AdapterImageSlider.OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        // constructor
        private AdapterImageSlider(Activity activity, ArrayList<Integer> animItems, ArrayList<Integer> descriptionItems) {
            this.act = activity;
            this.animItems = animItems;
            this.descItems = descriptionItems;
        }

        @Override
        public int getCount() {
            return this.animItems.size();
        }


        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final int animAttachment = animItems.get(position);
            final int descAttachment = descItems.get(position);
            LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.item_slider_image_onboarding, container, false);

            LottieAnimationView lottieAnimationView = v.findViewById(R.id.lottieAnimationView);
            TextViewRegular description = v.findViewById(R.id.description);
            lottieAnimationView.setVisibility(View.VISIBLE);
            description.setVisibility(View.VISIBLE);
            lottieAnimationView.setAnimation(animAttachment);
            description.setText(descAttachment);
            ((ViewPager) container).addView(v);

            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((LinearLayout) object);

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
        if (current == size - 1)
            lytBtnNext.setText(R.string.get_started);
        else
            lytBtnNext.setText(R.string.next);
    }


}
