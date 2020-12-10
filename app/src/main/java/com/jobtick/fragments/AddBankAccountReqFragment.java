package com.jobtick.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.jobtick.R;
import com.jobtick.activities.ActivityBase;
import com.jobtick.activities.TaskDetailsActivity;
import com.jobtick.models.BankAccountModel;
import com.jobtick.payment.AddBankAccount;
import com.jobtick.payment.AddBankAccountImpl;
import com.jobtick.utils.SessionManager;
import com.jobtick.widget.ExtendedEntryText;

import java.util.Objects;

public class AddBankAccountReqFragment extends Fragment implements TextWatcher {

    private MaterialButton btnNext;
    private ExtendedEntryText edtAccountName;
    private ExtendedEntryText edtBsb;
    private ExtendedEntryText edtAccountNumber;
    private SessionManager sessionManager;

    private AddBankAccount addBankAccount;

    public AddBankAccountReqFragment() {
    }

    public static AddBankAccountReqFragment newInstance() {
        return new AddBankAccountReqFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(getContext());
        btnNext = view.findViewById(R.id.btn_add_card);
        btnNext.setOnClickListener(v -> {
            if (validate()) {
                addBankAccountDetails();
            }
        });
        edtAccountName = view.findViewById(R.id.edt_account_name);
        edtBsb = view.findViewById(R.id.edt_bsb);
        edtAccountNumber = view.findViewById(R.id.edt_account_number);

        edtBsb.addTextChangedListener(this);
        edtAccountNumber.addTextChangedListener(this);
        edtAccountName.addTextChangedListener(this);

        BankAccountModel bankAccountModel = ((TaskDetailsActivity) getActivity()).bankAccountModel;
        if (bankAccountModel != null && bankAccountModel.getData() != null) {
            edtAccountName.setText(bankAccountModel.getData().getAccount_name());
            edtBsb.setText(bankAccountModel.getData().getBsb_code());
            edtAccountNumber.setText("xxxxx" + bankAccountModel.getData().getAccount_number());
        }

        addBankAccount = new AddBankAccountImpl(requireContext(), sessionManager) {
            @Override
            public void onSuccess() {
                ((ActivityBase) getActivity()).hideProgressDialog();
                goNext();
            }

            @Override
            public void onError(Exception e) {
                ((ActivityBase) getActivity()).hideProgressDialog();
                if(Objects.equals(e.getMessage(), "Bank account already exist."))
                    goNext();
                else
                    ((ActivityBase) getActivity()).showToast(e.getMessage(), requireContext());
            }

            @Override
            public void onValidationError(ErrorType errorType, String message) {
                ((ActivityBase) getActivity()).hideProgressDialog();
                if (errorType == ErrorType.UnAuthenticatedUser)
                    ((ActivityBase) getActivity()).unauthorizedUser();
                else
                    ((ActivityBase) getActivity()).showToast(message, requireContext());
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_bank_account_req, container, false);
    }

    private void addBankAccountDetails() {
        ((ActivityBase) getActivity()).showProgressDialog();

        addBankAccount.add(edtAccountName.getText().toString(),
                edtBsb.getText().toString(),
                edtAccountNumber.getText().toString());
    }

    private void goNext(){
        ((TickerRequirementsBottomSheet) getParentFragment()).changeFragment(2);
    }


    public boolean validate() {
        if (TextUtils.isEmpty(edtAccountName.getText().toString())) {
            edtAccountName.setError("Please Enter Account Name");
            return false;
        }

        if (TextUtils.isEmpty(edtBsb.getText().toString())) {
            edtBsb.setError("Please enter BSB");
            return false;
        }

        if (TextUtils.isEmpty(edtAccountNumber.getText().toString())) {
            edtAccountNumber.setError("Please account Number");
            return false;
        }
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        boolean enabled = edtAccountName.getText().length() > 0 &&
                edtAccountNumber.getText().length() > 0 &&
                edtBsb.getText().length() > 0;

        btnNext.setEnabled(enabled);
    }
}