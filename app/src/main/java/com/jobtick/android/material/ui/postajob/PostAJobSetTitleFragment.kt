package com.jobtick.android.material.ui.postajob

import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
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

class PostAJobSetTitleFragment : Fragment() {

    private lateinit var activity: PostAJobActivity
    private lateinit var next: MaterialButton
    private lateinit var jobTitle: TextInputLayout
    private lateinit var infoIcon: AppCompatImageView
    private lateinit var label: MaterialTextView
    private lateinit var sessionManagerA: SessionManager
    private lateinit var viewModel: PostAJobViewModel


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_a_job_set_title, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVars()
    }


    private fun initVars() {
        sessionManagerA = SessionManager(requireContext())
        viewModel = ViewModelProvider(
                requireActivity(),
                ViewModelFactory(ApiHelper(ApiClient.getClientV2(sessionManagerA)))
        ).get(PostAJobViewModel::class.java)
        activity = (requireActivity() as PostAJobActivity)
        next = requireView().findViewById(R.id.btn_next)
        jobTitle = requireView().findViewById(R.id.job_title)
        infoIcon = requireView().findViewById(R.id.info)
        label = requireView().findViewById(R.id.label)
        next.setOnClickListener {
            resetError()
            if (checkValidation()) {
                viewModel.setTitle(jobTitle.editText!!.text.toString())
                activity.navController.navigate(R.id.postAJobAddLocationFragment)
                //activity.navController.navigate(R.id.postAJobAttachmentFragment)
            }
        }
        jobTitle.editText?.doOnTextChanged { text, _, _, _ ->
            next.isEnabled = text?.length != null && text.length > 25
        }
        jobTitle.editText?.setOnFocusChangeListener { _, b ->
            if (b) {
                infoIcon.setColorFilter(resources.getColor(R.color.primary_500), android.graphics.PorterDuff.Mode.SRC_IN)
                label.setTextColor(resources.getColor(R.color.neutral_light_600))
            } else {
                infoIcon.setColorFilter(resources.getColor(R.color.neutral_light_400), android.graphics.PorterDuff.Mode.SRC_IN)
                label.setTextColor(resources.getColor(R.color.neutral_light_400))
            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest {
                jobTitle.editText!!.setText(it.title)
            }

        }
    }


    private fun resetError() {
        jobTitle.isErrorEnabled = false
        jobTitle.error = ""
    }


    private fun checkValidation(): Boolean {
        when {

            jobTitle.editText?.text.isNullOrEmpty() -> {
                setError("Please enter your job title", jobTitle)
                return false
            }
        }
        return true
    }

    private fun setError(error: String, txtInput: TextInputLayout) {
        jobTitle.isErrorEnabled = true
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