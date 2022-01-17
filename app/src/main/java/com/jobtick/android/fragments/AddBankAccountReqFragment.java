package com.jobtick.android.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.jobtick.android.R;
import com.jobtick.android.activities.ActivityBase;
import com.jobtick.android.activities.TaskDetailsActivity;
import com.jobtick.android.models.BankAccountModel;
import com.jobtick.android.payment.AddBankAccount;
import com.jobtick.android.payment.AddBankAccountImpl;
import com.jobtick.android.utils.MyExtensions;
import com.jobtick.android.utils.SessionManager;
import com.jobtick.android.widget.ExtendedEntryText;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AddBankAccountReqFragment extends Fragment implements TextWatcher {

    private MaterialButton btnNext;
    private ExtendedEntryText edtAccountName;
    private ExtendedEntryText edtBsb;
    private ExtendedEntryText edtAccountNumber;
    private SessionManager sessionManager;
    private BottomSheetDialogFragment bottomSheet;

    private AddBankAccount addBankAccount;

    public AddBankAccountReqFragment() {
    }

    public static AddBankAccountReqFragment newInstance() {
        return new AddBankAccountReqFragment();
    }

    public void setBottomSheet(@NotNull BottomSheetDialogFragment tickerRequirementsBottomSheet) {
        this.bottomSheet = tickerRequirementsBottomSheet;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
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
        MyExtensions.onFocus(edtAccountName.editText, bottomSheet);
        MyExtensions.onFocus(edtAccountNumber.editText, bottomSheet);
        MyExtensions.onFocus(edtBsb.editText, bottomSheet);

        edtBsb.addTextChangedListener(this);
        edtAccountNumber.addTextChangedListener(this);
        edtAccountName.addTextChangedListener(this);

        BankAccountModel bankAccountModel = ((TaskDetailsActivity) requireActivity()).bankAccountModel;
        if (bankAccountModel != null && bankAccountModel.getData() != null) {
            edtAccountName.setText(bankAccountModel.getData().getAccount_name());
            edtBsb.setText(bankAccountModel.getData().getBsb_code());
            edtAccountNumber.setText("xxxxx" + bankAccountModel.getData().getAccount_number());
        }

        addBankAccount = new AddBankAccountImpl(requireContext(), sessionManager) {
            @Override
            public void onSuccess() {
                ((ActivityBase) requireActivity()).hideProgressDialog();
                goNext();
            }

            @Override
            public void onError(Exception e) {
                ((ActivityBase) requireActivity()).hideProgressDialog();
                if (Objects.equals(e.getMessage(), "Bank account already exist."))
                    goNext();
                else
                    ((ActivityBase) requireActivity()).showToast(getString(R.string.add_bank_account_error), requireContext());
            }

            @Override
            public void onValidationError(ErrorType errorType, String message) {
                ((ActivityBase) requireActivity()).hideProgressDialog();
                if (errorType == ErrorType.UnAuthenticatedUser)
                    ((ActivityBase) requireActivity()).unauthorizedUser();
                else
                    ((ActivityBase) requireActivity()).showToast(message, requireContext());
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_bank_account_req, container, false);
    }

    private void addBankAccountDetails() {
        ((ActivityBase) requireActivity()).showProgressDialog();

        addBankAccount.add(edtAccountName.getText(),
                edtBsb.getText(),
                edtAccountNumber.getText());
    }

    private void goNext() {
        ((TickerRequirementsBottomSheet) getParentFragment()).changeFragment(2);
    }


    public boolean validate() {
        if (TextUtils.isEmpty(edtAccountName.getText())) {
            edtAccountName.setError("Please Enter Account Name");
            return false;
        }

        if (TextUtils.isEmpty(edtBsb.getText())) {
            edtBsb.setError("Please enter BSB");
            return false;
        }

        if (TextUtils.isEmpty(edtAccountNumber.getText())) {
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