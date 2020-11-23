package com.jobtick.adapers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jobtick.R;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.models.TaskCategory;

import java.util.HashMap;
import java.util.List;

public class TaskCategoryPreviewAdapter extends RecyclerView.Adapter<BaseViewHolder> {


    private List<TaskCategory> items;
    private HashMap<Integer, Boolean> hashMapSelected;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isLoaderVisible = false;
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, TaskCategory obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public TaskCategoryPreviewAdapter(Context context, List<TaskCategory> items) {
        this.items = items;
        ctx = context;
        this.hashMapSelected = new HashMap<>();
        for (int i = 0; items.size() > i; i++) {
            hashMapSelected.put(i, false);
        }
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
            txtName.setText(items.get(position).getName());


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

        return new TaskCategoryPreviewAdapter.OriginalViewHolder(
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