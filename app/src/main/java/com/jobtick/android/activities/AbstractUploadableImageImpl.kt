package com.jobtick.android.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.jobtick.android.AppController
import com.jobtick.android.fragments.AttachmentBottomSheet
import com.jobtick.android.utils.NewCameraUtil
import com.jobtick.android.utils.OnCropImage
import com.jobtick.android.utils.OnUCropImageImpl
import com.yalantis.ucrop.UCrop
import java.io.File

abstract class AbstractUploadableImageImpl(private val activity: FragmentActivity) : UploadableImage {
    private val attachmentBottomSheet: AttachmentBottomSheet
    var isCircle = false
    override fun showAttachmentImageBottomSheet(isCircle: Boolean) {
        this.isCircle = isCircle
        attachmentBottomSheet.show(activity.supportFragmentManager, "")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            val file = File(resultUri!!.path)
            onImageReady(file)
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            if (cropError != null) (activity.applicationContext as AppController).mCrashlytics.recordException(cropError)
        }
        if (requestCode == AttachmentBottomSheet.GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
            val filePath = data!!.data
            val onCropImage: OnCropImage = OnUCropImageImpl(activity)
            onCropImage.crop(filePath, isCircle)
        }
        if (requestCode == AttachmentBottomSheet.CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            val imagePath = NewCameraUtil.getImagePath()
            if (imagePath == null) {
                (activity as ActivityBase).showToast("Can not find image.", activity)
                return
            }
            val imageUri = Uri.fromFile(File(imagePath))
            val cropImage: OnCropImage = OnUCropImageImpl(activity)
            cropImage.crop(imageUri, isCircle)
        }
    }

    abstract fun onImageReady(imageFile: File)

    init {
        attachmentBottomSheet = AttachmentBottomSheet()
    }
}