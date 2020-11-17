package com.jobtick.fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.jobtick.R;
import com.jobtick.activities.DashboardActivity;
import com.jobtick.activities.FiltersActivity;
import com.jobtick.activities.MapViewActivity;
import com.jobtick.activities.SearchCategoryActivity;
import com.jobtick.activities.TaskDetailsActivity;
import com.jobtick.adapers.FilterAdapter;
import com.jobtick.adapers.TaskListAdapter;
import com.jobtick.models.FilterModel;
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
import butterknife.OnClick;

import static com.jobtick.pagination.PaginationListener.PAGE_START;

/**
 * A simple {@link Fragment} subclass.
 */
public class BrowseFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, TaskListAdapter.OnItemClickListener,
        SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    @BindView(R.id.recycler_view_filters)
    RecyclerView recyclerViewFilters;
    @BindView(R.id.recycler_view_browse)
    RecyclerView recyclerViewBrowse;
    @BindView(R.id.lyt_btn_filters)
    LinearLayout lytBtnFilters;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.txt_filters)
    TextView txtFilters;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.ivMapView)
    FloatingActionButton ivMapView;

    private DashboardActivity dashboardActivity;
    private ArrayList<String> filters = new ArrayList<>();
    private FilterModel filterModel = new FilterModel();
    private FilterAdapter filterAdapter;
    private SessionManager sessionManager;
    private TaskListAdapter taskListAdapter;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;
    private Toolbar toolbar;
    private boolean isFabHide = false;
    private ArrayList<TaskModel> taskArrayList = new ArrayList<>();

    public BrowseFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_browse, container, false);
        ButterKnife.bind(this, view);
        initToolbar();
        return view;
    }

    private void initToolbar() {
        dashboardActivity = (DashboardActivity) getActivity();
        if (dashboardActivity != null) {
            toolbar = dashboardActivity.findViewById(R.id.toolbar);
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_browse_task);
            ImageView ivNotification = dashboardActivity.findViewById(R.id.ivNotification);
            ivNotification.setVisibility(View.GONE);
            TextView toolbar_title = dashboardActivity.findViewById(R.id.toolbar_title);
            toolbar_title.setVisibility(View.VISIBLE);

            toolbar_title.setText(R.string.explore);

            toolbar_title.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.poppins_medium));
            toolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey_100));
            androidx.appcompat.widget.Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.LEFT;
            toolbar_title.setLayoutParams(params);

            toolbar.post(() -> {
                Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, null);
                toolbar.setNavigationIcon(d);
            });
            sessionManager = new SessionManager(dashboardActivity);
        }
        setHasOptionsMenu(true);
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
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_search) {
                Intent creating_task = new Intent(getActivity(), SearchCategoryActivity.class);
                startActivity(creating_task);
            }
            return false;
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
        taskListAdapter = new TaskListAdapter(taskArrayList);
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
        recyclerViewFilters.setAdapter(filterAdapter);

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
            if (filterModel.getLocation().length() > 10) {
                filters.add(filterModel.getLocation().substring(0, 10) + " - " + filterModel.getDistance() + " KM");
            } else {
                filters.add(filterModel.getLocation() + " - " + filterModel.getDistance() + " KM");
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
    }

    @OnClick(R.id.lyt_btn_filters)
    public void onViewClicked() {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(dashboardActivity, FiltersActivity.class);
        bundle.putParcelable(Constant.FILTER, filterModel);
        intent.putExtras(bundle);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 101) {
            if (data != null && data.getExtras() != null) {
                Bundle bundle = data.getExtras();
                filterModel = (FilterModel) bundle.getParcelable(Constant.FILTER);
                sessionManager.setFilter(filterModel);
                setFilterData();
                onRefresh();
            }
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
        queryParameter = queryParameter + "&hide_assigned=true";

        ArrayList<TaskModel> items = new ArrayList<>();
        Helper.closeKeyboard(dashboardActivity);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_TASKS + "?page=" + currentPage + queryParameter,
                response -> {
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

                        if (currentPage != PAGE_START)
                            taskListAdapter.removeLoading();
                        taskListAdapter.addItems(items);

                        taskArrayList = items;

                        swipeRefresh.setRefreshing(false);
                        // check weather is last page or not
                        if (currentPage < totalPage) {
                            taskListAdapter.addLoading();
                        } else {
                            isLastPage = true;
                        }
                        isLoading = false;
                    } catch (JSONException e) {
                        dashboardActivity.hideProgressDialog();
                        Log.e("EXCEPTION", String.valueOf(e));
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
    public void onRefresh() {
        currentPage = PAGE_START;
        isLastPage = false;
        taskListAdapter.clear();
        doApiCall();
    }

    @Override
    public void onItemClick(View view, TaskModel obj, int position, String action) {
        Intent intent = new Intent(dashboardActivity, TaskDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ConstantKey.SLUG, obj.getSlug());
        bundle.putInt(ConstantKey.USER_ID, obj.getPoster().getId());
        intent.putExtras(bundle);
        startActivity(intent);
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
        Log.e("Close", "Called");
        toolbar.getMenu().findItem(R.id.action_map).setVisible(true);
        return true;
    }
}
