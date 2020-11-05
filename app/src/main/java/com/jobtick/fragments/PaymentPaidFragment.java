package com.jobtick.fragments;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobtick.R;
import com.jobtick.activities.ActivityBase;
import com.jobtick.adapers.PaymentHistoryListAdapter;
import com.jobtick.interfaces.PaymentOnClick;
import com.jobtick.models.payments.PaymentHistory;
import com.jobtick.utils.Constant;
import com.jobtick.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentPaidFragment extends Fragment {

    @BindView(R.id.txt_total_payment)
    TextView totalPayment;
    @BindView(R.id.txt_total_transactions)
    TextView totalTransaction;
    @BindView(R.id.payment_history_list)
    RecyclerView paymentHistoryList;
    @BindView(R.id.lottieAnimationView)
    LottieAnimationView lottieAnimationView;
    @BindView(R.id.container)
    CardView container;

    private SessionManager sessionManager;

    private PaymentPaidFragment() {
        // Required empty public constructor
    }

    public static PaymentPaidFragment newInstance() {
        PaymentPaidFragment fragment = new PaymentPaidFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(getActivity());
        getPoster();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_payment_history_outgoing, container, false);
        ButterKnife.bind(this, view);
        return view;

    }

    public void getPoster() {
        ((ActivityBase) getActivity()).showProgressDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_GET_PAYMENT_HISTORY_POSTER,
                response -> {

                    ((ActivityBase) getActivity()).hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success") && jsonObject.has("data")) {
                            JSONArray jsonObject_data = jsonObject.getJSONArray("data");
                            Gson googleJson = new Gson();
                            Type listType = new TypeToken<List<PaymentHistory>>() {
                            }.getType();
                            List<PaymentHistory> jsonObjList = googleJson.fromJson(jsonObject_data.toString(), listType);
                            fillData(jsonObjList, jsonObject.getString("total_amount"));
                        }
                    } catch (JSONException e) {
                        Log.e("EXCEPTION", String.valueOf(e));
                        e.printStackTrace();
                    }
                },
                error -> {
                    ((ActivityBase) getActivity()).errorHandle1(error.networkResponse);
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
        Log.e("URL", stringRequest.getUrl());
    }

    private void fillData(List<PaymentHistory> data, String total_amount) {
        if (data.size() == 0) {
            lottieAnimationView.setVisibility(View.VISIBLE);
            container.setVisibility(View.GONE);
        } else {
            lottieAnimationView.setVisibility(View.GONE);
            container.setVisibility(View.VISIBLE);
        }
        totalPayment.setText(total_amount);
        totalTransaction.setText(data.size() + " transactions");
        paymentHistoryList.setLayoutManager(new LinearLayoutManager(getContext()));
        paymentHistoryList.setAdapter(new PaymentHistoryListAdapter(data, true, paymentHistory -> {
            System.out.println(paymentHistory.getAmount());
            PaymentPaidBottomSheet paymentPaidBottomSheet = new PaymentPaidBottomSheet(paymentHistory);
            paymentPaidBottomSheet.show(getParentFragmentManager(), "");

        }));

    }
}