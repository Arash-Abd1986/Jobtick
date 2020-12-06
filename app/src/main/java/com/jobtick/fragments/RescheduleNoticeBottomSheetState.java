package com.jobtick.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jobtick.R;
import com.jobtick.activities.ActivityBase;
import com.jobtick.activities.TaskDetailsActivity;
import com.jobtick.models.TaskModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

import static com.jobtick.utils.Constant.URL_CREATE_RESCHEDULE;

public class RescheduleNoticeBottomSheetState extends AbstractStateExpandedBottomSheet {

    TextView name;
    TextView description;
    TextView previousDate;
    TextView previousTime;
    TextView newTime;
    TextView reason;
    Button decline;
    Button accept;

    private SessionManager sessionManager;
    private TaskModel taskModel;
    private int pos;


    private NoticeListener listener;

    public static RescheduleNoticeBottomSheetState newInstance(TaskModel taskModel, int pos) {
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
        previousDate = view.findViewById(R.id.txt_previous_date);
        previousTime = view.findViewById(R.id.txt_previous_time);
        newTime = view.findViewById(R.id.txt_new_time);
        reason = view.findViewById(R.id.reason_description);
        decline = view.findViewById(R.id.btn_decline);
        accept = view.findViewById(R.id.btn_accept);

        decline.setOnClickListener(v -> {
            declineRequest();
        });

        accept.setOnClickListener(v -> {
            acceptRequest();
        });

        init();
        return view;
    }

    private void init() {
        name.setText(taskModel.getPoster().getName());
        description.setText(taskModel.getTitle());
        reason.setText(taskModel.getRescheduleReqeust().get(pos).getReason());
        newTime.setText(taskModel.getRescheduleReqeust().get(pos).getNew_duedate());
        previousDate.setText(taskModel.getDueDate());

        if (taskModel.getDueTime().getMidday())
            previousTime.setText(R.string.anytime);
        if (taskModel.getDueTime().getMorning())
            previousTime.setText(R.string.morning);
        if (taskModel.getDueTime().getEvening())
            previousTime.setText(R.string.evening);
        if (taskModel.getDueTime().getAfternoon())
            previousTime.setText(R.string.afternoon);
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
                                Toast.makeText(requireContext(), jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());
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
                                Toast.makeText(requireContext(), jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());
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
    }
}
