package com.jobtick.android.fragments.mu_profile_fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.databinding.FragmentProfileAccountBinding
import com.jobtick.android.databinding.FragmentProfilePaymentsBinding
import com.jobtick.android.models.response.getbalance.CreditCardModel
import com.jobtick.android.utils.*
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragmentPayments : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var activity: DashboardActivity
    private lateinit var sessionManager: SessionManager
    private var _binding: FragmentProfilePaymentsBinding? = null
    private val binding get() = _binding!!
    private var hasCreditCard: Boolean = false
    private lateinit var creditCardModel : CreditCardModel

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
        SetToolbar(activity, "Payments", "", R.id.navigation_profile, binding.header, view)


        if(sessionManager.roleLocal == "poster") {

            binding.bankAccount.visibility = View.GONE
            binding.billingAddress.visibility = View.GONE
            binding.serperator.visibility = View.GONE
            binding.creditCardParent.visibility = View.VISIBLE
            paymentMethod
        }
        else {
            binding.bankAccount.visibility = View.VISIBLE
            binding.billingAddress.visibility = View.VISIBLE
            binding.creditCardParent.visibility = View.GONE
            binding.serperator.visibility = View.VISIBLE

        }

        binding.bankAccount.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_payments_to_navigation_profile_bank_account)
        }

        binding.billingAddress.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_payments_to_navigation_profile_billing_address)
        }

        binding.paymentHistory.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_payments_to_navigation_profile_payment_history)
        }

        binding.creditCardParent.setOnClickListener {
            if(!hasCreditCard)
                view.findNavController().navigate(R.id.action_navigation_profile_payments_to_navigation_profile_credit_card)
            else
            {
                try {

                val bundle = bundleOf("holdername" to creditCardModel.data?.get(0)?.card?.user?.first_name.toString() + " "
                 +  creditCardModel.data?.get(0)?.card?.user?.last_name.toString(), "cardnumber" to String.format(
                    "xxxx xxxx xxxx %s",
                    creditCardModel!!.data!![0].card!!.last4
                ), "cvc" to "1234", "expdate" to String.format(
                    Locale.ENGLISH, "%s/%d",
                    if (creditCardModel!!.data!![0].card!!.exp_month!! < 10) "0" + creditCardModel!!.data!![0].card!!.exp_month else creditCardModel!!.data!![0].card!!.exp_month,
                    creditCardModel!!.data!![0].card!!.exp_year
                ))
                view.findNavController().navigate(R.id.action_navigation_profile_payments_to_navigation_profile_credit_card_preview, bundle)
                }catch (e: Exception) {}
            }

        }
        binding.header.back.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_payments_to_navigation_profile)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilePaymentsBinding.inflate(inflater, container, false)
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

    private val paymentMethod: Unit
        get() {
            activity.showProgressDialog()
            val stringRequest: StringRequest =
                object : StringRequest(
                    Method.GET, Constant.URL_PAYMENTS_METHOD,
                    Response.Listener { response: String? ->
                        Timber.e(response)
                        try {
                            val jsonObject = JSONObject(response!!)
                            Timber.e(jsonObject.toString())
                            if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                                if (jsonObject.getBoolean("success")) {
                                    Log.d("creditres", jsonObject.toString())
                                    if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                        val jsonString = jsonObject.toString() // http request
                                        val gson = Gson()
                                        creditCardModel =
                                            gson.fromJson(jsonString, CreditCardModel::class.java)
                                        if (creditCardModel != null && creditCardModel!!.data != null && creditCardModel!!.data!![0].card != null) {
                                            val brand = creditCardModel!!.data!![0].card!!.brand
                                            binding.creditCard.text = brand
                                            if (brand.equals(
                                                    CardTypes.MASTER.type,
                                                    ignoreCase = true
                                                )
                                            ) binding.cardType.setImageDrawable(
                                                ContextCompat.getDrawable(
                                                    activity,
                                                    R.drawable.ic_card_master
                                                )
                                            )
                                            if (brand.equals(
                                                    CardTypes.VISA.type,
                                                    ignoreCase = true
                                                )
                                            ) binding.cardType.setImageDrawable(
                                                ContextCompat.getDrawable(
                                                    activity,
                                                    R.drawable.ic_card_visa
                                                )
                                            )
                                            if (brand!!.contains(CardTypes.AMERICAN.type)) binding.cardType.setImageDrawable(
                                                ContextCompat.getDrawable(
                                                    activity,
                                                    R.drawable.ic_card_american_express
                                                )
                                            )
//                                            edtExpiryDate.text = String.format(
//                                                Locale.ENGLISH, "Expiry Date: %s/%d",
//                                                if (creditCardModel!!.data!![0].card!!.exp_month!! < 10) "0" + creditCardModel!!.data!![0].card!!.exp_month else creditCardModel!!.data!![0].card!!.exp_month,
//                                                creditCardModel!!.data!![0].card!!.exp_year
//                                            )
                                            binding.creditCard.text = String.format(
                                                "xxxx xxxx xxxx %s",
                                                creditCardModel!!.data!![0].card!!.last4
                                            )
                                            setupViewCreditCard(true)

                                        } else {
                                            setupViewCreditCard(false)
                                        }
                                        activity.hideProgressDialog()
                                    }
                                } else {
                                    setupViewCreditCard(false)
                                    activity.showToast("Something went Wrong", activity)
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
                                        activity.hideProgressDialog()
                                        return@ErrorListener
                                    }
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                        Timber.e(error.toString())
                        activity.errorHandle1(error.networkResponse)
                        activity.hideProgressDialog()
                    }
                ) {
                    override fun getHeaders(): Map<String, String> {
                        val map1: MutableMap<String, String> = HashMap()
                        map1["authorization"] =
                            sessionManager.tokenType + " " + sessionManager.accessToken
                        map1["Content-Type"] = "application/x-www-form-urlencoded"
                        map1["Version"] = BuildConfig.VERSION_CODE.toString()
                        // map1.put("X-Requested-With", "XMLHttpRequest");
                        return map1
                    }
                }
            stringRequest.retryPolicy = DefaultRetryPolicy(
                0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            val requestQueue = Volley.newRequestQueue(activity)
            requestQueue.add(stringRequest)
        }

    fun setupViewCreditCard(boolean: Boolean) {
        if(!boolean)
            binding.creditcardmidParent.visibility = View.GONE
        else
            binding.creditcardmidParent.visibility = View.VISIBLE
        hasCreditCard = boolean
    }

}