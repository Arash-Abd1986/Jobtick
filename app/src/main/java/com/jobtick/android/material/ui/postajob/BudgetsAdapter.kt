package com.jobtick.android.material.ui.postajob

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.jobtick.android.R
import com.jobtick.android.adapers.BaseViewHolder
import com.jobtick.android.models.response.searchsuburb.Feature
import com.jobtick.android.network.model.response.DataX
import java.util.logging.Handler

class BudgetsAdapter : RecyclerView.Adapter<BaseViewHolder>() {
    private var items: ArrayList<DataX> = ArrayList()
    lateinit var budgetClickListener: BudgetClickListener

    interface SubClickListener {
        fun clickOnSearchedLoc(location: Feature)
    }

    interface DismissListener {
        fun dismiss()
    }

    fun getItems(): List<DataX> {
        return items
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: ArrayList<DataX>) {
        this.items = items.filter { it.id != 0 } as ArrayList<DataX>
        notifyDataSetChanged()
    }

    inner class OriginalViewHolder(v: View) : BaseViewHolder(v) {
        var budget: MaterialButton
        override fun clear() {}

        @SuppressLint("SetTextI18n")
        override fun onBind(position: Int) {
            super.onBind(position)
            budget.text = """${"$"}${items[position].min_budget} to ${"$"}${items[position].max_budget}"""
            budget.isChecked = items[position].isChecked

            budget.setOnClickListener {
                budgetClickListener.onBudgetClick(items[position])
                items.filter { it != items[position] }.forEach { it.isChecked = false }
                items.filter { it == items[position] }.forEach { it.isChecked = true }
                notifyDataSetChanged()
            }
        }

        init {
            budget = v.findViewById(R.id.budget)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return OriginalViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_budget, parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(mItems: MutableList<DataX>) {
        items.addAll(mItems)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        items = ArrayList()
        notifyDataSetChanged()
    }

    fun getItem(position: Int): DataX {
        return items[position]
    }

    @SuppressLint("NotifyDataSetChanged")
    fun unselectAll() {
        items.forEach { it.isChecked = false }
        notifyDataSetChanged()
    }

    interface BudgetClickListener {
        fun onBudgetClick(data: DataX)
    }
}