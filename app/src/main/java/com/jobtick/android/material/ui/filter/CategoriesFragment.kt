package com.jobtick.android.material.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jobtick.android.R
import com.jobtick.android.activities.FiltersActivity
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.coroutines.Status
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.scrolview.FastScrollItemIndicator
import com.jobtick.android.utils.scrolview.FastScrollerView
import com.jobtick.android.viewmodel.PostAJobViewModel
import com.jobtick.android.viewmodel.ViewModelFactory

class CategoriesFragment : Fragment() {
    private lateinit var viewModel: PostAJobViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var rlCategories: RecyclerView
    private lateinit var fastScroller: FastScrollerView
    val adapter = CategoriesAdapter()
    private lateinit var addTagListSuggest: ArrayList<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVars()
        initVM()
        initAdapter()
    }

    private fun initAdapter() {
        adapter.itemClick = activity as FiltersActivity
        rlCategories.adapter = adapter
        rlCategories.layoutManager = LinearLayoutManager(requireContext())
    }


    private fun initVars() {
        rlCategories = requireView().findViewById(R.id.rlCategories)
        fastScroller = requireView().findViewById(R.id.fastScroller)
        sessionManager = SessionManager(requireContext())
    }


    private fun initVM() {
        viewModel = ViewModelProvider(
                this,
                ViewModelFactory(ApiHelper(ApiClient.getClientV1WithToken(sessionManager)))
        ).get(PostAJobViewModel::class.java)
        viewModel.getSkills("")
        viewModel.response2.observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        (requireActivity() as FiltersActivity).hideProgressDialog()
                        addTagListSuggest = ArrayList()
                        it.data!!.data.forEach {
                            addTagListSuggest.add(it.title)
                        }
                        adapter.addItems(addTagListSuggest.map { CategoriesAdapter.CategoriesItem(it, false) }.toMutableList())
                        fastScroller.setupWithRecyclerView(rlCategories, { it ->
                            val item = addTagListSuggest[it]
                            FastScrollItemIndicator.Text(
                                    item.substring(0, 1).toUpperCase() // Grab the first letter and capitalize it
                            )
                        })
                    }
                    Status.ERROR -> {
                        (requireActivity() as FiltersActivity).hideProgressDialog()
                    }
                    Status.LOADING -> {
                        (requireActivity() as FiltersActivity).showProgressDialog()
                    }
                }
            }
        }
    }

}