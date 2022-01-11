package com.jobtick.android.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.jobtick.android.R;
import com.jobtick.android.activities.ActivityBase;
import com.jobtick.android.models.UserAccountModel;
import com.jobtick.android.payment.VerifyPhoneNumber;
import com.jobtick.android.payment.VerifyPhoneNumberImpl;
import com.jobtick.android.utils.MyExtensions;
import com.jobtick.android.utils.SessionManager;
import com.jobtick.android.widget.ExtendedEntryText;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PhoneReqFragment extends Fragment implements TextWatcher {

    private UserAccountModel userAccountModel;
    private SessionManager sessionManager;
    MaterialButton btnNext;
    private ExtendedEntryText phone, verify;
    private BottomSheetDialogFragment bottomSheet;

    private boolean otpSent;

    private VerifyPhoneNumber verifyPhoneNumber;

    public PhoneReqFragment() {
    }
    public void setBottomSheet(@NotNull BottomSheetDialogFragment tickerRequirementsBottomSheet) {
        this.bottomSheet = tickerRequirementsBottomSheet;
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
        MyExtensions.onFocus(phone.editText, bottomSheet);
        MyExtensions.onFocus(verify.editText, bottomSheet);

        verify.addTextChangedListener(this);
        phone.addTextChangedListener(this);

        btnNext.setOnClickListener(v -> {
            if(!validationPhone() || !validationVerify()) return;

            verifyPhoneNumber.verify(verify.getText().trim());
        });

        phone.setExtendedViewOnClickListener(() -> {
                if(!validationPhone()) return;
                ((ActivityBase) requireActivity()).showProgressDialog();
                verifyPhoneNumber.sendOTP(phone.getText().trim());
            });

        verifyPhoneNumber = new VerifyPhoneNumberImpl(requireContext(), sessionManager) {
            @Override
            public void onSuccess(SuccessType successType) {
                ((ActivityBase) requireActivity()).hideProgressDialog();
                if(successType == SuccessType.OTP){
                    ((ActivityBase) requireActivity()).showSuccessToast("OTP is sent successfully.", requireContext());
                    otpSent = true;
                }
                else if( successType == SuccessType.Verify){
                    goNext();
                }
            }

            @Override
            public void onError(Exception e) {
                ((ActivityBase) requireActivity()).hideProgressDialog();
                if(Objects.equals(e.getMessage(), "This mobile number is already verified."))
                    goNext();
                ((ActivityBase) requireActivity()).showToast(e.getMessage(), requireContext());
            }

            @Override
            public void onValidationError(ErrorType errorType, String message) {
                ((ActivityBase) requireActivity()).hideProgressDialog();
                if(errorType == ErrorType.UN_AUTHENTICATED_USER)
                    ((ActivityBase) requireActivity()).unauthorizedUser();
                else
                    ((ActivityBase) requireActivity()).showToast(message, requireContext());
            }
        };
    }

    private void goNext(){
        assert getParentFragment() != null;
        ((TickerRequirementsBottomSheet) getParentFragment()).changeFragment(5);
    }



    private boolean validationPhone() {
        if (phone.getText().length() != 10) {
            phone.setError("Enter correct mobile number");
            return false;
        }
        return true;
    }
    private boolean validationVerify() {
         if (verify.getText().length() != 6) {
            verify.setError("Enter 6 digits verification code.");
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
