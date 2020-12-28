package com.jobtick.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.adapers.NotificationListAdapter;
import com.jobtick.models.ConversationModel;
import com.jobtick.models.UserAccountModel;
import com.jobtick.models.notification.NotifDatum;
import com.jobtick.models.notification.PushNotificationModel2;
import com.jobtick.pagination.PaginationListener;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.jobtick.pagination.PaginationListener.PAGE_START;

public class NotificationActivity extends ActivityBase implements NotificationListAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;

    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;

    NotificationListAdapter notificationListAdapter;
    private PushNotificationModel2 pushNotificationModel2;
    private LinearLayout noNotifications;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(this);
        noNotifications = findViewById(R.id.no_notifications_container);
        initToolbar();
        initComponent();
        getNotificationList();
    }

    private void initComponent() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(NotificationActivity.this);


        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        notificationListAdapter = new NotificationListAdapter(new ArrayList<>());
        notificationListAdapter.setOnItemClickListener(this);
        swipeRefresh.setOnRefreshListener(this);
        recyclerView.setAdapter(notificationListAdapter);
        recyclerView.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                getNotificationList();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }


    private void initToolbar() {
        toolbar.setTitle("Notifications");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notification, menu);
        return true;
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.nav_setting:
                startActivity(new Intent(this, NotificationSettings.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void getNotificationList() {

        swipeRefresh.setRefreshing(true);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_NOTIFICATION_LIST + "?page=" + currentPage,
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
                            makeNotificationsAsRead();
                        } else {
                            showToast("something went wrong.", this);
                            checkList();
                            return;
                        }

                        if (jsonObject.has("meta") && !jsonObject.isNull("meta")) {
                            JSONObject jsonObject_meta = jsonObject.getJSONObject("meta");
                            totalPage = jsonObject_meta.getInt("last_page");
                            Constant.PAGE_SIZE = jsonObject_meta.getInt("per_page");
                        }

                        /*
                         *manage progress view
                         */
                        if (currentPage != PAGE_START)
                            notificationListAdapter.removeLoading();

                        notificationListAdapter.addItems(pushNotificationModel2.getData());
                        checkList();

                        // check weather is last page or not
                        if (currentPage < totalPage) {
                            notificationListAdapter.addLoading();
                        } else {
                            isLastPage = true;
                        }
                        isLoading = false;
                    } catch (JSONException e) {
                        hideProgressDialog();
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                        checkList();
                    }
                },
                error -> {
                    checkList();
                    errorHandle1(error.networkResponse);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<>();
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("Authorization", "Bearer " + sessionManager.getAccessToken());
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(NotificationActivity.this);
        requestQueue.add(stringRequest);
        Timber.e(stringRequest.getUrl());

    }


    private void makeNotificationsAsRead() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_NOTIFICATION_MARK_ALL_READ,
                response -> {
                    Timber.i("make all notifications as read success.");
                },
                error -> {
                    Timber.i("make all notifications as read NOT success.");
                })
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<>();
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("Authorization", "Bearer " + sessionManager.getAccessToken());
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        Timber.e(stringRequest.getUrl());
    }



    @Override
    public void onRefresh() {
        swipeRefresh.setRefreshing(true);
        currentPage = PAGE_START;
        isLastPage = false;
        notificationListAdapter.clear();
        getNotificationList();
    }

    private void checkList() {
        if (notificationListAdapter.getItemCount() <= 0) {
            noNotifications.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noNotifications.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void onItemClick(View view, NotifDatum obj, int position, String action) {


        if (obj.getData() != null && obj.getData().getTrigger() != null) {
            if(obj.getData().getTrigger().equals("task")) {
                Intent intent = new Intent(NotificationActivity.this, TaskDetailsActivity.class);
                Bundle bundleIntent = new Bundle();
                bundleIntent.putString(ConstantKey.SLUG, obj.getData().getTaskSlug());
                //TODO: need to put poster id to this, but is has to be implemented at taskDetailsActivity not from outside
                //bundleIntent.putInt(ConstantKey.USER_ID, obj.getUser().getId());
                intent.putExtras(bundleIntent);
                startActivity(intent);
            }else if(obj.getData().getTrigger().equals("conversation")) {

                ConversationModel model = new ConversationModel(obj.getData().getConversation().getId(),
                        obj.getUserAccountModel().getName(), obj.getData().getTaskId(), null, null, obj.getCreatedAt(),
                        sessionManager.getUserAccount(),
                        obj.getUserAccountModel(),
                        obj.getData().getTaskSlug(), obj.getData().getTaskStatus());

                Intent intent = new Intent(this, ChatActivity.class);
                Bundle bundle = new Bundle();
                ChatActivity.conversationModel = model;
            //    bundle.putParcelable(ConstantKey.CONVERSATION, model);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }


    }
}
