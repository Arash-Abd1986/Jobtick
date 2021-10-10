package com.jobtick.android.fragments;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.jobtick.android.BuildConfig;
import com.jobtick.android.R;

import android.annotation.SuppressLint;
import android.widget.Toast;

import com.jobtick.android.activities.ActivityBase;
import com.jobtick.android.adapers.PaymentHistoryListAdapter;
import com.jobtick.android.models.payments.PaymentHistory;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.SessionManager;
import com.jobtick.android.utils.StringUtils;
import com.jobtick.android.widget.EndlessRecyclerViewOnScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class AbstractPaymentFragment extends Fragment {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_total_payment)
    TextView totalPayment;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_total_transactions)
    TextView totalTransaction;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.payment_history_list)
    RecyclerView paymentHistoryList;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.no_transaction_container)
    LinearLayout noTransactions;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.container)
    CardView container;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.pbLoading)
    ProgressBar pbLoading;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.filter)
    TextView filter;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.download)
    TextView download;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_filter)
    TextView txtFilter;


    private SessionManager sessionManager;

    private int cyear, cmonth, cday;
    private String from;
    private String fromApiFormat;
    private String to;
    private String toApiFormat;
    private BottomSheetDialog mBottomSheetDialog;

    public AbstractPaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(requireActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_payment_history, container, false);
        ButterKnife.bind(this, view);
        getPaymentHistory(null, null);

        filter.setOnClickListener(v -> {
            showBottomSheetDialogDate(true);
        });

        download.setOnClickListener(v -> {

        });

        return view;
    }

    public void getPaymentHistory(String from, String to) {
        String type;
        if(isPoster())
           type = "poster_payment_filter=true";
        else
            type = "worker_payment_filter=true";

        String url;

        boolean firstInit = from == null && to == null;

        if(!firstInit){
            url = Constant.URL_GET_PAYMENT_HISTORY_FILTER + "?" +
                    type + "&date_from=" + from + "&date_to=" + to;
        }else
            url = Constant.URL_GET_PAYMENT_HISTORY_FILTER + "?" + type ;

        pbLoading.setVisibility(View.VISIBLE);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+"&limit=50",

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success") && jsonObject.has("data")) {
                            JSONArray jsonObject_data = jsonObject.getJSONArray("data");
                            Gson googleJson = new Gson();
                            Type listType = new TypeToken<List<PaymentHistory>>() {
                            }.getType();
                            List<PaymentHistory> jsonObjList = googleJson.fromJson(jsonObject_data.toString(), listType);
                            fillData(jsonObjList, jsonObject.getString("total_amount"), firstInit);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    ((ActivityBase) requireActivity()).hideProgressDialog();
                    pbLoading.setVisibility(View.GONE);
                },
                error -> {
                    ((ActivityBase) requireActivity()).errorHandle1(error.networkResponse);
//                    ((ActivityBase) requireActivity()).hideProgressDialog();
                    pbLoading.setVisibility(View.GONE);
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
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
    private EndlessRecyclerViewOnScrollListener onScrollListener;
    private final int currentPage = 1;
    PaymentHistoryBottomSheet paymentPaidBottomSheet;
    private void fillData(List<PaymentHistory> data, String total_amount, boolean firstInit) {
        if (data.size() == 0 && firstInit) {
            noTransactions.setVisibility(View.VISIBLE);
            container.setVisibility(View.GONE);
        } else if (firstInit){
            noTransactions.setVisibility(View.GONE);
            container.setVisibility(View.VISIBLE);
        }
        totalPayment.setText(StringUtils.getPriceTxt(total_amount));
        totalTransaction.setText(String.format(Locale.ENGLISH, "%d transactions", data.size()));
        paymentHistoryList.setLayoutManager(new LinearLayoutManager(getContext()));
        Collections.reverse(data);
        onScrollListener = new EndlessRecyclerViewOnScrollListener() {
            @Override
            public void onLoadMore(int currentPage) {
            }

            @Override
            public int getTotalItem() {
                return 0;
            }
        };
        paymentHistoryList.addOnScrollListener(onScrollListener);
        paymentHistoryList.setAdapter(new PaymentHistoryListAdapter(data, isPoster(), paymentHistory -> {
            paymentPaidBottomSheet = PaymentHistoryBottomSheet.newInstance(paymentHistory);
            paymentPaidBottomSheet.show(getParentFragmentManager(), "");

        }));

    }

    @Override
    public void onPause() {
        super.onPause();
        if(paymentPaidBottomSheet!=null)
            paymentPaidBottomSheet.dismiss();
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
        TextView txtCancel = view.findViewById(R.id.txt_cancel);
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
                this.from = String.format(Locale.ENGLISH, "%d/%d/%d", cday , cmonth + 1, cyear);
                this.fromApiFormat = String.format(Locale.ENGLISH, "%d-%d-%d", cyear , cmonth + 1, cday);
                mBottomSheetDialog.dismiss();
                showBottomSheetDialogDate(false);
            }
            else{
                this.to = String.format(Locale.ENGLISH, "%d/%d/%d", cday , cmonth + 1, cyear);
                this.toApiFormat = String.format(Locale.ENGLISH, "%d-%d-%d", cyear , cmonth + 1, cday);
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

        if(isFilterSet)
            getPaymentHistory(fromApiFormat, toApiFormat);
        else
            getPaymentHistory(null, null);
    }

    abstract boolean isPoster();
}