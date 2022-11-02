package com.jobtick.android.fragments

import android.widget.TextView
import android.app.ProgressDialog
import android.content.Context
import android.graphics.drawable.Icon
import com.jobtick.android.models.TaskModel
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.jobtick.android.R
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.volley.*
import com.android.volley.Request.Method.POST
import com.android.volley.toolbox.StringRequest
import timber.log.Timber
import com.jobtick.android.activities.ActivityBase
import org.json.JSONException
import com.jobtick.android.utils.HttpStatus
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.jobtick.android.BuildConfig
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.error
import com.jobtick.android.viewmodel.JobDetailsViewModel
import com.jobtick.android.viewmodel.ViewModelFactory
import org.json.JSONObject
import java.lang.ClassCastException
import java.util.*

class IncreaseBudgetBottomSheet : Fragment() {
    private var oldPrice: TextView? = null
    private var serviceFee: TextView? = null
    private var receiveMoney: TextView? = null
    private var addPrice: TextInputLayout? = null
    private var newPrice: TextView? = null
    private var reason: TextInputLayout? = null
    private var submit: Button? = null
    private var pDialog: ProgressDialog? = null
    private var totalBudget = 0
    private var taskModel: TaskModel? = null
    private var sessionManager: SessionManager? = null
    private var listener: NoticeListener? = null
    lateinit var viewModel: JobDetailsViewModel
    lateinit var label: MaterialTextView
    lateinit var info: AppCompatImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_increase_budget, container, false)
        sessionManager = SessionManager(context)
        oldPrice = view.findViewById(R.id.old_price)
        newPrice = view.findViewById(R.id.new_price)
        serviceFee = view.findViewById(R.id.service_fee)
        receiveMoney = view.findViewById(R.id.receive_money)
        addPrice = view.findViewById(R.id.add_price)
        reason = view.findViewById(R.id.reason)
        submit = view.findViewById(R.id.submit)
        info = view.findViewById(R.id.info)
        label = view.findViewById(R.id.label)
        submit!!.setOnClickListener(View.OnClickListener {
            if (!validation()){
                return@OnClickListener}
            val increasedPrice = addPrice!!.editText!!.text.toString().toInt()
            submitIncreaseBudget(increasedPrice.toString(), reason!!.editText!!.text.toString().trim { it <= ' ' })
        })

        addPrice!!.editText!!.doOnTextChanged { text, _, _, _ ->
            if (validation()){
                addPrice!!.error("Increase amount must be between $5 and $500.", label, info, setError = false, isBold = true)
            }
            text?.let {
                if (text.isNotEmpty()) {
                    //setupBudget(text.toString().toInt() + taskModel!!.amount)
                }
            }

        }
        initProgressDialog()
        init()
        return view
    }

    private fun init() {
        viewModel = ViewModelProvider(
                requireActivity(),
                ViewModelFactory(ApiHelper(ApiClient.getClient()))
        ).get(JobDetailsViewModel::class.java)

        viewModel.geTaskModelResponse().observe(viewLifecycleOwner) { taskModel ->
            this.taskModel = taskModel
            oldPrice!!.text = String.format(Locale.ENGLISH, "%d", taskModel!!.amount)
        }
    }

    private fun setupBudget(budget: Int) {
        val workerServiceFee = taskModel!!.worker.workerTier.serviceFee.toFloat()
        val serviceFee1 = budget * workerServiceFee / 100
        serviceFee!!.text = String.format("$%s", serviceFee1)
        newPrice!!.text = String.format("$%s", budget)
        totalBudget = (budget - budget * workerServiceFee / 100).toInt()
        receiveMoney!!.text = String.format(Locale.ENGLISH, "$%d", totalBudget)
    }

    fun initProgressDialog() {
        pDialog = ProgressDialog(context)
        pDialog!!.setTitle(getString(R.string.processing))
        pDialog!!.setMessage(getString(R.string.please_wait))
        pDialog!!.setCancelable(false)
    }

    fun showProgressDialog() {
        if (!pDialog!!.isShowing) pDialog!!.show()
    }

    fun hideProgressDialog() {
        if (pDialog!!.isShowing) pDialog!!.dismiss()
    }

    private fun submitIncreaseBudget(increase_budget: String, increase_budget_reason: String) {
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(POST, Constant.URL_TASKS + "/" + taskModel!!.slug + Constant.URL_BUDGET_Increment,
                Response.Listener { response: String? ->
                    Timber.e(response)
                    hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                listener!!.onSubmitIncreasePrice()
                                //dismiss();
                            } else {
                                (requireActivity() as ActivityBase).showToast("Something went Wrong", context)
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
                            (requireActivity() as ActivityBase).unauthorizedUser()
                            hideProgressDialog()
                            return@ErrorListener
                        }
                        try {
                            val jsonObject = JSONObject(jsonError)
                            val jsonObject_error = jsonObject.getJSONObject("error")
                            if (jsonObject_error.has("errors")) {
                                val jsonObjectErrors = jsonObject_error.getJSONObject("errors")
                                if (jsonObjectErrors.has("amount") && !jsonObjectErrors.isNull("amount")) {
                                    val jsonArrayAmount = jsonObjectErrors.getJSONArray("amount")
                                    (requireActivity() as ActivityBase).showToast(jsonArrayAmount.getString(0), requireContext())
                                } else if (jsonObjectErrors.has("creation_reason") && !jsonObjectErrors.isNull("creation_reason")) {
                                    val jsonArray_amount = jsonObjectErrors.getJSONArray("creation_reason")
                                    (requireActivity() as ActivityBase).showToast(jsonArray_amount.getString(0), requireContext())
                                }
                            } else {
                                if (jsonObject_error.has("message")) {
                                    (requireActivity() as ActivityBase).showToast(jsonObject_error.getString("message"), requireContext())
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        (requireActivity() as ActivityBase).showToast("Something went Wrong", context)
                    }
                    Timber.e(error.toString())
                    hideProgressDialog()
                }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager!!.tokenType + " " + sessionManager!!.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["X-Requested-With"] = "XMLHttpRequest"
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }

            override fun getParams(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["amount"] = increase_budget
                map1["creation_reason"] = increase_budget_reason
                Timber.e(map1.size.toString())
                Timber.e(map1.toString())
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }

    private fun validation(): Boolean {
        if (addPrice!!.editText!!.text.isEmpty()) {
            addPrice!!.error("Increase amount must be between $5 and $500.", label, info, setError = true, isBold = false)
            return false
        }
        if (addPrice!!.editText!!.text.toString().toInt() < 5 || addPrice!!.editText!!.text.toString().toInt() > 500) {
            addPrice!!.error("Increase amount must be between $5 and $500.", label, info, setError = true, isBold = false)
            return false
        }
        if (addPrice!!.editText!!.text.toString().toInt() > 500) {
            addPrice!!.error("Increase amount must be between $5 and $500.", label, info, setError = true, isBold = false)
            return false
        }
        return true
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as NoticeListener
        } catch (e: ClassCastException) {
            throw ClassCastException(this.toString()
                    + " must implement NoticeListener")
        }
    }

    interface NoticeListener {
        fun onSubmitIncreasePrice()
    }
}