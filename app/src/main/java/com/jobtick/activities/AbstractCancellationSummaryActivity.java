package com.jobtick.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.jobtick.R;
import com.jobtick.cancellations.AbstractCancellationReasonsActivity;
import com.jobtick.cancellations.cancellationSubmittedActivity;
import com.jobtick.models.TaskModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.ImageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class AbstractCancellationSummaryActivity extends ActivityBase {

    private MaterialToolbar toolbar;
    private TextView title;
    private TextView securedPayment;
    private ImageView imgAvatar;
    private TextView taskTitle;
    private TextView posterName;
    private TextView taskFee;
    private TextView cancellationReason;
    private View commentBox;
    private TextView commentContent;
    private TextView respondHeader;
    private View feeContainer;
    private TextView feeAmount;
    private TextView learnMore;

    private Button submit;

    protected TaskModel taskModel;
    protected String str_SLUG = null;

    private String strReason;
    private String strComment;
    private int reasonId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancellation_summary);
        taskModel = TaskDetailsActivity.taskModel;

        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.title);
        securedPayment = findViewById(R.id.secured_payment);

        imgAvatar = findViewById(R.id.ivAvatar);
        taskTitle = findViewById(R.id.task_title);
        posterName = findViewById(R.id.poster_name);
        taskFee = findViewById(R.id.task_fee);
        cancellationReason = findViewById(R.id.cancellation_reason);

        commentBox = findViewById(R.id.comment_box);
        commentContent = findViewById(R.id.comment_content);

        respondHeader = findViewById(R.id.cancellation_respond_header);

        feeContainer = findViewById(R.id.cancellation_fee_container);
        feeAmount = findViewById(R.id.cancellation_fee_amount);
        learnMore = findViewById(R.id.learn_more);

        submit = findViewById(R.id.btn_submit);

        submit.setOnClickListener(v -> {
            cancellationSubmit(strReason, strComment, reasonId);
        });

        init();
        initToolbar();
    }

    private void init() {

        str_SLUG = taskModel.getSlug();

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) throw new IllegalStateException("there is no bundle.");

        title.setText(Html.fromHtml(bundle.getString(ConstantKey.CANCELLATION_TITLE)));

        if (taskModel.getPoster().getAvatar() != null && taskModel.getPoster().getAvatar().getThumbUrl() != null) {
            ImageUtil.displayImage(imgAvatar, taskModel.getPoster().getAvatar().getThumbUrl(), null);
        } else {
            //deafult image
        }
        taskTitle.setText(taskModel.getTitle());
        taskFee.setText(String.format(Locale.ENGLISH, "$ %d", taskModel.getAmount()));
        posterName.setText(taskModel.getPoster().getName());

        strComment = bundle.getString(AbstractCancellationReasonsActivity.CANCELLATION_COMMENT, null);
        strReason = bundle.getString(AbstractCancellationReasonsActivity.CANCELLATION_REASON, null);
        reasonId = bundle.getInt(AbstractCancellationReasonsActivity.CANCELLATION_ID, 0);

        //first we check, it the values are inserted in CancellationWorker/PosterActivity
        if (strReason != null) {
            submit.setVisibility(View.VISIBLE);

            cancellationReason.setText(reasonRectify(strReason));
            if (strComment == null || strComment.trim().isEmpty())
                commentBox.setVisibility(View.GONE);
            else
                commentContent.setText(strComment.trim());

            float feeValue = bundle.getFloat(AbstractCancellationReasonsActivity.CANCELLATION_VALUE, 0f);
            if(feeValue != 0f){
                feeAmount.setText(String.format(Locale.ENGLISH, "-$ %.1f", feeValue));
                feeContainer.setVisibility(View.VISIBLE);
            }

            //here we sure the values is in cancellation model, but as usual, we have to check it
        } else if (taskModel.getCancellation() != null) {
            submit.setVisibility(View.GONE);
            cancellationReason.setText(reasonRectify(taskModel.getCancellation().getReason()));
            if (taskModel.getCancellation().getComment() == null || taskModel.getCancellation().getComment().trim().isEmpty())
                commentBox.setVisibility(View.GONE);
            else
                commentContent.setText(taskModel.getCancellation().getComment().trim());
        }

        //TODO: set value to fee, show it or not


    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cancellation Request Summary");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private String reasonRectify(String reason){
        if(reason.contains("{worker}"))
            reason = reason.replace("{worker}", taskModel.getWorker().getName());
        if(reason.contains("{poster}"))
            reason = reason.replace("{poster}", taskModel.getWorker().getName());

        reason = String.format("\"%s\"", reason);

        return reason;
    }

    protected void decline(){

    }
    protected void accept(){

    }
    protected void withdraw(){

    }

    protected void cancellationSubmit(String str_reason, String str_comment, int reasonId) {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, Constant.URL_TASKS + "/" + str_SLUG + "/cancellation",
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {

                                Bundle bundle = new Bundle();
                                bundle.putString(ConstantKey.CANCELLATION_SUBMITTED, "Cancellation submitted successfully.");
                                Intent intent = new Intent(this, cancellationSubmittedActivity.class);
                                intent.putExtras(bundle);
                                startActivityForResult(intent, ConstantKey.RESULTCODE_CANCELLATION);
                            } else {
                                showToast("Something went Wrong", this);
                            }
                        }

                    } catch (JSONException e) {
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
                                Toast.makeText(this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", this);
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

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<>();
                map1.put("reason", str_reason);
                map1.put("reason_id", Integer.toString(reasonId));
                if (str_comment != null) {
                    map1.put("comment", str_comment);
                }
                Timber.e(String.valueOf(map1.size()));
                Timber.e(map1.toString());
                return map1;

            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == ConstantKey.RESULTCODE_CANCELLATION){
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }
}