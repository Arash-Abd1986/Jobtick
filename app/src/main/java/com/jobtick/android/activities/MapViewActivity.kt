package com.jobtick.android.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.appbar.MaterialToolbar
import com.jobtick.android.R
import com.jobtick.android.adapers.FilterAdapter
import com.jobtick.android.adapers.TaskListAdapterV2
import com.jobtick.android.models.FilterModel
import com.jobtick.android.models.TaskModel
import com.jobtick.android.models.response.myjobs.Data
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.coroutines.Status
import com.jobtick.android.network.model.request.NearJobsRequest
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.pagination.PaginationListener
import com.jobtick.android.utils.*
import com.jobtick.android.viewmodel.MapViewModel
import com.jobtick.android.viewmodel.ViewModelFactory
import java.util.*
import kotlin.collections.ArrayList


class MapViewActivity : ActivityBase(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    TaskListAdapterV2.OnItemClickListener {
    private var toolbar: MaterialToolbar? = null
    private var mapView: MapView? = null
    private var taskListArrayList: ArrayList<TaskModel>? = null
    private var recyclerViewTask: RecyclerView? = null
    private var txtFilters: TextView? = null
    private var txtSearch: TextView? = null
    private var lytBtnFilters: LinearLayout? = null
    private var recyclerViewFilters: RecyclerView? = null
    private var locationButton: View? = null
    private var googleMap: GoogleMap? = null
    private var filters: ArrayList<String>? = null
    private var filterModel: FilterModel? = null
    private var filterAdapter: FilterAdapter? = null
    private lateinit var sessionManagerM: SessionManager
    private var taskListAdapter: TaskListAdapterV2? = null
    private var currentPage = PaginationListener.PAGE_START
    private val isLastPageItem = false
    private var totalPage = 10
    private var totalItem = 10
    private var isLoadingItem = false
    private var myLatitude = 0.0
    private var myLongitude = 0.0
    private var mySuburb: String? = null
    private lateinit var viewModel: MapViewModel
    private var mMarkerArray = ArrayList<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_view)
        initIDS()
        initVM()
        initUI(savedInstanceState) //Initialize UI
        filters = ArrayList()
        filterModel = FilterModel()
        recyclerViewFilters!!.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewFilters!!.setHasFixedSize(true)
        sessionManagerM = SessionManager(this)
        filterAdapter = FilterAdapter(filters)
        filterAdapter!!.setmOnFilterDeleteListener { filters!!.clear() }
        recyclerViewFilters!!.adapter = filterAdapter
        if (sessionManagerM.filter != null) {
            filterModel = sessionManagerM.filter
        }
        setFilterData()
        taskListArrayList = ArrayList()
        val bundle = intent.extras
        if (bundle != null) {
            taskListArrayList = bundle.getParcelableArrayList(ConstantKey.TASK)
        }
        toolbar!!.setNavigationOnClickListener { onBackPressed() }
        locationButton!!.setOnClickListener { V: View? -> goToLocation(myLatitude, myLongitude) }
        recyclerViewTask!!.setHasFixedSize(true)
        // use a linear layout manager
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewTask!!.layoutManager = layoutManager
        taskListAdapter = TaskListAdapterV2(ArrayList(), sessionManager!!.userAccount.id,true)
        recyclerViewTask!!.adapter = taskListAdapter
        taskListAdapter!!.setOnItemClickListener(this)
        doApiCall()


        txtSearch!!.setOnClickListener {
            viewModel.getNearJobs(
                NearJobsRequest(
                    googleMap!!.cameraPosition.target.latitude.toFloat(),
                    googleMap!!.cameraPosition.target.longitude.toFloat(), 50, 100
                )
            )
        }
    }

    private fun initVM() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiHelper(ApiClient.getClientV2(sessionManager)))
        ).get(MapViewModel::class.java)
        viewModel.response.observe(this, {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        hideProgressDialog()
                        txtSearch!!.visibility = View.GONE
                        mMarkerArray = ArrayList()
                        try {
                            googleMap!!.clear()
                            if (it.data == null) {
                                showToast("some went to wrong", this)
                                return@observe
                            }
                            var i = 0
                            while (it.data.data.size > i) {
                                try {
                                    val latitude =
                                        java.lang.Double.valueOf(it.data.data[i].latitude)
                                    val longitude =
                                        java.lang.Double.valueOf(it.data.data[i].longitude)
                                    addMarker(
                                        latitude + (Random().nextFloat() / 60 - Random().nextFloat() / 60),
                                        longitude + (Random().nextFloat() / 60 - Random().nextFloat() / 60),
                                        it.data.data[i].title,
                                        i
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                i++
                            }
                            findCurrentLocation()
                            /* addMarker(myLatitude, myLongitude, mySuburb, -1)
                             goToLocation(myLatitude, myLongitude)*/
                            taskListAdapter!!.clear()
                            taskListAdapter!!.addItemsWithoutLoading(it.data.data)
                        } catch (e: Exception) {
                            hideProgressDialog()
                            this.showToast("Something went wrong", this)
                            e.printStackTrace()
                        }
                    }
                    Status.ERROR -> {
                        hideProgressDialog()
                        this.showToast("Something went wrong", this)
                    }
                    Status.LOADING -> {
                        showProgressDialog()
                    }
                }
            }
        })

    }

    private fun initIDS() {
        toolbar = findViewById(R.id.toolbar)
        mapView = findViewById(R.id.map_view)
        recyclerViewTask = findViewById(R.id.recycler_view_task)
        txtFilters = findViewById(R.id.txt_filters)
        txtSearch = findViewById(R.id.txt_search)
        lytBtnFilters = findViewById(R.id.lyt_btn_filters)
        recyclerViewFilters = findViewById(R.id.recycler_view_filters)
        locationButton = findViewById(R.id.location_button)
        onViewClick()
    }

    private fun onViewClick() {
        lytBtnFilters!!.setOnClickListener {
            val bundle = Bundle()
            val intent = Intent(this@MapViewActivity, FiltersActivity::class.java)
            bundle.putParcelable(Constant.FILTER, filterModel)
            intent.putExtras(bundle)
            startActivityForResult(intent, 101)
        }

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 101) {
            if (data != null && data.extras != null) {
                val bundle = data.extras
                filterModel = bundle!!.getParcelable<Parcelable>(Constant.FILTER) as FilterModel?
                sessionManagerM.filter = filterModel
                setFilterData()
                currentPage = PaginationListener.PAGE_START
                taskListAdapter!!.clear()
                doApiCall()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setFilterData() {
        filters!!.clear()
        removeAllMarkers()
        if (filterModel!!.section != null) {
            filters!!.add(filterModel!!.section)
        }
        if (filterModel!!.location != null && filterModel!!.distance != null) {
            if (filterModel!!.location.length > 10) {
                filters!!.add(
                    filterModel!!.location.substring(
                        0,
                        10
                    ) + " - " + filterModel!!.distance + " KM"
                )
            } else {
                filters!!.add(filterModel!!.location + " - " + filterModel!!.distance + " KM")
            }
        }
        if (filterModel!!.price != null) {
            filters!!.add(filterModel!!.price)
        }
        if (filterModel!!.task_open != null) {
            filters!!.add(filterModel!!.task_open)
        }
        if (filters!!.size != 0) {
            filterAdapter!!.notifyDataSetChanged()
        }
    }

    fun initUI(savedInstanceState: Bundle?) {
        mapView!!.onCreate(savedInstanceState)
        mapView!!.onResume() // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mapView!!.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val cameraPosition = CameraPosition.Builder()
            .target(
                LatLng(
                    sessionManagerM.latitude.toDouble(),
                    sessionManagerM.longitude.toDouble()
                )
            )
            .zoom(12f)
            .build()
        this.googleMap = googleMap
        this.googleMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        Handler().postDelayed({ mapView!!.visibility = View.VISIBLE }, 200)
        this.googleMap!!.setOnMarkerClickListener(this)
        this.googleMap!!.uiSettings.isMapToolbarEnabled = false
        this.googleMap!!.setInfoWindowAdapter(CustomInfoWindowForGoogleMap(this))
        this.googleMap!!.setOnCameraMoveStartedListener {
            txtSearch!!.visibility = View.VISIBLE
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        if (marker.tag != null) {
            val position = marker.tag as Int
            if (position != -1) recyclerViewTask!!.smoothScrollToPosition(position)
            mMarkerArray.forEach {
                it.setIcon(bitmapFromVector(this, R.drawable.ic_pin_white_image_large))
            }
            marker.setIcon(bitmapFromVector(this, R.drawable.ic_pin_blue_image_large))
        }
        return false
    }

    private fun doApiCall() {
        Helper.closeKeyboard(this)
        viewModel.getNearJobs(
            NearJobsRequest(
                sessionManagerM.latitude.toFloat(),
                sessionManagerM.longitude.toFloat(), 50, 100
            )
        )

    }

    override fun onItemClick(view: View?, obj: Data?, position: Int, action: String?) {
        val intent = Intent(this@MapViewActivity, TaskDetailsActivity::class.java)
        val bundle = Bundle()
        bundle.putString(ConstantKey.SLUG, obj!!.slug)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun goToLocation(latitude: Double, longitude: Double) {
        val cameraPosition = CameraPosition.Builder()
            .target(LatLng(latitude, longitude))
            .zoom(12f)
            .build()
        googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun addMarker(latitude: Double, longitude: Double, title: String?, tag: Int) {
        val destination = LatLng(latitude, longitude)
        val markerOptions = MarkerOptions().position(destination)
            .icon(bitmapFromVector(this, R.drawable.ic_pin_white_image_large))
        val marker = googleMap!!.addMarker(markerOptions)
        marker.tag = tag
        marker.title = title
        mMarkerArray.add(marker)
    }

    private fun bitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)

        vectorDrawable!!.setBounds(
            0,
            0,
            (31).dpToPx(),
            (40).dpToPx()
        )
        val bitmap = Bitmap.createBitmap(
            (31).dpToPx(),
            (40).dpToPx(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun removeAllMarkers() {
        if (googleMap != null) googleMap!!.clear()
    }

    private fun findCurrentLocation() {
        if (filterModel != null && filterModel!!.latitude != null && filterModel!!.logitude != null) {
            myLatitude = filterModel!!.latitude.toDouble()
            myLongitude = filterModel!!.logitude.toDouble()
            mySuburb = Tools.getStringFromRes(this@MapViewActivity, R.string.selected_suburb)
        } else {
            myLatitude = sessionManagerM.latitude.toDouble()
            myLongitude = sessionManagerM.longitude.toDouble()
            mySuburb = Tools.getStringFromRes(this@MapViewActivity, R.string.current_location)
        }
    }
}