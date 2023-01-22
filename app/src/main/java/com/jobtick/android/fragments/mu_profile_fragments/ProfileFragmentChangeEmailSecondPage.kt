package com.jobtick.android.fragments.mu_profile_fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.jobtick.android.R
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.databinding.FragmentProfileAccountBinding
import com.jobtick.android.databinding.FragmentProfileChangeEmailSecondPageBinding
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.SetToolbar

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragmentChangeEmailSecondPage : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var activity: DashboardActivity
    private lateinit var sessionManager: SessionManager
    private var _binding: FragmentProfileChangeEmailSecondPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("hereinattach2", "")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = (requireActivity() as DashboardActivity)
        sessionManager = SessionManager(context)
        Log.d("hereinattach", "")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SetToolbar(activity, "", "", null, binding.header, view)

        if(requireArguments().getString("email")?.isNotEmpty() == true) {
            binding.txtNameTitle.text = "Email Address"
            binding.txtNameTitle1.text = "Email Address"
            binding.text.text = requireArguments().getString("email")
            binding.image.setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.new_design_alternate_email))

        }
        else {
            binding.txtNameTitle.text = "Mobile Number"
            binding.txtNameTitle1.text = "Mobile Number"
            binding.text.text = requireArguments().getString("number")
            binding.image.setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.new_design_smartphone))
        }
        binding.btnCancel.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_change_email_second_page_to_navigation_profile_account)
        }

        binding.btnNext.setOnClickListener{
            if(requireArguments().getString("email")?.isNotEmpty() == true) {
                val bundle: Bundle = bundleOf("email" to binding.text.text.toString())
                view.findNavController().navigate(
                    R.id.action_navigation_profile_change_email_second_page_to_navigation_profile_otp_verification,
                    bundle
                )
            } else {
                val bundle: Bundle = bundleOf("number" to binding.text.text.toString())
                view.findNavController().navigate(
                    R.id.action_navigation_profile_change_email_second_page_to_navigation_profile_otp_verification_mobile,
                    bundle
                )
            }
        }


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileChangeEmailSecondPageBinding.inflate(inflater, container, false)
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
}