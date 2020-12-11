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
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.R;
import com.jobtick.text_view.TextViewRegular;
import com.jobtick.models.TaskModel;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.TimeHelper;
import com.jobtick.utils.Tools;
import com.jobtick.widget.ExtendedCommentText;
import com.jobtick.widget.ExtendedEntryText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static com.jobtick.utils.Constant.MAX_RESCHEDULE_DAY;
import static com.jobtick.utils.Constant.URL_CREATE_RESCHEDULE;
import static com.jobtick.utils.Constant.URL_TASKS;

public class RescheduleTimeRequestActivity extends ActivityBase implements ExtendedEntryText.ExtendedViewOnClickListener{


    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    @BindView(R.id.txt_date)
    ExtendedEntryText txtDate;

    @BindView(R.id.txt_previous_date)
    TextView txtPreviousDate;

    @BindView(R.id.txt_previous_time)
    TextView getTxtPreviousTime;

    int year, month, day;
    long dueDate;
    DatePickerDialog.OnDateSetListener mDateSetListener;
    String str_due_date = null;


    private TaskModel taskModel;


    @BindView(R.id.edt_note)
    ExtendedCommentText edtNote;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reschedulereq_from_worker);

        ButterKnife.bind(this);
        initToolbar();
        init();
    }


    private void init() {

        taskModel = new TaskModel();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
        //    taskModel = bundle.getParcelable(ConstantKey.TASK);
            taskModel = TaskDetailsActivity.taskModel;
        }

        if (taskModel != null) {
            //format 2021-01-07
            txtPreviousDate.setText(taskModel.getDueDate());
            dueDate = TimeHelper.convertDateToLong(taskModel.getDueDate());
            if(taskModel.getDueTime().getAfternoon())
                getTxtPreviousTime.setText("Afternoon");
            if(taskModel.getDueTime().getEvening())
                getTxtPreviousTime.setText("Evening");
            if(taskModel.getDueTime().getMorning())
                getTxtPreviousTime.setText("Morning");
            if(taskModel.getDueTime().getMidday())
                getTxtPreviousTime.setText("Anytime");
        }
        mDateSetListener = (view, year, month, dayOfMonth) -> {
            month = month + 1;
            str_due_date = Tools.getDayMonthDateTimeFormat(year + "-" + month + "-" + dayOfMonth);
            txtDate.setText(str_due_date);
        };
        txtDate.setExtendedViewOnClickListener(this);
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_cancel);
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


    @OnClick({R.id.lyt_btn_verify, R.id.lyt_btn_decline})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lyt_btn_verify:
                if(validation())
                    CreateRequest();
                break;
            case R.id.lyt_btn_decline:
                finish();
                break;
        }
    }

    private boolean validation(){
        if(txtDate.getText().length() == 0){
            txtDate.setError("Please enter new date.");
            return false;
        }
        if(edtNote.getText().length() < edtNote.geteMinSize()){
            edtNote.setError("");
            return false;
        }
        return true;
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
                                showToast("Something went Wrong", RescheduleTimeRequestActivity.this);
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
                        showToast("Something Went Wrong", RescheduleTimeRequestActivity.this);
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
                map1.put("reason", edtNote.getText());
                map1.put("new_duedate", Tools.getApplicationFromatToServerFormat(txtDate.getText()));
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(RescheduleTimeRequestActivity.this);
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

    @Override
    public void onClick() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                RescheduleTimeRequestActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                mDateSetListener,
                year, month, day);
        //set min date to tomorrow
        long oneDay = 86400000;
        dialog.getDatePicker().setMinDate(((System.currentTimeMillis() - 1000) + oneDay));
        //set max date to 14 days after due date
        dialog.getDatePicker().setMaxDate(dueDate + oneDay * MAX_RESCHEDULE_DAY + 1000);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
