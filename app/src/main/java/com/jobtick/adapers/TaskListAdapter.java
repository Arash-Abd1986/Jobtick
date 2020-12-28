package com.jobtick.adapers;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.models.TaskModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ImageUtil;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private Context context;
    private OnItemClickListener mOnItemClickListener;
    private OnDraftDeleteListener mOnDraftDeleteListener;

    private Integer userId;

    public interface OnItemClickListener {
        void onItemClick(View view, TaskModel obj, int position, String action);

    }
    public interface OnDraftDeleteListener{

        void onDraftDeleteButtonClick(View view, TaskModel taskModel);
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

    public void addItems(List<TaskModel> mItems) {
        this.mItems.addAll(mItems);
        notifyDataSetChanged();
    }

    public void addLoading() {
        if(isLoaderVisible) return;
        isLoaderVisible = true;
        this.mItems.add(new TaskModel());
        notifyItemInserted(this.mItems.size() - 1);
    }

    public void removeLoading() {
        if(!isLoaderVisible) return;
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
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_avatar)
        CircularImageView imgAvatar;
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
        @BindView(R.id.txt_due_time)
        TextView txtDueTime;
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
        ImageView tvDelete;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.card_task_background)
        CardView cardTaskBackground;

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
            if (item.getPoster() != null && item.getPoster().getAvatar() != null && item.getPoster().getAvatar() != null)
                ImageUtil.displayImage(imgAvatar, item.getPoster().getAvatar().getUrl(), null);
            txtTitle.setText(item.getTitle());

            if (item.getDueDate() != null && !item.getDueDate().equals("")) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = format.parse(item.getDueDate());

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM dd");
                    assert date != null;
                    String dayOfTheWeek = sdf.format(date);
                    txtDueDate.setText(dayOfTheWeek);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (item.getDueTime() != null) {
                txtDueTime.setText("");
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
                if (item.getDueTime().getMidday()!=null && item.getDueTime().getMidday()) {
                    if (TextUtils.isEmpty(txtDueTime.getText().toString())) {
                        txtDueTime.setText("Any time");
                    } else {
                        txtDueTime.append(", Any time");
                    }
                }
                if (!item.getDueTime().getMorning() && !item.getDueTime().getAfternoon() &&
                        !item.getDueTime().getEvening() && item.getDueTime().getMidday()!=null && !item.getDueTime().getMidday()) {
                    txtDueTime.setText("No time set");
                }
            } else {
                txtDueTime.setText("No time set");
            }
            if (item.getLocation() != null && item.getTaskType().equals("physical")) {
                txtLocation.setText(item.getLocation());
            } else if(item.getTaskType()!=null && item.getTaskType().equals("remote")){
                txtLocation.setText("Remote job");
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

            if (item.getOfferCount() != null && item.getOfferCount() == 0) {
                txtOfferCount.setVisibility(View.GONE);
            } else {
                txtOfferCount.setVisibility(View.VISIBLE);
            }
            if (item.getOfferCount() != null && item.getOfferCount() <= 1) {
                txtOfferCount.setText(item.getOfferCount().toString());
            } else {
                txtOfferCount.setText(item.getOfferCount().toString());
            }

            if(item.getStatus() != null) {
                setStatusText(item);
                setColors(item);
            }
            cardTaskBackground.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, item, getAdapterPosition(), "action");
                }
            });

            tvDelete.setOnClickListener(v ->{
                if(mOnDraftDeleteListener != null){
                    mOnDraftDeleteListener.onDraftDeleteButtonClick(v, item);
                }
            });
        }


        private void setColors(TaskModel item){
            switch (item.getStatus()) {
                case "draft":
                    txtStatus.setVisibility(View.GONE);
                    txtStatusDraft.setVisibility(View.VISIBLE);
                    txtStatusDraft.setBackgroundResource(R.drawable.shape_rounded_gray_light);
                    GradientDrawable backgroundGradient = (GradientDrawable) txtStatusDraft.getBackground();
                    cardTaskBackground.setCardBackgroundColor(ContextCompat.getColor(context, R.color.myJobsColorTaskOverDraft));
                    backgroundGradient.setColor(ContextCompat.getColor(context, R.color.myJobsColorTaskOverDraftTrans));
                    txtStatusDraft.setBackground(backgroundGradient);
                    txtStatusDraft.setTextColor(ContextCompat.getColor(context, R.color.myJobsColorTaskOverDraftTextColor));
//                        tvDelete.setVisibility(View.VISIBLE);
                    break;
                case "open":
                case "offered":
                    cardTaskBackground.setCardBackgroundColor(ContextCompat.getColor(context, R.color.myJobsColorTaskOffer));
//                        backgroundGradient.setColor(ContextCompat.getColor(context, R.color.myJobsColorTaskOfferTrans));
//                        txtStatus.setBackground(backgroundGradient);
                    txtStatus.setTextColor(ContextCompat.getColor(context, R.color.myJobsColorTaskOfferTrans));
                    break;
                case "assigned":
                case "completed":
                case "overdue":
                    cardTaskBackground.setCardBackgroundColor(ContextCompat.getColor(context, R.color.myJobsColorTaskAssigned));
//                        backgroundGradient.setColor(ContextCompat.getColor(context, R.color.myJobsColorTaskAssignedTrans));
                    txtStatus.setTextColor(ContextCompat.getColor(context, R.color.myJobsColorTaskAssignedTrans));
//                        txtStatus.setBackground(backgroundGradient);
                    break;
                    case "closed":
                    cardTaskBackground.setCardBackgroundColor(ContextCompat.getColor(context, R.color.myJobsColorTaskCompleted));
//                        backgroundGradient.setColor(ContextCompat.getColor(context, R.color.myJobsColorTaskCompletedTrans));
//                        txtStatus.setBackground(backgroundGradient);
                    txtStatus.setTextColor(ContextCompat.getColor(context, R.color.myJobsColorTaskCompletedTrans));
                    break;
                case "cancelled":
                    cardTaskBackground.setCardBackgroundColor(ContextCompat.getColor(context, R.color.myJobsColorTaskCancelled));
//                        backgroundGradient.setColor(ContextCompat.getColor(context, R.color.myJobsColorTaskCancelledTrans));
//                        txtStatus.setBackground(backgroundGradient);
                    txtStatus.setTextColor(ContextCompat.getColor(context, R.color.myJobsColorTaskCancelledTrans));
                    break;
            }
        }

        private void setStatusText(TaskModel item){
            if(userId != null && item.getPoster() != null && item.getPoster().getId() != null &&
                    item.getPoster().getId().equals(userId) && item.getStatus().equals("open")){
                txtStatus.setText("Posted");
            }else if(userId != null && item.getStatus() != null && item.getStatus().equals("open")){
                txtStatus.setText("Offered");
            }else if(item.getStatus().equals("completed") || item.getStatus().equals("assigned") || item.getStatus().equals("overdue")){
                txtStatus.setText("Assigned");
            }else if(item.getStatus().equals("closed")){
                txtStatus.setText("Completed");
            }else if(item.getStatus().equals("cancelled")){
                txtStatus.setText("Cancelled");
            }else if(item.getStatus().equals("draft")){
                txtStatusDraft.setText("Drafted");
            }else{
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
