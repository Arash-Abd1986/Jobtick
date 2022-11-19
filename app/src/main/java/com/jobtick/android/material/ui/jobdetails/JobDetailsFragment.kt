package com.jobtick.android.material.ui.jobdetails

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.activities.*
import com.jobtick.android.fragments.ConfirmAskToReleaseBottomSheet
import com.jobtick.android.fragments.ConfirmReleaseBottomSheet
import com.jobtick.android.fragments.WithdrawBottomSheet
import com.jobtick.android.models.*
import com.jobtick.android.models.response.conversationinfo.GetConversationInfoResponse
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.*
import com.jobtick.android.viewmodel.JobDetailsViewModel
import com.jobtick.android.viewmodel.ViewModelFactory
import org.json.JSONObject
import timber.log.Timber
import java.text.ParseException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class JobDetailsPosterFragment : Fragment(), WithdrawBottomSheet.Withdraw {
    private lateinit var taskStatus: MaterialTextView
    private lateinit var offerCount: MaterialTextView
    private lateinit var pDatetime: MaterialTextView
    private lateinit var date: MaterialTextView
    private lateinit var dayPart: MaterialTextView
    private lateinit var location: MaterialTextView
    private lateinit var budget: MaterialTextView
    private lateinit var direction: MaterialTextView
    private lateinit var title: MaterialTextView
    private lateinit var name: MaterialTextView
    private lateinit var seeAll: MaterialTextView
    private lateinit var description: MaterialTextView
    private lateinit var msgHeader: MaterialTextView
    private lateinit var msgAction: MaterialTextView
    private lateinit var txtRoleTitle: MaterialTextView
    private lateinit var msgBody: MaterialTextView
    private lateinit var icChat: AppCompatImageView
    private lateinit var msgIcon: AppCompatImageView
    private lateinit var icLocation: AppCompatImageView
    private lateinit var linMessage: LinearLayout
    private lateinit var btnNext: MaterialButton
    lateinit var viewModel: JobDetailsViewModel
    private lateinit var rlMedias: RecyclerView
    private lateinit var mediaAdapter: MediaAdapter
    private lateinit var sessionManager: SessionManager
    private lateinit var activity: JobDetailsActivity
    private lateinit var linNext: LinearLayout
    private lateinit var linAttachmentsTitle: LinearLayout
    private var isAllMedias = false
    private var jobState = JobState.MAKE_AN_OFFER

    private enum class JobState {
        MAKE_AN_OFFER, VIEW_OFFER_LIST, VIEW_MY_OFFER, SUBMIT_RELEASE_MONEY
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_job_details_poster, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        activity = (requireActivity() as JobDetailsActivity)

        setIds()
        initVm()
    }

    private fun initVm() {
        viewModel = ViewModelProvider(
                requireActivity(),
                ViewModelFactory(ApiHelper(ApiClient.getClient()))
        )[JobDetailsViewModel::class.java]
        viewModel.geTaskModelResponse().observe(viewLifecycleOwner) {
            setUpView(it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpView(taskModel: TaskModel) {
        if (taskModel.dueTime != null) {
            setDateTime(taskModel.dueTime, taskModel.dueDate)
            title.text = taskModel.title
            taskStatus.text = taskModel.status
            offerCount.text = getOfferCount(taskModel.offerCount)
            pDatetime.text = taskModel.createdAt
            setMoreLess(description, taskModel.description, 3)
            setTaskLocation(taskModel)
            setMedias(taskModel.attachments)
            budget.text = "$${taskModel.budget}"
            changeViewByStatus(taskModel)


            icChat.setOnClickListener {
                activity.showProgressDialog()
                getConversationId(
                        taskModel.slug,
                        taskModel.poster.id.toString()
                )
            }
            direction.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=${sessionManager.latitude},${sessionManager.longitude}&daddr=${taskModel.position.latitude},${taskModel.position.longitude}"))
                startActivity(intent)
            }
            seeAll.setOnClickListener {
                setMediaList(taskModel.attachments)
            }
            setOfferStatus(taskModel)
            btnNext.setOnClickListener {
                when (jobState) {
                    JobState.MAKE_AN_OFFER -> makeAnOffer(taskModel)
                    JobState.VIEW_MY_OFFER -> viewMyOffer(taskModel)
                    JobState.VIEW_OFFER_LIST -> showOfferList(taskModel)
                    JobState.SUBMIT_RELEASE_MONEY -> showCustomDialogAskToReleaseMoney(taskModel)
                }

            }
        }
    }

    private fun showCustomDialogAskToReleaseMoney(taskModel: TaskModel) {
        if (taskModel.additionalFund != null && taskModel.additionalFund.status == "pending") {
            activity.showToast(
                    "Increase price request already pending. You either delete or wait for poster response on that.",
                    requireContext()
            )
            return
        }
        if (taskModel.rescheduleReqeust != null && taskModel.rescheduleReqeust.size > 0) {
            for (i in taskModel.rescheduleReqeust.indices) {
                if (taskModel.rescheduleReqeust[i].status == "pending") {
                    if (taskModel.status.lowercase(Locale.getDefault()) != Constant.TASK_CANCELLED && taskModel.status.lowercase(Locale.getDefault()) != Constant.TASK_CLOSED) {
                        if (viewModel.userType == JobDetailsViewModel.UserType.TICKER || viewModel.userType == JobDetailsViewModel.UserType.POSTER) {
                            activity.showToast(
                                    "Reschedule time request already pending. You either delete or wait for poster response on that.",
                                    requireContext()
                            )
                            return
                        }
                    }
                }
            }
        }
        val fragmentManager = parentFragmentManager
        val dialog = ConfirmAskToReleaseBottomSheet(requireContext())
        dialog.show(fragmentManager, "")
    }

    private fun viewMyOffer(taskModel: TaskModel) {
        val infoBottomSheet = WithdrawBottomSheet(this, taskModel.offers.filter { it.worker.id == sessionManager.userAccount.id }[0])
        infoBottomSheet.show(parentFragmentManager, null)
    }

    private fun changeViewByStatus(taskModel: TaskModel) {
        when (taskModel.status) {
            "closed" -> {
                linMessage.visible()
                linNext.gone()
                offerCount.gone()
                taskStatus.text = "Ticked"
                msgHeader.text = "Congrats! Job Ticked"
                msgBody.text = "Ticker has successfully completed your job."
                msgIcon.setImageResource(R.drawable.ic_check_circle)
                msgIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.feedback))
                msgAction.gone()
                linMessage.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.feedback_light))
                if (viewModel.userType == JobDetailsViewModel.UserType.POSTER) {
                    name.text = taskModel.worker.name
                    txtRoleTitle.text = "Ticked by"
                } else {
                    name.text = taskModel.poster.name
                    txtRoleTitle.text = "Ticked by"
                }
            }
            "assigned" -> {
                offerCount.gone()
                taskStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary_400))
                checkRescheduleRequest(taskModel)
                initIncreaseBudget(taskModel)
                if (viewModel.userType == JobDetailsViewModel.UserType.TICKER) {
                    linNext.visible()
                    btnNext.text = "Ask for payment"
                    jobState = JobState.SUBMIT_RELEASE_MONEY
                    budget.gone()
                    name.text = taskModel.poster.name
                    txtRoleTitle.text = "Post by"
                } else {
                    name.text = taskModel.worker.name
                    txtRoleTitle.text = "Assigned to"
                    linNext.gone()
                }
            }
            "completed" -> {
                linNext.gone()
                taskStatus.text = "Ticked"
                offerCount.gone()
                taskStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary_400))
                if (viewModel.userType == JobDetailsViewModel.UserType.POSTER) {
                    showConfirmReleaseCard(taskModel)
                } else if (viewModel.userType == JobDetailsViewModel.UserType.TICKER) {
                    showAskToReleaseCard(taskModel)
                }
            }
        }
    }

    private fun showAskToReleaseCard(taskModel: TaskModel) {
        linMessage.visible()
        msgBody.text = Html.fromHtml(
                "You have requested to release money on this job on <b>" +
                        TimeHelper.convertToShowTimeFormat(taskModel.conversation.task.completedAt) + "</b>"
        )
        msgHeader.text = "Release Money Request"
        msgAction.gone()
        msgIcon.setImageResource(R.drawable.ic_payment_setting)
        msgIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.primary_500))
    }

    private fun showConfirmReleaseCard(taskModel: TaskModel) {
        linMessage.visible()
        val whoRequestToReleaseMoney = taskModel.worker.name

        msgBody.text = Html.fromHtml(
                "<b>" + whoRequestToReleaseMoney + "</b> " +
                        " have requested to release money on this job on <b>" +
                        TimeHelper.convertToShowTimeFormat(taskModel.conversation.task.completedAt) + "</b>"
        )
        msgHeader.text = "Release Money Request"
        msgIcon.setImageResource(R.drawable.ic_payment_setting)
        msgIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.primary_500))
        msgAction.text = "View request"
        msgAction.setOnClickListener {
            showCustomDialogReleaseMoney()
        }

    }

    private fun showCustomDialogReleaseMoney() {
        val fragmentManager = parentFragmentManager
        val dialog = ConfirmReleaseBottomSheet(requireContext())
        dialog.show(fragmentManager, "")
    }

    private fun initIncreaseBudget(taskModel: TaskModel) {
        if (taskModel.additionalFund != null && taskModel.additionalFund.status == "pending") {
            showIncreaseBudgetCard(taskModel)
        }
    }

    var isIncreaseBudgetRequestForMine = false
    var isRescheduledRequestForMine = false

    private fun showIncreaseBudgetCard(taskModel: TaskModel) {
        var increaseRequestByWho = ""
        val requesterId = taskModel.additionalFund.requesterId
        if (taskModel.worker != null) {
            if (taskModel.worker.id == requesterId) {
                increaseRequestByWho = taskModel.worker.name + " has"
            }
        }
        if (taskModel.poster != null) {
            if (taskModel.poster.id == requesterId) {
                increaseRequestByWho = taskModel.poster.name + " has"
            }
        }
        if (sessionManager.userAccount.id == requesterId) {
            isIncreaseBudgetRequestForMine = true
            increaseRequestByWho = "You have"
        } else {
            isIncreaseBudgetRequestForMine = false
        }

        linMessage.visible()
        msgAction.visible()
        msgHeader.text = "Increase Price Request"
        msgBody.text = Html.fromHtml(
                increaseRequestByWho +
                        " requested to increase price on this job on <b>" +
                        TimeHelper.convertToShowTimeFormat(taskModel.additionalFund.createdAt) + "</b>"
        )
        msgIcon.setImageResource(R.drawable.ic_increase_price_v6)

        linMessage.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary_50))
        msgAction.setOnClickListener {
            showDialogIncreaseBudgetNoticeRequest(isIncreaseBudgetRequestForMine, taskModel)
        }
        msgIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.primary_500))
        msgAction.text = "View request"
    }

    private fun showDialogIncreaseBudgetNoticeRequest(isMine: Boolean, taskModel: TaskModel) {
        activity.navController.navigate(JobDetailsPosterFragmentDirections.actionJobDetailsPosterFragmentToIncreaseBudgetNoticeBottomSheet(taskModel, isMine))
    }

    private fun checkRescheduleRequest(taskModel: TaskModel) {
        var loc = 0
        var rescheduledByWho = ""

        if (taskModel.rescheduleReqeust != null && taskModel.rescheduleReqeust.size > 0) {
            for (i in taskModel.rescheduleReqeust.indices) {
                if (taskModel.rescheduleReqeust[i].status == "pending") {
                    //TODO user should be poster or ticker not viewer
                    linMessage.visible()
                    msgAction.text = "View request"
                    msgHeader.text = "Reschedule Request"
                    msgIcon.setImageResource(R.drawable.ic_reschedule)

                    val requesterId = taskModel.rescheduleReqeust[i].requester_id
                    if (taskModel.worker != null && taskModel.worker.id == requesterId) {
                        rescheduledByWho = "Ticker has"
                    }
                    if (taskModel.poster != null && taskModel.poster.id == requesterId) {
                        rescheduledByWho = taskModel.poster.name + "has"
                    }
                    if (sessionManager.userAccount.id == requesterId) {
                        rescheduledByWho = "You have"
                    }

                    msgBody.text = Html.fromHtml(
                            rescheduledByWho +
                                    " requested to reschedule time on this job on <b>" +
                                    TimeHelper.convertToShowTimeFormat(taskModel.rescheduleReqeust[i].created_at) + ".</b>"
                    )
                    loc = i
                    linNext.gone()
                    linMessage.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary_50))
                    break
                }
                if (viewModel.userType == JobDetailsViewModel.UserType.POSTER && taskModel.rescheduleReqeust[i].requester_id != sessionManager.userAccount.id)
                    if (taskModel.rescheduleReqeust[i].status == "accepted") {
                        linMessage.visible()
                        msgAction.gone()
                        msgHeader.text = "Reschedule Request Accepted"
                        msgBody.text = "Ticker accepted your reschedule time request on this job."
                        msgIcon.setImageResource(R.drawable.ic_reschedule)
                        msgIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.feedback))
                        linMessage.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.feedback_light))
                    } else if (taskModel.rescheduleReqeust[i].status == "declined") {
                        linMessage.visible()
                        msgAction.gone()
                        msgHeader.text = "Reschedule Request Declined"
                        msgBody.text = "Ticker declined your reschedule time request on this job."
                        msgIcon.setImageResource(R.drawable.ic_error_v3)
                        linMessage.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary_error_light))
                    }
            }
            msgAction.setOnClickListener {
                showDialogRescheduleRequest(taskModel, loc, isMine = rescheduledByWho == "You have", msgBody.text)
            }
        }
    }

    private fun showDialogRescheduleRequest(taskModel: TaskModel, pos: Int, isMine: Boolean, text: CharSequence) {
        val bundle = Bundle()
        bundle.putParcelable(ConstantKey.TASK, taskModel)
        bundle.putInt(ConstantKey.POSITION, pos)
        bundle.putBoolean(ConstantKey.IS_MY_TASK, isMine)
        bundle.putCharSequence(ConstantKey.COMPLETES_MESSAGE_SUBTITLE, text)
        activity.navController.navigate(R.id.rescheduleNoticeBottomSheetState, bundle)
    }

    private fun showOfferList(taskModel: TaskModel) {
        var gson = Gson()
        var jsonString = gson.toJson(taskModel)
        val i = Intent(requireContext(), OfferListActivity::class.java)
        val bundle = Bundle()
        bundle.putString(ConstantKey.TASK, jsonString)
        bundle.putBoolean(IS_USER_THE_POSTER, true)
        i.putExtras(bundle)
        startActivity(i)
    }

    @SuppressLint("SetTextI18n")
    private fun setOfferStatus(taskModel: TaskModel) {
        if (viewModel.userType == JobDetailsViewModel.UserType.POSTER)
            if (taskModel.offerCount > 0) {
                if (taskModel.offerCount > 0) {
                    btnNext.text = "See offers(${taskModel.offerCount})"
                    budget.gone()
                    jobState = JobState.VIEW_OFFER_LIST
                } else {
                    btnNext.text = "Awaiting for offers"
                    budget.gone()
                    btnNext.isEnabled = false
                }
            } else
                if (taskModel.offerCount > 0) {
                    taskModel.offers.forEach {
                        if (it.worker.id == sessionManager.userAccount.id) {
                            btnNext.text = "View offer"
                            jobState = JobState.VIEW_MY_OFFER
                        }
                    }
                }
    }

    private fun setMediaList(attachments: ArrayList<AttachmentModel>) {
        if (attachments.size == 0)
            linAttachmentsTitle.gone()
        else
            linAttachmentsTitle.visible()
        if (isAllMedias) {
            mediaAdapter.addItems(attachments.toV2(), !isAllMedias)
            isAllMedias = false
            seeAll.text = "See Less"
        } else {
            seeAll.text = "See All"
            isAllMedias = true
            if (attachments.size > 3)
                mediaAdapter.addItems(attachments.toV2().subList(0, 3), !isAllMedias)
            else
                mediaAdapter.addItems(attachments.toV2(), !isAllMedias)
        }
    }


    private fun setMedias(attachments: java.util.ArrayList<AttachmentModel>) {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        mediaAdapter = MediaAdapter(ArrayList(), requireContext(), width)
        rlMedias.adapter = mediaAdapter
        rlMedias.layoutManager = GridLayoutManager(requireContext(), 3)
        rlMedias.addItemDecoration(SpacesItemDecorationV2((8).dpToPx()))
        setMediaList(attachments)
    }

    private fun setTaskLocation(taskModel: TaskModel) {
        if (taskModel.taskType == "physical" && taskModel.location != null) {
            location.text = taskModel.location
            direction.visible()
            icLocation.setImageResource(R.drawable.ic_location_v6)
        } else {
            location.text = "Remote"
            direction.gone()
            icLocation.setImageResource(R.drawable.ic_remote_v6)
        }
    }

    private fun getOfferCount(offerCount: Int?): CharSequence {
        return if (offerCount == 1) "1 Offer" else "$offerCount Offer"
    }

    @SuppressLint("SetTextI18n")
    private fun setDateTime(time: DueTimeModel, dueDate: String) {
        when {
            time.morning -> {
                dayPart.text = "Morning"
            }
            time.afternoon -> {
                dayPart.text = "Afternoon"
            }
            time.evening -> {
                dayPart.text = "Evening"
            }
            time.anytime != null && time.anytime -> {
                dayPart.text = "Anytime"
            }
        }
        try {
            date.text = Tools.formatJobDetailsDateV3(Tools.jobDetailsDate(dueDate))
        } catch (e: ParseException) {
            date.text = dueDate
        }
    }

    private fun setIds() {
        taskStatus = requireView().findViewById(R.id.taskStatus)
        offerCount = requireView().findViewById(R.id.offerCount)
        pDatetime = requireView().findViewById(R.id.p_dateTime)
        date = requireView().findViewById(R.id.date)
        dayPart = requireView().findViewById(R.id.dayPart)
        location = requireView().findViewById(R.id.location)
        direction = requireView().findViewById(R.id.direction)
        title = requireView().findViewById(R.id.title)
        name = requireView().findViewById(R.id.posterName)
        icChat = requireView().findViewById(R.id.ic_chat)
        btnNext = requireView().findViewById(R.id.btn_next)
        description = requireView().findViewById(R.id.description)
        rlMedias = requireView().findViewById(R.id.rl_medias)
        budget = requireView().findViewById(R.id.budget)
        seeAll = requireView().findViewById(R.id.seeAll)
        msgHeader = requireView().findViewById(R.id.msg_header)
        msgBody = requireView().findViewById(R.id.msg_body)
        msgAction = requireView().findViewById(R.id.msg_action)
        msgIcon = requireView().findViewById(R.id.msg_icon)
        linNext = requireView().findViewById(R.id.linNext)
        linMessage = requireView().findViewById(R.id.lin_message)
        icLocation = requireView().findViewById(R.id.icLocation)
        linAttachmentsTitle = requireView().findViewById(R.id.linAttachmentsTitle)
        txtRoleTitle = requireView().findViewById(R.id.txt_role_title)
    }

    private fun makeAnOffer(taskModel: TaskModel) {
        val intent = Intent(requireContext(), MakeAnOfferActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelable("model", taskModel)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun getConversationId(slug: String, targetId: String) {
        val stringRequest: StringRequest = object : StringRequest(
                Method.GET,
                Constant.BASE_URL_V2 + "jobs/" + slug + "/start_chat/" + targetId,
                com.android.volley.Response.Listener { response: String? ->
                    Timber.e(response)
                    activity.hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response!!)
                        Log.d("start chat", jsonObject.toString())
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
                                val userID = sessionManager.userAccount.id
                                var senderId = 0
                                var reciverId = 0
                                for (i in 0..1) {
                                    if (data.users[i].id == userID) {
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
                            if (sender.id == reciver.id) {
                                activity.showToast("Something Went Wrong", requireContext())
                            } else {
                                val intent = Intent(requireContext(), ChatActivity::class.java)
                                val bundle = Bundle()
                                bundle.putParcelable(ConstantKey.CONVERSATION, conversationModel)
                                intent.putExtras(bundle)
                                startActivityForResult(intent, ConstantKey.RESULTCODE_PRIVATE_CHAT)
                            }
                        } else {
                            activity.showToast("Something Went Wrong", requireContext())
                        }
                    } catch (e: Exception) {
                        Timber.e(e.toString())
                        e.printStackTrace()
                        activity.showToast("Something Went Wrong", requireContext())
                    }
                },
                com.android.volley.Response.ErrorListener { error: VolleyError ->
                    activity.showToast("Something Went Wrong", requireContext())
                    Timber.e(error.toString())
                    activity.hideProgressDialog()
                }
        ) {
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

    override fun startWithdraw(int: Int) {

    }
}

private fun ArrayList<AttachmentModel>.toV2(): List<AttachmentModelV2> {
    return this.map {
        AttachmentModelV2(id = it.id, name = it.name, fileName = it.fileName,
                mime = it.mime, type = it.type, file = null, createdAt = it.createdAt,
                drawable = it.drawable, isChecked = false, isCheckedEnable = false, modalUrl = it.modalUrl, url = it.url, thumbUrl = it.thumbUrl)
    }
}
