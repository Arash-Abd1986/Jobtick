package com.jobtick.android.fragments.others

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.fragments.AbstractStateExpandedBottomSheet
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.SessionManager
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class AddCouponFragment : AbstractStateExpandedBottomSheet() {
    var sessionManager: SessionManager? = null
    var isVerified = false
    private var listener: SubmitListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_BottomSheetDialog)
    }

    private lateinit var btnVerify: Button
    private lateinit var tvError: TextView
    private lateinit var etPromoCode: EditText
    private lateinit var ivState: ImageView
    private lateinit var pbLoading: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_coupon, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(activity)
        setupPromotionCodeChecker()
        btnVerify.setOnClickListener { v: View? -> verify() }
    }
    interface SubmitListener {
        fun onVerifySubmit(coupon: String?)
        fun onClose()
    }

    private fun verify() {
        if (etPromoCode.text.isNotEmpty()) {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)
            ivState.visibility = View.GONE
            pbLoading.visibility = View.VISIBLE
            checkPromoCode()
        }
    }

    private fun setupPromotionCodeChecker() {
        btnVerify = requireView().findViewById(R.id.btnVerify)
        tvError = requireView().findViewById(R.id.tvError)
        etPromoCode = requireView().findViewById(R.id.etPromoCode)
        ivState = requireView().findViewById(R.id.ivState)
        pbLoading = requireView().findViewById(R.id.pbLoading)
    }

    private fun checkPromoCode() {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Constant.URL_OFFERS_PAYING_CALCULATION,
                Response.Listener { response: String? ->
                    pbLoading.visibility = View.GONE
                    try {
                        val jsonObject = JSONObject(response!!)
                        val data = jsonObject.getString("data")
                        isVerified = true
                        ivState.visibility = View.VISIBLE
                        ivState.setImageResource(R.drawable.ic_verified_coupon)
                        pbLoading.visibility = View.GONE
                        tvError.visibility = View.GONE
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        isVerified = false
                        tvError.visibility = View.GONE
                        ivState.setImageResource(R.drawable.ic_unverified_coupon)
                        ivState.visibility = View.VISIBLE
                    }
                    if (isVerified)
                        Handler().postDelayed({
                            listener!!.onVerifySubmit(etPromoCode.text.toString())
                        },2000)
                },
                Response.ErrorListener { error: VolleyError ->
                    pbLoading.visibility = View.GONE
                    isVerified = false
                    val networkResponse = error.networkResponse
                    if (networkResponse?.data != null) {
                        val jsonError = String(networkResponse.data)
                        try {
                            val jsonObject = JSONObject(jsonError)
                            val jsonObjectError = jsonObject.getJSONObject("error")
                            val message = jsonObjectError.getString("message")
                            tvError.text = message
                            tvError.visibility = View.VISIBLE
                            ivState.setImageResource(R.drawable.ic_unverified_coupon)
                            ivState.visibility = View.VISIBLE
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            tvError.visibility = View.GONE
                            ivState.setImageResource(R.drawable.ic_unverified_coupon)
                            ivState.visibility = View.VISIBLE
                        }
                    } else {
                        tvError.visibility = View.GONE
                        ivState.setImageResource(R.drawable.ic_unverified_coupon)
                        ivState.visibility = View.VISIBLE
                    }
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
                if (amount != null) map1["amount"] = amount.toString()
                map1["discount_code"] = etPromoCode.text.toString()
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(activity)
        requestQueue.add(stringRequest)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as SubmitListener
        } catch (e: ClassCastException) {
            throw ClassCastException(this.toString()
                    + " must implement NoticeListener")
        }
    }

    @Deprecated("")
    private fun checkPromoCodeOld() {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Constant.URL_CHECK_COUPON,
                Response.Listener { response: String? ->
                    pbLoading.visibility = View.GONE
                    try {
                        val jsonObject = JSONObject(response!!)
                        val data = jsonObject.getString("data")
                        ivState.visibility = View.VISIBLE
                        ivState.setImageResource(R.drawable.ic_verified_coupon)
                        pbLoading.visibility = View.GONE
                        tvError.visibility = View.GONE
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        tvError.visibility = View.GONE
                        ivState.setImageResource(R.drawable.ic_unverified_coupon)
                        ivState.visibility = View.VISIBLE
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    pbLoading.visibility = View.GONE
                    val networkResponse = error.networkResponse
                    if (networkResponse?.data != null) {
                        val jsonError = String(networkResponse.data)
                        try {
                            val jsonObject = JSONObject(jsonError)
                            val jsonObjectError = jsonObject.getJSONObject("error")
                            val message = jsonObjectError.getString("message")
                            tvError.text = message
                            tvError.visibility = View.VISIBLE
                            ivState.setImageResource(R.drawable.ic_unverified_coupon)
                            ivState.visibility = View.VISIBLE
                        } catch (e: Exception) {
                            tvError.visibility = View.GONE
                            ivState.visibility = View.VISIBLE
                            ivState.setImageResource(R.drawable.ic_unverified_coupon)
                        }
                    }
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
                map1["amount"] = amount.toString()
                map1["coupon"] = etPromoCode.text.toString()
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(activity)
        requestQueue.add(stringRequest)
    }

    companion object {
        var amount: Int? = null
        fun newInstance(netPaying: Int?): AddCouponFragment {
            amount = netPaying
            val args = Bundle()
            val fragment = AddCouponFragment()
            fragment.arguments = args
            return fragment
        }
    }
}