package com.jobtick.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.fragments.FilterAllFragment;
import com.jobtick.fragments.FilterInPersonFragment;
import com.jobtick.fragments.FilterRemotelyFragment;
import com.jobtick.models.FilterModel;
import com.jobtick.utils.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FiltersActivity extends AppCompatActivity implements FilterInPersonFragment.FragmentCallbackFilterInPerson, FilterAllFragment.FragmentCallbackFilterAll, FilterRemotelyFragment.FragmentCallbackFilterRemote {


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rb_remotely)
    RadioButton rbRemotely;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rb_in_person)
    RadioButton rbInPerson;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rb_all)
    RadioButton rbAll;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rg_filters)
    RadioGroup rgFilters;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    FilterModel filterModel;
    Map<String, String> filters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        ButterKnife.bind(this);
        filters = new HashMap<>();
        filterModel = new FilterModel();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getParcelable(Constant.FILTER) != null) {
            filterModel = bundle.getParcelable(Constant.FILTER);
        }
        initToolbar();
        initComponent();
    }

    private void initComponent() {
        setupViewPager(viewPager);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        viewPager.setOffscreenPageLimit(3);

        if (filterModel != null) {
            if (filterModel.getSection().equalsIgnoreCase(Constant.FILTER_REMOTE)) {
                viewPager.setCurrentItem(0);
                rbRemotely.setChecked(true);
                rbInPerson.setChecked(false);
                rbAll.setChecked(false);
            } else if (filterModel.getSection().equalsIgnoreCase(Constant.FILTER_IN_PERSON)) {
                viewPager.setCurrentItem(1);
                rbRemotely.setChecked(false);
                rbInPerson.setChecked(true);
                rbAll.setChecked(false);
            } else {
                viewPager.setCurrentItem(2);
                rbRemotely.setChecked(false);
                rbInPerson.setChecked(false);
                rbAll.setChecked(true);
            }
        } else {
            viewPager.setCurrentItem(2);
            rbRemotely.setChecked(false);
            rbInPerson.setChecked(false);
            rbAll.setChecked(true);
        }


        rgFilters.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb_btn = (RadioButton) findViewById(checkedId);
            if (rb_btn.getText().equals(getResources().getString(R.string.remotely))) {
                viewPager.setCurrentItem(0);
                rbRemotely.setChecked(true);
                rbInPerson.setChecked(false);
                rbAll.setChecked(false);
            } else if (rb_btn.getText().equals(getResources().getString(R.string.in_person))) {
                viewPager.setCurrentItem(1);
                rbRemotely.setChecked(false);
                rbInPerson.setChecked(true);
                rbAll.setChecked(false);
            } else if (rb_btn.getText().equals(getResources().getString(R.string.all))) {
                viewPager.setCurrentItem(2);
                rbRemotely.setChecked(false);
                rbInPerson.setChecked(false);
                rbAll.setChecked(true);
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(FilterRemotelyFragment.newInstance(this, filterModel), getResources().getString(R.string.remotely));
        adapter.addFragment(FilterInPersonFragment.newInstance(this, filterModel), getResources().getString(R.string.in_person));
        adapter.addFragment(FilterAllFragment.newInstance(this, filterModel), getResources().getString(R.string.all));
        viewPager.setAdapter(adapter);
    }


    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Filters");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constant.FILTER, filterModel);
        intent.putExtras(bundle);
        setResult(101, intent);

        super.onBackPressed();
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(final int position) {
            switch (position) {
                case 0:
                    rbRemotely.setChecked(true);
                    rbInPerson.setChecked(false);
                    rbAll.setChecked(false);
                    break;
                case 1:
                    rbRemotely.setChecked(false);
                    rbInPerson.setChecked(true);
                    rbAll.setChecked(false);
                    break;
                case 2:
                    rbRemotely.setChecked(false);
                    rbInPerson.setChecked(false);
                    rbAll.setChecked(true);
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
    public void getInPersonData(FilterModel filterModel) {

        this.filterModel = filterModel;

        onBackPressed();
    }

    @Override
    public void getAllData(FilterModel filterModel) {
        this.filterModel = filterModel;
        onBackPressed();
    }

    @Override
    public void getRemoteData(FilterModel filterModel) {
        this.filterModel = filterModel;
        onBackPressed();
    }


    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
