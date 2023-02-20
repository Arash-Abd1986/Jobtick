package com.jobtick.android.activities


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.jobtick.android.R
import com.jobtick.android.adapers.OfferListAdapterV2
import com.jobtick.android.fragments.OfferBottomSheet
import com.jobtick.android.models.OfferModel
import com.jobtick.android.models.TaskModel
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.SessionManager

const val OFFER_LIST = "offer_list"
const val IS_USER_THE_POSTER = "is_user_the_poster"

class OfferListActivity : ActivityBase(){

    private lateinit var ivBack: ImageView
    private lateinit var recyclerViewOffers: RecyclerView
    private lateinit var linearLayout2: RelativeLayout
    private lateinit var offerListAdapter: OfferListAdapterV2
    var isUserThePoster = false
    private var sessionManagerO: SessionManager? = null
    var taskModel: TaskModel? = null
    var navController: NavController? = null
    var navHostFragment: NavHostFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer_list_v2)
        val bundle = intent.extras
        val gson = Gson()
        taskModel =  gson.fromJson(bundle!!.getString(ConstantKey.TASK), TaskModel::class.java)
        isUserThePoster = bundle.getBoolean(IS_USER_THE_POSTER)
        initIDS()
        initComponents()
        initNavigation()
      //  initIDS()
       // initComponents()
       // initOfferList()
    }

//    private fun initOfferList() {
//        recyclerViewOffers.setHasFixedSize(true)
//        val layoutManager = LinearLayoutManager(this)
//        recyclerViewOffers.layoutManager = layoutManager
//        offerListAdapter = OfferListAdapterV2(this, isUserThePoster, ArrayList())
//        recyclerViewOffers.adapter = offerListAdapter
//        offerListAdapter.addItems(taskModel!!.offers)
//        offerListAdapter.setOnItemClickListener(this)
//    }

    private fun initIDS() {
        ivBack = findViewById(R.id.back)
        linearLayout2 = findViewById(R.id.header)
//        recyclerViewOffers = findViewById(R.id.recycler_view_offers)
    }

    private fun initComponents() {
        ivBack.setOnClickListener { finish() }
//        sessionManagerO = SessionManager(applicationContext)
    }

//    override fun onItemOfferClick(offer: OfferModel?, action: String?) {
//        if (action == "profile") {
//            val intent = Intent(this, ProfileActivity::class.java)
//            intent.putExtra("id", offer!!.worker.id.toInt())
//            startActivity(intent)
//        } else {
//            val questionsBottomSheet =
//                    OfferBottomSheet(offer!!, taskModel = taskModel!!, isUserThePoster, sessionManagerO!!, isAssigned = false, isMyOffer = false)
//            questionsBottomSheet.show(supportFragmentManager, null)
//        }
//    }

    private fun initNavigation() {
        Log.d("sdsdsddddfd", "here")
        navController = Navigation.findNavController(this, R.id.nav_host)
        val graphInflater = navController!!.navInflater
        val navGraph = graphInflater.inflate(R.navigation.offer_list_graph)
        navGraph.setStartDestination(R.id.offerListFragment)
        navController!!.graph = navGraph

        navController!!.addOnDestinationChangedListener { _: NavController?, destination: NavDestination, _: Bundle? ->

            when (destination.id) {
                R.id.offerListFragment -> linearLayout2.visibility = View.VISIBLE
                R.id.navigation_public_profile -> linearLayout2.visibility = View.GONE
            }
        }
    }


}