package com.jobtick.android.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.fragments.PosterRequirementsBottomSheet
import com.jobtick.android.fragments.others.AddCouponFragment
import com.jobtick.android.fragments.others.AddCouponFragment.SubmitListener
import com.jobtick.android.material.ui.jobdetails.PaymentData
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.models.calculation.PayingCalculationModel
import com.jobtick.android.models.response.getbalance.CreditCardModel
import com.jobtick.android.utils.*
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.Locale

class PaymentOverviewActivity :
        ActivityBase(),
        PosterRequirementsBottomSheet.NoticeListener,
        SubmitListener {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var txtTaskCost: MaterialTextView
    private lateinit var btnDeleteCoupon: MaterialButton
    private lateinit var txtServiceFee: MaterialTextView
    private lateinit var txtTotalCost: MaterialTextView
    private lateinit var tvCoupon: MaterialTextView
    private lateinit var txtAccountNumber: MaterialTextView
    private lateinit var rltPaymentMethod: LinearLayout
    private lateinit var btnPay: MaterialButton
    private lateinit var lytAddCreditCard: LinearLayout
    private lateinit var llCoupon: LinearLayout
    private lateinit var relCouponDetails: RelativeLayout
    private lateinit var msgHeader: MaterialTextView
    private lateinit var msgAction: MaterialTextView
    private lateinit var msgBody: MaterialTextView
    private lateinit var msgIcon: AppCompatImageView
    private lateinit var change: AppCompatImageView
    private var paymentData: PaymentData? = null
    private var userAccountModel: UserAccountModel? = null
    private var coupon: String? = null
    private var gson: Gson? = null
    private val stateRequirement = HashMap<PosterRequirementsBottomSheet.Requirement, Boolean>()
    private var wallet = 0.0
    var found: String? = null
    var id: String? = null
    private var amount = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_overview)
        setIDs()
        initToolbar()
        val bundle = intent.extras
        userAccountModel = sessionManager.userAccount
        paymentData = bundle!!.getParcelable(ConstantKey.OFFER)
        // val offerID = bundle.getString(ConstantKey.OFFER)
        gson = Gson()
        found = bundle.getString("found")
        id = bundle.getString("id")
        amount = paymentData!!.amount
        if (found != null) {
            llCoupon.visibility = View.GONE
            amount = found!!.toInt()
        }
        setUpData()
        paymentMethod
        onViewClick()
    }

    private fun setIDs() {
        toolbar = findViewById(R.id.toolbar)
        txtTaskCost = findViewById(R.id.txt_task_cost)
        btnDeleteCoupon = findViewById(R.id.btnDeleteCoupon)
        txtServiceFee = findViewById(R.id.txt_service_fee)
        txtTotalCost = findViewById(R.id.txt_total_cost)
        tvCoupon = findViewById(R.id.tvCoupon)
        txtAccountNumber = findViewById(R.id.txt_account_number)
        rltPaymentMethod = findViewById(R.id.rlt_payment_method)
        btnPay = findViewById(R.id.btn_pay)
        lytAddCreditCard = findViewById(R.id.lyt_add_credit_card)
        llCoupon = findViewById(R.id.llCoupon)
        relCouponDetails = findViewById(R.id.relCouponDetails)
        msgHeader = findViewById(R.id.msg_header)
        msgBody = findViewById(R.id.msg_body)
        msgAction = findViewById(R.id.msg_action)
        msgIcon = findViewById(R.id.msg_icon)
        change = findViewById(R.id.change)
        msgAction.gone()
    }

    var addCouponFragment: AddCouponFragment? = null
    private fun setupCoupon(netPayingAmount: Int) {
        llCoupon.setOnClickListener {
            addCouponFragment = AddCouponFragment.newInstance(netPayingAmount)
            addCouponFragment!!.show(supportFragmentManager, "")
        }
        if (coupon != null) {
            tvCoupon.text = coupon
            relCouponDetails.visibility = View.VISIBLE
        }
        btnDeleteCoupon.setOnClickListener {
            coupon = null
            relCouponDetails.visibility = View.GONE
            llCoupon.visibility = View.VISIBLE
            calculate(amount.toString() + "")
        }
    }

    private fun initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Payment Overview"
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setUpData() {
        txtTaskCost.text = String.format(Locale.ENGLISH, "$%d", amount)
        stateRequirement[PosterRequirementsBottomSheet.Requirement.CreditCard] = false
        setupCoupon(amount)
        calculate(amount.toFloat().toString())
        msgBody.text = "You will be requested to release it after job completion."
        msgHeader.text = "Jobtick Secure Payment"
        msgIcon.setImageResource(R.drawable.ic_payment_sec)
    } // map1.put("X-Requested-With", "XMLHttpRequest");// Print Error!

    // http request
    private val paymentMethod: Unit
        get() {
            showProgressDialog()
            val stringRequest: StringRequest =
                    object : StringRequest(
                            Method.GET, Constant.URL_PAYMENTS_METHOD,
                            Response.Listener { response: String? ->
                                Timber.e(response)
                                try {
                                    val jsonObject = JSONObject(response!!)
                                    Timber.e(jsonObject.toString())
                                    if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                                        if (jsonObject.getBoolean("success")) {
                                            if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                                val jsonString = jsonObject.toString() // http request
                                                val gson = Gson()
                                                val creditCardModel =
                                                        gson.fromJson(jsonString, CreditCardModel::class.java)
                                                if (creditCardModel != null && creditCardModel.data!![0].card != null) {
                                                    setUpLayout(creditCardModel)
                                                } else {
                                                    setUpAddPaymentLayout()
                                                }
                                            }
                                        } else {
                                            setUpAddPaymentLayout()
                                            showToast("Something went Wrong", this)
                                        }
                                    }
                                } catch (e: JSONException) {
                                    Timber.e(e.toString())
                                    e.printStackTrace()
                                    setUpAddPaymentLayout()
                                }
                            },
                            Response.ErrorListener { error: VolleyError ->
                                setUpAddPaymentLayout()
                                val networkResponse = error.networkResponse
                                if (networkResponse?.data != null) {
                                    val jsonError = String(networkResponse.data)
                                    // Print Error!
                                    Timber.e(jsonError)
                                    try {
                                        val jsonObject = JSONObject(jsonError)
                                        val jsonObjectError = jsonObject.getJSONObject("error")
                                        if (jsonObjectError.has("error_code") && !jsonObjectError.isNull("error_code")) {
                                            if (ConstantKey.NO_PAYMENT_METHOD == jsonObjectError.getString("error_code")) {
                                                return@ErrorListener
                                            }
                                        }
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }
                                }
                                Timber.e(error.toString())
                                errorHandle1(error.networkResponse)
                            }
                    ) {
                        @Throws(AuthFailureError::class)
                        override fun getHeaders(): Map<String, String> {
                            val map1: MutableMap<String, String> = HashMap()
                            map1["authorization"] =
                                    sessionManager.tokenType + " " + sessionManager.accessToken
                            map1["Content-Type"] = "application/x-www-form-urlencoded"
                            map1["Version"] = BuildConfig.VERSION_CODE.toString()
                            // map1.put("X-Requested-With", "XMLHttpRequest");
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

    private fun setUpAddPaymentLayout() {
        btnPay.isEnabled = false
        btnPay.alpha = 0.5f
        rltPaymentMethod.visibility = View.GONE
        lytAddCreditCard.visibility = View.GONE
        hideProgressDialog()
    }

    private fun setUpLayout(creditCardModel: CreditCardModel) {
        btnPay.isEnabled = true
        btnPay.alpha = 1.0f
        lytAddCreditCard.visibility = View.VISIBLE
        txtAccountNumber.text =
                String.format("**** **** **** %s", creditCardModel.data!![0].card!!.last4)
        rltPaymentMethod.visibility = View.VISIBLE
        /* if (creditCardModel.data!![1].wallet != null) {
             txtWallet.visibility = View.VISIBLE
             txtWalletTitle.visibility = View.VISIBLE
             txtWalletTitle.text = "Wallet Balance"
             wallet = creditCardModel.data!![1].wallet!!.balance!!.toDouble()
             txtWallet.text = String.format(Locale.ENGLISH, "$ %.1f", wallet)
         } else {
             throw IllegalStateException("There is no wallet value in api using provided format of object.")
         }*/
        calculate(amount.toFloat().toString())
    }

    private fun onViewClick() {
        btnPay.setOnClickListener {
            if (found == null) {
                payAcceptOffer(coupon)
            } else {
                acceptRequest(id!!)
            }
        }
        txtAccountNumber.setOnClickListener {
            showCreditCardRequirementBottomSheet()
            setUpAddPaymentLayout()
        }
        change.setOnClickListener {
            showCreditCardRequirementBottomSheet()
            setUpAddPaymentLayout()
        }
        lytAddCreditCard.setOnClickListener {
            showCreditCardRequirementBottomSheet()
        }
    }

    private fun showCreditCardRequirementBottomSheet() {
        val posterRequirementsBottomSheet =
                PosterRequirementsBottomSheet.newInstance(stateRequirement)
        posterRequirementsBottomSheet.show(supportFragmentManager, "")
    }

    private fun payAcceptOffer(coupon: String?) {
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(
                Method.POST,
                Constant.URL_TASKS + "/" + paymentData!!.slug + "/accept-offer",
                Response.Listener { response ->
                    Timber.e(response)
                    //   hidepDialog();
                    try {
                        val jsonObject = JSONObject(response!!)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                    val jsonObjectData = jsonObject.getJSONObject("data")
                                    if (jsonObjectData.has("status") && !jsonObjectData.isNull("status")) {
                                        if (jsonObjectData.getString("status")
                                                        .equals("assigned", ignoreCase = true)
                                        ) {
                                            hideProgressDialog()
                                            FireBaseEvent.getInstance(applicationContext)
                                                    .sendEvent(
                                                            FireBaseEvent.Event.PAYMENT_OVERVIEW,
                                                            FireBaseEvent.EventType.API_RESPOND_SUCCESS,
                                                            FireBaseEvent.EventValue.PAYMENT_OVERVIEW_SUBMIT
                                                    )
                                            var intent = Intent()
                                            var bundle = Bundle()
                                            bundle.putBoolean(ConstantKey.PAYMENT_OVERVIEW, true)
                                            intent.putExtras(bundle)
                                            setResult(ConstantKey.RESULTCODE_PAYMENTOVERVIEW, intent)
                                            intent = Intent(
                                                    this@PaymentOverviewActivity,
                                                    CompleteMessageActivity::class.java
                                            )
                                            bundle = Bundle()
                                            bundle.putString(
                                                    ConstantKey.COMPLETES_MESSAGE_TITLE,
                                                    "Your payment is secured, and you will be requested to release it after completion!"
                                            )
                                            bundle.putString(
                                                    ConstantKey.COMPLETES_MESSAGE_SUBTITLE,
                                                    "Wait for an answer or continue looking for more tasks!"
                                            )
                                            intent.putExtras(bundle)
                                            startActivity(intent)
                                            finish()
                                            return@Listener
                                        }
                                    }
                                }
                                hideProgressDialog()
                            } else {
                                hideProgressDialog()
                                showToast("Something went Wrong", this@PaymentOverviewActivity)
                            }
                        } else {
                            showToast("Payment failed", this@PaymentOverviewActivity)
                        }
                    } catch (e: JSONException) {
                        Timber.e(e.toString())
                        e.printStackTrace()
                        hideProgressDialog()
                        showToast("Something went wrong", this@PaymentOverviewActivity)
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    hideProgressDialog()
                    val networkResponse = error.networkResponse
                    if (networkResponse?.data != null) {
                        val jsonError = String(networkResponse.data)
                        try {
                            val jsonObject = JSONObject(jsonError)
                            val jsonObjectError = jsonObject.getJSONObject("error")
                            val message = jsonObjectError.getString("message")
                            showToast(message, this@PaymentOverviewActivity)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            showToast("Something went wrong", this@PaymentOverviewActivity)
                        }
                    } else {
                        showToast("Something went wrong", this@PaymentOverviewActivity)
                    }
                }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                // map1.put("X-Requested-With", "XMLHttpRequest");
                return map1
            }

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["offer_id"] = paymentData!!.offerId.toString()
                if (coupon != null) map1["discount_code"] = coupon
                Timber.e(map1.size.toString())
                Timber.e(map1.toString())
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(
                0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(this@PaymentOverviewActivity)
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
    }

    private fun acceptRequest(id: String) {
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(
                Method.GET,
                Constant.BASE_URL + Constant.URL_ADDITIONAL_FUND + "/" + id + "/accept",
                Response.Listener { response: String? ->
                    Timber.e(response)
                    hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response)
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                found = null
                                onBackPressed()
                            } else {
                                showToast(
                                        "Something went Wrong",
                                        this
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
                            val jsonObject = JSONObject(jsonError)
                            val jsonObject_error = jsonObject.getJSONObject("error")
                            if (jsonObject_error.has("errors")) {
                                val jsonObject_errors =
                                        jsonObject_error.getJSONObject("errors")
                                if (jsonObject_errors.has("amount") && !jsonObject_errors.isNull("amount")) {
                                    val jsonArray_amount =
                                            jsonObject_errors.getJSONArray("amount")
                                    showToast(
                                            jsonArray_amount.getString(
                                                    0
                                            ),
                                            this
                                    )
                                } else if (jsonObject_errors.has("creation_reason") && !jsonObject_errors.isNull(
                                                "creation_reason"
                                        )
                                ) {
                                    val jsonArray_amount =
                                            jsonObject_errors.getJSONArray("creation_reason")
                                    showToast(
                                            jsonArray_amount.getString(
                                                    0
                                            ),
                                            this
                                    )
                                }
                            } else {
                                if (jsonObject_error.has("message")) {
                                    showToast(
                                            jsonObject_error.getString(
                                                    "message"
                                            ),
                                            this
                                    )
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        showToast(
                                "Something Went Wrong",
                                this
                        )
                    }
                    Timber.e(error.toString())
                    hideProgressDialog()
                }
        ) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = java.util.HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
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
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    fun calculate(amount: String) {
        val stringRequest: StringRequest =
                object : StringRequest(
                        Method.POST, Constant.URL_OFFERS_PAYING_CALCULATION,
                        Response.Listener { response: String? ->
                            hideProgressDialog()
                            try {
                                val jsonObject = JSONObject(response!!)
                                val data = jsonObject.getString("data")
                                val model = gson!!.fromJson(data, PayingCalculationModel::class.java)
                                txtServiceFee.text =
                                        String.format(Locale.ENGLISH, "$%.1f", model.serviceFee)
                                txtTotalCost.text = String.format(
                                        Locale.ENGLISH,
                                        "$%.1f",
                                        model.netPayingAmount)
                                //txtDiscountFee.text = String.format(Locale.ENGLISH, "$%.1f", model.discount)
                                /*if (model.netPayingAmount - wallet >= 0) {
                                    if (wallet > 0) txtWallet.text = String.format(
                                            Locale.ENGLISH,
                                            "-$%.1f",
                                            wallet
                                    ) else txtWallet.text =
                                            String.format(Locale.ENGLISH, "$%.1f", Math.abs(wallet))
                                    txtTotalCost.text = String.format(
                                            Locale.ENGLISH,
                                            "$%.1f",
                                            model.netPayingAmount - wallet
                                    )
                                } else {
                                    txtTotalCost.text = String.format(Locale.ENGLISH, "$%.1f", 0f)
                                    txtWallet.text =
                                            String.format(Locale.ENGLISH, "-$%.1f", model.netPayingAmount)
                                }*/
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        },
                        Response.ErrorListener { error: VolleyError ->
                            hideProgressDialog()
                            val networkResponse = error.networkResponse
                            if (networkResponse?.data != null) {
                                val jsonError = String(networkResponse.data)
                                try {
                                    val jsonObject = JSONObject(jsonError)
                                    val jsonObjectError = jsonObject.getJSONObject("error")
                                    val message = jsonObjectError.getString("message")
                                    showToast(message, this@PaymentOverviewActivity)
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                    showToast("Something went wrong", this@PaymentOverviewActivity)
                                }
                            } else {
                                showToast("Something went wrong", this@PaymentOverviewActivity)
                            }
                        }
                ) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val map1: MutableMap<String, String> = HashMap()
                        map1["authorization"] =
                                sessionManager.tokenType + " " + sessionManager.accessToken
                        map1["Content-Type"] = "application/x-www-form-urlencoded"
                        map1["X-Requested-With"] = "XMLHttpRequest"
                        map1["Version"] = BuildConfig.VERSION_CODE.toString()
                        return map1
                    }

                    override fun getParams(): Map<String, String> {
                        val map1: MutableMap<String, String> = HashMap()
                        map1["amount"] = amount
                        if (coupon != null) map1["discount_code"] = coupon!!
                        return map1
                    }
                }
        stringRequest.retryPolicy = DefaultRetryPolicy(
                0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(this@PaymentOverviewActivity)
        requestQueue.add(stringRequest)
    }

    override fun onCreditCardAdded() {
        paymentMethod
    }

    override fun onVerifySubmit(coupon: String?) {
        calculate(amount.toString() + "")
        this.coupon = coupon
        tvCoupon.text = coupon
        llCoupon.visibility = View.GONE
        relCouponDetails.visibility = View.VISIBLE
        if (addCouponFragment != null) addCouponFragment!!.dismiss()
    }

    override fun onClose() {
        coupon = null
        if (addCouponFragment != null) addCouponFragment!!.dismiss()
    }

    companion object {
        private val TAG = PaymentOverviewActivity::class.java.simpleName
    }
}
