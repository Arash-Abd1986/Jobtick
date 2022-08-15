package com.jobtick.android.material.ui.postajob

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.jobtick.android.R
import com.jobtick.android.activities.ActivityBase
import com.jobtick.android.adapers.AttachmentAdapter
import com.jobtick.android.models.AttachmentModelV2
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.*
import com.jobtick.android.viewmodel.PostAJobViewModel
import com.jobtick.android.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import timber.log.Timber
import java.io.File

private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123

class PostAJobAttachmentFragment : Fragment(), MediaAdapter.OnItemClickListener, MediaAdapter.OnShowOptions {

    private lateinit var activity: PostAJobActivity
    private lateinit var attachmentArrayList: ArrayList<AttachmentModelV2>

    private lateinit var next: MaterialButton
    private lateinit var linOptions: LinearLayout
    private lateinit var sessionManagerA: SessionManager
    private lateinit var viewModel: PostAJobViewModel
    private lateinit var rlAmount: RecyclerView
    private lateinit var mediaAdapter: MediaAdapter
    private lateinit var option1Icon: AppCompatImageView
    private lateinit var option1Txt: AppCompatTextView
    private lateinit var option2Icon: AppCompatImageView
    private lateinit var option2Txt: AppCompatTextView
    private var imagePath: String? = null
    private var filePath: Uri? = null
    private var bitmap: Bitmap? = null
    private var title: String? = null
    private var slug: String? = null
    private var option: Option = Option.HIDE
    private var retryItem: AttachmentModelV2? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_a_job_attachment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVars()
        initVM()
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun initVars() {
        sessionManagerA = SessionManager(requireContext())
        attachmentArrayList = ArrayList()
        attachmentArrayList.add(AttachmentModelV2(type = AttachmentAdapter.VIEW_TYPE_ADD))
        activity = (requireActivity() as PostAJobActivity)
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        mediaAdapter = MediaAdapter(attachmentArrayList, context = requireContext(), width)
        mediaAdapter.setOnItemClickListener(this)
        mediaAdapter.showOptions = this
        next = requireView().findViewById(R.id.btn_next)
        linOptions = requireView().findViewById(R.id.linOptions)
        rlAmount = requireView().findViewById(R.id.rl_medias)
        option1Icon = requireView().findViewById(R.id.option1_icon)
        option1Txt = requireView().findViewById(R.id.option1_txt)
        option2Icon = requireView().findViewById(R.id.option2_icon)
        option2Txt = requireView().findViewById(R.id.option2_txt)
        rlAmount.adapter = mediaAdapter
        rlAmount.layoutManager = GridLayoutManager(requireContext(), 3)
        rlAmount.addItemDecoration(SpacesItemDecorationV2((8).dpToPx()))
        title = ConstantKey.CREATE_A_JOB
        next.setOnClickListener {
         activity.navController.popBackStack()
        }
        option1Icon.setOnClickListener {
            when (option) {
                Option.SELECT_ALL -> {
                    attachmentArrayList.forEach { if (it.id > 0) it.isChecked = true }
                    mediaAdapter.notifyDataSetChanged()
                }
                Option.RETRY -> {
                    retryItem?.let { retryItem ->
                        attachmentArrayList.remove(retryItem)
                        mediaAdapter.notifyDataSetChanged()
                    }

                }
            }
        }
        option2Icon.setOnClickListener {
            when (option) {
                Option.SELECT_ALL -> {
                    attachmentArrayList = attachmentArrayList.filter { !it.isChecked } as ArrayList<AttachmentModelV2>
                    mediaAdapter.notifyDataSetChanged()
                }
                Option.RETRY -> {
                    retryItem?.let { retryItem ->
                        attachmentArrayList.forEach { if (it == retryItem) it.id = -1 }
                        uploadDataInTempApi(retryItem.file!!)
                        mediaAdapter.notifyDataSetChanged()
                    }

                }
            }
        }
    }

    private fun initVM() {
        sessionManagerA = SessionManager(requireContext())

        viewModel = ViewModelProvider(
                requireActivity(),
                ViewModelFactory(ApiHelper(ApiClient.getClientV1WithToken(sessionManagerA)))
        ).get(PostAJobViewModel::class.java)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest {

            }
        }
    }

    override fun onItemClick(view: View?, obj: AttachmentModelV2?, position: Int, action: String?) {
        if (checkPermissionReadExternalStorage(requireContext())) {
            val openGallary = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(Intent.createChooser(openGallary, "Open Gallary"), 1)
        }
    }

    fun checkPermissionReadExternalStorage(
            context: Context?): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        return if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                                (context as Activity?)!!,
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                } else {
                    ActivityCompat
                            .requestPermissions(
                                    activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
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

    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == 1 && resultCode == ActivityBase.RESULT_OK) {
                filePath = data!!.data
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, filePath)
                imagePath = getPath(data.data)
                //imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
                //  uploadImageWithAmount();
                val file = File(imagePath)
                if (title.equals(ConstantKey.CREATE_A_JOB, ignoreCase = true)) {
                    attachmentArrayList.add(AttachmentModelV2(-1, file.name, file.name,
                            "", "", "", "", "", -1, AttachmentAdapter.VIEW_TYPE_IMAGE, file = file))
                    mediaAdapter.notifyDataSetChanged()
                    uploadDataInTempApi(file)
                } else {
                    uploadDataInTaskMediaApi(file)
                }
                Timber.e(imagePath)
            } else if (requestCode == 1001 && resultCode == ActivityBase.RESULT_OK) {
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
                        //showToast("Try Again", this)
                    }
                }
            } else if (resultCode == ActivityBase.RESULT_CANCELED) {
                //showToast(getString(R.string.msg_image_select), this@AttachmentActivity)
            } else {
                //showToast(getString(R.string.error_image_select), this@AttachmentActivity)
            }
        } catch (e: Exception) {
            Timber.e(e.toString())
        }
    }

    private fun uploadDataInTempApi(pictureFile: File) {
        val call: Call<String?>?
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile)
        val imageFile = MultipartBody.Part.createFormData("media", pictureFile.name, requestFile)
        call = ApiClient.getClient().getTaskTempAttachmentMediaData("XMLHttpRequest", sessionManagerA.tokenType + " " + sessionManagerA.accessToken, imageFile)
        call!!.enqueue(object : Callback<String?> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<String?>, response: retrofit2.Response<String?>) {
                Timber.e(response.toString())
                if (response.code() == 422) {
                    //showToast(response.message(), this@AttachmentActivity)
                    val name = ((call.request().body() as MultipartBody).parts()[0].headers()!!.value(0).toString()).split("\"")[3]
                    val item = attachmentArrayList.filter { it.name == name }[0]
                    val index = attachmentArrayList.indexOf(item)
                    item.id = -2
                    attachmentArrayList[index] = item
                    mediaAdapter.notifyDataSetChanged()
                    return
                }
                try {
                    val strResponse = response.body()
                    Timber.e(strResponse)
                    val jsonObject = JSONObject(strResponse)
                    Timber.e(jsonObject.toString())
                    if (jsonObject.has("data")) {
                        val jsonobjectData = jsonObject.getJSONObject("data")
                        val gson = Gson()
                        val attachment = gson.fromJson(jsonobjectData.toString(), AttachmentModelV2::class.java)

                        if (attachmentArrayList.size != 0) {
                            val item = attachmentArrayList.filter {
                                it.name == attachment.name + attachment.fileName!!.substring(attachment!!.fileName!!.length - 4, attachment.fileName!!.length)
                            }[0]
                            val index = attachmentArrayList.indexOf(item)
                            attachmentArrayList[index] = attachment
                            mediaAdapter.notifyDataSetChanged()
                        }
                    } else {
                        val name = ((call.request().body() as MultipartBody).parts()[0].headers()!!.value(0).toString()).split("\"")[3]
                        val item = attachmentArrayList.filter { it.name == name }[0]
                        val index = attachmentArrayList.indexOf(item)
                        item.id = -2
                        attachmentArrayList[index] = item
                        mediaAdapter.notifyDataSetChanged()
                    }
                    //  adapter.notifyItemRangeInserted(0,attachmentArrayList.size());
                } catch (e: Exception) {
                    //showToast("Something went wrong", this@AttachmentActivity)
                    e.printStackTrace()
                    val name = ((call.request().body() as MultipartBody).parts()[0].headers()!!.value(0).toString()).split("\"")[3]
                    val item = attachmentArrayList.filter { it.name == name }[0]
                    val index = attachmentArrayList.indexOf(item)
                    item.id = -2
                    attachmentArrayList[index] = item
                    mediaAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                activity.hideProgressDialog()
                Timber.e(call.toString())
                val name = ((call.request().body() as MultipartBody).parts()[0].headers()!!.value(0).toString()).split("\"")[3]
                val item = attachmentArrayList.filter { it.name == name }[0]
                val index = attachmentArrayList.indexOf(item)
                item.id = -2
                attachmentArrayList[index] = item
                mediaAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun uploadDataInTaskMediaApi(pictureFile: File) {
        activity.showProgressDialog()
        val call: Call<String?>?

        //    File file = new File(imagePath);
        //    Log.e("AttachmentApi: ", file.getName());
        //   Log.e("uploadProfile++:11 ", imagePath);
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile)
        // MultipartBody.Part is used to send also the actual file name
        val imageFile = MultipartBody.Part.createFormData("media", pictureFile.name, requestFile)
        call = ApiClient.getClient().getTasKAttachmentMediaUpload(slug, "XMLHttpRequest", sessionManagerA.tokenType + " " + sessionManagerA.accessToken, imageFile)
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: retrofit2.Response<String?>) {
                activity.hideProgressDialog()
                Timber.e(response.toString())
                if (response.code() == 422) {
                    //showToast(response.message(), this@AttachmentActivity)
                    return
                }
                try {
                    val strResponse = response.body()
                    if (response.code() == HttpStatus.NOT_FOUND) {
                        //showToast("not found", this@AttachmentActivity)
                        return
                    }
                    if (response.code() == HttpStatus.AUTH_FAILED) {
                        activity.unauthorizedUser()
                        return
                    }
                    if (response.code() == HttpStatus.SUCCESS) {
                        Timber.e(strResponse)
                        val jsonObject = JSONObject(strResponse)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("data")) {
                            val attachment = AttachmentModelV2()
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
                        mediaAdapter.notifyItemInserted(0)
                        //  adapter.notifyItemRangeInserted(0,attachmentArrayList.size());
                        // showToast("attachment added", AttachmentActivity.this);
                    } else {
                        //showToast("Something went wrong", this@AttachmentActivity)
                    }
                } catch (e: JSONException) {
                    //showToast("Something went wrong", this@AttachmentActivity)
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                activity.hideProgressDialog()
                Timber.e(call.toString())
            }
        })
    }

    private fun showLimitDialog() {
        val builder1 = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder1.setMessage("Maximum video duration exceeds 30 sec")
        builder1.setCancelable(true)
        builder1.setPositiveButton(
                "OK"
        ) { dialog, id -> dialog.cancel() }
        val alert11 = builder1.create()
        alert11.show()
    }

    @SuppressLint("Range")
    fun getPath(uri: Uri?): String {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = requireActivity().contentResolver.query(uri!!, filePathColumn, null, null, null)!!
        cursor.moveToFirst()
        val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
        val picturePath: String = cursor.getString(columnIndex)
        cursor.close()
        Timber.e(picturePath)
        return picturePath
    }

    override fun showOptions(item: AttachmentModelV2?, option: Option) {
        next.visibility = View.GONE
        linOptions.visibility = View.VISIBLE
        this.option = option
        if (option == Option.RETRY)
            this.retryItem = item
        when (option) {
            Option.VIEW -> {
                option1Icon.setImageResource(R.drawable.ic_visibility_v5)
                option1Txt.text = "View"
                option2Icon.setImageResource(R.drawable.ic_delete_v5)
                option2Txt.text = "Delete"
            }
            Option.RETRY -> {
                option1Icon.setImageResource(R.drawable.ic_delete_v5)
                option1Txt.text = "Delete"
                option2Icon.setImageResource(R.drawable.ic_restart_alt_v5)
                option2Txt.text = "Upload again"
            }
            Option.SELECT_ALL -> {
                option1Icon.setImageResource(R.drawable.ic_library_add_check)
                option1Txt.text = "Select all"
                option2Icon.setImageResource(R.drawable.ic_delete_v5)
                option2Txt.text = "Delete"
            }
            Option.HIDE -> {
                next.visibility = View.VISIBLE
                linOptions.visibility = View.GONE
            }
        }

    }

    override fun onStop() {
        viewModel.setAttachments(attachmentArrayList)
        super.onStop()
    }
}