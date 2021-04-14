package com.jobtick.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.android.BuildConfig;
import com.jobtick.android.R;
import android.annotation.SuppressLint;

import com.jobtick.android.adapers.TaskAlertAdapter;
import com.jobtick.android.models.task.TaskAlert;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.HttpStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class TaskAlertsActivity extends ActivityBase implements TaskAlertAdapter.OnItemClickListener {


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.cb_receive_alerts)
    CheckBox cbReceiveAlerts;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_btn_add_custom_alert)
    TextView txtBtnAddCustomAlert;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;


    TaskAlertAdapter adapter;
    ArrayList<TaskAlert> taskAlertArrayList;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.no_alerts_container)
    LinearLayout noAlerts;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.alerts_container)
    LinearLayout alerts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_alerts);
        ButterKnife.bind(this);
        initToolbar();
        initComponent();
    }

    private void initComponent() {
        taskAlertArrayList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(TaskAlertsActivity.this));
        recyclerView.setHasFixedSize(true);
        adapter = new TaskAlertAdapter(this, new ArrayList<>());
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        getListOfTaskAlert();
    }


    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Job Alerts");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @OnClick(R.id.txt_btn_add_custom_alert)
    public void onViewClicked() {
        Intent newTaskAlerts = new Intent(TaskAlertsActivity.this, NewTaskAlertsActivity.class);
        startActivityForResult(newTaskAlerts, 1);
    }

    @Override
    public void onItemClick(View view, TaskAlert obj, int position, String action) {
        if (action.equalsIgnoreCase("delete")) {
            adapter.removeItems(position);
            removeTaskAlert(obj.getId());

            checkList();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            getListOfTaskAlert();

            if (data != null) {
/*                Bundle bundle = data.getExtras();
                int position = bundle.getInt("POSITION");
                if (position != -1) {
                    taskAlertArrayList.remove(position);
                    taskAlertArrayList.add(position, (TaskAlert) bundle.getParcelable("TASK_ALERT"));
                } else {
                    taskAlertArrayList.add((TaskAlert) bundle.getParcelable("TASK_ALERT"));
                }
               *//* if (taskAlertArrayList.size() != 0) {
                    if (recyclerView.getVisibility() != View.VISIBLE) {
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (recyclerView.getVisibility() == View.VISIBLE) {
                        recyclerView.setVisibility(View.GONE);
                    }
                }*//*
                adapter.notifyDataSetChanged();*/

            }
        }
    }

    public void removeTaskAlert(int taskAlertId) {
        //{{baseurl}}/taskalerts/:taskalert_id

        showProgressDialog();
        StringRequest stringRequest = new StringRequest(StringRequest.Method.DELETE, Constant.URL_TASK_ALERT + "/" + taskAlertId,
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {

                            } else {
                                showToast("Something went Wrong", TaskAlertsActivity.this);
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
                            showToast(jsonObject_error.getString("message"), this);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", TaskAlertsActivity.this);
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
                map1.put("Version", String.valueOf(BuildConfig.VERSION_CODE));
                return map1;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(TaskAlertsActivity.this);
        requestQueue.add(stringRequest);
    }


    public void getListOfTaskAlert() {
        taskAlertArrayList.clear();
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, Constant.URL_TASK_ALERT,
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                    JSONArray jsonArray_data = jsonObject.getJSONArray("data");
                                    for (int i = 0; jsonArray_data.length() > i; i++) {
                                        JSONObject jsonObject_taskModel_list = jsonArray_data.getJSONObject(i);
                                        TaskAlert taskModel = new TaskAlert().getJsonToModel(jsonObject_taskModel_list, TaskAlertsActivity.this);
                                        taskAlertArrayList.add(taskModel);
                                    }
                                }
                                adapter.addItems(taskAlertArrayList);
                            } else {
                                showToast("Something went Wrong", TaskAlertsActivity.this);
                            }
                        }
                    } catch (JSONException e) {
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                    }
                    checkList();
                },
                error -> {
                    checkList();
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
                        showToast("Something Went Wrong", TaskAlertsActivity.this);
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
                map1.put("Version", String.valueOf(BuildConfig.VERSION_CODE));
                return map1;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(TaskAlertsActivity.this);
        requestQueue.add(stringRequest);

    }

    private void checkList() {
        if (taskAlertArrayList.size() <= 0) {
            noAlerts.setVisibility(View.VISIBLE);
            alerts.setVisibility(View.GONE);
        } else {
            noAlerts.setVisibility(View.GONE);
            alerts.setVisibility(View.VISIBLE);
        }
    }

}
