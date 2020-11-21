package com.jobtick.adapers;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.activities.ZoomImageActivity;
import com.jobtick.models.AttachmentModel;
import com.jobtick.R;
import com.jobtick.models.ChatModel;
import com.jobtick.utils.ImageUtil;
import com.jobtick.utils.Tools;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ChatAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    private static final int MSG_TYPE_LEFT = 2;

    private final Context context;
    private final String username;
    private final Integer sender_id;

    public interface OnItemClickListener {
        void onItemClick(View view, ChatModel obj, int position, String action);
    }

    public void setOnItemClickListener() {
    }

    private boolean isLoaderVisible = false;
    private final ArrayList<ChatModel> mItems;

    public ChatAdapter(Context context, ArrayList<ChatModel> mItems, String username, Integer sender_id) {
        this.mItems = mItems;
        this.context = context;
        //  SessionManager sessionManager = new SessionManager(context);
        this.username = username;
        this.sender_id = sender_id;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case MSG_TYPE_RIGHT:
                return new MsgRightViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.list_chat_right, parent, false));
            case MSG_TYPE_LEFT:
                return new MsgLeftViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.list_chat_left, parent, false));
            case VIEW_TYPE_LOADING:
            default:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            if (position == mItems.size() - 1) {
                return VIEW_TYPE_LOADING;
            } else {
                final ChatModel chatModel = mItems.get(position);
                if (chatModel.getSenderId().equals(sender_id)) {
                    return MSG_TYPE_RIGHT;
                } else {
                    return MSG_TYPE_LEFT;
                }
            }
        } else {
            final ChatModel chatModel = mItems.get(position);
            if (chatModel.getSenderId().equals(sender_id)) {
                return MSG_TYPE_RIGHT;
            } else {
                return MSG_TYPE_LEFT;
            }
        }


    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    public void addItems(List<ChatModel> mItems) {
        this.mItems.addAll(0, mItems);
        notifyDataSetChanged();
    }

    public void addItems(ChatModel mItems) {
        this.mItems.add(mItems);
        notifyItemChanged(this.mItems.size());
    }

    /*public void getEventCall(ChatModel conversationModel) {
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
    } */

    public void addLoading() {
        isLoaderVisible = true;
        this.mItems.add(new ChatModel());
        notifyItemInserted(this.mItems.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = mItems.size() - 1;
        ChatModel item = getItem(position);
        if (item != null) {
            mItems.remove(position);
            notifyItemRemoved(position);
        }
    }


    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    ChatModel getItem(int position) {
        return mItems.get(position);
    }

    public class MsgRightViewHolder extends BaseViewHolder {

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txtOnlyDate)
        TextView txtOnlyDate;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.layoutDate)
        RelativeLayout layoutDate;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txtName)
        TextView txtName;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txtShowMessage)
        TextView txtShowMessage;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.imgPath)
        ImageView imgPath;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.textLayout)
        RelativeLayout textLayout;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txtMsgTime)
        TextView txtMsgTime;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.imgMsgSeen)
        ImageView imgMsgSeen;


        MsgRightViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        protected void clear() {
        }


        public void onBind(int position) {
            super.onBind(position);

            ChatModel item = mItems.get(position);

            if (item.getMessage() != null) {
                txtShowMessage.setVisibility(View.VISIBLE);
                imgPath.setVisibility(View.GONE);
                txtShowMessage.setText(item.getMessage());
            }

            if (item.getAttachment() != null) {
                txtShowMessage.setVisibility(View.GONE);
                imgPath.setVisibility(View.VISIBLE);
                ImageUtil.displayImage(imgPath, item.getAttachment().getModalUrl(), null);
                imgPath.setOnClickListener(v -> {
                    //  final Screens screens = new Screens(mContext);
                    //  screens.openFullImageViewActivity(v, chat.getImgPath());
                    ArrayList<AttachmentModel> attachmentArrayList = new ArrayList<>();
                    AttachmentModel model = new AttachmentModel();
                    model.setModalUrl(item.getAttachment().getModalUrl());
                    attachmentArrayList.add(model);
                    Intent intent = new Intent(context, ZoomImageActivity.class);
                    intent.putExtra("url", attachmentArrayList);
                    intent.putExtra("title", "");
                    intent.putExtra("pos", position);
                    context.startActivity(intent);
                });
            }

            txtShowMessage.setOnLongClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("jobtick", txtShowMessage.getText().toString());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(context, "Copied", Toast.LENGTH_LONG).show();

                return false;
            });

            txtOnlyDate.setVisibility(View.GONE);
            try {
                final long first = Tools.dateToMillis(mItems.get(position - 1).getCreatedAt());
                final long second = Tools.dateToMillis(item.getCreatedAt());
                if (!Tools.hasSameDate(first, second)) {
                    txtOnlyDate.setVisibility(View.VISIBLE);
                    txtOnlyDate.setText(Tools.formatFullDate(item.getCreatedAt()));
                }
            } catch (Exception e) {
                if (position == 0) {
                    txtOnlyDate.setVisibility(View.VISIBLE);
                    txtOnlyDate.setText(Tools.formatFullDate(item.getCreatedAt()));
                }
            }

            txtName.setText(username);


            long timeMilliSeconds = 0;
            try {
                timeMilliSeconds = Tools.dateToMillis(item.getCreatedAt());
            } catch (Exception ignored) {
            }

            if (timeMilliSeconds > 0) {
                txtMsgTime.setText(Tools.formatLocalTime(timeMilliSeconds));
            } else {
                txtMsgTime.setVisibility(View.GONE);
            }


            if (position == mItems.size() - 1) {
                imgMsgSeen.setImageResource(R.drawable.ic_vector__7_);
            } else {
                imgMsgSeen.setVisibility(View.GONE);
            }
        }
    }

    public class MsgLeftViewHolder extends BaseViewHolder {
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txtOnlyDate)
        TextView txtOnlyDate;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txtName)
        TextView txtName;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txtShowMessage)
        TextView txtShowMessage;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.imgPath)
        ImageView imgPath;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.textLayout)
        RelativeLayout textLayout;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txtMsgTime)
        TextView txtMsgTime;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.imgMsgSeen)
        ImageView imgMsgSeen;

        MsgLeftViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        protected void clear() {
        }


        public void onBind(int position) {
            super.onBind(position);
            ChatModel item = mItems.get(position);

            if (item.getMessage() != null) {
                txtShowMessage.setVisibility(View.VISIBLE);
                imgPath.setVisibility(View.GONE);
                txtShowMessage.setText(item.getMessage());
            }

            if (item.getAttachment() != null) {
                txtShowMessage.setVisibility(View.GONE);
                imgPath.setVisibility(View.VISIBLE);
                ImageUtil.displayImage(imgPath, item.getAttachment().getModalUrl(), null);
                imgPath.setOnClickListener(v -> {
                    //  final Screens screens = new Screens(mContext);
                    //  screens.openFullImageViewActivity(v, chat.getImgPath());
                    ArrayList<AttachmentModel> attachmentArrayList = new ArrayList<>();
                    AttachmentModel model = new AttachmentModel();
                    model.setModalUrl(item.getAttachment().getModalUrl());
                    attachmentArrayList.add(model);
                    Intent intent = new Intent(context, ZoomImageActivity.class);
                    intent.putExtra("url", attachmentArrayList);
                    intent.putExtra("title", "");
                    intent.putExtra("pos", position);
                    context.startActivity(intent);
                });
            }

            txtShowMessage.setOnLongClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("jobtick", txtShowMessage.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Copied", Toast.LENGTH_LONG).show();

                return false;
            });


            txtOnlyDate.setVisibility(View.GONE);
            try {
                final long first = Tools.dateToMillis(mItems.get(position - 1).getCreatedAt());
                final long second = Tools.dateToMillis(item.getCreatedAt());
                if (!Tools.hasSameDate(first, second)) {
                    txtOnlyDate.setVisibility(View.VISIBLE);
                    txtOnlyDate.setText(Tools.formatFullDate(item.getCreatedAt()));
                }
            } catch (Exception e) {
                if (position == 0) {
                    txtOnlyDate.setVisibility(View.VISIBLE);
                    txtOnlyDate.setText(Tools.formatFullDate(item.getCreatedAt()));
                }
            }

            txtName.setText(username);


            long timeMilliSeconds = 0;
            try {
                timeMilliSeconds = Tools.dateToMillis(item.getCreatedAt());
            } catch (Exception ignored) {
            }

            if (timeMilliSeconds > 0) {
                txtMsgTime.setText(Tools.formatLocalTime(timeMilliSeconds));
            } else {
                txtMsgTime.setVisibility(View.GONE);
            }


            if (position == mItems.size() - 1) {
                if (item.getIsSeen().equals(1)) {
                    imgMsgSeen.setImageResource(R.drawable.ic_check_read);
                } else {
                    imgMsgSeen.setImageResource(R.drawable.ic_check_delivery);
                }
            } else {
                imgMsgSeen.setVisibility(View.GONE);
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




