package com.jobtick.incrementbudget;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.edit_text.EditTextRegular;
import com.jobtick.R;
import com.jobtick.text_view.TextViewRegular;
import com.jobtick.activities.ActivityBase;
import com.jobtick.activities.CompleteMessageActivity;
import com.jobtick.models.TaskModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static com.jobtick.utils.Constant.URL_ADDITIONAL_FUND;

public class IncrementBudgetDeclineActivity extends ActivityBase {

    private static final String TAG = IncrementBudgetDeclineActivity.class.getName();
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.edt_comments)
    EditTextRegular edtComments;
    @BindView(R.id.lyt_btn_submit)
    LinearLayout lytBtnSubmit;

    private SessionManager sessionManager;
    private TaskModel taskModel;
    private Boolean isMyTask = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancellation_decline);
        ButterKnife.bind(this);

        sessionManager = new SessionManager(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle.getParcelable(ConstantKey.TASK) != null) {
            taskModel = bundle.getParcelable(ConstantKey.TASK);
            isMyTask = bundle.getBoolean(ConstantKey.IS_MY_TASK);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    @OnClick(R.id.lyt_btn_submit)
    public void onViewClicked() {
        if(validation()){
            rejectRequest(String.valueOf(taskModel.getAdditionalFund().getId()),edtComments.getText().toString().trim());
        }
    }

    private boolean validation() {
        if(TextUtils.isEmpty(edtComments.getText().toString().trim())){
            edtComments.setError("?");
            return false;
        }
        return true;
    }

    private void rejectRequest(String id, String rejectReason) {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, Constant.BASE_URL + URL_ADDITIONAL_FUND + "/" + id + "/reject",
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                Intent intent = new Intent(IncrementBudgetDeclineActivity.this, CompleteMessageActivity.class);
                                Bundle bundle1 = new Bundle();
                                bundle1.putString(ConstantKey.COMPLETES_MESSAGE_TITLE, "Request Declined");
                                bundle1.putString(ConstantKey.COMPLETES_MESSAGE_SUBTITLE, "Budget Request has been Declined !");
                                bundle1.putInt(ConstantKey.COMPLETES_MESSAGE_FROM, ConstantKey.RESULTCODE_INCREASE_BUDGET);

                                intent.putExtras(bundle1);
                                startActivity(intent);
                                finish();
                            } else {
                                showToast("Something went Wrong", IncrementBudgetDeclineActivity.this);
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
                        Log.e("error", jsonError);
                        if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                            unauthorizedUser();
                            hideProgressDialog();
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);


                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");


                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                                if (jsonObject_errors.has("amount") && !jsonObject_errors.isNull("amount")) {
                                    JSONArray jsonArray_amount = jsonObject_errors.getJSONArray("amount");
                                    showCustomDialog(jsonArray_amount.getString(0));
                                } else if (jsonObject_errors.has("creation_reason") && !jsonObject_errors.isNull("creation_reason")) {
                                    JSONArray jsonArray_amount = jsonObject_errors.getJSONArray("creation_reason");
                                    showCustomDialog(jsonArray_amount.getString(0));
                                }
                            } else {
                                if (jsonObject_error.has("message")) {
                                    showCustomDialog(jsonObject_error.getString("message"));
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", IncrementBudgetDeclineActivity.this);
                    }
                    Timber.e(error.toString());
                    hideProgressDialog();
                }) {


            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<String, String>();

                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();

                map1.put("rejection_reason", rejectReason);
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(IncrementBudgetDeclineActivity.this);
        requestQueue.add(stringRequest);
        Log.e(TAG, stringRequest.getUrl());

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