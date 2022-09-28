package com.jobtick.android.material.ui.filter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.jobtick.android.R
import com.jobtick.android.adapers.BaseViewHolder
import com.jobtick.android.network.model.response.DataX
import com.jobtick.android.utils.gone
import com.jobtick.android.utils.invisible
import com.jobtick.android.utils.visible

class CategoriesAdapter : RecyclerView.Adapter<BaseViewHolder>() {
    private var items: ArrayList<CategoriesItem> = ArrayList()
    lateinit var itemClick: ItemClick
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false))
    }

    inner class MyViewHolder(v: View) : BaseViewHolder(v) {
        override fun clear() {}
        var title = v.findViewById<MaterialTextView>(R.id.title)
        var letter = v.findViewById<MaterialTextView>(R.id.letter)
        var checkBox = v.findViewById<ImageView>(R.id.checkBox)
        override fun onBind(position: Int) {
            super.onBind(position)
            title.text = items[position].title
            val heading = items[position].title.substring(0, 1).toUpperCase() // Grab the first letter and capitalize it
            if (position == 0) {
                letter.visible()
                letter.text = heading
            } else
                if (items[position - 1].title.substring(0, 1).toUpperCase() == heading)
                    letter.gone()
                else {
                    letter.visible()
                    letter.text = heading
                }
            if (items[position].isChecked)
                checkBox.visible()
            else
                checkBox.gone()
            title.setOnClickListener {
                val item = items[position]
                item.isChecked = !item.isChecked
                items[position] = item
                notifyItemChanged(position)
                itemClick.onItemClick(items)
            }

        }

    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(mItems: MutableList<CategoriesItem>) {
        items.addAll(mItems)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getItems(): MutableList<CategoriesItem> {
        return items
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAndClearItems(mItems: MutableList<CategoriesItem>) {
        items.clear()
        items.addAll(mItems)
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return items.size
    }

    data class CategoriesItem(var title: String, var isChecked: Boolean)

    interface ItemClick {
        fun onItemClick(items: MutableList<CategoriesItem>)
    }
}