package com.jobtick.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jobtick.R;
import com.jobtick.adapers.TaskCategoryAdapter;
import com.jobtick.models.TaskCategory;
import com.jobtick.models.TaskModel;
import com.jobtick.pagination.PaginationListener;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class CategroyListActivity extends ActivityBase implements TaskCategoryAdapter.OnItemClickListener {

    @BindView(R.id.recyclerView_categories)
    RecyclerView recyclerViewCategories;

    @BindView(R.id.iv_backbutton)
    ImageView ivBackButton;

    @BindView(R.id.lyt_search_category)
    CardView lytSearchCategories;

    private TaskModel taskModel;
    private TaskCategoryAdapter adapter;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;

    // @BindView(R.id.toolbar)
    //  MaterialToolbar toolbar;
    private String query = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        ButterKnife.bind(this);
        init();
        setCaegoryData();
        clickevent();
        //toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }


    public void clickevent() {
        ivBackButton.setOnClickListener(v -> finish());

        lytSearchCategories.setOnClickListener(v -> {

            Intent categoryActivity = new Intent(CategroyListActivity.this, SearchCategoryActivity.class);
            startActivity(categoryActivity);
            finish();
        });
    }


    public void init() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString("category") != null) {
                query = bundle.getString("category");
            }
        }
    }

    private void setCaegoryData() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(CategroyListActivity.this);
        recyclerViewCategories.setLayoutManager(layoutManager);
        //      recyclerViewCategories.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(dashboardActivity, 2), true));
        recyclerViewCategories.setHasFixedSize(true);
        List<TaskCategory> items = getTaskCategoryData();
        //set data and list adapter
        adapter = new TaskCategoryAdapter(CategroyListActivity.this, new ArrayList<>());
        recyclerViewCategories.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        /*
         * add scroll listener while user reach in bottom load more will call
         */
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

        Intent creating_task = new Intent(CategroyListActivity.this, TaskCreateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("categoryID", obj.getId());
        //  bundle.putParcelable(ConstantKey.TASK, taskModel);
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
                                TaskCategory taskModel = new TaskCategory().getJsonToModel(jsonObject_taskModel_list, CategroyListActivity.this);
                                items.add(taskModel);
                            }
                        } else {
                            showToast("some went to wrong", CategroyListActivity.this);
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
                            adapter.removeLoading();

                        if (items.size() <= 0) {
                            //ivNoPost.setVisibility(View.VISIBLE);
                            recyclerViewCategories.setVisibility(View.GONE);
                        } else {
                            //  ivNoPost.setVisibility(View.GONE);
                            recyclerViewCategories.setVisibility(View.VISIBLE);

                        }
                        adapter.addItems(items);

                        // check weather is last page or not
                        if (currentPage < totalPage) {
                            adapter.addLoading();
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
        RequestQueue requestQueue = Volley.newRequestQueue(CategroyListActivity.this);
        requestQueue.add(stringRequest);
        Log.e("url", stringRequest.getUrl());

        return items;

    }

}
