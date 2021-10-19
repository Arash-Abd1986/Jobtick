package com.jobtick.android.activities

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.Spanned
import android.view.*
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.android.volley.DefaultRetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.adapers.*
import com.jobtick.android.cancellations.*
import com.jobtick.android.fragments.*
import com.jobtick.android.fragments.RescheduleNoticeBottomSheetState.Companion.newInstance
import com.jobtick.android.fragments.TickerRequirementsBottomSheet.Companion.newInstance
import com.jobtick.android.fragments.WithdrawBottomSheet.Withdraw
import com.jobtick.android.interfaces.OnRequestAcceptListener
import com.jobtick.android.interfaces.OnWidthDrawListener
import com.jobtick.android.models.*
import com.jobtick.android.models.response.conversationinfo.GetConversationInfoResponse
import com.jobtick.android.utils.*
import com.jobtick.android.widget.ExtendedAlertBox
import com.jobtick.android.widget.ExtendedAlertBox.OnExtendedAlertButtonClickListener
import com.jobtick.android.widget.SpacingItemDecoration
import com.jobtick.android.widget.StatusLayout
import com.mikhaellopez.circularimageview.CircularImageView
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.Consumer

class TaskDetailsActivity : ActivityBase(), OfferListAdapter.OnItemClickListener, Withdraw, QuestionListAdapter.OnItemClickListener,
        QuestionAttachmentAdapter.OnItemClickListener, OnRequestAcceptListener, OnWidthDrawListener, OnExtendedAlertButtonClickListener,
        RescheduleNoticeBottomSheetState.NoticeListener, IncreaseBudgetBottomSheet.NoticeListener, IncreaseBudgetNoticeBottomSheet.NoticeListener,
        IncreaseBudgetDeclineBottomSheet.NoticeListener, ConfirmAskToReleaseBottomSheet.NoticeListener, ConfirmReleaseBottomSheet.NoticeListener,
        TickerRequirementsBottomSheet.NoticeListener {
    private lateinit var alertBox: ExtendedAlertBox
    private lateinit var toolbar: Toolbar
    private lateinit var collapsingToolbar: CollapsingToolbarLayout
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var txtCompletionRate: TextView
    private lateinit var lytStatus: StatusLayout
    private lateinit var txtDueDate: TextView
    private lateinit var txtRatingValue: TextView
    private lateinit var txtDueTime: TextView
    private lateinit var txtCreatedDate: TextView
    private lateinit var txtLocation: TextView
    private lateinit var imgMapPin: ImageView
    private lateinit var txtDescription: TextView
    private lateinit var txtOfferCount: TextView
    private lateinit var firstOffer: TextView
    private lateinit var firstOfferLyt: LinearLayout
    private lateinit var txtBudget: TextView
    private lateinit var lytBtnMessage: LinearLayout
    private lateinit var imgPChat: AppCompatImageView
    private lateinit var llLocation: LinearLayout
    private lateinit var postedByLyt: RelativeLayout
    private lateinit var lytBtnMakeAnOffer: LinearLayout
    private lateinit var cardMakeAnOffer: CardView
    private lateinit var recyclerViewOffers: RecyclerView
    private lateinit var nestedScrollView: NestedScrollView
    private lateinit var txtBtnText: TextView
    private lateinit var txtOffersCount: TextView
    private lateinit var lytBtnViewAllOffers: LinearLayout
    private lateinit var cardViewAllOffers: RelativeLayout
    private lateinit var cardOfferLayout: CardView
    private lateinit var txtTitle: TextView
    private lateinit var txtWaitingForOffer: FrameLayout
    private lateinit var imgAvtarWorker: CircularImageView
    private lateinit var txtWorkerName: TextView
    private lateinit var ratingbar_worker: ImageView
    private lateinit var lineOpenState: View
    private lateinit var cardAssigneeLayout: RelativeLayout
    private lateinit var imgAvtarPoster: CircularImageView
    private lateinit var txtPosterName: TextView
    private lateinit var txtPosterLocation: TextView
    private lateinit var txtPosterLastOnline: TextView
    private lateinit var adapterImageSlider: AdapterImageSlider
    private lateinit var viewPager: ViewPager
    private lateinit var layoutDots: LinearLayout
    private lateinit var budget: TextView
    private lateinit var liAssign: LinearLayout
    private lateinit var linearUserProfile: LinearLayout
    private lateinit var mustHaveLyt: CardView
    private lateinit var mustHaveList: RecyclerView
    private lateinit var bottom_sheet: FrameLayout
    private lateinit var content: LinearLayout
    private lateinit var lnOffers: LinearLayout
    private lateinit var llLoading: RelativeLayout
    private lateinit var tvPrivateChatName: TextView
    private lateinit var tvPrivateChatLocation: TextView
    private lateinit var tvPrivateChatLastOnline: TextView
    private lateinit var imgAvatarChat: CircularImageView
    private lateinit var llBtnPrivateMessageDisable: LinearLayout
    private lateinit var llBtnPrivateMessageEnable: LinearLayout
    private lateinit var llBtnPosterMessageEnable: LinearLayout
    private lateinit var relPrivateChat: RelativeLayout
    private lateinit var txtAskQuestion: TextView
    private var isFav = true
    private var offerListS = ArrayList<OfferModel>()
    private var offerListF = ArrayList<OfferModel>()
    lateinit var userAccountModel: UserAccountModel
    lateinit var bankAccountModel: BankAccountModel
    lateinit var billingAdreessModel: BillingAdreessModel
    private val dataList = ArrayList<AttachmentModel>()
    private val attachmentArrayListQuestion = ArrayList<AttachmentModel>()
    private var str_slug: String? = ""
    private var isUserThePoster = false
    private var isUserTheTicker = false
    var isToolbarCollapsed = false
    private lateinit var offerListAdapter: OfferListAdapter
    private var noActionAvailable = false
    private lateinit var adapter: QuestionAttachmentAdapter
    var pushOfferID = 0
    var pushQuestionID = 0
    private var mBottomSheetDialog: BottomSheetDialog? = null
    private var mBehavior: BottomSheetBehavior<*>? = null
    private val requirementState = HashMap<TickerRequirementsBottomSheet.Requirement?, Boolean?>()
    private var alertType: AlertType? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        val w = window
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_details)
        setIDs()
        onViewClick()
        alertBox.onExtendedAlertButtonClickListener = this
        BottomSheetBehavior.from<FrameLayout?>(bottom_sheet).also { mBehavior = it }
        requestAcceptListener = this
        widthDrawListener = this
        userAccountModel = SessionManager(this).userAccount
        initialStage()
    }

    private fun showQuestions() {
        val questionsBottomSheet = QuestionsBottomSheet(sessionManager, str_slug!!, taskModel!!, isUserThePoster, pushQuestionID)
        questionsBottomSheet.show(supportFragmentManager, null)
    }

    private fun setIDs() {
        relPrivateChat = findViewById(R.id.relPrivateChat)
        tvPrivateChatName = findViewById(R.id.tvPrivateChatName)
        tvPrivateChatLocation = findViewById(R.id.tvPrivateChatLocation)
        tvPrivateChatLastOnline = findViewById(R.id.tvPrivateChatLastOnline)
        txtAskQuestion = findViewById(R.id.txt_ask_question)
        imgAvatarChat = findViewById(R.id.imgAvatarChat)
        llBtnPrivateMessageDisable = findViewById(R.id.llBtnPrivateMessageDisable)
        llBtnPrivateMessageEnable = findViewById(R.id.llBtnPrivateMessageEnable)
        llBtnPosterMessageEnable = findViewById(R.id.llBtnPosterMessageEnable)
        llLoading = findViewById(R.id.llLoading)
        lnOffers = findViewById(R.id.ln_offers)
        content = findViewById(R.id.content)
        alertBox = findViewById(R.id.alert_box)
        toolbar = findViewById(R.id.toolbar)
        collapsingToolbar = findViewById(R.id.collapsing_toolbar)
        appBarLayout = findViewById(R.id.app_bar_layout)
        txtCompletionRate = findViewById(R.id.txt_completion_rate)
        lytStatus = findViewById(R.id.lyt_status)
        txtDueDate = findViewById(R.id.txt_due_date)
        txtRatingValue = findViewById(R.id.txt_rating_value)
        txtDueTime = findViewById(R.id.txt_due_time)
        txtCreatedDate = findViewById(R.id.txt_created_date)
        txtLocation = findViewById(R.id.txt_location)
        imgMapPin = findViewById(R.id.img_map_pin)
        txtDescription = findViewById(R.id.txt_description)
        txtOfferCount = findViewById(R.id.txt_offer_count)
        firstOffer = findViewById(R.id.first_offer)
        firstOfferLyt = findViewById(R.id.first_offer_lyt)
        txtBudget = findViewById(R.id.txt_budget)
        lytBtnMessage = findViewById(R.id.lyt_btn_message)
        imgPChat = findViewById(R.id.img_pChat)
        llLocation = findViewById(R.id.llLocation)
        postedByLyt = findViewById(R.id.postedByLyt)
        lytBtnMakeAnOffer = findViewById(R.id.lyt_btn_make_an_offer)
        cardMakeAnOffer = findViewById(R.id.card_make_an_offer)
        recyclerViewOffers = findViewById(R.id.recycler_view_offers)
        nestedScrollView = findViewById(R.id.nested_scroll_view)
        txtBtnText = findViewById(R.id.txt_btn_text)
        txtOffersCount = findViewById(R.id.txt_offers_count)
        lytBtnViewAllOffers = findViewById(R.id.lyt_btn_view_all_offers)
        cardViewAllOffers = findViewById(R.id.card_view_all_offers)
        cardOfferLayout = findViewById(R.id.card_offer_layout)
        txtTitle = findViewById(R.id.txt_title)
        txtWaitingForOffer = findViewById(R.id.txt_waiting_for_offer)
        imgAvtarWorker = findViewById(R.id.img_avtar_worker)
        txtWorkerName = findViewById(R.id.txt_worker_name)
        ratingbar_worker = findViewById(R.id.ratingbar_worker)
        lineOpenState = findViewById(R.id.lineOpen)
        cardAssigneeLayout = findViewById(R.id.card_assignee_layout)
        imgAvtarPoster = findViewById(R.id.img_avtar_poster)
        txtPosterName = findViewById(R.id.txt_poster_name)
        txtPosterLocation = findViewById(R.id.txt_poster_location)
        txtPosterLastOnline = findViewById(R.id.txt_poster_last_online)
        viewPager = findViewById(R.id.pager)
        layoutDots = findViewById(R.id.layout_dots)
        budget = findViewById(R.id.txt_budgets)
        liAssign = findViewById(R.id.liAssign)
        linearUserProfile = findViewById(R.id.linearUserProfile)
        mustHaveLyt = findViewById(R.id.mustHaveLyt)
        mustHaveList = findViewById(R.id.mustHaveList)
        bottom_sheet = findViewById(R.id.bottom_sheet)
    }

    override fun getExtras() {
        super.getExtras()
        if (intent == null || intent.extras == null) {
            return
        }
        val bundle = intent.extras
        if (bundle!!.getString(ConstantKey.SLUG) != null) {
            str_slug = bundle.getString(ConstantKey.SLUG)
        }
        if (bundle.getInt(ConstantKey.PUSH_OFFER_ID) != 0) {
            pushOfferID = bundle.getInt(ConstantKey.PUSH_OFFER_ID)
        }
        if (bundle.getInt(ConstantKey.PUSH_QUESTION_ID) != 0) {
            pushQuestionID = bundle.getInt(ConstantKey.PUSH_QUESTION_ID)
        }
    }

    private fun initialStage() {
        recyclerViewOffers.visibility = View.GONE
        cardViewAllOffers.visibility = View.GONE
        initToolbar()
        getData
    }

    private fun initOfferList() {
        recyclerViewOffers.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this@TaskDetailsActivity)
        recyclerViewOffers.layoutManager = layoutManager
        offerListAdapter = OfferListAdapter(this@TaskDetailsActivity, isUserThePoster, ArrayList())
        recyclerViewOffers.adapter = offerListAdapter
        offerListAdapter.setOnItemClickListener(this)
    }

    @SuppressLint("SetTextI18n", "NonConstantResourceId")
    private fun initStatusTask(status: String) {
        when (status) {
            Constant.TASK_ASSIGNED -> {
                lytStatus.setStatus(getString(R.string.assigned))

                cardMakeAnOffer.backgroundTintList = ContextCompat.getColorStateList(this@TaskDetailsActivity,
                        R.color.colorTaskAssigned)
                txtOffersCount.visibility = View.GONE
                //                txtStatusOpen.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_tab_primary_2dp));
                if (isUserThePoster) {
                    cardMakeAnOffer.visibility = View.GONE
                    txtBtnText.text = ConstantKey.BTN_ASSIGNED
                    toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_cancellation, true)
                    toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_report, false)
                    toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_increase_budget, true)
                    toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_reschedule, true)

//                    cardPrivateChat.setVisibility(View.VISIBLE);
                    //Cancellation
                } else {
                    lineOpenState.visibility = View.GONE

                    // worker task
                    if (noActionAvailable) {
                        cardMakeAnOffer.visibility = View.GONE
                        toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_report, false)
                        toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_cancellation, false)
                        toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_increase_budget, false)
                        toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_reschedule, false)
                    } else {
                        cardMakeAnOffer.visibility = View.VISIBLE
                        txtBtnText.text = ConstantKey.BTN_ASK_TO_RELEASE_MONEY
                        toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_copy, true)
                        toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_cancellation, true)
                        toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_increase_budget, true)

                    }
                }
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_edit, false)
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_delete, false)
                cardOfferLayout.visibility = View.GONE
                //                cardMessage.setVisibility(View.VISIBLE);
                cardAssigneeLayout.visibility = View.VISIBLE
                //rltQuestionAdd.visibility = View.GONE
                //cardQuestionsLayout.visibility = View.VISIBLE
                setPrice()
            }
            Constant.TASK_OPEN -> {
                lytStatus.setStatus(getString(R.string.open))
                lineOpenState.visibility = View.GONE
                cardMakeAnOffer.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
                cardMakeAnOffer.backgroundTintList = ContextCompat.getColorStateList(this@TaskDetailsActivity,
                        R.color.colorPrimary)
                if (isUserThePoster) {
                    //poster task
                    toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_edit, taskModel!! == null || taskModel!!.offers.size == 0)
                    toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_delete, true)
                    toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_report, false)
                    cardMakeAnOffer.visibility = View.GONE
                } else {
                    //worker task
                    toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_edit, false)
                    toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_delete, false)
                    toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_report, true)
                    if (taskModel!!.offerSent) {
                        cardMakeAnOffer.visibility = View.GONE
                        txtBtnText.text = ConstantKey.BTN_OFFER_PENDING
                    } else {
                        cardMakeAnOffer.visibility = View.VISIBLE
                        txtBtnText.text = ConstantKey.BTN_MAKE_AN_OFFER
                    }
                }
                //toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_report, false);
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_cancellation, false)
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_reschedule, false)
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_increase_budget, false)
                //                cardMessage.setVisibility(View.VISIBLE);
                cardAssigneeLayout.visibility = View.GONE
                //                cardPrivateChat.setVisibility(View.GONE);
                if (taskModel!!.offers.size != 0) {
                    recyclerViewOffers.visibility = View.VISIBLE
                    offerListAdapter.clear()
                    if (taskModel!!.offers.size > 0) {
                        var i = 0
                        while (i < taskModel!!.offers.size) {
                            if (taskModel!!.offers[i].worker.id == sessionManager.userAccount.id) {
                                val item = taskModel!!.offers[i]
                                taskModel!!.offers.removeAt(i)
                                taskModel!!.offers.add(0, item)
                                break
                            }
                            i++
                        }
                    }
                    if (taskModel!!.offers.size > 5) {
                        offerListS = ArrayList()
                        offerListF = ArrayList()
                        offerListF.addAll(taskModel!!.offers)
                        offerListS.addAll(taskModel!!.offers.subList(0, 2))
                        offerListAdapter.addItems(offerListS)
                    } else {
                        offerListAdapter.addItems(taskModel!!.offers)
                    }
                    var i = 0
                    while (i < taskModel!!.offers.size) {
                        if (taskModel!!.offers[i].id == pushOfferID) {
                            onItemOfferClick(null, taskModel!!.offers[i], i, "reply")
                            break
                        }
                        i++
                    }
                } else {
                    recyclerViewOffers.visibility = View.GONE
                }
                cardOfferLayout.visibility = View.VISIBLE
                //cardQuestionsLayout.visibility = View.VISIBLE
                setPrice()
            }
            Constant.TASK_CANCELLED -> {
                lytStatus.setStatus(getString(R.string.cancelled))


                cardMakeAnOffer.visibility = View.GONE
                lineOpenState.visibility = View.GONE
                cardMakeAnOffer.backgroundTintList = ContextCompat.getColorStateList(this@TaskDetailsActivity,
                        R.color.colorPrimary)
                if (taskModel!!.worker != null) cardAssigneeLayout.visibility = View.VISIBLE else {
                    cardAssigneeLayout.visibility = View.GONE
                }
                cardOfferLayout.visibility = View.GONE
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_edit, false)
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_delete, false)
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_cancellation, false)
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_increase_budget, false)
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_reschedule, false)
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_report, false)
                cardAssigneeLayout.visibility = View.VISIBLE
                //rltQuestionAdd.visibility = View.GONE
                //cardQuestionsLayout.visibility = View.VISIBLE
                setPrice()
            }
            Constant.TASK_OVERDUE, Constant.TASK_COMPLETED -> {

                lineOpenState.visibility = View.VISIBLE
                txtOffersCount.visibility = View.GONE
                cardMakeAnOffer.backgroundTintList = ContextCompat.getColorStateList(this@TaskDetailsActivity,
                        R.color.colorPrimary)
                if (isUserThePoster) {
                    if (status == Constant.TASK_OVERDUE)
                        lytStatus.setStatus(getString(R.string.overdue))
                    else lytStatus.setStatus(getString(R.string.completed))


                    // poster task
                    if (noActionAvailable) {
                        cardMakeAnOffer.visibility = View.GONE
                    } else {
                        cardMakeAnOffer.visibility = View.GONE
                        txtBtnText.text = ConstantKey.BTN_RELEASE_MONEY
                    }
                    toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_report, false)
                } else {
                    lytStatus.setStatus(getString(R.string.completed))
                    // worker task
                    if (noActionAvailable) {
                        cardMakeAnOffer.visibility = View.GONE
                        toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_report, false)
                    } else {
                        cardMakeAnOffer.visibility = View.GONE
                        txtBtnText.text = ConstantKey.BTN_WAIT_TO_RELEASE_MONEY
                        toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_report, false)
                    }
                }
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_edit, false)
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_delete, false)
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_increase_budget, false)
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_reschedule, false)
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_cancellation, false)

//                cardMessage.setVisibility(View.VISIBLE);
                cardAssigneeLayout.visibility = View.VISIBLE
                //                cardPrivateChat.setVisibility(View.VISIBLE);
                cardOfferLayout.visibility = View.GONE
                //rltQuestionAdd.visibility = View.GONE
                //cardQuestionsLayout.visibility = View.VISIBLE
                setPrice()
            }
            Constant.TASK_DRAFT -> {
            }
            Constant.TASK_CLOSED -> {
                lytStatus.setStatus(getString(R.string.completed))

                lineOpenState.visibility = View.VISIBLE
                txtOffersCount.visibility = View.GONE
                if (isUserThePoster) {
                    cardMakeAnOffer.visibility = View.GONE
                    txtBtnText.text = ConstantKey.BTN_WRITE_A_REVIEW
                    toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_report, false)
                } else {
                    // worker task
                    if (noActionAvailable) {
                        cardMakeAnOffer.visibility = View.GONE
                        toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_report, false)
                    } else {
                        cardMakeAnOffer.visibility = View.GONE
                        txtBtnText.text = ConstantKey.BTN_WRITE_A_REVIEW
                        toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_report, false)
                    }
                }
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_edit, false)
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_delete, false)
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_cancellation, false)
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_increase_budget, false)
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_reschedule, false)


//                cardMessage.setVisibility(View.VISIBLE);
                cardAssigneeLayout.visibility = View.VISIBLE
                //                cardPrivateChat.setVisibility(View.VISIBLE);
                cardOfferLayout.visibility = View.GONE
                cardAssigneeLayout.visibility = View.VISIBLE
                //rltQuestionAdd.visibility = View.GONE
                //cardQuestionsLayout.visibility = View.VISIBLE
                setPrice()
            }
        }
        handleOverDueStatus(status)
        initCancelled()
        initCancellation()
        initJobReceipt()
        initIncreaseBudget()
        initRescheduleTime()
        initReview()
        initReleaseMoney()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            toolbar.menu.setGroupDividerEnabled(true)
        }
        toolbar.setNavigationOnClickListener { v: View? -> onBackPressed() }
        toolbar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_share -> Helper.shareTask(this@TaskDetailsActivity,
                        """Hey ! Checkout this task. 
 https://${Constant.SHARE_APPEND_TXT}jobtick.com/explore-jobs/${taskModel!!.slug}""")
                R.id.menu_bookmark -> if (taskModel!!.bookmarkID != null) {
                    try {
                        if (isFav) {
                            isFav = false
                            removeBookmark()
                            Handler().postDelayed({ isFav = true }, 3000)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    try {
                        if (isFav) {
                            isFav = false
                            addToBookmark()
                            Handler().postDelayed({ isFav = true }, 3000)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                R.id.action_report -> {
                    val bundleReport = Bundle()
                    val intentReport = Intent(this@TaskDetailsActivity, ReportActivity::class.java)
                    bundleReport.putString(ConstantKey.SLUG, taskModel!!.slug)
                    bundleReport.putString("key", ConstantKey.KEY_TASK_REPORT)
                    intentReport.putExtras(bundleReport)
                    startActivity(intentReport)
                }
                R.id.action_edit -> MaterialAlertDialogBuilder(this@TaskDetailsActivity)
                        .setTitle(resources.getString(R.string.title_edit))
                        .setNegativeButton(resources.getString(R.string.no)) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
                        .setPositiveButton(resources.getString(R.string.yes)) { dialog: DialogInterface, which: Int ->
                            dialog.dismiss()
                            EditTask(taskModel!!)
                        }.show()
                R.id.action_delete -> MaterialAlertDialogBuilder(this@TaskDetailsActivity)
                        .setTitle(resources.getString(R.string.title_delete))
                        .setNegativeButton(resources.getString(R.string.no)) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
                        .setPositiveButton(resources.getString(R.string.yes)) { dialog: DialogInterface, which: Int ->
                            dialog.dismiss()
                            deleteTaskPermanent(taskModel!!.slug)
                        }.show()
                R.id.action_copy -> copyTask(taskModel!!)
                R.id.action_cancellation -> {
                    val intent: Intent = if (isUserThePoster) {
                        Intent(this@TaskDetailsActivity, CancellationPosterActivity::class.java)
                    } else {
                        Intent(this@TaskDetailsActivity, CancellationWorkerActivity::class.java)
                    }
                    val bundle: Bundle = Bundle()
                    bundle.putString(ConstantKey.SLUG, taskModel!!.slug)
                    intent.putExtras(bundle)
                    startActivityForResult(intent, ConstantKey.RESULTCODE_CANCELLATION)
                }
                R.id.action_increase_budget -> if (isUserThePoster) {
                    showToast("You can not increase budget of your job!", this)
                } else {
                    showDialogIncreaseBudgetRequest()
                }
                R.id.action_rechedule -> {
                    intent = Intent(this@TaskDetailsActivity, RescheduleTimeRequestActivity::class.java)
                    val bundle = Bundle()
                    intent.putExtras(bundle)
                    startActivityForResult(intent, ConstantKey.RESULTCODE_RESCHEDULE)
                }
                R.id.action_job_receipt -> {
                    intent = Intent(this@TaskDetailsActivity, JobReceiptActivity::class.java)
                    val bundle = Bundle()
                    bundle.putBoolean(ConstantKey.IS_MY_TASK, isUserThePoster)
                    bundle.putString(ConstantKey.TASK_SLUG, taskModel!!.slug)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            }
            false
        }
    }

    private fun handleOverDueStatus(status: String) {
        if (taskModel == null) return
        if (taskModel!!.worker == null) return
        var taskDate: Calendar? = null
        val temp = Date(System.currentTimeMillis())
        val today = Calendar.getInstance()
        today.time = temp
        if (taskModel!!.dueDate != null && taskModel!!.dueDate != "") {
            @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("yyyy-MM-dd")
            try {
                val date = format.parse(taskModel!!.dueDate)
                taskDate = Calendar.getInstance()
                taskDate.time = date
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        if (taskDate == null || today == null) return
        if (status == Constant.TASK_OVERDUE) {
            if (isUserThePoster || isUserTheTicker) {
                taskDate.add(Calendar.DAY_OF_YEAR, 14)
                val df = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
                if (taskDate.timeInMillis - today.timeInMillis >= 0) {
                    toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_cancellation, true)
                    toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_reschedule, true)
                    toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_increase_budget, true)
                } else {
                    toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_cancellation, true)
                    toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_reschedule, false)
                    toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_increase_budget, false)
                }
            }
        }
    }

    private fun onLoadingFinished() {
        if (isGetBankAccountLoaded && isInitPageLoaded && isGetBillingAddressLoaded && isUserProfileLoaded) {
            cardMakeAnOffer.isClickable = true
        }
    }

    private val getData: Unit
        get() {
            allUserProfileDetails
            bankAccountAddress
            billingAddress
            initPage()
        }
    private var isGetBankAccountLoaded = false// Print Error!

    //http request
    val bankAccountAddress: Unit
        get() {
            val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.BASE_URL + Constant.ADD_ACCOUNT_DETAILS,
                    com.android.volley.Response.Listener { response: String? ->
                        Timber.e(response)
                        try {
                            val jsonObject = JSONObject(response!!)
                            Timber.e(jsonObject.toString())
                            if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                                if (jsonObject.getBoolean("success")) {
                                    val jsonString = jsonObject.toString() //http request
                                    var data = BankAccountModel()
                                    val gson = Gson()
                                    data = gson.fromJson(jsonString, BankAccountModel::class.java)
                                    if (data != null) {
                                        if (data.isSuccess) {
                                            if (data.data != null && data.data.account_number != null) {
                                                bankAccountModel = data
                                            }
                                        }
                                    }
                                } else {
                                    showToast("Something went Wrong", this)
                                }
                            }
                        } catch (e: JSONException) {
                            Timber.e(e.toString())
                            e.printStackTrace()
                        }
                        isGetBankAccountLoaded = true
                        onLoadingFinished()
                    },
                    com.android.volley.Response.ErrorListener { error: VolleyError ->
                        val networkResponse = error.networkResponse
                        if (networkResponse != null && networkResponse.data != null) {
                            val jsonError = String(networkResponse.data)
                            // Print Error!
                            Timber.e(jsonError)
                            if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                                unauthorizedUser()
                                hideProgressDialog()
                                return@ErrorListener
                            }
                            try {
                                hideProgressDialog()
                                val jsonObject = JSONObject(jsonError)
                                val jsonObject_error = jsonObject.getJSONObject("error")
                                if (jsonObject_error.has("message")) {
                                    showSuccessToast(jsonObject_error.getString("message"), this)
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        } else {
                            showToast("Something Went Wrong", this)
                        }
                        Timber.e(error.toString())
                        hideProgressDialog()
                        isGetBankAccountLoaded = true
                        onLoadingFinished()
                    }) {
                override fun getHeaders(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                    map1["Content-Type"] = "application/x-www-form-urlencoded"
                    map1["X-Requested-With"] = "XMLHttpRequest"
                    map1["Version"] = BuildConfig.VERSION_CODE.toString()
                    return map1
                }
            }
            stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(stringRequest)
        }
    private var isGetBillingAddressLoaded = false// Print Error!

    //http request
    val billingAddress: Unit
        get() {
            val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.BASE_URL + Constant.ADD_BILLING,
                    com.android.volley.Response.Listener { response: String? ->
                        Timber.e(response)
                        try {
                            hideProgressDialog()
                            val jsonObject = JSONObject(response!!)
                            Timber.e(jsonObject.toString())
                            if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                                if (jsonObject.getBoolean("success")) {
                                    val jsonString = jsonObject.toString() //http request
                                    var data = BillingAdreessModel()
                                    val gson = Gson()
                                    data = gson.fromJson(jsonString, BillingAdreessModel::class.java)
                                    if (data != null) {
                                        if (data.isSuccess) {
                                            if (data.data != null && data.data.line1 != null) {
                                                billingAdreessModel = data
                                            }
                                        }
                                    }
                                } else {
                                    showToast("Something went Wrong", this)
                                }
                            }
                        } catch (e: JSONException) {
                            Timber.e(e.toString())
                            e.printStackTrace()
                        }
                        isGetBillingAddressLoaded = true
                        onLoadingFinished()
                    },
                    com.android.volley.Response.ErrorListener { error: VolleyError ->
                        val networkResponse = error.networkResponse
                        if (networkResponse?.data != null) {
                            val jsonError = String(networkResponse.data)
                            // Print Error!
                            Timber.e(jsonError)
                            if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                                unauthorizedUser()
                                return@ErrorListener
                            }
                            try {
                                val jsonObject = JSONObject(jsonError)
                                val jsonObject_error = jsonObject.getJSONObject("error")
                                if (jsonObject_error.has("message")) {
                                    showToast(jsonObject_error.getString("message"), this)
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        } else {
                            showToast("Something Went Wrong", this)
                        }
                        Timber.e(error.toString())
                        isGetBillingAddressLoaded = true
                        onLoadingFinished()
                    }) {
                override fun getHeaders(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                    map1["Content-Type"] = "application/x-www-form-urlencoded"
                    map1["X-Requested-With"] = "XMLHttpRequest"
                    return map1
                }
            }
            stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(stringRequest)
        }
    private var isUserProfileLoaded = false

    // map1.put("X-Requested-With", "XMLHttpRequest");
    val allUserProfileDetails: Unit
        get() {
            val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.URL_PROFILE + "/" + sessionManager.userAccount.id,
                    com.android.volley.Response.Listener { response: String? ->
                        try {
                            val jsonObject = JSONObject(response!!)
                            Timber.e(jsonObject.toString())
                            if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                userAccountModel = UserAccountModel().getJsonToModel(jsonObject.getJSONObject("data"))
                                sessionManager.userAccount = userAccountModel
                            }
                        } catch (e: JSONException) {
                            Timber.e(e.toString())
                            e.printStackTrace()
                        }
                        isUserProfileLoaded = true
                        onLoadingFinished()
                    },
                    com.android.volley.Response.ErrorListener { error: VolleyError ->
                        errorHandle1(error.networkResponse)
                        isUserProfileLoaded = true
                        onLoadingFinished()
                    }) {
                override fun getHeaders(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                    map1["Content-Type"] = "application/x-www-form-urlencoded"
                    // map1.put("X-Requested-With", "XMLHttpRequest");
                    return map1
                }
            }
            stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(stringRequest)
        }
    private var isInitPageLoaded = false
    private fun initPage() {
        val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.URL_TASKS + "/" + str_slug,
                com.android.volley.Response.Listener { response: String? ->
                    try {
                        content.visibility = View.VISIBLE
                        val jsonObject = JSONObject(response!!)
                        Timber.e(jsonObject.toString())
                        println(jsonObject.toString())
                        if (jsonObject.has("success") &&
                                !jsonObject.isNull("success") &&
                                jsonObject.getBoolean("success")) {
                            if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                val jsonObject_data = jsonObject.getJSONObject("data")
                                taskModel = TaskModel().getJsonToModel(jsonObject_data, this@TaskDetailsActivity)
                                taskModel!!.offerSent = false
                                taskModel!!.offers.forEach(Consumer { offerModel1: OfferModel -> if (offerModel1.worker.id == sessionManager.userAccount.id) taskModel!!.offerSent = true }
                                )
                                setOwnerTask()
                                initOfferList()
                                initStatusTask(taskModel!!.status.toLowerCase())
                                initComponent()
                                setDataInLayout(taskModel!!)
                                setChatButton(taskModel!!.status.toLowerCase(), jsonObject_data)
                                setPosterChatButton(taskModel!!.status.toLowerCase(), jsonObject_data)
                                if ((taskModel!!.poster.id.toString() == sessionManager.userAccount.id.toString()) and (taskModel!!.status.equals(Constant.TASK_OPEN, ignoreCase = true))) {
                                    lytStatus.setStatus(getString(R.string.posted))
                                } else {
                                    if (taskModel!!.offerSent and (taskModel!!.status.equals(Constant.TASK_OPEN, ignoreCase = true)))
                                        lytStatus.setStatus(getString(R.string.offered))
                                }
                                if (jsonObject_data.has("conversations") && !jsonObject_data.isNull("conversations")) {
                                    for (i in 0 until jsonObject_data.getJSONArray("conversations").length()) {
                                        val first = jsonObject_data.getJSONArray("conversations").getJSONObject(i).getJSONArray("users").getJSONObject(0).getInt("id")
                                        val sec = jsonObject_data.getJSONArray("conversations").getJSONObject(i).getJSONArray("users").getJSONObject(1).getInt("id")
                                        if (first == sessionManager.userAccount.id || sec == sessionManager.userAccount.id) {
                                            imgPChat.setImageDrawable(getDrawable(R.drawable.ic_p_chat_enable_v2))
                                            lytBtnMessage.setOnClickListener { v: View? -> getConversationId(taskModel!!.slug, taskModel!!.poster.id.toString()) }
                                        }
                                    }
                                }
                                if (taskModel!!.taskType == "physical") {
                                    llLocation.setOnClickListener { v: View? ->
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:<" + taskModel!!.position.latitude + ">,<" + taskModel!!.position.longitude + ">?q=<" + taskModel!!.position.latitude + ">,<" + taskModel!!.position.longitude + ">(" + "job address" + ")"))
                                        startActivity(intent)
                                    }
                                }

                            }
                        } else {
                            showToast("Something went wrong", this@TaskDetailsActivity)
                        }
                    } catch (e: JSONException) {
                        showToast("JSONException", this@TaskDetailsActivity)
                        Timber.e(e.toString())
                        e.printStackTrace()
                    }
                    isInitPageLoaded = true
                    llLoading.visibility = View.GONE
                    onLoadingFinished()
                },
                com.android.volley.Response.ErrorListener { error: VolleyError ->
                    isInitPageLoaded = true
                    llLoading.visibility = View.GONE
                    onLoadingFinished()

                    //    fl_task_details.setVisibility(View.GONE);
                    errorHandle1(error.networkResponse)
                }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                // map1.put("X-Requested-With", "XMLHttpRequest");
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this@TaskDetailsActivity)
        requestQueue.add(stringRequest)
    }


    @SuppressLint("SetTextI18n")
    private fun setChatButton(state: String, jsonObject: JSONObject) {


        if (!isUserThePoster || taskModel!!.worker == null) return
        relPrivateChat.visibility = View.VISIBLE
        if (taskModel!!.worker.avatar != null && taskModel!!.worker.avatar.thumbUrl != null) {
            ImageUtil.displayImage(imgAvatarChat, taskModel!!.worker.avatar.thumbUrl, null)
        }
        imgAvatarChat.setOnClickListener { v: View? ->
            if (taskModel!!.worker.id != null) {
                val intent = Intent(this@TaskDetailsActivity, ProfileActivity::class.java)
                intent.putExtra("id", taskModel!!.worker.id)
                startActivity(intent)
            }
        }
        tvPrivateChatName.text = taskModel!!.worker.name
        if (taskModel!!.location != null && !taskModel!!.location.isEmpty()) {
            tvPrivateChatLocation.text = taskModel!!.location
        } else {
            tvPrivateChatLocation.text = "Remote job"
        }
        if (taskModel!!.worker.location != null && taskModel!!.worker.location.isNotEmpty()) {
            tvPrivateChatLocation.visibility = View.VISIBLE
            //            txtPosterLocation.setText(taskModel!!.getLocation());
        } else {
            tvPrivateChatLocation.visibility = View.VISIBLE
        }
        tvPrivateChatLastOnline.text = "Active " + taskModel!!.worker.lastOnline
        llBtnPrivateMessageEnable.setOnClickListener { v: View? ->
            try {
                val conversation = jsonObject.getJSONObject("conversation")
                val conversationModel = ConversationModel(this@TaskDetailsActivity).getJsonToModel(conversation,
                        this@TaskDetailsActivity)
                val intent = Intent(this@TaskDetailsActivity, ChatActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelable(ConstantKey.CONVERSATION, conversationModel)
                intent.putExtras(bundle)
                startActivityForResult(intent, ConstantKey.RESULTCODE_PRIVATE_CHAT)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        when (state) {
            Constant.TASK_DRAFT, Constant.TASK_PENDING, Constant.TASK_OPEN -> {
                llBtnPrivateMessageDisable.visibility = View.VISIBLE
                llBtnPrivateMessageEnable.visibility = View.GONE
            }
            else -> {
                llBtnPrivateMessageDisable.visibility = View.GONE
                llBtnPrivateMessageEnable.visibility = View.VISIBLE
            }
        }
    }

    private fun setPosterChatButton(state: String, jsonObject: JSONObject) {
        if (taskModel!!.worker == null) return
        if (taskModel!!.worker.id != sessionManager.userAccount.id) return
        llBtnPosterMessageEnable.setOnClickListener { v: View? ->
            try {
                val conversation = jsonObject.getJSONObject("conversation")
                val conversationModel = ConversationModel(this@TaskDetailsActivity).getJsonToModel(conversation,
                        this@TaskDetailsActivity)
                val intent = Intent(this@TaskDetailsActivity, ChatActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelable(ConstantKey.CONVERSATION, conversationModel)
                intent.putExtras(bundle)
                startActivityForResult(intent, ConstantKey.RESULTCODE_PRIVATE_CHAT)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        when (state) {
            Constant.TASK_DRAFT, Constant.TASK_PENDING, Constant.TASK_OPEN -> {
                lytBtnMessage.visibility = View.VISIBLE
                llBtnPosterMessageEnable.visibility = View.GONE
            }
            else -> {
                lytBtnMessage.visibility = View.GONE
                llBtnPosterMessageEnable.visibility = View.VISIBLE
            }
        }
    }

    private fun setOwnerTask() {
        if (taskModel!!.poster.id == sessionManager.userAccount.id) {
            //this is self task
            isUserThePoster = true
            isUserTheTicker = false
            postedByLyt.visibility = View.GONE
        } else {
            //this is another tasks
            postedByLyt.visibility = View.VISIBLE
            if (taskModel!!.worker != null) {
                if (taskModel!!.worker.id == sessionManager.userAccount.id) {
                    isUserThePoster = false
                    isUserTheTicker = true
                    noActionAvailable = false
                } else {
                    noActionAvailable = true
                    isUserTheTicker = false
                }
            } else {
                isUserThePoster = false
                isUserTheTicker = false
                noActionAvailable = false
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDataInLayout(taskModel: TaskModel?) {
        txtTitle.text = taskModel!!.title
        txtCreatedDate.text = "Posted " + taskModel.createdAt
        if (taskModel.bookmarkID != null) {
            toolbar.menu.findItem(R.id.menu_bookmark).setIcon(R.drawable.ic_bookmark_white_filled_background_white_32dp)
        }
        txtDescription.text = taskModel.description
        if (taskModel.poster.avatar != null && taskModel.poster.avatar.thumbUrl != null) {
            ImageUtil.displayImage(imgAvtarPoster, taskModel.poster.avatar.thumbUrl, null)
        } //TODO DUMMY IMAGE
        imgAvtarPoster.setOnClickListener { v: View? ->
            if (taskModel.poster.id != null) {
                val intent = Intent(this@TaskDetailsActivity, ProfileActivity::class.java)
                intent.putExtra("id", taskModel.poster.id)
                startActivity(intent)
            }
        }
        txtPosterName.text = taskModel.poster.name
        if (taskModel.taskType == "physical" && taskModel.location != null) {
            txtLocation.text = taskModel.location
        } else {
            txtLocation.text = "Remote job"
        }
        /*  if (taskModel.poster.location != null && taskModel.poster.location.isNotEmpty()) {
              txtPosterLocation.visibility = View.VISIBLE
              //            txtPosterLocation.setText(taskModel!!.getLocation());
          } else {
              txtPosterLocation.visibility = View.VISIBLE
          }*/
        txtPosterLastOnline.text = "Active " + taskModel.poster.lastOnline
        if (taskModel.dueTime != null) {
            val dueTime = convertObjectToString(taskModel.dueTime, "")
            txtDueTime.text = dueTime
        }
        if (taskModel.dueTime != null) {
            try {
                val time = Tools.jobDetailsDate(taskModel.dueDate)
                txtDueDate.text = Tools.formatJobDetailsDateV3(time) + ", "
            } catch (e: ParseException) {
                txtDueDate.text = taskModel.dueDate + ", "
            }
        }
        if (taskModel.musthave != null && taskModel.musthave.size > 0) {
            mustHaveLyt.visibility = View.VISIBLE
            val showMustHaveListAdapter = ShowMustHaveListAdapter(taskModel.musthave)
            mustHaveList.layoutManager = LinearLayoutManager(this)
            mustHaveList.adapter = showMustHaveListAdapter
        } else {
            mustHaveLyt.visibility = View.GONE
        }
    }

    private fun convertObjectToString(time: DueTimeModel, dueTime: String): String {
        var dueTime = dueTime
        if (time.morning) {
            dueTime = "Morning"
        }
        if (time.afternoon) {
            dueTime = if (dueTime.isNotEmpty()) {
                "$dueTime,Afternoon"
            } else {
                dueTime + "Afternoon"
            }
        }
        if (time.evening) {
            dueTime = if (dueTime.isNotEmpty()) {
                "$dueTime,Evening"
            } else {
                dueTime + "Evening"
            }
        }
        if (time.anytime != null && time.anytime) {
            dueTime = if (dueTime.isNotEmpty()) {
                "$dueTime,Anytime"
            } else {
                dueTime + "Anytime"
            }
        }
        return dueTime
    }

    private fun initToolbar() {
        appBarLayout.addOnOffsetChangedListener(object : OnOffsetChangedListener {
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(application, R.color.black))
                    toolbar.setTitleTextColor(resources.getColor(R.color.black))
                    isToolbarCollapsed = true
                } else if (isToolbarCollapsed) {
                    collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(application, R.color.white))
                    toolbar.setTitleTextColor(resources.getColor(R.color.white))
                    isToolbarCollapsed = false
                }
            }
        })
    }

    private fun copyTask(taskModel: TaskModel?) {
        val update_task = Intent(this@TaskDetailsActivity, TaskCreateActivity::class.java)
        val bundle = Bundle()
        bundle.putString(ConstantKey.TITLE, ConstantKey.COPY_TASK)
        bundle.putBoolean(ConstantKey.COPY, true)
        update_task.putExtras(bundle)
        startActivity(update_task)
    }

    private fun EditTask(taskModel: TaskModel?) {
        val update_task = Intent(this@TaskDetailsActivity, TaskCreateActivity::class.java)
        val bundle = Bundle()
        bundle.putString(ConstantKey.TITLE, ConstantKey.UPDATE_TASK)
        bundle.putBoolean(ConstantKey.EDIT, true)
        update_task.putExtras(bundle)
        startActivity(update_task)
    }

    private fun deleteTaskPermanent(slug: String) {
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Constant.URL_TASKS + "/" + slug + "/cancellation",
                com.android.volley.Response.Listener { response: String? ->
                    Timber.e(response)
                    try {
                        val jsonObject = JSONObject(response!!)
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                getData
                                showSuccessToast("Job Cancelled Successfully", this@TaskDetailsActivity)
                            } else {
                                showToast("Job not cancelled", this@TaskDetailsActivity)
                            }
                        } else {
                            showToast(getString(R.string.server_went_wrong), this@TaskDetailsActivity)
                        }
                        hideProgressDialog()
                    } catch (e: JSONException) {
                        hideProgressDialog()
                        e.printStackTrace()
                    }
                },
                com.android.volley.Response.ErrorListener { error: VolleyError? -> hideProgressDialog() }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                map1["Content-Type"] = "application/json"
                map1["X-Requested-With"] = "XMLHttpRequest"
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this@TaskDetailsActivity)
        requestQueue.add(stringRequest)
    }


    @SuppressLint("SetTextI18n")
    private fun initComponent() {
        setOfferView(taskModel!!.offerCount)
        initIncreaseBudget()
        setPrice()
        setAttachmentAndSlider()
        setWorker()
    }

    private fun setPrice() {
        if (taskModel!!.status != null && !taskModel!!.status.equals(Constant.TASK_OPEN, ignoreCase = true)
                && (isUserThePoster || isUserTheTicker)) {
            budget.text = String.format(Locale.ENGLISH, "%d", taskModel!!.amount)
        } else {
            budget.text = String.format(Locale.ENGLISH, "%d", taskModel!!.budget)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setWorker() {
        //worker
        if (taskModel!!.worker != null) {
            if (taskModel!!.worker.avatar != null && taskModel!!.worker.avatar.thumbUrl != null) {
                ImageUtil.displayImage(imgAvtarWorker, taskModel!!.worker.avatar.thumbUrl, null)
            } //TODO DUMMY IMAGE
            txtWorkerName.text = taskModel!!.worker.name
            if (taskModel!!.worker != null && taskModel!!.worker.workerRatings != null && taskModel!!.worker.workerRatings.avgRating != null && taskModel!!.worker.workerRatings.receivedReviews != null) {
                txtRatingValue.text = String.format(Locale.US, "%.1f", taskModel!!.worker.workerRatings.avgRating) + " (" + taskModel!!.worker.workerRatings.receivedReviews + ")"
            } else {
                ratingbar_worker.visibility = View.GONE
            }
            if (taskModel!!.worker.workTaskStatistics != null) txtCompletionRate.text = taskModel!!.worker.workTaskStatistics.completionRate.toString() + "%"
        }
    }

    private fun setAttachmentAndSlider() {
        Timber.e("%s", taskModel!!.attachments.size)
        adapterImageSlider = AdapterImageSlider(this, ArrayList())
        if (taskModel!!.attachments == null || taskModel!!.attachments.size == 0) {
            val attachment = AttachmentModel()
            val attachments = ArrayList<AttachmentModel>()
            attachments.add(attachment)
            taskModel!!.attachments = attachments
        }
        adapterImageSlider.setItems(taskModel!!.attachments)
        dataList.addAll(taskModel!!.attachments)
        viewPager.adapter = adapterImageSlider
        // displaying selected image first
        viewPager.currentItem = 0
        if (taskModel!!.attachments != null && taskModel!!.attachments.size > 1) {
            addBottomDots(layoutDots, adapterImageSlider.count, 0)
        }

        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(pos: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(pos: Int) {
                addBottomDots(layoutDots, adapterImageSlider.count, pos)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

    }


    @SuppressLint("SetTextI18n")
    private fun setOfferView(offerCount: Int) {
        if (!isUserThePoster) {
            lnOffers.visibility = View.GONE
            return
        }
        if (offerCount == 0) {
            txtOffersCount.text = getString(R.string.offer)
            txtWaitingForOffer.visibility = View.VISIBLE
            if (isUserThePoster) {
                firstOfferLyt.visibility = View.GONE
            } else {
                firstOfferLyt.visibility = View.VISIBLE
            }
        } else {
            txtOffersCount.text = "Offers ($offerCount)"
            txtWaitingForOffer.visibility = View.GONE
        }

        //TODO taskModel!!.getOfferCount() > 5
        if (offerCount > 5) {
            cardViewAllOffers.visibility = View.VISIBLE
        } else {
            cardViewAllOffers.visibility = View.GONE
        }
        cardViewAllOffers.setOnClickListener { v: View? ->
            offerListAdapter.clear()
            offerListAdapter.addItems(offerListF)
            cardViewAllOffers.visibility = View.GONE
        }
    }

    private fun addBottomDots(layout_dots: LinearLayout?, size: Int, current: Int) {
        val dots = arrayOfNulls<ImageView>(size)
        layout_dots!!.removeAllViews()
        for (i in dots.indices) {
            dots[i] = ImageView(this)
            val width_height = 15
            val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams(width_height, width_height))
            params.setMargins(10, 10, 10, 10)
            dots[i]!!.layoutParams = params
            dots[i]!!.setImageResource(R.drawable.shape_circle_outline_gray)
            layout_dots.addView(dots[i])
        }
        if (dots.isNotEmpty()) {
            dots[current]!!.setImageResource(R.drawable.shape_circle)
        }
    }


    private fun onViewClick() {
        var intent: Intent
        var bundle: Bundle
        txtAskQuestion.setOnClickListener {
            showQuestions()
        }
        lytBtnViewAllOffers.setOnClickListener {
            intent = Intent(this@TaskDetailsActivity, ViewAllOffersActivity::class.java)
            bundle = Bundle()
            bundle.putString(ConstantKey.SLUG, taskModel!!.slug)
            intent.putExtras(bundle)
            startActivityForResult(intent, 121)
        }
        txtBtnText.setOnClickListener { offerClick() }
        lytBtnMakeAnOffer.setOnClickListener { offerClick() }
        firstOffer.setOnClickListener { offerClick() }
        liAssign.setOnClickListener {
            val workerIntent = Intent(this@TaskDetailsActivity, ProfileActivity::class.java)
            workerIntent.putExtra("id", taskModel!!.worker.id)
            startActivity(workerIntent)
            return@setOnClickListener
        }
        linearUserProfile.setOnClickListener {
            val posterIntent = Intent(this@TaskDetailsActivity, ProfileActivity::class.java)
            posterIntent.putExtra("id", taskModel!!.poster.id)
            startActivity(posterIntent)
        }
    }

    private fun offerClick() {
        when (txtBtnText.text.toString().trim { it <= ' ' }) {
            ConstantKey.BTN_ASK_TO_RELEASE_MONEY -> showCustomDialogAskToReleaseMoney()
            ConstantKey.BTN_RELEASE_MONEY -> showCustomDialogReleaseMoney()
            ConstantKey.BTN_MAKE_AN_OFFER -> if (!needRequirementSheet()) {
                if (taskModel!!.musthave.size == 0) {
                    intent = Intent(this@TaskDetailsActivity, MakeAnOfferActivity::class.java)
                    val bundle = Bundle()
                    bundle.putInt("id", taskModel!!.id)
                    bundle.putInt("budget", taskModel!!.budget)
                    intent.putExtras(bundle)
                    startActivity(intent)
                } else {
                    showRequirementDialog()
                }
            } else {
                val tickerRequirementsBottomSheet = newInstance(requirementState)
                tickerRequirementsBottomSheet.show(supportFragmentManager, "")
            }
            ConstantKey.BTN_OFFER_PENDING -> {
            }
            ConstantKey.BTN_CANCELLATION_REQUEST_RECEIVED, ConstantKey.BTN_CANCELLATION_REQUEST_SENT -> {
            }
            ConstantKey.BTN_WRITE_A_REVIEW -> {
            }
        }
    }

    private fun handleState() {
        requirementState[TickerRequirementsBottomSheet.Requirement.Profile] = false
        requirementState[TickerRequirementsBottomSheet.Requirement.BankAccount] = false
        requirementState[TickerRequirementsBottomSheet.Requirement.BillingAddress] = false
        requirementState[TickerRequirementsBottomSheet.Requirement.BirthDate] = false
        requirementState[TickerRequirementsBottomSheet.Requirement.PhoneNumber] = false
        if (::userAccountModel.isInitialized)
            if (userAccountModel.avatar != null && userAccountModel.avatar.url != null && userAccountModel.avatar.url != "") {
                requirementState[TickerRequirementsBottomSheet.Requirement.Profile] = true
            }
        //checking one field is enough
        if (::bankAccountModel.isInitialized)
            if (bankAccountModel.data != null && bankAccountModel.data.account_number != null && bankAccountModel.data.account_number != "") {
                requirementState[TickerRequirementsBottomSheet.Requirement.BankAccount] = true
            }
        //checking one filed is enough
        if (::billingAdreessModel.isInitialized)
            if (billingAdreessModel.data != null && billingAdreessModel.data.post_code != null && billingAdreessModel.data.post_code != "") {
                requirementState[TickerRequirementsBottomSheet.Requirement.BillingAddress] = true
            }
        if (::userAccountModel.isInitialized)
            if (userAccountModel.dob != null && userAccountModel.dob != "") {
                requirementState[TickerRequirementsBottomSheet.Requirement.BirthDate] = true
            }
        if (::userAccountModel.isInitialized)
            if (userAccountModel.mobile != null && userAccountModel.mobile != "" && userAccountModel.mobileVerifiedAt != null && userAccountModel.mobileVerifiedAt != "") {
                requirementState[TickerRequirementsBottomSheet.Requirement.PhoneNumber] = true
            }
    }

    private fun needRequirementSheet(): Boolean {
        handleState()
        return !(requirementState[TickerRequirementsBottomSheet.Requirement.Profile]!! &&
                requirementState[TickerRequirementsBottomSheet.Requirement.BankAccount]!! &&
                requirementState[TickerRequirementsBottomSheet.Requirement.BillingAddress]!! &&
                requirementState[TickerRequirementsBottomSheet.Requirement.BirthDate]!! &&
                requirementState[TickerRequirementsBottomSheet.Requirement.PhoneNumber]!!)
    }

    private fun submitAskToReleaseMoney() {
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Constant.URL_TASKS + "/" + taskModel!!.slug + "/complete",
                com.android.volley.Response.Listener { response: String? ->
                    Timber.e(response)
                    try {
                        val jsonObject = JSONObject(response!!)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            initialStage()
                        } else {
                            hideProgressDialog()
                            showToast(getString(R.string.server_went_wrong), this@TaskDetailsActivity)
                        }
                    } catch (e: JSONException) {
                        hideProgressDialog()
                        e.printStackTrace()
                    }
                },
                com.android.volley.Response.ErrorListener { error: VolleyError ->
                    val networkResponse = error.networkResponse
                    if (networkResponse != null && networkResponse.data != null) {
                        val jsonError = String(networkResponse.data)
                        // Print Error!
                        Timber.e(jsonError)
                        if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                            unauthorizedUser()
                            hideProgressDialog()
                            return@ErrorListener
                        }
                        hideProgressDialog()
                        try {
                            val jsonObject = JSONObject(jsonError)
                            val jsonObject_error = jsonObject.getJSONObject("error")
                            if (jsonObject_error.has("message")) {
                                showToast(jsonObject_error.getString("message"), this)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        showToast("Something Went Wrong", this@TaskDetailsActivity)
                    }
                    Timber.e(error.toString())
                    hideProgressDialog()
                }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this@TaskDetailsActivity)
        requestQueue.add(stringRequest)
    }

    private fun submitReleaseMoney() {
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Constant.URL_TASKS + "/" + taskModel!!.slug + "/close",
                com.android.volley.Response.Listener { response: String? ->
                    Timber.e(response)
                    try {
                        val jsonObject = JSONObject(response!!)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            initialStage()
                        } else {
                            hideProgressDialog()
                            showToast(getString(R.string.server_went_wrong), this@TaskDetailsActivity)
                        }
                    } catch (e: JSONException) {
                        hideProgressDialog()
                        e.printStackTrace()
                    }
                },
                com.android.volley.Response.ErrorListener { error: VolleyError ->
                    val networkResponse = error.networkResponse
                    if (networkResponse != null && networkResponse.data != null) {
                        val jsonError = String(networkResponse.data)
                        // Print Error!
                        Timber.e(jsonError)
                        if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                            unauthorizedUser()
                            hideProgressDialog()
                            return@ErrorListener
                        }
                        hideProgressDialog()
                        try {
                            val jsonObject = JSONObject(jsonError)
                            val jsonObject_error = jsonObject.getJSONObject("error")
                            if (jsonObject_error.has("message")) {
                                showToast(jsonObject_error.getString("message"), this)
                            }

                            //  ((CredentialActivity)requireActivity()).showToast(message,requireActivity());
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        showToast("Something Went Wrong", this@TaskDetailsActivity)
                    }
                    Timber.e(error.toString())
                    hideProgressDialog()
                }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this@TaskDetailsActivity)
        requestQueue.add(stringRequest)
    }

/*    private fun postComment(str_comment: String, attachmentModels: QuestionAttachmentAdapter?) {
        if (str_comment.length < 5) {
            showToast("The question text must be at least 5 characters", this)
            return
        }
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Constant.URL_QUESTIONS + "/" + taskModel!!.id + "/create",
                com.android.volley.Response.Listener { response: String? ->
                    Timber.e(response)
                    try {
                        val jsonObject = JSONObject(response!!)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            edtComment.setText("")
                            attachmentArrayListQuestion.clear()
                            attachmentArrayListQuestion.add(AttachmentModel())
                            if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                               *//* if (recyclerViewQuestions.visibility != View.VISIBLE) {
                                    recyclerViewQuestions.visibility = View.VISIBLE
                                }*//*
                                val questionModel = QuestionModel().getJsonToModel(jsonObject.getJSONObject("data"))
                                questionListAdapter.addItem(questionModel)
                            }
                        } else {
                            showToast(getString(R.string.server_went_wrong), this@TaskDetailsActivity)
                        }
                        if (adapter != null) adapter.notifyDataSetChanged()
                        dataOnlyQuestions
                        hideProgressDialog()
                    } catch (e: JSONException) {
                        hideProgressDialog()
                        e.printStackTrace()
                    }
                },
                com.android.volley.Response.ErrorListener { error: VolleyError ->
                    val networkResponse = error.networkResponse
                    if (networkResponse?.data != null) {
                        val jsonError = String(networkResponse.data)

                        // Print Error!
                        Timber.e(jsonError)
                        if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                            unauthorizedUser()
                            hideProgressDialog()
                            return@ErrorListener
                        }
                        try {
                            val jsonObject = JSONObject(jsonError)
                            val jsonObject_error = jsonObject.getJSONObject("error")
                            if (jsonObject_error.has("message")) {
                                showToast(jsonObject_error.getString("message"), this)
                            }
                            //                            if (jsonObject_error.has("errors")) {
//                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
//                            }
                            //  ((CredentialActivity)requireActivity()).showToast(message,requireActivity());
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        showToast("Something Went Wrong", this@TaskDetailsActivity)
                    }
                    Timber.e(error.toString())
                    hideProgressDialog()
                }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                return map1
            }

            override fun getParams(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["question_text"] = str_comment
                if (attachmentModels != null && attachmentModels.size != 0 && attachmentModels.getAttachment(0).id != null) {
                    var i = 0
                    while (attachmentModels.size > i) {
                        map1["attachments[$i]"] = attachmentModels.getAttachment(i).id.toString()
                        i++
                    }
                }
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this@TaskDetailsActivity)
        requestQueue.add(stringRequest)
    }*/

    override fun onMakeAnOffer() {
        initialStage()
    }

    override fun onRequestAccept() {
        getData
    }

    override fun onWithdraw(id: Int) {
        val infoBottomSheet = WithdrawBottomSheet(this, id)
        infoBottomSheet.show(supportFragmentManager, null)
    }

    override fun onStripeRequirementFilled() {
        initialStage()
    }

    override fun startWithdraw(id: Int) {
        doApiCall(Constant.URL_OFFERS + "/" + id)
    }

    private class AdapterImageSlider  // constructor
    (private val act: Activity, private var items: ArrayList<AttachmentModel>) : PagerAdapter() {
        fun setOnItemClickListener() {}
        override fun getCount(): Int {
            return items.size
        }

        fun getItem(pos: Int): AttachmentModel {
            return items[pos]
        }

        fun setItems(items: ArrayList<AttachmentModel>) {
            this.items = items
            notifyDataSetChanged()
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val attachment = items[position]
            val inflater = act.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val v = inflater.inflate(R.layout.item_slider_image, container, false)
            val image = v.findViewById<ImageView>(R.id.image)
            if (attachment.modalUrl != null) {
                Glide.with(image).load(attachment.modalUrl).into(image)
                image.scaleType = ImageView.ScaleType.CENTER_CROP
                image.setOnClickListener { v1: View? ->
                    /*   if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, attachment);
                }*/
                    val intent = Intent(act, ZoomImageActivity::class.java)
                    intent.putExtra("url", items)
                    intent.putExtra("title", "")
                    intent.putExtra("pos", position)
                    act.startActivity(intent)
                }
            } else {
                image.scaleType = ImageView.ScaleType.FIT_XY
                if (taskModel!!.location != null && !taskModel!!.location.isEmpty()) {
//                    Tools.displayImageOriginal(act, image, attachment.getDrawable());
                    image.setImageResource(R.drawable.banner1)
                } else {
//                    Tools.displayImageOriginal(act, image, attachment.getDrawable());
                    image.setImageResource(R.drawable.banner1)
                }
            }
            container.addView(v)
            return v
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as RelativeLayout)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) (fragment as? TickerRequirementsBottomSheet)?.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ConstantKey.RESULTCODE_MAKEANOFFER) {
            if (data != null) {
                val bundle = data.extras
                if (bundle != null) {
                    if (bundle.getBoolean(ConstantKey.MAKE_AN_OFFER)) {
                        initialStage()
                        // txtBtnText.setText(ConstantKey.BTN_OFFER_PENDING);
                    }
                }
            }
        }
        if (requestCode == ConstantKey.RESULTCODE_PAYMENTOVERVIEW) {
            if (data != null) {
                val bundle = data.extras
                if (bundle != null) {
                    if (bundle.getBoolean(ConstantKey.PAYMENT_OVERVIEW)) {
                        initialStage()
                    }
                }
            }
        }
        if (requestCode == ConstantKey.RESULTCODE_WRITE_REVIEW) {
            if (data != null) {
                val bundle = data.extras
                if (bundle != null) {
                    if (bundle.getBoolean(ConstantKey.WRITE_REVIEW)) {
                        initialStage()
                    }
                }
            }
        }
        if (requestCode == ConstantKey.RESULTCODE_CANCELLATION) {
            if (data != null) {
                val bundle = data.extras
                if (bundle != null) {
                    if (bundle.getBoolean(ConstantKey.CANCELLATION)) {
                        initialStage()
                    }
                }
            }
        }
        if (requestCode == ConstantKey.RESULTCODE_RESCHEDULE) {
            getData
        }
        if (requestCode == ConstantKey.RESULTCODE_CANCELLATION_DONE) {
            if (data != null) {
                val bundle = data.extras
                if (bundle != null) {
                    if (bundle.getBoolean(ConstantKey.DONE)) {
                        initialStage()
                    }
                }
            }
        }
        if (requestCode == 21 || requestCode == 20) {
            //dataOnlyQuestions
        }
        if (requestCode == GALLERY_PICKUP_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data!!.data != null) {
                    val imageStoragePath = CameraUtils.getPath(this@TaskDetailsActivity, data.data)
                    val file = File(imageStoragePath)
                    // uploadDataQuestionMediaApi(file)
                }
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
            } else {
                // failed to record video
                showToast("Sorry! Failed to Pickup Image", this)
            }
        }
    }

    fun checkPermissionREAD_EXTERNAL_STORAGE(context: Context?): Boolean {
        return if (ContextCompat.checkSelfPermission(context!!,
                        permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(((context as Activity?)!!), arrayOf(permission.READ_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
            false
        } else {
            true
        }
    }

    //Adapter override method
    override fun onItemOfferClick(v: View?, obj: OfferModel, position: Int, action: String) {
        when {
            action.equals("reply", ignoreCase = true) -> {
                val intent = Intent(this@TaskDetailsActivity, PublicChatActivity::class.java)
                val bundle = Bundle()
                offerModel = obj
                isOfferQuestion = "offer"
                bundle.putBoolean("isPoster", isUserThePoster)
                bundle.putString("posterID", taskModel!!.poster.id.toString())
                intent.putExtras(bundle)
                startActivityForResult(intent, 20)
            }
            action.equals("accept", ignoreCase = true) -> {
                val intent = Intent(this@TaskDetailsActivity, PaymentOverviewActivity::class.java)
                val bundle = Bundle()
                offerModel = obj
                intent.putExtras(bundle)
                startActivityForResult(intent, ConstantKey.RESULTCODE_PAYMENTOVERVIEW)
            }
            action.equals("report", ignoreCase = true) -> {
                val intent = Intent(this@TaskDetailsActivity, ReportActivity::class.java)
                val bundle = Bundle()
                bundle.putString(ConstantKey.SLUG, taskModel!!.slug)
                bundle.putString("key", ConstantKey.KEY_OFFER_REPORT)
                intent.putExtras(bundle)
                startActivity(intent)
            }
            action.equals("message", ignoreCase = true) -> {
                getConversationId(taskModel!!.slug, obj.worker.id.toString())
            }
        }
    }

    override fun onItemQuestionClick(view: View?, obj: QuestionModel, position: Int, action: String) {
        if (action.equals("reply", ignoreCase = true)) {
            val intent = Intent(this@TaskDetailsActivity, PublicChatActivity::class.java)
            val bundle = Bundle()
            questionModel = obj
            isOfferQuestion = "question"
            //bundle.putParcelable(ConstantKey.QUESTION_LIST_MODEL, obj);
            intent.putExtras(bundle)
            startActivityForResult(intent, 21)
        } else if (action.equals("report", ignoreCase = true)) {
            val intent = Intent(this@TaskDetailsActivity, ReportActivity::class.java)
            val bundle = Bundle()
            bundle.putString(ConstantKey.SLUG, taskModel!!.slug)
            bundle.putString("key", ConstantKey.KEY_COMMENT_REPORT)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    override fun onItemClick(view: View, obj: AttachmentModel, position: Int, action: String) {
        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            if (action.equals("add", ignoreCase = true)) {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_PICKUP_IMAGE_REQUEST_CODE)
            } else if (action.equals("delete", ignoreCase = true)) {
                //recyclerViewQuestionAttachment.removeViewAt(position)
                attachmentArrayListQuestion.removeAt(position)
                adapter.notifyItemRemoved(position)
                adapter.notifyItemRangeRemoved(position, attachmentArrayListQuestion.size)
                attachmentArrayListQuestion.clear()
                attachmentArrayListQuestion.add(AttachmentModel())
                adapter.notifyDataSetChanged()
            }
        }
    }

/*    private fun uploadDataQuestionMediaApi(pictureFile: File) {
        showProgressDialog()
        val call: Call<String?>?
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile)
        val imageFile = MultipartBody.Part.createFormData("media", pictureFile.name, requestFile)
        call = ApiClient.getClient().getTaskTempAttachmentMediaData("XMLHttpRequest", sessionManager.tokenType + " " + sessionManager.accessToken, imageFile)
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                hideProgressDialog()
                Timber.e(response.toString())
                if (response.code() == HttpStatus.HTTP_VALIDATION_ERROR) {
                    showToast(response.message(), this@TaskDetailsActivity)
                    return
                }
                try {
                    val strResponse = response.body()
                    if (response.code() == HttpStatus.NOT_FOUND) {
                        showToast("not found", this@TaskDetailsActivity)
                        return
                    }
                    if (response.code() == HttpStatus.AUTH_FAILED) {
                        unauthorizedUser()
                        return
                    }
                    if (response.code() == HttpStatus.SUCCESS) {
                        adapter.clear()
                        Timber.e(strResponse)
                        assert(strResponse != null)
                        val jsonObject = JSONObject(strResponse)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("data")) {
                            val jsonObject_data = jsonObject.getJSONObject("data")
                            val attachment = AttachmentModel().getJsonToModel(jsonObject_data)
                            attachmentArrayListQuestion.add(attachment)
                        }
                        adapter.notifyItemInserted(0)
                        adapter.notifyDataSetChanged()
                        //  adapter.notifyItemRangeInserted(0,attachmentArrayList.size());
                    } else {
                        showToast("Something went wrong", this@TaskDetailsActivity)
                    }
                } catch (e: JSONException) {
                    showToast("Something went wrong", this@TaskDetailsActivity)
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                hideProgressDialog()
            }
        })
    }*/

    private fun doApiCall(url: String) {
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.DELETE, url,
                com.android.volley.Response.Listener { response: String? ->
                    hideProgressDialog()
                    Timber.e(response)
                    // categoryArrayList.clear();
                    try {
                        val jsonObject = JSONObject(response!!)
                        var data = OfferDeleteModel()
                        val gson = Gson()
                        data = gson.fromJson(jsonObject.toString(), OfferDeleteModel::class.java)
                        initialStage()
                        //       showToast(data.getMessage(), this);
                    } catch (e: JSONException) {
                        hideProgressDialog()
                        Timber.e(e.toString())
                        e.printStackTrace()
                    }
                },
                com.android.volley.Response.ErrorListener { error: VolleyError ->
                    //  swipeRefresh.setRefreshing(false);
                    hideProgressDialog()
                    errorHandle1(error.networkResponse)
                }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                //    map1.put("X-Requested-With", "XMLHttpRequest");
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this@TaskDetailsActivity)
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
    }

    fun addToBookmark() {
        toolbar.menu.findItem(R.id.menu_bookmark).setIcon(R.drawable.ic_bookmark_white_filled_background_white_32dp)
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Constant.URL_TASKS + "/" + taskModel!!.slug + "/bookmark",
                com.android.volley.Response.Listener { response: String? ->
                    toolbar.menu.findItem(R.id.menu_bookmark).setIcon(R.drawable.ic_bookmark_white_filled_background_white_32dp)
                    getData
                },
                com.android.volley.Response.ErrorListener { error: VolleyError ->
                    errorHandle1(error.networkResponse)
                    toolbar.menu.findItem(R.id.menu_bookmark).setIcon(R.drawable.ic_bookmark_white_background_white_32dp)
                }) {
            override fun getParams(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["is_important"] = "0"
                return map1
            }

            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["X-Requested-With"] = "XMLHttpRequest"
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this@TaskDetailsActivity)
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
    }

    private fun removeBookmark() {
        toolbar.menu.findItem(R.id.menu_bookmark).setIcon(R.drawable.ic_bookmark_white_background_white_32dp)
        val stringRequest: StringRequest = object : StringRequest(Method.DELETE, Constant.BASE_URL + "bookmarks/" + taskModel!!.bookmarkID,
                com.android.volley.Response.Listener { response: String? ->
                    Timber.e(response)
                    try {
                        val jsonObject = JSONObject(response!!)
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                toolbar.menu.findItem(R.id.menu_bookmark).setIcon(R.drawable.ic_bookmark_white_background_white_32dp)
                                getData
                                if (SavedTaskActivity.onRemoveSavedtasklistener != null) {
                                    SavedTaskActivity.onRemoveSavedtasklistener!!.onRemoveSavedTask()
                                }
                            } else {
                                showToast("Task not deleted", this@TaskDetailsActivity)
                            }
                        } else {
                            showToast(getString(R.string.server_went_wrong), this@TaskDetailsActivity)
                        }
                    } catch (e: JSONException) {
                        hideProgressDialog()
                        e.printStackTrace()
                        toolbar.menu.findItem(R.id.menu_bookmark).setIcon(R.drawable.ic_bookmark_white_background_white_32dp)
                    }
                },
                com.android.volley.Response.ErrorListener { error: VolleyError ->
                    //  swipeRefresh.setRefreshing(false);
                    errorHandle1(error.networkResponse)
                }) {
            override fun getParams(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["is_important"] = "1"
                return map1
            }

            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["X-Requested-With"] = "XMLHttpRequest"
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this@TaskDetailsActivity)
        requestQueue.add(stringRequest)
    }

    fun showRequirementDialog() {
        if (mBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        @SuppressLint("InflateParams") val view = layoutInflater.inflate(R.layout.sheet_requirement, null)
        val lyt_btn_make_an_offer = view.findViewById<LinearLayout>(R.id.lyt_btn_make_an_offer)
        val card_make_an_offer: CardView = view.findViewById(R.id.card_make_an_offer)
        val adapter: MustHaveListAdapter
        val addTagList = ArrayList<MustHaveModel>()
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(SpacingItemDecoration(1, Tools.dpToPx(this, 5), true))
        recyclerView.setHasFixedSize(true)
        for (i in taskModel!!.musthave.indices) {
            val mustHaveModel = MustHaveModel()
            mustHaveModel.mustHaveTitle = taskModel!!.musthave[i]
            mustHaveModel.isChecked = false
            addTagList.add(mustHaveModel)
        }
        adapter = MustHaveListAdapter(this, addTagList)
        adapter.onCheckedAllItems {
            if (adapter.isAllSelected) {
                card_make_an_offer.backgroundTintList = ContextCompat.getColorStateList(this@TaskDetailsActivity,
                        R.color.colorPrimary)
            } else {
                card_make_an_offer.backgroundTintList = ContextCompat.getColorStateList(this@TaskDetailsActivity,
                        R.color.colorAccent)
            }
        }
        lyt_btn_make_an_offer.setOnClickListener { v: View? ->
            if (adapter.isAllSelected) {
                val intent = Intent(this@TaskDetailsActivity, MakeAnOfferActivity::class.java)
                val bundle = Bundle()
                bundle.putInt("id", taskModel!!.id)
                bundle.putInt("budget", taskModel!!.budget)
                intent.putExtras(bundle)
                startActivity(intent)
                mBottomSheetDialog!!.dismiss()
            } else {
                showToast("Please select all must-have requirement", this)
            }
        }
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        mBottomSheetDialog = BottomSheetDialog(this)
        mBottomSheetDialog!!.setContentView(view)
        mBottomSheetDialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // set background transparent
        (view.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))
        mBottomSheetDialog!!.show()
        mBottomSheetDialog!!.setOnDismissListener { dialog: DialogInterface? -> mBottomSheetDialog = null }
    }

    private fun initCancelled() {
        if (alertType == AlertType.CANCELLED) {
            hideAlertBox()
        }
        if (taskModel!!.status.toLowerCase() == "cancelled") {
            showCancelledCard()
        }
    }

    private fun initCancellation() {
        if (alertType == AlertType.CANCELLATION) {
            hideAlertBox()
        }
        if (taskModel!!.cancellation == null) return
        if (isUserThePoster) {
            if (taskModel!!.cancellation.status.equals(ConstantKey.CANCELLATION_PENDING, ignoreCase = true)) {
                showCancellationCard(false, taskModel!!.cancellation.requesterId != sessionManager.userAccount.id)
                cardMakeAnOffer.visibility = View.GONE
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_cancellation, false)
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_reschedule, false)
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_increase_budget, false)
            }
        } else {
            if (taskModel!!.cancellation.status.equals(ConstantKey.CANCELLATION_PENDING, ignoreCase = true) &&
                    isUserTheTicker) {
                showCancellationCard(true, taskModel!!.cancellation.requesterId == sessionManager.userAccount.id)
                cardMakeAnOffer.visibility = View.GONE
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_cancellation, false)
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_reschedule, false)
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_increase_budget, false)
            }
        }
    }

    private fun initReleaseMoney() {
        if (alertType == AlertType.ASK_TO_RELEASE) {
            hideAlertBox()
        } else if (alertType == AlertType.CONFIRM_RELEASE) {
            hideAlertBox()
        }
        if (taskModel!!.status.toLowerCase() == "completed") {
            if (isUserThePoster) {
                showConfirmReleaseCard()
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_cancellation, false)
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_reschedule, false)
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_increase_budget, false)
            } else if (isUserTheTicker) {
                showAskToReleaseCard()
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_cancellation, false)
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_reschedule, false)
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_increase_budget, false)
            }
        }
    }

    private fun initReview() {
        if (alertType == AlertType.REVIEW) {
            hideAlertBox()
        }
        if (taskModel!!.status.toLowerCase() != "closed") return
        if (taskModel!!.reviewModels == null) {
            if (isUserThePoster || isUserTheTicker) showReviewCard()
            return
        }
        var showReview = true
        val reviewModels = taskModel!!.reviewModels
        //for poster
        if (isUserThePoster) {
            for (reviewModel in reviewModels) {
                if (reviewModel.rateeType == "worker") {
                    showReview = false
                    break
                }
            }
        } //for worker
        else {
            for (reviewModel in reviewModels) {
                if (reviewModel.rateeType == "poster") {
                    showReview = false
                    break
                }
            }
        }
        if (showReview && (isUserTheTicker || isUserThePoster)) showReviewCard()
    }

    private fun initRescheduleTime() {
        if (alertType == AlertType.RESCHEDULE) {
            hideAlertBox()
        }
        if (taskModel!!.rescheduleReqeust != null && taskModel!!.rescheduleReqeust.size > 0) {
            for (i in taskModel!!.rescheduleReqeust.indices) {
                if (taskModel!!.rescheduleReqeust[i].status == "pending") {
                    if (taskModel!!.status.toLowerCase() != Constant.TASK_CANCELLED && taskModel!!.status.toLowerCase() != Constant.TASK_CLOSED) {
                        pos = i
                        if (isUserThePoster || isUserTheTicker) {
                            showRescheduleTimeCard(i)
                            toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_cancellation, false)
                            toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_reschedule, false)
                            toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_increase_budget, false)
                            break
                        }
                    }
                }
            }
        }
    }

    private fun initJobReceipt() {
        toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_job_receipt, false)
        if (isUserTheTicker || isUserThePoster) toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_job_receipt, taskModel!!.status.toLowerCase() == Constant.TASK_CLOSED)
    }

    private fun initIncreaseBudget() {
        if (alertType == AlertType.INCREASE_BUDGET) {
            hideAlertBox()
        }
        if (isUserThePoster) {
            toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_increase_budget, false)
        }
        if (taskModel!!.additionalFund != null && taskModel!!.additionalFund.status == "pending") {
            if (isUserThePoster) {
                showIncreaseBudgetCard()
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_cancellation, false)
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_reschedule, false)
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_increase_budget, false)
            } else if (isUserTheTicker) {
                showIncreaseBudgetCard()
                //TODO: updated design of mahan should be applied here
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_cancellation, false)
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_reschedule, false)
                toolbar.menu.findItem(R.id.item_three_dot).subMenu.setGroupVisible(R.id.grp_increase_budget, false)
            }
        }
    }

    private var pos = 0
    private fun showDialogRescheduleRequest(pos: Int, isMine: Boolean) {
        val fragmentManager = supportFragmentManager
        val dialog = newInstance(taskModel!!, pos, isMine)
        dialog.show(fragmentManager, "")
    }

    private fun showDialogIncreaseBudgetRequest() {
        val fragmentManager = supportFragmentManager
        val dialog = IncreaseBudgetBottomSheet.newInstance(taskModel!!, 0)
        dialog.show(fragmentManager, "")
    }

    private fun showDialogIncreaseBudgetNoticeRequest(isMine: Boolean) {
        val fragmentManager = supportFragmentManager
        val dialog = IncreaseBudgetNoticeBottomSheet.newInstance(taskModel!!, isMine)
        dialog.show(fragmentManager, "")
    }

    private fun showDialogIncreaseBudgetDeclineRequest() {
        val fragmentManager = supportFragmentManager
        val dialog = IncreaseBudgetDeclineBottomSheet.newInstance(taskModel!!)
        dialog.show(fragmentManager, "")
    }

    private fun showCustomDialogAskToReleaseMoney() {
        if (taskModel!!.additionalFund != null && taskModel!!.additionalFund.status == "pending") {
            showToast("Increase price request already pending. You either delete or wait for poster response on that.", this)
            return
        }
        if (taskModel!!.rescheduleReqeust != null && taskModel!!.rescheduleReqeust.size > 0) {
            for (i in taskModel!!.rescheduleReqeust.indices) {
                if (taskModel!!.rescheduleReqeust[i].status == "pending") {
                    if (taskModel!!.status.toLowerCase() != Constant.TASK_CANCELLED && taskModel!!.status.toLowerCase() != Constant.TASK_CLOSED) {
                        pos = i
                        if (isUserThePoster || isUserTheTicker) {
                            showToast("Reschedule time request already pending. You either delete or wait for poster response on that.", this)
                            return
                        }
                    }
                }
            }
        }
        val fragmentManager = supportFragmentManager
        val dialog = ConfirmAskToReleaseBottomSheet(baseContext)
        dialog.show(fragmentManager, "")
    }

    private fun showCustomDialogReleaseMoney() {
        val fragmentManager = supportFragmentManager
        val dialog = ConfirmReleaseBottomSheet(baseContext)
        dialog.show(fragmentManager, "")
    }

    private fun showCancelledCard() {
        showAlertBox(Html.fromHtml("This job has been canceled"),
                ConstantKey.BTN_POST_NEW_JOB, AlertType.CANCELLED, true)
    }

    private var isTicker = false
    private var tickerCancels = false
    private var cancellationTitle: String? = null
    private fun showCancellationCard(isTicker: Boolean, tickerCancels: Boolean) {
        this.isTicker = isTicker
        this.tickerCancels = tickerCancels
        if (isTicker && tickerCancels) {
            cancellationTitle = "<b>You</b> have requested to cancel this job on <b>" +
                    TimeHelper.convertToShowTimeFormat(taskModel!!.cancellation.createdAt) + "</b>"
            showAlertBox(Html.fromHtml(cancellationTitle), ConstantKey.BTN_VIEW_CANCELLATION_REQUEST,
                    AlertType.CANCELLATION, true)
        }
        if (!isTicker && !tickerCancels) {
            cancellationTitle = "<b>You</b> have requested to cancel this job on <b>" +
                    TimeHelper.convertToShowTimeFormat(taskModel!!.cancellation.createdAt) + "</b>"
            showAlertBox(Html.fromHtml(cancellationTitle), ConstantKey.BTN_VIEW_CANCELLATION_REQUEST,
                    AlertType.CANCELLATION, true)
        }
        if (isTicker && !tickerCancels) {
            val cancelledByWho = taskModel!!.poster.name
            cancellationTitle = "<b>" + cancelledByWho + "</b> " +
                    "has requested to cancel this job on <b>" +
                    TimeHelper.convertToShowTimeFormat(taskModel!!.cancellation.createdAt) + "</b>"
            showAlertBox(Html.fromHtml(cancellationTitle), ConstantKey.BTN_VIEW_CANCELLATION_REQUEST,
                    AlertType.CANCELLATION, true)
        }
        if (!isTicker && tickerCancels) {
            val cancelledByWho = taskModel!!.worker.name
            cancellationTitle = "<b>" + cancelledByWho + "</b> " +
                    "has requested to cancel this job on <b>" +
                    TimeHelper.convertToShowTimeFormat(taskModel!!.cancellation.createdAt) + "</b>"
            showAlertBox(Html.fromHtml(cancellationTitle), ConstantKey.BTN_VIEW_CANCELLATION_REQUEST,
                    AlertType.CANCELLATION, true)
        }
    }

    private fun showRescheduleTimeCard(pos: Int) {
        if (taskModel!!.rescheduleReqeust[pos] == null) return
        var rescheduledByWho = ""
        val requesterId = taskModel!!.rescheduleReqeust[pos].requester_id
        if (taskModel!!.worker != null) {
            if (taskModel!!.worker.id == requesterId) {
                rescheduledByWho = taskModel!!.worker.name
            }
        }
        if (taskModel!!.poster != null) {
            if (taskModel!!.poster.id == requesterId) {
                rescheduledByWho = taskModel!!.poster.name
            }
        }
        if (sessionManager.userAccount.id == requesterId) {
            isRescheduledRequestForMine = true
            rescheduledByWho = "You"
        } else {
            isRescheduledRequestForMine = false
        }
        showAlertBox(Html.fromHtml("<b>" + rescheduledByWho + "</b> " +
                "has requested to reschedule time for this job on <b>" +
                TimeHelper.convertToShowTimeFormat(taskModel!!.rescheduleReqeust[pos].created_at) + "</b>"),
                ConstantKey.BTN_RESCHEDULE_REQUEST_SENT, AlertType.RESCHEDULE, true)
    }

    var isIncreaseBudgetRequestForMine = false
    var isRescheduledRequestForMine = false
    private fun showIncreaseBudgetCard() {
        var increaseRequestByWho = ""
        val requesterId = taskModel!!.additionalFund.requesterId
        if (taskModel!!.worker != null) {
            if (taskModel!!.worker.id == requesterId) {
                increaseRequestByWho = taskModel!!.worker.name
            }
        }
        if (taskModel!!.poster != null) {
            if (taskModel!!.poster.id == requesterId) {
                increaseRequestByWho = taskModel!!.poster.name
            }
        }
        if (sessionManager.userAccount.id == requesterId) {
            isIncreaseBudgetRequestForMine = true
            increaseRequestByWho = "You"
        } else {
            isIncreaseBudgetRequestForMine = false
        }
        showAlertBox(Html.fromHtml("<b>" + increaseRequestByWho + "</b> " +
                "has requested to increase price on this job on <b>" +
                TimeHelper.convertToShowTimeFormat(taskModel!!.additionalFund.createdAt) + "</b>"),
                ConstantKey.BTN_INCREASE_BUDGET_REQUEST_SENT, AlertType.INCREASE_BUDGET, true)
    }

    private fun showReviewCard() {
        val writeAReviewForWho: String
        writeAReviewForWho = if (isUserThePoster) taskModel!!.worker.name else taskModel!!.poster.name
        showAlertBox(Html.fromHtml(
                "Make our community more trusted by leaving a review for <b>$writeAReviewForWho</b>"),
                ConstantKey.BTN_LEAVE_A_REVIEW, AlertType.REVIEW, true)
    }

    private fun showAskToReleaseCard() {
        showAlertBox(Html.fromHtml(
                "You have requested to release money this job on <b>" +
                        TimeHelper.convertToShowTimeFormat(taskModel!!.conversation.task.completedAt) + "</b>"),
                null, AlertType.ASK_TO_RELEASE, false)
    }

    private fun showConfirmReleaseCard() {
        val whoRequestToReleaseMoney = taskModel!!.worker.name
        showAlertBox(Html.fromHtml("<b>" + whoRequestToReleaseMoney + "</b> " +
                " have requested to release money this job on <b>" +
                TimeHelper.convertToShowTimeFormat(taskModel!!.conversation.task.completedAt) + "</b>"),
                ConstantKey.BTN_CONFIRM_RELEASE_MONEY, AlertType.CONFIRM_RELEASE, true)
    }

    //we use spanned to support middle bolds
    private fun showAlertBox(title: Spanned, buttonText: String?, alertType: AlertType,
                             hasButton: Boolean) {
        alertBox.visibility = View.VISIBLE
        this.alertType = alertType
        alertBox.setTitle(title)
        alertBox.isHasButton = hasButton
        alertBox.buttonText = buttonText
    }

    private fun hideAlertBox() {
        alertBox.visibility = View.GONE
        alertType = null
    }

    override fun onExtendedAlertButtonClick() {
        if (alertType == null) {
            return
        }
        when (alertType) {
            AlertType.CANCELLATION -> {
                val intent: Intent
                intent = if (tickerCancels && isTicker) {
                    Intent(this, TTCancellationSummaryActivity::class.java)
                } else if (tickerCancels && !isTicker) {
                    Intent(this, TPCancellationSummaryActivity::class.java)
                } else if (!tickerCancels && isTicker) {
                    Intent(this, PTCancellationSummaryActivity::class.java)
                } else {
                    Intent(this, PPCancellationSummaryActivity::class.java)
                }
                val bundle = Bundle()
                bundle.putString(ConstantKey.CANCELLATION_TITLE, cancellationTitle)
                intent.putExtras(bundle)
                startActivityForResult(intent, ConstantKey.RESULTCODE_CANCELLATION)
            }
            AlertType.RESCHEDULE -> {
                showDialogRescheduleRequest(pos, isRescheduledRequestForMine)
            }
            AlertType.INCREASE_BUDGET -> {
                showDialogIncreaseBudgetNoticeRequest(isIncreaseBudgetRequestForMine)
            }
            AlertType.REVIEW -> {
                val intent = Intent(this@TaskDetailsActivity, LeaveReviewActivity::class.java)
                val bundle = Bundle()
                //    bundle.putParcelable(ConstantKey.TASK, taskModel!!);
                bundle.putBoolean(ConstantKey.IS_MY_TASK, isUserThePoster)
                intent.putExtras(bundle)
                startActivityForResult(intent, ConstantKey.RESULTCODE_WRITE_REVIEW)
            }
            AlertType.CANCELLED -> {
                val infoBottomSheet = CategoryListBottomSheet(sessionManager)
                infoBottomSheet.show(supportFragmentManager, null)
            }
            AlertType.CONFIRM_RELEASE -> {
                showCustomDialogReleaseMoney()
            }
        }
    }

    private fun getConversationId(slug: String, targetId: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.BASE_URL_v2 + "jobs/" + slug + "/start_chat/" + targetId,
                com.android.volley.Response.Listener { response: String? ->
                    Timber.e(response)
                    try {
                        val jsonObject = JSONObject(response!!)
                        val gson = Gson()
                        val chatModel = ChatModel()
                        val sender = UserAccountModel()
                        val reciver = UserAccountModel()
                        val senderA = AttachmentModel()
                        val reciverA = AttachmentModel()
                        val attachment = AttachmentModel()
                        val (data, _, success) = gson.fromJson(jsonObject.toString(), GetConversationInfoResponse::class.java)
                        if (success!!) {
                            if (data!!.last_message != null) {
                                chatModel.id = data.last_message!!.id
                                chatModel.conversationId = data.last_message.conversation_id
                                chatModel.createdAt = data.last_message.created_at
                                chatModel.message = data.last_message.message
                                chatModel.senderId = data.last_message.sender_id
                                chatModel.isSeen = data.last_message.is_seen
                                if (data.last_message.attachment != null) {
                                    attachment.url = data.last_message.attachment.url
                                    attachment.id = data.last_message.attachment.id
                                    attachment.thumbUrl = data.last_message.attachment.thumb_url
                                    attachment.name = data.last_message.attachment.name
                                    attachment.modalUrl = data.last_message.attachment.modal_url
                                    attachment.mime = data.last_message.attachment.mime
                                    attachment.createdAt = data.last_message.attachment.created_at
                                    chatModel.attachment = attachment
                                }
                            }
                            if (data.users!!.size == 2) {
                                var senderId = 0
                                var reciverId = 0
                                for (i in 0..1) {
                                    if (data.users[i].id === sessionManager.userAccount.id) {
                                        senderId = i
                                        if (senderId == 0) {
                                            reciverId = 1
                                        }
                                    }
                                }
                                senderA.createdAt = data.users[senderId].avatar!!.created_at
                                senderA.fileName = data.users[senderId].avatar!!.file_name
                                senderA.id = data.users[senderId].avatar!!.id
                                senderA.mime = data.users[senderId].avatar!!.mime
                                senderA.modalUrl = data.users[senderId].avatar!!.modal_url
                                senderA.name = data.users[senderId].avatar!!.name
                                senderA.thumbUrl = data.users[senderId].avatar!!.thumb_url
                                senderA.url = data.users[senderId].avatar!!.url
                                sender.avatar = senderA
                                if (data.users[senderId].position != null) {
                                    if (data.users[senderId].position!!.latitude != null) sender.latitude = data.users[senderId].position!!.latitude
                                    if (data.users[senderId].position!!.longitude != null) sender.longitude = data.users[senderId].position!!.longitude
                                }
                                if (data.users[senderId].last_online != null) sender.lastOnline = data.users[senderId].last_online
                                sender.name = data.users[senderId].name
                                sender.id = data.users[senderId].id
                                reciverA.createdAt = data.users[reciverId].avatar!!.created_at
                                reciverA.fileName = data.users[reciverId].avatar!!.file_name
                                reciverA.id = data.users[reciverId].avatar!!.id
                                reciverA.mime = data.users[reciverId].avatar!!.mime
                                reciverA.modalUrl = data.users[reciverId].avatar!!.modal_url
                                reciverA.name = data.users[reciverId].avatar!!.name
                                reciverA.thumbUrl = data.users[reciverId].avatar!!.thumb_url
                                reciverA.url = data.users[reciverId].avatar!!.url
                                reciver.avatar = reciverA
                                if (data.users[reciverId].position != null) {
                                    if (data.users[reciverId].position!!.longitude != null) reciver.longitude = data.users[reciverId].position!!.longitude
                                    if (data.users[reciverId].position!!.latitude != null) reciver.latitude = data.users[reciverId].position!!.latitude
                                }
                                reciver.lastOnline = data.users[reciverId].last_online
                                reciver.name = data.users[reciverId].name
                                reciver.id = data.users[reciverId].id
                            }
                            val conversationModel = ConversationModel(data.id, data.name, data.task!!.id,
                                    chatModel,
                                    data.unseen_count, data.created_at,
                                    sender,
                                    reciver,
                                    data.task.slug, data.task.status, data.chat_closed)
                            val intent = Intent(this@TaskDetailsActivity, ChatActivity::class.java)
                            val bundle = Bundle()
                            bundle.putParcelable(ConstantKey.CONVERSATION, conversationModel)
                            intent.putExtras(bundle)
                            startActivityForResult(intent, ConstantKey.RESULTCODE_PRIVATE_CHAT)
                        } else {
                            showToast("Something Went Wrong", this)
                        }
                    } catch (e: Exception) {
                        Timber.e(e.toString())
                        e.printStackTrace()
                        showToast("Something Went Wrong", this)
                    }
                    isGetBankAccountLoaded = true
                    onLoadingFinished()
                },
                com.android.volley.Response.ErrorListener { error: VolleyError ->
                    showToast("Something Went Wrong", this)
                    Timber.e(error.toString())
                    hideProgressDialog()
                    onLoadingFinished()
                }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["X-Requested-With"] = "XMLHttpRequest"
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    override fun onAskToReleaseConfirmClick() {
        submitAskToReleaseMoney()
    }

    override fun onReleaseConfirmClick() {
        submitReleaseMoney()
    }

    override fun onIncreaseBudgetAcceptClick() {
        getData
    }

    override fun onIncreaseBudgetRejectClick() {
        showDialogIncreaseBudgetDeclineRequest()
    }

    override fun onIncreaseBudgetWithDrawClick() {
        getData
    }

    override fun onIncreaseBudgetSubmitClick() {
        getData
    }

    override fun onRescheduleTimeAcceptDeclineClick() {
        getData
    }

    override fun onRescheduleWithDraw() {
        getData
    }

    override fun onSubmitIncreasePrice() {
        getData
    }

    enum class AlertType {
        CANCELLATION, RESCHEDULE, INCREASE_BUDGET, ASK_TO_RELEASE, CONFIRM_RELEASE, REVIEW, CANCELLED //more we add later
    }

    companion object {
        @JvmField
        var taskModel: TaskModel? = TaskModel()
        var isOfferQuestion = ""
        var offerModel: OfferModel? = null
        var questionModel: QuestionModel? = null
        var requestAcceptListener: OnRequestAcceptListener? = null

        @JvmField
        var widthDrawListener: OnWidthDrawListener? = null
        private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123
        private const val GALLERY_PICKUP_IMAGE_REQUEST_CODE = 400
    }
}