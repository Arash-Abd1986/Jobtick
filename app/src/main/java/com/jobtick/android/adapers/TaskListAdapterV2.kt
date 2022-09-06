package com.jobtick.android.adapers

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.google.android.material.button.MaterialButton
import com.jobtick.android.R
import com.jobtick.android.activities.SkillsTagActivity
import com.jobtick.android.activities.TaskAlertsActivity
import com.jobtick.android.models.UserAccountModel
import com.jobtick.android.models.response.myjobs.Data
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.ImageUtil
import com.jobtick.android.utils.TimeHelper
import com.jobtick.android.utils.Tools
import com.mikhaellopez.circularimageview.CircularImageView
import java.text.ParseException

class TaskListAdapterV2(
        private val mItems: ArrayList<Data> = ArrayList(),
        private val userAccountModel: UserAccountModel?,
        private val isSingleLineBody: Boolean,
        private val isFromExplore: Boolean = false
) :
        RecyclerView.Adapter<BaseViewHolder>() {
    private var context: Context? = null
    private var mOnItemClickListener: OnItemClickListener? = null
    var onDraftDeleteListener: OnDraftDeleteListener? = null
    var showSkills = true
    var showAlert = true

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
                LayoutInflater.from(parent.context).inflate(if (isFromExplore) R.layout.item_task_view else R.layout.item_task_view_map, parent, false)
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
        var txtOfferCount: TextView? = null
        var txtTitle: TextView? = null
        var txtLocation: TextView? = null
        var txtDueDate: TextView? = null
        var txtBudget: TextView? = null
        var txtStatus: TextView? = null
        var content: View? = null
        var jobAlertCard: View? = null
        var btnAddAlert: MaterialButton? = null
        var btnCloseAlert: AppCompatImageView? = null
        var jobSkillsCard: View? = null
        var btnAddSkills: MaterialButton? = null
        var btnCloseSkills: AppCompatImageView? = null
        override fun clear() {}

        init {

            txtOfferCount = itemView!!.findViewById(R.id.txt_offer_count)
            txtTitle = itemView.findViewById(R.id.txt_title)
            txtLocation = itemView.findViewById(R.id.txt_location)
            txtDueDate = itemView.findViewById(R.id.txt_due_date)
            txtBudget = itemView.findViewById(R.id.txt_budget)
            txtStatus = itemView.findViewById(R.id.txt_status)
            content = itemView.findViewById(R.id.content)
            jobAlertCard = itemView.findViewById(R.id.job_alert_card)
            btnAddAlert = itemView.findViewById(R.id.btn_add_alert)
            btnCloseAlert = itemView.findViewById(R.id.btn_close_alert)
            jobSkillsCard = itemView.findViewById(R.id.job_skills_card)
            btnAddSkills = itemView.findViewById(R.id.btn_add_skills)
            btnCloseSkills = itemView.findViewById(R.id.btn_close_skills)
        }

        @SuppressLint("SetTextI18n")
        override fun onBind(position: Int) {
            super.onBind(position)
            val item = mItems[position]
            btnAddAlert!!.setOnClickListener {
                val taskAlerts = Intent(context, TaskAlertsActivity::class.java)
                taskAlerts.addFlags(FLAG_ACTIVITY_NEW_TASK)
                context!!.startActivity(taskAlerts)
                showAlert = false
            }
            btnAddSkills!!.setOnClickListener {
                if (userAccountModel != null) {
                    val intent = Intent(context, SkillsTagActivity::class.java)
                    val bundle = Bundle()
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                    bundle.putStringArrayList(ConstantKey.SKILLS, userAccountModel.skills.skills)
                    bundle.putString(ConstantKey.TOOLBAR_TITLE, ConstantKey.EXPERIENCE)
                    bundle.putString(ConstantKey.TITLE, "Add your occupation")
                    intent.putExtras(bundle)
                    context!!.startActivity(intent)
                    showSkills = false
                }
            }

            btnCloseAlert!!.setOnClickListener {
                jobSkillsCard!!.visibility = View.GONE
                showSkills = false
            }
            btnCloseAlert!!.setOnClickListener {
                jobAlertCard!!.visibility = View.GONE
                showAlert = false
            }
            if (isSingleLineBody)
                txtTitle!!.maxLines = 1
            else
                txtTitle!!.maxLines = 2
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
                if (count > 1) {
                    txtOfferCount!!.text = "$count Offers"
                    when {
                        count > 1000 -> {
                            txtOfferCount!!.text = "+" + count / 1000 + "K" + " Offers"
                        }
                        count > 100 -> {
                            txtOfferCount!!.text = "+" + count / 100 * 100 + " Offers"
                        }
                        count > 10 -> {
                            txtOfferCount!!.text = "+" + count / 10 * 10 + " Offers"
                        }
                    }
                } else if (count == 1) {
                    txtOfferCount!!.text = "$count Offer"

                } else {
                    txtOfferCount!!.text = "No offer"
                }
            } else {
                txtOfferCount!!.text = "No offer"
            }
            if (item.status != null && isFromExplore) {
                txtStatus!!.visibility = View.VISIBLE
                setStatusText(item)
            }
            content!!.setOnClickListener { v: View? ->
                if (mOnItemClickListener != null) {
                    mOnItemClickListener!!.onItemClick(v, item, adapterPosition, "action")
                }
            }

        }

        private fun setColors(item: Data) {
            when (item.status) {
                "draft" -> {
                    txtStatus!!.visibility = View.VISIBLE
                }
                "open", "posted", "offered" -> {
                    txtStatus!!.visibility = View.VISIBLE
                }
                "assigned", "overdue" -> {
                    txtStatus!!.visibility = View.VISIBLE
                }
                "completed" -> {
                    txtStatus!!.visibility = View.VISIBLE

                }
                "closed" -> {
                    txtStatus!!.visibility = View.VISIBLE
                }
                "cancelled" -> {
                    txtStatus!!.visibility = View.VISIBLE
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
                userAccountModel?.id == item.poster_id && item.status == "open" && item.offers!!.isNotEmpty() -> {
                    txtStatus!!.text = "Offered"
                }
                userAccountModel?.id == item.poster_id && item.status == "open" -> {
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
