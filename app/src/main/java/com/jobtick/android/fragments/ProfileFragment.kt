package com.jobtick.android.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
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
import com.jobtick.android.activities.ActivityBase
import com.jobtick.android.activities.CategoryListActivity
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.activities.EditProfileActivity
import com.jobtick.android.activities.ReviewsActivity
import com.jobtick.android.activities.SettingActivity
import com.jobtick.android.activities.ZoomImageActivity
import com.jobtick.android.adapers.AttachmentAdapter
import com.jobtick.android.adapers.BadgesAdapter
import com.jobtick.android.interfaces.onProfileUpdateListener
import com.jobtick.android.models.AccountStatusModel
import com.jobtick.android.models.AttachmentModel
import com.jobtick.android.models.BadgesModel
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.network.model.response.Levels
import com.jobtick.android.network.model.response.LevelsItem
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.ImageUtil
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.Tools
import com.jobtick.android.utils.cleanRound
import com.jobtick.android.utils.setMoreLess
import com.jobtick.android.widget.CircularProgressView
import com.jobtick.android.widget.SpacingItemDecoration
import com.mikhaellopez.circularimageview.CircularImageView
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment(), onProfileUpdateListener, AttachmentAdapter.OnItemClickListener {
    private var recyclerViewPortfolio: RecyclerView? = null
    private var imgAvatar: CircularImageView? = null
    private var ivCall: ImageView? = null
    private var ivCard: ImageView? = null
    private var ivCall2: ImageView? = null
    private var ivCard2: ImageView? = null
    private var txtAbout: TextView? = null
    private var tvAboutHeading: TextView? = null
    private var tvAboutMeHeader: TextView? = null
    private var imgVerified: ImageView? = null
    private var tagEducation: TagContainerLayout? = null
    private var lytEducation: LinearLayout? = null
    private var tagLanguage: TagContainerLayout? = null
    private var lytLanguage: LinearLayout? = null
    private var tagSkills: TagContainerLayout? = null
    private var lytSkills: LinearLayout? = null
    private var txtFullName: TextView? = null
    private var txtSuburb: TextView? = null
    private var txtLastSeen: TextView? = null
    private var tvViewAllReviews: TextView? = null
    var tvPosterNoreview: TextView? = null
    var tvTickerNoreview: TextView? = null
    var lnTickerJobsuccess: LinearLayout? = null
    var lnPosterJobsuccess: LinearLayout? = null
    private var txtAccountLevel: TextView? = null
    private var ratingbarAsTicker: RatingBar? = null
    private var ratingbarAsPoster: RatingBar? = null
    private var tvTickerReview: TextView? = null
    private var tvPosterReview: TextView? = null
    private var tvTickerCompletionRate: TextView? = null
    private var tvPosterCompletionRate: TextView? = null
    private var rbPortfollio: RadioButton? = null
    private var rbSkills: RadioButton? = null
    private var btnQuote: CardView? = null
    private var llEnlarge: LinearLayout? = null
    private var ivMedalBoronz: ImageView? = null
    private var ivMedalTop: ImageView? = null
    private var txtLevel: TextView? = null
    private var ivMedalSilver: ImageView? = null
    private var ivMedalGold: ImageView? = null
    private var ivMedalMax: ImageView? = null
    private var progressLevel1: CircularProgressView? = null
    private var progressLevel2: CircularProgressView? = null
    private var progressLevel3: CircularProgressView? = null
    private var progressLevel4: CircularProgressView? = null
    private var pbLoading: ProgressBar? = null
    private var content: LinearLayout? = null
    private var dashboardActivity: DashboardActivity? = null
    private var toolbar: Toolbar? = null
    private var sessionManager: SessionManager? = null
    private var userAccountModel: UserAccountModel? = null
    private var attachmentArrayList: ArrayList<AttachmentModel>? = null
    private var badgesModelArrayList: ArrayList<BadgesModel>? = null
    private var adapter: AttachmentAdapter? = null
    private var badgesAdapter: BadgesAdapter? = null
    private var poppinsMedium: Typeface? = null
    private var lPort: LinearLayout? = null
    private var lSkill: LinearLayout? = null
    private var flAddSkill: FrameLayout? = null
    private var noPortfolio: LinearLayout? = null
    private var ivLevelInfo: ImageView? = null
    private var ivProfileInfo: ImageView? = null
    private var tickerReview: LinearLayout? = null
    private var linLevel: LinearLayout? = null
    private var linFcc2: LinearLayout? = null
    private var linFcc: LinearLayout? = null
    private var posterReview: LinearLayout? = null
    private var noSkill: LinearLayout? = null
    private var addSkill: TextView? = null
    private var addPortFilo: TextView? = null
    private var txtLevel1: TextView? = null
    private var txtLevel2: TextView? = null
    private var txtLevel3: TextView? = null
    private var txtLevel4: TextView? = null

    private var txtAccountStatus: TextView? = null
    private var txtPaymentStatus: TextView? = null
    private var txtSkillsStatus: TextView? = null
    private var txtAlertStatus: TextView? = null
    private var imAccountStatus: ImageView? = null
    private var imPaymentStatus: ImageView? = null
    private var imSkillsStatus: ImageView? = null
    private var imAlertStatus: ImageView? = null
    private var rlLevels: RelativeLayout? = null
    private var line1: View? = null
    private var line2: View? = null
    private var line3: View? = null
    private var levels: ArrayList<LevelsItem>? = null
    private var lastMonthIncome = 0F
    private var level = 1
    private lateinit var poster: RadioButton
    private lateinit var ticker: RadioButton
    private lateinit var lytTicker: LinearLayout
    private lateinit var lnReviews: LinearLayout
    private lateinit var lytPoster: LinearLayout
    private lateinit var txtReviewCount1Starp: TextView
    private lateinit var tickerReviewNum: TextView
    private lateinit var posterReviewNum: TextView
    private lateinit var txtReviewCount2Starp: TextView
    private lateinit var txtReviewCount3Starp: TextView
    private lateinit var txtReviewCount4Starp: TextView
    private lateinit var txtReviewCount5Starp: TextView
    private lateinit var progressBar1Starp: ProgressBar
    private lateinit var progressBar2Starp: ProgressBar
    private lateinit var progressBar3Starp: ProgressBar
    private lateinit var progressBar4Starp: ProgressBar
    private lateinit var progressBar5Starp: ProgressBar
    private lateinit var progressBar5Star: ProgressBar
    private lateinit var progressBar4Star: ProgressBar
    private lateinit var progressBar3Star: ProgressBar
    private lateinit var progressBar2Star: ProgressBar
    private lateinit var progressBar1Star: ProgressBar
    private lateinit var txtReviewCount5Star: TextView
    private lateinit var txtReviewCount4Star: TextView
    private lateinit var txtReviewCount3Star: TextView
    private lateinit var txtReviewCount2Star: TextView
    private lateinit var txtReviewCount1Star: TextView
    private lateinit var txtRole: TextView
    private lateinit var linMode: LinearLayout
    private lateinit var reviewTabs: RadioGroup
    private lateinit var swTickerMode: SwitchCompat
    private lateinit var lnAboutMe: LinearLayout
    private lateinit var rlStatus: RelativeLayout
    private lateinit var fmPSkills: FrameLayout
    private var lnPosterReview: RelativeLayout? = null
    private var lnTickerReview: RelativeLayout? = null
    override fun onResume() {
        super.onResume()
        sessionManager?.let {
            it.userAccount?.let {
                allProfileData
            }
        }
    }

    @SuppressLint("SetTextI18n", "RtlHardcoded")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        onProfileupdatelistener = this
        return view
    }

    private fun setIDs() {
        txtAccountStatus = requireView().findViewById(R.id.txt_account_status)
        txtPaymentStatus = requireView().findViewById(R.id.txt_payment_status)
        txtSkillsStatus = requireView().findViewById(R.id.txt_skills_status)
        txtAlertStatus = requireView().findViewById(R.id.txt_alert_status)
        imAccountStatus = requireView().findViewById(R.id.im_account_status)
        imPaymentStatus = requireView().findViewById(R.id.im_payment_status)
        imSkillsStatus = requireView().findViewById(R.id.im_skills_status)
        imAlertStatus = requireView().findViewById(R.id.im_alert_status)
        rlLevels = requireView().findViewById(R.id.rl_levels)
        line1 = requireView().findViewById(R.id.line1)
        line2 = requireView().findViewById(R.id.line2)
        line3 = requireView().findViewById(R.id.line3)
        noPortfolio = requireView().findViewById(R.id.no_port_folio)
        lPort = requireView().findViewById(R.id.lyt_Port)
        lSkill = requireView().findViewById(R.id.lyt_skills)
        flAddSkill = requireView().findViewById(R.id.fl_add_skill)
        noSkill = requireView().findViewById(R.id.no_port_skill)
        addPortFilo = requireView().findViewById(R.id.txt_add_portfolio)
        addSkill = requireView().findViewById(R.id.txt_add_skill)
        posterReview = requireView().findViewById(R.id.poster_review)
        tickerReview = requireView().findViewById(R.id.ticker_review)
        ivLevelInfo = requireView().findViewById(R.id.ivLevelInfo)
        ivProfileInfo = requireView().findViewById(R.id.ivProfileInfo)
        recyclerViewPortfolio = requireView().findViewById(R.id.recycler_view_portfolio)
        imgAvatar = requireView().findViewById(R.id.img_avatar)
        txtAbout = requireView().findViewById(R.id.txt_about)
        ivCall = requireView().findViewById(R.id.ivCall)
        ivCard = requireView().findViewById(R.id.ivCard)
        ivCall2 = requireView().findViewById(R.id.ivCall2)
        ivCard2 = requireView().findViewById(R.id.ivCard2)
        tvAboutHeading = requireView().findViewById(R.id.tvAboutHeading)
        imgVerified = requireView().findViewById(R.id.img_verified)
        tagEducation = requireView().findViewById(R.id.tag_education)
        lytEducation = requireView().findViewById(R.id.lyt_education)
        tagLanguage = requireView().findViewById(R.id.tag_language)
        lytLanguage = requireView().findViewById(R.id.lyt_language)
        tagSkills = requireView().findViewById(R.id.tag_experience)
        lytSkills = requireView().findViewById(R.id.lyt_experience)
        txtFullName = requireView().findViewById(R.id.txt_full_name)
        txtSuburb = requireView().findViewById(R.id.txt_suburb)
        txtLastSeen = requireView().findViewById(R.id.txt_last_seen)
        tvViewAllReviews = requireView().findViewById(R.id.tvViewAllReviews)
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
        ivMedalBoronz = requireView().findViewById(R.id.ivMedalBoronz)
        ivMedalTop = requireView().findViewById(R.id.ivMedalTop)
        txtLevel = requireView().findViewById(R.id.txt_level)
        ivMedalSilver = requireView().findViewById(R.id.ivMedalSilver)
        ivMedalGold = requireView().findViewById(R.id.ivMedalGOld)
        ivMedalMax = requireView().findViewById(R.id.ivMedalMax)
        progressLevel1 = requireView().findViewById(R.id.progress_level1)
        progressLevel2 = requireView().findViewById(R.id.progress_level2)
        progressLevel3 = requireView().findViewById(R.id.progress_level3)
        progressLevel4 = requireView().findViewById(R.id.progress_level4)
        linFcc = requireView().findViewById(R.id.lin_fcc)
        linFcc2 = requireView().findViewById(R.id.lin_fcc2)
        linLevel = requireView().findViewById(R.id.lin_level)
        txtLevel1 = requireView().findViewById(R.id.txt_level1)
        txtLevel2 = requireView().findViewById(R.id.txt_level2)
        txtLevel3 = requireView().findViewById(R.id.txt_level3)
        txtLevel4 = requireView().findViewById(R.id.txt_level4)
        tvPosterNoreview = requireView().findViewById(R.id.tv_poster_NoReview)
        tvTickerNoreview = requireView().findViewById(R.id.tv_ticker_NoReview)
        lnPosterJobsuccess = requireView().findViewById(R.id.ln_poster_jobSuccess)
        lnTickerJobsuccess = requireView().findViewById(R.id.ln_ticker_jobSuccess)
        lytTicker = requireView().findViewById(R.id.ticker)
        lytPoster = requireView().findViewById(R.id.Poster)
        lnReviews = requireView().findViewById(R.id.ln_reviews)
        progressBar1Starp = requireView().findViewById(R.id.progress_bar_1_starP)
        txtReviewCount1Starp = requireView().findViewById(R.id.txt_review_count_1_starP)
        progressBar2Starp = requireView().findViewById(R.id.progress_bar_2_starP)
        txtReviewCount2Starp = requireView().findViewById(R.id.txt_review_count_2_starP)
        progressBar3Starp = requireView().findViewById(R.id.progress_bar_3_starP)
        txtReviewCount3Starp = requireView().findViewById(R.id.txt_review_count_3_starP)
        progressBar4Starp = requireView().findViewById(R.id.progress_bar_4_starP)
        txtReviewCount4Starp = requireView().findViewById(R.id.txt_review_count_4_starP)
        progressBar5Starp = requireView().findViewById(R.id.progress_bar_5_starP)
        txtReviewCount5Starp = requireView().findViewById(R.id.txt_review_count_5_starP)
        poster = requireView().findViewById(R.id.rbPoster)
        ticker = requireView().findViewById(R.id.rbTicker)
        progressBar5Star = requireView().findViewById(R.id.progress_bar_5_star)
        progressBar4Star = requireView().findViewById(R.id.progress_bar_4_star)
        progressBar3Star = requireView().findViewById(R.id.progress_bar_3_star)
        progressBar2Star = requireView().findViewById(R.id.progress_bar_2_star)
        progressBar1Star = requireView().findViewById(R.id.progress_bar_1_star)
        txtReviewCount5Star = requireView().findViewById(R.id.txt_review_count_5_star)
        txtReviewCount4Star = requireView().findViewById(R.id.txt_review_count_4_star)
        txtReviewCount3Star = requireView().findViewById(R.id.txt_review_count_3_star)
        txtReviewCount2Star = requireView().findViewById(R.id.txt_review_count_2_star)
        txtReviewCount1Star = requireView().findViewById(R.id.txt_review_count_1_star)
        lnPosterReview = requireView().findViewById(R.id.ln_poster_review)
        lnTickerReview = requireView().findViewById(R.id.ln_ticker_review)
        posterReviewNum = requireView().findViewById(R.id.poster_review_num)
        tickerReviewNum = requireView().findViewById(R.id.ticker_review_num)
        reviewTabs = requireView().findViewById(R.id.reviewTabs)
        linMode = requireView().findViewById(R.id.lin_mode)
        swTickerMode = requireView().findViewById(R.id.sw_ticker_mode)
        txtRole = requireView().findViewById(R.id.txt_role)
        lnAboutMe = requireView().findViewById(R.id.ln_about_me)
        rlStatus = requireView().findViewById(R.id.rl_status)
        fmPSkills = requireView().findViewById(R.id.fm_p_skills)
        linFcc!!.visibility = View.GONE
        reviewTabs.visibility = View.GONE
        linFcc2!!.visibility = View.VISIBLE
        linMode.visibility = View.VISIBLE
        txtRole.visibility = View.VISIBLE
    }

    private fun initToolbar() {
        poppinsMedium = ResourcesCompat.getFont(requireContext(), R.font.roboto_medium)
        onProfileupdatelistener = this
        if (dashboardActivity != null) {
            toolbar = dashboardActivity!!.findViewById(R.id.toolbar)
            if (sessionManager?.accessToken != null) {
                toolbar!!.menu.clear()
                toolbar!!.inflateMenu(R.menu.menu_profile)
            }else {
               val signIn = dashboardActivity!!.findViewById<LinearLayout>(R.id.lin_signIn)
                signIn.visibility= View.VISIBLE
                signIn.setOnClickListener {
                    sessionManager?.let {
                        it.needSignIN = true
                    }
                    dashboardActivity!!.unauthorizedUser()
                }
            }
            val ivNotification = dashboardActivity!!.findViewById<ImageView>(R.id.ivNotification)
            ivNotification.visibility = View.GONE
            val toolbarTitle = dashboardActivity!!.findViewById<TextView>(R.id.toolbar_title)
            toolbarTitle.visibility = View.VISIBLE
            toolbarTitle.setText(R.string.profile)
            toolbarTitle!!.textSize = 20f
            toolbarTitle.typeface =
                ResourcesCompat.getFont(requireContext(), R.font.roboto_medium)
            toolbar!!.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey_100))
            val params = Toolbar.LayoutParams(
                Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT
            )
            params.gravity = Gravity.START
            toolbarTitle.layoutParams = params
            btnQuote!!.setOnClickListener {
                val creatingTask = Intent(requireActivity(), CategoryListActivity::class.java)
                val bundle = Bundle()
                bundle.putString("category", "")
                creatingTask.putExtras(bundle)
                requireContext().startActivity(creatingTask)
            }
            toolbar!!.navigationIcon = null
        }
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
        dashboardActivity = requireActivity() as DashboardActivity
        sessionManager = SessionManager(dashboardActivity)
        initToolbar()
        userAccountModel = UserAccountModel()
        attachmentArrayList = ArrayList()
        badgesModelArrayList = ArrayList()
        toolbar!!.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_flag -> {
                }
                R.id.action_edit -> startActivity(
                    Intent(
                        dashboardActivity,
                        SettingActivity::class.java
                    )
                )
            }
            false
        }
        rlLevels!!.setOnClickListener {
            val levelsInfoBottomSheet = LevelsBottomSheet(levels!!, lastMonthIncome)
            levelsInfoBottomSheet.show(parentFragmentManager, "")
        }
        init()
        sessionManager?.let {
            it.userAccount?.let {
                allProfileData
            }
        }

        initComponent()
    }

    private fun initComponent() {
        if (sessionManager?.accessToken != null)
            if (sessionManager!!.roleLocal == "poster") {
                lnPosterReview!!.visibility = View.VISIBLE
                lnTickerReview!!.visibility = View.GONE
                fmPSkills.visibility = View.GONE
                rlStatus.visibility = View.GONE
                lnAboutMe.visibility = View.GONE
                linLevel!!.visibility = View.GONE
                flAddSkill!!.visibility = View.GONE
                swTickerMode.isChecked = false
                txtRole.text = "As a Poster"
            } else {
                lnPosterReview!!.visibility = View.GONE
                lnTickerReview!!.visibility = View.VISIBLE
                fmPSkills.visibility = View.VISIBLE
                rlStatus.visibility = View.VISIBLE
                lnAboutMe.visibility = View.VISIBLE
                linLevel!!.visibility = View.VISIBLE
                flAddSkill!!.visibility = View.VISIBLE
                swTickerMode.isChecked = true
                txtRole.text = "As a Ticker"
            }
        else {
            lnPosterReview!!.visibility = View.GONE
            fmPSkills.visibility = View.GONE
            rlStatus.visibility = View.GONE
            lnAboutMe.visibility = View.GONE
            linLevel!!.visibility = View.GONE
            flAddSkill!!.visibility = View.GONE
            reviewTabs.visibility = View.GONE
            imgVerified!!.visibility = View.GONE
            lnReviews!!.visibility = View.GONE
            if (sessionManager!!.roleLocal == "poster") {
                swTickerMode.isChecked = false
                txtRole.text = "As a Poster"
            } else {

                swTickerMode.isChecked = true
                txtRole.text = "As a Ticker"
            }
            txtFullName!!.text = "Guest"
            txtSuburb!!.text = "Location"
        }
        swTickerMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                sessionManager!!.roleLocal = "ticker"
            else
                sessionManager!!.roleLocal = "poster"
            initComponent()
            (requireActivity() as DashboardActivity).resetBottomBar()
        }
        rbPortfollio!!.setOnCheckedChangeListener { _: CompoundButton?, _: Boolean -> onChangeTabBiography() }
        rbSkills!!.setOnCheckedChangeListener { _: CompoundButton?, _: Boolean -> onChangeTabBiography() }
    }

    @SuppressLint("SetTextI18n")
    private fun onChangeTabUser() {
        if (userAccountModel!!.postTaskStatistics != null && userAccountModel!!.postTaskStatistics.completionRate != null) {
            tvTickerCompletionRate!!.text =
                userAccountModel!!.postTaskStatistics.completionRate.toString() + "%"
        }
    }

    private fun onChangeTabBiography() {
        if (context == null) return
        if (rbPortfollio!!.isChecked) {
            // lytAbout.setVisibility(View.VISIBLE);
            if (attachmentArrayList!!.size <= 0) {
                noPortfolio!!.visibility = View.VISIBLE
                noSkill!!.visibility = View.GONE
                recyclerViewPortfolio!!.visibility = View.GONE
                addPortFilo!!.setOnClickListener { view13: View? ->
                    val intent = Intent(requireActivity(), EditProfileActivity::class.java)
                    intent.putExtra(ConstantKey.TAB, ConstantKey.PORTFO_SKILLS)
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
            flAddSkill!!.visibility = View.GONE
            rbPortfollio!!.setTextColor(resources.getColor(R.color.N600))
            rbSkills!!.setTextColor(resources.getColor(R.color.N100))
        } else if (rbSkills!!.isChecked) {
            if (tagEducation!!.size() <= 0 && tagSkills!!.size() <= 0 && tagLanguage!!.size() <= 0) {
                noPortfolio!!.visibility = View.GONE
                noSkill!!.visibility = View.VISIBLE
                addSkill!!.setOnClickListener { view13: View? ->
                    val intent = Intent(requireActivity(), EditProfileActivity::class.java)
                    intent.putExtra(ConstantKey.TAB, ConstantKey.PORTFO_SKILLS)
                    startActivity(intent)
                }
                lSkill!!.visibility = View.GONE
                flAddSkill!!.visibility = View.GONE
            } else {
                flAddSkill!!.setOnClickListener {
                    val intent = Intent(requireActivity(), EditProfileActivity::class.java)
                    intent.putExtra(ConstantKey.TAB, ConstantKey.PORTFO_SKILLS)
                    startActivity(intent)
                }
                noPortfolio!!.visibility = View.GONE
                noSkill!!.visibility = View.GONE
                lSkill!!.visibility = View.VISIBLE
                flAddSkill!!.visibility = View.VISIBLE
            }
            recyclerViewPortfolio!!.visibility = View.GONE
            lPort!!.visibility = View.GONE
            rbPortfollio!!.setTextColor(resources.getColor(R.color.N100))
            rbSkills!!.setTextColor(resources.getColor(R.color.N600))
        }
    }

    private fun init() {
        recyclerViewPortfolio!!.layoutManager = GridLayoutManager(dashboardActivity, 3)
        recyclerViewPortfolio!!.addItemDecoration(
            SpacingItemDecoration(
                3,
                Tools.dpToPx(dashboardActivity, 3),
                true
            )
        )
        recyclerViewPortfolio!!.setHasFixedSize(true)
        adapter = AttachmentAdapter(attachmentArrayList, false, activity)
        recyclerViewPortfolio!!.adapter = adapter
        adapter!!.setOnItemClickListener(this)
        badgesAdapter = BadgesAdapter(badgesModelArrayList)
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

    // map1.put("X-Requested-With", "XMLHttpRequest");
    private val allProfileData: Unit
        get() {
            pbLoading!!.visibility = View.VISIBLE
            content!!.visibility = View.GONE
            val stringRequest: StringRequest = object : StringRequest(
                Method.GET,
                Constant.URL_PROFILE + "/" + sessionManager!!.userAccount.id,
                Response.Listener { response: String? ->
                    Timber.e(response)
                    content!!.visibility = View.VISIBLE
                    pbLoading!!.visibility = View.GONE
                    btnQuote!!.visibility = View.GONE
                    try {
                        val jsonObject = JSONObject(response!!)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                            userAccountModel =
                                UserAccountModel().getJsonToModel(jsonObject.getJSONObject("data"))
                            val gson = Gson()
                            val levels = gson.fromJson(
                                jsonObject.getJSONObject("data").getJSONArray("levels").toString(),
                                Levels::class.java
                            )
                            this.levels = ArrayList()
                            this.levels!!.addAll(levels)
                            checkLevel(levels)
                            if (userAccountModel!!.lastMonthIncome != null)
                                this.lastMonthIncome = userAccountModel!!.lastMonthIncome.toFloat()
                            // setLevels(levels)
                            setJobStatus(userAccountModel!!.account_status)
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
                                flAddSkill!!.visibility = View.GONE
                            } else {
                                noPortfolio!!.visibility = View.GONE
                                lSkill!!.visibility = View.VISIBLE
                                flAddSkill!!.visibility = View.VISIBLE
                            }
                            badgesAdapter!!.addItems(badgesModelArrayList)
                            if (userAccountModel!!.portfolio.size == 0) {
                                noPortfolio!!.visibility = View.VISIBLE
                            } else {
                                noPortfolio!!.visibility = View.GONE
                            }
                            if (rbPortfollio!!.isChecked) {
                                lSkill!!.visibility = View.GONE
                                flAddSkill!!.visibility = View.GONE
                                noSkill!!.visibility = View.GONE
                            }
                            setUpAllEditFields(userAccountModel)
                        } else {
                            dashboardActivity!!.showToast("Connection error", dashboardActivity)
                        }
                    } catch (e: JSONException) {
                        dashboardActivity!!.showToast("Something went wrong", dashboardActivity)
                        Timber.e(e.toString())
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    dashboardActivity!!.errorHandle1(
                        error.networkResponse
                    )
                }
            ) {
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
            val requestQueue = Volley.newRequestQueue(dashboardActivity)
            requestQueue.add(stringRequest)
            Timber.e(stringRequest.url)
        }

    private fun setLevels(levels: Levels?) {

        when (lastMonthIncome) {
            in levels?.get(0)!!.min_amount.toFloat()..levels[0].max_amount.toFloat() -> {
                level = 1
                progressLevel1!!.progress =
                    (((lastMonthIncome - levels[0].min_amount.toFloat()) / (levels[0].max_amount.toFloat() - levels[0].min_amount.toFloat())) * 100).toInt() + 1
                txtLevel1!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
                if (progressLevel1!!.progress < 4) {
                    progressLevel1!!.progress = 3
                }
                ivMedalBoronz!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_level1_active
                    )
                )
            }
            in levels[1].min_amount.toFloat()..levels[1].max_amount.toFloat() -> {
                level = 2
                progressLevel2!!.progress =
                    (((lastMonthIncome - levels[1].min_amount.toFloat()) / (levels[1].max_amount.toFloat() - levels[1].min_amount.toFloat())) * 100).toInt()
                if (progressLevel2!!.progress < 4) {
                    progressLevel2!!.progress = 3
                }
                progressLevel1!!.progress = 100
                txtLevel1!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
                txtLevel2!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
                ivMedalSilver!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_level2_active
                    )
                )
                ivMedalBoronz!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_level1_active
                    )
                )
            }
            in levels[2].min_amount.toFloat()..levels[2].max_amount.toFloat() -> {
                level = 3
                progressLevel1!!.progress = 100
                progressLevel2!!.progress = 100

                progressLevel3!!.progress =
                    (((lastMonthIncome - levels[2].min_amount.toFloat()) / (levels[2].max_amount.toFloat() - levels[2].min_amount.toFloat())) * 100).toInt()
                if (progressLevel3!!.progress < 4) {
                    progressLevel3!!.progress = 3
                }
                txtLevel1!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
                txtLevel2!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
                txtLevel3!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
                ivMedalGold!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_level3_active
                    )
                )
                ivMedalBoronz!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_level1_active
                    )
                )
            }
            else -> {
                level = 4
                progressLevel1!!.progress = 100
                progressLevel2!!.progress = 100
                progressLevel3!!.progress = 100
                progressLevel4!!.progress =
                    (((lastMonthIncome - levels[3].min_amount.toFloat()) / (levels[3].max_amount.toFloat() - levels[3].min_amount.toFloat())) * 100).toInt()
                if (progressLevel4!!.progress < 4) {
                    progressLevel4!!.progress = 3
                }
                txtLevel1!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
                txtLevel2!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
                txtLevel3!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
                txtLevel4!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
                ivMedalMax!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_level4_active
                    )
                )
                ivMedalBoronz!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_level1_active
                    )
                )
                ivMedalGold!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_level3_active
                    )
                )
                ivMedalSilver!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_level2_active
                    )
                )
            }
        }
    }

    private fun setJobStatus(accountStatus: AccountStatusModel) {
        if (accountStatus.isBank_account) {
            txtPaymentStatus!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
            imPaymentStatus!!.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_completed_status
                )
            )
        } else {
            txtPaymentStatus!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.N050))
            imPaymentStatus!!.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_not_completed_status
                )
            )
        }

        /*    if(accountStatus.isBasic_info && accountStatus.isBank_account)
                line1!!.background = (ContextCompat.getDrawable(requireContext(), R.color.G300))
            if(accountStatus.isSkills && accountStatus.isBank_account)
                line2!!.background = (ContextCompat.getDrawable(requireContext(), R.color.G300))
            if(accountStatus.isSkills && accountStatus.isJobalerts)
                line3!!.background = (ContextCompat.getDrawable(requireContext(), R.color.G300))
            */
        if (accountStatus.isBasic_info) {
            txtAccountStatus!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
            imAccountStatus!!.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_completed_status
                )
            )
            // line1!!.background = (ContextCompat.getDrawable(requireContext(), R.color.G300))
        } else {
            txtAccountStatus!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.N050))
            imAccountStatus!!.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_not_completed_status
                )
            )
            // line1!!.background = (ContextCompat.getDrawable(requireContext(), R.color.N040))
        }

        if (accountStatus.isSkills) {
            txtSkillsStatus!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
            imSkillsStatus!!.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_completed_status
                )
            )
            // line2!!.background = (ContextCompat.getDrawable(requireContext(), R.color.G300))
        } else {
            txtSkillsStatus!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.N050))
            imSkillsStatus!!.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_not_completed_status
                )
            )
            // line2!!.background = (ContextCompat.getDrawable(requireContext(), R.color.N040))
        }

        if (accountStatus.isJobalerts) {
            txtAlertStatus!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.N900))
            imAlertStatus!!.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_completed_status
                )
            )
            // line3!!.background = (ContextCompat.getDrawable(requireContext(), R.color.G300))
        } else {
            txtAlertStatus!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.N050))
            imAlertStatus!!.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_not_completed_status
                )
            )
            // line3!!.background = (ContextCompat.getDrawable(requireContext(), R.color.N040))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpAllEditFields(userAccountModel: UserAccountModel?) {
        if (userAccountModel == null) return
        tvAboutHeading!!.setTypeface(txtAbout!!.typeface, Typeface.BOLD_ITALIC)
        if (userAccountModel.mobileVerifiedAt != null) {
            ivCall!!.setBackgroundResource(R.drawable.bg_rounded_profile_badge_enable)
            ivCall2!!.setBackgroundResource(R.drawable.bg_rounded_profile_badge_enable)
        }
        if (userAccountModel.account_status != null && userAccountModel.account_status.isBadges) {
            ivCard!!.setBackgroundResource(R.drawable.bg_rounded_profile_badge_enable)
            ivCard2!!.setBackgroundResource(R.drawable.bg_rounded_profile_badge_enable)
        }
        if (userAccountModel.about == null || userAccountModel.about == "") {
            txtAbout!!.text = ""
            txtAbout!!.visibility = View.GONE
        } else {
            txtAbout!!.visibility = View.VISIBLE
            setMoreLess(txtAbout!!, userAccountModel.about, 5)
        }
        if (userAccountModel.tagline == null || userAccountModel.tagline == "") {
            tvAboutHeading!!.text = "\"\""
            tvAboutHeading!!.visibility = View.GONE
        } else {
            tvAboutHeading!!.visibility = View.VISIBLE
            tvAboutHeading!!.text = "\"" + userAccountModel.tagline + "\""
        }

        if (userAccountModel.workerRatings == null) {
            ratingbarAsTicker!!.visibility = View.GONE
            tvViewAllReviews!!.visibility = View.GONE
            lnTickerJobsuccess!!.visibility = View.GONE
            lytTicker.visibility = View.GONE
            tvTickerNoreview!!.visibility = View.VISIBLE
        } else {
            ratingbarAsTicker!!.visibility = View.VISIBLE
            tickerReview!!.visibility = View.VISIBLE
            lnTickerJobsuccess!!.visibility = View.VISIBLE
            tvTickerNoreview!!.visibility = View.GONE
            ratingbarAsTicker!!.rating = userAccountModel.workerRatings.avgRating
            tvTickerReview!!.text =
                "(" + userAccountModel.workerRatings.receivedReviews.toString() + ")"
            if (userAccountModel.workTaskStatistics != null) tvTickerCompletionRate!!.text =
                userAccountModel.workTaskStatistics.completionRate.toString() + "%"
            // worker
            if (userAccountModel.workerRatings != null) {
                tickerReviewNum.text =
                    userAccountModel.workerRatings.avgRating.toString().cleanRound()
                if (userAccountModel.workerRatings != null && userAccountModel.workerRatings.breakdownModel.get1() != null) {
                    progressBar1Star.progress =
                        userAccountModel.workerRatings.breakdownModel.get1()
                    txtReviewCount1Star.text =
                        "(" + userAccountModel.workerRatings.breakdownModel.get1()
                            .toString() + ")"
                } else {
                    progressBar1Star.progress = 0
                    txtReviewCount1Star.text = "(0)"
                }
                if (userAccountModel.workerRatings != null && userAccountModel.workerRatings.breakdownModel.get2() != null) {
                    progressBar2Star.progress =
                        userAccountModel.workerRatings.breakdownModel.get2()
                    txtReviewCount2Star.text =
                        "(" + userAccountModel.workerRatings.breakdownModel.get2()
                            .toString() + ")"
                } else {
                    progressBar2Star.progress = 0
                    txtReviewCount2Star.text = "(0)"
                }
                if (userAccountModel.workerRatings != null && userAccountModel.workerRatings.breakdownModel.get3() != null) {
                    progressBar3Star.progress =
                        userAccountModel.workerRatings.breakdownModel.get3()
                    txtReviewCount3Star.text =
                        "(" + userAccountModel.workerRatings.breakdownModel.get3()
                            .toString() + ")"
                } else {
                    progressBar3Star.progress = 0
                    txtReviewCount3Star.text = "(0)"
                }
                if (userAccountModel.workerRatings != null && userAccountModel.workerRatings.breakdownModel.get4() != null) {
                    progressBar4Star.progress =
                        userAccountModel.workerRatings.breakdownModel.get4()
                    txtReviewCount4Star.text =
                        "(" + userAccountModel.workerRatings.breakdownModel.get4()
                            .toString() + ")"
                } else {
                    progressBar4Star.progress = 0
                    txtReviewCount4Star.text = "(0)"
                }
                if (userAccountModel.workerRatings != null && userAccountModel.workerRatings.breakdownModel.get5() != null) {
                    progressBar5Star.progress =
                        userAccountModel.workerRatings.breakdownModel.get5()
                    txtReviewCount5Star.text =
                        "(" + userAccountModel.workerRatings.breakdownModel.get5()
                            .toString() + ")"
                } else {
                    progressBar5Star.progress = 0
                    txtReviewCount5Star.text = "(0)"
                }
            }
        }
        if (userAccountModel.posterRatings == null) {
            ratingbarAsPoster!!.visibility = View.GONE
            tvViewAllReviews!!.visibility = View.GONE
            lnPosterJobsuccess!!.visibility = View.GONE
            tvPosterNoreview!!.visibility = View.VISIBLE
            lytPoster.visibility = View.GONE
        } else {
            posterReview!!.visibility = View.VISIBLE
            ratingbarAsPoster!!.visibility = View.VISIBLE
            lnPosterJobsuccess!!.visibility = View.VISIBLE
            tvPosterNoreview!!.visibility = View.GONE
            ratingbarAsPoster!!.rating = userAccountModel.posterRatings.avgRating
            tvPosterReview!!.text =
                "(" + userAccountModel.posterRatings.receivedReviews.toString() + ")"
            if (userAccountModel.postTaskStatistics != null) tvPosterCompletionRate!!.text =
                userAccountModel.postTaskStatistics.completionRate.toString() + "%"
            // poster
            if (userAccountModel.posterRatings != null) {
                posterReviewNum.text =
                    userAccountModel.posterRatings.avgRating.toString().cleanRound()
                if (userAccountModel.posterRatings != null && userAccountModel.posterRatings.breakdownModel.get1() != null) {
                    progressBar1Starp.progress =
                        userAccountModel.posterRatings.breakdownModel.get1()
                    txtReviewCount1Starp.text =
                        "(" + userAccountModel.posterRatings.breakdownModel.get1()
                            .toString() + ")"
                } else {
                    progressBar1Starp.progress = 0
                    txtReviewCount1Starp.text = "(0)"
                }
                if (userAccountModel.posterRatings != null && userAccountModel.posterRatings.breakdownModel.get2() != null) {
                    progressBar2Starp.progress =
                        userAccountModel.posterRatings.breakdownModel.get2()
                    txtReviewCount2Starp.text =
                        "(" + userAccountModel.posterRatings.breakdownModel.get2()
                            .toString() + ")"
                } else {
                    progressBar2Starp.progress = 0
                    txtReviewCount2Starp.text = "(0)"
                }
                if (userAccountModel.posterRatings != null && userAccountModel.posterRatings.breakdownModel.get3() != null) {
                    progressBar3Starp.progress =
                        userAccountModel.posterRatings.breakdownModel.get3()
                    txtReviewCount3Starp.text =
                        "(" + userAccountModel.posterRatings.breakdownModel.get3()
                            .toString() + ")"
                } else {
                    progressBar3Starp.progress = 0
                    txtReviewCount3Starp.text = "(0)"
                }
                if (userAccountModel.posterRatings != null && userAccountModel.posterRatings.breakdownModel.get4() != null) {
                    progressBar4Starp.progress =
                        userAccountModel.posterRatings.breakdownModel.get4()
                    txtReviewCount4Starp.text =
                        "(" + userAccountModel.posterRatings.breakdownModel.get4()
                            .toString() + ")"
                } else {
                    progressBar4Starp.progress = 0
                    txtReviewCount4Starp.text = "(0)"
                }
                if (userAccountModel.posterRatings != null && userAccountModel.posterRatings.breakdownModel != null && userAccountModel.posterRatings.breakdownModel.get5() != null) {
                    progressBar5Starp.progress =
                        userAccountModel.posterRatings.breakdownModel.get5()
                    txtReviewCount5Starp.text =
                        "(" + userAccountModel.posterRatings.breakdownModel.get5()
                            .toString() + ")"
                } else {
                    progressBar5Starp.progress = 0
                    txtReviewCount5Starp.text = "(0)"
                }
            }
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
        if (userAccountModel.skills.skills == null && userAccountModel.skills.skills.size == 0 && userAccountModel.skills.language == null &&
            userAccountModel.skills.language.size == 0 &&
            userAccountModel.skills.education == null && userAccountModel.skills.education.size == 0
        ) {
            noPortfolio!!.visibility = View.GONE
            lSkill!!.visibility = View.GONE
            flAddSkill!!.visibility = View.GONE
            lytEducation!!.visibility = View.GONE
            lytSkills!!.visibility = View.GONE
            lytLanguage!!.visibility = View.GONE
            lPort!!.visibility = View.GONE
        } else {
            if (userAccountModel.skills.education != null && userAccountModel.skills.education.size != 0) {
                lytEducation!!.visibility = View.VISIBLE
                // tagEducation.setText(userAccountModel.getSkills().getEducation());
                tagEducation!!.tags = userAccountModel.skills.education
            } else {
                lytEducation!!.visibility = View.GONE
                tagEducation!!.tags = ArrayList()
            }
            if (userAccountModel.skills.skills != null && userAccountModel.skills.skills.size != 0) {
                lytSkills!!.visibility = View.VISIBLE
                tagSkills!!.tags = userAccountModel.skills.skills
            } else {
                lytSkills!!.visibility = View.GONE
                tagSkills!!.tags = ArrayList()
            }
            if (userAccountModel.skills.language != null && userAccountModel.skills.language.size != 0) {
                lytLanguage!!.visibility = View.VISIBLE
                tagLanguage!!.tags = userAccountModel.skills.language
            } else {
                lytLanguage!!.visibility = View.GONE
                tagLanguage!!.tags = ArrayList()
            }
        }
        tagEducation!!.tagTypeface = poppinsMedium
        tagLanguage!!.tagTypeface = poppinsMedium
        tagSkills!!.tagTypeface = poppinsMedium
        if (userAccountModel.avatar != null) {
            ImageUtil.displayImage(imgAvatar, userAccountModel.avatar.url, null)
        }
        txtFullName!!.text = userAccountModel.name
        txtSuburb!!.text = userAccountModel.location
        // txtAccountLevel.setText(""+userAccountModel.getWorkerTier().getName());
        txtLastSeen!!.text = "Last Seen " + userAccountModel.lastOnline
        tvViewAllReviews!!.setOnClickListener { v: View? ->
            val bundle = Bundle()
            bundle.putInt(Constant.userID, userAccountModel.id)
            bundle.putString("WhoIs", Constant.AS_A_WORKER)
            bundle.putInt(Constant.Level, level)
            ReviewsActivity.userAccountModel = null
            //      bundle.putParcelable(Constant.userAccount, userAccountModel);
            startActivity(
                Intent(dashboardActivity, ReviewsActivity::class.java)
                    .putExtras(bundle)
            )
        }
        onChangeTabBiography()
        onChangeTabUser()
    }

    override fun updatedSuccesfully(path: String) {
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
