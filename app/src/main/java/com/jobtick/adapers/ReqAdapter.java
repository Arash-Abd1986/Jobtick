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
                ImageReqFragment imageReqFragment=new ImageReqFragment();
                return imageReqFragment;
            case 1:
                CreditReqFragment creditReqFragment=new CreditReqFragment();
                return creditReqFragment;
            case 2:
                MapReqFragment mapReqFragment=new MapReqFragment();
                return mapReqFragment;
            case 3:
                CalenderReqFragment calenderReqFragment=new CalenderReqFragment();
                return calenderReqFragment;
            case 4:
                PhoneReqFragment phoneReqFragment=new PhoneReqFragment();
                return phoneReqFragment;
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return mTab;
    }
}
