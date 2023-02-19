package com.jobtick.android.fragments.mu_profile_fragments

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.jobtick.android.R
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.adapers.PortfolioAdapter
import com.jobtick.android.adapers.SkillsSearchAdapter
import com.jobtick.android.databinding.FragmentProfileNameBinding
import com.jobtick.android.databinding.FragmentProfilePortfolioBinding
import com.jobtick.android.databinding.FragmentProfilePortfolioImagePreviewBinding
import com.jobtick.android.databinding.FragmentProfilePortfolioItemBinding
import com.jobtick.android.models.response.PortfolioDataModel
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.SetToolbar
import com.jobtick.android.utils.isLetter
import com.jobtick.android.viewmodel.ProfileNewViewModel
import com.jobtick.android.viewmodel.ProfilePortfolioViewModel
import com.jobtick.android.viewmodel.ProfileSkillsViewModel
import org.json.JSONArray
import org.json.JSONObject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragmentPortfolioImagePreview : Fragment(), PortfolioAdapter.OnItemClickListener {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var activity: Activity
    private lateinit var sessionManager: SessionManager
    private var _binding: FragmentProfilePortfolioImagePreviewBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfilePortfolioViewModel
    private var list =  ArrayList<PortfolioDataModel>()
    private var portfolioAdapter: PortfolioAdapter? = null
    private lateinit var jsonArray: JSONArray

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
        SetToolbar(activity, "View Image", "", R.id.navigation_profile_account, binding.header, view)
        try {
            if (requireArguments().get("image") != null) {
                Glide.with(binding.image).load(requireArguments().get("image")).into(binding.image)
            }
        }catch (e: Exception) {
            (activity as DashboardActivity).showToast(getString(R.string.server_went_wrong), activity)
            view.findNavController().navigate(R.id.action_navigation_profile_portfolio_image_preview_to_navigation_profile_portfolio_item)
        }

        binding.header.back.setOnClickListener {
            view.findNavController().popBackStack()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilePortfolioImagePreviewBinding.inflate(inflater, container, false)
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

    override fun onItemClick(view: View?, obj: PortfolioDataModel?, position: Int) {
        Log.d("asdadadsa", position.toString())
        val bundle = bundleOf("json" to jsonArray.getJSONObject(position).toString())
        view?.findNavController()?.navigate(R.id.action_navigation_profile_portfolio_item_to_navigation_profile_add_portfolio_item, bundle)
    }


}