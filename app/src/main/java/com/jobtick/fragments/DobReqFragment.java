package com.jobtick.fragments;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.jobtick.R;
import com.jobtick.activities.ActivityBase;
import com.jobtick.activities.TaskDetailsActivity;
import com.jobtick.models.UserAccountModel;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.SessionManager;
import com.jobtick.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

import static com.jobtick.utils.Constant.BASE_URL;
import static com.jobtick.utils.Constant.MIN_AGE_FOR_USE_APP;
import static com.jobtick.utils.Constant.PROFILE_INFO;

public class DobReqFragment extends Fragment {

    private UserAccountModel userAccountModel;
    MaterialButton btnNext;
    SessionManager sessionManager;

    int year, month, day;
    String str_DOB = null;
    String str_DOB_MODEL = "";
    DatePickerDialog.OnDateSetListener mDateSetListener;
    TextView txtBirthDate;

    public DobReqFragment() {
    }

    public static DobReqFragment newInstance() {
        return new DobReqFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(getContext());
        btnNext = view.findViewById(R.id.btn_add_card);

        txtBirthDate = view.findViewById(R.id.txt_birth_date);
        txtBirthDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, -(MIN_AGE_FOR_USE_APP));
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    mDateSetListener,
                    year, month, day);
            dialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        btnNext.setOnClickListener(v -> {
            updateBirthDate();
        });

        mDateSetListener = (view1, year, month, dayOfMonth) -> {
            month = month + 1;
            str_DOB_MODEL = year + "-" + month + "-" + dayOfMonth;
            str_DOB = Tools.getDayMonthDateTimeFormat2(year + "-" + month + "-" + dayOfMonth);
            txtBirthDate.setText(str_DOB);
            btnNext.setEnabled(true);
        };

        UserAccountModel userAccountModel = ((TaskDetailsActivity) getActivity()).userAccountModel;
        if (userAccountModel != null && userAccountModel.getDob() != null) {
            txtBirthDate.setText(Tools.getDayMonthDateTimeFormat2(userAccountModel.getDob()));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dob_req, container, false);
    }

    private void updateBirthDate() {
        ((ActivityBase) requireActivity()).showProgressDialog();
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, BASE_URL + PROFILE_INFO,
                response -> {
                    ((ActivityBase) requireActivity()).hideProgressDialog();
                    Timber.e(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                goNext();
                            } else {
                                ((ActivityBase) getActivity()).showToast("something went wrong.", requireContext());
                            }
                        }
                    } catch (JSONException e) {
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                        ((ActivityBase) getActivity()).showToast(e.getMessage(), requireContext());
                    }


                },
                error -> {
                    ((ActivityBase) requireActivity()).hideProgressDialog();
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        // Print Error!
                        Timber.e(jsonError);
                        if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                            ((ActivityBase) getActivity()).unauthorizedUser();
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                            if (jsonObject_error.has("message")) {
                                ((ActivityBase) getActivity()).showToast(jsonObject_error.getString("message"), requireContext());
                            }

                        } catch (JSONException e) {
                            ((ActivityBase) getActivity()).showToast(e.getMessage(), requireContext());
                            e.printStackTrace();
                        }
                    } else {
                        ((ActivityBase) getActivity()).showToast("something went wrong.", requireContext());
                    }
                    Timber.e(error.toString());
                }) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map1 = new HashMap<String, String>();
                if (!str_DOB_MODEL.equals("")) {
                    map1.put("dob", str_DOB_MODEL);
                }

                return map1;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);

    }

    private void goNext() {
        ((TickerRequirementsBottomSheet) getParentFragment()).changeFragment(4);
    }
}