package com.jobtick.android.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jobtick.android.R
import com.jobtick.android.activities.*
import com.jobtick.android.adapers.QuestionAttachmentAdapterV2
import com.jobtick.android.adapers.QuestionListAdapter
import com.jobtick.android.models.AttachmentModel
import com.jobtick.android.models.OfferModel
import com.jobtick.android.models.QuestionModel
import com.jobtick.android.models.TaskModel
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.*
import com.jobtick.android.widget.SpacingItemDecoration
import com.tapadoo.alerter.Alerter
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.File
import java.util.function.Consumer

class QuestionsBottomSheet(
        private val sessionManager: SessionManager, private val str_slug: String,
        private var taskModel: TaskModel, private val isUserThePoster: Boolean,
        private val pushQuestionID: Int) : BottomSheetDialogFragment(),
        QuestionListAdapter.OnItemClickListener, QuestionAttachmentAdapterV2.OnItemClickListener {
    private lateinit var recyclerViewQuestions: RecyclerView
    private lateinit var lytBtnViewAllQuestions: LinearLayout
    private lateinit var lytViewAllQuestions: RelativeLayout
    private lateinit var cardQuestionsLayout: LinearLayout
    private lateinit var edtComment: EditText
    private lateinit var lytBtnCommentSend: ImageView
    private lateinit var imgAddAttachment: ImageView
    private lateinit var recyclerViewQuestionAttachment: RecyclerView
    private lateinit var rltQuestionAdd: RelativeLayout
    private lateinit var linGray: View

    private lateinit var questionListAdapter: QuestionListAdapter
    private val attachmentArrayListQuestion = java.util.ArrayList<AttachmentModel>()
    private lateinit var adapter: QuestionAttachmentAdapterV2
    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123
    private val GALLERY_PICKUP_IMAGE_REQUEST_CODE = 400
    lateinit var  noticeListener:NoticeListener
    interface NoticeListener {
        fun onOnNewQuestion()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_questions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setIDs()
        onClick()
        initQuestionList()
        (activity as TaskDetailsActivity).showProgressDialog()
        dataOnlyQuestions
        setQuestionView()
        initQuestion()
    }

    private fun onClick() {
        imgAddAttachment.setOnClickListener {
            onItemClick(requireView(), AttachmentModel(), -1, "add")
        }

        lytViewAllQuestions.setOnClickListener {
            val intent = Intent(requireContext(), ViewAllQuestionsActivity::class.java)
            val bundle = Bundle()
            bundle.putString(ConstantKey.SLUG, taskModel.slug)
            bundle.putString(ConstantKey.TASK_STATUS, taskModel.status.toLowerCase())
            intent.putExtras(bundle)
            startActivityForResult(intent, 121)
        }
        lytBtnCommentSend.setOnClickListener {
            if (TextUtils.isEmpty(edtComment.text.toString().trim { it <= ' ' })) {
                edtComment.error = "?"
                return@setOnClickListener
            } else {
                if (attachmentArrayListQuestion.size == 0) {
                    postComment(
                            edtComment.text.toString().trim { it <= ' ' },
                            null
                    )
                } else {
                    postComment(
                            edtComment.text.toString().trim { it <= ' ' },
                            adapter
                    )
                }
            }
        }
    }

    private fun setIDs() {
        recyclerViewQuestions = requireView().findViewById(R.id.recycler_view_questions)
        lytBtnViewAllQuestions = requireView().findViewById(R.id.lyt_btn_view_all_questions)
        lytViewAllQuestions = requireView().findViewById(R.id.lyt_view_all_questions)
        cardQuestionsLayout = requireView().findViewById(R.id.card_questions_layout)
        edtComment = requireView().findViewById(R.id.edt_comment)
        lytBtnCommentSend = requireView().findViewById(R.id.lyt_btn_comment_send)
        recyclerViewQuestionAttachment = requireView().findViewById(R.id.recycler_view_question_attachment)
        rltQuestionAdd = requireView().findViewById(R.id.rlt_layout_action_data)
        imgAddAttachment = requireView().findViewById(R.id.img_add_attachment)
        linGray = requireView().findViewById(R.id.lin_gray)
    }

    private fun initQuestionList() {

        val name = taskModel.poster.name
        var simpleName = name
        try {
            simpleName = name.substring(0, name.indexOf(" ") + 2) + "."
        } catch (e: Exception) {
            e.printStackTrace()
        }
        edtComment.hint = String.format("Ask %s a question", simpleName)
        recyclerViewQuestions.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerViewQuestions.layoutManager = layoutManager
        questionListAdapter = QuestionListAdapter(
            requireContext(),
            ArrayList(),
            taskModel.status.toLowerCase(),
            taskModel.poster.id,
            sessionManager.userAccount.id
        )
        recyclerViewQuestions.adapter = questionListAdapter
        questionListAdapter.setOnItemClickListener(this)
        questionListAdapter.clear()
        for (i in taskModel.questions.indices) {
            if (taskModel.questions[i].id == pushQuestionID) {
                onItemQuestionClick(null, taskModel.questions[i], i, "reply")
                break
            }
        }
    }

    private fun setQuestionView() {
        if (!(taskModel.status.equals("open", ignoreCase = true))) {
            rltQuestionAdd.visibility = View.GONE
            linGray.visibility = View.GONE
        } else {
            rltQuestionAdd.visibility = View.VISIBLE
            linGray.visibility = View.VISIBLE
        }

        //TODO taskModel.getQuestionCount() > 5
        if (taskModel.questionCount > 5) {
            lytViewAllQuestions.visibility = View.VISIBLE
        } else {
            lytViewAllQuestions.visibility = View.GONE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initQuestion() {
        attachmentArrayListQuestion.clear()
        recyclerViewQuestionAttachment.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        recyclerViewQuestionAttachment.addItemDecoration(SpacingItemDecoration(3, Tools.dpToPx(requireContext(), 5), true))
        recyclerViewQuestionAttachment.setHasFixedSize(true)
        //set data and list adapter
        adapter = QuestionAttachmentAdapterV2(attachmentArrayListQuestion, true)
        recyclerViewQuestionAttachment.adapter = adapter
        adapter.notifyDataSetChanged()
        adapter.setOnItemClickListener(this)
    }

    override fun onItemQuestionClick(view: View?, obj: QuestionModel?, position: Int, action: String?) {
        if (action.equals("reply", ignoreCase = true)) {
            val intent = Intent(requireContext(), PublicChatActivity::class.java)
            val bundle = Bundle()
            TaskDetailsActivity.questionModel = obj
            TaskDetailsActivity.isOfferQuestion = "question"
            //bundle.putParcelable(ConstantKey.QUESTION_LIST_MODEL, obj);
            intent.putExtras(bundle)
            startActivityForResult(intent, 21)
        } else if (action.equals("report", ignoreCase = true)) {
            val intent = Intent(requireContext(), ReportActivity::class.java)
            val bundle = Bundle()
            bundle.putString(ConstantKey.SLUG, TaskDetailsActivity.taskModel!!.slug)
            bundle.putString("key", ConstantKey.KEY_COMMENT_REPORT)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }


    override fun onItemClick(view: View, obj: AttachmentModel, position: Int, action: String) {
        if (checkPermissionREAD_EXTERNAL_STORAGE(requireContext())) {
            if (action.equals("add", ignoreCase = true)) {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_PICKUP_IMAGE_REQUEST_CODE)
            } else if (action.equals("delete", ignoreCase = true)) {
                recyclerViewQuestionAttachment.removeViewAt(position)
                attachmentArrayListQuestion.removeAt(position)
                adapter.notifyItemRemoved(position)
                adapter.notifyItemRangeRemoved(position, attachmentArrayListQuestion.size)
                attachmentArrayListQuestion.clear()
                attachmentArrayListQuestion.add(AttachmentModel())
                adapter.notifyDataSetChanged()
            }
        }
    }

    fun checkPermissionREAD_EXTERNAL_STORAGE(context: Context?): Boolean {
        return if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(((context as Activity?)!!), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
            false
        } else {
            true
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        noticeListener.onOnNewQuestion()
    }
    private fun postComment(str_comment: String, attachmentModels: QuestionAttachmentAdapterV2?) {
        if (str_comment.length < 5) {
            showToast("The question text must be at least 5 characters", requireContext())
            return
        }
        (activity as TaskDetailsActivity).showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Constant.URL_QUESTIONS + "/" + taskModel.id + "/create",
                com.android.volley.Response.Listener { response: String? ->
                    Timber.e(response)
                    (activity as TaskDetailsActivity).hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response!!)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            edtComment.setText("")
                            attachmentArrayListQuestion.clear()
                            adapter.clear()
                            //attachmentArrayListQuestion.add(AttachmentModel())
                            if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                if (recyclerViewQuestions.visibility != View.VISIBLE) {
                                    recyclerViewQuestions.visibility = View.VISIBLE
                                }
                                val questionModel = QuestionModel().getJsonToModel(jsonObject.getJSONObject("data"))
                                val mItems: ArrayList<QuestionModel> = ArrayList()
                                mItems.addAll(questionListAdapter.getItems()!!)
                                mItems.add(0,questionModel)
                                questionListAdapter.clear()
                                questionListAdapter.addItems(mItems)
                            }
                        } else {
                            showToast(getString(R.string.server_went_wrong), requireContext())
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                com.android.volley.Response.ErrorListener { error: VolleyError ->
                    (activity as TaskDetailsActivity).hideProgressDialog()
                    val networkResponse = error.networkResponse
                    if (networkResponse?.data != null) {
                        val jsonError = String(networkResponse.data)

                        // Print Error!
                        Timber.e(jsonError)
                        if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                            //unauthorizedUser()
                            //hideProgressDialog()
                            return@ErrorListener
                        }
                        try {
                            val jsonObject = JSONObject(jsonError)
                            val jsonObject_error = jsonObject.getJSONObject("error")
                            if (jsonObject_error.has("message")) {
                                showToast(jsonObject_error.getString("message"), requireContext())
                            }
                            //                            if (jsonObject_error.has("errors")) {
//                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
//                            }
                            //  ((CredentialActivity)requireActivity()).showToast(message,requireActivity());
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        showToast("Something Went Wrong", requireContext())
                    }
                    Timber.e(error.toString())
                    // hideProgressDialog()
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
        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)
    }

    private val dataOnlyQuestions: Unit
        get() {
            (activity as TaskDetailsActivity).showProgressDialog()
            val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.URL_TASKS + "/" + str_slug,
                    com.android.volley.Response.Listener { response: String? ->
                        try {
                            (activity as TaskDetailsActivity).hideProgressDialog()
                            val jsonObject = JSONObject(response!!)
                            Timber.e(jsonObject.toString())
                            println(jsonObject.toString())
                            if (jsonObject.has("success") &&
                                    !jsonObject.isNull("success") &&
                                    jsonObject.getBoolean("success")) {
                                if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                    val jsonObject_data = jsonObject.getJSONObject("data")
                                    taskModel = TaskModel().getJsonToModel(jsonObject_data, requireContext())
                                    taskModel.offerSent = false
                                    taskModel.offers.forEach(Consumer { offerModel1: OfferModel -> if (offerModel1.worker.id == sessionManager.userAccount.id) taskModel.offerSent = true }
                                    )
                                    initQuestion()
                                    questionListAdapter.clear()
                                    if (taskModel.questions.size > 5) questionListAdapter.addItems(taskModel.questions.subList(0, 5)) else questionListAdapter.addItems(taskModel.questions)
                                    for (i in taskModel.questions.indices) {
                                        if (taskModel.questions[i].id == pushQuestionID) {
                                            onItemQuestionClick(null, taskModel.questions[i], i, "reply")
                                            break
                                        }
                                    }
                                }
                            } else {
                                showToast("Something went wrong", requireContext())
                            }
                        } catch (e: JSONException) {
                            showToast("JSONException", requireContext())
                            Timber.e(e.toString())
                            e.printStackTrace()
                        }
                    },
                    com.android.volley.Response.ErrorListener {
                        (activity as TaskDetailsActivity).hideProgressDialog()
                        showToast(getString(R.string.server_went_wrong), requireContext())
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
            val requestQueue = Volley.newRequestQueue(requireContext())
            requestQueue.add(stringRequest)
        }

    fun showToast(content: String?, context: Context?) {
        Alerter.create(activity)
                .setTitle("")
                .setText(content!!)
                .setBackgroundResource(R.color.colorRedError)
                .show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 21 || requestCode == 20) {
            dataOnlyQuestions
        }
        if (requestCode == GALLERY_PICKUP_IMAGE_REQUEST_CODE) {
            if (resultCode == ActivityBase.RESULT_OK) {
                if (data!!.data != null) {
                    val imageStoragePath = CameraUtils.getPath(activity, data.data)
                    val file = File(imageStoragePath)
                    uploadDataQuestionMediaApi(file)
                }
            } else if (resultCode == ActivityBase.RESULT_CANCELED) {
                // user cancelled recording
            } else {
                // failed to record video
                showToast("Sorry! Failed to Pickup Image", requireContext())
            }
        }
    }

    private fun uploadDataQuestionMediaApi(pictureFile: File) {
        //showProgressDialog()
        val call: Call<String?>?
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile)
        val imageFile = MultipartBody.Part.createFormData("media", pictureFile.name, requestFile)
        call = ApiClient.getClient().getTaskTempAttachmentMediaData("XMLHttpRequest", sessionManager.tokenType + " " + sessionManager.accessToken, imageFile)
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                //hideProgressDialog()
                Timber.e(response.toString())
                if (response.code() == HttpStatus.HTTP_VALIDATION_ERROR) {
                    showToast(response.message(), requireContext())
                    return
                }
                try {
                    val strResponse = response.body()
                    if (response.code() == HttpStatus.NOT_FOUND) {
                        showToast("not found", requireContext())
                        return
                    }
                    if (response.code() == HttpStatus.AUTH_FAILED) {
                        //unauthorizedUser()
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
                        showToast("Something went wrong", requireContext())
                    }
                } catch (e: JSONException) {
                    showToast("Something went wrong", requireContext())
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                //hideProgressDialog()
            }
        })
    }

}