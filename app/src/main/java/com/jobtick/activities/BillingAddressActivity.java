package com.jobtick.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.models.BillingAdreessModel;
import com.jobtick.payment.AddBillingAddress;
import com.jobtick.payment.AddBillingAddressImpl;
import com.jobtick.utils.Constant;
import com.jobtick.utils.StateHelper;
import com.jobtick.widget.ExtendedEntryText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BillingAddressActivity extends ActivityBase {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_address_line_1)
    ExtendedEntryText edtAddressLine1;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_address_line_2)
    ExtendedEntryText edtAddressLine2;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_suburs)
    ExtendedEntryText edtSuburs;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_state)
    ExtendedEntryText edtState;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_postcode)
    ExtendedEntryText edtPostcode;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_Country)
    ExtendedEntryText edtCountry;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_change_billing_address)
    MaterialButton lytBtnChangeBillingAddress;

    private AddBillingAddress addBillingAddress;

    private StateHelper stateHelper;
    private boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing_address);
        ButterKnife.bind(this);
        stateHelper = new StateHelper(this);
        initUi();
        initToolbar();
        edtState.setAdapter(stateHelper.getStates());
        addBillingAddress = new AddBillingAddressImpl(this, sessionManager) {
            @Override
            public void onSuccess() {
                hideProgressDialog();
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }

            @Override
            public void onError(Exception e) {
                showToast(e.getMessage(), BillingAddressActivity.this);
            }

            @Override
            public void onValidationError(ErrorType errorType, String message) {
                if (errorType == ErrorType.UnAuthenticatedUser)
                    unauthorizedUser();
                else
                    showToast(message, BillingAddressActivity.this);
            }
        };
    }

    private void initUi(){
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;
        if (bundle.getParcelable(BillingAdreessModel.class.getName()) == null) return;
        BillingAdreessModel billingAdreessModel = bundle.getParcelable(BillingAdreessModel.class.getName());
        if(!billingAdreessModel.isSuccess() || billingAdreessModel.getData() == null) return;

        edtAddressLine1.setText(billingAdreessModel.getData().getLine1());
        edtAddressLine2.setText(billingAdreessModel.getData().getLine2());
        String state = stateHelper.getStateName(billingAdreessModel.getData().getState());
        edtState.setText(state);
        edtSuburs.setText(billingAdreessModel.getData().getCity());
        edtPostcode.setText(billingAdreessModel.getData().getPost_code());
        edtCountry.setText(getString(R.string.australia));

        editMode = bundle.getBoolean(Constant.EDIT_MODE);
        if(editMode)
            lytBtnChangeBillingAddress.setText(R.string.edit_billing_address);
    }


    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(editMode)
            getSupportActionBar().setTitle(getString(R.string.edit_billing_address));
        else
            getSupportActionBar().setTitle(getString(R.string.add_billing_address));
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

    @OnClick(R.id.lyt_btn_change_billing_address)
    public void onViewClicked() {

        if (!validation()) return;
        showProgressDialog();
        addBillingAddress.add(edtAddressLine1.getText(),
                edtAddressLine2.getText(),
                edtSuburs.getText(),
                stateHelper.getStateAbr(edtState.getText()),
                edtPostcode.getText(), "AU");

    }

    private boolean validation() {
        if (TextUtils.isEmpty(edtAddressLine1.getText().trim())) {
            edtAddressLine1.setError("Address is mandatory");
            return false;
        }

        if (TextUtils.isEmpty(edtSuburs.getText().trim())) {
            edtSuburs.setError("Please enter Suburb");
            return false;
        }
        if (TextUtils.isEmpty(edtState.getText().trim())) {
            edtState.setError("Please enter state");
            return false;
        }
        if (TextUtils.isEmpty(edtPostcode.getText().trim())) {
            edtPostcode.setError("Please enter Passcode");
            return false;
        }
        if (edtPostcode.getText().length() != 4) {
            edtPostcode.setError("Please enter 4 digit Passcode");
            return false;
        }
        if (TextUtils.isEmpty(edtCountry.getText().trim())) {
            edtCountry.setError("Please Enter Country");
            return false;
        }
        if(!stateHelper.isCorrectState(edtState.getText())) {
            edtState.setError("State is not correct!");
            return false;
        }
        return true;
    }
}
