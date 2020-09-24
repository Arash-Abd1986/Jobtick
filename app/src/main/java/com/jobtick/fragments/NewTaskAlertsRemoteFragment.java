package com.jobtick.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;
import com.jobtick.TextView.TextViewMedium;
import com.jobtick.activities.NewTaskAlertsActivity;
import com.jobtick.models.task.TaskAlert;
import com.jobtick.utils.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewTaskAlertsRemoteFragment extends Fragment {

    @BindView(R.id.edt_keyword)
    EditTextRegular edtKeyword;
    @BindView(R.id.lyt_btn_save_alert)
    LinearLayout lytBtnSaveAlert;
    NewTaskAlertsActivity newTaskAlertsActivity;
    SessionManager sessionManager;
    TaskAlert taskAlert;
    int position;
    @BindView(R.id.txt_save_update_alert)
    TextViewMedium txtSaveUpdateAlert;
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
        newTaskAlertsActivity = (NewTaskAlertsActivity) getActivity();
        assert newTaskAlertsActivity != null;
        sessionManager = new SessionManager(newTaskAlertsActivity);
        taskAlert = new TaskAlert();
        if (getArguments() != null) {
            taskAlert = getArguments().getParcelable("TASK_ALERT");
            position = getArguments().getInt("POSITION");
        }
        if (taskAlert.isValid()) {
            edtKeyword.setText(taskAlert.getKetword());
            txtSaveUpdateAlert.setText("Update alert");
        } else {
            txtSaveUpdateAlert.setText("save alert");
        }

    }

    @OnClick(R.id.lyt_btn_save_alert)
    public void onViewClicked() {
        switch (getValidationCode()) {
            case 0:
                taskAlert.setAlert_type("remote");
                taskAlert.setKetword(edtKeyword.getText().toString().trim());
                operationRemoteListener.onRemoteSave(position, taskAlert);
                break;
            case 1:
                edtKeyword.setError("Please enter keyword");
                break;
        }
    }

    private int getValidationCode() {
        if (TextUtils.isEmpty(edtKeyword.getText().toString().trim())) {
            return 1;
        }
        return 0;
    }

    public interface OperationRemoteListener {
        void onRemoteSave(int position, TaskAlert taskAlert);
    }
}
