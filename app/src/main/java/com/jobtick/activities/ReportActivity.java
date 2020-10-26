package com.jobtick.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.R;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.TextView.TextViewSemiBold;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static com.jobtick.utils.Constant.URL_OFFERS;
import static com.jobtick.utils.Constant.URL_TASKS;
import static com.jobtick.utils.Constant.userID;
import static com.jobtick.utils.ConstantKey.KEY_COMMENT_REPORT;
import static com.jobtick.utils.ConstantKey.KEY_OFFER_REPORT;
import static com.jobtick.utils.ConstantKey.KEY_QUESTION_REPORT;
import static com.jobtick.utils.ConstantKey.KEY_TASK_REPORT;
import static com.jobtick.utils.ConstantKey.KEY_USER_REPORT;

public class ReportActivity extends ActivityBase {

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;


    @BindView(R.id.edt_subject)
    TextViewSemiBold edtSubject;

    @BindView(R.id.edt_description)
    EditText edtDescription;

    @BindView(R.id.spinner_report_category)
    Spinner spinnerReportCategory;

    @BindView(R.id.linear_report_category)
    LinearLayout linearReportCategory;

    private String str_SLUG = null;
    private int str_USERID = 0;
    private int str_OFFERID = 0;
    private int str_COMMENTID = 0;
    private int str_QUESTIONID = 0;
    private String str_KEY = null;
    private boolean isFristTime = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super
                .onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);
        initToolbar();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(ConstantKey.SLUG)) {
                str_KEY = bundle.getString("key");
                str_SLUG = bundle.getString(ConstantKey.SLUG);
            }
            if (bundle.containsKey(Constant.userID)) {
                str_KEY = bundle.getString("key");
                str_USERID = bundle.getInt(userID);
            }
            if (bundle.containsKey(ConstantKey.offerId)) {
                str_KEY = bundle.getString("key");
                str_OFFERID = bundle.getInt(ConstantKey.offerId);
            }
            if (bundle.containsKey(ConstantKey.questionId)) {
                str_KEY = bundle.getString("key");
                str_QUESTIONID = bundle.getInt(ConstantKey.questionId);
            }
            if (bundle.containsKey(ConstantKey.commentId)) {
                str_KEY = bundle.getString("key");
                str_COMMENTID = bundle.getInt(ConstantKey.commentId);
            }

        }

        spinnerReportCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFristTime) {
                    edtSubject.setText(spinnerReportCategory.getSelectedItem().toString());
                }
                isFristTime = true;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Report");
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    @OnClick({R.id.lyt_btn_report, R.id.linear_report_category})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lyt_btn_report:
                if (str_KEY.equals(KEY_USER_REPORT)) {
                    reportUserSpam();
                } else if (str_KEY.equals(KEY_TASK_REPORT)) {
                    reportTaskSpam();
                } else if (str_KEY.equals(KEY_COMMENT_REPORT)) {
                    reportCommentSpam();
                } else if (str_KEY.equals(KEY_OFFER_REPORT)) {
                    reportOfferSpam();
                } else if (str_KEY.equals(KEY_QUESTION_REPORT)) {
                    reportQuestionSpam();
                }
                break;
            case R.id.linear_report_category:
                spinnerReportCategory.performClick();
                break;
        }
    }

    private void reportTaskSpam() {
        //  {{baseurl}}/tasks/:slug/report
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_TASKS + "/" + str_SLUG + "/report",
                response -> {
                    Timber.e(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                showCustomDialog("Reported Task Successfully");
                            } else {
                                showToast("Something went wrong !", ReportActivity.this);
                            }
                        } else {
                            showToast(getString(R.string.server_went_wrong), ReportActivity.this);
                        }
                        hideProgressDialog();
                    } catch (JSONException e) {
                        hideProgressDialog();
                        e.printStackTrace();
                    }

                },
                error -> {
                    hideProgressDialog();

                    //  swipeRefresh.setRefreshing(false);
                    errorHandle1(error.networkResponse);
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("subject", edtSubject.getText().toString());
                map1.put("description", edtDescription.getText().toString());
                return map1;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");

                return map1;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(ReportActivity.this);
        requestQueue.add(stringRequest);

    }

    private void reportUserSpam() {

        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_TASKS + "/" + str_USERID + "/report",
                response -> {
                    Timber.e(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                showCustomDialog("Reported User Successfully");

                            } else {
                                showToast("Something went wrong ", ReportActivity.this);
                            }
                        } else {
                            showToast(getString(R.string.server_went_wrong), ReportActivity.this);
                        }
                        hideProgressDialog();
                    } catch (JSONException e) {
                        hideProgressDialog();
                        e.printStackTrace();
                    }

                },
                error -> {
                    hideProgressDialog();

                    //  swipeRefresh.setRefreshing(false);
                    errorHandle1(error.networkResponse);
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("subject", edtSubject.getText().toString());
                map1.put("description", edtDescription.getText().toString());

                return map1;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");

                return map1;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(ReportActivity.this);
        requestQueue.add(stringRequest);

    }

    private void reportQuestionSpam() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_TASKS + "/" + str_QUESTIONID + "/report",
                response -> {
                    Timber.e(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                showCustomDialog("Question Reported Successfully");
                            } else {
                                showToast("Something went wrong !", ReportActivity.this);
                            }
                        } else {
                            showToast(getString(R.string.server_went_wrong), ReportActivity.this);
                        }
                        hideProgressDialog();
                    } catch (JSONException e) {
                        hideProgressDialog();
                        e.printStackTrace();
                    }

                },
                error -> {
                    hideProgressDialog();

                    //  swipeRefresh.setRefreshing(false);
                    errorHandle1(error.networkResponse);
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("subject", edtSubject.getText().toString());
                map1.put("description", edtDescription.getText().toString());
                return map1;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");

                return map1;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(ReportActivity.this);
        requestQueue.add(stringRequest);
    }

    private void reportCommentSpam() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_TASKS + "/" + str_COMMENTID + "/report",
                response -> {
                    Timber.e(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                showCustomDialog("Comment Reported Successfully");
                            } else {
                                showToast("Something went wrong !", ReportActivity.this);
                            }
                        } else {
                            showToast(getString(R.string.server_went_wrong), ReportActivity.this);
                        }
                        hideProgressDialog();
                    } catch (JSONException e) {
                        hideProgressDialog();
                        e.printStackTrace();
                    }

                },
                error -> {
                    hideProgressDialog();

                    //  swipeRefresh.setRefreshing(false);
                    errorHandle1(error.networkResponse);
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("subject", edtSubject.getText().toString());
                map1.put("description", edtDescription.getText().toString());
                return map1;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");

                return map1;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(ReportActivity.this);
        requestQueue.add(stringRequest);
    }

    private void reportOfferSpam() {
        //{{baseurl}}/offers/:id/report

        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_OFFERS + "/" + str_OFFERID + "/report",
                response -> {
                    Timber.e(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                showCustomDialog("Offer Reported Successfully");
                            } else {
                                showToast("Something went wrong !", ReportActivity.this);
                            }
                        } else {
                            showToast(getString(R.string.server_went_wrong), ReportActivity.this);
                        }
                        hideProgressDialog();
                    } catch (JSONException e) {
                        hideProgressDialog();
                        e.printStackTrace();
                    }

                },
                error -> {
                    hideProgressDialog();

                    //  swipeRefresh.setRefreshing(false);
                    errorHandle1(error.networkResponse);
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("subject", edtSubject.getText().toString());
                map1.put("description", edtDescription.getText().toString());
                return map1;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");

                return map1;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(ReportActivity.this);
        requestQueue.add(stringRequest);
    }

    private void showCustomDialog(String message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_show_warning);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        TextViewRegular txtMessage = dialog.findViewById(R.id.txt_message);
        txtMessage.setText(message);

        dialog.findViewById(R.id.btn_ok).setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


}

