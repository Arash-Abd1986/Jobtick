package com.jobtick.android.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.ImageView
import com.android.volley.NetworkResponse
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.jobtick.android.R
import com.jobtick.android.payment.AddCreditCard
import com.jobtick.android.payment.AddCreditCardImpl
import com.jobtick.android.utils.NumberTextWatcherForSlash
import com.jobtick.android.utils.StringUtils
import com.jobtick.android.widget.ExtendedEntryText
import java.util.*

class AddCreditCardActivity : ActivityBase() {
    private var toolbar: MaterialToolbar? = null
    private var edtFullName: ExtendedEntryText? = null
    private var edtCardNumber: ExtendedEntryText? = null
    private var edtExpiryDate: ExtendedEntryText? = null
    private var edtSecurityNumber: ExtendedEntryText? = null
    private var lytBtnAddCreditCard: MaterialButton? = null
    private var ivCardType: ImageView? = null
    private var expMonth = 0
    private var expYear = 0
    private var addCreditCard: AddCreditCard? = null
    private var mDateSetListener: DatePickerDialog.OnDateSetListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_credit_card)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        initIDs()
        initToolbar()
        edtExpiryDate!!.setExtendedViewOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH]
            val dialog = createDialogWithoutDateField(DatePickerDialog(this,
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    mDateSetListener,
                    year, month, 1))
            dialog.datePicker.minDate = calendar.timeInMillis
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }
        mDateSetListener = DatePickerDialog.OnDateSetListener { _: DatePicker?, year: Int, month: Int, _: Int ->
            val monthL = month + 1
            val formattedMonth = String.format(Locale.US, "%02d", monthL)
            val date = "$formattedMonth/$year"
            edtExpiryDate!!.text = date
        }
        addCreditCard = object : AddCreditCardImpl(this, sessionManager) {
            override fun onSuccess() {
                hideProgressDialog()
                val returnIntent = Intent()
                setResult(RESULT_OK, returnIntent)
                finish()
            }

            override fun onError(e: Exception) {
                showToast(getString(R.string.credit_card_error), this@AddCreditCardActivity)
                hideProgressDialog()
            }

            override fun onNetworkResponseError(networkResponse: NetworkResponse) {
                errorHandle1(networkResponse)
                hideProgressDialog()
            }

            override fun onValidationError(validationErrorType: ValidationErrorType, message: String) {
                showToast(message, this@AddCreditCardActivity)
                hideProgressDialog()
            }
        }
        setupCardTypes()
        setupExpireDateAutoSlash()
    }

    private fun initIDs() {
        toolbar = findViewById(R.id.toolbar)
        edtFullName = findViewById(R.id.edt_full_name)
        edtCardNumber = findViewById(R.id.edt_card_number)
        edtExpiryDate = findViewById(R.id.edt_expiry_date)
        edtSecurityNumber = findViewById(R.id.edt_security_number)
        lytBtnAddCreditCard = findViewById(R.id.lyt_btn_add_credit_card)
        ivCardType = findViewById(R.id.ivCardType)
        onViewClick()
    }

    private fun setupExpireDateAutoSlash() {
        edtExpiryDate!!.addTextChangedListener(NumberTextWatcherForSlash(edtExpiryDate!!))
    }

    private fun setupCardTypes() {
        val keyDel = intArrayOf(0)
        val lenB = intArrayOf(0)
        val lenA = intArrayOf(0)
        edtCardNumber!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                lenB[0] = s.length
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                lenA[0] = s.length
                var is465 = false
                var is4444 = false
                if (lenB[0] >= lenA[0]) keyDel[0] = 1
                edtCardNumber!!.setFilter(19)
                when {
                    edtCardNumber!!.text.startsWith("34") || edtCardNumber!!.text.startsWith("37") -> {
                        is465 = true
                        edtCardNumber!!.setFilter(17)
                    }
                    edtCardNumber!!.text.startsWith("5") -> {
                        is4444 = true
                    }
                    edtCardNumber!!.text.startsWith("4") -> {
                        is4444 = true
                    }
                }
                if (keyDel[0] == 0) {
                    val len = edtCardNumber!!.text.length
                    val needs465Spacing = is465 && (len == 4 || len == 11)
                    val needs4444Spacing = is4444 && (len == 4 || len == 9 || len == 14)
                    if (needs465Spacing || needs4444Spacing) {
                        edtCardNumber!!.text = edtCardNumber!!.text + " "
                        edtCardNumber!!.setSelection(edtCardNumber!!.text.length)
                    }
                } else {
                    keyDel[0] = 0
                }
            }

            override fun afterTextChanged(s: Editable) {
                if (edtCardNumber!!.text.length > 1) {
                    val cardFirstLetters = edtCardNumber!!.text.substring(0, 2)
                    when {
                        cardFirstLetters == "34" || cardFirstLetters == "37" -> {
                            ivCardType!!.visibility = View.VISIBLE
                            ivCardType!!.setImageResource(R.drawable.ic_card_american_express)
                        }
                        edtCardNumber!!.text.startsWith("5") -> {
                            ivCardType!!.visibility = View.VISIBLE
                            ivCardType!!.setImageResource(R.drawable.ic_card_master)
                        }
                        edtCardNumber!!.text.startsWith("4") -> {
                            ivCardType!!.visibility = View.VISIBLE
                            ivCardType!!.setImageResource(R.drawable.ic_card_visa)
                        }
                        else -> {
                            ivCardType!!.visibility = View.INVISIBLE
                        }
                    }
                } else if (ivCardType!!.visibility == View.VISIBLE) {
                    ivCardType!!.visibility = View.INVISIBLE
                }
            }
        })
    }

    private fun initToolbar() {
        toolbar!!.setNavigationIcon(R.drawable.ic_back)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Add Credit Card"
    }

    private fun setExpiryDate(expiryDate: String) {
        expMonth = expiryDate.substring(0, 2).toInt()
        expYear = 2000 + expiryDate.substring(3).toInt()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun onViewClick() {
        lytBtnAddCreditCard!!.setOnClickListener {
            if (validation()) {
                setExpiryDate(edtExpiryDate!!.text)
                showProgressDialog()
                addCreditCard!!.getToken(edtCardNumber!!.text.replace(" ", ""),
                        expMonth, expYear,
                        edtSecurityNumber!!.text,
                        edtFullName!!.text)
            }
        }

    }

    private fun createDialogWithoutDateField(dpd: DatePickerDialog): DatePickerDialog {
        try {
            dpd.datePicker.findViewById<View>(resources.getIdentifier("day", "id", "android")).visibility = View.GONE
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return dpd
    }

    private fun validation(): Boolean {
        if (edtFullName!!.text.isEmpty()) {
            edtFullName!!.setError("The card name must be filled.")
            return false
        } else if (edtCardNumber!!.text.isEmpty()) {
            edtCardNumber!!.setError("The card number must be filled.")
            return false
        } else if (edtExpiryDate!!.text == null || edtExpiryDate!!.text.isEmpty()
                || edtExpiryDate!!.text.length != 5) {
            edtExpiryDate!!.setError("The card expiry date must be filled.")
            return false
        } else if (!StringUtils.checkCreditCardExpiryFormatSimple(edtExpiryDate!!.text)) {
            edtExpiryDate!!.setError("The card expiry date is not correct.")
            return false
        } else if (edtExpiryDate!!.text.substring(0, 2).toInt() > 12) {
            edtExpiryDate!!.setError("The card expiry date is not correct.")
        } else if (edtSecurityNumber!!.text.isEmpty()) {
            edtSecurityNumber!!.setError("The card CVC must be filled.")
            return false
        }
        return true
    }
}