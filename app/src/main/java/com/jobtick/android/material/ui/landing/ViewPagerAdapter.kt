package com.jobtick.android.material.ui.landing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.jobtick.android.R

class ViewPagerAdapter(private val data: Array<String>) :
    RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>() {
    lateinit var itemClick: ItemClick
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        return when (viewType) {
            0 -> PagerViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.onboarding_page1, parent, false)
            )
            1 -> PagerViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.onboarding_page2, parent, false)
            )
            2 -> PagerViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.onboarding_page3, parent, false)
            )
            else -> PagerViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.onboarding_page3, parent, false)
            )
        }

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        fun bind(data: String) {

        }
    }

    interface ItemClick {
        fun buttonClick(type: ItemType)
    }

    enum class ItemType {
        NEXT, SKIP
    }
}