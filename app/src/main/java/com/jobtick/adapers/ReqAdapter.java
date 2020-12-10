package com.jobtick.adapers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.jobtick.fragments.DobReqFragment;
import com.jobtick.fragments.AddBankAccountReqFragment;
import com.jobtick.fragments.ProfileReqFragment;
import com.jobtick.fragments.AddBillingReqFragment;
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
                return new ProfileReqFragment();
            case 1:
                return new AddBankAccountReqFragment();
            case 2:
                return new AddBillingReqFragment();
            case 3:
                return new DobReqFragment();
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
