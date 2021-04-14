package com.jobtick.android.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jobtick.android.BuildConfig;
import com.jobtick.android.R;
import com.jobtick.android.activities.ActivityBase;
import com.jobtick.android.activities.TaskDetailsActivity;
import com.jobtick.android.models.TaskModel;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.ConstantKey;
import com.jobtick.android.utils.HttpStatus;
import com.jobtick.android.utils.SessionManager;
import com.jobtick.android.widget.ExtendedCommentText;
import com.jobtick.android.widget.ExtendedEntryText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

import static com.jobtick.android.utils.Constant.URL_BUDGET_Increment;


public class IncreaseBudgetBottomSheet extends AbstractStateExpandedBottomSheet {

    private TextView oldPrice;
    private TextView serviceFee;
    private TextView receiveMoney;
    private ExtendedEntryText newPrice;
    private ExtendedCommentText reason;
    private Button submit;

    protected ProgressDialog pDialog;

    private int total_budget = 0;
    private TaskModel taskModel;
    private SessionManager sessionManager;


    private NoticeListener listener;

    public static IncreaseBudgetBottomSheet newInstance(TaskModel taskModel, int pos){
        Bundle bundle = new Bundle();
    //    bundle.putParcelable(ConstantKey.TASK, taskModel);
        bundle.putInt(ConstantKey.POSITION, pos);
        IncreaseBudgetBottomSheet fragment = new IncreaseBudgetBottomSheet();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_BottomSheetDialog);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_increase_budget, container, false);
        sessionManager = new SessionManager(getContext());
     //   assert getArguments() != null;
     //   taskModel = getArguments().getParcelable(ConstantKey.TASK);
        taskModel = TaskDetailsActivity.taskModel;

        oldPrice = view.findViewById(R.id.old_price);
        serviceFee = view.findViewById(R.id.service_fee);
        receiveMoney = view.findViewById(R.id.receive_money);
        newPrice = view.findViewById(R.id.new_price);
        reason = view.findViewById(R.id.reason);
        submit = view.findViewById(R.id.submit);


        submit.setOnClickListener(v -> {
            if(!validation()) return;
            //TODO: API is giving increased price, but it should get all new price, so
            //we calculate new increased price, after API updating, we bring back it.
            int increasedPrice = Integer.parseInt(newPrice.getText()) - Integer.parseInt(oldPrice.getText().toString());
            submitIncreaseBudget(Integer.toString(increasedPrice), reason.getText().trim());
        });

        newPrice.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    setupBudget(Integer.parseInt(s.toString()));
                }
            }
        });

        initProgressDialog();
        init();
        return view;
    }

    private void init(){
        oldPrice.setText(String.format(Locale.ENGLISH, "%d", taskModel.getAmount()));
    }


    private void setupBudget(int budget) {
        float worker_service_fee = taskModel.getWorker().getWorkerTier().getServiceFee();
        float service_fee = ((budget * worker_service_fee) / 100);
        serviceFee.setText(String.format("$ %s", service_fee));
        total_budget = (int) (budget - ((budget * worker_service_fee) / 100));
        receiveMoney.setText(String.format(Locale.ENGLISH, "$ %d", total_budget));
    }

    public void initProgressDialog() {
        pDialog = new ProgressDialog(getContext());
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


    private void submitIncreaseBudget(String increase_budget, String increase_budget_reason) {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, Constant.URL_TASKS + "/" + taskModel.getSlug() + URL_BUDGET_Increment,
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                listener.onSubmitIncreasePrice();
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
                            hideProgressDialog();
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
                        ((ActivityBase)requireActivity()).showToast("Something went Wrong", getContext());
                    }
                    Timber.e(error.toString());
                    hideProgressDialog();
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();

                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                map1.put("Version", String.valueOf(BuildConfig.VERSION_CODE));
                return map1;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<>();
                map1.put("amount", increase_budget);
                map1.put("creation_reason", increase_budget_reason);

                Timber.e(String.valueOf(map1.size()));
                Timber.e(map1.toString());
                return map1;

            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }


    private boolean validation(){
        if(newPrice.getText().length() == 0){
            newPrice.setError("Please enter new price");
            return false;
        }
        if(Integer.parseInt(newPrice.getText()) < 5 || Integer.parseInt(newPrice.getText()) > 9999){
            newPrice.setError("Between 5 and 9999");
            return false;
        }
        if(Integer.parseInt(newPrice.getText()) <= Integer.parseInt(oldPrice.getText().toString())) {
            newPrice.setError("Please increase price");
            return false;
        }
        if(Integer.parseInt(newPrice.getText()) > (Integer.parseInt(oldPrice.getText().toString()) + 500)) {
            newPrice.setError("No more than $ 500!");
            return false;
        }
        else if(reason.getText().length() < reason.geteMinSize()){
            reason.setError("");
            return false;
        }
        return true;
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
        void onSubmitIncreasePrice();
    }
}