package com.jobtick.fragments;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.text.TextUtils;
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
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jobtick.R;
import com.jobtick.activities.ActivityBase;
import com.jobtick.activities.EditProfileActivity;
import com.jobtick.activities.SkillsTagActivity;
import com.jobtick.adapers.AttachmentAdapter;
import com.jobtick.adapers.AttachmentAdapterEditProfile;
import com.jobtick.models.AttachmentModel;
import com.jobtick.models.UserAccountModel;
import com.jobtick.retrofit.ApiClient;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.Helper;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.ImageUtil;
import com.jobtick.utils.SessionManager;
import com.jobtick.utils.Tools;
import com.jobtick.widget.SpacingItemDecoration;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;

import static com.jobtick.fragments.ProfileFragment.onProfileupdatelistener;

public class CalenderReqFragment extends Fragment {

    private UserAccountModel userAccountModel;
    TextView btnNext;
    SessionManager sessionManager;
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
        sessionManager = new SessionManager(getContext());
        btnNext = view.findViewById(R.id.txt_btn_next);

        txtBirthDate= view.findViewById(R.id.txt_birth_date);
        txtBirthDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    mDateSetListener,
                    year, month, day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });
        getAllUserProfileDetails();
        btnNext.setOnClickListener(v -> {

            ((RequirementsBottomSheet) getParentFragment()).changeFragment(4);

        });

        mDateSetListener = (view1, year, month, dayOfMonth) -> {
            month = month + 1;
            str_DOB = Tools.getDayMonthDateTimeFormat2(year + "-" + month + "-" + dayOfMonth);
            txtBirthDate.setText(str_DOB);
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
                response -> {
                    if (getActivity() != null) {
                        ((ActivityBase) getActivity()).hideProgressDialog();
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());

                        if (jsonObject.has("data") && !jsonObject.isNull("data")) {

                            userAccountModel = new UserAccountModel().getJsonToModel(jsonObject.getJSONObject("data"));
                            setUpDate(userAccountModel);
                        } else {
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        Toast.makeText(getActivity(), "JSONException", Toast.LENGTH_SHORT).show();
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                    }
                },
                error -> {
                    if (getActivity() != null) {
                        ((ActivityBase) getActivity()).errorHandle1(error.networkResponse);
                        ((ActivityBase) getActivity()).hideProgressDialog();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                // map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }
        };

        if (getActivity() != null) {
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(stringRequest);
        }
    }





    private void setUpDate(UserAccountModel userAccountModel) {
        if (userAccountModel.getDob() != null && !userAccountModel.getDob().equals("")) {
            ((RequirementsBottomSheet) getParentFragment()).changeFragment(2);
        }
        txtBirthDate.setText(Tools.getDayMonthDateTimeFormat2(userAccountModel.getDob()));

    }

}