package com.jobtick.android.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.activities.others.ReferAFriendActivity
import com.jobtick.android.fragments.CashOutBottomSheet
import com.jobtick.android.fragments.CategoryListBottomSheet
import com.jobtick.android.fragments.LogOutBottomSheet
import com.jobtick.android.interfaces.onProfileUpdateListener
import com.jobtick.android.models.FilterModel
import com.jobtick.android.models.PushNotificationModel
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.models.response.AccountResponse
import com.jobtick.android.models.response.getbalance.CreditCardModel
import com.jobtick.android.utils.*
import com.onesignal.OneSignal
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*
import kotlin.math.abs

class DashboardActivity : ActivityBase(), NavigationView.OnNavigationItemSelectedListener, onProfileUpdateListener, Navigator {
    var drawerLayout: DrawerLayout? = null
    var toolbar: Toolbar? = null
    var appBarConfiguration: AppBarConfiguration? = null
    var sessionManager1: SessionManager? = null
    var imgUserAvatar: ImageView? = null
    var imgVerifiedAccount: ImageView? = null
    var txtUserName: TextView? = null
    var txtAccountLevel: TextView? = null
    var btnCashOut: CardView? = null
    var myBalance: TextView? = null
    var navT1: TextView? = null
    var navT2: TextView? = null
    var navT4: TextView? = null
    var navT5: TextView? = null
    var navI1: AppCompatImageView? = null
    var navI2: AppCompatImageView? = null
    var navI4: AppCompatImageView? = null
    var navI5: AppCompatImageView? = null
    var llWalletBalance: LinearLayout? = null
    private var linFilterExplore: LinearLayout? = null
    var home: LinearLayout? = null
    var search: LinearLayout? = null
    var chat: LinearLayout? = null
    var profile: LinearLayout? = null
    var smallPlus: FrameLayout? = null
    private var creditCardModel: CreditCardModel? = null
    var navController: NavController? = null

    @SuppressLint("NonConstantResourceId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        toolbar = findViewById(R.id.toolbar)
        // Tools.clearSystemBarLight(this);
        toolbar!!.elevation = 0f
        sessionManager1 = SessionManager(this)
        onProfileupdatelistenerSideMenu = this
        val navigationView = findViewById<View>(R.id.navigation_view) as NavigationView
        val headerView = navigationView.getHeaderView(0)
        imgUserAvatar = headerView.findViewById(R.id.img_user_avatar)
        imgVerifiedAccount = headerView.findViewById(R.id.img_verified_account)
        txtUserName = headerView.findViewById(R.id.txt_user_name)
        txtAccountLevel = headerView.findViewById(R.id.txt_account_level)
        btnCashOut = headerView.findViewById(R.id.btn_cashout)
        myBalance = headerView.findViewById(R.id.my_balance)
        llWalletBalance = headerView.findViewById(R.id.llWalletBalance)
        linFilterExplore = findViewById(R.id.lin_filter_explore)
        home = findViewById(R.id.home)
        search = findViewById(R.id.search)
        chat = findViewById(R.id.chat)
        profile = findViewById(R.id.profile)
        navT1 = findViewById(R.id.nav_t1)
        navT2 = findViewById(R.id.nav_t2)
        navT4 = findViewById(R.id.nav_t4)
        navT5 = findViewById(R.id.nav_t5)
        navI1 = findViewById(R.id.nav_i1)
        navI2 = findViewById(R.id.nav_i2)
        navI4 = findViewById(R.id.nav_i4)
        navI5 = findViewById(R.id.nav_i5)
        smallPlus = findViewById(R.id.small_plus)
        setHeaderLayout()
        navigationView.setNavigationItemSelectedListener(this)
        drawerLayout = findViewById(R.id.drawer_layout)

        appBarConfiguration = AppBarConfiguration.Builder(
                R.id.navigation_new_task, R.id.navigation_my_tasks, R.id.navigation_browse, R.id.navigation_inbox, R.id.navigation_profile)
                .setDrawerLayout(drawerLayout)
                .build()
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(toolbar!!, navController!!, appBarConfiguration!!)
        val toggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            override fun onDrawerOpened(drawerView: View) {
                updateCounter(navigationView)
                super.onDrawerOpened(drawerView)
            }
        }
        drawerLayout!!.setDrawerListener(toggle)
        toggle.syncState()
        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.getParcelable<Parcelable?>(ConstantKey.PUSH_NOTIFICATION_MODEL) != null) {
                val pushNotificationModel: PushNotificationModel? = bundle.getParcelable(ConstantKey.PUSH_NOTIFICATION_MODEL)
                if (pushNotificationModel != null)
                    if (pushNotificationModel.getTrigger() != null) {
                        val intent = Intent(this@DashboardActivity, TaskDetailsActivity::class.java)
                        val bundleintent = Bundle()
                        if (pushNotificationModel.getTrigger() == ConstantKey.PUSH_TASK) {
                            bundleintent.putString(ConstantKey.SLUG, pushNotificationModel.getModel_slug())
                            intent.putExtras(bundleintent)
                            startActivity(intent)
                        }
                        if (pushNotificationModel.getTrigger() == ConstantKey.PUSH_COMMENT) {
                            if (pushNotificationModel.getOffer_id() != 0) {
                                bundleintent.putString(ConstantKey.SLUG, pushNotificationModel.getModel_slug())
                                bundleintent.putInt(ConstantKey.PUSH_OFFER_ID, pushNotificationModel.getOffer_id())
                                intent.putExtras(bundleintent)
                                startActivity(intent)
                            }
                            if (pushNotificationModel.getQuestion_id() != 0) {
                                bundleintent.putString(ConstantKey.SLUG, pushNotificationModel.getModel_slug())
                                bundleintent.putInt(ConstantKey.PUSH_QUESTION_ID, pushNotificationModel.getQuestion_id())
                                intent.putExtras(bundleintent)
                                startActivity(intent)
                            }
                        }
                        if (pushNotificationModel.getTrigger() == ConstantKey.PUSH_CONVERSATION) {
                            val bundle1 = Bundle()
                            bundle1.putInt(ConstantKey.PUSH_CONVERSATION_ID, pushNotificationModel.getConversation_id())
                            val graph = navController!!.graph
                            graph.startDestination = R.id.navigation_inbox
                            navController!!.setGraph(graph, bundle1)
                        }
                    }
            }
        }
        setNavClick()
        accountDetails
        goToFragment()
        balance
        onNavClick()
    }

    private fun setNavClick() {
        home!!.setOnClickListener { v: View? ->
            linFilterExplore!!.visibility = View.GONE
            navController!!.navigate(R.id.navigation_new_task)
        }
        search!!.setOnClickListener { v: View? ->
            Handler().postDelayed({
                linFilterExplore!!.visibility = View.VISIBLE
            }, 50)
            navController!!.navigate(R.id.navigation_browse)
        }
        chat!!.setOnClickListener { v: View? ->
            linFilterExplore!!.visibility = View.GONE
            navController!!.navigate(R.id.navigation_inbox)
        }
        profile!!.setOnClickListener { v: View? ->
            linFilterExplore!!.visibility = View.GONE
            navController!!.navigate(R.id.navigation_profile)
        }
    }

    @SuppressLint("NonConstantResourceId")
    private fun onNavClick() {
        smallPlus!!.setOnClickListener { v: View? -> startCategoryList() }
        navController!!.addOnDestinationChangedListener { controller: NavController?, destination: NavDestination, arguments: Bundle? ->
            when (destination.id) {
                R.id.navigation_new_task -> {
                    setMenuItemProperties(0)
                }
                R.id.navigation_browse -> {
                    setMenuItemProperties(1)
                }
                R.id.navigation_inbox -> {
                    setMenuItemProperties(3)
                }
                R.id.navigation_profile -> {
                    setMenuItemProperties(4)
                }
            }
        }
    }

    fun setMenuItemProperties(index: Int) {
        for (i in 0..4) {
            if (i == index) {
                chooseItem(i, "LARGE")
            } else {
                chooseItem(i, "SMALL")
            }
        }
    }

    private fun chooseItem(i: Int, status: String) {
        when (i) {
            0 -> {
                setMenuItemIcon(navI1, i, status)
            }
            1 -> {
                setMenuItemIcon(navI2, i, status)
            }
            3 -> {
                setMenuItemIcon(navI4, i, status)
            }
            4 -> {
                setMenuItemIcon(navI5, i, status)
            }
        }
    }

    private fun startCategoryList() {
        val infoBottomSheet = CategoryListBottomSheet(sessionManager1)
        infoBottomSheet.show(supportFragmentManager, null)
    }

    fun setMenuItemIcon(item: AppCompatImageView?, itemId: Int, status: String) {
        if (status == "LARGE") {
            when (itemId) {
                0 -> {
                    item!!.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_home_medium))
                    navT1!!.visibility = View.VISIBLE
                }
                1 -> {
                    item!!.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_explore_medium))
                    navT2!!.visibility = View.VISIBLE
                }
                3 -> {
                    item!!.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_chats_medium))
                    navT4!!.visibility = View.VISIBLE
                }
                4 -> {
                    item!!.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_profile_medium))
                    navT5!!.visibility = View.VISIBLE
                }
            }
        } else {
            when (itemId) {
                0 -> {
                    item!!.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_home_small))
                    navT1!!.visibility = View.INVISIBLE
                }
                1 -> {
                    item!!.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_explore_small))
                    navT2!!.visibility = View.INVISIBLE
                }
                3 -> {
                    item!!.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_chats_small))
                    navT4!!.visibility = View.INVISIBLE
                }
                4 -> {
                    item!!.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_profile_small))
                    navT5!!.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun goToFragment() {
        if (intent.getBooleanExtra(ConstantKey.GO_TO_MY_JOBS, false)) navController!!.navigate(R.id.navigation_my_tasks) else if (intent.getBooleanExtra(ConstantKey.GO_TO_HOME, false)) navController!!.navigate(R.id.navigation_new_task) else if (intent.getBooleanExtra(ConstantKey.GO_TO_EXPLORE, false)) navController!!.navigate(R.id.navigation_browse) else if (intent.getBooleanExtra(ConstantKey.GO_TO_CHAT, false)) navController!!.navigate(R.id.navigation_inbox) else if (intent.getBooleanExtra(ConstantKey.GO_TO_PROFILE, false)) navController!!.navigate(R.id.navigation_profile)

        //TODO: when this activity is opening again, (for second time) tool bar background becomes white.
        //the workaround is here but need to fix it in true way.
        toolbar!!.setBackgroundResource(R.color.backgroundLightGrey)
    }

    fun goToFragment(fragment: Fragment) {
        when (fragment) {
            Fragment.MY_JOBS -> navController!!.navigate(R.id.navigation_my_tasks)
            Fragment.HOME -> navController!!.navigate(R.id.navigation_new_task)
            Fragment.EXPLORE -> navController!!.navigate(R.id.navigation_browse)
            Fragment.CHAT -> navController!!.navigate(R.id.navigation_inbox)
            Fragment.PROFILE -> navController!!.navigate(R.id.navigation_profile)
            Fragment.INVITE -> startActivity(Intent(this, ReferAFriendActivity::class.java))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setHeaderLayout() {
        if (sessionManager1!!.userAccount.avatar != null && sessionManager1!!.userAccount.avatar.thumbUrl != null) {
            ImageUtil.displayImage(imgUserAvatar, sessionManager1!!.userAccount.avatar.thumbUrl, null)
        } else {
            //default image
        }
        OneSignal.setExternalUserId(sessionManager1!!.userAccount.id.toString(), { results: JSONObject? -> })
        if (sessionManager1!!.userAccount.isVerifiedAccount == 1) {
            imgVerifiedAccount!!.visibility = View.VISIBLE
        } else {
            imgVerifiedAccount!!.visibility = View.GONE
        }
        if (sessionManager1!!.userAccount.posterTier != null) {
            when (sessionManager1!!.userAccount.posterTier.id) {
                1 -> {
                    txtAccountLevel!!.setText(R.string.level_1)
                }
                2 -> {
                    txtAccountLevel!!.setText(R.string.level_2)
                }
                3 -> {
                    txtAccountLevel!!.setText(R.string.level_3)
                }
            }
        } else {
            txtAccountLevel!!.setText(R.string.level_0)
        }
        if (sessionManager1!!.userAccount.name != null) {
            txtUserName!!.text = sessionManager1!!.userAccount.name
        } else {
            txtUserName!!.text = "Profile not updated"
        }
    }

    private fun updateCounter(navigationView: NavigationView) {
        val m = navigationView.menu
        val menuItem = m.findItem(R.id.nav_dashboard)
        val linearLayout = menuItem.actionView as LinearLayout
        val TextView = linearLayout.findViewById<View>(R.id.txt_bedge) as TextView
        TextView.visibility = View.VISIBLE
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return (NavigationUI.navigateUp(navController, appBarConfiguration!!)
                || super.onSupportNavigateUp())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit_profile -> {
                startActivity(Intent(this@DashboardActivity, EditProfileActivity::class.java))
                true
            }
            R.id.action_logout -> {
                showLogoutBottomSheet()
                true
            }
            R.id.action_rate_us -> {
                val rating_link = "https://play.google.com/store/apps/details?id=$packageName"
                val rate_us = Intent(Intent.ACTION_VIEW)
                rate_us.data = Uri.parse(rating_link)
                startActivity(rate_us)
                true
            }
            R.id.action_share -> {
                //                startActivity(new Intent(this, ReferAFriendActivity.class));
                referAFriend()
                true
            }
            R.id.action_privacy_policy -> {
                val privacy_policy_link = "https://sites.google.com/view/_/home"
                val privacy_policy = Intent(Intent.ACTION_VIEW)
                privacy_policy.data = Uri.parse(privacy_policy_link)
                startActivity(privacy_policy)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutBottomSheet() {
        drawerLayout!!.close()
        val logOutBottomSheet = LogOutBottomSheet.newInstance()
        logOutBottomSheet.show(supportFragmentManager, "")
    }

    private fun referAFriend() {
        try {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    """

https://play.google.com/store/apps/details?id=$packageName
 Sponsor code : ${sessionManager1!!.userAccount.fname}

ThankYou
Team ${resources.getString(R.string.app_name)}""")
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        } catch (e: Exception) {
            Timber.d(e.message)
        }
    }


    override fun onBackPressed() {
        linFilterExplore!!.visibility = View.GONE
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        when {
            drawer.isDrawerOpen(GravityCompat.START) -> {
                drawer.closeDrawer(GravityCompat.START)
            }
            drawer.isDrawerOpen(GravityCompat.END) -> {
                drawer.closeDrawer(GravityCompat.END)
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        drawerLayout!!.closeDrawer(GravityCompat.START)
        when (menuItem.itemId) {
            R.id.nav_dashboard -> {
                val dashboard = Intent(this@DashboardActivity, Dashboard2Activity::class.java)
                startActivity(dashboard)
                return true
            }
            R.id.nav_payment -> {
                startActivity(Intent(this@DashboardActivity, PaymentHistoryActivity::class.java))
                return true
            }
            R.id.nav_saved_tasks -> {
                val savedTask = Intent(this@DashboardActivity, SavedTaskActivity::class.java)
                startActivity(savedTask)
                return true
            }
            R.id.nav_notifications -> {
                val intent = Intent(this@DashboardActivity, NotificationActivity::class.java)
                startActivity(intent)
                return true
            }
              R.id.nav_task_alerts -> {
                  val taskAlerts = Intent(this@DashboardActivity, TaskAlertsActivity::class.java)
                  startActivity(taskAlerts)
                  return true
              }
            R.id.nav_refer_a_friend -> {
                startActivity(Intent(this, ReferAFriendActivity::class.java))
                return true
            }
            R.id.nav_settings -> {
                val settings = Intent(this@DashboardActivity, SettingActivity::class.java)
                startActivity(settings)
                return true
            }
            R.id.nav_help_topics -> {
                val helpTopics = Intent(this@DashboardActivity, HelpActivity::class.java)
                startActivity(helpTopics)
                return true
            }
            R.id.nav_logout -> {
                showLogoutBottomSheet()
                return true
            }
        }
        return false
    }// map1.put("X-Requested-With", "XMLHttpRequest");// Print Error!

    //http request
    private val balance: Unit
        private get() {
            val stringRequest: StringRequest = object : StringRequest(Method.GET,
                    Constant.URL_PAYMENTS_METHOD,
                    Response.Listener { response: String? ->
                        Timber.e(response)
                        try {
                            val jsonObject = JSONObject(response)
                            Timber.e(jsonObject.toString())
                            if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                                if (jsonObject.getBoolean("success")) {
                                    if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                        val jsonString = jsonObject.toString() //http request
                                        val gson = Gson()
                                        creditCardModel = gson.fromJson(jsonString, CreditCardModel::class.java)
                                        if (creditCardModel != null && creditCardModel!!.data != null && creditCardModel!!.data!![1].wallet != null && creditCardModel!!.data!![1].wallet!!.balance != null) {
                                            when {
                                                creditCardModel!!.data!![1].wallet!!.balance!!.toDouble() > 0 -> {
                                                    myBalance!!.text = creditCardModel!!.data!![1].wallet!!.balance
                                                    btnCashOut!!.visibility = View.VISIBLE
                                                    btnCashOut!!.setOnClickListener { v: View? ->
                                                        drawerLayout!!.close()
                                                        val cashOutBottomSheet = CashOutBottomSheet.newInstance(creditCardModel)
                                                        cashOutBottomSheet.show(supportFragmentManager, "")
                                                    }
                                                    llWalletBalance!!.setBackgroundColor(resources.getColor(R.color.colorPrimary, null))
                                                }
                                                creditCardModel!!.data!![1].wallet!!.balance!!.toDouble() < 0 -> {
                                                    myBalance!!.text = abs(creditCardModel!!.data!![1].wallet!!.balance!!.toDouble()).toString()
                                                    btnCashOut!!.visibility = View.VISIBLE
                                                    llWalletBalance!!.setBackgroundColor(resources.getColor(R.color.colorRedBalance, null))
                                                }
                                                else -> {
                                                    btnCashOut!!.visibility = View.GONE
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    showToast("Something went Wrong", this)
                                }
                            }
                        } catch (e: Exception) {
                            showToast("Something went Wrong", this)
                            Timber.e(e.toString())
                            e.printStackTrace()
                        }
                    },
                    Response.ErrorListener { error: VolleyError ->
                        val networkResponse = error.networkResponse
                        if (networkResponse?.data != null) {
                            val jsonError = String(networkResponse.data)
                            // Print Error!
                            Timber.e(jsonError)
                            try {
                                val jsonObject = JSONObject(jsonError)
                                val jsonObject_error = jsonObject.getJSONObject("error")
                                if (jsonObject_error.has("error_code") && !jsonObject_error.isNull("error_code")) {
                                    if (ConstantKey.NO_PAYMENT_METHOD == jsonObject_error.getString("error_code")) {
                                        hideProgressDialog()
                                    }
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                        Timber.e(error.toString())
                        errorHandle1(error.networkResponse)
                    }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["authorization"] = sessionManager1!!.tokenType + " " + sessionManager1!!.accessToken
                    map1["Content-Type"] = "application/x-www-form-urlencoded"
                    map1["Version"] = BuildConfig.VERSION_CODE.toString()
                    // map1.put("X-Requested-With", "XMLHttpRequest");
                    return map1
                }
            }
            stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(stringRequest)
        }

    override fun updatedSuccesfully(path: String) {
        ImageUtil.displayImage(imgUserAvatar, path, null)
    }

    override fun updateProfile() {}
    override fun navigate(id: Int) {
        navController!!.navigate(id)
    }

    enum class Fragment {
        HOME, MY_JOBS, EXPLORE, CHAT, PROFILE, INVITE
    }

    //TODO
    private fun showRateAppDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.item_rate_app)
        dialog.setTitle("Title...")
        val dialogButton = dialog.findViewById<View>(R.id.ivClose) as ImageView
        val submit = dialog.findViewById<View>(R.id.submit) as Button
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener { v: View? -> dialog.dismiss() }
        submit.setOnClickListener { v: View? ->
            dialog.dismiss()
            launchMarket()
        }
        dialog.show()
    }

    private val accountDetails: Unit
        get() {
            val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.URL_GET_ACCOUNT,
                    Response.Listener { response: String? ->
                        try {
                            val jsonObject = JSONObject(response)
                            val jsonObject_data = jsonObject.getJSONObject("data")
                            val userAccountModel = UserAccountModel().getJsonToModel(jsonObject_data)
                            sessionManager1!!.userAccount = userAccountModel
                            if (sessionManager1!!.filter == null) {
                                val gson = Gson()
                                val filterModel = FilterModel()
                                val data = gson.fromJson(response, AccountResponse::class.java)
                                filterModel.distance = data.data!!.browsejobs_default_filters!!.distance
                                filterModel.latitude = data.data.position!!.latitude.toString()
                                filterModel.logitude = data.data.position.longitude.toString()
                                filterModel.location = data.data.location
                                filterModel.price = (data.data.browsejobs_default_filters!!.min_price
                                        + "$-" + data.data.browsejobs_default_filters.max_price + "$")
                                filterModel.section = Constant.FILTER_ALL
                                sessionManager1!!.filter = filterModel
                            } else if (!sessionManager1!!.filter.location.contains(",")) {
                                val gson = Gson()
                                val filterModel = FilterModel()
                                val data = gson.fromJson(response, AccountResponse::class.java)
                                filterModel.distance = data.data!!.browsejobs_default_filters!!.distance
                                filterModel.latitude = data.data.position!!.latitude.toString()
                                filterModel.logitude = data.data.position.longitude.toString()
                                filterModel.location = data.data.location
                                filterModel.price = (data.data.browsejobs_default_filters!!.min_price
                                        + "$-" + data.data.browsejobs_default_filters.max_price + "$")
                                filterModel.section = Constant.FILTER_ALL
                                sessionManager1!!.filter = filterModel
                            }
                            if (sessionManager1!!.userAccount.account_status.isBasic_info) {
                                sessionManager1!!.latitude = userAccountModel.latitude.toString()
                                sessionManager1!!.longitude = userAccountModel.longitude.toString()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },
                    Response.ErrorListener { error: VolleyError? -> }) {
                override fun getHeaders(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["authorization"] = sessionManager1!!.tokenType + " " + sessionManager1!!.accessToken
                    map1["Content-Type"] = "application/x-www-form-urlencoded"
                    map1["X-Requested-With"] = "XMLHttpRequest"
                    map1["Version"] = BuildConfig.VERSION_CODE.toString()
                    return map1
                }
            }
            stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            val requestQueue = Volley.newRequestQueue(this@DashboardActivity)
            requestQueue.add(stringRequest)
        }

    private fun launchMarket() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(
                "https://play.google.com/store/apps/details?id=com.jobtick.android")
        intent.setPackage("com.android.vending")
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this@DashboardActivity, " unable to find market app", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        @JvmField
        var onProfileupdatelistenerSideMenu: onProfileUpdateListener? = null
    }
}