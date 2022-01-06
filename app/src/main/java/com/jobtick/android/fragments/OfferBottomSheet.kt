package com.jobtick.android.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.activities.*
import com.jobtick.android.models.*
import com.jobtick.android.models.response.conversationinfo.GetConversationInfoResponse
import com.jobtick.android.utils.*
import com.mikhaellopez.circularimageview.CircularImageView
import com.tapadoo.alerter.Alerter
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class OfferBottomSheet(
    private val item: OfferModel,
    private val isUserThePoster: Boolean,
    private val sessionManager: SessionManager,
    private val isAssigned: Boolean,
    private val isMyOffer: Boolean
) : BottomSheetDialogFragment() {
    lateinit var cardLiveVideo: CardView
    lateinit var txtMessage: TextView
    lateinit var txtName: TextView
    lateinit var txtActionLeft: TextView
    lateinit var txtCompletionRate: TextView
    lateinit var txtBudget: TextView
    lateinit var imgOfferOnTask: ImageView
    lateinit var starRatingBar: RatingBar
    lateinit var imgAvatar: CircularImageView
    lateinit var lnAction: LinearLayout
    lateinit var ivFlag: ImageView
    lateinit var imgBtnPlay: ImageView
    lateinit var lnOfferMessage: ConstraintLayout
    private var spanS: Spannable? = null
    private var spanF: Spannable? = null
    lateinit var offerBottomSheetClick: OfferBottomSheetClick

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_offer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setIDs()
        setUI()
    }

    private fun setIDs() {

        cardLiveVideo = requireView().findViewById(R.id.card_live_video)
        txtMessage = requireView().findViewById(R.id.txt_message)
        txtName = requireView().findViewById(R.id.txt_name)
        txtActionLeft = requireView().findViewById(R.id.txt_action_left)
        txtCompletionRate = requireView().findViewById(R.id.txt_completion_rate)
        imgOfferOnTask = requireView().findViewById(R.id.img_offer_on_task)
        lnAction = requireView().findViewById(R.id.ln_accept_offer)
        txtBudget = requireView().findViewById(R.id.txt_budget)
        imgAvatar = requireView().findViewById(R.id.img_avatar)
        lnOfferMessage = requireView().findViewById(R.id.cons_message)
        ivFlag = requireView().findViewById(R.id.img_report)
        imgBtnPlay = requireView().findViewById(R.id.img_btn_play)
        starRatingBar = requireView().findViewById(R.id.ratingbar_worker)
    }

    @SuppressLint("SetTextI18n")
    private fun setUI() {
        txtActionLeft.text = when {
            isAssigned -> "Reschedule"
            isMyOffer -> "Withdraw"
            else -> "Accept"
        }

        if (isAssigned or isMyOffer) {
            txtActionLeft.setTextColor(ContextCompat.getColor(requireContext(), R.color.P300))
            lnAction.setBackgroundShape(
                ContextCompat.getColor(requireContext(), R.color.transparent),
                ContextCompat.getColor(requireContext(), R.color.P300),
                8,
                1,
                GradientDrawable.RECTANGLE
            )
        } else {
            lnAction.setBackgroundShape(
                ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                8,
                GradientDrawable.RECTANGLE
            )
        }
        lnOfferMessage.setOnClickListener {
            onItemOfferClick("message")
        }
        lnAction.setOnClickListener {
            when {
                isAssigned -> onItemOfferClick("reschedule")
                isMyOffer -> onItemOfferClick("withdraw")
                else -> onItemOfferClick("accept")
            }
        }
        if (isMyOffer) {
            lnOfferMessage.visibility = View.GONE
            val param = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (50).dpToPx(),
                10.0f
            )
            lnAction.layoutParams = param
        }

        ivFlag.setOnClickListener {
            onItemOfferClick("report")
        }
        imgAvatar.setOnClickListener {
            onItemOfferClick("profile")
        }

        txtName.setOnClickListener {
            onItemOfferClick("profile")
        }

        txtCompletionRate.text = item.worker.workTaskStatistics.completionRate.toString() + "%"
        if (item.worker.avatar != null) {
            ImageUtil.displayImage(imgAvatar, item.worker.avatar.thumbUrl, null)
        }
        txtName.text = item.worker.name
        txtBudget.text = item.offerPrice.toString()
        if (item.worker != null && item.worker.workerRatings != null && item.worker.workerRatings.avgRating != null) {
            starRatingBar.rating = item.worker.workerRatings.avgRating
        } else {
            starRatingBar.rating = 0f
        }

        if (item.attachments != null && item.attachments.isNotEmpty()) {
            cardLiveVideo.visibility = View.VISIBLE
            txtMessage.visibility = View.GONE
            ImageUtil.displayImage(imgOfferOnTask, item.attachments[0].modalUrl, null)

            imgBtnPlay.setOnClickListener {
                if (item.attachments[0] == null || item.attachments[0].url == null) {
                    (requireActivity() as ActivityBase).showToast(
                        "Sorry, there is no video to play.",
                        requireContext()
                    )
                    return@setOnClickListener
                }
                val intent = Intent(requireActivity(), VideoPlayerActivity::class.java)
                intent.putExtra(ConstantKey.VIDEO_PATH, "" + item.attachments[0].url)
                requireActivity().startActivity(intent)
            }
        } else {
            cardLiveVideo.visibility = View.GONE
            txtMessage.visibility = View.VISIBLE

            txtMessage.text = item.message
            spanS = null
            spanF = null
            txtMessage.post {
                val lineCount = txtMessage.lineCount
                if (lineCount > Constant.MAX_LINE_TEXTVIEW_MORE_4) {
                    spanF = SpannableString(txtMessage.text.toString() + "  Less")
                    spanF!!.setSpan(
                        ForegroundColorSpan(Color.BLUE),
                        txtMessage.text.toString().length,
                        txtMessage.text.toString().length + 6,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    val end = txtMessage.layout.getLineStart(4)
                    spanS =
                        SpannableString(txtMessage.text.toString().substring(0, end - 1) + "  More")
                    spanS!!.setSpan(
                        ForegroundColorSpan(Color.BLUE),
                        txtMessage.text.toString().substring(0, end).length,
                        txtMessage.text.toString().substring(0, end - 1).length + 6,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    txtMessage.text = spanS
                    item.isUserPrefrenceToMore = true
                    if (item.isUserPrefrenceToMore) {
                        txtMessage.maxLines = Constant.MAX_LINE_TEXTVIEW_MORE_4
                    }
                }

            }
        }

    }


    fun showToast(content: String?, context: Context?) {
        Alerter.create(activity)
            .setTitle("")
            .setText(content!!)
            .setBackgroundResource(R.color.colorRedError)
            .show()
    }

    //Adapter override method
    private fun onItemOfferClick(action: String) {
        when {
            action.equals("reply", ignoreCase = true) -> {
                val intent = Intent(requireContext(), PublicChatActivity::class.java)
                val bundle = Bundle()
                TaskDetailsActivity.offerModel = item
                TaskDetailsActivity.isOfferQuestion = "offer"
                bundle.putBoolean("isPoster", isUserThePoster)
                bundle.putString("posterID", TaskDetailsActivity.taskModel!!.poster.id.toString())
                intent.putExtras(bundle)
                startActivityForResult(intent, 20)
            }
            action.equals("accept", ignoreCase = true) -> {
                val intent = Intent(requireContext(), PaymentOverviewActivity::class.java)
                val bundle = Bundle()
                TaskDetailsActivity.offerModel = item
                intent.putExtras(bundle)
                startActivityForResult(intent, ConstantKey.RESULTCODE_PAYMENTOVERVIEW)
            }
            action.equals("report", ignoreCase = true) -> {
                val intent = Intent(requireContext(), ReportActivity::class.java)
                val bundle = Bundle()
                bundle.putString(ConstantKey.SLUG, TaskDetailsActivity.taskModel!!.slug)
                bundle.putString("key", ConstantKey.KEY_OFFER_REPORT)
                intent.putExtras(bundle)
                startActivity(intent)
            }
            action.equals("message", ignoreCase = true) -> {
                getConversationId(TaskDetailsActivity.taskModel!!.slug, item.worker.id.toString())
            }

            action.equals("profile", ignoreCase = true) -> {
                val intent = Intent(requireActivity(), ProfileActivity::class.java)
                intent.putExtra("id", item.worker.id.toInt())
                startActivity(intent)
            }

            action.equals("reschedule", ignoreCase = true) or action.equals(
                "withdraw",
                ignoreCase = true
            ) -> {
                this.dismiss()
                offerBottomSheetClick.onClickOnOffer(item, action)
            }
        }
    }

    private fun getConversationId(slug: String, targetId: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.GET,
            Constant.BASE_URL_v2 + "jobs/" + slug + "/start_chat/" + targetId,
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
                    val (data, _, success) = gson.fromJson(
                        jsonObject.toString(),
                        GetConversationInfoResponse::class.java
                    )
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
                            if (data.users[senderId].avatar != null) {
                                senderA.createdAt = data.users[senderId].avatar!!.created_at
                                senderA.fileName = data.users[senderId].avatar!!.file_name
                                senderA.id = data.users[senderId].avatar!!.id
                                senderA.mime = data.users[senderId].avatar!!.mime
                                senderA.modalUrl = data.users[senderId].avatar!!.modal_url
                                senderA.name = data.users[senderId].avatar!!.name
                                senderA.thumbUrl = data.users[senderId].avatar!!.thumb_url
                                senderA.url = data.users[senderId].avatar!!.url
                            }
                            sender.avatar = senderA
                            if (data.users[senderId].position != null) {
                                if (data.users[senderId].position!!.latitude != null) sender.latitude =
                                    data.users[senderId].position!!.latitude
                                if (data.users[senderId].position!!.longitude != null) sender.longitude =
                                    data.users[senderId].position!!.longitude
                            }
                            if (data.users[senderId].last_online != null) sender.lastOnline =
                                data.users[senderId].last_online
                            sender.name = data.users[senderId].name
                            sender.id = data.users[senderId].id
                            if (data.users[reciverId].avatar != null) {
                                reciverA.createdAt = data.users[reciverId].avatar!!.created_at
                                reciverA.fileName = data.users[reciverId].avatar!!.file_name
                                reciverA.id = data.users[reciverId].avatar!!.id
                                reciverA.mime = data.users[reciverId].avatar!!.mime
                                reciverA.modalUrl = data.users[reciverId].avatar!!.modal_url
                                reciverA.name = data.users[reciverId].avatar!!.name
                                reciverA.thumbUrl = data.users[reciverId].avatar!!.thumb_url
                                reciverA.url = data.users[reciverId].avatar!!.url
                            }
                            reciver.avatar = reciverA
                            if (data.users[reciverId].position != null) {
                                if (data.users[reciverId].position!!.longitude != null) reciver.longitude =
                                    data.users[reciverId].position!!.longitude
                                if (data.users[reciverId].position!!.latitude != null) reciver.latitude =
                                    data.users[reciverId].position!!.latitude
                            }
                            reciver.lastOnline = data.users[reciverId].last_online
                            reciver.name = data.users[reciverId].name
                            reciver.id = data.users[reciverId].id
                        }
                        val conversationModel = ConversationModel(
                            data.id, data.name, data.task!!.id,
                            chatModel,
                            data.unseen_count, data.created_at,
                            sender,
                            reciver,
                            data.task.slug, data.task.status, data.chat_closed
                        )
                        val intent = Intent(requireContext(), ChatActivity::class.java)
                        val bundle = Bundle()
                        bundle.putParcelable(ConstantKey.CONVERSATION, conversationModel)
                        intent.putExtras(bundle)
                        startActivityForResult(intent, ConstantKey.RESULTCODE_PRIVATE_CHAT)
                    } else {
                        showToast("Something Went Wrong", requireContext())
                    }
                } catch (e: Exception) {
                    Timber.e(e.toString())
                    e.printStackTrace()
                    showToast("Something Went Wrong", requireContext())
                }
            },
            com.android.volley.Response.ErrorListener { error: VolleyError ->
                showToast("Something Went Wrong", requireContext())
                Timber.e(error.toString())
                //hideProgressDialog()
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
        stringRequest.retryPolicy = DefaultRetryPolicy(
            0, -1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)
    }


    interface OfferBottomSheetClick {
        fun onClickOnOffer(item: OfferModel, action: String)
    }
}