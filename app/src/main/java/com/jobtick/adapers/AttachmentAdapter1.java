package com.jobtick.adapers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.R;
import com.jobtick.models.AttachmentModel;
import com.jobtick.utils.ImageUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AttachmentAdapter1 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_ADD = 0;
    public static final int VIEW_TYPE_IMAGE = 1;

    private Context context;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, AttachmentModel obj, int position, String action);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    private boolean isLoaderVisible = true;
    private Boolean delete_action;
    private List<AttachmentModel> items;

    public AttachmentAdapter1(Context context, List<AttachmentModel> items, Boolean delete_action) {
        this.items = items;
        this.context = context;
        this.delete_action = delete_action;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_IMAGE:
                return new AttachmentAdapter1.IMAGEViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_show_attachment_1, parent, false));
            case VIEW_TYPE_ADD:
            default:
                return new AttachmentAdapter1.ADDViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_attachment_1, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_IMAGE) {
            ((AttachmentAdapter1.IMAGEViewHolder) holder).onBind(holder.getAdapterPosition());
        } else {
            ((AttachmentAdapter1.ADDViewHolder) holder).onBind(holder.getAdapterPosition());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }


    public void addItems(List<AttachmentModel> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
        Log.e("AddItems", "call");
    }

    public void addLoading() {
        isLoaderVisible = true;
        this.items.add(new AttachmentModel());
        notifyItemInserted(this.items.size() - 1);
    }


    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    AttachmentModel getItem(int position) {
        return items.get(position);
    }

    public class IMAGEViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_view)
        ImageView imgView;
        @BindView(R.id.img_btn_delete)
        ImageView imgBtnDelete;


        IMAGEViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }


        public void onBind(int position) {
            // super.onBind(position);
            AttachmentModel item = items.get(position);
            if (item.getThumbUrl() != null) {
                ImageUtil.displayImage(imgView, item.getThumbUrl(), null);

            }
            if (delete_action) {
                imgBtnDelete.setVisibility(View.VISIBLE);
            } else {
                imgBtnDelete.setVisibility(View.GONE);
            }
            imgBtnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position, "delete");
                    }
                }
            });
            imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(v, items.get(position), position, "show");
                    }
                }
            });

        }
    }

    public class ADDViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rlt_add_attachment)
        RelativeLayout rltAddAttachment;


        ADDViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(int position) {
            // super.onBind(position);
            AttachmentModel item = items.get(position);
            rltAddAttachment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position, "add");
                    }
                }
            });


        }


    }
}