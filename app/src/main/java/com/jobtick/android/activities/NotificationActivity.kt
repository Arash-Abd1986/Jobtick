package com.jobtick.android.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.adapers.NotificationListAdapter
import com.jobtick.android.models.ConversationModel
import com.jobtick.android.models.notification.NotifDatum
import com.jobtick.android.models.notification.PushNotificationModel2
import com.jobtick.android.pagination.PaginationListener
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.SessionManager
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class NotificationActivity : ActivityBase(), NotificationListAdapter.OnItemClickListener, OnRefreshListener {

    private var toolbar: MaterialToolbar? = null
    private var recyclerView: RecyclerView? = null
    private var swipeRefresh: SwipeRefreshLayout? = null
    private var currentPage = PaginationListener.PAGE_START
    private var isLastPageItem = false
    private var totalPage = 10
    private var isLoadingItem = false
    private var notificationListAdapter: NotificationListAdapter? = null
    private var pushNotificationModel2: PushNotificationModel2? = null
    private var noNotifications: LinearLayout? = null
    private lateinit var sessionManagerN: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        setIDs()
        sessionManagerN = SessionManager(this)
        noNotifications = findViewById(R.id.no_notifications_container)
        initToolbar()
        initComponent()
        notificationList
    }

    private fun setIDs() {

        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.recycler_view)
        swipeRefresh = findViewById(R.id.swipeRefresh)
    }

    private fun initComponent() {
        val layoutManager = LinearLayoutManager(this@NotificationActivity)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.setHasFixedSize(true)
        notificationListAdapter = NotificationListAdapter(ArrayList())
        notificationListAdapter!!.setOnItemClickListener(this)
        swipeRefresh!!.setOnRefreshListener(this)
        recyclerView!!.adapter = notificationListAdapter
        recyclerView!!.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                isLoadingItem = true
                currentPage++
                notificationList
            }


            override val isLastPage: Boolean
                get() = isLastPageItem
            override val isLoading: Boolean
                get() = isLoadingItem
        })
    }

    private fun initToolbar() {
        toolbar!!.title = "Notifications"
        toolbar!!.setNavigationIcon(R.drawable.ic_back)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_notification, menu)
        return true
    }

    @SuppressLint("NonConstantResourceId")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.nav_setting -> startActivity(Intent(this, NotificationSettings::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private val notificationList:
            Unit
        get() {
            if (currentPage == PaginationListener.PAGE_START) swipeRefresh!!.isRefreshing = true
            val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.URL_NOTIFICATION_LIST + "?page=" + currentPage,
                    Response.Listener { response: String? ->
                        Timber.e(response)
                        hideProgressDialog()
                        try {
                            val jsonObject = JSONObject(response!!)
                            Timber.e(jsonObject.toString())
                            if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                val jsonString = jsonObject.toString() //http request
                                val gson = Gson()
                                pushNotificationModel2 = gson.fromJson(jsonString, PushNotificationModel2::class.java)
                                makeNotificationsAsRead()
                            } else {
                                showToast("something went wrong.", this)
                                checkList()
                                return@Listener
                            }
                            if (jsonObject.has("meta") && !jsonObject.isNull("meta")) {
                                val jsonObject_meta = jsonObject.getJSONObject("meta")
                                totalPage = jsonObject_meta.getInt("last_page")
                                Constant.PAGE_SIZE = jsonObject_meta.getInt("per_page")
                            }

                            /*
                                *manage progress view
                                */if (currentPage != PaginationListener.PAGE_START) notificationListAdapter!!.removeLoading()
                            notificationListAdapter!!.addItems(pushNotificationModel2!!.data)
                            checkList()

                            // check weather is last page or not
                            if (currentPage < totalPage) {
                                notificationListAdapter!!.addLoading()
                            } else {
                                isLastPageItem = true
                            }
                            isLoadingItem = false
                        } catch (e: JSONException) {
                            hideProgressDialog()
                            Timber.e(e.toString())
                            e.printStackTrace()
                            checkList()
                        }
                    },
                    Response.ErrorListener { error: VolleyError ->
                        checkList()
                        errorHandle1(error.networkResponse)
                    }) {
                override fun getHeaders(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["Content-Type"] = "application/x-www-form-urlencoded"
                    map1["Authorization"] = "Bearer " + sessionManagerN.accessToken
                    map1["Version"] = BuildConfig.VERSION_CODE.toString()
                    return map1
                }
            }
            stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            val requestQueue = Volley.newRequestQueue(this@NotificationActivity)
            requestQueue.add(stringRequest)
            Timber.e(stringRequest.url)
        }

    private fun makeNotificationsAsRead() {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Constant.URL_NOTIFICATION_MARK_ALL_READ,
                Response.Listener { Timber.i("make all notifications as read success.") },
                Response.ErrorListener { Timber.i("make all notifications as read NOT success.") }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["Authorization"] = "Bearer " + sessionManagerN.accessToken
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
    }

    override fun onRefresh() {
        swipeRefresh!!.isRefreshing = true
        currentPage = PaginationListener.PAGE_START
        isLastPageItem = false
        notificationListAdapter!!.clear()
        notificationList
    }

    private fun checkList() {
        if (notificationListAdapter!!.itemCount <= 0) {
            noNotifications!!.visibility = View.VISIBLE
            recyclerView!!.visibility = View.GONE
        } else {
            noNotifications!!.visibility = View.GONE
            recyclerView!!.visibility = View.VISIBLE
        }
        swipeRefresh!!.isRefreshing = false
    }

    override fun onItemClick(view: View, obj: NotifDatum, position: Int, action: String) {
        if (obj.data != null && obj.data.trigger != null) {
            if (obj.data.trigger == "task" || obj.data.trigger == "comment") {
                val intent = Intent(this@NotificationActivity, TaskDetailsActivity::class.java)
                val bundleIntent = Bundle()
                bundleIntent.putString(ConstantKey.SLUG, obj.data.taskSlug)
                //TODO: need to put poster id to this, but is has to be implemented at taskDetailsActivity not from outside
                //bundleIntent.putInt(ConstantKey.USER_ID, obj.getUser().getId());
                intent.putExtras(bundleIntent)
                startActivity(intent)
            } else if (obj.data.trigger == "conversation") {
                val model = ConversationModel(obj.data.conversation.id,
                        obj.userAccountModel.name, obj.data.taskId, null, null, obj.createdAt,
                        sessionManagerN.userAccount,
                        obj.userAccountModel,
                        obj.data.taskSlug, obj.data.taskStatus, null)
                val intent = Intent(this, ChatActivity::class.java)
                val bundle = Bundle()
                ChatActivity.conversationModel = model
                //    bundle.putParcelable(ConstantKey.CONVERSATION, model);
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
    }
}