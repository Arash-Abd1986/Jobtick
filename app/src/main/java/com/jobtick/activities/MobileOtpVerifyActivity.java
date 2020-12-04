package com.jobtick.activities;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.fragments.SignInFragment;
import com.jobtick.utils.Constant;
import com.jobtick.utils.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MobileOtpVerifyActivity extends ActivityBase {


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
    StringBuilder sb;

    String phone_number = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_mobile);
        ButterKnife.bind(this);
        sb = new StringBuilder();
        otpEnterInEdtText();

        if (getIntent() != null && getIntent().getExtras().containsKey("phone_number")) {
            phone_number = getIntent().getExtras().getString("phone_number", "");
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void otpEnterInEdtText() {
        // Get clipboard manager object.
        edtFirst.addListener(() -> pasteData());

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
                    Helper.closeKeyboard(MobileOtpVerifyActivity.this);
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

        Object clipboardService = getSystemService(Context.CLIPBOARD_SERVICE);
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
            getOTPVerified(otp);
        } else {
        }

    }

    public void getOTPVerified
            (String otp) {
        showProgressDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_OTP_VERIFICATION,
                response -> {

                    hideProgressDialog();
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                showCustomDialog("Verification Done! ");
                            }
                        }

                    } catch (JSONException e) {
                        Log.e("EXCEPTION", String.valueOf(e));
                        e.printStackTrace();

                    }

                },
                error -> {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        // Print Error!
                        Log.e("intent22", jsonError);
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                            String message = jsonObject_error.getString("message");
                            if (message.equalsIgnoreCase("unauthorized")) {
                                Fragment fragment = new SignInFragment();
                                //   switchContent(fragment);
                              /*  FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.container, fragment);
                                ft.commit();*/
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            showToast(message, MobileOtpVerifyActivity.this);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", MobileOtpVerifyActivity.this);
                    }
                    hideProgressDialog();
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());

                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("phone_number", phone_number);
                map1.put("otp", otp);
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showCustomDialog(String message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_show_warning);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        TextViewRegular txtMessage = dialog.findViewById(R.id.txt_message);
        txtMessage.setText(message);

        ((AppCompatButton) dialog.findViewById(R.id.btn_ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                finish();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


}
