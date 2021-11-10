package com.jobtick.android.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.adapers.TaskCategoryAdapter
import com.jobtick.android.models.TaskCategory
import com.jobtick.android.pagination.PaginationListener
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.ConstantKey
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class CategoryListActivity : ActivityBase(), TaskCategoryAdapter.OnItemClickListener {
    @JvmField
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recyclerView_categories)
    var recyclerViewCategories: RecyclerView? = null

    @JvmField
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.iv_backbutton)
    var ivBackButton: ImageView? = null
    private var adapter: TaskCategoryAdapter? = null
    private var currentPage = PaginationListener.PAGE_START
    private var isLastPageItem = false
    private var totalPage = 10
    private var isLoadingItem = false
    private var query: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_list)
        setIDs()
        init()
        setCategoryData()
        clickEvent()
    }

    private fun setIDs() {
        recyclerViewCategories = findViewById(R.id.recyclerView_categories)
        ivBackButton = findViewById(R.id.iv_backbutton)
    }

    fun clickEvent() {
        ivBackButton!!.setOnClickListener { v: View? -> finish() }
    }

    fun init() {
        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.getString("category") != null) {
                query = bundle.getString("category")
            }
        }
    }

    private fun setCategoryData() {
        val layoutManager = LinearLayoutManager(this@CategoryListActivity)
        recyclerViewCategories!!.layoutManager = layoutManager
        recyclerViewCategories!!.setHasFixedSize(true)
        val items = taskCategoryData
        adapter = TaskCategoryAdapter(this@CategoryListActivity, ArrayList())
        recyclerViewCategories!!.adapter = adapter
        adapter!!.setOnItemClickListener(this)
        recyclerViewCategories!!.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                isLoadingItem = true
                currentPage++
                taskCategoryData
            }

            override val isLastPage: Boolean
                get() = isLastPageItem
            override val isLoading: Boolean
                get() = isLoadingItem
        })
    }

    override fun onItemClick(view: View, obj: TaskCategory, position: Int) {
        val creatingTask = Intent(this@CategoryListActivity, TaskCreateActivity::class.java)
        val bundle = Bundle()
        bundle.putInt(ConstantKey.CATEGORY_ID, obj.id)
        creatingTask.putExtras(bundle)
        startActivityForResult(creatingTask, ConstantKey.RESULTCODE_CATEGORY)
        finish()
    }

    val taskCategoryData: List<TaskCategory>
        get() {
            val items: MutableList<TaskCategory> = ArrayList()
            val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.BASE_URL + Constant.TASK_CATEGORY + "?query=" + query + "&page=" + currentPage, label@
            Response.Listener { response: String? ->
                try {
                    val jsonObject = JSONObject(response!!)
                    if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                        val jsonArrayData = jsonObject.getJSONArray("data")
                        var i = 0
                        while (jsonArrayData.length() > i) {
                            val jsonObjectTaskModelList = jsonArrayData.getJSONObject(i)
                            val taskModel = TaskCategory().getJsonToModel(jsonObjectTaskModelList, this@CategoryListActivity)
                            items.add(taskModel)
                            i++
                        }
                    } else {
                        showToast("some went to wrong", this@CategoryListActivity)
                        return@Listener
                    }
                    if (jsonObject.has("meta") && !jsonObject.isNull("meta")) {
                        val jsonObjectMeta = jsonObject.getJSONObject("meta")
                        totalPage = jsonObjectMeta.getInt("last_page")
                        Constant.PAGE_SIZE = jsonObjectMeta.getInt("per_page")
                    }
                    if (currentPage != PaginationListener.PAGE_START) adapter!!.removeLoading()
                    if (items.size <= 0) {
                        recyclerViewCategories!!.visibility = View.GONE
                    } else {
                        recyclerViewCategories!!.visibility = View.VISIBLE
                    }
                    adapter!!.addItems(items)
                    if (currentPage < totalPage) {
                        adapter!!.addLoading()
                    } else {
                        isLastPageItem = true
                    }
                    isLoadingItem = false
                } catch (e: JSONException) {
                    hideProgressDialog()
                    e.printStackTrace()
                }
            },
                    Response.ErrorListener { error: VolleyError -> errorHandle1(error.networkResponse) }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["Content-Type"] = "application/x-www-form-urlencoded"
                    map1["Authorization"] = "Bearer " + sessionManager.accessToken
                    map1["Version"] = BuildConfig.VERSION_CODE.toString()
                    return map1
                }
            }
            stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            val requestQueue = Volley.newRequestQueue(this@CategoryListActivity)
            requestQueue.add(stringRequest)
            return items
        }
}