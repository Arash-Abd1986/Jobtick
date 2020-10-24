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
import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;
import com.jobtick.activities.ActivityBase;
import com.jobtick.activities.BillingAddressActivity;
import com.jobtick.activities.PaymentSettingsActivity;
import com.jobtick.models.BillingAdreessModel;
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
import static com.jobtick.utils.Constant.ADD_BILLING;
import static com.jobtick.utils.Constant.BASE_URL;


public class MapReqFragment extends Fragment {

    TextView btnNext;
    EditTextRegular edtAddressLine1;
    EditTextRegular edtAddressLine2;
    EditTextRegular edtSuburs;
    EditTextRegular edtState;
    EditTextRegular edtPostcode;
    EditTextRegular edtCountry;
    SessionManager sessionManager;

    public MapReqFragment() {
    }

    public static MapReqFragment newInstance() {
        return new MapReqFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(getContext());
        edtAddressLine1 = view.findViewById(R.id.edt_address_line_1);
        edtAddressLine2 = view.findViewById(R.id.edt_address_line_2);
        edtSuburs = view.findViewById(R.id.edt_suburs);
        edtState = view.findViewById(R.id.edt_state);
        edtPostcode = view.findViewById(R.id.edt_postcode);
        edtCountry = view.findViewById(R.id.edt_Country);
        btnNext = view.findViewById(R.id.txt_btn_next);
        btnNext.setOnClickListener(v -> {
            ((RequirementsBottomSheet) getParentFragment()).changeFragment(3);

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_req, container, false);
        return view;
    }

    @OnClick(R.id.lyt_btn_change_billing_address)
    public void onViewClicked() {

        if (validation()) {
            AddBillingAddress();
        }

    }

    private boolean validation() {
        if (TextUtils.isEmpty(edtAddressLine1.getText().toString().trim())) {
            edtAddressLine1.setError("Address is mandatory");
            return false;
        }

        if (TextUtils.isEmpty(edtAddressLine2.getText().toString().trim())) {
            edtAddressLine2.setError("Address is mandatory");
            return false;
        }


        if (TextUtils.isEmpty(edtSuburs.getText().toString().trim())) {
            edtSuburs.setError("Please enter Suburb");
            return false;
        }
        if (TextUtils.isEmpty(edtState.getText().toString().trim())) {
            edtState.setError("Please enter state");
            return false;
        }
        if (TextUtils.isEmpty(edtPostcode.getText().toString().trim())) {
            edtPostcode.setError("Please enter Passcode");
            return false;
        }
        if (edtPostcode.getText().toString().length() != 4) {
            edtPostcode.setError("Please enter 4 digit Passcode");
            return false;
        }
        if (TextUtils.isEmpty(edtCountry.getText().toString().trim())) {
            edtCountry.setError("Please Enter Country");
            return false;
        }
        return true;
    }

    private void AddBillingAddress() {
        ((ActivityBase) getActivity()). showProgressDialog();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, BASE_URL + ADD_BILLING,
                response -> {
                    Timber.e(response);
                    ((ActivityBase) getActivity()).hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
/*                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(ConstantKey.WRITE_REVIEW, true);
                                intent.putExtras(bundle);
                                setResult(ConstantKey.RESULTCODE_WRITE_REVIEW, intent);
                                onBackPressed();*/
                                Toast.makeText(getActivity(), "Updated successfully !", Toast.LENGTH_SHORT).show();


                                if (onBankaccountadded != null) {
                                    onBankaccountadded.billingAddressAdd();
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
                            //  showCustomDialog(jsonObject_error.getString("message"));
                            if (jsonObject_error.has("message")) {
                                Toast.makeText(getActivity(), jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Something went Wrong !", Toast.LENGTH_SHORT).show();
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

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("line1", edtAddressLine1.getText().toString());
                map1.put("line2", edtAddressLine2.getText().toString());
                map1.put("location", edtSuburs.getText().toString());
                map1.put("post_code", edtPostcode.getText().toString());
                map1.put("state", edtState.getText().toString());

                map1.put("country", edtCountry.getText().toString());
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public void getBillingAddress() {

        ((ActivityBase) getActivity()).showProgressDialog();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, BASE_URL + ADD_BILLING,
                response -> {
                    Timber.e(response);
                    ((ActivityBase) getActivity()).hideProgressDialog();
                    try {
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
                                            edtAddressLine1.setText(data.getData().getLine1());
                                            edtAddressLine2.setText(data.getData().getLine2());
                                            edtCountry.setText(data.getData().getCountry());
                                            edtState.setText(data.getData().getState());
                                            edtPostcode.setText(data.getData().getPost_code());
                                            edtSuburs.setText(data.getData().getLocation());
                                        }
                                    }
                                }
                            } else {
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
                                Toast.makeText(getActivity(), jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ((ActivityBase) getActivity()).showToast("Something Went Wrong", getActivity());
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }

}