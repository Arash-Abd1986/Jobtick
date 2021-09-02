package com.jobtick.android.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.models.BankAccountModel
import com.jobtick.android.models.BillingAdreessModel
import com.jobtick.android.models.response.getbalance.CreditCardModel
import com.jobtick.android.utils.*
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class PaymentSettingsActivity : ActivityBase() {
    private var creditCardModel: CreditCardModel? = null
    private var bankAccountModel: BankAccountModel? = null
    private var billingAdreessModel: BillingAdreessModel? = null

    private lateinit var toolbar: MaterialToolbar
    private lateinit var deleteCard: MaterialButton
    private lateinit var deleteBankAccount: MaterialButton
    private lateinit var deleteBillingAddress: MaterialButton
    private lateinit var editBillingAddress: MaterialButton
    private lateinit var editBankAccount: MaterialButton
    private lateinit var rbPayments: RadioButton
    private lateinit var rbWithdrawal: RadioButton
    private lateinit var rgPaymentsWithdrawal: RadioGroup
    private lateinit var addCreditCard: CardView
    private lateinit var addBankAccount: CardView
    private lateinit var addBillingAddress: CardView
    private lateinit var paymentSpecs: LinearLayout
    private lateinit var withdrawalSpecs: LinearLayout
    private lateinit var addCreditCardSpecs: LinearLayout
    private lateinit var addBankAccountSpecs: LinearLayout
    private lateinit var addBillingAddressSpecs: LinearLayout
    private lateinit var bsb: TextView
    private lateinit var accountNumber: TextView
    private lateinit var address: TextView
    private lateinit var state: TextView
    private lateinit var suburb: TextView
    private lateinit var postCode: TextView
    private lateinit var country: TextView
    private lateinit var edtExpiryDate: TextView
    private lateinit var creditAccountNumber: TextView
    private lateinit var cardType: TextView
    private lateinit var cardIcon: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_settings)
        setIDs()
        initToolbar()
        initView()
        onViewClick()
        radioBtnClick()
    }

    private fun setIDs() {
        toolbar = findViewById(R.id.toolbar)
        deleteCard = findViewById(R.id.delete_card)
        deleteBankAccount = findViewById(R.id.delete_bank_account)
        deleteBillingAddress = findViewById(R.id.delete_billing_address)
        editBillingAddress = findViewById(R.id.edit_billing_address)
        editBankAccount = findViewById(R.id.edit_bank_account)
        rbPayments = findViewById(R.id.rb_payments)
        rbWithdrawal = findViewById(R.id.rb_withdrawal)
        rgPaymentsWithdrawal = findViewById(R.id.rg_payments_withdrawal)
        addCreditCard = findViewById(R.id.add_credit_card)
        addBankAccount = findViewById(R.id.add_bank_account)
        addBillingAddress = findViewById(R.id.add_billing_address)
        paymentSpecs = findViewById(R.id.linear_payment_specs)
        withdrawalSpecs = findViewById(R.id.linear_withdrawal_specs)
        addCreditCardSpecs = findViewById(R.id.linear_add_credit_card)
        addBankAccountSpecs = findViewById(R.id.linear_add_bank_account)
        addBillingAddressSpecs = findViewById(R.id.linear_add_billing_address)
        bsb = findViewById(R.id.tv_bsb)
        accountNumber = findViewById(R.id.tv_account_number)
        address = findViewById(R.id.tv_address)
        state = findViewById(R.id.tv_state)
        suburb = findViewById(R.id.tv_suburb)
        postCode = findViewById(R.id.tv_postcode)
        country = findViewById(R.id.tv_country)
        edtExpiryDate = findViewById(R.id.credit_expiry_date)
        creditAccountNumber = findViewById(R.id.credit_account_number)
        cardType = findViewById(R.id.card_type)
        cardIcon = findViewById(R.id.card_icon)
    }

    private fun initToolbar() {
        toolbar.title = "Payment Settings"
        toolbar.setNavigationIcon(R.drawable.ic_back)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
    

    private fun initView() {
        rbPayments.isChecked = true
        rbPayments.setTextColor(resources.getColor(R.color.white))
        rbWithdrawal.setTextColor(resources.getColor(R.color.black))
        paymentSpecs.visibility = View.VISIBLE
        withdrawalSpecs.visibility = View.GONE
        billingAddress
        paymentMethod
        bankAccountDetails
    }

    private fun radioBtnClick() {
        rgPaymentsWithdrawal.setOnCheckedChangeListener { group, checkedId ->
            val rbBtn = findViewById<View>(checkedId) as RadioButton
            if (rbBtn.id == R.id.rb_payments) {
                rbPayments.setTextColor(resources.getColor(R.color.white))
                rbWithdrawal.setTextColor(resources.getColor(R.color.black))
                paymentSpecs.visibility = View.VISIBLE
                withdrawalSpecs.visibility = View.GONE
            } else {
                rbPayments.setTextColor(resources.getColor(R.color.black))
                rbWithdrawal.setTextColor(resources.getColor(R.color.white))
                paymentSpecs.visibility = View.GONE
                withdrawalSpecs.visibility = View.VISIBLE
            }
        }
    }

    private fun onViewClick() {
        addCreditCard.setOnClickListener {
            addPaymentCard()
        }
        addBankAccount.setOnClickListener { editBankAccount(false) }
        addBillingAddress.setOnClickListener { editBillingAddress(false) }
        deleteCard.setOnClickListener { deleteCreditCard() }
        deleteBankAccount.setOnClickListener {  deleteBankAccountDetails()}
        deleteBillingAddress.setOnClickListener {  deleteBillingAddress()}
        editBillingAddress.setOnClickListener { editBillingAddress(true) }
        editBankAccount.setOnClickListener { editBankAccount(true) }

    }

    private fun addPaymentCard() {
        val addCreditCard1 = Intent(this@PaymentSettingsActivity, AddCreditCardActivity::class.java)
        startActivityForResult(addCreditCard1, 111)
    }

    private fun editBankAccount(editMode: Boolean) {
        val addBankAccount1 = Intent(this@PaymentSettingsActivity, AddBankAccountActivity::class.java)
        val bundle = Bundle()
        addBankAccount1.putExtra(Constant.EDIT_MODE, editMode)
        if (editMode) {
            bundle.putParcelable(BankAccountModel::class.java.name, bankAccountModel)
        }
        addBankAccount1.putExtras(bundle)
        startActivityForResult(addBankAccount1, 222)
    }

    private fun editBillingAddress(editMode: Boolean) {
        val addBillingAddress1 = Intent(this@PaymentSettingsActivity, BillingAddressActivity::class.java)
        val bundle = Bundle()
        addBillingAddress1.putExtra(Constant.EDIT_MODE, editMode)
        if (editMode) {
            bundle.putParcelable(BillingAdreessModel::class.java.name, billingAdreessModel)
        }
        addBillingAddress1.putExtras(bundle)
        startActivityForResult(addBillingAddress1, 333)
    }

    private fun setupViewBankAccountDetails(success: Boolean) {
        if (success) {
            addBankAccount.visibility = View.GONE
            addBankAccountSpecs.visibility = View.VISIBLE
        } else {
            addBankAccount.visibility = View.VISIBLE
            addBankAccountSpecs.visibility = View.GONE
        }
    }

    private fun setupViewBillingAddress(success: Boolean) {
        if (success) {
            addBillingAddress.visibility = View.GONE
            addBillingAddressSpecs.visibility = View.VISIBLE
        } else {
            addBillingAddress.visibility = View.VISIBLE
            addBillingAddressSpecs.visibility = View.GONE
        }
    }

    private fun setupViewCreditCard(success: Boolean) {
        if (success) {
            addCreditCard.visibility = View.GONE
            addCreditCardSpecs.visibility = View.VISIBLE
        } else {
            addCreditCard.visibility = View.VISIBLE
            addCreditCardSpecs.visibility = View.GONE
        }
    }// Print Error!

    //http request
    val bankAccountDetails: Unit
        get() {
            showProgressDialog()
            val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.BASE_URL + Constant.ADD_ACCOUNT_DETAILS,
                    Response.Listener { response: String? ->
                        Timber.e(response)
                        hideProgressDialog()
                        try {
                            val jsonObject = JSONObject(response)
                            Timber.e(jsonObject.toString())
                            if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                                if (jsonObject.getBoolean("success")) {
                                    val jsonString = jsonObject.toString() //http request
                                    val gson = Gson()
                                    bankAccountModel = gson.fromJson(jsonString, BankAccountModel::class.java)
                                    if (bankAccountModel != null && bankAccountModel!!.isSuccess && bankAccountModel!!.data != null && bankAccountModel!!.data.account_number != null) {
                                        setupViewBankAccountDetails(true)
                                        accountNumber.text = String.format("xxxxx%s", bankAccountModel!!.data.account_number)
                                        bsb.text = bankAccountModel!!.data.bsb_code
                                    } else {
                                        setupViewBankAccountDetails(false)
                                    }
                                } else {
                                    showToast("Something went Wrong", this@PaymentSettingsActivity)
                                    setupViewBankAccountDetails(false)
                                }
                            }
                        } catch (e: JSONException) {
                            Timber.e(e.toString())
                            e.printStackTrace()
                            showToast(e.message, this@PaymentSettingsActivity)
                            setupViewBankAccountDetails(false)
                        }
                    },
            Response.ErrorListener { error: VolleyError ->
                setupViewBankAccountDetails(false)
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
                    showToast("Something Went Wrong", this@PaymentSettingsActivity)
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
            val requestQueue = Volley.newRequestQueue(this@PaymentSettingsActivity)
            requestQueue.add(stringRequest)
        }// Print Error!

    //http request
    val billingAddress: Unit
        get() {
            showProgressDialog()
            val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.BASE_URL + Constant.ADD_BILLING,
                    Response.Listener { response: String? ->
                        Timber.e(response)
                        hideProgressDialog()
                        try {
                            val jsonObject = JSONObject(response)
                            Timber.e(jsonObject.toString())
                            if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                                if (jsonObject.getBoolean("success")) {
                                    val jsonString = jsonObject.toString() //http request
                                    val gson = Gson()
                                    billingAdreessModel = gson.fromJson(jsonString, BillingAdreessModel::class.java)
                                    if (billingAdreessModel != null) {
                                        if (billingAdreessModel!!.isSuccess) {
                                            if (billingAdreessModel!!.data != null && billingAdreessModel!!.data.line1 != null) {
                                                setupViewBillingAddress(true)
                                                address.text = billingAdreessModel!!.data.line1
                                                suburb.text = billingAdreessModel!!.data.city
                                                postCode.text = billingAdreessModel!!.data.post_code
                                                country.setText(R.string.australia)
                                                val helper = StateHelper(applicationContext)
                                                state.text = helper.getStateName(billingAdreessModel!!.data.state)
                                            }
                                        }
                                    }
                                } else {
                                    setupViewBillingAddress(false)
                                    showToast("Something went Wrong", this@PaymentSettingsActivity)
                                }
                            }
                        } catch (e: JSONException) {
                            Timber.e(e.toString())
                            e.printStackTrace()
                            setupViewBillingAddress(false)
                        }
                    },
            Response.ErrorListener { error: VolleyError ->
                setupViewBillingAddress(false)
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
                    showToast("Something Went Wrong", this@PaymentSettingsActivity)
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
            val requestQueue = Volley.newRequestQueue(this@PaymentSettingsActivity)
            requestQueue.add(stringRequest)
        }// map1.put("X-Requested-With", "XMLHttpRequest");// Print Error!

    //http request
    private val paymentMethod: Unit
        get() {
            showProgressDialog()
            val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.URL_PAYMENTS_METHOD,
                    Response.Listener { response: String? ->
                        Timber.e(response)
                        try {
                            val jsonObject = JSONObject(response)
                            Timber.e(jsonObject.toString())
                            if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                                if (jsonObject.getBoolean("success")) {
                                    if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                        val jsonString = jsonObject.toString() //http request
                                        val gson = Gson()
                                        creditCardModel = gson.fromJson(jsonString, CreditCardModel::class.java)
                                        if (creditCardModel != null && creditCardModel!!.data != null && creditCardModel!!.data!![0].card != null) {
                                            setupViewCreditCard(true)
                                            val brand = creditCardModel!!.data!![0].card!!.brand
                                            cardType.text = brand
                                            if (brand.equals(CardTypes.MASTER.type, ignoreCase = true)) cardIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_card_master))
                                            if (brand.equals(CardTypes.VISA.type, ignoreCase = true)) cardIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_card_visa))
                                            if (brand!!.contains(CardTypes.AMERICAN.type)) cardIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_card_american_express))
                                            edtExpiryDate.text = String.format(Locale.ENGLISH, "Expiry Date: %s/%d",
                                                    if (creditCardModel!!.data!![0].card!!.exp_month!! < 10) "0" + creditCardModel!!.data!![0].card!!.exp_month else creditCardModel!!.data!![0].card!!.exp_month,
                                                    creditCardModel!!.data!![0].card!!.exp_year)
                                            creditAccountNumber.text = String.format("xxxx xxxx xxxx %s", creditCardModel!!.data!![0].card!!.last4)
                                        } else {
                                            setupViewCreditCard(false)
                                        }
                                        hideProgressDialog()
                                    }
                                } else {
                                    setupViewCreditCard(false)
                                    showToast("Something went Wrong", this@PaymentSettingsActivity)
                                }
                            }
                        } catch (e: JSONException) {
                            Timber.e(e.toString())
                            e.printStackTrace()
                            setupViewCreditCard(false)
                        }
                    },
            Response.ErrorListener { error: VolleyError ->
                setupViewCreditCard(false)
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
                                hideProgressDialog()
                                return@ErrorListener
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                Timber.e(error.toString())
                errorHandle1(error.networkResponse)
                hideProgressDialog()
            }) {
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
            val requestQueue = Volley.newRequestQueue(this@PaymentSettingsActivity)
            requestQueue.add(stringRequest)
        }

    private fun deleteBankAccountDetails() {
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.DELETE, Constant.URL_DELETE_BANK_ACCOUNT + "/" + bankAccountModel!!.data.id,
                Response.Listener { response: String? ->
                    Timber.e(response)
                    hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                setupViewBankAccountDetails(false)
                            } else {
                                showToast("Something went Wrong", this@PaymentSettingsActivity)
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
                        try {
                            val jsonObject = JSONObject(jsonError)
                            val jsonObjectError = jsonObject.getJSONObject("error")
                            if (jsonObjectError.has("error_text") && !jsonObjectError.isNull("error_text")) {
                                if (ConstantKey.NO_PAYMENT_METHOD.equals(jsonObjectError.getString("error_text"), ignoreCase = true)) {
                                    //  setUpAddPaymentLayout();
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    Timber.e(error.toString())
                    errorHandle1(error.networkResponse)
                    hideProgressDialog()
                }) {
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
        val requestQueue = Volley.newRequestQueue(this@PaymentSettingsActivity)
        requestQueue.add(stringRequest)
    }

    private fun deleteBillingAddress() {
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.DELETE, Constant.URL_DELETE_BILLING_ADDRESS,
                Response.Listener { response: String? ->
                    Timber.e(response)
                    hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                setupViewBillingAddress(false)
                            } else {
                                showToast("Something went Wrong", this@PaymentSettingsActivity)
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
                        try {
                            val jsonObject = JSONObject(jsonError)
                            val jsonObjectError = jsonObject.getJSONObject("error")
                            if (jsonObjectError.has("error_text") && !jsonObjectError.isNull("error_text")) {
                                if (ConstantKey.NO_PAYMENT_METHOD.equals(jsonObjectError.getString("error_text"), ignoreCase = true)) {
                                    //  setUpAddPaymentLayout();
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    Timber.e(error.toString())
                    errorHandle1(error.networkResponse)
                    hideProgressDialog()
                }) {
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
        val requestQueue = Volley.newRequestQueue(this@PaymentSettingsActivity)
        requestQueue.add(stringRequest)
    }

    private fun deleteCreditCard() {
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.DELETE, Constant.URL_PAYMENTS_METHOD,
                Response.Listener { response: String? ->
                    Timber.e(response)
                    hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                setupViewCreditCard(false)
                            } else {
                                showToast("Something went Wrong", this@PaymentSettingsActivity)
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
                        try {
                            val jsonObject = JSONObject(jsonError)
                            val jsonObject_error = jsonObject.getJSONObject("error")
                            if (jsonObject_error.has("error_text") && !jsonObject_error.isNull("error_text")) {
                                if (ConstantKey.NO_PAYMENT_METHOD.equals(jsonObject_error.getString("error_text"), ignoreCase = true)) {
                                    //  setUpAddPaymentLayout();
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    Timber.e(error.toString())
                    errorHandle1(error.networkResponse)
                    hideProgressDialog()
                }) {
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
        val requestQueue = Volley.newRequestQueue(this@PaymentSettingsActivity)
        requestQueue.add(stringRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == 111) {
                paymentMethod
            } else if (requestCode == 222) {
                bankAccountDetails
            } else if (requestCode == 333) {
                billingAddress
            }
        }
    }
}