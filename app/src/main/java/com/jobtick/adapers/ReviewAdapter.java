package com.jobtick.adapers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.R;
import com.jobtick.TextView.TextViewBold;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.models.ReviewModel;
import com.jobtick.models.TaskModel;
import com.jobtick.utils.TimeAgo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewAdapter extends RecyclerView.Adapter<BaseViewHolder> {


    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private Context context;
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


        @BindView(R.id.txt_post_title)
        TextViewBold txtPostTitle;

        @BindView(R.id.txt_user_name)
        TextViewRegular txtUserName;

        @BindView(R.id.ratingbar)
        AppCompatRatingBar ratingBar;

        @BindView(R.id.txt_created_date)
        TextViewRegular txtCreatedDate;

        @BindView(R.id.txt_review_message)
        TextViewRegular txtReviewMessage;

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
            ratingBar.setProgress(item.getRating());
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
