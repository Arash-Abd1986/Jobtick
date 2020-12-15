package com.jobtick.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.adapers.SectionsPagerAdapter;
import com.jobtick.fragments.PaymentPosterFragment;
import com.jobtick.fragments.PaymentTickerFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentHistoryActivity extends ActivityBase {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rb_outgoing)
    RadioButton rbOutgoing;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rb_earned)
    RadioButton rbEarned;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rg_outgoing_earned)
    RadioGroup rgOutgoingEarned;

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

    private void initComponent() {
        setupViewPager(viewPager);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(2);
        clickEvent();
    }

    private void initToolbar() {
        toolbar.setTitle("Payment History");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_payment_history, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.nav_setting:
                startActivity(new Intent(this, PaymentSettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
        adapter.addFragment(PaymentPosterFragment.newInstance(), getResources().getString(R.string.date_time));
        adapter.addFragment(PaymentTickerFragment.newInstance(), getResources().getString(R.string.details));
        viewPager.setAdapter(adapter);
    }
}
