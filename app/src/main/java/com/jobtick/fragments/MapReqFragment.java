package com.jobtick.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;
import com.jobtick.activities.ActivityBase;
import com.jobtick.activities.BillingAddressActivity;
import com.jobtick.activities.PaymentSettingsActivity;
import com.jobtick.activities.TaskDetailsActivity;
import com.jobtick.models.BankAccountModel;
import com.jobtick.models.BillingAdreessModel;
import com.jobtick.payment.AddBankAccountImpl;
import com.jobtick.payment.AddBillingAddress;
import com.jobtick.payment.AddBillingAddressImpl;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static com.jobtick.activities.PaymentSettingsActivity.onBankaccountadded;
import static com.jobtick.utils.Constant.ADD_BILLING;
import static com.jobtick.utils.Constant.BASE_URL;


public class MapReqFragment extends Fragment {

    TextView btnNext;
    EditTextRegular edtAddressLine1;
    EditTextRegular edtSuburs;
    EditTextRegular edtState;
    EditTextRegular edtPostcode;
    EditTextRegular edtCountry;
    SessionManager sessionManager;

    private AddBillingAddress addBillingAddress;

    public MapReqFragment() {
    }

    public static MapReqFragment newInstance() {
        return new MapReqFragment();
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
        btnNext = view.findViewById(R.id.txt_btn_next);
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
                if(Objects.equals(e.getMessage(), "Billing address already exist."))
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

        //TODO: there is no such data, so we disable getting data for all fragments.
//        BillingAdreessModel billingAdreessModel = ((TaskDetailsActivity) getActivity()).billingAdreessModel;
//        if (billingAdreessModel != null && billingAdreessModel.getData() != null) {
//            edtAddressLine1.setText(billingAdreessModel.getData().getLine1());
//            edtSuburs.setText(billingAdreessModel.getData().getLocation());
//            edtState.setText(billingAdreessModel.getData().getState());
//            edtPostcode.setText(billingAdreessModel.getData().getPost_code());
//            edtCountry.setText(billingAdreessModel.getData().getCountry());
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_req, container, false);
        return view;
    }

    @OnClick(R.id.lyt_btn_change_billing_address)
    public void onViewClicked() {
        if (validation()) return;
        addBillingAddress();
    }

    private void goNext(){
        ((RequirementsBottomSheet) getParentFragment()).changeFragment(3);
    }

    private void addBillingAddress() {
        ((ActivityBase) getActivity()).showProgressDialog();

        addBillingAddress.add(edtAddressLine1.getText().toString(),"",
                edtSuburs.getText().toString(),
                edtState.getText().toString(),
                edtPostcode.getText().toString(),
                edtCountry.getText().toString());

    }


    private boolean validation() {
        if (TextUtils.isEmpty(edtAddressLine1.getText().toString().trim())) {
            edtAddressLine1.setError("Address is mandatory");
            return false;
        }

        if (TextUtils.isEmpty(edtSuburs.getText().toString().trim())) {
            edtSuburs.setError("Please enter Suburb");
            return false;
        }
        if (TextUtils.isEmpty(edtState.getText().toString().trim())) {
            edtState.setError("Please enter state");
            return false;
        }
        if (TextUtils.isEmpty(edtPostcode.getText().toString().trim())) {
            edtPostcode.setError("Please enter Passcode");
            return false;
        }
        if (edtPostcode.getText().toString().length() != 4) {
            edtPostcode.setError("Please enter 4 digit Passcode");
            return false;
        }
        if (TextUtils.isEmpty(edtCountry.getText().toString().trim())) {
            edtCountry.setError("Please Enter Country");
            return false;
        }
        return true;
    }


}