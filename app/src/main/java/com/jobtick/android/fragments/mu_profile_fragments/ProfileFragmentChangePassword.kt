package com.jobtick.android.fragments.mu_profile_fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.jobtick.android.R
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.databinding.FragmentProfileAccountBinding
import com.jobtick.android.databinding.FragmentProfileChangePasswordBinding
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.SetToolbar
import com.jobtick.android.viewmodel.ChangePassViewModel
import com.jobtick.android.viewmodel.ProfileChangePasswordViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragmentChangePassword : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var activity: DashboardActivity
    private lateinit var sessionManager: SessionManager
    private var changePassViewmodel: ProfileChangePasswordViewModel? = null
    private lateinit var mutableMap: MutableMap<String, String>
    private var _binding: FragmentProfileChangePasswordBinding? = null
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
        SetToolbar(activity, "Change Password", "Save", R.id.navigation_profile, binding.header, view)
        changePassViewmodel = ViewModelProvider(this)[ProfileChangePasswordViewModel::class.java]

        binding.header.back.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_change_password_to_navigation_profile)
        }

        binding.header.txtAction.setOnClickListener {
            if(checkValidation()) {
                mutableMap = mutableMapOf("old_password" to binding.edittextCurrentPassword.toString(),
                    "new_password" to binding.edittextNewPassword.toString())
                changePassViewmodel?.changePass(activity,mutableMap)
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileChangePasswordBinding.inflate(inflater, container, false)
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
        when {
            binding.edittextCurrentPassword.editText?.text.isNullOrEmpty() -> {
                binding.edittextCurrentPassword.setError("Please enter your Current Password")
                return false
            }
            binding.edittextNewPassword.editText?.text.isNullOrEmpty() -> {
                binding.edittextNewPassword.setError("Please enter your New Password")
                return false
            }
            binding.edittextCurrentPassword.editText?.text.isNullOrEmpty() -> {
                binding.edittextCurrentPassword.setError("Please enter New Password Confirmation")
                return false
            }
            !binding.edittextNewPassword.editText?.text?.trim().toString().equals(
                binding.edittextConfirmPassword.editText?.text?.trim().toString()
            ) -> {
                activity.showToast("new password and confirm password are not the same!", activity)
                return false
            }


        }
        return true
    }

}