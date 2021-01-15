package com.jobtick.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatRatingBar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.android.R;
import android.annotation.SuppressLint;

import com.jobtick.android.models.TaskModel;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.ConstantKey;
import com.jobtick.android.utils.HttpStatus;
import com.jobtick.android.utils.ImageUtil;
import com.jobtick.android.utils.SessionManager;
import com.jobtick.android.utils.Tools;
import com.jobtick.android.widget.ExtendedCommentText;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class LeaveReviewActivity extends ActivityBase {

    private static final String TAG = LeaveReviewActivity.class.getName();

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_avatar)
    CircularImageView imgAvatar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_verified_account)
    ImageView imgVerifiedAccount;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.account_rating)
    TextView accountRating;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_full_name)
    TextView txtFullName;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.job_success_percentage)
    TextView jobSuccessPercentage;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.job_title)
    TextView jobTitle;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_calender_date)
    TextView txtCalenderDate;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_location)
    TextView txtLocation;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_amount)
    TextView txtAmount;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.ratingbar)
    AppCompatRatingBar ratingbar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_progress_ratingbar)
    TextView txtProgressRatingbar;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_write_review)
    ExtendedCommentText edtWriteReview;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.submit)
    Button submit;

    private SessionManager sessionManager;
    private TaskModel taskModel;
    private Boolean isMyTask = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_review);
        ButterKnife.bind(this);

        sessionManager = new SessionManager(LeaveReviewActivity.this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isMyTask = bundle.getBoolean(ConstantKey.IS_MY_TASK);
            taskModel = TaskDetailsActivity.taskModel;
          //  taskModel = bundle.getParcelable(ConstantKey.TASK);
        }

        initToolbar();
        setData();
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Leave a review");
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

    private void setData() {

        jobTitle.setText(taskModel.getTitle());
        if (taskModel.getLocation() != null) {
            txtLocation.setText(taskModel.getLocation());
        } else {
            txtLocation.setText(R.string.in_person);
        }
        txtAmount.setText(String.format(Locale.ENGLISH, "$ %d", taskModel.getAmount()));
        txtCalenderDate.setText(Tools.getDayMonthDateTimeFormat(taskModel.getDueDate()));

        if (!isMyTask) {
            //worker write review
            if (taskModel.getPoster().getAvatar() != null && taskModel.getPoster().getAvatar().getThumbUrl() != null) {
                ImageUtil.displayImage(imgAvatar, taskModel.getPoster().getAvatar().getThumbUrl(), null);
            } else {
                //deafult image
            }
            if (taskModel.getPoster().getIsVerifiedAccount() == 1) {
                imgVerifiedAccount.setVisibility(View.VISIBLE);
            } else {
                imgVerifiedAccount.setVisibility(View.GONE);
            }
            txtFullName.setText(taskModel.getPoster().getName());

            int jobSuccess = 0;
            if(taskModel.getPoster().getPostTaskStatistics() != null &&
                    taskModel.getPoster().getPostTaskStatistics().getCompletionRate() != null)
                jobSuccess = taskModel.getPoster().getPostTaskStatistics().getCompletionRate();
            jobSuccessPercentage.setText(String.format(Locale.ENGLISH, "%d%%", jobSuccess));

            int avgRating = 0;
            int reviews = 0;
            if(taskModel.getPoster().getPosterRatings() != null && taskModel.getPoster().getPosterRatings().getAvgRating() != null)
                avgRating = taskModel.getPoster().getPosterRatings().getAvgRating();

            if(taskModel.getPoster().getPosterRatings() != null && taskModel.getPoster().getPosterRatings().getReceivedReviews() != null)
                reviews = taskModel.getPoster().getPosterRatings().getReceivedReviews();

            accountRating.setText(
                    String.format(Locale.ENGLISH, "  %d (%d)",
                           avgRating, reviews));

        } else {
            //poster write review
            if (taskModel.getWorker().getAvatar() != null && taskModel.getWorker().getAvatar().getThumbUrl() != null) {
                ImageUtil.displayImage(imgAvatar, taskModel.getWorker().getAvatar().getThumbUrl(), null);
            } else {
                //deafult image
            }
            if (taskModel.getWorker().getIsVerifiedAccount() == 1) {
                imgVerifiedAccount.setVisibility(View.VISIBLE);
            } else {
                imgVerifiedAccount.setVisibility(View.GONE);
            }
            txtFullName.setText(taskModel.getWorker().getName());

            int jobSuccess = 0;
            if(taskModel.getWorker().getWorkTaskStatistics() != null &&
                    taskModel.getWorker().getWorkTaskStatistics().getCompletionRate() != null)
                jobSuccess = taskModel.getWorker().getWorkTaskStatistics().getCompletionRate();
            jobSuccessPercentage.setText(String.format(Locale.ENGLISH, "%d%%", jobSuccess));

            int avgRating = 0;
            int reviews = 0;
            if(taskModel.getWorker().getWorkerRatings() != null && taskModel.getWorker().getWorkerRatings().getAvgRating() != null)
                avgRating = taskModel.getWorker().getWorkerRatings().getAvgRating();

            if(taskModel.getWorker().getWorkerRatings() != null && taskModel.getWorker().getWorkerRatings().getReceivedReviews() != null)
                reviews = taskModel.getWorker().getWorkerRatings().getReceivedReviews();

            accountRating.setText(
                    String.format(Locale.ENGLISH, "  %d (%d)",
                            avgRating, reviews));
        }

        ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if (v > 0.5 && v < 1.5)
                    txtProgressRatingbar.setText(R.string.awful);
                if (v > 1.5 && v < 2.5)
                    txtProgressRatingbar.setText(R.string.bad);
                if (v > 2.5 && v < 3.5)
                    txtProgressRatingbar.setText(R.string.ok);
                if (v > 3.5 && v < 4.5)
                    txtProgressRatingbar.setText(R.string.good);
                if (v > 4.5)
                    txtProgressRatingbar.setText(R.string.excellent);

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick({R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.submit:
                if (!validation()) return;
                if (isMyTask) {
                    submitReview(String.valueOf((int) ratingbar.getRating()), edtWriteReview.getText().trim(), Constant.URL_TASKS + "/" + taskModel.getSlug() + "/rating/submit-review");
                } else {
                    submitReview(String.valueOf((int) ratingbar.getRating()), edtWriteReview.getText().trim(), Constant.URL_TASKS + "/" + taskModel.getSlug() + "/rating/submit-review");
                }
                break;
        }
    }

    private void submitReview(String rating, String written_review, String url) {
        showProgressDialog();

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, url,
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(ConstantKey.WRITE_REVIEW, true);
                                intent.putExtras(bundle);
                                setResult(ConstantKey.RESULTCODE_WRITE_REVIEW, intent);
                                finish();
                            } else {
                                showToast("Something went Wrong", LeaveReviewActivity.this);
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
                            hideProgressDialog();
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                            if (jsonObject_error.has("message")) {
                                showToast(jsonObject_error.getString("message"), this);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", LeaveReviewActivity.this);
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
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("rating", rating);
                map1.put("message", written_review);
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(LeaveReviewActivity.this);
        requestQueue.add(stringRequest);
        Timber.e(stringRequest.getUrl());
    }

    private boolean validation() {
        if (ratingbar.getRating() < 0.5) {
            showToast("Please rate!", this);
            return false;
        }
        if (TextUtils.isEmpty(edtWriteReview.getText().trim())) {
            edtWriteReview.setError("");
            return false;
        }
        if (edtWriteReview.getText().length() < edtWriteReview.geteMinSize()) {
            edtWriteReview.setError("");
            return false;
        }

        return true;
    }


}