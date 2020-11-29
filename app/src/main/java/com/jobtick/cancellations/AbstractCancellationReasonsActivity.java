package com.jobtick.cancellations;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.jobtick.R;
import com.jobtick.activities.ActivityBase;
import com.jobtick.activities.TaskDetailsActivity;
import com.jobtick.models.TaskModel;
import com.jobtick.models.cancellation.notice.CancellationNoticeModel;
import com.jobtick.models.cancellation.reason.CancellationReasonModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.SessionManager;
import com.jobtick.widget.ExtendedCommentText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public abstract class AbstractCancellationReasonsActivity extends ActivityBase{

    private String TAG = AbstractCancellationReasonsActivity.class.getName();

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    @BindView(R.id.comment_box)
    ExtendedCommentText commentText;


    protected String str_SLUG = null;
    protected TaskModel taskModel;
    protected CancellationReasonModel cancellationReasonModel;
    protected SessionManager sessionManager;

    protected String reason;
    protected float cancellationFeeValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancellation_reasons);
        ButterKnife.bind(this);
        initToolbar();
        sessionManager = new SessionManager(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        taskModel = TaskDetailsActivity.taskModel;
        str_SLUG = taskModel.getSlug();

//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null) {
//            if (bundle.getString(ConstantKey.SLUG) != null) {
//                str_SLUG = bundle.getString(ConstantKey.SLUG);
//            }
//            if (bundle.getParcelable(ConstantKey.TASK) != null) {
//                taskModel = bundle.getParcelable(ConstantKey.TASK);
//            }
//        }

        getCancellationReasonList();
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cancellation Request");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void getNoticeList() {

        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, Constant.BASE_URL + "cancellation/notice",
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                    String data = jsonObject.getString("data");
                                    Gson gson = new Gson();
                                    CancellationNoticeModel notice = gson.fromJson(data, CancellationNoticeModel.class);
                                    cancellationFeeValue = calculateCancellationFee(notice);
                                    hideProgressDialog();
                                }
                            } else {
                                showToast("Something went Wrong", AbstractCancellationReasonsActivity.this);
                                hideProgressDialog();
                            }
                        }


                    } catch (JSONException e) {
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                        hideProgressDialog();
                    }


                },
                error -> {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        // Print Error!
                        Timber.e(jsonError);
                        if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                            unauthorizedUser();
                            hideProgressDialog();
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                            if (jsonObject_error.has("message")) {
                                Toast.makeText(AbstractCancellationReasonsActivity.this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", AbstractCancellationReasonsActivity.this);
                    }
                    Timber.e(error.toString());
                    hideProgressDialog();
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(AbstractCancellationReasonsActivity.this);
        requestQueue.add(stringRequest);
        Log.e(TAG, stringRequest.getUrl());

    }

    private float calculateCancellationFee(CancellationNoticeModel notice){

        float fee = (Integer.parseInt(notice.getFeePercentage()) / 100.00f) * taskModel.getAmount();
        float maxFee = Integer.parseInt(notice.getMaxFeeAmount());
        maxFee = Math.min(maxFee, fee);

        return maxFee;
    }

    private void getCancellationReasonList() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, Constant.URL_CANCELLATION + "/reasons",
                response -> {
                    Timber.e(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                    String data = jsonObject.getString("data");
                                    Gson gson = new Gson();
                                    this.cancellationReasonModel = gson.fromJson(data, CancellationReasonModel.class);
                                    setReasons(cancellationReasonModel);
                                    getNoticeList();
                                }
                            } else {
                                showToast("Something went Wrong", AbstractCancellationReasonsActivity.this);
                            }
                        }


                    } catch (JSONException e) {
                        hideProgressDialog();
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                    }


                },
                error -> {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        // Print Error!
                        Timber.e(jsonError);
                        if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                            unauthorizedUser();
                            hideProgressDialog();
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                            if (jsonObject_error.has("message")) {
                                Toast.makeText(AbstractCancellationReasonsActivity.this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", AbstractCancellationReasonsActivity.this);
                    }
                    Timber.e(error.toString());
                    hideProgressDialog();
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(AbstractCancellationReasonsActivity.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public abstract void setReasons(CancellationReasonModel cancellationReasonModel);


    public static final String CANCELLATION_REASON = "cancellation reason";
    public static final String CANCELLATION_COMMENT = "cancellation comment";
    public static final String CANCELLATION_ID = "cancellation id";
}