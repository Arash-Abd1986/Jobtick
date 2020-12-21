package com.jobtick.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.NetworkResponse;
import com.google.android.material.button.MaterialButton;
import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.activities.ActivityBase;
import com.jobtick.payment.AddCreditCard;
import com.jobtick.payment.AddCreditCardImpl;
import com.jobtick.utils.SessionManager;
import com.jobtick.widget.ExtendedEntryText;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.Calendar;

import butterknife.BindView;
import timber.log.Timber;

public class CreditCardReqFragment extends Fragment implements TextWatcher {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_add_card)
    MaterialButton btnAddCard;

    ExtendedEntryText edtFullName;
    ExtendedEntryText edtCardNumber;
    ExtendedEntryText edtExpiryDate;
    ExtendedEntryText edtSecurityNumber;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(getContext());
        btnAddCard = view.findViewById(R.id.btn_add_card);
        btnAddCard.setOnClickListener(v -> {
            if(validation()){
                ((ActivityBase) requireActivity()).showProgressDialog();
                addCreditCard.getToken(edtCardNumber.getText(),
                        expMonth, expYear,
                        edtSecurityNumber.getText(),
                        edtFullName.getText());
            }
        });

        edtFullName = view.findViewById(R.id.edt_full_name);
        edtCardNumber = view.findViewById(R.id.edt_card_number);
        edtExpiryDate = view.findViewById(R.id.edt_expiry_date);
        edtSecurityNumber = view.findViewById(R.id.edt_security_number);

        edtFullName.addTextChangedListener(this);
        edtCardNumber.addTextChangedListener(this);
        edtSecurityNumber.addTextChangedListener(this);
        edtExpiryDate.addTextChangedListener(this);

        edtExpiryDate.setExtendedViewOnClickListener(this::displayDialog);


        addCreditCard = new AddCreditCardImpl(requireContext(), sessionManager) {
            @Override
            public void onSuccess() {
                ((ActivityBase) requireActivity()).hideProgressDialog();
                goNext();
            }

            @Override
            public void onError(Exception e) {
                ((ActivityBase) requireActivity()).hideProgressDialog();
                ((ActivityBase) requireActivity()).showToast(e.getMessage(), requireContext());
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_credit_card_req, container, false);
    }

    private void goNext() {
        ((PosterRequirementsBottomSheet) getParentFragment()).changeFragment(1);
    }


    public void displayDialog() {
        final Calendar today = Calendar.getInstance();

        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(requireContext(), new MonthPickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(int selectedMonth, int selectedYear) {
                expMonth = selectedMonth;
                expYear = selectedYear;
                edtExpiryDate.setText(selectedMonth + " /" + selectedYear);
                Timber.tag("CreditCardReqFragment").d("selectedMonth : " + selectedMonth + " selectedYear : " + selectedYear);
            }
        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

        builder.setActivatedMonth(today.get(Calendar.MONTH))
                .setTitle("Select month and year ")
                .setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {
                    @Override
                    public void onMonthChanged(int selectedMonth) {
                        Timber.tag("creditCardFragment").d("Selected month : " + selectedMonth);
                    }
                })
                .setOnYearChangedListener(new MonthPickerDialog.OnYearChangedListener() {
                    @Override
                    public void onYearChanged(int selectedYear) {

                        Timber.tag("CreditCardFragment").d("Selected year : " + selectedYear);
                    }
                })
                .setMaxYear(2040)
                .build()
                .show();

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
        else if(edtExpiryDate.getText().isEmpty()){
            edtExpiryDate.setError("The card expiry date must be filled.");
            return false;
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
        boolean btnEnabled = edtCardNumber.getText().length() == 16 &&
                edtFullName.getText().length() > 0 &&
                edtSecurityNumber.getText().length() > 0 &&
                edtExpiryDate.getText().length() > 0;
            btnAddCard.setEnabled(btnEnabled);
    }
}