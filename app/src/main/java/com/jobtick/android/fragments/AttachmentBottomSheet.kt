package com.jobtick.android.fragments

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.PermissionRequest
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.activities.ActivityBase
import com.jobtick.android.utils.NewCameraUtil
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AttachmentBottomSheet : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.sheet_attachment, container, false)
        val cameraBtn = view.findViewById<LinearLayout>(R.id.lyt_btn_camera)
        val galleryBtn = view.findViewById<LinearLayout>(R.id.lyt_btn_image)
        val lytBtnVideo = view.findViewById<LinearLayout>(R.id.lyt_btn_video)
        val lyrBtnVideoCamera = view.findViewById<LinearLayout>(R.id.lyt_btn_video_camera)

        cameraBtn.setOnClickListener { view1: View? ->
            if (requireContext().checkSelfPermission(permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                val permissionlistener: PermissionListener = object : PermissionListener {

                    override fun onPermissionGranted() {
                        val cameraIntent = NewCameraUtil.getTakePictureIntent(requireContext())
                        if (cameraIntent == null) {
                            (requireActivity() as ActivityBase).showToast("can not write to your files to save picture.", requireContext())
                        }
                        try {
                            requireActivity().startActivityForResult(cameraIntent, CAMERA_REQUEST)
                            dismiss()
                        } catch (e: ActivityNotFoundException) {
                            (requireActivity() as ActivityBase).showToast("Can not find your camera.", requireContext())
                        }
                    }

                    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {

                    }
                }
                TedPermission.with(context)
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(permission.CAMERA)
                        .check()
            } else {
                val cameraIntent = NewCameraUtil.getTakePictureIntent(requireContext())
                if (cameraIntent == null) {
                    (requireActivity() as ActivityBase).showToast("can not write to your files to save picture.", requireContext())
                }
                try {
                    requireActivity().startActivityForResult(cameraIntent, CAMERA_REQUEST)
                    dismiss()
                } catch (e: ActivityNotFoundException) {
                    (requireActivity() as ActivityBase).showToast("Can not find your camera.", requireContext())
                }
            }
        }

        galleryBtn.setOnClickListener { v: View? ->
            if (checkPermissionREAD_EXTERNAL_STORAGE(requireContext())) {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                requireActivity().startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST)
                dismiss()
            }
        }

        lytBtnVideo.setOnClickListener { view12: View? ->
            galleryVideo
            dismiss()
        }

        lyrBtnVideoCamera.setOnClickListener { view13: View? ->
            recordVideo()
            dismiss()
        }
        return view
    }

    fun checkPermissionREAD_EXTERNAL_STORAGE(context: Context?): Boolean {
        val permissionlistener: PermissionListener = object : PermissionListener {

            override fun onPermissionGranted() {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                requireActivity().startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST)
                dismiss()

            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {

            }
        }
        val currentAPIVersion = Build.VERSION.SDK_INT
        return if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                            permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                TedPermission.with(context)
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(permission.READ_EXTERNAL_STORAGE)
                        .check()
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

    private val galleryVideo: Unit
        private get() {
            @SuppressLint("IntentReset") val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            intent.type = "video/*"
            startActivityForResult(intent, PICK_VIDEO)
        }

    private fun recordVideo() {
        var fileUri: Uri
        try {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            fileUri = outputMediaFileUri
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAX_VIDEO_DURATION)
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0)
            intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, MAX_VIDEO_SIZE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri) // set the image file
            // start the video capture Intent
            startActivityForResult(intent, VIDEO_CAPTURE)
        } catch (e: Exception) {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            fileUri = outputMediaFileUri1
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAX_VIDEO_DURATION)
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0)
            intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, MAX_VIDEO_SIZE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri) // set the image file

            // start the video capture Intent
            startActivityForResult(intent, VIDEO_CAPTURE)
        }
    }

    private val outputMediaFileUri: Uri
        private get() = Uri.fromFile(outputMediaFile)

    private val outputMediaFileUri1: Uri
        private get() = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".provider", outputMediaFile!!)

    companion object {
        private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123
        const val VIDEO_FORMAT = ".mp4"
        const val VIDEO_SIGN = "VID_"
        private const val VIDEO_CAPTURE = 101
        const val CAMERA_REQUEST = 1001
        const val GALLERY_REQUEST = 1002
        private const val MY_CAMERA_PERMISSION_CODE = 100
        const val MAX_VIDEO_DURATION: Long = 30
        const val MAX_VIDEO_SIZE = 20 * 1024 * 1024.toLong()
        const val MEDIA_DIRECTORY_NAME = "Jobtick"
        const val TIME_STAMP_FORMAT = "yyyyMMdd_HHmmss"
        const val MEDIA_TYPE_VIDEO = 2
        private const val PICK_VIDEO = 107
        private val outputMediaFile: File?
            private get() {
                val mediaStorageDir = File(
                        Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        MEDIA_DIRECTORY_NAME)
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        return null
                    }
                }
                val mediaFile: File
                mediaFile = if (TaskDetailFragment.MEDIA_TYPE_VIDEO == MEDIA_TYPE_VIDEO) {
                    File(mediaStorageDir.path + File.separator
                            + VIDEO_SIGN + timeStamp + VIDEO_FORMAT)
                } else {
                    return null
                }
                return mediaFile
            }

        private val timeStamp: String
            private get() = SimpleDateFormat(TIME_STAMP_FORMAT,
                    Locale.getDefault()).format(Date())
    }
}