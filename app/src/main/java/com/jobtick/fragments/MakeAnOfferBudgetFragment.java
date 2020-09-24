package com.jobtick.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.EditText.EditTextBold;
import com.jobtick.R;
import com.jobtick.TextView.TextViewBold;
import com.jobtick.TextView.TextViewMedium;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.activities.MakeAnOfferActivity;
import com.jobtick.models.MakeAnOfferModel;
import com.jobtick.models.TaskModel;
import com.jobtick.models.UserAccountModel;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class MakeAnOfferBudgetFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.edt_budget)
    EditTextBold edtBudget;
    @BindView(R.id.img_btn_minus)
    ImageView imgBtnMinus;
    @BindView(R.id.img_btn_plus)
    ImageView imgBtnPlus;
    @BindView(R.id.lyt_button_minus_plus)
    LinearLayout lytButtonMinusPlus;
    @BindView(R.id.txt_service_fee)
    TextViewBold txtServiceFee;
    @BindView(R.id.txt_final_budget)
    TextViewBold txtFinalBudget;
    @BindView(R.id.txt_account_level)
    TextViewRegular txtAccountLevel;
    @BindView(R.id.txt_current_service_fee)
    TextViewBold txtCurrentServiceFee;
    @BindView(R.id.txt_learn_how_level_affects_service_fee)
    TextViewRegular txtLearnHowLevelAffectsServiceFee;
    @BindView(R.id.lyt_btn_continue)
    LinearLayout lytBtnContinue;
    @BindView(R.id.card_continue)
    CardView cardContinue;
    @BindView(R.id.img_level)
    ImageView imgLevel;

    @BindView(R.id.txtEdit)
    TextViewMedium txtEdit;

    private MakeAnOfferModel makeAnOfferModel;
    // private TaskModel taskModel;
    private MakeAnOfferActivity makeAnOfferActivity;
    BudgetCallbackFunction budgetCallbackFunction;
    private UserAccountModel userAccountModel;
    private SessionManager sessionManager;

    public static MakeAnOfferBudgetFragment newInstance(MakeAnOfferModel makeAnOfferModel, BudgetCallbackFunction budgetCallbackFunction) {

        Bundle args = new Bundle();
        args.putParcelable(ConstantKey.MAKE_AN_OFFER_MODEL, makeAnOfferModel);
        // args.putParcelable(ConstantKey.TASK, taskModel);
        MakeAnOfferBudgetFragment fragment = new MakeAnOfferBudgetFragment();
        fragment.budgetCallbackFunction = budgetCallbackFunction;
        fragment.setArguments(args);
        return fragment;
    }

    public MakeAnOfferBudgetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_make_an_offer_budget, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        makeAnOfferActivity = (MakeAnOfferActivity) getActivity();
        if (makeAnOfferActivity != null) {
            sessionManager = new SessionManager(makeAnOfferActivity);
        }
        makeAnOfferModel = new MakeAnOfferModel();
        userAccountModel = new UserAccountModel();
        userAccountModel = sessionManager.getUserAccount();
        if (getArguments() != null && getArguments().getParcelable(ConstantKey.MAKE_AN_OFFER_MODEL) != null) {
            makeAnOfferModel = getArguments().getParcelable(ConstantKey.MAKE_AN_OFFER_MODEL);
        }
        /*if (getArguments() != null && getArguments().getParcelable(ConstantKey.TASK) != null) {
            taskModel = getArguments().getParcelable(ConstantKey.TASK);
        }*/
        if (makeAnOfferModel != null) {
            initLayout();
        }
        toolbar.setNavigationOnClickListener(MakeAnOfferBudgetFragment.this);

        setupBudget(Integer.parseInt(edtBudget.getText().toString().trim()));
        edtBudget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    //reset all data
                } else {
                    setupBudget(Integer.parseInt(s.toString()));
                }
            }
        });
    }

    private void setupBudget(int budget) {
        float worker_service_fee = userAccountModel.getWorkerTier().getServiceFee();
        txtCurrentServiceFee.setText(worker_service_fee + " %");
        float service_fee = ((budget * worker_service_fee) / 100);
        txtServiceFee.setText("$ " + service_fee);
        float total_budget = budget - ((budget * worker_service_fee) / 100);
        txtFinalBudget.setText("$ " + total_budget);
    }


    private void initLayout() {
        edtBudget.setText(String.format("%d", makeAnOfferModel.getOffer_price()));
        txtAccountLevel.setText("Account Level " + userAccountModel.getWorkerTier().getId());
        if (userAccountModel.getWorkerTier().getId() == 1) {
            imgLevel.setImageDrawable(makeAnOfferActivity.getResources().getDrawable(R.drawable.ic_level_1));
        } else if (userAccountModel.getWorkerTier().getId() == 2) {
            imgLevel.setImageDrawable(makeAnOfferActivity.getResources().getDrawable(R.drawable.ic_level_2));
        } else if (userAccountModel.getWorkerTier().getId() == 3) {
            imgLevel.setImageDrawable(makeAnOfferActivity.getResources().getDrawable(R.drawable.ic_level_3));
        } else if (userAccountModel.getWorkerTier().getId() == 4) {
            //TODO need to img ic_level_4
            imgLevel.setImageDrawable(makeAnOfferActivity.getResources().getDrawable(R.drawable.ic_level_3));
        }
    }

    @OnClick({R.id.img_btn_minus, R.id.img_btn_plus, R.id.lyt_btn_continue, R.id.txtEdit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_btn_minus:
                budgetModification("-");
                break;
            case R.id.img_btn_plus:
                budgetModification("+");
                break;
            case R.id.lyt_btn_continue:
                if (budgetCallbackFunction != null) {
                    if (TextUtils.isEmpty(edtBudget.getText().toString().trim())) {
                        edtBudget.setText("Budget is empty");
                    } else {
                        makeAnOfferModel.setOffer_price(Integer.parseInt(edtBudget.getText().toString().trim()));
                        budgetCallbackFunction.continueButtonBudget(makeAnOfferModel);
                    }
                }
                break;
            case R.id.txtEdit:
                edtBudget.setFocusable(true);
                edtBudget.setFocusableInTouchMode(true);
                edtBudget.setClickable(true);

                break;

        }
    }

    private void budgetModification(String sign) {
        try {
            int budget = Integer.parseInt(edtBudget.getText().toString().trim());
            if (sign.equalsIgnoreCase("-")) {
                if (budget > 0) {
                    --budget;
                    edtBudget.setText(String.format("%d", budget));
                } else {
                    edtBudget.setText(String.format("%d", 0));
                }
            } else {
                if (budget > 0) {
                    ++budget;
                    edtBudget.setText(String.format("%d", budget));
                } else {
                    edtBudget.setText(String.format("%d", 1));
                }
            }
            edtBudget.setSelection(edtBudget.getText().length());
        } catch (Exception e) {
            e.printStackTrace();
            edtBudget.setText(String.format("%d", 0));
        }
    }

    @Override
    public void onClick(View v) {
        if (budgetCallbackFunction != null) {
            budgetCallbackFunction.backButtonBudget();
        }
    }

    public interface BudgetCallbackFunction {
        void backButtonBudget();

        void continueButtonBudget(MakeAnOfferModel makeAnOfferModel);
    }
}
