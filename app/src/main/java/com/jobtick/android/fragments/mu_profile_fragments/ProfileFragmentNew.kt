package com.jobtick.android.fragments.mu_profile_fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.jobtick.android.R
import com.jobtick.android.activities.AbstractUploadableImageImpl
import com.jobtick.android.activities.ActivityBase
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.activities.UploadableImage
import com.jobtick.android.adapers.AttachmentAdapter
import com.jobtick.android.databinding.FragmentProfileNewBinding
import com.jobtick.android.fragments.AttachmentBottomSheet
import com.jobtick.android.fragments.ProfileFragment
import com.jobtick.android.models.AttachmentModel
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.*
import com.yalantis.ucrop.UCrop
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
import java.io.FileOutputStream

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragmentNew : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var activity: DashboardActivity
    private var _binding: FragmentProfileNewBinding? = null
    private val binding get() = _binding!!
//    private lateinit var profileNewViewModel: ProfileNewViewModel
    private var sessionManager: SessionManager? = null
    private var uploadableImage: UploadableImage? = null

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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(sessionManager!!.roleLocal == "poster") {
            SetToolbar(activity, "Profile", "Switch to Ticker", null, binding.header, view)

            if(sessionManager!!.userAccount!!.isVerifiedAccount == 1)
                binding.textVerificationStatus.text = "Verified Poster"
            else
                binding.isloginParent2.visibility = View.GONE
        }
        else {
            SetToolbar(activity, "Profile", "Switch to Poster", null, binding.header, view)
            try {
                if (sessionManager!!.userAccount!!.isVerifiedAccount == 1)
                    binding.textVerificationStatus.text = "Verified Ticker"
                else
                    binding.isloginParent2.visibility = View.GONE
            }catch (e: Exception) {
                Log.d("exceptioninprofile", e.toString())
            }
        }
        try {
            Glide.with(binding.imgAvatar).load(sessionManager!!.userAccount!!.avatar!!.url)
                .into(binding.imgAvatar)
        }catch (e: Exception) {
            binding.imgAvatar.setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.new_design_person))
        }

        binding.imgAvatar.setOnClickListener { uploadableImage!!.showAttachmentImageBottomSheet(true) }
        uploadableImage = object : AbstractUploadableImageImpl(requireActivity()) {
            override fun onImageReady(imageFile: File) {
                Log.d("onImageReady", "ss")
                Glide.with(binding.imgAvatar).load(Uri.fromFile(imageFile)).into(binding.imgAvatar)
            }
        }

        binding.imgAvatar.setOnClickListener { v: View? ->
            if (checkPermissionREAD_EXTERNAL_STORAGE(activity)) {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                    AttachmentBottomSheet.GALLERY_REQUEST
                )
            }
        }


        binding.imgAvatar.setOnClickListener { view1: View? ->
            if (checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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

        if(sessionManager!!.accessToken != null) {
            binding.fullName.text = sessionManager!!.userAccount!!.name
            binding.noligonParent.visibility = View.GONE
            binding.isloginParent.visibility = View.VISIBLE
            binding.isloginParent2.visibility = View.VISIBLE

        }
        else {
            binding.fullName.text = "Guest"
            binding.noligonParent.visibility = View.VISIBLE
            binding.isloginParent.visibility = View.GONE
            binding.isloginParent2.visibility = View.GONE
        }

        binding.header.txtAction.setOnClickListener {

                    if (binding.header.txtAction.text == "Switch to Ticker") {
                        binding.header.txtAction.text = "Switch to Poster"
                        binding.portfolioSkillsParent.visibility = View.VISIBLE
                        binding.line.visibility = View.VISIBLE
                    } else {
                        binding.header.txtAction.text = "Switch to Ticker"
                        binding.portfolioSkillsParent.visibility = View.GONE
                        binding.line.visibility = View.GONE
            }
        }

        binding.loginParent.setOnClickListener {
            activity.unauthorizedUser()
        }

        binding.payments.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_to_navigation_profile_payments)
        }
        Log.d("authkey", sessionManager!!.accessToken+"")
        binding.accountParent.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_to_navigation_profile_account)
        }

        binding.portfilioParent.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_to_navigation_profile_portfolio)
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

        binding.textPublicProfile.setOnClickListener {
            activity.showToast("به زودی انشااله ...", activity)
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
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(
                    Intent.createChooser(intent, "Select Picture"),
                    AttachmentBottomSheet.GALLERY_REQUEST
                )

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
//    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        Log.d("onactivity", "asd")
//        uploadableImage!!.onActivityResult(requestCode, resultCode, data)
//    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("imagepath11: ", resultCode.toString() + ", " + requestCode + ", " + data.toString())
        if (requestCode == AttachmentBottomSheet.GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
            val filePath = data!!.data
           // val onCropImage: OnCropImage = OnUCropImageImpl(activity)
            val bitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, filePath)
            if (filePath != null) {
                uploadProfileAvatar(filePath.toFile())
            }
            binding.imgAvatar.setImageBitmap(bitmap)
//            if (filePath != null) {
//                crop(filePath, false, activity)
//            }
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
            val bitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, imageUri)
            binding.imgAvatar.setImageBitmap(bitmap)
            uploadProfileAvatar(File(imagePath))
           // val cropImage: OnCropImage = OnUCropImageImpl(activity)
           // crop(imageUri, false, activity)
        }
    }
//     fun crop(uri: Uri, isCircle: Boolean, activity: Activity) {
//         val uriPathHelper = UriPathHelper(activity)
//        val imageCachePath = uriPathHelper.getCachePath(uri)
//        val cacheCopyUri = Uri.fromFile(File(imageCachePath))
//        val cacheCopyFile = File(imageCachePath)
//        val destinationUri = Uri.fromFile(File(activity.cacheDir, "crop_" + cacheCopyFile.name))
//        val options = UCrop.Options()
//        options.setCompressionFormat(Bitmap.CompressFormat.JPEG)
//        options.setCircleDimmedLayer(isCircle)
//        options.setActiveControlsWidgetColor(activity.getColor(R.color.colorPrimary))
//        options.setStatusBarColor(activity.getColor(R.color.backgroundLightGrey))
//        options.setToolbarColor(activity.getColor(R.color.backgroundLightGrey))
//        options.setDimmedLayerColor(activity.getColor(R.color.backgroundLightGrey))
//        options.setRootViewBackgroundColor(activity.getColor(R.color.backgroundLightGrey))
//        if (isCircle) {
//            options.setCropFrameColor(activity.getColor(R.color.transparent))
//            options.setCropGridColor(activity.getColor(R.color.transparent))
//            UCrop.of(cacheCopyUri, destinationUri)
//                .withOptions(options)
//                .start(activity)
//        } else {
//            options.setCropFrameColor(activity.getColor(R.color.N070))
//            options.setCropGridColor(activity.getColor(R.color.N050))
//            UCrop.of(cacheCopyUri, destinationUri)
//                .withOptions(options)
//                .start(activity, this)
//        }
//    }

//    private inner class UriPathHelper(val context: Context) {
//        private var contentUri: Uri? = null
//
//        @SuppressLint("NewApi")
//        fun getCachePath(uri: Uri): String? {
//            // check here to KITKAT or new version
//            val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
//            var selection: String? = null
//            var selectionArgs: Array<String>? = null
//            // DocumentProvider
//            if (isKitKat) {
//                // ExternalStorageProvider
//                if (isExternalStorageDocument(uri)) {
//                    val docId = DocumentsContract.getDocumentId(uri)
//                    val split = docId.split(":".toRegex()).toTypedArray()
//                    val type = split[0]
//                    val fullPath = getPathFromExtSD(split)
//                    return if (fullPath != "") {
//                        fullPath
//                    } else {
//                        null
//                    }
//                }
//
//
//                // DownloadsProvider
//                if (isDownloadsDocument(uri)) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        val id: String
//                        context.contentResolver.query(uri, arrayOf(MediaStore.MediaColumns.DISPLAY_NAME), null, null, null).use { cursor ->
//                            if (cursor != null && cursor.moveToFirst()) {
//                                val fileName = cursor.getString(0)
//                                val path = Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName
//                                if (!TextUtils.isEmpty(path)) {
//                                    return path
//                                }
//                            }
//                        }
//                        id = DocumentsContract.getDocumentId(uri)
//                        if (!TextUtils.isEmpty(id)) {
//                            if (id.startsWith("raw:")) {
//                                return id.replaceFirst("raw:".toRegex(), "")
//                            }
//                            val contentUriPrefixesToTry = arrayOf(
//                                "content://downloads/public_downloads",
//                                "content://downloads/my_downloads"
//                            )
//                            for (contentUriPrefix in contentUriPrefixesToTry) {
//                                return try {
//                                    val contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), java.lang.Long.valueOf(id))
//                                    getDataColumn(context, contentUri, null, null)
//                                } catch (e: NumberFormatException) {
//                                    //In Android 8 and Android P the id is not a number
//                                    uri.path!!.replaceFirst("^/document/raw:".toRegex(), "").replaceFirst("^raw:".toRegex(), "")
//                                }
//                            }
//                        }
//                    } else {
//                        val id = DocumentsContract.getDocumentId(uri)
//                        if (id.startsWith("raw:")) {
//                            return id.replaceFirst("raw:".toRegex(), "")
//                        }
//                        try {
//                            contentUri = ContentUris.withAppendedId(
//                                Uri.parse("content://downloads/public_downloads"), id.toLong())
//                        } catch (e: NumberFormatException) {
//                            e.printStackTrace()
//                        }
//                        if (contentUri != null) {
//                            return getDataColumn(context, contentUri, null, null)
//                        }
//                    }
//                }
//
//
//                // MediaProvider
//                if (isMediaDocument(uri)) {
//                    val docId = DocumentsContract.getDocumentId(uri)
//                    val split = docId.split(":".toRegex()).toTypedArray()
//                    val type = split[0]
//                    var contentUri: Uri? = null
//                    if ("image" == type) {
//                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//                    } else if ("video" == type) {
//                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
//                    } else if ("audio" == type) {
//                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//                    }
//                    selection = "_id=?"
//                    selectionArgs = arrayOf(split[1])
//                    return getDataColumn(context, contentUri, selection,
//                        selectionArgs)
//                }
//                if (isGoogleDriveUri(uri)) {
//                    return getDriveFilePath(uri)
//                }
//                if (isWhatsAppFile(uri)) {
//                    return getFilePathForWhatsApp(uri)
//                }
//                if ("content".equals(uri.scheme, ignoreCase = true)) {
//                    if (isGooglePhotosUri(uri)) {
//                        return uri.lastPathSegment
//                    }
//                    if (isGoogleDriveUri(uri)) {
//                        return getDriveFilePath(uri)
//                    }
//                    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//
//                        // return getFilePathFromURI(context,uri);
//                        copyFileToInternalStorage(uri, "userfiles")
//                        // return getRealPathFromURI(context,uri);
//                    } else {
//                        getDataColumn(context, uri, null, null)
//                    }
//                }
//                if ("file".equals(uri.scheme, ignoreCase = true)) {
//                    return uri.path
//                }
//            } else {
//                if (isWhatsAppFile(uri)) {
//                    return getFilePathForWhatsApp(uri)
//                }
//                if ("content".equals(uri.scheme, ignoreCase = true)) {
//                    val projection = arrayOf(
//                        MediaStore.Images.Media.DATA
//                    )
//                    var cursor: Cursor? = null
//                    try {
//                        cursor = context.contentResolver
//                            .query(uri, projection, selection, selectionArgs, null)
//                        val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//                        if (cursor.moveToFirst()) {
//                            return cursor.getString(column_index)
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//            }
//            return null
//        }
//
//        private fun fileExists(filePath: String): Boolean {
//            val file = File(filePath)
//            return file.exists()
//        }
//
//        private fun getPathFromExtSD(pathData: Array<String>): String {
//            val type = pathData[0]
//            val relativePath = "/" + pathData[1]
//            var fullPath = ""
//
//            // on my Sony devices (4.4.4 & 5.1.1), `type` is a dynamic string
//            // something like "71F8-2C0A", some kind of unique id per storage
//            // don't know any API that can get the root path of that storage based on its id.
//            //
//            // so no "primary" type, but let the check here for other devices
//            if ("primary".equals(type, ignoreCase = true)) {
//                fullPath = Environment.getExternalStorageDirectory().toString() + relativePath
//                if (fileExists(fullPath)) {
//                    return fullPath
//                }
//            }
//
//            // Environment.isExternalStorageRemovable() is `true` for external and internal storage
//            // so we cannot relay on it.
//            //
//            // instead, for each possible path, check if file exists
//            // we'll start with secondary storage as this could be our (physically) removable sd card
//            fullPath = System.getenv("SECONDARY_STORAGE") + relativePath
//            if (fileExists(fullPath)) {
//                return fullPath
//            }
//            fullPath = System.getenv("EXTERNAL_STORAGE") + relativePath
//            return if (fileExists(fullPath)) {
//                fullPath
//            } else fullPath
//        }
//
//        private fun getDriveFilePath(uri: Uri): String {
//            val returnCursor = context.contentResolver.query(uri, null, null, null, null)
//            /*
//             * Get the column indexes of the data in the Cursor,
//             *     * move to the first row in the Cursor, get the data,
//             *     * and display it.
//             * */
//            val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//            val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
//            returnCursor.moveToFirst()
//            val name = returnCursor.getString(nameIndex)
//            val size = java.lang.Long.toString(returnCursor.getLong(sizeIndex))
//            val file = File(context.cacheDir, name)
//            try {
//                val inputStream = context.contentResolver.openInputStream(uri)
//                val outputStream = FileOutputStream(file)
//                var read = 0
//                val maxBufferSize = 1 * 1024 * 1024
//                val bytesAvailable = inputStream!!.available()
//
//                //int bufferSize = 1024;
//                val bufferSize = Math.min(bytesAvailable, maxBufferSize)
//                val buffers = ByteArray(bufferSize)
//                while (inputStream.read(buffers).also { read = it } != -1) {
//                    outputStream.write(buffers, 0, read)
//                }
//                Timber.e("Size %s", file.length())
//                inputStream.close()
//                outputStream.close()
//                Timber.e("Path %s", file.path)
//                Timber.e("Size %s", file.length())
//            } catch (e: Exception) {
//                Timber.e(e)
//            }
//            return file.path
//        }
//
//        /***
//         * Used for Android Q+
//         * @param uri
//         * @param newDirName if you want to create a directory, you can set this variable
//         * @return
//         */
//        fun copyFileToInternalStorage(uri: Uri, newDirName: String): String {
//            val returnCursor = context.contentResolver.query(uri, arrayOf(
//                OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE
//            ), null, null, null)
//
//
//            /*
//             * Get the column indexes of the data in the Cursor,
//             *     * move to the first row in the Cursor, get the data,
//             *     * and display it.
//             * */
//            val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//            val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
//            returnCursor.moveToFirst()
//            val name = returnCursor.getString(nameIndex)
//            val size = java.lang.Long.toString(returnCursor.getLong(sizeIndex))
//            val output: File
//            output = if (newDirName != "") {
//                val dir = File(context.cacheDir.toString() + "/" + newDirName)
//                if (!dir.exists()) {
//                    dir.mkdir()
//                }
//                File(context.cacheDir.toString() + "/" + newDirName + "/" + name)
//            } else {
//                File(context.cacheDir.toString() + "/" + name)
//            }
//            try {
//                val inputStream = context.contentResolver.openInputStream(uri)
//                val outputStream = FileOutputStream(output)
//                var read = 0
//                val bufferSize = 1024
//                val buffers = ByteArray(bufferSize)
//                while (inputStream!!.read(buffers).also { read = it } != -1) {
//                    outputStream.write(buffers, 0, read)
//                }
//                inputStream.close()
//                outputStream.close()
//            } catch (e: Exception) {
//                Timber.e(e)
//            }
//            return output.path
//        }
//
//        private fun getFilePathForWhatsApp(uri: Uri): String {
//            return copyFileToInternalStorage(uri, "whatsapp")
//        }
//
//        private fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
//            var cursor: Cursor? = null
//            val column = "_data"
//            val projection = arrayOf(column)
//            try {
//                cursor = context.contentResolver.query(uri!!, projection,
//                    selection, selectionArgs, null)
//                if (cursor != null && cursor.moveToFirst()) {
//                    val index = cursor.getColumnIndexOrThrow(column)
//                    return cursor.getString(index)
//                }
//            } finally {
//                cursor?.close()
//            }
//            return null
//        }
//
//        private fun isExternalStorageDocument(uri: Uri): Boolean {
//            return "com.android.externalstorage.documents" == uri.authority
//        }
//
//        private fun isDownloadsDocument(uri: Uri): Boolean {
//            return "com.android.providers.downloads.documents" == uri.authority
//        }
//
//        private fun isMediaDocument(uri: Uri): Boolean {
//            return "com.android.providers.media.documents" == uri.authority
//        }
//
//        private fun isGooglePhotosUri(uri: Uri): Boolean {
//            return "com.google.android.apps.photos.content" == uri.authority
//        }
//
//        private fun isWhatsAppFile(uri: Uri): Boolean {
//            return "com.whatsapp.provider.media" == uri.authority
//        }
//
//        private fun isGoogleDriveUri(uri: Uri): Boolean {
//            return "com.google.android.apps.docs.storage" == uri.authority || "com.google.android.apps.docs.storage.legacy" == uri.authority
//        }
//
//    }

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
                            ImageUtil.displayImage(binding.imgAvatar, attachment.url, null)
                            if (ProfileFragment.onProfileupdatelistener != null) {
                                ProfileFragment.onProfileupdatelistener!!.updatedSuccesfully(attachment.url)
                            }
                            if (DashboardActivity.onProfileupdatelistenerSideMenu != null) {
                                DashboardActivity.onProfileupdatelistenerSideMenu!!.updatedSuccesfully(attachment.url)
                            }
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

}