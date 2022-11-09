package com.jobtick.android.material.ui.jobdetails

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.activities.ActivityBase
import com.jobtick.android.models.TaskModel
import com.jobtick.android.utils.*
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

class RescheduleNoticeFragment : Fragment() {
    var name: TextView? = null
    var description: TextView? = null
    var previousDate: TextView? = null
    var newTime: TextView? = null
    var reason: TextView? = null
    var decline: Button? = null
    var accept: Button? = null
    var btnWithdraw: Button? = null
    var llAcceptDecline: LinearLayout? = null
    var llWithDraw: LinearLayout? = null
    var close: AppCompatImageView? = null
    private var sessionManager: SessionManager? = null
    private var taskModel: TaskModel? = null
    private var pos = 0
    private var listener: NoticeListener? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_reschedule_notice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        assert(arguments != null)
        taskModel = requireArguments().getParcelable(ConstantKey.TASK)
        pos = requireArguments().getInt(ConstantKey.POSITION)
        setIDs()
        decline!!.setOnClickListener { declineRequest() }
        accept!!.setOnClickListener {
            acceptRequest()
        }
        btnWithdraw!!.setOnClickListener { withdrawRequest() }
        init()
    }

    private fun setIDs() {
        name = requireView().findViewById(R.id.name)
        description = requireView().findViewById(R.id.description)
        llAcceptDecline = requireView().findViewById(R.id.lyt_button)
        llWithDraw = requireView().findViewById(R.id.lytWithDraw)
        previousDate = requireView().findViewById(R.id.txt_previous_date)
        newTime = requireView().findViewById(R.id.txt_new_time)
        reason = requireView().findViewById(R.id.reason_description)
        decline = requireView().findViewById(R.id.btn_decline)
        accept = requireView().findViewById(R.id.btn_accept)
        btnWithdraw = requireView().findViewById(R.id.btnWithdraw)
        close = requireView().findViewById(R.id.close)
    }

    @SuppressLint("SetTextI18n")
    private fun init() {

        description!!.text = taskModel!!.title
        reason!!.text = taskModel!!.rescheduleReqeust[pos].reason
        newTime!!.text =
                TimeHelper.convertToJustDateFormat(taskModel!!.rescheduleReqeust[pos].new_duedate)
        previousDate!!.text = taskModel!!.dueDate + " \u2022 " + if (taskModel!!.dueTime.anytime) getString(R.string.anytime)
        else if (taskModel!!.dueTime.morning) getString(R.string.morning)
        else if (taskModel!!.dueTime.evening) getString(R.string.evening)
        else getString(R.string.afternoon)
        name!!.text = requireArguments().getCharSequence(ConstantKey.COMPLETES_MESSAGE_SUBTITLE)
        when {
            requireArguments().getBoolean(ConstantKey.IS_MY_TASK) -> {
                llAcceptDecline!!.visibility = View.GONE
                llWithDraw!!.visibility = View.VISIBLE
            }
        }
        close!!.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as NoticeListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                    this.toString()
                            + " must implement NoticeListener"
            )
        }
    }

    private fun withdrawRequest() {
        (requireActivity() as ActivityBase).showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.DELETE,
                Constant.BASE_URL + Constant.URL_CREATE_RESCHEDULE + "/" + taskModel!!.rescheduleReqeust[pos].id,
                Response.Listener { response: String? ->
                    Timber.e(response)
                    (requireActivity() as ActivityBase).hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response!!)
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                listener!!.onRescheduleWithDraw()
                                requireActivity().onBackPressed()
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
                                            ), requireContext()
                                    )
                                } else if (jsonObjectErrors.has("creation_reason") && !jsonObjectErrors.isNull(
                                                "creation_reason"
                                        )
                                ) {
                                    val jsonArrayAmount =
                                            jsonObjectErrors.getJSONArray("creation_reason")
                                    (requireActivity() as ActivityBase).showToast(
                                            jsonArrayAmount.getString(
                                                    0
                                            ), requireContext()
                                    )
                                }
                            } else {
                                if (jsonObjectError.has("message")) {
                                    (requireActivity() as ActivityBase).showToast(
                                            jsonObjectError.getString(
                                                    "message"
                                            ), requireContext()
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
                }) {
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

    fun acceptRequest() {
        (requireActivity() as ActivityBase).showProgressDialog()
        val stringRequest: StringRequest = object :
                StringRequest(Method.GET, Constant.BASE_URL + Constant.URL_CREATE_RESCHEDULE + "/" +
                        taskModel!!.rescheduleReqeust[pos].id + "/accept",
                        Response.Listener { response: String? ->
                            Timber.e(response)
                            try {
                                val jsonObject = JSONObject(response!!)
                                if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                                    if (jsonObject.getBoolean("success")) {
                                        listener!!.onRescheduleTimeAcceptDeclineClick()
                                        requireActivity().onBackPressed()

                                    } else {
                                        (requireActivity() as ActivityBase).showToast(
                                                "Something went wrong",
                                                requireContext()
                                        )
                                    }
                                } else {
                                    (requireActivity() as ActivityBase).showToast(
                                            "Something went wrong",
                                            requireContext()
                                    )
                                }
                                (requireActivity() as ActivityBase).hideProgressDialog()
                            } catch (e: JSONException) {
                                (requireActivity() as ActivityBase).hideProgressDialog()
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
                                (requireActivity() as ActivityBase).hideProgressDialog()
                                try {
                                    val jsonObject = JSONObject(jsonError)
                                    val jsonObjectError = jsonObject.getJSONObject("error")
                                    if (jsonObjectError.has("message")) {
                                        (requireActivity() as ActivityBase).showToast(
                                                jsonObjectError.getString(
                                                        "message"
                                                ), requireContext()
                                        )
                                    }
                                } catch (e: JSONException) {
                                    (requireActivity() as ActivityBase).showToast(
                                            "Something Went Wrong",
                                            requireContext()
                                    )
                                    e.printStackTrace()
                                }
                            } else {
                                (requireActivity() as ActivityBase).showToast(
                                        "Something Went Wrong",
                                        requireContext()
                                )
                            }
                            Timber.e(error.toString())
                            (requireActivity() as ActivityBase).hideProgressDialog()
                        }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] =
                        sessionManager!!.tokenType + " " + sessionManager!!.accessToken
                map1["Accept"] = "application/json"
                map1["X-Requested-With"] = "XMLHttpRequest"
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
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

    fun declineRequest() {
        (requireActivity() as ActivityBase).showProgressDialog()
        val stringRequest: StringRequest = object :
                StringRequest(Method.POST, Constant.BASE_URL + Constant.URL_CREATE_RESCHEDULE + "/" +
                        taskModel!!.rescheduleReqeust[pos].id + "/reject",
                        Response.Listener { response: String? ->
                            Timber.e(response)
                            try {
                                val jsonObject = JSONObject(response!!)
                                if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                                    if (jsonObject.getBoolean("success")) {
                                        listener!!.onRescheduleTimeAcceptDeclineClick()
                                        requireActivity().onBackPressed()

                                    } else {
                                        (requireActivity() as ActivityBase).showToast(
                                                "Something went wrong",
                                                requireContext()
                                        )
                                    }
                                } else {
                                    (requireActivity() as ActivityBase).showToast(
                                            "Something went wrong",
                                            requireContext()
                                    )
                                }
                                (requireActivity() as ActivityBase).hideProgressDialog()
                            } catch (e: JSONException) {
                                (requireActivity() as ActivityBase).hideProgressDialog()
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
                                (requireActivity() as ActivityBase).hideProgressDialog()
                                try {
                                    val jsonObject = JSONObject(jsonError)
                                    val jsonObject_error = jsonObject.getJSONObject("error")
                                    if (jsonObject_error.has("message")) {
                                        (requireActivity() as ActivityBase).showToast(
                                                jsonObject_error.getString(
                                                        "message"
                                                ), requireContext()
                                        )
                                    }
                                } catch (e: JSONException) {
                                    (requireActivity() as ActivityBase).showToast(
                                            "Something Went Wrong",
                                            requireContext()
                                    )
                                    e.printStackTrace()
                                }
                            } else {
                                (requireActivity() as ActivityBase).showToast(
                                        "Something Went Wrong",
                                        requireContext()
                                )
                            }
                            Timber.e(error.toString())
                            (requireActivity() as ActivityBase).hideProgressDialog()
                        }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] =
                        sessionManager!!.tokenType + " " + sessionManager!!.accessToken
                map1["Accept"] = "application/json"
                map1["X-Requested-With"] = "XMLHttpRequest"
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }

            override fun getParams(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                //TODO: we put an empty string because there is no declined reason in design.
                map1["declined_reason"] = "There is no declined reason in design, so just fill it."
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

    interface NoticeListener {
        fun onRescheduleTimeAcceptDeclineClick()
        fun onRescheduleWithDraw()
    }
}