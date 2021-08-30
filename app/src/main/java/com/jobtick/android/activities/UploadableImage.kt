package com.jobtick.android.activities

import android.content.Intent

interface UploadableImage {
    fun showAttachmentImageBottomSheet(isImageCircle: Boolean)
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}