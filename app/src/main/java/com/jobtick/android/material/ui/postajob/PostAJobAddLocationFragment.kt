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
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.jobtick.android.R
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.viewmodel.PostAJobViewModel
import com.jobtick.android.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PostAJobAddLocationFragment : Fragment() {


    private lateinit var activity: PostAJobActivity
    private lateinit var next: MaterialButton
    private lateinit var inPerson: MaterialButton
    private lateinit var remote: MaterialButton
    private lateinit var suburb: TextInputLayout
    private lateinit var sessionManagerA: SessionManager
    private lateinit var viewModel: PostAJobViewModel


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_a_job_add_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVars()
        initVM()
    }

    private fun initVM() {
        sessionManagerA = SessionManager(requireContext())

        viewModel = ViewModelProvider(
                requireActivity(),
                ViewModelFactory(ApiHelper(ApiClient.getClientV2(sessionManagerA)))
        ).get(PostAJobViewModel::class.java)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest {
                if (it.location.isNotEmpty()) {
                    suburb.editText?.setText(it.location)
                    inPerson.isChecked = true
                    suburb.visibility = View.VISIBLE
                }
            }
        }

    }

    private fun initVars() {

        activity = (requireActivity() as PostAJobActivity)
        next = requireView().findViewById(R.id.btn_next)
        inPerson = requireView().findViewById(R.id.btnInperson)
        remote = requireView().findViewById(R.id.btnRemote)
        suburb = requireView().findViewById(R.id.suburb)
        next.setOnClickListener {
            resetError()
            if (checkValidation()) {
                activity.navController.navigate(R.id.postAJobAddLocationFragment)
            }
        }
        suburb.editText?.doOnTextChanged { text, _, _, _ ->
            next.isEnabled = text?.length != null && text.length > 3
        }
        suburb.editText?.setOnFocusChangeListener { _, b ->
            if (b) {
                activity.navController.navigate(R.id.getLocationFragment)
            } else {

            }
        }
        suburb.setOnClickListener {
            activity.navController.navigate(R.id.getLocationFragment)
        }
        inPerson.setOnClickListener {
            inPerson.isChecked = true
            remote.isChecked = false
            suburb.visibility = View.VISIBLE
        }
        remote.setOnClickListener {
            remote.isChecked = true
            inPerson.isChecked = false
            suburb.visibility = View.GONE
        }
    }


    private fun resetError() {
        suburb.isErrorEnabled = false
        suburb.error = ""
    }


    private fun checkValidation(): Boolean {
        when {

            suburb.editText?.text.isNullOrEmpty() -> {
                setError("Please enter your job title", suburb)
                return false
            }
        }
        return true
    }

    private fun setError(error: String, txtInput: TextInputLayout) {
        suburb.isErrorEnabled = true
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