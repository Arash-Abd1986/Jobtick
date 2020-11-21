package com.jobtick.fragments;

import android.os.Bundle;
import com.jobtick.models.FilterModel;
import com.jobtick.utils.Constant;


public class FilterInPersonFragment extends AbstractFilterFragment {

    private FragmentCallbackFilterInPerson fragmentCallbackFilterInPerson;

    public static FilterInPersonFragment newInstance(FragmentCallbackFilterInPerson fragmentCallbackFilterInPerson,
                                                     FilterModel filterModel) {

        Bundle args = new Bundle();
        args.putParcelable(Constant.FILTER, filterModel);
        FilterInPersonFragment fragment = new FilterInPersonFragment();
        fragment.fragmentCallbackFilterInPerson = fragmentCallbackFilterInPerson;
        fragment.setArguments(args);
        return fragment;
    }

    public FilterInPersonFragment() {
        // Required empty public constructor
    }

    @Override
    void fragmentCallback(FilterModel filterModel) {
        fragmentCallbackFilterInPerson.getInPersonData(filterModel);
    }

    public interface FragmentCallbackFilterInPerson {
        void getInPersonData(FilterModel filterModel);
    }
}
