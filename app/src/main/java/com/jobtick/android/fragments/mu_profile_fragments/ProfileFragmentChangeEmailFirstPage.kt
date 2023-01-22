package com.jobtick.android.fragments.mu_profile_fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.jobtick.android.R
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.databinding.FragmentProfileChangeEmailFirstPageBinding
import com.jobtick.android.utils.KeyboardUtil
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.SetToolbar

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragmentChangeEmailFirstPage : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var activity: DashboardActivity
    private lateinit var sessionManager: SessionManager
    private var _binding: FragmentProfileChangeEmailFirstPageBinding? = null
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


        if(requireArguments().getString("number") == "1") {
            SetToolbar(activity, "Change Mobile Number", "Verify", R.id.navigation_profile_account, binding.header, view)
                binding.edittextFirstname.hint = "Mobile Number"
        }
        else if(requireArguments().getString("email") == "1") {
            SetToolbar(activity, "Change Email", "Verify", R.id.navigation_profile_account, binding.header, view)
             binding.edittextFirstname.hint = "Email Address"
        }

        binding.edittextFirstnameValue.setOnFocusChangeListener{view, b ->
            if(requireArguments().getString("number") == "1") {
                if (b)
                    binding.edittextFirstnameValue.hint = "+61 4XX XXX XXX"
                else
                    binding.edittextFirstnameValue.hint = ""
            }

        }


        binding.header.back.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_change_email_first_page_to_navigation_profile_account)
        }
        sessionManager
        binding.header.txtAction.setOnClickListener{
            binding.edittextFirstname.error = null
            KeyboardUtil.hideKeyboard(activity)
            var bundle: Bundle? = null
            val editTextList = listOf(binding.edittextFirstnameValue)
            if(validate(editTextList)) {
            if(requireArguments().getString("email")?.isNotEmpty() == true) {
                bundle = bundleOf("email" to binding.edittextFirstnameValue.text.toString())
            }
            else if(requireArguments().getString("number")?.isNotEmpty() == true)
                bundle = bundleOf("number" to binding.edittextFirstnameValue.text.toString())
            view.findNavController().navigate(R.id.action_navigation_profile_change_email_first_page_to_navigation_profile_change_email_second_page, bundle)
            }
            else
                binding.edittextFirstname.error = "Pleases fill in the blanks!"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileChangeEmailFirstPageBinding.inflate(inflater, container, false)
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

    private fun validate(view: List<TextInputEditText>): Boolean {
        for (t in view)
            if(t.text?.trim()!!.isEmpty())
                return false
        return true
    }
}