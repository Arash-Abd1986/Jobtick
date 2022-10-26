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
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.jobtick.android.R
import com.jobtick.android.adapers.FilterAdapter
import com.jobtick.android.adapers.TaskListAdapterV2
import com.jobtick.android.material.ui.jobdetails.JobDetailsActivity
import com.jobtick.android.models.FilterModel
import com.jobtick.android.models.response.myjobs.Data
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.coroutines.Status
import com.jobtick.android.network.model.request.NearJobsRequest
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.pagination.PaginationListener
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.CustomInfoWindowForGoogleMap
import com.jobtick.android.utils.Helper
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.Tools
import com.jobtick.android.utils.dpToPx
import com.jobtick.android.viewmodel.MapViewModel
import com.jobtick.android.viewmodel.ViewModelFactory
import java.util.Random

class MapViewActivity :
        ActivityBase(),
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        TaskListAdapterV2.OnItemClickListener {
    private var toolbar: MaterialToolbar? = null
    private var mapView: MapView? = null
    private var recyclerViewTask: RecyclerView? = null
    private var txtFilters: TextView? = null
    private var txtSearch: TextView? = null
    private var ivMapView: TextView? = null
    private var loadingView: ProgressBar? = null
    private var lytBtnFilters: LinearLayout? = null
    private var recyclerViewFilters: RecyclerView? = null
    private var locationButton: View? = null
    private var location: TextInputEditText? = null
    private var googleMap: GoogleMap? = null
    private var filters: ArrayList<String>? = null
    private var filterModel: FilterModel? = null
    private var filterAdapter: FilterAdapter? = null
    private lateinit var sessionManagerM: SessionManager
    private var taskListAdapter: TaskListAdapterV2? = null
    private var currentPage = PaginationListener.PAGE_START
    private var myLatitude = 0.0
    private var myLongitude = 0.0
    private var mySuburb: String? = null
    private lateinit var viewModel: MapViewModel
    private var mMarkerArray = ArrayList<Marker>()
    private var lat = 0F
    private var long = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_view)
        initIDS()
        initVM()
        initUI(savedInstanceState) // Initialize UI
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

        toolbar!!.setNavigationOnClickListener { onBackPressed() }
        locationButton!!.setOnClickListener { V: View? -> goToLocation(myLatitude, myLongitude) }
        recyclerViewTask!!.setHasFixedSize(true)
        // use a linear layout manager
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewTask!!.layoutManager = layoutManager
        taskListAdapter = TaskListAdapterV2(ArrayList(), sessionManager!!.userAccount, true)
        recyclerViewTask!!.adapter = taskListAdapter
        taskListAdapter!!.setOnItemClickListener(this)
        val bundle = intent.extras
        if (bundle != null) {
            lat = bundle.getFloat("lat")
            long = bundle.getFloat("long")
            doApiCall(lat, long, "")
        }
    }

    private fun initVM() {
        viewModel = ViewModelProvider(
                this,
                ViewModelFactory(ApiHelper(ApiClient.getClientV2(sessionManager)))
        ).get(MapViewModel::class.java)
        viewModel.response.observe(this) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        hideProgressDialog()
                        txtSearch!!.visibility = View.GONE
                        loadingView!!.visibility = View.GONE
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
                                            (it.data.data[i].latitude)!!.toDouble()
                                    val longitude =
                                            (it.data.data[i].longitude)!!.toDouble()
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
                            /*if (googleMap!!.cameraPosition.zoom>12f && mMarkerArray.isNotEmpty()){
                                goToLocation((it.data.data[0].latitude)!!.toDouble(),(it.data.data[0].longitude)!!.toDouble())
                            }*/
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
                        // showProgressDialog()
                    }
                }
            }
        }
    }

    private fun initIDS() {
        toolbar = findViewById(R.id.toolbar)
        mapView = findViewById(R.id.map_view)
        recyclerViewTask = findViewById(R.id.recycler_view_task)
        txtFilters = findViewById(R.id.txt_filters)
        txtSearch = findViewById(R.id.txt_search)
        ivMapView = findViewById(R.id.ivMapView)
        loadingView = findViewById(R.id.loadingView)
        lytBtnFilters = findViewById(R.id.lyt_btn_filters)
        recyclerViewFilters = findViewById(R.id.recycler_view_filters)
        locationButton = findViewById(R.id.location_button)
        location = findViewById(R.id.location)
        onViewClick()
        location?.doOnTextChanged { text, _, _, _ ->
            taskListAdapter!!.clear()
            loadingView!!.visibility = View.VISIBLE
            setFilterData()
            currentPage = PaginationListener.PAGE_START
            taskListAdapter!!.clear()
            doApiCall(googleMap!!.cameraPosition.target.latitude.toFloat(),
                    googleMap!!.cameraPosition.target.longitude.toFloat(), text.toString())
        }
    }

    private fun onViewClick() {
        lytBtnFilters!!.setOnClickListener {
            val bundle = Bundle()
            val intent = Intent(this@MapViewActivity, FiltersActivity::class.java)
            bundle.putParcelable(Constant.FILTER, filterModel)
            intent.putExtras(bundle)
            startActivityForResult(intent, 101)
        }
        ivMapView!!.setOnClickListener {
            onBackPressed()
        }
        /* location?.doOnTextChanged { text, start, before, count ->
             currentPage = PaginationListener.PAGE_START
             isLastPageItems = false
             taskListAdapter!!.clear()
             doApiCall(text.toString())
         }*/
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
                doApiCall(bundle.getFloat("lat"), bundle.getFloat("long"), "")
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
                                lat.toDouble(),
                                long.toDouble()
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
        this.googleMap!!.setOnCameraIdleListener {
            txtSearch!!.visibility = View.VISIBLE
            loadingView!!.visibility = View.VISIBLE
            viewModel.getNearJobs(
                    NearJobsRequest(
                            googleMap.cameraPosition.target.latitude.toFloat(),
                            googleMap.cameraPosition.target.longitude.toFloat(), 50, 100
                    )
            )
        }
        this.googleMap!!.setOnCameraMoveStartedListener {


        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        if (marker.tag != null) {
            val position = marker.tag as Int
            if (position != -1) recyclerViewTask!!.smoothScrollToPosition(position)
            mMarkerArray.forEach {
                it.setIcon(bitmapFromVector(this, R.drawable.ic_loc_not_selected, 16, 16))
            }
            marker.setIcon(bitmapFromVector(this, R.drawable.ic_selected_pin, 44, 64))
        }
        return true
    }

    private fun doApiCall(lat: Float, long: Float, filter: String) {
        viewModel.getNearJobs(
                NearJobsRequest(
                        lat,
                        long, 50, 100, filter
                )
        )
    }

    override fun onItemClick(view: View?, obj: Data?, position: Int, action: String?) {
        val intent = Intent(this@MapViewActivity, JobDetailsActivity::class.java)
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

    private fun addMarker(latitude: Double, longitude: Double, title: String?, tag: Int, isMyLocation: Boolean = false) {
        val destination = LatLng(latitude, longitude)
        val markerOptions = MarkerOptions().position(destination)
                .icon(bitmapFromVector(this, if (isMyLocation) R.drawable.ic_loc_selected else R.drawable.ic_loc_not_selected, 16, 16))
        val marker = googleMap!!.addMarker(markerOptions)
        marker.tag = tag
        marker.title = title
        mMarkerArray.add(marker)
    }

    private fun bitmapFromVector(context: Context, vectorResId: Int, w: Int, h: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)

        vectorDrawable!!.setBounds(
                0,
                0,
                (w).dpToPx(),
                (h).dpToPx()
        )
        val bitmap = Bitmap.createBitmap(
                (w).dpToPx(),
                (h).dpToPx(),
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
            addMarker(
                    myLatitude,
                    myLongitude,
                    mySuburb,
                    Random().nextInt(), true
            )
        } else {
            myLatitude = sessionManagerM.latitude.toDouble()
            myLongitude = sessionManagerM.longitude.toDouble()
            mySuburb = Tools.getStringFromRes(this@MapViewActivity, R.string.current_location)
            addMarker(
                    myLatitude,
                    myLongitude,
                    mySuburb,
                    Random().nextInt(), true
            )
        }
    }
}
