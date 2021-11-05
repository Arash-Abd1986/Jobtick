package com.jobtick.android.adapers

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.jobtick.android.R
import com.jobtick.android.activities.ProfileActivity
import com.jobtick.android.activities.ReportActivity
import com.jobtick.android.activities.ZoomImageActivity
import com.jobtick.android.models.AttachmentModel
import com.jobtick.android.models.CommentModel
import com.jobtick.android.models.OfferModel
import com.jobtick.android.models.QuestionModel
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.ImageUtil
import com.jobtick.android.utils.setMoreLess
import com.mikhaellopez.circularimageview.CircularImageView
import java.util.*

class PublicChatListAdapter : RecyclerView.Adapter<BaseViewHolder> {
    private val context: Context?
    private val isInPublicChat: Boolean
    private val posterID: String?
    private var mOnItemClickListener: OnItemClickListener? = null
    private var offerModel: OfferModel? = null
    private var questionModel: QuestionModel? = null
    private var isOfferModel = true
    private var status: String? = null
    fun addExtraItems(item: OfferModel?, isOfferModel: Boolean) {
        offerModel = item
        this.isOfferModel = isOfferModel
    }

    fun addExtraItems(item: QuestionModel?, isOfferModel: Boolean) {
        questionModel = item
        this.isOfferModel = isOfferModel
    }

    interface OnItemClickListener {
        fun onItemClick(view: View?, obj: OfferModel?, position: Int, action: String?)
        fun onItemClick(view: View?, obj: QuestionModel?, position: Int, action: String?)
        fun onItemClick(view: View?, obj: CommentModel?, position: Int, action: String?)
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        mOnItemClickListener = mItemClickListener
    }

    private var isLoaderVisible = false
    private val mItems: MutableList<CommentModel>?

    constructor(
        context: Context?,
        mItems: MutableList<CommentModel>?,
        isInPublicChat: Boolean,
        posterID: String?
    ) {
        this.mItems = mItems
        this.context = context
        this.isInPublicChat = isInPublicChat
        this.posterID = posterID
    }

    constructor(
        context: Context?,
        mItems: MutableList<CommentModel>?,
        status: String?,
        posterID: Int
    ) {
        this.mItems = mItems
        this.context = context
        isInPublicChat = false
        this.status = status
        this.posterID = posterID.toString() + ""
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_LOADING -> ProgressHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
            )
            else -> ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_offer_chat, parent, false)
            )
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

    fun addItems(mItems: List<CommentModel>?) {
        this.mItems!!.addAll(mItems!!)
        notifyDataSetChanged()
    }

    fun addItem(mItem: CommentModel) {
        mItems!!.add(mItem)
        notifyDataSetChanged()
        notifyItemInserted(mItems.size)
    }

    fun addLoading() {
        isLoaderVisible = true
        mItems!!.add(CommentModel())
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

    private fun getItem(position: Int): CommentModel {
        return mItems!![position]
    }

    inner class ViewHolder internal constructor(itemView: View?) : BaseViewHolder(itemView) {

        var imgAvatar: CircularImageView = itemView!!.findViewById(R.id.img_avatar)
        var txtName: TextView = itemView!!.findViewById(R.id.txt_name)
        var txtCreatedDate: TextView = itemView!!.findViewById(R.id.txt_created_date)
        var txtMessage: TextView = itemView!!.findViewById(R.id.txt_message)
        var imgFile: ImageView = itemView!!.findViewById(R.id.img_file)
        var cardImgFile: CardView = itemView!!.findViewById(R.id.card_img_file)
        var isPoster: TextView = itemView!!.findViewById(R.id.is_poster)
        var ivFlag: LinearLayout = itemView!!.findViewById(R.id.ivFlag)


        override fun clear() {}

        @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
        override fun onBind(position: Int) {
            super.onBind(position)
            val item = mItems!![position]
            if (item.user.avatar != null) ImageUtil.displayImage(
                imgAvatar,
                item.user.avatar.thumbUrl,
                null
            )
            txtName.text = item.user.name
            txtMessage.text = item.commentText
            txtCreatedDate.text = item.createdAt

            if (item.attachments != null && item.attachments.size != 0) {
                cardImgFile.visibility = View.VISIBLE
                ImageUtil.displayImage(imgFile, item.attachments[0].modalUrl, null)
                if (context != null) {
                    imgFile.setOnClickListener { v: View? ->
                        val intent = Intent(context, ZoomImageActivity::class.java)
                        val attachmentArrayList = ArrayList<AttachmentModel>()
                        attachmentArrayList.add(item.attachments[0])
                        intent.putExtra("url", attachmentArrayList)
                        intent.putExtra("title", "")
                        intent.putExtra("pos", 0)
                        context.startActivity(intent)
                    }
                }
            } else {
                cardImgFile.visibility = View.GONE
            }
            if (posterID != null)
                if (item.user.id.toString() == posterID) isPoster.visibility =
                    View.VISIBLE else isPoster.visibility = View.GONE

            ivFlag.setOnClickListener { view: View? ->
                val bundle = Bundle()
                val intent = Intent(context, ReportActivity::class.java)
                bundle.putString("key", ConstantKey.KEY_COMMENT_REPORT)
                bundle.putInt(ConstantKey.commentId, item.id)
                intent.putExtras(bundle)
                context!!.startActivity(intent)
            }
            imgAvatar.setOnClickListener { v: View? ->
                val intent = Intent(context, ProfileActivity::class.java)
                intent.putExtra("id", item.user.id)
                context!!.startActivity(intent)
            }
            txtName.setOnClickListener { imgAvatar.performClick() }

            setMoreLess(txtMessage, item.commentText, 3)
        }


    }

    @JvmOverloads
    fun toggleArrow(show: String, view: View, delay: Boolean = true) {
        if (show.equals("Less", ignoreCase = true)) {
            view.animate().setDuration(if (delay) 200 else 0.toLong()).rotation(180f)
        } else {
            view.animate().setDuration(if (delay) 200 else 0.toLong()).rotation(0f)
        }
    }

    class ProgressHolder internal constructor(itemView: View?) : BaseViewHolder(itemView) {
        override fun clear() {}
    }

    companion object {
        /*
     * Note:
     * In OfferChatList
     * i am added OfferListModel Model Class
     * because reply btn i want work both side
     * */
        private const val VIEW_TYPE_LOADING = 0
        private const val VIEW_TYPE_NORMAL = 1
    }
}