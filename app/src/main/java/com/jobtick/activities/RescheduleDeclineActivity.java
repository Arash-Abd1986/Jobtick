package com.jobtick.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

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
import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.utils.Constant;
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

import static com.jobtick.utils.Constant.URL_CREATE_RESCHEDULE;

public class RescheduleDeclineActivity extends ActivityBase {

    private static final String TAG = RescheduleDeclineActivity.class.getName();
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.edt_comments)
    EditTextRegular edtComments;
    @BindView(R.id.lyt_btn_submit)
    LinearLayout lytBtnSubmit;

    private SessionManager sessionManager;

    private int reqeustId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reschedule_decline);
        ButterKnife.bind(this);

        sessionManager = new SessionManager(this);

        Bundle bundle = getIntent().getExtras();
        reqeustId=bundle.getInt("request_id");
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
            RescheduleRequestReject(edtComments.getText().toString().trim());
        }
    }

    private boolean validation() {
        if(TextUtils.isEmpty(edtComments.getText().toString().trim())){
            edtComments.setError("?");
            return false;
        }
        return true;
    }


    public void RescheduleRequestReject(String message) {
//reschedule/:duedateextend/accept
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.BASE_URL + URL_CREATE_RESCHEDULE + "/" + reqeustId+"/reject",
                response -> {
                    Timber.e(response);

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                    showCustomDialog("Request Declined Successfully");

                            } else {
                                showCustomDialog(getString(R.string.server_went_wrong));
                            }
                        } else {
                            showToast(getString(R.string.server_went_wrong), RescheduleDeclineActivity.this);
                        }
                        hideProgressDialog();
                    } catch (JSONException e) {
                        hideProgressDialog();
                        e.printStackTrace();
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
                            hideProgressDialog();
                            try {
                                JSONObject jsonObject = new JSONObject(jsonError);

                                JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                                if (jsonObject_error.has("message")) {
                                    Toast.makeText(RescheduleDeclineActivity.this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                                if (jsonObject_error.has("errors")) {
                                    JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                                }
                                //  ((CredentialActivity)getActivity()).showToast(message,getActivity());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            showToast("Something Went Wrong", RescheduleDeclineActivity.this);
                        }
                        Timber.e(error.toString());
                        hideProgressDialog();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<>();
                map1.put("declined_reason",message);
                return map1;
            }

            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                return map1;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(RescheduleDeclineActivity.this);
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

        ((AppCompatButton) dialog.findViewById(R.id.btn_ok)).setOnClickListener(new View.OnClickListener() {
               @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

}