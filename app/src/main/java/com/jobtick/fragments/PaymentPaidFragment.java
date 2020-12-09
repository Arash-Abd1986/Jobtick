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
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jobtick.R;
import com.jobtick.TextView.TextViewMedium;
import com.jobtick.activities.ActivityBase;
import com.jobtick.adapers.PaymentHistoryListAdapter;
import com.jobtick.models.payments.PaymentHistory;
import com.jobtick.utils.Constant;
import com.jobtick.utils.SessionManager;
import com.jobtick.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Collections;
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
    @BindView(R.id.no_transaction_container)
    LinearLayout noTransactions;
    @BindView(R.id.container)
    CardView container;

    @BindView(R.id.filter)
    TextView filter;
    @BindView(R.id.download)
    TextView download;
    @BindView(R.id.txt_filter)
    TextView txtFilter;


    private SessionManager sessionManager;

    private int cyear, cmonth, cday;
    private String from;
    private String to;
    private BottomSheetDialog mBottomSheetDialog;

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

        filter.setOnClickListener(v -> {
            showBottomSheetDialogDate(true);
        });

        download.setOnClickListener(v -> {

        });

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
            noTransactions.setVisibility(View.VISIBLE);
            container.setVisibility(View.GONE);
        } else {
            noTransactions.setVisibility(View.GONE);
            container.setVisibility(View.VISIBLE);
        }
        totalPayment.setText(StringUtils.getPriceTxt(total_amount));
        totalTransaction.setText(data.size() + " transactions");
        paymentHistoryList.setLayoutManager(new LinearLayoutManager(getContext()));
        Collections.reverse(data);
        paymentHistoryList.setAdapter(new PaymentHistoryListAdapter(data, true, paymentHistory -> {
            PaymentHistoryBottomSheet paymentPaidBottomSheet = PaymentHistoryBottomSheet.newInstance(paymentHistory);
            paymentPaidBottomSheet.show(getParentFragmentManager(), "");

        }));

    }

    private void showBottomSheetDialogDate(boolean from) {

        final View view = getLayoutInflater().inflate(R.layout.sheet_date, null);

        mBottomSheetDialog = new BottomSheetDialog(getContext());
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        TextView title = view.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);

        if(from)
            title.setText("From");
        else
            title.setText("To");


        CalendarView calendarView = view.findViewById(R.id.calenderView);
        TextViewMedium txtCancel = view.findViewById(R.id.txt_cancel);
        LinearLayout lytBtnDone = view.findViewById(R.id.lyt_btn_done);

        calendarView.setMinDate(System.currentTimeMillis());

        Calendar now = Calendar.getInstance();
        Calendar min = Calendar.getInstance();
        if(from)
            min.add(Calendar.MONTH, -12);
        else
            min.set(cyear, cmonth, cday);


        calendarView.setMaxDate(now.getTimeInMillis());
        calendarView.setMinDate(min.getTimeInMillis());

        Calendar calendar = Calendar.getInstance();
        cyear = calendar.get(Calendar.YEAR);
        cmonth = calendar.get(Calendar.MONTH);
        cday = calendar.get(Calendar.DAY_OF_MONTH);


        txtCancel.setOnClickListener(v -> {
            mBottomSheetDialog.dismiss();
            txtFilter.setText("All date");
            filter.setText("All");
            setFilterToList(false);
        });



        lytBtnDone.setOnClickListener(v -> {
            if(from) {
                this.from = cday + "/" + cmonth + "/" + cyear;
                mBottomSheetDialog.dismiss();
                showBottomSheetDialogDate(false);
            }
            else{
                this.to = cday + "/" + cmonth + "/" + cyear;
                txtFilter.setText(String.format("%s to %s", this.from, this.to));
                filter.setText("Custom range");
                mBottomSheetDialog.dismiss();
                setFilterToList(true);
            }
        });

        calendarView.setOnDateChangeListener((arg0, year, month, date) -> {

            cmonth = month;
            cyear = year;
            cday = date;
        });


        // set background transparent
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(dialog ->{
                if(!from)
                    mBottomSheetDialog = null;
        });
    }

    private void setFilterToList(boolean isFilterSet) {

        ((ActivityBase)requireActivity()).showSuccessToast("filter is " + isFilterSet, getContext());
    }
}