package com.jobtick.adapers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.R;
import com.jobtick.TextView.TextViewMedium;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.models.TaskCategory;
import com.jobtick.models.task.TaskAlert;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskAlertAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<TaskAlert> items = new ArrayList<>();
    private OnLoadMoreListener onLoadMoreListener;

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, TaskAlert obj, int position, String action);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public TaskAlertAdapter(Context context, List<TaskAlert> items) {
        this.items = items;
        ctx = context;

    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_minus)
        ImageView imgMinus;
        @BindView(R.id.txt_keyword)
        TextViewMedium txtKeyword;
        @BindView(R.id.txt_type_detail)
        TextViewRegular txtTypeDetail;
        @BindView(R.id.lyt_item_keyword)
        LinearLayout lytItemKeyword;

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
            TaskAlert taskAlert = items.get(position);

            view.txtKeyword.setText(taskAlert.getKetword());

            if (taskAlert.getAlert_type().equalsIgnoreCase("remote")) {
                view.txtTypeDetail.setText("Remote Task");
            } else {
                view.txtTypeDetail.setText(taskAlert.getSuburb() + ", " + taskAlert.getDistance() + " KM Radius");
            }

            view.imgMinus.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, items.get(view.getAdapterPosition()), view.getAdapterPosition(), "delete");
                }
            });

            view.lytItemKeyword.setOnClickListener(v -> {
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


    public void addItems(List<TaskAlert> mItems) {
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