package com.jobtick.android.fragments


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.TypedValue
import android.view.*
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.jobtick.android.R
import com.jobtick.android.activities.AbstractUploadableImageImpl
import com.jobtick.android.activities.TaskCreateActivity
import com.jobtick.android.activities.TaskCreateActivity.ActionDraftTaskDetails
import com.jobtick.android.activities.UploadableImage
import com.jobtick.android.adapers.AddTagAdapter
import com.jobtick.android.adapers.AttachmentAdapter1
import com.jobtick.android.adapers.SuburbSearchAdapter.SubClickListener
import com.jobtick.android.models.AttachmentModel
import com.jobtick.android.models.PositionModel
import com.jobtick.android.models.TaskModel
import com.jobtick.android.models.response.searchsuburb.Feature
import com.jobtick.android.models.task.AttachmentModels
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.*
import com.jobtick.android.widget.ExtendedCommentTextNewDesign
import com.jobtick.android.widget.ExtendedCommentTextNewDesignWithError
import com.jobtick.android.widget.SpacingItemDecoration
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import kotlin.math.roundToInt


class TaskDetailFragment : Fragment(), AttachmentAdapter1.OnItemClickListener, TextWatcher,
    SubClickListener {
    private lateinit var lytBtnDetails: LinearLayout
    private lateinit var cardDetails: CardView
    private lateinit var addAttach: FrameLayout
    private lateinit var tvAttachTitle: MaterialTextView
    private lateinit var tvRequireTitle: MaterialTextView
    private lateinit var addAttachSmall: FrameLayout
    private lateinit var lytBntDateTime: LinearLayout
    private lateinit var lytBtnBudget: LinearLayout
    private lateinit var edtTitle: ExtendedCommentTextNewDesignWithError
    private lateinit var edtDescription: ExtendedCommentTextNewDesignWithError
    private lateinit var recyclerAddMustHave: RecyclerView
    private lateinit var rltAddMustHave: FrameLayout
    private lateinit var relReqSmall: RelativeLayout
    private lateinit var checkboxOnline: SwitchCompat
    private lateinit var txtSuburb: ExtendedCommentTextNewDesign
    private lateinit var btnNext: MaterialButton
    private lateinit var bottomSheet: FrameLayout
    private lateinit var rcAttachment: RecyclerView
    private lateinit var imgDetails: ImageView
    private lateinit var txtDetails: TextView
    private lateinit var imgDateTime: ImageView
    private lateinit var txtDateTime: TextView
    private lateinit var imgBudget: ImageView
    private lateinit var txtBudget: TextView
    private lateinit var scrollBar: NestedScrollView

    private val requiredCount = -1
    private val TAG = TaskDetailFragment::class.java.name
    private val PLACE_SELECTION_REQUEST_CODE = 21
    private var attachmentAdapter: AttachmentAdapter1? = null
    private var mBottomSheetDialog: BottomSheetDialog? = null
    private var taskCreateActivity: TaskCreateActivity? = null
    private var tagAdapter: AddTagAdapter? = null
    private var tagAdapterBottomSheet: AddTagAdapter? = null
    private var addTagList: ArrayList<String>? = null
    private var operationsListener: OperationsListener? = null
    private var task: TaskModel? = null
    private var sessionManager: SessionManager? = null
    private var uploadableImage: UploadableImage? = null
    var isEditTask = false
    var taskSlug: String? = null
    private val attachmentArrayList: ArrayList<AttachmentModel>? = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_detail, container, false)
        uploadableImage = object : AbstractUploadableImageImpl(requireActivity()) {
            override fun onImageReady(imageFile: File) {
                if (!isEditTask) uploadDataInTempApi(imageFile) else uploadDataForEditTask(imageFile)
            }
        }
        return view
    }

    private fun setIDS() {
        lytBtnDetails = requireView().findViewById(R.id.lyt_btn_details)
        cardDetails = requireView().findViewById(R.id.card_details)
        addAttach = requireView().findViewById(R.id.add_attach)
        tvAttachTitle = requireView().findViewById(R.id.tv_attach_title)
        tvRequireTitle = requireView().findViewById(R.id.tv_require_title)
        addAttachSmall = requireView().findViewById(R.id.add_attach_small)
        lytBntDateTime = requireView().findViewById(R.id.lyt_bnt_date_time)
        lytBtnBudget = requireView().findViewById(R.id.lyt_btn_budget)
        edtTitle = requireView().findViewById(R.id.edt_title)
        edtDescription = requireView().findViewById(R.id.edt_description)
        recyclerAddMustHave = requireView().findViewById(R.id.recycler_add_must_have)
        rltAddMustHave = requireView().findViewById(R.id.rlt_add_must_have)
        relReqSmall = requireView().findViewById(R.id.rel_req_small)
        checkboxOnline = requireView().findViewById(R.id.checkbox_online)
        txtSuburb = requireView().findViewById(R.id.txt_suburb)
        btnNext = requireView().findViewById(R.id.btn_next)
        bottomSheet = requireView().findViewById(R.id.bottom_sheet)
        rcAttachment = requireView().findViewById(R.id.rcAttachment)
        imgDetails = requireView().findViewById(R.id.img_details)
        txtDetails = requireView().findViewById(R.id.txt_details)
        imgDateTime = requireView().findViewById(R.id.img_date_time)
        txtDateTime = requireView().findViewById(R.id.txt_date_time)
        imgBudget = requireView().findViewById(R.id.img_budget)
        txtBudget = requireView().findViewById(R.id.txt_budget)
        scrollBar = requireView().findViewById(R.id.scroll_bar)
        onViewClick()
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVars()
        pushEvent(EventTitles.N_PAGE_VIEW_PJ_DETAILS.key, bundleOf(
            "usr_name" to sessionManager!!.userAccount.name,
            "usr_id" to sessionManager!!.userAccount.id,
            "email" to sessionManager!!.userAccount.email,
            "phone_number" to sessionManager!!.userAccount.mobile,
            "title" to "post a job, details",
            "description" to ""
        ))
        setIDS()
        selectDetailsBtn()
        setComponent()
        init()
        textChangeCheck()
    }

    private fun initVars() {
        taskCreateActivity = requireActivity() as TaskCreateActivity
        sessionManager = SessionManager(taskCreateActivity)

        task = TaskModel()
        addTagList = ArrayList()
        task!!.title = requireArguments().getString("TITLE")
        task!!.description = requireArguments().getString("DESCRIPTION")
        task!!.musthave = requireArguments().getStringArrayList("MUSTHAVE")
        task!!.taskType = requireArguments().getString("TASK_TYPE")
        task!!.location = requireArguments().getString("LOCATION")
        task!!.position = requireArguments().getParcelable("POSITION")
        task!!.attachments =
            ArrayList((requireArguments().getParcelable<Parcelable>("ATTACHMENT") as AttachmentModels?)!!.attachmentModelList)
        if (!task!!.attachments.isNullOrEmpty())
            if (task!!.attachments[0].id == null)
                task!!.attachments.removeAt(0)
        isEditTask = requireArguments().getBoolean("isEditTask", false)
        taskSlug = requireArguments().getString("taskSlug", null)
        //lytBtnDetails.setBackgroundResource(R.drawable.rectangle_round_white_with_shadow);
    }

    private fun setComponent() {
        edtTitle.text = task!!.title
        edtDescription.text = task!!.description
        if (task!!.taskType != null) {
            if (task!!.taskType.equals("remote", ignoreCase = true)) {
                checkboxOnline.isChecked = true
                txtSuburb.visibility = View.GONE
            } else {
                checkboxOnline.isChecked = false
                txtSuburb.visibility = View.VISIBLE
            }
        } else {
            checkboxOnline.isChecked = false
            txtSuburb.visibility = View.VISIBLE
        }
        if (task!!.musthave != null && task!!.musthave.size != 0) {
            addTagList!!.addAll(task!!.musthave)
        }
        txtSuburb.text = task!!.location
        if (task!!.location != null) {
            txtSuburb.setPadding(0, 0, 0, 0)
        }
    }

    private fun init() {
        cardDetails.outlineProvider = ViewOutlineProvider.BACKGROUND
        showBottomSheetAddMustHave(true)
        recyclerAddMustHave.layoutManager = GridLayoutManager(taskCreateActivity, 1)
        recyclerAddMustHave.addItemDecoration(
            SpacingItemDecoration(
                1,
                Tools.dpToPx(taskCreateActivity, 5),
                true
            )
        )
        recyclerAddMustHave.setHasFixedSize(true)
        tagAdapter = AddTagAdapter(addTagList, true) { data: String? ->
            addTagList!!.remove(data)
            tagAdapter!!.updateItem(addTagList)
            tagAdapterBottomSheet!!.updateItem(addTagList)
        }
        if (addTagList!!.size == 0) {
            relReqSmall.visibility = View.GONE
            tvRequireTitle.setTextColor(resources.getColor(R.color.N100))
            rltAddMustHave.visibility = View.VISIBLE
        } else if (addTagList!!.size < 4) {
            tvRequireTitle.setTextColor(resources.getColor(R.color.P300))
            relReqSmall.visibility = View.VISIBLE
            rltAddMustHave.visibility = View.GONE
        }
        recyclerAddMustHave.adapter = tagAdapter
        rcAttachment.layoutManager = GridLayoutManager(taskCreateActivity, 4)
        rcAttachment.setHasFixedSize(true)
        attachmentAdapter = AttachmentAdapter1(attachmentArrayList, true)
        rcAttachment.adapter = attachmentAdapter
        attachmentAdapter!!.setOnItemClickListener(this)
        if (task!!.attachments != null && !task!!.attachments.isEmpty()) {
            attachmentAdapter!!.addItems(task!!.attachments)
        }
    }


    private fun onViewClick() {
        addAttach.setOnClickListener { v: View? ->
            edtDescription.clearFocus()
            edtTitle.clearFocus()
            uploadableImage!!.showAttachmentImageBottomSheet(false)
        }
        addAttachSmall.setOnClickListener { v: View? ->
            edtDescription.clearFocus()
            edtTitle.clearFocus()
            uploadableImage!!.showAttachmentImageBottomSheet(false)
        }
        taskCreateActivity!!.setActionDraftTaskDetails(object : ActionDraftTaskDetails {
            override fun callDraftTaskDetails(taskModel: TaskModel?) {
                if (edtTitle.text.trim { it <= ' ' }.length >= 10) {
                    taskModel!!.title = edtTitle.text.trim { it <= ' ' }
                }
                if (edtDescription.text.trim { it <= ' ' }.length >= 25) {
                    taskModel!!.description = edtDescription.text.trim { it <= ' ' }
                }
                if (!TextUtils.isEmpty(txtSuburb.text.trim { it <= ' ' })) {
                    taskModel!!.location = task!!.location
                    taskModel.position = task!!.position
                }
                if (addTagList != null) taskModel!!.musthave = addTagList
                if (attachmentArrayList != null) taskModel!!.attachments = attachmentArrayList
                taskModel!!.taskType = if (checkboxOnline.isChecked) "remote" else "physical"
                operationsListener!!.draftTaskDetails(taskModel, false)
            }
        })
        txtSuburb.setOnClickListener { v: View? ->
            edtDescription.clearFocus()
            edtTitle.clearFocus()
            //            Intent intent = new SuburbAutoComplete(requireActivity()).getIntent();
//            startActivityForResult(intent, PLACE_SELECTION_REQUEST_CODE);
            val infoBottomSheet = SearchSuburbBottomSheet(this, false, "Your job full address <b>won't be shown before job assignment.</b>")
            infoBottomSheet.show(parentFragmentManager, null)
        }
        rltAddMustHave.setOnClickListener {
            edtDescription.clearFocus()
            edtTitle.clearFocus()
            showBottomSheetAddMustHave(false)
        }
        relReqSmall.setOnClickListener {
            if ((recyclerAddMustHave.adapter)!!.itemCount >= 3) taskCreateActivity!!.showToast(
                "you can add only 3 requirements",
                taskCreateActivity
            ) else showBottomSheetAddMustHave(false)
        }
        checkboxOnline.setOnClickListener {
            edtDescription.clearFocus()
            edtTitle.clearFocus()
            if (checkboxOnline.isChecked) {
                txtSuburb.visibility = View.GONE
            } else {
                txtSuburb.visibility = View.VISIBLE
            }
            if (validationCode == 0) {
                btnNext.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.shape_rounded_back_button_active
                    )!!
                )
            } else {
                btnNext.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.shape_rounded_back_button_deactive
                    )!!
                )
            }
        }
        btnNext.setOnClickListener {
            when (validationCode) {
                0 -> {
                    //success
                    pushEvent(EventTitles.N_CLICK_PJ_DETAILS_NEXT.key, bundleOf(
                        "usr_name" to sessionManager!!.userAccount.name,
                        "usr_id" to sessionManager!!.userAccount.id,
                        "email" to sessionManager!!.userAccount.email,
                        "phone_number" to sessionManager!!.userAccount.mobile,
                        "task_type" to if (checkboxOnline.isChecked) "remote" else "physical",
                        "suburb" to txtSuburb.text.trim { it <= ' ' },
                        "title" to eventCleaner(edtTitle.text.trim { it <= ' ' }),
                        "description" to eventCleaner(edtDescription.text.trim { it <= ' ' })
                    ))
                    operationsListener!!.onNextClick(
                        edtTitle.text.trim { it <= ' ' },
                        edtDescription.text.trim { it <= ' ' },
                        addTagList,
                        if (checkboxOnline.isChecked) "remote" else "physical",
                        txtSuburb.text.trim { it <= ' ' },
                        task!!.position,
                        attachmentArrayList
                    )
                    operationsListener!!.onValidDataFilled()
                }
                1 -> {
                    edtTitle.setError("Please write a title for your job")
                    scrollBar.post { scrollBar.fullScroll(ScrollView.FOCUS_UP) }
                }
                2 -> {
                    edtDescription.setError("Please write a description for your job")
                    edtDescription.requestFocus()
                }
                3 -> {
                    txtSuburb.setError("Please enter job location")
                    scrollBar.post { scrollBar.fullScroll(ScrollView.FOCUS_DOWN) }
                }
            }
        }

    }

    private val validationCode: Int
        get() {
            if (TextUtils.isEmpty(edtTitle.text.trim { it <= ' ' }) || edtTitle.text.trim { it <= ' ' }.length < 10) {
                return 1
            } else if (TextUtils.isEmpty(edtDescription.text.trim { it <= ' ' }) || edtDescription.text.trim { it <= ' ' }.length < 25) {
                return 2
            } else if (!checkboxOnline.isChecked) {
                if (TextUtils.isEmpty(txtSuburb.text.trim { it <= ' ' })) {
                    return 3
                }
            }
            return 0
        }

    override fun onItemClick(view: View, obj: AttachmentModel, position: Int, action: String) {
        if (action.equals("delete", ignoreCase = true)) {
            if (isEditTask) {
                deleteEditTaskAttachment(obj.id)
            }
            rcAttachment.removeViewAt(position)
            attachmentArrayList!!.removeAt(position)
            attachmentAdapter!!.notifyItemRemoved(position)
            attachmentAdapter!!.notifyItemRangeRemoved(position, attachmentArrayList.size)
            if (attachmentArrayList.size == 0) {
                addAttach.visibility = View.VISIBLE
                tvAttachTitle.setTextColor(resources.getColor(R.color.N100))
                addAttachSmall.visibility = View.GONE
            }
        } else if (action.equals("show", ignoreCase = true)) {
            showBottomSheetDialogViewFullImage(obj.modalUrl, position)
        }
    }

    override fun onResume() {
        super.onResume()
        if (validationCode == 0) {
            btnNext.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.shape_rounded_back_button_active
                )!!
            )
        } else {
            btnNext.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.shape_rounded_back_button_deactive
                )!!
            )
        }
    }

    private fun deleteEditTaskAttachment(attachmentId: Int) {
        if (taskSlug == null) return
        taskCreateActivity!!.showProgressDialog()
        val call: Call<String?>?
        call = ApiClient.getClient().deleteEditTaskAttachment(
            taskSlug,
            "XMLHttpRequest",
            sessionManager!!.tokenType + " " + sessionManager!!.accessToken,
            attachmentId
        )
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                taskCreateActivity!!.hideProgressDialog()
                if (response.code() == 422) {
                    return
                }
                try {
                    val strResponse = response.body()
                    val jsonObject = JSONObject(strResponse)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                // ((AppController) getContext()).mCrashlytics.recordException(t);
            }
        })
    }

    private fun textChangeCheck() {
        edtTitle.addTextChangedListener(this)
        edtDescription.addTextChangedListener(this)
        txtSuburb.addTextChangedListener(this)
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable) {
        if (validationCode == 0) {
            btnNext.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.shape_rounded_back_button_active
                )!!
            )
        } else {
            btnNext.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.shape_rounded_back_button_deactive
                )!!
            )
        }
    }

    override fun clickOnSearchedLoc(location: Feature) {
        txtSuburb.setPadding(0, 0, 0, 0)
        txtSuburb.text = location.place_name_en
        val positionModel = PositionModel()
        positionModel.longitude = location.geometry!!.coordinates!![0]
        positionModel.latitude = location.geometry.coordinates!![1]
        task!!.location = location.place_name_en
        task!!.position = positionModel
    }

    interface OperationsListener {
        fun onNextClick(
            title: String?,
            description: String?,
            musthave: ArrayList<String>?,
            task_type: String?,
            location: String?,
            positionModel: PositionModel?,
            attachmentArrayList: ArrayList<AttachmentModel>?
        )

        fun onValidDataFilled()
        fun draftTaskDetails(taskModel: TaskModel?, moveForword: Boolean)
    }

    private fun showBottomSheetDialogViewFullImage(url: String?, currentPosition: Int) {
        /*if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
*/
        @SuppressLint("InflateParams") val view =
            layoutInflater.inflate(R.layout.sheet_full_image, null)
        mBottomSheetDialog = BottomSheetDialog(taskCreateActivity!!)
        mBottomSheetDialog!!.setContentView(view)
        mBottomSheetDialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        val ivAttachment = view.findViewById<ImageView>(R.id.iv_attachment)
        if (url != null) {
            Glide.with(ivAttachment).load(url).into(ivAttachment)
        }
        val lytBtnDelete = view.findViewById<LinearLayout>(R.id.lyt_btn_delete)
        lytBtnDelete.setOnClickListener {
            if (isEditTask) deleteEditTaskAttachment(attachmentArrayList!![currentPosition].id)
            rcAttachment.removeViewAt(currentPosition)
            attachmentArrayList!!.removeAt(currentPosition)
            attachmentAdapter!!.notifyItemRemoved(currentPosition)
            attachmentAdapter!!.notifyItemRangeRemoved(currentPosition, attachmentArrayList.size)
            mBottomSheetDialog!!.dismiss()
        }
        val lytBtnBack = view.findViewById<LinearLayout>(R.id.lyt_btn_back)
        lytBtnBack.setOnClickListener { v: View? -> mBottomSheetDialog!!.dismiss() }

        // set background transparent
        (view.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))
        mBottomSheetDialog!!.show()
        mBottomSheetDialog!!.setOnDismissListener { mBottomSheetDialog = null }
    }

    @SuppressLint("SetTextI18n")
    private fun showBottomSheetAddMustHave(justInit: Boolean) {
        @SuppressLint("InflateParams") val view =
            layoutInflater.inflate(R.layout.sheet_add_must_have, null)
        mBottomSheetDialog = BottomSheetDialog(taskCreateActivity!!)
        mBottomSheetDialog!!.setContentView(view)
        mBottomSheetDialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        val txtCount = view.findViewById<TextView>(R.id.txt_count)
        val recyclerAddMustHaveBottomSheet: RecyclerView =
            view.findViewById(R.id.recycler_add_must_have_bottom_sheet)
        val txtTotalCount = view.findViewById<TextView>(R.id.txt_total_count)
        val btnAdd = view.findViewById<FrameLayout>(R.id.btn_add)
        val btnClose: MaterialButton = view.findViewById(R.id.btn_close)
        val root = view.findViewById<RelativeLayout>(R.id.root_bottom_sheet)
        val relRequire = view.findViewById<RelativeLayout>(R.id.rel_require)
        val edtAddTag = view.findViewById<EditText>(R.id.edtAddTag)
        val r = resources
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 750f, r.displayMetrics
        ).roundToInt()
        val pxMin = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 400f, r.displayMetrics
        ).roundToInt()
        edtAddTag.onFocusChangeListener = OnFocusChangeListener { v: View?, hasFocus: Boolean ->
            if (hasFocus) root.minimumHeight = px else root.minimumHeight = pxMin
        }
        edtAddTag.requestFocus()
        edtAddTag.post {
            val imm = requireActivity().baseContext
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(edtAddTag, 0)
        }
        relRequire.setOnClickListener {
            edtAddTag.requestFocus()
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(edtAddTag, InputMethodManager.SHOW_IMPLICIT)
        }
        edtAddTag.setOnEditorActionListener { _: TextView?, i: Int, _: KeyEvent? ->
            if (i == EditorInfo.IME_ACTION_SEND) {
                btnAdd.performClick()
                return@setOnEditorActionListener true
            }
            false
        }
        btnClose.setOnClickListener { v: View? -> mBottomSheetDialog!!.dismiss() }
        recyclerAddMustHaveBottomSheet.layoutManager = GridLayoutManager(taskCreateActivity, 1)
        recyclerAddMustHaveBottomSheet.addItemDecoration(
            SpacingItemDecoration(
                1,
                Tools.dpToPx(taskCreateActivity, 5),
                true
            )
        )
        recyclerAddMustHaveBottomSheet.setHasFixedSize(true)
        tagAdapterBottomSheet = AddTagAdapter(addTagList, true) { data: String? ->
            addTagList!!.remove(data)
            tagAdapterBottomSheet!!.updateItem(addTagList)
            tagAdapter!!.updateItem(addTagList)
            btnAdd.isEnabled = addTagList!!.size < 3
        }
        recyclerAddMustHaveBottomSheet.adapter = tagAdapterBottomSheet
        txtCount.text = addTagList!!.size.toString() + ""
        btnAdd.setOnClickListener { v: View? ->
            if (addTagList!!.size == 0) relReqSmall.visibility =
                View.INVISIBLE else if (addTagList!!.size < 4) relReqSmall.visibility =
                View.INVISIBLE
            if (TextUtils.isEmpty(edtAddTag.text.toString().trim { it <= ' ' })) {
                edtAddTag.error = "Text is empty"
                return@setOnClickListener
            }
            if (addTagList!!.size < 3) {
                btnAdd.isEnabled = addTagList!!.size != 2
                txtCount.text = (addTagList!!.size + 1).toString()
                addTagList!!.add(edtAddTag.text.toString().trim { it <= ' ' })
                tagAdapterBottomSheet!!.updateItem(addTagList)
                tagAdapter!!.updateItem(addTagList)
                edtAddTag.setText("")
            } else {
                taskCreateActivity!!.showToast(
                    "you can add only 3 requirements",
                    taskCreateActivity
                )
            }
            if (addTagList!!.size > 0) {
                recyclerAddMustHaveBottomSheet.visibility = View.VISIBLE
            } else {
                recyclerAddMustHaveBottomSheet.visibility = View.GONE
            }
            if (addTagList!!.size == 3) mBottomSheetDialog!!.dismiss()
            if (addTagList!!.size == 0) {
                relReqSmall.visibility = View.GONE
                rltAddMustHave.visibility = View.VISIBLE
                tvRequireTitle.setTextColor(resources.getColor(R.color.N100))
            } else if (addTagList!!.size < 4) {
                tvRequireTitle.setTextColor(resources.getColor(R.color.P300))
                relReqSmall.visibility = View.VISIBLE
                rltAddMustHave.visibility = View.GONE
            }
        }
        (view.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))
        if (!justInit) {
            mBottomSheetDialog!!.show()
            mBottomSheetDialog!!.setOnDismissListener { dialog: DialogInterface? ->
                mBottomSheetDialog = null
            }
        }
    }

    private fun uploadDataInTempApi(pictureFile: File) {
        pushEvent(EventTitles.N_API_TEMP_ATTACHMENT.key, bundleOf(
            "usr_name" to sessionManager!!.userAccount.name,
            "usr_id" to sessionManager!!.userAccount.id,
            "email" to sessionManager!!.userAccount.email,
            "phone_number" to sessionManager!!.userAccount.mobile,
            "file_name" to pictureFile.name
        ))
        taskCreateActivity!!.showProgressDialog()
        val call: Call<String?>?
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile)
        val imageFile = MultipartBody.Part.createFormData("media", pictureFile.name, requestFile)
        call = ApiClient.getClient()
            .getTaskTempAttachmentMediaData( /*"application/x-www-form-urlencoded",*/"XMLHttpRequest",
                sessionManager!!.tokenType + " " + sessionManager!!.accessToken, imageFile
            )
        call?.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                taskCreateActivity!!.hideProgressDialog()
                if (response.code() == 422) {
                    taskCreateActivity!!.showToast(response.message(), taskCreateActivity)
                    return
                }
                try {
                    val strResponse = response.body()
                    val jsonObject = JSONObject(strResponse)
                    if (jsonObject.has("data")) {
                        val jsonObjectData = jsonObject.getJSONObject("data")
                        val attachment = AttachmentModel().getJsonToModel(jsonObjectData)
                        if (attachmentArrayList!!.size == 0) {
                            attachmentArrayList.add(attachment)
                        } else {
                            attachmentArrayList.size
                            attachmentArrayList.add(attachmentArrayList.size, attachment)
                        }
                        if (attachmentArrayList.size > 0) {
                            addAttach.visibility = View.GONE
                            tvAttachTitle.setTextColor(resources.getColor(R.color.P300))
                            addAttachSmall.visibility = View.VISIBLE
                        } else {
                            tvAttachTitle.setTextColor(resources.getColor(R.color.N100))
                            addAttach.visibility = View.VISIBLE
                            addAttachSmall.visibility = View.GONE
                        }
                    }
                    attachmentAdapter!!.notifyItemInserted(attachmentArrayList!!.size - 1)
                } catch (e: Exception) {
                    taskCreateActivity!!.showToast("Something went wrong", taskCreateActivity)
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                //((AppController) getContext()).mCrashlytics.recordException(t);
                try {
                    taskCreateActivity!!.showToast("Something went wrong", taskCreateActivity)
                    t.printStackTrace()
                    taskCreateActivity!!.hideProgressDialog()
                } catch (e: Exception) {
                    t.printStackTrace()
                }
            }
        })
    }

    private fun uploadDataForEditTask(pictureFile: File) {
        if (taskSlug == null) return
        taskCreateActivity!!.showProgressDialog()
        val call: Call<String?>?
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile)
        val imageFile = MultipartBody.Part.createFormData("media", pictureFile.name, requestFile)
        call = ApiClient.getClient().getTasKAttachmentMediaUpload(
            taskSlug,
            "XMLHttpRequest",
            sessionManager!!.tokenType + " " + sessionManager!!.accessToken,
            imageFile
        )
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                taskCreateActivity!!.hideProgressDialog()
                if (response.code() == 422) {
                    taskCreateActivity!!.showToast(response.message(), taskCreateActivity)
                    return
                }
                try {
                    val strResponse = response.body()
                    val jsonObject = JSONObject(strResponse!!)
                    if (jsonObject.has("data")) {
                        val jsonObjectData = jsonObject.getJSONObject("data")
                        val attachment = AttachmentModel().getJsonToModel(jsonObjectData)
                        if (attachmentArrayList!!.size != 0) {
                            attachmentArrayList.add(attachmentArrayList.size - 1, attachment)
                        }
                    }
                    attachmentAdapter!!.notifyItemInserted(attachmentArrayList!!.size - 1)
                } catch (e: Exception) {
                    taskCreateActivity!!.showToast("Something went wrong", taskCreateActivity)
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                // ((AppController) getContext()).mCrashlytics.recordException(t);
                try {
                    taskCreateActivity!!.showToast("Something went wrong", taskCreateActivity)
                    t.printStackTrace()
                    taskCreateActivity!!.hideProgressDialog()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun selectDetailsBtn() {
        val cslPrimary =
            AppCompatResources.getColorStateList(requireContext(), R.color.colorPrimary)
        imgDetails.imageTintList = cslPrimary
        imgDetails.setImageDrawable(resources.getDrawable(R.drawable.ic_details_big))
        txtDetails.setTextColor(resources.getColor(R.color.colorPrimary))
        val face = ResourcesCompat.getFont(requireActivity(), R.font.dmsans_medium)
        txtDetails.typeface = face
        val cslGrey = AppCompatResources.getColorStateList(requireContext(), R.color.greyC4C4C4)
        imgDateTime.imageTintList = cslGrey
        imgBudget.imageTintList = cslGrey
        txtDateTime.setTextColor(resources.getColor(R.color.colorGrayC9C9C9))
        txtBudget.setTextColor(resources.getColor(R.color.colorGrayC9C9C9))
    }


    fun checkTabs() {
        if ((requireActivity() as TaskCreateActivity?)!!.isBudgetComplete) {
            val cslGreen = AppCompatResources.getColorStateList(requireContext(), R.color.green)
            imgBudget.imageTintList = cslGreen
            txtBudget.setTextColor(resources.getColor(R.color.green))
        }
        if ((requireActivity() as TaskCreateActivity?)!!.isDateTimeComplete) {
            val cslGreen = AppCompatResources.getColorStateList(requireContext(), R.color.green)
            imgDateTime.imageTintList = cslGreen
            txtDateTime.setTextColor(resources.getColor(R.color.green))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        uploadableImage!!.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 25) {
            addTagList!!.clear()
            addTagList!!.addAll(data!!.getStringArrayListExtra("TAG")!!)
            tagAdapter!!.notifyDataSetChanged()
            tagAdapterBottomSheet!!.notifyDataSetChanged()
        }
        if (requestCode == PLACE_SELECTION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            txtSuburb.text = SuburbAutoComplete.getSuburbName(data)
            val positionModel = PositionModel()
            positionModel.latitude = SuburbAutoComplete.getLatitudeDouble(data)
            positionModel.longitude = SuburbAutoComplete.getLongitudeDouble(data)
            task!!.location = SuburbAutoComplete.getSuburbName(data)
            task!!.position = positionModel
        }
    }

    companion object {
        const val MEDIA_TYPE_VIDEO = 2
        fun newInstance(
            title: String?,
            description: String?,
            mustHave: ArrayList<String?>?,
            task_type: String?,
            location: String?,
            positionModel: PositionModel?,
            attachmentModels: AttachmentModels?,
            operationsListener: OperationsListener?,
            isEditTask: Boolean,
            taskSlug: String?
        ): TaskDetailFragment {
            val fragment = TaskDetailFragment()
            fragment.operationsListener = operationsListener
            val args = Bundle()
            args.putString("TITLE", title)
            args.putString("DESCRIPTION", description)
            args.putStringArrayList("MUSTHAVE", mustHave)
            args.putString("TASK_TYPE", task_type)
            args.putString("LOCATION", location)
            args.putBoolean("isEditTask", isEditTask)
            args.putString("taskSlug", taskSlug)
            args.putParcelable("POSITION", positionModel)
            args.putParcelable("ATTACHMENT", attachmentModels)
            fragment.arguments = args
            return fragment
        }
    }
}