package com.jobtick.fragments;

import android.os.Bundle;

import com.jobtick.models.FilterModel;
import com.jobtick.utils.Constant;


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
    void fragmentCallback(FilterModel filterModel) {
        fragmentCallbackFilterRemote.getRemoteData(filterModel);
    }

    @Override
    int getFilterType() {
        return FilterType.REMOTELY;
    }

    public interface FragmentCallbackFilterRemote {
        void getRemoteData(FilterModel filterModel);
    }
}
