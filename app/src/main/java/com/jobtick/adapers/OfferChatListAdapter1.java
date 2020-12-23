package com.jobtick.adapers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.jobtick.R;
import android.annotation.SuppressLint;
import android.widget.TextView;

import com.jobtick.text_view.TextViewBold;
import com.jobtick.text_view.TextViewRegular;
import com.jobtick.models.CommentModel;
import com.jobtick.models.OfferModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class OfferChatListAdapter1 extends RecyclerView.Adapter<BaseViewHolder> {

    /*
     * Note:
     * In OfferChatList
     * i am added OfferListModel Model Class
     * because reply btn i want work both side
     * */
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;


    private final Context context;
    private OnItemClickListener mOnItemClickListener;
    private OfferModel offerModel;

    public void addExtraItems(OfferModel item) {
        this.offerModel = item;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, OfferModel obj, int position, String action);

        void onItemClick(View view, CommentModel obj, int position, String action);
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }


    private boolean isLoaderVisible = false;
    private final List<CommentModel> mItems;

    public OfferChatListAdapter1(Context context, List<CommentModel> mItems) {
        this.mItems = mItems;
        this.context = context;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offer_chat, parent, false));
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

    public void addItems(List<CommentModel> mItems) {
        this.mItems.addAll(mItems);
        notifyDataSetChanged();
    }

    public void addItem(CommentModel mItem) {
        this.mItems.add(mItem);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        this.mItems.add(new CommentModel());
        notifyItemInserted(this.mItems.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = mItems.size() - 1;
        CommentModel item = getItem(position);
        if (item != null) {
            mItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    private CommentModel getItem(int position) {
        return mItems.get(position);
    }

    public class ViewHolder extends BaseViewHolder {
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
        @BindView(R.id.txt_more_less)
        TextView txtMoreLess;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_file)
        ImageView imgFile;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.lyt_btn_reply)
        LinearLayout lytBtnReply;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.lyt_btn_more)
        LinearLayout lytBtnMore;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_more_less_arrow)
        ImageView imgMoreLessArrow;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.card_img_file)
        CardView cardImgFile;


        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        protected void clear() {
        }


        public void onBind(int position) {
            super.onBind(position);
            CommentModel item = mItems.get(position);
//            if(item.getUser().getAvatar() != null)
//            ImageUtil.displayImage(imgAvatar, item.getUser().getAvatar().getThumbUrl(), null);
//            txtName.setText(item.getUser().getName());
//            txtMessage.setText(item.getCommentText());
//            txtCreatedDate.setText(item.getCreatedAt());
//
//            txtMessage.setOnLayoutListener(new TextViewRegular.OnLayoutListener() {
//                @Override
//                public void onLayouted(TextView view) {
//                    if (mItems.get(getAdapterPosition()).getIsUserPrefrenceToMore() == null) {
//                       // txtMessage.setMaxLines(Integer.MAX_VALUE);
//                        int lineCount = view.getLineCount();
//                        Log.e("Count",lineCount+"");
//                        if (lineCount > Constant.MAX_LINE_TEXTVIEW_MORE) {
//                            txtMessage.setMaxLines(Constant.MAX_LINE_TEXTVIEW_MORE);
//                            Log.e("Count","VISIBLE");
//                            lytBtnMore.setVisibility(View.VISIBLE);
//                        } else {
//                            lytBtnMore.setVisibility(View.GONE);
//                        }
//                    } else {
//                        if (item.getIsUserPrefrenceToMore()) {
//                            txtMessage.setMaxLines(Integer.MAX_VALUE);
//                            mItems.get(position).setStrMore("Less");
//                        } else {
//                            txtMessage.setMaxLines(Constant.MAX_LINE_TEXTVIEW_MORE);
//                            mItems.get(position).setStrMore("More");
//                        }
//                    }
//                    // Setting UI for lessMore button ...
//                    txtMoreLess.setText(item.getStrMore());
//                }
//            });
//            /*ViewTreeObserver viewTreeObserver = txtMessage.getViewTreeObserver();
//            if(viewTreeObserver.isAlive()) {
//                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
//                        txtMessage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                        if (mItems.get(position).getIsUserPrefrenceToMore() == null) {
//                            txtMessage.setMaxLines(Integer.MAX_VALUE);
//                            int lineCount = txtMessage.getLineCount();
//                            if (lineCount > Constant.MAX_LINE_TEXTVIEW_MORE) {
//                                txtMessage.setMaxLines(Constant.MAX_LINE_TEXTVIEW_MORE);
//                                lytBtnMore.setVisibility(View.VISIBLE);
//                            } else {
//                                lytBtnMore.setVisibility(View.GONE);
//                            }
//                        } else {
//                            if (item.getIsUserPrefrenceToMore()) {
//                                txtMessage.setMaxLines(Integer.MAX_VALUE);
//                                mItems.get(position).setStrMore("Less");
//                            } else {
//                                txtMessage.setMaxLines(Constant.MAX_LINE_TEXTVIEW_MORE);
//                                mItems.get(position).setStrMore("More");
//                            }
//                        }
//                        // Setting UI for lessMore button ...
//                        txtMoreLess.setText(item.getStrMore());
//                    }
//                });
//            }*/
//
//          /*  txtMessage.setMaxLines(Integer.MAX_VALUE);
//            int lineCount = txtMessage.getLineCount();
//            Log.e("valueline", "Number of lines is " + lineCount);
//            if (lineCount > Constant.MAX_LINE_TEXTVIEW_MORE) {
//                txtMessage.setMaxLines(Constant.MAX_LINE_TEXTVIEW_MORE);
//                lytBtnMore.setVisibility(View.VISIBLE);
//                txtMoreLess.setText(item.getStrMore());
//                if(mItems.get(position).getIsUserPrefrenceToMore() == null) {
//                    mItems.get(position).setIsUserPrefrenceToMore(true);
//                }
//            } else {
//                lytBtnMore.setVisibility(View.GONE);
//
//                if(mItems.get(position).getIsUserPrefrenceToMore() == null) {
//                    mItems.get(position).setIsUserPrefrenceToMore(false);
//                }
//            }*/
//
//            if (item.getAttachments() != null && item.getAttachments().size() != 0) {
//                cardImgFile.setVisibility(View.VISIBLE);
//                ImageUtil.displayImage(imgFile, item.getAttachments().get(0).getThumbUrl(), null);
//            } else {
//                cardImgFile.setVisibility(View.GONE);
//            }
//            if (item.getReply()) {
//                lytBtnReply.setVisibility(View.VISIBLE);
//            } else {
//                lytBtnReply.setVisibility(View.GONE);
//            }
//            lytBtnReply.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mOnItemClickListener != null) {
//                        mOnItemClickListener.onItemClick(v, offerModel, getAdapterPosition(), "reply");
//                    }
//                }
//            });
//
//         /*   lytBtnMore.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                   *//* String show = toggleLayoutExpand(item.getStrMore().equalsIgnoreCase("More") ? "Less" : "More", imgMoreLessArrow);
//                    if(show.equalsIgnoreCase("More")){
//                        txtMessage.setMaxLines(Constant.MAX_LINE_TEXTVIEW_MORE);
//                    }else{
//                        txtMessage.setMaxLines(Integer.MAX_VALUE);
//                    }
//                    mItems.get(position).setStrMore(show);
//                    txtMoreLess.setText(show);*//*
//
//                    if (item.getStrMore().equalsIgnoreCase("More")) {
//                        txtMessage.setMaxLines(Integer.MAX_VALUE);
//                        txtMoreLess.setText("Less");
//                        imgMoreLessArrow.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_up));
//                        mItems.get(position).setStrMore("Less");
//                        item.setStrMore("Less");
//                    } else {
//                        txtMessage.setMaxLines(Constant.MAX_LINE_TEXTVIEW_MORE);
//                        txtMoreLess.setText("More");
//                        imgMoreLessArrow.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_down));
//                        mItems.get(position).setStrMore("More");
//                        item.setStrMore("More");
//                    }
//                }
//            });*/
//
//            lytBtnMore.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(item.getIsUserPrefrenceToMore() == null){
//                        mItems.get(position).setIsUserPrefrenceToMore(true);
//                    }else{
//                        mItems.get(position).setIsUserPrefrenceToMore(!item.getIsUserPrefrenceToMore());
//                    }
//                    notifyDataSetChanged();
//                }
//            });
//        }
//    }
//
//    private String toggleLayoutExpand(String show, View view) {
//        toggleArrow(show, view);
       /* if (show.equalsIgnoreCase("Less")) {

            ViewAnimation.expand(lyt_expand);
        } else {
            ViewAnimation.collapse(lyt_expand);
        }*/
            //       return show;
        }


        public boolean toggleArrow(String show, View view) {
            return toggleArrow(show, view, true);
        }

        public boolean toggleArrow(String show, View view, boolean delay) {
            if (show.equalsIgnoreCase("Less")) {
                view.animate().setDuration(delay ? 200 : 0).rotation(180);
                return true;
            } else {
                view.animate().setDuration(delay ? 200 : 0).rotation(0);
                return false;
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




