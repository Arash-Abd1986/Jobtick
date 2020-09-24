package com.jobtick.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jobtick.R;
import com.jobtick.activities.DashboardActivity;
import com.jobtick.activities.TaskCreateActivity;
import com.jobtick.activities.TaskDetailsActivity;
import com.jobtick.adapers.TaskListAdapter;
import com.jobtick.models.TaskModel;
import com.jobtick.pagination.PaginationListener;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.Helper;
import com.jobtick.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.droidsonroids.gif.GifImageView;
import timber.log.Timber;

import static com.jobtick.pagination.PaginationListener.PAGE_START;
import static com.jobtick.utils.Constant.TASK_DRAFT_CASE_ALL_JOB_KEY;
import static com.jobtick.utils.Constant.TASK_DRAFT_CASE_ALL_JOB_VALUE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyTasksFragment extends Fragment implements TaskListAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.recycler_view_status)
    RecyclerView recyclerViewStatus;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    private DashboardActivity dashboardActivity;
    private SessionManager sessionManager;
    private View view;

    @BindView(R.id.ivNoPosst)
    public GifImageView ivNoPost;

    private TaskListAdapter taskListAdapter;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;


    private String[] status = new String[]{
            TASK_DRAFT_CASE_ALL_JOB_KEY,
            Constant.TASK_DRAFT_CASE_UPPER_FIRST,
            Constant.TASK_CANCELLED_CASE_UPPER_FIRST,
            Constant.TASK_ASSIGNED_CASE_UPPER_FIRST,
            Constant.TASK_OPEN_CASE_UPPER_FIRST,
            Constant.TASK_PENDING_CASE_UPPER_FIRST,
            Constant.TASK_COMPLETED_CASE_UPPER_FIRST,
            Constant.TASK_OFFERED_CASE_UPPER_FIRST
    };

    private String single_choice_selected = null;
    private String temp_single_choice_selected = null;
    private String str_search = null;
    private String temp_str_search = null;
    private Toolbar toolbar;

    public MyTasksFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_tasks, container, false);
        ButterKnife.bind(this, view);
        swipeRefresh.setOnRefreshListener(this);
        dashboardActivity = (DashboardActivity) getActivity();
        if (dashboardActivity != null) {
            toolbar = dashboardActivity.findViewById(R.id.toolbar);
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_my_task);
        }
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(dashboardActivity);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(dashboardActivity);
        recyclerViewStatus.setLayoutManager(layoutManager);
        taskListAdapter = new TaskListAdapter(dashboardActivity, new ArrayList<>());
        recyclerViewStatus.setAdapter(taskListAdapter);
        taskListAdapter.setOnItemClickListener(this);
        swipeRefresh.setRefreshing(true);
        single_choice_selected = TASK_DRAFT_CASE_ALL_JOB_VALUE;
        getStatusList();
        /*
         * add scroll listener while user reach in bottom load more will call
         */
        recyclerViewStatus.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                getStatusList();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });


        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_search:
                    Menu menu = toolbar.getMenu();
                    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
                    searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            str_search = query;
                            onRefresh();
                            return true;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            str_search = newText;
                            return false;
                        }
                    });

                    break;
                case R.id.action_filter:

                    showSingleChoiceDialog();

                    break;
            }
            return false;
        });

    }

    private void getStatusList() {
        if (single_choice_selected.equals(TASK_DRAFT_CASE_ALL_JOB_VALUE)) {
            toolbar.setTitle(TASK_DRAFT_CASE_ALL_JOB_KEY);


        } else {
            toolbar.setTitle(single_choice_selected);

        }
        String query_parameter = "";
        if (str_search != null) {
            query_parameter += "&search_query=" + str_search;
        }

        query_parameter += "&mytask=" + single_choice_selected.toLowerCase();

        swipeRefresh.setRefreshing(true);
        ArrayList<TaskModel> items = new ArrayList<>();
        Helper.closeKeyboard(dashboardActivity);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_TASKS + "?page=" + currentPage + query_parameter,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("responce_url", response);
                        // categoryArrayList.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.e("json", jsonObject.toString());
                            if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                JSONArray jsonArray_data = jsonObject.getJSONArray("data");
                                for (int i = 0; jsonArray_data.length() > i; i++) {
                                    JSONObject jsonObject_taskModel_list = jsonArray_data.getJSONObject(i);
                                    TaskModel taskModel = new TaskModel().getJsonToModel(jsonObject_taskModel_list, dashboardActivity);
                                    items.add(taskModel);
                                }
                            } else {
                                dashboardActivity.showToast("some went to wrong", dashboardActivity);
                                return;
                            }

                            if (jsonObject.has("meta") && !jsonObject.isNull("meta")) {
                                JSONObject jsonObject_meta = jsonObject.getJSONObject("meta");
                                totalPage = jsonObject_meta.getInt("last_page");
                                Constant.PAGE_SIZE = jsonObject_meta.getInt("per_page");
                            }

                            /*
                             *manage progress view
                             */
                            if (currentPage != PAGE_START)
                                taskListAdapter.removeLoading();

                            if (items.size() <= 0) {
                                ivNoPost.setVisibility(View.VISIBLE);
                                recyclerViewStatus.setVisibility(View.GONE);
                            } else {
                                ivNoPost.setVisibility(View.GONE);
                                recyclerViewStatus.setVisibility(View.VISIBLE);

                            }
                            taskListAdapter.addItems(items);

                            swipeRefresh.setRefreshing(false);
                            // check weather is last page or not
                            if (currentPage < totalPage) {
                                taskListAdapter.addLoading();
                            } else {
                                isLastPage = true;
                            }
                            isLoading = false;
                            str_search = null;
                        } catch (JSONException e) {
                            str_search = null;
                            dashboardActivity.hidepDialog();
                            Log.e("EXCEPTION", String.valueOf(e));
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  swipeRefresh.setRefreshing(false);
                        str_search = null;
                        swipeRefresh.setRefreshing(false);
                        dashboardActivity.errorHandle1(error.networkResponse);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("Authorization", "Bearer " + sessionManager.getAccessToken());
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(dashboardActivity);
        requestQueue.add(stringRequest);
        Log.e("url", stringRequest.getUrl());

    }


    @Override
    public void onItemClick(View view, TaskModel obj, int position, String action) {
        if (obj.getStatus().toLowerCase().equalsIgnoreCase(Constant.TASK_DRAFT.toLowerCase())) {
            getDataFromServer(obj.getSlug());
        } else {
            Intent intent = new Intent(dashboardActivity, TaskDetailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(ConstantKey.SLUG, obj.getSlug());
            bundle.putInt(ConstantKey.USER_ID, obj.getPoster().getId());
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private void getDataFromServer(String slug) {
        dashboardActivity.showpDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_TASKS + "/" + slug,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Timber.e(response);
                        dashboardActivity.hidepDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.e("json", jsonObject.toString());
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
                    }
                },
                error -> {
                    dashboardActivity.errorHandle1(error.networkResponse);
                    dashboardActivity.hidepDialog();
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();

                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
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
        update_task.putExtras(bundle);
        startActivityForResult(update_task, ConstantKey.RESULTCODE_UPDATE_TASK);


    }

    private void showSingleChoiceDialog() {


        temp_single_choice_selected = TASK_DRAFT_CASE_ALL_JOB_VALUE;


        // custom dialog
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_task_filter);

        // set the custom dialog components - text, image and button


        RadioGroup radioFilter = dialog.findViewById(R.id.radioFilter);


        radioFilter.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb = (RadioButton) dialog.findViewById(checkedId);
            temp_single_choice_selected = rb.getText().toString();
            if (temp_single_choice_selected.equals(TASK_DRAFT_CASE_ALL_JOB_KEY)) {
                temp_single_choice_selected = TASK_DRAFT_CASE_ALL_JOB_VALUE;
            }

        });


        LinearLayout lyt_btn_cancel = dialog.findViewById(R.id.lyt_btn_cancel);
        lyt_btn_cancel.setOnClickListener(v -> {
            temp_single_choice_selected = null;

            dialog.dismiss();
        });


        LinearLayout lyt_btn_ok = dialog.findViewById(R.id.lyt_btn_ok);
        lyt_btn_ok.setOnClickListener(v -> {


            if (temp_single_choice_selected.equals(TASK_DRAFT_CASE_ALL_JOB_KEY)) {
                temp_single_choice_selected = TASK_DRAFT_CASE_ALL_JOB_VALUE;
            }
            single_choice_selected = temp_single_choice_selected;
            temp_single_choice_selected = null;
            currentPage = PAGE_START;
            isLastPage = false;
            taskListAdapter.clear();
            getStatusList();

            dialog.dismiss();
        });


        dialog.show();



     /*   AlertDialog.Builder builder = new AlertDialog.Builder(dashboardActivity);

        ViewGroup viewGroup = dashboardActivity.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.c, viewGroup, false);


        builder.setTitle("Sort By");
        builder.setSingleChoiceItems(status, 0, (dialogInterface, i) -> temp_single_choice_selected = status[i]);

        builder.setPositiveButton(R.string.OK, (dialogInterface, i) -> {
            if (single_choice_selected.equals(TASK_DRAFT_CASE_ALL_JOB_KEY)) {
                single_choice_selected = TASK_DRAFT_CASE_ALL_JOB_VALUE;
            }
            single_choice_selected = temp_single_choice_selected;
            temp_single_choice_selected = null;
            currentPage = PAGE_START;
            isLastPage = false;
            taskListAdapter.clear();
            getStatusList();
            dialogInterface.dismiss();
        });
        builder.setNegativeButton(R.string.CANCEL, (dialog, which) -> {
            temp_single_choice_selected = null;
            dialog.dismiss();
        });
        builder.show();*/
    }


    @Override
    public void onRefresh() {
        swipeRefresh.setRefreshing(true);
        currentPage = PAGE_START;
        isLastPage = false;
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
    }
}
