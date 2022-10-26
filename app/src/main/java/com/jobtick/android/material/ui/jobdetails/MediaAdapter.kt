package com.jobtick.android.material.ui.jobdetails

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
import com.google.android.material.textview.MaterialTextView
import com.jobtick.android.R
import com.jobtick.android.adapers.AttachmentAdapter
import com.jobtick.android.models.AttachmentModelV2
import com.jobtick.android.utils.dpToPx
import com.jobtick.android.utils.gone
import com.jobtick.android.utils.visible
import timber.log.Timber


class MediaAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private var mOnItemClickListener: OnItemClickListener? = null
    private var context: Context? = null
    private var screenWidth: Int = 0
    private var selectionMode = AttachmentAdapter.VIEW_TYPE_PLACE_HOLDER
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
            AttachmentAdapter.VIEW_TYPE_ADD -> ADDViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_media_plus, parent, false))
            else -> IMAGEViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_media, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) != AttachmentAdapter.VIEW_TYPE_ADD) {
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
        var pdf: AppCompatImageView = itemView!!.findViewById(R.id.pdf)
        var rlMain: RelativeLayout = itemView!!.findViewById(R.id.rlMain)
        var checkBox: AppCompatCheckBox = itemView!!.findViewById(R.id.checkBox)
        var txtMore: MaterialTextView = itemView!!.findViewById(R.id.txtMore)

        @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
        fun onBind(position: Int) {
            val item = items?.get(position)

            rlMain.updateLayoutParams {
                height = (screenWidth - (64).dpToPx()) / 3
            }
            items?.let {
                if (it.size > 3) {
                    if (position == 2) {
                        txtMore.visible()
                        txtMore.text = "+" + (items.size - 3)
                    } else {
                        txtMore.gone()
                    }
                } else
                    txtMore.gone()

            }

            when (selectionMode) {
                AttachmentAdapter.VIEW_TYPE_IMAGE -> {
                    if (item!!.type == AttachmentAdapter.VIEW_TYPE_IMAGE) {
                        checkBox.visibility = View.VISIBLE
                        checkBox.isChecked = item.isChecked
                    } else {
                        checkBox.visibility = View.GONE
                    }
                }
                AttachmentAdapter.VIEW_TYPE_PDF -> {
                    if (item!!.type == AttachmentAdapter.VIEW_TYPE_PDF) {
                        checkBox.visibility = View.VISIBLE
                        checkBox.isChecked = item.isChecked
                    } else {
                        checkBox.visibility = View.GONE
                    }
                }
                AttachmentAdapter.VIEW_TYPE_ERROR -> {
                    if (item!!.type == AttachmentAdapter.VIEW_TYPE_ERROR) {
                        checkBox.visibility = View.VISIBLE
                        checkBox.isChecked = item.isChecked
                    } else {
                        checkBox.visibility = View.GONE
                    }
                }
            }

            checkBox.setOnClickListener {
                item!!.isChecked = !item.isChecked
                items?.set(position, item)
                items?.forEach { if (it != item) it.isChecked = false }
                calcShowOptions(items, item)
                notifyDataSetChanged()
            }
            media.setOnLongClickListener {
                selectionMode = AttachmentAdapter.VIEW_TYPE_IMAGE
                notifyDataSetChanged()
                true
            }
            error.setOnLongClickListener {
                selectionMode = AttachmentAdapter.VIEW_TYPE_ERROR
                notifyDataSetChanged()
                true
            }
            pdf.setOnLongClickListener {
                selectionMode = AttachmentAdapter.VIEW_TYPE_PDF
                notifyDataSetChanged()
                true
            }
            if (item!!.type == AttachmentAdapter.VIEW_TYPE_PROGRESS) {
                progress.visibility = View.VISIBLE
                media.visibility = View.GONE
                error.visibility = View.GONE
            } else if (item.type == AttachmentAdapter.VIEW_TYPE_ERROR) {
                progress.visibility = View.GONE
                media.visibility = View.GONE
                error.visibility = View.VISIBLE
            } else if (item.type == AttachmentAdapter.VIEW_TYPE_PLACE_HOLDER) {
                progress.visibility = View.GONE
                media.visibility = View.GONE
                error.visibility = View.GONE
            } else {
                if (item.type == AttachmentAdapter.VIEW_TYPE_PDF) {
                    pdf.visibility = View.VISIBLE
                    media.visibility = View.GONE
                } else {
                    pdf.visibility = View.GONE
                    media.visibility = View.VISIBLE
                }
                progress.visibility = View.GONE
                error.visibility = View.GONE
                Glide.with(media).load(item.thumbUrl).into(media)
            }
        }

        private fun calcShowOptions(items: MutableList<AttachmentModelV2>?, item: AttachmentModelV2) {
            val selectedSize = items!!.filter {
                it.isChecked && (item.type != AttachmentAdapter.VIEW_TYPE_ADD) && (item.type != AttachmentAdapter.VIEW_TYPE_PROGRESS)
                        && (item.type != AttachmentAdapter.VIEW_TYPE_PLACE_HOLDER)
            }.size
            when (selectedSize) {
                0 -> {
                    showOptions.showOptions(item, Option.HIDE)
                }
                1 -> {
                    when (item.type) {
                        AttachmentAdapter.VIEW_TYPE_ERROR -> {
                            showOptions.showOptions(item, Option.RETRY)
                        }
                        AttachmentAdapter.VIEW_TYPE_IMAGE -> {
                            showOptions.showOptions(item, Option.VIEW)
                        }
                        AttachmentAdapter.VIEW_TYPE_PDF -> {
                            showOptions.showOptions(item, Option.DELETE)
                        }
                    }

                }
                else -> {
                    showOptions.showOptions(item, Option.SELECT_ALL)
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
                if (items!!.size <= 9)
                    mOnItemClickListener!!.onItemClick(it, items.get(index = position), adapterPosition, "add")
            }
            if (items!!.filter { it.type != AttachmentAdapter.VIEW_TYPE_PLACE_HOLDER }.size >= 9) {
                add.setColorFilter(context!!.getColor(R.color.neutral_light_400))
            } else {
                add.setColorFilter(context!!.getColor(R.color.primary))

            }
        }

    }
}


enum class Option {
    EDIT, RETRY, VIEW, SELECT_ALL, HIDE, DELETE
}