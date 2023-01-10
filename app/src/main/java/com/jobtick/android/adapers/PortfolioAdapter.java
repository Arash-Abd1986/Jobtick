package com.jobtick.android.adapers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jobtick.android.R;
import com.jobtick.android.models.PreviewCategoryModel;
import com.jobtick.android.models.response.PortfolioDataModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PortfolioAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final List<PortfolioDataModel> items;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, com.jobtick.android.models.response.PortfolioDataModel obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public PortfolioAdapter(List<PortfolioDataModel> items) {
        this.items = items;
    }

    public class OriginalViewHolder extends BaseViewHolder {

        TextView txtTitle, txtDescription, txtNumber;
        LinearLayout parent;

        public OriginalViewHolder(View v) {
            super(v);
            txtTitle = v.findViewById(R.id.titleTxt);
            txtDescription = v.findViewById(R.id.descriptionTxt);
            txtNumber = v.findViewById(R.id.numberTxt);
            parent = v.findViewById(R.id.parent);
        }

        @Override
        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
            txtTitle.setText(items.get(position).getTitle());
            txtDescription.setText(items.get(position).getDescription());
            if(items.get(position).getImg_count().equals("1"))
                txtNumber.setText(items.get(position).getImg_count() + " Item");
            else
                txtNumber.setText(items.get(position).getImg_count() + " Items");


            parent.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, items.get(position), position);
                }
            });
        }


    }


    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new PortfolioAdapter.OriginalViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_portfolio_items, parent, false));

    }


    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItems(List<PortfolioDataModel> mItems) {
        this.items.addAll(mItems);
        notifyDataSetChanged();
    }


    PortfolioDataModel getItem(int position) {
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