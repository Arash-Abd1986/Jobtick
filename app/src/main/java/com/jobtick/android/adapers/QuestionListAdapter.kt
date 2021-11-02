package com.jobtick.android.adapers

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jobtick.android.R
import com.jobtick.android.activities.ProfileActivity
import com.jobtick.android.models.AttachmentModel
import com.jobtick.android.models.CommentModel
import com.jobtick.android.models.OfferModel
import com.jobtick.android.models.QuestionModel
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.ImageUtil
import com.jobtick.android.utils.setMoreLess
import com.mikhaellopez.circularimageview.CircularImageView
import java.util.*

class QuestionListAdapter(private val context: Context, private val mItems: MutableList<QuestionModel>?, private val status: String, private val posterID: Int) : RecyclerView.Adapter<BaseViewHolder>() {
    private var mOnItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemQuestionClick(view: View?, obj: QuestionModel?, position: Int, action: String?)
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        mOnItemClickListener = mItemClickListener
    }

    private var isLoaderVisible = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_NORMAL -> ViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_question, parent, false))
            else -> ProgressHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false))
        }
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

    fun addItems(mItems: List<QuestionModel>?) {
        this.mItems!!.addAll(mItems!!)
        notifyDataSetChanged()
    }

    fun addItem(items: QuestionModel) {
        mItems!!.add(0, items)
        notifyItemInserted(0)
    }
    fun getItems(): MutableList<QuestionModel>? {
        return mItems
    }

    fun addLoading() {
        isLoaderVisible = true
        mItems!!.add(QuestionModel())
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

    private fun getItem(position: Int): QuestionModel {
        return mItems!![position]
    }

    inner class ViewHolder internal constructor(itemView: View?) : BaseViewHolder(itemView), PublicChatListAdapter.OnItemClickListener, AttachmentAdapter2.OnItemClickListener {

        var imgAvatar: CircularImageView = itemView!!.findViewById(R.id.img_avatar)
        var txtName: TextView = itemView!!.findViewById(R.id.txt_name)
        var txtCreatedDate: TextView = itemView!!.findViewById(R.id.txt_created_date)
        var txtMessage: TextView = itemView!!.findViewById(R.id.txt_message)
        var recyclerViewQuestion: RecyclerView = itemView!!.findViewById(R.id.recycler_view_question)
        var imgFile: ImageView = itemView!!.findViewById(R.id.img_file_questions)
        var cardImgFile: CardView = itemView!!.findViewById(R.id.card_img_file)
        var lytBtnReply: LinearLayout = itemView!!.findViewById(R.id.lyt_btn_reply)
        var lnMoreReply: LinearLayout = itemView!!.findViewById(R.id.ln_more_reply)
        var txtMoreReplyQuestion: TextView = itemView!!.findViewById(R.id.txt_more_reply_question)
        var recyclerViewQuestionsChat: RecyclerView = itemView!!.findViewById(R.id.recycler_view_questions_chat)
        var ivReport: ImageView = itemView!!.findViewById(R.id.ivReport)
        var linearUserProfile: LinearLayout = itemView!!.findViewById(R.id.linear_user_profile)


        override fun clear() {}

        @SuppressLint("SetTextI18n")
        override fun onBind(position: Int) {
            super.onBind(position)
            val item = mItems!![position]
            if (item.user.avatar != null) {
                ImageUtil.displayImage(imgAvatar, item.user.avatar.thumbUrl, null)
            }
            if (status == Constant.TASK_OPEN) lytBtnReply.visibility = View.VISIBLE else lytBtnReply.visibility = View.GONE
            if (item.commentsCount > 3) {
                val remainingNumber = item.commentsCount - 3
                if (remainingNumber == 1) {
                    txtMoreReplyQuestion.text = "View more reply (1)"
                } else {
                    txtMoreReplyQuestion.text = "View more reply ($remainingNumber)"
                }
                lnMoreReply.visibility = View.VISIBLE
            } else {
                lnMoreReply.visibility = View.GONE
            }
            txtName.text = item.user.name
            txtCreatedDate.text = item.createdAt
            txtMessage.visibility = View.VISIBLE
            setMoreLess(txtMessage,item.questionText,3)
            if (item.attachments.size != 0) {
                recyclerViewQuestion.visibility = View.VISIBLE
                val attachmentAdapter = AttachmentAdapter2(item.attachments, false, context)
                recyclerViewQuestion.setHasFixedSize(true)
                recyclerViewQuestion.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                recyclerViewQuestion.adapter = attachmentAdapter
                recyclerViewQuestion.isNestedScrollingEnabled = true
                attachmentAdapter.setOnItemClickListener(this)
            } else {
                recyclerViewQuestion.visibility = View.GONE
            }
            lytBtnReply.setOnClickListener { v: View? ->
                if (mOnItemClickListener != null) {
                    mOnItemClickListener!!.onItemQuestionClick(v, item, adapterPosition, "reply")
                }
            }
            ivReport.setOnClickListener { v: View? ->
                if (mOnItemClickListener != null) {
                    mOnItemClickListener!!.onItemQuestionClick(v, item, adapterPosition, "report")
                }
            }
            txtMoreReplyQuestion.setOnClickListener { v: View? ->
                if (mOnItemClickListener != null) {
                    mOnItemClickListener!!.onItemQuestionClick(v, item, adapterPosition, "reply")
                }
            }
            linearUserProfile.setOnClickListener { v: View? ->
                val intent = Intent(context, ProfileActivity::class.java)
                intent.putExtra("id", item.user.id)
                context.startActivity(intent)
            }
            imgAvatar.setOnClickListener { v: View? -> linearUserProfile.performClick() }
            txtName.setOnClickListener { v: View? -> linearUserProfile.performClick() }
            val publicChatListAdapter = PublicChatListAdapter(context, ArrayList(), status, posterID)
            recyclerViewQuestionsChat.setHasFixedSize(true)
            recyclerViewQuestionsChat.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            recyclerViewQuestionsChat.adapter = publicChatListAdapter
            recyclerViewQuestionsChat.isNestedScrollingEnabled = true
            publicChatListAdapter.addItems(item.comments)
            publicChatListAdapter.addExtraItems(item, false)
            publicChatListAdapter.setOnItemClickListener(this)
        }

        override fun onItemClick(view: View?, obj: OfferModel?, position: Int, action: String?) {}
        override fun onItemClick(view: View?, obj: QuestionModel?, position: Int, action: String?) {
            if (action.equals("reply", ignoreCase = true)) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener!!.onItemQuestionClick(view, obj, adapterPosition, "reply")
                }
            }
        }

        override fun onItemClick(view: View?, obj: CommentModel?, position: Int, action: String?) {}
        override fun onItemClick(view: View, obj: AttachmentModel, position: Int, action: String) {}

    }

    class ProgressHolder internal constructor(itemView: View?) : BaseViewHolder(itemView) {
        override fun clear() {}
    }

    companion object {
        private const val VIEW_TYPE_LOADING = 0
        private const val VIEW_TYPE_NORMAL = 1
    }
}