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
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.text_view.TextViewMedium;
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


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_date)
    ExtendedEntryText txtDate;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_previous_date)
    TextView txtPreviousDate;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_previous_time)
    TextView getTxtPreviousTime;

    int year, month, day;
    long dueDate;
    DatePickerDialog.OnDateSetListener mDateSetListener;
    String str_due_date = null;


    private TaskModel taskModel;


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_note)
    ExtendedCommentText edtNote;

    private BottomSheetDialog mBottomSheetDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reschedule_time_request);

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
                getTxtPreviousTime.setText(R.string.afternoon);
            if(taskModel.getDueTime().getEvening())
                getTxtPreviousTime.setText(R.string.evening);
            if(taskModel.getDueTime().getMorning())
                getTxtPreviousTime.setText(R.string.morning);
            if(taskModel.getDueTime().getMidday())
                getTxtPreviousTime.setText(R.string.anyTime);
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
                                showToast(jsonObject_error.getString("message"), this);
                            }
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

    @Override
    public void onClick() {
        showBottomSheetDialogDate();
    }

    private void showBottomSheetDialogDate() {

        final View view = getLayoutInflater().inflate(R.layout.sheet_date, null);

        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        long oneDay = 86400000;
        CalendarView calendarView = view.findViewById(R.id.calenderView);
        //set min date to tomorrow
        calendarView.setMinDate(((System.currentTimeMillis() - 1000) + oneDay));
        //set max date to two weeks later
        calendarView.setMaxDate(dueDate + oneDay * MAX_RESCHEDULE_DAY + 1000);

        TextViewMedium txtCancel = view.findViewById(R.id.txt_cancel);
        txtCancel.setOnClickListener(v -> {
            mBottomSheetDialog.dismiss();
        });

        LinearLayout lytBtnDone = view.findViewById(R.id.lyt_btn_done);

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        month = month + 1;

        day = calendar.get(Calendar.DAY_OF_MONTH);
        lytBtnDone.setOnClickListener(v -> {


            str_due_date = Tools.getDayMonthDateTimeFormat(year + "-" + month + "-" + day);
            txtDate.setText(str_due_date);

            mBottomSheetDialog.dismiss();

        });

        calendarView.setOnDateChangeListener((arg0, year, month, date) -> {

            this.month = month + 1;
            this.year = year;
            this.day = date;
        });


        // set background transparent
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(dialog -> mBottomSheetDialog = null);
    }
}
