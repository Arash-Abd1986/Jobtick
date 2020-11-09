package com.jobtick.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.models.TaskModel;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static com.jobtick.utils.Constant.URL_CREATE_RESCHEDULE;
import static com.jobtick.utils.Constant.URL_TASKS;

public class RescheduleReqFromWorkerActivity extends ActivityBase {


    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    @BindView(R.id.txt_date)
    TextViewRegular txtDate;

    @BindView(R.id.txt_previous_date)
    TextViewRegular txtPreviousDate;

    int year, month, day;
    DatePickerDialog.OnDateSetListener mDateSetListener;
    String str_due_date = null;


    private TaskModel taskModel;


    @BindView(R.id.edt_note)
    EditTextRegular edtNote;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reschedulereq_from_worker);

        ButterKnife.bind(this);
        initToolbar();
        init();
    }


    private void init() {

        /*taskModel = new TaskModel();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            taskModel = bundle.getParcelable(ConstantKey.TASK);
        }*/
        taskModel= TaskDetailsActivity.taskModel;

        if (taskModel != null) {
            txtPreviousDate.setText(taskModel.getDueDate());
        }
        mDateSetListener = (view, year, month, dayOfMonth) -> {
            month = month + 1;
            str_due_date = Tools.getDayMonthDateTimeFormat(year + "-" + month + "-" + dayOfMonth);
            txtDate.setText(str_due_date);
        };
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Reschedule time");
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @OnClick({R.id.txt_date, R.id.lyt_btn_verify, R.id.lyt_btn_decline})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_date:
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        RescheduleReqFromWorkerActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                        mDateSetListener,
                        year, month, day);
                //   dialog.getDatePicker().setMinDate(((System.currentTimeMillis() - 1000)+86400));
                Toast.makeText(RescheduleReqFromWorkerActivity.this, "" + (System.currentTimeMillis() - 1000), Toast.LENGTH_SHORT).show();
                dialog.getDatePicker().setMinDate(((System.currentTimeMillis() - 1000) + 86400000));
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                break;
            case R.id.lyt_btn_verify:
                CreateRequest();
                break;
            case R.id.lyt_btn_decline:
                finish();
                break;
        }
    }


    private void CreateRequest() {

        ///{{baseurl}}/tasks/:task_slug/reschedule

        showProgressDialog();
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, URL_TASKS + "/" + taskModel.getSlug() + "/" + URL_CREATE_RESCHEDULE,
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                finish();
                            } else {
                                showToast("Something went Wrong", RescheduleReqFromWorkerActivity.this);
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
                                // Toast.makeText(RescheduleReqFromWorkerActivity.this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                                showCustomDialog(jsonObject_error.getString("message"));


                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", RescheduleReqFromWorkerActivity.this);
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
            protected Map<String, String> getParams() {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("reason", edtNote.getText().toString());
                map1.put("new_duedate", Tools.getApplicationFromatToServerFormat(txtDate.getText().toString()));
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(RescheduleReqFromWorkerActivity.this);
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

        dialog.findViewById(R.id.btn_ok).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

}
