package com.jobtick.android.fragments.mu_profile_fragments

import com.jobtick.android.material.ui.postajob.MediaAdapter
import com.jobtick.android.material.ui.postajob.Option
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import com.jobtick.android.R
import com.jobtick.android.activities.ActivityBase
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.adapers.AttachmentAdapter
import com.jobtick.android.databinding.FragmentProfileHelpAndSupportBinding
import com.jobtick.android.databinding.FragmentProfilePortfolioBinding
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
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.File


private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123

class ProfileFragmentPortfolio : Fragment(), MediaAdapter.OnItemClickListener, MediaAdapter.OnShowOptions, PickiTCallbacks {

    private lateinit var activity: DashboardActivity
    private lateinit var attachmentArrayList: ArrayList<AttachmentModelV2>

    private lateinit var next: MaterialButton
    private lateinit var linOptions: LinearLayout
    private lateinit var options1: LinearLayout
    private lateinit var options2: LinearLayout
    private lateinit var option1: LinearLayout
    private lateinit var sessionManagerA: SessionManager
    private lateinit var viewModel: PostAJobViewModel
    private lateinit var rlAmount: RecyclerView
    private lateinit var mediaAdapter: MediaAdapter
    private lateinit var option1Icon: AppCompatImageView
    private lateinit var option1Txt: AppCompatTextView
    private lateinit var option2Icon: AppCompatImageView
    private lateinit var option2Txt: AppCompatTextView
  //  private lateinit var txtTitle: AppCompatTextView
    private lateinit var image: AppCompatImageView
    private var imagePath: String? = null
    private var filePath: Uri? = null
    private var bitmap: Bitmap? = null
    private var title: String? = null
    private var slug: String? = null
    private var option: Option = Option.HIDE
    private var selectedItem: AttachmentModelV2? = null
    private var _binding: FragmentProfilePortfolioBinding? = null
    private val binding get() = _binding!!
    private var attachmentIDs = mutableMapOf<String, MutableList<String>>()
    private var portfolioID = "-1"
    var pickiT: PickiT? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfilePortfolioBinding.inflate(inflater, container, false)
        return binding.root
       // return inflater.inflate(R.layout.fragment_profile_portfolio, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = (requireActivity() as DashboardActivity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SetToolbar(activity, "Add Portfolio", "Save", R.id.navigation_profile, binding.header, view)
        binding.header.back.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_add_portfolio_item_to_navigation_profile_portfolio_item)
        }

        binding.header.txtAction.setOnClickListener {
            if(checkValidation())
                addOrUpdatePortfolio(activity)
        }

        binding.deleteTxt.setOnClickListener {
            if(portfolioID.equals("-1"))
            {
                activity.showToast("error", activity)
                return@setOnClickListener
            }
            else

                showDeleteDialog()
        }
        binding.about.editText!!.setOnFocusChangeListener{view, b ->
            if(b)
                binding.about.editText!!.hint = "Description"
            else
                binding.about.editText!!.hint = ""

        }

        initVars()
        initVM()

        try {
            if (!requireArguments().getString("json").isNullOrEmpty()) {
                binding.shareInProParent.visibility = View.VISIBLE
                binding.deleteTxt.visibility = View.VISIBLE
                binding.header.txtTitle.text = "Edit Portfolio"
                val jsonObject = JSONObject(requireArguments().getString("json").toString())
                binding.isShare.isChecked = jsonObject.getString("share_in") == "1"
                binding.edittextFirstnameValue.setText(jsonObject.getString("title").toString())
                binding.about.editText?.setText(jsonObject.getString("description").toString())

                portfolioID = jsonObject.getString("id")
                val jsonArray = jsonObject.getJSONArray("img")
                for(i in 0 until jsonArray.length()) {
                    val jsonobjectData = jsonArray.getJSONObject(i)
                    val gson = Gson()
                    val attachment =
                        gson.fromJson(jsonobjectData.toString(), AttachmentModelV2::class.java)
                    attachmentIDs.getOrPut("attachments[]", ::mutableListOf).add(attachment.id.toString())

                    if (attachment.mime!!.contains("pdf")) {
                        attachment.type = AttachmentAdapter.VIEW_TYPE_PDF
                    } else {
                        attachment.type = AttachmentAdapter.VIEW_TYPE_IMAGE
                    }
                    attachmentArrayList[i+1] = attachment
                }
            }
            else {
                binding.shareInProParent.visibility = View.GONE
                binding.deleteTxt.visibility = View.GONE
                binding.header.txtTitle.text = "Add Portfolio"
            }
            mediaAdapter.notifyDataSetChanged()

        }catch (e: Exception) {
            binding.shareInProParent.visibility = View.GONE
            binding.deleteTxt.visibility = View.GONE
            binding.header.txtTitle.text = "Add Portfolio"

        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initVars() {

        pickiT = PickiT(requireContext(), this, requireActivity())
        sessionManagerA = SessionManager(requireContext())
        val displayMetrics = DisplayMetrics()
        //activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        attachmentArrayList = ArrayList()
        mediaAdapter = MediaAdapter(attachmentArrayList, context = requireContext(), width)
        mediaAdapter.setOnItemClickListener(this)
        mediaAdapter.showOptions = this
        next = requireView().findViewById(R.id.btn_next)
        linOptions = requireView().findViewById(R.id.linOptions)
        option1 = requireView().findViewById(R.id.option1)
        rlAmount = requireView().findViewById(R.id.rl_medias)
        option1Icon = requireView().findViewById(R.id.option1_icon)
        option1Txt = requireView().findViewById(R.id.option1_txt)
      //  txtTitle = requireView().findViewById(R.id.txtTitle)
        option2Icon = requireView().findViewById(R.id.option2_icon)
        option2Txt = requireView().findViewById(R.id.option2_txt)
        options1 = requireView().findViewById(R.id.options1)
        options2 = requireView().findViewById(R.id.options2)
        image = requireView().findViewById(R.id.image)
        rlAmount.adapter = mediaAdapter
        rlAmount.layoutManager = GridLayoutManager(requireContext(), 3)
        rlAmount.addItemDecoration(SpacesItemDecorationV2((8).dpToPx()))
        title = ConstantKey.CREATE_A_JOB
//        next.setOnClickListener {
//            activity.navController.popBackStack()
//        }
        options1.setOnClickListener {
            linOptions.visibility = View.GONE
            when (option) {
                Option.SELECT_ALL -> {
                    attachmentArrayList.forEach { if (it.type == selectedItem!!.type) it.isChecked = true }
                    mediaAdapter.notifyDataSetChanged()
                }
                Option.VIEW -> {
                    rlAmount.visibility = View.VISIBLE
                    if (image.visibility == View.GONE) {
                        linOptions.visibility = View.VISIBLE
                        image.visibility = View.VISIBLE
                        Glide.with(image).load(selectedItem!!.url).into(image)
                     //   image.setImageURI(Uri.parse(selectedItem!!.file!!.path))
                        option1Icon.setImageResource(R.drawable.new_design_cross)
                        option1Txt.text = "Close"
                     //   txtTitle.visibility = View.GONE

                       // activity.previewMode(true)
                    } else {
                      //  activity.previewMode(false)
                        image.visibility = View.GONE
                        option1Icon.setImageResource(R.drawable.ic_visibility_v5)
                        option1Txt.text = "View"
                    //    txtTitle.visibility = View.VISIBLE
                        rlAmount.visibility = View.VISIBLE
                    }

                }
                Option.RETRY -> {
                    selectedItem?.let { retryItem ->
                        attachmentArrayList.remove(retryItem)
                        mediaAdapter.notifyDataSetChanged()
                        next.visibility = View.GONE
                        linOptions.visibility = View.GONE

                    }

                }
                else -> {}
            }
        }
        options2.setOnClickListener {
            linOptions.visibility = View.GONE
            when (option) {
                Option.SELECT_ALL -> {
                    attachmentArrayList.removeAll(attachmentArrayList.filter { it.isChecked }.toSet())
                    mediaAdapter.notifyDataSetChanged()
                    if (attachmentArrayList.size == 1) {
                        next.visibility = View.GONE
                        linOptions.visibility = View.GONE
                    }
                }
                Option.RETRY -> {
                    selectedItem?.let { retryItem ->
                        attachmentArrayList.forEach {
                            if (it == retryItem) {
                                it.type = AttachmentAdapter.VIEW_TYPE_PROGRESS
                                it.isChecked = false
                            }
                        }
                        uploadDataInTempApi(retryItem.file!!)
                        mediaAdapter.notifyDataSetChanged()
                    }
                    next.visibility = View.GONE
                    linOptions.visibility = View.GONE
                }
                Option.VIEW -> {
                    selectedItem?.let { retryItem ->
                        attachmentIDs["attachments[]"]?.remove(retryItem.id.toString())

                   //     attachmentIDs.remove("attachments[]", retryItem.id.toString())
                        attachmentArrayList.remove(retryItem)
                        mediaAdapter.notifyDataSetChanged()
                        image.visibility = View.GONE

                    }

                }
                Option.DELETE -> {
                    Log.d("attachment3", attachmentIDs.size.toString())

                    selectedItem?.let { retryItem ->
                        attachmentArrayList.remove(retryItem)
                        mediaAdapter.notifyDataSetChanged()
                    }

                }
                else -> {}
            }
        }
    }

    private fun initVM() {
        sessionManagerA = SessionManager(requireContext())

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiHelper(ApiClient.getClientV1WithToken(sessionManagerA)))
        ).get(PostAJobViewModel::class.java)
        activity.lifecycleScope.launch {
            viewModel.state.collectLatest {
                if (attachmentArrayList.isEmpty()) {
                    attachmentArrayList.add(AttachmentModelV2(type = AttachmentAdapter.VIEW_TYPE_ADD))
                    attachmentArrayList.addAll(it.attachments)
                    for (i in 0 until 4 - it.attachments.size)
                        attachmentArrayList.add(AttachmentModelV2(type = AttachmentAdapter.VIEW_TYPE_PLACE_HOLDER))
                    mediaAdapter.notifyDataSetChanged()
                }
                if (!it.isImageVisible) {
                    //activity.previewMode(false)
                    image.visibility = View.GONE
                    option1Icon.setImageResource(R.drawable.ic_visibility_v5)
                    option1Txt.text = "View"
                 //   txtTitle.visibility = View.VISIBLE
                    rlAmount.visibility = View.VISIBLE
                }
            }
        }

    }

    override fun onItemClick(view: View?, obj: AttachmentModelV2?, position: Int, action: String?) {
        Log.d("clickedhere", "2")

        //  showDialog()
        if (checkPermissionReadExternalStorage(requireContext())) {
            Log.d("clickedhere", "1")
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
                    attachmentArrayList.add(attachmentArrayList.indexOf(attachmentArrayList.first { it.type == AttachmentAdapter.VIEW_TYPE_PLACE_HOLDER }), AttachmentModelV2(-1, file.name, file.name,
                        "", "", "", "", "", -1, AttachmentAdapter.VIEW_TYPE_PROGRESS, file = file))
                    attachmentArrayList.removeAt(5)
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
                        pickiT!!.getPath(data.data!!, Build.VERSION.SDK_INT)
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

                    item.type = AttachmentAdapter.VIEW_TYPE_ERROR
                    attachmentArrayList[index] = item
                    mediaAdapter.notifyDataSetChanged()
                    return
                }
                try {
                    val strResponse = response.body()
                    Log.d("tempresponse1", strResponse.toString())
                    Timber.e(strResponse)
                    val jsonObject = JSONObject(strResponse)
                    Timber.e(jsonObject.toString())
                    if (jsonObject.has("data")) {
                        val jsonobjectData = jsonObject.getJSONObject("data")
                        val gson = Gson()
                        val attachment = gson.fromJson(jsonobjectData.toString(), AttachmentModelV2::class.java)
                      //  attachmentIDs["attachments[]"] = attachment.id.toString()
                        attachmentIDs.getOrPut("attachments[]", ::mutableListOf).add(attachment.id.toString())
                        Log.d("requestBodyP", attachmentIDs["attachments[]"].toString())

                        if (attachment.mime!!.contains("pdf")) {
                            attachment.type = AttachmentAdapter.VIEW_TYPE_PDF
                        } else {
                            attachment.type = AttachmentAdapter.VIEW_TYPE_IMAGE
                        }
                        if (attachmentArrayList.size != 0) {
                            val item = attachmentArrayList.filter {
                                it.name == attachment.name + attachment.fileName!!.substring(attachment!!.fileName!!.length - 4, attachment.fileName!!.length)
                            }[0]
                            val index = attachmentArrayList.indexOf(item)
                            attachment.file = pictureFile
                            attachmentArrayList[index] = attachment
                            mediaAdapter.notifyDataSetChanged()
                        }
                    } else {
                        val name = ((call.request().body() as MultipartBody).parts()[0].headers()!!.value(0).toString()).split("\"")[3]
                        val item = attachmentArrayList.filter { it.name == name }[0]
                        val index = attachmentArrayList.indexOf(item)

                        item.type = AttachmentAdapter.VIEW_TYPE_ERROR
                        attachmentArrayList[index] = item
                        mediaAdapter.notifyDataSetChanged()
                    }
                    //  adapter.notifyItemRangeInserted(0,attachmentArrayList.size());
                } catch (e: Exception) {
                    Log.d("tempresponse2", e.toString())
                    //showToast("Something went wrong", this@AttachmentActivity)
                    e.printStackTrace()
                    val name = ((call.request().body() as MultipartBody).parts()[0].headers()!!.value(0).toString()).split("\"")[3]
                    val item = attachmentArrayList.filter { it.name == name }[0]
                    val index = attachmentArrayList.indexOf(item)

                    item.type = AttachmentAdapter.VIEW_TYPE_ERROR
                    attachmentArrayList[index] = item
                    mediaAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                Log.d("tempresponse3", t.toString())
                activity.hideProgressDialog()
                Timber.e(call.toString())
                val name = ((call.request().body() as MultipartBody).parts()[0].headers()!!.value(0).toString()).split("\"")[3]
                val item = attachmentArrayList.filter { it.name == name }[0]
                val index = attachmentArrayList.indexOf(item)

                item.type = AttachmentAdapter.VIEW_TYPE_ERROR
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
                            if (attachment.mime!!.contains("pdf")) {
                                attachment.type = AttachmentAdapter.VIEW_TYPE_PDF
                            } else {
                                attachment.type = AttachmentAdapter.VIEW_TYPE_IMAGE
                            }
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
        option1.visibility = View.VISIBLE
        this.option = option
        this.selectedItem = item
        when (option) {
            Option.VIEW -> {
                option1Icon.setImageResource(R.drawable.ic_visibility_v5)
                option1Txt.text = "View"
                option2Icon.setImageResource(R.drawable.ic_delete_v5)
                option2Txt.text = "Delete"
                viewModel.setIsImageVisible(true)
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
                next.visibility = View.GONE
                linOptions.visibility = View.GONE
            }
            Option.DELETE -> {
                option2Icon.setImageResource(R.drawable.ic_delete_v5)
                option2Txt.text = "Delete"
                next.visibility = View.GONE
                option1.visibility = View.INVISIBLE
                linOptions.visibility = View.VISIBLE

            }
            else -> {}
        }

    }

    override fun onStop() {
        viewModel.setAttachments(attachmentArrayList.filter {
            it.type == AttachmentAdapter.VIEW_TYPE_IMAGE ||
                    it.type == AttachmentAdapter.VIEW_TYPE_PDF
        } as ArrayList<AttachmentModelV2>)
        super.onStop()
    }

    private fun showDialog() {
        val view: View = layoutInflater.inflate(R.layout.dialog_attachment, null)
        val infoDialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
        val window = infoDialog.window;

        val wlp = window!!.attributes;
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        wlp.gravity = Gravity.BOTTOM
        window.attributes = wlp
        infoDialog.show()
        window.findViewById<MaterialButton>(R.id.cancel).setOnClickListener {
            infoDialog.dismiss()
        }
        window.findViewById<MaterialButton>(R.id.gallery).setOnClickListener {
            if (checkPermissionReadExternalStorage(requireContext())) {
                val openGallary = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(Intent.createChooser(openGallary, "Open Gallary"), 1)
                infoDialog.dismiss()
            }
        }
        window.findViewById<MaterialButton>(R.id.pdf).setOnClickListener {
            if (checkPermissionReadExternalStorage(requireContext())) {
                val intent = Intent()
                intent.type = "application/pdf"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select PDF File"), 1001)
                infoDialog.dismiss()
            }
        }
    }

    override fun PickiTonUriReturned() {

    }

    override fun PickiTonStartListener() {
    }

    override fun PickiTonProgressUpdate(progress: Int) {
    }

    override fun PickiTonCompleteListener(path: String?, wasDriveFile: Boolean, wasUnknownProvider: Boolean, wasSuccessful: Boolean, Reason: String?) {
        path
        val file = File(path)
        if (file != null) {
            if (title.equals(ConstantKey.CREATE_A_JOB, ignoreCase = true)) {
                attachmentArrayList.add(attachmentArrayList.indexOf(attachmentArrayList.first { it.type == AttachmentAdapter.VIEW_TYPE_PLACE_HOLDER }), AttachmentModelV2(-1, file.name, file.name,
                    "", "", "", "", "", -1, AttachmentAdapter.VIEW_TYPE_PROGRESS, file = file))
                attachmentArrayList.removeAt(5)
                mediaAdapter.notifyDataSetChanged()
                uploadDataInTempApi(file)
            } else {
                uploadDataInTaskMediaApi(file)
            }
        } else {
            //showToast("Try Again", this)
        }
    }

    override fun PickiTonMultipleCompleteListener(paths: java.util.ArrayList<String>?, wasSuccessful: Boolean, Reason: String?) {
    }

    @SuppressLint("SuspiciousIndentation")
    fun addOrUpdatePortfolio(context: Context) {
        val sessionManager = SessionManager(requireActivity())
        (context as DashboardActivity).showProgressDialog()
        Helper.closeKeyboard(context)
        val type = if(portfolioID != "-1") {
            attachmentIDs.getOrPut("id", ::mutableListOf).add(portfolioID)
            "update"
        } else
            "add"
        attachmentIDs.getOrPut("title", ::mutableListOf).add(binding.edittextFirstnameValue.text.toString())
        attachmentIDs.getOrPut("description", ::mutableListOf).add(binding.about.editText?.text.toString())
        if(binding.isShare.isChecked)
            attachmentIDs.getOrPut("share_in", ::mutableListOf).add("1")
        else
            attachmentIDs.getOrPut("share_in", ::mutableListOf).add("0")

        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM).apply {
            val quizzes: List<String> = attachmentIDs["attachments[]"].orEmpty()

            for (input in quizzes) {
                addFormDataPart("attachments[]", input)
            }
            addFormDataPart("title", binding.edittextFirstnameValue.text.toString())
            addFormDataPart("description", binding.about.editText?.text.toString())
            if(binding.isShare.isChecked)
                addFormDataPart("share_in", "1")
            else
                addFormDataPart("share_in", "0")

            //sessionManager?.role?.let { addFormDataPart("role_as", it) }
        }.build()
//        for(i in 0 until requestBody.size())
//        Log.d("requestBodyport", requestBody.parts().get(i).body().toString())
        val call: Call<String?>? = ApiClient.getClientV1WithToken(sessionManager).addPortfolioItem(
            type,
            "XMLHttpRequest",
            requestBody
        )
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                context.hideProgressDialog()
                try {
                    if(response.isSuccessful) {
                        Log.d("responseUpdateProfile", response.toString())
                        val jsonObject = JSONObject(response.body()!!)
                        Log.d("responseUpdateProfile", jsonObject.toString())
                        (requireActivity() as DashboardActivity).showToast("Saved Successfully!", requireActivity())
                     //   jsonobject.value = jsonObject.getJSONObject("data")
//                        for(i in 0 until jsonObjectSkills.length()) {
//                            jsonObjectSkills.getJSONObject(i.toString()).getString("id")
//                            jsonObjectSkills.getJSONObject(i.toString()).getString("title")
//                        }
                    }else
                    {
                        val jObjError = JSONObject(
                            response.errorBody()!!.string()
                        )
                        Log.d("attachmentIderr", jObjError.toString())
                        context.showToast(jObjError.getJSONObject("error").getString("message"), context)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    context.showToast("Something Went Wrong", context)
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                context.hideProgressDialog()
                context.showToast(t.toString(), context)

                Timber.e(call.toString())
            }
        })
    }
    fun deletePortfolio(id: String) {
        val sessionManager = SessionManager(requireActivity())
        (context as DashboardActivity).showProgressDialog()
        Helper.closeKeyboard(activity)
        val call: Call<String?>? = ApiClient.getClientV1WithToken(sessionManager).deletePortfolio(id,
            "XMLHttpRequest"
        )
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                activity.hideProgressDialog()
                try {
                    if(response.isSuccessful) {
                        (requireActivity() as DashboardActivity).showToast("Deleted Successfully!", requireActivity())
                        view?.findNavController()?.navigate(R.id.action_navigation_profile_add_portfolio_item_to_navigation_profile_portfolio_item)
                    }else
                    {
                        val jObjError = JSONObject(
                            response.errorBody()!!.string()
                        )
                        Log.d("attachmentIderr", jObjError.toString())
                        activity.showToast(jObjError.getJSONObject("error").getString("message"), context)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    activity.showToast("Something Went Wrong", context)
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                activity.hideProgressDialog()
                activity.showToast(t.toString(), context)

                Timber.e(call.toString())
            }
        })
    }

    private fun checkValidation(): Boolean {
        activity.hideKeyboard()
        activity.hideKeyboard()
        when {
            binding.edittextFirstname.editText?.text.isNullOrEmpty() -> {
                Helper.setError(activity, "Please enter title", binding.edittextFirstname)
                return false
            }
            binding.about.editText?.text.isNullOrEmpty() -> {
                Helper.setError(activity, "Please enter desctiption", binding.about)
                return false
            }
            attachmentIDs.isEmpty() -> {
                activity.showToast("Please add atleast one image!", activity)
                return false
            }
        }
        return true
    }
    fun showDeleteDialog() {
        val cancel: MaterialButton?
        val delete: MaterialButton?
        val title: TextView?
        val mainTitle: TextView?
        val dialog = Dialog(activity, R.style.AnimatedDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        dialog.setContentView(R.layout.dialog_discard_changes_new)

        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog.window!!.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        cancel = dialog.findViewById(R.id.cancel)
        delete = dialog.findViewById(R.id.discard)
        title = dialog.findViewById(R.id.title)
        mainTitle = dialog.findViewById(R.id.mainTitle)
        mainTitle.text = "Delete Portfolio"
        delete.text = "Delete"
        title.setText(activity.getString(R.string.profile_portfolio_delete))

        cancel.setOnClickListener {
            dialog.cancel()
        }

        delete.setOnClickListener {
            dialog.cancel()
           deletePortfolio(portfolioID)
        }


        dialog.show()

    }

}