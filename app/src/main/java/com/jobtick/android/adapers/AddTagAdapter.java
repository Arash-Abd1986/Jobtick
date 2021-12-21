package com.jobtick.android.adapers;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.android.R;
import com.jobtick.android.utils.MyExtensions;

import android.annotation.SuppressLint;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddTagAdapter extends RecyclerView.Adapter<AddTagAdapter.ViewHolder> {

    private List<String> items;
    private Boolean isAdded;
    private final OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(String data);
    }

    public AddTagAdapter(List<String> items, Boolean isAdded, OnItemClickListener mItemClickListener) {
        this.isAdded = isAdded;
        this.items = items;
        this.mOnItemClickListener = mItemClickListener;
    }

    public void updateItem(List<String> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_btn_remove)
        ImageView imgBtnRemove;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_btn_add_must_have)
        TextView txtBtnAddMustHave;
        @BindView(R.id.top_cn)
        ConstraintLayout top_cn;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_must_have, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String data = items.get(position);
        holder.txtBtnAddMustHave.setText(data);
        holder.imgBtnRemove.setOnClickListener(v -> mOnItemClickListener.onItemClick(data));
        if (isAdded){
            MyExtensions.setBackgroundShape(holder.top_cn, ContextCompat.getColor(holder.itemView.getContext(), R.color.P075),
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.P075),
                    8,
                    0,
                    GradientDrawable.RECTANGLE);
            holder.imgBtnRemove.setImageResource(R.drawable.ic_close_v4);
        }else{
            holder.txtBtnAddMustHave.setOnClickListener(v -> mOnItemClickListener.onItemClick(data));
            holder.imgBtnRemove.setImageResource(R.drawable.ic_btn_plus_gray);
            MyExtensions.setBackgroundShape(holder.top_cn, ContextCompat.getColor(holder.itemView.getContext(), R.color.N30),
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.N30),
                    8,
                    0,
                    GradientDrawable.RECTANGLE);        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}