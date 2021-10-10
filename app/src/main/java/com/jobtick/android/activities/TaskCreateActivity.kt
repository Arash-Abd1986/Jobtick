package com.jobtick.android.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.MaterialToolbar
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.adapers.SectionsPagerAdapter
import com.jobtick.android.fragments.ConfirmDeleteTaskBottomSheet
import com.jobtick.android.fragments.TaskBudgetFragment
import com.jobtick.android.fragments.TaskDateTimeFragment
import com.jobtick.android.fragments.TaskDetailFragment
import com.jobtick.android.models.AttachmentModel
import com.jobtick.android.models.DueTimeModel
import com.jobtick.android.models.PositionModel
import com.jobtick.android.models.TaskModel
import com.jobtick.android.models.task.AttachmentModels
import com.jobtick.android.utils.*
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class TaskCreateActivity : ActivityBase(), TaskDetailFragment.OperationsListener, TaskDateTimeFragment.OperationsListener, TaskBudgetFragment.OperationsListener, ConfirmDeleteTaskBottomSheet.NoticeListener {

    private lateinit var creatingTaskLayout: FrameLayout
    private lateinit var toolbar: MaterialToolbar
    private lateinit var lytBtnDetails: LinearLayout
    private lateinit var lytBntDateTime: LinearLayout
    private lateinit var lytBtnBudget: LinearLayout
    private lateinit var cardSelectionView: CardView
    private lateinit var viewPager: ViewPager
    private lateinit var imgDetails: ImageView
    private lateinit var ivDelete: ImageView
    private lateinit var txtDetails: TextView
    private lateinit var imgDateTime: ImageView
    private lateinit var txtDateTime: TextView
    private lateinit var imgBudget: ImageView
    private lateinit var txtBudget: TextView

    private lateinit var taskModel: TaskModel
    private var title: String? = null
    private var actionDraftDateTime: ActionDraftDateTime? = null
    private var actionDraftTaskDetails: ActionDraftTaskDetails? = null
    private var actionDraftTaskBudget: ActionDraftTaskBudget? = null
    private var isDraftWorkDone = false
    private var isEditTask = false
    private var isJobDraftedYet = false
    var isBudgetComplete = false
    var isDateTimeComplete = false
    private var slug: String? = ""
    private val adapter = SectionsPagerAdapter(supportFragmentManager)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_create)
        initIDS()
        onViewClick()
        initVars()
        initToolbar(title)
        initComponent()
    }

    private fun initVars() {
        val bundle = intent.extras
        taskModel = TaskModel()
        if (bundle != null && bundle.containsKey(ConstantKey.TASK)) {
            taskModel = bundle.getParcelable(ConstantKey.TASK)!!
        }
        if (bundle != null && bundle.containsKey(ConstantKey.CATEGORY_ID)) {
            taskModel.category_id = bundle.getInt(ConstantKey.CATEGORY_ID, 1)
        }
        if (bundle != null && bundle.getBoolean(ConstantKey.COPY, false)) {
            taskModel = TaskDetailsActivity.taskModel!!
            if (taskModel.poster != null &&
                    taskModel.poster.id != sessionManager.userAccount.id) {
                val taskModelTemp = TaskModel()
                taskModelTemp.poster = sessionManager.userAccount
                taskModelTemp.category_id = taskModel.category_id
                taskModelTemp.title = taskModel.title
                taskModelTemp.description = taskModel.description
                taskModel = taskModelTemp
            } else if (taskModel.poster != null && taskModel.poster.id == sessionManager.userAccount.id) {
                taskModel.slug = null
                taskModel.id = null
            }
        }
        title = ConstantKey.CREATE_A_JOB
        if (bundle?.getString(ConstantKey.TITLE) != null) {
            title = bundle.getString(ConstantKey.TITLE)
        }
        if (bundle != null && bundle.getBoolean(ConstantKey.EDIT, false)) {
            taskModel = TaskDetailsActivity.taskModel!!
            isEditTask = true
        }
        if (bundle != null && bundle.getBoolean(ConstantKey.DRAFT_JOB, false)) {
            slug = bundle.getString(ConstantKey.SLUG)
            isJobDraftedYet = true
        }
    }

    private fun initIDS() {
        creatingTaskLayout = findViewById(R.id.creating_task_layout)
        toolbar = findViewById(R.id.toolbar)
        lytBtnDetails = findViewById(R.id.lyt_btn_details)
        lytBntDateTime = findViewById(R.id.lyt_bnt_date_time)
        lytBtnBudget = findViewById(R.id.lyt_btn_budget)
        cardSelectionView = findViewById(R.id.card_selection_view)
        viewPager = findViewById(R.id.view_pager)
        imgDetails = findViewById(R.id.img_details)
        ivDelete = findViewById(R.id.iv_delete)
        txtDetails = findViewById(R.id.txt_details)
        imgDateTime = findViewById(R.id.img_date_time)
        txtDateTime = findViewById(R.id.txt_date_time)
        imgBudget = findViewById(R.id.img_budget)
        txtBudget = findViewById(R.id.txt_budget)
    }

    fun setActionDraftTaskDetails(actionDraftTaskDetails: ActionDraftTaskDetails?) {
        this.actionDraftTaskDetails = actionDraftTaskDetails
    }

    fun setActionDraftDateTime(actionDraftDateTime: ActionDraftDateTime?) {
        this.actionDraftDateTime = actionDraftDateTime
    }

    fun setActionDraftTaskBudget(actionDraftTaskBudget: ActionDraftTaskBudget?) {
        this.actionDraftTaskBudget = actionDraftTaskBudget
    }

    private fun initComponent() {
        setupViewPager(viewPager)
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener)
        viewPager.currentItem = 0
        viewPager.offscreenPageLimit = 3
        selectDetailsBtn()
        lytBntDateTime.isEnabled = false
        lytBtnBudget.isEnabled = false
        lytBtnDetails.isSelected = true
    }

    private fun setupViewPager(viewPager: ViewPager?) {
        adapter.addFragment(TaskDetailFragment.newInstance(taskModel.title, taskModel.description, taskModel.musthave, taskModel.taskType, taskModel.location, taskModel.position, AttachmentModels(taskModel.attachments), this, isEditTask, taskModel.slug), resources.getString(R.string.details))
        adapter.addFragment(TaskDateTimeFragment.newInstance(if (taskModel.dueDate == null) null else taskModel.dueDate.substring(0, 10), taskModel.dueTime, this), resources.getString(R.string.date_time))
        adapter.addFragment(TaskBudgetFragment.newInstance(taskModel.budget, taskModel.hourlyRate, taskModel.totalHours, taskModel.paymentType, this), resources.getString(R.string.budget))
        viewPager!!.adapter = adapter
    }

    private fun initToolbar(title: String?) {
        var title = title
        if (isJobDraftedYet) {
            toolbar.setNavigationIcon(R.drawable.ic_back_black)
            ivDelete.visibility = View.VISIBLE
            title = "Edit draft"
        } else toolbar.setNavigationIcon(R.drawable.ic_cancel)
        ivDelete.setOnClickListener { v: View? ->
            val confirmBottomSheet = ConfirmDeleteTaskBottomSheet(this)
            confirmBottomSheet.listener = this
            confirmBottomSheet.show(this.supportFragmentManager, "")
        }
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = title
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_task_create, menu)
        return true
    }

    @SuppressLint("NonConstantResourceId")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                isDraftWorkDone = false
                onBackPressed()
            }
            R.id.menu_attachment -> {
                val bundle = Bundle()
                val intent = Intent(this, AttachmentActivity::class.java)
                bundle.putParcelableArrayList(ConstantKey.ATTACHMENT, taskModel.attachments)
                bundle.putString(ConstantKey.TITLE, title)
                bundle.putString(ConstantKey.SLUG, taskModel.slug)
                intent.putExtras(bundle)
                startActivityForResult(intent, ConstantKey.RESULTCODE_ATTACHMENT)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (isDraftWorkDone) {
            super.onBackPressed()
        } else {
            if (isEditTask) {
                super.onBackPressed()
            } else {
                if (isJobDraftedYet) super.onBackPressed() else actionDraftTaskDetails!!.callDraftTaskDetails(taskModel)
            }
        }
    }

    var viewPagerPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            when (position) {
                0 -> {
                    selectDetailsBtn()
                    val fragmentDetails = adapter.getItem(0) as TaskDetailFragment
                    fragmentDetails.checkTabs()
                }
                1 -> {
                    selectDateTimeBtn()
                    val fragmentDateTime = adapter.getItem(1) as TaskDateTimeFragment
                    fragmentDateTime.checkTabs()
                }
                2 -> selectBudgetBtn()
            }
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
        override fun onPageScrollStateChanged(arg0: Int) {}
    }

    private fun selectBudgetBtn() {
        imgBudget.imageTintList = AppCompatResources.getColorStateList(this, R.color.colorPrimary)
        txtBudget.setTextColor(resources.getColor(R.color.colorPrimary))
        val csl_green = AppCompatResources.getColorStateList(this, R.color.green)
        imgDateTime.imageTintList = csl_green
        imgDetails.imageTintList = csl_green
        txtDateTime.setTextColor(resources.getColor(R.color.green))
        txtDetails.setTextColor(resources.getColor(R.color.green))
    }

    private fun selectDateTimeBtn() {
        imgDateTime.imageTintList = AppCompatResources.getColorStateList(this, R.color.colorPrimary)
        txtDateTime.setTextColor(resources.getColor(R.color.colorPrimary))
        imgDetails.imageTintList = AppCompatResources.getColorStateList(this, R.color.green)
        imgBudget.imageTintList = AppCompatResources.getColorStateList(this, R.color.greyC4C4C4)
        txtDetails.setTextColor(resources.getColor(R.color.green))
        txtBudget.setTextColor(resources.getColor(R.color.colorGrayC9C9C9))
    }

    private fun selectDetailsBtn() {
        imgDetails.imageTintList =  AppCompatResources.getColorStateList(this, R.color.colorPrimary)
        txtDetails.setTextColor(resources.getColor(R.color.colorPrimary))
        val cslGrey = AppCompatResources.getColorStateList(this, R.color.greyC4C4C4)
        imgDateTime.imageTintList = cslGrey
        imgBudget.imageTintList = cslGrey
        txtDateTime.setTextColor(resources.getColor(R.color.colorGrayC9C9C9))
        txtBudget.setTextColor(resources.getColor(R.color.colorGrayC9C9C9))
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        menu.setHeaderTitle("Select The Action")
    }

    private fun onViewClick() {
        lytBtnDetails.setOnClickListener {
            viewPager.currentItem = 0
            selectDetailsBtn()
        }
        lytBntDateTime.setOnClickListener {
            viewPager.currentItem = 1
            selectDateTimeBtn()
        }
        lytBtnBudget.setOnClickListener {
            viewPager.currentItem = 2
            selectBudgetBtn()
        }
    }
    override fun onNextClick(title: String?, description: String?, musthave: ArrayList<String>?, task_type: String?, location: String?, positionModel: PositionModel?, attachmentArrayList: ArrayList<AttachmentModel>?) {
        taskModel.title = title
        taskModel.description = description
        taskModel.musthave = musthave
        taskModel.position = positionModel
        taskModel.location = location
        taskModel.taskType = task_type
        taskModel.attachments = attachmentArrayList
        viewPager.currentItem = 1
        selectDateTimeBtn()    }



    override fun onNextClickDateTime(due_date: String?, dueTimeModel: DueTimeModel?) {
        taskModel.dueDate = due_date
        taskModel.dueTime = dueTimeModel
        viewPager.currentItem = 2
        selectBudgetBtn()
    }

    override fun onBackClickDateTime(due_date: String?, dueTimeModel: DueTimeModel?) {
        taskModel.dueDate = due_date
        taskModel.dueTime = dueTimeModel
        viewPager.currentItem = 0
        selectDetailsBtn()
    }

    override fun onValidDataFilledDateTimeNext() {
        lytBntDateTime.isEnabled = true
        lytBtnBudget.isSelected = true
        lytBtnDetails.isEnabled = true
    }

    override fun onValidDataFilledDateTimeBack() {
        lytBntDateTime.isEnabled = false
        lytBtnBudget.isEnabled = false
        lytBtnDetails.isSelected = true
    }


    override fun onValidDataFilled() {
        lytBntDateTime.isSelected = true
        lytBtnBudget.isEnabled = false
        lytBtnDetails.isEnabled = true
    }

    override fun draftTaskDetails(taskModel: TaskModel?, moveForeword: Boolean) {
        if (moveForeword) {
            actionDraftDateTime!!.callDraftTaskDateTime(this.taskModel)
        } else {
            this.taskModel = taskModel!!
            if (taskModel.title != null && taskModel.title.trim { it <= ' ' }.length >= 10) {
                uploadDataToServer(true)
            } else {
                val intent = Intent()
                val bundle = Bundle()
                bundle.putBoolean(ConstantKey.CATEGORY, true)
                intent.putExtras(bundle)
                setResult(ConstantKey.RESULTCODE_CATEGORY, intent)
                super.onBackPressed()
            }
        }
    }

    override fun draftTaskDateTime(taskModel: TaskModel?, moveForeword: Boolean) {
        if (moveForeword) {
            actionDraftTaskBudget!!.callDraftTaskBudget(this.taskModel)
        } else {
            this.taskModel = taskModel!!
            uploadDataToServer(true)
        }
    }

    override fun draftTaskBudget(taskModel: TaskModel?) {
        this.taskModel = taskModel!!
        uploadDataToServer(true)
    }

    override fun onNextClickBudget(budget: Int, hour_budget: Int, total_hours: Int, payment_type: String?) {
        taskModel.budget = budget
        taskModel.hourlyRate = hour_budget
        taskModel.totalHours = total_hours
        taskModel.paymentType = payment_type
        uploadDataToServer(false)
    }

    private fun uploadDataToServer(draft: Boolean) {
        val queryParameter: String
        val METHOD: Int
        if (taskModel.slug != null) {
            queryParameter = "/" + taskModel.slug
            METHOD = Request.Method.PATCH
        } else {
            queryParameter = "/create"
            METHOD = Request.Method.POST
        }
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(METHOD, Constant.URL_TASKS + queryParameter,
                Response.Listener { response: String? ->
                    Timber.e(response)
                    hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                var intent: Intent
                                val bundle: Bundle
                                if (draft) {
                                    intent = Intent()
                                    bundle = Bundle()
                                    bundle.putBoolean(ConstantKey.UPDATE_TASK, true)
                                    intent.putExtras(bundle)
                                    setResult(ConstantKey.RESULTCODE_UPDATE_TASK, intent)
                                    isDraftWorkDone = true
                                    //                                    Toasty.info(TaskCreateActivity.this,"Draft saved", Toast.LENGTH_LONG).show();
                                    Toast.makeText(this@TaskCreateActivity, "Draft saved", Toast.LENGTH_LONG).show()
                                    onBackPressed()
                                    return@Listener
                                } else {
                                    intent = Intent()
                                    bundle = Bundle()
                                    bundle.putBoolean(ConstantKey.CATEGORY, true)
                                    intent.putExtras(bundle)
                                    setResult(ConstantKey.RESULTCODE_CATEGORY, intent)
                                }
                                if (draft) {
                                    isDraftWorkDone = true
                                    onBackPressed()
                                    return@Listener
                                }
                                FireBaseEvent.getInstance(applicationContext)
                                        .sendEvent(FireBaseEvent.Event.POST_A_JOB,
                                                FireBaseEvent.EventType.API_RESPOND_SUCCESS,
                                                FireBaseEvent.EventValue.POST_A_JOB_SUBMIT)
                                if (isEditTask) {
                                    intent = Intent(this, CompleteMessageActivity::class.java)
                                    val bundle1 = Bundle()
                                    bundle1.putString(ConstantKey.COMPLETES_MESSAGE_TITLE, "Job Edited Successfully")
                                    bundle1.putInt(ConstantKey.COMPLETES_MESSAGE_FROM, ConstantKey.RESULTCODE_CREATE_TASK)
                                    bundle1.putString(ConstantKey.SLUG, taskModel.slug)
                                    intent.putExtras(bundle1)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    var taskSlug: String? = null
                                    try {
                                        if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                            val data = jsonObject.getJSONObject("data")
                                            taskSlug = data.getString("slug")
                                            Log.d("taskSlug", taskSlug)
                                        } else taskSlug = null
                                    } catch (e: Exception) {
                                        taskSlug = null
                                    }
                                    intent = Intent(this@TaskCreateActivity, CompleteMessageActivity::class.java)
                                    val bundle2 = Bundle()
                                    bundle2.putInt(ConstantKey.COMPLETES_MESSAGE_FROM, ConstantKey.RESULTCODE_CREATE_TASK)
                                    bundle2.putString(ConstantKey.SLUG, taskSlug)
                                    intent.putExtras(bundle2)
                                    startActivity(intent)
                                    finish()
                                }
                            } else {
                                showToast("Something went Wrong", this@TaskCreateActivity)
                            }
                        }
                    } catch (e: JSONException) {
                        Timber.e(e.toString())
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    val networkResponse = error.networkResponse
                    if (networkResponse?.data != null) {
                        val jsonError = String(networkResponse.data)
                        if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                            unauthorizedUser()
                            hideProgressDialog()
                            return@ErrorListener
                        }
                        try {
                            val jsonObject = JSONObject(jsonError)
                            val jsonObjectError = jsonObject.getJSONObject("error")
                            if (jsonObjectError.has("message")) {
                                showToast(jsonObjectError.getString("message"), this)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        showToast("Something Went Wrong", this@TaskCreateActivity)
                    }
                    println(error.toString())
                    hideProgressDialog()
                }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                map1["Accept"] = "application/json"
                map1["X-Requested-With"] = "XMLHttpRequest"
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }

            override fun getParams(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["category_id"] = Integer.toString(taskModel.category_id)
                map1["title"] = taskModel.title
                if (taskModel.description != null) map1["description"] = taskModel.description
                if (taskModel.location != null && taskModel.location != "") map1["location"] = taskModel.location
                if (taskModel.position != null) {
                    map1["latitude"] = taskModel.position.latitude.toString()
                    map1["longitude"] = taskModel.position.longitude.toString()
                }
                if (taskModel.taskType != null) map1["task_type"] = taskModel.taskType
                if (taskModel.paymentType != null) map1["payment_type"] = taskModel.paymentType
                if (taskModel.paymentType != null) {
                    if (taskModel.paymentType.equals("fixed", ignoreCase = true)) {
                        if (taskModel.budget >= 5) map1["budget"] = taskModel.budget.toString()
                    } else {
                        if (taskModel.totalHours * taskModel.hourlyRate >= 5) {
                            map1["budget"] = (taskModel.hourlyRate * taskModel.totalHours).toString()
                            map1["total_hours"] = taskModel.totalHours.toString()
                            map1["hourly_rate"] = taskModel.hourlyRate.toString()
                        }
                    }
                }
                if (taskModel.dueDate != null) map1["due_date"] = Tools.getApplicationFromatToServerFormat(taskModel.dueDate)
                if (taskModel.attachments != null && taskModel.attachments.size != 0) {
                    var i = 0
                    while (taskModel.attachments.size > i) {
                        if (taskModel.attachments[i].id != null) {
                            map1["attachments[$i]"] = taskModel.attachments[i].id.toString()
                        }
                        i++
                    }
                }
                if (taskModel.musthave != null && taskModel.musthave.size != 0) {
                    var i = 0
                    while (taskModel.musthave.size > i) {
                        map1["musthave[$i]"] = taskModel.musthave[i]
                        i++
                    }
                }
                if (taskModel.dueTime != null) {
                    var count = 0
                    if (taskModel.dueTime.morning) {
                        map1["due_time[$count]"] = "morning"
                        count += 1
                    }
                    if (taskModel.dueTime.afternoon) {
                        map1["due_time[$count]"] = "afternoon"
                        count += 1
                    }
                    if (taskModel.dueTime.evening) {
                        map1["due_time[$count]"] = "evening"
                        count += 1
                    }
                    if (taskModel.dueTime.anytime) {
                        map1["due_time[$count]"] = "anytime"
                    }
                }
                if (draft) {
                    map1["draft"] = "1"
                }
                println(map1.size)
                println(map1.toString())
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this@TaskCreateActivity)
        requestQueue.add(stringRequest)
    }

    override fun onBackClickBudget(budget: Int, hour_budget: Int, total_hours: Int, payment_type: String?) {
        taskModel.budget = budget
        taskModel.hourlyRate = hour_budget
        taskModel.totalHours = total_hours
        taskModel.paymentType = payment_type
        viewPager.currentItem = 1
        selectDateTimeBtn()
    }

    override fun onValidDataFilledBudgetNext() {}
    override fun onValidDataFilledBudgetBack() {
        lytBntDateTime.isSelected = true
        lytBtnBudget.isEnabled = false
        lytBtnDetails.isEnabled = true
    }



    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) (fragment as? TaskDetailFragment)?.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ConstantKey.RESULTCODE_ATTACHMENT) {
            if (data != null) {
                if (data.getParcelableArrayListExtra<Parcelable>(ConstantKey.ATTACHMENT) != null) {
                    val attachmentArrayList: ArrayList<AttachmentModel> = data.getParcelableArrayListExtra(ConstantKey.ATTACHMENT)!!
                    taskModel.attachments = attachmentArrayList
                }
            }
        }
    }

    override fun onDeleteConfirmClick() {
        deleteTask(slug)
    }

    protected fun deleteTask(slug: String?) {
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.DELETE, Constant.URL_TASKS + "/" + slug,
                Response.Listener { response: String? ->
                    Timber.e(response)
                    hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                showToast("Job has been deleted successfully", this)
                                sessionManager.needRefresh = true
                                onBackPressed()
                            } else {
                                showToast("Something went Wrong", this)
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
                            unauthorizedUser()
                            return@ErrorListener
                        }
                        try {
                            val jsonObject = JSONObject(jsonError)
                            val jsonObject_error = jsonObject.getJSONObject("error")
                            if (jsonObject_error.has("message")) {
                                showToast(jsonObject_error.getString("message"), this)
                            }
                            if (jsonObject_error.has("errors")) {
                                val jsonObject_errors = jsonObject_error.getJSONObject("errors")
                            }
                            //  ((CredentialActivity)requireActivity()).showToast(message,requireActivity());
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        showToast("Something Went Wrong", this)
                    }
                    Timber.e(error.toString())
                }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["X-Requested-With"] = "XMLHttpRequest"
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    interface ActionDraftTaskDetails {
        fun callDraftTaskDetails(taskModel: TaskModel?)
    }

    interface ActionDraftDateTime {
        fun callDraftTaskDateTime(taskModel: TaskModel?)
    }

    interface ActionDraftTaskBudget {
        fun callDraftTaskBudget(taskModel: TaskModel?)
    }
}