package com.jobtick.android.material.ui.postajob

import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.jobtick.android.R
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.coroutines.Status
import com.jobtick.android.network.model.response.DataX
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.*
import com.jobtick.android.viewmodel.PostAJobViewModel
import com.jobtick.android.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PostAJobBudgetFragment : Fragment(), BudgetsAdapter.BudgetClickListener {

    private lateinit var activity: PostAJobActivity
    private lateinit var next: MaterialButton
    private lateinit var btnAmount: MaterialButton
    private lateinit var budget: TextInputLayout
    private lateinit var sessionManagerA: SessionManager
    private lateinit var viewModel: PostAJobViewModel
    private lateinit var rlAmount: RecyclerView
    private lateinit var budgetsAdapter: BudgetsAdapter
    private var budgetTypeId: Int? = null


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_a_job_budget, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVars()
        initVM()
    }


    private fun initVars() {
        sessionManagerA = SessionManager(requireContext())

        activity = (requireActivity() as PostAJobActivity)
        budgetsAdapter = BudgetsAdapter()
        budgetsAdapter.budgetClickListener = this
        next = requireView().findViewById(R.id.btn_next)
        btnAmount = requireView().findViewById(R.id.btn_amount)
        budget = requireView().findViewById(R.id.budget)

        rlAmount = requireView().findViewById(R.id.rl_amount)
        rlAmount.adapter = budgetsAdapter
        rlAmount.layoutManager = GridLayoutManager(requireContext(), 2)
        rlAmount.addItemDecoration(SpacesItemDecoration((16).dpToPx()))

        next.setOnClickListener {
            resetError()
            if (btnAmount.isChecked) {
                if (checkValidation()) {
                    activity.viewModel.setBudget(budget.editText!!.text.toString())
                    activity.viewModel.setIsBudgetSpecific(true)
                    activity.navController.navigate(R.id.detailsFragment)
                }
            } else {
                activity.viewModel.setIsBudgetSpecific(false)
                activity.viewModel.setBudgetTypeID(budgetTypeId!!)
                activity.navController.navigate(R.id.detailsFragment)

            }
        }
        budget.editText!!.setOnFocusChangeListener { view, b ->
            if (b){
                budget.editText!!.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dolar, 0, 0, 0)
            }
        }
        budget.editText?.doOnTextChanged { text, _, _, _ ->
            text?.let {
                next.isEnabled = !text.isNullOrEmpty()
            }
            if (text!!.isNotEmpty())
                budget.editText!!.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dolar, 0, 0, 0)
            else
                budget.editText!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }

        btnAmount.setOnClickListener {
            activity.viewModel.setIsBudgetSpecific(true)
            budgetsAdapter.unselectAll()
            budget.visible()
            next.isEnabled = budget.editText!!.text?.length != null && budget.editText!!.text.length > 1
        }

    }

    private fun resetError() {
        budget.isErrorEnabled = false
        budget.error = ""
    }

    private fun checkValidation(): Boolean {
        when {

            budget.editText?.text?.toString()?.toInt()!! < 6 -> {
                setError("The price must be between \$5 and \$9999", budget)
                return false
            }
        }
        return true
    }

    private fun setError(error: String, txtInput: TextInputLayout) {
        budget.isErrorEnabled = true
        val errorDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_error)
        val ss = SpannableString("    $error\n")
        errorDrawable!!.setBounds(0, 0, errorDrawable.intrinsicWidth, errorDrawable.intrinsicHeight)
        val span = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ImageSpan(errorDrawable, ImageSpan.ALIGN_CENTER)
        } else {
            ImageSpan(errorDrawable, ImageSpan.ALIGN_BOTTOM)
        }
        ss.setSpan(span, 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        txtInput.error = ss
    }

    private fun initVM() {
        sessionManagerA = SessionManager(requireContext())

        viewModel = ViewModelProvider(
                requireActivity(),
                ViewModelFactory(ApiHelper(ApiClient.getClientV1WithToken(sessionManagerA)))
        ).get(PostAJobViewModel::class.java)
        if (activity.viewModel.state.value.isBudgetSpecific == null) {
            viewModel.getBudgets()
        }else{
            next.isEnabled = true
        }
        if (activity.viewModel.state.value.isBudgetSpecific == true) {
            budget.visible()
            btnAmount.isChecked = true
            budget.editText!!.setText(activity.viewModel.state.value.budget)
        }
        activity.viewModel.response.observe(viewLifecycleOwner) {
            activity.hideProgressDialog()
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        budgetsAdapter.setItems(it.data!!.data as ArrayList<DataX>)
                    }
                    Status.ERROR -> {
                        activity.showToast(getString(R.string.msg_network_error), requireContext())
                    }
                    Status.LOADING -> {
                        activity.showProgressDialog()
                    }
                }
            }
        }


    }


    override fun onBudgetClick(data: DataX) {
        budget.gone()
        btnAmount.isChecked = false
        activity.viewModel.setIsBudgetSpecific(false)
        activity.viewModel.setBudgetData(data)
        next.isEnabled = true
        budgetTypeId = data.id
    }

}