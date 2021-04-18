package com.jobtick.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.android.R;

import android.annotation.SuppressLint;
import android.widget.Switch;

import com.jobtick.android.fragments.AbstractFilterFragment;
import com.jobtick.android.fragments.FilterAllFragment;
import com.jobtick.android.fragments.FilterInPersonFragment;
import com.jobtick.android.fragments.FilterRemotelyFragment;
import com.jobtick.android.models.FilterModel;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.SuburbAutoComplete;
import com.jobtick.android.widget.ExtendedEntryText;

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
    SwitchCompat rbRemotely;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rb_in_person)
    SwitchCompat rbInPerson;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_suburb)
    ExtendedEntryText txtSuburb;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    FilterModel filterModel;
    Map<String, String> filters;
    private boolean all = true;
    private AbstractFilterFragment abstractFilterFragment1;
    private AbstractFilterFragment abstractFilterFragment2;
    private AbstractFilterFragment abstractFilterFragment3;
    private int currentTab = 0;

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


    public void setSuburb(String suburb){
        txtSuburb.setText(suburb);
    }
    public void setSubError(String error){
        txtSuburb.setError(error);
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
                all = (false);
            } else if (filterModel.getSection().equalsIgnoreCase(Constant.FILTER_IN_PERSON)) {
                viewPager.setCurrentItem(1);
                rbRemotely.setChecked(false);
                rbInPerson.setChecked(true);
                all = (false);
            } else {
                viewPager.setCurrentItem(2);
                rbRemotely.setChecked(false);
                rbInPerson.setChecked(false);
                all = (true);
            }
        } else {
            viewPager.setCurrentItem(2);
            rbRemotely.setChecked(false);
            rbInPerson.setChecked(false);
            all = (true);
        }


        rbRemotely.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!rbInPerson.isChecked()) {
                    viewPager.setCurrentItem(0);
                    all = (false);
                }
                if (rbInPerson.isChecked()) {
                    all = (true);
                    viewPager.setCurrentItem(2);
                }
            } else {
                viewPager.setCurrentItem(1);
                rbInPerson.setChecked(true);
                all = (false);
            }
        });
        rbInPerson.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!rbRemotely.isChecked()) {
                    viewPager.setCurrentItem(1);
                    all = (false);
                }
                if (rbRemotely.isChecked()) {
                    all = (true);
                    viewPager.setCurrentItem(2);
                }
            } else {
                rbRemotely.setChecked(true);
                viewPager.setCurrentItem(0);
                all = (false);
            }
        });
        txtSuburb.setExtendedViewOnClickListener(() -> {
            switch (currentTab){
                case 0:
                    abstractFilterFragment1.startFindLocation();
                    break;
                case 1:
                    abstractFilterFragment2.startFindLocation();
                    break;
                case 2:
                    abstractFilterFragment3.startFindLocation();
                    break;
            }

        });

    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        abstractFilterFragment1 = FilterRemotelyFragment.newInstance(this, filterModel);
        abstractFilterFragment2 = FilterInPersonFragment.newInstance(this, filterModel);
        abstractFilterFragment3 = FilterAllFragment.newInstance(this, filterModel);
        adapter.addFragment(abstractFilterFragment1, getResources().getString(R.string.remotely));
        adapter.addFragment(abstractFilterFragment2, getResources().getString(R.string.in_person));
        adapter.addFragment(abstractFilterFragment3, getResources().getString(R.string.all));

        viewPager.setAdapter(adapter);
    }


    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Filter");
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
            currentTab = position;
            switch (position) {
                case 0:
                    txtSuburb.setVisibility(View.GONE);
                    break;
                case 1:
                    txtSuburb.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    txtSuburb.setVisibility(View.VISIBLE);
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
