package com.jobtick.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.Toolbar;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.activities.ActivityBase;
import com.jobtick.activities.DashboardActivity;
import com.jobtick.activities.SearchTaskActivity;
import com.jobtick.activities.TaskCreateActivity;
import com.jobtick.activities.TaskDetailsActivity;
import com.jobtick.adapers.TaskListAdapter;
import com.jobtick.models.TaskModel;
import com.jobtick.pagination.PaginationListener;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.Helper;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.jobtick.pagination.PaginationListener.PAGE_START;
import static com.jobtick.utils.Constant.TASK_ASSIGNED_CASE_UPPER_FIRST;
import static com.jobtick.utils.Constant.TASK_COMPLETED_CASE_UPPER_FIRST;
import static com.jobtick.utils.Constant.TASK_DRAFT_CASE_ALL_JOB_KEY;
import static com.jobtick.utils.Constant.TASK_DRAFT_CASE_ALL_JOB_VALUE;
import static com.jobtick.utils.Constant.TASK_DRAFT_CASE_UPPER_FIRST;
import static com.jobtick.utils.Constant.TASK_OFFERED_CASE_UPPER_FIRST;
import static com.jobtick.utils.Constant.TASK_OPEN_CASE_UPPER_FIRST;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyTasksFragment extends Fragment implements TaskListAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener,
        TaskListAdapter.OnDraftDeleteListener, ConfirmDeleteTaskBottomSheet.NoticeListener {


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_view_status)
    RecyclerView recyclerViewStatus;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    private DashboardActivity dashboardActivity;
    private SessionManager sessionManager;


    private TaskListAdapter taskListAdapter;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;
    ImageView ivNotification;
    TextView toolbar_title;
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
        dashboardActivity = (DashboardActivity) getActivity();
        if (dashboardActivity == null) return;
        toolbar = dashboardActivity.findViewById(R.id.toolbar);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_my_task);
        toolbar.setVisibility(View.VISIBLE);
        ivNotification = dashboardActivity.findViewById(R.id.ivNotification);
        ivNotification.setVisibility(View.GONE);
        toolbar_title = dashboardActivity.findViewById(R.id.toolbar_title);
        toolbar_title.setVisibility(View.VISIBLE);
        toolbar_title.setText("My jobs");

        toolbar_title.setTypeface(ResourcesCompat.getFont(getContext(), R.font.poppins_semi_bold));
        androidx.appcompat.widget.Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.START;
        toolbar_title.setLayoutParams(params);
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

                    Intent intent = new Intent(dashboardActivity, SearchTaskActivity.class);
                    intent.putExtra(ConstantKey.FROM_MY_JOBS_WITH_LOVE, true);
                    dashboardActivity.startActivity(intent);

                    break;
                case R.id.action_filter:

                    //showSingleChoiceDialog();
                    showSortBottomSheet();
                    break;
            }
            return false;
        });

    }

    private void getStatusList() {
        if (single_choice_selected.equals(TASK_DRAFT_CASE_ALL_JOB_VALUE)) {
            toolbar_title.setText(TASK_DRAFT_CASE_ALL_JOB_KEY);


        } else {
            String title = single_choice_selected;
            if (title.equals(TASK_OPEN_CASE_UPPER_FIRST)) {
                title = "Posted";
            }
            toolbar_title.setText(title);
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
                        Timber.e(response);
                        // categoryArrayList.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Timber.e(jsonObject.toString());
                            if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                JSONArray jsonArray_data = jsonObject.getJSONArray("data");
                                for (int i = 0; jsonArray_data.length() > i; i++) {
                                    JSONObject jsonObject_taskModel_list = jsonArray_data.getJSONObject(i);
                                    TaskModel taskModel = new TaskModel().getJsonToModel(jsonObject_taskModel_list, dashboardActivity);
                                    //TODO: here we have a bug, when total items is 21, each page 20, there is just one item to
                                    // get to next page. but we have a null item in list. api returns it correct
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

//                            if(currentPage == 1 && totalPage == 1){
//                                resetTaskListAdapter();
//                            }

                            /*
                             *manage progress view
                             */
                            if (currentPage == PAGE_START) {
                                resetTaskListAdapter();
                            }
                            taskListAdapter.addItems(items);
                            if (items.size() <= 0) {
                                noJobs.setVisibility(View.VISIBLE);
                                recyclerViewStatus.setVisibility(View.GONE);
                            } else {
                                noJobs.setVisibility(View.GONE);
                                recyclerViewStatus.setVisibility(View.VISIBLE);

                            }

                            swipeRefresh.setRefreshing(false);
                            // check weather is last page or not
                            if (currentPage < totalPage) {
                                taskListAdapter.addLoading();
                            } else {
                                if (currentPage == totalPage)
                                    taskListAdapter.removeLoading();
                                isLastPage = true;
                            }
                            isLoading = false;
                            str_search = null;
                        } catch (JSONException e) {
                            str_search = null;
                            dashboardActivity.hideProgressDialog();
                            Timber.e(String.valueOf(e));
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
        Timber.e(stringRequest.getUrl());

    }

    private void resetTaskListAdapter() {
        taskListAdapter = new TaskListAdapter(new ArrayList<>());
        taskListAdapter.setOnItemClickListener(this);
        taskListAdapter.setOnDraftDeleteListener(this);
        recyclerViewStatus.setAdapter(taskListAdapter);
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
            Timber.i("MyTasksFragment Starting Task with slug: %s", obj.getSlug());
        }
    }

    private void getDataFromServer(String slug) {
        dashboardActivity.showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_TASKS + "/" + slug,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
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
                    }
                },
                error -> {
                    dashboardActivity.errorHandle1(error.networkResponse);
                    dashboardActivity.hideProgressDialog();
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


    private void showSortBottomSheet() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.custom_task_filter, null);

        //  new KeyboardUtil(getActivity(), view);

        mBottomSheetDialog = new BottomSheetDialog(dashboardActivity);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        mBottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        AppCompatRadioButton rbAll = view.findViewById(R.id.radioAll);
        AppCompatRadioButton radioDraft = view.findViewById(R.id.radioDraft);
        AppCompatRadioButton radioPosted = view.findViewById(R.id.radioPosted);
        AppCompatRadioButton radioAssigned = view.findViewById(R.id.radioAssigned);
        AppCompatRadioButton radioCompleted = view.findViewById(R.id.radioCompleted);
        AppCompatRadioButton radioOffer = view.findViewById(R.id.radioOffer);


        RelativeLayout relativeCompleted = view.findViewById(R.id.relativeCompleted);
        RelativeLayout relativeAllJobs = view.findViewById(R.id.relativeAllJobs);
        RelativeLayout relativeDraft = view.findViewById(R.id.relativeDraft);
        RelativeLayout relativePosted = view.findViewById(R.id.relativePosted);
        RelativeLayout relativeAssigned = view.findViewById(R.id.relativeAssigned);
        RelativeLayout relativeOffer = view.findViewById(R.id.relativeOffer);

        relativeDraft.setOnClickListener(v -> {
            radioDraft.performClick();
        });

        relativePosted.setOnClickListener(v -> {
            radioPosted.performClick();

        });

        relativeAssigned.setOnClickListener(v -> {
            radioAssigned.performClick();
        });

        relativeOffer.setOnClickListener(v -> {
            radioOffer.performClick();
        });


        relativeAllJobs.setOnClickListener(v -> {
            rbAll.performClick();
        });

        relativeCompleted.setOnClickListener(v -> {
            radioCompleted.performClick();
        });

        if (single_choice_selected.equals(TASK_DRAFT_CASE_ALL_JOB_VALUE)) {
            rbAll.setChecked(true);
        } else if (single_choice_selected.equals(TASK_ASSIGNED_CASE_UPPER_FIRST)) {
            radioAssigned.setChecked(true);
        } else if (single_choice_selected.equals(TASK_OPEN_CASE_UPPER_FIRST)) {
            radioPosted.setChecked(true);
        } else if (single_choice_selected.equals(TASK_OFFERED_CASE_UPPER_FIRST)) {
            radioOffer.setChecked(true);
        } else if (single_choice_selected.equals(TASK_DRAFT_CASE_UPPER_FIRST)) {
            radioDraft.setChecked(true);
        } else if (single_choice_selected.equals(TASK_COMPLETED_CASE_UPPER_FIRST)) {
            radioCompleted.setChecked(true);
        }


        radioDraft.setOnClickListener(v -> {
            radioDraft.setChecked(true);
            rbAll.setChecked(false);
            radioAssigned.setChecked(false);
            radioPosted.setChecked(false);
            radioAssigned.setChecked(false);
            radioCompleted.setChecked(false);
            radioOffer.setChecked(false);

            refreshSort(radioDraft.getTag().toString());

            mBottomSheetDialog.dismiss();

        });

        rbAll.setOnClickListener(v -> {
            radioDraft.setChecked(false);
            rbAll.setChecked(true);
            radioAssigned.setChecked(false);
            radioPosted.setChecked(false);
            radioCompleted.setChecked(false);
            radioOffer.setChecked(false);
            refreshSort(rbAll.getTag().toString());
            mBottomSheetDialog.dismiss();

        });


        radioAssigned.setOnClickListener(v -> {
            radioDraft.setChecked(false);
            rbAll.setChecked(false);
            radioAssigned.setChecked(true);
            radioPosted.setChecked(false);
            radioCompleted.setChecked(false);
            radioOffer.setChecked(false);
            refreshSort(radioAssigned.getTag().toString());
            mBottomSheetDialog.dismiss();

        });


        radioPosted.setOnClickListener(v -> {
            radioDraft.setChecked(false);
            rbAll.setChecked(false);
            radioAssigned.setChecked(false);
            radioPosted.setChecked(true);
            radioCompleted.setChecked(false);
            radioOffer.setChecked(false);
            refreshSort(radioPosted.getTag().toString());

            mBottomSheetDialog.dismiss();

        });


        radioCompleted.setOnClickListener(v ->
        {

            radioDraft.setChecked(false);
            rbAll.setChecked(false);
            radioAssigned.setChecked(false);
            radioPosted.setChecked(false);
            radioCompleted.setChecked(true);
            radioOffer.setChecked(false);
            refreshSort(radioCompleted.getTag().toString());

            mBottomSheetDialog.dismiss();

        });

        radioOffer.setOnClickListener(v -> {

            radioDraft.setChecked(false);
            rbAll.setChecked(false);
            radioAssigned.setChecked(false);
            radioPosted.setChecked(false);
            radioCompleted.setChecked(false);
            radioOffer.setChecked(true);
            refreshSort(radioOffer.getTag().toString());
            mBottomSheetDialog.dismiss();


        });


        // set background transparent
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        RadioGroup radioFilter = view.findViewById(R.id.radioFilter);


        radioFilter.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb = (RadioButton) view.findViewById(checkedId);
            temp_single_choice_selected = rb.getText().toString();
            if (temp_single_choice_selected.equals(TASK_DRAFT_CASE_ALL_JOB_KEY)) {
                temp_single_choice_selected = TASK_DRAFT_CASE_ALL_JOB_VALUE;
            }

            single_choice_selected = temp_single_choice_selected;
            temp_single_choice_selected = null;
            currentPage = PAGE_START;
            isLastPage = false;
            taskListAdapter.clear();
            getStatusList();


        });


        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });

       /* AddTagSheetFragment addPhotoBottomDialogFragment =
                AddTagSheetFragment.newInstance();
        addPhotoBottomDialogFragment.show(taskCreateActivity.getSupportFragmentManager(),
                "add_photo_dialog_fragment");
*/

    }

    public void refreshSort(String rbText) {
        temp_single_choice_selected = rbText;
        if (temp_single_choice_selected.equals(TASK_DRAFT_CASE_ALL_JOB_KEY)) {
            temp_single_choice_selected = TASK_DRAFT_CASE_ALL_JOB_VALUE;
        }

        single_choice_selected = temp_single_choice_selected;
        temp_single_choice_selected = null;
        currentPage = PAGE_START;
        isLastPage = false;
        taskListAdapter.clear();
        getStatusList();

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

    protected void deleteTask(TaskModel taskModel) {
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
                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ((ActivityBase) requireActivity()).showToast("Something Went Wrong", requireContext());
                    }
                    Timber.e(error.toString());
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();

                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }

    private TaskModel taskModel;

    @Override
    public void onDraftDeleteButtonClick(View view, TaskModel taskModel) {
        this.taskModel = taskModel;
        ConfirmDeleteTaskBottomSheet confirmBottomSheet = new ConfirmDeleteTaskBottomSheet(requireContext());
        confirmBottomSheet.setListener(this);
        confirmBottomSheet.show(requireActivity().getSupportFragmentManager(), "");
    }

    @Override
    public void onDeleteConfirmClick() {
        deleteTask(taskModel);
    }
}
