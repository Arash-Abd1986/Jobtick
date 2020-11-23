package com.jobtick.adapers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.internal.LinkedTreeMap;
import com.jobtick.R;
import java.util.List;

public abstract class PreviewAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    private final List<T> items;
    private OnItemClickListener<T> mOnItemClickListener;

    public interface OnItemClickListener<T> {
        void onItemClick(View view, T obj, int position);
    }

    public abstract String getPreviewParameterName();

    public void setOnItemClickListener(final OnItemClickListener<T> mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public PreviewAdapter(List<T> items) {
        this.items = items;
    }

    public class OriginalViewHolder extends BaseViewHolder {

        TextView txtName;
        RelativeLayout lytOuter;

        public OriginalViewHolder(View v) {
            super(v);
            txtName = v.findViewById(R.id.category_text);
            lytOuter = v.findViewById(R.id.category_container);
        }

        @Override
        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
            Object getrow = items.get(position);
            LinkedTreeMap<Object, Object> t = (LinkedTreeMap) getrow;
            String name = t.get(getPreviewParameterName()).toString();
            txtName.setText(name);

            lytOuter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(v, items.get(position), position);
                    }
                }
            });
        }


    }


    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new PreviewAdapter<T>.OriginalViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_search, parent, false));

    }


    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItems(List<T> mItems) {
        this.items.addAll(mItems);
        notifyDataSetChanged();
    }


    T getItem(int position) {
        return items.get(position);
    }

    public class ProgressHolder extends BaseViewHolder {
        ProgressHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void clear() {
        }
    }


}