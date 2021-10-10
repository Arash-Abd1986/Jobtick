package com.jobtick.android.adapers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jobtick.android.R;
import com.jobtick.android.models.TaskModel;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.ImageUtil;
import com.jobtick.android.utils.TimeHelper;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private Context context;
    private OnItemClickListener mOnItemClickListener;
    private OnDraftDeleteListener mOnDraftDeleteListener;

    private final Integer userId;

    public interface OnItemClickListener {
        void onItemClick(View view, TaskModel obj, int position, String action);

    }

    public interface OnDraftDeleteListener {

        void onDraftDeleteButtonClick(View view, TaskModel taskModel, int position);
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
    private final List<TaskModel> mItems;

    public TaskListAdapter(List<TaskModel> mItems, @Nullable Integer userId) {
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
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_view, parent, false));
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

    public void addItems(List<TaskModel> mItems, int allItems) {
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
        this.mItems.add(new TaskModel());
        notifyItemInserted(position);
    }

    private void removeLoading() {
        if (!isLoaderVisible) return;
        isLoaderVisible = false;
        int position = mItems.size() - 1;
        if (position == -1)
            return;
        TaskModel item = getItem(position);
        if (item != null) {
            mItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    TaskModel getItem(int position) {
        return mItems.get(position);
    }

    public class ViewHolder extends BaseViewHolder {
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_avatar3)
        CircularImageView imgAvatar3;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_avatar2)
        CircularImageView imgAvatar2;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_avatar1)
        CircularImageView imgAvatar1;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_offer_count)
        TextView txtOfferCount;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_title)
        TextView txtTitle;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_location)
        TextView txtLocation;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_due_date)
        TextView txtDueDate;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_budget)
        TextView txtBudget;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_status)
        TextView txtStatus;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_status_draft)
        TextView txtStatusDraft;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.tv_delete)
        TextView tvDelete;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.card_task_background)
        View cardTaskBackground;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.content)
        View content;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        protected void clear() {
        }

        @SuppressLint("SetTextI18n")
        public void onBind(int position) {
            super.onBind(position);
            TaskModel item = mItems.get(position);
            if (item.getPoster() != null && item.getPoster().getAvatar() != null && item.getPoster().getAvatar().getModalUrl() != null) {
//                ImageUtil.displayImage(imgAvatar, item.getPoster().getAvatar().getThumbUrl(), null);
//                Glide.with(imgAvatar).load(item.getPoster().getAvatar().getThumbUrl()).placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).into(imgAvatar);
                ImageUtil.displayImage(imgAvatar3, item.getPoster().getAvatar().getThumbUrl(), null);
                ImageUtil.displayImage(imgAvatar2, item.getPoster().getAvatar().getThumbUrl(), null);
                ImageUtil.displayImage(imgAvatar1, item.getPoster().getAvatar().getThumbUrl(), null);
            } else {
                imgAvatar3.setImageResource(R.drawable.pic);
            }

            txtTitle.setText(item.getTitle());
            txtDueDate.setText(TimeHelper.convertToWeekDateFormat(item.getDueDate()));

            if (item.getLocation() != null && item.getTaskType() != null && item.getTaskType().equals("physical")) {
                txtLocation.setText(item.getLocation());
            } else if (item.getTaskType() != null && item.getTaskType().equals("remote")) {
                txtLocation.setText("Remote job");
            } else {
                txtLocation.setText("No location set");
            }
            if (item.getStatus() != null && item.getStatus().equalsIgnoreCase(Constant.TASK_DRAFT)) {
                // txtStatus.setVisibility(View.GONE);
                txtBudget.setText("$" + item.getBudget());
            } else {
                //  txtStatus.setVisibility(View.VISIBLE);
                if (item.getStatus() != null && item.getStatus().equalsIgnoreCase(Constant.TASK_OPEN)) {
                    txtBudget.setText("$" + item.getBudget());
                } else {
                    txtBudget.setText("$" + item.getAmount());
                }
            }

            if (item.getOfferCount() == null)
                txtOfferCount.setVisibility(View.GONE);
            else {
                if (item.getOfferCount() >= 1) {
                    txtOfferCount.setVisibility(View.VISIBLE);
                    txtOfferCount.setText(item.getOfferCount().toString());
                } else {
                    txtOfferCount.setVisibility(View.GONE);
                }
            }
            if (item.getStatus() != null) {
                setColors(item);
                setStatusText(item);
            }
            content.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, item, getAdapterPosition(), "action");
                }
            });

            tvDelete.setOnClickListener(v -> {
                if (mOnDraftDeleteListener != null) {
                    mOnDraftDeleteListener.onDraftDeleteButtonClick(v, item, position);
                }
            });
        }


        private void setColors(TaskModel item) {
            switch (item.getStatus()) {
                case "draft":
                    txtStatus.setVisibility(View.VISIBLE);
                    txtStatusDraft.setVisibility(View.GONE);
                    tvDelete.setVisibility(View.GONE);
                    cardTaskBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_rounded_draft));
                    txtStatus.setTextColor(ContextCompat.getColor(context, R.color.N080));
                    break;
                case "open":
                case "posted":
                case "offered":
                    txtStatus.setVisibility(View.VISIBLE);
                    txtStatusDraft.setVisibility(View.GONE);
                    tvDelete.setVisibility(View.GONE);
                    cardTaskBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_rounded_offered));
                    txtStatus.setTextColor(ContextCompat.getColor(context, R.color.myJobsColorTaskOfferTrans));
                    break;
                case "assigned":
                case "overdue":
                    txtStatus.setVisibility(View.VISIBLE);
                    txtStatusDraft.setVisibility(View.GONE);
                    tvDelete.setVisibility(View.GONE);
                    cardTaskBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_rounded_overdue));
                    txtStatus.setTextColor(ContextCompat.getColor(context, R.color.myJobsColorTaskAssignedTrans));
                    break;
                case "completed":
                case "closed":
                    txtStatus.setVisibility(View.VISIBLE);
                    txtStatusDraft.setVisibility(View.GONE);
                    tvDelete.setVisibility(View.GONE);
                    cardTaskBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_rounded_closed));
                    txtStatus.setTextColor(ContextCompat.getColor(context, R.color.myJobsColorTaskCompletedTrans));
                    break;
                case "cancelled":
                    txtStatus.setVisibility(View.VISIBLE);
                    txtStatusDraft.setVisibility(View.GONE);
                    tvDelete.setVisibility(View.GONE);
                    cardTaskBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_rounded_cancelled));
                    txtStatus.setTextColor(ContextCompat.getColor(context, R.color.myJobsColorTaskCancelledTrans));
                    break;
            }
        }

        private void setStatusText(TaskModel item) {
            if (userId != null && item.getPoster() != null && item.getPoster().getId() != null &&
                    item.getPoster().getId().equals(userId) && item.getStatus().equals("open")) {
                txtStatus.setText("Posted");
            } else if (userId != null && item.getStatus() != null && item.getStatus().equals("open")) {
                txtStatus.setText("Offered");
            } else if (item.getStatus().equals("assigned") || item.getStatus().equals("overdue")) {
                txtStatus.setText("Assigned");
            } else if (item.getStatus().equals("completed")) {
                txtStatus.setText("Completed");
            } else if (item.getStatus().equals("closed")) {
                txtStatus.setText("Closed");
            } else if (item.getStatus().equals("cancelled")) {
                txtStatus.setText("Cancelled");
            } else if (item.getStatus().equals("draft")) {
                txtStatus.setText("Drafted");
            } else if (item.getStatus().equalsIgnoreCase("open")) {
                txtStatus.setText("Open");
            } else {
                txtStatus.setText(item.getStatus());
            }
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
