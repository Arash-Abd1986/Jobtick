package com.jobtick.android.activities.new


import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jobtick.android.R
import com.jobtick.android.activities.ActivityBase
import com.jobtick.android.adapers.OfferListAdapterV2
import com.jobtick.android.fragments.OfferBottomSheet
import com.jobtick.android.models.OfferModel
import com.jobtick.android.utils.SessionManager

const val OFFER_LIST = "offer_list"
const val IS_USER_THE_POSTER = "is_user_the_poster"

class OfferListActivity : ActivityBase(), OfferListAdapterV2.OnItemClickListener {

    private lateinit var ivBack: ImageView
    private lateinit var recyclerViewOffers: RecyclerView
    private lateinit var offerListAdapter: OfferListAdapterV2
    private var isUserThePoster = false
    private var sessionManagerO: SessionManager? = null
    private lateinit var offerListF: ArrayList<OfferModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer_list)
        val bundle = intent.extras
        offerListF = bundle!!.getParcelableArrayList(OFFER_LIST)!!
        isUserThePoster = bundle.getBoolean(IS_USER_THE_POSTER)
        initIDS()
        initComponents()
        initOfferList()
    }

    private fun initOfferList() {
        recyclerViewOffers.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recyclerViewOffers.layoutManager = layoutManager
        offerListAdapter = OfferListAdapterV2(this, isUserThePoster, ArrayList())
        recyclerViewOffers.adapter = offerListAdapter
        offerListAdapter.addItems(offerListF)
        offerListAdapter.setOnItemClickListener(this)
    }

    private fun initIDS() {
        ivBack = findViewById(R.id.iv_back)
        recyclerViewOffers = findViewById(R.id.recycler_view_offers)
    }


    private fun initComponents() {
        ivBack.setOnClickListener { v: View? -> super.onBackPressed() }
        sessionManagerO = SessionManager(applicationContext)
    }


    override fun onItemOfferClick(offer: OfferModel?, action: String?) {
        val questionsBottomSheet = OfferBottomSheet(offer!!, isUserThePoster, sessionManagerO!!, false)
        questionsBottomSheet.show(supportFragmentManager, null)
    }

}