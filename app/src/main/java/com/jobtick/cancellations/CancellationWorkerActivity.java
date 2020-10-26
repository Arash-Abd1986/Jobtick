package com.jobtick.cancellations;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.EditText.EditTextMedium;
import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;
import com.jobtick.TextView.TextViewBold;
import com.jobtick.activities.ActivityBase;
import com.jobtick.activities.TaskDetailsActivity;
import com.jobtick.models.CancellationReasonModel;
import com.jobtick.models.TaskModel;
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

public class CancellationWorkerActivity extends ActivityBase {

    private String TAG = CancellationWorkerActivity.class.getName();
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.rb_reason_1)
    RadioButton rbReason1;
    @BindView(R.id.rb_reason_2)
    RadioButton rbReason2;
    @BindView(R.id.rb_reason_3)
    RadioButton rbReason3;
    @BindView(R.id.rb_reason_4)
    RadioButton rbReason4;
    @BindView(R.id.rb_reason_5)
    RadioButton rbReason5;
    @BindView(R.id.rg_reason_message)
    RadioGroup rgReasonMessage;
    @BindView(R.id.edt_comments)
    EditTextMedium edtComments;
    @BindView(R.id.txt_cancellation_fee)
    TextViewBold txtCancellationFee;
    @BindView(R.id.lyt_btn_submit)
    LinearLayout lytBtnSubmit;
    @BindView(R.id.card_btn_submit)
    CardView cardBtnSubmit;
    @BindView(R.id.edt_description_counter)
    EditTextRegular edtDescriptionCounter;

    private String str_SLUG = null;
    private TaskModel taskModel;
    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancellation_worker);
        ButterKnife.bind(this);

        sessionManager = new SessionManager(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        taskModel= TaskDetailsActivity.taskModel;

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if(bundle.getString(ConstantKey.SLUG) != null){
                str_SLUG = bundle.getString(ConstantKey.SLUG);
            }
            /*if (bundle.getParcelable(ConstantKey.TASK) != null) {
                taskModel = bundle.getParcelable(ConstantKey.TASK);
            }*/
        }
        getCancellationReasonList();
        edtComments.addTextChangedListener(new TextWatcher() {
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
                        edtDescriptionCounter.setText(s.length() + "/100");
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

    @OnClick(R.id.lyt_btn_submit)
    public void onViewClicked() {
        String str_comment = null;
        if(TextUtils.isEmpty(edtComments.getText().toString().trim())) {
            str_comment = edtComments.getText().toString().trim();
        }
        String str_reason = null;
        if(rbReason1.isChecked()){
            str_reason = rbReason1.getText().toString().trim();
        }else if(rbReason2.isChecked()){
            str_reason = rbReason2.getText().toString().trim();
        }else if(rbReason3.isChecked()){
            str_reason = rbReason3.getText().toString().trim();
        }else if(rbReason4.isChecked()){
            str_reason = rbReason4.getText().toString().trim();
        }else if(rbReason5.isChecked()){
            str_reason = rbReason5.getText().toString().trim();
        }

        cancellationSubmit(str_reason, str_comment);



    }

    private void getCancellationReasonList() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, Constant.URL_CANCELLATION + "/reasons",
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                if(jsonObject.has("data") && !jsonObject.isNull("data")){
                                    JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                                    CancellationReasonModel cancellationReasonModel = new CancellationReasonModel(taskModel).getJsonTOModel(jsonObject_data);
                                    setReasons(cancellationReasonModel);
                                    getNoticeList();
                                }
                            } else {
                                showToast("Something went Wrong", CancellationWorkerActivity.this);
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
                                Toast.makeText(CancellationWorkerActivity.this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", CancellationWorkerActivity.this);
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
        RequestQueue requestQueue = Volley.newRequestQueue(CancellationWorkerActivity.this);
        requestQueue.add(stringRequest);
        Log.e(TAG, stringRequest.getUrl());
    }


    private void getNoticeList() {
        showProgressDialog();

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
                                    JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                                    txtCancellationFee.setText("$ " + jsonObject_data.getString("max_fee_amount"));

                                }
                            } else {
                                showToast("Something went Wrong", CancellationWorkerActivity.this);
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
                                Toast.makeText(CancellationWorkerActivity.this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", CancellationWorkerActivity.this);
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
        RequestQueue requestQueue = Volley.newRequestQueue(CancellationWorkerActivity.this);
        requestQueue.add(stringRequest);
        Log.e(TAG, stringRequest.getUrl());

    }





    private void setReasons(CancellationReasonModel cancellationReasonModel) {
        for(int i = 0 ; cancellationReasonModel.getWorker().size() > i ; i++){
            switch (i){
                case 0:
                    rbReason1.setText(cancellationReasonModel.getWorker().get(i));
                    break;
                case 1:
                    rbReason2.setText(cancellationReasonModel.getWorker().get(i));
                    break;
                case 2:
                    rbReason3.setText(cancellationReasonModel.getWorker().get(i));
                    break;
                case 3:
                    rbReason4.setText(cancellationReasonModel.getWorker().get(i));
                    break;
                case 4:
                    rbReason5.setText(cancellationReasonModel.getWorker().get(i));
                    break;

            }
        }
    }
    private void cancellationSubmit(String str_reason, String str_comment) {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, Constant.URL_TASKS+ "/"+str_SLUG+ "/cancellation",
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
                                bundle.putBoolean(ConstantKey.CANCELLATION, true);
                                intent.putExtras(bundle);
                                setResult(ConstantKey.RESULTCODE_CANCELLATION, intent);

                                intent = new Intent(CancellationWorkerActivity.this, CancellationSubmitedActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                showToast("Something went Wrong", CancellationWorkerActivity.this);
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
                                Toast.makeText(CancellationWorkerActivity.this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");


                            }

                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", CancellationWorkerActivity.this);
                    }
                    Timber.e(error.toString());
                    hideProgressDialog();
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();

                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                // map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<>();
                map1.put("reason", str_reason);
                if(str_comment != null) {
                    map1.put("comment", str_comment);
                }
                Timber.e(String.valueOf(map1.size()));
                Timber.e(map1.toString());
                return map1;

            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(CancellationWorkerActivity.this);
        requestQueue.add(stringRequest);
        Log.e(TAG, stringRequest.getUrl());
    }

}