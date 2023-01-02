package com.jobtick.android.fragments.mu_profile_fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.activities.ChatActivity
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.activities.TaskDetailsActivity
import com.jobtick.android.databinding.FragmentProfileBankAccountBinding
import com.jobtick.android.models.OfferModel
import com.jobtick.android.models.TaskModel
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.*
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
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
    private var cyear = 0
    private var cmonth = 0
    private var cday = 0
    private var str_due_date: String? = null
    private var calendarView: CalendarView? = null

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
        SetToolbar(activity, "Account", "", R.id.navigation_profile, binding.header, view)
        getBankAccount(activity)
//        DatePickerDialog.OnDateSetListener { view1: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
//            val lMonth = month + 1
//            str_due_date = Tools.getDayMonthDateTimeFormat("$year-$lMonth-$dayOfMonth")
//            binding.startDateValue.text = str_due_date
//        }

        val calendar = Calendar.getInstance()
        cyear = calendar[Calendar.YEAR]
        cmonth = calendar[Calendar.MONTH]
        cday = calendar[Calendar.DAY_OF_MONTH]
        binding.startDateValue.text  = Tools.getDayMonthDateTimeFormat("$cyear-$cmonth-$cday")

        binding.calender.setOnClickListener {
            showCalendar()
        }

        binding.header.back.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_bank_account_to_navigation_profile_payments)
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

    fun showCalendar() {
        val accept: MaterialButton?
        val decline: MaterialButton?
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.bottom_sheet_date_picker)
        accept = dialog.findViewById(R.id.btn_accept)
        decline = dialog.findViewById(R.id.btn_decline)
        calendarView = dialog.findViewById(R.id.calenderView) as CalendarView
        calendarView!!.setOnDateChangeListener { arg0: CalendarView?, year: Int, month: Int, date: Int ->
            cmonth = month + 1
            cyear = year
            cday = date
            str_due_date = Tools.getDayMonthDateTimeFormat("$cyear-$cmonth-$cday")
            binding.startDateValue.text = str_due_date
        }

        decline.setOnClickListener {
            dialog.cancel()
            binding.startDateValue.text = sessionManager.userAccount.dob
        }

        accept.setOnClickListener {
            dialog.cancel()
        }
        dialog.show()

    }

//    private fun getBankAccount() {
//        activity.showProgressDialog()
//        val stringRequest: StringRequest = object : StringRequest(
//            Method.GET, Constant.BASE_URL + Constant.ADD_ACCOUNT_DETAILS,
//            com.android.volley.Response.Listener {
//
//            },
//            com.android.volley.Response.ErrorListener { activity.hideProgressDialog() }) {
////            override fun getParams(): Map<String, String> {
////                val map1: MutableMap<String, String> = HashMap()
////                return map1
////            }
//
//            override fun getHeaders(): Map<String, String> {
//                val map1: MutableMap<String, String> = HashMap()
//                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
//                map1["Content-Type"] = "application/json"
//                map1["X-Requested-With"] = "XMLHttpRequest"
//                map1["Version"] = BuildConfig.VERSION_CODE.toString()
//                return map1
//            }
//        }
//        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
//            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
//        val requestQueue = Volley.newRequestQueue(activity)
//        requestQueue.add(stringRequest)
//        Timber.e(stringRequest.url)
//    }


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
                        Log.d("onresponse", response.body().toString())
                        val jsonObject = JSONObject(response.body()!!)
                        context.showSuccessToast(jsonObject.getString("message"), context)

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
                Log.d("onresponse1", t.toString())

                context.showToast(t.toString(), context)
                context.hideProgressDialog()
                Timber.e(call.toString())

            }
        })
    }


}