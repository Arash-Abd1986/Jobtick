package com.jobtick.fragments;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.R;
import com.jobtick.TextView.TextViewBold;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.activities.MakeAnOfferActivity;
import com.jobtick.models.MakeAnOfferModel;
import com.jobtick.models.UserAccountModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.ImageUtil;
import com.jobtick.utils.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MakeAnOfferReviewFragment extends Fragment implements View.OnClickListener {


    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.img_info)
    ImageView imgInfo;
    @BindView(R.id.txt_total_budget)
    TextViewBold txtTotalBudget;
    @BindView(R.id.txt_service_fee)
    TextViewBold txtServiceFee;
    @BindView(R.id.txt_receive_budget)
    TextViewBold txtReceiveBudget;
    @BindView(R.id.img_btn_close)
    ImageView imgBtnClose;
    @BindView(R.id.img_btn_play)
    ImageView imgBtnPlay;
    @BindView(R.id.txt_review_conditions)
    TextViewRegular txtReviewConditions;
    @BindView(R.id.lyt_btn_submit_offer)
    LinearLayout lytBtnSubmitOffer;
    @BindView(R.id.card_submit_offer)
    CardView cardSubmitOffer;
    @BindView(R.id.img_offer_on_task)
    ImageView imgOfferOnTask;
    @BindView(R.id.card_live_video)
    CardView cardLiveVideo;

    private MakeAnOfferModel makeAnOfferModel;
    private MakeAnOfferActivity makeAnOfferActivity;
    private ReviewCallbackFunction reviewCallbackFunction;
    private SessionManager sessionManager;
    private UserAccountModel userAccountModel;

    public MakeAnOfferReviewFragment() {
        // Required empty public constructor
    }


    public static MakeAnOfferReviewFragment newInstance(MakeAnOfferModel makeAnOfferModel, ReviewCallbackFunction reviewCallbackFunction) {
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_make_an_offer_review, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        makeAnOfferActivity = (MakeAnOfferActivity) getActivity();
        makeAnOfferModel = new MakeAnOfferModel();
        userAccountModel = new UserAccountModel();
        sessionManager = new SessionManager(makeAnOfferActivity);
        userAccountModel = sessionManager.getUserAccount();
        if (getArguments() != null && getArguments().getParcelable(ConstantKey.MAKE_AN_OFFER_MODEL) != null) {
            makeAnOfferModel = (MakeAnOfferModel) getArguments().getParcelable(ConstantKey.MAKE_AN_OFFER_MODEL);
        }

        if (makeAnOfferModel != null) {
            txtTotalBudget.setText(String.format("$%d", makeAnOfferModel.getOffer_price()));
        }
        toolbar.setNavigationOnClickListener(MakeAnOfferReviewFragment.this);
        reviewConditions();
        setupBudget(makeAnOfferModel.getOffer_price());
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


    private void setupBudget(int budget) {
        int worker_service_fee = userAccountModel.getWorkerTier().getServiceFee();
        float service_fee = (float) ((budget * worker_service_fee) / 100);
        txtServiceFee.setText("$ " + service_fee);
        float total_budget = budget - (float) ((budget * worker_service_fee) / 100);
        txtReceiveBudget.setText("$ " + total_budget);
    }


    private void reviewConditions() {
        String text = txtReviewConditions.getText().toString().trim();
        SpannableString spannableString = new SpannableString(text);
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Toast.makeText(makeAnOfferActivity, "Terms And Conditions", Toast.LENGTH_SHORT).show();
            }
        };

        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Toast.makeText(makeAnOfferActivity, "Cummunity Guidelines", Toast.LENGTH_SHORT).show();
            }
        };

        spannableString.setSpan(clickableSpan1, 43, 61, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickableSpan2, 66, 87, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        txtReviewConditions.setText(spannableString);
        txtReviewConditions.setMovementMethod(LinkMovementMethod.getInstance());
    }


    @OnClick({R.id.img_btn_close, R.id.img_btn_play, R.id.lyt_btn_submit_offer})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_btn_close:
                makeAnOfferModel.setAttachment(null);
                if (reviewCallbackFunction != null) {
                    reviewCallbackFunction.backButtonReview(makeAnOfferModel);
                }
                break;
            case R.id.img_btn_play:
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
