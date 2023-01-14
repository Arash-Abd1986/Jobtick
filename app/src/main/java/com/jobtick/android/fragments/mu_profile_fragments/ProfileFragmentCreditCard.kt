package com.jobtick.android.fragments.mu_profile_fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.android.volley.NetworkResponse
import com.google.android.material.datepicker.MaterialDatePicker
import com.jobtick.android.R
import com.jobtick.android.activities.ActivityBase
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.databinding.FragmentProfileBankAccountBinding
import com.jobtick.android.databinding.FragmentProfileCreditCardBinding
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.payment.AddBankAccount
import com.jobtick.android.payment.AddBankAccountImpl
import com.jobtick.android.payment.AddCreditCard
import com.jobtick.android.payment.AddCreditCardImpl
import com.jobtick.android.utils.Helper
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.SetToolbar
import com.jobtick.android.utils.hideKeyboard
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragmentCreditCard : Fragment(){
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var activity: DashboardActivity
    private lateinit var sessionManager: SessionManager
    private var _binding: FragmentProfileCreditCardBinding? = null
    private val binding get() = _binding!!
    private var cyear = 0
    private var cmonth = 0
    private var dob = ""
    private var cday = 0
    private var addCreditCard: AddCreditCard? = null

    private var addBankAccount: AddBankAccount? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = (requireActivity() as DashboardActivity)
        sessionManager = SessionManager(context)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SetToolbar(activity, "Add Credit Card", "Save", R.id.navigation_profile, binding.header, view)



        binding.header.txtAction.setOnClickListener {


            if(checkValidation())
            addCreditCard!!.getToken(binding.edittextCardNumber.editText!!.text.toString().replace(" ", ""),
                dob.split("-")[1].toInt(), dob.split("-")[0].toInt(),
                binding.edittextCvc.editText!!.text.toString(),
                binding.edittextAccountHolder.editText!!.text.toString())

        }

        addCreditCard = object : AddCreditCardImpl(activity, sessionManager) {
            override fun onSuccess() {
                activity.hideProgressDialog()

            }

            override fun onError(e: Exception) {
                activity.showToast(getString(R.string.credit_card_error), activity)
                activity.hideProgressDialog()
            }

            override fun onNetworkResponseError(networkResponse: NetworkResponse) {
                activity.errorHandle1(networkResponse)
                activity.hideProgressDialog()
            }

            override fun onValidationError(validationErrorType: ValidationErrorType, message: String) {
                activity.showToast(message, activity)
                activity.hideProgressDialog()
            }
        }


        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("EEEE, MMMM d yyyy")
        val formattedDate: String = format.format(calendar.time)
        //binding.startDateValue.text = formattedDate
        cyear = calendar[Calendar.YEAR]
        cmonth = calendar[Calendar.MONTH]
        cday = calendar[Calendar.DAY_OF_MONTH]
      //  binding.startDateValue.text = Tools.getDayMonthDateTimeFormat("$cyear-$cmonth-$cday")
        binding.edittextExpiryDate.editText?.setOnClickListener {
            showCalendatMaterial()
         //   showCalendar()
        }

        binding.header.back.setOnClickListener {
            view.findNavController()
                .navigate(R.id.action_navigation_profile_credit_card_to_navigation_profile_payments)
        }
        binding.edittextExpiryDate.editText?.isFocusable = false
        binding.edittextCvc.editText!!.setOnFocusChangeListener{view, b ->
            if(b)
                binding.edittextCvc.editText!!.hint = "e.g. 1234"
            else
                binding.edittextCvc.editText!!.hint = ""
        }

        binding.edittextExpiryDate.editText!!.setOnFocusChangeListener{view, b ->
            if(b)
                binding.edittextExpiryDate.editText!!.hint = "MM/YYYY"
            else
                binding.edittextExpiryDate.editText!!.hint = ""

        }

        binding.edittextCardNumber.editText!!.setOnFocusChangeListener{view, b ->
            if(b)
                binding.edittextCardNumber.editText!!.hint = "1234 5678 9101 1121"
            else
                binding.edittextCardNumber.editText!!.hint = ""

        }

        binding.edittextAccountHolder.editText!!.setOnFocusChangeListener{view, b ->
            if(b)
                binding.edittextAccountHolder.editText!!.hint = "e.g. Oliver Smith"
            else
                binding.edittextAccountHolder.editText!!.hint = ""


        }


    }
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            _binding = FragmentProfileCreditCardBinding.inflate(inflater, container, false)
            return binding.root
        }

        companion object {
            @JvmStatic
            fun newInstance(param1: String, param2: String) =
                ProfileFragmentAccount().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
        }

    fun showCalendatMaterial() {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Birthday")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        datePicker.show(childFragmentManager, "")

        datePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = it
            val format = SimpleDateFormat("yyyy-MM-dd")
            val format1 = SimpleDateFormat("MM/yyyy")
            val formattedDate: String = format.format(calendar.time)
            val formattedDate1: String = format1.format(calendar.time)
            binding.edittextExpiryDate.editText?.setText(formattedDate1)
            dob = formattedDate
            Log.d("dateinbankaccount", dob)
        }
    }

    private fun checkValidation(): Boolean {
        activity.hideKeyboard()
        when {
            binding.edittextAccountHolder.editText?.text.isNullOrEmpty() -> {
                binding.edittextAccountHolder.setError("Please enter Account Holder")
                return false
            }
            binding.edittextCardNumber.editText?.text.isNullOrEmpty() -> {
                binding.edittextCardNumber.setError("Please enter Card Number")
                return false
            }
            binding.edittextCvc.editText?.text.isNullOrEmpty() -> {
                binding.edittextCvc.setError("Please enter CVC")
                return false
            }
            dob.equals("") -> {
                activity.showToast("Please Select Expiry Date", activity)
                return false
            }
        }
        return true
    }

}