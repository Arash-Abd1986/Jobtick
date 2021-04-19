package com.jobtick.android.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.jobtick.android.BuildConfig;
import com.jobtick.android.R;

import android.annotation.SuppressLint;

import com.jobtick.android.activities.DashboardActivity;
import com.jobtick.android.activities.FiltersActivity;
import com.jobtick.android.activities.MapViewActivity;
import com.jobtick.android.activities.SearchTaskActivity;
import com.jobtick.android.activities.TaskDetailsActivity;
import com.jobtick.android.adapers.FilterAdapter;
import com.jobtick.android.adapers.TaskListAdapter;
import com.jobtick.android.adapers.TaskListAdapterV2;
import com.jobtick.android.models.FilterModel;
import com.jobtick.android.models.TaskModel;
import com.jobtick.android.models.response.myjobs.Data;
import com.jobtick.android.models.response.myjobs.MyJobsResponse;
import com.jobtick.android.pagination.PaginationListener;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.ConstantKey;
import com.jobtick.android.utils.Helper;
import com.jobtick.android.utils.SessionManager;

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

import static com.jobtick.android.pagination.PaginationListener.PAGE_START;
import static com.jobtick.android.utils.Constant.MAX_FILTER_DISTANCE_IN_KILOMETERS;

/**
 * A simple {@link Fragment} subclass.
 */
public class BrowseFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, TaskListAdapterV2.OnItemClickListener,
        SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_view_filters)
    RecyclerView recyclerViewFilters;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_view_browse)
    RecyclerView recyclerViewBrowse;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_filters)
    LinearLayout lytBtnFilters;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lin_search)
    LinearLayout linSearch;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_filters)
    TextView txtFilters;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_search_categoreis)
    TextView edtSearch;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btnVoice)
    ImageView btnVoice;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.ivMapView)
    FloatingActionButton ivMapView;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.empty_filter)
    RelativeLayout empty_filter;

    private DashboardActivity dashboardActivity;
    private final ArrayList<String> filters = new ArrayList<>();
    private FilterModel filterModel = new FilterModel();
    private FilterAdapter filterAdapter;
    private SessionManager sessionManager;
    private TaskListAdapterV2 taskListAdapter;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private int totalItem = 10;
    private boolean isLoading = false;
    private Toolbar toolbar;
    private LinearLayout linFilterExplore;
    private TextView txtFilter;
    private boolean isFabHide = false;
    private ArrayList<Data> taskArrayList = new ArrayList<>();

    public BrowseFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_browse, container, false);
        ButterKnife.bind(this, view);
        initToolbar();
        sessionManager = new SessionManager(dashboardActivity);
        return view;
    }

    private void initToolbar() {
        dashboardActivity = (DashboardActivity) requireActivity();
        if (dashboardActivity == null) return;
        toolbar = dashboardActivity.findViewById(R.id.toolbar);
        toolbar.getMenu().clear();
        //toolbar.inflateMenu(R.menu.menu_browse_task);
        ImageView ivNotification = dashboardActivity.findViewById(R.id.ivNotification);
        linFilterExplore = dashboardActivity.findViewById(R.id.lin_filter_explore);
        txtFilter = dashboardActivity.findViewById(R.id.txt_filter);
        ivNotification.setVisibility(View.GONE);
        linFilterExplore.setVisibility(View.VISIBLE);
        linFilterExplore.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            Intent intent = new Intent(dashboardActivity, FiltersActivity.class);
            bundle.putParcelable(Constant.FILTER, filterModel);
            intent.putExtras(bundle);
            startActivityForResult(intent, 101);
        });
        TextView toolbar_title = dashboardActivity.findViewById(R.id.toolbar_title);
        toolbar_title.setVisibility(View.VISIBLE);
        toolbar_title.setText(R.string.explore);
        toolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.backgroundLightGrey));
        toolbar_title.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.roboto_semi_bold));
        toolbar_title.setTextSize(20F);
        androidx.appcompat.widget.Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.START;
        toolbar_title.setLayoutParams(params);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        linFilterExplore.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        linFilterExplore.setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initFilter();
        initBrowse();
        doApiCall();
        setCTAListener();
        setAnimationFab();
    }

    private void setCTAListener() {
        linSearch.setOnClickListener(v -> {
            Intent creating_task = new Intent(requireActivity(), SearchTaskActivity.class);
            startActivity(creating_task);
        });
        edtSearch.setOnClickListener(v -> {
            Intent creating_task = new Intent(requireActivity(), SearchTaskActivity.class);
            startActivity(creating_task);
        });
        btnVoice.setOnClickListener(v -> {
            Intent creating_task = new Intent(requireActivity(), SearchTaskActivity.class);
            startActivity(creating_task);
        });

        ivMapView.setOnClickListener(v -> {
            Intent intent = new Intent(dashboardActivity, MapViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(ConstantKey.TASK, taskArrayList);
            intent.putExtras(bundle);
            dashboardActivity.startActivity(intent);
        });


    }

    private void initBrowse() {
        swipeRefresh.setOnRefreshListener(this);
        recyclerViewBrowse.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewBrowse.setLayoutManager(layoutManager);
        taskListAdapter = new TaskListAdapterV2(taskArrayList, null);
        recyclerViewBrowse.setAdapter(taskListAdapter);
        taskListAdapter.setOnItemClickListener(this);
        swipeRefresh.setRefreshing(true);

        recyclerViewBrowse.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                doApiCall();
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
    }

    private void initFilter() {
        recyclerViewFilters.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        recyclerViewFilters.setHasFixedSize(true);

        filterAdapter = new FilterAdapter(filters);
        filterAdapter.setmOnFilterDeleteListener(filters::clear);
        recyclerViewFilters.setAdapter(filterAdapter);
        txtFilter.setText(filters.size() + " Filter");
        if (sessionManager.getFilter() != null) {
            filterModel = sessionManager.getFilter();
        }
        setFilterData();
    }

    private void setAnimationFab() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            recyclerViewBrowse.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (scrollX < oldScrollX) {
                    animateFab(false);
                }
                if (scrollX > oldScrollX) {
                    animateFab(true);
                }
            });
        }
    }

    private void animateFab(final boolean hide) {
        if (isFabHide && hide || !isFabHide && !hide) return;
        isFabHide = hide;
        int moveX = hide ? (2 * appbar.getWidth()) : 0;
        appbar.animate().translationY(-moveX).setStartDelay(100).setDuration(300).start();
    }

    private void setFilterData() {
        filters.clear();
        if (filterModel.getSection() != null) {
            filters.add(filterModel.getSection());
        }
        if (filterModel.getLocation() != null && filterModel.getDistance() != null) {
            String distance = "100 KM+";
            if (!filterModel.getDistance().equals(Integer.toString(MAX_FILTER_DISTANCE_IN_KILOMETERS))) {
                distance = filterModel.getDistance() + " KM";
            }
            if (filterModel.getLocation().length() > 10) {
                filters.add(filterModel.getLocation().substring(0, 10) + " - " + distance);
            } else {
                filters.add(filterModel.getLocation() + " - " + distance);
            }

        }
        if (filterModel.getPrice() != null) {
            filters.add(filterModel.getPrice());
        }
        if (filterModel.getTask_open() != null) {
            filters.add(filterModel.getTask_open());
        }
        if (filters.size() != 0) {
            filterAdapter.notifyDataSetChanged();
        }
        txtFilter.setText(filters.size() + " Filter");
    }

//    @OnClick({R.id.lyt_search_new})
//    public void onViewClicked() {
//        Bundle bundle = new Bundle();
//        Intent intent = new Intent(dashboardActivity, FiltersActivity.class);
//        bundle.putParcelable(Constant.FILTER, filterModel);
//        intent.putExtras(bundle);
//        startActivityForResult(intent, 101);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (data != null && data.getExtras() != null) {
                Bundle bundle = data.getExtras();
                filterModel = (FilterModel) bundle.getParcelable(Constant.FILTER);
                sessionManager.setFilter(filterModel);
                setFilterData();
                onRefresh();
            }
        } else if (requestCode == 202) {
            //TODO: Do something to show user that he offered on the job.
        }
    }

    private void doApiCall() {
        String queryParameter = "";
        if (filterModel.getQuery() != null && !filterModel.getQuery().equalsIgnoreCase("")) {
            queryParameter = "&search_query=" + filterModel.getQuery();
        }
        if (filterModel.getSection().equalsIgnoreCase(Constant.FILTER_ALL)) {
            queryParameter = queryParameter + "&task_type=" + Constant.FILTER_ALL_QUERY;
            queryParameter = queryParameter + "&distance=" + filterModel.getDistance() + "km";
            String[] price = filterModel.getPrice().replace("$", "").replace(",", "").split("-");
            queryParameter = queryParameter + "&min_price=" + price[0].trim() + "&max_price=" + price[1].trim();
            queryParameter = queryParameter + "&current_lat=" + filterModel.getLatitude();
            queryParameter = queryParameter + "&current_lng=" + filterModel.getLogitude();
            if (filterModel.getTask_open() != null) {
                queryParameter = queryParameter + "&hide_assigned=true";
            }
        } else if (filterModel.getSection().equalsIgnoreCase(Constant.FILTER_REMOTE)) {
            queryParameter = queryParameter + "&task_type=" + Constant.FILTER_REMOTE_QUERY;
            String[] price = filterModel.getPrice().replace("$", "").replace(",", "").split("-");
            queryParameter = queryParameter + "&min_price=" + price[0].trim() + "&max_price=" + price[1].trim();
            if (filterModel.getTask_open() != null) {
                queryParameter = queryParameter + "&hide_assigned=true";
            }
        } else if (filterModel.getSection().equalsIgnoreCase(Constant.FILTER_IN_PERSON)) {
            queryParameter = queryParameter + "&task_type=" + Constant.FILTER_IN_PERSON_QUERY;
            queryParameter = queryParameter + "&distance=" + filterModel.getDistance() + "km";
            String[] price = filterModel.getPrice().replace("$", "").replace(",", "").split("-");
            queryParameter = queryParameter + "&min_price=" + price[0].trim() + "&max_price=" + price[1].trim();
            queryParameter = queryParameter + "&current_lat=" + filterModel.getLatitude();
            queryParameter = queryParameter + "&current_lng=" + filterModel.getLogitude();
            if (filterModel.getTask_open() != null) {
                queryParameter = queryParameter + "&hide_assigned=true";
            }
        }
//        queryParameter = queryParameter + "&hide_assigned=true";

        Helper.closeKeyboard(dashboardActivity);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_TASKS_v2 + "?page=" + currentPage + queryParameter,
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
                        totalPage = myJobsResponse.getTotal();

                        taskListAdapter.addItems(myJobsResponse.getData(), totalItem);
                        isLastPage = taskListAdapter.getItemCount() == totalItem;

                        swipeRefresh.setRefreshing(false);
                        isLoading = false;
                        if (totalItem == 0) {
                            empty_filter.setVisibility(View.VISIBLE);
                            recyclerViewBrowse.setVisibility(View.GONE);
                        } else {
                            empty_filter.setVisibility(View.GONE);
                            recyclerViewBrowse.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        dashboardActivity.hideProgressDialog();
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                    }
                },
                error -> {
                    swipeRefresh.setRefreshing(false);
                    dashboardActivity.errorHandle1(error.networkResponse);
                }) {
            @Override
            public Map<String, String> getHeaders() {
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


    @Override
    public void onRefresh() {
        swipeRefresh.setRefreshing(true);
        currentPage = PAGE_START;
        isLastPage = false;
        taskListAdapter.clear();
        doApiCall();
    }

    @Override
    public void onItemClick(View view, Data obj, int position, String action) {
        Intent intent = new Intent(dashboardActivity, TaskDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ConstantKey.SLUG, obj.getSlug());
        //    bundle.putInt(ConstantKey.USER_ID, obj.getPoster().getId());
        intent.putExtras(bundle);
        startActivityForResult(intent, 202);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        filterModel.setQuery(query);
        onRefresh();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        filterModel.setQuery(newText);
        return false;
    }

    @Override
    public boolean onClose() {
        toolbar.getMenu().findItem(R.id.action_map).setVisible(true);
        return true;
    }
}
