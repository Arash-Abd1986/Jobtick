package com.jobtick.android.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.HttpStatus
import com.jobtick.android.viewmodel.ChangePassViewModel
import com.jobtick.android.widget.ExtendedEntryText
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class ChangePasswordActivity : ActivityBase(), TextWatcher {
    private var toolbar: MaterialToolbar? = null
    private var oldPassword: ExtendedEntryText? = null
    private var newPassword: ExtendedEntryText? = null
    private var btnChangePassword: MaterialButton? = null
    private lateinit var changePassViewModel: ChangePassViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        initIDS()
        initToolbar()
        initVM()
        btnChangePassword!!.isEnabled = false
        oldPassword!!.addTextChangedListener(this)
        newPassword!!.addTextChangedListener(this)
    }

    private fun initIDS() {
        toolbar = findViewById(R.id.toolbar)
        oldPassword = findViewById(R.id.old_password)
        newPassword = findViewById(R.id.new_password)
        btnChangePassword = findViewById(R.id.btn_change_password)
        btnChangePassword!!.setOnClickListener {
            if (validation()) changePassword()
        }
    }

    private fun initToolbar() {
        toolbar!!.setNavigationIcon(R.drawable.ic_back)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Change Password"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun validation(): Boolean {
        return when {
            TextUtils.isEmpty(oldPassword!!.text.trim { it <= ' ' }) -> {
                oldPassword!!.setError("Enter your old password")
                false
            }
            TextUtils.isEmpty(newPassword!!.text.trim { it <= ' ' }) -> {
                newPassword!!.setError("Enter new password")
                false
            }
            oldPassword!!.text.length < 8 -> {
                oldPassword!!.setError("Password must be 8 character or more")
                false
            }
            else -> true
        }
    }

    private fun changePassword() {
        showProgressDialog()
        val strOldPassword = oldPassword!!.text.trim { it <= ' ' }
        val strNewPassword = newPassword!!.text.trim { it <= ' ' }
        changePassViewModel.changePass(sessionManager!!.accessToken, Volley.newRequestQueue(this), strOldPassword, strNewPassword)
    }

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun afterTextChanged(editable: Editable) {
        val enabled = oldPassword!!.text.isNotEmpty() && newPassword!!.text.isNotEmpty()
        btnChangePassword!!.isEnabled = enabled
    }

    private fun initVM() {
        changePassViewModel = ViewModelProvider(this).get(ChangePassViewModel::class.java)
        changePassViewModel.changePassResponse().observe(this) {
            hideProgressDialog()
            try {

                if (it.has("success") && !it.isNull("success")) {
                    if (it.getBoolean("success")) {
                        showSuccessToast("Password Changed Successfully !", this)
                    }
                }
            } catch (e: JSONException) {
                Timber.e(e.toString())
                e.printStackTrace()
            }
        }
        changePassViewModel.getError2().observe(this, androidx.lifecycle.Observer {
            this.showToast("Something went wrong", this)

        })
        changePassViewModel.getError().observe(this, androidx.lifecycle.Observer { networkResponse ->
            if (networkResponse?.data != null) {
                val jsonError = String(networkResponse.data)
                // Print Error!
                Timber.e(jsonError)
                if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                    unauthorizedUser()
                    hideProgressDialog()
                    return@Observer
                }
                try {
                    val jsonObject = JSONObject(jsonError)
                    val jsonObjectError = jsonObject.getJSONObject("error")
                    if (jsonObjectError.has("message")) {
                        showToast(jsonObjectError.getString("message"), this@ChangePasswordActivity)
                    }
                    if (jsonObjectError.has("errors")) {
                        val jsonObjectErrors = jsonObjectError.getJSONObject("errors")
                        if (jsonObjectErrors.has("old_password")) {
                            val jsonArrayMobile = jsonObjectErrors.getJSONArray("old_password")
                            val oldPassword1 = jsonArrayMobile.getString(0)
                            oldPassword!!.setError(oldPassword1)
                        }
                        if (jsonObjectErrors.has("new_password")) {
                            val jsonArrayMobile = jsonObjectErrors.getJSONArray("new_password")
                            val newPassword1 = jsonArrayMobile.getString(0)
                            newPassword!!.setError(newPassword1)
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                showToast("Something Went Wrong", this@ChangePasswordActivity)
            }
            hideProgressDialog()
        })
    }
}