package com.jobtick.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.adapers.TaskCategoryAdapter;
import com.jobtick.models.TaskCategory;
import com.jobtick.models.TaskModel;
import com.jobtick.pagination.PaginationListener;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jobtick.pagination.PaginationListener.PAGE_START;

public class CategoryListActivity extends ActivityBase implements TaskCategoryAdapter.OnItemClickListener {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recyclerView_categories)
    RecyclerView recyclerViewCategories;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.iv_backbutton)
    ImageView ivBackButton;


    private TaskModel taskModel;
    private TaskCategoryAdapter adapter;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;

    private String query = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        ButterKnife.bind(this);
        init();
        setCategoryData();
        clickEvent();
    }

    public void clickEvent() {
        ivBackButton.setOnClickListener(v -> finish());
    }

    public void init() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString("category") != null) {
                query = bundle.getString("category");
            }
        }
    }

    private void setCategoryData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(CategoryListActivity.this);
        recyclerViewCategories.setLayoutManager(layoutManager);
        recyclerViewCategories.setHasFixedSize(true);
        List<TaskCategory> items = getTaskCategoryData();
        adapter = new TaskCategoryAdapter(CategoryListActivity.this, new ArrayList<>());
        recyclerViewCategories.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        recyclerViewCategories.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                getTaskCategoryData();
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

    @Override
    public void onItemClick(View view, TaskCategory obj, int position) {
        Intent creating_task = new Intent(CategoryListActivity.this, TaskCreateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(ConstantKey.CATEGORY_ID, obj.getId());
        creating_task.putExtras(bundle);
        startActivityForResult(creating_task, ConstantKey.RESULTCODE_CATEGORY);
        finish();
    }

    public List<TaskCategory> getTaskCategoryData() {
        List<TaskCategory> items = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.BASE_URL + Constant.TASK_CATEGORY + "?query=" + query + "&page=" + currentPage,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                            JSONArray jsonArray_data = jsonObject.getJSONArray("data");
                            for (int i = 0; jsonArray_data.length() > i; i++) {
                                JSONObject jsonObject_taskModel_list = jsonArray_data.getJSONObject(i);
                                TaskCategory taskModel = new TaskCategory().getJsonToModel(jsonObject_taskModel_list, CategoryListActivity.this);
                                items.add(taskModel);
                            }
                        } else {
                            showToast("some went to wrong", CategoryListActivity.this);
                            return;
                        }

                        if (jsonObject.has("meta") && !jsonObject.isNull("meta")) {
                            JSONObject jsonObject_meta = jsonObject.getJSONObject("meta");
                            totalPage = jsonObject_meta.getInt("last_page");
                            Constant.PAGE_SIZE = jsonObject_meta.getInt("per_page");
                        }

                        if (currentPage != PAGE_START)
                            adapter.removeLoading();
                        if (items.size() <= 0) {
                            recyclerViewCategories.setVisibility(View.GONE);
                        } else {
                            recyclerViewCategories.setVisibility(View.VISIBLE);
                        }
                        adapter.addItems(items);

                        if (currentPage < totalPage) {
                            adapter.addLoading();
                        } else {
                            isLastPage = true;
                        }
                        isLoading = false;
                    } catch (JSONException e) {
                        hideProgressDialog();
                        e.printStackTrace();
                    }
                },
                error -> errorHandle1(error.networkResponse)) {
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
        RequestQueue requestQueue = Volley.newRequestQueue(CategoryListActivity.this);
        requestQueue.add(stringRequest);
        return items;
    }
}
