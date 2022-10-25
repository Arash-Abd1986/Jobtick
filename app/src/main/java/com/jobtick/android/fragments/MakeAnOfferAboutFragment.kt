package com.jobtick.android.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.hardware.camera2.CameraCharacteristics
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.jobtick.android.R
import com.jobtick.android.activities.ActivityBase
import com.jobtick.android.activities.MakeAnOfferActivity
import com.jobtick.android.activities.TaskDetailsActivity
import com.jobtick.android.activities.VideoPlayerActivity
import com.jobtick.android.adapers.AttachmentAdapter
import com.jobtick.android.models.AttachmentModel
import com.jobtick.android.models.MakeAnOfferModel
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.*
import com.jobtick.android.widget.ExtendedCommentText
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.mikhaellopez.circularimageview.CircularImageView
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.File
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class MakeAnOfferAboutFragment : Fragment(), View.OnClickListener {
    private lateinit var edtDescription: ExtendedCommentText
    private lateinit var description: TextInputLayout
    private lateinit var checkboxSaveAsTemplate: CheckBox
    private lateinit var viewSelection: LinearLayout
    private lateinit var saveQuickOfferTxt: MaterialButton
    private lateinit var quickOfferDesc: TextView
    private lateinit var lytBtnMakeALiveVideo: LinearLayout
    private lateinit var lytRecord2: LinearLayout
    private lateinit var lytBtnContinue: MaterialButton
    private lateinit var btnTxtOffer: MaterialButton
    private lateinit var gxtGuideLineVideo: TextView
    private lateinit var cardLiveVideo: CardView
    private lateinit var ivBack: ImageView
    private lateinit var imgThumbnail: ImageView
    private lateinit var LytVideoPlay: RelativeLayout
    private lateinit var viewOffer: RelativeLayout
    private lateinit var llPlayVideo: LinearLayout
    private lateinit var bottomSheet: FrameLayout
    private lateinit var llJobDetails: LinearLayout
    private lateinit var llCancelVideo: LinearLayout
    private lateinit var recordVideo: MaterialButton
    private var makeAnOfferModel: MakeAnOfferModel? = null
    private var makeAnOfferActivity: MakeAnOfferActivity? = null
    private var aboutCallbackFunction: AboutCallbackFunction? = null
    private var quickOffer: String? = null
    private var sessionManager: SessionManager? = null
    private var mBottomSheetDialog: BottomSheetDialog? = null
    private var mBehavior: BottomSheetBehavior<*>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_make_an_offer_about, container, false)
        return view
    }

    private fun setIds() {

        edtDescription = requireView().findViewById(R.id.edt_description)
        checkboxSaveAsTemplate = requireView().findViewById(R.id.checkbox_save_as_template)
        saveQuickOfferTxt = requireView().findViewById(R.id.saveQuickOfferTxt)
        viewSelection = requireView().findViewById(R.id.viewSelection)
        quickOfferDesc = requireView().findViewById(R.id.quickOfferDesc)
        lytBtnMakeALiveVideo = requireView().findViewById(R.id.lyt_btn_make_a_live_video)
        lytRecord2 = requireView().findViewById(R.id.lytRecord2)
        lytBtnContinue = requireView().findViewById(R.id.lyt_btn_continue)
        btnTxtOffer = requireView().findViewById(R.id.btnTxtOffer)
        gxtGuideLineVideo = requireView().findViewById(R.id.guideline_video)
        cardLiveVideo = requireView().findViewById(R.id.card_live_video)
        ivBack = requireView().findViewById(R.id.ivBack)
        imgThumbnail = requireView().findViewById(R.id.imgThumbnail)
        LytVideoPlay = requireView().findViewById(R.id.LytVideoPlay)
        viewOffer = requireView().findViewById(R.id.viewOffer)
        llPlayVideo = requireView().findViewById(R.id.llPlayVideo)
        bottomSheet = requireView().findViewById(R.id.bottom_sheet)
        llJobDetails = requireView().findViewById(R.id.llJobDetails)
        llCancelVideo = requireView().findViewById(R.id.llCancelVideo)
        recordVideo = requireView().findViewById(R.id.recordVideo)
        description = requireView().findViewById(R.id.description)
    }

    private fun setQuickOffer(quickOffer: String?, currentText: String) {
        if (quickOffer != "") {
            if ((currentText == quickOffer)) {
                saveQuickOfferTxt.isEnabled = false
            } else if (currentText.length < 25) {
                saveQuickOfferTxt.isEnabled = true
                saveQuickOfferTxt.setText(R.string.use_quick_offer)
                loadQuickOffer()
                saveQuickOfferTxt.setOnClickListener {
                    description.editText!!.setText(quickOffer)
                }
            } else {
                saveQuickOfferTxt.setOnClickListener { saveQuickOffer(currentText) }
                saveQuickOfferTxt.isEnabled = true
                saveQuickOfferTxt.setText(R.string.update_quick_offer)
            }
        } else {
            saveQuickOfferTxt.setText(R.string.save_as_a_Quick_offer)
            saveQuickOfferTxt.setOnClickListener { saveQuickOffer(currentText) }
            if (currentText.length < 25) {
                saveQuickOfferTxt.isEnabled = false
                saveQuickOfferTxt.alpha = 0.5f
            } else {
                saveQuickOfferTxt.isEnabled = true
                saveQuickOfferTxt.alpha = 1f
            }
        }
    }

    private fun saveQuickOffer(currentQuickOffer: String) {
        (requireActivity() as ActivityBase).showSuccessToast("Quick offer saved", context)
        quickOfferDesc.text = String.format(Locale.ENGLISH, "%s...", currentQuickOffer.trim { it <= ' ' }.substring(0, Math.min(currentQuickOffer.trim { it <= ' ' }.length - 1, 19)))
        sessionManager!!.setQuickOffer(currentQuickOffer, sessionManager!!.userAccount.id)
    }

    private fun loadQuickOffer() {
        val quickOffer = sessionManager!!.getQuickOffer(sessionManager!!.userAccount.id)
        quickOfferDesc.text = String.format(Locale.ENGLISH, "%s...", quickOffer.trim { it <= ' ' }.substring(0, Math.min(quickOffer.trim { it <= ' ' }.length - 1, 19)))
    }

    private fun setLayoout() {
        // cardLiveVideo.setVisibility(View.GONE);
        // cardLiveVideo.setVisibility(View.VISIBLE);
        checkboxSaveAsTemplate.isChecked = makeAnOfferModel!!.isCheckbok
        if (makeAnOfferModel!!.message != null) {
            description.editText!!.setText(makeAnOfferModel!!.message)
        }
    }

    private fun showJobDetailDialog() {
        if (mBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        val view = layoutInflater.inflate(R.layout.sheet_job_details, null)
        val tvJobTitle = view.findViewById<TextView>(R.id.tvJobTitle)
        val tvPosterName = view.findViewById<TextView>(R.id.tvPosterName)
        val tvLocation = view.findViewById<TextView>(R.id.tvLocation)
        val tvDate = view.findViewById<TextView>(R.id.tvDate)
        val tvDesc = view.findViewById<TextView>(R.id.tvDesc)
        tvJobTitle.text = TaskDetailsActivity.taskModel!!.title
        tvPosterName.text = TaskDetailsActivity.taskModel!!.poster.name
        if (TaskDetailsActivity.taskModel!!.location != null) tvLocation.text = TaskDetailsActivity.taskModel!!.location else tvLocation.setText(R.string.remotely)
        tvDesc.text = TaskDetailsActivity.taskModel!!.description

        //
        tvDate.text = Tools.getDayMonthDateTimeFormat2(TaskDetailsActivity.taskModel!!.dueDate)
        val imgAvtarPoster = view.findViewById<CircularImageView>(R.id.ivAvatar)
        if (TaskDetailsActivity.taskModel!!.poster.avatar != null && TaskDetailsActivity.taskModel!!.poster.avatar.thumbUrl != null) {
            ImageUtil.displayImage(imgAvtarPoster, TaskDetailsActivity.taskModel!!.poster.avatar.thumbUrl, null)
        } else {
            //TODO DUMMY IMAGE
        }
        /*  mBottomSheetDialog = BottomSheetDialog(requireActivity())
          mBottomSheetDialog!!.setContentView(view)
          mBottomSheetDialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)*/


        // set background transparent
        (view.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))
        /* mBottomSheetDialog!!.show()
         mBottomSheetDialog!!.setOnDismissListener { mBottomSheetDialog = null }*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setIds()
//        mBehavior = BottomSheetBehavior.from(bottomSheet)
        sessionManager = SessionManager(context)
        quickOffer = sessionManager!!.getQuickOffer(sessionManager!!.userAccount.id)
        setQuickOffer(quickOffer, "")
        description.editText!!.doAfterTextChanged {
            setQuickOffer(quickOffer, it.toString())
            it?.let {
                lytBtnContinue.isEnabled = it.length > 25
            }
        }
        LytVideoPlay.visibility = View.GONE
        makeAnOfferActivity = requireActivity() as MakeAnOfferActivity
        if (makeAnOfferActivity != null) {
            sessionManager = SessionManager(makeAnOfferActivity)
        }
        makeAnOfferModel = MakeAnOfferModel()
        if (arguments != null && requireArguments().getParcelable<Parcelable?>(ConstantKey.MAKE_AN_OFFER_MODEL) != null) {
            makeAnOfferModel = requireArguments().getParcelable(ConstantKey.MAKE_AN_OFFER_MODEL)
        }
        if (makeAnOfferModel != null) {
            setLayoout()
        }
        // toolbar.setNavigationOnClickListener(MakeAnOfferAboutFragment.this);
        checkboxSaveAsTemplate.setOnCheckedChangeListener { _, _ -> /*  if(isChecked){
                    cardLiveVideo.setVisibility(View.GONE);
                }else{
                    cardLiveVideo.setVisibility(View.VISIBLE);
                }*/
        }
        ivBack.setOnClickListener { makeAnOfferActivity!!.onBackPressed() }
        recordVideo.setOnClickListener {
            if (!CameraUtils.isDeviceSupportCamera(makeAnOfferActivity)) {
                (requireActivity() as ActivityBase).showToast("Sorry! Your device doesn't support camera", requireContext())
                // will close the app if the device doesn't have camera
                //finish();
                return@setOnClickListener
            }
            if (CameraUtils.checkPermissions(makeAnOfferActivity)) {
                captureVideo()
            } else {
                requestCameraPermission(ConstantKey.MEDIA_TYPE_VIDEO)
            }
        }
        btnTxtOffer.setOnClickListener {
            viewOffer.visibility = View.VISIBLE
            viewSelection.visibility = View.GONE
        }
        lytBtnMakeALiveVideo.setOnClickListener {
            // Checking availability of the camera
            if (!CameraUtils.isDeviceSupportCamera(makeAnOfferActivity)) {
                (requireActivity() as ActivityBase).showToast("Sorry! Your device doesn't support camera", requireContext())
            }
            if (CameraUtils.checkPermissions(makeAnOfferActivity)) {
                captureVideo()
            } else {
                requestCameraPermission(ConstantKey.MEDIA_TYPE_VIDEO)
            }
        }
        lytRecord2.setOnClickListener {
            // Checking availability of the camera
            if (!CameraUtils.isDeviceSupportCamera(makeAnOfferActivity)) {
                (requireActivity() as ActivityBase).showToast("Sorry! Your device doesn't support camera", requireContext())
            }
            if (CameraUtils.checkPermissions(makeAnOfferActivity)) {
                captureVideo()
            } else {
                requestCameraPermission(ConstantKey.MEDIA_TYPE_VIDEO)
            }
        }
        lytBtnContinue.setOnClickListener {
            when (validation()) {
                1 -> edtDescription.setError("Please enter your description or record a video")
                2 -> (requireActivity() as ActivityBase).showToast("Please enter only video or text", requireContext())
                0 -> if (aboutCallbackFunction != null) {
                    makeAnOfferModel!!.message = description.editText!!.text.toString().trim { it <= ' ' }
                    makeAnOfferModel!!.isCheckbok = checkboxSaveAsTemplate.isChecked
                    aboutCallbackFunction!!.continueButtonAbout(makeAnOfferModel)
                }
            }
        }
        llJobDetails.setOnClickListener {
            // showJobDetailDialog()
        }
        llCancelVideo.setOnClickListener {
            lytBtnMakeALiveVideo.visibility = View.VISIBLE
            LytVideoPlay.visibility = View.GONE
            description.editText!!.setText("")
            imageStoragePath = ""
        }
        gxtGuideLineVideo.setOnClickListener {
            val intent = Intent(requireActivity(), VideoPlayerActivity::class.java)
            intent.putExtra(ConstantKey.VIDEO_PATH, Constant.URL_VIDEO_GUIDELINE)
            requireActivity().startActivity(intent)
        }
    }

    private fun validation(): Int {
        if (description.editText!!.text.length < 25 && makeAnOfferModel!!.attachment == null) {
            return 1
        } else if (!TextUtils.isEmpty(description.editText!!.text.trim { it <= ' ' }) && makeAnOfferModel!!.attachment != null) {
            return 2
        }
        return 0
    }

    /**
     * Requesting permissions using Dexter library
     */
    private fun requestCameraPermission(type: Int) {
        Dexter.withContext(makeAnOfferActivity)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            if (type == ConstantKey.MEDIA_TYPE_IMAGE) {
                                // capture picture
                                //captureImage();
                            } else {
                                captureVideo()
                            }
                        } else if (report.isAnyPermissionPermanentlyDenied) {
                            makeAnOfferActivity!!.showPermissionsAlert()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }
                }).check()
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
        val fileUri = CameraUtils.getOutputMediaFileUri(makeAnOfferActivity, file)
        // set video quality
        // intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAX_VIDEO_DURATION)
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0)
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, MAX_VIDEO_SIZE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            intent.putExtra("android.intent.extras.CAMERA_FACING", CameraCharacteristics.LENS_FACING_FRONT)
            intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true) // Tested on API 24 Android version 7.0(Samsung S6)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra("android.intent.extras.CAMERA_FACING", CameraCharacteristics.LENS_FACING_FRONT) // Tested on API 27 Android version 8.0(Nexus 6P)
            intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
        } else {
            intent.putExtra("android.intent.extras.CAMERA_FACING", 1)
        }
        // Tested API 21 Android version 5.0.1(Samsung S4)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri) // set the image file
        // start the video capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE)
    }

    /**
     * Activity result method will be called after closing the camera
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(makeAnOfferActivity, imageStoragePath)
                uploadDataInTempApi(File(Uri.parse("file://" + imageStoragePath).path))
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // user cancelled recording
                (requireActivity() as ActivityBase).showToast(
                        "User cancelled video recording", requireContext())
            } else {
                // failed to record video
                (requireActivity() as ActivityBase).showToast(
                        "Sorry! Failed to record video", requireContext())
            }
        }
    }

    private fun uploadDataInTempApi(pictureFile: File) {
        makeAnOfferActivity!!.showProgressDialog()
        val call: Call<String?>?

        //    File file = new File(imagePath);
        //    Log.e("AttachmentApi: ", file.getName());
        //   Log.e("uploadProfile++:11 ", imagePath);
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile)
        // MultipartBody.Part is used to send also the actual file name
        val imageFile = MultipartBody.Part.createFormData("media", pictureFile.name, requestFile)
        call = ApiClient().buildAndGetClientForVideoUpload().getTaskTempAttachmentMediaData(
                "XMLHttpRequest", sessionManager!!.tokenType + " " + sessionManager!!.accessToken, imageFile)
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                makeAnOfferActivity!!.hideProgressDialog()
                Timber.tag("Response").e(response.toString())
                if (response.code() == 422) {
                    makeAnOfferActivity!!.showToast(response.message(), makeAnOfferActivity)
                    return
                }
                try {
                    val strResponse = response.body()
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
                        makeAnOfferModel!!.attachment = attachment
                    }

                    // aboutCallbackFunction.continueButtonAbout(makeAnOfferModel);
                    if (aboutCallbackFunction != null) {
                        makeAnOfferModel!!.message = description.editText!!.text.toString().trim { it <= ' ' }
                        makeAnOfferModel!!.isCheckbok = checkboxSaveAsTemplate.isChecked
                        aboutCallbackFunction!!.continueButtonAbout(makeAnOfferModel)
                    }
                    lytBtnMakeALiveVideo.visibility = View.GONE
                    LytVideoPlay.visibility = View.VISIBLE
                    ImageUtil.displayImage(imgThumbnail, makeAnOfferModel!!.attachment.modalUrl, null)
                    //ImageUtil.displayImage(imgThumbnail, "https://images.wallpaperscraft.com/image/road_asphalt_marking_130996_1280x720.jpg", null);
                    llPlayVideo.setOnClickListener {
                        val intent: Intent = Intent(requireActivity(), VideoPlayerActivity::class.java)
                        intent.putExtra(ConstantKey.VIDEO_PATH, "" + makeAnOfferModel!!.attachment.url)
                        requireActivity().startActivity(intent)
                    }

                    //https://images.wallpaperscraft.com/image/road_asphalt_marking_130996_1280x720.jpg

                    //  adapter.notifyItemRangeInserted(0,attachmentArrayList.size());
                } catch (e: Exception) {
                    makeAnOfferActivity!!.showToast("Something went wrong", makeAnOfferActivity)
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                try {
                    makeAnOfferActivity!!.hideProgressDialog()
                    Timber.tag("Response").e(call.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    override fun onClick(v: View) {
        if (aboutCallbackFunction != null) {
            aboutCallbackFunction!!.backButtonAbout()
        }
    }

    interface AboutCallbackFunction {
        fun backButtonAbout()
        fun continueButtonAbout(makeAnOfferModel: MakeAnOfferModel?)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        // get the file url
        if (savedInstanceState != null) {
            imageStoragePath = savedInstanceState.getString(KEY_IMAGE_STORAGE_PATH)
        }
    }

    companion object {
        val MAX_VIDEO_DURATION: Long = 30
        val MAX_VIDEO_SIZE = (80 * 1024 * 1024).toLong()

        // key to store image path in savedInstance state
        private val KEY_IMAGE_STORAGE_PATH = "image_path"
        private var imageStoragePath: String? = null
        private val CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200
        fun newInstance(makeAnOfferModel: MakeAnOfferModel?, aboutCallbackFunction: AboutCallbackFunction?): MakeAnOfferAboutFragment {
            val args = Bundle()
            args.putParcelable(ConstantKey.MAKE_AN_OFFER_MODEL, makeAnOfferModel)
            val fragment = MakeAnOfferAboutFragment()
            fragment.aboutCallbackFunction = aboutCallbackFunction
            fragment.arguments = args
            return fragment
        }
    }
}