package com.jobtick.adapers;

import android.content.Context;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.jobtick.R;
import com.jobtick.TextView.TextViewBold;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.models.ConversationModel;
import com.jobtick.models.UserAccountModel;
import com.jobtick.utils.ImageUtil;
import com.jobtick.utils.SessionManager;
import com.jobtick.utils.TimeAgo;
import com.jobtick.utils.Tools;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import butterknife.BindView;
import butterknife.ButterKnife;


public class InboxListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;


    private SessionManager sessionManager;

    private Context context;
    private OnItemClickListener mOnItemClickListener;




    public interface OnItemClickListener {
        void onItemClick(View view, ConversationModel obj, int position, String action);
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }


    private boolean isLoaderVisible = false;
    private List<ConversationModel> mItems;

    public InboxListAdapter(Context context, List<ConversationModel> mItems) {
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
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inbox_user, parent, false));
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

    public void addItems(List<ConversationModel> mItems) {
        this.mItems.addAll(mItems);
        notifyDataSetChanged();
    }

    public void setUnSeenCountZero(int position) {
        ConversationModel conversationModel = mItems.get(position);
        conversationModel.setUnseenCount(0);
        mItems.remove(position);
        mItems.add(position,conversationModel);
        notifyItemChanged(position);
    }

    public void getEventCall(ConversationModel conversationModel) {
        int new_position = 0;
        boolean is_available = false;
        for (int i = 0; this.mItems.size() > i; i++) {
            if (conversationModel.getId().equals(mItems.get(i).getId())) {
                new_position = i;
                is_available = true;
            }
        }
        if (is_available) {
            if (new_position != 0) {
                mItems.remove(new_position);
                mItems.add(0, conversationModel);
                notifyItemMoved(new_position, 0);
                notifyItemChanged(0);
            } else {
                mItems.remove(new_position);
                mItems.add(0, conversationModel);
                notifyItemChanged(0);
            }
        } else {
            mItems.add(0, conversationModel);
            notifyItemInserted(0);
        }
    }

    public void addLoading() {
        isLoaderVisible = true;
        this.mItems.add(new ConversationModel(context));
        notifyItemInserted(this.mItems.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = mItems.size() - 1;
        ConversationModel item = getItem(position);
        if (item != null) {
            mItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void setOnlineStatus(ArrayList<Integer> integerArrayList) {
        for (int i = 0; mItems.size() > i; i++) {
            boolean is_available = false;
            ConversationModel conversationModel = mItems.get(i);
            for (int n : integerArrayList) {
                if(conversationModel.getId() != null) {
                    if (n == conversationModel.getId()) {
                        is_available = true;
                        break;
                    }
                }
            }
            if (is_available) {
                conversationModel.getReceiver().setIsOnline(true);
                mItems.remove(i);
                notifyItemRemoved(i);
                mItems.add(i, conversationModel);
                notifyItemChanged(i);
            }
        }
    }

    public void addNewSubscribe(int userId) {
        boolean is_available = false;
        int position = 0;
        ConversationModel conversationModel = new ConversationModel(context);
        for (int i = 0; mItems.size() > i; i++) {
            conversationModel = mItems.get(i);
            if(conversationModel.getId() != null) {
                if (userId == conversationModel.getId()) {
                    is_available = true;
                    position = i;
                    break;
                }
            }
        }
        if (is_available) {
            conversationModel.getReceiver().setIsOnline(true);
            mItems.remove(position);
            notifyItemRemoved(position);
            mItems.add(position, conversationModel);
            notifyItemChanged(position);
        }
    }


    public void addNewUnSubscribe(int userId) {
        boolean is_available = false;
        int position = 0;
        ConversationModel conversationModel = new ConversationModel(context);
        for (int i = 0; mItems.size() > i; i++) {
            conversationModel = mItems.get(i);
            if(conversationModel.getId() != null) {
                if (userId == conversationModel.getId()) {
                    is_available = true;
                    position = i;
                    break;
                }
            }
        }
        if (is_available) {
            conversationModel.getReceiver().setIsOnline(false);
            mItems.remove(position);
            notifyItemRemoved(position);
            mItems.add(position, conversationModel);
            notifyItemChanged(position);
        }
    }


    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    ConversationModel getItem(int position) {
        return mItems.get(position);
    }

    public class ViewHolder extends BaseViewHolder {
        @BindView(R.id.img_avatar)
        CircularImageView imgAvatar;
        @BindView(R.id.txt_title)
        TextViewBold txtTitle;
        @BindView(R.id.txt_status)
        TextViewRegular txtStatus;
        @BindView(R.id.txt_user_name)
        TextViewRegular txtUserName;
        @BindView(R.id.img_gallery)
        ImageView imgGallery;
        @BindView(R.id.txt_last_message)
        TextViewRegular txtLastMessage;
        @BindView(R.id.txt_unseen_count)
        TextViewRegular txtUnseenCount;
        @BindView(R.id.txt_time_status)
        TextViewRegular txtTimeStatus;
        @BindView(R.id.rlt_last_message)
        RelativeLayout rltLastMessage;
        @BindView(R.id.lyt_parent_layout)
        LinearLayout lytParentLayout;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        protected void clear() {
        }


        public void onBind(int position) {
            super.onBind(position);
            ConversationModel item = mItems.get(position);

            if (!item.getReceiver().getId().equals(sessionManager.getUserAccount().getId())) {
                if (item.getReceiver().getAvatar() != null && item.getReceiver().getAvatar().getThumbUrl() != null) {
                    ImageUtil.displayImage(imgAvatar, item.getReceiver().getAvatar().getThumbUrl(), null);
                } else {
                    //TODO DEFAULT IMAGE
                }
                txtUserName.setText(item.getReceiver().getName());
                if (item.getReceiver().getIsOnline()) {
                    txtStatus.setVisibility(View.VISIBLE);
                } else {
                    txtStatus.setVisibility(View.GONE);
                }
            }

            txtTitle.setText(item.getName());
            if (item.getLastMessage() != null) {
                rltLastMessage.setVisibility(View.VISIBLE);
                try {
                    txtTimeStatus.setText(TimeAgo.setTimeAgo(Tools.dateToMillis(item.getLastMessage().getCreatedAt())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (item.getLastMessage().getAttachment() != null) {
                    txtLastMessage.setText("Image");
                    imgGallery.setVisibility(View.VISIBLE);
                } else {
                    imgGallery.setVisibility(View.GONE);
                }
                if (item.getLastMessage().getMessage() != null) {
                    txtLastMessage.setText(item.getLastMessage().getMessage());
                }
            } else {
                rltLastMessage.setVisibility(View.GONE);
            }

            if(item.getUnseenCount() != 0){
                txtUnseenCount.setVisibility(View.VISIBLE);
                txtUnseenCount.setText(""+item.getUnseenCount());
            }else{
                txtUnseenCount.setVisibility(View.GONE);
            }
            lytParentLayout.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, item, getAdapterPosition(), "parent_layout");
                }
            });

            imgAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(v, item, getAdapterPosition(), "avatar");
                    }
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




