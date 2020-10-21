package com.jobtick.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.Button.ButtonRegular;
import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.activities.AuthActivity;
import com.jobtick.utils.Helper;
import com.jobtick.utils.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class VerifyAccountFragment extends Fragment {
    String email, password;
    SessionManager sessionManager;
    @BindView(R.id.edt_verification_code)
    EditTextRegular edtVerificationCode;

    @BindView(R.id.email_verify_message)
    TextViewRegular emailVerifyMessage;

    AuthActivity authActivity;
    @BindView(R.id.lyt_btn_finish)
    LinearLayout lytBtnFinish;

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    public VerifyAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_verify_account, container, false);
        ButterKnife.bind(this, view);
        authActivity = (AuthActivity) getActivity();
        email = getArguments().getString("email");
        password = getArguments().getString("password");
        sessionManager = new SessionManager(getActivity());
        otpEnterInEdtText();

        toolbar.setNavigationOnClickListener(v->{
            authActivity.onBackPressed();
        });

        emailVerifyMessage.setText(email);
        return view;
    }

    private void otpEnterInEdtText() {
        // Get clipboard manager object.
        edtVerificationCode.addListener(new EditTextRegular.GoEditTextListener() {
            @Override
            public void onUpdate() {
                pasteData();
            }
        });


        edtVerificationCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("debug verification: ", s.toString());
                if(s.length() > 6){
                    edtVerificationCode.setText(s.subSequence(0, 6));
                    edtVerificationCode.setSelection(6);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtVerificationCode.getText().length() == 6) {
                    Helper.closeKeyboard(authActivity);
                }
            }

        });
    }

    private void pasteData() {

        Object clipboardService = authActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        final ClipboardManager clipboardManager = (ClipboardManager) clipboardService;
        // Get clip data from clipboard.
        ClipData clipData = clipboardManager.getPrimaryClip();
        // Get item count.
        int itemCount = clipData.getItemCount();
        if (itemCount > 0) {
            // Get source text.
            ClipData.Item item = clipData.getItemAt(0);
            String text = item.getText().toString();

            edtVerificationCode.setText(text);
        }
    }

    @OnClick(R.id.lyt_btn_finish)
    public void onViewClicked() {
        String otp = edtVerificationCode.getText().toString().trim();

        if (validation())
            authActivity.verification(email, password, otp);
    }

    private boolean validation() {
        if(edtVerificationCode.getText().toString().trim().length() != 6){
            edtVerificationCode.setError("Verification code must be equal to 6 characters.");
            return false;
        }
        return true;
    }

}
