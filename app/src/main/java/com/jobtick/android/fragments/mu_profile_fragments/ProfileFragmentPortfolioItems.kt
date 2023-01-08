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
import com.jobtick.android.databinding.FragmentProfilePortfolioBinding
import com.jobtick.android.databinding.FragmentProfilePortfolioItemBinding
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.SetToolbar
import com.jobtick.android.utils.isLetter
import com.jobtick.android.viewmodel.ProfileNewViewModel
import com.jobtick.android.viewmodel.ProfileSkillsViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragmentPortfolioItems : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var activity: Activity
    private lateinit var sessionManager: SessionManager
    private var _binding: FragmentProfilePortfolioItemBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileSkillsViewModel
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
        SetToolbar(activity, "Portfolio", "Add Portfolio Item", R.id.navigation_profile_account, binding.header, view)

        viewModel = ViewModelProvider(requireActivity())[ProfileSkillsViewModel::class.java]

        binding.header.txtAction.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_portfolio_item_to_navigation_profile_add_portfolio_item)

        }
        binding.header.back.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_portfolio_to_navigation_profile)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilePortfolioItemBinding.inflate(inflater, container, false)
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