package com.jobtick.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jobtick.R;
import com.jobtick.activities.EditProfileActivity;
import com.jobtick.models.AttachmentModel;
import com.jobtick.models.UserAccountModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ImageUtil;
import com.jobtick.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class CalenderReqFragment extends Fragment {

    private ArrayList<AttachmentModel> attachmentArrayList;
    private UserAccountModel userAccountModel;
    TextView btnNext;

    private static final String TAG = EditProfileActivity.class.getName();


    int year, month, day;
    String str_DOB = null;
    DatePickerDialog.OnDateSetListener mDateSetListener;
     TextView txtBirthDate;
    public CalenderReqFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static CalenderReqFragment newInstance() {
        return new CalenderReqFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnNext = view.findViewById(R.id.txt_btn_next);
        txtBirthDate= view.findViewById(R.id.txt_birth_date);
        getAllUserProfileDetails();
        btnNext.setOnClickListener(v -> {
            ((RequirementsBottomSheet) getParentFragment()).changeFragment(4);

        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                str_DOB = Tools.getDayMonthDateTimeFormat2(year + "-" + month + "-" + dayOfMonth);
                txtBirthDate.setText(str_DOB);
            }
        };

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_calender_req, container, false);
        return view;
    }
    private void getAllUserProfileDetails() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_PROFILE + "/" + sessionManager.getUserAccount().getId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        hideProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Timber.e(jsonObject.toString());


                            if (jsonObject.has("data") && !jsonObject.isNull("data")) {

                                userAccountModel = new UserAccountModel().getJsonToModel(jsonObject.getJSONObject("data"));
                                /*
                                 * Add Button for empty attachment
                                 * */
                                setUpAllEditFields(userAccountModel);
                                attachmentArrayList = userAccountModel.getPortfolio();
                                attachmentArrayList.add(new AttachmentModel());
                                Log.e(TAG, attachmentArrayList.size() + "");
                                if (attachmentArrayList.size() != 0) {
                                    //recyclerView.setVisibility(View.VISIBLE);
                                  //  adapter.addItems(attachmentArrayList);
                                }
                            } else {
                                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();

                            }

                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "JSONException", Toast.LENGTH_SHORT).show();

                            Timber.e(String.valueOf(e));
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorHandle1(error.networkResponse);
                        hideProgressDialog();
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                // map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
        Log.e(TAG, stringRequest.getUrl());
    }
    private void setUpAllEditFields(UserAccountModel userAccountModel)
    {

        txtBirthDate.setText(Tools.getDayMonthDateTimeFormat2(userAccountModel.getDob()));


    }
}