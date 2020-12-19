package com.jobtick.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.text_view.TextViewMedium;
import com.jobtick.adapers.SectionsPagerAdapter;
import com.jobtick.fragments.TaskBudgetFragment;
import com.jobtick.fragments.TaskDateTimeFragment;
import com.jobtick.fragments.TaskDetailFragment;
import com.jobtick.models.AttachmentModel;
import com.jobtick.models.DueTimeModel;
import com.jobtick.models.PositionModel;
import com.jobtick.models.TaskModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.FireBaseEvent;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static com.jobtick.utils.ConstantKey.COMPLETES_MESSAGE_FROM;
import static com.jobtick.utils.ConstantKey.RESULTCODE_CREATE_TASK;

public class TaskCreateActivity extends ActivityBase implements TaskDetailFragment.OperationsListener,
        TaskDateTimeFragment.OperationsListener, TaskBudgetFragment.OperationsListener {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.creating_task_layout)
    FrameLayout creatingTaskLayout;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_details)
    LinearLayout lytBtnDetails;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_bnt_date_time)
    LinearLayout lytBntDateTime;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_budget)
    LinearLayout lytBtnBudget;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.card_selection_view)
    CardView cardSelectionView;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_details)
    ImageView imgDetails;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_details)
    TextViewMedium txtDetails;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_date_time)
    ImageView imgDateTime;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_date_time)
    TextViewMedium txtDateTime;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_budget)
    ImageView imgBudget;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_budget)
    TextViewMedium txtBudget;

    TaskModel taskModel;
    private String title;
    private ActionDraftDateTime actionDraftDateTime;
    private ActionDraftTaskDetails actionDraftTaskDetails;
    private ActionDraftTaskBudget actionDraftTaskBudget;
    private boolean isDraftWorkDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_create);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getParcelable(ConstantKey.TASK) != null) {
            taskModel = bundle.getParcelable(ConstantKey.TASK);
        } else {
            taskModel = new TaskModel();
        }
        if (bundle != null && bundle.containsKey(ConstantKey.CATEGORY_ID)) {
            taskModel.setCategory_id(bundle.getInt(ConstantKey.CATEGORY_ID, 1));
        }

        title = ConstantKey.CREATE_A_JOB;
        if (bundle != null && bundle.getString(ConstantKey.TITLE) != null) {
            title = bundle.getString(ConstantKey.TITLE);
        }
        initToolbar(title);
        initComponent();
    }


    public void setActionDraftTaskDetails(ActionDraftTaskDetails actionDraftTaskDetails) {
        this.actionDraftTaskDetails = actionDraftTaskDetails;
    }

    public void setActionDraftDateTime(ActionDraftDateTime actionDraftDateTime) {
        this.actionDraftDateTime = actionDraftDateTime;
    }

    public void setActionDraftTaskBudget(ActionDraftTaskBudget actionDraftTaskBudget) {
        this.actionDraftTaskBudget = actionDraftTaskBudget;
    }

    private void initComponent() {
        setupViewPager(viewPager);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(3);
        selectDetailsBtn();
        lytBntDateTime.setEnabled(false);
        lytBtnBudget.setEnabled(false);
        lytBtnDetails.setSelected(true);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(TaskDetailFragment.newInstance(taskModel.getTitle(), taskModel.getDescription(), taskModel.getMusthave(), taskModel.getTaskType(), taskModel.getLocation(), taskModel.getPosition(), this), getResources().getString(R.string.details));
        adapter.addFragment(TaskDateTimeFragment.newInstance(taskModel.getDueDate() == null ? null : taskModel.getDueDate().substring(0, 10), taskModel.getDueTime(), this), getResources().getString(R.string.date_time));
        adapter.addFragment(TaskBudgetFragment.newInstance(taskModel.getBudget(), taskModel.getHourlyRate(), taskModel.getTotalHours(), taskModel.getPaymentType(), this), getResources().getString(R.string.budget));
        viewPager.setAdapter(adapter);
    }

    private void initToolbar(String title) {
        toolbar.setNavigationIcon(R.drawable.ic_cancel);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_create, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                isDraftWorkDone = false;
                onBackPressed();
                break;
            case R.id.menu_attachment:
                Bundle bundle = new Bundle();
                Intent intent = new Intent(this, AttachmentActivity.class);
                bundle.putParcelableArrayList(ConstantKey.ATTACHMENT, taskModel.getAttachments());
                bundle.putString(ConstantKey.TITLE, title);
                bundle.putString(ConstantKey.SLUG, taskModel.getSlug());
                intent.putExtras(bundle);
                startActivityForResult(intent, ConstantKey.RESULTCODE_ATTACHMENT);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isDraftWorkDone) {
            super.onBackPressed();
        } else {
            actionDraftTaskDetails.callDraftTaskDetails(this.taskModel);
        }
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(final int position) {
            switch (position) {
                case 0:
                    selectDetailsBtn();
                    break;
                case 1:
                    selectDateTimeBtn();
                    break;
                case 2:
                    selectBudgetBtn();
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

    private void selectBudgetBtn() {
        ColorStateList csl_primary = AppCompatResources.getColorStateList(this, R.color.colorPrimary);
        imgBudget.setImageTintList(csl_primary);
        txtBudget.setTextColor(getResources().getColor(R.color.colorPrimary));
        ColorStateList csl_green = AppCompatResources.getColorStateList(this, R.color.green);
        imgDateTime.setImageTintList(csl_green);
        imgDetails.setImageTintList(csl_green);
        txtDateTime.setTextColor(getResources().getColor(R.color.green));
        txtDetails.setTextColor(getResources().getColor(R.color.green));
    }

    private void selectDateTimeBtn() {
        ColorStateList csl_primary = AppCompatResources.getColorStateList(this, R.color.colorPrimary);
        imgDateTime.setImageTintList(csl_primary);
        txtDateTime.setTextColor(getResources().getColor(R.color.colorPrimary));
        ColorStateList csl_grey = AppCompatResources.getColorStateList(this, R.color.greyC4C4C4);
        ColorStateList csl_green = AppCompatResources.getColorStateList(this, R.color.green);
        imgDetails.setImageTintList(csl_green);
        imgBudget.setImageTintList(csl_grey);
        txtDetails.setTextColor(getResources().getColor(R.color.green));
        txtBudget.setTextColor(getResources().getColor(R.color.colorGrayC9C9C9));
    }

    private void selectDetailsBtn() {
        ColorStateList csl_primary = AppCompatResources.getColorStateList(this, R.color.colorPrimary);
        imgDetails.setImageTintList(csl_primary);
        txtDetails.setTextColor(getResources().getColor(R.color.colorPrimary));
        ColorStateList csl_grey = AppCompatResources.getColorStateList(this, R.color.greyC4C4C4);
        imgDateTime.setImageTintList(csl_grey);
        imgBudget.setImageTintList(csl_grey);
        txtDateTime.setTextColor(getResources().getColor(R.color.colorGrayC9C9C9));
        txtBudget.setTextColor(getResources().getColor(R.color.colorGrayC9C9C9));
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        menu.setHeaderTitle("Select The Action");
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.lyt_btn_details, R.id.lyt_bnt_date_time, R.id.lyt_btn_budget})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lyt_btn_details:
                viewPager.setCurrentItem(0);
                selectDetailsBtn();
                break;
            case R.id.lyt_bnt_date_time:
                viewPager.setCurrentItem(1);
                selectDateTimeBtn();
                break;
            case R.id.lyt_btn_budget:
                viewPager.setCurrentItem(2);
                selectBudgetBtn();
                break;
        }
    }

    @Override
    public void onNextClick(String title, String description, ArrayList<String> musthave, String task_type,
                            String location, PositionModel positionModel, ArrayList<AttachmentModel> attachmentArrayList) {
        taskModel.setTitle(title);
        taskModel.setDescription(description);
        taskModel.setMusthave(musthave);
        taskModel.setPosition(positionModel);
        taskModel.setLocation(location);
        taskModel.setTaskType(task_type);
        taskModel.setAttachments(attachmentArrayList);
        viewPager.setCurrentItem(1);
        selectDateTimeBtn();
    }

    @Override
    public void onNextClickDateTime(String due_date, DueTimeModel dueTimeModel) {
        taskModel.setDueDate(due_date);
        taskModel.setDueTime(dueTimeModel);
        viewPager.setCurrentItem(2);
        selectBudgetBtn();
    }

    @Override
    public void onBackClickDateTime(String due_date, DueTimeModel dueTimeModel) {
        taskModel.setDueDate(due_date);
        taskModel.setDueTime(dueTimeModel);
        viewPager.setCurrentItem(0);
        selectDetailsBtn();
    }

    @Override
    public void onValidDataFilledDateTimeNext() {
        lytBntDateTime.setEnabled(true);
        lytBtnBudget.setSelected(true);
        lytBtnDetails.setEnabled(true);
    }

    @Override
    public void onValidDataFilledDateTimeBack() {
        lytBntDateTime.setEnabled(false);
        lytBtnBudget.setEnabled(false);
        lytBtnDetails.setSelected(true);
    }

    @Override
    public void onValidDataFilled() {
        lytBntDateTime.setSelected(true);
        lytBtnBudget.setEnabled(false);
        lytBtnDetails.setEnabled(true);
    }

    @Override
    public void draftTaskDetails(TaskModel taskModel, boolean moveForeword) {
        if (moveForeword) {
            actionDraftDateTime.callDraftTaskDateTime(this.taskModel);
        } else {
            this.taskModel = taskModel;
            if (taskModel.getTitle() != null) {
                uploadDataToServer(true);
            } else {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putBoolean(ConstantKey.CATEGORY, true);
                intent.putExtras(bundle);
                setResult(ConstantKey.RESULTCODE_CATEGORY, intent);
                super.onBackPressed();
            }
        }
    }

    @Override
    public void draftTaskDateTime(TaskModel taskModel, boolean moveForeword) {
        if (moveForeword) {
            actionDraftTaskBudget.callDraftTaskBudget(this.taskModel);
        } else {
            this.taskModel = taskModel;
            uploadDataToServer(true);
        }
    }

    @Override
    public void draftTaskBudget(TaskModel taskModel) {
        this.taskModel = taskModel;
        uploadDataToServer(true);
    }

    @Override
    public void onNextClickBudget(int budget, int hour_budget, int total_hours, String payment_type) {
        taskModel.setBudget(budget);
        taskModel.setHourlyRate(hour_budget);
        taskModel.setTotalHours(total_hours);
        taskModel.setPaymentType(payment_type);
        uploadDataToServer(false);
    }

    private void uploadDataToServer(Boolean draft) {
        String queryParameter;
        int METHOD;
        if (taskModel.getSlug() != null) {
            queryParameter = "/" + taskModel.getSlug();
            METHOD = Request.Method.PATCH;
        } else {
            queryParameter = "/create";
            METHOD = Request.Method.POST;
        }
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(METHOD, Constant.URL_TASKS + queryParameter,
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                Intent intent;
                                Bundle bundle;
                                if (draft) {
                                    intent = new Intent();
                                    bundle = new Bundle();
                                    bundle.putBoolean(ConstantKey.UPDATE_TASK, true);
                                    intent.putExtras(bundle);
                                    setResult(ConstantKey.RESULTCODE_UPDATE_TASK, intent);
                                    isDraftWorkDone = true;
                                    onBackPressed();
                                    return;
                                } else {
                                    intent = new Intent();
                                    bundle = new Bundle();
                                    bundle.putBoolean(ConstantKey.CATEGORY, true);
                                    intent.putExtras(bundle);
                                    setResult(ConstantKey.RESULTCODE_CATEGORY, intent);
                                }

                                if (draft) {
                                    isDraftWorkDone = true;
                                    onBackPressed();
                                    return;
                                }

                                FireBaseEvent.getInstance(getApplicationContext())
                                        .sendEvent(FireBaseEvent.Event.POST_A_JOB,
                                                FireBaseEvent.EventType.API_RESPOND_SUCCESS,
                                                FireBaseEvent.EventValue.POST_A_JOB_SUBMIT);

                                intent = new Intent(TaskCreateActivity.this, CompleteMessageActivity.class);
                                intent.putExtra(COMPLETES_MESSAGE_FROM, RESULTCODE_CREATE_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                showToast("Something went Wrong", TaskCreateActivity.this);
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
                        if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                            unauthorizedUser();
                            hideProgressDialog();
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                            if (jsonObject_error.has("message")) {
                              showToast(jsonObject_error.getString("message"), this);
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", TaskCreateActivity.this);
                    }
                    System.out.println(error.toString());
                    hideProgressDialog();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Accept", "application/json");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map1 = new HashMap<>();
                map1.put("category_id", Integer.toString(taskModel.getCategory_id()));
                map1.put("title", taskModel.getTitle());
                if (taskModel.getDescription() != null)
                    map1.put("description", taskModel.getDescription());
                if (taskModel.getLocation() != null && !taskModel.getLocation().equals(""))
                    map1.put("location", taskModel.getLocation());
                if (taskModel.getPosition() != null) {
                    map1.put("latitude", String.valueOf(taskModel.getPosition().getLatitude()));
                    map1.put("longitude", String.valueOf(taskModel.getPosition().getLongitude()));
                }
                if (taskModel.getTaskType() != null)
                    map1.put("task_type", taskModel.getTaskType());
                if (taskModel.getPaymentType() != null)
                    map1.put("payment_type", taskModel.getPaymentType());
                if (taskModel.getPaymentType() != null) {
                    if (taskModel.getPaymentType().equalsIgnoreCase("fixed")) {
                        if (taskModel.getBudget() >= 10)
                            map1.put("budget", String.valueOf(taskModel.getBudget()));
                    } else {
                        if (((taskModel.getTotalHours()) * taskModel.getHourlyRate()) >= 10) {
                            map1.put("budget", String.valueOf((taskModel.getTotalHours()) * taskModel.getHourlyRate()));
                            map1.put("total_hours", String.valueOf(taskModel.getTotalHours()));
                            map1.put("hourly_rate", String.valueOf(taskModel.getHourlyRate()));
                        }
                    }
                }
                if (taskModel.getDueDate() != null)
                    map1.put("due_date", Tools.getApplicationFromatToServerFormat(taskModel.getDueDate()));
                if (taskModel.getAttachments() != null && taskModel.getAttachments().size() != 0) {
                    for (int i = 0; taskModel.getAttachments().size() > i; i++) {
                        if (taskModel.getAttachments().get(i).getId() != null) {
                            map1.put("attachments[" + i + "]", String.valueOf(taskModel.getAttachments().get(i).getId()));
                        }
                    }
                }
                if (taskModel.getMusthave() != null && taskModel.getMusthave().size() != 0) {
                    for (int i = 0; taskModel.getMusthave().size() > i; i++) {
                        map1.put("musthave[" + i + "]", taskModel.getMusthave().get(i));
                    }
                }
                if (taskModel.getDueTime() != null) {
                    int count = 0;
                    if (taskModel.getDueTime().getMorning()) {
                        map1.put("due_time[" + count + "]", "morning");
                        count = count + 1;
                    }
                    if (taskModel.getDueTime().getAfternoon()) {
                        map1.put("due_time[" + count + "]", "afternoon");
                        count = count + 1;
                    }
                    if (taskModel.getDueTime().getEvening()) {
                        map1.put("due_time[" + count + "]", "evening");
                        count = count + 1;
                    }
                    if (taskModel.getDueTime().getMidday()) {
                        map1.put("due_time[" + count + "]", "midday");
                    }
                }
                if (draft) {
                    map1.put("draft", "1");
                }
                System.out.println((map1.size()));
                System.out.println((map1.toString()));

                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(TaskCreateActivity.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackClickBudget(int budget, int hour_budget, int total_hours, String payment_type) {
        taskModel.setBudget(budget);
        taskModel.setHourlyRate(hour_budget);
        taskModel.setTotalHours(total_hours);
        taskModel.setPaymentType(payment_type);
        viewPager.setCurrentItem(1);
        selectDateTimeBtn();
    }

    @Override
    public void onValidDataFilledBudgetNext() {
    }

    @Override
    public void onValidDataFilledBudgetBack() {
        lytBntDateTime.setSelected(true);
        lytBtnBudget.setEnabled(false);
        lytBtnDetails.setEnabled(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantKey.RESULTCODE_ATTACHMENT) {
            if (data != null) {
                if (data.getParcelableArrayListExtra(ConstantKey.ATTACHMENT) != null) {
                    ArrayList<AttachmentModel> attachmentArrayList = data.getParcelableArrayListExtra(ConstantKey.ATTACHMENT);
                    taskModel.setAttachments(attachmentArrayList);
                }
            }
        }
    }

    public interface ActionDraftTaskDetails {
        void callDraftTaskDetails(TaskModel taskModel);
    }

    public interface ActionDraftDateTime {
        void callDraftTaskDateTime(TaskModel taskModel);
    }

    public interface ActionDraftTaskBudget {
        void callDraftTaskBudget(TaskModel taskModel);
    }
}
