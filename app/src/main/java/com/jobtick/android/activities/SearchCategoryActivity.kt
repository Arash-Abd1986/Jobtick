package com.jobtick.android.activities

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.adapers.PreviewAdapter
import com.jobtick.android.adapers.TaskCategoryAdapter
import com.jobtick.android.models.PreviewCategoryModel
import com.jobtick.android.models.PreviewCategorySetModel
import com.jobtick.android.models.TaskCategory
import com.jobtick.android.pagination.PaginationListener
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.SessionManager
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class SearchCategoryActivity : ActivityBase(), OnEditorActionListener, TaskCategoryAdapter.OnItemClickListener, PreviewAdapter.OnItemClickListener<PreviewCategoryModel?> {
    private lateinit var lytSearchNew: MaterialButton
    private lateinit var ivBack: ImageView
    private lateinit var recyclerViewCategories: RecyclerView
    private lateinit var emptySearch: RelativeLayout
    private lateinit var edtSearchCategories: EditText
    private var sessionManagerS: SessionManager? = null
    private var adapter: TaskCategoryAdapter? = null
    private var previewCategorySetModel: PreviewCategorySetModel? = null
    private var previewAdapter: PreviewAdapter? = null
    private var currentPage = PaginationListener.PAGE_START
    private var isLastPageItem = false
    private var totalPage = 10
    private var isLoadingItem = false
    private var query = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_all)
        setIDs()
        onViewClick()
        //  RelativeLayout emptySearch = findViewById(R.id.empty_search);
        sessionManagerS = SessionManager(this)
        edtSearchCategories.requestFocus()
        edtSearchCategories.performClick()
        edtSearchCategories.setOnEditorActionListener(this)
        setPreviewAdapter()
    }

    private fun setIDs() {
        lytSearchNew = findViewById(R.id.lyt_search_new)
        lytSearchNew = findViewById(R.id.iv_back)
        recyclerViewCategories = findViewById(R.id.list)
        emptySearch = findViewById(R.id.empty_search)
        edtSearchCategories = findViewById(R.id.edt_search_categoreis)
    }

   private fun onViewClick() {
        ivBack.setOnClickListener {
            onBackPressed()
        }
        lytSearchNew.setOnClickListener {
            edtSearchCategories.setText("")
            edtSearchCategories.requestFocus()
            edtSearchCategories.performClick()
            showKeyboard(edtSearchCategories)
        }

    }

    override fun onEditorAction(textView: TextView?, actionId: Int, keyEvent: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            //set adapter to online mode (previewMode will be false)
            setOnlineAdapter()
            setLoadMoreListener()
            taskCategoryData
            return true
        }
        return false
    }

    val taskCategoryData: List<TaskCategory>
        get() {
            val items: MutableList<TaskCategory> = ArrayList()
            query = edtSearchCategories.text.toString()
            val stringRequest: StringRequest = object : StringRequest(Method.GET,
                    Constant.BASE_URL + Constant.TASK_CATEGORY + "?query=" + query + "&page=" + currentPage,
                    Response.Listener { response: String? ->
                        try {
                            val jsonObject = JSONObject(response!!)
                            if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                val jsonArrayData = jsonObject.getJSONArray("data")
                                var i = 0
                                while (jsonArrayData.length() > i) {
                                    val jsonobjectTaskmodelList = jsonArrayData.getJSONObject(i)
                                    val taskModel = TaskCategory().getJsonToModel(jsonobjectTaskmodelList, this@SearchCategoryActivity)
                                    items.add(taskModel)
                                    i++
                                }
                            } else {
                                showToast("some went to wrong", this@SearchCategoryActivity)
                                return@Listener
                            }
                            if (jsonObject.has("meta") && !jsonObject.isNull("meta")) {
                                val jsonObjectMeta = jsonObject.getJSONObject("meta")
                                totalPage = jsonObjectMeta.getInt("last_page")
                                Constant.PAGE_SIZE = jsonObjectMeta.getInt("per_page")
                            }
                            if (currentPage != PaginationListener.PAGE_START) adapter!!.removeLoading()
                            if (items.size <= 0) {
                                emptySearch.visibility = View.VISIBLE
                                recyclerViewCategories.visibility = View.GONE
                            } else {
                                recyclerViewCategories.visibility = View.VISIBLE
                                emptySearch.visibility = View.GONE
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
                override fun getHeaders(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["Content-Type"] = "application/x-www-form-urlencoded"
                    map1["Authorization"] = "Bearer " + sessionManagerS!!.accessToken
                    map1["Version"] = BuildConfig.VERSION_CODE.toString()
                    return map1
                }
            }
            stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            val requestQueue = Volley.newRequestQueue(this@SearchCategoryActivity)
            requestQueue.add(stringRequest)
            return items
        }

    private fun setPreviewAdapter() {
        previewCategorySetModel = sessionManagerS!!.getPreviewModel(SearchCategoryActivity::class.java)
        if (previewCategorySetModel == null) previewCategorySetModel = PreviewCategorySetModel()
        previewAdapter = PreviewAdapter(ArrayList(previewCategorySetModel!!.previewSet))
        recyclerViewCategories.adapter = previewAdapter
        recyclerViewCategories.setHasFixedSize(true)
        previewAdapter!!.setOnItemClickListener(this)
    }

    private fun setOnlineAdapter() {
        adapter = TaskCategoryAdapter(this, ArrayList())
        recyclerViewCategories.adapter = adapter
        adapter!!.setOnItemClickListener(this)
    }

    private var previewMode = true
    private fun setLoadMoreListener() {
        if (!previewMode) return
        recyclerViewCategories.addOnScrollListener(object : PaginationListener((recyclerViewCategories.layoutManager as LinearLayoutManager?)!!) {
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
        previewMode = false
    }

    private fun showKeyboard(editText: EditText?) {
        editText!!.post {
            val imm = baseContext
                    .getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, 0)
        }
    }

    override fun onItemClick(view: View, obj: PreviewCategoryModel?, position: Int) {
        previewCategorySetModel!!.addItem(obj)
        sessionManagerS!!.setPreviewModel(previewCategorySetModel, SearchCategoryActivity::class.java)
        val creatingTask = Intent(this@SearchCategoryActivity, TaskCreateActivity::class.java)
        val bundle = Bundle()
        bundle.putInt(ConstantKey.CATEGORY_ID, obj!!.id)
        creatingTask.putExtras(bundle)
        startActivityForResult(creatingTask, ConstantKey.RESULTCODE_CATEGORY)
        finish()
    }

    override fun onItemClick(view: View, obj: TaskCategory, position: Int) {
        onItemClick(view, obj.previewModel, position)
    }
}