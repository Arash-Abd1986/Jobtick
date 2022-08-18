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
import com.google.android.material.textview.MaterialTextView
import com.jobtick.android.R
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.getShortAddress
import com.jobtick.android.viewmodel.PostAJobViewModel
import com.jobtick.android.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PostAJobAddLocationFragment : Fragment() {


    private lateinit var activity: PostAJobActivity
    private lateinit var next: MaterialButton
    private lateinit var inPerson: MaterialButton
    private lateinit var remote: MaterialButton
    private lateinit var suburb: MaterialTextView
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
                if (it.location != null) {
                    suburb.text = it.location!!.place_name_en!!.getShortAddress()
                    suburb.setTextColor(ContextCompat.getColor(requireContext(), R.color.neutral_light_700))
                    suburb.setBackgroundResource(R.drawable.back_white_dark_gray_corner_4)
                    inPerson.isChecked = true
                    suburb.visibility = View.VISIBLE
                }else{
                    if (it.isRemote) {
                        remote.isChecked = true
                        next.isEnabled = true
                    }
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (inPerson.isChecked){
            suburb.visibility = View.VISIBLE
        }
    }

    private fun initVars() {

        activity = (requireActivity() as PostAJobActivity)
        next = requireView().findViewById(R.id.btn_next)
        inPerson = requireView().findViewById(R.id.btnInperson)
        remote = requireView().findViewById(R.id.btnRemote)
        suburb = requireView().findViewById(R.id.suburb)
        next.setOnClickListener {
            if (checkValidation()) {
                viewModel.setIsRemote(remote.isChecked)
                activity.navController.navigate(R.id.postAJobDateTimeFragment)
            }
        }
        suburb.doOnTextChanged { text, _, _, _ ->
            next.isEnabled = text?.length != null && text.length > 3
        }
        suburb.setOnClickListener {
            activity.navController.navigate(R.id.getLocationFragment)
        }
        inPerson.setOnClickListener {
            inPerson.isChecked = true
            remote.isChecked = false
            suburb.visibility = View.VISIBLE
            next.isEnabled = suburb.text?.length != null && suburb.text != "Suburb or Postcode" && suburb.text.length> 3
        }
        remote.setOnClickListener {
            remote.isChecked = true
            inPerson.isChecked = false
            suburb.visibility = View.GONE
            next.isEnabled = true
        }
    }


    private fun checkValidation(): Boolean {
        when {
            suburb.text.isNullOrEmpty() -> {
                return false
            }
        }
        return true
    }


}