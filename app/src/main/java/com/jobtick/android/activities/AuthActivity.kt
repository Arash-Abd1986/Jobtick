package com.jobtick.android.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.facebook.AccessToken
import com.facebook.BuildConfig
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.installations.InstallationTokenResult
import com.jobtick.android.R
import com.jobtick.android.fragments.ForgotPassword1Fragment
import com.jobtick.android.fragments.ForgotPassword2Fragment
import com.jobtick.android.fragments.ForgotPassword3Fragment
import com.jobtick.android.fragments.SignInFragment
import com.jobtick.android.fragments.SignUpFragment
import com.jobtick.android.fragments.VerifyAccountFragment
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.FireBaseEvent
import com.jobtick.android.utils.FireBaseEvent.EventValue
import com.jobtick.android.utils.Helper
import com.jobtick.android.utils.SessionManager
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException

open class AuthActivity : ActivityBase() {
    val DEVICE = "Android"
    var signInFragment: SignInFragment? = null
    var verifyAccountFragment: VerifyAccountFragment? = null
    var authLayout: FrameLayout? = null
    var callbackManager: CallbackManager? = null
    var mGoogleSignInClient: GoogleSignInClient? = null
    private lateinit var sessionManagerA: SessionManager
    var forgotPassword1Fragment: ForgotPassword1Fragment? = null
    var signUpFragment: SignUpFragment? = null
    private lateinit var editTextError: EditTextError
    private lateinit var onResendOtp: OnResendOtp
    private var fireBaseEvent: FireBaseEvent? = null
    fun setEditTextError(editTextError: EditTextError?) {
        this.editTextError = editTextError!!
    }

    fun forgotPasswordSpecialVerification(email: String, otp: String) {
        showProgressDialog()
        Helper.closeKeyboard(this)
        val stringRequest: StringRequest =
            object : StringRequest(
                Method.POST, Constant.URL_RESET_PASSWORD_VERIFY_OTP,
                Response.Listener { response: String? ->
                    hideProgressDialog()
                    try {
                        val bundle = Bundle()
                        val fragment: Fragment = ForgotPassword3Fragment()
                        val ft = supportFragmentManager.beginTransaction()
                        ft.replace(R.id.auth_layout, fragment).addToBackStack(fragment.toString())
                        bundle.putString("email", email)
                        bundle.putString("otp", otp)
                        fragment.arguments = bundle
                        ft.commit()
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
                            if (message.equals("unauthorized", ignoreCase = true)) {
                                val fragment: Fragment = SignInFragment()
                                val ft = supportFragmentManager.beginTransaction()
                                ft.replace(R.id.container, fragment)
                                ft.commit()
                            }
                            showToast(message, this@AuthActivity)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            showToast("Something Went Wrong", this@AuthActivity)
                        }
                    } else {
                        showToast("Something Went Wrong", this@AuthActivity)
                    }
                    hideProgressDialog()
                }
            ) {
                override fun getHeaders(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["Content-Type"] = "application/x-www-form-urlencoded"
                    map1["X-Requested-With"] = "XMLHttpRequest"
                    map1["Version"] = BuildConfig.VERSION_CODE.toString()
                    return map1
                }

                override fun getParams(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["email"] = email
                    map1["otp"] = otp
                    return map1
                }
            }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            0, -1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    fun resetPassword(email: String, otp: String, new_password: String) {
        showProgressDialog()
        Helper.closeKeyboard(this)
        val stringRequest: StringRequest =
            object : StringRequest(
                Method.POST, Constant.URL_FORGOT_PASSWORD,
                Response.Listener { response: String? ->
                    hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response!!)
                        val jsonObjectData = jsonObject.getJSONObject("data")
                        sessionManagerA.accessToken = jsonObjectData.getString("access_token")
                        sessionManagerA.tokenType = jsonObjectData.getString("token_type")
                        val jsonObjectUser = jsonObjectData.getJSONObject("user")
                        val userAccountModel = UserAccountModel().getJsonToModel(jsonObjectUser)
                        sessionManagerA.userAccount = userAccountModel
                        val intent = Intent(this@AuthActivity, AuthActivity::class.java)
                        intent.putExtra("type", "Signin")
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    } catch (e: JSONException) {
                        Timber.e(e.toString())
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    val networkResponse = error.networkResponse
                    if (networkResponse?.data != null) {
                        val jsonError = String(networkResponse.data)
                        // Print Error!
                        Timber.e(jsonError)
                        try {
                            val jsonObject = JSONObject(jsonError)
                            val jsonObjectError = jsonObject.getJSONObject("error")
                            val message = jsonObjectError.getString("message")
                            if (message.equals("unauthorized", ignoreCase = true)) {
                                val fragment: Fragment = SignInFragment()
                                switchContent(fragment)
                            }
                            showToast(message, this@AuthActivity)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            showToast("Something Went Wrong", this@AuthActivity)
                        }
                    } else {
                        showToast("Something Went Wrong", this@AuthActivity)
                    }
                    hideProgressDialog()
                }
            ) {
                override fun getHeaders(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["Content-Type"] = "application/x-www-form-urlencoded"
                    map1["X-Requested-With"] = "XMLHttpRequest"
                    map1["Version"] = BuildConfig.VERSION_CODE.toString()
                    return map1
                }

                override fun getParams(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["email"] = email
                    map1["otp"] = otp
                    map1["new_password"] = new_password
                    return map1
                }
            }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            0, -1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        val fm = supportFragmentManager
        Timber.tag("back stack entry").d(Integer.toString(fm.backStackEntryCount))
        if (fm.backStackEntryCount > 1) {
            fm.popBackStack()
            return
        }
        if (doubleBackToExitPressedOnce) {
            finish()
        } else {
            doubleBackToExitPressedOnce = true
            Toast.makeText(
                this, "Press back button again to exit",
                Toast.LENGTH_SHORT
            ).show()
            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 3000)
        }
    }

    interface EditTextError {
        fun onEmailError(emailError: String?)
        fun onPasswordError(passwordError: String?)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        setIDs()
        sessionManagerA = SessionManager(this)
        signInFragment = SignInFragment()
        verifyAccountFragment = VerifyAccountFragment()
        forgotPassword1Fragment = ForgotPassword1Fragment()
        signUpFragment = SignUpFragment()
        if (intent != null && intent.extras != null) {
            if (intent.hasExtra("type")) {
                if (intent.extras!!.getString("type") == "Signin") {
                    signInFragment = SignInFragment()
                    switchContent(signInFragment)
                } else {
                    signUpFragment = SignUpFragment()
                    switchContent(signUpFragment)
                }
            } else {
                switchContent(signInFragment)
            }
        } else {
            switchContent(signInFragment)
        }
        // switchContent(verifyAccountFragment);
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    // App code
                    val loggedIn = AccessToken.getCurrentAccessToken() == null
                    facebookGetRequiredParameter(loginResult)
                }

                override fun onCancel() {
                }

                override fun onError(exception: FacebookException) {
                    exception.printStackTrace()
                }
            }
        )
        init()
        fireBaseEvent = FireBaseEvent.getInstance(applicationContext)
    }

    private fun setIDs() {
        authLayout = findViewById(R.id.auth_layout)
    }

    protected fun facebookGetRequiredParameter(loginResult: LoginResult) {
        showProgressDialog()
        val request = GraphRequest.newMeRequest(
            loginResult.accessToken
        ) { `object`: JSONObject?, response: GraphResponse? ->
            // Application code
            try {
                var email = ""
                var firstName: String? = ""
                var lastName: String? = ""
                if (`object`!!.has("email")) email = `object`.getString("email")
                if (`object`.has("first_name")) firstName = `object`.getString("first_name")
                if (`object`.has("last_name")) lastName = `object`.getString("last_name")
                fbSubmitData(loginResult.accessToken.token, email, firstName, lastName)
            } catch (e: JSONException) {
                e.printStackTrace()
                hideProgressDialog()
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,email,first_name,last_name,gender")
        request.parameters = parameters
        request.executeAsync()
    }

    protected fun fbSubmitData(
        str_access_token: String,
        str_email: String,
        str_fname: String?,
        str_lname: String?
    ) {
        val strFcmToken = token
        val strDeviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val strDevice = "Android"
        Helper.closeKeyboard(this)
        val stringRequest: StringRequest =
            object : StringRequest(
                Method.POST, Constant.URL_SIGNIN_FACEBOOK,
                Response.Listener { response: String? ->
                    Timber.e(response)
                    hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response!!)
                        Timber.e(jsonObject.toString())
                        val jsonObjectData = jsonObject.getJSONObject("data")
                        sessionManagerA.accessToken = jsonObjectData.getString("access_token")
                        sessionManagerA.tokenType = jsonObjectData.getString("token_type")
                        val jsonObjectUser = jsonObjectData.getJSONObject("user")
                        val userAccountModel = UserAccountModel().getJsonToModel(jsonObjectUser)
                        sessionManagerA.userAccount = userAccountModel
                        if (fromSignUp) fireBaseEvent!!.sendEvent(
                            FireBaseEvent.Event.SIGN_UP,
                            FireBaseEvent.EventType.API_RESPOND_SUCCESS, EventValue.SIGN_UP_FACEBOOK
                        ) else fireBaseEvent!!.sendEvent(
                            FireBaseEvent.Event.LOGIN,
                            FireBaseEvent.EventType.API_RESPOND_SUCCESS, EventValue.LOGIN_FACEBOOK
                        )
                        proceedToCorrectActivity(userAccountModel)
                    } catch (e: JSONException) {
                        Timber.e(e.toString())
                        FirebaseCrashlytics.getInstance().recordException(e)
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    val networkResponse = error.networkResponse
                    if (networkResponse?.data != null) {
                        val jsonError = String(networkResponse.data)
                        // Print Error!
                        Timber.e(jsonError)
                        FirebaseCrashlytics.getInstance().recordException(error)
                        try {
                            val jsonObject = JSONObject(jsonError)
                            val jsonObjectError = jsonObject.getJSONObject("error")
                            if (jsonObjectError.has("message")) {
                                showToast(jsonObjectError.getString("message"), this@AuthActivity)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    Timber.e(error.toString())
                    hideProgressDialog()
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["Content-Type"] = "application/x-www-form-urlencoded"
                    map1["X-Requested-With"] = "XMLHttpRequest"
                    map1["Version"] = BuildConfig.VERSION_CODE.toString()
                    return map1
                }

                override fun getParams(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    // TODO: due we direct user to complete profile page, we ignore str_fname and str_lname
                    map1["fname"] = "Jobtick"
                    map1["lname"] = "User"
                    map1["email"] = str_email
                    map1["device_token"] = strDeviceId
                    map1["device_type"] = strDevice
                    map1["fcm_token"] = strFcmToken
                    map1["access_token"] = str_access_token
                    map1["latitude"] = sessionManagerA.latitude
                    map1["longitude"] = sessionManagerA.longitude
                    return map1
                }
            }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            0, -1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    fun switchContent(fragment: Fragment?) {
        val fragmentManager = supportFragmentManager
        while (fragmentManager.popBackStackImmediate());
        if (fragment != null) {
            val transaction = fragmentManager
                .beginTransaction().setCustomAnimations(
                    android.R.anim.fade_in, android.R.anim.fade_out,
                    android.R.anim.fade_in, android.R.anim.fade_out
                )
            // Replace whatever is in the content_fragment view with this fragment
            transaction.replace(R.id.auth_layout, fragment).addToBackStack(fragment.toString())

            // Commit the transaction
            transaction.commit()
        }
    }

    protected fun init() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.clientId))
            .requestEmail()
            .build()
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mGoogleSignInClient!!.signOut()
            .addOnCompleteListener(this) {
                // ...
            }

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        /*  GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null)
            signInUpdateUI(account);*/
    }

    var fromSignUp = false
    fun signInWithGoogle(fromSignUp: Boolean) {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
        this.fromSignUp = fromSignUp
    }

    fun facebookLogin(fromSignUp: Boolean) {
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
        this.fromSignUp = fromSignUp
    }

    @SuppressLint("HardwareIds")
    fun login(email: String?, password: String?) {

        showProgressDialog()
        val strFcmToken = token
        val strDeviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val strDevice = "Android"
        Helper.closeKeyboard(this)
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, Constant.URL_SIGNIN,
            Response.Listener { response: String? ->
                Timber.e(response)
                hideProgressDialog()
                try {
                    val jsonObject = JSONObject(response!!)
                    Timber.e(jsonObject.toString())
                    val jsonObjectData = jsonObject.getJSONObject("data")
                    sessionManagerA.accessToken = jsonObjectData.getString("access_token")
                    sessionManagerA.tokenType = jsonObjectData.getString("token_type")
                    val jsonObjectUser = jsonObjectData.getJSONObject("user")
                    val userAccountModel = UserAccountModel().getJsonToModel(jsonObjectUser)
                    sessionManagerA.userAccount = userAccountModel
                    fireBaseEvent!!.sendEvent(
                        FireBaseEvent.Event.LOGIN,
                        FireBaseEvent.EventType.API_RESPOND_SUCCESS, EventValue.LOGIN_NORMAL
                    )
                    proceedToCorrectActivity(userAccountModel)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    showToast("Something Went Wrong", this@AuthActivity)
                }
            },
            Response.ErrorListener { error: VolleyError ->
                hideProgressDialog()
                val networkResponse = error.networkResponse
                if (networkResponse?.data != null) {
                    val jsonError = String(networkResponse.data)
                    // Print Error!
                    try {
                        val jsonObject = JSONObject(jsonError)
                        val jsonObjectError = jsonObject.getJSONObject("error")
                        if (jsonObjectError.has("error_code")) {
                            if (jsonObjectError.getInt("error_code") == 1002) {
                                if (jsonObjectError.has("message")) showToast(
                                    jsonObjectError.getString(
                                        "message"
                                    ),
                                    this
                                )
                                return@ErrorListener
                            }
                        }
                        if (jsonObjectError.has("errors")) {
                            val jsonObjectErrors = jsonObjectError.getJSONObject("errors")
                            var errorEmail: String? = null
                            var errorPassword: String? = null
                            when {
                                jsonObjectErrors.has("email") -> {
                                    val jsonArrayMobile = jsonObjectErrors.getJSONArray("email")
                                    errorEmail = jsonArrayMobile.getString(0)
                                    showToast(errorEmail, this)
                                    editTextError.onEmailError(errorEmail)
                                }
                                jsonObjectErrors.has("password") -> {
                                    val jsonArrayMobile = jsonObjectErrors.getJSONArray("password")
                                    errorPassword = jsonArrayMobile.getString(0)
                                    showToast(errorPassword, this)
                                    editTextError.onPasswordError(errorEmail)
                                }
                                jsonObjectErrors.has("device_token") -> {
                                    val jsonArrayDeviceToken =
                                        jsonObjectErrors.getJSONArray("device_token")
                                    showToast(jsonArrayDeviceToken.getString(0), this@AuthActivity)
                                }
                                jsonObjectErrors.has("device_type") -> {
                                    val jsonArrayDeviceType =
                                        jsonObjectErrors.getJSONArray("device_type")
                                    showToast(jsonArrayDeviceType.getString(0), this@AuthActivity)
                                }
                                jsonObjectErrors.has("fcm_token") -> {
                                    val jsonArrayFcmToken =
                                        jsonObjectErrors.getJSONArray("fcm_token")
                                    showToast(jsonArrayFcmToken.getString(0), this@AuthActivity)
                                }
                                jsonObjectErrors.has("latitude") -> {
                                    val jsonArrayLatitude =
                                        jsonObjectErrors.getJSONArray("latitude")
                                    showToast(jsonArrayLatitude.getString(0), this@AuthActivity)
                                }
                                jsonObjectErrors.has("longitude") -> {
                                    val jsonArrayLongitude =
                                        jsonObjectErrors.getJSONArray("longitude")
                                    showToast(jsonArrayLongitude.getString(0), this@AuthActivity)
                                }
                            }
                        } else {
                            val message = jsonObjectError.getString("message")
                            showToast(message, this@AuthActivity)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    showToast("Something Went Wrong", this@AuthActivity)
                }
                hideProgressDialog()
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["X-Requested-With"] = "XMLHttpRequest"
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }

            override fun getParams(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                if (email != null) map1["email"] = email
                if (password != null) map1["password"] = password
                if (strDeviceId != null) map1["device_token"] = strDeviceId
                if (strDevice != null) map1["device_type"] = strDevice
                if (strFcmToken != null) map1["fcm_token"] = strFcmToken
                //                if (sessionManagerA.getLatitude() != null)
//                    map1.put("latitude", sessionManagerA.getLatitude());
//                if (sessionManagerA.getLongitude() != null)
//                    map1.put("longitude", sessionManagerA.getLongitude());
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            0, -1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    // Get new Instance ID token
    private val token: String
        private get() {
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

    private fun proceedToCorrectActivity(userAccountModel: UserAccountModel) {
        val intent: Intent
        if (userAccountModel.account_status.isBasic_info) {
            intent = Intent(this, DashboardActivity::class.java)
            sessionManagerA.userAccount = userAccountModel
            sessionManagerA.latitude = userAccountModel.latitude.toString()
            sessionManagerA.longitude = userAccountModel.longitude.toString()
            sessionManagerA.login = true
            openActivity(intent)
        } else {
            intent = Intent(this, CompleteRegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun openActivity(intent: Intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
            Timber.tag("LoginGoogle").d(task.toString())
        } else {
            callbackManager!!.onActivityResult(requestCode, resultCode, data)
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            signInUpdateUI(account)
            Timber.d("LoginGoogle account:%s", account.toString())
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            signInUpdateUI(null)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    var strIdToken = ""
    private fun signInUpdateUI(account: GoogleSignInAccount?) {
        try {
            showProgressDialog()
            val strEmail = account!!.email
            val strFcmToken = token
            val strDeviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            val strDevice = "Android"
            val runnable = Runnable {
                try {
                    val scope = "oauth2:" + Scopes.EMAIL + " " + Scopes.PROFILE
                    strIdToken = GoogleAuthUtil.getToken(
                        applicationContext,
                        account.account,
                        scope,
                        Bundle()
                    )
                    Helper.closeKeyboard(this)
                    val stringRequest: StringRequest =
                        object : StringRequest(
                            Method.POST, Constant.URL_SIGNIN_GOOGLE,
                            Response.Listener { response: String? ->
                                Timber.e(response)
                                hideProgressDialog()
                                try {
                                    val jsonObject = JSONObject(response!!)
                                    Timber.e(jsonObject.toString())
                                    val jsonObjectData = jsonObject.getJSONObject("data")
                                    sessionManagerA.accessToken =
                                        jsonObjectData.getString("access_token")
                                    sessionManagerA.tokenType =
                                        jsonObjectData.getString("token_type")
                                    val jsonObjectUser = jsonObjectData.getJSONObject("user")
                                    val userAccountModel =
                                        UserAccountModel().getJsonToModel(jsonObjectUser)
                                    sessionManagerA.userAccount = userAccountModel
                                    if (fromSignUp) fireBaseEvent!!.sendEvent(
                                        FireBaseEvent.Event.SIGN_UP,
                                        FireBaseEvent.EventType.API_RESPOND_SUCCESS,
                                        EventValue.SIGN_UP_GOOGLE
                                    ) else fireBaseEvent!!.sendEvent(
                                        FireBaseEvent.Event.LOGIN,
                                        FireBaseEvent.EventType.API_RESPOND_SUCCESS,
                                        EventValue.LOGIN_GOOGLE
                                    )
                                    proceedToCorrectActivity(userAccountModel)
                                } catch (e: JSONException) {
                                    Timber.e(e.toString())
                                    e.printStackTrace()
                                    FirebaseCrashlytics.getInstance().recordException(e)
                                }
                            },
                            Response.ErrorListener { error: VolleyError ->
                                val networkResponse = error.networkResponse
                                if (networkResponse?.data != null) {
                                    val jsonError = String(networkResponse.data)
                                    // Print Error!
                                    Timber.e(jsonError)
                                    FirebaseCrashlytics.getInstance().recordException(error)
                                    try {
                                        val jsonObject = JSONObject(jsonError)
                                        val jsonObjectError = jsonObject.getJSONObject("error")
                                        if (jsonObjectError.has("message")) {
                                            showToast(
                                                jsonObjectError.getString("message"),
                                                this@AuthActivity
                                            )
                                        }
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }
                                } else {
                                    showToast("Something Went Wrong", this@AuthActivity)
                                }
                                Timber.e(error.toString())
                                hideProgressDialog()
                            }
                        ) {
                            @Throws(AuthFailureError::class)
                            override fun getHeaders(): Map<String, String> {
                                val map1: MutableMap<String, String> = HashMap()
                                map1["Content-Type"] = "application/x-www-form-urlencoded"
                                map1["X-Requested-With"] = "XMLHttpRequest"
                                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                                return map1
                            }

                            override fun getParams(): Map<String, String> {
                                val map1: MutableMap<String, String> = HashMap()

                                // TODO: We ignore this and set it to no name due to getting these data in complete profile
                                map1["fname"] = "Jobtick"
                                map1["lname"] = "User"
                                map1["email"] = strEmail!!
                                map1["device_token"] = strDeviceId
                                map1["device_type"] = strDevice
                                if (strFcmToken != null && !strFcmToken.isEmpty()) map1["fcm_token"] =
                                    strFcmToken
                                map1["access_token"] = strIdToken
                                map1["latitude"] = sessionManagerA.latitude
                                map1["longitude"] = sessionManagerA.longitude
                                return map1
                            }
                        }
                    stringRequest.retryPolicy = DefaultRetryPolicy(
                        0, -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
                    val requestQueue = Volley.newRequestQueue(this)
                    requestQueue.add(stringRequest)
                } catch (e: IOException) {
                    e.printStackTrace()
                    FirebaseCrashlytics.getInstance().recordException(e)
                } catch (e: GoogleAuthException) {
                    e.printStackTrace()
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }
            AsyncTask.execute(runnable)
        } catch (e: Exception) {
            hideProgressDialog()
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun Signup(email: String, password: String, checked: Boolean) {
        if (checked) {
            showProgressDialog()
            val strFcmToken = token
            val strDeviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            val strDevice = DEVICE
            Helper.closeKeyboard(this)
            val stringRequest: StringRequest = object : StringRequest(
                Method.POST, Constant.URL_SIGNUP,
                Response.Listener { response: String? ->
                    Timber.tag("responce_url").e(response)
                    hideProgressDialog()
                    //  showToast("Check your inbox", AuthActivity.this);
                    fireBaseEvent!!.sendEvent(
                        FireBaseEvent.Event.SIGN_UP,
                        FireBaseEvent.EventType.API_RESPOND_SUCCESS, EventValue.SIGN_UP_NORMAL
                    )
                    val bundle = Bundle()
                    val fragment: Fragment = VerifyAccountFragment()
                    val ft = supportFragmentManager.beginTransaction()
                    ft.replace(R.id.auth_layout, fragment).addToBackStack(fragment.toString())
                    bundle.putString("email", email)
                    bundle.putString("password", password)
                    fragment.arguments = bundle
                    ft.commit()
                },
                Response.ErrorListener { error: VolleyError ->
                    val networkResponse = error.networkResponse
                    if (networkResponse?.data != null) {
                        val jsonError = String(networkResponse.data)
                        // Print Error!
                        Timber.tag("intent22").e(jsonError)
                        try {
                            val jsonObject = JSONObject(jsonError)
                            val jsonObjectError = jsonObject.getJSONObject("error")
                            if (jsonObjectError.has("errors")) {
                                val jsonObjectErrors = jsonObjectError.getJSONObject("errors")
                                var errorEmail = ""
                                var errorPassword = ""
                                if (jsonObjectErrors.has("email")) {
                                    val jsonArrayMobile = jsonObjectErrors.getJSONArray("email")
                                    errorEmail = jsonArrayMobile.getString(0)
                                    showToast(errorEmail, this@AuthActivity)
                                    editTextError.onEmailError(errorEmail)
                                } else if (jsonObjectErrors.has("password")) {
                                    val jsonArrayMobile = jsonObjectErrors.getJSONArray("password")
                                    errorPassword = jsonArrayMobile.getString(0)
                                    showToast(errorPassword, this@AuthActivity)
                                    editTextError.onPasswordError(errorPassword)
                                }
                                // signUpFragment.error(error_email,error_password);
                            } else if (jsonObjectError.has("message")) {
                                showToast(jsonObjectError.getString("message"), this@AuthActivity)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        showToast("Something Went Wrong", this@AuthActivity)
                    }
                    Timber.tag("error").e(error.toString())
                    hideProgressDialog()
                }
            ) {
                override fun getHeaders(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["Content-Type"] = "application/x-www-form-urlencoded"
                    map1["X-Requested-With"] = "XMLHttpRequest"
                    map1["Version"] = BuildConfig.VERSION_CODE.toString()
                    return map1
                }

                override fun getParams(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["email"] = email
                    map1["password"] = password
                    map1["device_token"] = strDeviceId
                    map1["device_type"] = strDevice
                    map1["fcm_token"] = strFcmToken
                    map1["latitude"] = "0"
                    map1["longitude"] = "0"
                    map1["fname"] = "Jobtick"
                    // map1.put("lname", "User");
                    map1["location"] = "no location"
                    return map1
                }
            }
            stringRequest.retryPolicy = DefaultRetryPolicy(
                0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(stringRequest)
        } else {
            showToast("Before you can sign up, you must accept the Jobtick Terms of Services.", this@AuthActivity)
        }
    }

    fun verification(email: String, password: String, otp: String) {
        showProgressDialog()
        Helper.closeKeyboard(this)
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, Constant.URL_SIGNIN,
            Response.Listener { response: String? ->
                Timber.e(response)
                hideProgressDialog()
                try {
                    val jsonObject = JSONObject(response!!)
                    Timber.e(jsonObject.toString())
                    val jsonObjectData = jsonObject.getJSONObject("data")
                    sessionManagerA.accessToken = jsonObjectData.getString("access_token")
                    sessionManagerA.tokenType = jsonObjectData.getString("token_type")
                    val jsonObjectUser = jsonObjectData.getJSONObject("user")
                    val userAccountModel = UserAccountModel().getJsonToModel(jsonObjectUser)
                    sessionManagerA.userAccount = userAccountModel
                    proceedToCorrectActivity(userAccountModel)
                } catch (e: JSONException) {
                    Timber.e(e.toString())
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error: VolleyError ->
                val networkResponse = error.networkResponse
                if (networkResponse?.data != null) {
                    val jsonError = String(networkResponse.data)
                    // Print Error!
                    Timber.e(jsonError)
                    try {
                        val jsonObject = JSONObject(jsonError)
                        val jsonObjectError = jsonObject.getJSONObject("error")
                        val message = jsonObjectError.getString("message")
                        if (message.equals("unauthorized", ignoreCase = true)) {
                            val fragment: Fragment = SignInFragment()
                            val ft = supportFragmentManager.beginTransaction()
                            ft.replace(R.id.container, fragment)
                            ft.commit()
                        }
                        showToast(message, this@AuthActivity)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    showToast("Something Went Wrong", this@AuthActivity)
                }
                Timber.e(error.toString())
                hideProgressDialog()
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["X-Requested-With"] = "XMLHttpRequest"
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }

            override fun getParams(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["email"] = email
                map1["password"] = password
                map1["otp"] = otp
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            0, -1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    fun newEmailVerification(email: String, otp: String) {
        showProgressDialog()
        Helper.closeKeyboard(this)
        val stringRequest: StringRequest =
            object : StringRequest(
                Method.POST, Constant.URL_EMAIL_VERIFICATION,
                Response.Listener { response: String? ->
                    Timber.e(response)
                    hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response!!)
                        Timber.e(jsonObject.toString())
                        val jsonObjectData = jsonObject.getJSONObject("data")
                        sessionManagerA.accessToken = jsonObjectData.getString("access_token")
                        sessionManagerA.tokenType = jsonObjectData.getString("token_type")
                        val jsonObjectUser = jsonObjectData.getJSONObject("user")
                        val userAccountModel = UserAccountModel().getJsonToModel(jsonObjectUser)
                        sessionManagerA.userAccount = userAccountModel
                        proceedToCorrectActivity(userAccountModel)
                    } catch (e: JSONException) {
                        Timber.e(e.toString())
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    val networkResponse = error.networkResponse
                    if (networkResponse?.data != null) {
                        val jsonError = String(networkResponse.data)
                        // Print Error!
                        Timber.e(jsonError)
                        try {
                            val jsonObject = JSONObject(jsonError)
                            val jsonObjectError = jsonObject.getJSONObject("error")
                            val message = jsonObjectError.getString("message")
                            if (message.equals("unauthorized", ignoreCase = true)) {
                                val fragment: Fragment = SignInFragment()
                                val ft = supportFragmentManager.beginTransaction()
                                ft.replace(R.id.container, fragment)
                                ft.commit()
                            }
                            showToast(message, this@AuthActivity)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        showToast("Something Went Wrong", this@AuthActivity)
                    }
                    Timber.e(error.toString())
                    hideProgressDialog()
                }
            ) {
                override fun getHeaders(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["Content-Type"] = "application/x-www-form-urlencoded"
                    map1["X-Requested-With"] = "XMLHttpRequest"
                    map1["Version"] = BuildConfig.VERSION_CODE.toString()
                    return map1
                }

                override fun getParams(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["email"] = email
                    map1["otp"] = otp
                    return map1
                }
            }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            0, -1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    fun nextStepForgotPassword(email: String) {
        showProgressDialog()
        Helper.closeKeyboard(this)
        val stringRequest: StringRequest =
            object : StringRequest(
                Method.POST, Constant.URL_RESET_PASSWORD,
                Response.Listener { response: String? ->
                    Timber.e(response)
                    hideProgressDialog()
                    //  showToast("Check your inbox", AuthActivity.this);
                    val bundle = Bundle()
                    val fragment: Fragment = ForgotPassword2Fragment()
                    val ft = supportFragmentManager.beginTransaction()
                    ft.replace(R.id.auth_layout, fragment).addToBackStack(fragment.toString())
                    bundle.putString("email", email)
                    fragment.arguments = bundle
                    ft.commit()
                },
                Response.ErrorListener { error: VolleyError ->
                    val networkResponse = error.networkResponse
                    if (networkResponse?.data != null) {
                        val jsonError = String(networkResponse.data)
                        // Print Error!
                        Timber.e(jsonError)
                        try {
                            val jsonObject = JSONObject(jsonError)
                            val jsonObjectError = jsonObject.getJSONObject("error")
                            if (jsonObjectError.has("errors")) {
                                val jsonObjectErrors = jsonObjectError.getJSONObject("errors")
                                var strError: String? = null
                                when {
                                    jsonObjectErrors.has("email") -> {
                                        val jsonArrayMobile = jsonObjectErrors.getJSONArray("email")
                                        strError = jsonArrayMobile.getString(0)
                                        showToast(strError, this)
                                        editTextError.onEmailError(strError)
                                    }
                                    jsonObjectErrors.has("password") -> {
                                        val jsonArrayMobile =
                                            jsonObjectErrors.getJSONArray("password")
                                        strError = jsonArrayMobile.getString(0)
                                        showToast(strError, this)
                                        editTextError.onPasswordError(strError)
                                    }
                                    else -> {
                                        val message = jsonObjectError.getString("message")
                                        showToast(message, this)
                                    }
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        showToast("Something Went Wrong", this@AuthActivity)
                    }
                    hideProgressDialog()
                }
            ) {
                override fun getHeaders(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["Content-Type"] = "application/x-www-form-urlencoded"
                    map1["X-Requested-With"] = "XMLHttpRequest"
                    map1["Version"] = BuildConfig.VERSION_CODE.toString()
                    return map1
                }

                override fun getParams(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["email"] = email
                    return map1
                }
            }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            0, -1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    // There is no such API any more, but we keep it af any changes occurs again
    fun resendOtp(email: String) {
        showProgressDialog()
        Helper.closeKeyboard(this)
        val stringRequest: StringRequest =
            object : StringRequest(
                Method.POST, Constant.URL_RESEND_OTP,
                Response.Listener { response: String? ->
                    Timber.e(response)
                    hideProgressDialog()
                    onResendOtp.success()
                },
                Response.ErrorListener { error: VolleyError ->
                    onResendOtp.failure()
                    val networkResponse = error.networkResponse
                    if (networkResponse?.data != null) {
                        val jsonError = String(networkResponse.data)
                        try {
                            val jsonObject = JSONObject(jsonError)
                            val jsonObjectError = jsonObject.getJSONObject("error")
                            if (jsonObjectError.has("errors")) {
                                val jsonObjectErrors = jsonObjectError.getJSONObject("errors")
                                val errorEmail: String
                                if (jsonObjectErrors.has("email")) {
                                    val jsonArrayMobile = jsonObjectErrors.getJSONArray("email")
                                    errorEmail = jsonArrayMobile.getString(0)
                                    showToast(errorEmail, this)
                                    editTextError.onEmailError(errorEmail)
                                }
                            } else {
                                val message = jsonObjectError.getString("message")
                                showToast(message, this)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        showToast("Something Went Wrong", this@AuthActivity)
                    }
                    hideProgressDialog()
                }
            ) {
                override fun getHeaders(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["Content-Type"] = "application/x-www-form-urlencoded"
                    map1["X-Requested-With"] = "XMLHttpRequest"
                    map1["Version"] = BuildConfig.VERSION_CODE.toString()
                    return map1
                }

                override fun getParams(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["email"] = email
                    return map1
                }
            }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            0, -1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    fun resendOtpForResetPassword(email: String) {
        showProgressDialog()
        Helper.closeKeyboard(this)
        val stringRequest: StringRequest =
            object : StringRequest(
                Method.POST, Constant.URL_RESET_PASSWORD,
                Response.Listener { response: String? ->
                    Timber.e(response)
                    hideProgressDialog()
                    onResendOtp.success()
                },
                Response.ErrorListener { error: VolleyError ->
                    onResendOtp.failure()
                    val networkResponse = error.networkResponse
                    if (networkResponse?.data != null) {
                        val jsonError = String(networkResponse.data)
                        try {
                            val jsonObject = JSONObject(jsonError)
                            val jsonObjectError = jsonObject.getJSONObject("error")
                            if (jsonObjectError.has("errors")) {
                                val jsonObjectErrors = jsonObjectError.getJSONObject("errors")
                                val errorEmail: String
                                if (jsonObjectErrors.has("email")) {
                                    val jsonArrayMobile = jsonObjectErrors.getJSONArray("email")
                                    errorEmail = jsonArrayMobile.getString(0)
                                    showToast(errorEmail, this)
                                    editTextError.onEmailError(errorEmail)
                                }
                            } else {
                                val message = jsonObjectError.getString("message")
                                showToast(message, this)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        showToast("Something Went Wrong", this@AuthActivity)
                    }
                    hideProgressDialog()
                }
            ) {
                override fun getHeaders(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["Content-Type"] = "application/x-www-form-urlencoded"
                    map1["X-Requested-With"] = "XMLHttpRequest"
                    map1["Version"] = BuildConfig.VERSION_CODE.toString()
                    return map1
                }

                override fun getParams(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["email"] = email
                    return map1
                }
            }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            0, -1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    fun newResendOtp(email: String) {
        showProgressDialog()
        Helper.closeKeyboard(this)
        val stringRequest: StringRequest =
            object : StringRequest(
                Method.POST, Constant.URL_NEW_RESEND_OTP,
                Response.Listener { response: String? ->
                    Timber.e(response)
                    hideProgressDialog()
                    onResendOtp.success()
                },
                Response.ErrorListener { error: VolleyError ->
                    onResendOtp.failure()
                    val networkResponse = error.networkResponse
                    if (networkResponse?.data != null) {
                        val jsonError = String(networkResponse.data)
                        // Print Error!
                        Timber.e(jsonError)
                        try {
                            val jsonObject = JSONObject(jsonError)
                            val jsonObjectError = jsonObject.getJSONObject("error")
                            if (jsonObjectError.has("errors")) {
                                val jsonObjectErrors = jsonObjectError.getJSONObject("errors")
                                val errorEmail: String
                                if (jsonObjectErrors.has("email")) {
                                    val jsonArrayMobile = jsonObjectErrors.getJSONArray("email")
                                    errorEmail = jsonArrayMobile.getString(0)
                                    showToast(errorEmail, this)
                                    editTextError.onEmailError(errorEmail)
                                }
                            } else {
                                val message = jsonObjectError.getString("message")
                                showToast(message, this)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        showToast("Something Went Wrong", this@AuthActivity)
                    }
                    Timber.e(error.toString())
                    hideProgressDialog()
                }
            ) {
                override fun getHeaders(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["Content-Type"] = "application/x-www-form-urlencoded"
                    map1["X-Requested-With"] = "XMLHttpRequest"
                    map1["Version"] = BuildConfig.VERSION_CODE.toString()
                    return map1
                }

                override fun getParams(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["email"] = email
                    return map1
                }
            }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            0, -1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    //    public OnResendOtp getOnResendOtp() {
    //        return onResendOtp;
    //    }
    fun setOnResendOtp(onResendOtp: OnResendOtp?) {
        this.onResendOtp = onResendOtp!!
    }

    interface OnResendOtp {
        fun success()
        fun failure()
    }

    companion object {
        private const val RC_SIGN_IN = 234
    }
}
