package com.jobtick.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.jobtick.R;
import com.jobtick.activities.ActivityBase;
import com.jobtick.models.BankAccountModel;
import com.jobtick.models.BillingAdreessModel;
import com.jobtick.models.UserAccountModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

import static com.jobtick.utils.Constant.ADD_ACCOUNT_DETAILS;
import static com.jobtick.utils.Constant.ADD_BILLING;
import static com.jobtick.utils.Constant.BASE_URL;

public class RequirementsBottomSheet extends BottomSheetDialogFragment {

    protected ProgressDialog pDialog;
    private ImageView img, credit, map, calender, phone;
    private SessionManager sessionManager;
    private UserAccountModel userAccountModel;
    private BankAccountModel bankAccountModel;
    private BillingAdreessModel billingAdreessModel;

    public RequirementsBottomSheet() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        sessionManager = new SessionManager(getContext());
        return inflater.inflate(R.layout.bottom_sheet_requirements, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getAllUserProfileDetails();
        getBankAccountAddress();
        getBillingAddress();
        img = view.findViewById(R.id.img_requirement);
        credit = view.findViewById(R.id.credit_requirement);
        map = view.findViewById(R.id.map_requirement);
        calender = view.findViewById(R.id.calender_requirement);
        phone = view.findViewById(R.id.phone_requirement);
        img.setOnClickListener(v -> selectImageBtn());
        credit.setOnClickListener(v -> selectCreditBtn());
        map.setOnClickListener(v -> selectMapBtn());
        calender.setOnClickListener(v -> selectCalenderBtn());
        phone.setOnClickListener(v -> selectPhoneBtn());
    }

    private void getAllUserProfileDetails() {
        ((ActivityBase) getActivity()).showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_PROFILE + "/" + sessionManager.getUserAccount().getId(),
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());

                        if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                            userAccountModel = new UserAccountModel().getJsonToModel(jsonObject.getJSONObject("data"));
                            handleState();
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

    public void getBankAccountAddress() {
        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, BASE_URL + ADD_ACCOUNT_DETAILS,
                response -> {
                    Timber.e(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                String jsonString = jsonObject.toString(); //http request
                                BankAccountModel data = new BankAccountModel();
                                Gson gson = new Gson();
                                data = gson.fromJson(jsonString, BankAccountModel.class);

                                if (data != null) {
                                    if (data.isSuccess()) {
                                        if (data.getData() != null && data.getData().getAccount_number() != null) {
                                            bankAccountModel = data;
                                            handleState();
                                        }
                                    }
                                }
                            } else {
                                if (getActivity() == null) {
                                    return;
                                }
                                ((ActivityBase) getActivity()).showToast("Something went Wrong", getActivity());
                            }
                        }
                    } catch (JSONException e) {
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                    }
                },
                error -> {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        // Print Error!
                        Timber.e(jsonError);
                        if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                            if (getActivity() == null) {
                                return;
                            }
                            ((ActivityBase) getActivity()).unauthorizedUser();
                            ((ActivityBase) getActivity()).hideProgressDialog();
                            return;
                        }
                        try {
                            ((ActivityBase) getActivity()).hideProgressDialog();
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                            //  showCustomDialog(jsonObject_error.getString("message"));
                            if (jsonObject_error.has("message")) {
                                Toast.makeText(getContext(), jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (getActivity() == null) {
                            return;
                        }
                        ((ActivityBase) getActivity()).showToast("Something Went Wrong", getContext());

                    }
                    Timber.e(error.toString());
                    ((ActivityBase) getActivity()).hideProgressDialog();
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    public void getBillingAddress() {

        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, BASE_URL + ADD_BILLING,
                response -> {
                    Timber.e(response);
                    try {
                        if (getActivity() != null) {
                            ((ActivityBase) getActivity()).hideProgressDialog();
                        }
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                String jsonString = jsonObject.toString(); //http request
                                BillingAdreessModel data = new BillingAdreessModel();
                                Gson gson = new Gson();
                                data = gson.fromJson(jsonString, BillingAdreessModel.class);

                                if (data != null) {
                                    if (data.isSuccess()) {
                                        if (data.getData() != null && data.getData().getLine1() != null) {
                                            billingAdreessModel = data;
                                            handleState();
                                        }
                                    }
                                }
                            } else {
                                ((ActivityBase) getActivity()).showToast("Something went Wrong", getContext());
                            }
                        }
                    } catch (JSONException e) {
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                    }
                },
                error -> {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        // Print Error!
                        Timber.e(jsonError);
                        if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                            ((ActivityBase) getActivity()).unauthorizedUser();
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                            //  showCustomDialog(jsonObject_error.getString("message"));
                            if (jsonObject_error.has("message")) {
                                Toast.makeText(getContext(), jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ((ActivityBase) getActivity()).showToast("Something Went Wrong", getContext());
                    }
                    Timber.e(error.toString());
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void handleState() {
        if (userAccountModel != null) {
            if (userAccountModel.getAvatar() == null || userAccountModel.getAvatar().getUrl().equals("")) {
                selectImageBtn();
            } else if (bankAccountModel == null || bankAccountModel.getData() == null || bankAccountModel.getData().getAccount_name().equals("") || bankAccountModel.getData().getAccount_number().equals("")) {
                selectCreditBtn();
            } else if (userAccountModel.getDob() == null || userAccountModel.getDob().equals("")) {
                selectCalenderBtn();
            } else if (billingAdreessModel == null || billingAdreessModel.getData() == null || billingAdreessModel.getData().getLocation().equals("") || billingAdreessModel.getData().getPost_code().equals("")) {
                selectMapBtn();
            } else if (userAccountModel.getMobile().equals("") || userAccountModel.getMobileVerifiedAt().equals("")) {
                selectPhoneBtn();
            }
        } else {
            selectImageBtn();
        }
    }

    private void selectImageBtn() {
        img.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_white_shape));
        map.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        credit.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        calender.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        phone.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));

        ImageReqFragment fragment = ImageReqFragment.newInstance();
        getChildFragmentManager().
                beginTransaction().
                replace(R.id.requirements_layout, fragment).
                commitAllowingStateLoss();

    }

    private void selectCreditBtn() {
        img.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        map.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        credit.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_white_shape));
        calender.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        phone.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));

        CreditReqFragment fragment = CreditReqFragment.newInstance();
        getChildFragmentManager().
                beginTransaction().
                replace(R.id.requirements_layout, fragment).
                commitAllowingStateLoss();
    }

    private void selectMapBtn() {
        img.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        map.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_white_shape));
        credit.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        calender.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        phone.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));


        MapReqFragment fragment = MapReqFragment.newInstance();
        getChildFragmentManager().
                beginTransaction().
                replace(R.id.requirements_layout, fragment).
                commitAllowingStateLoss();
    }

    private void selectCalenderBtn() {
        img.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        map.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        credit.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        calender.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_white_shape));
        phone.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));

        CalenderReqFragment fragment = CalenderReqFragment.newInstance();
        getChildFragmentManager().
                beginTransaction().
                replace(R.id.requirements_layout, fragment).
                commitAllowingStateLoss();
    }

    private void selectPhoneBtn() {

        img.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        map.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        credit.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        calender.setBackground(ContextCompat.getDrawable(getContext(), R.color.transparent));
        phone.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_white_shape));

        PhoneReqFragment fragment = PhoneReqFragment.newInstance();
        getChildFragmentManager().
                beginTransaction().
                replace(R.id.requirements_layout, fragment).
                commitAllowingStateLoss();
    }

    public void changeFragment(int key) {
        switch (key) {
            case 0:
                selectImageBtn();
                break;
            case 1:
                selectCreditBtn();
                break;
            case 2:
                selectMapBtn();
                break;
            case 3:
                selectCalenderBtn();
                break;
            case 4:
                selectPhoneBtn();
                break;
        }

    }

}