package com.jobtick.android.material.ui.filter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.jobtick.android.R
import com.jobtick.android.adapers.BaseViewHolder
import com.jobtick.android.network.model.response.DataX
import com.jobtick.android.utils.gone
import com.jobtick.android.utils.invisible
import com.jobtick.android.utils.visible

class CategoriesAdapter : RecyclerView.Adapter<BaseViewHolder>() {
    private var items: ArrayList<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false))
    }

    inner class MyViewHolder(v: View) : BaseViewHolder(v) {
        override fun clear() {}
        var title = v.findViewById<MaterialTextView>(R.id.title)
        var letter = v.findViewById<MaterialTextView>(R.id.letter)
        override fun onBind(position: Int) {
            super.onBind(position)
            title.text = items[position]
            val heading = items[position].substring(0, 1).toUpperCase() // Grab the first letter and capitalize it
            if (position == 0) {
                letter.visible()
                letter.text = heading
            } else
                if (items[position - 1].substring(0, 1).toUpperCase() == heading)
                    letter.gone()
                else {
                    letter.visible()
                    letter.text = heading
                }


        }

    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(mItems: MutableList<String>) {
        items.addAll(mItems)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }
}