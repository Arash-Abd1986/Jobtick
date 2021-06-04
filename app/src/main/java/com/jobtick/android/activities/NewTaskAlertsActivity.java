package com.jobtick.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.SwitchCompat;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.android.BuildConfig;
import com.jobtick.android.R;

import android.annotation.SuppressLint;

import com.jobtick.android.adapers.SectionsPagerAdapter;
import com.jobtick.android.adapers.SuburbSearchAdapter;
import com.jobtick.android.fragments.NewTaskAlertsInPersonFragment;
import com.jobtick.android.fragments.NewTaskAlertsRemoteFragment;
import com.jobtick.android.fragments.SearchSuburbBottomSheet;
import com.jobtick.android.models.response.searchsuburb.Feature;
import com.jobtick.android.models.task.TaskAlert;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.HttpStatus;
import com.jobtick.android.widget.ExtendedEntryText;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class NewTaskAlertsActivity extends ActivityBase implements NewTaskAlertsInPersonFragment.OperationInPersonListener{

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    TaskAlert taskAlert;
    int position = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task_alerts);
        ButterKnife.bind(this);
        taskAlert = new TaskAlert();
        if (getIntent().getExtras() != null) {
            taskAlert = (TaskAlert) getIntent().getExtras().getParcelable("TASK_ALERT");
            position = getIntent().getExtras().getInt("POSITION");
        }
        initToolbar();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, NewTaskAlertsInPersonFragment.newInstance(taskAlert, position, this), "New Job Alert")
                    .commit();
        }

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
        if (taskAlert.isValid()) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt("POSITION", position);
            bundle.putParcelable("TASK_ALERT", taskAlert);
            intent.putExtras(bundle);
            setResult(1, intent);
        }
        super.onBackPressed();
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Job Alert");
    }



    @Override
    public void onInPersonSave(int position, TaskAlert taskAlert, String minPrice, String maxPrice) {
        this.taskAlert = taskAlert;
        this.position = position;
        if (taskAlert.getAlert_type().equals("physical")) {
            taskAlert.setLattitude(taskAlert.getLattitude());
            taskAlert.setLongitude(taskAlert.getLongitude());
            taskAlert.setSuburb(taskAlert.getSuburb());
        }else{
            taskAlert.setLattitude(null);
            taskAlert.setLongitude(null);
            taskAlert.setSuburb(null);
        }
        addTaskAlert(this.taskAlert,minPrice, maxPrice);
    }


    private void addTaskAlert(TaskAlert taskAlert, String minPrice,String maxPrice) {
        //{{baseurl}}/taskalerts
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, Constant.URL_TASK_ALERT_V2,
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                    finish();
                                }
                            } else {
                                showToast("Something went Wrong", NewTaskAlertsActivity.this);
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
                        showToast("Something Went Wrong", NewTaskAlertsActivity.this);
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

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("keyword", taskAlert.getKetword());
                map1.put("type", taskAlert.getAlert_type());
                map1.put("minprice", minPrice);
                map1.put("maxprice", maxPrice);
                if (taskAlert.getAlert_type().equals("physical")) {
                    map1.put("location", taskAlert.getSuburb());
                    map1.put("latitude", taskAlert.getLattitude().toString());
                    map1.put("longitude", taskAlert.getLongitude().toString());
                    map1.put("distance", String.valueOf(taskAlert.getDistance()));
                }

                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(NewTaskAlertsActivity.this);
        requestQueue.add(stringRequest);
        Timber.e(stringRequest.getUrl());
    }


}


