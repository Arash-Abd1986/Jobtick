package com.jobtick.android.activities

import com.jobtick.android.adapers.AttachmentAdapter
import com.jobtick.android.models.AttachmentModel
import com.jobtick.android.R
import com.google.android.material.appbar.MaterialToolbar
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.graphics.Bitmap
import android.os.Bundle
import com.jobtick.android.utils.ConstantKey
import android.os.Parcelable
import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.jobtick.android.widget.SpacingItemDecoration
import com.jobtick.android.utils.Tools
import android.widget.LinearLayout
import android.os.Build
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.view.WindowManager
import android.content.DialogInterface
import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import timber.log.Timber
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.android.volley.*
import okhttp3.RequestBody
import okhttp3.MultipartBody
import com.jobtick.android.retrofit.ApiClient
import org.json.JSONException
import com.jobtick.android.utils.HttpStatus
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.jobtick.android.BuildConfig
import com.jobtick.android.utils.Constant
import okhttp3.MediaType
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AttachmentActivity : ActivityBase(), AttachmentAdapter.OnItemClickListener {
    private lateinit var attachmentArrayList: ArrayList<AttachmentModel>
    private lateinit var toolbar: MaterialToolbar
    private lateinit var bottomSheet: FrameLayout
    private lateinit var recyclerView: RecyclerView

    private var mBehavior: BottomSheetBehavior<*>? = null
    private var mBottomSheetDialog: BottomSheetDialog? = null
    private var fileUri: Uri? = null
    private var imagePath: String? = null
    private var filePath: Uri? = null
    private var bitmap: Bitmap? = null
    var adapter: AttachmentAdapter? = null
    private var strVideoPath: String? = null
    private var title: String? = null
    private var slug: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attachment)
        setIDS()
        mBehavior = BottomSheetBehavior.from<FrameLayout?>(bottomSheet)
        attachmentArrayList = ArrayList()
        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.getString(ConstantKey.TITLE) != null) {
                title = bundle.getString(ConstantKey.TITLE)
            }
            if (bundle.getString(ConstantKey.SLUG) != null) {
                slug = bundle.getString(ConstantKey.SLUG)
            }
            if (bundle.getParcelableArrayList<Parcelable>(ConstantKey.ATTACHMENT) != null) {
                attachmentArrayList = bundle.getParcelableArrayList(ConstantKey.ATTACHMENT)!!
                attachmentArrayList.add(AttachmentModel())
            } else {
                attachmentArrayList.add(AttachmentModel())
            }
        } else {
            attachmentArrayList.add(AttachmentModel())
        }
        init()
        initToolbar()
    }

    private fun setIDS() {
        toolbar= findViewById(R.id.toolbar)
        bottomSheet= findViewById(R.id.bottom_sheet)
        recyclerView= findViewById(R.id.recycler_view)
    }

    private fun initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_cancel)
        setSupportActionBar(toolbar)
        Objects.requireNonNull(supportActionBar)!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Attachment"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        attachmentArrayList.removeAt(attachmentArrayList.size - 1)
        val intent = Intent()
        intent.putExtra(ConstantKey.ATTACHMENT, attachmentArrayList)
        setResult(ConstantKey.RESULTCODE_ATTACHMENT, intent)
        super.onBackPressed()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun init() {
        recyclerView.layoutManager = GridLayoutManager(this@AttachmentActivity, 3)
        recyclerView.addItemDecoration(SpacingItemDecoration(3, Tools.dpToPx(this@AttachmentActivity, 5), true))
        recyclerView.setHasFixedSize(true)
        //set data and list adapter
        adapter = AttachmentAdapter(attachmentArrayList, true)
        recyclerView.adapter = adapter
        adapter!!.notifyDataSetChanged()
        adapter!!.setOnItemClickListener(this)
    }

    private fun showBottomSheetDialog() {
        if (mBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        val view = layoutInflater.inflate(R.layout.sheet_attachment, null)
        val lytBtnCamera = view.findViewById<LinearLayout>(R.id.lyt_btn_camera)
        val lytBtnImage = view.findViewById<LinearLayout>(R.id.lyt_btn_image)
        val lytBtnVideo = view.findViewById<LinearLayout>(R.id.lyt_btn_video)
        val lytBtnDoc = view.findViewById<LinearLayout>(R.id.lyt_btn_doc)
        val lyrBtnVideoCamera = view.findViewById<LinearLayout>(R.id.lyt_btn_video_camera)
        lytBtnCamera.setOnClickListener { view1: View? ->
            if (checkSelfPermission(permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(permission.CAMERA), MY_CAMERA_PERMISSION_CODE)
            } else {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            }
            mBottomSheetDialog!!.hide()
        }
        lytBtnVideo.setOnClickListener { view12: View? ->
            galleryVideo
            mBottomSheetDialog!!.hide()
        }
        lyrBtnVideoCamera.setOnClickListener { view13: View? ->
            recordVideo()
            mBottomSheetDialog!!.hide()
        }
        lytBtnDoc.setOnClickListener { view14: View? ->
            val intent = Intent()
            intent.type = "application/pdf"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select PDF File"), 1001)
            mBottomSheetDialog!!.hide()
        }
        lytBtnImage.setOnClickListener {
            if (checkPermissionReadExternalStorage(this@AttachmentActivity)) {
                val opengallary = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(Intent.createChooser(opengallary, "Open Gallary"), 1)
            }
            mBottomSheetDialog!!.hide()
        }
        mBottomSheetDialog = BottomSheetDialog(this)
        mBottomSheetDialog!!.setContentView(view)
        mBottomSheetDialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)


        // set background transparent
        (view.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))
        mBottomSheetDialog!!.show()
        mBottomSheetDialog!!.setOnDismissListener { dialog: DialogInterface? -> mBottomSheetDialog = null }
    }

    private val galleryVideo: Unit
        get() {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            intent.type = "video/*"
            startActivityForResult(intent, PICK_VIDEO)
        }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast("camera permission granted", this@AttachmentActivity)
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            } else {
                showToast("camera permission denied", this@AttachmentActivity)
            }
        }
    }

    fun checkPermissionReadExternalStorage(
            context: Context?): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        return if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context!!,
                            permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                                (context as Activity?)!!,
                                permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            permission.READ_EXTERNAL_STORAGE)
                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (context as Activity?)!!, arrayOf(permission.READ_EXTERNAL_STORAGE),
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
                }
                false
            } else {
                true
            }
        } else {
            true
        }
    }

    fun showDialog(msg: String, context: Context?,
                   permission: String) {
        val alertBuilder = AlertDialog.Builder(context)
        alertBuilder.setCancelable(true)
        alertBuilder.setTitle("Permission necessary")
        alertBuilder.setMessage("$msg permission is necessary")
        alertBuilder.setPositiveButton(android.R.string.yes
        ) { dialog: DialogInterface?, which: Int ->
            ActivityCompat.requestPermissions((context as Activity?)!!, arrayOf(permission),
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
        }
        val alert = alertBuilder.create()
        alert.show()
    }

    fun getPath(uri: Uri?): String {
        var cursor = contentResolver.query(uri!!, null, null, null, null)
        cursor!!.moveToFirst()
        var documentId = cursor.getString(0)
        documentId = documentId.substring(documentId.lastIndexOf(":") + 1)
        cursor.close()
        cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", arrayOf(documentId), null)
        cursor!!.moveToFirst()
        val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        Timber.e(path)
        cursor.close()
        return path
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == 1 && resultCode == RESULT_OK) {
                filePath = data!!.data
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                imagePath = getPath(data.data)
                //imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
                //  uploadImageWithAmount();
                val file = File(imagePath)
                if (title.equals(ConstantKey.CREATE_A_JOB, ignoreCase = true)) {
                    uploadDataInTempApi(file)
                } else {
                    uploadDataInTaskMediaApi(file)
                }
                Timber.e(imagePath)
            } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
                val bitmap = data!!.extras!!["data"] as Bitmap?
                //bitmap to convert into file
                val pictureFile = File(externalCacheDir, "jobtick")
                val fos = FileOutputStream(pictureFile)
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.close()
                if (title.equals(ConstantKey.CREATE_A_JOB, ignoreCase = true)) {
                    uploadDataInTempApi(pictureFile)
                } else {
                    uploadDataInTaskMediaApi(pictureFile)
                }
            } else if (requestCode == PICK_VIDEO && resultCode == RESULT_OK) {
                strVideoPath = getImagePath(data!!.data)
                Timber.e(strVideoPath)
                val mpl = MediaPlayer.create(this@AttachmentActivity, Uri.parse(strVideoPath))
                val si = mpl.duration
                val duration = TimeUnit.MILLISECONDS.toSeconds(si.toLong())
                val file = File(strVideoPath)
                val file_size = file.length().toString().toInt().toLong()
                if (duration > MAX_VIDEO_DURATION) {
                    showLimitDialog()
                    strVideoPath = null
                } else if (file_size > MAX_VIDEO_SIZE) {
                    showToast("Maximum video size exceeds(20 MB)", this@AttachmentActivity)
                    strVideoPath = null
                } else {
                    Timber.e(duration.toString() + "")
                    Timber.e(file_size.toString() + "")
                    // uploadUrl = strVideoPath;
                    Timber.e("video")
                    if (title.equals(ConstantKey.CREATE_A_JOB, ignoreCase = true)) {
                        uploadDataInTempApi(file)
                    } else {
                        uploadDataInTaskMediaApi(file)
                    }
                }
            } else if (requestCode == VIDEO_CAPTURE && resultCode == RESULT_OK) {
                strVideoPath = fileUri!!.path
                val mpl = MediaPlayer.create(this@AttachmentActivity, Uri.parse(strVideoPath))
                //                    MediaPlayer mpl = MediaPlayer.create(requireActivity(), data.getData());
                val si = mpl.duration
                val duration = TimeUnit.MILLISECONDS.toSeconds(si.toLong())
                val file = File(strVideoPath)
                val fileSize = file.length().toString().toInt().toLong()
                if (duration > MAX_VIDEO_DURATION) {
                    showLimitDialog()
                    strVideoPath = null
                } else if (fileSize > MAX_VIDEO_SIZE) {
                    showToast("Maximum video size exceeds(20 MB)", this@AttachmentActivity)
                    strVideoPath = null
                } else {
                    Timber.e(duration.toString() + "")
                    Timber.e(fileSize.toString() + "")
                    Timber.e("video")
                    if (title.equals(ConstantKey.CREATE_A_JOB, ignoreCase = true)) {
                        uploadDataInTempApi(file)
                    } else {
                        uploadDataInTaskMediaApi(file)
                    }
                }
            } else if (requestCode == 1001 && resultCode == RESULT_OK) {
                if (data != null && data.data != null) {
                    var file: File? = null
                    if (data.data!!.path != null) {
                        file = File(data.data!!.path)
                    }
                    if (file != null) {
                        if (title.equals(ConstantKey.CREATE_A_JOB, ignoreCase = true)) {
                            uploadDataInTempApi(file)
                        } else {
                            uploadDataInTaskMediaApi(file)
                        }
                    } else {
                        showToast("Try Again", this)
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                showToast(getString(R.string.msg_image_select), this@AttachmentActivity)
            } else {
                showToast(getString(R.string.error_image_select), this@AttachmentActivity)
            }
        } catch (e: Exception) {
            Timber.e(e.toString())
        }
    }

    private fun recordVideo() {
        try {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO)
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAX_VIDEO_DURATION)
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0)
            intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, MAX_VIDEO_SIZE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri) // set the image file
            // start the video capture Intent
            startActivityForResult(intent, VIDEO_CAPTURE)
        } catch (e: Exception) {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            fileUri = getOutputMediaFileUri1(MEDIA_TYPE_VIDEO)
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAX_VIDEO_DURATION)
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0)
            intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, MAX_VIDEO_SIZE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri) // set the image file

            // start the video capture Intent
            startActivityForResult(intent, VIDEO_CAPTURE)
        }
    }

    private fun getOutputMediaFileUri(type: Int): Uri {
        return Uri.fromFile(getOutputMediaFile(type))
    }

    private fun getOutputMediaFileUri1(type: Int): Uri {
        return FileProvider.getUriForFile(this@AttachmentActivity, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(type)!!)
    }

    private fun showLimitDialog() {
        val builder1 = androidx.appcompat.app.AlertDialog.Builder(this@AttachmentActivity)
        builder1.setMessage("Maximum video duration exceeds 30 sec")
        builder1.setCancelable(true)
        builder1.setPositiveButton(
                "OK"
        ) { dialog, id -> dialog.cancel() }
        val alert11 = builder1.create()
        alert11.show()
    }

    fun getImagePath(uri: Uri?): String {
        var cursor = contentResolver.query(uri!!, null, null, null, null)
        cursor!!.moveToFirst()
        var documentId = cursor.getString(0)
        documentId = documentId.substring(documentId.lastIndexOf(":") + 1)
        cursor.close()
        cursor = contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Video.Media._ID + " = ? ", arrayOf(documentId), null)
        cursor!!.moveToFirst()
        val path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
        cursor.close()
        return path
    }

    private fun uploadDataInTempApi(pictureFile: File) {
        showProgressDialog()
        val call: Call<String?>?

        //    File file = new File(imagePath);
        //    Log.e("AttachmentApi: ", file.getName());
        //   Log.e("uploadProfile++:11 ", imagePath);
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile)
        // MultipartBody.Part is used to send also the actual file name
        val imageFile = MultipartBody.Part.createFormData("media", pictureFile.name, requestFile)
        call = ApiClient.getClient().getTaskTempAttachmentMediaData( /*"application/x-www-form-urlencoded",*/"XMLHttpRequest", sessionManager.tokenType + " " + sessionManager.accessToken, imageFile)
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: retrofit2.Response<String?>) {
                hideProgressDialog()
                Timber.e(response.toString())
                if (response.code() == 422) {
                    showToast(response.message(), this@AttachmentActivity)
                    return
                }
                try {
                    val strResponse = response.body()
                    Timber.e(strResponse)
                    val jsonObject = JSONObject(strResponse)
                    Timber.e(jsonObject.toString())
                    if (jsonObject.has("data")) {
                        val jsonObject_data = jsonObject.getJSONObject("data")
                        val attachment = AttachmentModel().getJsonToModel(jsonObject_data)
                        if (attachmentArrayList.size != 0) {
                            attachmentArrayList.add(attachmentArrayList.size - 1, attachment)
                        }
                    }
                    adapter!!.notifyItemInserted(0)
                    //  adapter.notifyItemRangeInserted(0,attachmentArrayList.size());
                } catch (e: JSONException) {
                    showToast("Something went wrong", this@AttachmentActivity)
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                hideProgressDialog()
                Timber.e(call.toString())
            }
        })
    }

    private fun uploadDataInTaskMediaApi(pictureFile: File) {
        showProgressDialog()
        val call: Call<String?>?

        //    File file = new File(imagePath);
        //    Log.e("AttachmentApi: ", file.getName());
        //   Log.e("uploadProfile++:11 ", imagePath);
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile)
        // MultipartBody.Part is used to send also the actual file name
        val imageFile = MultipartBody.Part.createFormData("media", pictureFile.name, requestFile)
        call = ApiClient.getClient().getTasKAttachmentMediaUpload(slug, "XMLHttpRequest", sessionManager.tokenType + " " + sessionManager.accessToken, imageFile)
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: retrofit2.Response<String?>) {
                hideProgressDialog()
                Timber.e(response.toString())
                if (response.code() == 422) {
                    showToast(response.message(), this@AttachmentActivity)
                    return
                }
                try {
                    val strResponse = response.body()
                    if (response.code() == HttpStatus.NOT_FOUND) {
                        showToast("not found", this@AttachmentActivity)
                        return
                    }
                    if (response.code() == HttpStatus.AUTH_FAILED) {
                        unauthorizedUser()
                        return
                    }
                    if (response.code() == HttpStatus.SUCCESS) {
                        Timber.e(strResponse)
                        val jsonObject = JSONObject(strResponse)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("data")) {
                            val attachment = AttachmentModel()
                            val jsonObject_data = jsonObject.getJSONObject("data")
                            if (jsonObject_data.has("id") && !jsonObject_data.isNull("id")) attachment.id = jsonObject_data.getInt("id")
                            if (jsonObject_data.has("name") && !jsonObject_data.isNull("name")) attachment.name = jsonObject_data.getString("name")
                            if (jsonObject_data.has("file_name") && !jsonObject_data.isNull("file_name")) attachment.fileName = jsonObject_data.getString("file_name")
                            if (jsonObject_data.has("mime") && !jsonObject_data.isNull("mime")) attachment.mime = jsonObject_data.getString("mime")
                            if (jsonObject_data.has("url") && !jsonObject_data.isNull("url")) attachment.url = jsonObject_data.getString("url")
                            if (jsonObject_data.has("thumb_url") && !jsonObject_data.isNull("thumb_url")) attachment.thumbUrl = jsonObject_data.getString("thumb_url")
                            if (jsonObject_data.has("modal_url") && !jsonObject_data.isNull("modal_url")) attachment.modalUrl = jsonObject_data.getString("modal_url")
                            if (jsonObject_data.has("created_at") && !jsonObject_data.isNull("created_at")) attachment.createdAt = jsonObject_data.getString("created_at")
                            attachment.type = AttachmentAdapter.VIEW_TYPE_IMAGE
                            if (attachmentArrayList.size != 0) {
                                attachmentArrayList.add(attachmentArrayList.size - 1, attachment)
                            }
                        }
                        adapter!!.notifyItemInserted(0)
                        //  adapter.notifyItemRangeInserted(0,attachmentArrayList.size());
                        // showToast("attachment added", AttachmentActivity.this);
                    } else {
                        showToast("Something went wrong", this@AttachmentActivity)
                    }
                } catch (e: JSONException) {
                    showToast("Something went wrong", this@AttachmentActivity)
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                hideProgressDialog()
                Timber.e(call.toString())
            }
        })
    }

    override fun onItemClick(view: View, obj: AttachmentModel, position: Int, action: String) {
        if (action.equals("add", ignoreCase = true)) {
            showBottomSheetDialog()
        } else if (action.equals("delete", ignoreCase = true)) {
            if (title.equals(ConstantKey.CREATE_A_JOB, ignoreCase = true)) {
                recyclerView.removeViewAt(position)
                attachmentArrayList.removeAt(position)
                adapter!!.notifyItemRemoved(position)
                adapter!!.notifyItemRangeRemoved(position, attachmentArrayList.size)
                showToast("Delete this attachment", this@AttachmentActivity)
            } else {
                deleteMediaInAttachment(position, obj)
            }
        }
    }

    private fun deleteMediaInAttachment(position: Int, obj: AttachmentModel) {
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.DELETE, Constant.URL_TASKS + "/" + slug + "/attachment?media=" + attachmentArrayList[position].id,
                Response.Listener { response ->
                    Timber.e(response)
                    hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                recyclerView.removeViewAt(position)
                                attachmentArrayList.removeAt(position)
                                adapter!!.notifyItemRemoved(position)
                                adapter!!.notifyItemRangeRemoved(position, attachmentArrayList.size)
                                showToast("Delete this attachment", this@AttachmentActivity)
                            } else {
                                showToast("Something went Wrong", this@AttachmentActivity)
                            }
                        }
                    } catch (e: JSONException) {
                        Timber.e(e.toString())
                        e.printStackTrace()
                    }
                }, Response.ErrorListener { error: VolleyError ->
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

                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        showToast("Something Went Wrong", this@AttachmentActivity)
                    }
                    Timber.e(error.toString())
                    hideProgressDialog()
                }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                // map1.put("X-Requested-With", "XMLHttpRequest");
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this@AttachmentActivity)
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123
        const val VIDEO_FORMAT = ".mp4"
        const val VIDEO_SIGN = "VID_"
        private const val VIDEO_CAPTURE = 101
        private const val CAMERA_REQUEST = 1888
        private const val MY_CAMERA_PERMISSION_CODE = 100
        const val MAX_VIDEO_DURATION: Long = 30
        const val MAX_VIDEO_SIZE = (20 * 1024 * 1024).toLong()
        const val MEDIA_DIRECTORY_NAME = "Jobtick"
        const val VIDEO_NAME = "video_name"
        const val AD_THUMB = "thumb_name"
        const val TIME_STAMP_FORMAT = "yyyyMMdd_HHmmss"
        const val MEDIA_TYPE_VIDEO = 2
        private const val PICK_VIDEO = 107
        private fun getOutputMediaFile(type: Int): File? {
            val mediaStorageDir = File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    MEDIA_DIRECTORY_NAME)
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return null
                }
            }
            val mediaFile: File = if (type == MEDIA_TYPE_VIDEO) {
                File(mediaStorageDir.path + File.separator
                        + VIDEO_SIGN + timeStamp + VIDEO_FORMAT)
            } else {
                return null
            }
            Timber.e(mediaFile.toString())
            return mediaFile
        }

        private val timeStamp: String
            private get() = SimpleDateFormat(TIME_STAMP_FORMAT,
                    Locale.getDefault()).format(Date())
    }
}