package com.jobtick.android.activities

import android.R.attr
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.jobtick.android.AppController
import com.jobtick.android.fragments.AttachmentBottomSheet
import com.jobtick.android.utils.NewCameraUtil
import com.jobtick.android.utils.OnCropImage
import com.jobtick.android.utils.OnUCropImageImpl
import com.yalantis.ucrop.UCrop
import java.io.*


abstract class AbstractUploadableImageImpl(private val activity: FragmentActivity) : UploadableImage {
    private val attachmentBottomSheet: AttachmentBottomSheet
    var isCircle = false
    private val IMAGE_DIRECTORY = "/demonuts_upload_gallery"
    private val BUFFER_SIZE = 1024 * 2


    override fun showAttachmentImageBottomSheet(isCircle: Boolean) {
        this.isCircle = isCircle
        attachmentBottomSheet.show(activity.supportFragmentManager, "")
    }

    @SuppressLint("Range", "Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("imagepath10: ", resultCode.toString() + ", " + requestCode + ", " + data.toString())
        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            val file = File(resultUri!!.path)
            Log.d("imagepath4: ", file.absolutePath)

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
            val cropImage: OnCropImage = OnUCropImageImpl(activity)
            cropImage.crop(imageUri, isCircle)
        }

        if(requestCode == AttachmentBottomSheet.PDF_REQUEST && resultCode == Activity.RESULT_OK) {
//            val uri: Uri = data?.data!!
//            val uriString: String? = uri.path
//            var pdfName: String? = null
//            val inputStream = activity.contentResolver.openInputStream(uri)
//            if (inputStream != null) {
//                val file = uriString?.let { File(it) }
//                if (file != null) {
//                    onPdfReady(file, file.name, uri)
//                }
//            }

            val uri = data!!.data
            val uriString = uri.toString()
            val myFile = File(uriString)

            val path = getFilePathFromURI(activity, uri)
            val file = File(path)
            if (uri != null) {
                onPdfReady(file, file.name, uri)
            }
            Log.d("ioooo", path!!)

        }
    }

    abstract fun onImageReady(imageFile: File)
    abstract fun onPdfReady(pdf: File, string: String, uri: Uri)

    init {
        attachmentBottomSheet = AttachmentBottomSheet()
    }

    open fun getFilePathFromURI(context: Context, contentUri: Uri?): String? {
        //copy file and send new file path
        val fileName = getFileName(contentUri)
        val wallpaperDirectory: File = File(
            Environment.getExternalStorageDirectory().toString() + IMAGE_DIRECTORY
        )
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }
        if (!TextUtils.isEmpty(fileName)) {
            val copyFile = File(wallpaperDirectory.toString() + File.separator + fileName)
            // create folder if not exists
            copy(context, contentUri, copyFile)
            return copyFile.absolutePath
        }
        return null
    }

    open fun getFileName(uri: Uri?): String? {
        if (uri == null) return null
        var fileName: String? = null
        val path = uri.path
        val cut = path!!.lastIndexOf('/')
        if (cut != -1) {
            fileName = path.substring(cut + 1)
        }
        return fileName
    }

    open fun copy(context: Context, srcUri: Uri?, dstFile: File?) {
        try {
            val inputStream: InputStream = srcUri?.let {
                context.getContentResolver().openInputStream(
                    it
                )
            }
                ?: return
            val outputStream: OutputStream = FileOutputStream(dstFile)
            copystream(inputStream, outputStream)
            inputStream.close()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(Exception::class, IOException::class)
    open fun copystream(input: InputStream?, output: OutputStream?): Int {
        val buffer = ByteArray(BUFFER_SIZE)
        val `in` = BufferedInputStream(input, BUFFER_SIZE)
        val out = BufferedOutputStream(output, BUFFER_SIZE)
        var count = 0
        var n = 0
        try {
            while (`in`.read(buffer, 0, BUFFER_SIZE).also { n = it } != -1) {
                out.write(buffer, 0, n)
                count += n
            }
            out.flush()
        } finally {
            try {
                out.close()
            } catch (e: IOException) {
                Log.e(e.message, java.lang.String.valueOf(e))
            }
            try {
                `in`.close()
            } catch (e: IOException) {
                Log.e(e.message, java.lang.String.valueOf(e))
            }
        }
        return count
    }
}