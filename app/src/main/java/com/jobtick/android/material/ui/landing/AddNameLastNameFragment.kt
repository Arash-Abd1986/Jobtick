package com.jobtick.android.material.ui.landing

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.installations.InstallationTokenResult
import com.jobtick.android.R
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.Helper
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.isLetter
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class AddNameLastNameFragment : Fragment() {

    private lateinit var activity: OnboardingActivity
    private lateinit var next: MaterialButton
    private lateinit var fname: TextInputLayout
    private lateinit var lname: TextInputLayout
    private lateinit var txtError: MaterialTextView
    private lateinit var sessionManagerA: SessionManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_name_last_name, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVars()
    }


    private fun initVars() {
        sessionManagerA = SessionManager(requireContext())

        activity = (requireActivity() as OnboardingActivity)
        next = requireView().findViewById(R.id.btn_next)
        fname = requireView().findViewById(R.id.name)
        lname = requireView().findViewById(R.id.lname)
        txtError = requireView().findViewById(R.id.error)

        next.setOnClickListener {
            resetError()
            if (checkValidation()) {
                updateProfile()
            }
        }
    }

    private fun updateProfile() {
        activity.showProgressDialog()
        val call: Call<String?>?
        activity.showProgressDialog()
        Helper.closeKeyboard(requireActivity())

        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM).apply {
            addFormDataPart("fname", fname.editText?.text.toString())
            addFormDataPart("lname", lname.editText?.text.toString())

        }.build()
        call = ApiClient.getClientV2(sessionManagerA).uploadProfile(
            "XMLHttpRequest",
            requestBody
        )
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                activity.hideProgressDialog()
                try {
                    val jsonObject = JSONObject(response.body()!!)
                    val jsonObject_user = jsonObject.getJSONObject("data")
                    val userAccountModel = UserAccountModel().getJsonToModel(jsonObject_user)
                    sessionManagerA.userAccount = userAccountModel
                    val intent = Intent(requireActivity(), DashboardActivity::class.java)
                    startActivity(intent)
                    //showSuccessToast(jsonObject.getString("message"), this@EditProfileActivity)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    //showToast("Something Went Wrong", this@EditProfileActivity)
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                activity.hideProgressDialog()
                Timber.e(call.toString())
            }
        })
    }

    private fun resetError() {
        fname.error = ""
        lname.error = ""
        txtError.visibility = View.GONE
    }

    private fun setError(error: String) {
        txtError.visibility = View.VISIBLE
        txtError.text = error
    }

    private fun checkValidation(): Boolean {
        when {
            fname.editText?.text.isNullOrEmpty() && lname.editText?.text.isNullOrEmpty() -> {
                setError("Please enter your first name and last name")
                return false
            }
            fname.editText?.text.isNullOrEmpty() -> {
                setError("Please enter your first name", fname)
                return false
            }
            !fname.editText?.text.toString().isLetter() -> {
                setError("Your first name should only contain letters", fname)
                return false
            }
            lname.editText?.text.isNullOrEmpty() -> {
                setError("Please enter your last name", lname)
                return false
            }
            !lname.editText?.text.toString().isLetter() -> {
                setError("Your last name should only contain letters", lname)
                return false
            }
        }
        return true
    }

    private fun setError(error: String, txtInput: TextInputLayout) {
        val errorDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_error)
        val ss = SpannableString("    $error\n")
        errorDrawable!!.setBounds(0, 0, errorDrawable.intrinsicWidth, errorDrawable.intrinsicHeight)
        val span = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ImageSpan(errorDrawable, ImageSpan.ALIGN_CENTER)
        } else {
            ImageSpan(errorDrawable, ImageSpan.ALIGN_BOTTOM)
        }
        ss.setSpan(span, 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        txtInput.error = ss
    }

    private val token: String
        get() {
            val token = arrayOf("")
            FirebaseInstallations.getInstance().getToken(true)
                .addOnCompleteListener { task: Task<InstallationTokenResult> ->
                    if (!task.isSuccessful) {
                        return@addOnCompleteListener
                    }

                    // Get new Instance ID token
                    token[0] = task.result.token
                }
            return token[0]
        }


}