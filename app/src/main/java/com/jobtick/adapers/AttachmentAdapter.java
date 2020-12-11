package com.jobtick.adapers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.models.AttachmentModel;
import com.jobtick.utils.ImageUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


public class AttachmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_ADD = 0;
    public static final int VIEW_TYPE_IMAGE = 1;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, AttachmentModel obj, int position, String action);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    private final Boolean delete_action;
    private final List<AttachmentModel> items;

    public AttachmentAdapter(List<AttachmentModel> items, Boolean delete_action) {
        this.items = items;
        this.delete_action = delete_action;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_IMAGE:
                return new IMAGEViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_show_attachment, parent, false));
            case VIEW_TYPE_ADD:
            default:
                return new ADDViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_attachment, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_IMAGE) {
            ((IMAGEViewHolder) holder).onBind(holder.getAdapterPosition());
        } else {
            ((ADDViewHolder) holder).onBind(holder.getAdapterPosition());
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
        Timber.e("call");
    }

    public void addLoading() {
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
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_view)
        ImageView imgView;
        @SuppressLint("NonConstantResourceId")
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
                ImageUtil.displayImage(imgView, item.getUrl(), null);

            }
            if (delete_action) {
                imgBtnDelete.setVisibility(View.VISIBLE);
            } else {
                imgBtnDelete.setVisibility(View.GONE);
            }
            imgBtnDelete.setOnClickListener(view -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, items.get(position), position, "delete");
                }
            });
            imgView.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, items.get(position), position, "show");
                }
            });

        }
    }

    public class ADDViewHolder extends RecyclerView.ViewHolder {
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.rlt_add_attachment)
        RelativeLayout rltAddAttachment;


        ADDViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(int position) {
            // super.onBind(position);
            AttachmentModel item = items.get(position);
            rltAddAttachment.setOnClickListener(view -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, items.get(position), position, "add");
                }
            });


        }


    }
}




