package com.jobtick.android.fragments

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.activities.ActivityBase
import com.jobtick.android.activities.PaymentOverviewActivity
import com.jobtick.android.activities.TaskDetailsActivity
import com.jobtick.android.models.TaskModel
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.HttpStatus
import com.jobtick.android.utils.SessionManager
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.Locale

class IncreaseBudgetNoticeBottomSheet : AbstractStateExpandedBottomSheet() {
    var name: TextView? = null
    var description: TextView? = null
    var oldPrice: TextView? = null
    var newPrice: TextView? = null
    var reason: TextView? = null
    var decline: Button? = null
    var accept: Button? = null
    var btnWithdraw: Button? = null
    var llAcceptDecline: LinearLayout? = null
    var llWithDraw: LinearLayout? = null
    private var pDialog: ProgressDialog? = null
    private var taskModel: TaskModel? = null
    private var sessionManager: SessionManager? = null
    private var listener: NoticeListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_increase_budget_notice, container, false)
        sessionManager = SessionManager(context)
        assert(arguments != null)
        taskModel = TaskDetailsActivity.taskModel
        name = view.findViewById(R.id.name)
        llAcceptDecline = view.findViewById(R.id.lyt_button)
        llWithDraw = view.findViewById(R.id.lytWithDraw)
        description = view.findViewById(R.id.description)
        newPrice = view.findViewById(R.id.new_price)
        oldPrice = view.findViewById(R.id.old_price)
        reason = view.findViewById(R.id.reason_description)
        decline = view.findViewById(R.id.btn_decline)
        accept = view.findViewById(R.id.btn_accept)
        btnWithdraw = view.findViewById(R.id.btnWithdraw)
        decline!!.setOnClickListener {
            listener!!.onIncreaseBudgetRejectClick()
            dismiss()
        }
        accept!!.setOnClickListener {
            dismiss()
            val intent = Intent(requireContext(), PaymentOverviewActivity::class.java)
            val bundle = Bundle()
            bundle.putString("found", taskModel!!.additionalFund.amount.toString())
            bundle.putString("id", taskModel!!.additionalFund.id.toString())
            TaskDetailsActivity.offerModel =
                taskModel!!.offers.filter { it.worker.id == taskModel!!.worker.id }[0]
            intent.putExtras(bundle)
            startActivityForResult(intent, ConstantKey.RESULTCODE_PAYMENTOVERVIEW)
        }
        btnWithdraw!!.setOnClickListener {
            withdrawRequest(
                taskModel!!.additionalFund.id.toString()
            )
        }
        init()
        initProgressDialog()
        return view
    }

    private fun withdrawRequest(id: String) {
        (requireActivity() as ActivityBase).showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(
            Method.DELETE,
            Constant.BASE_URL + Constant.URL_ADDITIONAL_FUND + "/" + id,
            Response.Listener { response: String? ->
                Timber.e(response)
                (requireActivity() as ActivityBase).hideProgressDialog()
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                        if (jsonObject.getBoolean("success")) {
                            listener!!.onIncreaseBudgetWithDrawClick()
                            dismiss()
                        } else {
                            (requireActivity() as ActivityBase).showToast(
                                "Something went Wrong",
                                context
                            )
                        }
                    }
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
                    if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                        (requireActivity() as ActivityBase).unauthorizedUser()
                        (requireActivity() as ActivityBase).hideProgressDialog()
                        return@ErrorListener
                    }
                    try {
                        val jsonObject = JSONObject(jsonError)
                        val jsonObjectError = jsonObject.getJSONObject("error")
                        if (jsonObjectError.has("errors")) {
                            val jsonObjectErrors = jsonObjectError.getJSONObject("errors")
                            if (jsonObjectErrors.has("amount") && !jsonObjectErrors.isNull("amount")) {
                                val jsonArrayAmount = jsonObjectErrors.getJSONArray("amount")
                                (requireActivity() as ActivityBase).showToast(
                                    jsonArrayAmount.getString(
                                        0
                                    ),
                                    requireContext()
                                )
                            } else if (jsonObjectErrors.has("creation_reason") && !jsonObjectErrors.isNull(
                                    "creation_reason"
                                )
                            ) {
                                val jsonArray_amount =
                                    jsonObjectErrors.getJSONArray("creation_reason")
                                (requireActivity() as ActivityBase).showToast(
                                    jsonArray_amount.getString(
                                        0
                                    ),
                                    requireContext()
                                )
                            }
                        } else {
                            if (jsonObjectError.has("message")) {
                                (requireActivity() as ActivityBase).showToast(
                                    jsonObjectError.getString(
                                        "message"
                                    ),
                                    requireContext()
                                )
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    (requireActivity() as ActivityBase).showToast("Something Went Wrong", context)
                }
                Timber.e(error.toString())
                (requireActivity() as ActivityBase).hideProgressDialog()
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] =
                    sessionManager!!.tokenType + " " + sessionManager!!.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                //   map1.put("X-Requested-With", "XMLHttpRequest");
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            0, -1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)
    }

    private fun init() {
        val oldP = taskModel!!.amount.toString().toInt()
        val newP = taskModel!!.additionalFund.amount.toString().toInt() + oldP
        name!!.text = taskModel!!.worker.name
        description!!.text = taskModel!!.title
        reason!!.text = taskModel!!.additionalFund.creationReason
        newPrice!!.text = String.format(Locale.ENGLISH, "%d", newP)
        oldPrice!!.text = String.format(Locale.ENGLISH, "%d", oldP)
        if (isMine) {
            name!!.text = "You"
            llAcceptDecline!!.visibility = View.GONE
            llWithDraw!!.visibility = View.VISIBLE
        }
    }

    fun initProgressDialog() {
        pDialog = ProgressDialog(requireContext())
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as NoticeListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                this.toString() +
                    " must implement NoticeListener"
            )
        }
    }

    interface NoticeListener {
        fun onIncreaseBudgetAcceptClick()
        fun onIncreaseBudgetRejectClick()
        fun onIncreaseBudgetWithDrawClick()
    }

    companion object {
        var isMine = false
        fun newInstance(taskModel: TaskModel?): IncreaseBudgetNoticeBottomSheet {
            isMine = false
            val bundle = Bundle()
            //    bundle.putParcelable(ConstantKey.TASK, taskModel);
            val fragment = IncreaseBudgetNoticeBottomSheet()
            fragment.arguments = bundle
            return fragment
        }

        fun newInstance(
            taskModel: TaskModel?,
            isMineRequest: Boolean
        ): IncreaseBudgetNoticeBottomSheet {
            isMine = isMineRequest
            val bundle = Bundle()
            //    bundle.putParcelable(ConstantKey.TASK, taskModel);
            val fragment = IncreaseBudgetNoticeBottomSheet()
            fragment.arguments = bundle
            return fragment
        }
    }
}
