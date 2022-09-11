package com.jobtick.android.fragments;

import android.os.Bundle;

import com.jobtick.android.models.FilterModel;
import com.jobtick.android.utils.Constant;


public class FilterRemotelyFragment extends AbstractFilterFragment {

    private FragmentCallbackFilterRemote fragmentCallbackFilterRemote;
    public static FilterRemotelyFragment newInstance(FragmentCallbackFilterRemote fragmentCallbackFilterRemote,
                                                     FilterModel filterModel) {
        Bundle args = new Bundle();
        args.putParcelable(Constant.FILTER, filterModel);
        FilterRemotelyFragment fragment = new FilterRemotelyFragment();
        fragment.fragmentCallbackFilterRemote = fragmentCallbackFilterRemote;
        fragment.setArguments(args);
        return fragment;
    }

    public FilterRemotelyFragment() {
        // Required empty public constructor
    }

    @Override
    public void fragmentCallback(FilterModel filterModel) {
        fragmentCallbackFilterRemote.getRemoteData(filterModel);
    }

    @Override
    public int getFilterType() {
        return FilterType.REMOTELY;
    }

    public interface FragmentCallbackFilterRemote {
        void getRemoteData(FilterModel filterModel);
    }
}
