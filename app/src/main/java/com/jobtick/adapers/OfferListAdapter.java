package com.jobtick.adapers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.activities.ReportActivity;
import com.jobtick.activities.UserProfileActivity;
import com.jobtick.activities.VideoPlayerActivity;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.SessionManager;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.jobtick.R;
import com.jobtick.TextView.TextViewBold;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.models.CommentModel;
import com.jobtick.models.OfferModel;
import com.jobtick.models.QuestionModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

import static com.jobtick.activities.TaskDetailsActivity.widthDrawListener;
import static com.jobtick.utils.ConstantKey.KEY_OFFER_REPORT;
import static com.jobtick.utils.ConstantKey.KEY_USER_REPORT;
import static com.jobtick.utils.ConstantKey.VIDEO_PATH;


public class OfferListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;

    private boolean isMyTask = false;
    SessionManager sessionManager;

    private Context context;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemOfferClick(View view, OfferModel obj, int position, String action);

        //  void onItemClick(View view, OfferChatModel obj, int position, String action);
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }


    private boolean isLoaderVisible = false;
    private List<OfferModel> mItems;

    public OfferListAdapter(Context context, boolean isMyTask, List<OfferModel> mItems) {
        this.mItems = mItems;
        this.isMyTask = isMyTask;
        this.context = context;
        sessionManager = new SessionManager(context);

    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        @BindView(R.id.img_avatar)
        CircularImageView imgAvatar;
        @BindView(R.id.rlt_profile)
        RelativeLayout rltProfile;
        @BindView(R.id.txt_name)
        TextView txtName;
        @BindView(R.id.ratingbar_worker)
        AppCompatRatingBar ratingbarWorker;
        @BindView(R.id.txt_rating_value)
        TextView txtRatingValue;
        @BindView(R.id.txt_completion_rate)
        TextView txtCompletionRate;
        @BindView(R.id.txt_budget)
        TextView txtBudget;
        @BindView(R.id.txt_btn_accept)
        TextView txtBtnAccept;
        @BindView(R.id.card_accept)
        CardView cardAccept;
        @BindView(R.id.lyt_budget_status)
        LinearLayout lytBudgetStatus;
        /*        @BindView(R.id.txt_type)
                TextView txtType;*/
        @BindView(R.id.txt_created_date)
        TextView txtCreatedDate;
        @BindView(R.id.txt_message)
        TextView txtMessage;
        @BindView(R.id.img_offer_on_task)
        ImageView imgOfferOnTask;
        @BindView(R.id.img_btn_play)
        ImageView imgBtnPlay;
        @BindView(R.id.card_live_video)
        CardView cardLiveVideo;
        @BindView(R.id.lyt_btn_reply)
        LinearLayout lytBtnReply;
        @BindView(R.id.img_more_less_arrow)
        ImageView imgMoreLessArrow;
        @BindView(R.id.txt_more_less)
        TextView txtMoreLess;
        @BindView(R.id.lyt_btn_more)
        LinearLayout lytBtnMore;
        @BindView(R.id.txt_more_reply)
        TextView txtMoreReply;

        @BindView(R.id.linear_more_reply)
        LinearLayout linearMoreReply;

        @BindView(R.id.recycler_view_offer_chat)
        RecyclerView recyclerViewOfferChat;

        @BindView(R.id.card_deleteOffer)
        CardView cardDeleteOffer;

        @BindView(R.id.img_verified_account)
        ImageView imgVerifiedAccount;

        @BindView(R.id.linear_user_profile)
        LinearLayout linearUserProfile;

/*        @BindView(R.id.textViewOptions)
        TextView textViewOptions;*/

        @BindView(R.id.ivFlag)
        ImageView ivFlag;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        protected void clear() {
        }


        public void onBind(int position) {
            super.onBind(position);
            OfferModel item = mItems.get(position);
            if (item.getWorker().getAvatar() != null) {
                ImageUtil.displayImage(imgAvatar, item.getWorker().getAvatar().getThumbUrl(), null);
            }
            if (isMyTask) {
                txtBudget.setVisibility(View.VISIBLE);
                cardAccept.setVisibility(View.VISIBLE);
            } else {
                txtBudget.setVisibility(View.GONE);
                cardAccept.setVisibility(View.GONE);
            }
            if (item.getReply()) {
                lytBtnReply.setVisibility(View.VISIBLE);
            } else {
                lytBtnReply.setVisibility(View.GONE);
            }

            if (item.getCommentsTotal() > 3) {
                int remaining_number = item.getCommentsTotal() - 3;
                if (remaining_number == 1) {
                    txtMoreReply.setText("view 1 reply");
                } else {
                    txtMoreReply.setText("view" + remaining_number + "  replies");
                }
                linearMoreReply.setVisibility(View.VISIBLE);
            } else {
                linearMoreReply.setVisibility(View.GONE);

            }

            if (item.getWorker().getId() == sessionManager.getUserAccount().getId()) {
                cardDeleteOffer.setVisibility(View.VISIBLE);
                txtBudget.setVisibility(View.VISIBLE);

                ivFlag.setVisibility(View.GONE);
            } else {
                cardDeleteOffer.setVisibility(View.GONE);
                ivFlag.setVisibility(View.VISIBLE);

            }

            if (item.getWorker().getIsVerifiedAccount() == 1) {
                imgVerifiedAccount.setVisibility(View.VISIBLE);
            } else {
                imgVerifiedAccount.setVisibility(View.GONE);

            }
            txtBudget.setText("$ " + item.getOfferPrice());
            txtName.setText(item.getWorker().getName());
            if (item.getWorker() != null && item.getWorker().getWorkerRatings() != null && item.getWorker().getWorkerRatings().getAvgRating() != null) {
                txtRatingValue.setText("(" + item.getWorker().getWorkerRatings().getAvgRating() + ")");
                ratingbarWorker.setProgress(item.getWorker().getWorkerRatings().getAvgRating());

            }
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
                Bundle bundleProfile = new Bundle();
                bundleProfile.putInt(Constant.userID, item.getWorker().getId());
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtras(bundleProfile);
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
                    imgMoreLessArrow.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_up));
                    mItems.get(getAdapterPosition()).setStrMore("Less");
                    item.setStrMore("Less");
                } else {
                    txtMessage.setMaxLines(Constant.MAX_LINE_TEXTVIEW_MORE_4);
                    lytBtnMore.setVisibility(View.VISIBLE);
                    txtMoreLess.setText("More");
                    imgMoreLessArrow.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_down));
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




