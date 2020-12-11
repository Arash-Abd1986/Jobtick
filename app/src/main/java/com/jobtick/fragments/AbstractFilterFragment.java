package com.jobtick.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;
import com.jobtick.R;
import com.jobtick.activities.FiltersActivity;
import com.jobtick.models.FilterModel;
import com.jobtick.utils.Constant;
import com.jobtick.widget.ExtendedEntryText;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * In person filter and all filter is similar, so we use abstract for it
 */
public abstract class AbstractFilterFragment extends Fragment {


    @BindView(R.id.txt_suburb)
    ExtendedEntryText txtSuburb;
    @BindView(R.id.txt_distance_km)
    TextView txtDistanceKm;
    @BindView(R.id.sk_distance)
    Slider skDistance;
    @BindView(R.id.txt_price_min_max)
    TextView txtPriceMinMax;
    @BindView(R.id.sk_price)
    RangeSlider skPrice;
    @BindView(R.id.cb_open_tasks)
    CheckBox cbOpenTasks;
    @BindView(R.id.lyt_btn_save_filter)
    MaterialButton lytBtnSaveFilter;

    @BindView(R.id.distance_container)
    LinearLayout distanceContainer;

    private FiltersActivity filtersActivity;
    private final int pMin = 5;
    private final int pMax = 9999;
    private final int PLACE_SELECTION_REQUEST_CODE = 1;
    private FilterModel filterModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        ButterKnife.bind(this, view);
        txtSuburb.setExtendedViewOnClickListener(() -> {
            Intent intent = new PlaceAutocomplete.IntentBuilder()
                    .accessToken(Mapbox.getAccessToken())
                    .placeOptions(PlaceOptions.builder()
                            .backgroundColor(filtersActivity.getResources().getColor(R.color.backgroundLightGrey))
                            .limit(10)
                            .country("AU")
                            .build(PlaceOptions.MODE_FULLSCREEN))
                    .build(getActivity());
            startActivityForResult(intent, PLACE_SELECTION_REQUEST_CODE);
        });

        if (getFilterType() == FilterType.REMOTELY) {
            distanceContainer.setVisibility(View.GONE);
            txtSuburb.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        filtersActivity = (FiltersActivity) getActivity();
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
                txtDistanceKm.setText(String.format("%sKM", (int) Float.parseFloat(filterModel.getDistance())));
                skDistance.setValue((int) Float.parseFloat(filterModel.getDistance()));
            }
        }

    } else

    {
        setSeekBarPrice(pMin, pMax);
    }

    seekbar();

    seekbarPrice();
}

    private void getPminPmax(int min, int max) {
        skPrice.setValues((float) min, (float) max);
    }

    @SuppressLint("DefaultLocale")
    private void seekbarPrice() {
        skPrice.addOnChangeListener((slider, value, fromUser) -> {
            int min = (int) (float) slider.getValues().get(0);
            int max = (int) (float) slider.getValues().get(1);
            System.out.println("filter: min: " + min + "max: " + max);
            setSeekBarPrice(min, max);
        });
    }

    @SuppressLint("DefaultLocale")
    private void setSeekBarPrice(int pMin, int pMax) {
        txtPriceMinMax.setText(String.format("$ %d - $ %d", pMin, pMax));
    }


    @SuppressLint("DefaultLocale")
    private void seekbar() {
        skDistance.addOnChangeListener((slider, value, fromUser) -> {
            txtDistanceKm.setText(String.format("%d KM", (int) slider.getValue()));
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_SELECTION_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {

            // Retrieve the information from the selected location's CarmenFeature
            CarmenFeature carmenFeature = PlacePicker.getPlace(data);
            System.out.println("CarmenFeature = " + carmenFeature.toJson());
            txtSuburb.setText(carmenFeature.placeName());

            filterModel.setLatitude(String.valueOf(carmenFeature.center().latitude()));
            filterModel.setLogitude(String.valueOf(carmenFeature.center().longitude()));
        }
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.lyt_btn_save_filter)
    public void onViewClicked() {
        if (TextUtils.isEmpty(txtSuburb.getText().trim()) &&
                (getFilterType() == FilterType.IN_PERSON || getFilterType() == FilterType.ALL)) {
            txtSuburb.setError("Select location");
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
            filterModel.setDistance(String.valueOf((int) skDistance.getValue()));
        }
        filterModel.setPrice(txtPriceMinMax.getText().toString().trim());
        if (cbOpenTasks.isChecked()) {
            filterModel.setTask_open(Constant.FILTER_TASK_OPEN);
        } else {
            filterModel.setTask_open(null);
        }
        fragmentCallback(filterModel);
    }

    abstract void fragmentCallback(FilterModel filterModel);

    abstract int getFilterType();

public interface FilterType {
    int ALL = 0;
    int IN_PERSON = 1;
    int REMOTELY = 2;
}
}
