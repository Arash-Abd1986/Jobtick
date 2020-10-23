package com.jobtick.cancellations;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.R;
import com.jobtick.TextView.TextViewBold;
import com.jobtick.activities.ActivityBase;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class CancellationAcceptedActivity extends ActivityBase {

    private static final String TAG = CancellationAcceptedActivity.class.getName();
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.txt_title)
    TextViewBold txtTitle;
    @BindView(R.id.txt_cancellation_reason)
    TextViewBold txtCancellationReason;
    @BindView(R.id.lyt_btn_no)
    LinearLayout lytBtnNo;
    @BindView(R.id.lyt_btn_yes)
    LinearLayout lytBtnYes;
    @BindView(R.id.lyt_bottom)
    LinearLayout lytBottom;

    private SessionManager sessionManager;
    // private TaskModel taskModel;
    private Boolean isMyTask = false;
    private int cancellationID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancellation_accepted);
        ButterKnife.bind(this);


        sessionManager = new SessionManager(this);

        Bundle bundle = getIntent().getExtras();

            cancellationID = bundle.getInt(ConstantKey.KEY_TASK_CANCELLATION_ID);
            isMyTask = bundle.getBoolean(ConstantKey.IS_MY_TASK);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @OnClick({R.id.lyt_btn_no, R.id.lyt_btn_yes})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lyt_btn_no:
                onBackPressed();

                break;
            case R.id.lyt_btn_yes:
                cancellationAccept();
                break;
        }
    }

    private void cancellationAccept() {
        showpDialog();
        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, Constant.BASE_URL + "cancellation/" + cancellationID + "/accept",

                response -> {
                    Timber.e(response);
                    hidepDialog();
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {

                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(ConstantKey.ACCEPT, true);
                                intent.putExtras(bundle);
                                setResult(ConstantKey.RESULTCODE_CANCELLATION_ACCEPT, intent);
                                onBackPressed();
                            } else {
                                showToast("Something went Wrong", CancellationAcceptedActivity.this);
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
                            hidepDialog();
                            return;

                        }
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);

                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                            if (jsonObject_error.has("message")) {
                                Toast.makeText(CancellationAcceptedActivity.this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", CancellationAcceptedActivity.this);
                    }
                    Timber.e(error.toString());
                    hidepDialog();
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
        RequestQueue requestQueue = Volley.newRequestQueue(CancellationAcceptedActivity.this);
        requestQueue.add(stringRequest);
        Log.e(TAG, stringRequest.getUrl());
    }


}