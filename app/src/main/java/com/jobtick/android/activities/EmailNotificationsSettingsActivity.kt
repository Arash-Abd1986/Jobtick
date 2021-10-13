package com.jobtick.android.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.CompoundButton
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.models.response.chat.ChatResponse
import com.jobtick.android.models.response.mailnotifsetting.NotifSettingResponse
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.Helper
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.lang.Exception
import java.util.*

private const val TYPE = "email"

class EmailNotificationsSettingsActivity : ActivityBase() {
    private var toolbar: MaterialToolbar? = null
    private var chbUpdatesRec: CheckBox? = null
    private var chbJobAlert: CheckBox? = null
    private var chbJobUpdate: CheckBox? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_notification_settings)
        toolbar = findViewById(R.id.toolbar)
        chbUpdatesRec = findViewById(R.id.chb_updates_rec)
        chbJobAlert = findViewById(R.id.chb_job_alert)
        chbJobUpdate = findViewById(R.id.chb_job_update)
        initToolbar()

    }

    private fun initToolbar() {
        toolbar!!.setNavigationIcon(R.drawable.ic_back)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Email Notifications"
        getSetting()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveSettings() {
        chbUpdatesRec!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                setSetting(TYPE, "recommendations", "1")
            else
                setSetting(TYPE, "recommendations", "0")
        }
        chbJobUpdate!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                setSetting(TYPE, "jobupdates", "1")
            else
                setSetting(TYPE, "jobupdates", "0")
        }
        chbJobAlert!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                setSetting(TYPE, "jobalerts", "1")
            else
                setSetting(TYPE, "jobalerts", "0")
        }
    }

    private fun getSetting() {
        showProgressDialog()
        Helper.closeKeyboard(this@EmailNotificationsSettingsActivity)
        val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.BASE_URL_v2 + "notifications/settings?type=" + TYPE,
                Response.Listener { response: String? ->
                    Timber.e(response)
                    hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response!!)
                        val gson = Gson()
                        val notifSettingResponse = gson.fromJson(jsonObject.toString(), NotifSettingResponse::class.java)

                        chbJobAlert!!.isChecked = notifSettingResponse.data!!.jobalerts != 0
                        chbJobUpdate!!.isChecked = notifSettingResponse.data.jobupdates != 0
                        chbUpdatesRec!!.isChecked = notifSettingResponse.data.recommendations != 0
                        saveSettings()

                    } catch (e: Exception) {
                        hideProgressDialog()
                        saveSettings()
                        Timber.e(e.toString())
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    hideProgressDialog()
                    saveSettings()
                    errorHandle1(error.networkResponse)
                }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["Authorization"] = "Bearer " + sessionManager.accessToken
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this@EmailNotificationsSettingsActivity)
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
    }// categoryArrayList.clear();

    private fun setSetting(type: String, item: String, value: String) {
        showProgressDialog()
        Helper.closeKeyboard(this@EmailNotificationsSettingsActivity)
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Constant.BASE_URL_v2 + "notifications/settings",
                Response.Listener { response: String? ->
                    hideProgressDialog()
                    Timber.e(response)
                },
                Response.ErrorListener { error: VolleyError ->
                    hideProgressDialog()
                    errorHandle1(error.networkResponse)
                }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["Authorization"] = "Bearer " + sessionManager.accessToken
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }

            override fun getParams(): MutableMap<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1.put("type", type)
                map1.put("item", item)
                map1.put("value", value)
                return map1

            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this@EmailNotificationsSettingsActivity)
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
    }// categoryArrayList.clear();

}