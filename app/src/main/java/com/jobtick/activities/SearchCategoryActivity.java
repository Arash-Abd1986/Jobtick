package com.jobtick.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.adapers.TaskCategoryAdapter;
import com.jobtick.adapers.PreviewAdapter;
import com.jobtick.models.PreviewCategorySetModel;
import com.jobtick.models.TaskCategory;
import com.jobtick.models.PreviewCategoryModel;
import com.jobtick.pagination.PaginationListener;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jobtick.pagination.PaginationListener.PAGE_START;

public class SearchCategoryActivity extends ActivityBase implements TextView.OnEditorActionListener,
        TaskCategoryAdapter.OnItemClickListener, PreviewAdapter.OnItemClickListener<PreviewCategoryModel> {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_search_new)
    MaterialButton lytSearchNew;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.iv_back)
    ImageView ivBack;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.list)
    RecyclerView recyclerViewCategories;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.empty_search)
    RelativeLayout emptySearch;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_search_categoreis)
    EditText edtSearchCategories;

    private SessionManager sessionManager;
    private TaskCategoryAdapter adapter;
    private PreviewCategorySetModel previewCategorySetModel;
    private PreviewAdapter previewAdapter;

    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;

    private String query = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_all);
        ButterKnife.bind(this);
        //  RelativeLayout emptySearch = findViewById(R.id.empty_search);
        sessionManager = new SessionManager(this);

        edtSearchCategories.requestFocus();
        edtSearchCategories.performClick();
        edtSearchCategories.setOnEditorActionListener(this);

        setPreviewAdapter();
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.lyt_search_new, R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lyt_search_new:
                edtSearchCategories.setText("");
                edtSearchCategories.requestFocus();
                edtSearchCategories.performClick();
                showKeyboard(edtSearchCategories);
                break;
            case R.id.iv_back:
                finish();
                break;

        }

    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            //set adapter to online mode (previewMode will be false)
            setOnlineAdapter();
            setLoadMoreListener();
            getTaskCategoryData();
            return true;
        }
        return false;
    }


    public List<TaskCategory> getTaskCategoryData() {
        List<TaskCategory> items = new ArrayList<>();
        query = edtSearchCategories.getText().toString();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.BASE_URL + Constant.TASK_CATEGORY + "?query=" + query + "&page=" + currentPage,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                            JSONArray jsonArray_data = jsonObject.getJSONArray("data");
                            for (int i = 0; jsonArray_data.length() > i; i++) {
                                JSONObject jsonObject_taskModel_list = jsonArray_data.getJSONObject(i);
                                TaskCategory taskModel = new TaskCategory().getJsonToModel(jsonObject_taskModel_list, SearchCategoryActivity.this);
                                items.add(taskModel);
                            }
                        } else {
                            showToast("some went to wrong", SearchCategoryActivity.this);
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
                            emptySearch.setVisibility(View.VISIBLE);
                            recyclerViewCategories.setVisibility(View.GONE);
                        } else {
                            recyclerViewCategories.setVisibility(View.VISIBLE);
                            emptySearch.setVisibility(View.GONE);
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
        RequestQueue requestQueue = Volley.newRequestQueue(SearchCategoryActivity.this);
        requestQueue.add(stringRequest);
        return items;
    }

    private void setPreviewAdapter() {
        previewCategorySetModel = sessionManager.getPreviewModel(SearchCategoryActivity.class);
        if (previewCategorySetModel == null)
            previewCategorySetModel = new PreviewCategorySetModel();

        previewAdapter = new PreviewAdapter(new ArrayList<>(previewCategorySetModel.getPreviewSet()));
        recyclerViewCategories.setAdapter(previewAdapter);

        recyclerViewCategories.setHasFixedSize(true);
        previewAdapter.setOnItemClickListener(this);
    }

    private void setOnlineAdapter(){
        adapter = new TaskCategoryAdapter(this, new ArrayList<>());
        recyclerViewCategories.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    private boolean previewMode = true;
    private void setLoadMoreListener() {
        if (!previewMode) return;
        recyclerViewCategories.addOnScrollListener(new PaginationListener((LinearLayoutManager) recyclerViewCategories.getLayoutManager()) {
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
        previewMode = false;
    }

    private void showKeyboard(EditText editText) {
        editText.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getBaseContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, 0);
            }
        });
    }

    @Override
    public void onItemClick(View view, PreviewCategoryModel obj, int position) {
        previewCategorySetModel.addItem(obj);
        sessionManager.setPreviewModel(previewCategorySetModel, SearchCategoryActivity.class);
        Intent creating_task = new Intent(SearchCategoryActivity.this, TaskCreateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(ConstantKey.CATEGORY_ID, obj.getId());
        creating_task.putExtras(bundle);
        startActivityForResult(creating_task, ConstantKey.RESULTCODE_CATEGORY);
        finish();
    }

    @Override
    public void onItemClick(View view, TaskCategory obj, int position) {
        onItemClick(view, obj.getPreviewModel(),position);
    }
}

