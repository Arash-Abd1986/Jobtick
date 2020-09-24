package com.jobtick.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;
import com.jobtick.activities.AuthActivity;
import com.jobtick.utils.Helper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPassword2Fragment extends Fragment {

    String email;
    @BindView(R.id.edt_first)
    EditTextRegular edtFirst;
    @BindView(R.id.edt_second)
    EditTextRegular edtSecond;
    @BindView(R.id.edt_third)
    EditTextRegular edtThird;
    @BindView(R.id.edt_fourth)
    EditTextRegular edtFourth;
    @BindView(R.id.edt_fifth)
    EditTextRegular edtFifth;
    @BindView(R.id.edt_sixth)
    EditTextRegular edtSixth;
    AuthActivity authActivity;
    StringBuilder sb;
    @BindView(R.id.lyt_btn_finish)
    LinearLayout lytBtnFinish;

    public ForgotPassword2Fragment() {
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
        sb = new StringBuilder();
        otpEnterInEdtText();
        return view;
    }

    private void otpEnterInEdtText() {
        // Get clipboard manager object.
        edtFirst.addListener(new EditTextRegular.GoEditTextListener() {
            @Override
            public void onUpdate() {
                pasteData();
            }
        });

        edtFirst.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (sb.length() == 0 & edtFirst.length() == 1) {
                    sb.append(s);
                    edtFirst.clearFocus();
                    edtSecond.requestFocus();
                    edtSecond.setCursorVisible(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

                if (sb.length() == 1) {
                    sb.deleteCharAt(0);
                }
            }

            public void afterTextChanged(Editable s) {
                if (sb.length() == 0) {
                    edtFirst.requestFocus();
                }

            }
        });

        edtSecond.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (sb.length() == 1 & edtSecond.length() == 1) {
                    sb.append(s);
                    edtSecond.clearFocus();
                    edtThird.requestFocus();
                    edtThird.setCursorVisible(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

                if (sb.length() == 2) {
                    sb.deleteCharAt(1);
                }
            }

            public void afterTextChanged(Editable s) {
                if (sb.length() == 1) {
                    edtSecond.requestFocus();
                }

            }
        });

        edtThird.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (sb.length() == 2 & edtThird.length() == 1) {
                    sb.append(s);
                    edtThird.clearFocus();
                    edtFourth.requestFocus();
                    edtFourth.setCursorVisible(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

                if (sb.length() == 3) {
                    sb.deleteCharAt(2);
                }
            }

            public void afterTextChanged(Editable s) {
                if (sb.length() == 2) {
                    edtThird.requestFocus();
                }

            }
        });

        edtFourth.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (sb.length() == 3 & edtFourth.length() == 1) {
                    sb.append(s);
                    edtFourth.clearFocus();
                    edtFifth.requestFocus();
                    edtFifth.setCursorVisible(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

                if (sb.length() == 4) {
                    sb.deleteCharAt(3);
                }
            }

            public void afterTextChanged(Editable s) {
                if (sb.length() == 3) {
                    edtFourth.requestFocus();
                }

            }
        });

        edtFifth.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (sb.length() == 4 & edtFifth.length() == 1) {
                    sb.append(s);
                    edtFifth.clearFocus();
                    edtSixth.requestFocus();
                    edtSixth.setCursorVisible(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

                if (sb.length() == 5) {
                    sb.deleteCharAt(4);
                }
            }

            public void afterTextChanged(Editable s) {
                if (sb.length() == 4) {
                    edtFifth.requestFocus();
                }

            }
        });

        edtSixth.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (sb.length() == 5 & edtSixth.length() == 1) {
                    sb.append(s);
                    edtSixth.clearFocus();
                    Helper.closeKeyboard(authActivity);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

                if (sb.length() == 6) {
                    sb.deleteCharAt(5);
                }
            }

            public void afterTextChanged(Editable s) {
                if (sb.length() == 5) {
                    edtSixth.requestFocus();
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

            // Set the text to target textview.
            /// Toast.makeText(authActivity, text, Toast.LENGTH_SHORT).show();
            edtFirst.setText(String.valueOf(text.charAt(0)));
            edtSecond.setText(String.valueOf(text.charAt(1)));
            edtThird.setText(String.valueOf(text.charAt(2)));
            edtFourth.setText(String.valueOf(text.charAt(3)));
            edtFifth.setText(String.valueOf(text.charAt(4)));
            edtSixth.setText(String.valueOf(text.charAt(5)));

        }
    }

    @OnClick(R.id.lyt_btn_finish)
    public void onViewClicked() {
        String otp = edtFirst.getText().toString().trim() +
                edtSecond.getText().toString().trim() +
                edtThird.getText().toString().trim() +
                edtFourth.getText().toString().trim() +
                edtFifth.getText().toString().trim() +
                edtSixth.getText().toString().trim();
        if (otp.length() == 6) {
            authActivity.forgotPasswordverification(email, otp);
        } else {
            authActivity.showToast("check otp", authActivity);


        }

    }
}