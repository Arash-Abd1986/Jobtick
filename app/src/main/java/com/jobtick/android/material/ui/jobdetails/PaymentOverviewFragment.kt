package com.jobtick.android.material.ui.jobdetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.activities.CompleteMessageActivity
import com.jobtick.android.fragments.PosterRequirementsBottomSheet
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.models.calculation.PayingCalculationModel
import com.jobtick.android.models.response.getbalance.CreditCardModel
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.*
import com.jobtick.android.viewmodel.ViewModelFactory
import com.jobtick.android.viewmodel.home.PaymentOverviewViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*


class PaymentOverviewFragment : Fragment(), PosterRequirementsBottomSheet.NoticeListener {

    private lateinit var txtTaskCost: MaterialTextView
    private lateinit var txtServiceFee: MaterialTextView
    private lateinit var txtTotalCost: MaterialTextView
    private lateinit var txtAccountNumber: MaterialTextView
    private lateinit var rltPaymentMethod: LinearLayout
    private lateinit var btnPay: MaterialButton
    private lateinit var btnAddCard: MaterialButton
    private lateinit var msgHeader: MaterialTextView
    private lateinit var msgAction: MaterialTextView
    private lateinit var msgBody: MaterialTextView
    private lateinit var msgIcon: AppCompatImageView
    private lateinit var change: AppCompatImageView
    private lateinit var back: AppCompatImageView
    private lateinit var title: MaterialTextView
    private lateinit var frame: FrameLayout
    private var paymentData: PaymentData? = null
    private var userAccountModel: UserAccountModel? = null
    private var coupon: String? = null
    private var gson: Gson? = null
    private val stateRequirement = HashMap<PosterRequirementsBottomSheet.Requirement, Boolean>()
    private var wallet = 0.0
    var found: String? = null
    var id: String? = null
    private var amount = -1
    lateinit var activity: PaymentOverviewActivity
    lateinit var sessionManager: SessionManager
    lateinit var viewModel: PaymentOverviewViewModel

    var isVerified = false
    private lateinit var btnVerify: Button
    private lateinit var etPromoCode: TextInputLayout
    private lateinit var ivState: ImageView
    private lateinit var pbLoading: ProgressBar


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_payment_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setIDs()
        activity = (requireActivity() as PaymentOverviewActivity)
        sessionManager = SessionManager(requireContext())
        userAccountModel = sessionManager.userAccount
        // val offerID = bundle.getString(ConstantKey.OFFER)
        gson = Gson()
        initVm()
        back.setOnClickListener {
            activity.onBackPressed()
        }
        title.text = "Payment Overview"

    }

    private fun initVm() {
        viewModel = ViewModelProvider(requireActivity(), ViewModelFactory(ApiHelper(ApiClient.getClient())))[PaymentOverviewViewModel::class.java]
        lifecycleScope.launch {
            viewModel.state.collectLatest {
                paymentData = it.paymentData
                found = it.found
                id = it.id
                amount = paymentData!!.amount
                if (found != null) {
                    // llCoupon.visibility = View.GONE
                    amount = found!!.toInt()
                }
                setUpData()
                paymentMethod
                onViewClick()
                if (it.isCardDeletedByMe != true)
                    if (it.isCardDeleted?.not() == true) {
                        rltPaymentMethod.visibility = View.VISIBLE
                        btnAddCard.visibility = View.GONE
                    } else {
                        rltPaymentMethod.visibility = View.GONE
                        btnAddCard.visibility = View.VISIBLE
                    }
                else{
                    rltPaymentMethod.visibility = View.GONE
                    btnAddCard.visibility = View.VISIBLE
                }
            }
        }

    }

    private fun verify() {
        if (etPromoCode.editText!!.text.isNotEmpty()) {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)
            ivState.visibility = View.GONE
            pbLoading.visibility = View.VISIBLE
            checkPromoCode()
        }
    }

    private fun checkPromoCode() {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Constant.URL_OFFERS_PAYING_CALCULATION, Response.Listener { response: String? ->
            pbLoading.visibility = View.GONE
            try {
                val jsonObject = JSONObject(response!!)
                val data = jsonObject.getString("data")
                isVerified = true
                ivState.visibility = View.VISIBLE
                ivState.setImageResource(R.drawable.ic_verified_coupon)
                pbLoading.visibility = View.GONE
                etPromoCode.error = ""
            } catch (e: JSONException) {
                e.printStackTrace()
                isVerified = false
                etPromoCode.error = ""
                ivState.setImageResource(R.drawable.ic_unverified_coupon)
                ivState.visibility = View.VISIBLE
            }
            if (isVerified) Handler().postDelayed({
                onVerifySubmit(etPromoCode.editText!!.text.toString())
            }, 2000)
        }, Response.ErrorListener { error: VolleyError ->
            pbLoading.visibility = View.GONE
            isVerified = false
            val networkResponse = error.networkResponse
            if (networkResponse?.data != null) {
                val jsonError = String(networkResponse.data)
                try {
                    val jsonObject = JSONObject(jsonError)
                    val jsonObjectError = jsonObject.getJSONObject("error")
                    val message = jsonObjectError.getString("message")
                    etPromoCode.error = message

                    ivState.setImageResource(R.drawable.ic_unverified_coupon)
                    ivState.visibility = View.VISIBLE
                } catch (e: JSONException) {
                    e.printStackTrace()
                    etPromoCode.error = ""
                    ivState.setImageResource(R.drawable.ic_unverified_coupon)
                    ivState.visibility = View.VISIBLE
                }
            } else {
                etPromoCode.error = ""
                ivState.setImageResource(R.drawable.ic_unverified_coupon)
                ivState.visibility = View.VISIBLE
            }
        }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager!!.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["X-Requested-With"] = "XMLHttpRequest"
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }

            override fun getParams(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["amount"] = amount.toString()
                map1["discount_code"] = etPromoCode.editText!!.text.toString()
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(activity)
        requestQueue.add(stringRequest)
    }

    private fun setIDs() {
        txtTaskCost = requireView().findViewById(R.id.txt_task_cost)
        txtServiceFee = requireView().findViewById(R.id.txt_service_fee)
        txtTotalCost = requireView().findViewById(R.id.txt_total_cost)
        txtAccountNumber = requireView().findViewById(R.id.txt_account_number)
        rltPaymentMethod = requireView().findViewById(R.id.rlt_payment_method)
        btnPay = requireView().findViewById(R.id.btn_pay)
        msgHeader = requireView().findViewById(R.id.msg_header)
        msgBody = requireView().findViewById(R.id.msg_body)
        msgAction = requireView().findViewById(R.id.msg_action)
        msgIcon = requireView().findViewById(R.id.msg_icon)
        change = requireView().findViewById(R.id.change)
        back = requireView().findViewById(R.id.back)
        title = requireView().findViewById(R.id.title)
        btnAddCard = requireView().findViewById(R.id.btnAddCard)
        btnVerify = requireView().findViewById(R.id.btnVerify)
        etPromoCode = requireView().findViewById(R.id.etPromoCode)
        ivState = requireView().findViewById(R.id.ivState)
        pbLoading = requireView().findViewById(R.id.pbLoading)
        msgAction.gone()
    }

    private fun setupCoupon(netPayingAmount: Int) {
        /* llCoupon.setOnClickListener {
             addCouponFragment = AddCouponFragment.newInstance(netPayingAmount)
             addCouponFragment!!.show(parentFragmentManager, "")
         }*/
        /*if (coupon != null) {
            tvCoupon.text = coupon
            relCouponDetails.visibility = View.VISIBLE
        }*/
        /*btnDeleteCoupon.setOnClickListener {
            coupon = null
            relCouponDetails.visibility = View.GONE
           // llCoupon.visibility = View.VISIBLE
            calculate(amount.toString() + "")
        }*/
    }

    private fun setUpData() {
        txtTaskCost.text = String.format(Locale.ENGLISH, "$%d", amount)
        stateRequirement[PosterRequirementsBottomSheet.Requirement.CreditCard] = false
        calculate(amount.toFloat().toString())
        msgBody.text = "You will be requested to release it after job completion."
        msgHeader.text = "Jobtick Secure Payment"
        msgIcon.setImageResource(R.drawable.ic_payment_sec)
    } // map1.put("X-Requested-With", "XMLHttpRequest");// Print Error!

    // http request


    private fun setUpAddPaymentLayout() {
        btnPay.isEnabled = false
        btnPay.alpha = 0.5f
        viewModel.setIsCardDeleted(true)
        activity.hideProgressDialog()
    }

    private fun setUpLayout(creditCardModel: CreditCardModel) {
        btnPay.isEnabled = true
        btnPay.alpha = 1.0f
        viewModel.setIsCardDeleted(false)
        txtAccountNumber.text = String.format("**** **** **** %s", creditCardModel.data!![0].card!!.last4)

        txtAccountNumber.setOnClickListener {
            val action = PaymentOverviewFragmentDirections.actionPaymentOverviewFragmentToCardInfoFragment(
                    CardInfo(accountHolder = "",
                            cardNumber = String.format("**** **** **** %s", creditCardModel.data!![0].card!!.last4),
                            expiryDate = creditCardModel.data!![0].card!!.exp_month.toString() + "/" + creditCardModel.data!![0].card!!.exp_year,
                            CVC = "***"))

            activity.navController.navigate(action)
        }
        change.setOnClickListener {
            val action = PaymentOverviewFragmentDirections.actionPaymentOverviewFragmentToCardInfoFragment(
                    CardInfo(accountHolder = "",
                            cardNumber = String.format("**** **** **** %s", creditCardModel.data!![0].card!!.last4),
                            expiryDate = creditCardModel.data!![0].card!!.exp_month.toString() + "/" + creditCardModel.data!![0].card!!.exp_year,
                            CVC = "***"))

            activity.navController.navigate(action)
        }
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
        btnAddCard.setOnClickListener {
            showCreditCardRequirementBottomSheet()
            setUpAddPaymentLayout()
        }

        btnVerify.setOnClickListener { verify() }
    }

    private fun showCreditCardRequirementBottomSheet() {
        activity.navController.navigate(R.id.creditCardReqFragment)
    }

    private fun payAcceptOffer(coupon: String?) {
        activity.showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Constant.URL_TASKS + "/" + paymentData!!.slug + "/accept-offer", Response.Listener { response ->
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
                                    activity.hideProgressDialog()
                                   // FireBaseEvent.getInstance(requireContext()).sendEvent(FireBaseEvent.Event.PAYMENT_OVERVIEW, FireBaseEvent.EventType.API_RESPOND_SUCCESS, FireBaseEvent.EventValue.PAYMENT_OVERVIEW_SUBMIT)
                                    var intent = Intent()
                                    var bundle = Bundle()
                                    bundle.putBoolean(ConstantKey.PAYMENT_OVERVIEW, true)
                                    intent.putExtras(bundle)
                                    activity.setResult(ConstantKey.RESULTCODE_PAYMENTOVERVIEW, intent)
                                    intent = Intent(requireContext(), CompleteMessageActivity::class.java)
                                    bundle = Bundle()
                                    bundle.putString(ConstantKey.COMPLETES_MESSAGE_TITLE, "Your payment is secured, and you will be requested to release it after completion!")
                                    bundle.putString(ConstantKey.COMPLETES_MESSAGE_SUBTITLE, "Wait for an answer or continue looking for more tasks!")
                                    intent.putExtras(bundle)
                                    startActivity(intent)
                                    activity.finish()
                                    return@Listener
                                }
                            }
                        }
                        activity.hideProgressDialog()
                    } else {
                        activity.hideProgressDialog()
                        activity.showToast("Something went Wrong", requireContext())
                    }
                } else {
                    activity.showToast("Payment failed", requireContext())
                }
            } catch (e: JSONException) {
                Timber.e(e.toString())
                e.printStackTrace()
                activity.hideProgressDialog()
                activity.showToast("Something went wrong", requireContext())
            }
        }, Response.ErrorListener { error: VolleyError ->
            activity.hideProgressDialog()
            val networkResponse = error.networkResponse
            if (networkResponse?.data != null) {
                val jsonError = String(networkResponse.data)
                try {
                    val jsonObject = JSONObject(jsonError)
                    val jsonObjectError = jsonObject.getJSONObject("error")
                    val message = jsonObjectError.getString("message")
                    activity.showToast(message, requireContext())
                } catch (e: JSONException) {
                    e.printStackTrace()
                    activity.showToast("Something went wrong", requireContext())
                }
            } else {
                activity.showToast("Something went wrong", requireContext())
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
                map1["offer_id"] = paymentData!!.offerId.toString()
                if (coupon != null) map1["discount_code"] = coupon
                Timber.e(map1.size.toString())
                Timber.e(map1.toString())
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
    }

    private fun acceptRequest(id: String) {
        activity.showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.BASE_URL + Constant.URL_ADDITIONAL_FUND + "/" + id + "/accept", Response.Listener { response: String? ->
            Timber.e(response)
            activity.hideProgressDialog()
            try {
                val jsonObject = JSONObject(response)
                if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                    if (jsonObject.getBoolean("success")) {
                        found = null
                        activity.onBackPressed()
                    } else {
                        activity.showToast("Something went Wrong", requireContext())
                    }
                }
            } catch (e: JSONException) {
                Timber.e(e.toString())
                e.printStackTrace()
            }
        }, Response.ErrorListener { error: VolleyError ->
            val networkResponse = error.networkResponse
            if (networkResponse?.data != null) {
                val jsonError = String(networkResponse.data)
                // Print Error!
                Timber.e(jsonError)
                if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                    activity.unauthorizedUser()
                    activity.hideProgressDialog()
                    return@ErrorListener
                }
                try {
                    val jsonObject = JSONObject(jsonError)
                    val jsonobjectError = jsonObject.getJSONObject("error")
                    if (jsonobjectError.has("errors")) {
                        val jsonobjectErrors = jsonobjectError.getJSONObject("errors")
                        if (jsonobjectErrors.has("amount") && !jsonobjectErrors.isNull("amount")) {
                            val jsonarrayAmount = jsonobjectErrors.getJSONArray("amount")
                            activity.showToast(jsonarrayAmount.getString(0), requireContext())
                        } else if (jsonobjectErrors.has("creation_reason") && !jsonobjectErrors.isNull("creation_reason")) {
                            val jsonArray_amount = jsonobjectErrors.getJSONArray("creation_reason")
                            activity.showToast(jsonArray_amount.getString(0), requireContext())
                        }
                    } else {
                        if (jsonobjectError.has("message")) {
                            activity.showToast(jsonobjectError.getString("message"), requireContext())
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                activity.showToast("Something Went Wrong", requireContext())
            }
            Timber.e(error.toString())
            activity.hideProgressDialog()
        }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = java.util.HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                //   map1.put("X-Requested-With", "XMLHttpRequest");
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)
    }

    fun calculate(amount: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Constant.URL_OFFERS_PAYING_CALCULATION, Response.Listener { response: String? ->
            activity.hideProgressDialog()
            try {
                val jsonObject = JSONObject(response!!)
                val data = jsonObject.getString("data")
                val model = gson!!.fromJson(data, PayingCalculationModel::class.java)
                txtServiceFee.text = String.format(Locale.ENGLISH, "$%.1f", model.serviceFee)
                txtTotalCost.text = String.format(Locale.ENGLISH, "$%.1f", model.netPayingAmount)
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
        }, Response.ErrorListener { error: VolleyError ->
            activity.hideProgressDialog()
            val networkResponse = error.networkResponse
            if (networkResponse?.data != null) {
                val jsonError = String(networkResponse.data)
                try {
                    val jsonObject = JSONObject(jsonError)
                    val jsonObjectError = jsonObject.getJSONObject("error")
                    val message = jsonObjectError.getString("message")
                    activity.showToast(message, requireContext())
                } catch (e: JSONException) {
                    e.printStackTrace()
                    activity.showToast("Something went wrong", requireContext())
                }
            } else {
                activity.showToast("Something went wrong", requireContext())
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
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)
    }

    override fun onCreditCardAdded() {
        paymentMethod
    }

    fun onVerifySubmit(coupon: String?) {
        calculate(amount.toString() + "")
        this.coupon = coupon
        //tvCoupon.text = coupon
        //llCoupon.visibility = View.GONE
        //relCouponDetails.visibility = View.VISIBLE
        // if (addCouponFragment != null) addCouponFragment!!.dismiss()
    }

    private val paymentMethod: Unit
        get() {
            activity.showProgressDialog()
            val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.URL_PAYMENTS_METHOD, Response.Listener { response: String? ->
                Timber.e(response)
                try {
                    val jsonObject = JSONObject(response!!)
                    Timber.e(jsonObject.toString())
                    if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                        if (jsonObject.getBoolean("success")) {
                            if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                val jsonString = jsonObject.toString() // http request
                                val gson = Gson()
                                val creditCardModel = gson.fromJson(jsonString, CreditCardModel::class.java)
                                if (creditCardModel != null && creditCardModel.data!![0].card != null) {
                                    setUpLayout(creditCardModel)
                                } else {
                                    //show add card
                                    setUpAddPaymentLayout()
                                }
                            }
                        } else {
                            setUpAddPaymentLayout()
                            activity.showToast("Something went Wrong", requireContext())
                        }
                    }
                } catch (e: JSONException) {
                    Timber.e(e.toString())
                    e.printStackTrace()
                    setUpAddPaymentLayout()
                }
            }, Response.ErrorListener { error: VolleyError ->
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
                activity.errorHandle1(error.networkResponse)
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
            stringRequest.retryPolicy = DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            val requestQueue = Volley.newRequestQueue(requireContext())
            requestQueue.add(stringRequest)
        }

    fun onClose() {
        coupon = null
        // if (addCouponFragment != null) addCouponFragment!!.dismiss()
    }

    companion object {
        private val TAG = PaymentOverviewActivity::class.java.simpleName
    }
}
