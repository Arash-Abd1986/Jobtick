package com.jobtick.android.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.jobtick.android.BuildConfig;
import com.jobtick.android.R;
import com.jobtick.android.activities.ActivityBase;
import com.jobtick.android.activities.DashboardActivity;
import com.jobtick.android.activities.SearchTaskActivity;
import com.jobtick.android.activities.TaskCreateActivity;
import com.jobtick.android.activities.TaskDetailsActivity;
import com.jobtick.android.adapers.TaskListAdapterV2;
import com.jobtick.android.models.TaskModel;
import com.jobtick.android.models.response.myjobs.Data;
import com.jobtick.android.models.response.myjobs.MyJobsResponse;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.ConstantKey;
import com.jobtick.android.utils.Helper;
import com.jobtick.android.utils.HttpStatus;
import com.jobtick.android.utils.SessionManager;
import com.jobtick.android.widget.EndlessRecyclerViewOnScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.jobtick.android.utils.Constant.TASK_ASSIGNED_CASE_RELATED_JOB_VALUE;
import static com.jobtick.android.utils.Constant.TASK_ASSIGNED_CASE_UPPER_FIRST;
import static com.jobtick.android.utils.Constant.TASK_DRAFT_CASE_ALL_JOB_KEY;
import static com.jobtick.android.utils.Constant.TASK_DRAFT_CASE_ALL_JOB_VALUE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyTasksFragment extends Fragment implements TaskListAdapterV2.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener,
        TaskListAdapterV2.OnDraftDeleteListener, ConfirmDeleteTaskBottomSheet.NoticeListener {


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_view_status)
    RecyclerView recyclerViewStatus;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    private DashboardActivity dashboardActivity;
    private SessionManager sessionManager;
    private EndlessRecyclerViewOnScrollListener onScrollListener;


    private TaskListAdapterV2 taskListAdapter;
    private int currentPage = 1;
    private boolean isLastPage = false;
    private int totalItem = 10;
    ImageView ivNotification;
    TextView toolbar_title;
    private TextView filterText;
    private ImageView filterIcon;
    private LinearLayout linFilter;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.bottom_sheet)
    FrameLayout bottomSheet;


    private String single_choice_selected = null;
    private String temp_single_choice_selected = null;
    private String str_search = null;
    private final String temp_str_search = null;
    private Toolbar toolbar;
    private LinearLayout noJobs;
    PopupWindow mypopupWindow;

    public MyTasksFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_tasks, container, false);
        ButterKnife.bind(this, view);
        noJobs = view.findViewById(R.id.no_jobs_container);
        swipeRefresh.setOnRefreshListener(this);
        initToolbar();
        setHasOptionsMenu(true);
        mBehavior = BottomSheetBehavior.from(bottomSheet);

        return view;
    }

    private void initToolbar() {
        dashboardActivity = (DashboardActivity) requireActivity();
        if (dashboardActivity == null) return;
        toolbar = dashboardActivity.findViewById(R.id.toolbar);
        toolbar.getMenu().clear();
        //toolbar.inflateMenu(R.menu.menu_my_task_black);
        toolbar.setVisibility(View.VISIBLE);
        ivNotification = dashboardActivity.findViewById(R.id.ivNotification);
        ivNotification.setVisibility(View.GONE);
        toolbar_title = dashboardActivity.findViewById(R.id.toolbar_title);
        linFilter = dashboardActivity.findViewById(R.id.lin_filter);
        filterText = dashboardActivity.findViewById(R.id.filter_text);
        filterIcon = dashboardActivity.findViewById(R.id.filter_icon);
        linFilter.setOnClickListener(v -> {
            filterText.setTextColor(ContextCompat.getColor(getActivity(), R.color.P300));
            filterIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_sort_arrow_up));
            mypopupWindow.showAsDropDown(toolbar.findViewById(R.id.lin_filter), 0, 0);
        });
        toolbar_title.setVisibility(View.VISIBLE);
        linFilter.setVisibility(View.VISIBLE);
        toolbar_title.setText(R.string.my_jobs);

        toolbar_title.setTypeface(ResourcesCompat.getFont(getContext(), R.font.roboto_semi_bold));
        androidx.appcompat.widget.Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.START;
        toolbar_title.setLayoutParams(params);
    }

    @Override
    public void onStop() {
        super.onStop();
        linFilter.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        linFilter.setVisibility(View.VISIBLE);
        sessionManager.setNeedRefresh(false);
        getStatusList();
    }

    private void setPopUpWindow() {
        LayoutInflater inflater = (LayoutInflater)
                getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.my_jobs_menu, null);

        mypopupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        mypopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView allJobs = (TextView) view.findViewById(R.id.all_jobs);
        TextView assigned = (TextView) view.findViewById(R.id.assigned);
        TextView posted = (TextView) view.findViewById(R.id.posted);
        TextView offered = (TextView) view.findViewById(R.id.offered);
        TextView draft = (TextView) view.findViewById(R.id.draft);
        TextView completed = (TextView) view.findViewById(R.id.completed);
        TextView overdue = (TextView) view.findViewById(R.id.overdue);
        TextView closed = (TextView) view.findViewById(R.id.closed);
        TextView cancelled = (TextView) view.findViewById(R.id.cancelled);
        mypopupWindow.setOnDismissListener(() -> {
            filterText.setTextColor(ContextCompat.getColor(getActivity(), R.color.N900));
            filterIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_sort_arrow_down));
        });
        allJobs.setOnClickListener(v -> {
            allJobs.setTextColor(ContextCompat.getColor(getActivity(), R.color.N900));
            assigned.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            posted.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            offered.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            draft.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            completed.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            overdue.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            closed.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            cancelled.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            refreshSort("All Jobs");
            mypopupWindow.dismiss();
            filterText.setText("All jobs");
        });
        assigned.setOnClickListener(v -> {
            allJobs.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            assigned.setTextColor(ContextCompat.getColor(getActivity(), R.color.N900));
            posted.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            offered.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            draft.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            completed.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            overdue.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            closed.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            cancelled.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            refreshSort("Assigned");
            mypopupWindow.dismiss();
            filterText.setText("Assigned");
        });
        posted.setOnClickListener(v -> {
            allJobs.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            assigned.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            posted.setTextColor(ContextCompat.getColor(getActivity(), R.color.N900));
            offered.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            draft.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            completed.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            overdue.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            closed.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            cancelled.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            refreshSort("Open");
            mypopupWindow.dismiss();
            filterText.setText("Posted");

        });
        offered.setOnClickListener(v -> {
            allJobs.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            assigned.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            posted.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            offered.setTextColor(ContextCompat.getColor(getActivity(), R.color.N900));
            draft.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            completed.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            overdue.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            closed.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            cancelled.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            refreshSort("Offered");
            mypopupWindow.dismiss();
            filterText.setText("Offered");
        });
        draft.setOnClickListener(v -> {
            allJobs.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            assigned.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            posted.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            offered.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            draft.setTextColor(ContextCompat.getColor(getActivity(), R.color.N900));
            completed.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            overdue.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            closed.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            cancelled.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            refreshSort("Draft");
            mypopupWindow.dismiss();
            filterText.setText("Draft");
        });
        completed.setOnClickListener(v -> {
            allJobs.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            assigned.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            posted.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            offered.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            draft.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            completed.setTextColor(ContextCompat.getColor(getActivity(), R.color.N900));
            overdue.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            closed.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            cancelled.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            refreshSort("Completed");
            mypopupWindow.dismiss();
            filterText.setText("Completed");
        });
        overdue.setOnClickListener(v -> {
            allJobs.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            assigned.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            posted.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            offered.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            draft.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            completed.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            overdue.setTextColor(ContextCompat.getColor(getActivity(), R.color.N900));
            closed.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            cancelled.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            refreshSort("Overdue");
            mypopupWindow.dismiss();
            filterText.setText("Overdue");
        });
        closed.setOnClickListener(v -> {
            allJobs.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            assigned.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            posted.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            offered.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            draft.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            completed.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            overdue.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            cancelled.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            closed.setTextColor(ContextCompat.getColor(getActivity(), R.color.N900));
            refreshSort("Closed");
            mypopupWindow.dismiss();
            filterText.setText("Closed");
        });
        cancelled.setOnClickListener(v -> {
            allJobs.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            assigned.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            posted.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            offered.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            draft.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            completed.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            overdue.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            closed.setTextColor(ContextCompat.getColor(getActivity(), R.color.N300));
            cancelled.setTextColor(ContextCompat.getColor(getActivity(), R.color.N900));
            refreshSort("Cancelled");
            mypopupWindow.dismiss();
            filterText.setText("Cancelled");
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(dashboardActivity);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(dashboardActivity);
        recyclerViewStatus.setLayoutManager(layoutManager);
        resetTaskListAdapter();
        recyclerViewStatus.setAdapter(taskListAdapter);
        swipeRefresh.setRefreshing(true);
        single_choice_selected = TASK_DRAFT_CASE_ALL_JOB_VALUE;
        getStatusList();
        onScrollListener = new EndlessRecyclerViewOnScrollListener() {
            @Override
            public void onLoadMore(int currentPage) {
                MyTasksFragment.this.currentPage = currentPage;
                getStatusList();
            }

            @Override
            public int getTotalItem() {
                return totalItem;
            }
        };

        recyclerViewStatus.addOnScrollListener(onScrollListener);
        setPopUpWindow();

        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_search:

                    Intent intent = new Intent(dashboardActivity, SearchTaskActivity.class);
                    intent.putExtra(ConstantKey.FROM_MY_JOBS_WITH_LOVE, true);
                    dashboardActivity.startActivity(intent);

                    break;
            }
            return false;
        });

    }

    private void getStatusList() {
        String query_parameter = "";
        if (str_search != null) {
            query_parameter += "&search_query=" + str_search;
        }

        if (single_choice_selected.equalsIgnoreCase(TASK_DRAFT_CASE_ALL_JOB_VALUE))
            query_parameter += "";
        else
            query_parameter += "&status=" + single_choice_selected.toLowerCase();

        if (currentPage == 1)
            swipeRefresh.setRefreshing(true);

        Helper.closeKeyboard(dashboardActivity);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_MY_JOBS + "?page=" + currentPage + query_parameter,
                response -> {
                    Timber.e(response);
                    // categoryArrayList.clear();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        Gson gson = new Gson();
                        MyJobsResponse myJobsResponse = gson.fromJson(jsonObject.toString(), MyJobsResponse.class);

                        if (myJobsResponse.getData() == null) {
                            dashboardActivity.showToast("some went to wrong", dashboardActivity);
                            return;
                        }

                        totalItem = myJobsResponse.getTotal();
                        Constant.PAGE_SIZE = myJobsResponse.getPer_page();

                        if (currentPage == 1) {
                            resetTaskListAdapter();
                        }
                        taskListAdapter.addItems(myJobsResponse.getData(), totalItem);
                        isLastPage = taskListAdapter.getItemCount() == totalItem;

                        if (myJobsResponse.getData().size() <= 0) {
                            noJobs.setVisibility(View.VISIBLE);
                            recyclerViewStatus.setVisibility(View.GONE);
                        } else {
                            noJobs.setVisibility(View.GONE);
                            recyclerViewStatus.setVisibility(View.VISIBLE);

                        }

                        swipeRefresh.setRefreshing(false);
                        str_search = null;
                    } catch (JSONException e) {
                        str_search = null;
                        dashboardActivity.hideProgressDialog();
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                    }
                },
                error -> {
                    //  swipeRefresh.setRefreshing(false);
                    str_search = null;
                    swipeRefresh.setRefreshing(false);
                    dashboardActivity.errorHandle1(error.networkResponse);
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("Authorization", "Bearer " + sessionManager.getAccessToken());
                map1.put("Version", String.valueOf(BuildConfig.VERSION_CODE));
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(dashboardActivity);
        requestQueue.add(stringRequest);
        Timber.e(stringRequest.getUrl());

    }

    private void resetTaskListAdapter() {
        taskListAdapter = new TaskListAdapterV2(new ArrayList<>(), sessionManager.getUserAccount().getId());
        taskListAdapter.setOnItemClickListener(this);
        taskListAdapter.setOnDraftDeleteListener(this);
        recyclerViewStatus.setAdapter(taskListAdapter);
    }


    @Override
    public void onItemClick(View view, Data obj, int position, String action) {
        if (obj.getStatus().toLowerCase().equalsIgnoreCase(Constant.TASK_DRAFT.toLowerCase())) {
            getDataFromServer(obj.getSlug());
        } else {
            Intent intent = new Intent(dashboardActivity, TaskDetailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(ConstantKey.SLUG, obj.getSlug());
            //   bundle.putInt(ConstantKey.USER_ID, obj.getPoster().getId());
            intent.putExtras(bundle);
            startActivityForResult(intent, ConstantKey.RESULTCODE_MY_JOBS);
            Timber.i("MyTasksFragment Starting Task with slug: %s", obj.getSlug());
        }
    }

    private void getDataFromServer(String slug) {
        dashboardActivity.showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_TASKS + "/" + slug,
                response -> {
                    Timber.e(response);
                    dashboardActivity.hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {

                                if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                    JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                                    TaskModel taskModel = new TaskModel().getJsonToModel(jsonObject_data, dashboardActivity);
                                    EditTask(taskModel);
                                }

                            } else {
                                dashboardActivity.showToast("Something went wrong", dashboardActivity);
                            }
                        } else {
                            dashboardActivity.showToast("Something went wrong", dashboardActivity);
                        }
                    } catch (JSONException e) {
                        dashboardActivity.showToast("JSONException", dashboardActivity);
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                    }
                },
                error -> {
                    dashboardActivity.errorHandle1(error.networkResponse);
                    dashboardActivity.hideProgressDialog();
                }) {


            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<String, String>();

                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("Version", String.valueOf(BuildConfig.VERSION_CODE));
                // map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(dashboardActivity);
        requestQueue.add(stringRequest);
    }


    private void EditTask(TaskModel taskModel) {

        Intent update_task = new Intent(dashboardActivity, TaskCreateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(ConstantKey.TASK, taskModel);
        bundle.putString(ConstantKey.TITLE, ConstantKey.CREATE_TASK);
        bundle.putString(ConstantKey.SLUG, taskModel.getSlug());
        bundle.putBoolean(ConstantKey.DRAFT_JOB, true);
        update_task.putExtras(bundle);
        startActivityForResult(update_task, ConstantKey.RESULTCODE_UPDATE_TASK);
    }


    public void refreshSort(String rbText) {
        temp_single_choice_selected = rbText.toLowerCase();
        if (temp_single_choice_selected.equalsIgnoreCase(TASK_DRAFT_CASE_ALL_JOB_KEY)) {
            temp_single_choice_selected = TASK_DRAFT_CASE_ALL_JOB_VALUE;
        }
        if (temp_single_choice_selected.equalsIgnoreCase(TASK_ASSIGNED_CASE_UPPER_FIRST)) {
            temp_single_choice_selected = TASK_ASSIGNED_CASE_RELATED_JOB_VALUE;
        }

        single_choice_selected = temp_single_choice_selected;
        temp_single_choice_selected = null;
        onScrollListener.reset();
        totalItem = 0;
        currentPage = 1;
        taskListAdapter.clear();
        getStatusList();

    }


    @Override
    public void onRefresh() {
        swipeRefresh.setRefreshing(true);
        onScrollListener.reset();
        totalItem = 0;
        currentPage = 1;
        taskListAdapter.clear();
        getStatusList();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantKey.RESULTCODE_UPDATE_TASK) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    if (bundle.getBoolean(ConstantKey.UPDATE_TASK)) {
                        onRefresh();
                    }
                }
            }
        }
        if (requestCode == ConstantKey.RESULTCODE_MY_JOBS) {
            onRefresh();
        }
    }

    protected void deleteTask(Data taskModel) {
        swipeRefresh.setRefreshing(true);
        StringRequest stringRequest = new StringRequest(StringRequest.Method.DELETE, Constant.URL_TASKS + "/" + taskModel.getSlug(),
                response -> {
                    Timber.e(response);
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {

                            if (jsonObject.getBoolean("success")) {
                                onRefresh();

                            } else {
                                ((ActivityBase) requireActivity()).showToast("Something went Wrong", requireContext());
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
                            ((ActivityBase) requireActivity()).unauthorizedUser();
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                            if (jsonObject_error.has("message")) {
                                ((ActivityBase) requireActivity()).showToast(jsonObject_error.getString("message"), requireContext());
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            //  ((CredentialActivity)requireActivity()).showToast(message,requireActivity());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ((ActivityBase) requireActivity()).showToast("Something Went Wrong", requireContext());
                    }
                    Timber.e(error.toString());
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
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }

    private Data taskModel;

    private int position = 0;

    @Override
    public void onDraftDeleteButtonClick(View view, Data taskModel, int position) {
        this.taskModel = taskModel;
        this.position = position;
        ConfirmDeleteTaskBottomSheet confirmBottomSheet = new ConfirmDeleteTaskBottomSheet(requireContext());
        confirmBottomSheet.setListener(this);
        confirmBottomSheet.show(requireActivity().getSupportFragmentManager(), "");
    }

    @Override
    public void onDeleteConfirmClick() {
        deleteTask(taskModel);
    }
}
