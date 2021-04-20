package com.jobtick.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
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
import com.google.gson.Gson;
import com.jobtick.android.BuildConfig;
import com.jobtick.android.R;
import android.annotation.SuppressLint;
import timber.log.Timber;
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
import com.jobtick.android.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jobtick.android.pagination.PaginationListener.PAGE_START;


public class MapViewActivity extends ActivityBase implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, TaskListAdapterV2.OnItemClickListener {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.map_view)
    MapView mapView;
    ArrayList<TaskModel> taskListArrayList;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_view_task)
    RecyclerView recyclerViewTask;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_filters)
    TextView txtFilters;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_filters)
    LinearLayout lytBtnFilters;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_view_filters)
    RecyclerView recyclerViewFilters;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.location_button)
    View locationButton;
    private GoogleMap googleMap;

    private ArrayList<String> filters;
    private FilterModel filterModel;
    private FilterAdapter filterAdapter;
    private SessionManager sessionManager;

    private TaskListAdapterV2 taskListAdapter;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private int totalItem = 10;
    private boolean isLoading = false;
    private double myLatitude, myLongitude;
    private String mySuburb;

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
        filterAdapter = new FilterAdapter(filters);
        filterAdapter.setmOnFilterDeleteListener(filters::clear);
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

        locationButton.setOnClickListener(V -> {
            goToLocation(myLatitude, myLongitude);
        });

        recyclerViewTask.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTask.setLayoutManager(layoutManager);
        taskListAdapter = new TaskListAdapterV2( new ArrayList<>(), null);
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
                currentPage = PAGE_START;
                taskListAdapter.clear();
                doApiCall();
            }
        }
    }


    private void setFilterData() {
        filters.clear();
        removeAllMarkers();
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
        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(new LatLng( Double.parseDouble(sessionManager.getLatitude()),  Double.parseDouble(sessionManager.getLongitude())))
                        .zoom(12)
                        .build();
        this.googleMap = googleMap;
        this.googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        new Handler().postDelayed(() -> {
            mapView.setVisibility(View.VISIBLE);
        },200);
        this.googleMap.setOnMarkerClickListener(this);


    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getTag() != null) {
            int position = (int) marker.getTag();
            if(position != -1)
                recyclerViewTask.smoothScrollToPosition(position);
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
            queryParameter = queryParameter + "&distance=" + filterModel.getDistance() ;
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
            queryParameter = queryParameter + "&distance=" + filterModel.getDistance() ;
            String[] price = filterModel.getPrice().replace("$", "").replace(",", "").split("-");
            queryParameter = queryParameter + "&min_price=" + price[0].trim() + "&max_price=" + price[1].trim();
            queryParameter = queryParameter + "&current_lat=" + filterModel.getLatitude();
            queryParameter = queryParameter + "&current_lng=" + filterModel.getLogitude();
            if (filterModel.getTask_open() != null) {
                queryParameter = queryParameter + "&hide_assigned=true";
            }
        }
        Helper.closeKeyboard(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_TASKS_v2 + "?task_type=physical&page=" + currentPage + queryParameter,
                response -> {
                    Timber.e(response);
                    // categoryArrayList.clear();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        Gson gson = new Gson();
                        MyJobsResponse myJobsResponse = gson.fromJson(jsonObject.toString(), MyJobsResponse.class);
                        if (myJobsResponse.getData() == null) {
                            this.showToast("some went to wrong", this);
                            return;
                        }
                        totalItem = myJobsResponse.getTotal();
                        Constant.PAGE_SIZE = myJobsResponse.getPer_page();
                        totalPage = myJobsResponse.getTotal();

                        for (int i = 0; myJobsResponse.getData().size() > i; i++) {
                            try {
                                Double latitude = Double.valueOf(myJobsResponse.getData().get(i).getLatitude());
                                Double longitude = Double.valueOf(myJobsResponse.getData().get(i).getLongitude());

                                addMarker(latitude, longitude, myJobsResponse.getData().get(i).getTitle(), i);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        findCurrentLocation();

                        addMarker(myLatitude, myLongitude, mySuburb, -1);

                        goToLocation(myLatitude, myLongitude);
                        taskListAdapter.addItems(myJobsResponse.getData(), totalItem);


                    } catch (JSONException e) {
                        hideProgressDialog();
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                    }
                },
                error -> errorHandle1(error.networkResponse)) {
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        Timber.e(stringRequest.getUrl());
    }


    @Override
    public void onItemClick(View view, Data obj, int position, String action) {
        Intent intent = new Intent(MapViewActivity.this, TaskDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ConstantKey.SLUG, obj.getSlug());
    //    bundle.putInt(ConstantKey.USER_ID, obj.getPoster().getId());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void goToLocation(double latitude, double longitude){

        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(new LatLng(latitude, longitude))
                        .zoom(12)
                        .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void addMarker(double latitude, double longitude, String title, int tag){

        LatLng destination = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions().position(destination)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_blue_image));

        Marker marker = googleMap.addMarker(markerOptions);
        marker.setTag(tag);
        marker.setTitle(title);
    }

    private void removeAllMarkers(){
        if(googleMap != null)
            googleMap.clear();
    }

    private void findCurrentLocation(){
        if(filterModel != null && filterModel.getLatitude() != null && filterModel.getLogitude() != null){
            myLatitude = Double.parseDouble(filterModel.getLatitude());
            myLongitude = Double.parseDouble(filterModel.getLogitude());
            mySuburb = Tools.getStringFromRes(MapViewActivity.this, R.string.selected_suburb);
        }else{
            myLatitude = Double.parseDouble(sessionManager.getLatitude());
            myLongitude = Double.parseDouble(sessionManager.getLongitude());
            mySuburb = Tools.getStringFromRes(MapViewActivity.this, R.string.current_location);
        }
    }
}