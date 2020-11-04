package com.jobtick.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jobtick.R;
import com.jobtick.adapers.SectionsPagerAdapter;
import com.jobtick.fragments.PaymentHistoryEarnedFragment;
import com.jobtick.fragments.PaymentHistoryOutgoingFragment;
import com.jobtick.fragments.SignInFragment;
import com.jobtick.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentHistoryActivity extends ActivityBase {

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.rb_outgoing)
    RadioButton rbOutgoing;
    @BindView(R.id.rb_earned)
    RadioButton rbEarned;
    @BindView(R.id.rg_outgoing_earned)
    RadioGroup rgOutgoingEarned;
    @BindView(R.id.ivBack)
    ImageView ivBack;

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(final int position) {
            switch (position) {
                case 0:
                    rbEarned.setChecked(false);
                    rbOutgoing.setChecked(true);
                    break;
                case 1:
                    rbEarned.setChecked(true);
                    rbOutgoing.setChecked(false);
                    break;
                case 2:

                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);
        ButterKnife.bind(this);
        initComponent();
        initToolbar();
    }

    private void initToolbar() {
        ivBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void initComponent() {
        setupViewPager(viewPager);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(2);
        clickEvent();
    }

    private void clickEvent() {
        rgOutgoingEarned.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_outgoing:
                    viewPager.setCurrentItem(0);
                    break;

                case R.id.rb_earned:
                    viewPager.setCurrentItem(1);
                    break;
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(PaymentHistoryOutgoingFragment.newInstance(), getResources().getString(R.string.date_time));
        adapter.addFragment(PaymentHistoryEarnedFragment.newInstance(), getResources().getString(R.string.details));
        viewPager.setAdapter(adapter);
    }
}
