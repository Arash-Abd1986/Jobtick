package com.jobtick.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jobtick.R;
import com.jobtick.adapers.TaskListAdapter;
import com.jobtick.interfaces.OnRemoveSavedTaskListener;
import com.jobtick.models.TaskModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.droidsonroids.gif.GifImageView;

import static com.jobtick.pagination.PaginationListener.PAGE_START;

public class SavedTaskActivity extends ActivityBase implements TaskListAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, OnRemoveSavedTaskListener {


    private TaskListAdapter taskListAdapter;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;

    private SessionManager sessionManager;

    @BindView(R.id.recyclerview_savedTask)
    RecyclerView recyclerViewStatus;

    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;

  //  @BindView(R.id.ivNoPosst)
    //public GifImageView ivNoPost;

    private LottieAnimationView lottieAnim;
/*
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
*/

    @BindView(R.id.ivBack)
    ImageView ivBack;

    public static OnRemoveSavedTaskListener onRemoveSavedtasklistener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_task);
        ButterKnife.bind(this);

        lottieAnim=findViewById(R.id.lottieAnimationView);
        onRemoveSavedtasklistener = this;
        sessionManager = new SessionManager(SavedTaskActivity.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(SavedTaskActivity.this);
        recyclerViewStatus.setLayoutManager(layoutManager);
        taskListAdapter = new TaskListAdapter(new ArrayList<>());
        recyclerViewStatus.setAdapter(taskListAdapter);
        taskListAdapter.setOnItemClickListener(this);
        swipeRefresh.setOnRefreshListener(this);

        // swipeRefresh.setRefreshing(true);
        getStatusList();

        //toolbar.setNavigationOnClickListener(view -> onBackPressed());
        ivBack.setOnClickListener(v -> finish());

    }


    private void getStatusList() {


        swipeRefresh.setRefreshing(true);
        ArrayList<TaskModel> items = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_BOOKMARK + "?page=" + currentPage,
                response -> {
                    swipeRefresh.setRefreshing(false);

                    Log.e("responce_url", response);
                    // categoryArrayList.clear();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Log.e("json", jsonObject.toString());
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
                            Constant.PAGE_SIZE = jsonObject_meta.getInt("per_page");
                        }

                        /*
                         *manage progress view
                         */
                        if (currentPage != PAGE_START)
                            taskListAdapter.removeLoading();

                        if (items.size() <= 0) {
                            lottieAnim.setVisibility(View.VISIBLE);
                            recyclerViewStatus.setVisibility(View.GONE);
                        } else {
                            lottieAnim.setVisibility(View.GONE);
                            recyclerViewStatus.setVisibility(View.VISIBLE);

                        }
                        taskListAdapter.addItems(items);

                        swipeRefresh.setRefreshing(false);
                        // check weather is last page or not
                        if (currentPage < totalPage) {
                            taskListAdapter.addLoading();
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
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(SavedTaskActivity.this);
        requestQueue.add(stringRequest);
        Log.e("url", stringRequest.getUrl());

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
            bundle.putInt(ConstantKey.USER_ID, obj.getPoster().getId());
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
