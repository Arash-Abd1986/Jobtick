package com.jobtick.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jobtick.R;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.adapers.TaskAlertAdapter;
import com.jobtick.models.task.TaskAlert;
import com.jobtick.utils.Constant;
import com.jobtick.utils.HttpStatus;

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

/*
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
*/
    @BindView(R.id.cb_receive_alerts)
    CheckBox cbReceiveAlerts;
    @BindView(R.id.txt_btn_add_custom_alert)
    TextView txtBtnAddCustomAlert;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.ivBack)
    ImageView ivBack;

    TaskAlertAdapter adapter;
    ArrayList<TaskAlert> taskAlertArrayList;

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
       /* toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Job Alerts");*/
        ivBack.setOnClickListener(v->{
            finish();
        });
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

            /*if (taskAlertArrayList.size() == 0) {
                recyclerView.setVisibility(View.GONE);
            }*/
        } else {
          /*  Intent newTaskAlerts = new Intent(TaskAlertsActivity.this, NewTaskAlertsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("TASK_ALERT", obj);
            bundle.putInt("POSITION", position);
            newTaskAlerts.putExtras(bundle);
            startActivityForResult(newTaskAlerts, 1);*/
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
                                showCustomDialog("Removed successfully !");
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
                            showCustomDialog(jsonObject_error.getString("message"));
                           /* if (jsonObject_error.has("message")) {
                                Toast.makeText(AddCouponPaymentOverviewActivity.this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }*/
                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());
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
                return map1;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(TaskAlertsActivity.this);
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

        dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
                            //  showCustomDialog(jsonObject_error.getString("message"));
                           /* if (jsonObject_error.has("message")) {
                                Toast.makeText(AddCouponPaymentOverviewActivity.this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }*/
                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());
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
                return map1;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(TaskAlertsActivity.this);
        requestQueue.add(stringRequest);

    }

}
