package com.jobtick.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.R;
import com.jobtick.adapers.NotificationListAdapter;
import com.jobtick.models.NotificationModel;
import com.jobtick.models.PushNotificationModel;
import com.jobtick.pagination.PaginationListener;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jobtick.pagination.PaginationListener.PAGE_START;
import static com.jobtick.utils.ConstantKey.ID;
import static com.jobtick.utils.ConstantKey.PUSH_COMMENT;
import static com.jobtick.utils.ConstantKey.PUSH_CONVERSATION;
import static com.jobtick.utils.ConstantKey.PUSH_OFFER;
import static com.jobtick.utils.ConstantKey.PUSH_QUESTION;
import static com.jobtick.utils.ConstantKey.PUSH_STATUS;
import static com.jobtick.utils.ConstantKey.PUSH_TASK;
import static com.jobtick.utils.ConstantKey.PUSH_TASK_ID;
import static com.jobtick.utils.ConstantKey.PUSH_TASK_SLUG;
import static com.jobtick.utils.ConstantKey.PUSH_TITLE;
import static com.jobtick.utils.ConstantKey.PUSH_TRIGGER;

public class NotificationActivity extends ActivityBase implements NotificationListAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;


    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;

    NotificationListAdapter notificationListAdapter;
    ArrayList<PushNotificationModel> notificationModelArrayList;
    private LottieAnimationView lottieAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
        lottieAnim=findViewById(R.id.lottieAnimationView);
        initToolbar();
        initComponent();
        getNotificationList();
    }

    private void initComponent() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(NotificationActivity.this);


        notificationModelArrayList = new ArrayList<>();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        notificationListAdapter = new NotificationListAdapter(this, notificationModelArrayList);
        notificationListAdapter.setOnItemClickListener(this);
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
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitle(getResources().getString(R.string.notifications));
        toolbar.inflateMenu(R.menu.menu_notification);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.nav_setting:
                startActivity(new Intent(this, NotificationActivity.class));
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
        ArrayList<NotificationModel> items = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_NOTIFICATION_LIST + "?page=" + currentPage,
                response -> {
                    swipeRefresh.setRefreshing(false);
                    // categoryArrayList.clear();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Log.e("json", jsonObject.toString());
                        if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                            JSONArray jsonArray_data = jsonObject.getJSONArray("data");
                            for (int i = 0; jsonArray_data.length() > i; i++) {
                                JSONObject jsonObject_taskModel_list = jsonArray_data.getJSONObject(i);
                                PushNotificationModel notificationModel = new PushNotificationModel();

                                JSONObject data = jsonObject_taskModel_list.getJSONObject("data");
                                if (data != null) {
                                    try {
                                        notificationModel.setTrigger(data.getString(PUSH_TRIGGER));

                                        if (data.has(PUSH_TASK_SLUG) && !data.isNull(PUSH_TASK_SLUG)) {
                                            notificationModel.setModel_slug(data.getString(PUSH_TASK_SLUG));
                                        }

                                        if (data.has(PUSH_TASK_ID) && !data.isNull(PUSH_TASK_ID)) {
                                            notificationModel.setModel_id(data.getInt(PUSH_TASK_ID));
                                        }

                                        if (data.has(PUSH_OFFER) && !data.isNull(PUSH_OFFER)) {
                                            JSONObject jsonObjectOffer = data.getJSONObject(PUSH_OFFER);
                                            if (jsonObjectOffer.has(ID) && !jsonObjectOffer.isNull(ID)) {
                                                notificationModel.setOffer_id(jsonObjectOffer.getInt(ID));
                                            }
                                        }
                                        if (data.has(PUSH_QUESTION) && !data.isNull(PUSH_QUESTION)) {
                                            JSONObject jsonObjectQuestion = data.getJSONObject(PUSH_QUESTION);
                                            if (jsonObjectQuestion.has(ID) && !jsonObjectQuestion.isNull(ID)) {

                                                notificationModel.setQuestion_id(jsonObjectQuestion.getInt(ID));
                                            }
                                        }
                                        if (data.has(PUSH_CONVERSATION) && !data.isNull(PUSH_CONVERSATION)) {
                                            JSONObject jsonObjectConversation = data.getJSONObject(PUSH_CONVERSATION);
                                            if (jsonObjectConversation.has(ID) && !jsonObjectConversation.isNull(ID)) {
                                                notificationModel.setConversation_id(jsonObjectConversation.getInt(ID));
                                            }
                                        }
                                        if (data.has(PUSH_TITLE) && !data.isNull(PUSH_TITLE)) {
                                            notificationModel.setTitle(data.getString(PUSH_TITLE));
                                        }
                                        if (data.has(PUSH_STATUS) && !data.isNull(PUSH_STATUS)) {
                                            notificationModel.setStatus(data.getString(PUSH_STATUS));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    notificationModelArrayList.add(notificationModel);
                                }

                            }
                        } else {
                            showToast("some went to wrong", NotificationActivity.this);
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

                        if (items.size() <= 0) {
                            lottieAnim.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            lottieAnim.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                        }
                        notificationListAdapter.addItems(notificationModelArrayList);

                        swipeRefresh.setRefreshing(false);
                        // check weather is last page or not
                        if (currentPage < totalPage) {
                            notificationListAdapter.addLoading();
                        } else {
                            isLastPage = true;
                        }
                        isLoading = false;
                    } catch (JSONException e) {
                        hideProgressDialog();
                        Log.e("EXCEPTION", String.valueOf(e));
                        e.printStackTrace();
                    }
                },
                error -> {
                    //  swipeRefresh.setRefreshing(false);

                    swipeRefresh.setRefreshing(false);
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
        RequestQueue requestQueue = Volley.newRequestQueue(NotificationActivity.this);
        requestQueue.add(stringRequest);
        Log.e("url", stringRequest.getUrl());

    }


    @Override
    public void onRefresh() {
        swipeRefresh.setRefreshing(true);
        currentPage = PAGE_START;
        isLastPage = false;
        notificationListAdapter.clear();
        getNotificationList();
    }

    @Override
    public void onItemClick(View view, PushNotificationModel obj, int position, String action) {
        if (obj.getTrigger() != null) {
            Intent intent = new Intent(NotificationActivity.this, TaskDetailsActivity.class);
            Bundle bundleintent = new Bundle();
            if (obj.getTrigger().equals(PUSH_TASK)) {
                bundleintent.putString(ConstantKey.SLUG, obj.getModel_slug());
                intent.putExtras(bundleintent);
                startActivity(intent);
            }
            if (obj.getTrigger().equals(PUSH_COMMENT)) {

                if (obj.getOffer_id() != 0) {
                    bundleintent.putString(ConstantKey.SLUG, obj.getModel_slug());
                    bundleintent.putInt(ConstantKey.PUSH_OFFER_ID, obj.getOffer_id());
                    intent.putExtras(bundleintent);
                    startActivity(intent);
                }
                if (obj.getQuestion_id() != 0) {
                    bundleintent.putString(ConstantKey.SLUG, obj.getModel_slug());
                    bundleintent.putInt(ConstantKey.PUSH_QUESTION_ID, obj.getQuestion_id());
                    intent.putExtras(bundleintent);
                    startActivity(intent);
                }
            }
            if (obj.getTrigger().equals(PUSH_CONVERSATION)) {

               /* Bundle bundle1 = new Bundle();
                bundle1.putInt(PUSH_CONVERSATION_ID, obj.getConversation_id());
                NavGraph graph = navController.getGraph();
                graph.setStartDestination(R.id.navigation_inbox);
                navController.setGraph(graph, bundle1);
*/
            }

        }
    }
}
