package com.jobtick.fragments;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jobtick.TextView.TextViewMedium;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker;
import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.TextView.TextViewSemiBold;
import com.jobtick.activities.NewTaskAlertsActivity;
import com.jobtick.models.GeocodeObject;
import com.jobtick.models.task.TaskAlert;
import com.jobtick.utils.Helper;
import com.jobtick.utils.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewTaskAlertsInPersonFragment extends Fragment {
    @BindView(R.id.lyt_btn_save_alert)
    LinearLayout lytBtnSaveAlert;
    @BindView(R.id.txt_save_update_alert)
    TextViewRegular txtSaveUpdateAlert;
    private int PLACE_SELECTION_REQUEST_CODE = 1;
    private String TAG = TaskDetailFragment.class.getName();
    @BindView(R.id.edt_keyword)
    EditTextRegular edtKeyword;
    @BindView(R.id.txt_suburb)
    TextViewMedium txtSuburb;
    @BindView(R.id.img_map_pin)
    ImageView imgMapPin;
    @BindView(R.id.txt_distance_km)
    TextViewSemiBold txtDistanceKm;
    @BindView(R.id.sk_distance)
    SeekBar skDistance;

    private NewTaskAlertsActivity newTaskAlertsActivity;
    private SessionManager sessionManager;
    TaskAlert taskAlert;
    int position;
    private OperationInPersonListener operationInPersonListener;

    public static NewTaskAlertsInPersonFragment newInstance(TaskAlert taskAlert, int position, OperationInPersonListener operationInPersonListener) {
        Bundle args = new Bundle();
        args.putParcelable("TASK_ALERT", taskAlert);
        args.putInt("POSITION", position);
        NewTaskAlertsInPersonFragment fragment = new NewTaskAlertsInPersonFragment();
        fragment.operationInPersonListener = operationInPersonListener;
        fragment.setArguments(args);
        return fragment;
    }

    public NewTaskAlertsInPersonFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_task_alerts_in_person, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newTaskAlertsActivity = (NewTaskAlertsActivity) getActivity();
        assert newTaskAlertsActivity != null;
        sessionManager = new SessionManager(newTaskAlertsActivity);
        taskAlert = new TaskAlert();
        if (getArguments() != null) {
            taskAlert = getArguments().getParcelable("TASK_ALERT");
            position = getArguments().getInt("POSITION");
        }
        if (taskAlert.isValid()) {
            txtSuburb.setText(taskAlert.getSuburb());
            edtKeyword.setText(taskAlert.getKetword());
            skDistance.setProgress(taskAlert.getDistance());
            txtDistanceKm.setText(taskAlert.getDistance() + " KM");
            txtSaveUpdateAlert.setText("Update alert");
        } else {
            txtSaveUpdateAlert.setText("save alert");
        }
        seekbar();
    }

    private void seekbar() {
        skDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtDistanceKm.setText(progress + " KM");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_SELECTION_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            CarmenFeature carmenFeature = PlacePicker.getPlace(data);
            Helper.Logger(TAG, "CarmenFeature = " + carmenFeature.toJson());
            GeocodeObject geocodeObject = Helper.getGeoCodeObject(getActivity(), carmenFeature.center().latitude()
                    , carmenFeature.center().longitude());
            //txtSuburb.setText(geocodeObject.getAddress());
            txtSuburb.setText(carmenFeature.placeName());

            // editArea.setText(geocodeObject.getKnownName());
              taskAlert.setLattitude(carmenFeature.center().latitude());
            taskAlert.setLongitude(carmenFeature.center().longitude());
            //  locationObject = new LatLng(carmenFeature.center().latitude(), carmenFeature.center().longitude());
        }
    }


    @OnClick({R.id.txt_suburb, R.id.rlt_suburb, R.id.lyt_btn_save_alert})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_suburb:
            case R.id.rlt_suburb:
                Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .accessToken(Mapbox.getAccessToken())
                        .placeOptions(PlaceOptions.builder()
                                // .backgroundColor(Helper.getAttrColor(taskCreateActivity, R.attr.colorBackground))
                                .backgroundColor(newTaskAlertsActivity.getResources().getColor(R.color.background))
                                .limit(30)
                                .country("AU")

                                /*.addInjectedFeature(home)
                                .addInjectedFeature(work)*/
                                .build(PlaceOptions.MODE_FULLSCREEN))
                        .build(getActivity());
                startActivityForResult(intent, PLACE_SELECTION_REQUEST_CODE);
                break;
            case R.id.lyt_btn_save_alert:
                switch (getValidationCode()) {
                    case 0:
                        taskAlert.setAlert_type("physical");
                        taskAlert.setDistance(Integer.parseInt(txtDistanceKm.getText().toString().trim().replace(" KM", "")));
                        taskAlert.setKetword(edtKeyword.getText().toString().trim());
                        taskAlert.setSuburb(txtSuburb.getText().toString().trim());

                        operationInPersonListener.onInPersonSave(position, taskAlert);
                        break;
                    case 1:
                        edtKeyword.setError("Please enter keyword");
                        break;
                    case 2:
                        txtSuburb.setError("Select suburb");
                        break;
                }
                break;
        }
    }



    private int getValidationCode() {
        if (TextUtils.isEmpty(edtKeyword.getText().toString().trim())) {
            return 1;
        } else if (TextUtils.isEmpty(txtSuburb.getText().toString().trim())) {
            return 2;
        }
        return 0;
    }

    public interface OperationInPersonListener {
        void onInPersonSave(int position, TaskAlert taskAlert);
    }
}
