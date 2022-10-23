package com.jobtick.android.fragments

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.activities.ActivityBase
import com.jobtick.android.activities.MakeAnOfferActivity
import com.jobtick.android.models.MakeAnOfferModel
import com.jobtick.android.models.TaskModel
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.models.calculation.EarningCalculationModel
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.SessionManager
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class MakeAnOfferBudgetFragment : Fragment() {
    private lateinit var txtServiceFee: TextView
    private lateinit var txtFeeTitle: TextView
    private lateinit var txtFinalBudget: TextView
    private lateinit var txtLearnHowLevelAffectsServiceFee: TextView
    private lateinit var btnNext: MaterialButton
    private lateinit var tvOffer: TextView
    private var gson: Gson? = null
    private var makeAnOfferModel: MakeAnOfferModel? = null
    private var makeAnOfferActivity: MakeAnOfferActivity? = null
    private var budgetCallbackFunction: BudgetCallbackFunction? = null
    private var userAccountModel: UserAccountModel? = null
    private var taskModel: TaskModel? = null
    private var sessionManager: SessionManager? = null
    private lateinit var budget: TextInputLayout
    private lateinit var infoIcon: AppCompatImageView
    private lateinit var label: MaterialTextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_make_an_offer_budget, container, false)
        ButterKnife.bind(this, view)
        gson = Gson()
        return view
    }

    override fun onResume() {
        super.onResume()
        if (budget.editText!!.text.isNotEmpty()) {
            btnNext.isEnabled = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setIds()
        makeAnOfferActivity = requireActivity() as MakeAnOfferActivity
        sessionManager = SessionManager(makeAnOfferActivity)

        makeAnOfferModel = MakeAnOfferModel()
        userAccountModel = UserAccountModel()
        userAccountModel = sessionManager!!.userAccount
        if (arguments != null && requireArguments().getParcelable<Parcelable?>(ConstantKey.MAKE_AN_OFFER_MODEL) != null) {
            makeAnOfferModel = requireArguments().getParcelable(ConstantKey.MAKE_AN_OFFER_MODEL)
        }
        if (arguments != null && requireArguments().getParcelable<Parcelable?>(ConstantKey.TASK) != null) {
            taskModel = requireArguments().getParcelable(ConstantKey.TASK)
        }
        if (makeAnOfferModel != null) {
            initLayout()
        }
        //toolbar.setNavigationOnClickListener(MakeAnOfferBudgetFragment.this);

        //Custom tool bar back here
        val ivBack = view.findViewById<ImageView>(R.id.ivBack)
        ivBack.setOnClickListener { makeAnOfferActivity!!.onBackPressed() }
        btnNext.setOnClickListener {
            if (budgetCallbackFunction != null) {
                if (!validation(true)) return@setOnClickListener
                makeAnOfferModel!!.offer_price = budget.editText!!.text.trim { it <= ' ' }.toString().toInt()
                budgetCallbackFunction!!.continueButtonBudget(makeAnOfferModel)
            }
        }
        txtFeeTitle.setOnClickListener { v: View? ->
            val infoBottomSheet = ServiceFeeInfoBottomSheet()
            infoBottomSheet.show(childFragmentManager, null)
        }
        txtLearnHowLevelAffectsServiceFee.setOnClickListener { v: View? -> }
    }

    private fun setIds() {
        txtServiceFee = requireView().findViewById(R.id.txt_service_fee)
        txtFeeTitle = requireView().findViewById(R.id.txt_fee_title)
        txtFinalBudget = requireView().findViewById(R.id.txt_final_budget)
        tvOffer = requireView().findViewById(R.id.tvOffer)
        txtLearnHowLevelAffectsServiceFee = requireView().findViewById(R.id.txt_learn_how_level_affects_service_fee)
        btnNext = requireView().findViewById(R.id.btn_next)
        budget = requireView().findViewById(R.id.budget)
        infoIcon = requireView().findViewById(R.id.info)
        label = requireView().findViewById(R.id.label)


        budget.editText!!.setOnFocusChangeListener { _, b ->
            if (b) {
                budget.editText!!.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dolar, 0, 0, 0)
            }
            if (b) {
                infoIcon.setColorFilter(resources.getColor(R.color.primary_500), android.graphics.PorterDuff.Mode.SRC_IN)
                label.setTextColor(resources.getColor(R.color.neutral_light_600))
            } else {
                infoIcon.setColorFilter(resources.getColor(R.color.neutral_light_400), android.graphics.PorterDuff.Mode.SRC_IN)
                label.setTextColor(resources.getColor(R.color.neutral_light_400))
            }
        }
        budget.editText?.doOnTextChanged { text, _, _, _ ->
            btnNext.isEnabled = validation(false)
            if (validation(false)) {
                infoIcon.setColorFilter(resources.getColor(R.color.primary_500), android.graphics.PorterDuff.Mode.SRC_IN)
                label.setTextColor(resources.getColor(R.color.neutral_light_600))
                budget.boxStrokeColor = resources.getColor(R.color.primary_500)
                budget.hintTextColor = AppCompatResources.getColorStateList(requireContext(), R.color.primary_500)
            } else {
                infoIcon.setColorFilter(resources.getColor(R.color.primary_error), android.graphics.PorterDuff.Mode.SRC_IN)
                label.setTextColor(resources.getColor(R.color.primary_error))
                budget.boxStrokeColor = resources.getColor(R.color.primary_error)
                budget.hintTextColor = AppCompatResources.getColorStateList(requireContext(), R.color.primary_error)
            }
            text?.let {
                if (it.isNotEmpty()) calculate(it.toString())
            }
            if (text!!.isNotEmpty())
                budget.editText!!.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dolar, 0, 0, 0)
            else
                budget.editText!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }
    }

    private fun initLayout() {
        if (makeAnOfferModel != null && makeAnOfferModel!!.offer_price != 0) {
            budget.editText!!.setText(String.format(Locale.ENGLISH, "%d", makeAnOfferModel!!.offer_price))
            txtServiceFee.text = String.format(Locale.ENGLISH, "$%.1f", makeAnOfferModel!!.allFee)
            txtFinalBudget.text = String.format(Locale.ENGLISH, "$%.1f", makeAnOfferModel!!.youWillReceive)
        }
        tvOffer.text = String.format(Locale.ENGLISH, "$%d", taskModel!!.budget)
    }


    var is4Digits = false


    private fun validation(showToast: Boolean): Boolean {
        if (budget.editText!!.text.isEmpty()) {
            if (showToast) (requireActivity() as ActivityBase).showToast("Please enter your offer.", requireContext())
            return false
        }
        if (budget.editText!!.text.toString().toInt() < 5) {
            if (showToast) (requireActivity() as ActivityBase).showToast("You can't offer lower than 5 dollars.", requireContext())
            return false
        }
        if (budget.editText!!.text.toString().toInt() < taskModel!!.budget) {
            if (showToast) (requireActivity() as ActivityBase).showToast("You can't offer lower than poster offer.", requireContext())
            return false
        }
        if (budget.editText!!.text.toString().toInt() > 9999) {
            if (showToast) (requireActivity() as ActivityBase).showToast("You can't offer more than 9999 dollars.", requireContext())
            return false
        }
        return true
    }

    fun calculate(amount: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Constant.URL_OFFERS_EARNING_CALCULATION,
                Response.Listener { response: String? ->
                    try {
                        val jsonObject = JSONObject(response)
                        val data = jsonObject.getString("data")
                        val model = gson!!.fromJson(data, EarningCalculationModel::class.java)
                        makeAnOfferModel!!.allFee = model.serviceFee + model.gstAmount
                        makeAnOfferModel!!.youWillReceive = model.netEarning
                        txtServiceFee.text = String.format(Locale.ENGLISH, "$%.1f", makeAnOfferModel!!.allFee)
                        txtFinalBudget.text = String.format(Locale.ENGLISH, "$%.1f", makeAnOfferModel!!.youWillReceive)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    val networkResponse = error.networkResponse
                    if (networkResponse?.data != null) {
                        val jsonError = String(networkResponse.data)
                        try {
                            val jsonObject = JSONObject(jsonError)
                            val jsonObjectError = jsonObject.getJSONObject("error")
                            val message = jsonObjectError.getString("message")
                            (requireActivity() as ActivityBase).showToast(message, requireContext())
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            (requireActivity() as ActivityBase).showToast("Something went wrong", requireContext())
                        }
                    } else {
                        (requireActivity() as ActivityBase).showToast("Something went wrong", requireContext())
                    }
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager!!.tokenType + " " + sessionManager!!.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["X-Requested-With"] = "XMLHttpRequest"
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }

            override fun getParams(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["amount"] = amount
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)
    }

    interface BudgetCallbackFunction {
        fun continueButtonBudget(makeAnOfferModel: MakeAnOfferModel?)
    }

    companion object {
        fun newInstance(makeAnOfferModel: MakeAnOfferModel?, taskModel: TaskModel?, budgetCallbackFunction: BudgetCallbackFunction?): MakeAnOfferBudgetFragment {
            val args = Bundle()
            args.putParcelable(ConstantKey.MAKE_AN_OFFER_MODEL, makeAnOfferModel)
            args.putParcelable(ConstantKey.TASK, taskModel)
            val fragment = MakeAnOfferBudgetFragment()
            fragment.budgetCallbackFunction = budgetCallbackFunction
            fragment.arguments = args
            return fragment
        }
    }
}