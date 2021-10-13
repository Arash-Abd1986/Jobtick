package com.jobtick.android.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import android.widget.*
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
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.activities.*
import com.jobtick.android.adapers.AttachmentAdapter
import com.jobtick.android.adapers.BadgesAdapter
import com.jobtick.android.interfaces.onProfileUpdateListener
import com.jobtick.android.models.AttachmentModel
import com.jobtick.android.models.BadgesModel
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.ImageUtil
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.Tools
import com.jobtick.android.widget.SpacingItemDecoration
import com.mikhaellopez.circularimageview.CircularImageView
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment(), onProfileUpdateListener, AttachmentAdapter.OnItemClickListener {
    private var recyclerViewPortfolio: RecyclerView? = null
    private var imgAvatar: CircularImageView? = null
    private var ivCall: ImageView? = null
    private var ivCard: ImageView? = null
    private var txtAbout: TextView? = null
    private var tvAboutHeading: TextView? = null
    private var tvAboutMeHeader: TextView? = null
    private var imgVerified: ImageView? = null
    private var tagEducation: TagContainerLayout? = null
    private var lytEducation: LinearLayout? = null
    private var tagSpecialities: TagContainerLayout? = null
    private var lytSpecialities: LinearLayout? = null
    private var tagLanguage: TagContainerLayout? = null
    private var lytLanguage: LinearLayout? = null
    private var tagExperience: TagContainerLayout? = null
    private var lytExperience: LinearLayout? = null
    private var tagTransportation: TagContainerLayout? = null
    private var lytTransportation: LinearLayout? = null
    private var txtFullName: TextView? = null
    private var txtSuburb: TextView? = null
    private var txtLastSeen: TextView? = null
    private var tvViewAllReviews: TextView? = null
    private var tvSkills: TextView? = null
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
    private var ivMedalSilver: ImageView? = null
    private var ivMedalGOld: ImageView? = null
    private var ivMedalMax: ImageView? = null
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
    private var noPortfolio: LinearLayout? = null
    private var ivLevelInfo: ImageView? = null
    private var ivProfileInfo: ImageView? = null
    private var noReview: LinearLayout? = null
    private var tickerReview: LinearLayout? = null
    private var posterReview: LinearLayout? = null
    private var noSkill: LinearLayout? = null
    private var txtNoReview: TextView? = null
    private var addSkill: TextView? = null
    private var addPortFilo: TextView? = null
    override fun onResume() {
        super.onResume()
        allProfileData
    }

    @SuppressLint("SetTextI18n", "RtlHardcoded")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        onProfileupdatelistener = this
        return view
    }
    private fun setIDs() {
        noPortfolio = requireView().findViewById(R.id.no_port_folio)
        lPort = requireView().findViewById(R.id.lyt_Port)
        lSkill = requireView().findViewById(R.id.lyt_skills)
        noSkill = requireView().findViewById(R.id.no_port_skill)
        addPortFilo = requireView().findViewById(R.id.txt_add_portfolio)
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
        ivCard = requireView().findViewById(R.id.ivCard)
        tvAboutHeading = requireView().findViewById(R.id.tvAboutHeading)
        imgVerified = requireView().findViewById(R.id.img_verified)
        tagEducation = requireView().findViewById(R.id.tag_education)
        lytEducation = requireView().findViewById(R.id.lyt_education)
        tagSpecialities = requireView().findViewById(R.id.tag_specialities)
        lytSpecialities = requireView().findViewById(R.id.lyt_specialities)
        tagLanguage = requireView().findViewById(R.id.tag_language)
        lytLanguage = requireView().findViewById(R.id.lyt_language)
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
        ivMedalBoronz = requireView().findViewById(R.id.ivMedalBoronz)
        ivMedalSilver = requireView().findViewById(R.id.ivMedalSilver)
        ivMedalGOld = requireView().findViewById(R.id.ivMedalGOld)
        ivMedalMax = requireView().findViewById(R.id.ivMedalMax)
    }

    private fun initToolbar() {
        dashboardActivity = requireActivity() as DashboardActivity
        poppinsMedium = ResourcesCompat.getFont(requireContext(), R.font.roboto_medium)
        onProfileupdatelistener = this
        if (dashboardActivity != null) {
            toolbar = dashboardActivity!!.findViewById(R.id.toolbar)
            toolbar!!.menu.clear()
            toolbar!!.inflateMenu(R.menu.menu_profile)
            val ivNotification = dashboardActivity!!.findViewById<ImageView>(R.id.ivNotification)
            ivNotification.visibility = View.GONE
            val toolbarTitle = dashboardActivity!!.findViewById<TextView>(R.id.toolbar_title)
            toolbarTitle.visibility = View.VISIBLE
            toolbarTitle.setText(R.string.profile)
            toolbarTitle.typeface = ResourcesCompat.getFont(requireContext(), R.font.roboto_semi_bold)
            toolbar!!.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey_100))
            val params = Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT)
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
        initToolbar()
        sessionManager = SessionManager(dashboardActivity)
        userAccountModel = UserAccountModel()
        attachmentArrayList = ArrayList()
        badgesModelArrayList = ArrayList()
        toolbar!!.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_flag -> {
                }
                R.id.action_edit -> startActivity(Intent(dashboardActivity, EditProfileActivity::class.java))
            }
            false
        }
        init()
        allProfileData
        initComponent()
    }

    private fun initComponent() {
        rbPortfollio!!.setOnCheckedChangeListener { group: CompoundButton?, checkedId: Boolean -> onChangeTabBiography() }
        rbSkills!!.setOnCheckedChangeListener { group: CompoundButton?, checkedId: Boolean -> onChangeTabBiography() }
    }

    @SuppressLint("SetTextI18n")
    private fun onChangeTabUser() {
        if (userAccountModel!!.postTaskStatistics != null && userAccountModel!!.postTaskStatistics.completionRate != null) {
            tvTickerCompletionRate!!.text = userAccountModel!!.postTaskStatistics.completionRate.toString() + "%"
        }
    }

    private fun onChangeTabBiography() {
        if (context == null) return
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
                    (requireActivity() as ActivityBase).showToast("MAX 10 picture", requireContext())
                }
                recyclerViewPortfolio!!.visibility = View.VISIBLE
                noSkill!!.visibility = View.GONE
                noPortfolio!!.visibility = View.GONE
                lPort!!.visibility = View.VISIBLE
            }
            lSkill!!.visibility = View.GONE
            rbPortfollio!!.setTextColor(resources.getColor(R.color.blue))
            rbSkills!!.setTextColor(resources.getColor(R.color.textColor))
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
            rbPortfollio!!.setTextColor(resources.getColor(R.color.textColor))
            rbSkills!!.setTextColor(resources.getColor(R.color.blue))
        }
    }

    private fun init() {
        recyclerViewPortfolio!!.layoutManager = GridLayoutManager(dashboardActivity, 3)
        recyclerViewPortfolio!!.addItemDecoration(SpacingItemDecoration(3, Tools.dpToPx(dashboardActivity, 3), true))
        recyclerViewPortfolio!!.setHasFixedSize(true)
        adapter = AttachmentAdapter(attachmentArrayList, false, activity)
        recyclerViewPortfolio!!.adapter = adapter
        adapter!!.setOnItemClickListener(this)
        badgesAdapter = BadgesAdapter(badgesModelArrayList)
    }

    // map1.put("X-Requested-With", "XMLHttpRequest");
    private val allProfileData: Unit
        get() {
            pbLoading!!.visibility = View.VISIBLE
            content!!.visibility = View.GONE
            val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.URL_PROFILE + "/" + sessionManager!!.userAccount.id,
                    Response.Listener { response: String? ->
                        Timber.e(response)
                        content!!.visibility = View.VISIBLE
                        pbLoading!!.visibility = View.GONE
                        btnQuote!!.visibility = View.GONE
                        try {
                            val jsonObject = JSONObject(response!!)
                            Timber.e(jsonObject.toString())
                            if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                userAccountModel = UserAccountModel().getJsonToModel(jsonObject.getJSONObject("data"))
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
                                dashboardActivity!!.showToast("Connection error", dashboardActivity)
                            }
                        } catch (e: JSONException) {
                            dashboardActivity!!.showToast("JSONException", dashboardActivity)
                            Timber.e(e.toString())
                            e.printStackTrace()
                        }
                    },
                    Response.ErrorListener { error: VolleyError -> dashboardActivity!!.errorHandle1(error.networkResponse) }) {
                override fun getHeaders(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["authorization"] = sessionManager!!.tokenType + " " + sessionManager!!.accessToken
                    map1["Content-Type"] = "application/x-www-form-urlencoded"
                    map1["Version"] = BuildConfig.VERSION_CODE.toString()
                    // map1.put("X-Requested-With", "XMLHttpRequest");
                    return map1
                }
            }
            stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            val requestQueue = Volley.newRequestQueue(dashboardActivity)
            requestQueue.add(stringRequest)
            Timber.e(stringRequest.url)
        }

    @SuppressLint("SetTextI18n")
    private fun setUpAllEditFields(userAccountModel: UserAccountModel?) {
        if (userAccountModel == null) return
        tvAboutHeading!!.setTypeface(txtAbout!!.typeface, Typeface.BOLD_ITALIC)
        if (userAccountModel.mobileVerifiedAt != null) {
            ivCall!!.setBackgroundResource(R.drawable.bg_rounded_profile_badge_enable)
        }
        if (userAccountModel.account_status != null && userAccountModel.account_status.isBadges) {
            ivCard!!.setBackgroundResource(R.drawable.bg_rounded_profile_badge_enable)
        }
        if (userAccountModel.about == null || userAccountModel.about == "") {
            txtAbout!!.text = ""
            txtAbout!!.visibility = View.GONE
        } else {
            txtAbout!!.visibility = View.VISIBLE
            txtAbout!!.text = "" + userAccountModel.about
        }
        if (userAccountModel.tagline == null || userAccountModel.tagline == "") {
            tvAboutHeading!!.text = "\"\""
            tvAboutHeading!!.visibility = View.GONE
        } else {
            tvAboutHeading!!.visibility = View.VISIBLE
            tvAboutHeading!!.text = "\"" + userAccountModel.tagline + "\""
        }
        if (userAccountModel.workerRatings == null) {
            tickerReview!!.visibility = View.GONE
            noReview!!.visibility = View.VISIBLE
            txtNoReview!!.visibility = View.VISIBLE
        } else {
            noReview!!.visibility = View.GONE
            tickerReview!!.visibility = View.VISIBLE
            ratingbarAsTicker!!.rating = userAccountModel.workerRatings.avgRating
            tvTickerReview!!.text = "(" + userAccountModel.workerRatings.receivedReviews.toString() + ")"
            if (userAccountModel.workTaskStatistics != null) tvTickerCompletionRate!!.text = userAccountModel.workTaskStatistics.completionRate.toString() + "%"
        }
        if (userAccountModel.posterRatings == null) {
            posterReview!!.visibility = View.GONE
            noReview!!.visibility = View.VISIBLE
            txtNoReview!!.visibility = View.VISIBLE
        } else {
            posterReview!!.visibility = View.VISIBLE
            noReview!!.visibility = View.GONE
            ratingbarAsPoster!!.rating = userAccountModel.posterRatings.avgRating
            tvPosterReview!!.text = "(" + userAccountModel.posterRatings.receivedReviews.toString() + ")"
            if (userAccountModel.postTaskStatistics != null) tvPosterCompletionRate!!.text = userAccountModel.postTaskStatistics.completionRate.toString() + "%"
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
            ImageUtil.displayImage(imgAvatar, userAccountModel.avatar.url, null)
        }
        txtFullName!!.text = userAccountModel.name
        txtSuburb!!.text = userAccountModel.location
        //txtAccountLevel.setText(""+userAccountModel.getWorkerTier().getName());
        txtLastSeen!!.text = "Last Seen " + userAccountModel.lastOnline
        tvViewAllReviews!!.setOnClickListener { v: View? ->
            val bundle = Bundle()
            bundle.putInt(Constant.userID, userAccountModel.id)
            bundle.putString("WhoIs", Constant.AS_A_WORKER)
            ReviewsActivity.userAccountModel = null
            //      bundle.putParcelable(Constant.userAccount, userAccountModel);
            startActivity(Intent(dashboardActivity, ReviewsActivity::class.java)
                    .putExtras(bundle))
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