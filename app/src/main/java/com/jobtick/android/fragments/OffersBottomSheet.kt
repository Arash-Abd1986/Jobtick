package com.jobtick.android.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.activities.*
import com.jobtick.android.adapers.OfferListAdapter
import com.jobtick.android.models.*
import com.jobtick.android.models.response.conversationinfo.GetConversationInfoResponse
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.SessionManager
import com.tapadoo.alerter.Alerter
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class OffersBottomSheet(
        private val offerListF : ArrayList<OfferModel>, private val isUserThePoster: Boolean,
        private val sessionManager: SessionManager) : BottomSheetDialogFragment(),OfferListAdapter.OnItemClickListener {
    private lateinit var recyclerViewOffers: RecyclerView
    private lateinit var offerListAdapter: OfferListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_offers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setIDs()
        initOfferList()

    }
    private fun initOfferList() {
        recyclerViewOffers.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerViewOffers.layoutManager = layoutManager
        offerListAdapter = OfferListAdapter(requireContext(), isUserThePoster, ArrayList())
        recyclerViewOffers.adapter = offerListAdapter
        offerListAdapter.setOnItemClickListener(this)
        offerListAdapter.addItems(offerListF)
    }


    private fun setIDs() {
        recyclerViewOffers = requireView().findViewById(R.id.recycler_view_offers)
    }




    fun showToast(content: String?, context: Context?) {
        Alerter.create(activity)
                .setTitle("")
                .setText(content!!)
                .setBackgroundResource(R.color.colorRedError)
                .show()
    }
    //Adapter override method
    override fun onItemOfferClick(v: View?, obj: OfferModel, position: Int, action: String) {
        when {
            action.equals("reply", ignoreCase = true) -> {
                val intent = Intent(requireContext(), PublicChatActivity::class.java)
                val bundle = Bundle()
                TaskDetailsActivity.offerModel = obj
                TaskDetailsActivity.isOfferQuestion = "offer"
                bundle.putBoolean("isPoster", isUserThePoster)
                bundle.putString("posterID", TaskDetailsActivity.taskModel!!.poster.id.toString())
                intent.putExtras(bundle)
                startActivityForResult(intent, 20)
            }
            action.equals("accept", ignoreCase = true) -> {
                val intent = Intent(requireContext(), PaymentOverviewActivity::class.java)
                val bundle = Bundle()
                TaskDetailsActivity.offerModel = obj
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
                getConversationId(TaskDetailsActivity.taskModel!!.slug, obj.worker.id.toString())
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
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)
    }

}