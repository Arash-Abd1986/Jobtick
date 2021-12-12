package com.jobtick.android.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.lujun.androidtagview.TagContainerLayout
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.activities.*
import com.jobtick.android.adapers.AttachmentAdapter
import com.jobtick.android.adapers.BadgesAdapter
import com.jobtick.android.interfaces.onProfileUpdateListener
import com.jobtick.android.models.AttachmentModel
import com.jobtick.android.models.BadgesModel
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.network.model.response.Levels
import com.jobtick.android.network.model.response.LevelsItem
import com.jobtick.android.utils.*
import com.jobtick.android.widget.SpacingItemDecoration
import com.mikhaellopez.circularimageview.CircularImageView
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileViewFragment : Fragment(), onProfileUpdateListener,
    AttachmentAdapter.OnItemClickListener {
    var userId = -1

    var recyclerViewPortfolio: RecyclerView? = null
    var imgAvatar: CircularImageView? = null
    var txtAbout: TextView? = null
    var ivCall: ImageView? = null
    var ivCall2: ImageView? = null
    var ivCard: ImageView? = null
    var ivCard2: ImageView? = null
    var tvAboutHeading: TextView? = null
    var imgVerified: ImageView? = null
    var tagEducation: TagContainerLayout? = null
    var lytEducation: LinearLayout? = null
    var tagSpecialities: TagContainerLayout? = null
    var lytSpecialities: LinearLayout? = null
    var tagLanguage: TagContainerLayout? = null
    var lytLanguage: LinearLayout? = null
    var tagExperience: TagContainerLayout? = null
    var lytExperience: LinearLayout? = null
    var tagTransportation: TagContainerLayout? = null
    var lytTransportation: LinearLayout? = null
    var txtFullName: TextView? = null
    var txtSuburb: TextView? = null
    var txtLastSeen: TextView? = null
    var tvViewAllReviews: TextView? = null
    var tvSkills: TextView? = null
    var txtAccountLevel: TextView? = null
    var ratingbarAsTicker: RatingBar? = null
    var ratingbarAsPoster: RatingBar? = null
    var tvTickerReview: TextView? = null
    var content: LinearLayout? = null
    var pbLoading: ProgressBar? = null
    var tvPosterReview: TextView? = null
    var tvTickerCompletionRate: TextView? = null
    var tvPosterCompletionRate: TextView? = null
    var rbPortfollio: RadioButton? = null
    var rbSkills: RadioButton? = null
    var btnQuote: CardView? = null
    var llEnlarge: LinearLayout? = null
    var lytBtnGetAQuote: LinearLayout? = null
    var ivMedalBoronz: ImageView? = null
    var ivMedalTop: ImageView? = null
    var ivMedalGOld: ImageView? = null
    var ivMedalMax: ImageView? = null
    private var profileActivity: ProfileActivity? = null
    private var sessionManager: SessionManager? = null
    private var userAccountModel: UserAccountModel? = null
    private var attachmentArrayList: ArrayList<AttachmentModel>? = null
    private var badgesModelArrayList: ArrayList<BadgesModel>? = null
    private var adapter: AttachmentAdapter? = null
    private var badgesAdapter: BadgesAdapter? = null
    private var poppinsMedium: Typeface? = null
    private var lPort: LinearLayout? = null
    private var lSkill: LinearLayout? = null
    private var noPortfolio: LinearLayout? = null
    private var noAbout: LinearLayout? = null
    private var ivLevelInfo: ImageView? = null
    private var ivProfileInfo: ImageView? = null
    private var noReview: LinearLayout? = null
    private var tickerReview: LinearLayout? = null
    private var posterReview: LinearLayout? = null
    private var noSkill: LinearLayout? = null
    private var txtNoReview: TextView? = null
    private var addSkill: TextView? = null
    private var addPortFilo: TextView? = null
    private var txtLevel: TextView? = null
    private var linLevel: LinearLayout? = null
    private var linFcc2: LinearLayout? = null
    private var linFcc: LinearLayout? = null
    private var levels: ArrayList<LevelsItem>? = null
    private var lastMonthIncome = 0F

    override fun onResume() {
        super.onResume()
        allProfileData
    }

    @SuppressLint("SetTextI18n", "RtlHardcoded")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_view, container, false)
        onProfileupdatelistener = this
        val b = arguments
        userId = (b)!!.getInt("userId", -1)
        return view
    }

    private fun setIDs() {
        noPortfolio = requireView().findViewById(R.id.no_port_folio)
        lPort = requireView().findViewById(R.id.lyt_Port)
        lSkill = requireView().findViewById(R.id.lyt_skills)
        noAbout = requireView().findViewById(R.id.no_about)
        noSkill = requireView().findViewById(R.id.no_port_skill)
        addPortFilo = requireView().findViewById(R.id.txt_add_portfolio)
        txtLevel = requireView().findViewById(R.id.txt_level)
        addSkill = requireView().findViewById(R.id.txt_add_skill)
        txtNoReview = requireView().findViewById(R.id.tv_no_review)
        noReview = requireView().findViewById(R.id.no_review)
        posterReview = requireView().findViewById(R.id.poster_review)
        tickerReview = requireView().findViewById(R.id.ticker_review)
        ivLevelInfo = requireView().findViewById(R.id.ivLevelInfo)
        ivProfileInfo = requireView().findViewById(R.id.ivProfileInfo)
        recyclerViewPortfolio = requireView().findViewById(R.id.recycler_view_portfolio)
        imgAvatar = requireView().findViewById(R.id.img_avatar)
        txtAbout = requireView().findViewById(R.id.txt_about)
        ivCall = requireView().findViewById(R.id.ivCall)
        ivCall2 = requireView().findViewById(R.id.ivCall2)
        ivCard = requireView().findViewById(R.id.ivCard)
        ivCard2 = requireView().findViewById(R.id.ivCard2)
        tvAboutHeading = requireView().findViewById(R.id.tvAboutHeading)
        imgVerified = requireView().findViewById(R.id.img_verified)
        tagEducation = requireView().findViewById(R.id.tag_education)
        lytEducation = requireView().findViewById(R.id.lyt_education)
        tagSpecialities = requireView().findViewById(R.id.tag_specialities)
        lytSpecialities = requireView().findViewById(R.id.lyt_specialities)
        tagLanguage = requireView().findViewById(R.id.tag_language)
        tagExperience = requireView().findViewById(R.id.tag_experience)
        lytExperience = requireView().findViewById(R.id.lyt_experience)
        tagTransportation = requireView().findViewById(R.id.tag_transportation)
        lytTransportation = requireView().findViewById(R.id.lyt_transportation)
        txtFullName = requireView().findViewById(R.id.txt_full_name)
        txtSuburb = requireView().findViewById(R.id.txt_suburb)
        txtLastSeen = requireView().findViewById(R.id.txt_last_seen)
        tvViewAllReviews = requireView().findViewById(R.id.tvViewAllReviews)
        tvSkills = requireView().findViewById(R.id.tvSkills)
        txtAccountLevel = requireView().findViewById(R.id.txt_account_level)
        ratingbarAsTicker = requireView().findViewById(R.id.ratingbarAsTicker)
        ratingbarAsPoster = requireView().findViewById(R.id.ratingbarAsPoster)
        tvTickerReview = requireView().findViewById(R.id.tvTickerReview)
        content = requireView().findViewById(R.id.content)
        pbLoading = requireView().findViewById(R.id.pbLoading)
        tvPosterReview = requireView().findViewById(R.id.tvPosterReview)
        tvTickerCompletionRate = requireView().findViewById(R.id.tvTickerCompletionRate)
        tvPosterCompletionRate = requireView().findViewById(R.id.tvPosterCompletionRate)
        rbPortfollio = requireView().findViewById(R.id.rbPortfollio)
        rbSkills = requireView().findViewById(R.id.rbSkills)
        btnQuote = requireView().findViewById(R.id.card_get_quote)
        llEnlarge = requireView().findViewById(R.id.llEnlarge)
        lytBtnGetAQuote = requireView().findViewById(R.id.lyt_btn_get_a_quote)
        ivMedalBoronz = requireView().findViewById(R.id.ivMedalBoronz)
        ivMedalTop = requireView().findViewById(R.id.ivMedalTop)
        ivMedalGOld = requireView().findViewById(R.id.ivMedalGOld)
        ivMedalMax = requireView().findViewById(R.id.ivMedalMax)
        lytLanguage = requireView().findViewById(R.id.lyt_language)
        linFcc = requireView().findViewById(R.id.lin_fcc)
        linFcc2 = requireView().findViewById(R.id.lin_fcc2)
        linLevel = requireView().findViewById(R.id.lin_level)
        linLevel!!.visibility = View.VISIBLE
        linFcc2!!.visibility = View.VISIBLE
        linFcc!!.visibility = View.GONE
    }

    private fun initToolbar() {
        profileActivity = requireActivity() as ProfileActivity
        poppinsMedium = ResourcesCompat.getFont(requireContext(), R.font.roboto_medium)
        onProfileupdatelistener = this
    }

    @SuppressLint("NonConstantResourceId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setIDs()
        ivLevelInfo!!.setOnClickListener {
            val levelsInfoBottomSheet = LevelsInfoBottomSheet()
            levelsInfoBottomSheet.show(parentFragmentManager, "")
        }
        ivProfileInfo!!.setOnClickListener {
            val levelInfoBottomSheet = LevelInfoBottomSheet()
            levelInfoBottomSheet.show(parentFragmentManager, "")
        }
        lytBtnGetAQuote!!.setOnClickListener {
            startCategoryList()
        }
        initToolbar()
        sessionManager = SessionManager(profileActivity)
        userAccountModel = UserAccountModel()
        attachmentArrayList = ArrayList()
        badgesModelArrayList = ArrayList()
        init()
        allProfileData
        initComponent()

        //   initComponentScroll(view);
    }

    private fun startCategoryList() {
        val infoBottomSheet = CategoryListBottomSheet(sessionManager)
        infoBottomSheet.show(requireActivity().supportFragmentManager, null)
    }

    private fun initComponentScroll(view: View) {
        val nestedContent: NestedScrollView = view.findViewById(R.id.nested_scroll_view)
        nestedContent.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (scrollY < oldScrollY) { // up
                animateFab(true)
            }
            if (scrollY > oldScrollY) { // down
                animateFab(true)
            }
        }
    }

    var isFabHide = false
    private fun animateFab(hide: Boolean) {
        if (isFabHide && hide || !isFabHide && !hide) return
        isFabHide = hide
        val moveY = if (hide) 2 * btnQuote!!.height else 0
        btnQuote!!.animate().translationY(moveY.toFloat()).setStartDelay(100).setDuration(300)
            .start()
    }

    private fun initComponent() {
        rbPortfollio!!.setOnCheckedChangeListener { group: CompoundButton?, checkedId: Boolean -> onChangeTabBiography() }
        rbSkills!!.setOnCheckedChangeListener { group: CompoundButton?, checkedId: Boolean -> onChangeTabBiography() }
    }

    @SuppressLint("SetTextI18n")
    private fun onChangeTabUser() {
        if (userAccountModel!!.postTaskStatistics != null && userAccountModel!!.postTaskStatistics.completionRate != null) {
            tvTickerCompletionRate!!.text =
                userAccountModel!!.postTaskStatistics.completionRate.toString() + "%"
        }
    }

    private fun onChangeTabBiography() {
        if (rbPortfollio!!.isChecked) {
            //lytAbout.setVisibility(View.VISIBLE);
            if (attachmentArrayList!!.size <= 0) {
                noPortfolio!!.visibility = View.VISIBLE
                noSkill!!.visibility = View.GONE
                recyclerViewPortfolio!!.visibility = View.GONE
                addPortFilo!!.setOnClickListener { view13: View? ->
                    val intent = Intent(requireActivity(), EditProfileActivity::class.java)
                    startActivity(intent)
                }
                lPort!!.visibility = View.GONE
            } else {
                if (attachmentArrayList!!.size > 10) {
                    (requireActivity() as ActivityBase).showToast(
                        "MAX 10 picture",
                        requireContext()
                    )
                }
                recyclerViewPortfolio!!.visibility = View.VISIBLE
                noSkill!!.visibility = View.GONE
                noPortfolio!!.visibility = View.GONE
                lPort!!.visibility = View.VISIBLE
            }
            lSkill!!.visibility = View.GONE
            rbPortfollio!!.setTextColor(resources.getColor(R.color.N600))
            rbSkills!!.setTextColor(resources.getColor(R.color.N100))
        } else if (rbSkills!!.isChecked) {
            if (tagEducation!!.size() <= 0 && tagExperience!!.size() <= 0 && tagLanguage!!.size() <= 0 && tagSpecialities!!.size() <= 0 && tagTransportation!!.size() <= 0) {
                noPortfolio!!.visibility = View.GONE
                noSkill!!.visibility = View.VISIBLE
                addSkill!!.setOnClickListener { view13: View? ->
                    val intent = Intent(requireActivity(), EditProfileActivity::class.java)
                    startActivity(intent)
                }
                lSkill!!.visibility = View.GONE
                tvSkills!!.visibility = View.GONE
            } else {
                noPortfolio!!.visibility = View.GONE
                noSkill!!.visibility = View.GONE
                lSkill!!.visibility = View.VISIBLE
                tvSkills!!.visibility = View.VISIBLE
            }
            recyclerViewPortfolio!!.visibility = View.GONE
            lPort!!.visibility = View.GONE
            rbPortfollio!!.setTextColor(resources.getColor(R.color.N100))
            rbSkills!!.setTextColor(resources.getColor(R.color.N600))
        }
    }

    private fun init() {
        recyclerViewPortfolio!!.layoutManager = GridLayoutManager(profileActivity, 3)
        recyclerViewPortfolio!!.addItemDecoration(
            SpacingItemDecoration(
                3,
                Tools.dpToPx(profileActivity, 3),
                true
            )
        )
        recyclerViewPortfolio!!.setHasFixedSize(true)
        adapter = AttachmentAdapter(attachmentArrayList, false, activity)
        recyclerViewPortfolio!!.adapter = adapter
        adapter!!.setOnItemClickListener(this)
        badgesAdapter = BadgesAdapter(badgesModelArrayList)
    }// map1.put("X-Requested-With", "XMLHttpRequest");

    private val allProfileData: Unit
        get() {
            val stringRequest: StringRequest =
                object : StringRequest(Method.GET, Constant.URL_PROFILE + "/" + userId,
                    Response.Listener { response: String? ->
                        Timber.e(response)
                        content!!.visibility = View.VISIBLE
                        pbLoading!!.visibility = View.GONE
                        btnQuote!!.visibility = View.VISIBLE;
                        try {
                            val jsonObject = JSONObject(response!!)
                            Timber.e(jsonObject.toString())
                            if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                userAccountModel =
                                    UserAccountModel().getJsonToModel(jsonObject.getJSONObject("data"))
                                val gson = Gson()
                                val levels = gson.fromJson(
                                    jsonObject.getJSONObject("data").getJSONArray("levels")
                                        .toString(),
                                    Levels::class.java
                                )
                                if (userAccountModel!!.lastMonthIncome != null)
                                    this.lastMonthIncome =
                                        userAccountModel!!.lastMonthIncome.toFloat()
                                this.levels = ArrayList()
                                this.levels!!.addAll(levels)
                                checkLevel(levels)
                                setUpAllEditFields(userAccountModel)
                                attachmentArrayList = userAccountModel!!.portfolio
                                adapter!!.clear()
                                badgesModelArrayList = userAccountModel!!.badges
                                if (attachmentArrayList!!.size <= 0) {
                                    noPortfolio!!.visibility = View.VISIBLE
                                    lPort!!.visibility = View.GONE
                                } else {
                                    recyclerViewPortfolio!!.visibility = View.VISIBLE
                                    noPortfolio!!.visibility = View.GONE
                                    lPort!!.visibility = View.VISIBLE
                                    adapter!!.addItems(attachmentArrayList)
                                }
                                if (badgesModelArrayList!!.size <= 0) {
                                    noPortfolio!!.visibility = View.VISIBLE
                                    lSkill!!.visibility = View.GONE
                                } else {
                                    noPortfolio!!.visibility = View.GONE
                                    lSkill!!.visibility = View.VISIBLE
                                }
                                badgesAdapter!!.addItems(badgesModelArrayList)
                                if (userAccountModel!!.portfolio.size == 0) {
                                    noPortfolio!!.visibility = View.VISIBLE
                                } else {
                                    noPortfolio!!.visibility = View.GONE
                                }
                                if (rbPortfollio!!.isChecked) {
                                    lSkill!!.visibility = View.GONE
                                    noSkill!!.visibility = View.GONE
                                }
                            } else {
                                profileActivity!!.showToast("Something went wrong", profileActivity)
                            }
                        } catch (e: JSONException) {
                            profileActivity!!.showToast("Something went wrong", profileActivity)
                            Timber.e(e.toString())
                            e.printStackTrace()
                        }
                    },
                    Response.ErrorListener { error: VolleyError ->
                        profileActivity!!.errorHandle1(error.networkResponse)
                        profileActivity!!.hideProgressDialog()
                    }) {
                    override fun getHeaders(): Map<String, String> {
                        val map1: MutableMap<String, String> = HashMap()
                        map1["authorization"] =
                            sessionManager!!.tokenType + " " + sessionManager!!.accessToken
                        map1["Content-Type"] = "application/x-www-form-urlencoded"
                        map1["Version"] = BuildConfig.VERSION_CODE.toString()
                        // map1.put("X-Requested-With", "XMLHttpRequest");
                        return map1
                    }
                }
            stringRequest.retryPolicy = DefaultRetryPolicy(
                0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            val requestQueue = Volley.newRequestQueue(profileActivity)
            requestQueue.add(stringRequest)
            Timber.e(stringRequest.url)
        }

    private fun checkLevel(levels: Levels?) {
        for (i in 0 until levels!!.size) {
            if ((((lastMonthIncome - levels[i].min_amount.toFloat()) / (levels[i].max_amount.toFloat() - levels[i].min_amount.toFloat())) * 100).toInt() >= 0) {
                when (i) {
                    0 -> {
                        ivMedalTop!!.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_level1_active
                            )
                        )
                        txtLevel!!.text = "Level 1"
                    }
                    1 -> {
                        ivMedalTop!!.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_level2_active
                            )
                        )
                        txtLevel!!.text = "Level 2"
                    }
                    2 -> {
                        ivMedalTop!!.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_level3_active
                            )
                        )
                        txtLevel!!.text = "Level 3"
                    }
                    3 -> {
                        ivMedalTop!!.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_level4_active
                            )
                        )
                        txtLevel!!.text = "Level 4"
                    }
                }
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setUpAllEditFields(userAccountModel: UserAccountModel?) {
        tvAboutHeading!!.setTypeface(txtAbout!!.typeface, Typeface.BOLD_ITALIC)
        if (userAccountModel!!.mobileVerifiedAt != null) {
            ivCall!!.setBackgroundResource(R.drawable.bg_rounded_profile_badge_enable)
            ivCall2!!.setBackgroundResource(R.drawable.bg_rounded_profile_badge_enable)
        }
        if (userAccountModel.account_status != null && userAccountModel.account_status.isBadges) {
            ivCard!!.setBackgroundResource(R.drawable.bg_rounded_profile_badge_enable)
            ivCard2!!.setBackgroundResource(R.drawable.bg_rounded_profile_badge_enable)
        }
        if (userAccountModel.about == null || userAccountModel.about == "") {
            txtAbout!!.text = "Nothing to show"
            txtAbout!!.visibility = View.GONE
        } else {
            txtAbout!!.visibility = View.VISIBLE
            setMoreLess(txtAbout!!, userAccountModel.about, 5)
        }
        if (userAccountModel.tagline == null || userAccountModel.tagline == "") {
            tvAboutHeading!!.text = "\"Nothing to show\""
            tvAboutHeading!!.visibility = View.GONE
        } else {
            tvAboutHeading!!.visibility = View.VISIBLE
            tvAboutHeading!!.text = "\"" + userAccountModel.tagline + "\""
        }
        if (userAccountModel.workerRatings == null) {
            tickerReview!!.visibility = View.GONE
            noReview!!.visibility = View.VISIBLE
            txtNoReview!!.visibility = View.VISIBLE
            tvViewAllReviews!!.visibility = View.GONE
        } else {
            noReview!!.visibility = View.GONE
            tickerReview!!.visibility = View.VISIBLE
            ratingbarAsTicker!!.rating = userAccountModel.workerRatings.avgRating
            tvTickerReview!!.text =
                "(" + userAccountModel.workerRatings.receivedReviews.toString() + ")"
            if (userAccountModel.workTaskStatistics != null) tvTickerCompletionRate!!.text =
                userAccountModel.workTaskStatistics.completionRate.toString() + "%"
        }
        if (userAccountModel.posterRatings == null) {
            posterReview!!.visibility = View.GONE
            noReview!!.visibility = View.VISIBLE
            txtNoReview!!.visibility = View.VISIBLE
            tvViewAllReviews!!.visibility = View.GONE
        } else {
            posterReview!!.visibility = View.VISIBLE
            noReview!!.visibility = View.GONE
            ratingbarAsPoster!!.rating = userAccountModel.posterRatings.avgRating
            tvPosterReview!!.text =
                "(" + userAccountModel.posterRatings.receivedReviews.toString() + ")"
            if (userAccountModel.postTaskStatistics != null) tvPosterCompletionRate!!.text =
                userAccountModel.postTaskStatistics.completionRate.toString() + "%"
        }
        when (userAccountModel.workerTier.id) {
            1 -> ivMedalBoronz!!.setImageResource(R.drawable.ic_boronz_selected)
            2 -> ivMedalBoronz!!.setImageResource(R.drawable.ic_silver_selected)
            3 -> ivMedalBoronz!!.setImageResource(R.drawable.ic_gold_selected)
            4 -> ivMedalBoronz!!.setImageResource(R.drawable.ic_max_selected)
        }
        if (userAccountModel.isVerifiedAccount == 1) {
            imgVerified!!.visibility = View.VISIBLE
        } else {
            imgVerified!!.visibility = View.GONE
        }
        if (userAccountModel.portfolio.size == 0) {
            noPortfolio!!.visibility = View.VISIBLE
        } else {
            noPortfolio!!.visibility = View.GONE
        }
        if (userAccountModel.skills.experience == null && userAccountModel.skills.experience.size == 0 && userAccountModel.skills.language == null && userAccountModel.skills.language.size == 0 && userAccountModel.skills.specialities == null && userAccountModel.skills.specialities.size == 0 && userAccountModel.skills.transportation == null && userAccountModel.skills.transportation.size == 0 && userAccountModel.skills.education == null && userAccountModel.skills.education.size == 0) {
            noPortfolio!!.visibility = View.GONE
            lSkill!!.visibility = View.GONE
            lytEducation!!.visibility = View.GONE
            lytExperience!!.visibility = View.GONE
            lytLanguage!!.visibility = View.GONE
            lytSpecialities!!.visibility = View.GONE
            lytTransportation!!.visibility = View.GONE
            recyclerViewPortfolio!!.visibility = View.GONE
            lPort!!.visibility = View.GONE
        } else {
            if (userAccountModel.skills.education != null && userAccountModel.skills.education.size != 0) {
                lytEducation!!.visibility = View.VISIBLE
                //tagEducation.setText(userAccountModel.getSkills().getEducation());
                tagEducation!!.tags = userAccountModel.skills.education
            } else {
                lytEducation!!.visibility = View.GONE
                tagEducation!!.tags = ArrayList()
            }
            if (userAccountModel.skills.experience != null && userAccountModel.skills.experience.size != 0) {
                lytExperience!!.visibility = View.VISIBLE
                tagExperience!!.tags = userAccountModel.skills.experience
            } else {
                lytExperience!!.visibility = View.GONE
                tagExperience!!.tags = ArrayList()
            }
            if (userAccountModel.skills.language != null && userAccountModel.skills.language.size != 0) {
                lytLanguage!!.visibility = View.VISIBLE
                tagLanguage!!.tags = userAccountModel.skills.language
            } else {
                lytLanguage!!.visibility = View.GONE
                tagLanguage!!.tags = ArrayList()
            }
            if (userAccountModel.skills.specialities != null && userAccountModel.skills.specialities.size != 0) {
                lytSpecialities!!.visibility = View.VISIBLE
                tagSpecialities!!.tags = userAccountModel.skills.specialities
            } else {
                lytSpecialities!!.visibility = View.GONE
                tagSpecialities!!.tags = ArrayList()
            }
            if (userAccountModel.skills.transportation != null && userAccountModel.skills.transportation.size != 0) {
                lytTransportation!!.visibility = View.VISIBLE
                tagTransportation!!.tags = userAccountModel.skills.transportation
            } else {
                lytTransportation!!.visibility = View.GONE
                tagTransportation!!.tags = ArrayList()
            }
        }
        tagEducation!!.tagTypeface = poppinsMedium
        tagSpecialities!!.tagTypeface = poppinsMedium
        tagLanguage!!.tagTypeface = poppinsMedium
        tagExperience!!.tagTypeface = poppinsMedium
        tagTransportation!!.tagTypeface = poppinsMedium
        if (userAccountModel.avatar != null) {
            ImageUtil.displayImage(imgAvatar, userAccountModel.avatar.thumbUrl, null)
        }
        txtFullName!!.text = userAccountModel.name
        txtSuburb!!.text = userAccountModel.location
        txtLastSeen!!.text = "Last Seen  " + userAccountModel.lastOnline
        tvViewAllReviews!!.setOnClickListener { v: View? ->
            val bundle = Bundle()
            bundle.putInt(Constant.userID, userId)
            ReviewsActivity.userAccountModel = userAccountModel
            bundle.putString("WhoIs", Constant.AS_A_WORKER)
            startActivity(
                Intent(profileActivity, ReviewsActivity::class.java)
                    .putExtras(bundle)
            )
        }
        onChangeTabBiography()
        onChangeTabUser()
    }


    override fun updatedSuccesfully(path: String?) {
        if (path != null) {
            ImageUtil.displayImage(imgAvatar, path, null)
        }
    }

    override fun updateProfile() {
        adapter!!.clear()
        allProfileData
    }

    override fun onItemClick(view: View, obj: AttachmentModel, position: Int, action: String) {
        val intent = Intent(context, ZoomImageActivity::class.java)
        intent.putExtra("url", attachmentArrayList)
        intent.putExtra("title", "")
        intent.putExtra("pos", position)
        startActivity(intent)
    }

    companion object {
        var onProfileupdatelistener: onProfileUpdateListener? = null
    }
}