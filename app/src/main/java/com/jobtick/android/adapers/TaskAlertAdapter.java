package com.jobtick.android.adapers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.android.R;
import android.annotation.SuppressLint;

import com.jobtick.android.models.response.jobalerts.Data;
import com.jobtick.android.models.task.TaskAlert;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskAlertAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<Data> items = new ArrayList<>();
    private OnLoadMoreListener onLoadMoreListener;

    private final Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Data obj, int position, String action);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public TaskAlertAdapter(Context context, List<Data> items) {
        this.items = items;
        ctx = context;

    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_minus)
        ImageView imgMinus;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_keyword)
        TextView txtKeyword;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_type_detail)
        TextView txtTypeDetail;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.item_keyword)
        RelativeLayout itemKeyword;

        public OriginalViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_alert, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;
            Data taskAlert = items.get(position);

            view.txtKeyword.setText(taskAlert.getText());

            if (taskAlert.getType().equalsIgnoreCase("remote")) {
                view.txtTypeDetail.setText(R.string.remote_task);
            } else {
                view.txtTypeDetail.setText(String.format(Locale.ENGLISH, "%s, %dKM Radius", taskAlert.getLocation(), taskAlert.getDistance()));
            }

            view.imgMinus.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, items.get(view.getAdapterPosition()), view.getAdapterPosition(), "delete");
                }
            });

            view.itemKeyword.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, items.get(view.getAdapterPosition()), view.getAdapterPosition(), "update");
                }
            });
        }
    }

    public void removeItems(int position)
    {
        this.items.remove(position);
        notifyDataSetChanged();
    }


    public void addItems(List<Data> mItems) {
        this.items.clear();
        this.items.addAll(mItems);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }

}