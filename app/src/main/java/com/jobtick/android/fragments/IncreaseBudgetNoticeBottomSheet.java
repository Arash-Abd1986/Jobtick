package com.jobtick.android.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jobtick.android.R;
import com.jobtick.android.activities.TaskDetailsActivity;
import com.jobtick.android.activities.ActivityBase;
import com.jobtick.android.models.TaskModel;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.HttpStatus;
import com.jobtick.android.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

import static com.jobtick.android.utils.Constant.URL_ADDITIONAL_FUND;

public class IncreaseBudgetNoticeBottomSheet extends AbstractStateExpandedBottomSheet {

    TextView name;
    TextView description;
    TextView oldPrice;
    TextView newPrice;
    TextView reason;
    Button decline;
    Button accept;
    Button btnWithdraw;
    LinearLayout llAcceptDecline;
    LinearLayout llWithDraw;

    protected ProgressDialog pDialog;
    static boolean isMine=false;

    private TaskModel taskModel;
    private SessionManager sessionManager;

    private NoticeListener listener;

    public static IncreaseBudgetNoticeBottomSheet newInstance(TaskModel taskModel){
        isMine=false;
        Bundle bundle = new Bundle();
    //    bundle.putParcelable(ConstantKey.TASK, taskModel);
        IncreaseBudgetNoticeBottomSheet fragment = new IncreaseBudgetNoticeBottomSheet();
        fragment.setArguments(bundle);
        return fragment;
    }
    public static IncreaseBudgetNoticeBottomSheet newInstance(TaskModel taskModel,boolean isMineRequest){
        isMine = isMineRequest;
        Bundle bundle = new Bundle();
        //    bundle.putParcelable(ConstantKey.TASK, taskModel);
        IncreaseBudgetNoticeBottomSheet fragment = new IncreaseBudgetNoticeBottomSheet();
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
        View view = inflater.inflate(R.layout.bottom_sheet_increase_budget_notice, container, false);
        sessionManager = new SessionManager(getContext());

        assert getArguments() != null;
        taskModel = TaskDetailsActivity.taskModel;


        name = view.findViewById(R.id.name);
        llAcceptDecline = view.findViewById(R.id.lyt_button);
        llWithDraw = view.findViewById(R.id.lytWithDraw);
        description = view.findViewById(R.id.description);
        newPrice = view.findViewById(R.id.new_price);
        oldPrice = view.findViewById(R.id.old_price);
        reason = view.findViewById(R.id.reason_description);
        decline = view.findViewById(R.id.btn_decline);
        accept = view.findViewById(R.id.btn_accept);
        btnWithdraw = view.findViewById(R.id.btnWithdraw);

        decline.setOnClickListener(v -> {
            listener.onIncreaseBudgetRejectClick();
            dismiss();
        });

        accept.setOnClickListener(v -> {
            acceptRequest(taskModel.getAdditionalFund().getId().toString());
        });

        btnWithdraw.setOnClickListener(v -> {
            withdrawRequest(taskModel.getAdditionalFund().getId().toString());
        });
        init();
        initProgressDialog();
        return view;
    }

    private void withdrawRequest(String id) {
        ((ActivityBase)requireActivity()).showProgressDialog();
        StringRequest stringRequest = new StringRequest(StringRequest.Method.DELETE, Constant.BASE_URL + URL_ADDITIONAL_FUND + "/" + id,
                response -> {
                    Timber.e(response);
                    ((ActivityBase)requireActivity()).hideProgressDialog();
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                listener.onIncreaseBudgetWithDrawClick();
                                dismiss();
                            } else {
                                ((ActivityBase)requireActivity()).showToast("Something went Wrong", getContext());
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
                        ((ActivityBase)requireActivity()).showToast("Something Went Wrong", getContext());
                    }
                    Timber.e(error.toString());
                    ((ActivityBase)requireActivity()).hideProgressDialog();
                }) {


            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<String, String>();

                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                //   map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }

    private void init() {
        //TODO: API is giving increased price, but it should get all new price, so
        //we calculate new increased price, after API updating, we bring back it.
        int oldP = Integer.parseInt(taskModel.getAmount().toString());
        int newP = Integer.parseInt(taskModel.getAdditionalFund().getAmount().toString()) + oldP;
        name.setText(taskModel.getPoster().getName());

        description.setText(taskModel.getTitle());
        reason.setText(taskModel.getAdditionalFund().getCreationReason());
        newPrice.setText(String.format(Locale.ENGLISH, "%d", newP));
        oldPrice.setText(String.format(Locale.ENGLISH, "%d", oldP));
        if(isMine){
                name.setText("you");
                llAcceptDecline.setVisibility(View.GONE);
                llWithDraw.setVisibility(View.VISIBLE);
        }
    }
    private void acceptRequest(String id) {
        ((ActivityBase)requireActivity()).showProgressDialog();
        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, Constant.BASE_URL + URL_ADDITIONAL_FUND + "/" + id + "/accept",
                response -> {
                    Timber.e(response);
                    ((ActivityBase)requireActivity()).hideProgressDialog();
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                listener.onIncreaseBudgetAcceptClick();
                                dismiss();
                            } else {
                                ((ActivityBase)requireActivity()).showToast("Something went Wrong", getContext());
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
                        ((ActivityBase)requireActivity()).showToast("Something Went Wrong", getContext());
                    }
                    Timber.e(error.toString());
                    ((ActivityBase)requireActivity()).hideProgressDialog();
                }) {


            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<String, String>();

                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                //   map1.put("X-Requested-With", "XMLHttpRequest");
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

//    private void getPaymentMethod() {
//        showProgressDialog();
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_PAYMENTS_METHOD,
//                response -> {
//                    Timber.e(response);
//                    hideProgressDialog();
//                    try {
//                        JSONObject jsonObject = new JSONObject(response);
//                        Timber.e(jsonObject.toString());
//                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
//                            if (jsonObject.getBoolean("success")) {
//                                if (jsonObject.has("data") && !jsonObject.isNull("data")) {
//                                    JSONObject jsonObject_data = jsonObject.getJSONObject("data");
//                                    if (jsonObject_data.has("card") && !jsonObject_data.isNull("card")) {
//                                        JSONObject jsonObject_card = jsonObject_data.getJSONObject("card");
//                                        PaymentMethodModel paymentMethodModel = new PaymentMethodModel().getJsonToModel(jsonObject_card);
//                                        setUpLayout(paymentMethodModel);
//                                    } else {
//                                        showToast("card not Available", IncrementBudgetRequestViewActivity.this);
//                                    }
//
//                                }
//                            } else {
//                                showToast("Something went Wrong", IncrementBudgetRequestViewActivity.this);
//                            }
//                        }
//                    } catch (JSONException e) {
//                        Timber.e(String.valueOf(e));
//                        e.printStackTrace();
//                    }
//                },
//                error -> {
//                    NetworkResponse networkResponse = error.networkResponse;
//                    if (networkResponse != null && networkResponse.data != null) {
//                        String jsonError = new String(networkResponse.data);
//                        // Print Error!
//                        Timber.e(jsonError);
//                        try {
//                            JSONObject jsonObject = new JSONObject(jsonError);
//                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");
//                            if (jsonObject_error.has("error_text") && !jsonObject_error.isNull("error_text")) {
//                                if (ConstantKey.NO_PAYMENT_METHOD.equalsIgnoreCase(jsonObject_error.getString("error_text"))) {
//                                    setUpAddPaymentLayout();
//                                }
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    Timber.e(error.toString());
//                    errorHandle1(error.networkResponse);
//                    hideProgressDialog();
//                }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> map1 = new HashMap<String, String>();
//                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
//                map1.put("Content-Type", "application/x-www-form-urlencoded");
//                // map1.put("X-Requested-With", "XMLHttpRequest");
//                return map1;
//            }
//        };
//
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        RequestQueue requestQueue = Volley.newRequestQueue(IncrementBudgetRequestViewActivity.this);
//        requestQueue.add(stringRequest);
//        Log.e(TAG, stringRequest.getUrl());
//    }


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
        void onIncreaseBudgetAcceptClick();
        void onIncreaseBudgetRejectClick();
        void onIncreaseBudgetWithDrawClick();
    }
}