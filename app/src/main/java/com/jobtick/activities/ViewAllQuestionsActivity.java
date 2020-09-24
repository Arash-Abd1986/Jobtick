package com.jobtick.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import com.jobtick.R;
import com.jobtick.adapers.OfferListAdapter;
import com.jobtick.adapers.QuestionListAdapter;
import com.jobtick.models.OfferModel;
import com.jobtick.models.QuestionModel;
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

import static com.jobtick.pagination.PaginationListener.PAGE_START;

public class ViewAllQuestionsActivity extends ActivityBase implements SwipeRefreshLayout.OnRefreshListener, QuestionListAdapter.OnItemClickListener {

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.recycler_view_all_questions)
    RecyclerView recyclerViewAllQuestions;

    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;
    private QuestionListAdapter questionListAdapter;
    private String str_slug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_questions);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getString(ConstantKey.SLUG) != null) {
            str_slug = bundle.getString(ConstantKey.SLUG);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        initCOmponent();
    }

    private void initCOmponent() {
        recyclerViewAllQuestions.setLayoutManager(new LinearLayoutManager(ViewAllQuestionsActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerViewAllQuestions.setHasFixedSize(true);
        sessionManager = new SessionManager(ViewAllQuestionsActivity.this);
        questionListAdapter = new QuestionListAdapter(ViewAllQuestionsActivity.this, new ArrayList<>());
        recyclerViewAllQuestions.setAdapter(questionListAdapter);
        swipeRefresh.setOnRefreshListener(this);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(ViewAllQuestionsActivity.this);
        questionListAdapter.setOnItemClickListener(this);
        swipeRefresh.setRefreshing(true);
        doApiCall();
        /*
         * add scroll listener while user reach in bottom load more will call
         */
        recyclerViewAllQuestions.addOnScrollListener(new PaginationListener(layoutManager) {
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
    }


    private void doApiCall() {
        ArrayList<QuestionModel> items = new ArrayList<>();
        Helper.closeKeyboard(ViewAllQuestionsActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_TASKS + "/" + str_slug + "/questions?page=" + currentPage,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("responce_url", response);
                        // categoryArrayList.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.e("json", jsonObject.toString());
                            if (!jsonObject.has("data") && jsonObject.isNull("data")) {
                                showToast("some went to wrong", ViewAllQuestionsActivity.this);
                                return;
                            }
                            JSONArray jsonArray_data = jsonObject.getJSONArray("data");
                            for (int i = 0; jsonArray_data.length() > i; i++) {
                                JSONObject jsonObject_offers = jsonArray_data.getJSONObject(i);
                                QuestionModel questionModel = new QuestionModel().getJsonToModel(jsonObject_offers);
                                items.add(questionModel);
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
                                questionListAdapter.removeLoading();
                            questionListAdapter.addItems(items);

                            swipeRefresh.setRefreshing(false);
                            // check weather is last page or not
                            if (currentPage < totalPage) {
                                questionListAdapter.addLoading();
                            } else {
                                isLastPage = true;
                            }
                            isLoading = false;
                        } catch (JSONException e) {
                            hidepDialog();
                            Log.e("EXCEPTION", String.valueOf(e));
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        swipeRefresh.setRefreshing(false);
                        errorHandle1(error.networkResponse);
                    }
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
        RequestQueue requestQueue = Volley.newRequestQueue(ViewAllQuestionsActivity.this);
        requestQueue.add(stringRequest);
        Log.e("url", stringRequest.getUrl());
    }

    @Override
    public void onRefresh() {
        currentPage = PAGE_START;
        isLastPage = false;
        questionListAdapter.clear();
        doApiCall();
    }

    @Override
    public void onItemQuestionClick(View view, QuestionModel obj, int position, String action) {
        if (action.equalsIgnoreCase("reply")) {
            Intent intent = new Intent(ViewAllQuestionsActivity.this, PublicChatActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable(ConstantKey.QUESTION_LIST_MODEL, obj);
            intent.putExtras(bundle);
            startActivityForResult(intent, 21);
        }
    }
}
