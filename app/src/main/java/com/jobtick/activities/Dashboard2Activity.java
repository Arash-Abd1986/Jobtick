package com.jobtick.activities;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.jobtick.R;
import com.jobtick.adapers.NotificationListAdapter;
import com.jobtick.adapers.SectionsPagerAdapter;
import com.jobtick.fragments.Dashboard2PosterFragment;
import com.jobtick.fragments.Dashboard2TickerFragment;
import com.jobtick.models.notification.NotifDatum;
import com.jobtick.models.notification.PushNotificationModel2;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.widget.ContentWrappingViewPager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.jobtick.utils.ConstantKey.PUSH_COMMENT;
import static com.jobtick.utils.ConstantKey.PUSH_TASK;

public class Dashboard2Activity extends ActivityBase implements NotificationListAdapter.OnItemClickListener, ViewPager.OnPageChangeListener {


    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    @BindView(R.id.rb_as_ticker)
    RadioButton rbAsATicker;

    @BindView(R.id.rb_as_poster)
    RadioButton rbAsAPoster;

    @BindView(R.id.rg_ticker_poster)
    RadioGroup rgTickerPoster;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.no_notifications_container)
    LinearLayout noNotifications;

    @BindView(R.id.ticker_poster_view_pager)
    ContentWrappingViewPager viewPager;

    NotificationListAdapter notificationListAdapter;
    private PushNotificationModel2 pushNotificationModel2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard2);
        ButterKnife.bind(this);
        initToolbar();
        initComponent();
        initNotificationList();
    }

    private void initToolbar() {
        toolbar.setTitle("Dashboard");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        if (notificationListAdapter.getItemCount() <= 0) {
            noNotifications.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noNotifications.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
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


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                rbAsATicker.setChecked(true);
                rbAsAPoster.setChecked(false);
                break;
            case 1:
                rbAsATicker.setChecked(false);
                rbAsAPoster.setChecked(true);
                break;
            case 2:

                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void initComponent() {
        setupViewPager(viewPager);
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(2);
        clickEvent();
    }

    private void clickEvent() {
        rgTickerPoster.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_as_ticker:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.rb_as_poster:
                    viewPager.setCurrentItem(1);
                    break;
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(Dashboard2TickerFragment.newInstance(), "User as Ticker");
        adapter.addFragment(Dashboard2PosterFragment.newInstance(), "User as Poster");
        viewPager.setAdapter(adapter);
    }


    @Override
    public void onItemClick(View view, NotifDatum obj, int position, String action) {
        if (obj.getData() != null && obj.getData().getTrigger() != null) {
            Intent intent = new Intent(this, TaskDetailsActivity.class);
            Bundle bundleIntent = new Bundle();
            if (obj.getData().getTrigger().equals(PUSH_TASK)) {
                bundleIntent.putString(ConstantKey.SLUG, obj.getData().getTaskSlug());
                intent.putExtras(bundleIntent);
                startActivity(intent);
            }
        }
    }
}