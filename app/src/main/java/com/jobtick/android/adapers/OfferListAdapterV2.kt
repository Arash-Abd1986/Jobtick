package com.jobtick.android.adapers

import android.annotation.SuppressLint
import android.content.Context
import android.text.Spannable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jobtick.android.R
import com.jobtick.android.models.OfferModel
import com.jobtick.android.utils.ImageUtil
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.cleanRound
import com.jobtick.android.utils.removeClearRound
import com.mikhaellopez.circularimageview.CircularImageView

class OfferListAdapterV2(
        private val context: Context,
        private val isMyTask: Boolean,
        private val mItems: MutableList<OfferModel>?
) : RecyclerView.Adapter<BaseViewHolder>() {
    var sessionManager: SessionManager
    var spanS: Spannable? = null
    var spanF: Spannable? = null
    private var mOnItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemOfferClick(
                obj: OfferModel?,
                action: String?
        ) //  void onItemClick(View view, OfferChatModel obj, int position, String action);
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        mOnItemClickListener = mItemClickListener
    }

    private var isLoaderVisible = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_offer_v3, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == mItems!!.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    override fun getItemCount(): Int {
        return mItems?.size ?: 0
    }

    fun addItems(mItems: List<OfferModel>?) {
        this.mItems!!.addAll(mItems!!)
        notifyDataSetChanged()
    }

    fun addLoading() {
        isLoaderVisible = true
        mItems!!.add(OfferModel())
        notifyItemInserted(mItems.size - 1)
    }

    fun removeLoading() {
        isLoaderVisible = false
        val position = mItems!!.size - 1
        val item = getItem(position)
        if (item != null) {
            mItems.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        mItems!!.clear()
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): OfferModel {
        return mItems!![position]
    }

    inner class ViewHolder internal constructor(itemView: View?) : BaseViewHolder(itemView) {
        var imgAvatar: CircularImageView = itemView!!.findViewById(R.id.img_avatar)
        var txtName: TextView = itemView!!.findViewById(R.id.txt_name)
        var txtCompletionRate: TextView = itemView!!.findViewById(R.id.txt_completion_rate)
        var txtJobSuccess: TextView = itemView!!.findViewById(R.id.txt_job_success)
        var txtBudget: TextView = itemView!!.findViewById(R.id.txt_budget)
        var starRatingBar: RatingBar = itemView!!.findViewById(R.id.ratingbar_worker)
        var txtCreatedDate: TextView = itemView!!.findViewById(R.id.txt_created_date)
        var txt_rank: TextView = itemView!!.findViewById(R.id.txt_rank)

        override fun clear() {}

        @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
        override fun onBind(position: Int) {
            super.onBind(position)
            val item = mItems!![position]
            if (item.worker.avatar != null) {
                ImageUtil.displayImage(imgAvatar, item.worker.avatar.thumbUrl, null)
            }
            txtBudget.text = "$" + item.offerPrice.toString()
            txtCreatedDate.text = item.createdAt
            txtName.text = item.worker.name
            if (item.worker != null && item.worker.workerRatings != null && item.worker.workerRatings.avgRating != null) {
                starRatingBar.rating = item.worker.workerRatings.avgRating
                txt_rank.text = "("+item.worker.workerRatings.avgRating.toString().cleanRound() +"/5)"
            } else {
                starRatingBar.rating = 0f
                txt_rank.text = "( 0.0/5)"
            }
            assert(item.worker != null)
            if (item.worker.workTaskStatistics.completionRate != null) {
                txtCompletionRate.text =
                        item.worker.workTaskStatistics.completionRate.toString() + "%"
                txtJobSuccess.visibility = View.VISIBLE
            } else {
                txtJobSuccess.visibility = View.GONE
                txtCompletionRate.text = "New"
            }

            itemView.setOnClickListener {
                mOnItemClickListener!!.onItemOfferClick(item, "")
            }

            imgAvatar.setOnClickListener { v: View? ->
                if (mOnItemClickListener != null) {
                    mOnItemClickListener!!.onItemOfferClick(item, "profile")
                }
            }
            txtName.setOnClickListener { v: View? ->
                if (mOnItemClickListener != null) {
                    mOnItemClickListener!!.onItemOfferClick(item, "profile")
                }
            }

        }

    }

    class ProgressHolder internal constructor(itemView: View?) : BaseViewHolder(itemView) {
        override fun clear() {}
    }

    companion object {
        private const val VIEW_TYPE_LOADING = 0
        private const val VIEW_TYPE_NORMAL = 1
    }

    init {
        sessionManager = SessionManager(context)
    }
}