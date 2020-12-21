package com.jobtick.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.activities.NewTaskAlertsActivity;
import com.jobtick.models.task.TaskAlert;
import com.jobtick.utils.SessionManager;
import com.jobtick.widget.ExtendedEntryText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewTaskAlertsRemoteFragment extends Fragment {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_keyword)
    ExtendedEntryText edtKeyword;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_update_alert)
    MaterialButton btnUpdate;
    NewTaskAlertsActivity newTaskAlertsActivity;
    SessionManager sessionManager;
    TaskAlert taskAlert;
    int position;
    private OperationRemoteListener operationRemoteListener;


    public static NewTaskAlertsRemoteFragment newInstance(TaskAlert taskAlert, int position, OperationRemoteListener operationRemoteListener) {
        Bundle args = new Bundle();
        args.putParcelable("TASK_ALERT", taskAlert);
        args.putInt("POSITION", position);
        NewTaskAlertsRemoteFragment fragment = new NewTaskAlertsRemoteFragment();
        fragment.operationRemoteListener = operationRemoteListener;
        fragment.setArguments(args);
        return fragment;
    }

    public NewTaskAlertsRemoteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_task_alerts_remote, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newTaskAlertsActivity = (NewTaskAlertsActivity) requireActivity();
        assert newTaskAlertsActivity != null;
        sessionManager = new SessionManager(newTaskAlertsActivity);
        taskAlert = new TaskAlert();
        if (getArguments() != null) {
            taskAlert = getArguments().getParcelable("TASK_ALERT");
            position = getArguments().getInt("POSITION");
        }
        if (taskAlert.isValid()) {
            edtKeyword.setText(taskAlert.getKetword());
            btnUpdate.setText(R.string.update_alert);
        } else {
            //  txtSaveUpdateAlert.setText("save alert");
        }

    }

    @OnClick(R.id.btn_update_alert)
    public void onViewClicked() {
        switch (getValidationCode()) {
            case 0:
                taskAlert.setAlert_type("remote");
                taskAlert.setKetword(edtKeyword.getText().trim());
                operationRemoteListener.onRemoteSave(position, taskAlert);
                break;
            case 1:
                edtKeyword.setError("Please enter keyword");
                break;
        }
    }

    private int getValidationCode() {
        if (TextUtils.isEmpty(edtKeyword.getText().trim())) {
            return 1;
        }
        return 0;
    }

    public interface OperationRemoteListener {
        void onRemoteSave(int position, TaskAlert taskAlert);
    }
}
