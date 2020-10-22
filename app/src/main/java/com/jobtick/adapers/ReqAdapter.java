package com.jobtick.adapers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.jobtick.fragments.CalenderReqFragment;
import com.jobtick.fragments.CreditReqFragment;
import com.jobtick.fragments.ImageReqFragment;
import com.jobtick.fragments.MapReqFragment;
import com.jobtick.fragments.PhoneReqFragment;

import java.util.List;

public class ReqAdapter extends FragmentStateAdapter {

    int mTab;


    public ReqAdapter(@NonNull FragmentActivity fragmentActivity, int mTab) {
        super(fragmentActivity);
        this.mTab = mTab;
    }

    public ReqAdapter(@NonNull Fragment fragment, int mTab) {
        super(fragment);
        this.mTab = mTab;
    }

    public ReqAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, int mTab) {
        super(fragmentManager, lifecycle);
        this.mTab = mTab;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
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

    @Override
    public int getItemCount() {
        return mTab;
    }
}
