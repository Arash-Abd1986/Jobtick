package com.jobtick.android.adapers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.android.R;

import android.annotation.SuppressLint;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddTagAdapter extends RecyclerView.Adapter<AddTagAdapter.ViewHolder> {

    private List<String> items;
    private Boolean isV3;
    private final OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(String data);
    }

    public AddTagAdapter(List<String> items, Boolean isV3, OnItemClickListener mItemClickListener) {
        this.items = items;
        this.mOnItemClickListener = mItemClickListener;
        this.isV3 = isV3;
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

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isV3)
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_must_have_v2, parent, false));
        else
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_must_have_v1, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String data = items.get(position);
        holder.txtBtnAddMustHave.setText(data);
        holder.imgBtnRemove.setOnClickListener(v -> mOnItemClickListener.onItemClick(data));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}