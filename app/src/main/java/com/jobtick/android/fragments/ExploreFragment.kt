package com.jobtick.android.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.jobtick.android.AppController
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.activities.FiltersActivity
import com.jobtick.android.activities.MapViewActivity
import com.jobtick.android.activities.SearchTaskActivity
import com.jobtick.android.activities.TaskAlertsActivity
import com.jobtick.android.activities.TaskDetailsActivity
import com.jobtick.android.adapers.FilterAdapter
import com.jobtick.android.adapers.TaskListAdapterV2
import com.jobtick.android.models.FilterModel
import com.jobtick.android.models.response.myjobs.Data
import com.jobtick.android.models.response.myjobs.MyJobsResponse
import com.jobtick.android.pagination.PaginationListener
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.Helper
import com.jobtick.android.utils.SessionManager
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class ExploreFragment :
    Fragment(),
    OnRefreshListener,
    TaskListAdapterV2.OnItemClickListener,
    SearchView.OnQueryTextListener,
    SearchView.OnCloseListener {
    private var recyclerViewFilters: RecyclerView? = null
    private var recyclerViewBrowse: RecyclerView? = null
    private var lytBtnFilters: LinearLayout? = null
    private var linSearch: LinearLayout? = null
    private var linNewMessage: RelativeLayout? = null
    private var swipeRefresh: SwipeRefreshLayout? = null
    private var txtFilters: TextView? = null
    private var edtSearch: TextView? = null
    private var txtJobAlert: TextView? = null
    private var icJobAlert: AppCompatImageView? = null
    private var lin_job_alert: LinearLayout? = null
    private var btnVoice: ImageView? = null
    private var ivSearch: ImageView? = null
    private var appbar: RelativeLayout? = null
    private var ivMapView: FloatingActionButton? = null
    private var emptyFilter: RelativeLayout? = null

    private var mSocket: Socket? = null
    private var dashboardActivity: DashboardActivity? = null
    private val filters = ArrayList<String>()
    private var filterModel: FilterModel? = FilterModel()
    private var filterAdapter: FilterAdapter? = null
    private var sessionManager: SessionManager? = null
    private var taskListAdapter: TaskListAdapterV2? = null
    private var currentPage = PaginationListener.PAGE_START
    private var isLastPageItems = false
    private var totalPage = 10
    private var totalItem = 10
    private var isLoadingItems = false
    private var toolbar: Toolbar? = null
    private var linFilterExplore: LinearLayout? = null
    private var txtFilter: TextView? = null
    private var txtNewJob: TextView? = null
    private var newJobCount = 0
    private val taskArrayList = ArrayList<Data>()

    private val TAG = "explore"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_browse, container, false)
        return view
    }

    private fun setIDs() {
        recyclerViewFilters = requireView().findViewById(R.id.recycler_view_filters)
        recyclerViewBrowse = requireView().findViewById(R.id.recycler_view_browse)
        lytBtnFilters = requireView().findViewById(R.id.lyt_btn_filters)
        linSearch = requireView().findViewById(R.id.lin_search)
        swipeRefresh = requireView().findViewById(R.id.swipeRefresh)
        txtFilters = requireView().findViewById(R.id.txt_filters)
        edtSearch = requireView().findViewById(R.id.edt_search_categoreis)
        txtJobAlert = requireView().findViewById(R.id.txt_job_alert)
        icJobAlert = requireView().findViewById(R.id.ic_job_alert)
        lin_job_alert = requireView().findViewById(R.id.lin_job_alert)
        btnVoice = requireView().findViewById(R.id.btnVoice)
        ivSearch = requireView().findViewById(R.id.iv_search)
        appbar = requireView().findViewById(R.id.appbar)
        ivMapView = requireView().findViewById(R.id.ivMapView)
        emptyFilter = requireView().findViewById(R.id.empty_filter)
        txtNewJob = requireView().findViewById(R.id.txt_new_job)
        linNewMessage = requireView().findViewById(R.id.linNewMessage)
    }

    private fun initToolbar() {
        if (dashboardActivity == null) return
        toolbar = dashboardActivity!!.findViewById(R.id.toolbar)
        toolbar!!.menu.clear()
        // toolbar.inflateMenu(R.menu.menu_browse_task);
        val ivNotification = dashboardActivity!!.findViewById<ImageView>(R.id.ivNotification)
        linFilterExplore = dashboardActivity!!.findViewById(R.id.lin_filter_explore)
        txtFilter = dashboardActivity!!.findViewById(R.id.txt_filter)
        ivNotification.visibility = View.GONE
        linFilterExplore!!.visibility = View.GONE
        lytBtnFilters!!.setOnClickListener {
            val bundle = Bundle()
            val intent = Intent(dashboardActivity, FiltersActivity::class.java)
            bundle.putParcelable(Constant.FILTER, filterModel)
            intent.putExtras(bundle)
            startActivityForResult(intent, 101)
        }
        val toolbarTitle = dashboardActivity!!.findViewById<TextView>(R.id.toolbar_title)
        toolbarTitle.visibility = View.VISIBLE
        toolbarTitle.setText(R.string.explore)
        toolbar!!.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.backgroundLightGrey
            )
        )
        toolbarTitle.typeface = ResourcesCompat.getFont(requireContext(), R.font.dmsans_medium)
        toolbarTitle.textSize = 20f
        val params = Toolbar.LayoutParams(
            Toolbar.LayoutParams.WRAP_CONTENT,
            Toolbar.LayoutParams.WRAP_CONTENT
        )
        params.gravity = Gravity.START
        toolbarTitle.layoutParams = params
        setHasOptionsMenu(true)
        toolbar!!.navigationIcon = null
        toolbar!!.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        if (!swipeRefresh!!.isRefreshing) {
            swipeRefresh!!.isRefreshing = true
            refresh()
        }

        val app = requireActivity().application as AppController
        if (mSocket == null)
            mSocket = app.socket
        if (!mSocket!!.connected()) {
            mSocket!!.off(Socket.EVENT_CONNECT, onConnect)
            mSocket!!.off(Socket.EVENT_DISCONNECT, onDisconnect)
            mSocket!!.off(Socket.EVENT_CONNECT_ERROR, onConnectError)
            mSocket!!.off("auth", onAutResponse)
            mSocket!!.off("newjob", newJob)
            mSocket!!.off("whoareyou", whoAreYou)

            mSocket!!.on(Socket.EVENT_CONNECT, onConnect)
            mSocket!!.on(Socket.EVENT_DISCONNECT, onDisconnect)
            mSocket!!.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
            mSocket!!.on("auth", onAutResponse)
            mSocket!!.on("newjob", newJob)
            mSocket!!.on("whoareyou", whoAreYou)

            mSocket!!.connect()
        }
    }

    private fun refresh() {
        currentPage = PaginationListener.PAGE_START
        isLastPageItems = false
        taskListAdapter!!.clear()
        doApiCall()
    }

    @SuppressLint("LogNotTimber")
    private val onDisconnect = Emitter.Listener { args: Array<Any?>? ->
        try {
            requireActivity().runOnUiThread {
                Log.i(
                    TAG,
                    "diconnected"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("LogNotTimber")
    private val onConnect = Emitter.Listener { args: Array<Any?>? ->
        requireActivity().runOnUiThread {
            Log.e(
                TAG,
                "Success connecting"
            )
        }
    }

    @SuppressLint("LogNotTimber")
    private val onConnectError = Emitter.Listener { args: Array<Any?>? ->
        requireActivity().runOnUiThread {
            Log.e(
                TAG,
                "Error connecting " + args
            )
        }
    }

    @SuppressLint("LogNotTimber")
    private val whoAreYou = Emitter.Listener { args: Array<Any?>? ->
        if (isAdded)
            requireActivity().runOnUiThread {
                try {
                    mSocket!!.emit("auth", sessionManager!!.accessToken)
                    Log.d(TAG, "who are you response")
                } catch (e: Exception) {
                    Log.e(TAG, e.message!!)
                    return@runOnUiThread
                }
            }
    }

    @SuppressLint("LogNotTimber")
    private val onAutResponse = Emitter.Listener { args: Array<Any> ->
        if (isAdded)
            requireActivity().runOnUiThread {
                try {
                    Log.d(TAG, "Aut Response")
                    if (args[0] as Boolean) {
                        Log.d(TAG, "Success autResponse")
                        mSocket!!.emit("subscribe", "explore")
                    }

                    //
                } catch (e: Exception) {
                    Log.e(TAG, e.message!!)
                    return@runOnUiThread
                }
            }
    }

    @SuppressLint("LogNotTimber")
    private val newJob = Emitter.Listener { args: Array<Any?>? ->
        if (isAdded)
            requireActivity().runOnUiThread {
                try {
                    linNewMessage!!.visibility = View.VISIBLE
                    newJobCount += 1
                    txtNewJob!!.text = getString(R.string.new_job_count, newJobCount.toString())
                } catch (e: Exception) {
                    Log.e(TAG, e.message!!)
                    return@runOnUiThread
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dashboardActivity = requireActivity() as DashboardActivity
        sessionManager = SessionManager(dashboardActivity)
        setIDs()
        initToolbar()
        initFilter()
        initBrowse()
        setCTAListener()
        initJobAlert()
    }

    private fun initJobAlert() {
        lin_job_alert!!.setOnClickListener {
            val taskAlerts = Intent(requireContext(), TaskAlertsActivity::class.java)
            startActivity(taskAlerts)
        }
        if (sessionManager?.userAccount?.account_status?.isJobalerts == true) {
            txtJobAlert!!.text = getString(R.string.edit_job_alert)
            icJobAlert!!.setImageResource(R.drawable.ic_edit_job_alert)
        } else {
            txtJobAlert!!.text = getString(R.string.add_job_alert)
            icJobAlert!!.setImageResource(R.drawable.ic_add_job_alert)
        }
    }

    private fun setCTAListener() {
        edtSearch!!.setOnClickListener {
            val creatingTask = Intent(requireActivity(), SearchTaskActivity::class.java)
            startActivity(creatingTask)
        }
        ivSearch!!.setOnClickListener {
            val creatingTask = Intent(requireActivity(), SearchTaskActivity::class.java)
            startActivity(creatingTask)
        }
        btnVoice!!.setOnClickListener {
            val creatingTask = Intent(requireActivity(), SearchTaskActivity::class.java)
            creatingTask.putExtra("IsVoice", true)
            startActivity(creatingTask)
        }
        ivMapView!!.setOnClickListener {
            if (sessionManager?.accessToken != null) {
                val intent = Intent(dashboardActivity, MapViewActivity::class.java)
                val bundle = Bundle()
                filterModel?.latitude?.let { it1 -> bundle.putFloat("lat", it1.toFloat()) }
                filterModel?.logitude?.let { it1 -> bundle.putFloat("long", it1.toFloat()) }
                intent.putExtras(bundle)
                dashboardActivity!!.startActivity(intent)

            } else {
                dashboardActivity!!.unauthorizedUser()
            }
        }
        txtNewJob!!.setOnClickListener { onRefresh() }
    }

    private fun initBrowse() {
        swipeRefresh!!.setOnRefreshListener(this)
        recyclerViewBrowse!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recyclerViewBrowse!!.layoutManager = layoutManager
        taskListAdapter = TaskListAdapterV2(
            taskArrayList,
            sessionManager!!.userAccount,
            false,
            isFromExplore = true
        )
        recyclerViewBrowse!!.adapter = taskListAdapter
        taskListAdapter!!.setOnItemClickListener(this)
        recyclerViewBrowse!!.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                isLoadingItems = true
                currentPage++
                doApiCall()
            }

            override val isLastPage: Boolean
                get() = isLastPageItems
            override val isLoading: Boolean
                get() = isLoadingItems
        })
    }

    @SuppressLint("SetTextI18n")
    private fun initFilter() {
        recyclerViewFilters!!.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewFilters!!.setHasFixedSize(true)
        filterAdapter = FilterAdapter(filters)
        filterAdapter!!.setmOnFilterDeleteListener { filters.clear() }
        recyclerViewFilters!!.adapter = filterAdapter
        // txtFilter!!.text = (filters.size + if (filters.contains("Remote & In person")) 1 else 0).toString() + " Filter" + if (filters.size > 1) "s" else ""
        if (sessionManager!!.filter != null) {
            filterModel = sessionManager!!.filter
        }
        setFilterData()
    }

    @SuppressLint("SetTextI18n")
    private fun setFilterData() {
        filters.clear()
        if (filterModel!!.section != null) {
            filters.add(filterModel!!.section)
        }
        if (filterModel!!.location != null && filterModel!!.distance != null) {
            var distance = "100 KM+"
            if (filterModel!!.distance != Integer.toString(Constant.MAX_FILTER_DISTANCE_IN_KILOMETERS)) {
                distance = filterModel!!.distance + " KM"
            }
            if (filterModel!!.location.length > 10) {
                filters.add(filterModel!!.location.substring(0, 10) + " - " + distance)
            } else {
                filters.add(filterModel!!.location + " - " + distance)
            }
        }
        if (filterModel!!.price != null) {
            try {
                val prices = filterModel!!.price.replace("$", "").split("-")
                filters.add("$" + prices[0] + "-$" + prices[1])
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (filterModel!!.task_open != null) {
            filters.add(filterModel!!.task_open)
        }
        if (filters.size != 0) {
            filterAdapter!!.notifyDataSetChanged()
        }
        /*txtFilter!!.text =
            (filters.size + if (filters.contains(Constant.FILTER_ALL)) 1 else 0).toString() + " Filter" + if (filters.size > 1) "s" else ""*/
    }

    //    @OnClick({R.id.lyt_search_new})
    //    public void onViewClicked() {
    //        Bundle bundle = new Bundle();
    //        Intent intent = new Intent(dashboardActivity, FiltersActivity.class);
    //        bundle.putParcelable(Constant.FILTER, filterModel);
    //        intent.putExtras(bundle);
    //        startActivityForResult(intent, 101);
    //    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            if (data != null && data.extras != null) {
                val bundle = data.extras
                filterModel = bundle!!.getParcelable<Parcelable>(Constant.FILTER) as FilterModel?
                sessionManager!!.filter = filterModel
                setFilterData()
                onRefresh()
            }
        } else if (requestCode == 202) {
            // TODO: Do something to show user that he offered on the job.
        }
    }

    private fun doApiCall() {
        var queryParameter = ""
        if (filterModel!!.query != null && !filterModel!!.query.equals("", ignoreCase = true)) {
            queryParameter = "&search_query=" + filterModel!!.query
        }
        if (filterModel!!.section.equals(Constant.FILTER_ALL, ignoreCase = true)) {
            queryParameter = queryParameter + "&task_type=" + Constant.FILTER_ALL_QUERY
            queryParameter = queryParameter + "&distance=" + filterModel!!.distance
            val price = filterModel!!.price.replace("$", "").replace(",", "").split("-".toRegex())
                .toTypedArray()
            queryParameter =
                queryParameter + "&min_price=" + price[0].trim { it <= ' ' } + "&max_price=" + price[1].trim { it <= ' ' }
            queryParameter = queryParameter + "&current_lat=" + filterModel!!.latitude
            queryParameter = queryParameter + "&current_lng=" + filterModel!!.logitude
            if (filterModel!!.task_open != null) {
                queryParameter = "$queryParameter&hide_assigned=true"
            }
        } else if (filterModel!!.section.equals(Constant.FILTER_REMOTE, ignoreCase = true)) {
            queryParameter = queryParameter + "&task_type=" + Constant.FILTER_REMOTE_QUERY
            val price = filterModel!!.price.replace("$", "").replace(",", "").split("-".toRegex())
                .toTypedArray()
            queryParameter =
                queryParameter + "&min_price=" + price[0].trim { it <= ' ' } + "&max_price=" + price[1].trim { it <= ' ' }
            if (filterModel!!.task_open != null) {
                queryParameter = "$queryParameter&hide_assigned=true"
            }
        } else if (filterModel!!.section.equals(Constant.FILTER_IN_PERSON, ignoreCase = true)) {
            queryParameter = queryParameter + "&task_type=" + Constant.FILTER_IN_PERSON_QUERY
            queryParameter = queryParameter + "&distance=" + filterModel!!.distance
            val price = filterModel!!.price.replace("$", "").replace(",", "").split("-".toRegex())
                .toTypedArray()
            queryParameter =
                queryParameter + "&min_price=" + price[0].trim { it <= ' ' } + "&max_price=" + price[1].trim { it <= ' ' }
            queryParameter = queryParameter + "&current_lat=" + filterModel!!.latitude
            queryParameter = queryParameter + "&current_lng=" + filterModel!!.logitude
            if (filterModel!!.task_open != null) {
                queryParameter = "$queryParameter&hide_assigned=true"
            }
        }
        //        queryParameter = queryParameter + "&hide_assigned=true";
        Helper.closeKeyboard(dashboardActivity)
        val stringRequest: StringRequest = object : StringRequest(
            Method.GET,
            Constant.URL_TASKS_v2 + "?page=" + currentPage + queryParameter,
            Response.Listener { response: String? ->
                Timber.e(response)
                swipeRefresh!!.isRefreshing = false
                newJobCount = 0
                linNewMessage!!.visibility = View.GONE
                // categoryArrayList.clear();
                try {
                    val jsonObject = JSONObject(response!!)
                    Timber.e(jsonObject.toString())
                    val gson = Gson()
                    val (_, data, _, _, _, _, _, _, _, per_page, _, _, total) = gson.fromJson(
                        jsonObject.toString(),
                        MyJobsResponse::class.java
                    )
                    if (data == null) {
                        dashboardActivity!!.showToast("some went to wrong", dashboardActivity)
                        return@Listener
                    }
                    totalItem = total!!
                    Constant.PAGE_SIZE = per_page!!
                    totalPage = total
                    taskListAdapter!!.addItems(data, totalItem)
                    isLastPageItems = taskListAdapter!!.itemCount == totalItem
                    swipeRefresh!!.isRefreshing = false
                    isLoadingItems = false
                    if (totalItem == 0) {
                        emptyFilter!!.visibility = View.VISIBLE
                        recyclerViewBrowse!!.visibility = View.GONE
                    } else {
                        emptyFilter!!.visibility = View.GONE
                        recyclerViewBrowse!!.visibility = View.VISIBLE
                    }
                } catch (e: JSONException) {
                    swipeRefresh!!.isRefreshing = false
                    dashboardActivity!!.hideProgressDialog()
                    Timber.e(e.toString())
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error: VolleyError ->
                swipeRefresh!!.isRefreshing = false
                dashboardActivity!!.hideProgressDialog()
                dashboardActivity!!.errorHandle1(error.networkResponse)
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["Authorization"] = "Bearer " + sessionManager!!.accessToken
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            0, -1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(dashboardActivity)
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
    }

    override fun onRefresh() {
        swipeRefresh!!.isRefreshing = true
        currentPage = PaginationListener.PAGE_START
        isLastPageItems = false
        taskListAdapter!!.clear()
        doApiCall()
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        filterModel!!.query = query
        onRefresh()
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        filterModel!!.query = newText
        return false
    }

    override fun onClose(): Boolean {
        toolbar!!.menu.findItem(R.id.action_map).isVisible = true
        return true
    }

    override fun onDestroy() {
        // Disconnect from the service
        mSocket!!.disconnect()
        mSocket!!.off(Socket.EVENT_CONNECT, onConnect)
        mSocket!!.off(Socket.EVENT_DISCONNECT, onDisconnect)
        mSocket!!.off(Socket.EVENT_CONNECT_ERROR, onConnectError)
        mSocket!!.off("auth", onAutResponse)
        mSocket!!.off("newjob", newJob)
        super.onDestroy()
    }

    override fun onItemClick(view: View?, obj: Data?, position: Int, action: String?) {
        if (sessionManager?.accessToken != null) {
            val intent = Intent(dashboardActivity, TaskDetailsActivity::class.java)
            val bundle = Bundle()
            bundle.putString(ConstantKey.SLUG, obj!!.slug)
            intent.putExtras(bundle)
            startActivityForResult(intent, 202)
        } else {
            dashboardActivity!!.unauthorizedUser()
        }
    }
}
