package com.jobtick.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.R;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.adapers.FilterAdapter;
import com.jobtick.adapers.TaskListAdapter;
import com.jobtick.models.FilterModel;
import com.jobtick.models.TaskModel;
import com.jobtick.pagination.PaginationListener;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.Helper;
import com.jobtick.utils.SessionManager;
import com.jobtick.utils.Tools;

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


public class MapViewActivity extends ActivityBase implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, TaskListAdapter.OnItemClickListener {

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.map_view)
    MapView mapView;
    ArrayList<TaskModel> taskListArrayList;
    @BindView(R.id.recycler_view_task)
    RecyclerView recyclerViewTask;
    @BindView(R.id.txt_filters)
    TextViewRegular txtFilters;
    @BindView(R.id.lyt_btn_filters)
    LinearLayout lytBtnFilters;
    @BindView(R.id.recycler_view_filters)
    RecyclerView recyclerViewFilters;
    private GoogleMap googleMap;
    private Double latitude, longitude;

    private ArrayList<String> filters;
    private FilterModel filterModel;
    private FilterAdapter filterAdapter;
    private SessionManager sessionManager;

    private TaskListAdapter taskListAdapter;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        ButterKnife.bind(this);
        initUI(savedInstanceState); //Initialize UI


        filters = new ArrayList<>();
        filterModel = new FilterModel();
        recyclerViewFilters.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewFilters.setHasFixedSize(true);
        sessionManager = new SessionManager(this);
        filterAdapter = new FilterAdapter(this, filters);
        recyclerViewFilters.setAdapter(filterAdapter);
        if (sessionManager.getFilter() != null) {
            filterModel = sessionManager.getFilter();
        }
        setFilterData();

        taskListArrayList = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            taskListArrayList = bundle.getParcelableArrayList(ConstantKey.TASK);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerViewTask.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTask.setLayoutManager(layoutManager);
        taskListAdapter = new TaskListAdapter(this, new ArrayList<>());
        recyclerViewTask.setAdapter(taskListAdapter);
        taskListAdapter.setOnItemClickListener(this);


        doApiCall();
        /*
         * add scroll listener while user reach in bottom load more will call
         */
        recyclerViewTask.addOnScrollListener(new PaginationListener(layoutManager) {
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
    @OnClick(R.id.lyt_btn_filters)
    public void onViewClicked() {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(MapViewActivity.this, FiltersActivity.class);
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
                currentPage=PAGE_START;
                taskListAdapter.clear();
                doApiCall();
            }
        }
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


    public void initUI(Bundle savedInstanceState) {

        mapView.onCreate(savedInstanceState);

        mapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }


        //Initialize & Setup Layout Manager with Recycler View


        //Initialize & Setup Adapter with Recycler View


        //Send request to Server for retrieving Nearest Data


        mapView.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setOnMarkerClickListener(this);


    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getTag() != null) {
            int position = (int) marker.getTag();
            recyclerViewTask.scrollToPosition(position);
        }
        return false;
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
        ArrayList<TaskModel> items = new ArrayList<>();
        Helper.closeKeyboard(this);
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
                                TaskModel taskModel = new TaskModel().getJsonToModel(jsonObject_taskModel_list, MapViewActivity.this);
                                items.add(taskModel);
                            }
                        } else {
                            showToast("some went to wrong", MapViewActivity.this);
                            return;
                        }
                        for (int i = 0; items.size() > i; i++) {
                            Double latitude = items.get(i).getPosition().getLatitude();
                            Double longitude = items.get(i).getPosition().getLongitude();
                            LatLng destination = new LatLng(latitude, longitude);

                            MarkerOptions markerOptions = new MarkerOptions().position(destination)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_grey));

                            Marker marker = googleMap.addMarker(markerOptions);
                            marker.setTag(i);
                            marker.setTitle(items.get(i).getTitle());

                        }
                        if (jsonObject.has("meta") && !jsonObject.isNull("meta")) {
                            JSONObject jsonObject_meta = jsonObject.getJSONObject("meta");
                            totalPage = jsonObject_meta.getInt("last_page");
                            Constant.PAGE_SIZE = jsonObject_meta.getInt("per_page");
                        }
                        //Log.e("location",""+new LatLng(Double.valueOf(latitude), Double.valueOf(longitude)));
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(sessionManager.getLatitude()), Double.valueOf(sessionManager.getLongitude())))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_grey)))
                                .setTitle(Tools.getStringFromRes(MapViewActivity.this, R.string.current_location));

                        CameraPosition cameraPosition =
                                new CameraPosition.Builder()
                                        .target(new LatLng(Double.valueOf(sessionManager.getLatitude()), Double.valueOf(sessionManager.getLongitude())))
                                        .zoom(16)
                                        .build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        /*
                         *manage progress view
                         */
                        if (currentPage != PAGE_START)
                            taskListAdapter.removeLoading();
                        taskListAdapter.addItems(items);


                        // check weather is last page or not
                        if (currentPage < totalPage) {
                            taskListAdapter.addLoading();
                        } else {
                            isLastPage = true;
                        }
                        isLoading = false;


                    } catch (JSONException e) {
                        hideProgressDialog();
                        Log.e("EXCEPTION", String.valueOf(e));
                        e.printStackTrace();
                    }
                },
                error -> errorHandle1(error.networkResponse)) {
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        Log.e("url", stringRequest.getUrl());
    }


    @Override
    public void onItemClick(View view, TaskModel obj, int position, String action) {
        Intent intent = new Intent(MapViewActivity.this, TaskDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ConstantKey.SLUG, obj.getSlug());
        bundle.putInt(ConstantKey.USER_ID, obj.getPoster().getId());
        intent.putExtras(bundle);
        startActivity(intent);
    }
}