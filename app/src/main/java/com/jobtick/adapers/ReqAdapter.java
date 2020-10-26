package com.jobtick.adapers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.jobtick.fragments.CalenderReqFragment;
import com.jobtick.fragments.CreditReqFragment;
import com.jobtick.fragments.ImageReqFragment;
import com.jobtick.fragments.MapReqFragment;
import com.jobtick.fragments.PhoneReqFragment;

import java.util.ArrayList;
import java.util.List;

public class ReqAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();

    public ReqAdapter(FragmentManager manager) {
        super(manager);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new ImageReqFragment();
            case 1:
                return new CreditReqFragment();
            case 2:
                return new MapReqFragment();
            case 3:
                return new CalenderReqFragment();
            case 4:
                return new PhoneReqFragment();
            default:
                return null;
        }
    }


    public void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
