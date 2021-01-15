package com.jobtick.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.jobtick.android.R;
import android.annotation.SuppressLint;

import com.jobtick.android.activities.ActivityBase;
import com.jobtick.android.activities.MakeAnOfferActivity;
import com.jobtick.android.activities.VideoPlayerActivity;
import com.jobtick.android.models.MakeAnOfferModel;
import com.jobtick.android.models.UserAccountModel;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.ConstantKey;
import com.jobtick.android.utils.ExternalIntentHelper;
import com.jobtick.android.utils.ImageUtil;
import com.jobtick.android.utils.SessionManager;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MakeAnOfferReviewFragment extends Fragment implements View.OnClickListener {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_info)
    ImageView imgInfo;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_total_budget)
    TextView txtTotalBudget;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_service_fee)
    TextView txtServiceFee;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_receive_budget)
    TextView txtReceiveBudget;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_btn_close)
    ImageView imgBtnClose;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_btn_play)
    ImageView imgBtnPlay;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_review_conditions)
    TextView txtReviewConditions;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_submit_offer)
    MaterialButton lytBtnSubmitOffer;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lytRecord2)
    LinearLayout lytRecord2;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_offer_on_task)
    ImageView imgOfferOnTask;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.card_live_video)
    FrameLayout cardLiveVideo;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.ivBack)
    ImageView ivBack;

    private MakeAnOfferModel makeAnOfferModel;
    private MakeAnOfferActivity makeAnOfferActivity;
    private ReviewCallbackFunction reviewCallbackFunction;
    private SessionManager sessionManager;
    private UserAccountModel userAccountModel;

    public MakeAnOfferReviewFragment() {
        // Required empty public constructor
    }

    public static MakeAnOfferReviewFragment newInstance(MakeAnOfferModel makeAnOfferModel,
                                                        ReviewCallbackFunction reviewCallbackFunction) {
        MakeAnOfferReviewFragment fragment = new MakeAnOfferReviewFragment();
        fragment.reviewCallbackFunction = reviewCallbackFunction;
        Bundle args = new Bundle();
        args.putParcelable(ConstantKey.MAKE_AN_OFFER_MODEL, makeAnOfferModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_make_an_offer_review, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        makeAnOfferActivity = (MakeAnOfferActivity) requireActivity();
        makeAnOfferModel = new MakeAnOfferModel();
        userAccountModel = new UserAccountModel();
        sessionManager = new SessionManager(makeAnOfferActivity);
        userAccountModel = sessionManager.getUserAccount();
        if (getArguments() != null && getArguments().getParcelable(ConstantKey.MAKE_AN_OFFER_MODEL) != null) {
            makeAnOfferModel = (MakeAnOfferModel) getArguments().getParcelable(ConstantKey.MAKE_AN_OFFER_MODEL);
        }

        if (makeAnOfferModel != null) {
            txtTotalBudget.setText(String.format(Locale.ENGLISH, "$%d", makeAnOfferModel.getOffer_price()));
            txtServiceFee.setText(String.format(Locale.ENGLISH, "$%.1f", makeAnOfferModel.getAllFee()));
            txtReceiveBudget.setText(String.format(Locale.ENGLISH, "$%.1f", makeAnOfferModel.getYouWillReceive()));
        }
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeAnOfferActivity.onBackPressed();
            }
        });

        // toolbar.setNavigationOnClickListener(MakeAnOfferReviewFragment.this);
        reviewConditions();
        if (makeAnOfferModel != null && makeAnOfferModel.getAttachment() != null) {
            //initLayout();
            cardLiveVideo.setVisibility(View.VISIBLE);

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    ImageUtil.displayImage(imgOfferOnTask, makeAnOfferModel.getAttachment().getModalUrl(), null);
                }
            });
        }
    }


    private void reviewConditions() {
        String text = txtReviewConditions.getText().toString().trim();
        SpannableString spannableString = new SpannableString(text);
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                ExternalIntentHelper.openLink(requireActivity(), Constant.URL_terms);
            }
        };

        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                 ExternalIntentHelper.openLink(requireActivity(), Constant.URL_support);
            }
        };

        spannableString.setSpan(clickableSpan1, 40, 57, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickableSpan2, 62, 82, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        txtReviewConditions.setText(spannableString);
        txtReviewConditions.setMovementMethod(LinkMovementMethod.getInstance());
    }


    @OnClick({R.id.img_btn_close, R.id.img_btn_play, R.id.lyt_btn_submit_offer, R.id.lytRecord2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_btn_close:
                makeAnOfferModel.setAttachment(null);
                if (reviewCallbackFunction != null) {
                    reviewCallbackFunction.backButtonReview(makeAnOfferModel);
                }
                break;
            case R.id.img_btn_play:
                if(makeAnOfferModel.getAttachment() == null || makeAnOfferModel.getAttachment().getUrl() == null){
                    ((ActivityBase)requireActivity()).showToast("Sorry, there is no video to play.", requireContext());
                    return;
                }
                Intent intent = new Intent(requireActivity(), VideoPlayerActivity.class);
                intent.putExtra(ConstantKey.VIDEO_PATH, "" + makeAnOfferModel.getAttachment().getUrl());
                requireActivity().startActivity(intent);
                break;
            case R.id.lytRecord2:
                makeAnOfferActivity.onBackPressed();
                break;
            case R.id.lyt_btn_submit_offer:
                if (reviewCallbackFunction != null) {
                    reviewCallbackFunction.submitButtonReview(makeAnOfferModel);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (reviewCallbackFunction != null) {
            reviewCallbackFunction.backButtonReview(makeAnOfferModel);
        }
    }

    public interface ReviewCallbackFunction {
        void backButtonReview(MakeAnOfferModel makeAnOfferModel);

        void submitButtonReview(MakeAnOfferModel makeAnOfferModel);
    }
}
