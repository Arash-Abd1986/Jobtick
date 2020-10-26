package com.jobtick.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jobtick.R;
import com.jobtick.activities.ActivityBase;
import com.jobtick.models.UserAccountModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class PhoneReqFragment extends Fragment {

    private UserAccountModel userAccountModel;
    private SessionManager sessionManager;
    private TextView btnNext, phone, verify;

    public PhoneReqFragment() {
    }

    public static PhoneReqFragment newInstance() {
        return new PhoneReqFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_phone_req, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(getContext());
        phone = view.findViewById(R.id.edt_phone_number);
        verify = view.findViewById(R.id.edt_sms);
        btnNext = view.findViewById(R.id.txt_btn_submit);
        btnNext.setOnClickListener(v -> {
            // ((RequirementsBottomSheet) getParentFragment()).changeFragment(3);

        });
    }

    private boolean validation() {
        if (TextUtils.isEmpty(phone.getText().toString().trim())) {
            phone.setError("Enter mobile number");
            return false;
        } else if (TextUtils.isEmpty(verify.getText().toString().trim())) {
            verify.setError("Enter verify");
        }
        return true;
    }

    private void setUpPhone(UserAccountModel userAccountModel) {
        phone.setText(userAccountModel.getMobile());
        verify.setText(userAccountModel.getMobileVerifiedAt());

    }

}
