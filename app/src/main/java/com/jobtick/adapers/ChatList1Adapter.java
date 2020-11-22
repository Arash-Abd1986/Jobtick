package com.jobtick.adapers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.R;
import com.jobtick.models.ChatModel;
import com.jobtick.utils.ImageUtil;
import com.jobtick.utils.Tools;

import java.util.ArrayList;

public class ChatList1Adapter extends RecyclerView.Adapter<ChatList1Adapter.ViewHolder> {

    private final int MSG_TYPE_RIGHT = 0;
    private final int MSG_TYPE_LEFT = 1;

    private Context mContext;
    private ArrayList<ChatModel> mItems;
    private String username;
    private Integer sender_id;

    public ChatList1Adapter(Context mContext, ArrayList<ChatModel> mItems, String username, Integer sender_id) {
        this.mContext = mContext;
        this.mItems = mItems;
        this.username = username;
        this.sender_id = sender_id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.list_chat_right, viewGroup, false);
            return new ChatList1Adapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_chat_left, viewGroup, false);
            return new ChatList1Adapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final ChatModel chatModel = mItems.get(position);

        if (chatModel.getMessage() != null) {
            viewHolder.txtShowMessage.setVisibility(View.VISIBLE);
            viewHolder.imgPath.setVisibility(View.GONE);
            viewHolder.txtShowMessage.setText(chatModel.getMessage());
        }

            if(chatModel.getAttachment() !=null) {
                viewHolder.txtShowMessage.setVisibility(View.GONE);
                viewHolder.imgPath.setVisibility(View.VISIBLE);
                ImageUtil.displayImage(viewHolder.imgPath, chatModel.getAttachment().getThumbUrl(), null);
                viewHolder.imgPath.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  final Screens screens = new Screens(mContext);
                        //  screens.openFullImageViewActivity(v, chat.getImgPath());
                    }
                });
            }



        viewHolder.txtOnlyDate.setVisibility(View.GONE);
        try {
            final long first = Tools.dateToMillis(mItems.get(position - 1).getCreatedAt());
            final long second = Tools.dateToMillis(chatModel.getCreatedAt());
            if (!Tools.hasSameDate(first, second)) {
                viewHolder.txtOnlyDate.setVisibility(View.VISIBLE);
                viewHolder.txtOnlyDate.setText(Tools.formatFullDate(chatModel.getCreatedAt()));
            }
        } catch (Exception e) {
            if (position == 0) {
                viewHolder.txtOnlyDate.setVisibility(View.VISIBLE);
                viewHolder.txtOnlyDate.setText(Tools.formatFullDate(chatModel.getCreatedAt()));
            }
        }

        switch (viewHolder.getItemViewType()) {
            case MSG_TYPE_LEFT:

                viewHolder.txtName.setText(username);
                break;
            case MSG_TYPE_RIGHT:
                break;
        }

        long timeMilliSeconds = 0;
        try {
            timeMilliSeconds = Tools.dateToMillis(chatModel.getCreatedAt());
        } catch (Exception e) {
        }

        if (timeMilliSeconds > 0) {
            viewHolder.txtMsgTime.setText(Tools.formatLocalTime(timeMilliSeconds));
        } else {
            viewHolder.txtMsgTime.setVisibility(View.GONE);
        }


        if (position == mItems.size() - 1) {
            if (chatModel.getIsSeen().equals(1)) {
                viewHolder.imgMsgSeen.setImageResource(R.drawable.ic_check_read);
            } else {
                viewHolder.imgMsgSeen.setImageResource(R.drawable.ic_check_delivery);
            }
        } else {
            viewHolder.imgMsgSeen.setVisibility(View.GONE);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public TextView txtName;
        public TextView txtShowMessage;
        public TextView txtMsgTime;
        public ImageView imgMsgSeen;
        public TextView txtOnlyDate;
        public ImageView imgPath;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//
//            txtOnlyDate = itemView.findViewById(R.id.txtOnlyDate);
//            txtShowMessage = itemView.findViewById(R.id.txtShowMessage);
//            txtName = itemView.findViewById(R.id.txtName);
//            imgMsgSeen = itemView.findViewById(R.id.imgMsgSeen);
//            txtMsgTime = itemView.findViewById(R.id.txtMsgTime);
//            imgPath = itemView.findViewById(R.id.imgPath);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        final ChatModel chatModel = mItems.get(position);

        if (chatModel.getSenderId().equals(sender_id)) {
            return MSG_TYPE_RIGHT;
       } else {
            return MSG_TYPE_LEFT;
       }
    }
}
