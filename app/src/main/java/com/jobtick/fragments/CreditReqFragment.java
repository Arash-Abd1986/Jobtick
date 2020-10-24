package com.jobtick.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.jobtick.EditText.EditTextMedium;
import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;
import com.jobtick.activities.ActivityBase;
import com.jobtick.activities.AddBankAccountActivity;
import com.jobtick.activities.PaymentSettingsActivity;
import com.jobtick.models.BankAccountModel;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static com.jobtick.activities.PaymentSettingsActivity.onBankaccountadded;
import static com.jobtick.utils.Constant.ADD_ACCOUNT_DETAILS;
import static com.jobtick.utils.Constant.BASE_URL;

public class CreditReqFragment extends Fragment {

    private TextView btnNext;
    private EditTextMedium edtAccountName;
    private EditTextMedium edtBsb;
    private EditTextMedium edtAccountNumber;
    private SessionManager sessionManager;

    public CreditReqFragment() {
    }

    public static CreditReqFragment newInstance() {
        return new CreditReqFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(getContext());
        btnNext = view.findViewById(R.id.txt_btn_next);
        btnNext.setOnClickListener(v -> {
            if (validate()) {
                addBankAccountDetails();
            }
        });
        edtAccountName = view.findViewById(R.id.edt_account_name);
        edtBsb = view.findViewById(R.id.edt_bsb);
        edtAccountNumber = view.findViewById(R.id.edt_account_number);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_credit_req, container, false);
    }

    public void addBankAccountDetails() {
        ((ActivityBase) getActivity()).showProgressDialog();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, BASE_URL + ADD_ACCOUNT_DETAILS,
                response -> {
                    Timber.e(response);
                    ((ActivityBase) getActivity()).hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                Toast.makeText(getActivity(), "Updated successfully !", Toast.LENGTH_SHORT).show();
                                ((RequirementsBottomSheet) getParentFragment()).changeFragment(2);
                                if (onBankaccountadded != null) {
                                    onBankaccountadded.bankAccountAdd();
                                }
                                ((ActivityBase) getActivity()).finish();
                            } else {
                                Toast.makeText(getActivity(), "Something went Wrong !", Toast.LENGTH_SHORT).show();
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
                            ((ActivityBase) getActivity()).hideProgressDialog();
                            return;
                        }
                        try {
                            ((ActivityBase) getActivity()).hideProgressDialog();
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                            // showCustomDialog(jsonObject_error.getString("message"));
                            if (jsonObject_error.has("message")) {
                                Toast.makeText(getActivity(), jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            Toast.makeText(getActivity(), "errors", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Something Went Wrong !", Toast.LENGTH_SHORT).show();

                    }
                    Timber.e(error.toString());
                    ((ActivityBase) getActivity()).hideProgressDialog();
                }) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("account_name", edtAccountName.getText().toString());
                map1.put("account_number", edtAccountNumber.getText().toString());
                map1.put("bsb_code", edtBsb.getText().toString());

                return map1;
            }

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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }

    public void getBankAccountDetails() {
        ((ActivityBase) getActivity()).showProgressDialog();
        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, BASE_URL + ADD_ACCOUNT_DETAILS,
                response -> {
                    Timber.e(response);
                    ((ActivityBase) getActivity()).hideProgressDialog();
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
                                            edtAccountName.setText(data.getData().getAccount_name());
                                            edtBsb.setText(data.getData().getBsb_code());
                                            edtAccountNumber.setText(data.getData().getAccount_number());
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
                            ((ActivityBase) getActivity()).unauthorizedUser();`
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

    public boolean validate() {
        if (TextUtils.isEmpty(edtAccountName.getText().toString())) {
            edtAccountName.setError("Please Enter Account Name");
            return false;
        }

        if (TextUtils.isEmpty(edtBsb.getText().toString())) {
            edtBsb.setError("Please enter BSB");
            return false;
        }

        if (TextUtils.isEmpty(edtAccountNumber.getText().toString())) {
            edtAccountNumber.setError("Please account Number");
            return false;
        }
        return true;
    }
}