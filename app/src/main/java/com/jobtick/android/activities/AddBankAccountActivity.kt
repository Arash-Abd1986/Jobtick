package com.jobtick.android.activities

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.view.MenuItem
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.jobtick.android.R
import com.jobtick.android.models.BankAccountModel
import com.jobtick.android.payment.AddBankAccount
import com.jobtick.android.payment.AddBankAccountImpl
import com.jobtick.android.utils.Constant
import com.jobtick.android.widget.ExtendedEntryText

class AddBankAccountActivity : ActivityBase() {
    private lateinit var toolbar: MaterialToolbar
    private lateinit var edtAccountName: ExtendedEntryText
    private lateinit var edtBsb: ExtendedEntryText
    private lateinit var edtAccountNumber: ExtendedEntryText
    private lateinit var lytBtnAddBankAccount: MaterialButton
    private var addBankAccount: AddBankAccount? = null
    private var editMode = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_bank_account)
        setIDs()
        initUi()
        initToolbar()
        onViewClick()
        addBankAccount = object : AddBankAccountImpl(this, sessionManager) {
            override fun onSuccess() {
                hideProgressDialog()
                val returnIntent = Intent()
                setResult(RESULT_OK, returnIntent)
                finish()
            }

            override fun onError(e: Exception) {
                showToast(getString(R.string.add_bank_account_error), this@AddBankAccountActivity)
                hideProgressDialog()
            }

            override fun onValidationError(errorType: ErrorType, message: String) {
                if (errorType == ErrorType.UnAuthenticatedUser) unauthorizedUser() else showToast(message, this@AddBankAccountActivity)
            }
        }
    }

    private fun setIDs() {
        toolbar = findViewById(R.id.toolbar)
        edtAccountName = findViewById(R.id.edt_account_name)
        edtBsb = findViewById(R.id.edt_bsb)
        edtAccountNumber = findViewById(R.id.edt_account_number)
        lytBtnAddBankAccount = findViewById(R.id.lyt_btn_add_bank_account)
    }

    private fun initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (editMode) supportActionBar!!.setTitle(R.string.edit_bank_account) else supportActionBar!!.setTitle(R.string.add_bank_account)
    }

    private fun initUi() {
        val bundle = intent.extras ?: return
        if (bundle.getParcelable<Parcelable?>(BankAccountModel::class.java.name) == null) return
        val bankAccountModel: BankAccountModel = bundle.getParcelable(BankAccountModel::class.java.name)!!
        if (!bankAccountModel.isSuccess || bankAccountModel.data == null) return
        edtAccountName.text = bankAccountModel.data.account_name
        edtAccountNumber.text = "xxxxx" + bankAccountModel.data.account_number
        edtBsb.text = bankAccountModel.data.bsb_code
        editMode = bundle.getBoolean(Constant.EDIT_MODE)
        if (editMode) lytBtnAddBankAccount!!.text = getString(R.string.edit_bank_account) else lytBtnAddBankAccount!!.text = getString(R.string.add_bank_account)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun onViewClick() {
        lytBtnAddBankAccount.setOnClickListener {
            if (!validate()) return@setOnClickListener
            showProgressDialog()
            addBankAccount!!.add(edtAccountName.text,
                    edtBsb.text,
                    edtAccountNumber.text)
        }

    }

    fun validate(): Boolean {
        if (TextUtils.isEmpty(edtAccountName.text)) {
            edtAccountName.setError("Please Enter Account Name")
            return false
        }
        if (TextUtils.isEmpty(edtBsb.text)) {
            edtBsb.setError("Please enter BSB")
            return false
        }
        if (TextUtils.isEmpty(edtAccountNumber.text)) {
            edtAccountNumber.setError("Please account Number")
            return false
        }
        return true
    }
}