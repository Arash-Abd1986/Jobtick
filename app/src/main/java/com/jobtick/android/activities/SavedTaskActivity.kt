package com.jobtick.android.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.MaterialToolbar
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.adapers.TaskListAdapter
import com.jobtick.android.interfaces.OnRemoveSavedTaskListener
import com.jobtick.android.models.TaskModel
import com.jobtick.android.pagination.PaginationListener
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.viewmodel.SavedJobsViewModel
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class SavedTaskActivity : ActivityBase(), TaskListAdapter.OnItemClickListener, OnRefreshListener,
    OnRemoveSavedTaskListener {
    private var taskListAdapter: TaskListAdapter? = null
    private var currentPage = PaginationListener.PAGE_START
    private var isLastPage = false
    private var totalPage = 10
    private var totalItem = 10
    private val isLoading = false
    private lateinit var sessionManagerL: SessionManager
    private lateinit var viewModel: SavedJobsViewModel
    private var recyclerViewStatus: RecyclerView? = null
    private var swipeRefresh: SwipeRefreshLayout? = null
    private var noPosts: LinearLayout? = null
    private var toolbar: MaterialToolbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_task)
        setIDS()
        noPosts = findViewById(R.id.no_posts_container)
        onRemoveSavedtasklistener = this
        sessionManagerL = SessionManager(this@SavedTaskActivity)
        val layoutManager = LinearLayoutManager(this@SavedTaskActivity)
        recyclerViewStatus!!.layoutManager = layoutManager
        taskListAdapter = TaskListAdapter(ArrayList(), null)
        recyclerViewStatus!!.adapter = taskListAdapter
        taskListAdapter!!.setOnItemClickListener(this)
        swipeRefresh!!.setOnRefreshListener(this)
        initToolbar()

        initVM()
        // swipeRefresh.setRefreshing(true);
        getStatusList()
    }

    private fun initVM() {
        viewModel = ViewModelProvider(this).get(SavedJobsViewModel::class.java)
        val items = ArrayList<TaskModel>()

        viewModel.getJobsResponse().observe(this, androidx.lifecycle.Observer {
            swipeRefresh!!.isRefreshing = false
            // categoryArrayList.clear();
            try {
                val jsonObject = it
                Timber.e(jsonObject.toString())
                if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                    val jsonArray_data = jsonObject.getJSONArray("data")
                    var i = 0
                    while (jsonArray_data.length() > i) {
                        val jsonObject_taskModel_list = jsonArray_data.getJSONObject(i)
                        val taskModel = TaskModel().getJsonToModel(
                            jsonObject_taskModel_list.getJSONObject("task"),
                            this@SavedTaskActivity
                        )
                        items.add(taskModel)
                        i++
                    }
                } else {
                    showToast("some went to wrong", this@SavedTaskActivity)
                    return@Observer
                }
                if (jsonObject.has("meta") && !jsonObject.isNull("meta")) {
                    val jsonObject_meta = jsonObject.getJSONObject("meta")
                    totalPage = jsonObject_meta.getInt("last_page")
                    totalItem = jsonObject_meta.getInt("total")
                    Constant.PAGE_SIZE = jsonObject_meta.getInt("per_page")
                }
                if (items.size <= 0) {
                    noPosts!!.visibility = View.VISIBLE
                    recyclerViewStatus!!.visibility = View.GONE
                } else {
                    noPosts!!.visibility = View.GONE
                    recyclerViewStatus!!.visibility = View.VISIBLE
                }
                taskListAdapter!!.addItems(items, totalItem)
                swipeRefresh!!.isRefreshing = false
            } catch (e: JSONException) {
                hideProgressDialog()
                Timber.e(e.toString())
                e.printStackTrace()
            }
        })
    }

    private fun setIDS() {
        recyclerViewStatus = findViewById(R.id.recyclerview_savedTask)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        toolbar = findViewById(R.id.toolbar)
    }

    private fun initToolbar() {
        toolbar!!.setNavigationIcon(R.drawable.ic_back)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Saved Jobs"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }//  swipeRefresh.setRefreshing(false);

    // categoryArrayList.clear();
    private fun getStatusList() {
        swipeRefresh!!.isRefreshing = true
        viewModel.getJobs(
            sessionManager!!.accessToken,
            Volley.newRequestQueue(this),
            currentPage.toString()
        )
    }

    override fun onRefresh() {
        swipeRefresh!!.isRefreshing = true
        currentPage = PaginationListener.PAGE_START
        isLastPage = false
        taskListAdapter!!.clear()
        getStatusList()
    }

    override fun onItemClick(view: View, obj: TaskModel, position: Int, action: String) {
        if (obj.status.toLowerCase().equals(Constant.TASK_DRAFT.toLowerCase(), ignoreCase = true)) {
            //    getDataFromServer(obj.getSlug());
        } else {
            val intent = Intent(this@SavedTaskActivity, TaskDetailsActivity::class.java)
            val bundle = Bundle()
            bundle.putString(ConstantKey.SLUG, obj.slug)
            //   bundle.putInt(ConstantKey.USER_ID, obj.getPoster().getId());
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    override fun onRemoveSavedTask() {
        currentPage = PaginationListener.PAGE_START
        isLastPage = false
        taskListAdapter!!.clear()
        getStatusList()
    }

    companion object {
        @JvmField
        var onRemoveSavedtasklistener: OnRemoveSavedTaskListener? = null
    }
}