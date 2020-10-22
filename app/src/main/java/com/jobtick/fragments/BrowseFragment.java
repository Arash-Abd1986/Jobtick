package com.jobtick.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jobtick.R;
import com.jobtick.TextView.TextViewRegular;
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
    TextViewRegular txtFilters;
    @BindView(R.id.appbar)
    AppBarLayout appbar;

    @BindView(R.id.ivMapView)
    FloatingActionButton ivMapView;
    private DashboardActivity dashboardActivity;
    private ArrayList<String> filters;
    private FilterModel filterModel;
    private FilterAdapter filterAdapter;
    private SessionManager sessionManager;

    private TaskListAdapter taskListAdapter;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;
    private Toolbar toolbar;

    ArrayList<TaskModel> taskArrayList;
   // private SearchView searchView;

    ImageView ivNotification;
    TextView toolbar_title;

    public BrowseFragment() {
        // Required empty public constructor
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_browse_1, container, false);
        ButterKnife.bind(this, view);
        dashboardActivity = (DashboardActivity) getActivity();
        if (dashboardActivity != null) {
            toolbar = dashboardActivity.findViewById(R.id.toolbar);
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_browse_task);
            ivNotification = dashboardActivity.findViewById(R.id.ivNotification);
            ivNotification.setVisibility(View.GONE);
            toolbar_title = dashboardActivity.findViewById(R.id.toolbar_title);
            toolbar_title.setVisibility(View.VISIBLE);

            toolbar_title.setText("Explore");

            toolbar_title.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.poppins_medium));
            toolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey_100));
            androidx.appcompat.widget.Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.LEFT;
            toolbar_title.setLayoutParams(params);
        }


        taskArrayList = new ArrayList<>();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        filters = new ArrayList<>();
        filterModel = new FilterModel();
        recyclerViewFilters.setLayoutManager(new LinearLayoutManager(dashboardActivity, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewFilters.setHasFixedSize(true);
        sessionManager = new SessionManager(dashboardActivity);
        filterAdapter = new FilterAdapter(dashboardActivity, filters);
        recyclerViewFilters.setAdapter(filterAdapter);
        if (sessionManager.getFilter() != null) {
            filterModel = sessionManager.getFilter();
        }
        setFilterData();

        swipeRefresh.setOnRefreshListener(this);
        recyclerViewBrowse.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(dashboardActivity);
        recyclerViewBrowse.setLayoutManager(layoutManager);
        taskListAdapter = new TaskListAdapter(dashboardActivity, new ArrayList<>());
        recyclerViewBrowse.setAdapter(taskListAdapter);
        taskListAdapter.setOnItemClickListener(this);
        swipeRefresh.setRefreshing(true);
        doApiCall();
        /*
         * add scroll listener while user reach in bottom load more will call
         */
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

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_search:
//                        toolbar.getMenu().findItem(R.id.action_map).setVisible(false);
//                        Menu menu = toolbar.getMenu();
//                        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//                        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
//                        searchView.setOnQueryTextListener(BrowseFragment.this);

                        Intent creating_task = new Intent(getActivity(), SearchCategoryActivity.class);
                        startActivity(creating_task);
                        // searchView.setOnCloseListener(BrowseFragment.this);
                        if (item.collapseActionView()) {
                            Log.e("Close", "Called");
                        }
                        break;
                   /* case R.id.action_map:
                        Intent intent = new Intent(dashboardActivity, MapViewActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList(ConstantKey.TASK, taskArrayList);
                        intent.putExtras(bundle);
                        dashboardActivity.startActivity(intent);
                        break;*/
                }
                return false;
            }
        });

        ivMapView.setOnClickListener(v -> {
            Intent intent = new Intent(dashboardActivity, MapViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(ConstantKey.TASK, taskArrayList);
            intent.putExtras(bundle);
            dashboardActivity.startActivity(intent);
        });


        setAnimation();
    }


    private void setAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            recyclerViewBrowse.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (scrollX < oldScrollX) { // up
                    Log.e("scrollx", "up" + scrollX);
                    Log.e("scrollxold", "up" + oldScrollX);
                    animateFab(false);
                }
                if (scrollX > oldScrollX) { // down
                    Log.e("scrollx", "down" + scrollX);
                    Log.e("scrollxold", "down" + oldScrollX);
                    animateFab(true);
                }

            });
        }
    }

    private boolean isFabHide = false;

    private void animateFab(final boolean hide) {
        if (isFabHide && hide || !isFabHide && !hide) return;
        isFabHide = hide;

        int moveX = hide ? (2 * appbar.getWidth()) : 0;
        Log.e("width", "" + moveX);


        appbar.animate().translationY(-moveX).setStartDelay(100).setDuration(300).start();

        /*appbar.animate().translationX(-moveX).setStartDelay(100).setDuration(300).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                Log.e("Start1", "called");
                if (moveX > 0) {
                    ViewAnimation.collapse(appbar);
                } else {
                    ViewAnimation.expand(appbar);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.e("Start2", "called");
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.e("Start3", "called");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.e("Start4", "called");
            }
        }).start();*/
    /*    if(hide){
            ViewAnimation.collapse(txtFilters);
        }else{
            ViewAnimation.expand(txtFilters);
        }*/
        // cardMakeAnOffer.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
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
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        swipeRefresh.setRefreshing(false);
                        dashboardActivity.errorHandle1(error.networkResponse);
                    }
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

    /**
     * Called when the user submits the query. This could be due to a key press on the
     * keyboard or due to pressing a submit button.
     * The listener can override the standard behavior by returning true
     * to indicate that it has handled the submit request. Otherwise return false to
     * let the SearchView handle the submission by launching any associated intent.
     *
     * @param query the query text that is to be submitted
     * @return true if the query has been handled by the listener, false to let the
     * SearchView perform the default action.
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        filterModel.setQuery(query);
        onRefresh();
        return true;
    }

    /**
     * Called when the query text is changed by the user.
     *
     * @param newText the new content of the query text field.
     * @return false if the SearchView should perform the default action of showing any
     * suggestions if available, true if the action was handled by the listener.
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        filterModel.setQuery(newText);
        return false;
    }


    /**
     * The user is attempting to close the SearchView.
     *
     * @return true if the listener wants to override the default behavior of clearing the
     * text field and dismissing it, false otherwise.
     */
    @Override
    public boolean onClose() {
        Log.e("Close", "Called");
        toolbar.getMenu().findItem(R.id.action_map).setVisible(true);
        return true;
    }


}
