package com.jobtick.android.fragments;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.NetworkResponse;
import com.google.android.material.button.MaterialButton;
import com.jobtick.android.R;
import android.annotation.SuppressLint;
import android.view.WindowManager;
import android.widget.ImageView;

import com.jobtick.android.activities.ActivityBase;
import com.jobtick.android.payment.AddCreditCard;
import com.jobtick.android.payment.AddCreditCardImpl;
import com.jobtick.android.utils.SessionManager;
import com.jobtick.android.utils.StringUtils;
import com.jobtick.android.utils.Tools;
import com.jobtick.android.widget.ExtendedEntryText;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;

import static com.jobtick.android.utils.Constant.MIN_AGE_FOR_USE_APP;

public class CreditCardReqFragment extends Fragment implements TextWatcher {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_add_card)
    MaterialButton btnAddCard;

    ExtendedEntryText edtFullName;
    ExtendedEntryText edtCardNumber;
    ExtendedEntryText edtExpiryDate;
    ExtendedEntryText edtSecurityNumber;
    ImageView ivCardType;

    private int expMonth;
    private int expYear;

    private AddCreditCard addCreditCard;
    private SessionManager sessionManager;

    public CreditCardReqFragment() {
    }

    public static CreditCardReqFragment newInstance() {
        return new CreditCardReqFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(getContext());
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        btnAddCard = view.findViewById(R.id.btn_add_card);
        btnAddCard.setOnClickListener(v -> {
            if(validation()){
                setExpiryDate(edtExpiryDate.getText());
                ((ActivityBase) requireActivity()).showProgressDialog();
                addCreditCard.getToken(edtCardNumber.getText(),
                        expMonth, expYear,
                        edtSecurityNumber.getText(),
                        edtFullName.getText());
            }
        });

        edtFullName = view.findViewById(R.id.edt_full_name);
        ivCardType = view.findViewById(R.id.ivCardType);
        edtCardNumber = view.findViewById(R.id.edt_card_number);
        edtExpiryDate = view.findViewById(R.id.edt_expiry_date);
        edtSecurityNumber = view.findViewById(R.id.edt_security_number);

        edtFullName.addTextChangedListener(this);
        edtCardNumber.addTextChangedListener(this);
        edtSecurityNumber.addTextChangedListener(this);
        edtExpiryDate.addTextChangedListener(this);
        edtExpiryDate.setExtendedViewOnClickListener(() -> {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);

            DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    mDateSetListener,
                    year, month,1);
            dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });
        mDateSetListener = (view1, year, month, dayOfMonth) -> {
            month = month + 1;
            String formattedMonth = String.format(Locale.US,"%02d", month);
            String date = formattedMonth + "/" + year;
            edtExpiryDate.setText(date);
        };

        addCreditCard = new AddCreditCardImpl(requireContext(), sessionManager) {
            @Override
            public void onSuccess() {
                ((ActivityBase) requireActivity()).hideProgressDialog();
                goNext();
            }

            @Override
            public void onError(Exception e) {
                ((ActivityBase) requireActivity()).hideProgressDialog();
                ((ActivityBase) requireActivity()).showToast(getString(R.string.credit_card_error), requireContext());
            }

            @Override
            public void onNetworkResponseError(NetworkResponse networkResponse) {
                ((ActivityBase) requireActivity()).errorHandle1(networkResponse);
                ((ActivityBase) requireActivity()).hideProgressDialog();
            }

            @Override
            public void onValidationError(ValidationErrorType validationErrorType, String message) {
                ((ActivityBase) requireActivity()).showToast(message, requireContext());
                ((ActivityBase) requireActivity()).hideProgressDialog();
            }
        };
        btnAddCard.setEnabled(false);
        setupCardTypes();
    }
    private void setupCardTypes() {
        edtCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(edtCardNumber.getText().length()>1)
                {
                    ivCardType.setVisibility(View.GONE);

                    String cardFirstLetters = edtCardNumber.getText().toString().substring(0,2);
                    if(cardFirstLetters.equals("34") || cardFirstLetters.equals("37")){
                        ivCardType.setVisibility(View.VISIBLE);
                        ivCardType.setImageResource(R.drawable.ic_card_american_express);
                    }
                    if(edtCardNumber.getText().substring(0,1).equals("5")){
                        ivCardType.setVisibility(View.VISIBLE);
                        ivCardType.setImageResource(R.drawable.ic_card_master);
                    }
                    if(edtCardNumber.getText().substring(0,1).equals("4")){
                        ivCardType.setVisibility(View.VISIBLE);
                        ivCardType.setImageResource(R.drawable.ic_card_visa);
                    }

                }else if(ivCardType.getVisibility()==View.VISIBLE){
                    ivCardType.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_credit_card_req, container, false);
    }

    private void goNext() {
        ((PosterRequirementsBottomSheet) getParentFragment()).changeFragment(1);
    }

    private void setExpiryDate(String expiryDate){
        expMonth = Integer.parseInt(expiryDate.substring(0, 2));
        expYear = Integer.parseInt(expiryDate.substring(3));
    }

    private boolean validation(){
        if(edtFullName.getText().isEmpty()){
            edtFullName.setError("The card name must be filled.");
            return false;
        }
        else if(edtCardNumber.getText().isEmpty()){
            edtCardNumber.setError("The card number must be filled.");
            return false;
        }
        else if(edtExpiryDate.getText().toString() == null || edtExpiryDate.getText().isEmpty()
                || edtExpiryDate.getText().length() != 7){
            edtExpiryDate.setError("The card expiry date must be filled.");
            return false;
        }
        else if(!StringUtils.checkCreditCardExpiryFormat(edtExpiryDate.getText().toString())){
            edtExpiryDate.setError("The card expiry date is not correct.");
            return false;
        }
        else if(Integer.parseInt(edtExpiryDate.getText().substring(0, 2)) > 12){
            edtExpiryDate.setError("The card expiry date is not correct.");
        }
        else if(edtSecurityNumber.getText().isEmpty()){
            edtSecurityNumber.setError("The card CVC must be filled.");
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
        boolean btnEnabled = edtCardNumber.getText().length() > 0 &&
                edtFullName.getText().length() > 0 &&
                edtSecurityNumber.getText().length() > 0 &&
                edtExpiryDate.getText().length() > 0;
            btnAddCard.setEnabled(btnEnabled);
    }
}