package com.jobtick.android.adapers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.android.R;
import android.annotation.SuppressLint;

import com.jobtick.android.activities.ProfileActivity;
import com.jobtick.android.activities.TaskDetailsActivity;
import com.jobtick.android.models.ReviewModel;
import com.jobtick.android.models.TaskModel;
import com.jobtick.android.utils.ConstantKey;
import com.jobtick.android.utils.ImageUtil;
import com.jobtick.android.utils.TimeAgo;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewAdapter extends RecyclerView.Adapter<BaseViewHolder> {


    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private OnItemClickListener mOnItemClickListener;
    private boolean isLoaderVisible = false;
    List<ReviewModel.DataBean> mItems;
    Context mContext;


    public interface OnItemClickListener {
        void onItemClick(View view, TaskModel obj, int position, String action);
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public ReviewAdapter(Context context, List<ReviewModel.DataBean> reviewModels) {
        mContext = context;
        mItems = reviewModels;

    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false));
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

    public void addItems(List<ReviewModel.DataBean> mItems) {
        this.mItems.addAll(mItems);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        this.mItems.add(new ReviewModel.DataBean());

        notifyItemInserted(this.mItems.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = mItems.size() - 1;
        ReviewModel.DataBean item = getItem(position);
        if (item != null) {
            mItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    ReviewModel.DataBean getItem(int position) {
        return mItems.get(position);
    }

    public class ViewHolder extends BaseViewHolder {


        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_post_title)
        TextView txtPostTitle;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_user_name)
        TextView txtUserName;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.ratingbar)
        AppCompatRatingBar ratingBar;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_created_date)
        TextView txtCreatedDate;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_review_message)
        TextView txtReviewMessage;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_avatar)
        CircularImageView imgAvatar;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        protected void clear() {
        }


        public void onBind(int position) {
            super.onBind(position);

            ReviewModel.DataBean item = mItems.get(position);
            txtPostTitle.setText(item.getTask().getTitle());
            txtUserName.setText(item.getRater().getName());
            txtCreatedDate.setText(TimeAgo.getTimeAgo(item.getCreated_at()));
            txtReviewMessage.setText(item.getMessage());
            ratingBar.setRating(item.getRating());
//            Log.d("RatingModel",""+item.getRating());
            if (item.getRater().getAvatar() != null)
                ImageUtil.displayImage(imgAvatar, item.getRater().getAvatar().getThumbUrl(), null);

            txtUserName.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra("id",item.getRater().getId());
                mContext.startActivity(intent);
            });

            imgAvatar.setOnClickListener(v -> txtUserName.performClick());
            txtCreatedDate.setOnClickListener(v -> txtUserName.performClick());

            txtPostTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(item.getTask()==null)return;

                    Intent intent = new Intent(mContext, TaskDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(ConstantKey.SLUG, item.getTask().getSlug());
                    //    bundle.putInt(ConstantKey.USER_ID, obj.getPoster().getId());
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
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
