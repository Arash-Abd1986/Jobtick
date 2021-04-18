package com.jobtick.android.adapers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.android.R;
import com.jobtick.android.models.response.myjobs.Data;
import com.jobtick.android.utils.ImageUtil;
import com.jobtick.android.utils.TimeHelper;
import com.jobtick.android.utils.Tools;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskListAdapterV2 extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private Context context;
    private OnItemClickListener mOnItemClickListener;
    private OnDraftDeleteListener mOnDraftDeleteListener;

    private Integer userId;

    public interface OnItemClickListener {
        void onItemClick(View view, Data obj, int position, String action);

    }

    public interface OnDraftDeleteListener {

        void onDraftDeleteButtonClick(View view, Data taskModel, int position);
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
    private final List<Data> mItems;

    public TaskListAdapterV2(List<Data> mItems, @Nullable Integer userId) {
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

    public void addItems(List<Data> mItems, int allItems) {
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
        this.mItems.add(new Data(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,null, null));
        notifyItemInserted(position);
    }

    private void removeLoading() {
        if (!isLoaderVisible) return;
        isLoaderVisible = false;
        int position = mItems.size() - 1;
        if (position == -1)
            return;
        Data item = getItem(position);
        if (item != null) {
            mItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    Data getItem(int position) {
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
            Data item = mItems.get(position);
            if (item.getOffers() != null)
                if (item.getOffers().size() > 0) {
                    txtOfferCount.setTextColor(ContextCompat.getColor(context, R.color.N900));
                    if (item.getOffers().size() >= 3) {
                        imgAvatar3.setVisibility(View.VISIBLE);
                        imgAvatar2.setVisibility(View.VISIBLE);
                        imgAvatar1.setVisibility(View.VISIBLE);

                        if (item.getOffers().get(2).getAvatar() != null)
                            ImageUtil.displayImage(imgAvatar3, item.getOffers().get(2).getAvatar(), null);
                        else
                            imgAvatar3.setImageResource(R.drawable.pic);
                        if (item.getOffers().get(1).getAvatar() != null)
                            ImageUtil.displayImage(imgAvatar2, item.getOffers().get(1).getAvatar(), null);
                        else
                            imgAvatar2.setImageResource(R.drawable.pic);

                        if (item.getOffers().get(0).getAvatar() != null)
                            ImageUtil.displayImage(imgAvatar1, item.getOffers().get(0).getAvatar(), null);
                        else
                            imgAvatar1.setImageResource(R.drawable.pic);

                    } else if (item.getOffers().size() == 2) {
                        imgAvatar1.setVisibility(View.GONE);
                        imgAvatar2.setVisibility(View.VISIBLE);
                        imgAvatar3.setVisibility(View.VISIBLE);

                        if (item.getOffers().get(1).getAvatar() != null)
                            ImageUtil.displayImage(imgAvatar2, item.getOffers().get(1).getAvatar(), null);
                        else
                            imgAvatar2.setImageResource(R.drawable.pic);

                        if (item.getOffers().get(0).getAvatar() != null)
                            ImageUtil.displayImage(imgAvatar3, item.getOffers().get(0).getAvatar(), null);
                        else
                            imgAvatar3.setImageResource(R.drawable.pic);
                    } else if (item.getOffers().size() == 1) {
                        imgAvatar1.setVisibility(View.GONE);
                        imgAvatar2.setVisibility(View.GONE);
                        imgAvatar3.setVisibility(View.VISIBLE);
                        if (item.getOffers().get(0).getAvatar() != null)
                            ImageUtil.displayImage(imgAvatar3, item.getOffers().get(0).getAvatar(), null);
                        else
                            imgAvatar3.setImageResource(R.drawable.pic);
                    }
                } else {
                    txtOfferCount.setTextColor(ContextCompat.getColor(context, R.color.N300));
                    imgAvatar3.setVisibility(View.GONE);
                    imgAvatar2.setVisibility(View.GONE);
                    imgAvatar1.setVisibility(View.GONE);
                    //imgAvatar3.setImageResource(R.drawable.pic);
                    txtOfferCount.setText("No offer yet");
                }
            else {
                imgAvatar3.setVisibility(View.GONE);
                imgAvatar2.setVisibility(View.GONE);
                imgAvatar1.setVisibility(View.GONE);
                txtOfferCount.setText("No offer yet");
                txtOfferCount.setTextColor(ContextCompat.getColor(context, R.color.N300));
                //imgAvatar3.setImageResource(R.drawable.pic);
            }


            txtTitle.setText(item.getTitle());
            txtDueDate.setText(TimeHelper.convertToWeekDateFormat(item.getDue_date()));

            if (item.getLocation() != null && item.getStatus() != null && item.getStatus().equals("physical")) {
                txtLocation.setText(item.getLocation());
            } else if (item.getStatus() != null && item.getStatus().equals("remote")) {
                txtLocation.setText("Remote job");
            } else {
                txtLocation.setText("No location set");
            }
            if (item.getBudget() != null) {
                txtBudget.setText("$" + item.getBudget());
            } else
                txtBudget.setText("$");

            if (item.getOffers() != null) {
                int count = item.getOffers().size();
                if (count >= 1) {
                    txtOfferCount.setVisibility(View.VISIBLE);
                    txtOfferCount.setText(count + "");
                    if (count > 1000) {
                        txtOfferCount.setText("+" + count / 1000 + "K");
                    } else if (count > 100) {
                        txtOfferCount.setText("+" + (count / 100) * 100);
                    } else if (count > 10) {
                        txtOfferCount.setText("+" + (count / 10) * 10);
                    }
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


        private void setColors(Data item) {
            switch (item.getStatus()) {
                case "draft":
                    txtStatus.setVisibility(View.VISIBLE);
                    txtStatusDraft.setVisibility(View.GONE);
                    tvDelete.setVisibility(View.GONE);
                    cardTaskBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_rounded_draft));
                    txtStatus.setTextColor(ContextCompat.getColor(context, R.color.P100));
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
                    txtStatus.setVisibility(View.VISIBLE);
                    txtStatusDraft.setVisibility(View.GONE);
                    tvDelete.setVisibility(View.GONE);
                    cardTaskBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_rounded_completed));
                    txtStatus.setTextColor(ContextCompat.getColor(context, R.color.G400));
                    break;
                case "closed":
                    txtStatus.setVisibility(View.VISIBLE);
                    txtStatusDraft.setVisibility(View.GONE);
                    tvDelete.setVisibility(View.GONE);
                    cardTaskBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_rounded_closed));
                    txtStatus.setTextColor(ContextCompat.getColor(context, R.color.N080));
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

        private void setStatusText(Data item) {
//            if (userId != null && item.getPoster() != null && item.getPoster().getId() != null &&
//                    item.getPoster().getId().equals(userId) && item.getStatus().equals("open")) {
//                txtStatus.setText("Posted");
//            } else


            if (userId != null && userId.equals(item.getPoster_id()) && item.getStatus().equals("open")) {
                txtStatus.setText("Posted");
            }
            else if (userId != null && item.getStatus().equals("open")) {
                txtStatus.setText("Open");
            } else if (item.getStatus().equalsIgnoreCase("Offered")) {
                txtStatus.setText("Offered");
            } else if (item.getStatus().equalsIgnoreCase("posted")) {
                txtStatus.setText("Posted");
            } else if (item.getStatus().equalsIgnoreCase("assigned")) {
                txtStatus.setText("Assigned");
            } else if (item.getStatus().equalsIgnoreCase("overdue")) {
                txtStatus.setText("Overdue");
            } else if (item.getStatus().equalsIgnoreCase("completed")) {
                txtStatus.setText("Completed");
            } else if (item.getStatus().equalsIgnoreCase("closed")) {
                txtStatus.setText("Closed");
            } else if (item.getStatus().equalsIgnoreCase("cancelled")) {
                txtStatus.setText("Cancelled");
            } else if (item.getStatus().equals("draft")) {
                txtStatus.setText("Drafted");
                if (item.getUpdated_at() != null) {
                    try {
                        txtOfferCount.setText("Last edited: " + Tools.formatJobDetailsDateV2(Tools.chatDateToMillisZ(item.getUpdated_at())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        txtOfferCount.setText("");
                    }
                }
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
