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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.jobtick.android.R
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.adapers.PortfolioAdapter
import com.jobtick.android.adapers.SkillsSearchAdapter
import com.jobtick.android.databinding.FragmentProfileNameBinding
import com.jobtick.android.databinding.FragmentProfilePortfolioBinding
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

class ProfileFragmentPortfolioItems : Fragment(), PortfolioAdapter.OnItemClickListener {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var activity: Activity
    private lateinit var sessionManager: SessionManager
    private var _binding: FragmentProfilePortfolioItemBinding? = null
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
        SetToolbar(activity, "Portfolio", "Add Portfolio", R.id.navigation_profile_account, binding.header, view)

        binding.portfolioItemsRecycler.layoutManager = LinearLayoutManager(activity)
        portfolioAdapter = PortfolioAdapter(list)
        binding.portfolioItemsRecycler.adapter = portfolioAdapter
        portfolioAdapter!!.setOnItemClickListener(this)

        viewModel = ViewModelProvider(this)[ProfilePortfolioViewModel::class.java]

        viewModel.getPortfolioItems(activity)

        viewModel.jsonobject.observe(viewLifecycleOwner) {
            binding.noData.noDataParent.visibility = View.GONE
            binding.dataParent.visibility = View.VISIBLE
            list = ArrayList()
            jsonArray = viewModel.jsonobject.value!!.getJSONArray("data")
            if(jsonArray.length() == 0) {
                (activity as DashboardActivity).showToast("No data!", activity)
                binding.noData.noDataParent.visibility = View.VISIBLE
                binding.dataParent.visibility = View.GONE
                binding.header.txtAction.visibility = View.GONE
                return@observe
            }
            for (i in 0 until jsonArray.length()) {
                val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                val portfolioDataModel = PortfolioDataModel()
                portfolioDataModel.title = jsonObject.getString("title")
                portfolioDataModel.description = jsonObject.getString("description")
                portfolioDataModel.img_count = jsonObject.getString("img_count")
                list.add(portfolioDataModel)
            }
            portfolioAdapter!!.addItems(list)

        }

        binding.noData.noDataButton.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_portfolio_item_to_navigation_profile_add_portfolio_item)

        }

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

    override fun onItemClick(view: View?, obj: PortfolioDataModel?, position: Int) {
        Log.d("asdadadsa", position.toString())
        val bundle = bundleOf("json" to jsonArray.getJSONObject(position).toString())
        view?.findNavController()?.navigate(R.id.action_navigation_profile_portfolio_item_to_navigation_profile_add_portfolio_item, bundle)
    }


}