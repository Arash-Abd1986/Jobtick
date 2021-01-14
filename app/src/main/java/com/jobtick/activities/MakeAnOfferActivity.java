package com.jobtick.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.fragments.MakeAnOfferAboutFragment;
import com.jobtick.fragments.MakeAnOfferBudgetFragment;
import com.jobtick.fragments.MakeAnOfferMustHaveFragment;
import com.jobtick.fragments.MakeAnOfferReviewFragment;
import com.jobtick.models.MakeAnOfferModel;
import com.jobtick.models.TaskModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.FireBaseEvent;
import com.jobtick.utils.HttpStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MakeAnOfferActivity extends ActivityBase implements MakeAnOfferMustHaveFragment.MustHaveCallbackFunction, MakeAnOfferBudgetFragment.BudgetCallbackFunction, MakeAnOfferAboutFragment.AboutCallbackFunction, MakeAnOfferReviewFragment.ReviewCallbackFunction {

    private static final String TAG = "MakeAnOfferActivity";
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.make_an_offer_layout)
    FrameLayout makeAnOfferLayout;
    MakeAnOfferModel makeAnOfferModel;
    TaskModel taskModel;
    int id = 0, bugdet = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_an_offer);
        ButterKnife.bind(this);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        makeAnOfferModel = new MakeAnOfferModel();
        // taskModel = new TaskModel();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getInt("id");
            bugdet = bundle.getInt("budget");

            makeAnOfferModel.setTask_id(id);
        }
        taskModel = TaskDetailsActivity.taskModel;

        Fragment fragment = MakeAnOfferBudgetFragment.newInstance(makeAnOfferModel, MakeAnOfferActivity.this);
        commit(fragment, MakeAnOfferBudgetFragment.class.getSimpleName());

    }

    private void backButtonBudget() {
        finish();
    }

    @Override
    public void backButtonMustHave() {
        onBackPressed();
    }

    @Override
    public void continueMustHave(MakeAnOfferModel makeAnOfferModel) {
        Fragment fragment = MakeAnOfferBudgetFragment.newInstance(makeAnOfferModel, MakeAnOfferActivity.this);
        commit(fragment, MakeAnOfferBudgetFragment.class.getSimpleName());
    }

    @Override
    public void onBackPressed() {
        //TODO
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.make_an_offer_layout);
        if (currentFragment instanceof MakeAnOfferMustHaveFragment) {
            super.onBackPressed();
        } else if (currentFragment instanceof MakeAnOfferBudgetFragment) {
            if (taskModel.getMusthave() != null && taskModel.getMusthave().size() > 0) {
                backButtonBudget();

            } else {
                super.onBackPressed();

            }
        } else if (currentFragment instanceof MakeAnOfferAboutFragment) {
            backButtonAbout();
        } else {
            backButtonReview(makeAnOfferModel);
        }


    }

    @Override
    public void continueButtonBudget(MakeAnOfferModel makeAnOfferModel) {
        Fragment fragment = MakeAnOfferAboutFragment.newInstance(makeAnOfferModel, MakeAnOfferActivity.this);
        commit(fragment, MakeAnOfferAboutFragment.class.getSimpleName());
    }

    private void commit(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager
                .beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                        android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.make_an_offer_layout, fragment, tag);
        transaction.commit();
    }

    @Override
    public void backButtonAbout() {
        Fragment fragment = MakeAnOfferBudgetFragment.newInstance(makeAnOfferModel, MakeAnOfferActivity.this);
        commit(fragment, MakeAnOfferBudgetFragment.class.getSimpleName());
    }

    @Override
    public void continueButtonAbout(MakeAnOfferModel makeAnOfferModel) {
        Fragment fragment = MakeAnOfferReviewFragment.newInstance(makeAnOfferModel, MakeAnOfferActivity.this);
        commit(fragment, MakeAnOfferReviewFragment.class.getSimpleName());
    }

    @Override
    public void backButtonReview(MakeAnOfferModel makeAnOfferModel) {
        Fragment fragment = MakeAnOfferAboutFragment.newInstance(makeAnOfferModel, MakeAnOfferActivity.this);
        commit(fragment, MakeAnOfferAboutFragment.class.getSimpleName());
    }


    @Override
    public void submitButtonReview(MakeAnOfferModel makeAnOfferModel) {
        submitOffer(makeAnOfferModel);
    }

    private void submitOffer(MakeAnOfferModel makeAnOfferModel) {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_OFFERS,
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {

                                FireBaseEvent.getInstance(getApplicationContext())
                                        .sendEvent(FireBaseEvent.Event.OFFER_SUMMARY,
                                                FireBaseEvent.EventType.API_RESPOND_SUCCESS,
                                                FireBaseEvent.EventValue.OFFER_SUMMARY_SUBMIT_OFFER);

                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(ConstantKey.MAKE_AN_OFFER, true);
                                intent.putExtras(bundle);
                                setResult(ConstantKey.RESULTCODE_MAKEANOFFER, intent);

                                intent = new Intent(MakeAnOfferActivity.this, CompleteMessageActivity.class);
                                Bundle bundle1 = new Bundle();
                                bundle1.putString(ConstantKey.COMPLETES_MESSAGE_TITLE, "Offer Sent Successfully");
                                bundle1.putString(ConstantKey.COMPLETES_MESSAGE_SUBTITLE, "Wait for an answer or continue looking for more tasks!");
                                bundle1.putInt(ConstantKey.COMPLETES_MESSAGE_FROM, ConstantKey.RESULTCODE_MAKEANOFFER);

                                intent.putExtras(bundle1);
                                startActivity(intent);
                                finish();
                            } else {
                                showToast("Something went Wrong", MakeAnOfferActivity.this);
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
                        if (networkResponse.statusCode == 500) {
                            showToast("Something Went Wrong", MakeAnOfferActivity.this);
                            hideProgressDialog();
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);

                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                            if (jsonObject_error.has("message")) {
                                showToast(jsonObject_error.getString("message"), this);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", MakeAnOfferActivity.this);
                    }
                    Timber.e(error.toString());
                    hideProgressDialog();
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();

                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");

                map1.put("X-REQUESTED-WITH", "xmlhttprequest");
//                headers.put("Accept", "application/json");
                // map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<>();
                map1.put("task_id", String.valueOf(makeAnOfferModel.getTask_id()));
                map1.put("offer_price", String.valueOf(makeAnOfferModel.getOffer_price()));
                map1.put("offer_price_type ", makeAnOfferModel.getOffer_price_type());
                if (makeAnOfferModel.getAttachment() != null && makeAnOfferModel.getAttachment().getThumbUrl() != null) {
                    map1.put("attachments[0]", String.valueOf(makeAnOfferModel.getAttachment().getId()));
                } else {
                    map1.put("message", makeAnOfferModel.getMessage());
                }

                Timber.e(String.valueOf(map1.size()));
                Timber.e(map1.toString());
                return map1;

            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(MakeAnOfferActivity.this);
        requestQueue.add(stringRequest);
        Timber.e(stringRequest.getUrl());
    }
}
