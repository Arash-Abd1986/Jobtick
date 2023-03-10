package com.jobtick.android.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import butterknife.ButterKnife
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.jobtick.android.AppController
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.adapers.ChatAdapter
import com.jobtick.android.fragments.AttachmentBottomSheet
import com.jobtick.android.fragments.ConfirmBlockTaskBottomSheet
import com.jobtick.android.material.ui.jobdetails.JobDetailsActivity
import com.jobtick.android.models.AttachmentModel
import com.jobtick.android.models.ChatModel
import com.jobtick.android.models.ConversationModel
import com.jobtick.android.models.response.chat.ChatResponse
import com.jobtick.android.models.response.chat.MessageItem
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.pagination.PaginationListener
import com.jobtick.android.utils.*
import com.mikhaellopez.circularimageview.CircularImageView
import com.pusher.client.channel.PresenceChannel
import com.pusher.client.channel.PrivateChannel
import io.socket.client.Socket
import io.socket.emitter.Emitter
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


class ChatActivity : ActivityBase(), OnRefreshListener, ConfirmBlockTaskBottomSheet.NoticeListener {

    private lateinit var toolbar: Toolbar
    private lateinit var icSetting: ImageView
    private lateinit var cvAction: RelativeLayout
    private lateinit var btnUnblock: Button
    private lateinit var txtStatus: TextView
    private lateinit var cardStatus: CardView
    private lateinit var imgBtnTaskAction: TextView
    private lateinit var imgAttachment: ImageView
    private lateinit var lytTaskDetails: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var imgBtnImageSelect: ImageView
    private lateinit var imgDelete: ImageView
    private lateinit var edtCommentMessage: EditText
    private lateinit var imgBtnSend: ImageView
    private lateinit var rltLayoutActionData: RelativeLayout
    private lateinit var rltLayoutActionDataDeactive: RelativeLayout
    private lateinit var rlImage: RelativeLayout
    private lateinit var imgAvatar: CircularImageView
    private lateinit var txtTitle: TextView
    private lateinit var txtSubtitle: TextView
    private lateinit var txtJobTitle: TextView
    private lateinit var lytScrollDown: LinearLayout
    private lateinit var bottomSheet: FrameLayout
    private lateinit var txtCount: TextView
    private lateinit var pbLoading: ProgressBar
    private lateinit var attachmentLayout: LinearLayout
    private lateinit var more: ImageView
    private lateinit var camera: ImageView
    private lateinit var gallery: ImageView
    private lateinit var document: ImageView
    private lateinit var noActiveChat: RelativeLayout
    private lateinit var noActiveChattxt: TextView

    private var mypopupWindow: PopupWindow? = null
    private var adapter: ChatAdapter? = null
    private var chatModelArrayList: ArrayList<ChatModel?>? = null
    var attachment: AttachmentModel? = null
    var channel: PrivateChannel? = null
    var presenceChannel: PresenceChannel? = null
    private var currentPage = PaginationListener.PAGE_START
    private val isLastPage = false
    private var unreadCount = 0
    private var isLastPosition = false
    var layoutManager: LinearLayoutManager? = null
    private var needRefresh = true
    var runnable: Runnable? = null
    var delay = 1000
    var items = ArrayList<MessageItem?>()
    private var mSocket: Socket? = null
    private var uploadableImage: UploadableImage? = null
    private var imageFileTemp: File? = null
    private lateinit var app: AppController
    private lateinit var unwrappedDrawable: Drawable
    private lateinit var wrappedDrawable: Drawable


    override fun onResume() {
        super.onResume()
        pbLoading.visibility = View.VISIBLE
     //   imgBtnSend.visibility = View.GONE
        imgBtnSend.isEnabled = false
        DrawableCompat.setTint(DrawableCompat.wrap(imgBtnSend.drawable),
            ContextCompat.getColor(this@ChatActivity, R.color.neutral_light_500))
        imgBtnSend.colorFilter
        if (mSocket == null)
            mSocket = app.socket
        if (!mSocket!!.connected()) {
            mSocket!!.off(Socket.EVENT_CONNECT, onConnect)
            mSocket!!.off(Socket.EVENT_DISCONNECT, onDisconnect)
            mSocket!!.off(Socket.EVENT_CONNECT_ERROR, onConnectError)
            mSocket!!.off("auth", onAutResponse)
            mSocket!!.off("whoareyou", whoAreYou)
            mSocket!!.off("newpm", onNewMessage)
            mSocket!!.off("userstatus", userStatus)
            mSocket!!.on(Socket.EVENT_CONNECT, onConnect)
            mSocket!!.on(Socket.EVENT_DISCONNECT, onDisconnect)
            mSocket!!.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
            mSocket!!.on("auth", onAutResponse)
            mSocket!!.on("whoareyou", whoAreYou)
            mSocket!!.on("newpm", onNewMessage)
            mSocket!!.on("userstatus", userStatus)
            mSocket!!.connect()
        } else {
            pbLoading.visibility = View.GONE
         //   imgBtnSend.visibility = View.VISIBLE
            imgBtnSend.isEnabled = true
            DrawableCompat.setTint(DrawableCompat.wrap(imgBtnSend.drawable),
                ContextCompat.getColor(this@ChatActivity, R.color.primary_500))
        }

//        needRefresh = true
//        handler.postDelayed(Runnable {
//            handler.postDelayed(runnable!!, delay.toLong())
//            if (needRefresh) lastMessages
//        }.also { runnable = it }, delay.toLong())
    }

    private val onDisconnect = Emitter.Listener { runOnUiThread { Log.i(TAG, "diconnected") } }
    private val onConnect = Emitter.Listener { runOnUiThread({ Log.e(TAG, "Success connecting") }) }
    private val onConnectError = Emitter.Listener { args: Array<Any?>? ->
        runOnUiThread {
            if (args != null) {
                for(item in args.withIndex())
                    Log.e(TAG, "Error connecting " + item.value)
            }
        }
    }
    private val onNewMessage = Emitter.Listener { args: Array<Any?>? ->
        runOnUiThread {
            try {
                Log.e(TAG, "message response" + args?.get(0)!!)
                val jsonObject = JSONObject(args.get(0).toString())

                val gson = Gson()
                val message = gson.fromJson(jsonObject.toString(), MessageItem::class.java)
                adapter!!.addItems(message)
                items.add(message)
                if (isLastPosition) {
                    recyclerView.scrollToPosition(adapter!!.itemCount - 1)
                } else {
                    unreadCount = unreadCount + 1
                    txtCount.visibility = View.VISIBLE
                    txtCount.text = unreadCount.toString()
                }

            } catch (e: Exception) {
                Log.e(TAG, e.message!!)
                return@runOnUiThread
            }
        }
    }
    private val userStatus = Emitter.Listener { args: Array<Any?>? ->
        runOnUiThread {
            try {
                Log.e(TAG, "status response" + args)
                setToolbarSubTitle((args!![0] as JSONObject).get(conversationModel!!.receiver.id.toString()) == 0)
            } catch (e: Exception) {
                Log.e(TAG, e.message!!)
                return@runOnUiThread
            }
        }
    }
    private val whoAreYou = Emitter.Listener { _: Array<Any?>? ->
        runOnUiThread {
            try {
                mSocket!!.emit("auth", sessionManager.accessToken)
                Log.e(TAG, "who are you response")
            } catch (e: Exception) {
                Log.e(TAG, e.message!!)
                return@runOnUiThread
            }
        }
    }
    private val onAutResponse = Emitter.Listener { args: Array<Any> ->
        runOnUiThread {
            try {
                if (args[0] as Boolean) {
                    Log.e(TAG, "Success autResponse")
                    mSocket!!.emit("subscribe", "userstatus-" + conversationModel!!.receiver.id.toString())
                    mSocket!!.emit("subscribe", "conversation-" + conversationModel!!.id)
                    mSocket!!.emit("isonline", conversationModel!!.receiver.id.toString())
                    pbLoading.visibility = View.GONE
                  //  imgBtnSend.visibility = View.VISIBLE
                    imgBtnSend.isEnabled = true
                    DrawableCompat.setTint(DrawableCompat.wrap(imgBtnSend.drawable),
                        ContextCompat.getColor(this@ChatActivity, R.color.primary_500))
                }

                //
            } catch (e: Exception) {
                Log.e(TAG, e.message!!)
                return@runOnUiThread
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        unwrappedDrawable = AppCompatResources.getDrawable(this, R.drawable.new_design_chat_send)!!
        wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable)

        ButterKnife.bind(this)
        ConstantKey.IS_CHAT_SCREEN = true
        setId()
        initToolbar()
        initVars()
        doApiCall()
        initComponentScroll()
        setOnclick()
        addBlockingSystem()
    }

    private fun setOnclick() {
        lytScrollDown.setOnClickListener {
            animateFab(true)
            unreadCount = 0
            txtCount.text = unreadCount.toString()
            recyclerView.smoothScrollToPosition(adapter!!.itemCount)
        }
        uploadableImage = object : AbstractUploadableImageImpl(this) {
            override fun onImageReady(imageFile: File) {
                Glide.with(imgAttachment).load(Uri.fromFile(imageFile)).into(imgAttachment)
                imageFileTemp = imageFile
                imgBtnImageSelect.visibility = View.INVISIBLE
                rlImage.visibility = View.VISIBLE
                attachmentLayout.visibility = View.GONE
               // more.visibility = View.VISIBLE
                // uploadDataInPortfolioMediaApi(imageFile)
            }

            override fun onPdfReady(pdf: File, string: String, uri: Uri) {
                imageFileTemp = pdf
                imgAttachment.setImageDrawable(AppCompatResources.getDrawable(this@ChatActivity, R.drawable.ic_picture_as_pdf))
                imgBtnImageSelect.visibility = View.INVISIBLE
                rlImage.visibility = View.VISIBLE
                attachmentLayout.visibility = View.GONE
//                val id = DocumentsContract.getDocumentId(uri)
//                val contentUri = ContentUris.withAppendedId(
//                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
//                )

//                val projection = arrayOf(MediaStore.Images.Media.DATA)
//                val cursor: Cursor? =
//                    contentResolver.query(contentUri, projection, null, null, null)
//                val column_index: Int? = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//                cursor?.moveToFirst()
//                imageFileTemp = column_index?.let { cursor.getString(it)?.let { File(it) } }
            }
        }
        imgDelete.setOnClickListener {
            imgBtnImageSelect.visibility = View.VISIBLE
            rlImage.visibility = View.GONE
            imageFileTemp = null
            more.visibility = View.VISIBLE
        }
        imgBtnImageSelect.setOnClickListener { uploadableImage!!.showAttachmentImageBottomSheet(false) }
        imgBtnSend.setOnClickListener {
            if (validation()) {
                val strMessage = Objects.requireNonNull(edtCommentMessage.text).toString().trim { it <= ' ' }
                addCommentIntoServer(imageFileTemp, strMessage)
                edtCommentMessage.text = null
            }
//            else
//                showToast("cant send!", this@ChatActivity)
        }
        imgBtnTaskAction.setOnClickListener {
//            val intent = Intent(this@ChatActivity, TaskDetailsActivity::class.java)
//            val bundle = Bundle()
//            bundle.putString(ConstantKey.SLUG, conversationModel!!.slug)
//            intent.putExtras(bundle)
//            startActivity(intent)


                val intent = Intent(this, JobDetailsActivity::class.java)
                val bundle = Bundle()
                bundle.putString(ConstantKey.SLUG, conversationModel!!.slug)
                intent.putExtras(bundle)
                startActivity(intent)

        }
        lytTaskDetails.setOnClickListener {
            val intent = Intent(this@ChatActivity, TaskDetailsActivity::class.java)
            val bundle = Bundle()
            bundle.putString(ConstantKey.SLUG, conversationModel!!.slug)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        more.setOnClickListener {
            attachmentLayout.visibility = View.VISIBLE
            more.visibility = View.GONE
        }
        edtCommentMessage.setOnFocusChangeListener{ _, b->
            if(b) {
                attachmentLayout.visibility = View.GONE
                more.visibility = View.VISIBLE
            }
            else
            {
                attachmentLayout.visibility = View.VISIBLE
                more.visibility = View.GONE
            }
        }


        edtCommentMessage.setOnClickListener{
            attachmentLayout.visibility = View.GONE
            more.visibility = View.VISIBLE
        }

        noActiveChat.setOnClickListener {
            if(noActiveChattxt.text.contains("blocked"))
                showDialogBlock("unblock")
             //  setUserBlockState(false)
        }


//        edtCommentMessage.setOnTouchListener { v, event ->
//            attachmentLayout.visibility = View.GONE
//            more.visibility = View.VISIBLE
//            v?.onTouchEvent(event) ?: true
//        }

        camera.setOnClickListener { view1: View? ->
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                val permissionlistener: PermissionListener = object : PermissionListener {

                    override fun onPermissionGranted() {
                        val cameraIntent = NewCameraUtil.getTakePictureIntent(this@ChatActivity)
                        if (cameraIntent == null) {
                            showToast("can not write to your files to save picture.", this@ChatActivity)
                        }
                        try {
                            startActivityForResult(cameraIntent,
                                AttachmentBottomSheet.CAMERA_REQUEST
                            )
                        } catch (e: ActivityNotFoundException) {
                            showToast("Can not find your camera.", this@ChatActivity)
                        }
                    }

                    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {

                    }
                }
                TedPermission.with(this)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(Manifest.permission.CAMERA)
                    .check()
            } else {
                val cameraIntent = NewCameraUtil.getTakePictureIntent(this)
                if (cameraIntent == null) {
                    showToast("can not write to your files to save picture.", this@ChatActivity)
                }
                try {
                    startActivityForResult(cameraIntent,
                        AttachmentBottomSheet.CAMERA_REQUEST
                    )
                } catch (e: ActivityNotFoundException) {
                    showToast("Can not find your camera.", this@ChatActivity)
                }
            }
        }

        gallery.setOnClickListener { v: View? ->
            if (checkPermissionREAD_EXTERNAL_STORAGE(this@ChatActivity)) {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                    AttachmentBottomSheet.GALLERY_REQUEST
                )
            }
        }

        document.setOnClickListener{
            if (checkPermissionREAD_EXTERNAL_STORAGE(this@ChatActivity)) {

                try {
                    val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
                    pdfIntent.type = "application/pdf"
                    pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
                    startActivityForResult(Intent.createChooser(pdfIntent, "PDF"), AttachmentBottomSheet.PDF_REQUEST)
//                startActivityForResult(Intent.createChooser(pdfOpenintent, "Select Pdf"),
//                    AttachmentBottomSheet.PDF_REQUEST
                    //  )

                    //  startActivity(pdfOpenintent)
                } catch (e: ActivityNotFoundException) {
                    showToast("SomeThing to wrong", this@ChatActivity)

                }
            }
        }


    }

    private fun initVars() {
        app = this.application as AppController
        attachment = AttachmentModel()
        if (app.socket != null)
            if (app.socket.connected())
                app.socket.disconnect()
        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.getParcelable<Parcelable?>(ConstantKey.CONVERSATION) != null) {
                try {
                    conversationModel = bundle.getParcelable(ConstantKey.CONVERSATION)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        if (conversationModel != null) {
            setToolbar(conversationModel)
            if (conversationModel!!.chatClosed != null &&
                    conversationModel!!.chatClosed) {
                rltLayoutActionData.visibility = View.GONE
                cvAction.visibility = View.GONE
                noActiveChat.visibility = View.VISIBLE
                noActiveChattxt.text = Html.fromHtml(getString(R.string.chat_close_state))
                rltLayoutActionDataDeactive.visibility = View.GONE
            }
        }
        recyclerView.setHasFixedSize(true)
        chatModelArrayList = ArrayList()
        swipeRefresh.setOnRefreshListener(this)
        // use a linear layout manager
        layoutManager = LinearLayoutManager(this@ChatActivity)
       // layoutManager!!.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        adapter = ChatAdapter(this@ChatActivity, ArrayList(), conversationModel!!.sender.id)
        recyclerView.adapter = adapter
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        toolbar.subtitle = "Offline"
        toolbar.setNavigationOnClickListener { onBackPressed() }
        toolbar.menu.clear()
    }

    private fun setId() {
        toolbar = findViewById(R.id.toolbar)
        cvAction = findViewById(R.id.cvAction)
        btnUnblock = findViewById(R.id.btnUnblock)
        txtStatus = findViewById(R.id.txt_status)

      //  cardStatus = findViewById(R.id.card_status)
        imgBtnTaskAction = findViewById(R.id.img_btn_task_action)
        imgBtnTaskAction.setText("View Job Details")
        imgAttachment = findViewById(R.id.img_attachment)
        lytTaskDetails = findViewById(R.id.lyt_task_details)
        recyclerView = findViewById(R.id.recycler_view)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        swipeRefresh.setColorSchemeColors(Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE)
        swipeRefresh.setBackgroundColor(getColor(android.R.color.transparent))
        imgBtnImageSelect = findViewById(R.id.img_btn_image_select)
        imgDelete = findViewById(R.id.img_btn_delete)
        edtCommentMessage = findViewById(R.id.edt_comment_message)
        imgBtnSend = findViewById(R.id.img_btn_send)
        rltLayoutActionData = findViewById(R.id.rlt_layout_action_data)
        rltLayoutActionDataDeactive = findViewById(R.id.rlt_layout_action_data_deactive)
        rlImage = findViewById(R.id.rl_image)
        imgAvatar = findViewById(R.id.img_avatar)
        txtTitle = findViewById(R.id.txt_title)
        txtSubtitle = findViewById(R.id.txt_subtitle)
        txtJobTitle = findViewById(R.id.txt_job_title)
        bottomSheet = findViewById(R.id.bottom_sheet)
        lytScrollDown = findViewById(R.id.lyt_scroll_down)
        txtCount = findViewById(R.id.txtCount)
        pbLoading = findViewById(R.id.pbLoading)
        icSetting = findViewById(R.id.icSetting)
        attachmentLayout = findViewById(R.id.attachmentsParent)
        more = findViewById(R.id.img_chat_more)
        gallery = findViewById(R.id.gallery)
        camera = findViewById(R.id.camera)
        document = findViewById(R.id.pdfDocument)
        noActiveChat = findViewById(R.id.noActiveChat)
        noActiveChattxt = findViewById(R.id.noActiveChattxt)
    }

    private fun addBlockingSystem() {
        setPopUpWindow()
        icSetting.setOnClickListener {
            mypopupWindow!!.showAsDropDown(icSetting, 0, Tools.px2dip(this, -20f))
        }

        btnUnblock.setOnClickListener {
            showDialogBlock("unblock")
            //  setUserBlockState(false) }
        }
        if (conversationModel != null) {
            if (conversationModel!!.blocked_by != null) {
                if (sessionManager.userAccount.id == conversationModel!!.blocked_by) {
                    btnUnblock.visibility = View.VISIBLE
                    cvAction.visibility = View.GONE
                    noActiveChat.visibility = View.VISIBLE
                   // noActiveChattxt.text = getString(R.string.chat_block_state)
                    noActiveChattxt.text = Html.fromHtml(getString(R.string.chat_block_state))                }
            }
        }
    }


    private fun setPopUpWindow() {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.chats_menu, null)
        mypopupWindow = PopupWindow(view, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true)
        mypopupWindow!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val block = view.findViewById<View>(R.id.blockParent) as LinearLayout
        val blockText = view.findViewById<View>(R.id.block) as TextView
        val blockIcon = view.findViewById<View>(R.id.block_icon) as ImageView
        val report = view.findViewById<View>(R.id.reportParent) as LinearLayout
        if(conversationModel!!.blocked_by == sessionManager.userAccount.id) {
            blockIcon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.new_design_block_black))
            blockText.setTextColor(getColor(R.color.light_neutral_n900))
            blockText.text = "Unblock user"

        }
        else {
            blockIcon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.new_design_block ))
            blockText.setTextColor(getColor(R.color.primary_error))
            blockText.text = "Block user"
        }
        report.setOnClickListener {
            val bundleReport = Bundle()
            val intentReport = Intent(this, ReportUser::class.java)
            bundleReport.putString(Constant.userID, conversationModel!!.id.toString())
            bundleReport.putString("key", ConstantKey.KEY_USER_REPORT)
            bundleReport.putString("name", conversationModel!!.name.toString())
            intentReport.putExtras(bundleReport)
            startActivity(intentReport)
//            val intent = Intent(this, ReportUser::class.java)
//            startActivity(intent)
        }
        block.setOnClickListener {
            if(conversationModel!!.blocked_by == sessionManager.userAccount.id)
                showDialogBlock("unblock")
            else
                showDialogBlock("block")
            mypopupWindow!!.dismiss()
//            val confirmBottomSheet = ConfirmBlockTaskBottomSheet(this, conversationModel!!.receiver.name)
//            confirmBottomSheet.listener = this
//            confirmBottomSheet.show(this.supportFragmentManager, "")
        }
    }

    private fun setUserBlockState(state: Boolean) {
        Log.d("ConversationId", conversationModel!!.id.toString())
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Constant.URL_BLOCK_CHAT,
                Response.Listener {
                    Log.d("blockresponse", it.toString())
                    if (state) {
                        conversationModel!!.blocked_by = sessionManager.userAccount.id
                        btnUnblock.visibility = View.VISIBLE
                        cvAction.visibility = View.GONE
                        noActiveChat.visibility = View.VISIBLE
                     //   noActiveChattxt.text = getString(R.string.chat_block_state)
                        noActiveChattxt.text = Html.fromHtml(getString(R.string.chat_block_state))


                    } else {
                        conversationModel!!.blocked_by = 0
                        btnUnblock.visibility = View.GONE
                        cvAction.visibility = View.VISIBLE
                        noActiveChat.visibility = View.GONE


                        // noActiveChattxt.text = getString(R.string.chat_block_state)
                    }
                    hideProgressDialog()
                },
                Response.ErrorListener { hideProgressDialog() }) {
            override fun getParams(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["conversation_id"] = conversationModel!!.id.toString()
                if (state) map1["block"] = "1" else map1["block"] = "0"
                return map1
            }

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
        val requestQueue = Volley.newRequestQueue(this@ChatActivity)
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
    }

    override fun onBackPressed() {
        ConstantKey.IS_CHAT_SCREEN = false
        val intent = Intent()
        val bundle = Bundle()
        bundle.putBoolean(ConstantKey.PRIVATE_CHAT, true)
        intent.putExtras(bundle)
        setResult(ConstantKey.RESULTCODE_PRIVATE_CHAT, intent)
        super.onBackPressed()
    }

    private fun initComponentScroll() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //TODO this is due
                if (dy > 0) {
                    animateFab(true)
                } else {
                    unreadCount = 0
                    animateFab(false)
                }
                val visibleItemCount = layoutManager!!.childCount
                val totalItemCount = layoutManager!!.itemCount
                val pastVisibleItems = layoutManager!!.findFirstVisibleItemPosition()
                //End of list
                isLastPosition = pastVisibleItems + visibleItemCount >= totalItemCount
            }
        })
    }

    var isFabHide = false
    private fun animateFab(hide: Boolean) {
        if (isFabHide && hide || !isFabHide && !hide) return
        isFabHide = hide
        val moveY = if (hide) 2 * lytScrollDown.height else 0
        lytScrollDown.animate().translationY(moveY.toFloat()).setStartDelay(100).setDuration(300).start()
        if (unreadCount == 0) {
            txtCount.visibility = View.GONE
        } else {
            txtCount.visibility = View.VISIBLE
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setToolbar(conversationModel: ConversationModel?) {
        if (conversationModel != null && conversationModel.receiver != null) {
            if (conversationModel.receiver.name != null) {
                txtTitle.text = conversationModel.receiver.name
                imgAvatar.setOnClickListener { v: View? ->
                    val intent = Intent(this@ChatActivity, ProfileActivity::class.java)
                    intent.putExtra("id", conversationModel.receiver.id)
                    startActivity(intent)
                }
                txtTitle.setOnClickListener { v: View? -> imgAvatar.performClick() }
            }
        }
        if (conversationModel!!.receiver != null && conversationModel!!.receiver.avatar != null && conversationModel.receiver.avatar.thumbUrl != null) {
//            imgAvatar.setImageDrawable(getResources().getDrawable(R.drawable.avatar));
            ImageUtil.displayImage(imgAvatar, conversationModel.receiver.avatar.thumbUrl, null)
        } else {
            imgAvatar.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.pic))
        }
        txtJobTitle.text = conversationModel.name
        txtStatus.text = conversationModel.status

//        when (conversationModel.status) {
//            Constant.TASK_CANCELLED -> {
//                cardStatus.setCardBackgroundColor(getColor(R.color.R050))
//                txtStatus.setTextColor(ContextCompat.getColor(this, R.color.myJobsColorTaskCancelledTrans))
//            }
//            Constant.TASK_CLOSED -> {
//                cardStatus.setCardBackgroundColor(getColor(R.color.N030))
//                txtStatus.setTextColor(ContextCompat.getColor(this, R.color.N080))
//            }
//            Constant.TASK_COMPLETE, "completed" -> {
//                cardStatus.setCardBackgroundColor(getColor(R.color.G050))
//                txtStatus.setTextColor(ContextCompat.getColor(this, R.color.G400))
//            }
//            (Constant.TASK_OPEN), "posted", "offered" -> {
//                cardStatus.setCardBackgroundColor(getColor(R.color.P050))
//                txtStatus.setTextColor(ContextCompat.getColor(this, R.color.P300))
//            }
//            Constant.TASK_ASSIGNED, "overdue" -> {
//                cardStatus.setCardBackgroundColor(getColor(R.color.Y050))
//                txtStatus.setTextColor(ContextCompat.getColor(this, R.color.Y400))
//            }
//            else -> {
//                cardStatus.setCardBackgroundColor(getColor(R.color.colorTaskCompleted))
//            }
//        }

    }


    @SuppressLint("SetTextI18n")
    private fun setToolbarSubTitle(status: Boolean) {
        if (status) {
            runOnUiThread {
                txtSubtitle.text = "Online"
                txtSubtitle.setTextColor(resources.getColor(R.color.colorPrimary))
            }
        } else {
            runOnUiThread {
                txtSubtitle.text = "Offline"
                txtSubtitle.setTextColor(resources.getColor(R.color.colorRedOffline))
            }
        }
    }


    private fun doApiCall() {
        items = ArrayList()
        Helper.closeKeyboard(this@ChatActivity)
        val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.URL_CHAT + "/" + conversationModel!!.id + "/messages" + "?page=" + currentPage,
                Response.Listener { response: String? ->
                    Timber.e(response)
                    // categoryArrayList.clear();
                    try {
                        Log.d("chatres",response.toString())

                        val jsonObject = JSONObject(response!!)
                        val gson = Gson()
                        val chatResponse = gson.fromJson(jsonObject.toString(), ChatResponse::class.java)
                        Timber.e(jsonObject.toString())
                        if (chatResponse.data == null) {
                            showToast("SomeThing to wrong", this@ChatActivity)
                            return@Listener
                        }
                        items.addAll(chatResponse.data.reversed())
                        if (chatResponse.meta != null) {
                            Constant.PAGE_SIZE = chatResponse.meta.per_page!!
                        }
                        adapter!!.addItems(items)

                        swipeRefresh.isRefreshing = false

                    } catch (e: JSONException) {
                        hideProgressDialog()
                        Timber.e(e.toString())
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    swipeRefresh.isRefreshing = false
                    errorHandle1(error.networkResponse)
                }) {
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
        val requestQueue = Volley.newRequestQueue(this@ChatActivity)
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
    }// categoryArrayList.clear();


    override fun onDestroy() {
        // Disconnect from the service
        mSocket!!.disconnect()
        mSocket!!.off(Socket.EVENT_CONNECT, onConnect)
        mSocket!!.off(Socket.EVENT_DISCONNECT, onDisconnect)
        mSocket!!.off(Socket.EVENT_CONNECT_ERROR, onConnectError)
        mSocket!!.off("auth", onAutResponse)
        mSocket!!.off("whoareyou", whoAreYou)
        mSocket!!.off("newpm", onNewMessage)
        mSocket!!.off("userstatus", userStatus)
        super.onDestroy()
    }

    private fun addCommentIntoServer(pictureFile: File?, str_message: String) {
        pbLoading.visibility = View.VISIBLE
      //  imgBtnSend.visibility = View.GONE
        imgBtnSend.isEnabled = false
        DrawableCompat.setTint(DrawableCompat.wrap(imgBtnSend.drawable),
            ContextCompat.getColor(this, R.color.neutral_light_500))
        var call: Call<String>
        call = ApiClient.getClient().sendMessage("XMLHttpRequest", sessionManager.tokenType!! + " " + sessionManager.accessToken!!, conversationModel!!.id.toString(), str_message)
        if (pictureFile != null) {
            val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile)
            val imageFile = MultipartBody.Part.createFormData("attachment", pictureFile.name, requestFile)

            call = ApiClient.getClient().sendMessageWithImage("XMLHttpRequest", sessionManager.tokenType!! + " " + sessionManager.accessToken!!, imageFile, conversationModel!!.id.toString(), str_message)
        }
        call.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: retrofit2.Response<String?>) {
                imageFileTemp = null
                imgBtnImageSelect.visibility = View.VISIBLE
                rlImage.visibility = View.GONE
                more.visibility = View.VISIBLE
                pbLoading.visibility = View.GONE
             //   imgBtnSend.visibility = View.VISIBLE
                imgBtnSend.isEnabled = true
                DrawableCompat.setTint(DrawableCompat.wrap(imgBtnSend.drawable),
                    ContextCompat.getColor(this@ChatActivity, R.color.primary_500))
                Timber.d(response.toString())
                if (response.code() == HttpStatus.HTTP_VALIDATION_ERROR) {
                    showToast(response.message(), this@ChatActivity)
                    return
                }
                try {
                    val strResponse = response.body()
                    if (response.code() == HttpStatus.NOT_FOUND) {
                        showToast("not found", this@ChatActivity)
                        return
                    }
                    if (response.code() == HttpStatus.AUTH_FAILED) {
                        unauthorizedUser()
                        return
                    }
                    if (response.code() == HttpStatus.SUCCESS) {
                        Timber.e(strResponse)
                        assert(strResponse != null)
                    } else {

                        showToast(JSONObject(response.errorBody()!!.string().toString()).getJSONObject("error").getString("message"), this@ChatActivity)
                    }
                } catch (e: JSONException) {
                    showToast("Something went wrong", this@ChatActivity)
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                //imageFileTemp = null
                pbLoading.visibility = View.GONE
             //   imgBtnSend.visibility = View.VISIBLE
                imgBtnSend.isEnabled = true
                DrawableCompat.setTint(DrawableCompat.wrap(imgBtnSend.drawable),
                    ContextCompat.getColor(this@ChatActivity, R.color.primary_500))
            ContextCompat.getColor(this@ChatActivity, R.color.primary_500)
                showToast("Something went wrong", this@ChatActivity)
                Log.d("errorOnSending", t.message.toString())
            }
        })
    }

    private fun validation(): Boolean {
        if (TextUtils.isEmpty(Objects.requireNonNull(edtCommentMessage.text).toString().trim { it <= ' ' })) {
            Log.d("validation", "ggg")
            if (imageFileTemp != null)
                return true
            //edtCommentMessage.error = "?"
            return false
        }
        return true
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        uploadableImage!!.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRefresh() {
        swipeRefresh.isRefreshing = true
        if (!isLastPage) {
            currentPage++
            doApiCall()
        } else {
            swipeRefresh.isRefreshing = false
        }
    }

    override fun onPause() {
        super.onPause()
        ConstantKey.IS_CHAT_SCREEN = false
    }

    companion object {
        @JvmField
        var conversationModel: ConversationModel? = null
        private const val TAG = "Chat Activity"
    }

    override fun onBlockConfirmClick() {
        setUserBlockState(true)
    }
    fun checkPermissionREAD_EXTERNAL_STORAGE(context: Context?): Boolean {
        val permissionlistener: PermissionListener = object : PermissionListener {

            override fun onPermissionGranted() {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                    AttachmentBottomSheet.GALLERY_REQUEST
                )

            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {

            }
        }
        val currentAPIVersion = Build.VERSION.SDK_INT
        return if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this@ChatActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                TedPermission.with(context)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .check()
                false
            } else {
                true
            }
        } else {
            true
        }
    }

    fun showDialogBlock(mode: String) {
        val cancel: MaterialButton?
        val delete: MaterialButton?
        val title: TextView?
        val mainTitle: TextView?
        val dialog = Dialog(this, R.style.AnimatedDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        dialog.setContentView(R.layout.dialog_discard_changes_new)

        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog.window!!.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        cancel = dialog.findViewById(R.id.cancel)
        delete = dialog.findViewById(R.id.discard)
        title = dialog.findViewById(R.id.title)
        mainTitle = dialog.findViewById(R.id.mainTitle)

        if(mode == "block") {
            delete.text = getString(R.string.block)
            mainTitle.text = getString(R.string.confirm_block_user)
            title.text = this.getString(R.string.block_user_title)

        }
        else {
            delete.setTextColor(getColor(R.color.primary_p500_base_light))
            title.text = getString(R.string.unblock_user_title) + conversationModel!!.name
            mainTitle.text = getString(R.string.confirm_unblock_user)
            delete.text = getString(R.string.unblock)
        }

        cancel.text = getString(R.string.cancel)

        cancel.setOnClickListener {
            dialog.cancel()
        }
        delete.setOnClickListener {
            //viewModel.deleteAccount(activity)
            if(mode == "block")
                setUserBlockState(true)
            else
                setUserBlockState(false)
            dialog.cancel()
        }

        dialog.show()

    }

}