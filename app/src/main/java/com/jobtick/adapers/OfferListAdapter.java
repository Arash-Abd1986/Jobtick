package com.jobtick.adapers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.activities.ProfileActivity;
import com.jobtick.activities.ReportActivity;
import com.jobtick.activities.TaskDetailsActivity;
import com.jobtick.activities.UserProfileActivity;
import com.jobtick.activities.VideoPlayerActivity;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.SessionManager;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.jobtick.R;
import com.jobtick.models.CommentModel;
import com.jobtick.models.OfferModel;
import com.jobtick.models.QuestionModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ImageUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jobtick.activities.TaskDetailsActivity.widthDrawListener;
import static com.jobtick.utils.ConstantKey.KEY_OFFER_REPORT;
import static com.jobtick.utils.ConstantKey.VIDEO_PATH;


public class OfferListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;

    private final boolean isMyTask;
    SessionManager sessionManager;

    private final Context context;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemOfferClick(View view, OfferModel obj, int position, String action);

        //  void onItemClick(View view, OfferChatModel obj, int position, String action);
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }


    private boolean isLoaderVisible = false;
    private final List<OfferModel> mItems;

    public OfferListAdapter(Context context, boolean isMyTask, List<OfferModel> mItems) {
        this.mItems = mItems;
        this.isMyTask = isMyTask;
        this.context = context;
        sessionManager = new SessionManager(context);

    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offers, parent, false));
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

    public void addItems(List<OfferModel> mItems) {
        this.mItems.addAll(mItems);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        this.mItems.add(new OfferModel());
        notifyItemInserted(this.mItems.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = mItems.size() - 1;
        OfferModel item = getItem(position);
        if (item != null) {
            mItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    private OfferModel getItem(int position) {
        return mItems.get(position);
    }

    public class ViewHolder extends BaseViewHolder implements PublicChatListAdapter.OnItemClickListener {
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_avatar)
        CircularImageView imgAvatar;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.rlt_profile)
        RelativeLayout rltProfile;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_name)
        TextView txtName;
//        @BindView(R.id.ratingbar_worker)
//        AppCompatRatingBar ratingbarWorker;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_rating_value)
        TextView txtRatingValue;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_completion_rate)
        TextView txtCompletionRate;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_budget)
        TextView txtBudget;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_btn_accept)
        TextView txtBtnAccept;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.card_accept)
        CardView cardAccept;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.lyt_budget_status)
        LinearLayout lytBudgetStatus;
        /*        @BindView(R.id.txt_type)
                TextView txtType;*/
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_created_date)
        TextView txtCreatedDate;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_message)
        TextView txtMessage;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_offer_on_task)
        ImageView imgOfferOnTask;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_btn_play)
        ImageView imgBtnPlay;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.card_live_video)
        CardView cardLiveVideo;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.lyt_btn_reply)
        LinearLayout lytBtnReply;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_more_less_arrow)
        ImageView imgMoreLessArrow;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_more_less)
        TextView txtMoreLess;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.lyt_btn_more)
        LinearLayout lytBtnMore;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_more_reply)
        TextView txtMoreReply;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.linearAcceptDeleteOffer)
        LinearLayout linearAcceptDeleteOffer;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.linear_more_reply)
        LinearLayout linearMoreReply;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.recycler_view_offer_chat)
        RecyclerView recyclerViewOfferChat;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.card_deleteOffer)
        CardView cardDeleteOffer;


        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.linear_user_profile)
        LinearLayout linearUserProfile;

/*        @BindView(R.id.textViewOptions)
        TextView textViewOptions;*/

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.ivFlag)
        LinearLayout ivFlag;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        protected void clear() {
        }


        @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
        public void onBind(int position) {
            super.onBind(position);
            OfferModel item = mItems.get(position);
            imgAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    linearUserProfile.performClick();
                }
            });
            if (item.getWorker().getAvatar() != null) {
                ImageUtil.displayImage(imgAvatar, item.getWorker().getAvatar().getThumbUrl(), null);
            }
            if (isMyTask) {
                txtBudget.setVisibility(View.VISIBLE);
                cardAccept.setVisibility(View.VISIBLE);
                linearAcceptDeleteOffer.setVisibility(View.VISIBLE);
            } else {
                txtBudget.setVisibility(View.GONE);
                cardAccept.setVisibility(View.GONE);
                linearAcceptDeleteOffer.setVisibility(View.GONE);
            }
            if (item.getReply()) {
                lytBtnReply.setVisibility(View.VISIBLE);
            } else {
                lytBtnReply.setVisibility(View.VISIBLE);
            }

            if (item.getCommentsTotal() > 3) {
                int remaining_number = item.getCommentsTotal() - 3;
                if (remaining_number == 1) {
                    txtMoreReply.setText("view 1 reply");
                } else {
                    txtMoreReply.setText("view " + remaining_number + " replies");
                    txtMoreReply.setTextColor(context.getResources().getColor(R.color.grey_70));
                }
                linearMoreReply.setVisibility(View.VISIBLE);
            } else {
                linearMoreReply.setVisibility(View.GONE);
            }

            if (item.getWorker().getId().equals(sessionManager.getUserAccount().getId())) {
                cardDeleteOffer.setVisibility(View.VISIBLE);
                txtBudget.setVisibility(View.VISIBLE);
                linearAcceptDeleteOffer.setVisibility(View.VISIBLE);

                ivFlag.setVisibility(View.GONE);
            } else {
                cardDeleteOffer.setVisibility(View.GONE);
                ivFlag.setVisibility(View.VISIBLE);

            }


            txtBudget.setText("$ " + item.getOfferPrice());
            txtName.setText(item.getWorker().getName());
            if (item.getWorker() != null && item.getWorker().getWorkerRatings() != null && item.getWorker().getWorkerRatings().getAvgRating() != null) {
                txtRatingValue.setText("(" + item.getWorker().getWorkerRatings().getAvgRating() + ")");
//                ratingbarWorker.setProgress(item.getWorker().getWorkerRatings().getAvgRating());

            }
            assert item.getWorker() != null;
            txtCompletionRate.setText(item.getWorker().getWorkTaskStatistics().getCompletionRate() + "%");
            txtCreatedDate.setText(item.getCreatedAt());
            if (item.getAttachments() != null && item.getAttachments().size() != 0) {
                cardLiveVideo.setVisibility(View.VISIBLE);
                txtMessage.setVisibility(View.GONE);
                //txtType.setText("Video Offer");
                ImageUtil.displayImage(imgOfferOnTask, item.getAttachments().get(0).getModalUrl(), null);
                lytBtnMore.setVisibility(View.GONE);
            } else {
                cardLiveVideo.setVisibility(View.GONE);
                txtMessage.setVisibility(View.VISIBLE);
                //  txtType.setText("Message");
                txtMessage.setText(item.getMessage());

               /*
                txtMessage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        txtMessage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int lineCount = txtMessage.getLineCount();
                        Log.e("valueline", "Number of lines is " + lineCount);
                        if (lineCount > Constant.MAX_LINE_TEXTVIEW_MORE) {
                            txtMessage.setMaxLines(Constant.MAX_LINE_TEXTVIEW_MORE);
                            lytBtnMore.setVisibility(View.VISIBLE);
                            txtMoreLess.setText(item.getStrMore());
                            mItems.get(getAdapterPosition()).setMore(true);
                        } else {
                            lytBtnMore.setVisibility(View.GONE);
                        }
                    }
                });

*/
                txtMessage.post(() -> {
                    int lineCount = txtMessage.getLineCount();
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


                imgBtnPlay.setOnClickListener(v -> {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemOfferClick(v, item, getAdapterPosition(), "play_video");
                    }
                });

            }

            imgOfferOnTask.setOnClickListener(v -> {
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra(VIDEO_PATH, item.getAttachments().get(0).getUrl());
                context.startActivity(intent);
            });

            linearUserProfile.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("id",item.getWorker().getId());
                context.startActivity(intent);
            });

            cardDeleteOffer.setOnClickListener(v -> {

                if (widthDrawListener != null) {
                    widthDrawListener.onWidthdraw(item.getId());
                }


            });
            lytBtnMore.setOnClickListener(v -> {
                if (item.getStrMore().equalsIgnoreCase("More")) {
                    txtMessage.setMaxLines(Integer.MAX_VALUE);
                    lytBtnMore.setVisibility(View.VISIBLE);
                    txtMoreLess.setText("Less");
                    imgMoreLessArrow.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_up_blue));
                    mItems.get(getAdapterPosition()).setStrMore("Less");
                    item.setStrMore("Less");
                } else {
                    txtMessage.setMaxLines(Constant.MAX_LINE_TEXTVIEW_MORE_4);
                    lytBtnMore.setVisibility(View.VISIBLE);
                    txtMoreLess.setText("More");
                    imgMoreLessArrow.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_down_blue));
                    mItems.get(getAdapterPosition()).setStrMore("More");
                    item.setStrMore("More");
                }
            });

            lytBtnReply.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemOfferClick(v, item, getAdapterPosition(), "reply");
                }
            });

            txtBtnAccept.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemOfferClick(v, item, getAdapterPosition(), "accept");
                }
            });

            txtMoreReply.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemOfferClick(v, item, getAdapterPosition(), "reply");
                }
            });


            ivFlag.setOnClickListener(view -> {

                Bundle bundle = new Bundle();
                Intent intent = new Intent(context, ReportActivity.class);
                bundle.putString("key", KEY_OFFER_REPORT);
                bundle.putInt(ConstantKey.offerId, item.getId());
                intent.putExtras(bundle);
                context.startActivity(intent);
/*
                //creating a popup menutextViewOptions
                PopupMenu popup = new PopupMenu(context, ivFlag);
                //inflating menu from xml resource
                popup.inflate(R.menu.menu_report);
                //adding click listener
                popup.setOnMenuItemClickListener(item1 -> {
                    switch (item1.getItemId()) {
                        case R.id.action_report:

                            break;
                    }
                    return false;
                });
                //displaying the popup
                popup.show();*/

            });


            PublicChatListAdapter publicChatListAdapter = new PublicChatListAdapter(context, new ArrayList<>());
            recyclerViewOfferChat.setHasFixedSize(true);
            recyclerViewOfferChat.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            recyclerViewOfferChat.setAdapter(publicChatListAdapter);
            recyclerViewOfferChat.setNestedScrollingEnabled(true);
            publicChatListAdapter.addItems(item.getComments());
            publicChatListAdapter.addExtraItems(item, true);
            publicChatListAdapter.setOnItemClickListener(this);
        }

        @Override
        public void onItemClick(View view, OfferModel obj, int position, String action) {
            if (action.equalsIgnoreCase("reply")) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemOfferClick(view, obj, getAdapterPosition(), "reply");
                }
            }
        }

        @Override
        public void onItemClick(View view, QuestionModel obj, int position, String action) {

        }

        @Override
        public void onItemClick(View view, CommentModel obj, int position, String action) {

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




