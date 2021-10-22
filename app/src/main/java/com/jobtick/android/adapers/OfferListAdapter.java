package com.jobtick.android.adapers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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

import com.jobtick.android.activities.ProfileActivity;
import com.jobtick.android.activities.ReportActivity;
import com.jobtick.android.activities.VideoPlayerActivity;
import com.jobtick.android.utils.ConstantKey;
import com.jobtick.android.utils.SessionManager;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.jobtick.android.R;

import android.annotation.SuppressLint;

import com.jobtick.android.models.CommentModel;
import com.jobtick.android.models.OfferModel;
import com.jobtick.android.models.QuestionModel;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.ImageUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jobtick.android.activities.TaskDetailsActivity.widthDrawListener;
import static com.jobtick.android.utils.ConstantKey.KEY_OFFER_REPORT;
import static com.jobtick.android.utils.ConstantKey.VIDEO_PATH;


public class OfferListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;

    private final boolean isMyTask;
    SessionManager sessionManager;
    Spannable spanS;
    Spannable spanF;
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
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offers_v2, parent, false));
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
        @BindView(R.id.my_offer)
        TextView myOffer;
        @BindView(R.id.txt_budget2)
        TextView txtBudget2;
        @BindView(R.id.txt_reply)
        TextView txtReply;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_btn_accept)
        TextView txtBtnAccept;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.card_accept)
        CardView cardAccept;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.lyt_budget_status)
        LinearLayout lytBudgetStatus;
        /*        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_type)
                TextView txtType;*/
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_created_date)
        TextView txtCreatedDate;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_created_date2)
        TextView txtCreatedDate2;
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
        @BindView(R.id.lin_message)
        LinearLayout linOfferMessage;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.lin_budget)
        LinearLayout linBudget;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.linear_more_reply)
        LinearLayout linearMoreReply;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.recycler_view_offer_chat)
        RecyclerView recyclerViewOfferChat;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.ratingbar_worker)
        ImageView starRatingBar;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_reply)
        ImageView imgReply;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.card_deleteOffer)
        LinearLayout cardDeleteOffer;


        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.linear_user_profile)
        LinearLayout linearUserProfile;

/*        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.textViewOptions)
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
            imgAvatar.setOnClickListener(v -> linearUserProfile.performClick());
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
            if (item.getReply() != null)
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

            txtBudget.setText("$" + item.getOfferPrice());
            myOffer.setText("$" + item.getOfferPrice());
            txtBudget2.setText("$" + item.getOfferPrice());
            if (item.getWorker().getId().equals(sessionManager.getUserAccount().getId())) {
                cardDeleteOffer.setVisibility(View.VISIBLE);
            } else {
                cardDeleteOffer.setVisibility(View.GONE);
            }


            txtName.setText(item.getWorker().getName());
            if (item.getWorker() != null && item.getWorker().getWorkerRatings() != null && item.getWorker().getWorkerRatings().getAvgRating() != null) {
                txtRatingValue.setText(String.format(java.util.Locale.US, "%.1f", item.getWorker().getWorkerRatings().getAvgRating()) + " (" + item.getWorker().getWorkerRatings().getReceivedReviews() + ")");

//                ratingbarWorker.setProgress(item.getWorker().getWorkerRatings().getAvgRating());

            } else {
                starRatingBar.setVisibility(View.GONE);
                txtRatingValue.setText("No review");
            }
            assert item.getWorker() != null;
            txtCompletionRate.setText(item.getWorker().getWorkTaskStatistics().getCompletionRate() + "%");
            txtCreatedDate.setText(item.getCreatedAt());
            txtCreatedDate2.setText(item.getCreatedAt());
            if (item.getAttachments() != null && item.getAttachments().size() != 0) {
                cardLiveVideo.setVisibility(View.VISIBLE);
                txtMessage.setVisibility(View.GONE);
                //txtType.setText("Video Offer");
                ImageUtil.displayImage(imgOfferOnTask, item.getAttachments().get(0).getModalUrl(), null);
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
                intent.putExtra("id", item.getWorker().getId());
                context.startActivity(intent);
            });

            cardDeleteOffer.setOnClickListener(v -> {

                if (widthDrawListener != null) {
                    widthDrawListener.onWithdraw(item.getId());
                }


            });


//            lytBtnReply.setOnClickListener(v -> {
//                if (mOnItemClickListener != null) {
//                    mOnItemClickListener.onItemOfferClick(v, item, getAdapterPosition(), "reply");
//                }
//            });

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

            txtReply.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemOfferClick(v, item, getAdapterPosition(), "reply");
                }
            });

            linOfferMessage.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemOfferClick(v, item, getAdapterPosition(), "message");
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

            if (isMyTask) {
                txtReply.setVisibility(View.GONE);
                txtBudget.setVisibility(View.VISIBLE);
                linOfferMessage.setVisibility(View.VISIBLE);
                linearAcceptDeleteOffer.setVisibility(View.VISIBLE);
                linBudget.setVisibility(View.GONE);
                myOffer.setVisibility(View.GONE);
                imgReply.setVisibility(View.GONE);
                ivFlag.setVisibility(View.VISIBLE);
                txtCreatedDate.setVisibility(View.VISIBLE);
                txtCreatedDate2.setVisibility(View.GONE);
                cardDeleteOffer.setVisibility(View.GONE);
            } else {
                ivFlag.setVisibility(View.GONE);
                linOfferMessage.setVisibility(View.GONE);
                txtReply.setVisibility(View.GONE);
                txtBudget.setVisibility(View.GONE);
                linBudget.setVisibility(View.VISIBLE);
                imgReply.setVisibility(View.GONE);
                linearAcceptDeleteOffer.setVisibility(View.VISIBLE);
                if (item.getWorker().getId().equals(sessionManager.getUserAccount().getId())) {
                    linearAcceptDeleteOffer.setVisibility(View.GONE);
                    cardDeleteOffer.setVisibility(View.VISIBLE);
                    myOffer.setVisibility(View.VISIBLE);
                    txtMessage.setVisibility(View.VISIBLE);
                    txtCreatedDate.setVisibility(View.VISIBLE);
                    txtCreatedDate2.setVisibility(View.GONE);

                } else {
                    txtCreatedDate.setVisibility(View.GONE);
                    txtCreatedDate2.setVisibility(View.VISIBLE);
                    myOffer.setVisibility(View.GONE);
                    txtMessage.setVisibility(View.GONE);
                    linearAcceptDeleteOffer.setVisibility(View.GONE);
                    cardDeleteOffer.setVisibility(View.GONE);
                }
            }

            PublicChatListAdapter publicChatListAdapter = new PublicChatListAdapter(context, new ArrayList<>(), "", 0);
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




