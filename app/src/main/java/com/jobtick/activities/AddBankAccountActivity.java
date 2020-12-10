package com.jobtick.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.jobtick.R;
import com.jobtick.models.BankAccountModel;
import com.jobtick.payment.AddBankAccount;
import com.jobtick.payment.AddBankAccountImpl;
import com.jobtick.widget.ExtendedEntryText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddBankAccountActivity extends ActivityBase {

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    @BindView(R.id.edt_account_name)
    ExtendedEntryText edtAccountName;

    @BindView(R.id.edt_bsb)
    ExtendedEntryText edtBsb;

    @BindView(R.id.edt_account_number)
    ExtendedEntryText edtAccountNumber;

    @BindView(R.id.lyt_btn_add_bank_account)
    MaterialButton lytBtnAddBankAccount;


    private AddBankAccount addBankAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_account);
        ButterKnife.bind(this);
        initToolbar();
        initUi();

        this.addBankAccount = new AddBankAccountImpl(this, sessionManager) {
            @Override
            public void onSuccess() {
                hideProgressDialog();
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }

            @Override
            public void onError(Exception e) {
                showToast(e.getMessage(), AddBankAccountActivity.this);
                hideProgressDialog();
            }

            @Override
            public void onValidationError(ErrorType errorType, String message) {
                if(errorType == ErrorType.UnAuthenticatedUser)
                    unauthorizedUser();
                else
                    showToast(message, AddBankAccountActivity.this);
            }
        };
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Bank Account");
    }

    private void initUi() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;
        if (bundle.getParcelable(BankAccountModel.class.getName()) == null) return;
        BankAccountModel bankAccountModel = bundle.getParcelable(BankAccountModel.class.getName());
        if (!bankAccountModel.isSuccess() || bankAccountModel.getData() == null) return;

        edtAccountName.setText(bankAccountModel.getData().getAccount_name());
        edtAccountNumber.setText("xxxxx" + bankAccountModel.getData().getAccount_number());
        edtBsb.setText(bankAccountModel.getData().getBsb_code());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R.id.lyt_btn_add_bank_account)
    public void onViewClicked() {

        if (!validate()) return;
        showProgressDialog();
        addBankAccount.add(edtAccountName.getText().toString(),
                edtBsb.getText().toString(),
                edtAccountNumber.getText().toString());
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
