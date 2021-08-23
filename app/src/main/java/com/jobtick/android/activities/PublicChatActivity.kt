package com.jobtick.android.activities

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.MaterialToolbar
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.adapers.AttachmentAdapter
import com.jobtick.android.adapers.PublicChatListAdapter
import com.jobtick.android.adapers.QuestionAttachmentAdapter
import com.jobtick.android.models.AttachmentModel
import com.jobtick.android.models.CommentModel
import com.jobtick.android.models.OfferModel
import com.jobtick.android.models.QuestionModel
import com.jobtick.android.retrofit.ApiClient
import com.jobtick.android.utils.*
import com.jobtick.android.widget.SpacingItemDecoration
import com.mikhaellopez.circularimageview.CircularImageView
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import timber.log.Timber
import java.io.File
import java.util.*

class PublicChatActivity : ActivityBase(), View.OnClickListener, AttachmentAdapter.OnItemClickListener, QuestionAttachmentAdapter.OnItemClickListener {
    private var toolbar: MaterialToolbar? = null
    private var ivReport: ImageView? = null
    private var imgAvatar: CircularImageView? = null
    private var rltProfile: RelativeLayout? = null
    private var txtName: TextView? = null
    private var txtRatingValue: TextView? = null
    private var txtCompletionRate: TextView? = null
    private var txtBudget: TextView? = null
    private var lytBudgetStatus: LinearLayout? = null
    private var txtCreatedDate: TextView? = null
    private var txtMessage: TextView? = null
    private var imgOfferOnTask: ImageView? = null
    private var imgBtnPlay: ImageView? = null
    private var cardLiveVideo: CardView? = null
    private var lytBtnReply: LinearLayout? = null
    private var lytBtnMore: LinearLayout? = null
    private var edtCommentMessage: EditText? = null
    private var imgBtnSend: ImageView? = null
    private var layoutOffer: LinearLayout? = null
    private var imgAvatarQuestion: CircularImageView? = null
    private var txtNameQuestion: TextView? = null
    private var txtCreatedDateQuestion: TextView? = null
    private var txtMessageQuestion: TextView? = null
    private var lytBtnReplyQuestion: LinearLayout? = null
    private var linearAcceptDeleteOffer: LinearLayout? = null
    private var imgMoreLessArrow: ImageView? = null
    private var txtMoreLess: TextView? = null
    private var lytBtnMoreQuestion: LinearLayout? = null
    private var layoutQuestion: LinearLayout? = null
    private var lytCreateMessage: LinearLayout? = null
    private var recyclerViewQuestion: RecyclerView? = null
    private var btnAccept: TextView? = null
    private var starRatingBar: ImageView? = null
    private var ivVerifiedAccount: ImageView? = null
    private var recyclerViewQuestionAttachment: RecyclerView? = null

    private var offerModel: OfferModel? = null
    private var questionModel: QuestionModel? = null
    var recyclerViewOfferChat: RecyclerView? = null
    var rltLayoutActionData: RelativeLayout? = null
    var attachment: AttachmentModel? = null
    private var publicChatListAdapter: PublicChatListAdapter? = null
    private val attachmentArraylistQuestion = ArrayList<AttachmentModel?>()
    private var adapter: QuestionAttachmentAdapter? = null
    var isPoster = false
    private var posterID: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_public_chat)
        setIDs()
        attachment = AttachmentModel()
        txtBudget!!.visibility = View.VISIBLE
        txtMessage!!.visibility = View.GONE
        cardLiveVideo!!.visibility = View.GONE
        lytBtnReply!!.visibility = View.GONE
        lytBtnMore!!.visibility = View.GONE
        lytBtnReplyQuestion!!.visibility = View.GONE
        lytBtnMoreQuestion!!.visibility = View.GONE
        layoutOffer!!.visibility = View.GONE
        layoutQuestion!!.visibility = View.GONE
        val bundle = intent.extras
        isPoster = bundle!!.getBoolean("isPoster", false)
        try {
            posterID = bundle.getString("posterID")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        offerModel = OfferModel()
        questionModel = QuestionModel()
        if (TaskDetailsActivity.isOfferQuestion == "offer") {
            offerModel = TaskDetailsActivity.offerModel
            toolbar!!.title = "Replay Offer"
        } else {
            questionModel = TaskDetailsActivity.questionModel
            toolbar!!.title = "Replay Question"
        }
        initLayout()
        toolbar!!.setNavigationOnClickListener { onBackPressed() }

        //imgBtnImageSelect.setOnClickListener(this);
        imgBtnPlay!!.setOnClickListener(this)
        imgBtnSend!!.setOnClickListener(this)
        lytBtnMore!!.setOnClickListener(this)
        val layoutManager = LinearLayoutManager(this@PublicChatActivity)
        recyclerViewOfferChat!!.layoutManager = layoutManager
        publicChatListAdapter = PublicChatListAdapter(this@PublicChatActivity, ArrayList(), true, posterID)
        recyclerViewOfferChat!!.adapter = publicChatListAdapter
        // publicChatListAdapter.setOnItemClickListener(this);
        initQuestion()
        if (offerModel!!.taskId != null) {
            doApiCall(Constant.URL_OFFERS + "/" + offerModel!!.id, true)
        } else {
            doApiCall(Constant.URL_QUESTIONS + "/" + questionModel!!.id, false)
        }
    }

    private fun setIDs() {
        toolbar = findViewById(R.id.toolbar)
        ivReport = findViewById(R.id.ivReport)
        imgAvatar = findViewById(R.id.img_avatar)
        rltProfile = findViewById(R.id.rlt_profile)
        txtName = findViewById(R.id.txt_name)
        txtRatingValue = findViewById(R.id.txt_rating_value)
        txtCompletionRate = findViewById(R.id.txt_completion_rate)
        txtBudget = findViewById(R.id.txt_budget)
        lytBudgetStatus = findViewById(R.id.lyt_budget_status)
        txtCreatedDate = findViewById(R.id.txt_created_date)
        txtMessage = findViewById(R.id.txt_message)
        imgOfferOnTask = findViewById(R.id.img_offer_on_task)
        imgBtnPlay = findViewById(R.id.img_btn_play)
        cardLiveVideo = findViewById(R.id.card_live_video)
        lytBtnReply = findViewById(R.id.lyt_btn_reply)
        lytBtnMore = findViewById(R.id.lyt_btn_more)
        edtCommentMessage = findViewById(R.id.edt_comment_message)
        imgBtnSend = findViewById(R.id.img_btn_send)
        layoutOffer = findViewById(R.id.layout_offer)
        imgAvatarQuestion = findViewById(R.id.img_avatar_question)
        txtNameQuestion = findViewById(R.id.txt_name_question)
        txtCreatedDateQuestion = findViewById(R.id.txt_created_date_question)
        txtMessageQuestion = findViewById(R.id.txt_message_question)
        lytBtnReplyQuestion = findViewById(R.id.lyt_btn_reply_question)
        linearAcceptDeleteOffer = findViewById(R.id.linearAcceptDeleteOffer)
        imgMoreLessArrow = findViewById(R.id.img_more_less_arrow)
        txtMoreLess = findViewById(R.id.txt_more_less)
        lytBtnMoreQuestion = findViewById(R.id.lyt_btn_more_question)
        layoutQuestion = findViewById(R.id.layout_question)
        lytCreateMessage = findViewById(R.id.lyt_create_message)
        recyclerViewQuestion = findViewById(R.id.recycler_view_questions_chat)
        btnAccept = findViewById(R.id.txt_btn_accept)
        starRatingBar = findViewById(R.id.ratingbar_worker)
        recyclerViewOfferChat = findViewById(R.id.recycler_view_offer_chat)
        rltLayoutActionData = findViewById(R.id.rlt_layout_action_data)
        ivVerifiedAccount = findViewById(R.id.iv_verified_account)
        recyclerViewQuestionAttachment = findViewById(R.id.recycler_view_question_attachment)
    }

    private fun initQuestion() {
        attachmentArraylistQuestion.clear()
        attachmentArraylistQuestion.add(AttachmentModel())
        recyclerViewQuestionAttachment!!.layoutManager = LinearLayoutManager(this@PublicChatActivity, RecyclerView.HORIZONTAL, false)
        recyclerViewQuestionAttachment!!.addItemDecoration(SpacingItemDecoration(3, Tools.dpToPx(this@PublicChatActivity, 5), true))
        recyclerViewQuestionAttachment!!.setHasFixedSize(true)
        //set data and list adapter
        adapter = QuestionAttachmentAdapter(attachmentArraylistQuestion, true)
        recyclerViewQuestionAttachment!!.adapter = adapter
        adapter!!.notifyDataSetChanged()
        adapter!!.setOnItemClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    private fun initLayout() {
        if (offerModel!!.taskId != null) {
            if (isPoster) {
                linearAcceptDeleteOffer!!.visibility = View.VISIBLE
                btnAccept!!.setOnClickListener { v: View? ->
                    val intent = Intent(this@PublicChatActivity, PaymentOverviewActivity::class.java)
                    val bundle = Bundle()
                    //    bundle.putParcelable(ConstantKey.TASK, taskModel);
                    //     bundle.putParcelable(ConstantKey.OFFER_LIST_MODEL, obj);
                    intent.putExtras(bundle)
                    startActivityForResult(intent, ConstantKey.RESULTCODE_PAYMENTOVERVIEW)
                }
            }
            ivReport!!.setOnClickListener { v: View? ->
                val intent = Intent(this@PublicChatActivity, ReportActivity::class.java)
                val bundle = Bundle()
                bundle.putInt(ConstantKey.offerId, offerModel!!.id)
                bundle.putString("key", ConstantKey.KEY_OFFER_REPORT)
                intent.putExtras(bundle)
                startActivity(intent)
            }
            layoutOffer!!.visibility = View.VISIBLE
            layoutQuestion!!.visibility = View.GONE
            txtBudget!!.text = String.format(Locale.ENGLISH, "$ %d", offerModel!!.offerPrice)
            if (offerModel!!.worker.avatar != null) ImageUtil.displayImage(imgAvatar, offerModel!!.worker.avatar.thumbUrl, null)
            txtName!!.text = offerModel!!.worker.name
            if (offerModel!!.worker != null && offerModel!!.worker.workerRatings != null && offerModel!!.worker.workerRatings.avgRating != null) {
                txtRatingValue!!.text = String.format(Locale.US, "%.1f", offerModel!!.worker.workerRatings.avgRating) + " (" + offerModel!!.worker.workerRatings.receivedReviews + ")"
                //                ratingbarWorker.setProgress(Math.round(offerModel.getWorker().getWorkerRatings().getAvgRating()));
            } else {
                starRatingBar!!.visibility = View.GONE
            }
            if (offerModel!!.worker.isVerifiedAccount == 1) {
                ivVerifiedAccount!!.visibility = View.VISIBLE
            } else {
                ivVerifiedAccount!!.visibility = View.GONE
            }
            txtCompletionRate!!.text = String.format(Locale.ENGLISH, "%d%% Job success", offerModel!!.worker.workTaskStatistics.completionRate)
            txtCreatedDate!!.text = offerModel!!.createdAt
            if (offerModel!!.attachments != null && offerModel!!.attachments.size != 0) {
                cardLiveVideo!!.visibility = View.VISIBLE
                //   txtType.setText("Video Offer");
                ImageUtil.displayImage(imgOfferOnTask, offerModel!!.attachments[0].modalUrl, null)
            } else {
                txtMessage!!.visibility = View.VISIBLE
                // txtType.setText("Message");
                txtMessage!!.text = offerModel!!.message
            }
            //  txtType.setVisibility(View.VISIBLE);
        } else {
            layoutOffer!!.visibility = View.GONE
            layoutQuestion!!.visibility = View.VISIBLE
            if (questionModel!!.user.avatar != null) ImageUtil.displayImage(imgAvatarQuestion, questionModel!!.user.avatar.thumbUrl, null)
            txtNameQuestion!!.text = questionModel!!.user.name
            txtMessageQuestion!!.visibility = View.VISIBLE
            txtMessageQuestion!!.text = questionModel!!.questionText
            if (questionModel!!.attachments != null && questionModel!!.attachments.size != 0) {
                recyclerViewQuestion!!.visibility = View.VISIBLE
                val attachmentAdapter = AttachmentAdapter(questionModel!!.attachments, false)
                recyclerViewQuestion!!.setHasFixedSize(true)
                recyclerViewQuestion!!.layoutManager = LinearLayoutManager(this@PublicChatActivity, LinearLayoutManager.HORIZONTAL, false)
                recyclerViewQuestion!!.adapter = attachmentAdapter
                recyclerViewQuestion!!.isNestedScrollingEnabled = true
                attachmentAdapter.setOnItemClickListener(this)
            } else {
                recyclerViewQuestion!!.visibility = View.GONE
            }
            txtCreatedDateQuestion!!.text = questionModel!!.createdAt
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.img_btn_play -> {
            }
            R.id.img_btn_send -> if (validation()) {
                val strMessage = edtCommentMessage!!.text.toString().trim { it <= ' ' }
                if (offerModel!!.taskId != null) {
                    if (attachment!!.thumbUrl != null) {
                        addCommentIntoServer(strMessage, attachment!!.id, Constant.URL_OFFERS + "/" + offerModel!!.id)
                        edtCommentMessage!!.text = null
                        //imgBtnImageSelect.setImageDrawable(getResources().getDrawable(R.drawable.ic_paperclip));
                    } else {
                        addCommentIntoServer(strMessage, null, Constant.URL_OFFERS + "/" + offerModel!!.id)
                        edtCommentMessage!!.text = null
                        // imgBtnImageSelect.setImageDrawable(getResources().getDrawable(R.drawable.ic_paperclip));
                    }
                } else {
                    if (attachment!!.thumbUrl != null) {
                        addCommentIntoServer(strMessage, attachment!!.id, Constant.URL_QUESTIONS + "/" + questionModel!!.id)
                        edtCommentMessage!!.text = null
                        // imgBtnImageSelect.setImageDrawable(getResources().getDrawable(R.drawable.ic_paperclip));
                    } else {
                        addCommentIntoServer(strMessage, null, Constant.URL_QUESTIONS + "/" + questionModel!!.id)
                        edtCommentMessage!!.text = null
                        //  imgBtnImageSelect.setImageDrawable(getResources().getDrawable(R.drawable.ic_paperclip));
                    }
                }
            }
        }
    }


    private fun checkPermissionReadExternalStorage(context: Context?): Boolean {
        return if (ContextCompat.checkSelfPermission(context!!,
                        permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((context as Activity?)!!, arrayOf(permission.READ_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
            false
        } else {
            true
        }
    }

    private fun addCommentIntoServer(str_message: String, id: Int?, url: String) {
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.POST, "$url/comments",
                Response.Listener { response ->
                    Timber.e(response)
                    hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            attachmentArraylistQuestion.clear()
                            attachmentArraylistQuestion.add(AttachmentModel())
                            if (jsonObject.getBoolean("success")) {
                                val jsonObjectOfferChat = jsonObject.getJSONObject("data")
                                val commentModel = CommentModel().getJsonToModel(jsonObjectOfferChat)
                                publicChatListAdapter!!.addItem(commentModel)
                                if (recyclerViewQuestion != null) recyclerViewQuestion!!.adapter = publicChatListAdapter
                            } else {
                                showToast("Something went Wrong", this@PublicChatActivity)
                            }
                            if (adapter != null) {
                                adapter!!.notifyDataSetChanged()
                            }
                        }
                    } catch (e: JSONException) {
                        Timber.e(e.toString())
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
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
                            val jsonObjectError = jsonObject.getJSONObject("error")
                            if (jsonObjectError.has("message")) {
                                showToast(jsonObjectError.getString("message"), this@PublicChatActivity)
                            }
                            if (jsonObjectError.has("errors")) {
                                val jsonObjectErrors = jsonObjectError.getJSONObject("errors")
                                if (jsonObjectErrors.has("comment_text") && !jsonObjectErrors.has("comment_text")) {
                                    val message = jsonObjectErrors.getString("comment_text")
                                    edtCommentMessage!!.error = message
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        showToast("Something Went Wrong", this@PublicChatActivity)
                    }
                    Timber.e(error.toString())
                    hideProgressDialog()
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["comment_text"] = str_message
                if (attachment!!.thumbUrl != null) {
                    map1["attachments"] = id.toString()
                }
                Timber.tag("MAP").e(map1.size.toString())
                Timber.e(map1.toString())
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this@PublicChatActivity)
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
    }

    private fun validation(): Boolean {
        if (TextUtils.isEmpty(edtCommentMessage!!.text.toString().trim { it <= ' ' })) {
            edtCommentMessage!!.error = "Please enter replay message"
            return false
        }
        return true
    }

    private fun doApiCall(url: String, isOffer: Boolean) {
        val items = ArrayList<CommentModel>()
        Helper.closeKeyboard(this@PublicChatActivity)
        val stringRequest: StringRequest = object : StringRequest(Method.GET, "$url/comments",
                Response.Listener { response: String? ->
                    Timber.e(response)
                    // categoryArrayList.clear();
                    try {
                        val jsonObject = JSONObject(response)
                        Timber.e(jsonObject.toString())
                        val jsonArrayData = jsonObject.getJSONArray("data")
                        var i = 0
                        while (jsonArrayData.length() > i) {
                            val jsonObjectOfferChat = jsonArrayData.getJSONObject(i)
                            val commentModel = CommentModel().getJsonToModel(jsonObjectOfferChat)
                            items.add(commentModel)
                            i++
                        }
                        publicChatListAdapter!!.clear()
                        publicChatListAdapter!!.addItems(items)
                        if (isOffer) {
                            recyclerViewOfferChat!!.scrollToPosition(items.size - 1)
                        } else {
                            val layoutManager = LinearLayoutManager(this@PublicChatActivity)
                            recyclerViewQuestion!!.visibility = View.VISIBLE
                            recyclerViewQuestion!!.layoutManager = layoutManager
                            recyclerViewQuestion!!.adapter = publicChatListAdapter
                            recyclerViewQuestion!!.scrollToPosition(items.size - 1)
                        }
                        recyclerViewQuestion!!.scrollToPosition(items.size - 1)
                    } catch (e: JSONException) {
                        hideProgressDialog()
                        Timber.e(e.toString())
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    //  swipeRefresh.setRefreshing(false);
                    errorHandle1(error.networkResponse)
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["Authorization"] = "Bearer " + sessionManager.accessToken
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this@PublicChatActivity)
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
    }

    private fun uploadDataInTempAttachmentMediaApi(pictureFile: File) {
        showProgressDialog()
        val call: Call<String?>?
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile)
        val imageFile = MultipartBody.Part.createFormData("media", pictureFile.name, requestFile)
        call = ApiClient.getClient().getTaskTempAttachmentMediaData("XMLHttpRequest", sessionManager.tokenType + " " + sessionManager.accessToken, imageFile)
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: retrofit2.Response<String?>) {
                hideProgressDialog()
                Timber.tag("Response").e(response.toString())
                if (response.code() == HttpStatus.HTTP_VALIDATION_ERROR) {
                    showToast(response.message(), this@PublicChatActivity)
                    return
                }
                try {
                    adapter!!.clear()
                    val strResponse = response.body()
                    if (response.code() == HttpStatus.NOT_FOUND) {
                        showToast("not found", this@PublicChatActivity)
                        return
                    }
                    if (response.code() == HttpStatus.AUTH_FAILED) {
                        unauthorizedUser()
                        return
                    }
                    if (response.code() == HttpStatus.SUCCESS) {
                        Timber.tag("body").e(strResponse)
                        val jsonObject = JSONObject(strResponse)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("data")) {
                            val jsonObject_data = jsonObject.getJSONObject("data")
                            attachment = AttachmentModel().getJsonToModel(jsonObject_data)
                            attachmentArraylistQuestion.add(attachment)
                        }
                        adapter!!.notifyItemInserted(0)
                        adapter!!.notifyDataSetChanged()
                        //  ImageUtil.displayRoundImage(imgBtnImageSelect, attachment.getThumbUrl(), null);
                    } else {
                        showToast("Something went wrong", this@PublicChatActivity)
                    }
                } catch (e: JSONException) {
                    showToast("Something went wrong", this@PublicChatActivity)
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                hideProgressDialog()
                Timber.tag("Response").e(call.toString())
            }
        })
    }

    /**
     * Activity result method will be called after closing the camera
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // if the result is capturing Image
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(applicationContext, imageStoragePath)
                val uri = Uri.parse("file://" + imageStoragePath)
                uploadDataInTempAttachmentMediaApi(File(uri.path))
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                showToast("User cancelled image capture", this)
            } else {
                // failed to capture image
                showToast("Sorry! Failed to capture image", this)
            }
        } else if (requestCode == GALLERY_PICKUP_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data!!.data != null) {
                    imageStoragePath = CameraUtils.getPath(this@PublicChatActivity, data.data)
                    val file = File(imageStoragePath)
                    uploadDataInTempAttachmentMediaApi(file)
                }
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                showToast(
                        "User cancelled Pickup Image", this)
            } else {
                // failed to record video
                showToast("Sorry! Failed to Pickup Image", this)
            }
        }
    }

    override fun onItemClick(view: View, obj: AttachmentModel, position: Int, action: String) {
        if (checkPermissionReadExternalStorage(this)) {
            if (action.equals("add", ignoreCase = true)) {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_PICKUP_IMAGE_REQUEST_CODE)
            } else if (action.equals("delete", ignoreCase = true)) {
                recyclerViewQuestionAttachment!!.removeViewAt(position)
                attachmentArraylistQuestion.removeAt(position)
                adapter!!.notifyItemRemoved(position)
                adapter!!.notifyItemRangeRemoved(position, attachmentArraylistQuestion.size)
                attachmentArraylistQuestion.clear()
                attachmentArraylistQuestion.add(AttachmentModel())
                adapter!!.notifyDataSetChanged()
            }
        }
    }

    override fun onPointerCaptureChanged(hasCapture: Boolean) {}

    companion object {
        // Activity request codes
        private const val CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100
        private const val GALLERY_PICKUP_IMAGE_REQUEST_CODE = 400
        private var imageStoragePath: String? = null
        private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123
    }
}