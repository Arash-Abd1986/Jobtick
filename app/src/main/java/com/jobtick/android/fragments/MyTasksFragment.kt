package com.jobtick.android.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.activities.*
import com.jobtick.android.adapers.TaskListAdapterV2
import com.jobtick.android.material.ui.jobdetails.JobDetailsActivity
import com.jobtick.android.material.ui.postajob.PostAJobActivity
import com.jobtick.android.models.TaskModel
import com.jobtick.android.models.response.myjobs.Data
import com.jobtick.android.models.response.myjobs.MyJobsResponse
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.Helper
import com.jobtick.android.utils.HttpStatus
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.widget.EndlessRecyclerViewOnScrollListener
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.Locale
import java.util.stream.Collectors

/**
 * A simple [Fragment] subclass.
 */
class MyTasksFragment :
    Fragment(),
    TaskListAdapterV2.OnItemClickListener,
    OnRefreshListener,
    TaskListAdapterV2.OnDraftDeleteListener,
    ConfirmDeleteTaskBottomSheet.NoticeListener {

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
    private lateinit var txt_title: TextView
    private lateinit var filterIcon: ImageView
    private lateinit var linFilter: LinearLayout
    private var mBehavior: BottomSheetBehavior<*>? = null
    var bottomSheet: FrameLayout? = null
    private var singleChoiceSelected: String? = null
    private var tempSingleChoiceSelected: String? = null
    private var strSearch: String? = null
    private lateinit var toolbar: Toolbar
    private var noJobs: LinearLayout? = null
    var mypopupWindow: PopupWindow? = null
    private var editTextSearch: TextInputEditText? = null
    private var stringRequestForJobs: StringRequest? = null
    private var more: ImageView? = null
    private var assigned: LinearLayout? = null
    private var ticked: LinearLayout? = null
    private var posted: LinearLayout? = null
    private var posted_text: TextView? = null
    private var posted_number: TextView? = null
    private var posted_image: ShapeableImageView? = null
    private var ticked_text: TextView? = null
    private var ticked_number: TextView? = null
    private var ticked_image: ShapeableImageView? = null
    private var assigned_text: TextView? = null
    private var assigned_number: TextView? = null
    private var assigned_image: ShapeableImageView? = null
//    private var expired_text: TextView? = null
//    private var expired_number: TextView? = null
//    private var expired_image: ShapeableImageView? = null
//    private var cancelled_text: TextView? = null
//    private var cancelled_number: TextView? = null
//    private var cancelled_image: ShapeableImageView? = null
//    private var closed_text: TextView? = null
//    private var closed_number: TextView? = null
//    private var closed_image: ShapeableImageView? = null
//    private var all: TextView? = null
    private var inflater: LayoutInflater? = null
    private var viewPopUp: View? = null
    private var jobsStatus = "posted"

    private var notification: ImageView? = null
    private var postAJob: RelativeLayout? = null
    private var postAJobInNoData: MaterialButton? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_tasks, container, false)
    }

    @SuppressLint("ResourceAsColor")
    private fun initIDS() {
        //TODO variables fot assign, cancelled and ...
        recyclerViewStatus = requireView().findViewById(R.id.recycler_view_status)
        txt_title = requireView().findViewById(R.id.txt_title)
        bottomSheet = requireView().findViewById(R.id.bottom_sheet)
        swipeRefresh = requireView().findViewById(R.id.swipeRefresh)
        swipeRefresh!!.setColorSchemeColors(Color.BLUE,Color.BLUE,Color.BLUE,Color.BLUE)
        swipeRefresh!!.setBackgroundColor(android.R.color.transparent)
        editTextSearch = requireView().findViewById(R.id.edt_search)
        assigned = requireView().findViewById(R.id.assigned)
        ticked = requireView().findViewById(R.id.ticked)
        posted = requireView().findViewById(R.id.posted)
        notification = requireView().findViewById(R.id.image_action)
        postAJob = requireView().findViewById(R.id.float_post_a_job)
        postAJobInNoData = requireView().findViewById(R.id.noDataButton)
        posted_number = requireView().findViewById(R.id.posted_number)
        posted_text = requireView().findViewById(R.id.posted_text)
        posted_image = requireView().findViewById(R.id.posted_image)
        ticked_number = requireView().findViewById(R.id.ticked_number)
        ticked_text = requireView().findViewById(R.id.ticked_text)
        ticked_image = requireView().findViewById(R.id.ticked_image)
        assigned_number = requireView().findViewById(R.id.assigned_number)
        assigned_text = requireView().findViewById(R.id.assigned_text)
        assigned_image = requireView().findViewById(R.id.assigned_image)

        assigned?.setOnClickListener{
            refreshSort("Assigned")
            jobsStatus = "assigned"
            initChips(jobsStatus)
        }
        ticked?.setOnClickListener{
            (activity as DashboardActivity).showToast("waiting for Api", activity)
            //initChips("ticked")

        }

        posted?.setOnClickListener{
            refreshSort("Open")
            jobsStatus = "posted"
            initChips(jobsStatus)
        }

        notification?.setOnClickListener {

            val intent = Intent(requireContext(), NotificationActivity::class.java)
            startActivityForResult(intent, ConstantKey.RESULTCODE_NOTIFICATION_READ)
        }
        setPopUpWindow()
        more = requireView().findViewById(R.id.more)
        more?.setOnClickListener{
            mypopupWindow!!.showAsDropDown(more, 0, 0)
        }

        val handler = Handler(Looper.getMainLooper())
        editTextSearch!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                stringRequestForJobs?.cancel()
                if(s.toString().trim().length > 2) {
                    handler.postDelayed({
                        strSearch = s.toString()
                        currentPage = 1
                        statusList
                    }, 1000)
                }

            }
        })

    }

//    private fun initToolbar() {
//        dashboardActivity = requireActivity() as DashboardActivity
//        if (dashboardActivity == null) return
//        toolbar = dashboardActivity!!.findViewById(R.id.toolbar)
//        toolbar.menu.clear()
//        // toolbar.inflateMenu(R.menu.menu_my_task_black);
//        toolbar.visibility = View.GONE
//        ivNotification = dashboardActivity!!.findViewById(R.id.ivNotification)
//        ivNotification.visibility = View.VISIBLE
//        toolbarTitle = dashboardActivity!!.findViewById(R.id.toolbar_title)
//        linFilter = dashboardActivity!!.findViewById(R.id.lin_filter)
//        filterText = dashboardActivity!!.findViewById(R.id.filter_text)
//        filterIcon = dashboardActivity!!.findViewById(R.id.filter_icon)
//        filterText.setOnClickListener {
//            filterText.setTextColor(
//                ContextCompat.getColor(
//                    requireContext(), R.color.P300
//                )
//            )
//            filterIcon.setImageDrawable(
//                ContextCompat.getDrawable(
//                    requireContext(),
//                    R.drawable.ic_sort_arrow_up
//                )
//            )
////            mypopupWindow!!.showAsDropDown(toolbar.findViewById(R.id.lin_filter), 0, 0)
//        }
//        ivNotification.setOnClickListener {
//            val intent = Intent(requireContext(), NotificationActivity::class.java)
//            startActivityForResult(intent, ConstantKey.RESULTCODE_NOTIFICATION_READ)
//        }
//        toolbarTitle.visibility = View.VISIBLE
//        linFilter.visibility = View.VISIBLE
//        toolbarTitle.setText(R.string.my_jobs)
//        toolbarTitle.textSize = 20f
//        toolbarTitle.typeface = ResourcesCompat.getFont(requireContext(), R.font.roboto_medium)
//        val params = Toolbar.LayoutParams(
//            Toolbar.LayoutParams.WRAP_CONTENT,
//            Toolbar.LayoutParams.WRAP_CONTENT
//        )
//        params.gravity = Gravity.START
//        toolbarTitle.layoutParams = params
//      //  toolbar.setNavigationIcon(R.drawable.ic_back)
//    }

    override fun onResume() {
        super.onResume()
        sessionManager!!.needRefresh = false
        statusList
    }

    @SuppressLint("MissingInflatedId")
    private fun setPopUpWindow() {
//        val inflater = requireContext().getSystemService(ActivityBase.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val view = inflater.inflate(R.layout.inventory_job_category_menu, null)
        mypopupWindow = PopupWindow(viewPopUp, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true)
        mypopupWindow!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val cancelled = viewPopUp?.findViewById<View>(R.id.cancelled) as RelativeLayout
        val closed = viewPopUp?.findViewById<View>(R.id.closed) as RelativeLayout
        val expired = viewPopUp?.findViewById<View>(R.id.expired) as RelativeLayout
        val all = viewPopUp?.findViewById<View>(R.id.all) as TextView
        cancelled.setOnClickListener {
            mypopupWindow?.dismiss()
            jobsStatus = "cancelled"
            initChips(jobsStatus)
            refreshSort("Cancelled")
        }
        closed.setOnClickListener {
            mypopupWindow?.dismiss()
            jobsStatus = "closed"
            initChips(jobsStatus)
            refreshSort("Closed")

        }

        expired.setOnClickListener {
            mypopupWindow?.dismiss()
            jobsStatus = "expired"
            initChips(jobsStatus)
            refreshSort("Overdue")
        }

        all.setOnClickListener {
            mypopupWindow?.dismiss()
            jobsStatus = "all"
            initChips(jobsStatus)
            refreshSort("All Jobs")

        }


    }


//    private fun setPopUpWindow() {
//        val inflater =
//            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val view = inflater.inflate(R.layout.my_jobs_menu, null)
//        mypopupWindow = PopupWindow(
//            view,
//            RelativeLayout.LayoutParams.WRAP_CONTENT,
//            RelativeLayout.LayoutParams.WRAP_CONTENT,
//            true
//        )
//        mypopupWindow!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        val allJobs = view.findViewById<View>(R.id.all_jobs) as TextView
//        val assigned = view.findViewById<View>(R.id.assigned) as TextView
//        val posted = view.findViewById<View>(R.id.posted) as TextView
//        val offered = view.findViewById<View>(R.id.offered) as TextView
//        val draft = view.findViewById<View>(R.id.draft) as TextView
//        val completed = view.findViewById<View>(R.id.completed) as TextView
//        val overdue = view.findViewById<View>(R.id.overdue) as TextView
//        val closed = view.findViewById<View>(R.id.closed) as TextView
//        val cancelled = view.findViewById<View>(R.id.cancelled) as TextView
//        if (sessionManager!!.roleLocal == "poster") {
//            allJobs.visibility = View.VISIBLE
//            posted.visibility = View.VISIBLE
//            assigned.visibility = View.VISIBLE
//            draft.visibility = View.VISIBLE
//            completed.visibility = View.VISIBLE
//            closed.visibility = View.VISIBLE
//            cancelled.visibility = View.VISIBLE
//            overdue.visibility = View.GONE
//            offered.visibility = View.GONE
//        } else {
//            allJobs.visibility = View.VISIBLE
//            offered.visibility = View.VISIBLE
//            assigned.visibility = View.VISIBLE
//            completed.visibility = View.VISIBLE
//            closed.visibility = View.VISIBLE
//            cancelled.visibility = View.VISIBLE
//            draft.visibility = View.GONE
//            overdue.visibility = View.GONE
//            posted.visibility = View.GONE
//        }
//        mypopupWindow!!.setOnDismissListener {
//            filterText.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
//            filterIcon.setImageDrawable(
//                ContextCompat.getDrawable(
//                    requireContext(),
//                    R.drawable.ic_sort_arrow_down
//                )
//            )
//        }
//        allJobs.setOnClickListener { v: View? ->
//            allJobs.setTextColor(
//                    ContextCompat.getColor(
//                            requireContext(), R.color.N900
//                    )
//            )
//            assigned.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            posted.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            offered.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            draft.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            completed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            overdue.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            closed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            cancelled.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            refreshSort("All Jobs")
//            mypopupWindow!!.dismiss()
//            filterText.text = "All jobs"
//        }
//        assigned.setOnClickListener { v: View? ->
//            allJobs.setTextColor(
//                    ContextCompat.getColor(
//                            requireContext(), R.color.N300
//                    )
//            )
//            assigned.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
//            posted.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            offered.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            draft.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            completed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            overdue.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            closed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            cancelled.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            refreshSort("Assigned")
//            mypopupWindow!!.dismiss()
//            filterText.text = "Assigned"
//        }
//        posted.setOnClickListener { v: View? ->
//            allJobs.setTextColor(
//                    ContextCompat.getColor(
//                            requireContext(), R.color.N300
//                    )
//            )
//            assigned.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            posted.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
//            offered.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            draft.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            completed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            overdue.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            closed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            cancelled.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            refreshSort("Open")
//            mypopupWindow!!.dismiss()
//            filterText.text = "Posted"
//        }
//        offered.setOnClickListener { v: View? ->
//            allJobs.setTextColor(
//                ContextCompat.getColor(
//                    requireContext(), R.color.N300
//                )
//            )
//            assigned.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            posted.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            offered.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
//            draft.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            completed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            overdue.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            closed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            cancelled.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            refreshSort("Offered")
//            mypopupWindow!!.dismiss()
//            filterText.text = "Offered"
//        }
//        draft.setOnClickListener { v: View? ->
//            allJobs.setTextColor(
//                ContextCompat.getColor(
//                    requireContext(), R.color.N300
//                )
//            )
//            assigned.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            posted.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            offered.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            draft.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
//            completed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            overdue.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            closed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            cancelled.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            refreshSort("Draft")
//            mypopupWindow!!.dismiss()
//            filterText.text = "Draft"
//        }
//        completed.setOnClickListener { v: View? ->
//            allJobs.setTextColor(
//                ContextCompat.getColor(
//                    requireContext(), R.color.N300
//                )
//            )
//            assigned.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            posted.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            offered.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            draft.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            completed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
//            overdue.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            closed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            cancelled.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            refreshSort("Completed")
//            mypopupWindow!!.dismiss()
//            filterText.text = "Completed"
//        }
//        overdue.setOnClickListener { v: View? ->
//            allJobs.setTextColor(
//                ContextCompat.getColor(
//                    requireContext(), R.color.N300
//                )
//            )
//            assigned.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            posted.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            offered.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            draft.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            completed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            overdue.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
//            closed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            cancelled.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            refreshSort("Overdue")
//            mypopupWindow!!.dismiss()
//            filterText.text = "Overdue"
//        }
//        closed.setOnClickListener { v: View? ->
//            allJobs.setTextColor(
//                    ContextCompat.getColor(
//                            requireContext(), R.color.N300
//                    )
//            )
//            assigned.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            posted.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            offered.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            draft.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            completed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            overdue.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            cancelled.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            closed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
//            refreshSort("Closed")
//            mypopupWindow!!.dismiss()
//            filterText.text = "Closed"
//        }
//        cancelled.setOnClickListener { v: View? ->
//            allJobs.setTextColor(
//                ContextCompat.getColor(
//                    requireContext(), R.color.N300
//                )
//            )
//            assigned.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            posted.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            offered.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            draft.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            completed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            overdue.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            closed.setTextColor(ContextCompat.getColor(requireContext(), R.color.N300))
//            cancelled.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
//            refreshSort("Cancelled")
//            mypopupWindow!!.dismiss()
//            filterText.text = "Cancelled"
//        }
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dashboardActivity = requireActivity() as DashboardActivity
        sessionManager = SessionManager(dashboardActivity)
        inflater = requireContext().getSystemService(ActivityBase.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        viewPopUp = inflater!!.inflate(R.layout.inventory_job_category_menu, null)
        initIDS()
        jobsStatus = "posted"
        //  singleChoiceSelected = Constant.TASK_DRAFT_CASE_ALL_JOB_VALUE
        singleChoiceSelected = "open"

        initChips(jobsStatus)
      //  singleChoiceSelected = Constant.TASK_DRAFT_CASE_ALL_JOB_VALUE
        sessionManager?.let {
            it.userAccount?.let {
                initView()
            }
        }
        noJobs = view.findViewById(R.id.no_jobs_container)

    }

    private fun initView() {
        swipeRefresh!!.setOnRefreshListener(this)
        //initToolbar()
        setHasOptionsMenu(true)
        mBehavior = BottomSheetBehavior.from<FrameLayout?>(bottomSheet!!)
        sessionManager = SessionManager(dashboardActivity)

        // use a linear layout manager
        val layoutManager = LinearLayoutManager(dashboardActivity)
        recyclerViewStatus!!.layoutManager = layoutManager
        resetTaskListAdapter()
        recyclerViewStatus!!.adapter = taskListAdapter
        swipeRefresh!!.isRefreshing = true
       // statusList
        postAJobInNoData?.setOnClickListener {
            if (sessionManager?.accessToken != null) {
                val creating_task = Intent(activity, PostAJobActivity::class.java)
                val bundle = Bundle()
                // bundle.putInt(ConstantKey.CATEGORY_ID, obj!!.id)
                creating_task.putExtras(bundle)
                requireActivity().startActivityForResult(creating_task, ConstantKey.RESULTCODE_CATEGORY)
            } else {
                val dashboardActivity = requireActivity() as DashboardActivity
                dashboardActivity.unauthorizedUser()
            }
        }

        postAJob?.setOnClickListener {
            if (sessionManager?.accessToken != null) {
                val creating_task = Intent(activity, PostAJobActivity::class.java)
                val bundle = Bundle()
                bundle.putInt(ConstantKey.CATEGORY_ID, 1)
                creating_task.putExtras(bundle)
                requireActivity().startActivityForResult(creating_task, ConstantKey.RESULTCODE_CATEGORY)
            } else {
                val dashboardActivity = requireActivity() as DashboardActivity
                dashboardActivity.unauthorizedUser()
            }
        }
        onScrollListener = object : EndlessRecyclerViewOnScrollListener() {
            override fun onLoadMore(currentPage: Int) {
                this@MyTasksFragment.currentPage = currentPage
                statusList
            }

            override fun onScrollDown() {
                postAJob?.visibility = View.GONE
            }

            override fun onScrollUp() {
                postAJob?.visibility = View.VISIBLE
            }

            override fun getTotalItem(): Int {
                return totalItemT
            }
        }
        recyclerViewStatus!!.addOnScrollListener(onScrollListener as EndlessRecyclerViewOnScrollListener)
       // setPopUpWindow()
//        toolbar.setOnMenuItemClickListener { item: MenuItem ->
//            when (item.itemId) {
//                R.id.action_search -> {
//                    val intent = Intent(dashboardActivity, SearchTaskActivity::class.java)
//                    intent.putExtra(ConstantKey.FROM_MY_JOBS_WITH_LOVE, true)
//                    dashboardActivity!!.startActivity(intent)
//                }
//            }
//            false
//        }
    } //  swipeRefresh.setRefreshing(false);

    // categoryArrayList.clear();
    private val statusList: Unit
         get() {
            var query_parameter = ""
            if (strSearch != null) {
                query_parameter += "&search_query=$strSearch"
            }
            query_parameter += if (singleChoiceSelected.equals(
                            Constant.TASK_DRAFT_CASE_ALL_JOB_VALUE,
                            ignoreCase = true
                    )
            ) "" else "&status=" + singleChoiceSelected!!.lowercase(Locale.getDefault())
            if (currentPage == 1) swipeRefresh!!.isRefreshing = true
            Helper.closeKeyboard(dashboardActivity)
            Log.d("mytaskfragmentjobsRoute", Constant.URL_MY_JOBS + "?page=" + currentPage + query_parameter)

            stringRequestForJobs = object : StringRequest(
                Method.GET, Constant.URL_MY_JOBS + "?page=" + currentPage + query_parameter,
                Response.Listener { response: String? ->
                    Timber.e(response)
                    Log.d("mytaskfragmentjobs", response.toString())
                    // categoryArrayList.clear();
                    try {
                        val jsonObject = JSONObject(response!!)
                        Timber.e(jsonObject.toString())
                        val gson = Gson()
                        var (_, data, _, _, _, _, _, _, _, per_page, _, _, total) = gson.fromJson(
                            jsonObject.toString(),
                            MyJobsResponse::class.java
                        )
                        if (data == null) {
                            dashboardActivity!!.showToast("some went to wrong", dashboardActivity)
                            return@Listener
                        }

//                        if(sessionManager?.roleLocal == "poster")
//                            data = data.stream().filter{it.poster_id == sessionManager?.userAccount?.id}.collect(Collectors.toList())
//                        else
//                            data = data.stream().filter{it.poster_id != sessionManager?.userAccount?.id}.collect(Collectors.toList())
//                            data.forEach { it.offers?.stream()?.filter{it.user_id == sessionManager?.userAccount?.id}}
//                        for((index, item) in data.withIndex())
//                            if(item.poster_id != sessionManager?.userAccount?.id)
//                                data1.data.(index)
                        totalItemT = total!!
                       // totalItemT = data!!.size
                        Constant.PAGE_SIZE = per_page!!
                        if (currentPage == 1) {
                            resetTaskListAdapter()
                        }
                        taskListAdapter!!.addItems(data!!, totalItemT)
                        isLastPage = taskListAdapter!!.itemCount == totalItemT

                            if (data.isEmpty()) {
                                noJobs!!.visibility = View.VISIBLE
                                postAJob?.visibility = View.GONE
                                recyclerViewStatus!!.visibility = View.GONE
                            } else {
                                noJobs!!.visibility = View.GONE
                                postAJob?.visibility = View.VISIBLE

                                recyclerViewStatus!!.visibility = View.VISIBLE
                            }
                        swipeRefresh!!.isRefreshing = false
                        strSearch = null
                    } catch (e: JSONException) {
                        strSearch = null
                        dashboardActivity!!.hideProgressDialog()
                        Timber.e(e.toString())
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    //  swipeRefresh.setRefreshing(false);
                    strSearch = null
                    swipeRefresh!!.isRefreshing = false
                    Log.d("errorerror", error.toString())
                    dashboardActivity!!.errorHandle1(error.networkResponse)
                }
            ) {
                override fun getHeaders(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["Content-Type"] = "application/x-www-form-urlencoded"
                    map1["Authorization"] = "Bearer " + sessionManager!!.accessToken
                    map1["Version"] = BuildConfig.VERSION_CODE.toString()
                    Log.d("mytaskfragmentjobsHeader", map1.toString())
                    return map1
                }
            }
             stringRequestForJobs!!.retryPolicy = DefaultRetryPolicy(
                0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            val requestQueue = Volley.newRequestQueue(dashboardActivity)
            requestQueue.add(stringRequestForJobs)
            Timber.e(stringRequestForJobs!!.url)
        }

    private fun resetTaskListAdapter() {
        taskListAdapter = TaskListAdapterV2(ArrayList(), sessionManager!!.userAccount, false)
        taskListAdapter!!.setOnItemClickListener(this)
        taskListAdapter!!.onDraftDeleteListener = this
        recyclerViewStatus!!.adapter = taskListAdapter
    }

    override fun onItemClick(view: View?, obj: Data?, position: Int, action: String?) {
        if (obj!!.status!!.lowercase(Locale.getDefault())
            .equals(Constant.TASK_DRAFT.lowercase(Locale.getDefault()), ignoreCase = true)
        ) {
            getDataFromServer(obj.slug)
        } else {
            val intent = Intent(dashboardActivity, JobDetailsActivity::class.java)
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
            }
        ) {
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
        tempSingleChoiceSelected = rbText.lowercase(Locale.getDefault())
        if (tempSingleChoiceSelected.equals(
                Constant.TASK_DRAFT_CASE_ALL_JOB_KEY,
                ignoreCase = true
            )
        ) {
            tempSingleChoiceSelected = Constant.TASK_DRAFT_CASE_ALL_JOB_VALUE
        }
        if (tempSingleChoiceSelected.equals(
                Constant.TASK_ASSIGNED_CASE_UPPER_FIRST,
                ignoreCase = true
            )
        ) {
            tempSingleChoiceSelected = Constant.TASK_ASSIGNED_CASE_RELATED_JOB_VALUE
        }
        singleChoiceSelected = tempSingleChoiceSelected
        tempSingleChoiceSelected = null
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
            StringRequest(
                Method.DELETE, Constant.URL_TASKS + "/" + taskModel!!.slug,
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
                                    ),
                                    requireContext()
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
                }
            ) {
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

    private fun initChips(str: String) {
        val cancelled_text = viewPopUp?.findViewById<View>(R.id.cancelled_text) as MaterialTextView
        val cancelled_number = viewPopUp?.findViewById<View>(R.id.cancelled_number) as TextView
        val cancelled_image = viewPopUp?.findViewById<View>(R.id.cancelled_image) as ShapeableImageView
        val closed_text = viewPopUp?.findViewById<View>(R.id.closed_text) as MaterialTextView
        val closed_number = viewPopUp?.findViewById<View>(R.id.closed_number) as TextView
        val closed_image = viewPopUp?.findViewById<View>(R.id.closed_image) as ShapeableImageView
        val expired_text = viewPopUp?.findViewById<View>(R.id.expired_text) as MaterialTextView
        val expired_number = viewPopUp?.findViewById<View>(R.id.expired_number) as TextView
        val expired_image = viewPopUp?.findViewById<View>(R.id.expired_image) as ShapeableImageView
        val all = viewPopUp?.findViewById<View>(R.id.all) as TextView

        AppCompatResources.getDrawable(requireContext(), R.drawable.circle_chips_inventory_workspace)?.let { ticked_number?.background =  it}
        AppCompatResources.getDrawable(requireContext(), R.drawable.circle_chips_inventory_workspace)?.let { posted_number?.background =  it}
        AppCompatResources.getDrawable(requireContext(), R.drawable.circle_chips_inventory_workspace)?.let { assigned_number?.background =  it}
        activity?.getColor(R.color.neutral_light_500)?.let { ticked_text?.setTextColor(it) }
        activity?.getColor(R.color.neutral_light_500)?.let { posted_text?.setTextColor(it) }
        activity?.getColor(R.color.neutral_light_500)?.let { assigned_text?.setTextColor(it) }
        activity?.getColor(R.color.neutral_light_500)?.let { expired_text.setTextColor(it) }
        activity?.getColor(R.color.neutral_light_500)?.let { cancelled_text.setTextColor(it) }
        activity?.getColor(R.color.neutral_light_500)?.let { closed_text.setTextColor(it) }
        AppCompatResources.getDrawable(requireContext(), R.drawable.circle_chips_inventory_workspace)?.let { expired_number?.background =  it}
        AppCompatResources.getDrawable(requireContext(), R.drawable.circle_chips_inventory_workspace)?.let { cancelled_number?.background =  it}
        AppCompatResources.getDrawable(requireContext(), R.drawable.circle_chips_inventory_workspace)?.let { closed_number?.background =  it}
        activity?.getColor(R.color.neutral_light_500)?.let { all.setTextColor(it) }


        when (str) {
            "assigned" -> {
                AppCompatResources.getDrawable(requireContext(), R.drawable.circle_chips_inventory_workspace_purple)?.let { assigned_number?.background =  it}

                activity?.getColor(R.color.secondary_400)?.let { assigned_text?.setTextColor(it) }
            }
            "posted" -> {
                AppCompatResources.getDrawable(requireContext(), R.drawable.circle_chips_inventory_workspace_purple)?.let { posted_number?.background =  it}

                activity?.getColor(R.color.secondary_400)?.let { posted_text?.setTextColor(it) }
            }

            "ticked" -> {
                AppCompatResources.getDrawable(requireContext(), R.drawable.circle_chips_inventory_workspace_purple)?.let { ticked_number?.background =  it}

                activity?.getColor(R.color.secondary_400)?.let { ticked_text?.setTextColor(it) }
            }

            "cancelled" -> {
                AppCompatResources.getDrawable(requireContext(), R.drawable.circle_chips_inventory_workspace_purple)?.let { cancelled_number?.background =  it}

                activity?.getColor(R.color.secondary_400)?.let { cancelled_text.setTextColor(it) }
            }

            "closed" -> {
                AppCompatResources.getDrawable(requireContext(), R.drawable.circle_chips_inventory_workspace_purple)?.let { closed_number?.background =  it}

                activity?.getColor(R.color.secondary_400)?.let { closed_text.setTextColor(it) }
            }

            "expired" -> {
                AppCompatResources.getDrawable(requireContext(), R.drawable.circle_chips_inventory_workspace_purple)?.let { expired_number?.background =  it}

                activity?.getColor(R.color.secondary_400)?.let { expired_text.setTextColor(it) }
            }

            "all" -> {
                activity?.getColor(R.color.secondary_400)?.let { all.setTextColor(it) }
            }

        }
    }
}
