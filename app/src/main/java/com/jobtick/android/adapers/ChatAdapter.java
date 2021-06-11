package com.jobtick.android.adapers;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jobtick.android.R;

import android.annotation.SuppressLint;

import com.jobtick.android.activities.ZoomImageActivity;
import com.jobtick.android.models.AttachmentModel;
import com.jobtick.android.models.response.chat.MessageItem;
import com.jobtick.android.utils.Tools;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    private static final int MSG_TYPE_LEFT = 2;
    private static final int MSG_TYPE_LOG = 3;

    private final Context context;
    private final Integer sender_id;

    public interface OnItemClickListener {
        void onItemClick(View view, MessageItem obj, int position, String action);
    }

    public void setOnItemClickListener() {
    }

    private boolean isLoaderVisible = false;
    private final ArrayList<MessageItem> mItems;

    public ChatAdapter(Context context, ArrayList<MessageItem> mItems, Integer sender_id) {
        this.mItems = mItems;
        this.context = context;
        this.sender_id = sender_id;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case MSG_TYPE_LOG:
                return new MsgRightViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.list_chat_log, parent, false));
            case MSG_TYPE_RIGHT:
                return new MsgRightViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.list_chat_right, parent, false));
            case MSG_TYPE_LEFT:
                return new MsgRightViewHolder(
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
                if (mItems.get(position) != null && mItems.get(position).getSender_id().equals(sender_id)) {
                    return MSG_TYPE_RIGHT;
                } else {
                    return MSG_TYPE_LEFT;
                }
            }
        } else {
//            if ((mItems.get(position)) != null && (mItems.get(position).is_log() == 1))
//                return MSG_TYPE_LOG;
//            else
            if (mItems.get(position) != null && mItems.get(position).getSender_id().equals(sender_id)) {
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

    public void addItems(List<MessageItem> mItems) {
        this.mItems.addAll(0, mItems);
        notifyDataSetChanged();
    }

    public void addItems(MessageItem mItems) {
        this.mItems.add(mItems);
        notifyItemInserted(this.mItems.size());
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
        this.mItems.add(new MessageItem(null, null, null, null, null, null, null,null));
        notifyItemInserted(this.mItems.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = mItems.size() - 1;
        MessageItem item = getItem(position);
        if (item != null) {
            mItems.remove(position);
            notifyItemRemoved(position);
        }
    }


    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    MessageItem getItem(int position) {
        return mItems.get(position);
    }

    public class MsgRightViewHolder extends BaseViewHolder {

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txtOnlyDate)
        TextView txtOnlyDate;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_size)
        TextView txtSize;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.view_file_chat)
        View view_file_chat;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.view_text_chat)
        View view_text_chat;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.view_text_chat_text)
        TextView txtShowMessage;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.view_file_chat_txtShowMessage)
        TextView txtShowMessageF;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.tx_chat_file)
        TextView txChatFile;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.view_text_chat_txtTime)
        TextView txtMsgTime;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.view_file_chat_txtMsgTime)
        TextView txtMsgTimeF;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.view_file_chat_imgPath)
        ImageView imagePathF;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.view_text_chat_doubleTick)
        ImageView doubleTick;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.view_file_chat_doubleTick)
        ImageView doubleTickF;

        MsgRightViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        protected void clear() {
        }


        @SuppressLint("SetTextI18n")
        public void onBind(int position) {
            super.onBind(position);

            MessageItem item = mItems.get(position);
            txtOnlyDate.setVisibility(View.GONE);

            if (item.getAttachment() != null) {
                view_text_chat.setVisibility(View.GONE);
                view_file_chat.setVisibility(View.VISIBLE);
                txtShowMessageF.setText(item.getAttachment().getFile_name());
                if ((item.getAttachment().getFile_size() / 1024) > 1024)
                    txtSize.setText(Math.round((item.getAttachment().getFile_size() / (1024 * 1024))) + " MB");
                else
                    txtSize.setText(Math.round((item.getAttachment().getFile_size() / 1024)) + " KB");

                txChatFile.setText(item.getMessage());
                if (item.getMessage().length() == 0) {
                    txChatFile.setVisibility(View.GONE);
                } else {
                    txChatFile.setVisibility(View.VISIBLE);
                }
                String time = parseCreatedMessage(item.getCreated_at());
                if (time != null) {
                    txtMsgTimeF.setVisibility(View.VISIBLE);
                    txtMsgTimeF.setText(time);
                } else {
                    txtMsgTimeF.setVisibility(View.GONE);
                }

                if (item.getSender_id().equals(sender_id)) {

                    if (item.is_seen() == 1) {
                        doubleTickF.setVisibility(View.GONE);
                    } else {
                        doubleTickF.setVisibility(View.GONE);
                    }
                } else {

                    doubleTickF.setVisibility(View.GONE);
                }

                Glide.with(imagePathF).load(item.getAttachment().getThumb_url()).into(imagePathF);
                imagePathF.setOnClickListener(v -> {
                    ArrayList<AttachmentModel> attachmentArrayList = new ArrayList<>();
                    AttachmentModel model = new AttachmentModel();
                    model.setModalUrl(item.getAttachment().getModal_url());
                    attachmentArrayList.add(model);
                    Intent intent = new Intent(context, ZoomImageActivity.class);
                    intent.putExtra("url", attachmentArrayList);
                    intent.putExtra("title", "");
                    intent.putExtra("pos", position);
                    context.startActivity(intent);
                });
            } else {
                txChatFile.setVisibility(View.GONE);
                view_text_chat.setVisibility(View.VISIBLE);
                view_file_chat.setVisibility(View.GONE);
                txtShowMessage.setText(item.getMessage());
                String time = parseCreatedMessage(item.getCreated_at());
                if (time != null) {
                    txtMsgTime.setVisibility(View.VISIBLE);
                    txtMsgTime.setText(time);
                } else {
                    txtMsgTime.setVisibility(View.VISIBLE);
                }

                if (item.getSender_id().equals(sender_id)) {
                    if (item.is_seen() == 1) {
                        doubleTick.setVisibility(View.GONE);
                    } else {
                        doubleTick.setVisibility(View.GONE);
                    }
                    txtShowMessage.setOnLongClickListener(v -> {
                        copyToClipboard(txtShowMessage);
                        return false;
                    });
                } else {
                    doubleTick.setVisibility(View.GONE);
                }
            }

//TODO:            ONLY MESSAGE VIDEO

            txtOnlyDate.setVisibility(View.GONE);
            try {
                final long first = Tools.chatDateToMillis(mItems.get(position - 1).getCreated_at());
                final long second = Tools.chatDateToMillis(item.getCreated_at());
                if (!Tools.hasSameDate(first, second)) {
                    txtOnlyDate.setVisibility(View.VISIBLE);
                    txtOnlyDate.setText(Tools.formatChatFullDate(item.getCreated_at()));
                }
            } catch (Exception e) {
                if (position == 0) {
                    txtOnlyDate.setVisibility(View.VISIBLE);
                    txtOnlyDate.setText(Tools.formatChatFullDate(item.getCreated_at()));
                }
            }
        }
    }

    private void copyToClipboard(TextView txt) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("jobtick", txt.getText().toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Copied", Toast.LENGTH_LONG).show();
    }

    private String parseCreatedMessage(String createdAt) {

        long timeMilliSeconds;
        try {
            timeMilliSeconds = Tools.chatDateToMillis(createdAt);
        } catch (Exception ignored) {
            timeMilliSeconds = 0;
        }

        if (timeMilliSeconds > 0) {
            return Tools.formatLocalTime(timeMilliSeconds);
        } else {
            return null;
        }
    }

    //    private String parseCreatedMessage(String createdAt) {
//
//        long timeMilliSeconds;
//        try {
//            timeMilliSeconds = Tools.chatDateToMillis(createdAt);
//            Log.d("ChatCheck","hi");
//        } catch (Exception ignored) {
//            timeMilliSeconds = 0;
//            Log.d("ChatCheck",ignored.getMessage());
//        }
//
//        if (timeMilliSeconds > 0) {
//            return Tools.formatLocalTime(timeMilliSeconds);
//        } else {
//            return null;
//        }
//    }
    public static class ProgressHolder extends BaseViewHolder {
        ProgressHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void clear() {
        }
    }
}


