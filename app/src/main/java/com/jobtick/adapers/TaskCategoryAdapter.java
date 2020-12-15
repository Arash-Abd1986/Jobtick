package com.jobtick.adapers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jobtick.R;
import com.jobtick.text_view.TextViewRegular;
import com.jobtick.models.TaskCategory;


import java.util.HashMap;
import java.util.List;

public class TaskCategoryAdapter extends RecyclerView.Adapter<BaseViewHolder> {


    private final List<TaskCategory> items;
    private final HashMap<Integer, Boolean> hashMapSelected;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isLoaderVisible = false;
    private final Context ctx;
    private OnItemClickListener mOnItemClickListener;
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;


    public interface OnItemClickListener {
        void onItemClick(View view, TaskCategory obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public TaskCategoryAdapter(Context context, List<TaskCategory> items) {
        this.items = items;
        ctx = context;
        this.hashMapSelected = new HashMap<>();
        for (int i = 0; items.size() > i; i++) {
            hashMapSelected.put(i, false);
        }
    }

    public class OriginalViewHolder extends BaseViewHolder {

        ImageView imgView;
        TextViewRegular txtName;
        RelativeLayout lytOuter;

        public OriginalViewHolder(View v) {
            super(v);
            imgView = v.findViewById(R.id.img_view);
            txtName = v.findViewById(R.id.txt_name);
            lytOuter = v.findViewById(R.id.lyt_outer);
        }

        @Override
        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);

            txtName.setText(items.get(position).getName());
            Glide.with(ctx).load(items.get(position).getIcon()).into(imgView);

           /* if (items.get(position).getIcon() != null && !items.get(position).getIcon().equals("")) {
                ImageUtil.displayImage(imgView, items.get(position).getIcon(), null);

            }*/
            lytOuter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(v, items.get(position), position);
                    }
                }
            });

/*            imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        makeAllUnselect(position);
                        notifyDataSetChanged();
                        mOnItemClickListener.onItemClick(v, items.get(position), position);
                    }
                }
            });*/
        }


    }


    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new TaskCategoryAdapter.OriginalViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_category, parent, false));
            case VIEW_TYPE_LOADING:
                return new TaskCategoryAdapter.ProgressHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default:
                return null;
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == items.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

/*    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_category, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }*/


    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public void makeAllUnselect(int position) {
        hashMapSelected.put(position, true);
        for (int i = 0; i < hashMapSelected.size(); i++) {
            if (i != position)
                hashMapSelected.put(i, false);
        }
    }

    public void allUnselect() {
        for (int i = 0; i < hashMapSelected.size(); i++) {
            hashMapSelected.put(i, false);
        }
    }

    public void addItems(List<TaskCategory> mItems) {
        this.items.addAll(mItems);
        notifyDataSetChanged();
    }


    public void addLoading() {
        isLoaderVisible = true;
        this.items.add(new TaskCategory());
        notifyItemInserted(this.items.size() - 1);
    }


    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = items.size() - 1;
        TaskCategory item = getItem(position);
        if (item != null) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    TaskCategory getItem(int position) {
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