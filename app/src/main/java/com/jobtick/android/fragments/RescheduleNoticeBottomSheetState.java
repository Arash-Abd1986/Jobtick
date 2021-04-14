package com.jobtick.android.fragments;

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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
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
import com.jobtick.android.utils.TimeHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

import static com.jobtick.android.utils.Constant.URL_ADDITIONAL_FUND;
import static com.jobtick.android.utils.Constant.URL_CREATE_RESCHEDULE;

public class RescheduleNoticeBottomSheetState extends AbstractStateExpandedBottomSheet {

    TextView name;
    TextView description;
    TextView previousDate;
    TextView previousTime;
    TextView newTime;
    TextView reason;
    Button decline;
    Button accept;
    Button btnWithdraw;
    LinearLayout llAcceptDecline;
    LinearLayout llWithDraw;
    static boolean isMine=false;

    private SessionManager sessionManager;
    private TaskModel taskModel;
    private int pos;


    private NoticeListener listener;

    public static RescheduleNoticeBottomSheetState newInstance(TaskModel taskModel, int pos,boolean isMineRequest) {
        isMine = isMineRequest;
        Bundle bundle = new Bundle();
        //    bundle.putParcelable(ConstantKey.TASK, taskModel);
        bundle.putInt(ConstantKey.POSITION, pos);
        RescheduleNoticeBottomSheetState fragment = new RescheduleNoticeBottomSheetState();
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

        View view = inflater.inflate(R.layout.bottom_sheet_reschedule_notice, container, false);
        sessionManager = new SessionManager(requireContext());

        assert getArguments() != null;
        //   taskModel = getArguments().getParcelable(ConstantKey.TASK);
        taskModel = TaskDetailsActivity.taskModel;
        pos = getArguments().getInt(ConstantKey.POSITION);


        name = view.findViewById(R.id.name);
        description = view.findViewById(R.id.description);
        llAcceptDecline = view.findViewById(R.id.lyt_button);
        llWithDraw = view.findViewById(R.id.lytWithDraw);
        previousDate = view.findViewById(R.id.txt_previous_date);
        previousTime = view.findViewById(R.id.txt_previous_time);
        newTime = view.findViewById(R.id.txt_new_time);
        reason = view.findViewById(R.id.reason_description);
        decline = view.findViewById(R.id.btn_decline);
        accept = view.findViewById(R.id.btn_accept);
        btnWithdraw = view.findViewById(R.id.btnWithdraw);

        decline.setOnClickListener(v -> {
            declineRequest();
        });

        accept.setOnClickListener(v -> {
            acceptRequest();
        });
        btnWithdraw.setOnClickListener(v -> {
            withdrawRequest();
        });

        init();
        return view;
    }

    private void init() {
        name.setText(taskModel.getPoster().getName());
        description.setText(taskModel.getTitle());
        reason.setText(taskModel.getRescheduleReqeust().get(pos).getReason());
        newTime.setText(TimeHelper.convertToJustDateFormat(taskModel.getRescheduleReqeust().get(pos).getNew_duedate()));
        previousDate.setText(taskModel.getDueDate());

        if (taskModel.getDueTime().getAnytime())
            previousTime.setText(R.string.anytime);
        if (taskModel.getDueTime().getMorning())
            previousTime.setText(R.string.morning);
        if (taskModel.getDueTime().getEvening())
            previousTime.setText(R.string.evening);
        if (taskModel.getDueTime().getAfternoon())
            previousTime.setText(R.string.afternoon);

        if(isMine){
            name.setText("You");
            llAcceptDecline.setVisibility(View.GONE);
            llWithDraw.setVisibility(View.VISIBLE);
        }
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
    private void withdrawRequest() {
        ((ActivityBase)requireActivity()).showProgressDialog();
        StringRequest stringRequest = new StringRequest(StringRequest.Method.DELETE, Constant.BASE_URL + URL_CREATE_RESCHEDULE + "/" + taskModel.getRescheduleReqeust().get(pos).getId(),
                response -> {
                    Timber.e(response);
                    ((ActivityBase)requireActivity()).hideProgressDialog();
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                listener.onRescheduleWithDraw();
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
                map1.put("Version", String.valueOf(BuildConfig.VERSION_CODE));
                //   map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }

    public void acceptRequest() {

        ((ActivityBase) requireActivity()).showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.BASE_URL + URL_CREATE_RESCHEDULE + "/" +
                taskModel.getRescheduleReqeust().get(pos).getId() + "/accept",
                response -> {
                    Timber.e(response);

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                listener.onRescheduleTimeAcceptDeclineClick();
                                dismiss();
                            } else {
                                ((ActivityBase) requireActivity()).showToast("Something went wrong", requireContext());
                            }
                        } else {
                            ((ActivityBase) requireActivity()).showToast("Something went wrong", requireContext());
                        }
                        ((ActivityBase) requireActivity()).hideProgressDialog();
                    } catch (JSONException e) {
                        ((ActivityBase) requireActivity()).hideProgressDialog();
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
                            ((ActivityBase) requireActivity()).unauthorizedUser();
                            ((ActivityBase) requireActivity()).hideProgressDialog();
                            return;
                        }
                        ((ActivityBase) requireActivity()).hideProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                            if (jsonObject_error.has("message")) {
                                ((ActivityBase)requireActivity()).showToast(jsonObject_error.getString("message"), requireContext());
                            }

                        } catch (JSONException e) {
                            ((ActivityBase) requireActivity()).showToast("Something Went Wrong", requireContext());
                            e.printStackTrace();
                        }
                    } else {
                        ((ActivityBase) requireActivity()).showToast("Something Went Wrong", requireContext());
                    }
                    Timber.e(error.toString());
                    ((ActivityBase) requireActivity()).hideProgressDialog();
                }) {

            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Accept", "application/json");
                map1.put("X-Requested-With", "XMLHttpRequest");
                map1.put("Version", String.valueOf(BuildConfig.VERSION_CODE));
                return map1;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }

    public void declineRequest() {

        ((ActivityBase) requireActivity()).showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.BASE_URL + URL_CREATE_RESCHEDULE + "/" +
                taskModel.getRescheduleReqeust().get(pos).getId() + "/reject",
                response -> {
                    Timber.e(response);

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                listener.onRescheduleTimeAcceptDeclineClick();
                                dismiss();
                            } else {
                                ((ActivityBase) requireActivity()).showToast("Something went wrong", requireContext());
                            }
                        } else {
                            ((ActivityBase) requireActivity()).showToast("Something went wrong", requireContext());
                        }
                        ((ActivityBase) requireActivity()).hideProgressDialog();
                    } catch (JSONException e) {
                        ((ActivityBase) requireActivity()).hideProgressDialog();
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
                            ((ActivityBase) requireActivity()).unauthorizedUser();
                            ((ActivityBase) requireActivity()).hideProgressDialog();
                            return;
                        }
                        ((ActivityBase) requireActivity()).hideProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                            if (jsonObject_error.has("message")) {
                                ((ActivityBase)requireActivity()).showToast(jsonObject_error.getString("message"), requireContext());
                            }

                        } catch (JSONException e) {
                            ((ActivityBase) requireActivity()).showToast("Something Went Wrong", requireContext());
                            e.printStackTrace();
                        }
                    } else {
                        ((ActivityBase) requireActivity()).showToast("Something Went Wrong", requireContext());
                    }
                    Timber.e(error.toString());
                    ((ActivityBase) requireActivity()).hideProgressDialog();
                }) {

            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Accept", "application/json");
                map1.put("X-Requested-With", "XMLHttpRequest");
                map1.put("Version", String.valueOf(BuildConfig.VERSION_CODE));
                return map1;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map1 = new HashMap<String, String>();
                //TODO: we put an empty string because there is no declined reason in design.
                map1.put("declined_reason", "There is no declined reason in design, so just fill it.");
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }


    public interface NoticeListener {
        void onRescheduleTimeAcceptDeclineClick();
        void onRescheduleWithDraw();
    }
}
