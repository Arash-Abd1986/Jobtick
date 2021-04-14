package com.jobtick.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.android.BuildConfig;
import com.jobtick.android.R;
import android.annotation.SuppressLint;

import com.jobtick.android.adapers.TaskListAdapter;
import com.jobtick.android.interfaces.OnRemoveSavedTaskListener;
import com.jobtick.android.models.TaskModel;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.ConstantKey;
import com.jobtick.android.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.jobtick.android.pagination.PaginationListener.PAGE_START;

public class SavedTaskActivity extends ActivityBase implements TaskListAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, OnRemoveSavedTaskListener {


    private TaskListAdapter taskListAdapter;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private int totalItem = 10;
    private boolean isLoading = false;

    private SessionManager sessionManager;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recyclerview_savedTask)
    RecyclerView recyclerViewStatus;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;

    private LinearLayout noPosts;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    public static OnRemoveSavedTaskListener onRemoveSavedtasklistener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_task);
        ButterKnife.bind(this);

        noPosts =findViewById(R.id.no_posts_container);
        onRemoveSavedtasklistener = this;
        sessionManager = new SessionManager(SavedTaskActivity.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(SavedTaskActivity.this);
        recyclerViewStatus.setLayoutManager(layoutManager);
        taskListAdapter = new TaskListAdapter(new ArrayList<>(), null);
        recyclerViewStatus.setAdapter(taskListAdapter);
        taskListAdapter.setOnItemClickListener(this);
        swipeRefresh.setOnRefreshListener(this);

        initToolbar();

        // swipeRefresh.setRefreshing(true);
        getStatusList();
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Saved Jobs");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    private void getStatusList() {


        swipeRefresh.setRefreshing(true);
        ArrayList<TaskModel> items = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_BOOKMARK + "?page=" + currentPage,
                response -> {
                    swipeRefresh.setRefreshing(false);

                    Timber.e(response);
                    // categoryArrayList.clear();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                            JSONArray jsonArray_data = jsonObject.getJSONArray("data");
                            for (int i = 0; jsonArray_data.length() > i; i++) {
                                JSONObject jsonObject_taskModel_list = jsonArray_data.getJSONObject(i);
                                TaskModel taskModel = new TaskModel().getJsonToModel(jsonObject_taskModel_list.getJSONObject("task"), SavedTaskActivity.this);
                                items.add(taskModel);
                            }
                        } else {
                            showToast("some went to wrong", SavedTaskActivity.this);
                            return;
                        }

                        if (jsonObject.has("meta") && !jsonObject.isNull("meta")) {
                            JSONObject jsonObject_meta = jsonObject.getJSONObject("meta");
                            totalPage = jsonObject_meta.getInt("last_page");
                            totalItem = jsonObject_meta.getInt("total");
                            Constant.PAGE_SIZE = jsonObject_meta.getInt("per_page");
                        }

                        if (items.size() <= 0) {
                            noPosts.setVisibility(View.VISIBLE);
                            recyclerViewStatus.setVisibility(View.GONE);
                        } else {
                            noPosts.setVisibility(View.GONE);
                            recyclerViewStatus.setVisibility(View.VISIBLE);

                        }
                        taskListAdapter.addItems(items, totalItem);
                        swipeRefresh.setRefreshing(false);
                    } catch (JSONException e) {
                        hideProgressDialog();
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  swipeRefresh.setRefreshing(false);

                        swipeRefresh.setRefreshing(false);
                        errorHandle1(error.networkResponse);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("Authorization", "Bearer " + sessionManager.getAccessToken());
                map1.put("Version", String.valueOf(BuildConfig.VERSION_CODE));
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(SavedTaskActivity.this);
        requestQueue.add(stringRequest);
        Timber.e(stringRequest.getUrl());

    }

    @Override
    public void onRefresh() {
        swipeRefresh.setRefreshing(true);
        currentPage = PAGE_START;
        isLastPage = false;
        taskListAdapter.clear();
        getStatusList();
    }

    @Override
    public void onItemClick(View view, TaskModel obj, int position, String action) {
        if (obj.getStatus().toLowerCase().equalsIgnoreCase(Constant.TASK_DRAFT.toLowerCase())) {
            //    getDataFromServer(obj.getSlug());
        } else {
            Intent intent = new Intent(SavedTaskActivity.this, TaskDetailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(ConstantKey.SLUG, obj.getSlug());
         //   bundle.putInt(ConstantKey.USER_ID, obj.getPoster().getId());
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public void onRemoveSavedTask() {
        currentPage = PAGE_START;
        isLastPage = false;
        taskListAdapter.clear();
        getStatusList();
    }
}
