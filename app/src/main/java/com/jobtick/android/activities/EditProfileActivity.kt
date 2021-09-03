package com.jobtick.android.activities

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.adapers.AttachmentAdapter
import com.jobtick.android.adapers.AttachmentAdapterEditProfile
import com.jobtick.android.adapers.SuburbSearchAdapter.SubClickListener
import com.jobtick.android.fragments.DatePickerBottomSheet
import com.jobtick.android.fragments.DatePickerBottomSheet.DateChange
import com.jobtick.android.fragments.ProfileFragment
import com.jobtick.android.fragments.SearchSuburbBottomSheet
import com.jobtick.android.models.AttachmentModel
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.models.response.searchsuburb.Feature
import com.jobtick.android.retrofit.ApiClient
import com.jobtick.android.utils.*
import com.jobtick.android.widget.ExtendedCommentText
import com.jobtick.android.widget.ExtendedEntryText
import com.jobtick.android.widget.SpacingItemDecoration
import com.mikhaellopez.circularimageview.CircularImageView
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
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "EditProfile"

class EditProfileActivity : ActivityBase(), AttachmentAdapterEditProfile.OnItemClickListener, SubClickListener, DateChange {
    private val PLACE_SELECTION_REQUEST_CODE = 1
    var edtFirstName: ExtendedEntryText? = null
    var edtLastName: ExtendedEntryText? = null
    var txtSuburb: ExtendedEntryText? = null
    var edtTagline: ExtendedEntryText? = null
    var edtPhoneNumber: ExtendedEntryText? = null
    var edtAboutMe: ExtendedCommentText? = null
    var edtEmailAddress: ExtendedEntryText? = null
    var txtBirthDate: ExtendedEntryText? = null
    var edtBusinessNumber: ExtendedEntryText? = null
    var imgTransportationBack: ImageView? = null
    var recyclerView: RecyclerView? = null
    var txtTransportation: TextView? = null
    var rltBtnTransportation: RelativeLayout? = null
    var txtLanguages: TextView? = null
    var imgLanguagesBack: ImageView? = null
    var rltBtnLanguages: RelativeLayout? = null
    var txtEducation: TextView? = null
    var imgEducationBack: ImageView? = null
    var rltBtnEducation: RelativeLayout? = null
    var txtExperience: TextView? = null
    var imgExperienceBack: ImageView? = null
    var txtGeneralInfo: TextView? = null
    var txtPrivateInfo: TextView? = null
    var txtPortfolioSkills: TextView? = null
    var llGeneral: LinearLayout? = null
    var llPinfo: LinearLayout? = null
    var llPS: LinearLayout? = null
    var underTab3: View? = null
    var underTab2: View? = null
    var underTab1: View? = null
    var rltBtnExperience: RelativeLayout? = null
    var txtSpecialities: TextView? = null
    var imgSpecialitiesBack: ImageView? = null
    var rltBtnSpecialities: RelativeLayout? = null
    var bottomSheet: FrameLayout? = null
    var lytDeletePicture: RelativeLayout? = null
    var cardSaveProfile: Button? = null
    var ivBack: ImageView? = null
    var smallPlus: FrameLayout? = null
    var imgAvatar: CircularImageView? = null
    private var attachmentArrayList: ArrayList<AttachmentModel>? = null
    private var attachmentIDs: ArrayList<String>? = null
    private var userAccountModel: UserAccountModel? = null
    private var str_latitude: String? = null
    private var str_longitude: String? = null
    private var mBehavior: BottomSheetBehavior<*>? = null
    private var str_DOB_MODEL = ""
    private var adapter: AttachmentAdapterEditProfile? = null
    private var uploadableImage: UploadableImage? = null
    private var isImageProfile = false
    private var year = 0
    private var month = 0
    private var day = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        initIDs()
        onViewClick()
        attachmentArrayList = ArrayList()
        mBehavior = BottomSheetBehavior.from<FrameLayout?>(bottomSheet!!)
        edtPhoneNumber!!.setExtendedViewOnClickListener { verifyPhone() }
        ivBack!!.setOnClickListener { onBackPressed() }
        init()
        allUserProfileDetails
        uploadableImage = object : AbstractUploadableImageImpl(this) {
            override fun onImageReady(imageFile: File) {
                uploadMedia(imageFile!!)
            }
        }
    }

    private fun initIDs() {
        edtFirstName = findViewById(R.id.edt_first_name)
        edtLastName = findViewById(R.id.edt_last_name)
        txtSuburb = findViewById(R.id.txt_suburb)
        edtTagline = findViewById(R.id.edt_tagline)
        edtPhoneNumber = findViewById(R.id.edt_phone_number)
        edtAboutMe = findViewById(R.id.edt_about_me)
        edtEmailAddress = findViewById(R.id.edt_email_address)
        txtBirthDate = findViewById(R.id.txt_birth_date)
        edtBusinessNumber = findViewById(R.id.edt_business_number)
        imgTransportationBack = findViewById(R.id.img_transportation_back)
        recyclerView = findViewById(R.id.recycler_view)
        txtTransportation = findViewById(R.id.txt_transportation)
        rltBtnTransportation = findViewById(R.id.rlt_btn_transportation)
        txtLanguages = findViewById(R.id.txt_languages)
        imgLanguagesBack = findViewById(R.id.img_languages_back)
        rltBtnLanguages = findViewById(R.id.rlt_btn_languages)
        txtEducation = findViewById(R.id.txt_education)
        imgEducationBack = findViewById(R.id.img_education_back)
        rltBtnEducation = findViewById(R.id.rlt_btn_education)
        txtExperience = findViewById(R.id.txt_experience)
        imgExperienceBack = findViewById(R.id.img_experience_back)
        imgExperienceBack = findViewById(R.id.img_experience_back)
        txtGeneralInfo = findViewById(R.id.txt_general_info)
        txtPrivateInfo = findViewById(R.id.txt_private_info)
        txtPortfolioSkills = findViewById(R.id.txt_portfolio_skills)
        llGeneral = findViewById(R.id.llGeneral)
        llPinfo = findViewById(R.id.llPinfo)
        llPS = findViewById(R.id.llPS)
        underTab3 = findViewById(R.id.under_tab3)
        underTab2 = findViewById(R.id.under_tab2)
        underTab1 = findViewById(R.id.under_tab1)
        rltBtnExperience = findViewById(R.id.rlt_btn_experience)
        txtSpecialities = findViewById(R.id.txt_specialities)
        imgSpecialitiesBack = findViewById(R.id.img_specialities_back)
        rltBtnSpecialities = findViewById(R.id.rlt_btn_specialities)
        bottomSheet = findViewById(R.id.bottom_sheet)
        lytDeletePicture = findViewById(R.id.lytDeletePicture)
        cardSaveProfile = findViewById(R.id.card_save_profile)
        ivBack = findViewById(R.id.ivBack)
        smallPlus = findViewById(R.id.small_plus)
        imgAvatar = findViewById(R.id.img_user_avatar)
    }

    private fun validation(): Boolean {
        when {
            TextUtils.isEmpty(edtFirstName!!.text.trim { it <= ' ' }) -> {
                edtFirstName!!.setError("Enter first name")
                showToast("Please fill inputs", this)
                return false
            }
            TextUtils.isEmpty(edtLastName!!.text.trim { it <= ' ' }) -> {
                edtLastName!!.setError("Enter last name")
                showToast("Please fill inputs", this)
                return false
            }
            TextUtils.isEmpty(txtSuburb!!.text.trim { it <= ' ' }) -> {
                showToast("Please fill inputs", this)
                txtSuburb!!.setError("Select your location")
                return false
            }
            else -> return true
        }
    }


    private fun updateProfile() {
        showProgressDialog()
        val call: Call<String?>?
        showProgressDialog()
        Helper.closeKeyboard(this)
        attachmentIDs = ArrayList()
        attachmentArrayList!!.forEach {
            attachmentIDs!!.add(it.id.toString())
        }
        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM).apply {
            addFormDataPart("fname", edtFirstName!!.text.trim { it <= ' ' })
            addFormDataPart("lname", edtLastName!!.text.trim { it <= ' ' })
            addFormDataPart("location", txtSuburb!!.text.trim { it <= ' ' })
            addFormDataPart("tagline", edtTagline!!.text.trim { it <= ' ' })
            addFormDataPart("about", edtAboutMe!!.text.trim { it <= ' ' })
            addFormDataPart("business_number", edtBusinessNumber!!.text.trim { it <= ' ' })
            addFormDataPart("role_as", sessionManager.role)
            if (str_DOB_MODEL != "")
                addFormDataPart("dob", str_DOB_MODEL)
            if (attachmentIDs!!.size > 0)
                attachmentIDs!!.forEach {
                    addFormDataPart("portfolio[]", it)
                }

            if (userAccountModel!!.skills.specialities!!.size > 0)
                userAccountModel!!.skills.specialities.forEach {
                    addFormDataPart("specialities[]", it)
                }
            if (userAccountModel!!.skills.transportation!!.size > 0)
                userAccountModel!!.skills.transportation!!.forEach {
                    addFormDataPart("transportation[]", it)
                }

            if (userAccountModel!!.skills.language!!.size > 0)
                userAccountModel!!.skills.language!!.forEach {
                    addFormDataPart("language[]", it)
                }

            if (userAccountModel!!.skills.education!!.size > 0)
                userAccountModel!!.skills.education!!.forEach {
                    addFormDataPart("education[]", it)
                }
            if (userAccountModel!!.skills.experience!!.size > 0)
                userAccountModel!!.skills.experience!!.forEach {
                    addFormDataPart("experience[]", it)
                }

            //addFormDataPart("latitude", null)
            //addFormDataPart("longitude", null)
            if (sessionManager.userAccount.avatar != null)
                if (sessionManager.userAccount.avatar.id != null)
                    addFormDataPart("avatar", sessionManager.userAccount.avatar.id.toString())
            //addFormDataPart("cover", null)
        }.build()
        Log.d(TAG, "updateProfile: " + requestBody)
        call = ApiClient.getClientV2().uploadProfile("XMLHttpRequest", sessionManager.tokenType + " " + sessionManager.accessToken, requestBody)
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                hideProgressDialog()
                try {
                    val jsonObject = JSONObject(response.body()!!)
                    val jsonObject_user = jsonObject.getJSONObject("data")
                    val userAccountModel = UserAccountModel().getJsonToModel(jsonObject_user)
                    sessionManager.userAccount = userAccountModel
                    sessionManager.latitude = str_latitude
                    sessionManager.longitude = str_longitude
                    initDatePicker()
                    showSuccessToast(jsonObject.getString("message"), this@EditProfileActivity)
                    if (ProfileFragment.onProfileupdatelistener != null) {
                        ProfileFragment.onProfileupdatelistener!!.updateProfile()
                    }
                    onBackPressed()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    showToast("Something Went Wrong", this@EditProfileActivity)
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                hideProgressDialog()
                Timber.e(call.toString())
            }
        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun init() {
        initTabs()
        recyclerView!!.layoutManager = GridLayoutManager(this@EditProfileActivity, 4)
        recyclerView!!.addItemDecoration(SpacingItemDecoration(4, Tools.dpToPx(this@EditProfileActivity, 8), true))
        recyclerView!!.setHasFixedSize(true)
        //set data and list adapter
        adapter = AttachmentAdapterEditProfile(this@EditProfileActivity, attachmentArrayList, true)
        recyclerView!!.adapter = adapter
        adapter!!.setOnItemClickListener(this)
        txtBirthDate!!.setExtendedViewOnClickListener {
            val utcFormat = SimpleDateFormat("yyyy-MM-dd")
            var oldDate = Date()
            try {
                oldDate = utcFormat.parse(userAccountModel!!.dob)!!
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val datePickerBottomSheet = DatePickerBottomSheet(Date(oldDate.toString()).time)
            datePickerBottomSheet.dchange = this
            datePickerBottomSheet.show(this.supportFragmentManager, "")
        }
        initDatePicker()
        txtSuburb!!.setExtendedViewOnClickListener {
            val infoBottomSheet = SearchSuburbBottomSheet(this)
            infoBottomSheet.show(supportFragmentManager, null)
        }
        smallPlus!!.setOnClickListener {
            isImageProfile = false
            uploadableImage!!.showAttachmentImageBottomSheet(false)
        }
    }

    private fun initTabs() {
        llGeneral!!.visibility = View.VISIBLE
        llPinfo!!.visibility = View.GONE
        llPS!!.visibility = View.GONE
        txtGeneralInfo!!.setOnClickListener {
            llGeneral!!.visibility = View.VISIBLE
            llPinfo!!.visibility = View.GONE
            llPS!!.visibility = View.GONE
            txtGeneralInfo!!.setTextColor(getColor(R.color.colorAccent))
            txtPrivateInfo!!.setTextColor(getColor(R.color.N100))
            txtPortfolioSkills!!.setTextColor(getColor(R.color.N100))
            underTab1!!.background = ContextCompat.getDrawable(this, R.drawable.tab_line_selected)
            underTab2!!.background = ContextCompat.getDrawable(this, R.drawable.tab_line)
            underTab3!!.background = ContextCompat.getDrawable(this, R.drawable.tab_line)
        }
        txtPrivateInfo!!.setOnClickListener {
            llGeneral!!.visibility = View.GONE
            llPinfo!!.visibility = View.VISIBLE
            llPS!!.visibility = View.GONE
            txtGeneralInfo!!.setTextColor(getColor(R.color.N100))
            txtPrivateInfo!!.setTextColor(getColor(R.color.colorAccent))
            txtPortfolioSkills!!.setTextColor(getColor(R.color.N100))
            underTab1!!.background = ContextCompat.getDrawable(this, R.drawable.tab_line)
            underTab2!!.background = ContextCompat.getDrawable(this, R.drawable.tab_line_selected)
            underTab3!!.background = ContextCompat.getDrawable(this, R.drawable.tab_line)
        }
        txtPortfolioSkills!!.setOnClickListener {
            llGeneral!!.visibility = View.GONE
            llPinfo!!.visibility = View.GONE
            llPS!!.visibility = View.VISIBLE
            txtGeneralInfo!!.setTextColor(getColor(R.color.N100))
            txtPrivateInfo!!.setTextColor(getColor(R.color.N100))
            txtPortfolioSkills!!.setTextColor(getColor(R.color.colorAccent))
            underTab1!!.background = ContextCompat.getDrawable(this, R.drawable.tab_line)
            underTab2!!.background = ContextCompat.getDrawable(this, R.drawable.tab_line)
            underTab3!!.background = ContextCompat.getDrawable(this, R.drawable.tab_line_selected)
        }
    }

    private fun initDatePicker() {
        userAccountModel = sessionManager.userAccount
        var calendar = Calendar.getInstance()
        if (userAccountModel!!.getDob() != null) {
            calendar[TimeHelper.getYear(userAccountModel!!.getDob()), TimeHelper.getMonth(userAccountModel!!.getDob())] = TimeHelper.getDay(userAccountModel!!.getDob())
            year = calendar[Calendar.YEAR]
            month = calendar[Calendar.MONTH]
            day = calendar[Calendar.DAY_OF_MONTH]
        } else {
            calendar = Calendar.getInstance()
            calendar.add(Calendar.YEAR, -Constant.MIN_AGE_FOR_USE_APP)
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            year = calendar[Calendar.YEAR]
            month = calendar[Calendar.MONTH]
            day = calendar[Calendar.DAY_OF_MONTH]
        }

        calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -Constant.MIN_AGE_FOR_USE_APP)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        year = calendar[Calendar.YEAR]
        month = calendar[Calendar.MONTH]
        day = calendar[Calendar.DAY_OF_MONTH]


    }

    private val allUserProfileDetails: Unit
        get() {
            val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.URL_PROFILE + "/" + sessionManager.userAccount.id,
                    com.android.volley.Response.Listener { response: String? ->
                        Timber.e(response)
                        hideProgressDialog()
                        try {
                            val jsonObject = JSONObject(response!!)
                            Timber.e(jsonObject.toString())
                            if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                userAccountModel = UserAccountModel().getJsonToModel(jsonObject.getJSONObject("data"))
                                /*
                    * Add Button for empty attachment
                    * */setUpAllEditFields(userAccountModel)
                                attachmentArrayList = userAccountModel!!.getPortfolio()
                                // attachmentArrayList.add(new AttachmentModel());
                                Timber.e("%s", attachmentArrayList!!.size)
                                if (attachmentArrayList!!.size >= 0) {
                                    recyclerView!!.visibility = View.VISIBLE
                                    adapter!!.addItems(attachmentArrayList)
                                }
                            } else {
                                showToast("Something went wrong", this@EditProfileActivity)
                            }
                        } catch (e: JSONException) {
                            showToast("JSONException", this@EditProfileActivity)
                            Timber.e(e.toString())
                            e.printStackTrace()
                        }
                    },
                    com.android.volley.Response.ErrorListener { error: VolleyError ->
                        errorHandle1(error.networkResponse)
                        hideProgressDialog()
                    }) {
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
            val requestQueue = Volley.newRequestQueue(this@EditProfileActivity)
            requestQueue.add(stringRequest)
            Timber.e(stringRequest.url)
        }

    private fun setUpAllEditFields(userAccountModel: UserAccountModel?) {
        transportationSetUp(userAccountModel)
        languagesSetUp(userAccountModel)
        educationSetUp(userAccountModel)
        experienceSetUp(userAccountModel)
        specialitiesSetUp(userAccountModel)
        edtFirstName!!.seteContent(userAccountModel!!.fname)
        edtLastName!!.seteContent(userAccountModel.lname)
        edtLastName!!.seteContent(userAccountModel.lname)
        if (userAccountModel.mobile != null) {
            if (userAccountModel.mobile.length > 3) edtPhoneNumber!!.text = "0" + userAccountModel.mobile.substring(3)
        } else {
            edtPhoneNumber!!.seteVerifyVisible(true)
        }
        if (userAccountModel.mobileVerifiedAt == null) {
            edtPhoneNumber!!.seteVerifyVisible(true)
        }
        edtPhoneNumber!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length > 0) edtPhoneNumber!!.seteVerifyVisible(true) else edtPhoneNumber!!.seteVerifyVisible(false)
            }

            override fun afterTextChanged(s: Editable) {}
        })
        //edtPaymentId.setText(userAccountModel.get());
        if (userAccountModel.location != null) {
            txtSuburb!!.seteContent(userAccountModel.location)
            str_latitude = userAccountModel.latitude.toString()
            str_longitude = userAccountModel.longitude.toString()
        }
        edtTagline!!.seteContent(userAccountModel.tagline)
        edtAboutMe!!.seteContent(userAccountModel.about)
        edtBusinessNumber!!.seteContent(userAccountModel.business_number)
        edtEmailAddress!!.seteContent(userAccountModel.email)
        txtBirthDate!!.text = Tools.getDayMonthDateTimeFormat(userAccountModel.dob)
        if (userAccountModel.avatar != null) {
            Glide.with(imgAvatar!!).load(userAccountModel.avatar.thumbUrl).into(imgAvatar!!)
            //lytDeletePicture.setVisibility(View.VISIBLE);
        } else {
            //lytDeletePicture.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    private fun specialitiesSetUp(userAccountModel: UserAccountModel?) {
        if (userAccountModel!!.skills.specialities != null && userAccountModel.skills.specialities.size != 0) {
            txtSpecialities!!.text = "" + userAccountModel.skills.specialities.size
            txtSpecialities!!.visibility = View.VISIBLE
        } else {
            txtSpecialities!!.visibility = View.VISIBLE
            txtSpecialities!!.text = "0"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun experienceSetUp(userAccountModel: UserAccountModel?) {
        if (userAccountModel!!.skills.experience != null && userAccountModel.skills.experience.size != 0) {
            txtExperience!!.text = "" + userAccountModel.skills.experience.size
            txtExperience!!.visibility = View.VISIBLE
        } else {
            txtExperience!!.visibility = View.VISIBLE
            txtExperience!!.text = "0"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun educationSetUp(userAccountModel: UserAccountModel?) {
        if (userAccountModel!!.skills.education != null && userAccountModel.skills.education.size != 0) {
            txtEducation!!.text = "" + userAccountModel.skills.education.size
            txtEducation!!.visibility = View.VISIBLE
        } else {
            txtEducation!!.visibility = View.VISIBLE
            txtEducation!!.text = "0"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun languagesSetUp(userAccountModel: UserAccountModel?) {
        if (userAccountModel!!.skills.language != null && userAccountModel.skills.language.size != 0) {
            txtLanguages!!.text = "" + userAccountModel.skills.language.size
            txtLanguages!!.visibility = View.VISIBLE
        } else {
            txtLanguages!!.visibility = View.VISIBLE
            txtLanguages!!.text = "0"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun transportationSetUp(userAccountModel: UserAccountModel?) {
        if (userAccountModel!!.skills.transportation != null && userAccountModel.skills.transportation.size != 0) {
            txtTransportation!!.text = "" + userAccountModel.skills.transportation.size
            txtTransportation!!.visibility = View.VISIBLE
        } else {
            txtTransportation!!.visibility = View.generateViewId()
            txtTransportation!!.text = "0"
        }
    }

    private fun convertArrayToString(tag: ArrayList<String>): String {
        var str_tag = ""
        for (s in tag) {
            str_tag = if (str_tag == "") {
                s
            } else {
                String.format("%s, %s", str_tag, s)
            }
        }
        return str_tag
    }


    override fun onItemClick(view: View, obj: AttachmentModel, position: Int, action: String) {
        if (action.equals("add", ignoreCase = true)) {
            isImageProfile = false
            uploadableImage!!.showAttachmentImageBottomSheet(false)
        } else if (action.equals("delete", ignoreCase = true)) {
            deleteMediaInAttachment(position)
        }
    }

    private fun deleteMediaInAttachment(position: Int) {
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.DELETE, Constant.URL_PROFILE + "/portfolio/" + attachmentArrayList!![position].id,
                com.android.volley.Response.Listener { response: String? ->
                    Timber.e(response)
                    hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response!!)
                        Timber.e(jsonObject.toString())
                        attachmentArrayList!!.removeAt(position)
                        adapter!!.DeleteItem(position)
                        showToast("Portfolio Deleted", this@EditProfileActivity)
                    } catch (e: JSONException) {
                        Timber.e(e.toString())
                        e.printStackTrace()
                    }
                },
                com.android.volley.Response.ErrorListener { error: VolleyError ->
                    val networkResponse = error.networkResponse
                    if (networkResponse != null && networkResponse.data != null) {
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
                            val jsonObject_error = jsonObject.getJSONObject("error")
                            if (jsonObject_error.has("message")) {
                                showToast(jsonObject_error.getString("message"), this)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        showToast("Something Went Wrong", this@EditProfileActivity)
                    }
                    Timber.e(error.toString())
                    hideProgressDialog()
                }) {
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
        val requestQueue = Volley.newRequestQueue(this@EditProfileActivity)
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
    }

    private fun onViewClick() {
        var bundle = Bundle()

        rltBtnTransportation!!.setOnClickListener {
            val intent = Intent(this@EditProfileActivity, SkillsTagActivity::class.java)
            bundle.putStringArrayList(ConstantKey.SKILLS, userAccountModel!!.skills.transportation)
            bundle.putString(ConstantKey.TOOLBAR_TITLE, ConstantKey.TRANSPORTATION)
            bundle.putString(ConstantKey.TITLE, "Add your transportation")
            intent.putExtras(bundle)
            startActivityForResult(intent, 1)
        }
        rltBtnLanguages!!.setOnClickListener {
            intent = Intent(this@EditProfileActivity, SkillsTagActivity::class.java)
            bundle = Bundle()
            bundle.putStringArrayList(ConstantKey.SKILLS, userAccountModel!!.skills.language)
            bundle.putString(ConstantKey.TOOLBAR_TITLE, ConstantKey.LANGUAGE)
            bundle.putString(ConstantKey.TITLE, "Add your language")
            intent.putExtras(bundle)
            startActivityForResult(intent, 2)
        }
        rltBtnEducation!!.setOnClickListener {
            intent = Intent(this@EditProfileActivity, SkillsTagActivity::class.java)
            bundle = Bundle()
            bundle.putStringArrayList(ConstantKey.SKILLS, userAccountModel!!.skills.education)
            bundle.putString(ConstantKey.TOOLBAR_TITLE, ConstantKey.EDUCATION)
            bundle.putString(ConstantKey.TITLE, "Add your education")
            intent.putExtras(bundle)
            startActivityForResult(intent, 3)
        }
        rltBtnExperience!!.setOnClickListener {
            intent = Intent(this@EditProfileActivity, SkillsTagActivity::class.java)
            bundle = Bundle()
            bundle.putStringArrayList(ConstantKey.SKILLS, userAccountModel!!.skills.experience)
            bundle.putString(ConstantKey.TOOLBAR_TITLE, ConstantKey.EXPERIENCE)
            bundle.putString(ConstantKey.TITLE, "Add your occupation")
            intent.putExtras(bundle)
            startActivityForResult(intent, 4)
        }
        rltBtnSpecialities!!.setOnClickListener {
            intent = Intent(this@EditProfileActivity, SkillsTagActivity::class.java)
            bundle = Bundle()
            bundle.putStringArrayList(ConstantKey.SKILLS, userAccountModel!!.skills.specialities)
            bundle.putString(ConstantKey.TOOLBAR_TITLE, ConstantKey.SPECIALITIES)
            bundle.putString(ConstantKey.TITLE, "Add your certificate")
            intent.putExtras(bundle)
            startActivityForResult(intent, 5)
        }
        imgAvatar!!.setOnClickListener {
            isImageProfile = true
            uploadableImage!!.showAttachmentImageBottomSheet(true)
        }
        lytDeletePicture!!.setOnClickListener {
            MaterialAlertDialogBuilder(this@EditProfileActivity)
                    .setTitle("")
                    .setMessage("Remove profile photo?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog1: DialogInterface, _: Int ->
                        dialog1.dismiss()
                        removeProfilePicture()
                    }
                    .setNegativeButton("No") { dialog12: DialogInterface, _: Int ->
                        //  Action for 'NO' Button
                        dialog12.cancel()
                    }.show()
        }
        cardSaveProfile!!.setOnClickListener {
            if (validation()) updateProfile()
        }
    }

    private fun verifyPhone() {
        //it should work with Australian Numbers, format: +0* **** ****
        if (edtPhoneNumber!!.text.length != 10) {
            showToast("Please enter correct mobile number", this@EditProfileActivity)
            return
        }
        if (edtPhoneNumber!!.text.toString() == userAccountModel!!.mobile) {
            showToast("This mobile number is already registered.", this@EditProfileActivity)
            return
        }
        val intent = Intent(this, MobileVerificationActivity::class.java)
        intent.putExtra("phone_number", edtPhoneNumber!!.text.toString())
        startActivityForResult(intent, PHONE_VERIFICATION_REQUEST_CODE)
    }

    private fun uploadDataInPortfolioMediaApi(pictureFile: File) {
        showProgressDialog()
        val call: Call<String?>?
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile)
        val imageFile = MultipartBody.Part.createFormData("media", pictureFile.name, requestFile)
        call = ApiClient.getClient().getTaskTempAttachmentMediaData("XMLHttpRequest",
                sessionManager.tokenType + " " + sessionManager.accessToken, imageFile)
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                hideProgressDialog()
                Timber.e(response.toString())
                if (response.code() == HttpStatus.HTTP_VALIDATION_ERROR) {
                    showToast(response.message(), this@EditProfileActivity)
                    return
                }
                try {
                    val strResponse = response.body()
                    if (response.code() == HttpStatus.NOT_FOUND) {
                        showToast("not found", this@EditProfileActivity)
                        return
                    }
                    if (response.code() == HttpStatus.AUTH_FAILED) {
                        unauthorizedUser()
                        return
                    }
                    if (response.code() == HttpStatus.SUCCESS) {
                        Timber.e(strResponse)
                        val jsonObject = JSONObject(strResponse!!)
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
                            attachmentArrayList!!.add(attachment)

                        }
                        val updateAttachment = ArrayList(attachmentArrayList)
                        attachmentArrayList!!.clear()
                        attachmentArrayList!!.addAll(updateAttachment)
                        adapter!!.clearAll()
                        adapter!!.addItems(attachmentArrayList)
                        updateAttachment.clear()
                    } else {
                        showToast("Something went wrong", this@EditProfileActivity)
                    }
                } catch (e: JSONException) {
                    showToast("Something went wrong", this@EditProfileActivity)
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                hideProgressDialog()
                showToast("Something went wrong", this@EditProfileActivity)
                Timber.e(call.toString())
            }
        })
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        uploadableImage!!.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (requestCode == PLACE_SELECTION_REQUEST_CODE && resultCode == RESULT_OK) {
                txtSuburb!!.text = SuburbAutoComplete.getSuburbName(data)
                str_latitude = SuburbAutoComplete.getLatitude(data)
                str_longitude = SuburbAutoComplete.getLongitude(data)
            }
            if (requestCode == PHONE_VERIFICATION_REQUEST_CODE && resultCode == RESULT_OK) {
                allUserProfileDetails
            }
            val bundle = data.extras
            if (bundle != null) {
                when (requestCode) {
                    1 -> {
                        userAccountModel!!.skills.transportation = bundle.getStringArrayList(ConstantKey.SKILLS)
                        transportationSetUp(userAccountModel)
                    }
                    2 -> {
                        userAccountModel!!.skills.language = bundle.getStringArrayList(ConstantKey.SKILLS)
                        languagesSetUp(userAccountModel)
                    }
                    3 -> {
                        userAccountModel!!.skills.education = bundle.getStringArrayList(ConstantKey.SKILLS)
                        educationSetUp(userAccountModel)
                    }
                    4 -> {
                        userAccountModel!!.skills.experience = bundle.getStringArrayList(ConstantKey.SKILLS)
                        experienceSetUp(userAccountModel)
                    }
                    5 -> {
                        userAccountModel!!.skills.specialities = bundle.getStringArrayList(ConstantKey.SKILLS)
                        specialitiesSetUp(userAccountModel)
                    }
                    234 -> {
                        userAccountModel!!.portfolio = bundle.getParcelableArrayList(ConstantKey.ATTACHMENT)
                        adapter!!.clearAll()
                        attachmentArrayList!!.clear()
                        attachmentArrayList = userAccountModel!!.portfolio
                        /*
                             * include add button */adapter!!.addItems(attachmentArrayList)
                    }
                }
            }
        }
    }

    private fun uploadMedia(imageFile: File) {
        val uri = Uri.fromFile(imageFile)
        var bitmap: Bitmap? = null
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        imgAvatar!!.setImageBitmap(bitmap)
        if (!isImageProfile) {
            uploadDataInPortfolioMediaApi(imageFile)
        } else {
            uploadProfileAvtar(imageFile)
        }
    }

    private fun uploadProfileAvtar(pictureFile: File) {
        showProgressDialog()
        val call: Call<String?>?
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile)
        val imageFile = MultipartBody.Part.createFormData("media", pictureFile.name, requestFile)
        call = ApiClient.getClientV2().uploadProfilePicture("XMLHttpRequest", sessionManager.tokenType + " " + sessionManager.accessToken, imageFile)
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                hideProgressDialog()
                Timber.e(response.toString())
                if (response.code() == HttpStatus.HTTP_VALIDATION_ERROR) {
                    showToast(response.message(), this@EditProfileActivity)
                    return
                }
                try {
                    val strResponse = response.body()
                    if (response.code() == HttpStatus.NOT_FOUND) {
                        showToast("not found", this@EditProfileActivity)
                        return
                    }
                    if (response.code() == HttpStatus.AUTH_FAILED) {
                        unauthorizedUser()
                        return
                    }
                    if (response.code() == HttpStatus.SUCCESS) {
                        Timber.e(strResponse)
                        val jsonObject = JSONObject(strResponse!!)
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
                            sessionManager.userAccount.avatar = attachment
                            lytDeletePicture!!.visibility = View.VISIBLE
                            if (ProfileFragment.onProfileupdatelistener != null) {
                                ProfileFragment.onProfileupdatelistener!!.updatedSuccesfully(attachment.thumbUrl)
                            }
                            if (DashboardActivity.onProfileupdatelistenerSideMenu != null) {
                                DashboardActivity.onProfileupdatelistenerSideMenu!!.updatedSuccesfully(attachment.thumbUrl)
                            }
                        }
                        //  adapter.notifyItemRangeInserted(0,attachmentArrayList.size());
                        // showToast("attachment added", AttachmentActivity.this);
                    } else {
                        showToast("Something went wrong", this@EditProfileActivity)
                    }
                } catch (e: JSONException) {
                    showToast("Something went wrong", this@EditProfileActivity)
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                hideProgressDialog()
                Timber.e(call.toString())
            }
        })
    }

    private fun removeProfilePicture() {
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.DELETE, Constant.URL_PROFILE + Constant.URL_REMOVE_AVTAR,
                com.android.volley.Response.Listener { response: String? ->
                    Timber.e(response)
                    hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response!!)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                showSuccessToast("Profile Picture has been  Deleted", this@EditProfileActivity)
                                imgAvatar!!.setImageResource(R.drawable.ic_circle_logo)
                                lytDeletePicture!!.visibility = View.GONE
                                if (ProfileFragment.onProfileupdatelistener != null) {
                                    ProfileFragment.onProfileupdatelistener!!.updatedSuccesfully("")
                                }
                                if (DashboardActivity.onProfileupdatelistenerSideMenu != null) {
                                    DashboardActivity.onProfileupdatelistenerSideMenu!!.updatedSuccesfully("")
                                }
                            } else {
                                showToast("Something went Wrong", this@EditProfileActivity)
                            }
                        }
                    } catch (e: JSONException) {
                        Timber.e(e.toString())
                        e.printStackTrace()
                    }
                },
                com.android.volley.Response.ErrorListener { error: VolleyError ->
                    val networkResponse = error.networkResponse
                    if (networkResponse != null && networkResponse.data != null) {
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
                            val jsonObject_error = jsonObject.getJSONObject("error")
                            if (jsonObject_error.has("message")) {
                                showToast(jsonObject_error.getString("message"), this)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        showToast("Something Went Wrong", this@EditProfileActivity)
                    }
                    Timber.e(error.toString())
                    hideProgressDialog()
                }) {
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
        val requestQueue = Volley.newRequestQueue(this@EditProfileActivity)
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
    }

    override fun clickOnSearchedLoc(location: Feature) {
        txtSuburb!!.text = location.place_name_en
        str_latitude = location.geometry!!.coordinates!![1].toString()
        str_longitude = location.geometry.coordinates!![0].toString()
    }

    override fun onDateChange(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val temp = Date(System.currentTimeMillis())
        val today = Calendar.getInstance()
        today.time = temp
        val currentYear = today[Calendar.YEAR]
        if (currentYear - 13 < year) {
            showToast("Your age must be over 13", this)
            return
        }
        val dob = Calendar.getInstance()
        dob[year, monthOfYear] = dayOfMonth
        str_DOB_MODEL = year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth
        this.year = year
        month = monthOfYear
        day = dayOfMonth
        txtBirthDate!!.text = dob.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())!! + " " + dayOfMonth + ", " + year
    }

    companion object {
        const val PHONE_VERIFICATION_REQUEST_CODE = 500
    }
}