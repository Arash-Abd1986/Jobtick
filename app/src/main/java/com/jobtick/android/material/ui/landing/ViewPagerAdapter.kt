package com.jobtick.android.material.ui.landing

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.textview.MaterialTextView
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
        holder.bind(position)
    }

    inner class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<MaterialTextView>(R.id.title)
        val msg = itemView.findViewById<MaterialTextView>(R.id.msg)
        val tickerRB = itemView.findViewById<MaterialRadioButton>(R.id.radio1)
        val posterRB = itemView.findViewById<MaterialRadioButton>(R.id.radio2)
        val tickerPosterRB = itemView.findViewById<MaterialRadioButton>(R.id.radio3)

        fun bind(position: Int) {
            when (position) {
                0 -> {
                    val sb: Spannable =
                        SpannableString(itemView.context.getString(R.string.slide_1_caption))
                    sb.setSpan(
                        ForegroundColorSpan(
                            itemView.context.getColor(R.color.secondary_light)
                        ),
                        25,
                        53,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    msg.text = sb
                }
                1 -> {
                    val sb: Spannable =
                        SpannableString(itemView.context.getString(R.string.create_your_professional_profile_and_start_earning_right_away))
                    sb.setSpan(
                        ForegroundColorSpan(
                            itemView.context.getColor(R.color.secondary_light)
                        ),
                        12,
                        32,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    msg.text = sb
                }
                2 -> {
                    val sb: Spannable =
                        SpannableString(itemView.context.getString(R.string.how_do_you_want_to_use_jobtick))
                    sb.setSpan(
                        ForegroundColorSpan(
                            itemView.context.getColor(R.color.secondary_light)
                        ),
                        19,
                        sb.length -1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    msg.text = sb

                    tickerRB.setOnCheckedChangeListener { compoundButton, b ->
                        if (b) {
                            posterRB.isChecked = false
                            tickerPosterRB.isChecked = false
                        }
                    }
                    posterRB.setOnCheckedChangeListener { compoundButton, b ->
                        if (b) {
                            tickerRB.isChecked = false
                            tickerPosterRB.isChecked = false
                        }
                    }
                    tickerPosterRB.setOnCheckedChangeListener { compoundButton, b ->
                        if (b) {
                            tickerRB.isChecked = false
                            posterRB.isChecked = false
                        }
                    }
                }
            }

        }
    }

    interface ItemClick {
        fun buttonClick(type: Role)
    }

    enum class Role {
        POSTER, TICKER, POSTER_TICKER
    }
}