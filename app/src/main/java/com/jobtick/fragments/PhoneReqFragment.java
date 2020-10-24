package com.jobtick.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jobtick.R;
import com.jobtick.activities.ActivityBase;
import com.jobtick.activities.EditProfileActivity;
import com.jobtick.models.AttachmentModel;
import com.jobtick.models.UserAccountModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.Helper;
import com.jobtick.utils.ImageUtil;
import com.jobtick.utils.SessionManager;
import com.jobtick.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import timber.log.Timber;

import static com.jobtick.fragments.ProfileFragment.onProfileupdatelistener;


public class PhoneReqFragment extends Fragment {


    private UserAccountModel userAccountModel;
    SessionManager sessionManager;
    private TextView btnNext,phone,verify;

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
        phone=view.findViewById(R.id.edt_phone_number);
        verify=view.findViewById(R.id.edt_sms);
        btnNext = view.findViewById(R.id.txt_btn_submit);
        btnNext.setOnClickListener(v -> {
           // ((RequirementsBottomSheet) getParentFragment()).changeFragment(3);

        });
        getAllUserProfileDetails();
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

    private void getAllUserProfileDetails() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_PROFILE + "/" + sessionManager.getUserAccount().getId(),
                response -> {
                    if (getActivity() != null) {
                        ((ActivityBase) getActivity()).hideProgressDialog();
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());

                        if (jsonObject.has("data") && !jsonObject.isNull("data")) {

                            userAccountModel = new UserAccountModel().getJsonToModel(jsonObject.getJSONObject("data"));
                            setUpPhone(userAccountModel);
                        } else {
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        Toast.makeText(getActivity(), "JSONException", Toast.LENGTH_SHORT).show();
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                    }
                },
                error -> {
                    if (getActivity() != null) {
                        ((ActivityBase) getActivity()).errorHandle1(error.networkResponse);
                        ((ActivityBase) getActivity()).hideProgressDialog();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                // map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }
        };

        if (getActivity() != null) {
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(stringRequest);
        }
    }


    private void setUpPhone(UserAccountModel userAccountModel) {
        if (userAccountModel.getMobile() != null && !userAccountModel.getMobile().equals("")) {
          //  ((RequirementsBottomSheet) getParentFragment()).changeFragment(1);

        }
        phone.setText(userAccountModel.getMobile());
        verify.setText(userAccountModel.getMobileVerifiedAt());

    }

}
