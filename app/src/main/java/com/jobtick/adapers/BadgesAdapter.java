package com.jobtick.adapers;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.R;
import com.jobtick.text_view.TextViewBold;
import com.jobtick.models.BadgesModel;
import com.jobtick.utils.ImageUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class BadgesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<BadgesModel> items;

    public BadgesAdapter(List<BadgesModel> items) {
        this.items = items;
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
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.imgView)
        ImageView imgView;

        @SuppressLint("NonConstantResourceId")
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




