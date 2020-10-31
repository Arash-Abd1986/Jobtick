package com.jobtick.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jobtick.R;
import com.jobtick.activities.MakeAnOfferActivity;
import com.jobtick.models.MakeAnOfferModel;
import com.jobtick.models.UserAccountModel;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.MinMaxFilter;
import com.jobtick.utils.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class MakeAnOfferBudgetFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.edt_budget)
    EditText edtBudget;
    @BindView(R.id.txt_service_fee)
    TextView txtServiceFee;
    @BindView(R.id.txt_final_budget)
    TextView txtFinalBudget;
    @BindView(R.id.txt_account_level)
    TextView txtAccountLevel;
    @BindView(R.id.txt_current_service_fee)
    TextView txtCurrentServiceFee;
    @BindView(R.id.txt_learn_how_level_affects_service_fee)
    TextView txtLearnHowLevelAffectsServiceFee;
    @BindView(R.id.lyt_btn_continue)
    LinearLayout lytBtnContinue;
    @BindView(R.id.card_continue)
    CardView cardContinue;
    @BindView(R.id.img_level)
    ImageView imgLevel;
    @BindView(R.id.tvOffer)
    TextView tvOffer;

    private MakeAnOfferModel makeAnOfferModel;
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
        //toolbar.setNavigationOnClickListener(MakeAnOfferBudgetFragment.this);

        //Custom tool bar back here
        ImageView ivBack = (ImageView) view.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                makeAnOfferActivity.onBackPressed();
            }
        });
        cardContinue.setClickable(false);

        //setupBudget(Integer.parseInt(edtBudget.getText().toString().trim()));
        final String number = edtBudget.getText().toString();
        if (!number.matches("0\\d{0}") || number.length() <= 5 || Integer.parseInt(number) <= 5) {
            //!number.matches("9\\d{9}") ||
            // !number.matches("(\\+98|0)?9\\d{9}") ||
            Toast.makeText(getActivity(), "between 5 - 9999!", Toast.LENGTH_LONG).show();
        } else {
            edtBudget.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable editable) {

                    String currentText = editable.toString();
                    int currentLength = currentText.length();

                    if (currentLength >= 1) {
                        cardContinue.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.colorPrimary));
                        cardContinue.setClickable(true);
                    } else {
                        cardContinue.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.colorAccent));
                        cardContinue.setClickable(false);
                    }

                    if (currentLength == 0) {
                        //reset all data
                    } else {
                        setupBudget(Integer.parseInt(editable.toString()));
                    }
                }
            });
        }


        // .setFilters(new InputFilter[]{new MinMaxFilter(5, 9999)});

        initComponentScroll();
    }

    private void setupBudget(int budget) {
        float worker_service_fee = userAccountModel.getWorkerTier().getServiceFee();
        txtCurrentServiceFee.setText(worker_service_fee + "%");
        float service_fee = ((budget * worker_service_fee) / 100);
        txtServiceFee.setText("$" + service_fee);
        float total_budget = budget - ((budget * worker_service_fee) / 100);
        txtFinalBudget.setText("$" + total_budget);
    }

    private void initLayout() {
        //edtBudget.setText(String.format("%d", makeAnOfferModel.getOffer_price()));
        tvOffer.setText("$" + String.format("%d", makeAnOfferModel.getOffer_price()));
        txtAccountLevel.setText("Level " + userAccountModel.getWorkerTier().getId());

        if (userAccountModel.getWorkerTier().getId() == 1) {
            //TODO change image level 1
            imgLevel.setImageDrawable(makeAnOfferActivity.getResources().getDrawable(R.drawable.ic_medal1));
        } else if (userAccountModel.getWorkerTier().getId() == 2) {
            //TODO change image level 2
            imgLevel.setImageDrawable(makeAnOfferActivity.getResources().getDrawable(R.drawable.ic_medal2));
        } else if (userAccountModel.getWorkerTier().getId() == 3) {
            //TODO change image level 3
            imgLevel.setImageDrawable(makeAnOfferActivity.getResources().getDrawable(R.drawable.ic_medal3));
        } else if (userAccountModel.getWorkerTier().getId() == 4) {
            //TODO change image level 4
            imgLevel.setImageDrawable(makeAnOfferActivity.getResources().getDrawable(R.drawable.ic_medal4));
        }
    }

    private void initComponentScroll() {
        NestedScrollView nested_content = (NestedScrollView) getActivity().findViewById(R.id.nested_scroll_view);
        nested_content.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY < oldScrollY) { // up
                    animateFab(false);
                }
                if (scrollY > oldScrollY) { // down
                    animateFab(true);
                }
            }
        });
    }

    boolean isFabHide = false;

    private void animateFab(final boolean hide) {
        if (isFabHide && hide || !isFabHide && !hide) return;
        isFabHide = hide;
        int moveY = hide ? (2 * cardContinue.getHeight()) : 0;
        cardContinue.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    @OnClick({R.id.lyt_btn_continue})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.lyt_btn_continue:
                if (budgetCallbackFunction != null) {
                    if (TextUtils.isEmpty(edtBudget.getText().toString().trim())) {
                        //edtBudget.setText("Budget is empty");
                        Toast.makeText(getActivity(), "Please enter offer amount", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (Float.parseFloat(edtBudget.getText().toString()) < 5) {
                        //Toast.makeText(getActivity(),"Offer only accept max $5",Toast.LENGTH_SHORT).show();

                        new MaterialAlertDialogBuilder(getActivity())
                                .setTitle("Alert!")
                                .setMessage("Offer price only allowed minimum $5")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        return;
                    } else if (Float.parseFloat(edtBudget.getText().toString()) > 9999) {


                        new MaterialAlertDialogBuilder(getActivity())
                                .setTitle("Alert!")
                                .setMessage("Offer price only allowed maximum $9999")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();
                                    }
                                }).show();
                        return;
                    } else {
                        makeAnOfferModel.setOffer_price(Integer.parseInt(edtBudget.getText().toString().trim()));
                        budgetCallbackFunction.continueButtonBudget(makeAnOfferModel);
                    }
                }
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
