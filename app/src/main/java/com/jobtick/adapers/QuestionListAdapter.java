package com.jobtick.adapers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.activities.ReportActivity;
import com.jobtick.activities.UserProfileActivity;
import com.jobtick.utils.ConstantKey;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.jobtick.R;
import com.jobtick.TextView.TextViewBold;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.models.AttachmentModel;
import com.jobtick.models.CommentModel;
import com.jobtick.models.OfferModel;
import com.jobtick.models.QuestionModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jobtick.utils.ConstantKey.KEY_OFFER_REPORT;
import static com.jobtick.utils.ConstantKey.KEY_QUESTION_REPORT;


public class QuestionListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;

    private Context context;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemQuestionClick(View view, QuestionModel obj, int position, String action);

        //  void onItemClick(View view, OfferChatModel obj, int position, String action);
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }


    private boolean isLoaderVisible = false;
    private List<QuestionModel> mItems;

    public QuestionListAdapter(Context context, List<QuestionModel> mItems) {
        this.mItems = mItems;
        this.context = context;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false));
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
            return position == mItems.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    public void addItems(List<QuestionModel> mItems) {
        this.mItems.addAll(mItems);
        notifyDataSetChanged();
    }


    public void addItem(QuestionModel items) {
        this.mItems.add(0, items);
        notifyItemInserted(0);
    }


    public void addLoading() {
        isLoaderVisible = true;
        this.mItems.add(new QuestionModel());
        notifyItemInserted(this.mItems.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = mItems.size() - 1;
        QuestionModel item = getItem(position);
        if (item != null) {
            mItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    private QuestionModel getItem(int position) {
        return mItems.get(position);
    }

    public class ViewHolder extends BaseViewHolder implements PublicChatListAdapter.OnItemClickListener, AttachmentAdapter.OnItemClickListener {
        @BindView(R.id.img_avatar)
        CircularImageView imgAvatar;
        @BindView(R.id.txt_name)
        TextViewBold txtName;
        @BindView(R.id.txt_created_date)
        TextViewRegular txtCreatedDate;
        @BindView(R.id.txt_message)
        TextViewRegular txtMessage;
        @BindView(R.id.recycler_view_question)
        RecyclerView recyclerViewQuestion;
        @BindView(R.id.img_file)
        ImageView imgFile;
        @BindView(R.id.card_img_file)
        CardView cardImgFile;
        @BindView(R.id.lyt_btn_reply)
        LinearLayout lytBtnReply;
        @BindView(R.id.img_more_less_arrow)
        ImageView imgMoreLessArrow;
        @BindView(R.id.txt_more_less)
        TextViewBold txtMoreLess;
        @BindView(R.id.lyt_btn_more)
        LinearLayout lytBtnMore;
        @BindView(R.id.txt_more_reply_question)
        TextViewBold txtMoreReplyQuestion;
        @BindView(R.id.recycler_view_questions_chat)
        RecyclerView recyclerViewQuestionsChat;


        @BindView(R.id.linear_user_profile)
        LinearLayout linearUserProfile;

       // @BindView(R.id.textViewOptions)
     //   TextView textViewOptions;


        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        protected void clear() {
        }


        public void onBind(int position) {
            super.onBind(position);
            QuestionModel item = mItems.get(position);
            if (item.getUser().getAvatar() != null) {
                ImageUtil.displayImage(imgAvatar, item.getUser().getAvatar().getThumbUrl(), null);
            }
            if (item.getReply()) {
                lytBtnReply.setVisibility(View.VISIBLE);
            } else {
                lytBtnReply.setVisibility(View.GONE);
            }

            if (item.getCommentsCount() > 3) {
                int remaining_number = item.getCommentsCount() - 3;
                if (remaining_number == 1) {
                    txtMoreReplyQuestion.setText("1 more reply");
                } else {
                    txtMoreReplyQuestion.setText(remaining_number + " more replies");
                }
                txtMoreReplyQuestion.setVisibility(View.VISIBLE);
            } else {
                txtMoreReplyQuestion.setVisibility(View.GONE);
            }


            txtName.setText(item.getUser().getName());
            txtCreatedDate.setText(item.getCreatedAt());

            txtMessage.setVisibility(View.VISIBLE);
            txtMessage.setText(item.getQuestionText());
            if (item.getAttachments().size() != 0) {
                recyclerViewQuestion.setVisibility(View.VISIBLE);
                AttachmentAdapter attachmentAdapter = new AttachmentAdapter(context, item.getAttachments(), false);
                recyclerViewQuestion.setHasFixedSize(true);
                recyclerViewQuestion.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                recyclerViewQuestion.setAdapter(attachmentAdapter);
                recyclerViewQuestion.setNestedScrollingEnabled(true);
                attachmentAdapter.setOnItemClickListener(this);
            } else {
                recyclerViewQuestion.setVisibility(View.GONE);
            }

            txtMessage.post(new Runnable() {
                @Override
                public void run() {
                    int lineCount = txtMessage.getLineCount();
                    Log.e("COUNT", String.valueOf(lineCount));
                    if (lineCount > Constant.MAX_LINE_TEXTVIEW_MORE_2) {
                        // view.setMaxLines(Constant.MAX_LINE_TEXTVIEW_MORE);
                        lytBtnMore.setVisibility(View.VISIBLE);
                        txtMoreLess.setText(item.getStrMore());
                        mItems.get(getAdapterPosition()).setIsUserPrefrenceToMore(true);
                        if (item.getIsUserPrefrenceToMore()) {
                            txtMessage.setMaxLines(Constant.MAX_LINE_TEXTVIEW_MORE_2);
                        }
                    } else {
                        lytBtnMore.setVisibility(View.GONE);
                    }
                }
            });
/*
            textViewOptions.setOnClickListener(view -> {

                //creating a popup menutextViewOptions
                PopupMenu popup = new PopupMenu(context, textViewOptions);
                //inflating menu from xml resource
                popup.inflate(R.menu.menu_report);
                //adding click listener
                popup.setOnMenuItemClickListener(item1 -> {
                    switch (item1.getItemId()) {
                        case R.id.action_report:
                            Bundle bundle = new Bundle();
                            Intent intent = new Intent(context, ReportActivity.class);
                            bundle.putString("key",KEY_QUESTION_REPORT);
                            bundle.putInt(ConstantKey.questionId, item.getId());
                            intent.putExtras(bundle);
                            context.startActivity(intent);                                break;
                    }
                    return false;
                });
                //displaying the popup
                popup.show();

            });
*/
            lytBtnMore.setOnClickListener(v -> {
                if (item.getStrMore().equalsIgnoreCase("More")) {
                    txtMessage.setMaxLines(Integer.MAX_VALUE);
                    lytBtnMore.setVisibility(View.VISIBLE);
                    txtMoreLess.setText("Less");
                    imgMoreLessArrow.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_up));
                    mItems.get(getAdapterPosition()).setStrMore("Less");
                    item.setStrMore("Less");
                } else {
                    txtMessage.setMaxLines(Constant.MAX_LINE_TEXTVIEW_MORE_2);
                    lytBtnMore.setVisibility(View.VISIBLE);
                    txtMoreLess.setText("More");
                    imgMoreLessArrow.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_down));
                    mItems.get(getAdapterPosition()).setStrMore("More");
                    item.setStrMore("More");
                }
            });

            lytBtnReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemQuestionClick(v, item, getAdapterPosition(), "reply");
                    }
                }
            });


            txtMoreReplyQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemQuestionClick(v, item, getAdapterPosition(), "reply");
                    }
                }
            });


            linearUserProfile.setOnClickListener(v -> {
                Bundle bundleProfile = new Bundle();
                bundleProfile.putInt(Constant.userID, item.getUser().getId());
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtras(bundleProfile);
                context.startActivity(intent);
            });
            PublicChatListAdapter publicChatListAdapter = new PublicChatListAdapter(context, new ArrayList<>());
            recyclerViewQuestionsChat.setHasFixedSize(true);
            recyclerViewQuestionsChat.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            recyclerViewQuestionsChat.setAdapter(publicChatListAdapter);
            recyclerViewQuestionsChat.setNestedScrollingEnabled(true);
            publicChatListAdapter.addItems(item.getComments());
            publicChatListAdapter.addExtraItems(item, false);
            publicChatListAdapter.setOnItemClickListener(this);
        }

        @Override
        public void onItemClick(View view, OfferModel obj, int position, String action) {

        }

        @Override
        public void onItemClick(View view, QuestionModel obj, int position, String action) {
            if (action.equalsIgnoreCase("reply")) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemQuestionClick(view, obj, getAdapterPosition(), "reply");
                }
            }
        }

        @Override
        public void onItemClick(View view, CommentModel obj, int position, String action) {

        }

        @Override
        public void onItemClick(View view, AttachmentModel obj, int position, String action) {

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




