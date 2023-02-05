package com.jobtick.android.fragments.mu_profile_fragments

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.databinding.FragmentProfileAccountBinding
import com.jobtick.android.databinding.FragmentProfilePaymentViewCreditCardBinding
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

class ProfileFragmentViewCreditCard : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var activity: DashboardActivity
    private lateinit var sessionManager: SessionManager
    private var _binding: FragmentProfilePaymentViewCreditCardBinding? = null
    private val binding get() = _binding!!

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
        binding.textAccountHolder.text = requireArguments().getString("holdername")
        binding.textCardNumber.text = requireArguments().getString("cardnumber")
        binding.cvc.text = requireArguments().getString("cvc")
        binding.expDate.text = requireArguments().getString("expdate")

        binding.txtAction.setOnClickListener { showDialog() }
        binding.back.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_credit_card_preview_to_navigation_profile_payments)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilePaymentViewCreditCardBinding.inflate(inflater, container, false)
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

    fun showDialog() {
        val cancel: MaterialButton?
        val delete: MaterialButton?
        val title: TextView?
        val mainTitle: TextView?
        val dialog = Dialog(activity, R.style.AnimatedDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        dialog.setContentView(R.layout.dialog_discard_changes_new)

        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog.window!!.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        cancel = dialog.findViewById(R.id.cancel)
        delete = dialog.findViewById(R.id.discard)
        title = dialog.findViewById(R.id.title)
        mainTitle = dialog.findViewById(R.id.mainTitle)

        delete.text = getString(R.string.delete)
        cancel.text = getString(R.string.cancel)
        mainTitle.text = getString(R.string.delete_credit_card)

        title.setText(activity.getString(R.string.profile_delete_credit_card_warning))

        cancel.setOnClickListener {
            dialog.cancel()
        }
        delete.setOnClickListener {
            //TODO delete Api
            //viewModel.deleteAccount(activity)
            dialog.cancel()
        }

        dialog.show()

    }

    private val deletecreditCard: Unit
        get() {
            activity.showProgressDialog()
            val stringRequest: StringRequest =
                object : StringRequest(
                    Method.DELETE, Constant.URL_PAYMENTS_METHOD,
                    Response.Listener { response: String? ->
                        Timber.e(response)
                    },
                    Response.ErrorListener { error: VolleyError ->
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

}