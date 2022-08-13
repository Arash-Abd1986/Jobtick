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
            if (btnAmount.isChecked) {
                activity.viewModel.setBudget(budget.editText!!.text.toString())
                activity.viewModel.setIsBudgetSpecific(true)
            } else
                activity.viewModel.setIsBudgetSpecific(false)
            activity.navController.navigate(R.id.detailsFragment)
        }
        budget.editText?.doOnTextChanged { text, _, _, _ ->
            next.isEnabled = text?.length != null && text.length > 1
            if (text!!.isNotEmpty())
                budget.editText!!.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dolar, 0, 0, 0)
            else
                budget.editText!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }

        btnAmount.setOnClickListener {
            viewModel.setIsBudgetSpecific(true)
            budgetsAdapter.unselectAll()
            budget.visible()
            next.isEnabled = budget.editText!!.text?.length != null && budget.editText!!.text.length > 1
        }
    }

    private fun initVM() {
        sessionManagerA = SessionManager(requireContext())

        viewModel = ViewModelProvider(
                this,
                ViewModelFactory(ApiHelper(ApiClient.getClientV1WithToken(sessionManagerA)))
        ).get(PostAJobViewModel::class.java)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest {

            }
        }
        viewModel.getBudgets()
        viewModel.response.observe(viewLifecycleOwner) {
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
        viewModel.setIsBudgetSpecific(false)
        viewModel.setBudgetData(data)
        next.isEnabled = true
    }

}