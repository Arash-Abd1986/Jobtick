package com.jobtick.fragments;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;
import com.jobtick.utils.SuburbAutoComplete;
import com.jobtick.widget.ExtendedEntryText;
import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.activities.NewTaskAlertsActivity;
import com.jobtick.models.task.TaskAlert;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jobtick.utils.Constant.MAX_FILTER_DISTANCE_IN_KILOMETERS;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewTaskAlertsInPersonFragment extends Fragment {
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_update_alert)
    MaterialButton btnUpdate;
    private final int PLACE_SELECTION_REQUEST_CODE = 1;
    private final String TAG = TaskDetailFragment.class.getName();
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_keyword)
    ExtendedEntryText edtKeyword;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_suburb)
    ExtendedEntryText txtSuburb;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_distance_km)
    TextView txtDistanceKm;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.sk_distance)
    Slider skDistance;

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
        txtSuburb.setExtendedViewOnClickListener(() -> {
            Intent intent = new SuburbAutoComplete(requireActivity()).getIntent();
            startActivityForResult(intent, PLACE_SELECTION_REQUEST_CODE);
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NewTaskAlertsActivity newTaskAlertsActivity = (NewTaskAlertsActivity) requireActivity();
        assert newTaskAlertsActivity != null;
        taskAlert = new TaskAlert();
        if (getArguments() != null) {
            taskAlert = getArguments().getParcelable("TASK_ALERT");
            position = getArguments().getInt("POSITION");
        }
        if (taskAlert.isValid()) {
            txtSuburb.setText(taskAlert.getSuburb());
            edtKeyword.setText(taskAlert.getKetword());
            skDistance.setValue(taskAlert.getDistance());
            txtDistanceKm.setText(String.format(Locale.ENGLISH, "%d KM", taskAlert.getDistance()));
            btnUpdate.setText(R.string.update_alert);
        } else {
         //   txtSaveUpdateAlert.setText("save alert");
        }
        initSlider();
    }

    private void initSlider() {
        skDistance.addOnChangeListener((slider, value, fromUser) -> {
            if(slider.getValue() != 101){
                txtDistanceKm.setText(String.format(Locale.ENGLISH, "%d KM", (int) slider.getValue()));
            }else {
                txtDistanceKm.setText(R.string.plus_100_km);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_SELECTION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {



            txtSuburb.setText(SuburbAutoComplete.getSuburbName(data));
            taskAlert.setLattitude(SuburbAutoComplete.getLatitudeDouble(data));
            taskAlert.setLongitude(SuburbAutoComplete.getLongitudeDouble(data));
        }
    }


    @OnClick({R.id.btn_update_alert})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_update_alert:
                switch (getValidationCode()) {
                    case 0:
                        taskAlert.setAlert_type("physical");
                        if(skDistance.getValue() != 101)
                            taskAlert.setDistance((int)skDistance.getValue());
                        else{
                            taskAlert.setDistance(MAX_FILTER_DISTANCE_IN_KILOMETERS);
                        }
                        taskAlert.setKetword(edtKeyword.getText().trim());
                        taskAlert.setSuburb(txtSuburb.getText().trim());

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
        if (TextUtils.isEmpty(edtKeyword.getText().trim())) {
            return 1;
        } else if (TextUtils.isEmpty(txtSuburb.getText().trim())) {
            return 2;
        }
        return 0;
    }

    public interface OperationInPersonListener {
        void onInPersonSave(int position, TaskAlert taskAlert);
    }
}
