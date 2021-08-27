package com.jobtick.android.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.adapers.SuburbSearchAdapter.SubClickListener
import com.jobtick.android.fragments.SearchSuburbBottomSheet
import com.jobtick.android.fragments.SelectRoleBottomSheet
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.models.response.searchsuburb.Feature
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.Helper
import com.jobtick.android.utils.SuburbAutoComplete
import com.jobtick.android.widget.ExtendedEntryText
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class CompleteRegistrationActivity : ActivityBase(), SelectRoleBottomSheet.NoticeListener, SubClickListener {

    var toolbar: Toolbar? = null
    var edtFirstName: ExtendedEntryText? = null
    var edtLastName: ExtendedEntryText? = null
    var lytBtnCompleteRegistration: MaterialButton? = null
    var suburb: ExtendedEntryText? = null
    private val PLACE_SELECTION_REQUEST_CODE = 1
    private var strLatitude: String? = null
    private var strLongitude: String? = null
    private var strFname: String? = null
    private var strLname: String? = null
    private var strSuburb: String? = null
    var context: Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_registration)
        setIDs()
        onViewClick()
        context = this@CompleteRegistrationActivity
        initToolbar()
        suburb!!.setExtendedViewOnClickListener {
            val infoBottomSheet = SearchSuburbBottomSheet(this)
            infoBottomSheet.show(supportFragmentManager, null)
        }
    }

    private fun setIDs() {
        toolbar = findViewById(R.id.toolbar)
        edtFirstName = findViewById(R.id.first_name)
        edtLastName = findViewById(R.id.last_name)
        lytBtnCompleteRegistration = findViewById(R.id.lyt_btn_complete_registration)
        suburb = findViewById(R.id.suburb)
    }

    private fun initToolbar() {
        toolbar!!.setNavigationIcon(R.drawable.ic_back)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = "Create Profile"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val AuthActivity = Intent(this, AuthActivity::class.java)
        startActivity(AuthActivity)
        finish()
    }

    private fun profileUpdate(fname: String?, lname: String?, suburb: String?, role: String?) {
        showProgressDialog()
        val count = intArrayOf(0)
        Helper.closeKeyboard(this)
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Constant.URL_USER_PROFILE_INFO,
                Response.Listener { response: String? ->
                    hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response)
                        val jsonObjectUser = jsonObject.getJSONObject("data")
                        val userAccountModel = UserAccountModel().getJsonToModel(jsonObjectUser)
                        sessionManager.userAccount = userAccountModel
                        sessionManager.login = true
                        sessionManager.latitude = strLatitude
                        sessionManager.longitude = strLongitude
                        val intent = Intent(this@CompleteRegistrationActivity, OnboardActivity::class.java)
                        intent.putExtra("as", role)
                        startActivity(intent)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    val networkResponse = error.networkResponse
                    if (networkResponse?.data != null) {
                        val jsonError = String(networkResponse.data)
                        // Print Error!
                        try {
                            val jsonObject = JSONObject(jsonError)
                            val jsonObjectError = jsonObject.getJSONObject("error")
                            val message = jsonObjectError.getString("message")
                            showToast(message, context)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        showToast("Something Went Wrong", context)
                    }
                    Timber.e(error.toString())
                    hideProgressDialog()
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["X-Requested-With"] = "XMLHttpRequest"
                map1["Authorization"] = "Bearer " + sessionManager.accessToken
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }

            override fun getParams(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["fname"] = fname!!
                map1["lname"] = lname!!
                map1["role_as"] = role!!
                map1["location"] = suburb!!
                map1["latitude"] = strLatitude!!
                map1["longitude"] = strLongitude!!
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun validation(): Boolean {
        when {
            TextUtils.isEmpty(edtFirstName!!.text) -> {
                edtFirstName!!.setError("Please enter first name")
                return false
            }
            edtFirstName!!.text.length < 3 -> {
                edtFirstName!!.setError("Please enter the first name correctly")
                return false
            }
            TextUtils.isEmpty(edtLastName!!.text) -> {
                edtLastName!!.setError("Please enter last name")
                return false
            }
            edtLastName!!.text.length < 3 -> {
                edtLastName!!.setError("Please enter the last name correctly")
                return false
            }
            TextUtils.isEmpty(suburb!!.text) -> {
                suburb!!.setError("Please enter suburb")
                return false
            }
            else -> return true
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_SELECTION_REQUEST_CODE && resultCode == RESULT_OK) {
            Helper.closeKeyboard(this)
            suburb!!.text = SuburbAutoComplete.getSuburbName(data)
            strLatitude = SuburbAutoComplete.getLatitude(data)
            strLongitude = SuburbAutoComplete.getLongitude(data)
        }
    }

    private fun onViewClick() {
        lytBtnCompleteRegistration!!.setOnClickListener {
            if (validation()) {
                strFname = edtFirstName!!.text.trim { it <= ' ' }
                strLname = edtLastName!!.text.trim { it <= ' ' }
                strSuburb = suburb!!.text.trim { it <= ' ' }
                openSelectRoleBottomSheet()
            }
        }
    }

    private fun openSelectRoleBottomSheet() {
        val roleBottomSheet = SelectRoleBottomSheet()
        roleBottomSheet.show(supportFragmentManager, "")
    }

    override fun onGetStartedClick(role: String?) {
        profileUpdate(strFname, strLname, strSuburb, role)
    }

    override fun clickOnSearchedLoc(location: Feature) {
        Helper.closeKeyboard(this)
        suburb!!.text = location.place_name_en
        strLatitude = location.geometry!!.coordinates!![1].toString()
        strLongitude = location.geometry.coordinates!![0].toString()
    }
}