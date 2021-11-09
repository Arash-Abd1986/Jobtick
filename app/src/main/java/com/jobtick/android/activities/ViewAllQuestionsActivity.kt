package com.jobtick.android.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.MaterialToolbar
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.adapers.QuestionListAdapter
import com.jobtick.android.models.QuestionModel
import com.jobtick.android.pagination.PaginationListener
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.Helper
import com.jobtick.android.utils.SessionManager
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class ViewAllQuestionsActivity : ActivityBase(), OnRefreshListener, QuestionListAdapter.OnItemClickListener {
    private lateinit var toolbar: MaterialToolbar
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var recyclerViewAllQuestions: RecyclerView
    private var currentPage = PaginationListener.PAGE_START
    private var isLastPageItem = false
    private var totalPage = 10
    private var isLoadingItem = false
    private var questionListAdapter: QuestionListAdapter? = null
    private var strSlug: String? = null
    private var status: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_all_questions)
        setIDs()
        val bundle = intent.extras
        if (bundle?.getString(ConstantKey.SLUG) != null) {
            strSlug = bundle.getString(ConstantKey.SLUG)
        }
        if (bundle?.getString(ConstantKey.TASK_STATUS) != null) {
            status = bundle.getString(ConstantKey.TASK_STATUS)
        }
        toolbar.setNavigationOnClickListener { onBackPressed() }
        initComponent()
    }

    private fun setIDs() {
        toolbar = findViewById(R.id.toolbar)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        recyclerViewAllQuestions = findViewById(R.id.recycler_view_all_questions)
    }

    private fun initComponent() {
        recyclerViewAllQuestions.layoutManager = LinearLayoutManager(this@ViewAllQuestionsActivity, LinearLayoutManager.VERTICAL, false)
        recyclerViewAllQuestions.setHasFixedSize(true)
        sessionManager = SessionManager(this@ViewAllQuestionsActivity)
        questionListAdapter = QuestionListAdapter(this@ViewAllQuestionsActivity, ArrayList(), status!!, 0,sessionManager.userAccount.id)
        recyclerViewAllQuestions.adapter = questionListAdapter
        swipeRefresh.setOnRefreshListener(this)

        val layoutManager = LinearLayoutManager(this@ViewAllQuestionsActivity)
        questionListAdapter!!.setOnItemClickListener(this)
        swipeRefresh.isRefreshing = true
        doApiCall()
        recyclerViewAllQuestions.addOnScrollListener(object : PaginationListener(layoutManager) {
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

    private fun doApiCall() {
        val items = ArrayList<QuestionModel>()
        Helper.closeKeyboard(this@ViewAllQuestionsActivity)
        val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.URL_TASKS + "/" + strSlug + "/questions?page=" + currentPage,
                Response.Listener { response ->
                    Timber.e(response)
                    // categoryArrayList.clear();
                    try {
                        val jsonObject = JSONObject(response!!)
                        Timber.e(jsonObject.toString())
                        if (!jsonObject.has("data") && jsonObject.isNull("data")) {
                            showToast("some went to wrong", this@ViewAllQuestionsActivity)
                            return@Listener
                        }
                        val jsonArrayData = jsonObject.getJSONArray("data")
                        var i = 0
                        while (jsonArrayData.length() > i) {
                            val jsonObjectOffers = jsonArrayData.getJSONObject(i)
                            val questionModel = QuestionModel().getJsonToModel(jsonObjectOffers)
                            items.add(questionModel)
                            i++
                        }
                        if (jsonObject.has("meta") && !jsonObject.isNull("meta")) {
                            val jsonObjectMeta = jsonObject.getJSONObject("meta")
                            totalPage = jsonObjectMeta.getInt("last_page")
                            Constant.PAGE_SIZE = jsonObjectMeta.getInt("per_page")
                        }

                        if (currentPage != PaginationListener.PAGE_START) questionListAdapter!!.removeLoading()
                        questionListAdapter!!.addItems(items)
                        swipeRefresh.isRefreshing = false
                        // check weather is last page or not
                        if (currentPage < totalPage) {
                            questionListAdapter!!.addLoading()
                        } else {
                            isLastPageItem = true
                        }
                        isLoadingItem = false
                    } catch (e: JSONException) {
                        hideProgressDialog()
                        Timber.e(e.toString())
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    swipeRefresh.isRefreshing = false
                    errorHandle1(error.networkResponse)
                }) {
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
        val requestQueue = Volley.newRequestQueue(this@ViewAllQuestionsActivity)
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
    }

    override fun onRefresh() {
        currentPage = PaginationListener.PAGE_START
        isLastPageItem = false
        questionListAdapter!!.clear()
        doApiCall()
    }

    override fun onItemQuestionClick(view: View?, obj: QuestionModel?, position: Int, action: String?) {
        if (action.equals("reply", ignoreCase = true)) {
            val intent = Intent(this@ViewAllQuestionsActivity, PublicChatActivity::class.java)
            TaskDetailsActivity.questionModel = obj
            TaskDetailsActivity.isOfferQuestion = "question"
            val bundle = Bundle()
            bundle.putParcelable(ConstantKey.QUESTION_LIST_MODEL, obj)
            intent.putExtras(bundle)
            startActivityForResult(intent, 21)
        }
    }
}