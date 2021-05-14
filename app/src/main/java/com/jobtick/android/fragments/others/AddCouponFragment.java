package com.jobtick.android.fragments.others;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jobtick.android.BuildConfig;
import com.jobtick.android.R;
import com.jobtick.android.fragments.AbstractStateExpandedBottomSheet;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;


public class AddCouponFragment extends AbstractStateExpandedBottomSheet {
    SessionManager sessionManager;
    static Integer amount;
    boolean isVerified = false;
    private SubmitListener listener;

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
    View view;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_coupon, container, false);
        sessionManager = new SessionManager(getActivity());
        setupPromotionCodeChecker(view);
        btnVerify.setOnClickListener(v -> {
            verify();
        });

        return view;
    }
    public interface SubmitListener{

        void onVerifySubmit(String coupon);
        void onClose();
    }
    private void verify() {
        if(isVerified)
            listener.onVerifySubmit(etPromoCode.getText().toString());
        else
            listener.onClose();
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
                if(etPromoCode.getText().length()==8){
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
                if(etPromoCode.getText().length()==8) {
                    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    ivState.setVisibility(View.GONE);
                    pbLoading.setVisibility(View.VISIBLE);
                    checkPromoCode();
                }
                else{
                    ivState.setVisibility(View.VISIBLE);
                    pbLoading.setVisibility(View.GONE);
                }
                
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
                map1.put("Version", String.valueOf(BuildConfig.VERSION_CODE));

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
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (SubmitListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(this.toString()
                    + " must implement NoticeListener");
        }
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
                map1.put("Version", String.valueOf(BuildConfig.VERSION_CODE));

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


}