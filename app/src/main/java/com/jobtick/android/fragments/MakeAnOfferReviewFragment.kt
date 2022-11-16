package com.jobtick.android.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import com.google.android.material.button.MaterialButton
import com.jobtick.android.R
import com.jobtick.android.activities.ActivityBase
import com.jobtick.android.material.ui.jobdetails.MakeAnOfferActivity
import com.jobtick.android.activities.VideoPlayerActivity
import com.jobtick.android.models.MakeAnOfferModel
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.utils.*
import java.util.*

class MakeAnOfferReviewFragment : Fragment(), View.OnClickListener {
    private lateinit var imgInfo: ImageView
    private lateinit var txtTotalBudget: TextView
    private lateinit var txtServiceFee: TextView
    private lateinit var txtReceiveBudget: TextView
    private lateinit var textOffer: TextView
    private lateinit var offerType: TextView
    private lateinit var imgBtnClose: ImageView
    private lateinit var imgBtnPlay: ImageView
    private lateinit var txtReviewConditions: TextView
    private lateinit var lytBtnSubmitOffer: MaterialButton
    private lateinit var lytRecord2: LinearLayout
    private lateinit var imgOfferOnTask: ImageView
    private lateinit var cardLiveVideo: FrameLayout
    private lateinit var ivBack: ImageView
    private var makeAnOfferModel: MakeAnOfferModel? = null
    private var reviewCallbackFunction: ReviewCallbackFunction? = null
    private var sessionManager: SessionManager? = null
    private var userAccountModel: UserAccountModel? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_make_an_offer_review, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setIds()
        makeAnOfferModel = MakeAnOfferModel()
        userAccountModel = UserAccountModel()
        sessionManager = SessionManager(requireContext())
        userAccountModel = sessionManager!!.userAccount
        if (arguments != null && requireArguments().getParcelable<Parcelable?>(ConstantKey.MAKE_AN_OFFER_MODEL) != null) {
            makeAnOfferModel = requireArguments().getParcelable(ConstantKey.MAKE_AN_OFFER_MODEL)
        }
        if (makeAnOfferModel != null) {
            txtTotalBudget.text = String.format(Locale.ENGLISH, "$%d", makeAnOfferModel!!.offer_price)
            txtServiceFee.text = String.format(Locale.ENGLISH, "$%.1f", makeAnOfferModel!!.allFee)
            txtReceiveBudget.text = String.format(Locale.ENGLISH, "$%.1f", makeAnOfferModel!!.youWillReceive)
        }
        ivBack.setOnClickListener { v: View? -> requireActivity()!!.onBackPressed() }

        // toolbar.setNavigationOnClickListener(MakeAnOfferReviewFragment.this);
        reviewConditions()
        if (makeAnOfferModel != null) if (makeAnOfferModel!!.attachment != null) {
            //initLayout();
            cardLiveVideo.visibility = View.VISIBLE
            offerType.text = "VIDEO OFFER"
            Handler(Looper.getMainLooper()).post { ImageUtil.displayImage(imgOfferOnTask, makeAnOfferModel!!.attachment.modalUrl, null) }
        } else {
            textOffer.visibility = View.VISIBLE
            offerType.text = "TEXT OFFER"
            textOffer.text = makeAnOfferModel!!.message
        }
    }
    private fun showDialog() {
        val layoutInflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.dialog_confirm_offer, null)
        val infoDialog = AlertDialog.Builder(requireContext())
                .setView(view)
                .create()
        val window = infoDialog.window;

        val wlp = window!!.attributes;
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        wlp.gravity = Gravity.CENTER
        window.attributes = wlp
        infoDialog.show()
        window.findViewById<MaterialButton>(R.id.cancel).setOnClickListener {
            infoDialog.dismiss()
        }
        window.findViewById<MaterialButton>(R.id.confirm).setOnClickListener {
            reviewCallbackFunction!!.submitButtonReview(makeAnOfferModel)
        }

    }
    private fun setIds() {
        imgInfo = requireView().findViewById(R.id.img_info)
        txtTotalBudget = requireView().findViewById(R.id.txt_total_budget)
        txtServiceFee = requireView().findViewById(R.id.txt_service_fee)
        txtReceiveBudget = requireView().findViewById(R.id.txt_receive_budget)
        textOffer = requireView().findViewById(R.id.textOffer)
        offerType = requireView().findViewById(R.id.offerType)
        imgBtnClose = requireView().findViewById(R.id.img_btn_close)
        imgBtnPlay = requireView().findViewById(R.id.img_btn_play)
        txtReviewConditions = requireView().findViewById(R.id.txt_review_conditions)
        lytBtnSubmitOffer = requireView().findViewById(R.id.lyt_btn_submit_offer)
        lytRecord2 = requireView().findViewById(R.id.lytRecord2)
        imgOfferOnTask = requireView().findViewById(R.id.img_offer_on_task)
        cardLiveVideo = requireView().findViewById(R.id.card_live_video)
        ivBack = requireView().findViewById(R.id.ivBack)
        onViewClick()
    }

    private fun reviewConditions() {
        val text = txtReviewConditions.text.toString().trim { it <= ' ' }
        val spannableString = SpannableString(text)
        val clickableSpan1: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                ExternalIntentHelper.openLink(requireActivity(), Constant.URL_terms)
            }
        }
        val clickableSpan2: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                ExternalIntentHelper.openLink(requireActivity(), Constant.URL_support)
            }
        }
        spannableString.setSpan(clickableSpan1, 40, 57, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(clickableSpan2, 62, 82, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        txtReviewConditions.text = spannableString
        txtReviewConditions.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun onViewClick() {
        imgBtnClose.setOnClickListener {
            makeAnOfferModel!!.attachment = null
            if (reviewCallbackFunction != null) {
                reviewCallbackFunction!!.backButtonReview(makeAnOfferModel)
            }
        }
        imgBtnPlay.setOnClickListener {
            if (makeAnOfferModel!!.attachment == null || makeAnOfferModel!!.attachment.url == null) {
                (requireActivity() as ActivityBase).showToast("Sorry, there is no video to play.", requireContext())
                return@setOnClickListener
            }
            val intent = Intent(requireActivity(), VideoPlayerActivity::class.java)
            intent.putExtra(ConstantKey.VIDEO_PATH, "" + makeAnOfferModel!!.attachment.url)
            requireActivity().startActivity(intent)
        }
        lytBtnSubmitOffer.setOnClickListener {
           showDialog()
        }
    }

    override fun onClick(v: View) {
        if (reviewCallbackFunction != null) {
            reviewCallbackFunction!!.backButtonReview(makeAnOfferModel)
        }
    }

    interface ReviewCallbackFunction {
        fun backButtonReview(makeAnOfferModel: MakeAnOfferModel?)
        fun submitButtonReview(makeAnOfferModel: MakeAnOfferModel?)
    }

    companion object {
        fun newInstance(makeAnOfferModel: MakeAnOfferModel?,
                        reviewCallbackFunction: ReviewCallbackFunction?): MakeAnOfferReviewFragment {
            val fragment = MakeAnOfferReviewFragment()
            fragment.reviewCallbackFunction = reviewCallbackFunction
            val args = Bundle()
            args.putParcelable(ConstantKey.MAKE_AN_OFFER_MODEL, makeAnOfferModel)
            fragment.arguments = args
            return fragment
        }
    }
}