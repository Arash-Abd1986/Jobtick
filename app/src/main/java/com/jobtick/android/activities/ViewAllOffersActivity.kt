package com.jobtick.android.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
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
import com.jobtick.android.adapers.OfferListAdapter
import com.jobtick.android.models.OfferModel
import com.jobtick.android.pagination.PaginationListener
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.Helper
import com.jobtick.android.utils.SessionManager
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class ViewAllOffersActivity : ActivityBase(), OnRefreshListener, OfferListAdapter.OnItemClickListener {
    private lateinit var toolbar: MaterialToolbar
    private lateinit var recyclerViewAllOfers: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private var currentPage = PaginationListener.PAGE_START
    private var isLastPageItem = false
    private var totalPage = 10
    private var isLoadingItem = false
    private var offerListAdapter: OfferListAdapter? = null
    private var strSlug: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_all_offers)
        setIDs()
        val bundle = intent.extras
        if (bundle?.getString(ConstantKey.SLUG) != null) {
            strSlug = bundle.getString(ConstantKey.SLUG)
        }
        toolbar.setNavigationOnClickListener { onBackPressed() }
        initCOmponent()
    }

    private fun setIDs() {
        toolbar = findViewById(R.id.toolbar)
        recyclerViewAllOfers = findViewById(R.id.recycler_view_all_ofers)
        swipeRefresh = findViewById(R.id.swipeRefresh)
    }

    private fun initCOmponent() {
        recyclerViewAllOfers.layoutManager = LinearLayoutManager(this@ViewAllOffersActivity, LinearLayoutManager.VERTICAL, false)
        recyclerViewAllOfers.setHasFixedSize(true)
        sessionManager = SessionManager(this@ViewAllOffersActivity)
        offerListAdapter = OfferListAdapter(this@ViewAllOffersActivity, false, ArrayList())
        recyclerViewAllOfers.adapter = offerListAdapter
        swipeRefresh.setOnRefreshListener(this)

        val layoutManager = LinearLayoutManager(this@ViewAllOffersActivity)
        offerListAdapter!!.setOnItemClickListener(this)
        swipeRefresh.isRefreshing = true
        doApiCall()
        recyclerViewAllOfers.addOnScrollListener(object : PaginationListener(layoutManager) {
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
        val items = ArrayList<OfferModel>()
        Helper.closeKeyboard(this@ViewAllOffersActivity)
        val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.URL_TASKS + "/" + strSlug + "/offers?page=" + currentPage,
                Response.Listener { response ->
                    Timber.e(response)
                    // categoryArrayList.clear();
                    try {
                        val jsonObject = JSONObject(response)
                        Timber.e(jsonObject.toString())
                        if (!jsonObject.has("data")) {
                            showToast("some went to wrong", this@ViewAllOffersActivity)
                            return@Listener
                        }
                        val jsonArrayData = jsonObject.getJSONArray("data")
                        var i = 0
                        while (jsonArrayData.length() > i) {
                            val jsonObjectOffers = jsonArrayData.getJSONObject(i)
                            val offerModel = OfferModel().getJsonToModel(jsonObjectOffers)
                            items.add(offerModel)
                            i++
                        }
                        if (jsonObject.has("meta") && !jsonObject.isNull("meta")) {
                            val jsonObjectMeta = jsonObject.getJSONObject("meta")
                            totalPage = jsonObjectMeta.getInt("last_page")
                            Constant.PAGE_SIZE = jsonObjectMeta.getInt("per_page")
                        }

                        /*
                                       *manage progress view
                                       */if (currentPage != PaginationListener.PAGE_START) offerListAdapter!!.removeLoading()
                        offerListAdapter!!.addItems(items)
                        swipeRefresh.isRefreshing = false
                        // check weather is last page or not
                        if (currentPage < totalPage) {
                            offerListAdapter!!.addLoading()
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
        val requestQueue = Volley.newRequestQueue(this@ViewAllOffersActivity)
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
    }

    override fun onRefresh() {
        currentPage = PaginationListener.PAGE_START
        isLastPageItem = false
        offerListAdapter!!.clear()
        doApiCall()
    }

    override fun onItemOfferClick(view: View, obj: OfferModel, position: Int, action: String) {
        if (action.equals("reply", ignoreCase = true)) {
            val intent = Intent(this@ViewAllOffersActivity, PublicChatActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(ConstantKey.OFFER_LIST_MODEL, obj)
            intent.putExtras(bundle)
            startActivityForResult(intent, 20)
        }
    }
}