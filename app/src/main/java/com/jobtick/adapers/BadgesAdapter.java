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
import com.jobtick.TextView.TextViewBold;
import com.jobtick.models.AttachmentModel;
import com.jobtick.models.BadgesModel;
import com.jobtick.utils.ImageUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class BadgesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_ADD = 0;
    public static final int VIEW_TYPE_IMAGE = 1;

    private Context context;


/*
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, AttachmentModel obj, int position, String action);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }
*/

    private boolean isLoaderVisible = true;
    private Boolean delete_action;
    private List<BadgesModel> items;

    public BadgesAdapter(Context context, List<BadgesModel> items, Boolean delete_action) {
        this.items = items;
        this.context = context;
        this.delete_action =    delete_action;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new IMAGEViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_badges, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((IMAGEViewHolder) holder).onBind(holder.getAdapterPosition());

    }


    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }


    public void addItems(List<BadgesModel> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }


    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    BadgesModel getItem(int position) {
        return items.get(position);
    }

    public class IMAGEViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgView)
        ImageView imgView;

        @BindView(R.id.txt_post_title)
        TextViewBold txtPostTitle;

        IMAGEViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(int position) {
            // super.onBind(position);
            BadgesModel item = items.get(position);
            if (item.getBadgesDetails() != null) {
                ImageUtil.displayImage(imgView, item.getBadgesDetails().getIcon(), null);
            }
            txtPostTitle.setText(item.getBadgesDetails().getName());

        }
    }

}




