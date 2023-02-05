package com.jobtick.android.fragments.mu_profile_fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.button.MaterialButton
import com.jobtick.android.R
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.databinding.FragmentProfileDeleteAccountBinding
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.SetToolbar
import com.jobtick.android.viewmodel.ProfileNewViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragmentDeleteAccount : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var activity: DashboardActivity
    private lateinit var sessionManager: SessionManager
    private var _binding: FragmentProfileDeleteAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileNewViewModel

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
        SetToolbar(activity, "Delete Account", "", R.id.navigation_profile, binding.header, view)

        viewModel = ViewModelProvider(this)[ProfileNewViewModel::class.java]

        viewModel.success.observe(viewLifecycleOwner) {
            sessionManager.userAccount = null
            sessionManager.login = false
            sessionManager.tokenType = null
            sessionManager.accessToken = null
            val intent = Intent(context, DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
        binding.deleteAccount.setOnClickListener {
            showDialog()
        }

        binding.header.back.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_delete_account_to_navigation_profile_account)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileDeleteAccountBinding.inflate(inflater, container, false)
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
    fun showDialog() {
        val cancel: MaterialButton?
        val delete: MaterialButton?
        val title: TextView?
        val mainTitle: TextView?
        val dialog = Dialog(activity, R.style.AnimatedDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        dialog.setContentView(R.layout.dialog_discard_changes_new)

        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog.window!!.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        cancel = dialog.findViewById(R.id.cancel)
        delete = dialog.findViewById(R.id.discard)
        title = dialog.findViewById(R.id.title)
        mainTitle = dialog.findViewById(R.id.mainTitle)

        delete.text = getString(R.string.delete)
        cancel.text = getString(R.string.cancel)
        mainTitle.text = getString(R.string.profile_delete_account)

        title.setText(activity.getString(R.string.profile_delete_account_warning))

        cancel.setOnClickListener {
            dialog.cancel()
        }
        delete.setOnClickListener {
            //viewModel.deleteAccount(activity)
            dialog.cancel()
        }

        dialog.show()

    }


}