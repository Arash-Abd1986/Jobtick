package com.jobtick.android.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jobtick.android.BuildConfig;
import com.jobtick.android.R;
import com.jobtick.android.activities.TaskDetailsActivity;
import com.jobtick.android.activities.ActivityBase;
import com.jobtick.android.models.TaskModel;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.HttpStatus;
import com.jobtick.android.utils.SessionManager;
import com.jobtick.android.widget.ExtendedCommentText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

import static com.jobtick.android.utils.Constant.URL_ADDITIONAL_FUND;

public class IncreaseBudgetDeclineBottomSheet extends BottomSheetDialogFragment {

    ExtendedCommentText reason;
    Button submit;
    protected ProgressDialog pDialog;

    private TaskModel taskModel;
    private SessionManager sessionManager;

    private NoticeListener listener;

    public static IncreaseBudgetDeclineBottomSheet newInstance(TaskModel taskModel){
        Bundle bundle = new Bundle();
    //    bundle.putParcelable(ConstantKey.TASK, taskModel);
        IncreaseBudgetDeclineBottomSheet fragment = new IncreaseBudgetDeclineBottomSheet();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_BottomSheetDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_increase_budget_decline, container, false);
        sessionManager = new SessionManager(getContext());

        assert getArguments() != null;
        taskModel = TaskDetailsActivity.taskModel;


        submit = view.findViewById(R.id.submit);
        reason = view.findViewById(R.id.reason);

        submit.setOnClickListener(v -> {
            if(!validation()) return;
            rejectRequest(taskModel.getAdditionalFund().getId().toString(), reason.getText());
        });

        initProgressDialog();
        return view;
    }


    private boolean validation(){
        if(submit.getText().length() == 0){
            submit.setError("Please enter reason");
            return false;
        }
        if(reason.getText().length() < reason.geteMinSize()) {
            submit.setError("");
            return false;
        }
        return true;
    }

    private void rejectRequest(String id, String rejectReason) {
        ((ActivityBase)requireActivity()).showProgressDialog();
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, Constant.BASE_URL + URL_ADDITIONAL_FUND + "/" + id + "/reject",
                response -> {
                    Timber.e(response);
                    ((ActivityBase)requireActivity()).hideProgressDialog();
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                listener.onIncreaseBudgetSubmitClick();
                                dismiss();
                            } else {
                                ((ActivityBase)requireActivity()).showToast("Something went Wrong", requireContext());
                            }
                        }


                    } catch (JSONException e) {
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();

                    }


                },
                error -> {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        // Print Error!
                        Timber.e(jsonError);
                        if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                            ((ActivityBase)requireActivity()).unauthorizedUser();
                            ((ActivityBase)requireActivity()).hideProgressDialog();
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);


                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");


                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                                if (jsonObject_errors.has("amount") && !jsonObject_errors.isNull("amount")) {
                                    JSONArray jsonArray_amount = jsonObject_errors.getJSONArray("amount");
                                    ((ActivityBase)requireActivity()).showToast(jsonArray_amount.getString(0), requireContext());
                                } else if (jsonObject_errors.has("creation_reason") && !jsonObject_errors.isNull("creation_reason")) {
                                    JSONArray jsonArray_amount = jsonObject_errors.getJSONArray("creation_reason");
                                    ((ActivityBase)requireActivity()).showToast(jsonArray_amount.getString(0), requireContext());
                                }
                            } else {
                                if (jsonObject_error.has("message")) {
                                    ((ActivityBase)requireActivity()).showToast(jsonObject_error.getString("message"), requireContext());
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ((ActivityBase)requireActivity()).showToast("Something Went Wrong", requireContext());
                    }
                    Timber.e(error.toString());
                    ((ActivityBase)requireActivity()).hideProgressDialog();
                }) {


            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<String, String>();

                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                map1.put("Version", String.valueOf(BuildConfig.VERSION_CODE));
                return map1;
            }


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();

                map1.put("rejection_reason", rejectReason);
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }


    public void initProgressDialog() {
        pDialog = new ProgressDialog(requireContext());
        pDialog.setTitle(getString(R.string.processing));
        pDialog.setMessage(getString(R.string.please_wait));
        pDialog.setCancelable(false);
    }


    public void showProgressDialog() {

        if (!pDialog.isShowing())
            pDialog.show();
    }

    public void hideProgressDialog() {

        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (NoticeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(this.toString()
                    + " must implement NoticeListener");
        }
    }


    public interface NoticeListener {
        void onIncreaseBudgetSubmitClick();
    }
}