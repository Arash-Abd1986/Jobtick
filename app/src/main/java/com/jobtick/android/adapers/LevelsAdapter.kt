package com.jobtick.android.adapers

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jobtick.android.R
import com.jobtick.android.network.model.response.levelsItem
import kotlin.math.roundToInt

class LevelsAdapter : RecyclerView.Adapter<BaseViewHolder>() {
    private var items: ArrayList<levelsItem> = ArrayList()
    lateinit var dismissListener: DismissListener


    interface DismissListener {
        fun dismiss()
    }

    fun getItems(): List<levelsItem> {
        return items
    }

    fun setItems(items: ArrayList<levelsItem>) {
        this.items = items
    }

    inner class OriginalViewHolder(v: View) : BaseViewHolder(v) {
        var txtName: TextView
        var description: TextView
        var info: TextView
        override fun clear() {}

        @SuppressLint("SetTextI18n")
        override fun onBind(position: Int) {
            super.onBind(position)
            val item = items[position]
            txtName.text = "Level " + (position + 1)
            info.text = """${item.service_fee.toFloat().roundToInt()}% service fee"""
            if (position == 0)
                description.text =
                    """Less than ${"$"}${item.threshold_amount.toFloat().roundToInt()} in the last 30 days"""
            else
                description.text =
                    """${"$"}${item.threshold_amount.toFloat().roundToInt()}+ in the last 30 days"""

        }

        init {
            txtName = v.findViewById(R.id.txt_level_title)
            description = v.findViewById(R.id.txt_description)
            info = v.findViewById(R.id.txt_info)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return OriginalViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_level, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addItems(mItems: MutableList<levelsItem>) {
        items.addAll(mItems)
        notifyDataSetChanged()
    }

    fun clear() {
        items = ArrayList()
        notifyDataSetChanged()
    }

    fun getItem(position: Int): levelsItem {
        return items[position]
    }
}