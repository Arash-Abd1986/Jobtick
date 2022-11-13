package com.jobtick.android.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import com.android.volley.NetworkResponse
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.jobtick.android.R
import com.jobtick.android.activities.ActivityBase
import com.jobtick.android.payment.AddCreditCard
import com.jobtick.android.payment.AddCreditCardImpl
import com.jobtick.android.utils.*
import java.util.*

class CreditCardReqFragment : Fragment(), TextWatcher {

    lateinit var btnAddCard: MaterialButton
    lateinit var edtFullName: TextInputLayout
    lateinit var edtCardNumber: TextInputLayout
    lateinit var edtExpiryDate: TextInputLayout
    lateinit var edtSecurityNumber: TextInputLayout
    lateinit var title: MaterialTextView
    private var expMonth = 0
    private var expYear = 0
    private var addCreditCard: AddCreditCard? = null
    private var sessionManager: SessionManager? = null

    var mDateSetListener: DatePickerDialog.OnDateSetListener? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(context)
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        btnAddCard = view.findViewById(R.id.btn_add_card)
        btnAddCard.setOnClickListener {
            if (validation()) {
                setExpiryDate(edtExpiryDate.editText!!.text.toString())
                (requireActivity() as ActivityBase).showProgressDialog()
                addCreditCard!!.getToken(edtCardNumber.editText!!.text.toString().trim { it <= ' ' },
                        expMonth, expYear,
                        edtSecurityNumber.editText!!.text.toString(),
                        edtFullName.editText!!.text.toString())
            }
        }
        edtFullName = view.findViewById(R.id.edt_full_name)
        edtCardNumber = view.findViewById(R.id.edt_card_number)
        edtExpiryDate = view.findViewById(R.id.edt_expiry_date)
        edtSecurityNumber = view.findViewById(R.id.edt_security_number)
        title = view.findViewById(R.id.title)
        edtFullName.editText!!.addTextChangedListener(this)
        edtCardNumber.editText!!.addTextChangedListener(this)
        edtSecurityNumber.editText!!.addTextChangedListener(this)
        edtExpiryDate.editText!!.addTextChangedListener(this)
        edtExpiryDate.setDifferentHintOnFocus("MM/YY")
        edtSecurityNumber.setDifferentHintOnFocus("e.g. 1234")
        edtFullName.setDifferentHintOnFocus("e.g. Oliver Smith")
        edtCardNumber.setDifferentHintOnFocus("1234 5678 9101 1121")
        title.text = "Add Credit Card"

        edtExpiryDate.editText!!.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH]
            val dialog = DatePickerDialog(requireContext(),
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    mDateSetListener,
                    year, month, 1)
            dialog.datePicker.minDate = calendar.timeInMillis
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }
        edtExpiryDate.editText!!.addTextChangedListener(EditTextNumberTextWatcherForSlash(edtExpiryDate.editText!!))
        mDateSetListener = DatePickerDialog.OnDateSetListener { _: DatePicker?, year: Int, month: Int, _: Int ->
            val month = month + 1
            val formattedMonth = String.format(Locale.US, "%02d", month)
            val date = "$formattedMonth/$year"
            edtExpiryDate.editText!!.setText(date)
        }
        addCreditCard = object : AddCreditCardImpl(requireContext(), sessionManager) {
            override fun onSuccess() {
                (requireActivity() as ActivityBase).hideProgressDialog()
                goNext()
            }

            override fun onError(e: Exception) {
                (requireActivity() as ActivityBase).hideProgressDialog()
                (requireActivity() as ActivityBase).showToast(getString(R.string.credit_card_error), requireContext())
            }

            override fun onNetworkResponseError(networkResponse: NetworkResponse) {
                (requireActivity() as ActivityBase).errorHandle1(networkResponse)
                (requireActivity() as ActivityBase).hideProgressDialog()
            }

            override fun onValidationError(validationErrorType: ValidationErrorType, message: String) {
                (requireActivity() as ActivityBase).showToast(message, requireContext())
                (requireActivity() as ActivityBase).hideProgressDialog()
            }
        }
        btnAddCard.isEnabled = false
        //        setupCardTypes();
    }

    private fun setupCardTypes() {
        edtCardNumber.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_credit_card_req, container, false)
    }

    private fun goNext() {
        (parentFragment as PosterRequirementsBottomSheet?)!!.changeFragment(1)
    }

    private fun setExpiryDate(expiryDate: String) {
        expMonth = expiryDate.substring(0, 2).toInt()
        expYear = 2000 + expiryDate.substring(3).toInt()
    }

    private fun validation(): Boolean {
        if (edtFullName.editText!!.text.toString().isEmpty()) {
            edtFullName.error = "The card name must be filled."
            return false
        } else if (edtCardNumber.editText!!.text.toString().isEmpty()) {
            edtCardNumber.error = "The card number must be filled."
            return false
        } else if (edtExpiryDate.editText!!.text == null || edtExpiryDate.editText!!.text.toString().isEmpty()
                || edtExpiryDate.editText!!.text.toString().length != 5) {
            edtExpiryDate.error = "The card expiry date must be filled."
            return false
        } else if (!StringUtils.checkCreditCardExpiryFormatSimple(edtExpiryDate.editText!!.text.toString())) {
            edtExpiryDate.error = "The card expiry date is not correct."
            return false
        } else if (edtExpiryDate.editText!!.text.toString().substring(0, 2).toInt() > 12) {
            edtExpiryDate.error = "The card expiry date is not correct."
        } else if (edtSecurityNumber.editText!!.text.toString().isEmpty()) {
            edtSecurityNumber.error = "The card CVC must be filled."
            return false
        }
        return true
    }

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        lenB[0] = charSequence.length
    }

    val keyDel = intArrayOf(0)
    val lenB = intArrayOf(0)
    val lenA = intArrayOf(0)
    @SuppressLint("SetTextI18n")
    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        lenA[0] = charSequence.length
        var is465 = false
        var is4444 = false
        if (lenB[0] >= lenA[0]) keyDel[0] = 1
        edtCardNumber.setFilter(19)
        if (edtCardNumber.editText!!.text.toString().startsWith("34") || edtCardNumber.editText!!.text.startsWith("37")) {
            is465 = true
            edtCardNumber.setFilter(17)
        } else if (edtCardNumber.editText!!.text.toString().startsWith("5")) {
            is4444 = true
        } else if (edtCardNumber.editText!!.text.toString().startsWith("4")) {
            is4444 = true
        }
        if (keyDel[0] == 0) {
            val len = edtCardNumber.editText!!.text.toString().length
            val needs465Spacing = is465 && (len == 4 || len == 11)
            val needs4444Spacing = is4444 && (len == 4 || len == 9 || len == 14)
            if (needs465Spacing || needs4444Spacing) {
                edtCardNumber.editText!!.setText(edtCardNumber.editText!!.text.toString() + " ")
                edtCardNumber.editText!!.setSelection(edtCardNumber.editText!!.text.toString().length)
            }
        } else {
            keyDel[0] = 0
        }
    }

    override fun afterTextChanged(editable: Editable) {
       val btnEnabled = edtCardNumber.editText!!.text.toString().isNotEmpty() && edtFullName.editText!!.text.toString().isNotEmpty()
                && edtSecurityNumber.editText!!.text.toString().isNotEmpty() && edtExpiryDate.editText!!.text.toString().isNotEmpty()
        btnAddCard.isEnabled = btnEnabled
//        if (edtCardNumber.editText!!.text.toString().length > 1) {
//            val cardFirstLetters = edtCardNumber.editText!!.text.toString().substring(0, 2)
//            if (cardFirstLetters == "34" || cardFirstLetters == "37") {
//                ivCardType.visibility = View.VISIBLE
//                ivCardType.setImageResource(R.drawable.ic_card_american_express)
//            } else if (edtCardNumber.editText!!.text.toString()[0] == '5') {
//                ivCardType.visibility = View.VISIBLE
//                ivCardType.setImageResource(R.drawable.ic_card_master)
//            } else if (edtCardNumber.editText!!.text.toString()[0] == '4') {
//                ivCardType.visibility = View.VISIBLE
//                ivCardType.setImageResource(R.drawable.ic_card_visa)
//            } else {
//                ivCardType.visibility = View.INVISIBLE
//            }
//        } else if (ivCardType.visibility == View.VISIBLE) {
//            ivCardType.visibility = View.INVISIBLE
//        }*/
    }

    companion object {
        @JvmStatic
        fun newInstance(): CreditCardReqFragment {
            return CreditCardReqFragment()
        }
    }
}