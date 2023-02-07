package com.jobtick.android.material.ui.landing

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.facebook.*
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
import com.jobtick.android.activities.ActivityBase
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.Helper
import com.jobtick.android.utils.SessionManager
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException


class OnboardingActivity : ActivityBase() {
    lateinit var navController: NavController
    var callbackManager: CallbackManager? = null
    var mGoogleSignInClient: GoogleSignInClient? = null
    private lateinit var sessionManagerA: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment?
        navController = navHostFragment!!.navController


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
    var fromSignUp = false

    fun signInWithGoogle(fromSignUp: Boolean) {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
        this.fromSignUp = fromSignUp
        Log.d("googlesignin", "signinwithgoole")
    }

    fun facebookLogin(fromSignUp: Boolean) {
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
        this.fromSignUp = fromSignUp
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
                                showToast(jsonObjectError.getString("message"), applicationContext)
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
            unauthorizedUser()
        }
    }
    private fun openActivity(intent: Intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

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
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
            Log.d("googlesignin", "onActivityResult")

            Timber.tag("LoginGoogle").d(task.toString())
        } else {
            Log.d("googlesignin", "onActivityResult1")

            callbackManager!!.onActivityResult(requestCode, resultCode, data)
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            signInUpdateUI(account)
            Timber.d("LoginGoogle account:%s", account.toString())
            Log.d("googlesignin", "handleSignInResult")

        } catch (e: ApiException) {
            Log.d("googlesignin", e.toString())

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
                                                this
                                            )
                                        }
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }
                                } else {
                                    showToast("Something Went Wrong", this)
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
    companion object {
        private const val RC_SIGN_IN = 234
    }
}