package com.jobtick.android.adapers;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.android.activities.ProfileActivity;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.jobtick.android.R;

import android.annotation.SuppressLint;

import timber.log.Timber;

import com.jobtick.android.models.AttachmentModel;
import com.jobtick.android.models.CommentModel;
import com.jobtick.android.models.OfferModel;
import com.jobtick.android.models.QuestionModel;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestionListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;

    private final Context context;
    private String status;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemQuestionClick(View view, QuestionModel obj, int position, String action);

    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }


    private boolean isLoaderVisible = false;
    private final List<QuestionModel> mItems;

    public QuestionListAdapter(Context context, List<QuestionModel> mItems, String status) {
        this.mItems = mItems;
        this.context = context;
        this.status = status;
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

    public class ViewHolder extends BaseViewHolder implements PublicChatListAdapter.OnItemClickListener, AttachmentAdapter2.OnItemClickListener {
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_avatar)
        CircularImageView imgAvatar;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_name)
        TextView txtName;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_created_date)
        TextView txtCreatedDate;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_message)
        TextView txtMessage;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.recycler_view_question)
        RecyclerView recyclerViewQuestion;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_file)
        ImageView imgFile;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.card_img_file)
        CardView cardImgFile;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.lyt_btn_reply)
        LinearLayout lytBtnReply;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_more_less)
        TextView txtMoreLess;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.lyt_btn_more)
        LinearLayout lytBtnMore;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_more_reply_question)
        TextView txtMoreReplyQuestion;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.recycler_view_questions_chat)
        RecyclerView recyclerViewQuestionsChat;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.ivReport)
        ImageView ivReport;


        @SuppressLint("NonConstantResourceId")
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
            if (status.equals(Constant.TASK_OPEN))
                lytBtnReply.setVisibility(View.VISIBLE);
            else
                lytBtnReply.setVisibility(View.GONE);

            if (item.getCommentsCount() > 3) {
                int remaining_number = item.getCommentsCount() - 3;
                if (remaining_number == 1) {
                    txtMoreReplyQuestion.setText(R.string.one_more_reply);
                } else {
                    txtMoreReplyQuestion.setText(String.format(Locale.ENGLISH, "%d %d", remaining_number, R.string.more_replies));
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
                AttachmentAdapter2 attachmentAdapter = new AttachmentAdapter2(item.getAttachments(), false, context);
                recyclerViewQuestion.setHasFixedSize(true);
                recyclerViewQuestion.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                recyclerViewQuestion.setAdapter(attachmentAdapter);
                recyclerViewQuestion.setNestedScrollingEnabled(true);
                attachmentAdapter.setOnItemClickListener(this);
            } else {
                recyclerViewQuestion.setVisibility(View.GONE);
            }

            txtMessage.post(() -> {

                int lineCount = txtMessage.getLineCount();
                Timber.tag("COUNT").e(String.valueOf(lineCount));
                if (lineCount > Constant.MAX_LINE_TEXTVIEW_MORE_4) {
                    // view.setMaxLines(Constant.MAX_LINE_TEXTVIEW_MORE);
                    lytBtnMore.setVisibility(View.VISIBLE);
                    txtMoreLess.setText(item.getStrMore());
                    mItems.get(getAdapterPosition()).setIsUserPrefrenceToMore(true);
                    if (item.getIsUserPrefrenceToMore()) {
                        txtMessage.setMaxLines(Constant.MAX_LINE_TEXTVIEW_MORE_4);
                    }
                } else {
                    lytBtnMore.setVisibility(View.GONE);
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
                    txtMoreLess.setText(R.string.less);
                    mItems.get(getAdapterPosition()).setStrMore("Less");
                    item.setStrMore("Less");
                } else {
                    txtMessage.setMaxLines(Constant.MAX_LINE_TEXTVIEW_MORE_4);
                    lytBtnMore.setVisibility(View.VISIBLE);
                    txtMoreLess.setText(R.string.more);
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

            ivReport.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemQuestionClick(v, item, getAdapterPosition(), "report");
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
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("id", item.getUser().getId());
                context.startActivity(intent);
            });
            imgAvatar.setOnClickListener(v -> linearUserProfile.performClick());
            txtName.setOnClickListener(v -> linearUserProfile.performClick());
            PublicChatListAdapter publicChatListAdapter = new PublicChatListAdapter(context, new ArrayList<>(),status);
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




