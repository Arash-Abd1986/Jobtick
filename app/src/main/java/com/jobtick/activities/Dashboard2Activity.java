package com.jobtick.activities;


import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.gson.Gson;
import com.jobtick.R;
import com.jobtick.adapers.NotificationListAdapter;
import com.jobtick.models.UserAccountModel;
import com.jobtick.models.notification.NotifDatum;
import com.jobtick.models.notification.PushNotificationModel2;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ImageUtil;
import com.jobtick.utils.SessionManager;
import com.jobtick.widget.ExtendedJobInfo;
import com.ramijemli.percentagechartview.PercentageChartView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class Dashboard2Activity extends ActivityBase implements NotificationListAdapter.OnItemClickListener {



    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    @BindView(R.id.txt_user_name)
    TextView txtUserName;

    @BindView(R.id.txt_account_level)
    TextView txtAccountLevel;

    @BindView(R.id.awaiting_for_offer)
    ExtendedJobInfo txtAwaitingOffer;

    @BindView(R.id.assigned)
    ExtendedJobInfo txtAssigend;

    @BindView(R.id.overdue)
    ExtendedJobInfo txtOverDue;

    @BindView(R.id.released_money)
    ExtendedJobInfo txtReleasedMoney;

    @BindView(R.id.complete)
    ExtendedJobInfo txtCompleted;

    @BindView(R.id.cancelled)
    ExtendedJobInfo extCancelled;

    @BindView(R.id.rb_as_ticker)
    MaterialRadioButton rbAsATicker;

    @BindView(R.id.rb_as_poster)
    MaterialRadioButton rbAsAPoster;

    @BindView(R.id.rg_ticker_poster)
    RadioGroup rgTickerPoster;

    @BindView(R.id.rbPortfollio)
    MaterialRadioButton rbProfile;

    @BindView(R.id.rbSkills)
    MaterialRadioButton rbSkills;

    @BindView(R.id.rgTabs)
    RadioGroup rgTabs;

    @BindView(R.id.iv_green_account)
    ImageView iv_green_account;

    @BindView(R.id.iv_payment)
    ImageView iv_payment;

    @BindView(R.id.iv_skills)
    ImageView iv_skills;

    @BindView(R.id.iv_badges)
    ImageView iv_badges;

    @BindView(R.id.img_user_avatar)
    ImageView imgUserAvatar;

    @BindView(R.id.profile_progress)
    PercentageChartView percentageChartView;

    @BindView(R.id.profile_container)
    RelativeLayout profileContainer;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    NotificationListAdapter notificationListAdapter;
    private PushNotificationModel2 pushNotificationModel2;
    private UserAccountModel userAccountModel;
    private SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard2);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(Dashboard2Activity.this);
        userAccountModel = sessionManager.getUserAccount();
        initToolbar();
        initComponent();
        setData();
        onChangeTabUser();
        initNotificationList();
    }

    private void initToolbar() {
        toolbar.setTitle("Dashboard");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setData() {


        txtUserName.setText(sessionManager.getUserAccount().getName());
        txtAccountLevel.setText(sessionManager.getUserAccount().getWorkerTier().getName());
        ImageUtil.displayImage(imgUserAvatar, sessionManager.getUserAccount().getAvatar().getThumbUrl(), null);
    }

    public void initComponent() {

        rgTickerPoster.setOnCheckedChangeListener((group, checkedId) -> {
            onChangeTabUser();
        });

        rgTabs.setOnCheckedChangeListener((group, checkedId) -> {
            onChangeTabProfile();
        });
    }

    private void onChangeTabProfile(){
        if(rbProfile.isChecked()){
            profileContainer.setVisibility(View.VISIBLE);
        }
        else{
            profileContainer.setVisibility(View.GONE);
        }
    }

    private void onChangeTabUser() {
        if (rbAsAPoster.isChecked()) {
            txtAwaitingOffer.setTitle("Awaiting \nfor offer");
            txtReleasedMoney.setTitle("Released\nMoney");
            if (userAccountModel.getPostTaskStatistics() != null && userAccountModel.getPostTaskStatistics().getAssigned() != null) {
                txtAssigend.setValue(userAccountModel.getPostTaskStatistics().getAssigned().toString());
            } else {
                txtAssigend.setValue("0");
            }
            if (userAccountModel.getPostTaskStatistics() != null && userAccountModel.getPostTaskStatistics().getCancelled() != null) {
                txtAssigend.setValue(userAccountModel.getPostTaskStatistics().getCancelled().toString());
            } else {
                extCancelled.setValue("0");
            }

            if (userAccountModel.getPosterTier() != null && userAccountModel.getPosterTier().getServiceFee() != null) {
                txtReleasedMoney.setValue(userAccountModel.getPosterTier().getServiceFee().toString());
            } else {
                txtReleasedMoney.setValue("0");
            }

            if (userAccountModel.getPostTaskStatistics() != null && userAccountModel.getPostTaskStatistics().getOverdue() != null) {
                txtOverDue.setValue(userAccountModel.getPostTaskStatistics().getOverdue().toString());
            } else {
                txtOverDue.setValue("0");
            }
            if (userAccountModel.getPostTaskStatistics() != null && userAccountModel.getPostTaskStatistics().getCompleted() != null) {
                txtCompleted.setValue(userAccountModel.getPostTaskStatistics().getCompleted().toString());
            } else {
                txtCompleted.setValue("0");
            }
            if (userAccountModel.getPostTaskStatistics() != null && userAccountModel.getPostTaskStatistics().getOpenForBids() != null) {
                txtAwaitingOffer.setValue(userAccountModel.getPostTaskStatistics().getOpenForBids().toString());
            } else {
                txtAwaitingOffer.setValue("0");
            }
            if (userAccountModel.getPostTaskStatistics() != null && userAccountModel.getPostTaskStatistics().getCompletionRate() != null) {
                percentageChartView.setProgress(Float.parseFloat(userAccountModel.getPostTaskStatistics().getCompletionRate().toString()), false);
            } else {
                percentageChartView.setProgress(0F, false);
            }
        } else {
            txtAwaitingOffer.setTitle("Offered");
            txtReleasedMoney.setTitle("Asked to\nrelease");
            if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getAssigned() != null) {
                txtAssigend.setValue(userAccountModel.getWorkTaskStatistics().getAssigned().toString());
            } else {
                txtAssigend.setValue("0");
            }
            if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getCancelled() != null) {
                extCancelled.setValue(userAccountModel.getWorkTaskStatistics().getCancelled().toString());
            } else {
                extCancelled.setValue("0");
            }
            if (userAccountModel.getWorkerTier() != null && userAccountModel.getWorkerTier().getServiceFee() != null) {
                txtReleasedMoney.setValue(userAccountModel.getWorkerTier().getServiceFee().toString());
            } else {
                txtReleasedMoney.setValue("0");
            }

            if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getOverdue() != null) {
                txtOverDue.setValue(userAccountModel.getWorkTaskStatistics().getOverdue().toString());
            } else {
                txtOverDue.setValue("0");
            }
            if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getCompleted() != null) {
                txtCompleted.setValue(userAccountModel.getWorkTaskStatistics().getCompleted().toString());
            } else {
                txtCompleted.setValue("0");
            }
            if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getTotalPosted() != null) {
                txtAwaitingOffer.setValue(userAccountModel.getWorkTaskStatistics().getTotalPosted().toString());
            } else {
                txtAwaitingOffer.setValue("0");
            }
            if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getCompletionRate() != null) {
                percentageChartView.setProgress(Float.parseFloat(userAccountModel.getWorkTaskStatistics().getCompletionRate().toString()), false);
            } else {
                percentageChartView.setProgress(0F, false);
            }
        }


        if (userAccountModel.getAccount_status() != null) {
            if (userAccountModel.getAccount_status().isPortfolio()) {
                iv_green_account.setImageResource(R.drawable.ic_progress_checked);

            } else {
                iv_green_account.setColorFilter(ContextCompat.getColor(Dashboard2Activity.this, R.color.grayActive), android.graphics.PorterDuff.Mode.SRC_IN);
            }

            if (rbAsAPoster.isChecked()) {
                if (userAccountModel.getAccount_status().isCredit_card()) {
                    iv_payment.setImageResource(R.drawable.ic_progress_checked);
                } else {
                    iv_payment.setColorFilter(ContextCompat.getColor(Dashboard2Activity.this, R.color.grayActive), android.graphics.PorterDuff.Mode.SRC_IN);
                }
            } else {
                if (userAccountModel.getAccount_status().isBank_account()) {
                    iv_payment.setImageResource(R.drawable.ic_progress_checked);

                } else {
                    iv_payment.setColorFilter(ContextCompat.getColor(Dashboard2Activity.this, R.color.grayActive), android.graphics.PorterDuff.Mode.SRC_IN);
                }


            }

            if (userAccountModel.getAccount_status().isSkills()) {
                iv_skills.setImageResource(R.drawable.ic_progress_checked);
            } else {
                iv_skills.setColorFilter(ContextCompat.getColor(Dashboard2Activity.this, R.color.grayActive), android.graphics.PorterDuff.Mode.SRC_IN);
            }
            if (userAccountModel.getAccount_status().isBadges()) {
                iv_badges.setImageResource(R.drawable.ic_progress_checked);

            } else {
                iv_badges.setColorFilter(ContextCompat.getColor(Dashboard2Activity.this, R.color.grayActive), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        }
    }

    @Override
    public void onItemClick(View view, NotifDatum obj, int position, String action) {

    }

    //we just get last 10 notifications
    private void getNotificationList() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_NOTIFICATION_LIST + "?page=1",
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                            String jsonString = jsonObject.toString(); //http request
                            Gson gson = new Gson();
                            pushNotificationModel2 = gson.fromJson(jsonString, PushNotificationModel2.class);
                        } else {
                            showToast("something went wrong.", this);
                            checkList();
                            return;
                        }

                        notificationListAdapter.addItems(pushNotificationModel2.getData());
                        checkList();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        checkList();
                    }
                },
                error -> {
                    checkList();
                    errorHandle1(error.networkResponse);
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("Authorization", "Bearer " + sessionManager.getAccessToken());
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(Dashboard2Activity.this);
        requestQueue.add(stringRequest);
        Log.e("url", stringRequest.getUrl());

    }

    private void checkList() {
    }

    private void initNotificationList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(Dashboard2Activity.this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        notificationListAdapter = new NotificationListAdapter(new ArrayList<>());
        notificationListAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(notificationListAdapter);

        getNotificationList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}