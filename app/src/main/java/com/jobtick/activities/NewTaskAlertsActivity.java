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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.text_view.TextViewRegular;
import com.jobtick.adapers.SectionsPagerAdapter;
import com.jobtick.fragments.NewTaskAlertsInPersonFragment;
import com.jobtick.fragments.NewTaskAlertsRemoteFragment;
import com.jobtick.models.task.TaskAlert;
import com.jobtick.utils.Constant;
import com.jobtick.utils.HttpStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class NewTaskAlertsActivity extends ActivityBase implements NewTaskAlertsInPersonFragment.OperationInPersonListener, NewTaskAlertsRemoteFragment.OperationRemoteListener {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rb_in_person)
    RadioButton rbInPerson;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rb_remote)
    RadioButton rbRemote;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rg_inPerson_remote)
    RadioGroup rgInPersonRemote;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.view_pager)
    ViewPager viewPager;

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
        initComponent();
    }

    private void initComponent() {
        setupViewPager(viewPager);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        if (taskAlert.isValid()) {
            if (taskAlert.getAlert_type().equalsIgnoreCase("remote")) {
                viewPager.setCurrentItem(1);
                rbInPerson.setChecked(false);
                rbRemote.setChecked(true);
            } else {
                viewPager.setCurrentItem(0);
                rbInPerson.setChecked(true);
                rbRemote.setChecked(false);
            }
        } else {
            viewPager.setCurrentItem(0);
        }
        viewPager.setOffscreenPageLimit(2);
        rgInPersonRemote.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.findViewById(checkedId).getId()) {
                    case R.id.rb_in_person:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.rb_remote:
                        viewPager.setCurrentItem(1);
                        break;
                }
            }
        });
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

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(NewTaskAlertsInPersonFragment.newInstance(taskAlert, position, this), "New Job Alert");
        adapter.addFragment(NewTaskAlertsRemoteFragment.newInstance(taskAlert, position, this), "New Job Alert");
        viewPager.setAdapter(adapter);
    }


    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Job Alert");
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(final int position) {
            switch (position) {
                case 0:
                    rbInPerson.setChecked(true);
                    rbRemote.setChecked(false);
                    break;
                case 1:
                    rbInPerson.setChecked(false);
                    rbRemote.setChecked(true);
                    break;
            }

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };


    @Override
    public void onInPersonSave(int position, TaskAlert taskAlert) {
        this.taskAlert = taskAlert;
        this.position = position;
        addTaskAlert(this.taskAlert);
    }

    @Override
    public void onRemoteSave(int position, TaskAlert taskAlert) {
        this.taskAlert = taskAlert;
        this.position = position;
        addTaskAlert(this.taskAlert);

    }


    private void addTaskAlert(TaskAlert taskAlert) {
        //{{baseurl}}/taskalerts
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, Constant.URL_TASK_ALERT,
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
                return map1;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("text_phrase", taskAlert.getKetword());
                map1.put("task_type", taskAlert.getAlert_type());
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


