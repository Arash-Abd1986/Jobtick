package com.jobtick.android.activities

import android.R.menu
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.transition.Explode
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.GravityCompat
import androidx.core.view.size
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.fragments.CategoryListBottomSheet
import com.jobtick.android.fragments.LogOutBottomSheet
import com.jobtick.android.interfaces.onProfileUpdateListener
import com.jobtick.android.models.FilterModel
import com.jobtick.android.models.PushNotificationModel
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.models.response.AccountResponse
import com.jobtick.android.models.response.getbalance.CreditCardModel
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.Navigator
import com.jobtick.android.utils.SessionManager
import com.onesignal.OneSignal
import org.json.JSONObject
import timber.log.Timber


class DashboardActivity : ActivityBase(), onProfileUpdateListener, Navigator {
    var drawerLayout: DrawerLayout? = null
    var toolbar: Toolbar? = null
    var appBarConfiguration: AppBarConfiguration? = null
    var sessionManager1: SessionManager? = null
    var myBalance: TextView? = null
    var navT1: TextView? = null
    var navT2: TextView? = null
    var navT4: TextView? = null
    var navT5: TextView? = null
    var navI1: AppCompatImageView? = null
    var navI2: AppCompatImageView? = null
    var navI4: AppCompatImageView? = null
    var navI5: AppCompatImageView? = null
    private var linFilterExplore: LinearLayout? = null
    private var linFilter: LinearLayout? = null
    private var linSignIN: LinearLayout? = null
    var home: LinearLayout? = null
    var search: LinearLayout? = null
    var chat: LinearLayout? = null
    var profile: LinearLayout? = null
    var smallPlus: FrameLayout? = null
    private var creditCardModel: CreditCardModel? = null
    var navController: NavController? = null
    var navHostFragment: NavHostFragment? = null
    private var navigationID = -1
    private lateinit var bottomNav: BottomNavigationView

    @SuppressLint("NonConstantResourceId")
    override fun onCreate(savedInstanceState: Bundle?) {
        with(window) {
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)

            // Set an exit transition
            exitTransition = Explode()
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        initVars()
        initNavigation()
        handleBundle()
        setNavClick()
        accountDetails
        goToFragment()
        onNavClick()
    }

    private fun initVars() {
        toolbar = findViewById(R.id.toolbar)
        // Tools.clearSystemBarLight(this);
        toolbar!!.elevation = 0f
        toolbar!!.setNavigationIcon(R.drawable.ic_setting)
        toolbar!!.visibility = View.GONE

        sessionManager1 = SessionManager(this)
        onProfileupdatelistenerSideMenu = this
        linFilterExplore = findViewById(R.id.lin_filter_explore)
        linFilter = findViewById(R.id.lin_filter)
        linSignIN = findViewById(R.id.lin_signIn)
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
        bottomNav = findViewById(R.id.bottom_nav)
        setHeaderLayout()
        drawerLayout = findViewById(R.id.drawer_layout)

        appBarConfiguration = AppBarConfiguration.Builder(
            R.id.navigation_new_task,
            R.id.navigation_my_tasks,
            R.id.navigation_browse,
            R.id.navigation_inbox,
            R.id.navigation_profile
        )
            .setDrawerLayout(drawerLayout)
            .build()
    }

    private fun initNavigation() {
        Log.d("sdsdsddddfd", "here")

       // bottomNav.menu.clear()
       // bottomNav.inflateMenu(R.menu.ticker_bottom_nav_menu)
       // bottomNav.performClick()
        bottomNav.itemIconTintList = null
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        val graphInflater = navController!!.navInflater
        val navGraph = graphInflater.inflate(R.navigation.mobile_navigation)
        navGraph.setStartDestination(  if (sessionManager!!.roleLocal == "poster") {
          //  R.id.navigation_new_task
            bottomNav.selectedItemId = R.id.navigation_inventory
           // bottomNav.getMenu().findItem(R.id.navigation_inventory).setChecked(true);
            R.id.navigation_my_tasks
        } else {
            bottomNav.selectedItemId = R.id.navigation_new_task
          //  bottomNav.getMenu().findItem(R.id.navigation_new_task).setChecked(true);

            R.id.navigation_browse
        })
        navController!!.graph = navGraph
        NavigationUI.setupWithNavController(toolbar!!, navController!!, appBarConfiguration!!)

        //new design for bottomnav -> TODO remove old bottomnav
        setBottomnav()
    }

    private fun handleBundle() {
        val bundle = intent.extras
        if (bundle != null) {
            if (!bundle.getString(ConstantKey.DEEP_LINK_BUNDLE).isNullOrEmpty()) {
                when (bundle.getString(ConstantKey.DEEP_LINK_BUNDLE)?.lowercase()) {
                    "/invite" -> {
                        val intent = Intent(this, ReferAFriendActivity::class.java)
                        startActivity(intent)
                    }
                    "/post-job" -> {
                        val intent = Intent(this, TaskCreateActivity::class.java)
                        startActivity(intent)
                    }
                    "/setting" -> {
                        val intent = Intent(this, SettingActivity::class.java)
                        startActivity(intent)
                    }
                    "/profile" -> {
                        navController!!.navigate(R.id.navigation_profile)
                    }
                    "/notifications" -> {
                        val intent = Intent(this, NotificationActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            if (bundle.getParcelable<Parcelable?>(ConstantKey.PUSH_NOTIFICATION_MODEL) != null) {
                val pushNotificationModel: PushNotificationModel? =
                    bundle.getParcelable(ConstantKey.PUSH_NOTIFICATION_MODEL)
                if (pushNotificationModel != null)
                    if (pushNotificationModel.getTrigger() != null) {
                        val intent = Intent(this@DashboardActivity, TaskDetailsActivity::class.java)
                        val bundleintent = Bundle()
                        if (pushNotificationModel.getTrigger() == ConstantKey.PUSH_TASK) {
                            bundleintent.putString(
                                ConstantKey.SLUG,
                                pushNotificationModel.getModel_slug()
                            )
                            intent.putExtras(bundleintent)
                            startActivity(intent)
                        }
                        if (pushNotificationModel.getTrigger() == ConstantKey.PUSH_COMMENT) {
                            if (pushNotificationModel.getOffer_id() != 0) {
                                bundleintent.putString(
                                    ConstantKey.SLUG,
                                    pushNotificationModel.getModel_slug()
                                )
                                bundleintent.putInt(
                                    ConstantKey.PUSH_OFFER_ID,
                                    pushNotificationModel.getOffer_id()
                                )
                                intent.putExtras(bundleintent)
                                startActivity(intent)
                            }
                            if (pushNotificationModel.getQuestion_id() != 0) {
                                bundleintent.putString(
                                    ConstantKey.SLUG,
                                    pushNotificationModel.getModel_slug()
                                )
                                bundleintent.putInt(
                                    ConstantKey.PUSH_QUESTION_ID,
                                    pushNotificationModel.getQuestion_id()
                                )
                                intent.putExtras(bundleintent)
                                startActivity(intent)
                            }
                        }
                        if (pushNotificationModel.getTrigger() == ConstantKey.PUSH_CONVERSATION) {
                            val bundle1 = Bundle()
                            bundle1.putInt(
                                ConstantKey.PUSH_CONVERSATION_ID,
                                pushNotificationModel.getConversation_id()
                            )
                            val graph = navController!!.graph
                            graph.setStartDestination(R.id.navigation_inbox)
                            navController!!.setGraph(graph, bundle1)
                        }
                    }
            }
        }
    }

    private fun setNavClick() {
        home!!.setOnClickListener {
            linFilter!!.visibility = View.GONE
            linSignIN!!.visibility = View.GONE
            if (sessionManager!!.roleLocal == "poster") {
                navController!!.navigate(R.id.navigation_my_tasks)
            } else {
                navController!!.navigate(R.id.navigation_browse)
                accountDetails
            }
        }
        search!!.setOnClickListener {
            //TODO visible unathorizeduser
          //  if (sessionManager?.accessToken != null) {
                navController!!.navigate(R.id.navigation_my_tasks)
                linFilter!!.visibility = View.VISIBLE
                linSignIN!!.visibility = View.GONE
//            } else {
//              //  unauthorizedUser()
//            }

        }
        chat!!.setOnClickListener {
          //  if (sessionManager?.accessToken != null) {
                linFilterExplore!!.visibility = View.GONE
                linFilter!!.visibility = View.GONE
                linSignIN!!.visibility = View.GONE
                navController!!.navigate(R.id.navigation_inbox)
//            } else {
//            //    unauthorizedUser()
//            }

        }
        profile!!.setOnClickListener {
            linFilterExplore!!.visibility = View.GONE
            linFilter!!.visibility = View.GONE
            navController!!.navigate(R.id.navigation_profile)
        }
        toolbar!!.setNavigationIcon(R.drawable.ic_setting)
        toolbar!!.setNavigationOnClickListener {
            linFilterExplore!!.visibility = View.GONE
            linFilter!!.visibility = View.GONE
            linSignIN!!.visibility = View.GONE
            if (navigationID == R.id.navigation_my_tasks) {
                onBackPressed()
            } else {
                if (sessionManager?.accessToken != null) {
                    val settings = Intent(this@DashboardActivity, SettingActivity::class.java)
                    startActivity(settings)
                } else {
                 //   unauthorizedUser()
                }
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    private fun onNavClick() {
        smallPlus!!.setOnClickListener { startCategoryList() }
        navController!!.addOnDestinationChangedListener { _: NavController?, destination: NavDestination, _: Bundle? ->
            toolbar!!.visibility = View.GONE
            when (destination.id) {
                R.id.navigation_new_task -> {
                    setMenuItemProperties(0)
                }
                R.id.navigation_browse -> {
                    setMenuItemProperties(0)
                }
                R.id.navigation_my_tasks -> {
                    setMenuItemProperties(1)
                }
                R.id.navigation_inbox -> {
                    setMenuItemProperties(3)
                }
                R.id.navigation_profile -> {
                    setMenuItemProperties(4)
                }
                else ->
                    setMenuItemProperties(4)
            }
            navigationID = destination.id
        }
    }

    fun resetBottomBar() {
        when (navigationID) {
            R.id.navigation_new_task -> {
                setMenuItemProperties(0)
            }
            R.id.navigation_browse -> {
                setMenuItemProperties(0)
            }
            R.id.navigation_my_tasks -> {
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
            val color = getColor(R.color.primary_500)
            val mode = PorterDuff.Mode.DST_ATOP
          //  item!!.setColorFilter(color, mode)

           // val unwrappedDrawable = AppCompatResources.getDrawable(this, item.id)
            val wrappedDrawable = DrawableCompat.wrap(item!!.drawable)
            DrawableCompat.setTint(wrappedDrawable, color)
            when (itemId) {

                0 -> {
                    if (sessionManager!!.roleLocal == "ticker") {
                            home?.visibility = View.VISIBLE
//                        item!!.setImageDrawable(
//                            ContextCompat.getDrawable(
//                                this,
//                                R.drawable.ic_home_medium
//                            )
//                        )
                        navT1!!.text = getString(R.string.explore)
                    } else {
                        home?.visibility = View.GONE
//                        item!!.setImageDrawable(
//                            ContextCompat.getDrawable(
//                                this,
//                                R.drawable.ic_explore_medium
//                            )
//                        )
                        navT1!!.text = getString(R.string.explore)
                    }
                    navT1!!.setTextColor(color)
                }
                1 -> {
                    if (sessionManager!!.roleLocal == "poster") {
                        navT2!!.text = "Inventory"
                    }
                    else
                        navT2!!.text = "Workspace"
//                    item!!.setImageDrawable(
//                        ContextCompat.getDrawable(
//                            this,
//                            R.drawable.new_des
//                        )
//                    )
                    navT2!!.setTextColor(color)
                   // item!!.setColorFilter(color, mode)
                }
                3 -> {
//                    item!!.setImageDrawable(
//                        ContextCompat.getDrawable(
//                            this,
//                            R.drawable.ic_chats_medium
//                        )
//                    )
                    navT4!!.setTextColor(color)
                }
                4 -> {
//                    item!!.setImageDrawable(
//                        ContextCompat.getDrawable(
//                            this,
//                            R.drawable.ic_profile_medium
//                        )
//                    )
                    navT5!!.setTextColor(color)
                }
            }
        } else {
            val color = getColor(R.color.neutral_light_500)
            val wrappedDrawable = DrawableCompat.wrap(item!!.drawable)
            DrawableCompat.setTint(wrappedDrawable, color)
//            val color = getColor(R.color.neutral_light_500)
//            val mode = PorterDuff.Mode.DST_OVER
//            item!!.setColorFilter(color, mode)
            when (itemId) {
                0 -> {
                    if (sessionManager!!.roleLocal == "ticker") {
                        home?.visibility = View.VISIBLE
//                        item!!.setImageDrawable(
//                            ContextCompat.getDrawable(
//                                this,
//                                R.drawable.ic_home_small
//                            )
//                        )
                        navT1!!.text = getString(R.string.explore)
                    } else {
                        home?.visibility = View.GONE
//                        item!!.setImageDrawable(
//                            ContextCompat.getDrawable(
//                                this,
//                                R.drawable.ic_explore_small
//                            )
//                        )
                        navT1!!.text = getString(R.string.explore)
                    }
                    navT1!!.setTextColor(getColor(R.color.neutral_light_500))
                }
                1 -> {
                    if (sessionManager!!.roleLocal == "poster") {
                        navT2!!.text = "Inventory"
                    }
                    else
                        navT2!!.text = "Workspace"
//                    item!!.setImageDrawable(
//                        ContextCompat.getDrawable(
//                            this,
//                            R.drawable.ic_my_jobs_small
//                        )
//                    )
                    navT2!!.setTextColor(getColor(R.color.neutral_light_500))
                }
                3 -> {
//                    item!!.setImageDrawable(
//                        ContextCompat.getDrawable(
//                            this,
//                            R.drawable.ic_chats_small
//                        )
//                    )
                    navT4!!.setTextColor(getColor(R.color.neutral_light_500))
                }
                4 -> {
//                    item!!.setImageDrawable(
//                        ContextCompat.getDrawable(
//                            this,
//                            R.drawable.ic_profile_small
//                        )
//                    )
                    navT5!!.setTextColor(getColor(R.color.neutral_light_500))
                }
            }
        }
    }

    private fun goToFragment() {
        when {
            intent.getBooleanExtra(
                ConstantKey.GO_TO_MY_JOBS,
                false
            ) -> navController!!.navigate(R.id.navigation_my_tasks)
            intent.getBooleanExtra(
                ConstantKey.GO_TO_HOME,
                false
            ) -> navController!!.navigate(R.id.navigation_new_task)
            intent.getBooleanExtra(
                ConstantKey.GO_TO_EXPLORE,
                false
            ) -> navController!!.navigate(R.id.navigation_browse)
            intent.getBooleanExtra(
                ConstantKey.GO_TO_CHAT,
                false
            ) -> navController!!.navigate(R.id.navigation_inbox)
            intent.getBooleanExtra(
                ConstantKey.GO_TO_PROFILE,
                false
            ) -> navController!!.navigate(R.id.navigation_profile)
        }

        // TODO: when this activity is opening again, (for second time) tool bar background becomes white.
        // the workaround is here but need to fix it in true way.
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

    private fun setHeaderLayout() {
        sessionManager?.let {
            it.userAccount?.let {
                OneSignal.setExternalUserId(it.id.toString())
                OneSignal.setEmail(it.email)
                OneSignal.sendTag("Email", it.email)
                OneSignal.sendTag(
                    "Name",
                    it.fname + " " + it.lname
                )
                OneSignal.sendTag(
                    "Mobile",
                    it.fname + it.mobile
                )
                OneSignal.sendTag(
                    "Location",
                    it.fname + it.location
                )
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return (
                NavigationUI.navigateUp(navController, appBarConfiguration!!) ||
                        super.onSupportNavigateUp()
                )
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
                val ratingLink = "https://play.google.com/store/apps/details?id=$packageName"
                val rateUs = Intent(Intent.ACTION_VIEW)
                rateUs.data = Uri.parse(ratingLink)
                startActivity(rateUs)
                true
            }
            R.id.action_share -> {
                //                startActivity(new Intent(this, ReferAFriendActivity.class));
                referAFriend()
                true
            }
            R.id.action_privacy_policy -> {
                val privacyPolicyLink = "https://sites.google.com/view/_/home"
                val privacyPolicy = Intent(Intent.ACTION_VIEW)
                privacyPolicy.data = Uri.parse(privacyPolicyLink)
                startActivity(privacyPolicy)
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
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                """

https://play.google.com/store/apps/details?id=$packageName
 Sponsor code : ${sessionManager1!!.userAccount.fname}

ThankYou
Team ${resources.getString(R.string.app_name)}"""
            )
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

    override fun updatedSuccesfully(path: String) {
        // ImageUtil.displayImage(imgUserAvatar, path, null)
    }

    override fun updateProfile() {}
    override fun navigate(id: Int) {
        navController!!.navigate(id)
    }

    enum class Fragment {
        HOME, MY_JOBS, EXPLORE, CHAT, PROFILE, INVITE
    }

    private val accountDetails: Unit
        get() {
            val stringRequest: StringRequest =
                object : StringRequest(
                    Method.GET, Constant.URL_GET_ACCOUNT,
                    Response.Listener { response: String? ->
                        try {
                            val jsonObject = JSONObject(response!!)
                            val jsonobjectData = jsonObject.getJSONObject("data")
                            sessionManager!!.role = jsonobjectData.getString("role")
                            val userAccountModel = UserAccountModel().getJsonToModel(jsonobjectData)
                            sessionManager1!!.userAccount = userAccountModel
                            if (sessionManager1!!.filter == null) {
                                val gson = Gson()
                                val filterModel = FilterModel()
                                val data = gson.fromJson(response, AccountResponse::class.java)
                                filterModel.distance =
                                    data.data!!.browsejobs_default_filters!!.distance
                                filterModel.latitude = data.data.position!!.latitude.toString()
                                filterModel.logitude = data.data.position.longitude.toString()
                                filterModel.location = data.data.location
                                filterModel.price =
                                    (
                                            data.data.browsejobs_default_filters!!.min_price +
                                                    "$-" + data.data.browsejobs_default_filters.max_price + "$"
                                            )
                                filterModel.section = Constant.FILTER_ALL
                                sessionManager1!!.filter = filterModel
                            } else if (!sessionManager1!!.filter.location.contains(",")) {
                                val gson = Gson()
                                val filterModel = FilterModel()
                                val data = gson.fromJson(response, AccountResponse::class.java)
                                filterModel.distance =
                                    data.data!!.browsejobs_default_filters!!.distance
                                filterModel.latitude = data.data.position!!.latitude.toString()
                                filterModel.logitude = data.data.position.longitude.toString()
                                filterModel.location = data.data.location
                                filterModel.price =
                                    (
                                            data.data.browsejobs_default_filters!!.min_price +
                                                    "$-" + data.data.browsejobs_default_filters.max_price + "$"
                                            )
                                filterModel.section = Constant.FILTER_ALL
                                sessionManager1!!.filter = filterModel
                            }
                            if (sessionManager1!!.userAccount.account_status.isBasic_info) {
                                sessionManager1!!.latitude = userAccountModel.latitude.toString()
                                sessionManager1!!.longitude = userAccountModel.longitude.toString()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    Response.ErrorListener { error: VolleyError? -> }
                ) {
                    override fun getHeaders(): Map<String, String> {
                        val map1: MutableMap<String, String> = HashMap()
                        map1["authorization"] =
                            sessionManager1!!.tokenType + " " + sessionManager1!!.accessToken
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
            val requestQueue = Volley.newRequestQueue(this@DashboardActivity)
            requestQueue.add(stringRequest)
        }

    private fun launchMarket() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(
            "https://play.google.com/store/apps/details?id=com.jobtick.android"
        )
        intent.setPackage("com.android.vending")
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this@DashboardActivity, " unable to find market app", Toast.LENGTH_LONG)
                .show()
        }
    }

    companion object {
        @JvmField
        var onProfileupdatelistenerSideMenu: onProfileUpdateListener? = null
    }

     fun setBottomnav() {

         bottomNav.itemIconTintList = null

         if (sessionManager!!.roleLocal == "poster") {
            bottomNav.menu.findItem(R.id.navigation_new_task).isVisible = false
            bottomNav.menu.findItem(R.id.navigation_workspace).isVisible = false
            bottomNav.menu.findItem(R.id.navigation_inventory).isVisible = true

        }
        else {
            bottomNav.menu.findItem(R.id.navigation_new_task).isVisible = true
             bottomNav.menu.findItem(R.id.navigation_workspace).isVisible = true
             bottomNav.menu.findItem(R.id.navigation_inventory).isVisible = false
        }

        bottomNav.setOnItemSelectedListener { item ->

            when(item.itemId) {
                R.id.navigation_new_task -> {
                    if (sessionManager!!.roleLocal == "poster") {
                        navController!!.navigate(R.id.navigation_new_task)
                    } else {
                        navController!!.navigate(R.id.navigation_browse)
                        accountDetails
                    }

                    true
                }
                R.id.navigation_inventory -> {
                    toolbar!!.visibility = View.GONE
                    navController!!.navigate(R.id.navigation_my_tasks)
                    true
                }
                R.id.navigation_workspace -> {
                    toolbar!!.visibility = View.GONE

                    navController!!.navigate(R.id.navigation_my_tasks)
                    true
                }

                R.id.navigation_browse -> {
                    navController!!.navigate(R.id.navigation_inbox)

                    true
                }
                R.id.navigation_profile -> {
                    navController!!.navigate(R.id.navigation_profile)
                    true
                }

                else -> false
            }
        }


    }
}
