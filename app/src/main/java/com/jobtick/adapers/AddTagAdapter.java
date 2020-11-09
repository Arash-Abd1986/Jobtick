package com.jobtick.adapers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.R;
import com.jobtick.TextView.TextViewRegular;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddTagAdapter extends RecyclerView.Adapter<AddTagAdapter.ViewHolder> {

    private List<String> items;
    private final OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(String data);
    }

    public AddTagAdapter(List<String> items, OnItemClickListener mItemClickListener) {
        this.items = items;
        this.mOnItemClickListener = mItemClickListener;
    }

    public void updateItem(List<String> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_btn_remove)
        ImageView imgBtnRemove;
        @BindView(R.id.txt_btn_add_must_have)
        TextView txtBtnAddMustHave;

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
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}