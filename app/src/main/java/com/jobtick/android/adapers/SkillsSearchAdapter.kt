package com.jobtick.android.adapers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jobtick.android.R
import com.jobtick.android.models.response.allSkills.Skills
import kotlin.collections.ArrayList

class SkillsSearchAdapter : RecyclerView.Adapter<BaseViewHolder>() {
    private var items: ArrayList<Skills> = ArrayList()
    lateinit var subClickListener: SubClickListener
  //  lateinit var dismissListener: DismissListener

    interface SubClickListener {
        fun clickOnSearchedLoc(location: Skills)
    }
    interface DismissListener {
        fun dismiss()
    }

    fun getItems(): List<Skills> {
        return items
    }

    fun setItems(items: ArrayList<Skills>) {
        this.items = items
    }

    inner class OriginalViewHolder(v: View) : BaseViewHolder(v) {
        var txtName: TextView
        var lytOuter: RelativeLayout
        var tick: ImageView
        var separator: View
        override fun clear() {}
        override fun onBind(position: Int) {
            super.onBind(position)
            if(position == items.size - 1)
                separator.visibility = View.GONE
            txtName.text = items[position].title!!
            lytOuter.setOnClickListener(null)
            if(items[position].isTicked)
                tick.visibility = View.VISIBLE
            else
                tick.visibility = View.GONE
            lytOuter.setOnClickListener { v: View? ->
                val item = items[position]
                item.isTicked = !item.isTicked
                subClickListener.clickOnSearchedLoc(item)
                if(item.isTicked)
                    tick.visibility = View.VISIBLE
                else
                    tick.visibility = View.GONE

              //  dismissListener.dismiss()
            }
        }

        init {
            txtName = v.findViewById(R.id.txt_title)
            lytOuter = v.findViewById(R.id.parent)
            tick = v.findViewById(R.id.checked)
            separator = v.findViewById(R.id.separator)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return OriginalViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.adapter_all_skill_list, parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addItems(mItems: MutableList<Skills>) {
        items.addAll(mItems)
        notifyDataSetChanged()
    }

    fun clear() {
        items = ArrayList()
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Skills {
        return items[position]
    }
}