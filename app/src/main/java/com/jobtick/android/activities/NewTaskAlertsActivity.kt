package com.jobtick.android.activities

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.MenuItem
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.MaterialToolbar
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.fragments.NewTaskAlertsInPersonFragment.Companion.newInstance
import com.jobtick.android.fragments.NewTaskAlertsInPersonFragment.OperationInPersonListener
import com.jobtick.android.models.task.TaskAlert
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.HttpStatus
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class NewTaskAlertsActivity : ActivityBase(), OperationInPersonListener {

    var toolbar: MaterialToolbar? = null

    var taskAlert: TaskAlert? = null
    var position = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task_alerts)
        toolbar = findViewById(R.id.toolbar)
        taskAlert = TaskAlert()
        if (intent.extras != null) {
            taskAlert = intent.extras!!.getParcelable<Parcelable>("TASK_ALERT") as TaskAlert?
            position = intent.extras!!.getInt("POSITION")
        }
        initToolbar()
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, newInstance(taskAlert, position, this), "New Job Alert")
                    .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (taskAlert!!.isValid) {
            val intent = Intent()
            val bundle = Bundle()
            bundle.putInt("POSITION", position)
            bundle.putParcelable("TASK_ALERT", taskAlert)
            intent.putExtras(bundle)
            setResult(1, intent)
        }
        super.onBackPressed()
    }

    private fun initToolbar() {
        toolbar!!.setNavigationIcon(R.drawable.ic_back)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "New Job Alert"
    }

    override fun onInPersonSave(position: Int, taskAlert: TaskAlert?, minPrice: String, maxPrice: String) {
        this.taskAlert = taskAlert
        this.position = position
        if (taskAlert!!.alert_type == "physical") {
            taskAlert.lattitude = taskAlert.lattitude
            taskAlert.longitude = taskAlert.longitude
            taskAlert.suburb = taskAlert.suburb
        } else {
            taskAlert.lattitude = null
            taskAlert.longitude = null
            taskAlert.suburb = null
        }
        addTaskAlert(this.taskAlert, minPrice, maxPrice)
    }

    private fun addTaskAlert(taskAlert: TaskAlert?, minPrice: String, maxPrice: String) {
        //{{baseurl}}/taskalerts
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Constant.URL_TASK_ALERT_V2,
                Response.Listener { response: String? ->
                    Timber.e(response)
                    hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                    finish()
                                }
                            } else {
                                showToast("Something went Wrong", this@NewTaskAlertsActivity)
                            }
                        }
                    } catch (e: JSONException) {
                        Timber.e(e.toString())
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError ->
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
                            hideProgressDialog()
                            val jsonObject = JSONObject(jsonError)
                            val jsonObject_error = jsonObject.getJSONObject("error")
                            showToast(jsonObject_error.getString("message"), this)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        showToast("Something Went Wrong", this@NewTaskAlertsActivity)
                    }
                    Timber.e(error.toString())
                    hideProgressDialog()
                }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["X-Requested-With"] = "XMLHttpRequest"
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["keyword"] = taskAlert!!.ketword
                map1["type"] = taskAlert.alert_type
                map1["minprice"] = minPrice
                map1["maxprice"] = maxPrice
                if (taskAlert.alert_type == "physical") {
                    map1["location"] = taskAlert.suburb
                    map1["latitude"] = taskAlert.lattitude.toString()
                    map1["longitude"] = taskAlert.longitude.toString()
                    map1["distance"] = taskAlert.distance.toString()
                }
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this@NewTaskAlertsActivity)
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
    }
}