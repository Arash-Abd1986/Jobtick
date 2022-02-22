package com.jobtick.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;
import com.jobtick.android.R;

import android.annotation.SuppressLint;

import com.jobtick.android.activities.FiltersActivity;
import com.jobtick.android.adapers.SuburbSearchAdapter;
import com.jobtick.android.models.FilterModel;
import com.jobtick.android.models.response.searchsuburb.Feature;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.Helper;
import com.jobtick.android.utils.SuburbAutoComplete;
import com.jobtick.android.widget.ExtendedEntryText;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jobtick.android.utils.Constant.MAX_FILTER_DISTANCE_IN_KILOMETERS;

/**
 * In person filter and all filter is similar, so we use abstract for it
 */
public abstract class AbstractFilterFragment extends Fragment implements SuburbSearchAdapter.SubClickListener {


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_suburb)
    ExtendedEntryText txtSuburb;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_distance_km)
    TextView txtDistanceKm;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.sk_distance)
    Slider skDistance;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_price_min_max)
    TextView txtPriceMinMax;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.sk_price)
    RangeSlider skPrice;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.cb_open_tasks)
    SwitchCompat cbOpenTasks;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_save_filter)
    LinearLayout lytBtnSaveFilter;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.cancel_filter)
    LinearLayout cancelFilter;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.distance_container)
    LinearLayout distanceContainer;

    private final int pMin = 5;
    private final int pMax = 9999;
    private final int PLACE_SELECTION_REQUEST_CODE = 1;
    private FilterModel filterModel;
    private FiltersActivity filtersActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        ButterKnife.bind(this, view);
        filtersActivity = (FiltersActivity) requireActivity();
        txtSuburb.setExtendedViewOnClickListener(() -> {
            Intent intent = new SuburbAutoComplete(requireActivity()).getIntent();
            startActivityForResult(intent, PLACE_SELECTION_REQUEST_CODE);
        });

        if (getFilterType() == FilterType.REMOTELY) {
            distanceContainer.setVisibility(View.GONE);
            txtSuburb.setVisibility(View.GONE);
        }
        return view;
    }

    public void startFindLocation() {
        SearchSuburbBottomSheet infoBottomSheet = new SearchSuburbBottomSheet(this, true,"");
        infoBottomSheet.show(getParentFragmentManager(), null);
    }

    @Override
    public void clickOnSearchedLoc(@NotNull Feature location) {
        Helper.closeKeyboard(requireActivity());
        txtSuburb.setText(location.getPlace_name_en());
        filtersActivity.setSuburb(location.getPlace_name_en());
        filterModel.setLatitude(String.valueOf(location.getGeometry().getCoordinates().get(1)));
        filterModel.setLogitude(String.valueOf(location.getGeometry().getCoordinates().get(0)));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        filterModel = new FilterModel();
        if (getArguments() != null) {
            if (getArguments().getParcelable(Constant.FILTER) != null) {
                filterModel = getArguments().getParcelable(Constant.FILTER);
                if (filterModel != null && filterModel.getPrice() != null) {
                    String[] price = filterModel.getPrice().replace("$", "").replace(",", "").split("-");
                    getPminPmax(Integer.parseInt(price[0].trim()), Integer.parseInt(price[1].trim()));
                    txtPriceMinMax.setText(filterModel.getPrice());
                }
                if (filterModel != null && filterModel.getTask_open() != null) {
                    cbOpenTasks.setChecked(filterModel.getTask_open() != null);
                }
                if (filterModel != null && (filterModel.getSection().equalsIgnoreCase(Constant.FILTER_ALL)
                        || filterModel.getSection().equalsIgnoreCase(Constant.FILTER_IN_PERSON))) {
                    txtSuburb.setText(filterModel.getLocation());
                    filtersActivity.setSuburb(filterModel.getLocation());
                    if (filterModel.getDistance().equals(Integer.toString(MAX_FILTER_DISTANCE_IN_KILOMETERS))) {
                        txtDistanceKm.setText(R.string.plus_100_km);
                        skDistance.setValue(105);
                    } else {
                        txtDistanceKm.setText(String.format("%s KM", (int) Float.parseFloat(filterModel.getDistance())));

                        //prevent crash in step size
                        if ((int) Float.parseFloat(filterModel.getDistance()) % 5 == 0)
                            skDistance.setValue((int) Float.parseFloat(filterModel.getDistance()));
                        else
                            skDistance.setValue(15);
                    }
                }
            }

        } else {
            setSeekBarPrice(pMin, pMax);
        }

        initDistanceSlider();

        initPriceSlider();
        setOnclick();
    }

    private void getPminPmax(int min, int max) {
        skPrice.setValues((float) min, (float) max);
    }

    private void initPriceSlider() {
        skPrice.addOnChangeListener((slider, value, fromUser) -> {
            int min = (int) (float) slider.getValues().get(0);
            int max = (int) (float) slider.getValues().get(1);
            System.out.println("filter: min: " + min + "max: " + max);
            setSeekBarPrice(min, max);
        });
    }

    private void setSeekBarPrice(int pMin, int pMax) {
        txtPriceMinMax.setText(String.format(Locale.ENGLISH, "$%d - $%d", pMin, pMax));
    }

    private void initDistanceSlider() {
        skDistance.addOnChangeListener((slider, value, fromUser) -> {
            if (slider.getValue() != 105) {
                txtDistanceKm.setText(String.format(Locale.ENGLISH, "%d KM", (int) slider.getValue()));
            } else {
                txtDistanceKm.setText(R.string.plus_100_km);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_SELECTION_REQUEST_CODE && resultCode == requireActivity().RESULT_OK) {
            txtSuburb.setText(SuburbAutoComplete.getSuburbName(data));
            filtersActivity.setSuburb(SuburbAutoComplete.getSuburbName(data));
            filterModel.setLatitude(SuburbAutoComplete.getLatitude(data));
            filterModel.setLogitude(SuburbAutoComplete.getLongitude(data));
        }
    }

    public void setOnclick() {
        lytBtnSaveFilter.setOnClickListener(v->{
            if (TextUtils.isEmpty(txtSuburb.getText().trim()) &&
                    (getFilterType() == FilterType.IN_PERSON || getFilterType() == FilterType.ALL)) {
                filtersActivity.setSubError("Select location");
                return;
            }

            if (getFilterType() == FilterType.ALL)
                filterModel.setSection(Constant.FILTER_ALL);
            else if (getFilterType() == FilterType.IN_PERSON)
                filterModel.setSection(Constant.FILTER_IN_PERSON);
            else if (getFilterType() == FilterType.REMOTELY) {
                filterModel.setSection(Constant.FILTER_REMOTE);
                filterModel.setDistance(null);
                filterModel.setLocation(null);
                filterModel.setLogitude(null);
                filterModel.setLatitude(null);
            }
            if (getFilterType() == FilterType.IN_PERSON || getFilterType() == FilterType.ALL) {
                filterModel.setLocation(txtSuburb.getText().trim());
                if (skDistance.getValue() != 105)
                    filterModel.setDistance(String.valueOf((int) skDistance.getValue()));
                else {
                    filterModel.setDistance(Integer.toString(MAX_FILTER_DISTANCE_IN_KILOMETERS));
                }
            }
            filterModel.setPrice(txtPriceMinMax.getText().toString().trim());
            if (cbOpenTasks.isChecked()) {
                filterModel.setTask_open(Constant.FILTER_TASK_OPEN);
            } else {
                filterModel.setTask_open(null);
            }
            fragmentCallback(filterModel);
        });
        cancelFilter.setOnClickListener(v->{
            txtSuburb.setText("");
            filtersActivity.setSuburb("");
            cbOpenTasks.setChecked(false);
        });
    }

    abstract void fragmentCallback(FilterModel filterModel);

    abstract int getFilterType();

    public interface FilterType {
        int ALL = 0;
        int IN_PERSON = 1;
        int REMOTELY = 2;
    }
}
