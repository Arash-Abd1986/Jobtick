package com.jobtick.android.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.activities.*
import com.jobtick.android.adapers.TaskListAdapterV2
import com.jobtick.android.models.TaskModel
import com.jobtick.android.models.response.myjobs.Data
import com.jobtick.android.models.response.myjobs.MyJobsResponse
import com.jobtick.android.utils.*
import com.jobtick.android.widget.EndlessRecyclerViewOnScrollListener
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class MyTasksFragment : Fragment(), TaskListAdapterV2.OnItemClickListener, OnRefreshListener,
    TaskListAdapterV2.OnDraftDeleteListener, ConfirmDeleteTaskBottomSheet.NoticeListener {

    private var recyclerViewStatus: RecyclerView? = null
    private var swipeRefresh: SwipeRefreshLayout? = null

    private var dashboardActivity: DashboardActivity? = null
    private var sessionManager: SessionManager? = null
    private var onScrollListener: EndlessRecyclerViewOnScrollListener? = null
    private var taskListAdapter: TaskListAdapterV2? = null
    private var currentPage = 1
    private var isLastPage = false
    private var totalItemT = 10
    private lateinit var ivNotification: ImageView
    private lateinit var toolbarTitle: TextView
    private lateinit var filterText: TextView
    private lateinit var filterIcon: ImageView
    private lateinit var linFilter: LinearLayout
    private var mBehavior: BottomSheetBehavior<*>? = null
    private val mBottomSheetDialog: BottomSheetDialog? = null
    var bottomSheet: FrameLayout? = null
    private var single_choice_selected: String? = null
    private var temp_single_choice_selected: String? = null
    private var str_search: String? = null
    private val temp_str_search: String? = null
    private lateinit var toolbar: Toolbar
    private var noJobs: LinearLayout? = null
    var mypopupWindow: PopupWindow? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_tasks, container, false)
    }

    private fun initIDS() {
        recyclerViewStatus = requireView().findViewById(R.id.recycler_view_status)
        bottomSheet = requireView().findViewById(R.id.bottom_sheet)
        swipeRefresh = requireView().findViewById(R.id.swipeRefresh)
    }

    private fun initToolbar() {
        dashboardActivity = requireActivity() as DashboardActivity
        if (dashboardActivity == null) return
        toolbar = dashboardActivity!!.findViewById(R.id.toolbar)
        toolbar.menu.clear()
        //toolbar.inflateMenu(R.menu.menu_my_task_black);
        toolbar.visibility = View.VISIBLE
        ivNotification = dashboardActivity!!.findViewById(R.id.ivNotification)
        ivNotification.visibility = View.GONE
        toolbarTitle = dashboardActivity!!.findViewById(R.id.toolbar_title)
        linFilter = dashboardActivity!!.findViewById(R.id.lin_filter)
        filterText = dashboardActivity!!.findViewById(R.id.filter_text)
        filterIcon = dashboardActivity!!.findViewById(R.id.filter_icon)
        linFilter.setOnClickListener(View.OnClickListener { v: View? ->
            filterText.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.P300
                )
            )
            filterIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_sort_arrow_up
                )
            )
            mypopupWindow!!.showAsDropDown(toolbar.findViewById(R.id.lin_filter), 0, 0)
        })
        toolbarTitle.visibility = View.VISIBLE
        linFilter.visibility = View.VISIBLE
        toolbarTitle.setText(R.string.my_jobs)
        toolbarTitle.textSize = 20f
        toolbarTitle.typeface = ResourcesCompat.getFont(requireContext(), R.font.roboto_medium)
        val params = Toolbar.LayoutParams(
            Toolbar.LayoutParams.WRAP_CONTENT,
            Toolbar.LayoutParams.WRAP_CONTENT
        )
        params.gravity = Gravity.START
        toolbarTitle.layoutParams = params
        toolbar.setNavigationIcon(R.drawable.ic_back)
    }

    override fun onStop() {
        super.onStop()
        linFilter.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        linFilter.visibility = View.VISIBLE
        sessionManager!!.needRefresh = false
        statusList
    }

    private fun setPopUpWindow() {
        val inflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.my_jobs_menu, null)
        mypopupWindow = PopupWindow(
            view,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        mypopupWindow!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val allJobs = view.findViewById<View>(R.id.all_jobs) as TextView
        val assigned = view.findViewById<View>(R.id.assigned) as TextView
        val posted = view.findViewById<View>(R.id.posted) as TextView
        val offered = view.findViewById<View>(R.id.offered) as TextView
        val draft = view.findViewById<View>(R.id.draft) as TextView
        val completed = view.findViewById<View>(R.id.completed) as TextView
        val overdue = view.findViewById<View>(R.id.overdue) as TextView
        val closed = view.findViewById<View>(R.id.closed) as TextView
        val cancelled = view.findViewById<View>(R.id.cancelled) as TextView
        mypopupWindow!!.setOnDismissListener {
            filterText.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
            filterIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_sort_arrow_down
                )
            )
        }
        allJobs.setOnClickListener { v: View? ->
            allJobs.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.N900
                )
            )
            assigned.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            posted.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            offered.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            draft.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            completed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            overdue.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            closed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            cancelled.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            refreshSort("All Jobs")
            mypopupWindow!!.dismiss()
            filterText.text = "All jobs"
        }
        assigned.setOnClickListener { v: View? ->
            allJobs.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.N300
                )
            )
            assigned.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
            posted.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            offered.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            draft.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            completed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            overdue.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            closed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            cancelled.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            refreshSort("Assigned")
            mypopupWindow!!.dismiss()
            filterText.text = "Assigned"
        }
        posted.setOnClickListener { v: View? ->
            allJobs.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.N300
                )
            )
            assigned.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            posted.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
            offered.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            draft.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            completed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            overdue.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            closed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            cancelled.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            refreshSort("Open")
            mypopupWindow!!.dismiss()
            filterText.text = "Posted"
        }
        offered.setOnClickListener { v: View? ->
            allJobs.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.N300
                )
            )
            assigned.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            posted.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            offered.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
            draft.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            completed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            overdue.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            closed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            cancelled.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            refreshSort("Offered")
            mypopupWindow!!.dismiss()
            filterText.text = "Offered"
        }
        draft.setOnClickListener { v: View? ->
            allJobs.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.N300
                )
            )
            assigned.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            posted.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            offered.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            draft.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
            completed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            overdue.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            closed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            cancelled.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            refreshSort("Draft")
            mypopupWindow!!.dismiss()
            filterText.text = "Draft"
        }
        completed.setOnClickListener { v: View? ->
            allJobs.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.N300
                )
            )
            assigned.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            posted.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            offered.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            draft.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            completed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
            overdue.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            closed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            cancelled.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            refreshSort("Completed")
            mypopupWindow!!.dismiss()
            filterText.text = "Completed"
        }
        overdue.setOnClickListener { v: View? ->
            allJobs.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.N300
                )
            )
            assigned.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            posted.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            offered.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            draft.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            completed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            overdue.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
            closed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            cancelled.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            refreshSort("Overdue")
            mypopupWindow!!.dismiss()
            filterText.text = "Overdue"
        }
        closed.setOnClickListener { v: View? ->
            allJobs.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.N300
                )
            )
            assigned.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            posted.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            offered.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            draft.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            completed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            overdue.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            cancelled.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            closed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
            refreshSort("Closed")
            mypopupWindow!!.dismiss()
            filterText.text = "Closed"
        }
        cancelled.setOnClickListener { v: View? ->
            allJobs.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.N300
                )
            )
            assigned.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            posted.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            offered.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            draft.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            completed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            overdue.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            closed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
            cancelled.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
            refreshSort("Cancelled")
            mypopupWindow!!.dismiss()
            filterText.text = "Cancelled"
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initIDS()
        noJobs = view.findViewById(R.id.no_jobs_container)
        swipeRefresh!!.setOnRefreshListener(this)
        initToolbar()
        setHasOptionsMenu(true)
        mBehavior = BottomSheetBehavior.from<FrameLayout?>(bottomSheet!!)
        sessionManager = SessionManager(dashboardActivity)

        // use a linear layout manager
        val layoutManager = LinearLayoutManager(dashboardActivity)
        recyclerViewStatus!!.layoutManager = layoutManager
        resetTaskListAdapter()
        recyclerViewStatus!!.adapter = taskListAdapter
        swipeRefresh!!.isRefreshing = true
        single_choice_selected = Constant.TASK_DRAFT_CASE_ALL_JOB_VALUE
        statusList
        onScrollListener = object : EndlessRecyclerViewOnScrollListener() {
            override fun onLoadMore(currentPage: Int) {
                this@MyTasksFragment.currentPage = currentPage
                statusList
            }

            override fun getTotalItem(): Int {
                return totalItemT
            }
        }
        recyclerViewStatus!!.addOnScrollListener(onScrollListener as EndlessRecyclerViewOnScrollListener)
        setPopUpWindow()
        toolbar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_search -> {
                    val intent = Intent(dashboardActivity, SearchTaskActivity::class.java)
                    intent.putExtra(ConstantKey.FROM_MY_JOBS_WITH_LOVE, true)
                    dashboardActivity!!.startActivity(intent)
                }
            }
            false
        }
    }//  swipeRefresh.setRefreshing(false);

    // categoryArrayList.clear();
    private val statusList: Unit
        private get() {
            var query_parameter = ""
            if (str_search != null) {
                query_parameter += "&search_query=$str_search"
            }
            query_parameter += if (single_choice_selected.equals(
                    Constant.TASK_DRAFT_CASE_ALL_JOB_VALUE,
                    ignoreCase = true
                )
            ) "" else "&status=" + single_choice_selected!!.toLowerCase()
            if (currentPage == 1) swipeRefresh!!.isRefreshing = true
            Helper.closeKeyboard(dashboardActivity)
            val stringRequest: StringRequest = object : StringRequest(
                Method.GET, Constant.URL_MY_JOBS + "?page=" + currentPage + query_parameter,
                Response.Listener { response: String? ->
                    Timber.e(response)
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
                        totalItemT = total!!
                        Constant.PAGE_SIZE = per_page!!
                        if (currentPage == 1) {
                            resetTaskListAdapter()
                        }
                        taskListAdapter!!.addItems(data, totalItemT)
                        isLastPage = taskListAdapter!!.itemCount == totalItemT
                        if (data.size <= 0) {
                            noJobs!!.visibility = View.VISIBLE
                            recyclerViewStatus!!.visibility = View.GONE
                        } else {
                            noJobs!!.visibility = View.GONE
                            recyclerViewStatus!!.visibility = View.VISIBLE
                        }
                        swipeRefresh!!.isRefreshing = false
                        str_search = null
                    } catch (e: JSONException) {
                        str_search = null
                        dashboardActivity!!.hideProgressDialog()
                        Timber.e(e.toString())
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    //  swipeRefresh.setRefreshing(false);
                    str_search = null
                    swipeRefresh!!.isRefreshing = false
                    dashboardActivity!!.errorHandle1(error.networkResponse)
                }) {
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

    private fun resetTaskListAdapter() {
        taskListAdapter = TaskListAdapterV2(ArrayList(), sessionManager!!.userAccount, false)
        taskListAdapter!!.setOnItemClickListener(this)
        taskListAdapter!!.onDraftDeleteListener = this
        recyclerViewStatus!!.adapter = taskListAdapter
    }

    override fun onItemClick(view: View?, obj: Data?, position: Int, action: String?) {
        if (obj!!.status!!.toLowerCase()
                .equals(Constant.TASK_DRAFT.toLowerCase(), ignoreCase = true)
        ) {
            getDataFromServer(obj.slug)
        } else {
            val intent = Intent(dashboardActivity, TaskDetailsActivity::class.java)
            val bundle = Bundle()
            bundle.putString(ConstantKey.SLUG, obj.slug)
            //   bundle.putInt(ConstantKey.USER_ID, obj.getPoster().getId());
            intent.putExtras(bundle)
            startActivityForResult(intent, ConstantKey.RESULTCODE_MY_JOBS)
            Timber.i("MyTasksFragment Starting Task with slug: %s", obj.slug)
        }
    }

    private fun getDataFromServer(slug: String?) {
        dashboardActivity!!.showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(
            Method.GET, Constant.URL_TASKS + "/" + slug,
            Response.Listener { response: String? ->
                Timber.e(response)
                dashboardActivity!!.hideProgressDialog()
                try {
                    val jsonObject = JSONObject(response!!)
                    Timber.e(jsonObject.toString())
                    if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                        if (jsonObject.getBoolean("success")) {
                            if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                val jsonObject_data = jsonObject.getJSONObject("data")
                                val taskModel =
                                    TaskModel().getJsonToModel(jsonObject_data, dashboardActivity)
                                EditTask(taskModel)
                            }
                        } else {
                            dashboardActivity!!.showToast("Something went wrong", dashboardActivity)
                        }
                    } else {
                        dashboardActivity!!.showToast("Something went wrong", dashboardActivity)
                    }
                } catch (e: JSONException) {
                    dashboardActivity!!.showToast("Something went wrong", dashboardActivity)
                    Timber.e(e.toString())
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error: VolleyError ->
                dashboardActivity!!.errorHandle1(error.networkResponse)
                dashboardActivity!!.hideProgressDialog()
            }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] =
                    sessionManager!!.tokenType + " " + sessionManager!!.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                // map1.put("X-Requested-With", "XMLHttpRequest");
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            0, -1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(dashboardActivity)
        requestQueue.add(stringRequest)
    }

    private fun EditTask(taskModel: TaskModel) {
        val update_task = Intent(dashboardActivity, TaskCreateActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelable(ConstantKey.TASK, taskModel)
        bundle.putString(ConstantKey.TITLE, ConstantKey.CREATE_TASK)
        bundle.putString(ConstantKey.SLUG, taskModel.slug)
        bundle.putBoolean(ConstantKey.DRAFT_JOB, true)
        update_task.putExtras(bundle)
        startActivityForResult(update_task, ConstantKey.RESULTCODE_UPDATE_TASK)
    }

    fun refreshSort(rbText: String) {
        temp_single_choice_selected = rbText.toLowerCase()
        if (temp_single_choice_selected.equals(
                Constant.TASK_DRAFT_CASE_ALL_JOB_KEY,
                ignoreCase = true
            )
        ) {
            temp_single_choice_selected = Constant.TASK_DRAFT_CASE_ALL_JOB_VALUE
        }
        if (temp_single_choice_selected.equals(
                Constant.TASK_ASSIGNED_CASE_UPPER_FIRST,
                ignoreCase = true
            )
        ) {
            temp_single_choice_selected = Constant.TASK_ASSIGNED_CASE_RELATED_JOB_VALUE
        }
        single_choice_selected = temp_single_choice_selected
        temp_single_choice_selected = null
        onScrollListener!!.reset()
        totalItemT = 0
        currentPage = 1
        taskListAdapter!!.clear()
        statusList
    }

    override fun onRefresh() {
        swipeRefresh!!.isRefreshing = true
        onScrollListener!!.reset()
        totalItemT = 0
        currentPage = 1
        taskListAdapter!!.clear()
        statusList
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ConstantKey.RESULTCODE_UPDATE_TASK) {
            if (data != null) {
                val bundle = data.extras
                if (bundle != null) {
                    if (bundle.getBoolean(ConstantKey.UPDATE_TASK)) {
                        onRefresh()
                    }
                }
            }
        }
        if (requestCode == ConstantKey.RESULTCODE_MY_JOBS) {
            onRefresh()
        }
    }

    protected fun deleteTask(taskModel: Data?) {
        swipeRefresh!!.isRefreshing = true
        val stringRequest: StringRequest = object :
            StringRequest(Method.DELETE, Constant.URL_TASKS + "/" + taskModel!!.slug,
                Response.Listener { response: String? ->
                    Timber.e(response)
                    try {
                        val jsonObject = JSONObject(response!!)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                onRefresh()
                            } else {
                                (requireActivity() as ActivityBase).showToast(
                                    "Something went Wrong",
                                    requireContext()
                                )
                            }
                        }
                    } catch (e: JSONException) {
                        Timber.e(e.toString())
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    val networkResponse = error.networkResponse
                    if (networkResponse != null && networkResponse.data != null) {
                        val jsonError = String(networkResponse.data)
                        // Print Error!
                        Timber.e(jsonError)
                        if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                            (requireActivity() as ActivityBase).unauthorizedUser()
                            return@ErrorListener
                        }
                        try {
                            val jsonObject = JSONObject(jsonError)
                            val jsonObject_error = jsonObject.getJSONObject("error")
                            if (jsonObject_error.has("message")) {
                                (requireActivity() as ActivityBase).showToast(
                                    jsonObject_error.getString(
                                        "message"
                                    ), requireContext()
                                )
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        (requireActivity() as ActivityBase).showToast(
                            "Something Went Wrong",
                            requireContext()
                        )
                    }
                    Timber.e(error.toString())
                }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] =
                    sessionManager!!.tokenType + " " + sessionManager!!.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["X-Requested-With"] = "XMLHttpRequest"
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            0, -1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)
    }

    private var taskModel: Data? = null
    private var position = 0
    override fun onDraftDeleteButtonClick(view: View?, taskModel: Data?, position: Int) {
        this.taskModel = taskModel
        this.position = position
        val confirmBottomSheet = ConfirmDeleteTaskBottomSheet(requireContext())
        confirmBottomSheet.listener = this
        confirmBottomSheet.show(requireActivity().supportFragmentManager, "")
    }

    override fun onDeleteConfirmClick() {
        deleteTask(taskModel)
    }
}