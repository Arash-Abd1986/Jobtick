package com.jobtick.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.cardview.widget.CardView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;
import com.jobtick.TextView.TextViewBold;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.TextView.TextViewSemiBold;
import com.jobtick.models.TaskModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.ImageUtil;
import com.jobtick.utils.SessionManager;
import com.jobtick.utils.Tools;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class LeaveReviewActivity extends ActivityBase {

    private static final String TAG = LeaveReviewActivity.class.getName();
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.img_avatar)
    CircularImageView imgAvatar;
    @BindView(R.id.img_verified_account)
    ImageView imgVerifiedAccount;
    @BindView(R.id.txt_account_level)
    TextViewRegular txtAccountLevel;
    @BindView(R.id.img_level)
    ImageView imgLevel;
    @BindView(R.id.txt_full_name)
    TextViewSemiBold txtFullName;
    @BindView(R.id.txt_suburb)
    TextViewRegular txtSuburb;
    @BindView(R.id.txt_review_count)
    TextViewRegular txtReviewCount;
    @BindView(R.id.lyt_total_review_count)
    LinearLayout lytTotalReviewCount;
    @BindView(R.id.image_1_left)
    ImageView image1Left;
    @BindView(R.id.img_1_right)
    ImageView img1Right;
    @BindView(R.id.img_map_pin)
    ImageView imgMapPin;
    @BindView(R.id.txt_location)
    TextViewRegular txtLocation;
    @BindView(R.id.img_calender)
    ImageView imgCalender;
    @BindView(R.id.txt_calender_date)
    TextViewRegular txtCalenderDate;
    @BindView(R.id.txt_amount)
    TextViewBold txtAmount;
    @BindView(R.id.ratingbar)
    AppCompatRatingBar ratingbar;
    @BindView(R.id.txt_progress_ratingbar)
    TextViewRegular txtProgressRatingbar;
    @BindView(R.id.edt_write_review)
    EditTextRegular edtWriteReview;
    @BindView(R.id.image_2_left)
    ImageView image2Left;
    @BindView(R.id.img_2_right)
    ImageView img2Right;
    @BindView(R.id.lyt_btn_submit_your_review)
    LinearLayout lytBtnSubmitYourReview;
    @BindView(R.id.card_button)
    CardView cardButton;


    @BindView(R.id.edt_description_counter)
    EditTextRegular edtDescriptionCounter;

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
        }
        taskModel= TaskDetailsActivity.taskModel;


        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        setData();

    }

    private void setData() {
        if (isMyTask) {
            //poster write review
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
            if (taskModel.getPoster().getEmailVerifiedAt() == null) {
                imgVerifiedAccount.setVisibility(View.GONE);
            } else {
                imgVerifiedAccount.setVisibility(View.VISIBLE);
            }
            txtFullName.setText(taskModel.getPoster().getName());
            txtAmount.setText("$ " + taskModel.getAmount());
            txtAccountLevel.setText("Account Level " + taskModel.getPoster().getPosterTier().getId());
            if (taskModel.getPoster().getPosterTier().getId() == 1) {
                imgLevel.setImageDrawable(getResources().getDrawable(R.drawable.ic_level_1));
            } else if (taskModel.getPoster().getPosterTier().getId() == 2) {
                imgLevel.setImageDrawable(getResources().getDrawable(R.drawable.ic_level_2));
            } else if (taskModel.getPoster().getPosterTier().getId() == 3) {
                imgLevel.setImageDrawable(getResources().getDrawable(R.drawable.ic_level_3));
            }
            if (taskModel.getLocation() != null) {
                txtLocation.setText(taskModel.getLocation());
            } else {
                txtLocation.setText("In Person");
            }

            if (taskModel.getPoster().getLocation() != null) {
                txtSuburb.setText(taskModel.getPoster().getLocation());
                txtSuburb.setVisibility(View.VISIBLE);
            } else {
                txtSuburb.setVisibility(View.GONE);
            }
            if (taskModel.getPoster().getPosterRatings() != null) {
                txtReviewCount.setText("(" + taskModel.getPoster().getPosterRatings().getAvgRating() + ")");
            } else {
                txtReviewCount.setText("(0.0)");
            }
            txtCalenderDate.setText(Tools.getDayMonthDateTimeFormat(taskModel.getDueDate().substring(0, 10)));
        } else {
            //worker write review
            if (taskModel.getWorker().getAvatar() != null && taskModel.getWorker().getAvatar().getThumbUrl() != null) {
                ImageUtil.displayImage(imgAvatar, taskModel.getWorker().getAvatar().getThumbUrl(), null);
            } else {
                //deafult image
            }
            if (taskModel.getWorker().getEmailVerifiedAt() == null) {
                imgVerifiedAccount.setVisibility(View.GONE);
            } else {
                imgVerifiedAccount.setVisibility(View.VISIBLE);
            }
            txtFullName.setText(taskModel.getWorker().getName());
            txtAmount.setText("$ " + taskModel.getAmount());
            txtAccountLevel.setText("Account Level " + taskModel.getWorker().getWorkerTier().getId());
            if (taskModel.getWorker().getWorkerTier().getId() == 1) {
                imgLevel.setImageDrawable(getResources().getDrawable(R.drawable.ic_level_1));
            } else if (taskModel.getWorker().getWorkerTier().getId() == 2) {
                imgLevel.setImageDrawable(getResources().getDrawable(R.drawable.ic_level_2));
            } else if (taskModel.getWorker().getWorkerTier().getId() == 3) {
                imgLevel.setImageDrawable(getResources().getDrawable(R.drawable.ic_level_3));
            }
            if (taskModel.getLocation() != null) {
                txtLocation.setText(taskModel.getLocation());
            } else {
                txtLocation.setText("In Person");
            }

            if (taskModel.getWorker().getLocation() != null) {
                txtSuburb.setText(taskModel.getWorker().getLocation());
                txtSuburb.setVisibility(View.VISIBLE);
            } else {
                txtSuburb.setVisibility(View.GONE);
            }
            if (taskModel.getWorker().getPosterRatings() != null) {
                txtReviewCount.setText("(" + taskModel.getWorker().getPosterRatings().getAvgRating() + ")");
            } else {
                txtReviewCount.setText("(0.0)");
            }

            txtCalenderDate.setText(Tools.getDayMonthDateTimeFormat(taskModel.getDueDate().substring(0, 10)));
        }
        ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                txtProgressRatingbar.setText("(" + ratingBar.getRating() + ")");
            }
        });


        edtWriteReview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equalsIgnoreCase("")) {
                    int length = s.length();
                    if (length <= 24) {
                        edtDescriptionCounter.setText(s.length() + "/25+");
                        edtDescriptionCounter.setTextColor(getResources().getColor(R.color.red_600));
                    } else {
                        edtDescriptionCounter.setText(s.length() + "/300");
                        edtDescriptionCounter.setTextColor(getResources().getColor(R.color.green));
                    }
                } else {
                    edtDescriptionCounter.setText("0/25+");
                    edtDescriptionCounter.setTextColor(getResources().getColor(R.color.red_600));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick({R.id.img_avatar, R.id.lyt_btn_submit_your_review})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_avatar:

                break;
            case R.id.lyt_btn_submit_your_review:
                if (validation()) {
                    if (isMyTask) {
                        submitReview(String.valueOf((int) ratingbar.getRating()), edtWriteReview.getText().toString().trim(), Constant.URL_TASKS + "/" + taskModel.getSlug() + "/rating/submit-review");
                    } else {
                        submitReview(String.valueOf((int) ratingbar.getRating()), edtWriteReview.getText().toString().trim(), Constant.URL_TASKS + "/" + taskModel.getSlug() + "/rating/submit-review");
                    }
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
                                onBackPressed();
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
                            //  showCustomDialog(jsonObject_error.getString("message"));
                            if (jsonObject_error.has("message")) {
                                Toast.makeText(LeaveReviewActivity.this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());
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
        Log.e(TAG, stringRequest.getUrl());
    }

    private boolean validation() {
        if (TextUtils.isEmpty(edtWriteReview.getText().toString().trim())) {
            edtWriteReview.setError("Write a review");
            return false;
        }
        return true;
    }


}