package com.jobtick.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.activities.ActivityBase;
import com.jobtick.activities.MakeAnOfferActivity;
import com.jobtick.activities.TaskDetailsActivity;
import com.jobtick.models.MakeAnOfferModel;
import com.jobtick.models.TaskModel;
import com.jobtick.models.UserAccountModel;
import com.jobtick.models.calculation.EarningCalculationModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.SessionManager;
import com.jobtick.widget.ExtendedEntryText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class MakeAnOfferBudgetFragment extends Fragment implements TextWatcher {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_budget)
    ExtendedEntryText edtBudget;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_service_fee)
    TextView txtServiceFee;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_final_budget)
    TextView txtFinalBudget;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_account_level)
    TextView txtAccountLevel;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_current_service_fee)
    TextView txtCurrentServiceFee;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_learn_how_level_affects_service_fee)
    TextView txtLearnHowLevelAffectsServiceFee;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_next)
    MaterialButton btnNext;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_level)
    ImageView imgLevel;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tvOffer)
    TextView tvOffer;

    Gson gson;

    private MakeAnOfferModel makeAnOfferModel;
    private MakeAnOfferActivity makeAnOfferActivity;
    BudgetCallbackFunction budgetCallbackFunction;
    private UserAccountModel userAccountModel;
    private TaskModel taskModel;
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
        gson = new Gson();
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
        taskModel = TaskDetailsActivity.taskModel;
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
        edtBudget.addTextChangedListener(this);

        btnNext.setOnClickListener(v -> {
            if (budgetCallbackFunction != null) {
                if (!validation(true)) return;

                makeAnOfferModel.setOffer_price(Integer.parseInt(edtBudget.getText().trim()));
                budgetCallbackFunction.continueButtonBudget(makeAnOfferModel);
            }
        });
    }

    private void initLayout() {
        //edtBudget.setText(String.format("%d", makeAnOfferModel.getOffer_price()));
        tvOffer.setText(String.format(Locale.ENGLISH, "$%d", taskModel.getBudget()));
        txtAccountLevel.setText(String.format(Locale.ENGLISH, "Level %d", userAccountModel.getWorkerTier().getId()));

        if (userAccountModel.getWorkerTier().getId() == 1) {
            //TODO change image level 1
            imgLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_medal1));
        } else if (userAccountModel.getWorkerTier().getId() == 2) {
            //TODO change image level 2
            imgLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_medal2));
        } else if (userAccountModel.getWorkerTier().getId() == 3) {
            //TODO change image level 3
            imgLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_medal3));
        } else if (userAccountModel.getWorkerTier().getId() == 4) {
            //TODO change image level 4
            imgLevel.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_medal4));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        btnNext.setEnabled(true);

        if (!validation(false)) {
            txtServiceFee.setText(R.string.dollar_00);
            txtFinalBudget.setText(R.string.dollar_00);
            txtCurrentServiceFee.setText("0%");
            return;
        }
        calculate(editable.toString());
    }

    private boolean validation(boolean showToast) {
        if (edtBudget.getText().length() == 0) {
            if (showToast)
                ((ActivityBase) requireActivity()).showToast("Please enter your offer.", requireContext());
            return false;
        }
        if (Integer.parseInt(edtBudget.getText()) < 5) {
            if (showToast)
                ((ActivityBase) requireActivity()).showToast("You can't offer lower than 5 dollars.", requireContext());
            return false;
        }
        if (Integer.parseInt(edtBudget.getText()) < taskModel.getBudget()) {
            if (showToast)
                ((ActivityBase) requireActivity()).showToast("You can't offer lower than poster offer.", requireContext());
            return false;
        }
        if (Integer.parseInt(edtBudget.getText()) > 9999) {
            if (showToast)
                ((ActivityBase) requireActivity()).showToast("You can't offer more than 9999 dollars.", requireContext());
            return false;
        }
        return true;
    }


    public void calculate(String amount) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_OFFERS_EARNING_CALCULATION,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String data = jsonObject.getString("data");

                        EarningCalculationModel model = gson.fromJson(data, EarningCalculationModel.class);
                        txtServiceFee.setText(String.format(Locale.ENGLISH, "$%.1f", (model.getServiceFee() + model.getGstAmount())));
                        txtFinalBudget.setText(String.format(Locale.ENGLISH, "$%.1f", model.getNetEarning()));
                        txtCurrentServiceFee.setText(String.format(Locale.ENGLISH, "%s", model.getServiceFeeRate()));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                            String message = jsonObject_error.getString("message");
                            ((ActivityBase) requireActivity()).showToast(message, requireContext());

                        } catch (JSONException e) {
                            e.printStackTrace();
                            ((ActivityBase) requireActivity()).showToast("Something went wrong", requireContext());
                        }
                    } else {
                        ((ActivityBase) requireActivity()).showToast("Something went wrong", requireContext());
                    }

                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");

                return map1;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("amount", amount);
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }


    public interface BudgetCallbackFunction {

        void continueButtonBudget(MakeAnOfferModel makeAnOfferModel);
    }
}
