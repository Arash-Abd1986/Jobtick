package com.jobtick.adapers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.R;
import com.jobtick.TextView.TextViewBold;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.models.NotificationModel;
import com.jobtick.models.PushNotificationModel;
import com.jobtick.utils.ImageUtil;
import com.jobtick.utils.SessionManager;
import com.jobtick.utils.TimeAgo;
import com.jobtick.utils.Tools;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class NotificationListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;


    private SessionManager sessionManager;

    private Context context;
    private OnItemClickListener mOnItemClickListener;


    public interface OnItemClickListener {
        void onItemClick(View view, PushNotificationModel obj, int position, String action);
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }


    private boolean isLoaderVisible = false;
    private List<PushNotificationModel> mItems;

    public NotificationListAdapter(Context context, List<PushNotificationModel> mItems) {
        this.mItems = mItems;
        this.context = context;
        this.sessionManager = new SessionManager(context);
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

    public void addItems(List<PushNotificationModel> mItems) {
        this.mItems.addAll(mItems);
        notifyDataSetChanged();
    }


    public void addLoading() {
        isLoaderVisible = true;
        this.mItems.add(new PushNotificationModel(context));
        notifyItemInserted(this.mItems.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = mItems.size() - 1;
        PushNotificationModel item = getItem(position);
        if (item != null) {
            mItems.remove(position);
            notifyItemRemoved(position);
        }
    }


    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    PushNotificationModel getItem(int position) {
        return mItems.get(position);
    }

    public class ViewHolder extends BaseViewHolder {


        @BindView(R.id.webviewNotificationTitle)
        WebView webViewNotification;

        @BindView(R.id.row_item)
        RelativeLayout rowItem;

        @BindView(R.id.txtStatus)
        TextViewRegular txtStatus;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        protected void clear() {
        }


        public void onBind(int position) {
            super.onBind(position);
            PushNotificationModel item = mItems.get(position);
            webViewNotification.loadData(item.getTitle(),
                    "text/html", "UTF-8");
            txtStatus.setText(item.getStatus());
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




