package com.jobtick.android.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.activities.ChatActivity
import com.jobtick.android.adapers.NotificationListAdapter
import com.jobtick.android.adapers.SectionsPagerAdapter
import com.jobtick.android.fragments.Dashboard2PosterFragment
import com.jobtick.android.fragments.Dashboard2TickerFragment
import com.jobtick.android.models.ConversationModel
import com.jobtick.android.models.notification.NotifDatum
import com.jobtick.android.models.notification.PushNotificationModel2
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.widget.ContentWrappingViewPager
import com.onesignal.OneSignal
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class Dashboard2Activity : ActivityBase(), NotificationListAdapter.OnItemClickListener, OnPageChangeListener {
    private lateinit var toolbar: MaterialToolbar
    private lateinit var rbAsATicker: RadioButton
    private lateinit var rbAsAPoster: RadioButton
    private lateinit var rgTickerPoster: RadioGroup
    private lateinit var recyclerView: RecyclerView
    private lateinit var noNotifications: LinearLayout
    private lateinit var viewPager: ContentWrappingViewPager
    private var notificationListAdapter: NotificationListAdapter? = null
    private var pushNotificationModel2: PushNotificationModel2? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard2)
        setIDs()
        initToolbar()
        initComponent()
        initNotificationList()
        setHeaderLayout()
    }

    private fun setIDs() {
        toolbar =findViewById(R.id.toolbar)
        rbAsATicker =findViewById(R.id.rb_as_ticker)
        rbAsAPoster =findViewById(R.id.rb_as_poster)
        rgTickerPoster =findViewById(R.id.rg_ticker_poster)
        recyclerView =findViewById(R.id.recycler_view)
        noNotifications =findViewById(R.id.no_notifications_container)
        viewPager =findViewById(R.id.ticker_poster_view_pager)
    }

    private fun setHeaderLayout() {
        OneSignal.setExternalUserId(sessionManager!!.userAccount.id.toString())
        OneSignal.setEmail(sessionManager!!.userAccount.email)
        OneSignal.sendTag("Email", sessionManager!!.userAccount.email)
        OneSignal.sendTag("Name", sessionManager!!.userAccount.fname + sessionManager!!.userAccount.lname)
        OneSignal.sendTag("Mobile", sessionManager!!.userAccount.fname + sessionManager!!.userAccount.mobile)
    }

    private fun initToolbar() {
        toolbar.title = "Dashboard"
        toolbar.setNavigationIcon(R.drawable.ic_back)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }//http request

    //we just get last 10 notifications
    private val notificationList: Unit
        get() {
            val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.URL_NOTIFICATION_LIST + "?page=1",
            Response.Listener { response: String? ->
                Timber.e(response)
                hideProgressDialog()
                try {
                    val jsonObject = JSONObject(response!!)
                    Timber.e(jsonObject.toString())
                    pushNotificationModel2 = if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                        val jsonString = jsonObject.toString() //http request
                        val gson = Gson()
                        gson.fromJson(jsonString, PushNotificationModel2::class.java)
                    } else {
                        showToast("something went wrong.", this)
                        checkList()
                        return@Listener
                    }
                    notificationListAdapter!!.addItems(pushNotificationModel2!!.data)
                    checkList()
                } catch (e: JSONException) {
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
                    map1["Authorization"] = "Bearer " + sessionManager.accessToken
                    map1["Version"] = BuildConfig.VERSION_CODE.toString()
                    return map1
                }
            }
            stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            val requestQueue = Volley.newRequestQueue(this@Dashboard2Activity)
            requestQueue.add(stringRequest)
            Timber.e(stringRequest.url)
        }

    private fun checkList() {
        if (notificationListAdapter!!.itemCount <= 0) {
            noNotifications.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            noNotifications.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun initNotificationList() {
        val layoutManager = LinearLayoutManager(this@Dashboard2Activity)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        notificationListAdapter = NotificationListAdapter(ArrayList())
        notificationListAdapter!!.setOnItemClickListener(this)
        recyclerView.adapter = notificationListAdapter
        notificationList
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    override fun onPageSelected(position: Int) {
        when (position) {
            0 -> {
                rbAsATicker.isChecked = true
                rbAsAPoster.isChecked = false
            }
            1 -> {
                rbAsATicker.isChecked = false
                rbAsAPoster.isChecked = true
            }
            2 -> {
            }
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}
    private fun initComponent() {
        setupViewPager(viewPager)
        viewPager.addOnPageChangeListener(this)
        viewPager.currentItem = 0
        viewPager.offscreenPageLimit = 2
        clickEvent()
    }

    private fun clickEvent() {
        rgTickerPoster.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
            when (checkedId) {
                R.id.rb_as_ticker -> viewPager.currentItem = 0
                R.id.rb_as_poster -> viewPager.currentItem = 1
            }
        }
    }

    private fun setupViewPager(viewPager: ViewPager?) {
        val adapter = SectionsPagerAdapter(supportFragmentManager)
        adapter.addFragment(Dashboard2TickerFragment.newInstance(), "User as Ticker")
        adapter.addFragment(Dashboard2PosterFragment.newInstance(), "User as Poster")
        viewPager!!.adapter = adapter
    }

    override fun onItemClick(view: View, obj: NotifDatum, position: Int, action: String) {
        if (obj.data != null && obj.data.trigger != null) {
            if (obj.data.trigger == "task" || obj.data.trigger == "comment") {
                val intent = Intent(this@Dashboard2Activity, TaskDetailsActivity::class.java)
                val bundleIntent = Bundle()
                bundleIntent.putString(ConstantKey.SLUG, obj.data.taskSlug)
                //TODO: need to put poster id to this, but is has to be implemented at taskDetailsActivity not from outside
                //bundleIntent.putInt(ConstantKey.USER_ID, obj.getUser().getId());
                intent.putExtras(bundleIntent)
                startActivity(intent)
            } else if (obj.data.trigger == "conversation") {
                val model = ConversationModel(obj.data.conversation.id,
                        obj.userAccountModel.name, obj.data.taskId, null, null, obj.createdAt,
                        sessionManager.userAccount,
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