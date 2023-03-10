package com.jobtick.android.material.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
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
import java.util.*
import kotlin.collections.ArrayList

class CategoriesFragment : Fragment() {
    private lateinit var viewModel: PostAJobViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var rlCategories: RecyclerView
    private lateinit var fastScroller: FastScrollerView
    private lateinit var btnNext: MaterialButton
    private val adapter = CategoriesAdapter()
    private lateinit var addTagListSuggest: ArrayList<String>
    private lateinit var categoriesItemList: ArrayList<CategoriesAdapter.CategoriesItem>
    private var search: TextInputEditText? = null

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
        search?.doOnTextChanged { text, start, before, count ->
            val filterList = categoriesItemList.filter { it.title.contains(text.toString()) }
            adapter.addAndClearItems(filterList as MutableList<CategoriesAdapter.CategoriesItem>)
        }
    }


    private fun initVars() {
        rlCategories = requireView().findViewById(R.id.rlCategories)
        fastScroller = requireView().findViewById(R.id.fastScroller)
        btnNext = requireView().findViewById(R.id.btn_next)
        sessionManager = SessionManager(requireContext())
        search = requireView().findViewById(R.id.search)
        btnNext.setOnClickListener {
            (activity as FiltersActivity).hideFragment()
        }
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
                        categoriesItemList = ArrayList()
                        it.data!!.data.forEach {
                            addTagListSuggest.add(it.title)
                        }
                        categoriesItemList = addTagListSuggest.map { CategoriesAdapter.CategoriesItem(it, false) }.toMutableList() as ArrayList<CategoriesAdapter.CategoriesItem>
                        adapter.addItems(addTagListSuggest.map { CategoriesAdapter.CategoriesItem(it, false) }.toMutableList())
                        fastScroller.setupWithRecyclerView(rlCategories, { it ->
                            val item = addTagListSuggest[it]
                            FastScrollItemIndicator.Text(
                                item.substring(0, 1).uppercase(Locale.ROOT) // Grab the first letter and capitalize it
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