package com.jobtick.android.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import com.android.volley.*
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
import com.jobtick.android.models.OfferModel
import com.jobtick.android.models.TaskModel
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.models.calculation.PayingCalculationModel
import com.jobtick.android.models.response.getbalance.CreditCardModel
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.FireBaseEvent
import com.jobtick.android.utils.ImageUtil
import com.mikhaellopez.circularimageview.CircularImageView
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class PaymentOverviewActivity : ActivityBase(), PosterRequirementsBottomSheet.NoticeListener, SubmitListener {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var imgAvatar: CircularImageView
    private lateinit var txtUserName: MaterialTextView
    private lateinit var txtPostTitle: MaterialTextView
    private lateinit var txtDiscountFee: MaterialTextView
    private lateinit var txtTaskCost: MaterialTextView
    private lateinit var btnDeleteCoupon: MaterialButton
    private lateinit var txtServiceFee: MaterialTextView
    private lateinit var txtWallet: MaterialTextView
    private lateinit var txtWalletTitle: MaterialTextView
    private lateinit var txtTotalCost: MaterialTextView
    private lateinit var tvCoupon: MaterialTextView
    private lateinit var imgBrand: CardView
    private lateinit var txtAccountNumber: MaterialTextView
    private lateinit var txtExpiresDate: MaterialTextView
    private lateinit var btnNew: MaterialButton
    private lateinit var rltPaymentMethod: RelativeLayout
    private lateinit var btnPay: MaterialButton
    private lateinit var lytAddCreditCard: LinearLayout
    private lateinit var llCoupon: LinearLayout
    private lateinit var relCouponDetails: RelativeLayout
    private lateinit var cardViewUser: LinearLayout

    private var taskModel: TaskModel? = null
    private var offerModel: OfferModel? = null
    private var userAccountModel: UserAccountModel? = null
    private var coupon: String? = null
    private var gson: Gson? = null
    private val stateRequirement = HashMap<PosterRequirementsBottomSheet.Requirement, Boolean>()
    private var wallet = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_overview)
        setIDs()
        initToolbar()
        userAccountModel = sessionManager.userAccount
        offerModel = OfferModel()
        taskModel = TaskDetailsActivity.taskModel!!
        offerModel = TaskDetailsActivity.offerModel
        gson = Gson()
        setUpData()
        paymentMethod
        onViewClick()
    }

    private fun setIDs() {
        toolbar = findViewById(R.id.toolbar)
        imgAvatar = findViewById(R.id.img_avatar)
        txtUserName = findViewById(R.id.txt_user_name)
        txtPostTitle = findViewById(R.id.txt_post_title)
        txtDiscountFee = findViewById(R.id.txt_discount_fee)
        txtTaskCost = findViewById(R.id.txt_task_cost)
        btnDeleteCoupon = findViewById(R.id.btnDeleteCoupon)
        txtServiceFee = findViewById(R.id.txt_service_fee)
        txtWallet = findViewById(R.id.txt_wallet_value)
        txtWalletTitle = findViewById(R.id.txt_wallet_title)
        txtTotalCost = findViewById(R.id.txt_total_cost)
        tvCoupon = findViewById(R.id.tvCoupon)
        imgBrand = findViewById(R.id.img_brand)
        txtAccountNumber = findViewById(R.id.txt_account_number)
        txtExpiresDate = findViewById(R.id.txt_expires_date)
        btnNew = findViewById(R.id.btn_new)
        rltPaymentMethod = findViewById(R.id.rlt_payment_method)
        btnPay = findViewById(R.id.btn_pay)
        lytAddCreditCard = findViewById(R.id.lyt_add_credit_card)
        llCoupon = findViewById(R.id.llCoupon)
        relCouponDetails = findViewById(R.id.relCouponDetails)
        cardViewUser = findViewById(R.id.card_view_user)
    }

    var addCouponFragment: AddCouponFragment? = null
    private fun setupCoupon(netPayingAmount: Int) {
        llCoupon.setOnClickListener { v: View? ->
            addCouponFragment = AddCouponFragment.newInstance(netPayingAmount)
            addCouponFragment!!.show(supportFragmentManager, "")
        }
        if (coupon != null) {
            tvCoupon.text = coupon
            relCouponDetails.visibility = View.VISIBLE
        }
        btnDeleteCoupon.setOnClickListener { v: View? ->
            coupon = null
            relCouponDetails.visibility = View.GONE
            llCoupon.visibility = View.VISIBLE
            calculate(offerModel!!.offerPrice.toString() + "")
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
        txtPostTitle.text = taskModel!!.title
        txtUserName.text = taskModel!!.poster.name
        txtTaskCost.text = String.format(Locale.ENGLISH, "$%d", offerModel!!.offerPrice)
        if (taskModel!!.poster.avatar != null && taskModel!!.poster.avatar.thumbUrl != null) {
            ImageUtil.displayImage(imgAvatar, taskModel!!.poster.avatar.thumbUrl, null)
        } else {
            //TODO set default image
        }
        stateRequirement[PosterRequirementsBottomSheet.Requirement.CreditCard] = false
        setupCoupon(offerModel!!.offerPrice)
        calculate(offerModel!!.offerPrice.toFloat().toString())
    }// map1.put("X-Requested-With", "XMLHttpRequest");// Print Error!

    //http request
    private val paymentMethod: Unit
        get() {
            showProgressDialog()
            val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.URL_PAYMENTS_METHOD,
                    Response.Listener { response: String? ->
                        Timber.e(response)
                        try {
                            val jsonObject = JSONObject(response!!)
                            Timber.e(jsonObject.toString())
                            if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                                if (jsonObject.getBoolean("success")) {
                                    if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                        val jsonString = jsonObject.toString() //http request
                                        val gson = Gson()
                                        val creditCardModel = gson.fromJson(jsonString, CreditCardModel::class.java)
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
            }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                    map1["Content-Type"] = "application/x-www-form-urlencoded"
                    map1["Version"] = BuildConfig.VERSION_CODE.toString()
                    // map1.put("X-Requested-With", "XMLHttpRequest");
                    return map1
                }
            }
            stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(stringRequest)
        }

    private fun setUpAddPaymentLayout() {
        btnPay.isEnabled = false
        btnPay.alpha = 0.5f
        rltPaymentMethod.visibility = View.GONE
        lytAddCreditCard.visibility = View.VISIBLE
        txtWallet.visibility = View.GONE
        txtWalletTitle.visibility = View.GONE
        hideProgressDialog()
    }

    private fun setUpLayout(creditCardModel: CreditCardModel) {
        btnPay.isEnabled = true
        btnPay.alpha = 1.0f
        lytAddCreditCard.visibility = View.GONE
        txtAccountNumber.text = String.format("**** **** **** %s", creditCardModel.data!![0].card!!.last4)
        txtExpiresDate.text = String.format(Locale.ENGLISH, "Expiry Date: %d/%d", creditCardModel.data!![0].card!!.exp_month, creditCardModel.data!![0].card!!.exp_year)
        rltPaymentMethod.visibility = View.VISIBLE
        if (creditCardModel.data!![1].wallet != null) {
            txtWallet.visibility = View.VISIBLE
            txtWalletTitle.visibility = View.VISIBLE
            txtWalletTitle.text = "Wallet Balance"
            wallet = creditCardModel.data!![1].wallet!!.balance!!.toDouble()
            txtWallet.text = String.format(Locale.ENGLISH, "$ %.1f", wallet)
        } else {
            throw IllegalStateException("There is no wallet value in api using provided format of object.")
        }
        calculate(offerModel!!.offerPrice.toFloat().toString())
    }

    private fun onViewClick() {
        btnPay.setOnClickListener {
            payAcceptOffer(coupon)
        }
        btnNew.setOnClickListener {
            showCreditCardRequirementBottomSheet()
            setUpAddPaymentLayout()
        }
        lytAddCreditCard.setOnClickListener {
            showCreditCardRequirementBottomSheet()
        }
    }

    private fun showCreditCardRequirementBottomSheet() {
        val posterRequirementsBottomSheet = PosterRequirementsBottomSheet.newInstance(stateRequirement)
        posterRequirementsBottomSheet.show(supportFragmentManager, "")
    }

    private fun payAcceptOffer(coupon: String?) {
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Constant.URL_TASKS + "/" + taskModel!!.slug + "/accept-offer",
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
                                        if (jsonObjectData.getString("status").equals("assigned", ignoreCase = true)) {
                                            hideProgressDialog()
                                            FireBaseEvent.getInstance(applicationContext)
                                                    .sendEvent(FireBaseEvent.Event.PAYMENT_OVERVIEW,
                                                            FireBaseEvent.EventType.API_RESPOND_SUCCESS,
                                                            FireBaseEvent.EventValue.PAYMENT_OVERVIEW_SUBMIT)
                                            var intent = Intent()
                                            var bundle = Bundle()
                                            bundle.putBoolean(ConstantKey.PAYMENT_OVERVIEW, true)
                                            intent.putExtras(bundle)
                                            setResult(ConstantKey.RESULTCODE_PAYMENTOVERVIEW, intent)
                                            intent = Intent(this@PaymentOverviewActivity, CompleteMessageActivity::class.java)
                                            bundle = Bundle()
                                            bundle.putString(ConstantKey.COMPLETES_MESSAGE_TITLE, "Your payment is secured, and you will be requested to release it after completion!")
                                            bundle.putString(ConstantKey.COMPLETES_MESSAGE_SUBTITLE, "Wait for an answer or continue looking for more tasks!")
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
                }) {
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
                map1["offer_id"] = offerModel!!.id.toString()
                if (coupon != null) map1["discount_code"] = coupon
                Timber.e(map1.size.toString())
                Timber.e(map1.toString())
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this@PaymentOverviewActivity)
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
    }

    fun calculate(amount: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Constant.URL_OFFERS_PAYING_CALCULATION,
                Response.Listener { response: String? ->
                    hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response!!)
                        val data = jsonObject.getString("data")
                        val model = gson!!.fromJson(data, PayingCalculationModel::class.java)
                        txtServiceFee.text = String.format(Locale.ENGLISH, "$%.1f", model.serviceFee)
                        txtDiscountFee.text = String.format(Locale.ENGLISH, "$%.1f", model.discount)
                        if (model.netPayingAmount - wallet >= 0) {
                            if (wallet > 0) txtWallet.text = String.format(Locale.ENGLISH, "-$%.1f", wallet) else txtWallet.text = String.format(Locale.ENGLISH, "$%.1f", Math.abs(wallet))
                            txtTotalCost.text = String.format(Locale.ENGLISH, "$%.1f", model.netPayingAmount - wallet)
                        } else {
                            txtTotalCost.text = String.format(Locale.ENGLISH, "$%.1f", 0f)
                            txtWallet.text = String.format(Locale.ENGLISH, "-$%.1f", model.netPayingAmount)
                        }
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
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
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
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this@PaymentOverviewActivity)
        requestQueue.add(stringRequest)
    }

    override fun onCreditCardAdded() {
        paymentMethod
    }

    override fun onVerifySubmit(coupon: String?) {
        calculate(offerModel!!.offerPrice.toString() + "")
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