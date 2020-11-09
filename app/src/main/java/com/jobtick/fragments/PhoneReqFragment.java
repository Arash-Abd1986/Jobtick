package com.jobtick.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.jobtick.R;
import com.jobtick.activities.ActivityBase;
import com.jobtick.models.UserAccountModel;
import com.jobtick.payment.VerifyPhoneNumber;
import com.jobtick.payment.VerifyPhoneNumberImpl;
import com.jobtick.utils.SessionManager;

import java.util.Objects;

public class PhoneReqFragment extends Fragment implements TextWatcher {

    private UserAccountModel userAccountModel;
    private SessionManager sessionManager;
    MaterialButton btnNext;
    private TextView phone, verify;
    private LinearLayout btnVerify;

    private boolean otpSent;

    private VerifyPhoneNumber verifyPhoneNumber;

    public PhoneReqFragment() {
    }

    public static PhoneReqFragment newInstance() {
        return new PhoneReqFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_phone_req, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(getContext());
        phone = view.findViewById(R.id.edt_phone_number);
        verify = view.findViewById(R.id.edt_sms);
        btnNext = view.findViewById(R.id.btn_submit);
        btnVerify = view.findViewById(R.id.lyt_btn_close);

        verify.addTextChangedListener(this);
        phone.addTextChangedListener(this);

        btnNext.setOnClickListener(v -> {
            if(!validationPhone() || !validationVerify()) return;

            verifyPhoneNumber.verify(verify.getText().toString().trim());
        });

        btnVerify.setOnClickListener(v -> {
                if(!validationPhone()) return;
                ((ActivityBase) getActivity()).showProgressDialog();
                verifyPhoneNumber.sendOTP(phone.getText().toString().trim());
            });

        verifyPhoneNumber = new VerifyPhoneNumberImpl(requireContext(), sessionManager) {
            @Override
            public void onSuccess(SuccessType successType) {
                ((ActivityBase) getActivity()).hideProgressDialog();
                if(successType == SuccessType.OTP){
                    ((ActivityBase) getActivity()).showToast("OTP is sent successfully.", requireContext());
                    otpSent = true;
                }
                else if( successType == SuccessType.Verify){
                    goNext();
                }
            }

            @Override
            public void onError(Exception e) {
                ((ActivityBase) getActivity()).hideProgressDialog();
                if(Objects.equals(e.getMessage(), "This phone number is already verified."))
                    goNext();
                ((ActivityBase) getActivity()).showToast(e.getMessage(), requireContext());
            }

            @Override
            public void onValidationError(ErrorType errorType, String message) {
                if(errorType == ErrorType.UnAuthenticatedUser)
                    ((ActivityBase) getActivity()).unauthorizedUser();
                else
                    ((ActivityBase) getActivity()).showToast(message, requireContext());
            }
        };
    }

    private void goNext(){
        ((TickerRequirementsBottomSheet) getParentFragment()).changeFragment(5);
    }



    private boolean validationPhone() {
        if (TextUtils.isEmpty(phone.getText().toString().trim())) {
            phone.setError("Enter mobile number");
            return false;
        }
        return true;
    }
    private boolean validationVerify() {
         if (TextUtils.isEmpty(verify.getText().toString().trim())) {
            verify.setError("Enter verify");
            return false;
        }
        return true;
    }

    private void setUpPhone(UserAccountModel userAccountModel) {
        phone.setText(userAccountModel.getMobile());
        verify.setText(userAccountModel.getMobileVerifiedAt());

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        boolean enabled = phone.getText().length() > 0 &&
                verify.getText().length() > 0 &&
                otpSent;

        btnNext.setEnabled(enabled);
    }
}
