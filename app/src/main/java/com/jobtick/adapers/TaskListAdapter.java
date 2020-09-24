package com.jobtick.adapers;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.R;
import com.jobtick.TextView.TextViewMedium;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.TextView.TextViewSemiBold;
import com.jobtick.models.TaskModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ImageUtil;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TaskListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private Context context;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, TaskModel obj, int position, String action);
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }


    private boolean isLoaderVisible = false;
    private List<TaskModel> mItems;

    public TaskListAdapter(Context context, List<TaskModel> mItems) {
        this.mItems = mItems;
        this.context = context;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_view, parent, false));
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default:
                return null;
        }
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

    public void addItems(List<TaskModel> mItems) {
        this.mItems.addAll(mItems);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        this.mItems.add(new TaskModel());
        notifyItemInserted(this.mItems.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = mItems.size() - 1;
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
        @BindView(R.id.img_avatar)
        CircularImageView imgAvatar;
        @BindView(R.id.txt_offer_count)
        TextViewSemiBold txtOfferCount;
        @BindView(R.id.lyt_offer_count)
        LinearLayout lytOfferCount;
        @BindView(R.id.lyt_task_profile)
        LinearLayout lytTaskProfile;
        @BindView(R.id.txt_title)
        TextViewMedium txtTitle;
        @BindView(R.id.txt_location)
        TextViewRegular txtLocation;
        @BindView(R.id.txt_due_date)
        TextViewRegular txtDueDate;
        @BindView(R.id.txt_due_time)
        TextViewRegular txtDueTime;
        @BindView(R.id.txt_budget)
        TextViewSemiBold txtBudget;
        @BindView(R.id.txt_status)
        TextViewSemiBold txtStatus;
        @BindView(R.id.lyt_budget_open)
        LinearLayout lytBudgetOpen;
        @BindView(R.id.lyt_btn_parent_layout)
        LinearLayout lytBtnParentLayout;


        @BindView(R.id.card_task_background)
        CardView cardTaskBackground;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        protected void clear() {
        }


        public void onBind(int position) {
            super.onBind(position);
            TaskModel item = mItems.get(position);
            if (item.getPoster() != null && item.getPoster().getAvatar() != null && item.getPoster().getAvatar().getThumbUrl() != null)
                ImageUtil.displayImage(imgAvatar, item.getPoster().getAvatar().getThumbUrl(), null);
            txtTitle.setText(item.getTitle());
            txtDueDate.setText(item.getDueDate());
            txtDueTime.setText(null);
            if (item.getDueTime() != null) {

                if (item.getDueTime().getMorning()) {
                    if (TextUtils.isEmpty(txtDueTime.getText().toString())) {
                        txtDueTime.setText("Morning");
                    } else {
                        txtDueTime.append(", Morning");
                    }
                }
                if (item.getDueTime().getAfternoon()) {
                    if (TextUtils.isEmpty(txtDueTime.getText().toString())) {
                        txtDueTime.setText("Afternoon");
                    } else {
                        txtDueTime.append(", Afternoon");
                    }
                }
                if (item.getDueTime().getEvening()) {
                    if (TextUtils.isEmpty(txtDueTime.getText().toString())) {
                        txtDueTime.setText("Evening");
                    } else {
                        txtDueTime.append(", Evening");
                    }
                }
                if (item.getDueTime().getMidday()) {
                    if (TextUtils.isEmpty(txtDueTime.getText().toString())) {
                        txtDueTime.setText("Midday");
                    } else {
                        txtDueTime.append(", Midday");
                    }
                }
            }
            txtStatus.setText(item.getStatus().toUpperCase());

            if (item.getLocation() != null) {
                txtLocation.setText(item.getLocation());
            } else {
                txtLocation.setText("In Person");
            }
            if (item.getStatus().equalsIgnoreCase(Constant.TASK_DRAFT)) {
                // txtStatus.setVisibility(View.GONE);
                lytOfferCount.setVisibility(View.GONE);
                txtBudget.setText("$ " + item.getBudget());
            } else {
                //  txtStatus.setVisibility(View.VISIBLE);
                lytOfferCount.setVisibility(View.VISIBLE);
                if (item.getStatus().equalsIgnoreCase(Constant.TASK_OPEN)) {
                    txtBudget.setText("$ " + item.getBudget());
                } else {
                    txtBudget.setText("$ " + item.getAmount());
                }
            }
            if (item.getOfferCount() <= 1) {
                txtOfferCount.setText(item.getOfferCount() + " Offer");
            } else {
                txtOfferCount.setText(item.getOfferCount() + " Offers");
            }
            GradientDrawable backgroundGradient = (GradientDrawable) txtStatus.getBackground();

            if (item.getStatus().equals("draft")) {
                cardTaskBackground.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorTaskOffer));
                backgroundGradient.setColor(ContextCompat.getColor(context, R.color.colorTaskOffer));
                txtStatus.setBackground(backgroundGradient);
                txtStatus.setTextColor(ContextCompat.getColor(context, R.color.colorTaskOffer));

            } else if (item.getStatus().equals("open")) {

                cardTaskBackground.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorTaskOffer));
                backgroundGradient.setColor(ContextCompat.getColor(context, R.color.colorTaskOfferTrans));
                txtStatus.setBackground(backgroundGradient);
                txtStatus.setTextColor(ContextCompat.getColor(context, R.color.colorTaskOffer));

            } else if (item.getStatus().equals("assigned")) {

                cardTaskBackground.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorTaskAssigned));
                backgroundGradient.setColor(ContextCompat.getColor(context, R.color.colorTaskAssignedTrans));
                txtStatus.setTextColor(ContextCompat.getColor(context, R.color.colorTaskAssigned));
                txtStatus.setBackground(backgroundGradient);

            } else if (item.getStatus().equals("completed")) {

                cardTaskBackground.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorTaskCompleted));
                backgroundGradient.setColor(ContextCompat.getColor(context, R.color.colorTaskCompletedTrans));

                txtStatus.setBackground(backgroundGradient);
                txtStatus.setTextColor(ContextCompat.getColor(context, R.color.colorTaskCompleted));

            } else if (item.getStatus().equals("offered")) {
                cardTaskBackground.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorTaskOffer));
                backgroundGradient.setColor(ContextCompat.getColor(context, R.color.colorTaskOfferTrans));
                txtStatus.setBackground(backgroundGradient);
                txtStatus.setTextColor(ContextCompat.getColor(context, R.color.colorTaskOffer));

            } else if (item.getStatus().equals("closed")) {
                cardTaskBackground.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorTaskCompleted));
                backgroundGradient.setColor(ContextCompat.getColor(context, R.color.colorTaskCompletedTrans));

                txtStatus.setBackground(backgroundGradient);
                txtStatus.setTextColor(ContextCompat.getColor(context, R.color.colorTaskCompleted));

            } else if (item.getStatus().equals("cancelled")) {

                cardTaskBackground.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorTaskCancelled));
                backgroundGradient.setColor(ContextCompat.getColor(context, R.color.colorTaskCancelledTrans));
                txtStatus.setBackground(backgroundGradient);
                txtStatus.setTextColor(ContextCompat.getColor(context, R.color.colorTaskCancelled));
            }

            lytBtnParentLayout.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, item, getAdapterPosition(), "action");
                }
            });

        }
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
