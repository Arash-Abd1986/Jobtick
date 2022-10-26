package com.jobtick.android.material.ui.jobdetails

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
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
import com.jobtick.android.activities.ChatActivity
import com.jobtick.android.activities.MakeAnOfferActivity
import com.jobtick.android.activities.TaskDetailsActivity
import com.jobtick.android.material.ui.postajob.PostAJobActivity
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

class JobDetailsTicketViewerFragment : Fragment() {
    private lateinit var taskStatus: MaterialTextView
    private lateinit var offerCount: MaterialTextView
    private lateinit var pDatetime: MaterialTextView
    private lateinit var date: MaterialTextView
    private lateinit var dayPart: MaterialTextView
    private lateinit var location: MaterialTextView
    private lateinit var budget: MaterialTextView
    private lateinit var direction: MaterialTextView
    private lateinit var title: MaterialTextView
    private lateinit var posterName: MaterialTextView
    private lateinit var description: MaterialTextView
    private lateinit var icChat: AppCompatImageView
    private lateinit var btnNext: MaterialButton
    lateinit var viewModel: JobDetailsViewModel
    private lateinit var rlMedias: RecyclerView
    private lateinit var mediaAdapter: MediaAdapter
    private lateinit var sessionManager: SessionManager
    private lateinit var activity: JobDetailsActivity


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_job_details_ticket_viewer, container, false)
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
        ).get(JobDetailsViewModel::class.java)
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
            posterName.text = taskModel.poster.name
            setMoreLess(description, taskModel.description, 3)
            setTaskLocation(taskModel)
            setMedias(taskModel.attachments)
            budget.text = "$${taskModel.budget}"
            btnNext.setOnClickListener {
                makeAnOffer(taskModel)
            }
            icChat.setOnClickListener {
                activity.showProgressDialog()
                getConversationId(
                        taskModel.slug,
                        taskModel.poster.id.toString()
                )
            }
        }
    }


    private fun setMedias(attachments: ArrayList<AttachmentModel>?) {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        mediaAdapter = MediaAdapter(ArrayList(), requireContext(), width)
        rlMedias.adapter = mediaAdapter
        rlMedias.layoutManager = GridLayoutManager(requireContext(), 3)
        rlMedias.addItemDecoration(SpacesItemDecorationV2((8).dpToPx()))
        mediaAdapter.addItems(attachments?.toV2())
    }

    private fun setTaskLocation(taskModel: TaskModel) {
        if (taskModel.taskType == "physical" && taskModel.location != null) {
            location.text = taskModel.location
        } else {
            location.text = "Remote job"
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
        posterName = requireView().findViewById(R.id.posterName)
        icChat = requireView().findViewById(R.id.ic_chat)
        btnNext = requireView().findViewById(R.id.btn_next)
        description = requireView().findViewById(R.id.description)
        rlMedias = requireView().findViewById(R.id.rl_medias)
        budget = requireView().findViewById(R.id.budget)
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
}

private fun ArrayList<AttachmentModel>.toV2(): List<AttachmentModelV2> {
    return this.map {
        AttachmentModelV2(id = it.id, name = it.name, fileName = it.fileName,
                mime = it.mime, type = it.type, file = null, createdAt = it.createdAt,
                drawable = it.drawable, isChecked = false, isCheckedEnable = false, modalUrl = it.modalUrl, url = it.url, thumbUrl = it.thumbUrl)
    }
}
