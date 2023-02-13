package com.jobtick.android.fragments.mu_profile_fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.activities.ActivityBase
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.adapers.PaymentHistoryListAdapter
import com.jobtick.android.databinding.FragmentProfilePaymentHistoryBinding
import com.jobtick.android.fragments.PaymentHistoryBottomSheet
import com.jobtick.android.models.payments.PaymentHistory
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.SetToolbar
import com.jobtick.android.widget.EndlessRecyclerViewOnScrollListener
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragmentPaymentHistory : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var activity: DashboardActivity
    private lateinit var sessionManager: SessionManager
    private var _binding: FragmentProfilePaymentHistoryBinding? = null
    private val binding get() = _binding!!
    private var textFilter = ""
    private var onScrollListener: EndlessRecyclerViewOnScrollListener? = null
    private var firstInit = false
    private var startDate = ""
    private var endDate = ""
    private var loadingStatus = false
    private var paymentHistoryBundle: Bundle? = null

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
        getPaymentHistory(startDate, endDate)
        loadingStatus = true
        Log.d("here", "1")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SetToolbar(activity, "Payments History", "", R.id.navigation_profile, binding.header, view)
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

        val format = SimpleDateFormat(getString(R.string.simpledateformat), Locale.getDefault())
        val formatForApi = SimpleDateFormat(getString(R.string.simpledateformatbackend), Locale.getDefault())
        val formattedDate: String = format.format(calendar.time)
        val formattedDateForApi: String = formatForApi.format(calendar.time)
        binding.textStartDate.text = formattedDate
        startDate = formattedDateForApi
        binding.textEndDate.text = formattedDate
        endDate = formattedDateForApi


        binding.recycler.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_payment_history_to_navigation_profile_payment_details)
        }
        binding.header.back.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_payment_history_to_navigation_profile_payments)
        }

        binding.textDownloadCsv.setOnClickListener{
            getPaymentHistoryCsv(startDate, endDate)
        }

        binding.filters.toggleButton.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->

            if(checkedId == binding.filters.custom.id && isChecked)
                binding.customParent.visibility = View.VISIBLE
            else
                binding.customParent.visibility = View.GONE

            textFilter = when(toggleButton.checkedButtonId) {
                R.id.day -> "d"
                R.id.week -> "w"
                R.id.month -> "m"
                else -> ""
            }

            firstInit = textFilter != ""
            Log.d("here", textFilter)

            if(isChecked)
                getPaymentHistory(startDate, endDate)
        }

        binding.parentStart.setOnClickListener {
            showCalendatMaterial("Start Date")
        }

        binding.parentEnd.setOnClickListener {
            showCalendatMaterial("End Date")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("here", "3")

        _binding = FragmentProfilePaymentHistoryBinding.inflate(inflater, container, false)
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

    fun getPaymentHistory(from: String?, to: String?) {
        val type: String = if (sessionManager.roleLocal == "poster") "poster_payment_filter=true" else "worker_payment_filter=true"
        val url: String = if (!firstInit) {
            Constant.URL_GET_PAYMENT_HISTORY_FILTER + "?" +
                    type + "&date_from=" + from + "&date_to=" + to
        } else Constant.URL_GET_PAYMENT_HISTORY_FILTER + "?" + type
        activity.showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(
            Method.GET,
            "$url&limit=50",
            Response.Listener { response: String? ->
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.has("success") && jsonObject.has("data")) {
                        val jsonObject_data = jsonObject.getJSONArray("data")
                        val googleJson = Gson()
                        val listType = object :
                            TypeToken<List<PaymentHistory?>?>() {}.type
                        val jsonObjList =
                            googleJson.fromJson<List<PaymentHistory>>(
                                jsonObject_data.toString(),
                                listType
                            )
                        fillData(jsonObjList, jsonObject.getString("total_amount"), firstInit)
                    }
                } catch (e: JSONException) {

                    e.printStackTrace()
                }
                //                    ((ActivityBase) requireActivity()).hideProgressDialog();
                activity.hideProgressDialog()
            },
            Response.ErrorListener { error: VolleyError ->
                (requireActivity() as ActivityBase).errorHandle1(error.networkResponse)
                //                    ((ActivityBase) requireActivity()).hideProgressDialog();
                activity.hideProgressDialog()


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
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            0, -1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }


    fun getPaymentHistoryCsv(from: String?, to: String?) {
        val type: String
        type = if (sessionManager.roleLocal == "poster") "poster_payment_filter=true" else "worker_payment_filter=true"
        val url: String
        url = if (!firstInit) {
            Constant.URL_GET_PAYMENT_HISTORY_CSV + "?" +
                    type + "&date_from=" + from + "&date_to=" + to
        } else Constant.URL_GET_PAYMENT_HISTORY_CSV + "?" + type
        activity.showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(
            Method.GET,
            "$url&limit=50",
            Response.Listener { response: String? ->
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.has("success") && jsonObject.has("data")) {
                        val jsonObject_data = jsonObject.getJSONArray("data")
                        val googleJson = Gson()
                        val listType = object :
                            TypeToken<List<PaymentHistory?>?>() {}.type
                        val jsonObjList =
                            googleJson.fromJson<List<PaymentHistory>>(
                                jsonObject_data.toString(),
                                listType
                            )
                        fillData(jsonObjList, jsonObject.getString("total_amount"), firstInit)
                    }
                } catch (e: JSONException) {

                    e.printStackTrace()
                }
                //                    ((ActivityBase) requireActivity()).hideProgressDialog();
                activity.hideProgressDialog()
            },
            Response.ErrorListener { error: VolleyError ->
                (requireActivity() as ActivityBase).errorHandle1(error.networkResponse)
                //                    ((ActivityBase) requireActivity()).hideProgressDialog();
                activity.hideProgressDialog()


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
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            0, -1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }


    private fun fillData(data: List<PaymentHistory>, total_amount: String, firstInit: Boolean) {

        binding.recycler.layoutManager = LinearLayoutManager(activity)
        onScrollListener = object : EndlessRecyclerViewOnScrollListener() {
            override fun onLoadMore(currentPage: Int) {}
            override fun onScrollDown() {
                TODO("Not yet implemented")
            }

            override fun onScrollUp() {
                TODO("Not yet implemented")
            }

            override fun getTotalItem(): Int {
                return 0
            }
        }
        binding.recycler.addOnScrollListener(onScrollListener as EndlessRecyclerViewOnScrollListener)
        binding.recycler.adapter = PaymentHistoryListAdapter(
            data,
            sessionManager.roleLocal == "poster"
        ) { paymentHistory: PaymentHistory? ->
            val bundle = bundleOf("payment" to paymentHistory, "slug" to paymentHistory!!.task.slug.toString())
            view?.findNavController()?.navigate(R.id.action_navigation_profile_payment_history_to_navigation_profile_payment_details, bundle)
        }
        if (data.isEmpty()) {
            activity.showToast("no data!", activity)
        }
    }
    fun showCalendatMaterial(str: String) {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(str)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        datePicker.show(childFragmentManager, str)

        datePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = it
            val format = SimpleDateFormat(getString(R.string.simpledateformat), Locale.getDefault())
            val formatForApi = SimpleDateFormat(getString(R.string.simpledateformatbackend), Locale.getDefault())
            val formattedDate: String = format.format(calendar.time)
            val formattedDateForApi: String = formatForApi.format(calendar.time)
            if(datePicker.tag == "Start Date") {
                binding.textStartDate.text = formattedDate
                startDate = formattedDateForApi
            }
            else {
                binding.textEndDate.text = formattedDate
                endDate = formattedDateForApi
                getPaymentHistory(startDate, endDate)
            }
        }
    }


}