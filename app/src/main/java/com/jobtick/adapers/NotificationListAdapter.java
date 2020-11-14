package com.jobtick.adapers;

import android.text.Html;
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
import com.jobtick.models.notification.NotifDatum;
import com.jobtick.utils.TimeHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class NotificationListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;


    private OnItemClickListener mOnItemClickListener;


    public interface OnItemClickListener {
        void onItemClick(View view, NotifDatum obj, int position, String action);
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }


    private boolean isLoaderVisible = false;
    private List<NotifDatum> mItems;

    public NotificationListAdapter(List<NotifDatum> mItems) {
        this.mItems = mItems;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_row, parent, false));
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

    public void addItems(List<NotifDatum> mItems) {
        this.mItems.addAll(mItems);
        notifyDataSetChanged();
    }


    public void addLoading() {
        isLoaderVisible = true;
        this.mItems.add(new NotifDatum());
        notifyItemInserted(this.mItems.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = mItems.size() - 1;
        NotifDatum item = getItem(position);
        if (item != null) {
            mItems.remove(position);
            notifyItemRemoved(position);
        }
    }


    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    NotifDatum getItem(int position) {
        return mItems.get(position);
    }

    public class ViewHolder extends BaseViewHolder {


        @BindView(R.id.notification_title)
        TextView notificationTitle;

        @BindView(R.id.row_item)
        RelativeLayout rowItem;

        @BindView(R.id.notification_time)
        TextView notificationTime;

        @BindView(R.id.ivProfile)
        ImageView profileAvatar;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        protected void clear() {
        }


        public void onBind(int position) {
            super.onBind(position);
            NotifDatum item = mItems.get(position);

            if(item.getUser() != null && item.getUser().getAvatar() != null && item.getUser().getAvatar().getUrl() != null)
                Glide.with(profileAvatar).load(item.getUser().getAvatar().getUrl()).into(profileAvatar);

            notificationTitle.setText(Html.fromHtml(item.getData().getTitle()));
            notificationTime.setText(TimeHelper.findDifferenceWithNow(item.getCreatedAt()));

            rowItem.setOnClickListener(v -> {
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




