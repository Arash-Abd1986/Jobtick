package com.jobtick.android.adapers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.android.R;
import com.jobtick.android.models.response.home.OfferedJob;
import com.jobtick.android.models.response.home.PostedJob;
import com.jobtick.android.utils.ImageUtil;
import com.jobtick.android.utils.TimeHelper;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.rishabhharit.roundedimageview.RoundedImageView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecommendedJobsAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private Context context;
    private OnItemClickListener mOnItemClickListener;
    private OnDraftDeleteListener mOnDraftDeleteListener;

    private final Integer userId;

    public interface OnItemClickListener {
        void onItemClick(View view, PostedJob obj, int position, String action);

    }

    public interface OnDraftDeleteListener {

        void onDraftDeleteButtonClick(View view, PostedJob taskModel, int position);
    }

    public OnDraftDeleteListener getOnDraftDeleteListener() {
        return mOnDraftDeleteListener;
    }

    public void setOnDraftDeleteListener(OnDraftDeleteListener mOnDraftDeleteListener) {
        this.mOnDraftDeleteListener = mOnDraftDeleteListener;
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    private boolean isLoaderVisible = false;
    private final List<PostedJob> mItems;

    public RecommendedJobsAdapter(List<PostedJob> mItems, @Nullable Integer userId) {
        this.mItems = mItems;
        this.userId = userId;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == VIEW_TYPE_LOADING) {
            return new ProgressHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
        }
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rcommended_job_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == mItems.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    public void addItems(List<PostedJob> mItems, int allItems) {
        removeLoading();
        this.mItems.addAll(mItems);
        notifyDataSetChanged();
        if (this.mItems.size() < allItems) {
            addLoading();
        }
    }

    private void addLoading() {
        if (isLoaderVisible) return;
        isLoaderVisible = true;
        int position = mItems.size() - 1;
        this.mItems.add(new PostedJob(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null));
        notifyItemInserted(position);
    }

    private void removeLoading() {
        if (!isLoaderVisible) return;
        isLoaderVisible = false;
        int position = mItems.size() - 1;
        if (position == -1)
            return;
        PostedJob item = getItem(position);
        if (item != null) {
            mItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    PostedJob getItem(int position) {
        return mItems.get(position);
    }

    public class ViewHolder extends BaseViewHolder {

        @BindView(R.id.sep_line)
        View sepLine;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_avatar)
        RoundedImageView imgAvatar;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_title)
        TextView txtTitle;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.card_task_background)
        RelativeLayout cardTaskBackground;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.content)
        View content;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_date)
        TextView txtDate;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_budget)
        TextView txtBudget;
        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        protected void clear() {
        }

        @SuppressLint("SetTextI18n")
        public void onBind(int position) {
            super.onBind(position);
            PostedJob item = mItems.get(position);
            if (position == mItems.size() -1){
                sepLine.setVisibility(View.GONE);
            }else{
                sepLine.setVisibility(View.VISIBLE);
            }

            ImageUtil.displayImage(imgAvatar, item.getPoster_avatar(), null);
            txtTitle.setText(item.getTitle());

            if (item.getDue_date() != null) {
                txtDate.setText(TimeHelper.convertToWeekDateFormatV2(item.getDue_date()));
            } else {
                txtDate.setText("");
            }
            if (item.getBudget() != null) {
                txtBudget.setText("$" + item.getBudget());
            } else
                txtBudget.setText("$");

            content.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, item, getAdapterPosition(), "action");
                }
            });

        }

    }

    public static class ProgressHolder extends BaseViewHolder {
        ProgressHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void clear() {
        }
    }
}
