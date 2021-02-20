package com.jobtick.android.fragments.others;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jobtick.android.R;
import com.jobtick.android.activities.PaymentOverviewActivity;
import com.jobtick.android.fragments.AbstractStateExpandedBottomSheet;
import com.jobtick.android.fragments.CashOutBottomSheet;
import com.jobtick.android.models.CreditCardModel;
import com.jobtick.android.models.calculation.PayingCalculationModel;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class AddCouponFragment extends AbstractStateExpandedBottomSheet {
    SessionManager sessionManager;
    static Integer amount;
    boolean isVerified = false;
    public static AddCouponFragment newInstance(Integer netPaying) {
        amount = netPaying;
        Bundle args = new Bundle();
        AddCouponFragment fragment = new AddCouponFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_BottomSheetDialog);
    }

    Button btnVerify;
    TextView tvError;
    EditText etPromoCode;
    ImageView ivState;
    ProgressBar pbLoading;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_coupon, container, false);
        sessionManager = new SessionManager(getActivity());
        setupPromotionCodeChecker(view);
        btnVerify.setOnClickListener(v -> {
            verify();
        });

        return view;
    }

    private void setupPromotionCodeChecker(View view) {
        btnVerify = view.findViewById(R.id.btnVerify);
        tvError = view.findViewById(R.id.tvError);
        etPromoCode = view.findViewById(R.id.etPromoCode);
        ivState = view.findViewById(R.id.ivState);
        pbLoading = view.findViewById(R.id.pbLoading);

        etPromoCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(etPromoCode.getText().length()>2){
                    ivState.setVisibility(View.GONE);
                    pbLoading.setVisibility(View.VISIBLE);
                }else{
                    ivState.setVisibility(View.VISIBLE);
                    pbLoading.setVisibility(View.GONE);
                }
            }
            private Timer timer =new Timer();

            @Override
            public void afterTextChanged(Editable s) {
                timer.cancel();
                timer =new Timer();
                long delay = 3000L;
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(etPromoCode.getText().length()==8){
                            ivState.setVisibility(View.GONE);
                            pbLoading.setVisibility(View.VISIBLE);
                            checkPromoCode();
                        }
                        else{
                            ivState.setVisibility(View.VISIBLE);
                            pbLoading.setVisibility(View.GONE);
                        }
                    }
                }, delay);
            }
        });

    }

    private void checkPromoCode() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_OFFERS_PAYING_CALCULATION,
                response -> {
                    pbLoading.setVisibility(View.GONE);

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String data = jsonObject.getString("data");

                        isVerified = true;
                        ivState.setVisibility(View.VISIBLE);
                        ivState.setImageResource(R.drawable.ic_verified_coupon);
                        pbLoading.setVisibility(View.GONE);
                        tvError.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        isVerified = false;
                        tvError.setVisibility(View.GONE);
                        ivState.setImageResource(R.drawable.ic_unverified_coupon);
                        ivState.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    pbLoading.setVisibility(View.GONE);
                    isVerified = false;
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                            String message = jsonObject_error.getString("message");
                            tvError.setText(message);
                            tvError.setVisibility(View.VISIBLE);
                            ivState.setImageResource(R.drawable.ic_unverified_coupon);
                            ivState.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            tvError.setVisibility(View.GONE);
                            ivState.setImageResource(R.drawable.ic_unverified_coupon);
                            ivState.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tvError.setVisibility(View.GONE);
                        ivState.setImageResource(R.drawable.ic_unverified_coupon);
                        ivState.setVisibility(View.VISIBLE);
                    }
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
            protected Map<String, String> getParams() {
                Map<String, String> map1 = new HashMap<String, String>();
                if(amount!=null)
                map1.put("amount", amount.toString());
                map1.put("discount_code", etPromoCode.getText().toString());
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    @Deprecated
    private void checkPromoCodeOld() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_CHECK_COUPON,
                response -> {
            pbLoading.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String data = jsonObject.getString("data");

                        ivState.setVisibility(View.VISIBLE);
                        ivState.setImageResource(R.drawable.ic_verified_coupon);
                        pbLoading.setVisibility(View.GONE);
                        tvError.setVisibility(View.GONE);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        tvError.setVisibility(View.GONE);
                        ivState.setImageResource(R.drawable.ic_unverified_coupon);
                        ivState.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    pbLoading.setVisibility(View.GONE);
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                            String message = jsonObject_error.getString("message");
                            tvError.setText(message);
                            tvError.setVisibility(View.VISIBLE);
                            ivState.setImageResource(R.drawable.ic_unverified_coupon);
                            ivState.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            tvError.setVisibility(View.GONE);
                            ivState.setVisibility(View.VISIBLE);
                            ivState.setImageResource(R.drawable.ic_unverified_coupon);
                        }
                    }
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
            protected Map<String, String> getParams() {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("amount", amount.toString());
                map1.put("coupon", etPromoCode.getText().toString());
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void verify() {

    }
}