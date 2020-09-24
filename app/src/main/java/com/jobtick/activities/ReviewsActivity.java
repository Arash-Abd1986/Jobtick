package com.jobtick.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.jobtick.R;
import com.jobtick.TextView.TextViewBold;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.adapers.ReviewAdapter;
import com.jobtick.adapers.TaskListAdapter;
import com.jobtick.models.ReviewModel;
import com.jobtick.models.TaskModel;
import com.jobtick.models.UserAccountModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.CustomToast;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.ImageUtil;
import com.jobtick.utils.SessionManager;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.jobtick.pagination.PaginationListener.PAGE_START;

public class ReviewsActivity extends ActivityBase {

    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;
    private ReviewAdapter reviewAdapter;
    private List<ReviewModel.DataBean> reviewModelList;
    private SessionManager sessionManager;
    private UserAccountModel userAccountModel;


    @BindView(R.id.txt_name)
    TextViewBold txtName;

    @BindView(R.id.txt_reviews_counts)
    TextViewRegular txtReviewCounts;

    @BindView(R.id.recyclerview_review)
    RecyclerView recyclerReview;

    @BindView(R.id.txt_completion_rate)
    TextViewRegular txtComplettionRate;

    @BindView(R.id.txt_rating_values)
    TextViewBold txtRatingValue;


    @BindView(R.id.ratingbar)
    AppCompatRatingBar ratingBar;


    @BindView(R.id.progress_bar_5_star)
    ProgressBar progress_bar_5_star;


    @BindView(R.id.progress_bar_4_star)
    ProgressBar progress_bar_4_star;

    @BindView(R.id.progress_bar_3_star)
    ProgressBar progress_bar_3_star;

    @BindView(R.id.progress_bar_2_star)
    ProgressBar progress_bar_2_star;

    @BindView(R.id.ratingbar_1_star)
    ProgressBar ratingbar_1_star;

    @BindView(R.id.progress_bar_1_star)
    ProgressBar progress_bar_1_star;

    public String WhoIs;

    @BindView(R.id.txt_review_count_5_star)
    TextViewRegular txt_review_count_5_star;

    @BindView(R.id.txt_review_count_4_star)
    TextViewRegular txt_review_count_4_star;


    @BindView(R.id.txt_review_count_3_star)
    TextViewRegular txt_review_count_3_star;


    @BindView(R.id.txt_review_count_2_star)
    TextViewRegular txt_review_count_2_star;

    @BindView(R.id.txt_review_count_1_star)
    TextViewRegular txt_review_count_1_star;

    @BindView(R.id.img_verified_account)
    ImageView img_verified_account;

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    @BindView(R.id.img_avatar)
    CircularImageView imgAvatar;
    int userId;


    public ReviewsActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        sessionManager = new SessionManager(ReviewsActivity.this);

        if (bundle != null) {
            if (bundle.containsKey(Constant.userID)) {
                userId = bundle.getInt(Constant.userID);
            }
            WhoIs = bundle.getString("WhoIs");
            if(bundle.containsKey(Constant.userAccount)) {
                userAccountModel = bundle.getParcelable(Constant.userAccount);
            }else
            {
                userAccountModel = sessionManager.getUserAccount();
            }
        }
        initpDialog();
        init();
        getReviewList();
    }


    public void init() {

        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        /*

        */
        LinearLayoutManager layoutManager = new LinearLayoutManager(ReviewsActivity.this);
        recyclerReview.setLayoutManager(layoutManager);
        reviewAdapter = new ReviewAdapter(ReviewsActivity.this, new ArrayList<>());
        recyclerReview.setAdapter(reviewAdapter);
        //  reviewAdapter.setOnItemClickListener(this);
        reviewModelList = new ArrayList<>();

        setData();
    }

    public void setData() {

        if (userAccountModel.getIsVerifiedAccount() == 1) {
            img_verified_account.setVisibility(View.VISIBLE);
        } else {
            img_verified_account.setVisibility(View.GONE);
        }

        if (userAccountModel.getAvatar() != null) {
            ImageUtil.displayImage(imgAvatar, userAccountModel.getAvatar().getThumbUrl(), null);
        }
        txtName.setText(userAccountModel.getName());
        if (WhoIs.equals(Constant.AS_A_POSTER)) {

            if (userAccountModel.getPosterRatings() != null && userAccountModel.getPosterRatings().getAvgRating() != null) {
                ratingBar.setProgress(userAccountModel.getPosterRatings().getAvgRating());
                txtRatingValue.setText("(" + userAccountModel.getPosterRatings().getAvgRating() + ")");
            }
            if (userAccountModel.getPostTaskStatistics() != null && userAccountModel.getPostTaskStatistics().getCompletionRate() != null) {
                txtComplettionRate.setText(userAccountModel.getPostTaskStatistics().getCompletionRate() + "% Completion Rate");
            }

            if (userAccountModel.getPosterRatings() != null &&
                    userAccountModel.getPosterRatings().getReceivedReviews() != null) {
                if (userAccountModel.getPosterRatings().getReceivedReviews() <= 1) {
                    txtReviewCounts.setText(userAccountModel.getPosterRatings().getReceivedReviews() + " review");
                } else {
                    txtReviewCounts.setText(userAccountModel.getPosterRatings().getReceivedReviews() + " reviews");
                }
            }

            if (userAccountModel.getPosterRatings() != null && userAccountModel.getPosterRatings().get1() != null) {
                progress_bar_1_star.setProgress(userAccountModel.getPosterRatings().get1());
                txt_review_count_1_star.setText("(" + userAccountModel.getPosterRatings().get1().toString() + ")");
            } else {
                progress_bar_1_star.setProgress(0);
                txt_review_count_1_star.setText("(0)");

            }

            if (userAccountModel.getPosterRatings() != null && userAccountModel.getPosterRatings().get2() != null) {
                progress_bar_2_star.setProgress(userAccountModel.getPosterRatings().get2());
                txt_review_count_2_star.setText("(" + userAccountModel.getPosterRatings().get2().toString() + ")");

            } else {
                progress_bar_2_star.setProgress(0);
                txt_review_count_2_star.setText("(0)");
            }

            if (userAccountModel.getPosterRatings() != null && userAccountModel.getPosterRatings().get3() != null) {
                progress_bar_3_star.setProgress(userAccountModel.getPosterRatings().get3());
                txt_review_count_3_star.setText("(" + userAccountModel.getPosterRatings().get3().toString() + ")");

            } else {
                progress_bar_3_star.setProgress(0);
                txt_review_count_3_star.setText("(0)");

            }
            if (userAccountModel.getPosterRatings() != null && userAccountModel.getPosterRatings().get4() != null) {
                progress_bar_4_star.setProgress(userAccountModel.getPosterRatings().get4());
                txt_review_count_4_star.setText("(" + userAccountModel.getPosterRatings().get4().toString() + ")");

            } else {
                progress_bar_4_star.setProgress(0);
                txt_review_count_4_star.setText("(0)");


            }
            if (userAccountModel.getPosterRatings() != null && userAccountModel.getPosterRatings().get5() != null) {
                progress_bar_5_star.setProgress(userAccountModel.getPosterRatings().get5());
                txt_review_count_5_star.setText("(" + userAccountModel.getPosterRatings().get5().toString() + ")");

            } else {
                progress_bar_5_star.setProgress(0);
                txt_review_count_5_star.setText("(0)");

            }


        } else {


            if (userAccountModel.getWorkerRatings() != null && userAccountModel.getWorkerRatings().getAvgRating() != null) {
                ratingBar.setProgress(userAccountModel.getWorkerRatings().getAvgRating());
                txtRatingValue.setText("(" + userAccountModel.getWorkerRatings().getAvgRating() + ")");
            }
            if (userAccountModel.getWorkTaskStatistics() != null && userAccountModel.getWorkTaskStatistics().getCompletionRate() != null) {
                txtComplettionRate.setText(userAccountModel.getWorkTaskStatistics().getCompletionRate() + "% Completion Rate");
            }
            if (userAccountModel.getWorkerRatings() != null
                    && userAccountModel.getWorkerRatings().getReceivedReviews() != null) {

                if (userAccountModel.getWorkerRatings().getReceivedReviews() <= 1) {
                    txtReviewCounts.setText(userAccountModel.getWorkerRatings().getReceivedReviews() + " review");
                } else {
                    txtReviewCounts.setText(userAccountModel.getWorkerRatings().getReceivedReviews() + " reviews");
                }
            }


            if (userAccountModel.getWorkerRatings() != null && userAccountModel.getWorkerRatings().get1() != null) {
                progress_bar_1_star.setProgress(userAccountModel.getWorkerRatings().get1());
                txt_review_count_1_star.setText("(" + userAccountModel.getWorkerRatings().get1().toString() + ")");

            } else {
                progress_bar_1_star.setProgress(0);
                txt_review_count_1_star.setText("(0)");

            }
            if (userAccountModel.getWorkerRatings() != null && userAccountModel.getWorkerRatings().get2() != null) {
                progress_bar_2_star.setProgress(userAccountModel.getWorkerRatings().get2());
                txt_review_count_2_star.setText("(" + userAccountModel.getWorkerRatings().get2().toString() + ")");

            } else {
                txt_review_count_2_star.setText("(0)");
                progress_bar_2_star.setProgress(0);
            }

            if (userAccountModel.getWorkerRatings() != null && userAccountModel.getWorkerRatings().get3() != null) {
                txt_review_count_3_star.setText("(" + userAccountModel.getWorkerRatings().get3().toString() + ")");

                progress_bar_3_star.setProgress(userAccountModel.getWorkerRatings().get3());
            } else {
                progress_bar_3_star.setProgress(0);
                txt_review_count_3_star.setText("(0)");


            }

            if (userAccountModel.getWorkerRatings() != null && userAccountModel.getWorkerRatings().get4() != null) {
                progress_bar_4_star.setProgress(userAccountModel.getWorkerRatings().get4());
                txt_review_count_4_star.setText("(" + userAccountModel.getWorkerRatings().get4().toString() + ")");
            } else {
                progress_bar_4_star.setProgress(0);
                txt_review_count_4_star.setText("(0)");

            }

            if (userAccountModel.getWorkerRatings() != null && userAccountModel.getWorkerRatings().get5() != null) {
                progress_bar_5_star.setProgress(userAccountModel.getWorkerRatings().get5());
                txt_review_count_5_star.setText("(" + userAccountModel.getWorkerRatings().get5().toString() + ")");

            } else {
                progress_bar_5_star.setProgress(0);
                txt_review_count_5_star.setText("(0)");


            }

        }

    }


    public void getReviewList() {
        showpDialog();

        //profile/:user_id/reviews/:ratee_type
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_PROFILE + "/" + userId + "/reviews/" + WhoIs,
                response -> {
                    // categoryArrayList.clear();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("data") && !jsonObject.isNull("data")) {


                            String jsonString = jsonObject.toString(); //http request
                            ReviewModel data = new ReviewModel();
                            Gson gson = new Gson();
                            data = gson.fromJson(jsonString, ReviewModel.class);


                            for (int i = 0; i < data.getData().size(); i++) {
                                reviewModelList.add(data.getData().get(i));
                            }

                        } else {
                            ReviewsActivity.this.showToast("something went to wrong", ReviewsActivity.this);
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
                            reviewAdapter.removeLoading();

                        if (reviewModelList.size() <= 0) {
                            //  ivNoPost.setVisibility(View.VISIBLE);
                            recyclerReview.setVisibility(View.GONE);
                        } else {
                            // ivNoPost.setVisibility(View.GONE);
                            recyclerReview.setVisibility(View.VISIBLE);

                        }
                        reviewAdapter.addItems(reviewModelList);

                        //  swipeRefresh.setRefreshing(false);
                        // check weather is last page or not
                        if (currentPage < totalPage) {
                            reviewAdapter.addLoading();
                        } else {
                            isLastPage = true;
                        }
                        isLoading = false;
                        hidepDialog();
                    } catch (JSONException e) {
                        //str_search = null;
                        //  dashboardActivity.hidepDialog();
                        Log.e("EXCEPTION", String.valueOf(e));
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
                            hidepDialog();
                            return;
                        }
                        try {
                            hidepDialog();
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                            showCustomDialog(jsonObject_error.getString("message"));
                               /* if (jsonObject_error.has("message")) {
                                    Toast.makeText(AddCouponPaymentOverviewActivity.this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                                if (jsonObject_error.has("errors")) {
                                    JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                                }*/
                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", ReviewsActivity.this);
                    }
                    Timber.e(error.toString());
                    hidepDialog();

                       /*str_search = null;
                    swipeRefresh.setRefreshing(false);
                    dashboardActivity.errorHandle1(error.networkResponse);*/
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
        RequestQueue requestQueue = Volley.newRequestQueue(ReviewsActivity.this);
        requestQueue.add(stringRequest);
        Log.e("url", stringRequest.getUrl());


    }


    public void showToast(String content, Context context) {
        CustomToast toast = new CustomToast(context);
        toast.setCustomView(content);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
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

        ((AppCompatButton) dialog.findViewById(R.id.btn_ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

}