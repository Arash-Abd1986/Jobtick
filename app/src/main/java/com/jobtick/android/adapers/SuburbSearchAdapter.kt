package com.jobtick.android.adapers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jobtick.android.R
import com.jobtick.android.models.response.searchsuburb.Feature
import com.jobtick.android.utils.getShortAddress
import com.jobtick.android.utils.getState
import java.util.*
import kotlin.collections.ArrayList

class SuburbSearchAdapter : RecyclerView.Adapter<BaseViewHolder>() {
    private var items: ArrayList<Feature> = ArrayList()
    lateinit var subClickListener: SubClickListener
    lateinit var dismissListener: DismissListener

    interface SubClickListener {
        fun clickOnSearchedLoc(location: Feature)
    }
    interface DismissListener {
        fun dismiss()
    }

    fun getItems(): List<Feature> {
        return items
    }

    fun setItems(items: ArrayList<Feature>) {
        this.items = items
    }

    inner class OriginalViewHolder(v: View) : BaseViewHolder(v) {
        var txtName: TextView
        var apply: ImageView
        var lytOuter: RelativeLayout
        override fun clear() {}
        override fun onBind(position: Int) {
            super.onBind(position)
            txtName.text = items[position].place_name_en
            lytOuter.setOnClickListener { v: View? ->
                val item = items[position]
                item.place_name_en = item.place_name_en!!.getShortAddress()
                item.state = item.place_name_en!!.getState()
                subClickListener.clickOnSearchedLoc(item)
                dismissListener.dismiss()
            }
            apply.setOnClickListener { v: View? ->
                val item = items[position]
                item.place_name_en = item.place_name_en!!.getShortAddress()
                item.state = item.place_name_en!!.getState()
                subClickListener.clickOnSearchedLoc(item)
                dismissListener.dismiss()
            }
        }

        init {
            txtName = v.findViewById(R.id.category_text)
            lytOuter = v.findViewById(R.id.category_container)
            apply = v.findViewById(R.id.apply)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return OriginalViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_suburb_search, parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addItems(mItems: MutableList<Feature>) {
        items.addAll(mItems)
        notifyDataSetChanged()
    }

    fun clear() {
        items = ArrayList()
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Feature {
        return items[position]
    }
}