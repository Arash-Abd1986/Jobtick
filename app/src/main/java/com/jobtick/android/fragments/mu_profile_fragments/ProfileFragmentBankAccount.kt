package com.jobtick.android.fragments.mu_profile_fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.jobtick.android.R
import com.jobtick.android.activities.ActivityBase
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.databinding.FragmentProfileBankAccountBinding
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.payment.AddBankAccount
import com.jobtick.android.payment.AddBankAccountImpl
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

class ProfileFragmentBankAccount : Fragment(){
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var activity: DashboardActivity
    private lateinit var sessionManager: SessionManager
    private var _binding: FragmentProfileBankAccountBinding? = null
    private val binding get() = _binding!!
    private var dob = ""
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
        SetToolbar(activity, "Bank Account", "Save", R.id.navigation_profile, binding.header, view)
        getBankAccount(activity)

        if(!sessionManager.userAccount.dob.isNullOrEmpty()) {
            val myDate = sessionManager.userAccount.dob.substring(0,
                sessionManager.userAccount!!.dob!!.length.coerceAtMost(10)
            )
            val sdf = SimpleDateFormat(getString(R.string.simpledateformatbackend), Locale.US)
            val date = sdf.parse(myDate)
            val format = SimpleDateFormat(getString(R.string.simpledateformat), Locale.US)
            val formattedDate: String = format.format(date!!)
            dob = myDate
            binding.startDateValue.text = formattedDate
        }
        else {
            binding.startDateValue.text = getString(R.string.choosedate)
            dob = ""
        }


        binding.header.txtAction.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_bank_account_to_navigation_profile_payments)
        }

        addBankAccount = object : AddBankAccountImpl(requireContext(), sessionManager) {
            override fun onSuccess() {
                activity.showToast("Saved Successfully!", activity)
                (requireActivity() as ActivityBase).hideProgressDialog()
            }

            override fun onError(e: Exception) {
                activity.showToast(getString(R.string.server_went_wrong), activity)
                (requireActivity() as ActivityBase).hideProgressDialog()
            }

            override fun onValidationError(errorType: ErrorType, message: String) {
                activity.showToast(message, activity)
                (requireActivity() as ActivityBase).hideProgressDialog()
            }
        }

        binding.calender.setOnClickListener {
            showCalendatMaterial()
         //   showCalendar()
        }
        binding.header.txtAction.setOnClickListener {
            if (checkValidation()) {
                activity.showProgressDialog()
                (addBankAccount as AddBankAccountImpl).add(
                    binding.edittextAccountHolder.editText?.text?.trim().toString(),
                    binding.edittextBsb.editText?.text?.trim().toString(),
                    binding.edittextAccountNumber.editText?.text?.trim().toString(),
                    dob
                )
            }
        }
        binding.header.back.setOnClickListener {
            view.findNavController()
                .navigate(R.id.action_navigation_profile_bank_account_to_navigation_profile_payments)
        }

        binding.edittextBsb.editText!!.setOnFocusChangeListener{ _, b ->
            if(b)
                binding.edittextBsb.editText!!.hint = "XXY-ZZZ"
            else
                binding.edittextBsb.editText!!.hint = ""

        }

        binding.edittextAccountNumber.editText!!.setOnFocusChangeListener{ _, b ->
            if(b)
                binding.edittextAccountNumber.editText!!.hint = "XXXXXX-YYYYYYY-ZZZ"
            else
                binding.edittextAccountNumber.editText!!.hint = ""
        }

        binding.edittextAccountHolder.editText!!.setOnFocusChangeListener{ _, b ->
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
            _binding = FragmentProfileBankAccountBinding.inflate(inflater, container, false)
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
                .setTheme(R.style.MaterialCalendarTheme)
                .setTitleText("Birthday")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        datePicker.show(childFragmentManager, "")

        datePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = it
            val format = SimpleDateFormat(getString(R.string.simpledateformat), Locale.US)
            val format1 = SimpleDateFormat(getString(R.string.simpledateformatbackend), Locale.US)
            val formattedDate: String = format.format(calendar.time)
            val formattedDate1: String = format1.format(calendar.time)
            binding.startDateValue.text = formattedDate
            dob = formattedDate1
        }
    }

    fun getBankAccount(context: Context) {
        sessionManager = SessionManager(context)
        (context as DashboardActivity).showProgressDialog()
        Helper.closeKeyboard(context)
        val call: Call<String?>? = ApiClient.getClientV1WithToken(sessionManager).getBankAccount(
            "XMLHttpRequest"
        )
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                context.hideProgressDialog()
                try {
                    if(response.isSuccessful) {
                        val jsonObject = JSONObject(response.body()!!)
                        if(!jsonObject.isNull("data")) {
                            val jsonObject2 = jsonObject.getJSONObject("data")

                            binding.edittextAccountNumber.editText?.setText(
                                "XXXXXX-YYYYYYY" + jsonObject2.getString(
                                    "account_number_last_four"
                                )
                            )
                            binding.edittextAccountHolder.editText?.setText(jsonObject2.getString("account_holder_name"))

                            binding.edittextBsb.editText?.setText(jsonObject2.getString("routing_number"))

                            if (jsonObject.has("message"))
                                context.showSuccessToast(jsonObject.getString("message"), context)
                        }
                        else {
                            activity.showToast("no data!", activity)
                        }

                    } else {
                        val jObjError = JSONObject(
                            response.errorBody()!!.string()
                        )
                        context.showToast(jObjError.getJSONObject("error").getString("message"), context)

                    }

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    context.showToast("Something Went Wrong", context)

                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                context.showToast(t.toString(), context)
                context.hideProgressDialog()
                Timber.e(call.toString())

            }
        })
    }

    private fun checkValidation(): Boolean {
        activity.hideKeyboard()
        when {
            binding.edittextAccountHolder.editText?.text.isNullOrEmpty() -> {
                binding.edittextAccountHolder.error = "Please enter Account Holder"
                return false
            }
            binding.edittextBsb.editText?.text.isNullOrEmpty() -> {
                binding.edittextBsb.error = "Please enter BSB"
                return false
            }
            binding.edittextAccountNumber.editText?.text.isNullOrEmpty() -> {
                binding.edittextAccountNumber.error = "Please enter Account Number"
                return false
            }
            dob == "" -> {
                activity.showToast("Please Select your Date Of Birth!", activity)
                return false
            }
        }
        return true
    }

}