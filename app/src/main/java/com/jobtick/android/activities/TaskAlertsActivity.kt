package com.jobtick.android.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.gson.Gson
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.adapers.TaskAlertAdapter
import com.jobtick.android.models.response.jobalerts.Data
import com.jobtick.android.models.response.jobalerts.JobAlertsResponse
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.HttpStatus
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class TaskAlertsActivity : ActivityBase(), TaskAlertAdapter.OnItemClickListener {
    var toolbar: MaterialToolbar? = null
    var cbReceiveAlerts: SwitchMaterial? = null
    var txtBtnAddCustomAlert: TextView? = null
    var recyclerView: RecyclerView? = null
    var adapter: TaskAlertAdapter? = null
    var taskAlertArrayList: ArrayList<Data>? = null
    var noAlerts: LinearLayout? = null
    var alerts: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_alerts)
        initIDs()
        initToolbar()
        initComponent()
    }

    private fun initIDs() {
        toolbar = findViewById(R.id.toolbar)
        cbReceiveAlerts = findViewById(R.id.cb_receive_alerts)
        txtBtnAddCustomAlert = findViewById(R.id.txt_btn_add_custom_alert)
        recyclerView = findViewById(R.id.recycler_view)
        noAlerts = findViewById(R.id.no_alerts_container)
        alerts = findViewById(R.id.alerts_container)
        txtBtnAddCustomAlert!!.setOnClickListener {
            val newTaskAlerts = Intent(this@TaskAlertsActivity, NewTaskAlertsActivity::class.java)
            startActivityForResult(newTaskAlerts, 1)
        }
    }

    private fun initComponent() {
        taskAlertArrayList = ArrayList()
        recyclerView!!.layoutManager = LinearLayoutManager(this@TaskAlertsActivity)
        recyclerView!!.setHasFixedSize(true)
        adapter = TaskAlertAdapter(this, ArrayList())
        adapter!!.setOnItemClickListener(this)
        recyclerView!!.adapter = adapter
        listOfTaskAlert
    }

    private fun initToolbar() {
        toolbar!!.setNavigationIcon(R.drawable.ic_back)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Job Alerts"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onItemClick(view: View, obj: Data, position: Int, action: String) {
        if (action.equals("delete", ignoreCase = true)) {
            adapter!!.removeItems(position)
            removeTaskAlert(obj.id!!)
            checkList()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            listOfTaskAlert
        }
    }

    fun removeTaskAlert(taskAlertId: Int) {
        //{{baseurl}}/taskalerts/:taskalert_id
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.DELETE, Constant.URL_TASK_ALERT_V2 + "/" + taskAlertId,
                Response.Listener { response: String? ->
                    Timber.e(response)
                    hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response!!)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                            } else {
                                showToast("Something went Wrong", this@TaskAlertsActivity)
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
                showToast("Something Went Wrong", this@TaskAlertsActivity)
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
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this@TaskAlertsActivity)
        requestQueue.add(stringRequest)
    }

    // Print Error!
    val listOfTaskAlert: Unit
        get() {
            taskAlertArrayList!!.clear()
            showProgressDialog()
            val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.URL_TASK_ALERT_V2,
                    Response.Listener { response: String? ->
                        Timber.e(response)
                        hideProgressDialog()
                        try {
                            val jsonObject = JSONObject(response!!)
                            Timber.e(jsonObject.toString())
                            val gson = Gson()
                            val (data, _, success) = gson.fromJson(jsonObject.toString(), JobAlertsResponse::class.java)
                            if (success != null) {
                                if (success) {
                                    if (data != null) {
                                        taskAlertArrayList!!.addAll(data)
                                    }
                                    adapter!!.addItems(taskAlertArrayList)
                                } else {
                                    showToast("Something went Wrong", this@TaskAlertsActivity)
                                }
                            }
                        } catch (e: Exception) {
                            Timber.e(e.toString())
                            e.printStackTrace()
                            showToast("Something went Wrong", this@TaskAlertsActivity)
                        }
                        checkList()
                    },
            Response.ErrorListener { error: VolleyError ->
                checkList()
                val networkResponse = error.networkResponse
                if (networkResponse?.data != null) {
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
                        val jsonObjectError = jsonObject.getJSONObject("error")
                        if (jsonObjectError.has("message")) {
                            showToast(jsonObjectError.getString("message"), this)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    showToast("Something Went Wrong", this@TaskAlertsActivity)
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
            }
            stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            val requestQueue = Volley.newRequestQueue(this@TaskAlertsActivity)
            requestQueue.add(stringRequest)
        }

    private fun checkList() {
        if (taskAlertArrayList!!.size <= 0) {
            noAlerts!!.visibility = View.VISIBLE
            alerts!!.visibility = View.GONE
        } else {
            noAlerts!!.visibility = View.GONE
            alerts!!.visibility = View.VISIBLE
        }
    }
}