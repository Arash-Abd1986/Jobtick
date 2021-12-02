package com.jobtick.android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.jobtick.android.R
import com.jobtick.android.adapers.LevelsAdapter
import com.jobtick.android.adapers.SuburbSearchAdapter
import com.jobtick.android.network.model.response.LevelsItem

class LevelsBottomSheet(val levels: ArrayList<LevelsItem>, val  lastMonthIncome: Float) : BottomSheetDialogFragment(),SuburbSearchAdapter.DismissListener {
    var recyclerView: RecyclerView? = null
    var input: TextInputEditText? = null
    private var adapter: LevelsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottomsheet_levels, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.rl_levels)


        setCategoryData()
    }

    private fun setCategoryData() {
        val layoutManager = LinearLayoutManager(activity)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.setHasFixedSize(true)
        adapter = LevelsAdapter(lastMonthIncome)
        recyclerView!!.adapter = adapter
        adapter!!.addItems(levels)
    }





}