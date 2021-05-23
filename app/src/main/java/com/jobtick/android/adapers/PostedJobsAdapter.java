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
import com.jobtick.android.models.response.home.PostedJob;
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

public class PostedJobsAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private Context context;
    private OnItemClickListener mOnItemClickListener;
    private OnDraftDeleteListener mOnDraftDeleteListener;

    private Integer userId;

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

    public PostedJobsAdapter(List<PostedJob> mItems, @Nullable Integer userId) {
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
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_posted_job_view, parent, false));
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
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_avatar3)
        CircularImageView imgAvatar3;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.sep_line)
        View sepLine;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_avatar2)
        CircularImageView imgAvatar2;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_avatar1)
        CircularImageView imgAvatar1;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_avatar0)
        CircularImageView imgAvatar0;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_offer_count)
        TextView txtOfferCount;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_title)
        TextView txtTitle;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_budget)
        TextView txtBudget;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_status)
        TextView txtStatus;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.card_task_background)
        RelativeLayout cardTaskBackground;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.rl_images)
        RelativeLayout rlImages;
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
            PostedJob item = mItems.get(position);
            if (position == mItems.size() -1){
                sepLine.setVisibility(View.GONE);
            }else{
                sepLine.setVisibility(View.VISIBLE);
            }
            if (item.getOffers() != null)
                if (item.getOffers().size() > 0) {
                    rlImages.setVisibility(View.VISIBLE);
                    txtOfferCount.setTextColor(ContextCompat.getColor(context, R.color.N900));
                    if (item.getOffers().size() >= 4) {
                        imgAvatar3.setVisibility(View.VISIBLE);
                        imgAvatar2.setVisibility(View.VISIBLE);
                        imgAvatar1.setVisibility(View.VISIBLE);
                        imgAvatar0.setVisibility(View.VISIBLE);

                        if (item.getOffers().get(3).getAvatar() != null)
                            ImageUtil.displayImage(imgAvatar3, item.getOffers().get(3).getAvatar(), null);
                        else
                            imgAvatar3.setImageResource(R.drawable.pic);

                        if (item.getOffers().get(2).getAvatar() != null)
                            ImageUtil.displayImage(imgAvatar2, item.getOffers().get(2).getAvatar(), null);
                        else
                            imgAvatar2.setImageResource(R.drawable.pic);

                        if (item.getOffers().get(1).getAvatar() != null)
                            ImageUtil.displayImage(imgAvatar1, item.getOffers().get(1).getAvatar(), null);
                        else
                            imgAvatar1.setImageResource(R.drawable.pic);

                        if (item.getOffers().get(0).getAvatar() != null)
                            ImageUtil.displayImage(imgAvatar0, item.getOffers().get(0).getAvatar(), null);
                        else
                            imgAvatar0.setImageResource(R.drawable.pic);

                    } else if (item.getOffers().size() == 3) {
                        imgAvatar0.setVisibility(View.GONE);
                        imgAvatar1.setVisibility(View.VISIBLE);
                        imgAvatar2.setVisibility(View.VISIBLE);
                        imgAvatar3.setVisibility(View.VISIBLE);

                        if (item.getOffers().get(2).getAvatar() != null)
                            ImageUtil.displayImage(imgAvatar1, item.getOffers().get(2).getAvatar(), null);
                        else
                            imgAvatar1.setImageResource(R.drawable.pic);

                        if (item.getOffers().get(1).getAvatar() != null)
                            ImageUtil.displayImage(imgAvatar2, item.getOffers().get(1).getAvatar(), null);
                        else
                            imgAvatar2.setImageResource(R.drawable.pic);

                        if (item.getOffers().get(0).getAvatar() != null)
                            ImageUtil.displayImage(imgAvatar3, item.getOffers().get(0).getAvatar(), null);
                        else
                            imgAvatar3.setImageResource(R.drawable.pic);
                    } else if (item.getOffers().size() == 2) {
                        imgAvatar0.setVisibility(View.GONE);
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
                        imgAvatar0.setVisibility(View.GONE);
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
                    imgAvatar0.setVisibility(View.GONE);
                    //imgAvatar3.setImageResource(R.drawable.pic);
                    rlImages.setVisibility(View.GONE);
                    txtOfferCount.setText("");
                }
            else {
                imgAvatar3.setVisibility(View.GONE);
                imgAvatar2.setVisibility(View.GONE);
                imgAvatar1.setVisibility(View.GONE);
                imgAvatar0.setVisibility(View.GONE);
                rlImages.setVisibility(View.GONE);
                txtOfferCount.setText("");
                txtOfferCount.setTextColor(ContextCompat.getColor(context, R.color.N300));
                //imgAvatar3.setImageResource(R.drawable.pic);
            }


            txtTitle.setText(item.getTitle());

            if (item.getAmount() != null) {
                txtBudget.setText("$" + item.getAmount());
            } else
                txtBudget.setText("$");

            if (item.getOffers() != null) {
                int count = item.getOffers().size();
                if (count >= 1) {
                    if (item.getOffers().size() < 5)
                        txtOfferCount.setVisibility(View.GONE);
                    else
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

        }


        private void setColors(PostedJob item) {
            switch (item.getStatus()) {
                case "draft":
                    txtStatus.setVisibility(View.VISIBLE);
                    cardTaskBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_rounded_draft));
                    txtStatus.setTextColor(ContextCompat.getColor(context, R.color.P100));
                    break;
                case "open":
                case "posted":
                case "offered":
                    txtStatus.setVisibility(View.VISIBLE);
                    cardTaskBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_rounded_offered_half));
                    txtStatus.setTextColor(ContextCompat.getColor(context, R.color.myJobsColorTaskOfferTrans));
                    break;
                case "assigned":
                case "overdue":
                    txtStatus.setVisibility(View.VISIBLE);
                    cardTaskBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_rounded_overdue_half));
                    txtStatus.setTextColor(ContextCompat.getColor(context, R.color.myJobsColorTaskAssignedTrans));
                    break;
                case "completed":
                    txtStatus.setVisibility(View.VISIBLE);
                    cardTaskBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_rounded_completed_half));
                    txtStatus.setTextColor(ContextCompat.getColor(context, R.color.G400));
                    break;
                case "closed":
                    txtStatus.setVisibility(View.VISIBLE);
                    cardTaskBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_rounded_closed_half));
                    txtStatus.setTextColor(ContextCompat.getColor(context, R.color.N080));
                    break;
                case "cancelled":
                    txtStatus.setVisibility(View.VISIBLE);
                    cardTaskBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_rounded_cancelled_half));
                    txtStatus.setTextColor(ContextCompat.getColor(context, R.color.myJobsColorTaskCancelledTrans));
                    break;
            }
        }

        private void setStatusText(PostedJob item) {
//            if (userId != null && item.getPoster() != null && item.getPoster().getId() != null &&
//                    item.getPoster().getId().equals(userId) && item.getStatus().equals("open")) {
//                txtStatus.setText("Posted");
//            } else


            if (userId != null && userId.equals(item.getPoster_id()) && item.getStatus().equals("open")) {
                txtStatus.setText("Posted");
            } else if (userId != null && item.getStatus().equals("open")) {
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
