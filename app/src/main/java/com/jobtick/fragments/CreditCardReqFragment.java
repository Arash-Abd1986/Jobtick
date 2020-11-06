package com.jobtick.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.NetworkResponse;
import com.google.android.material.button.MaterialButton;
import com.jobtick.R;
import com.jobtick.activities.ActivityBase;
import com.jobtick.payment.AddCreditCard;
import com.jobtick.payment.AddCreditCardImpl;
import com.jobtick.utils.SessionManager;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.Calendar;

import butterknife.BindView;

public class CreditCardReqFragment extends Fragment implements TextWatcher {

    @BindView(R.id.btn_add_card)
    MaterialButton btnAddCard;

    EditText edtFullName;
    EditText edtCardNumber;
    TextView edtExpiryDate;
    EditText edtSecurityNumber;

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
                ((ActivityBase) getActivity()).showProgressDialog();
                addCreditCard.getToken(edtCardNumber.getText().toString(),
                        expMonth, expYear,
                        edtSecurityNumber.getText().toString(),
                        edtFullName.getText().toString());
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

        edtExpiryDate.setOnClickListener(v -> {
            displayDialog();
        });


        addCreditCard = new AddCreditCardImpl(requireContext(), sessionManager) {
            @Override
            public void onSuccess() {
                ((ActivityBase) getActivity()).hideProgressDialog();
                goNext();
            }

            @Override
            public void onError(Exception e) {
                ((ActivityBase) getActivity()).hideProgressDialog();
                ((ActivityBase) getActivity()).showToast(e.getMessage(), requireContext());
            }

            @Override
            public void onNetworkResponseError(NetworkResponse networkResponse) {
                ((ActivityBase) getActivity()).errorHandle1(networkResponse);
                ((ActivityBase) getActivity()).hideProgressDialog();
            }

            @Override
            public void onValidationError(ValidationErrorType validationErrorType, String message) {
                ((ActivityBase) getActivity()).showToast(message, requireContext());
                ((ActivityBase) getActivity()).hideProgressDialog();
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
                Log.d("aa", "selectedMonth : " + selectedMonth + " selectedYear : " + selectedYear);
                Toast.makeText(requireContext(), "Date set with month " + selectedMonth + " year " + selectedYear, Toast.LENGTH_SHORT).show();
            }
        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

        builder.setActivatedMonth(today.get(Calendar.MONTH))
                .setTitle("Select month and year ")
                .setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {
                    @Override
                    public void onMonthChanged(int selectedMonth) {
                        Log.d("a", "Selected month : " + selectedMonth);
                        // Toast.makeText(MainActivity.this, " Selected month : " + selectedMonth, Toast.LENGTH_SHORT).show();
                    }
                })
                .setOnYearChangedListener(new MonthPickerDialog.OnYearChangedListener() {
                    @Override
                    public void onYearChanged(int selectedYear) {

                        Log.d("a", "Selected year : " + selectedYear);
                        // Toast.makeText(MainActivity.this, " Selected year : " + selectedYear, Toast.LENGTH_SHORT).show();
                    }
                })
                .setMaxYear(2040)
                .build()
                .show();

    }

    private boolean validation(){
        if(edtFullName.getText().toString().isEmpty()){
            edtFullName.setError("The card name must be filled.");
            return false;
        }
        else if(edtCardNumber.getText().toString().isEmpty()){
            edtCardNumber.setError("The card number must be filled.");
            return false;
        }
        else if(edtExpiryDate.getText().toString().isEmpty()){
            edtExpiryDate.setError("The card expiry date must be filled.");
            return false;
        }
        else if(edtSecurityNumber.getText().toString().isEmpty()){
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