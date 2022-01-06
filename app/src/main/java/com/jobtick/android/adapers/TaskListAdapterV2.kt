package com.jobtick.android.adapers

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.jobtick.android.R
import com.jobtick.android.models.response.myjobs.Data
import com.jobtick.android.utils.ImageUtil
import com.jobtick.android.utils.TimeHelper
import com.jobtick.android.utils.Tools
import com.mikhaellopez.circularimageview.CircularImageView
import java.text.ParseException

class TaskListAdapterV2(
    private val mItems: ArrayList<Data> = ArrayList(),
    private val userId: Int?,
    private val isSingleLineBody: Boolean
) :
    RecyclerView.Adapter<BaseViewHolder>() {
    private var context: Context? = null
    private var mOnItemClickListener: OnItemClickListener? = null
    var onDraftDeleteListener: OnDraftDeleteListener? = null

    interface OnItemClickListener {
        fun onItemClick(view: View?, obj: Data?, position: Int, action: String?)
    }

    interface OnDraftDeleteListener {
        fun onDraftDeleteButtonClick(view: View?, taskModel: Data?, position: Int)
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        mOnItemClickListener = mItemClickListener
    }

    private var isLoaderVisible = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        return if (viewType == VIEW_TYPE_LOADING) {
            ProgressHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
            )
        } else ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_task_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == mItems.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun addItems(mItems: List<Data>, allItems: Int) {
        removeLoading()
        this.mItems.addAll(mItems)
        notifyDataSetChanged()
        if (this.mItems.size < allItems) {
            addLoading()
        }
    }

    fun addItemsWithoutLoading(mItems: List<Data>) {
        removeLoading()
        this.mItems.addAll(mItems)
        notifyDataSetChanged()
    }

    private fun addLoading() {
        if (isLoaderVisible) return
        isLoaderVisible = true
        val position = mItems.size - 1
        mItems.add(
            Data(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
            )
        )
        notifyItemInserted(position)
    }

    private fun removeLoading() {
        if (!isLoaderVisible) return
        isLoaderVisible = false
        val position = mItems.size - 1
        if (position == -1) return
        val item = getItem(position)
        if (item != null) {
            mItems.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        mItems.clear()
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Data {
        return mItems[position]
    }

    inner class ViewHolder internal constructor(itemView: View?) : BaseViewHolder(itemView) {
        var imgAvatar3: CircularImageView? = null
        var imgAvatar2: CircularImageView? = null
        var imgAvatar1: CircularImageView? = null
        var imgAvatar0: CircularImageView? = null
        var txtOfferCount: TextView? = null
        var txtTitle: TextView? = null
        var txtLocation: TextView? = null
        var txtDueDate: TextView? = null
        var txtBudget: TextView? = null
        var txtStatus: TextView? = null
        var txtStatusDraft: TextView? = null
        var tvDelete: TextView? = null
        var cardTaskBackground: View? = null
        var content: View? = null
        override fun clear() {}

        init {
            imgAvatar3 = itemView!!.findViewById(R.id.img_avatar3)
            imgAvatar2 = itemView.findViewById(R.id.img_avatar2)
            imgAvatar1 = itemView.findViewById(R.id.img_avatar1)
            imgAvatar0 = itemView.findViewById(R.id.img_avatar0)
            txtOfferCount = itemView.findViewById(R.id.txt_offer_count)
            txtTitle = itemView.findViewById(R.id.txt_title)
            txtLocation = itemView.findViewById(R.id.txt_location)
            txtDueDate = itemView.findViewById(R.id.txt_due_date)
            txtBudget = itemView.findViewById(R.id.txt_budget)
            txtStatus = itemView.findViewById(R.id.txt_status)
            txtStatusDraft = itemView.findViewById(R.id.txt_status_draft)
            tvDelete = itemView.findViewById(R.id.tv_delete)
            cardTaskBackground = itemView.findViewById(R.id.card_task_background)
            content = itemView.findViewById(R.id.content)
        }

        @SuppressLint("SetTextI18n")
        override fun onBind(position: Int) {
            super.onBind(position)
            val item = mItems[position]
            if (isSingleLineBody)
                txtTitle!!.maxLines = 1
            else
                txtTitle!!.maxLines = 2
            if (item.offers != null) if (item.offers.isNotEmpty()) {
                txtOfferCount!!.setTextColor(ContextCompat.getColor(context!!, R.color.N900))
                when {
                    item.offers.size >= 4 -> {
                        imgAvatar3!!.visibility = View.VISIBLE
                        imgAvatar2!!.visibility = View.VISIBLE
                        imgAvatar1!!.visibility = View.VISIBLE
                        imgAvatar0!!.visibility = View.VISIBLE
                        if (item.offers[3].avatar != null) ImageUtil.displayImage(
                            imgAvatar3,
                            item.offers[3].avatar,
                            null
                        ) else imgAvatar3!!.setImageResource(R.drawable.pic)
                        if (item.offers[2].avatar != null) ImageUtil.displayImage(
                            imgAvatar2,
                            item.offers[2].avatar,
                            null
                        ) else imgAvatar2!!.setImageResource(R.drawable.pic)
                        if (item.offers[1].avatar != null) ImageUtil.displayImage(
                            imgAvatar1,
                            item.offers[1].avatar,
                            null
                        ) else imgAvatar1!!.setImageResource(R.drawable.pic)
                        if (item.offers[0].avatar != null) ImageUtil.displayImage(
                            imgAvatar0,
                            item.offers[0].avatar,
                            null
                        ) else imgAvatar0!!.setImageResource(R.drawable.pic)
                    }
                    item.offers.size == 3 -> {
                        imgAvatar0!!.visibility = View.GONE
                        imgAvatar1!!.visibility = View.VISIBLE
                        imgAvatar2!!.visibility = View.VISIBLE
                        imgAvatar3!!.visibility = View.VISIBLE
                        if (item.offers[2].avatar != null) ImageUtil.displayImage(
                            imgAvatar1,
                            item.offers[2].avatar,
                            null
                        ) else imgAvatar1!!.setImageResource(R.drawable.pic)
                        if (item.offers[1].avatar != null) ImageUtil.displayImage(
                            imgAvatar2,
                            item.offers[1].avatar,
                            null
                        ) else imgAvatar2!!.setImageResource(R.drawable.pic)
                        if (item.offers[0].avatar != null) ImageUtil.displayImage(
                            imgAvatar3,
                            item.offers[0].avatar,
                            null
                        ) else imgAvatar3!!.setImageResource(R.drawable.pic)
                    }
                    item.offers.size == 2 -> {
                        imgAvatar0!!.visibility = View.GONE
                        imgAvatar1!!.visibility = View.GONE
                        imgAvatar2!!.visibility = View.VISIBLE
                        imgAvatar3!!.visibility = View.VISIBLE
                        if (item.offers[1].avatar != null) ImageUtil.displayImage(
                            imgAvatar2,
                            item.offers[1].avatar,
                            null
                        ) else imgAvatar2!!.setImageResource(R.drawable.pic)
                        if (item.offers[0].avatar != null) ImageUtil.displayImage(
                            imgAvatar3,
                            item.offers[0].avatar,
                            null
                        ) else imgAvatar3!!.setImageResource(R.drawable.pic)
                    }
                    item.offers.size == 1 -> {
                        imgAvatar0!!.visibility = View.GONE
                        imgAvatar1!!.visibility = View.GONE
                        imgAvatar2!!.visibility = View.GONE
                        imgAvatar3!!.visibility = View.VISIBLE
                        if (item.offers[0].avatar != null) ImageUtil.displayImage(
                            imgAvatar3,
                            item.offers[0].avatar,
                            null
                        ) else imgAvatar3!!.setImageResource(R.drawable.pic)
                    }
                }
            } else {
                txtOfferCount!!.setTextColor(ContextCompat.getColor(context!!, R.color.N300))
                imgAvatar3!!.visibility = View.GONE
                imgAvatar2!!.visibility = View.GONE
                imgAvatar1!!.visibility = View.GONE
                imgAvatar0!!.visibility = View.GONE
                //imgAvatar3.setImageResource(R.drawable.pic);
                txtOfferCount!!.text = ""
            } else {
                imgAvatar3!!.visibility = View.GONE
                imgAvatar2!!.visibility = View.GONE
                imgAvatar1!!.visibility = View.GONE
                imgAvatar0!!.visibility = View.GONE
                txtOfferCount!!.text = ""
                txtOfferCount!!.setTextColor(ContextCompat.getColor(context!!, R.color.N300))
                //imgAvatar3.setImageResource(R.drawable.pic);
            }
            txtTitle!!.text = item.title
            txtDueDate!!.text = TimeHelper.convertToWeekDateFormatV2(item.due_date)
            if (item.location != null) {
                txtLocation!!.text = item.location
            } else {
                txtLocation!!.text = "Remote job"
            }
            if (item.amount != null) {
                txtBudget!!.text = "$" + item.amount
            } else txtBudget!!.text = ""
            if (item.offers != null) {
                val count = item.offers.size
                if (count >= 1) {
                    if (item.offers.size < 5) txtOfferCount!!.visibility =
                        View.GONE else txtOfferCount!!.visibility = View.VISIBLE
                    txtOfferCount!!.text = count.toString() + ""
                    when {
                        count > 1000 -> {
                            txtOfferCount!!.text = "+" + count / 1000 + "K"
                        }
                        count > 100 -> {
                            txtOfferCount!!.text = "+" + count / 100 * 100
                        }
                        count > 10 -> {
                            txtOfferCount!!.text = "+" + count / 10 * 10
                        }
                    }
                }
            }
            if (item.status != null) {
                setColors(item)
                setStatusText(item)
            }
            content!!.setOnClickListener { v: View? ->
                if (mOnItemClickListener != null) {
                    mOnItemClickListener!!.onItemClick(v, item, adapterPosition, "action")
                }
            }
            tvDelete!!.setOnClickListener { v: View? ->
                if (onDraftDeleteListener != null) {
                    onDraftDeleteListener!!.onDraftDeleteButtonClick(v, item, position)
                }
            }
        }

        private fun setColors(item: Data) {
            when (item.status) {
                "draft" -> {
                    txtStatus!!.visibility = View.VISIBLE
                    txtStatusDraft!!.visibility = View.GONE
                    tvDelete!!.visibility = View.GONE
                    cardTaskBackground!!.background = ContextCompat.getDrawable(
                        context!!, R.drawable.shape_rounded_draft
                    )
                    txtStatus!!.setTextColor(ContextCompat.getColor(context!!, R.color.P100))
                }
                "open", "posted", "offered" -> {
                    txtStatus!!.visibility = View.VISIBLE
                    txtStatusDraft!!.visibility = View.GONE
                    tvDelete!!.visibility = View.GONE
                    cardTaskBackground!!.background = ContextCompat.getDrawable(
                        context!!, R.drawable.shape_rounded_offered
                    )
                    txtStatus!!.setTextColor(
                        ContextCompat.getColor(
                            context!!,
                            R.color.myJobsColorTaskOfferTrans
                        )
                    )
                }
                "assigned", "overdue" -> {
                    txtStatus!!.visibility = View.VISIBLE
                    txtStatusDraft!!.visibility = View.GONE
                    tvDelete!!.visibility = View.GONE
                    cardTaskBackground!!.background = ContextCompat.getDrawable(
                        context!!, R.drawable.shape_rounded_overdue
                    )
                    txtStatus!!.setTextColor(
                        ContextCompat.getColor(
                            context!!,
                            R.color.myJobsColorTaskAssignedTrans
                        )
                    )
                }
                "completed" -> {
                    txtStatus!!.visibility = View.VISIBLE
                    txtStatusDraft!!.visibility = View.GONE
                    tvDelete!!.visibility = View.GONE
                    cardTaskBackground!!.background = ContextCompat.getDrawable(
                        context!!, R.drawable.shape_rounded_offered
                    )
                    txtStatus!!.setTextColor(ContextCompat.getColor(context!!, R.color.myJobsColorTaskOfferTrans))
                }
                "closed" -> {
                    txtStatus!!.visibility = View.VISIBLE
                    txtStatusDraft!!.visibility = View.GONE
                    tvDelete!!.visibility = View.GONE
                    cardTaskBackground!!.background = ContextCompat.getDrawable(
                        context!!, R.drawable.shape_rounded_closed
                    )
                    txtStatus!!.setTextColor(ContextCompat.getColor(context!!, R.color.N080))
                }
                "cancelled" -> {
                    txtStatus!!.visibility = View.VISIBLE
                    txtStatusDraft!!.visibility = View.GONE
                    tvDelete!!.visibility = View.GONE
                    cardTaskBackground!!.background = ContextCompat.getDrawable(
                        context!!, R.drawable.shape_rounded_cancelled
                    )
                    txtStatus!!.setTextColor(
                        ContextCompat.getColor(
                            context!!,
                            R.color.myJobsColorTaskCancelledTrans
                        )
                    )
                }
            }
        }

        @SuppressLint("SetTextI18n")
        private fun setStatusText(item: Data) {
//            if (userId != null && item.getPoster() != null && item.getPoster().getId() != null &&
//                    item.getPoster().getId().equals(userId) && item.getStatus().equals("open")) {
//                txtStatus.setText("Posted");
//            } else
            when {
                userId != null && userId == item.poster_id && item.status == "open" && item.offers!!.isNotEmpty() -> {
                    txtStatus!!.text = "Offered"
                }
                userId != null && userId == item.poster_id && item.status == "open" -> {
                    txtStatus!!.text = "Posted"
                }
                item.status.equals("open", ignoreCase = true) -> {
                    txtStatus!!.text = "Open"
                }
                item.status.equals("Offered", ignoreCase = true) -> {
                    txtStatus!!.text = "Offered"
                }
                item.status.equals("posted", ignoreCase = true) -> {
                    txtStatus!!.text = "Posted"
                }
                item.status.equals("assigned", ignoreCase = true) -> {
                    txtStatus!!.text = "Assigned"
                }
                item.status.equals("overdue", ignoreCase = true) -> {
                    txtStatus!!.text = "Overdue"
                }
                item.status.equals("completed", ignoreCase = true) -> {
                    txtStatus!!.text = "Completed"
                }
                item.status.equals("closed", ignoreCase = true) -> {
                    txtStatus!!.text = "Closed"
                }
                item.status.equals("cancelled", ignoreCase = true) -> {
                    txtStatus!!.text = "Cancelled"
                }
                item.status == "draft" -> {
                    txtStatus!!.text = "Drafted"
                    if (item.updated_at != null) {
                        try {
                            txtOfferCount!!.text = "Last edited: " + Tools.formatJobDetailsDateV2(
                                Tools.chatDateToMillisZ(item.updated_at)
                            )
                        } catch (e: ParseException) {
                            e.printStackTrace()
                            txtOfferCount!!.text = ""
                        }
                    }
                }

                else -> {
                    txtStatus!!.text = item.status
                }
            }
        }

        init {
            ButterKnife.bind(this, itemView!!)
        }
    }

    class ProgressHolder internal constructor(itemView: View?) : BaseViewHolder(itemView) {
        override fun clear() {}
    }

    companion object {
        private const val VIEW_TYPE_LOADING = 0
        private const val VIEW_TYPE_NORMAL = 1
    }
}