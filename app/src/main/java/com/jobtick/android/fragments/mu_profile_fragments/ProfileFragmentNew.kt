package com.jobtick.android.fragments.mu_profile_fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.Cursor.FIELD_TYPE_STRING
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.checkSelfPermission
//import androidx.core.content.PermissionChecker.checkSelfPermission

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import com.jobtick.android.R
import com.jobtick.android.activities.AbstractUploadableImageImpl
import com.jobtick.android.activities.ActivityBase
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.activities.UploadableImage
import com.jobtick.android.adapers.AttachmentAdapter
import com.jobtick.android.databinding.FragmentProfileNewBinding
import com.jobtick.android.fragments.AttachmentBottomSheet
import com.jobtick.android.material.ui.landing.OnboardingActivity
import com.jobtick.android.models.AttachmentModel
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.*
import com.jobtick.android.utils.CameraImagePick.CameraGalleryImagePick
import com.jobtick.android.utils.CameraImagePick.ImagePickedStatus
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

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragmentNew : Fragment(), PickiTCallbacks {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var activity: DashboardActivity
    private var _binding: FragmentProfileNewBinding? = null
    private val binding get() = _binding!!
    private var sessionManager: SessionManager? = null
    private var uploadableImage: UploadableImage? = null
    var pickiT: PickiT? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = (requireActivity() as DashboardActivity)
        sessionManager = SessionManager(context)
        pickiT = PickiT(requireContext(), this, requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(sessionManager!!.userAccount == null)
            sessionManager!!.userAccount = UserAccountModel()
        activity.toolbar!!.visibility = View.GONE

        if(sessionManager!!.roleLocal == "poster") {
            binding.portfolioSkillsParent.visibility = View.GONE
            binding.line.visibility = View.GONE
            binding.jobAlertParent.visibility = View.GONE
            binding.textPublicProfile.visibility = View.GONE
            binding.textVerificationStatus.visibility = View.GONE
            if(sessionManager!!.userAccount!!.isVerifiedAccount == 1)
                binding.textVerificationStatus.text = "Verified Poster"
            else
                binding.isloginParent2.visibility = View.GONE
        }
        else {
            try {
                binding.portfolioSkillsParent.visibility = View.VISIBLE
                binding.line.visibility = View.VISIBLE
                binding.jobAlertParent.visibility = View.VISIBLE
                binding.textPublicProfile.visibility = View.VISIBLE
                binding.textVerificationStatus.visibility = View.VISIBLE

                if (sessionManager!!.userAccount!!.isVerifiedAccount == 1)
                    binding.textVerificationStatus.text = "Verified Ticker"
                else
                    binding.isloginParent2.visibility = View.GONE
            }catch (e: Exception) {
            }
        }
        try {
          //  binding.avatarLoading.visibility = View.VISIBLE
            if (sessionManager!!.userAccount.avatar != null) {
                ImageUtil.displayImage(binding.imgAvatar, sessionManager!!.userAccount.avatar.thumbUrl, null)
            }

//            Glide.with(binding.imgAvatar).load(sessionManager!!.userAccount!!.avatar!!.thumbUrl).listener(
//                object : RequestListener<Drawable> {
//
//                    override fun onLoadFailed(
//                        e: GlideException?,
//                        model: Any?,
//                        target: Target<Drawable>?,
//                        isFirstResource: Boolean
//                    ): Boolean {
//                        binding.avatarLoading.visibility = View.GONE
//
//                        binding.imgAvatar.setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.new_design_person))
//                        return false
//                    }
//
//                    override fun onResourceReady(
//                        resource: Drawable?,
//                        model: Any?,
//                        target: Target<Drawable>?,
//                        dataSource: DataSource?,
//                        isFirstResource: Boolean
//                    ): Boolean {
//                        binding.avatarLoading.visibility = View.GONE
//
//                        return false
//                    }
//                })
//                .into(binding.imgAvatar)
        }catch (e: Exception) {
            binding.avatarLoading.visibility = View.GONE
            binding.imgAvatar.setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.new_design_person))
        }

        binding.imgAvatarParent.setOnClickListener {
            showDialog()
        }
        uploadableImage = object : AbstractUploadableImageImpl(requireActivity()) {
            override fun onImageReady(imageFile: File) {
                Glide.with(binding.imgAvatar).load(Uri.fromFile(imageFile)).into(binding.imgAvatar)
            }

            override fun onPdfReady(pdf: File, string: String, uri: Uri) {
                TODO("Not yet implemented")
            }
        }

        if(sessionManager!!.accessToken != null) {
            binding.imgAvatarParent.isEnabled = true
            binding.fullName.text = sessionManager!!.userAccount!!.name
            binding.noligonParent.visibility = View.GONE
            binding.isloginParent.visibility = View.VISIBLE
            binding.isloginParent2.visibility = View.VISIBLE

        }
        else {
            binding.imgAvatarParent.isEnabled = false
            binding.fullName.text = "Guest"
            binding.noligonParent.visibility = View.VISIBLE
            binding.isloginParent.visibility = View.GONE
            binding.isloginParent2.visibility = View.GONE
        }

        if(sessionManager!!.roleLocal == "ticker")
            binding.txtAction.text = "Switch to Poster"
        else
            binding.txtAction.text = "Switch to Ticker"

        binding.txtAction.setOnClickListener {

                    if (binding.txtAction.text == "Switch to Ticker") {
                        binding.txtAction.text = "Switch to Poster"
                        sessionManager!!.roleLocal = "ticker"
                        binding.portfolioSkillsParent.visibility = View.VISIBLE
                        binding.line.visibility = View.VISIBLE
                        binding.jobAlertParent.visibility = View.VISIBLE
                        binding.textVerificationStatus.visibility = View.VISIBLE
                        binding.textPublicProfile.visibility = View.VISIBLE

                    } else {
                        binding.txtAction.text = "Switch to Ticker"
                        sessionManager!!.roleLocal = "poster"
                        binding.portfolioSkillsParent.visibility = View.GONE
                        binding.line.visibility = View.GONE
                        binding.jobAlertParent.visibility = View.GONE
                        binding.textVerificationStatus.visibility = View.GONE
                        binding.textPublicProfile.visibility = View.GONE


                    }
          //  activity.resetBottomBar()
            activity.setBottomnav()

        }

        binding.joinJobtick.setOnClickListener {
            activity.unauthorizedUser()
        }

        binding.loginParent.setOnClickListener {
            val main = Intent(activity, OnboardingActivity::class.java)
            main.putExtra("signin", "sss")
            startActivity(main)
            //activity.unauthorizedUser()
        }

        binding.payments.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_to_navigation_profile_payments)
        }
        Log.d("authkey", sessionManager!!.accessToken+"")
        binding.accountParent.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_to_navigation_profile_account)
        }

        binding.portfilioParent.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_to_navigation_profile_portfolio_item)
        }

        binding.skillsParent.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_to_navigation_profile_skills)
        }
        binding.notificationParent.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_to_navigation_profile_notifications)
        }
        binding.helpSupportParent.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_to_navigation_profile_help_and_support)
        }

        binding.changePassword.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_to_navigation_profile_change_password)
        }

        binding.textPublicProfile.setOnClickListener {
                view.findNavController().navigate(R.id.action_navigation_profile_to_navigation_public_profile)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragmentNew().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun checkPermissionREAD_EXTERNAL_STORAGE(context: Context?): Boolean {
        val permissionlistener: PermissionListener = object : PermissionListener {

            override fun onPermissionGranted() {
//                val intent = Intent()
//                intent.type = "image/*"
//                intent.action = Intent.ACTION_GET_CONTENT
//                startActivityForResult(
//                    Intent.createChooser(intent, "Select Picture"),
//                    AttachmentBottomSheet.GALLERY_REQUEST
//                )

                val openGallary = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(Intent.createChooser(openGallary, "Open Gallary"), AttachmentBottomSheet.GALLERY_REQUEST)

            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {

            }
        }
        val currentAPIVersion = Build.VERSION.SDK_INT
        return if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity,
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("imagepath11: ", resultCode.toString() + ", " + requestCode + ", " + data.toString())
        if (requestCode == AttachmentBottomSheet.GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {

            val filePath = data!!.data
            //val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, filePath)
            val imagePath = getPath(data.data)
            //imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
            //  uploadImageWithAmount();
            val file = File(imagePath)
            uploadProfileAvatar(file)
        }
        if (requestCode == AttachmentBottomSheet.CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            val externalStorageVolumes = ContextCompat.getExternalFilesDirs(activity, null)
            val storageDir: File? = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

            val imagePath = NewCameraUtil.getImagePath()
            Log.d("imagepath3: ", imagePath)

            // val imagePath = storageDir?.absolutePath
            if (imagePath == null) {
                (activity as ActivityBase).showToast("Can not find image.", activity)
                return
            }
//            val imgUri = FileProvider.getUriForFile(
//                activity,
//                BuildConfig.APPLICATION_ID + ".provider",
//                File(imagePath)
//            )

            val imageUri = Uri.fromFile(File(imagePath))
           // val bitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, imageUri)
           // binding.imgAvatar.setImageBitmap(bitmap)
            uploadProfileAvatar(File(imagePath))
           // val cropImage: OnCropImage = OnUCropImageImpl(activity)
           // crop(imageUri, false, activity)
        }
    }


    private fun uploadProfileAvatar(pictureFile: File) {
        activity.showProgressDialog()
        val call: Call<String?>?
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile)
        val imageFile = MultipartBody.Part.createFormData("media", pictureFile.name, requestFile)
        call = ApiClient.getClient().uploadProfilePicture("XMLHttpRequest",
            sessionManager!!.tokenType + " " + sessionManager!!.accessToken,
            imageFile)
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                val res = response.body()
                println(res)
                if (response.code() == HttpStatus.HTTP_VALIDATION_ERROR) {
                    (activity as ActivityBase?)!!.showToast(response.message(), activity)
                    return
                }
                try {
                    if (response.code() == HttpStatus.NOT_FOUND) {
                        (requireActivity() as ActivityBase).showToast("not found", requireContext())
                        return
                    }
                    if (response.code() == HttpStatus.AUTH_FAILED) {
                        (activity as ActivityBase?)!!.unauthorizedUser()
                        return
                    }
                    if (response.code() == HttpStatus.SUCCESS) {
                        val jsonObject = JSONObject(res)
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
                            sessionManager!!.userAccount.avatar = attachment

                           ImageUtil.displayImage(binding.imgAvatar, attachment.thumbUrl, null)
                         //   binding.imgAvatar.setImageBitmap(BitmapFactory.decodeFile(pictureFile.path))
                      //      Glide.with(binding.imgAvatar).load(attachment.thumbUrl).into(binding.imgAvatar)
//                            if (ProfileFragment.onProfileupdatelistener != null) {
//                                ProfileFragment.onProfileupdatelistener!!.updatedSuccesfully(attachment.url)
//                            }
//                            if (DashboardActivity.onProfileupdatelistenerSideMenu != null) {
//                                DashboardActivity.onProfileupdatelistenerSideMenu!!.updatedSuccesfully(attachment.url)
//                            }
                        }


                    } else {
                        (activity as ActivityBase?)!!.showToast("Something went wrong", activity)
                    }
                } catch (e: JSONException) {
                    (activity as ActivityBase?)!!.showToast("Something went wrong", activity)
                    e.printStackTrace()
                }
                activity.hideProgressDialog()

            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                    activity.hideProgressDialog()
            }
        })
    }

    private fun showDialog() {
        val view: View = layoutInflater.inflate(R.layout.dialog_attachment, null)
        val infoDialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
        val window = infoDialog.window

        val wlp = window!!.attributes
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        wlp.gravity = Gravity.BOTTOM
        window.attributes = wlp
        infoDialog.show()
        window.findViewById<MaterialButton>(R.id.pdf).text = getString(R.string.camera)
        window.findViewById<MaterialTextView>(R.id.title).text = getString(R.string.upload_avatar)
        window.findViewById<MaterialButton>(R.id.pdf).icon = ContextCompat.getDrawable(activity, R.drawable.new_design_add_a_photo_outline)
        window.findViewById<MaterialButton>(R.id.cancel).setOnClickListener {
            infoDialog.dismiss()
        }
        window.findViewById<MaterialButton>(R.id.gallery).setOnClickListener {
            infoDialog.dismiss()
            if (checkPermissionREAD_EXTERNAL_STORAGE(requireContext())) {


                val openGallary = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(Intent.createChooser(openGallary, "Open Gallary"), AttachmentBottomSheet.GALLERY_REQUEST)
            }
        }

        window.findViewById<MaterialButton>(R.id.pdf).setOnClickListener { view1: View? ->
            infoDialog.dismiss()
            if (checkSelfPermission(activity,Manifest.permission.CAMERA) != PERMISSION_GRANTED) {
                val permissionlistener: PermissionListener = object : PermissionListener {

                    override fun onPermissionGranted() {
                        val cameraIntent = NewCameraUtil.getTakePictureIntent(activity)
                        if (cameraIntent == null) {
                            activity.showToast("can not write to your files to save picture.", activity)
                        }
                        try {
                            startActivityForResult(cameraIntent,
                                AttachmentBottomSheet.CAMERA_REQUEST
                            )
                        } catch (e: ActivityNotFoundException) {
                            activity.showToast("Can not find your camera.", activity)
                        }
                    }

                    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {

                    }
                }
                TedPermission.with(activity)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(Manifest.permission.CAMERA)
                    .check()
            } else {
                val cameraIntent = NewCameraUtil.getTakePictureIntent(activity)
                if (cameraIntent == null) {
                    activity.showToast("can not write to your files to save picture.", activity)
                }
                try {
                    startActivityForResult(cameraIntent,
                        AttachmentBottomSheet.CAMERA_REQUEST
                    )
                } catch (e: ActivityNotFoundException) {
                    activity.showToast("Can not find your camera.", activity)
                }
            }
        }

    }

    override fun PickiTonUriReturned() {
        TODO("Not yet implemented")
    }

    override fun PickiTonStartListener() {
        TODO("Not yet implemented")
    }

    override fun PickiTonProgressUpdate(progress: Int) {
        TODO("Not yet implemented")
    }

    override fun PickiTonCompleteListener(
        path: String?,
        wasDriveFile: Boolean,
        wasUnknownProvider: Boolean,
        wasSuccessful: Boolean,
        Reason: String?
    ) {
        TODO("Not yet implemented")
    }

    override fun PickiTonMultipleCompleteListener(
        paths: ArrayList<String>?,
        wasSuccessful: Boolean,
        Reason: String?
    ) {
        TODO("Not yet implemented")
    }

    @SuppressLint("Range", "SuspiciousIndentation")
    fun getPath(uri: Uri?): String? {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = requireActivity().contentResolver.query(uri!!, filePathColumn, null, null, null)!!
        var picturePath: String? = null
//        if (cursor == null) {
//            picturePath = uri.path
//        } else {
            cursor.moveToFirst()
            val columnIndex: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            Log.d("aaaasdasdasd", columnIndex.toString() + "," + cursor.toString())

            if (cursor.getType(columnIndex) == FIELD_TYPE_STRING)
                picturePath = cursor.getString(columnIndex)
            else
                picturePath = uri.path

        cursor.close()
        Timber.e(picturePath)
        return picturePath
    }

}