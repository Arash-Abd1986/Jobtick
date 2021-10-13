package com.jobtick.android.activities

import android.Manifest
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.adapers.AttachmentAdapter
import com.jobtick.android.models.AttachmentModel
import com.jobtick.android.retrofit.ApiClient
import com.jobtick.android.utils.*
import com.jobtick.android.widget.SpacingItemDecoration
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
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
import java.util.concurrent.TimeUnit

class PortfolioActivity : ActivityBase(), AttachmentAdapter.OnItemClickListener {
    private lateinit var toolbar: MaterialToolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomSheet: FrameLayout
    private var mBehavior: BottomSheetBehavior<*>? = null
    private var mBottomSheetDialog: BottomSheetDialog? = null
    var adapter: AttachmentAdapter? = null
    var attachmentArrayList: ArrayList<AttachmentModel>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attachment)
        setIDs()
        mBehavior = BottomSheetBehavior.from<FrameLayout?>(bottomSheet)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        attachmentArrayList = ArrayList()
        val bundle = intent.extras
        if (bundle?.getParcelableArrayList<Parcelable>(ConstantKey.ATTACHMENT) != null) {
            attachmentArrayList = bundle.getParcelableArrayList(ConstantKey.ATTACHMENT)
        }
        init()
        restoreFromBundle(savedInstanceState)
    }

    private fun setIDs() {
        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.recycler_view)
        bottomSheet = findViewById(R.id.bottom_sheet)
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra(ConstantKey.ATTACHMENT, attachmentArrayList)
        setResult(101, intent)
        super.onBackPressed()
    }

    private fun restoreFromBundle(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_IMAGE_STORAGE_PATH)) {
                imageStoragePath = savedInstanceState.getString(KEY_IMAGE_STORAGE_PATH)
                if (!TextUtils.isEmpty(imageStoragePath)) {
                    if (imageStoragePath!!.substring(imageStoragePath!!.lastIndexOf(".")) == "." + ConstantKey.IMAGE_EXTENSION) {
                        //  previewCapturedImage();
                    } else if (imageStoragePath!!.substring(imageStoragePath!!.lastIndexOf(".")) == "." + ConstantKey.VIDEO_EXTENSION) {
                        //  previewVideo();
                    }
                }
            }
        }
    }

    private fun init() {
        recyclerView.layoutManager = GridLayoutManager(this@PortfolioActivity, 3)
        recyclerView.addItemDecoration(SpacingItemDecoration(3, Tools.dpToPx(this@PortfolioActivity, 5), true))
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
        lytBtnCamera.setOnClickListener(View.OnClickListener { // Checking availability of the camera
            if (!CameraUtils.isDeviceSupportCamera(applicationContext)) {
                showToast("Sorry! Your device doesn't support camera", this@PortfolioActivity)
                // will close the app if the device doesn't have camera
                return@OnClickListener
            }
            if (CameraUtils.checkPermissions(applicationContext)) {
                captureImage()
            } else {
                requestCameraPermission(ConstantKey.MEDIA_TYPE_IMAGE)
            }
            mBottomSheetDialog!!.hide()
        })
        lytBtnVideo.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            intent.type = "video/*"
            startActivityForResult(intent, GALLERY_PICKUP_VIDEO_REQUEST_CODE)
            mBottomSheetDialog!!.hide()
        }
        lyrBtnVideoCamera.setOnClickListener(View.OnClickListener { // Checking availability of the camera
            if (!CameraUtils.isDeviceSupportCamera(applicationContext)) {
                showToast("Sorry! Your device doesn't support camera", this@PortfolioActivity)
                // will close the app if the device doesn't have camera
                return@OnClickListener
            }
            if (CameraUtils.checkPermissions(applicationContext)) {
                captureVideo()
            } else {
                requestCameraPermission(ConstantKey.MEDIA_TYPE_VIDEO)
            }
            mBottomSheetDialog!!.hide()
        })
        lytBtnDoc.setOnClickListener {
            val intent = Intent()
            intent.type = "application/pdf"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select PDF File"), 1001)
            mBottomSheetDialog!!.hide()
        }
        lytBtnImage.setOnClickListener {
            val opengallary = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(Intent.createChooser(opengallary, "Open Gallary"), GALLERY_PICKUP_IMAGE_REQUEST_CODE)
            mBottomSheetDialog!!.hide()
        }
        mBottomSheetDialog = BottomSheetDialog(this)
        mBottomSheetDialog!!.setContentView(view)
        mBottomSheetDialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)


        // set background transparent
        (view.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))
        mBottomSheetDialog!!.show()
        mBottomSheetDialog!!.setOnDismissListener { mBottomSheetDialog = null }
    }

    /**
     * Requesting permissions using Dexter library
     */
    private fun requestCameraPermission(type: Int) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            if (type == ConstantKey.MEDIA_TYPE_IMAGE) {
                                // capture picture
                                captureImage()
                            } else {
                                captureVideo()
                            }
                        } else if (report.isAnyPermissionPermanentlyDenied) {
                            showPermissionsAlert()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }
                }).check()
    }

    /**
     * Capturing Camera Image will launch camera app requested image capture
     */
    private fun captureImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file = CameraUtils.getOutputMediaFile(ConstantKey.MEDIA_TYPE_IMAGE)
        if (file != null) {
            imageStoragePath = file.absolutePath
        }
        val fileUri = CameraUtils.getOutputMediaFileUri(applicationContext, file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE)
    }

    /**
     * Saving stored image path to saved instance state
     */
    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putString(KEY_IMAGE_STORAGE_PATH, imageStoragePath)
    }

    /**
     * Restoring image path from saved instance state
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // get the file url
        imageStoragePath = savedInstanceState.getString(KEY_IMAGE_STORAGE_PATH)
    }

    /**
     * Launching camera app to record video
     */
    private fun captureVideo() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        val file = CameraUtils.getOutputMediaFile(ConstantKey.MEDIA_TYPE_VIDEO)
        if (file != null) {
            imageStoragePath = file.absolutePath
        }
        val fileUri = CameraUtils.getOutputMediaFileUri(applicationContext, file)
        // set video quality
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAX_VIDEO_DURATION)
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0)
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, MAX_VIDEO_SIZE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri) // set the image file
        // start the video capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE)
    }

    override fun onItemClick(view: View, obj: AttachmentModel, position: Int, action: String) {
        if (action.equals("add", ignoreCase = true)) {
            showBottomSheetDialog()
        } else if (action.equals("delete", ignoreCase = true)) {
            deleteMediaInAttachment(position, obj)
        }
    }

    private fun uploadDataInPortfolioMediaApi(pictureFile: File) {
        showProgressDialog()
        val call: Call<String?>?
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile)
        val imageFile = MultipartBody.Part.createFormData("media", pictureFile.name, requestFile)
        call = ApiClient.getClient().getPortfolioMediaUpload("XMLHttpRequest", sessionManager.tokenType + " " + sessionManager.accessToken, imageFile)
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: retrofit2.Response<String?>) {
                hideProgressDialog()
                Timber.tag("Response").e(response.toString())
                if (response.code() == HttpStatus.HTTP_VALIDATION_ERROR) {
                    showToast(response.message(), this@PortfolioActivity)
                    return
                }
                try {
                    val strResponse = response.body()
                    if (response.code() == HttpStatus.NOT_FOUND) {
                        showToast("not found", this@PortfolioActivity)
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
                            val attachment = AttachmentModel()
                            val jsonObjectData = jsonObject.getJSONObject("data")
                            if (jsonObjectData.has("id") && !jsonObjectData.isNull("id")) attachment.id = jsonObjectData.getInt("id")
                            if (jsonObjectData.has("name") && !jsonObjectData.isNull("name")) attachment.name = jsonObjectData.getString("name")
                            if (jsonObjectData.has("file_name") && !jsonObjectData.isNull("file_name")) attachment.fileName = jsonObjectData.getString("file_name")
                            if (jsonObjectData.has("mime") && !jsonObjectData.isNull("mime")) attachment.mime = jsonObjectData.getString("mime")
                            if (jsonObjectData.has("url") && !jsonObjectData.isNull("url")) attachment.url = jsonObjectData.getString("url")
                            if (jsonObjectData.has("thumb_url") && !jsonObjectData.isNull("thumb_url")) attachment.thumbUrl = jsonObjectData.getString("thumb_url")
                            if (jsonObjectData.has("modal_url") && !jsonObjectData.isNull("modal_url")) attachment.modalUrl = jsonObjectData.getString("modal_url")
                            if (jsonObjectData.has("created_at") && !jsonObjectData.isNull("created_at")) attachment.createdAt = jsonObjectData.getString("created_at")
                            attachment.type = AttachmentAdapter.VIEW_TYPE_IMAGE
                            if (attachmentArrayList!!.size != 0) {
                                attachmentArrayList!!.add(attachmentArrayList!!.size - 1, attachment)
                            }
                        }
                        adapter!!.notifyDataSetChanged()
                        //  adapter.notifyItemRangeInserted(0,attachmentArrayList.size());
                        // showToast("attachment added", AttachmentActivity.this);
                    } else {
                        showToast("Something went wrong", this@PortfolioActivity)
                    }
                } catch (e: JSONException) {
                    showToast("Something went wrong", this@PortfolioActivity)
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                hideProgressDialog()
                Timber.tag("Response").e(call.toString())
            }
        })
    }

    private fun deleteMediaInAttachment(position: Int, obj: AttachmentModel) {
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.DELETE, Constant.URL_PROFILE + "/portfolio/" + attachmentArrayList!![position].id,
                Response.Listener { response ->
                    Timber.e(response)
                    hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response!!)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                recyclerView.removeViewAt(position)
                                attachmentArrayList!!.removeAt(position)
                                adapter!!.notifyItemRemoved(position)
                                adapter!!.notifyItemRangeRemoved(position, attachmentArrayList!!.size)
                                showToast("Portfolio Deleted", this@PortfolioActivity)
                            } else {
                                showToast("Something went Wrong", this@PortfolioActivity)
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
                                showToast(jsonObjectError.getString("message"), this@PortfolioActivity)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        showToast("Something Went Wrong", this@PortfolioActivity)
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
                // map1.put("X-Requested-With", "XMLHttpRequest");
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this@PortfolioActivity)
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
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
                uploadDataInPortfolioMediaApi(File(uri.path))
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                showToast("User cancelled image capture", this)
            } else {
                // failed to capture image
                showToast("Sorry! Failed to capture image", this)
            }
        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(applicationContext, imageStoragePath)

                // video successfully recorded
                // preview the recorded video
                val uri = Uri.parse("file://$imageStoragePath")
                uploadDataInPortfolioMediaApi(File(uri.path))
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                showToast("User cancelled video recording", this)
                // failed to record video
                showToast("Sorry! Failed to record video", this)
            }
        } else if (requestCode == GALLERY_PICKUP_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                imageStoragePath = CameraUtils.getImagePath(this@PortfolioActivity, data!!.data)
                Timber.tag("path").e(imageStoragePath)
                if (imageStoragePath != null) {
                    val mpl = MediaPlayer.create(this@PortfolioActivity, Uri.parse(imageStoragePath))
                    val si = mpl.duration
                    val duration = TimeUnit.MILLISECONDS.toSeconds(si.toLong())
                    val file = File(imageStoragePath!!)
                    val fileSize = file.length().toString().toInt().toLong()
                    when {
                        duration > MAX_VIDEO_DURATION -> {
                            CameraUtils.showLimitDialog(this@PortfolioActivity)
                            imageStoragePath = null
                        }
                        fileSize > MAX_VIDEO_SIZE -> {
                            showToast("Maximum video size exceeds(20 MB)", this@PortfolioActivity)
                            imageStoragePath = null
                        }
                        else -> {
                            Timber.tag("Duration: ").e(duration.toString() + "")
                            Timber.tag("Size: ").e(fileSize.toString() + "")
                            // uploadUrl = strVideoPath;
                            Timber.tag("VIDEO").e("video")
                            uploadDataInPortfolioMediaApi(file)
                        }
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                showToast("User cancelled video recording", this)
            } else {
                // failed to record video
                showToast("Sorry! Failed to record video", this)
            }
        } else if (requestCode == GALLERY_PICKUP_IMAGE_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    if (data!!.data != null) {
                        imageStoragePath = CameraUtils.getPath(this@PortfolioActivity, data.data)
                        val file = File(imageStoragePath!!)
                        uploadDataInPortfolioMediaApi(file)
                    }
                }
                RESULT_CANCELED -> {
                    // user cancelled recording
                    showToast("User cancelled Pickup Image", this)
                }
                else -> {
                    // failed to record video
                    showToast("Sorry! Failed to Pickup Image", this)
                }
            }
        }
    }

    companion object {
        const val MAX_VIDEO_DURATION: Long = 30
        const val MAX_VIDEO_SIZE = (20 * 1024 * 1024).toLong()
        private var imageStoragePath: String? = null

        // key to store image path in savedInstance state
        const val KEY_IMAGE_STORAGE_PATH = "image_path"

        // Activity request codes
        private const val CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100
        private const val CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200
        private const val GALLERY_PICKUP_VIDEO_REQUEST_CODE = 300
        private const val GALLERY_PICKUP_IMAGE_REQUEST_CODE = 400
    }
}