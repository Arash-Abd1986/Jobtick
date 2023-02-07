package com.jobtick.android.fragments.mu_profile_fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.jobtick.android.R
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.databinding.FragmentProfileAccountBinding
import com.jobtick.android.databinding.FragmentProfilePushNotificationBinding
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.SetToolbar
import com.jobtick.android.viewmodel.ProfileNotificationsViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragmentPushNotifications : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var activity: DashboardActivity
    private lateinit var sessionManager: SessionManager
    private var _binding: FragmentProfilePushNotificationBinding? = null
    private val binding get() = _binding!!
    private var type: String = ""
    private lateinit var viewModel: ProfileNotificationsViewModel
    private var input = mutableMapOf<String, String>()

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

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    prepareDataForSaving()
                    view!!.findNavController().navigate(R.id.action_navigation_profile_push_notification_to_navigation_profile_notifications)

                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(requireArguments().getString("type")?.isNotEmpty() == true)
            requireArguments().getString("type")?.let {
                SetToolbar(activity,
                    it, "", R.id.navigation_profile, binding.header, view)
            }

        viewModel = ViewModelProvider(this)[ProfileNotificationsViewModel::class.java]

        when(requireArguments().getString("type")) {
            "Email Notifications" -> type = "email"
            "Push Notifications" -> type = "push"
            "SMS Notifications" -> type = "sms"
        }

        binding.transactions.isClickable = false

        viewModel.getNotificationSettings(activity, type)

        viewModel.jsonobject.observe(viewLifecycleOwner) {
            binding.jobAlerts.isChecked = viewModel.jsonobject.value?.getString("jobalerts") == "1"

            binding.updateRecommendations.isChecked = viewModel.jsonobject.value?.getString("jobupdates") == "1"


           // binding.transactions.isChecked = viewModel.jsonobject.value?.getString("transactional") == "1"
        }

        binding.header.back.setOnClickListener {
            prepareDataForSaving()
            view.findNavController().navigate(R.id.action_navigation_profile_push_notification_to_navigation_profile_notifications)
        }


        viewModel.success.observe(viewLifecycleOwner) {

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilePushNotificationBinding.inflate(inflater, container, false)
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

    private fun prepareDataForSaving() {
        if(binding.transactions.isChecked)
            input["transactional"] = "1"
        else
            input["transactional"] = "1"

        if(binding.updateRecommendations.isChecked)
            input["recommendations"] = "1"
        else
            input["recommendations"] = "0"

        if(binding.jobAlerts.isChecked)
            input["jobalerts"] = "1"
        else
            input["jobalerts"] = "0"

        viewModel.setNotificationSettings(activity, type, input)
    }
}