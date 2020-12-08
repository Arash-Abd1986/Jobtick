package com.jobtick.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.jobtick.R;
import com.jobtick.adapers.PreviewTaskAdapter;
import com.jobtick.adapers.TaskListAdapter;
import com.jobtick.models.PreviewTaskModel;
import com.jobtick.models.PreviewTaskSetModel;
import com.jobtick.models.TaskModel;
import com.jobtick.pagination.PaginationListener;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.Helper;
import com.jobtick.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jobtick.pagination.PaginationListener.PAGE_START;

public class SearchTaskActivity extends ActivityBase implements TextView.OnEditorActionListener,
        TaskListAdapter.OnItemClickListener, PreviewTaskAdapter.OnItemClickListener<PreviewTaskModel> {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_search_new)
    MaterialButton lytSearchNew;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.iv_back)
    ImageView ivBack;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.back_to_activity)
    MaterialButton lytCategories;

    @BindView(R.id.list)
    RecyclerView recyclerView;


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_search_categoreis)
    EditText edtSearch;

    private SessionManager sessionManager;
    private TaskListAdapter adapter;
    private PreviewTaskSetModel previewTaskSetModel;
    private PreviewTaskAdapter previewTaskAdapter;

    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;

    private boolean isFromMyJobs = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_all);
        ButterKnife.bind(this);
        isFromMyJobs = getIntent().getBooleanExtra(ConstantKey.FROM_MY_JOBS_WITH_LOVE, false);
        //  RelativeLayout emptySearch = findViewById(R.id.empty_search);
        sessionManager = new SessionManager(this);
        edtSearch.setHint("Search Jobs");
        edtSearch.requestFocus();
        edtSearch.performClick();
        edtSearch.setOnEditorActionListener(this);

        setPreviewAdapter();
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.lyt_search_new, R.id.back_to_activity, R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lyt_search_new:
                edtSearch.setText("");
                edtSearch.requestFocus();
                edtSearch.performClick();
                showKeyboard(edtSearch);
                break;
            case R.id.back_to_activity:
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
            doApiCall();
            return true;
        }
        return false;
    }


    private void doApiCall() {
        String queryParameter = "";

        queryParameter = edtSearch.getText().toString();

        ArrayList<TaskModel> items = new ArrayList<>();
        Helper.closeKeyboard(this);
        String url = Constant.URL_TASKS + "?search_query=" +  queryParameter + "&page=" + currentPage;
        if(isFromMyJobs)
            url = Constant.URL_TASKS + ConstantKey.ALL_MY_JOBS_URL_FILTER + "search_query=" +  queryParameter + "&page=" + currentPage;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.e("responce_url", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Log.e("json", jsonObject.toString());
                        if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                            JSONArray jsonArray_data = jsonObject.getJSONArray("data");
                            for (int i = 0; jsonArray_data.length() > i; i++) {
                                JSONObject jsonObject_taskModel_list = jsonArray_data.getJSONObject(i);
                                TaskModel taskModel = new TaskModel().getJsonToModel(jsonObject_taskModel_list, this);
                                items.add(taskModel);
                            }
                        } else {
                            showToast("some went to wrong", this);
                            return;
                        }
                        if (jsonObject.has("meta") && !jsonObject.isNull("meta")) {
                            JSONObject jsonObject_meta = jsonObject.getJSONObject("meta");
                            totalPage = jsonObject_meta.getInt("last_page");
                            Constant.PAGE_SIZE = jsonObject_meta.getInt("per_page");
                        }

                        if (currentPage != PAGE_START)
                            adapter.removeLoading();

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
                error -> {
                    errorHandle1(error.networkResponse);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("Authorization", "Bearer " + sessionManager.getAccessToken());
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        Log.e("url", stringRequest.getUrl());
    }


    private void setPreviewAdapter() {
        previewTaskSetModel = sessionManager.getPreviewTaskModel(SearchTaskActivity.class, isFromMyJobs);
        if (previewTaskSetModel == null)
            previewTaskSetModel = new PreviewTaskSetModel();

        previewTaskAdapter = new PreviewTaskAdapter(new ArrayList<>(previewTaskSetModel.getPreviewSet()));
        recyclerView.setAdapter(previewTaskAdapter);

        recyclerView.setHasFixedSize(true);
        previewTaskAdapter.setOnItemClickListener(this);
    }

    private void setOnlineAdapter(){
        adapter = new TaskListAdapter((new ArrayList<>()));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    private boolean previewMode = true;
    private void setLoadMoreListener() {
        if (!previewMode) return;
        recyclerView.addOnScrollListener(new PaginationListener((LinearLayoutManager) recyclerView.getLayoutManager()) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                doApiCall();
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
    public void onItemClick(View view, TaskModel obj, int position, String action) {
        onItemClick(view, obj.getPreviewTaskModel(), position);
    }

    @Override
    public void onItemClick(View view, PreviewTaskModel obj, int position) {
        previewTaskSetModel.addItem(obj);
        Intent intent = new Intent(this, TaskDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ConstantKey.SLUG, obj.getSlug());
        bundle.putInt(ConstantKey.USER_ID, obj.getUserId());
        intent.putExtras(bundle);
        startActivity(intent);
        sessionManager.setPreviewTaskModel(previewTaskSetModel, SearchTaskActivity.class, isFromMyJobs);
    }
}

