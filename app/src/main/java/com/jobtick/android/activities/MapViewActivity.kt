package com.jobtick.android.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.jobtick.android.R
import com.jobtick.android.adapers.FilterAdapter
import com.jobtick.android.adapers.TaskListAdapterV2
import com.jobtick.android.models.FilterModel
import com.jobtick.android.models.TaskModel
import com.jobtick.android.models.response.myjobs.Data
import com.jobtick.android.models.response.myjobs.MyJobsResponse
import com.jobtick.android.pagination.PaginationListener
import com.jobtick.android.utils.*
import com.jobtick.android.viewmodel.MapViewModel
import org.json.JSONException
import timber.log.Timber
import java.util.*

class MapViewActivity : ActivityBase(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    TaskListAdapterV2.OnItemClickListener {
    private var toolbar: MaterialToolbar? = null
    private var mapView: MapView? = null
    private var taskListArrayList: ArrayList<TaskModel>? = null
    private var recyclerViewTask: RecyclerView? = null
    private var txtFilters: TextView? = null
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
        taskListAdapter = TaskListAdapterV2(ArrayList(), null)
        recyclerViewTask!!.adapter = taskListAdapter
        taskListAdapter!!.setOnItemClickListener(this)
        doApiCall()

       recyclerViewTask!!.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                isLoadingItem = true
                currentPage++
                doApiCall()
            }

           override val isLastPage: Boolean
               get() = isLastPageItem
           override val isLoading: Boolean
               get() = isLoadingItem

        })
    }

    private fun initVM() {
        viewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        viewModel.getHomeResponse().observe(this, androidx.lifecycle.Observer{
            try {
                Timber.e(it.toString())
                val gson = Gson()
                val (_, data, _, _, _, _, _, _, _, per_page, _, _, total) = gson.fromJson(
                    it.toString(),
                    MyJobsResponse::class.java
                )
                if (data == null) {
                    showToast("some went to wrong", this)
                    return@Observer
                }
                totalItem = total!!
                Constant.PAGE_SIZE = per_page!!
                totalPage = total
                var i = 0
                while (data.size > i) {
                    try {
                        val latitude = java.lang.Double.valueOf(data?.get(i)!!.latitude)
                        val longitude = java.lang.Double.valueOf(data[i].longitude)
                        addMarker(latitude, longitude, data[i].title, i)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    i++
                }
                findCurrentLocation()
                addMarker(myLatitude, myLongitude, mySuburb, -1)
                goToLocation(myLatitude, myLongitude)
                taskListAdapter!!.addItems(data, totalItem)
            } catch (e: JSONException) {
                hideProgressDialog()
                this.showToast("Something went wrong", this)
                Timber.e(e.toString())
                e.printStackTrace()
            }
        })
        viewModel.getError().observe(this, androidx.lifecycle.Observer { jsonObject ->
            this.showToast("Something went wrong", this)
            this.hideProgressDialog()
        })
    }

    private fun initIDS() {
        toolbar = findViewById(R.id.toolbar)
        mapView = findViewById(R.id.map_view)
        recyclerViewTask = findViewById(R.id.recycler_view_task)
        txtFilters = findViewById(R.id.txt_filters)
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
                    sessionManagerM.getLatitude().toDouble(),
                    sessionManagerM.getLongitude().toDouble()
                )
            )
            .zoom(12f)
            .build()
        this.googleMap = googleMap
        this.googleMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        Handler().postDelayed({ mapView!!.visibility = View.VISIBLE }, 200)
        this.googleMap!!.setOnMarkerClickListener(this)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        if (marker.tag != null) {
            val position = marker.tag as Int
            if (position != -1) recyclerViewTask!!.smoothScrollToPosition(position)
        }
        return false
    }

    private fun doApiCall() {
        var queryParameter = ""
        if (filterModel!!.query != null && !filterModel!!.query.equals("", ignoreCase = true)) {
            queryParameter = "&search_query=" + filterModel!!.query
        }
        when {
            filterModel!!.section.equals(Constant.FILTER_ALL, ignoreCase = true) -> {
                queryParameter = queryParameter + "&task_type=" + Constant.FILTER_ALL_QUERY
                queryParameter = queryParameter + "&distance=" + filterModel!!.distance
                val price =
                    filterModel!!.price.replace("$", "").replace(",", "").split("-").toTypedArray()
                queryParameter =
                    queryParameter + "&min_price=" + price[0].trim { it <= ' ' } + "&max_price=" + price[1].trim { it <= ' ' }
                queryParameter = queryParameter + "&current_lat=" + filterModel!!.latitude
                queryParameter = queryParameter + "&current_lng=" + filterModel!!.logitude
                if (filterModel!!.task_open != null) {
                    queryParameter = "$queryParameter&hide_assigned=true"
                }
            }
            filterModel!!.section.equals(Constant.FILTER_REMOTE, ignoreCase = true) -> {
                queryParameter = queryParameter + "&task_type=" + Constant.FILTER_REMOTE_QUERY
                val price =
                    filterModel!!.price.replace("$", "").replace(",", "").split("-").toTypedArray()
                queryParameter =
                    queryParameter + "&min_price=" + price[0].trim { it <= ' ' } + "&max_price=" + price[1].trim { it <= ' ' }
                if (filterModel!!.task_open != null) {
                    queryParameter = "$queryParameter&hide_assigned=true"
                }
            }
            filterModel!!.section.equals(Constant.FILTER_IN_PERSON, ignoreCase = true) -> {
                queryParameter = queryParameter + "&task_type=" + Constant.FILTER_IN_PERSON_QUERY
                queryParameter = queryParameter + "&distance=" + filterModel!!.distance
                val price =
                    filterModel!!.price.replace("$", "").replace(",", "").split("-").toTypedArray()
                queryParameter =
                    queryParameter + "&min_price=" + price[0].trim { it <= ' ' } + "&max_price=" + price[1].trim { it <= ' ' }
                queryParameter = queryParameter + "&current_lat=" + filterModel!!.latitude
                queryParameter = queryParameter + "&current_lng=" + filterModel!!.logitude
                if (filterModel!!.task_open != null) {
                    queryParameter = "$queryParameter&hide_assigned=true"
                }
            }
        }
        Helper.closeKeyboard(this)
        viewModel.getJobs(sessionManager!!.accessToken, Volley.newRequestQueue(this),currentPage.toString(),queryParameter)

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
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_blue_image))
        val marker = googleMap!!.addMarker(markerOptions)
        marker.tag = tag
        marker.title = title
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