package com.jobtick.android.material.ui.postajob

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jobtick.android.R
import com.jobtick.android.adapers.AttachmentAdapter
import com.jobtick.android.models.AttachmentModelV2
import com.jobtick.android.utils.dpToPx
import timber.log.Timber


class MediaAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private var mOnItemClickListener: OnItemClickListener? = null
    private var context: Context? = null
    private var screenWidth: Int = 0
    private var selectionMode: Boolean = false
    lateinit var showOptions: OnShowOptions

    interface OnItemClickListener {
        fun onItemClick(view: View?, obj: AttachmentModelV2?, position: Int, action: String?)
    }

    interface OnShowOptions {
        fun showOptions(item: AttachmentModelV2?, option: Option)
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        mOnItemClickListener = mItemClickListener
    }

    private val items: MutableList<AttachmentModelV2>?

    constructor(items: MutableList<AttachmentModelV2>?) {
        this.items = items
    }

    constructor(items: MutableList<AttachmentModelV2>?, context: Context?, width: Int) {
        this.items = items
        this.context = context
        this.screenWidth = width
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            AttachmentAdapter.VIEW_TYPE_IMAGE -> IMAGEViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_media, parent, false))
            AttachmentAdapter.VIEW_TYPE_ADD -> ADDViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_media_plus, parent, false))
            else -> ADDViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_media, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == AttachmentAdapter.VIEW_TYPE_IMAGE) {
            (holder as IMAGEViewHolder).onBind(holder.getAdapterPosition())
        } else {
            (holder as ADDViewHolder).onBind(holder.getAdapterPosition())
        }
    }

    override fun getItemViewType(position: Int): Int {
        return items!![position].type!!
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    fun addItems(items: List<AttachmentModelV2>?) {
        this.items!!.addAll(items!!)
        notifyDataSetChanged()
        Timber.e("call")
    }

    fun addLoading() {
        items!!.add(AttachmentModelV2())
        notifyItemInserted(items.size - 1)
    }

    fun clear() {
        items!!.clear()
        notifyDataSetChanged()
    }

    fun getItem(position: Int): AttachmentModelV2 {
        return items!![position]
    }

    inner class IMAGEViewHolder internal constructor(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var media: AppCompatImageView = itemView!!.findViewById(R.id.media)
        var progress: ProgressBar = itemView!!.findViewById(R.id.progress)
        var error: AppCompatImageView = itemView!!.findViewById(R.id.error)
        var rlMain: RelativeLayout = itemView!!.findViewById(R.id.rlMain)
        var checkBox: AppCompatCheckBox = itemView!!.findViewById(R.id.checkBox)

        @SuppressLint("NotifyDataSetChanged")
        fun onBind(position: Int) {
            val item = items?.get(position)

            rlMain.updateLayoutParams {
                height = (screenWidth - (80).dpToPx()) / 3
            }
            if (selectionMode) {
                if (item!!.id != -1) {
                    checkBox.visibility = View.VISIBLE
                    checkBox.isChecked = item.isChecked
                } else
                    checkBox.visibility = View.GONE
            }
            checkBox.setOnCheckedChangeListener { _, b ->
                item!!.isChecked = b
                items?.set(position, item)
                calcShowOptions(items, item)
            }
            media.setOnLongClickListener {
                selectionMode = true
                notifyDataSetChanged()
                true
            }
            error.setOnLongClickListener {
                selectionMode = true
                notifyDataSetChanged()
                true
            }
            if (item!!.id == -1) {
                progress.visibility = View.VISIBLE
                media.visibility = View.GONE
                error.visibility = View.GONE
            } else if (item.id == -2) {
                progress.visibility = View.GONE
                media.visibility = View.GONE
                error.visibility = View.VISIBLE
            } else {
                progress.visibility = View.GONE
                error.visibility = View.GONE
                media.visibility = View.VISIBLE
                Glide.with(media).load(item.thumbUrl).into(media)
            }
        }

        private fun calcShowOptions(items: MutableList<AttachmentModelV2>?, item: AttachmentModelV2) {
            if (items!!.any { it.isChecked }) {
                if (items.filter { it.isChecked }.size > 1)
                    showOptions.showOptions(item, Option.SELECT_ALL)
                else if (items.filter { it.isChecked }.size == 1) {
                    if (items.filter { it.isChecked }[0].id == -2) {
                        showOptions.showOptions(item, Option.RETRY)
                    } else {
                        showOptions.showOptions(item, Option.VIEW)
                    }
                } else {
                    showOptions.showOptions(item, Option.HIDE)
                }
            }
        }


    }

    inner class ADDViewHolder internal constructor(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var add: AppCompatImageView = itemView!!.findViewById(R.id.add)
        var rlMain: RelativeLayout = itemView!!.findViewById(R.id.rlMain)

        fun onBind(position: Int) {
            rlMain.updateLayoutParams {
                height = (screenWidth - (80).dpToPx()) / 3
            }
            add.setOnClickListener {
                mOnItemClickListener!!.onItemClick(it, items?.get(position), adapterPosition, "add")

            }
        }

    }
}


enum class Option {
    EDIT, RETRY, VIEW, SELECT_ALL, HIDE
}