package com.jobtick.android.fragments.mu_profile_fragments

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.jobtick.android.R
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.databinding.FragmentProfileNameBinding
import com.jobtick.android.utils.Helper
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.SetToolbar
import com.jobtick.android.utils.isLetter
import com.jobtick.android.viewmodel.ProfileNewViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragmentName : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var activity: Activity
    private lateinit var sessionManager: SessionManager
    private var _binding: FragmentProfileNameBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileNewViewModel
    private lateinit var mutableMap: MutableMap<String, String>

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
        SetToolbar(activity, "Name", "Save", R.id.navigation_profile_account, binding.header, view)

        viewModel = ViewModelProvider(this)[ProfileNewViewModel::class.java]

        binding.edittextFirstnameValue.setText(arguments?.getString("firstName"))
        binding.edittextLastNameValue.setText(arguments?.getString("lastName"))

        binding.header.back.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_name_to_navigation_profile_account)
        }


        binding.header.txtAction.setOnClickListener {
            mutableMap = mutableMapOf("fname" to binding.edittextFirstnameValue.text.toString(),
                "lname" to binding.edittextLastNameValue.text.toString())

            if(checkValidation())
                viewModel.updateProfile(activity, mutableMap)
        }


        viewModel.userAccountModelObservable.observe(viewLifecycleOwner) {
            sessionManager = SessionManager(activity)
            binding.edittextFirstnameValue.setText(sessionManager.userAccount.fname)
            binding.edittextLastNameValue.setText(sessionManager.userAccount.lname)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileNameBinding.inflate(inflater, container, false)
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
    private fun checkValidation(): Boolean {
        resetError()
        when {
            binding.edittextFirstname.editText?.text.isNullOrEmpty() && binding.edittextLastname.editText?.text.isNullOrEmpty() -> {
                Helper.setError(activity, getString(R.string.please_enter_your_first_name), binding.edittextFirstname)
                Helper.setError(activity, getString(R.string.please_enter_your_last_name), binding.edittextLastname)
                return false
            }
            binding.edittextFirstname.editText?.text.isNullOrEmpty() -> {
                Helper.setError(activity, getString(R.string.please_enter_your_first_name), binding.edittextFirstname)

                return false
            }
            !binding.edittextFirstname.editText?.text.toString().isLetter() -> {
                Helper.setError(activity, getString(R.string.your_first_name_should_only), binding.edittextFirstname)
                return false
            }
            binding.edittextLastname.editText?.text.isNullOrEmpty() -> {
                Helper.setError(activity, getString(R.string.please_enter_your_last_name), binding.edittextLastname)

                return false
            }
            !binding.edittextLastname.editText?.text.toString().isLetter() -> {
                Helper.setError(activity, getString(R.string.your_last_name_should_only), binding.edittextLastname)
                return false
            }
        }
        return true
    }
    private fun resetError() {
        binding.edittextFirstname.error = null
        binding.edittextLastname.error = null
    }


}