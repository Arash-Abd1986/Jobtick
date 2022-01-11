package com.jobtick.android.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.jobtick.android.R;
import com.jobtick.android.activities.ActivityBase;
import com.jobtick.android.activities.TaskDetailsActivity;
import com.jobtick.android.adapers.SuburbSearchAdapter;
import com.jobtick.android.models.BillingAdreessModel;
import com.jobtick.android.models.response.searchsuburb.Feature;
import com.jobtick.android.payment.AddBillingAddress;
import com.jobtick.android.payment.AddBillingAddressImpl;
import com.jobtick.android.utils.Helper;
import com.jobtick.android.utils.MyExtensions;
import com.jobtick.android.utils.SessionManager;
import com.jobtick.android.utils.SuburbAutoComplete;
import com.jobtick.android.widget.ExtendedEntryText;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;


public class AddBillingReqFragment extends Fragment implements TextWatcher, SuburbSearchAdapter.SubClickListener {

    MaterialButton btnNext;
    ExtendedEntryText edtAddressLine1;
    ExtendedEntryText edtSuburs;
    ExtendedEntryText edtState;
    ExtendedEntryText edtPostcode;
    ExtendedEntryText edtCountry;
    SessionManager sessionManager;
    private BottomSheetDialogFragment bottomSheet;

    private AddBillingAddress addBillingAddress;

    private final int PLACE_SELECTION_REQUEST_CODE = 10002;

    public AddBillingReqFragment() {
    }
    public void setBottomSheet(@NotNull BottomSheetDialogFragment tickerRequirementsBottomSheet) {
        this.bottomSheet = tickerRequirementsBottomSheet;
    }

    public static AddBillingReqFragment newInstance() {
        return new AddBillingReqFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(getContext());
        edtAddressLine1 = view.findViewById(R.id.edt_address_line_1);
        edtSuburs = view.findViewById(R.id.edt_suburs);
        edtState = view.findViewById(R.id.edt_state);
        edtPostcode = view.findViewById(R.id.edt_postcode);
        edtCountry = view.findViewById(R.id.edt_Country);

        edtState.addTextChangedListener(this);
        edtSuburs.addTextChangedListener(this);
        edtPostcode.addTextChangedListener(this);
        edtAddressLine1.addTextChangedListener(this);
        edtCountry.addTextChangedListener(this);
        MyExtensions.onFocus(edtState.editText, bottomSheet);
        MyExtensions.onFocus(edtSuburs.editText, bottomSheet);
        MyExtensions.onFocus(edtPostcode.editText, bottomSheet);
        MyExtensions.onFocus(edtAddressLine1.editText, bottomSheet);
        MyExtensions.onFocus(edtCountry.editText, bottomSheet);


        btnNext = view.findViewById(R.id.btn_add_card);
        btnNext.setOnClickListener(v -> {
            if (!validation()) return;
            addBillingAddress();
        });

        addBillingAddress = new AddBillingAddressImpl(requireContext(), sessionManager) {
            @Override
            public void onSuccess() {
                ((ActivityBase) getActivity()).hideProgressDialog();
                goNext();
            }

            @Override
            public void onError(Exception e) {
                ((ActivityBase) getActivity()).hideProgressDialog();
                if (Objects.equals(e.getMessage(), "Billing address already exist."))
                    goNext();
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

        BillingAdreessModel billingAdreessModel = ((TaskDetailsActivity) getActivity()).billingAdressModel;
        if (billingAdreessModel != null && billingAdreessModel.getData() != null) {
            edtAddressLine1.setText(billingAdreessModel.getData().getLine1());
            edtSuburs.setText(billingAdreessModel.getData().getLocation());
            edtState.setText(billingAdreessModel.getData().getState());
            edtPostcode.setText(billingAdreessModel.getData().getPost_code());
            edtCountry.setText(getString(R.string.australia));
        }

        edtSuburs.setExtendedViewOnClickListener(() -> {
            SearchSuburbBottomSheet infoBottomSheet = new SearchSuburbBottomSheet(this);
            infoBottomSheet.show(getParentFragmentManager(), null);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_billing_address_req, container, false);
        return view;
    }

    @OnClick(R.id.lyt_btn_change_billing_address)
    public void onViewClicked() {
        if (validation()) return;
        addBillingAddress();
    }

    private void goNext() {
        ((TickerRequirementsBottomSheet) getParentFragment()).changeFragment(3);
    }

    private void addBillingAddress() {
        ((ActivityBase) getActivity()).showProgressDialog();

        addBillingAddress.add(edtAddressLine1.getText(), "",
                edtSuburs.getText(),
                edtState.getText(),
                edtPostcode.getText(),
                "AU");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_SELECTION_REQUEST_CODE && resultCode == RESULT_OK) {

            Helper.closeKeyboard(requireActivity());
            edtSuburs.setText(SuburbAutoComplete.getSuburbName(data));
            edtState.setText(SuburbAutoComplete.getState());
            edtCountry.setText(getString(R.string.australia));
        }
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
        boolean enabled = edtAddressLine1.getText().length() > 0 &&
                edtSuburs.getText().length() > 0 &&
                edtCountry.getText().length() > 0 &&
                edtPostcode.getText().length() == 4 &&
                edtState.getText().length() > 0;

        btnNext.setEnabled(enabled);
    }

    @Override
    public void clickOnSearchedLoc(@NotNull Feature location) {
        Helper.closeKeyboard(requireActivity());
        edtSuburs.setText(location.getPlace_name_en());
        edtState.setText(location.getState());
        edtCountry.setText(getString(R.string.australia));
    }
}