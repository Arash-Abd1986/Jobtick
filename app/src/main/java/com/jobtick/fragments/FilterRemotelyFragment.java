package com.jobtick.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.jobtick.R;
import com.jobtick.TextView.TextViewBold;
import com.jobtick.TextView.TextViewSemiBold;
import com.jobtick.activities.FiltersActivity;
import com.jobtick.models.FilterModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.apptik.widget.MultiSlider;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilterRemotelyFragment extends Fragment {


    @BindView(R.id.txt_price_min_max)
    TextViewBold txtPriceMinMax;
    @BindView(R.id.sk_price)
    MultiSlider skPrice;
    @BindView(R.id.cb_open_tasks)
    CheckBox cbOpenTasks;
    @BindView(R.id.lyt_btn_save_filter)
    LinearLayout lytBtnSaveFilter;
    @BindView(R.id.card_button)
    CardView cardButton;
    private SessionManager sessionManager;
    private FiltersActivity filtersActivity;
    private int pMin = 0, pMax = 100;
    private FilterModel filterModel;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filter_remotely, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        filtersActivity = (FiltersActivity) getActivity();
        filterModel = new FilterModel();
        if (filtersActivity != null) {
            sessionManager = new SessionManager(filtersActivity);
        }
        if (getArguments() != null) {
            if (getArguments().getParcelable(Constant.FILTER) != null) {
                filterModel = (FilterModel) getArguments().getParcelable(Constant.FILTER);
                if (filterModel != null && filterModel.getSection().equalsIgnoreCase(Constant.FILTER_REMOTE)) {
                    String[] price = filterModel.getPrice().replace("$", "").replace(",", "").split("-");
                    getPminPmax(Integer.parseInt(price[0].trim()), Integer.parseInt(price[1].trim()));
                    txtPriceMinMax.setText(filterModel.getPrice());
                    if (filterModel.getTask_open() != null) {
                        cbOpenTasks.setChecked(true);
                    } else {
                        cbOpenTasks.setChecked(false);
                    }
                }
            }

        } else {
            setSeekBarPrice(pMin, pMax);
        }


        seekbarPrice();


    }

    private void getPminPmax(int min, int max) {
        int a = 0, b = 0;
        // 5-10-20-50-100-200-500-1000-2000-3000-4000-5000-6000-7000-8000-9000-10000
        if (min == 10)
            a = 5;
        if (min == 20)
            a = 8;
        if (min == 50)
            a = 12;
        if (min == 100)
            a = 18;
        if (min == 200)
            a = 24;
        if (min == 500)
            a = 30;
        if (min == 1000)
            a = 36;
        if (min == 2000)
            a = 42;
        if (min == 3000)
            a = 48;
        if (min == 4000)
            a = 54;
        if (min == 5000)
            a = 60;
        if (min == 6000)
            a = 66;
        if (min == 7000)
            a = 72;
        if (min == 8000)
            a = 78;
        if (min == 9000)
            a = 84;
        if (min == 9500)
            a = 90;

        if (max == 20)
            b = 9;
        if (max == 50)
            b = 16;
        if (max == 100)
            b = 22;
        if (max == 200)
            b = 28;
        if (max == 500)
            b = 34;
        if (max == 1000)
            b = 40;
        if (max == 2000)
            b = 46;
        if (max == 3000)
            b = 52;
        if (max == 4000)
            b = 58;
        if (max == 5000)
            b = 64;
        if (max == 6000)
            b = 70;
        if (max == 7000)
            b = 76;
        if (max == 8000)
            b = 82;
        if (max == 9000)
            b = 88;
        if (max >= 9999)
            b = 94;

        skPrice.getThumb(0).setValue(a);
        skPrice.getThumb(1).setValue(b);


    }

    private void seekbarPrice() {
        skPrice.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
                if (thumbIndex == 0) {
                    //min value
                    pMin = value;
                } else {
                    //max value
                    pMax = value;
                }
                setSeekBarPrice(pMin, pMax);
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void setSeekBarPrice(int pMin, int pMax) {

        int a = 0, b = 0;
//       5-10-20-50-100-200-500-1000-2000-3000-4000-5000-6000-7000-8000-9000-10000
        if (pMin == 0) {
            a = 5;
        }
        if (pMin > 0 && pMin <= 6) {
            a = 10;
        }
        if (pMin >= 6 && pMin <= 12) {
            a = 20;
        }
        if (pMin >= 12 && pMin <= 18) {
            a = 50;
        }
        if (pMin >= 18 && pMin <= 24) {
            a = 100;
        }
        if (pMin >= 24 && pMin <= 30) {
            a = 200;
        }
        if (pMin >= 30 && pMin <= 36) {
            a = 500;
        }
        if (pMin >= 36 && pMin <= 42) {
            a = 1000;
        }
        if (pMin >= 42 && pMin <= 48) {
            a = 2000;
        }
        if (pMin >= 48 && pMin <= 54) {
            a = 3000;
        }
        if (pMin >= 54 && pMin <= 60) {
            a = 4000;
        }
        if (pMin >= 60 && pMin <= 66) {
            a = 5000;
        }
        if (pMin >= 66 && pMin <= 72) {
            a = 6000;
        }
        if (pMin >= 72 && pMin <= 78) {
            a = 7000;
        }
        if (pMin >= 78 && pMin <= 84) {
            a = 8000;
        }
        if (pMin >= 84 && pMin <= 90) {
            a = 9000;
        }
        if (pMin >= 90 && pMin <= 96) {
            a = 9500;
        }


        if (pMax >= 9 && pMax <= 16) {
            b = 20;
        }
        if (pMax >= 16 && pMax <= 22) {
            b = 50;
        }
        if (pMax >= 22 && pMax <= 28) {
            b = 100;
        }
        if (pMax >= 28 && pMax <= 34) {
            b = 200;
        }
        if (pMax >= 34 && pMax <= 40) {
            b = 500;
        }
        if (pMax >= 40 && pMax <= 46) {
            b = 1000;
        }
        if (pMax >= 46 && pMax <= 52) {
            b = 2000;
        }
        if (pMax >= 52 && pMax <= 58) {
            b = 3000;
        }
        if (pMax >= 58 && pMax <= 64) {
            b = 4000;
        }
        if (pMax >= 64 && pMax <= 70) {
            b = 5000;
        }
        if (pMax >= 70 && pMax <= 76) {
            b = 6000;
        }
        if (pMax >= 76 && pMax <= 82) {
            b = 7000;
        }
        if (pMax >= 82 && pMax <= 88) {
            b = 8000;
        }
        if (pMax >= 88 && pMax <= 94) {
            b = 9000;
        }
        if (pMax >= 94 && pMax <= 100) {
            b = 10000;
        }
        txtPriceMinMax.setText(String.format("$ %d - $ %d", a, b));
    }


    @OnClick(R.id.lyt_btn_save_filter)
    public void onViewClicked() {
        filterModel.setSection(Constant.FILTER_REMOTE);
        filterModel.setDistance(null);
        filterModel.setLocation(null);
        filterModel.setLogitude(null);
        filterModel.setLatitude(null);
        filterModel.setPrice(txtPriceMinMax.getText().toString().trim());
        if (cbOpenTasks.isChecked()) {
            filterModel.setTask_open(Constant.FILTER_TASK_OPEN);
        } else {
            filterModel.setTask_open(null);
        }
        fragmentCallbackFilterRemote.getRemoteData(filterModel);

    }

    public interface FragmentCallbackFilterRemote {
        void getRemoteData(FilterModel filterModel);
    }
}
