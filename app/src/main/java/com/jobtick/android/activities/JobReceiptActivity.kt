package com.jobtick.android.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.activities.JobReceiptActivity
import com.jobtick.android.models.TaskModel
import com.jobtick.android.models.payments.PaymentMethodModel
import com.jobtick.android.models.receipt.Invoice
import com.jobtick.android.models.receipt.Item
import com.jobtick.android.models.receipt.JobReceiptModel
import com.jobtick.android.models.receipt.Receipt
import com.jobtick.android.utils.*
import com.mikhaellopez.circularimageview.CircularImageView
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class JobReceiptActivity : ActivityBase() {
    private lateinit var toolbar: MaterialToolbar
    private lateinit var imgAvatar: CircularImageView
    private lateinit var imgVerifiedAccount: ImageView
    private lateinit var txtLocation: TextView
    private lateinit var txtFullName: TextView
    private lateinit var jobTitle: TextView
    private lateinit var txtAmount: TextView
    private lateinit var receiptNumber: TextView
    private lateinit var serviceFeeTitle: TextView
    private lateinit var jobCostValue: TextView
    private lateinit var serviceFee: TextView
    private lateinit var totalCost: TextView
    private lateinit var totalCostTitle: TextView
    private lateinit var paidOn: TextView
    private lateinit var paymentNumber: TextView
    private lateinit var cardLogo: CardView
    private lateinit var abnNumber: TextView
    private lateinit var invoiceNumber: TextView
    private lateinit var jobTickServiceValue: TextView
    private lateinit var jobTickServiceTitle: TextView
    private lateinit var jobTickGtsValue: TextView
    private lateinit var jobTickTotalValue: TextView
    private var sessionManagerJ: SessionManager? = null
    private var isMyTask = false
    private var taskSlug: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_receipt)
        setIDs()
        sessionManagerJ = SessionManager(this@JobReceiptActivity)
        val bundle = intent.extras
        if (bundle != null) {
            isMyTask = bundle.getBoolean(ConstantKey.IS_MY_TASK)
            taskSlug = bundle.getString(ConstantKey.TASK_SLUG)
        }
        checkNotNull(taskSlug) { "need to send taskslug on bundle" }
        initToolbar()
        init()
        getData(taskSlug!!)
    }

    private fun setIDs() {
        toolbar = findViewById(R.id.toolbar)
        imgAvatar = findViewById(R.id.img_avatar)
        imgVerifiedAccount = findViewById(R.id.img_verified_account)
        txtLocation = findViewById(R.id.txt_location)
        txtFullName = findViewById(R.id.txt_full_name)
        jobTitle = findViewById(R.id.job_title)
        txtAmount = findViewById(R.id.txt_amount)
        receiptNumber = findViewById(R.id.receipt_number)
        serviceFeeTitle = findViewById(R.id.service_fee_title)
        jobCostValue = findViewById(R.id.job_cast_value)
        serviceFee = findViewById(R.id.service_fee_value)
        totalCost = findViewById(R.id.total_cost_value)
        totalCostTitle = findViewById(R.id.total_cost_title)
        paidOn = findViewById(R.id.paid)
        paymentNumber = findViewById(R.id.payment_number)
        cardLogo = findViewById(R.id.card_logo)
        abnNumber = findViewById(R.id.abn_number)
        invoiceNumber = findViewById(R.id.invoice_number)
        jobTickServiceValue = findViewById(R.id.job_tick_service_fee_value)
        jobTickServiceTitle = findViewById(R.id.job_tick_service_fee_title)
        jobTickGtsValue = findViewById(R.id.job_tick_gts_value)
        jobTickTotalValue = findViewById(R.id.job_tick_total_value)
    }

    private fun init() {
        if (!isMyTask) {
            cardLogo.visibility = View.GONE
            paidOn.visibility = View.GONE
            paymentNumber.visibility = View.GONE
            totalCostTitle.setText(R.string.total)
        }
    }

    private fun initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Job receipt"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setData(model: JobReceiptModel) {
        var taskModel: TaskModel? = null
        var receipt: Receipt? = null
        var invoice: Invoice? = null
        var item: Item? = null
        var paymentMethod: PaymentMethodModel? = null
        if (model.task != null) taskModel = model.task
        if (model.receipt != null) receipt = model.receipt
        if (model.invoice != null) invoice = model.invoice
        if (model.invoice != null && model.invoice.items != null &&
                !model.invoice.items.isEmpty() && model.invoice.items[0] != null) {
            item = model.invoice.items[0]
        }
        if (model.receipt != null && model.receipt.paymentMethod != null) paymentMethod = model.receipt.paymentMethod
        if (receipt != null && receipt.receiptAmount != null) txtAmount.text = String.format(Locale.ENGLISH, "$%.0f", receipt.receiptAmount)
        if (receipt != null && receipt.receiptNumber != null) receiptNumber.text = receipt.receiptNumber
        if (taskModel != null && taskModel.title != null) jobTitle.text = taskModel.title
        if (taskModel != null && taskModel.poster != null && taskModel.poster.location != null) {
            txtLocation.text = taskModel.poster.location
        } else {
            txtLocation.setText(R.string.remote_task)
        }
        if (taskModel != null && taskModel.poster != null && taskModel.poster.avatar != null && taskModel.poster.avatar.thumbUrl != null) {
            Glide.with(imgAvatar).load(taskModel.poster.avatar.thumbUrl).into(imgAvatar)
        } else {
            //deafult image
        }
        if (taskModel != null && taskModel.poster != null && taskModel.poster.isVerifiedAccount == 1) {
            imgVerifiedAccount.visibility = View.VISIBLE
        } else {
            imgVerifiedAccount.visibility = View.GONE
        }
        if (taskModel != null && taskModel.poster != null && taskModel.poster.name != null) txtFullName.text = taskModel.poster.name
        if (receipt != null && receipt.taskCost != null) jobCostValue.text = String.format(Locale.ENGLISH, "$%.2f", receipt.taskCost).removeClearRound()
        if (receipt != null && receipt.fee != null) serviceFee.text = String.format(Locale.ENGLISH, "$%s", receipt.fee).removeClearRound()
        if (receipt != null && receipt.netAmount != null) totalCost.text = String.format(Locale.ENGLISH, "$%.2f", receipt.netAmount).removeClearRound()
        if (invoice != null && invoice.invoiceNumber != null) invoiceNumber.text = invoice.invoiceNumber
        if (invoice != null && invoice.abn != null) abnNumber.text = String.format(Locale.ENGLISH, "ABN: %s", invoice.abn)
        if (item != null && item.itemName != null) jobTickServiceTitle.text = item.itemName
        if (item != null && item.amount != null) jobTickServiceValue.text = String.format(Locale.ENGLISH, "$%s", item.amount).removeClearRound()
        if (item != null && item.taxAmount != null) jobTickGtsValue.text = String.format(Locale.ENGLISH, "$%s", item.taxAmount).removeClearRound()
        if (item != null && item.finalAmount != null) jobTickTotalValue.text = String.format(Locale.ENGLISH, "$%s", item.finalAmount).removeClearRound()
        if (isMyTask) {
            if (invoice != null && invoice.createdAt != null) paidOn.text = String.format(Locale.ENGLISH, "Paid On %s",
                    TimeHelper.convertToShowTimeFormat(invoice.createdAt))
            if (paymentMethod != null && paymentMethod.brand != null && paymentMethod.last4 != null) paymentNumber.text = String.format(Locale.ENGLISH, "%s *******%s",
                    paymentMethod.brand,
                    paymentMethod.last4)
        }
    }

    private fun getData(taskSlug: String) {
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.URL_TASKS + "/" + taskSlug + "/invoice",
                Response.Listener { response: String? ->
                    Timber.e(response)
                    hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response!!)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                            val model = JobReceiptModel().getJsonToModel(jsonObject.getJSONObject("data"), this)
                            setData(model)
                        } else {
                            showToast("something went wrong.", this)
                            return@Listener
                        }
                    } catch (e: JSONException) {
                        Timber.e(e.toString())
                        e.printStackTrace()
                    }
                    hideProgressDialog()
                },
                Response.ErrorListener { error: VolleyError ->
                    errorHandle1(error.networkResponse)
                    hideProgressDialog()
                }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["Authorization"] = "Bearer " + sessionManagerJ!!.accessToken
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this@JobReceiptActivity)
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
    }

    companion object {
        private val TAG = JobReceiptActivity::class.java.name
    }
}