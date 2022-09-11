package com.jobtick.android.fragments;

import android.os.Bundle;
import com.jobtick.android.models.FilterModel;
import com.jobtick.android.utils.Constant;

public class FilterAllFragment extends AbstractFilterFragment {

    private FragmentCallbackFilterAll fragmentCallbackFilterAll;

    public static FilterAllFragment newInstance(FragmentCallbackFilterAll fragmentCallbackFilterAll,
                                                FilterModel filterModel) {

        Bundle args = new Bundle();
        args.putParcelable(Constant.FILTER, filterModel);
        FilterAllFragment fragment = new FilterAllFragment();
        fragment.fragmentCallbackFilterAll = fragmentCallbackFilterAll;
        fragment.setArguments(args);
        return fragment;
    }

    public FilterAllFragment() {
        // Required empty public constructor
    }

    @Override
    public void fragmentCallback(FilterModel filterModel) {
        fragmentCallbackFilterAll.getAllData(filterModel);
    }

    @Override
    public int getFilterType() {
        return FilterType.ALL;
    }

    public interface FragmentCallbackFilterAll {
        void getAllData(FilterModel filterModel);
    }
}
