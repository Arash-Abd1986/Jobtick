package com.jobtick.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jobtick.EditText.EditTextMedium;
import com.jobtick.R;
import com.jobtick.activities.ActivityBase;
import com.jobtick.activities.TaskDetailsActivity;
import com.jobtick.models.BankAccountModel;
import com.jobtick.payment.AddBankAccount;
import com.jobtick.payment.AddBankAccountImpl;
import com.jobtick.utils.SessionManager;

import java.util.Objects;

public class CreditReqFragment extends Fragment {

    private TextView btnNext;
    private EditTextMedium edtAccountName;
    private EditTextMedium edtBsb;
    private EditTextMedium edtAccountNumber;
    private SessionManager sessionManager;

    private AddBankAccount addBankAccount;

    public CreditReqFragment() {
    }

    public static CreditReqFragment newInstance() {
        return new CreditReqFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(getContext());
        btnNext = view.findViewById(R.id.txt_btn_next);
        btnNext.setOnClickListener(v -> {
            if (validate()) {
                addBankAccountDetails();
            }
        });
        edtAccountName = view.findViewById(R.id.edt_account_name);
        edtBsb = view.findViewById(R.id.edt_bsb);
        edtAccountNumber = view.findViewById(R.id.edt_account_number);

        //TODO: there is no such data, so we disable getting data for all fragments.
//        BankAccountModel bankAccountModel = ((TaskDetailsActivity) getActivity()).bankAccountModel;
//        if (bankAccountModel != null && bankAccountModel.getData() != null) {
//            edtAccountName.setText(bankAccountModel.getData().getAccount_name());
//            edtBsb.setText(bankAccountModel.getData().getBsb_code());
//            edtAccountNumber.setText(bankAccountModel.getData().getAccount_number());
//        }

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
        return inflater.inflate(R.layout.fragment_credit_req, container, false);
    }

    private void addBankAccountDetails() {
        ((ActivityBase) getActivity()).showProgressDialog();

        addBankAccount.add(edtAccountName.getText().toString(),
                edtBsb.getText().toString(),
                edtAccountNumber.getText().toString());
    }

    private void goNext(){
        ((RequirementsBottomSheet) getParentFragment()).changeFragment(2);
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
}