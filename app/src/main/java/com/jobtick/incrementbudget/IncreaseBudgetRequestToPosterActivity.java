package com.jobtick.incrementbudget;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.widget.AppCompatButton;
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
import com.jobtick.TextView.TextViewMedium;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.TextView.TextViewSemiBold;
import com.jobtick.activities.ActivityBase;
import com.jobtick.activities.TaskDetailsActivity;
import com.jobtick.models.TaskModel;
import com.jobtick.models.UserAccountModel;
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

import static com.jobtick.utils.Constant.URL_BUDGET_Increment;

public class IncreaseBudgetRequestToPosterActivity extends ActivityBase {


    private static final String TAG = IncreaseBudgetFromPosterActivity.class.getName();

    protected ProgressDialog pDialog;
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.img_icon)
    ImageView imgIcon;
    @BindView(R.id.rb_fixed)
    RadioButton rbFixed;
    @BindView(R.id.rb_hourly)
    RadioButton rbHourly;
    @BindView(R.id.rg_hourly_fixed)
    RadioGroup rgHourlyFixed;
    @BindView(R.id.txt_doller_us)
    TextViewSemiBold txtDollerUs;
    @BindView(R.id.edt_budget)
    EditTextRegular edtBudget;
    @BindView(R.id.card_fixed_budget)
    CardView cardFixedBudget;
    @BindView(R.id.txt_doller_us_hourly)
    TextViewSemiBold txtDollerUsHourly;
    @BindView(R.id.edt_hourly_rate)
    EditTextRegular edtHourlyRate;
    @BindView(R.id.edt_hour)
    EditTextRegular edtHour;
    @BindView(R.id.lyt_hourly)
    LinearLayout lytHourly;
    @BindView(R.id.edt_note)
    EditTextRegular edtNote;
    @BindView(R.id.txt_text_job_cost)
    TextViewMedium txtTextJobCost;
    @BindView(R.id.txt_hourly_budget_show)
    TextViewMedium txtHourlyBudgetShow;
    @BindView(R.id.txt_job_cost)
    TextViewBold txtJobCost;
    @BindView(R.id.txt_service_fee)
    TextViewBold txtServiceFee;
    @BindView(R.id.txt_total_cost)
    TextViewBold txtTotalCost;
    @BindView(R.id.lyt_btn_request_payment)
    LinearLayout lytBtnRequestPayment;
    @BindView(R.id.card_button)
    CardView cardButton;

    private TaskModel taskModel;
    private int total_budget = 0;
    private UserAccountModel userAccountModel;
    private SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_increase_budget_request_to_poster);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(this);
        userAccountModel = sessionManager.getUserAccount();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        txtHourlyBudgetShow.setVisibility(View.GONE);
        radioBtnClick();

        /*taskModel = new TaskModel();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            taskModel = bundle.getParcelable(ConstantKey.TASK);
        }*/
        taskModel= TaskDetailsActivity.taskModel;



        edtBudget.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    setupBudget(Integer.parseInt(s.toString()));

                }

            }
        });

    }


    private void setupBudget(int budget) {
        float worker_service_fee = taskModel.getWorker().getWorkerTier().getServiceFee();
        float service_fee = ((budget * worker_service_fee) / 100);
        txtServiceFee.setText("$ " + service_fee);
        total_budget = (int) (budget - ((budget * worker_service_fee) / 100));
        txtTotalCost.setText("$ " + total_budget);
    }


    private void radioBtnClick() {
        rgHourlyFixed.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb_btn = (RadioButton) findViewById(checkedId);
                if (rb_btn.getText().equals("Fixed")) {
                    cardFixedBudget.setVisibility(View.VISIBLE);
                    lytHourly.setVisibility(View.GONE);
                    txtHourlyBudgetShow.setVisibility(View.GONE);
                    if (edtBudget.getText().toString().trim().length() != 0) {
                        setjobCost(Integer.parseInt(edtBudget.getText().toString().trim()));
                    } else {
                        txtJobCost.setText("$ 0");
                    }
                } else {
                    cardFixedBudget.setVisibility(View.GONE);
                    lytHourly.setVisibility(View.VISIBLE);
                    txtHourlyBudgetShow.setVisibility(View.VISIBLE);
                    if (edtHourlyRate.getText().toString().trim().length() != 0 && edtHour.getText().toString().trim().length() != 0) {
                        txtTextJobCost.setText("Job Cost " + "(" + Integer.parseInt(edtHourlyRate.getText().toString().trim()) + "*" + Integer.parseInt(edtHour.getText().toString().trim()) + ")");
                        setjobCost(Integer.parseInt(edtHour.getText().toString().trim()) * Integer.parseInt(edtHourlyRate.getText().toString().trim()));
                    } else {
                        txtTextJobCost.setText("Job Cost");
                        txtJobCost.setText("$ 0");
                    }
                }
            }
        });
    }

    private void setjobCost(int budget) {
        txtJobCost.setText("$ " + budget);
    }


    @OnClick(R.id.lyt_btn_request_payment)
    public void onViewClicked() {
        if (validation()) {
            submitIncreaseBudget(edtBudget.getText().toString(), edtNote.getText().toString().trim());
        }
    }

    private boolean validation() {
        if (TextUtils.isEmpty(edtBudget.getText().toString().trim())) {
            edtBudget.setError("?");
            return false;
        } else if (Integer.parseInt(edtBudget.getText().toString().trim()) < 4) {
            edtBudget.setError("min. $5");
            return false;
        } else if (Integer.parseInt(edtBudget.getText().toString().trim()) > 500) {
            edtBudget.setError("max. $500");
            return false;
        } else if (TextUtils.isEmpty(edtNote.getText().toString().trim())) {
            edtNote.setError("?");
            return false;
        }
        return true;
    }

    public void initpDialog() {
        pDialog = new ProgressDialog(this);
        pDialog.setTitle(getString(R.string.processing));
        pDialog.setMessage(getString(R.string.please_wait));
        pDialog.setCancelable(false);
    }


    public void showpDialog() {

        if (!pDialog.isShowing())
            pDialog.show();
    }

    public void hidepDialog() {

        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    private void submitIncreaseBudget(String increase_budget, String increase_budget_reason) {
        showpDialog();
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, Constant.URL_TASKS + "/" + taskModel.getSlug() + URL_BUDGET_Increment,
                response -> {
                    Timber.e(response);
                    hidepDialog();
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        Log.e("json", jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {

                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(ConstantKey.INCREASE_BUDGET, true);
                                intent.putExtras(bundle);
                                setResult(ConstantKey.RESULTCODE_INCREASE_BUDGET, intent);
                                onBackPressed();
                            } else {
                                showToast("Something went Wrong", IncreaseBudgetRequestToPosterActivity.this);
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
                            hidepDialog();
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
                        showToast("Something Went Wrong", IncreaseBudgetRequestToPosterActivity.this);
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

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<>();
                map1.put("amount", increase_budget);
                map1.put("creation_reason", increase_budget_reason);

                Timber.e(String.valueOf(map1.size()));
                Timber.e(map1.toString());
                return map1;

            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(IncreaseBudgetRequestToPosterActivity.this);
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