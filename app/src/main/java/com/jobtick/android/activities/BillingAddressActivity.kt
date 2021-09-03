package com.jobtick.android.activities

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.view.MenuItem
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.jobtick.android.R
import com.jobtick.android.adapers.SuburbSearchAdapter.SubClickListener
import com.jobtick.android.fragments.SearchSuburbBottomSheet
import com.jobtick.android.models.BillingAdreessModel
import com.jobtick.android.models.response.searchsuburb.Feature
import com.jobtick.android.payment.AddBillingAddress
import com.jobtick.android.payment.AddBillingAddressImpl
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.Helper
import com.jobtick.android.utils.SuburbAutoComplete
import com.jobtick.android.widget.ExtendedEntryText

class BillingAddressActivity : ActivityBase(), SubClickListener {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var edtAddressLine1: ExtendedEntryText
    private lateinit var edtAddressLine2: ExtendedEntryText
    private lateinit var edtSuburbs: ExtendedEntryText
    private lateinit var edtState: ExtendedEntryText
    private lateinit var edtPostcode: ExtendedEntryText
    private lateinit var edtCountry: ExtendedEntryText
    private lateinit var lytBtnChangeBillingAddress: MaterialButton
    private var addBillingAddress: AddBillingAddress? = null
    private var editMode = false
    private val PLACE_SELECTION_REQUEST_CODE = 10002
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_billing_address)
        setIDs()
        initUi()
        initToolbar()
        onViewClick()
        addBillingAddress = object : AddBillingAddressImpl(this, sessionManager) {
            override fun onSuccess() {
                hideProgressDialog()
                val returnIntent = Intent()
                setResult(RESULT_OK, returnIntent)
                finish()
            }

            override fun onError(e: Exception) {
                showToast(e.message, this@BillingAddressActivity)
            }

            override fun onValidationError(errorType: ErrorType, message: String) {
                if (errorType == ErrorType.UnAuthenticatedUser) unauthorizedUser() else showToast(message, this@BillingAddressActivity)
            }
        }
        edtSuburbs.setExtendedViewOnClickListener {
            val infoBottomSheet = SearchSuburbBottomSheet(this)
            infoBottomSheet.show(supportFragmentManager, null)
        }
    }

    private fun setIDs() {
        toolbar = findViewById(R.id.toolbar)
        edtAddressLine1 = findViewById(R.id.edt_address_line_1)
        edtAddressLine2 = findViewById(R.id.edt_address_line_2)
        edtSuburbs = findViewById(R.id.edt_suburs)
        edtState = findViewById(R.id.edt_state)
        edtPostcode = findViewById(R.id.edt_postcode)
        edtCountry = findViewById(R.id.edt_Country)
        lytBtnChangeBillingAddress = findViewById(R.id.lyt_btn_change_billing_address)
    }

    private fun initUi() {
        val bundle = intent.extras ?: return
        if (bundle.getParcelable<Parcelable?>(BillingAdreessModel::class.java.name) == null) return
        val billingAddressModel: BillingAdreessModel = bundle.getParcelable(BillingAdreessModel::class.java.name)!!
        if (!billingAddressModel.isSuccess || billingAddressModel.data == null) return
        edtAddressLine1.text = billingAddressModel.data.line1
        edtAddressLine2.text = billingAddressModel.data.line2
        edtState.text = billingAddressModel.data.state
        edtSuburbs.text = billingAddressModel.data.city
        edtPostcode.text = billingAddressModel.data.post_code
        edtCountry.text = getString(R.string.australia)
        editMode = bundle.getBoolean(Constant.EDIT_MODE)
        if (editMode) lytBtnChangeBillingAddress.setText(R.string.edit_billing_address)
    }

    private fun initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (editMode) supportActionBar!!.title = getString(R.string.edit_billing_address) else supportActionBar!!.title = getString(R.string.add_billing_address)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun onViewClick() {
        lytBtnChangeBillingAddress.setOnClickListener {
            if (!validation()) return@setOnClickListener
            showProgressDialog()
            addBillingAddress!!.add(edtAddressLine1.text,
                    edtAddressLine2.text,
                    edtSuburbs.text,
                    edtState.text,
                    edtPostcode.text, "AU")
        }

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_SELECTION_REQUEST_CODE && resultCode == RESULT_OK) {
            Helper.closeKeyboard(this)
            edtSuburbs.text = SuburbAutoComplete.getSuburbName(data)
            edtState.text = SuburbAutoComplete.getState()
            edtCountry.text = getString(R.string.australia)
        }
    }

    private fun validation(): Boolean {
        if (TextUtils.isEmpty(edtAddressLine1.text.trim { it <= ' ' })) {
            edtAddressLine1.setError("Address is mandatory")
            return false
        }
        if (TextUtils.isEmpty(edtSuburbs.text.trim { it <= ' ' })) {
            edtSuburbs.setError("Please enter Suburb")
            return false
        }
        if (TextUtils.isEmpty(edtState.text.trim { it <= ' ' })) {
            edtState.setError("Please enter state")
            return false
        }
        if (TextUtils.isEmpty(edtPostcode.text.trim { it <= ' ' })) {
            edtPostcode.setError("Please enter Passcode")
            return false
        }
        if (edtPostcode.text.length != 4) {
            edtPostcode.setError("Please enter 4 digit Passcode")
            return false
        }
        if (TextUtils.isEmpty(edtCountry.text.trim { it <= ' ' })) {
            edtCountry.setError("Please Enter Country")
            return false
        }
        return true
    }

    override fun clickOnSearchedLoc(location: Feature) {
        Helper.closeKeyboard(this)
        edtSuburbs.text = location.place_name_en!!.substring(0, location.place_name_en!!.indexOf(","))
        edtState.text = location.state
        edtCountry.text = getString(R.string.australia)
    }
}