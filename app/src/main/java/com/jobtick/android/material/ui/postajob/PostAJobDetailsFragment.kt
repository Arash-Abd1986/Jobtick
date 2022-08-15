package com.jobtick.android.material.ui.postajob

import android.annotation.SuppressLint
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
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.jobtick.android.R
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.viewmodel.PostAJobViewModel
import com.jobtick.android.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PostAJobDetailsFragment : Fragment() {

    private lateinit var activity: PostAJobActivity
    private lateinit var next: MaterialButton
    private lateinit var btnAttachments: MaterialButton
    private lateinit var jobDescription: TextInputLayout
    private lateinit var title: MaterialTextView
    private lateinit var dateTime: MaterialTextView
    private lateinit var suburb: MaterialTextView
    private lateinit var budget: MaterialTextView
    private lateinit var attachments: MaterialTextView
    private lateinit var sessionManagerA: SessionManager
    private lateinit var viewModel: PostAJobViewModel


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVars()
    }


    @SuppressLint("SetTextI18n")
    private fun initVars() {
        sessionManagerA = SessionManager(requireContext())
        viewModel = ViewModelProvider(
                requireActivity(),
                ViewModelFactory(ApiHelper(ApiClient.getClientV2(sessionManagerA)))
        ).get(PostAJobViewModel::class.java)
        activity = (requireActivity() as PostAJobActivity)
        next = requireView().findViewById(R.id.btn_next)
        jobDescription = requireView().findViewById(R.id.job_description)
        title = requireView().findViewById(R.id.title)
        dateTime = requireView().findViewById(R.id.dateTime)
        budget = requireView().findViewById(R.id.txtBudget)
        attachments = requireView().findViewById(R.id.attachments)
        btnAttachments = requireView().findViewById(R.id.btnAttachments)
        suburb = requireView().findViewById(R.id.suburb)
        btnAttachments.setOnClickListener {
            activity.navController.navigate(R.id.postAJobAttachmentFragment)
        }

        lifecycleScope.launch {
            viewModel.state.collectLatest {
                title.text = it.title
                if (it.isFlexible)
                    dateTime.text = "Flexible"
                else
                    it.date?.let {
                        dateTime.text = (it.month.toString() + it.day + it.year)
                    }
                if (it.isRemote)
                    suburb.text = "Remote"
                else
                    suburb.text = it.location!!.text_en
                if (it.isBudgetSpecific)
                    budget.text = it.budget
                else
                    it.budgetData?.let {
                        when {
                            it.min_budget == 0 -> {
                                budget.text = """Less than ${"$"}${it.max_budget}"""
                            }
                            it.max_budget == 9999 -> {
                                budget.text = """More than ${"$"}${it.min_budget}"""
                            }
                            else -> {
                                budget.text = """${"$"}${it.min_budget} to ${"$"}${it.max_budget}"""
                            }
                        }
                    }
                if (it.attachments.isEmpty())
                    attachments.text = "-"
                else if (it.attachments.size - 1 == 1)
                    attachments.text = (it.attachments.size - 1).toString() + " file"
                else
                    attachments.text = (it.attachments.size - 1).toString() + " files"
            }
        }
        next.setOnClickListener {
            resetError()
            if (checkValidation()) {
                viewModel.setDescription(jobDescription.editText!!.text.toString())
                activity.postJob()
            }
        }
        jobDescription.editText!!.setOnFocusChangeListener { view, b ->
            if (b) {
                jobDescription.hint = "Details"
            } else {
                if (jobDescription.editText!!.text.toString().isNotEmpty())
                    jobDescription.hint = "Write a brief description of the key details of your job"
                else
                    jobDescription.hint = "Details"
            }
        }
        jobDescription.editText?.doOnTextChanged { text, _, _, _ ->
            next.isEnabled = text?.length != null && text.length > 3
        }
    }


    private fun resetError() {
        jobDescription.isErrorEnabled = false
        jobDescription.error = ""
    }


    private fun checkValidation(): Boolean {
        when {

            jobDescription.editText?.text.isNullOrEmpty() -> {
                setError("Please enter your job title", jobDescription)
                return false
            }
        }
        return true
    }

    private fun setError(error: String, txtInput: TextInputLayout) {
        jobDescription.isErrorEnabled = true
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

}