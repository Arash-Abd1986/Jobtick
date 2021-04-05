package com.jobtick.android.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.jobtick.android.R;
import com.jobtick.android.activities.ActivityBase;
import com.jobtick.android.activities.CategoryListActivity;
import com.jobtick.android.activities.DashboardActivity;
import com.jobtick.android.activities.NotificationActivity;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.ConstantKey;
import com.jobtick.android.utils.Navigator;
import com.jobtick.android.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class NewTaskFragment extends Fragment {

    @SuppressLint("NonConstantResourceId")
    SessionManager sessionManager;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.ticker_card)
    CardView tickerCard;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.poster_card)
    CardView posterCard;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.name)
    TextView name;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.update_profile)
    TextView updateProfile;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.scrollView)
    NestedScrollView scrollView;

    View root;
    private Toolbar toolbar;

    private ImageView ivNotification;
    private Navigator navigator;

    public NewTaskFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_new_task_rev2, container, false);
        ButterKnife.bind(this, root);

        posterCard.setOnClickListener(v -> {
            startCategoryList();
        });

        tickerCard.setOnClickListener(v -> {
            ((DashboardActivity) requireActivity()).goToFragment(DashboardActivity.Fragment.EXPLORE);
        });

        sessionManager = new SessionManager(getContext());
        name.setText(sessionManager.getUserAccount().getName());
        //lytBtnPost.bringToFront();
        initToolbar();
        return root;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        navigator = (DashboardActivity) context;
        ;
        super.onAttach(context);

    }

    private void startCategoryList() {
        DashboardActivity dashboardActivity = (DashboardActivity) requireActivity();
        dashboardActivity.findViewById(R.id.small_plus).performClick();
    }

    private void initToolbar() {
        DashboardActivity dashboardActivity = (DashboardActivity) requireActivity();
        if (dashboardActivity == null) return;
        toolbar = dashboardActivity.findViewById(R.id.toolbar);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_new_task);
        toolbar.getMenu().findItem(R.id.action_search).setVisible(false);

        TextView toolbar_title = dashboardActivity.findViewById(R.id.toolbar_title);
        toolbar_title.setVisibility(View.VISIBLE);
        toolbar_title.setText(R.string.jobTick);
        toolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.backgroundLightGrey));
        androidx.appcompat.widget.Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        toolbar_title.setLayoutParams(params);

        ivNotification = dashboardActivity.findViewById(R.id.ivNotification);
        ivNotification.setVisibility(View.VISIBLE);
        updateProfile.setOnClickListener(v ->
        {
            navigator.navigate(R.id.navigation_profile);
        });
        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), NotificationActivity.class);
            startActivityForResult(intent, ConstantKey.RESULTCODE_NOTIFICATION_READ);
        });

        getNotificationList();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setOnMenuItemClickListener(item -> false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == ConstantKey.RESULTCODE_NOTIFICATION_READ) {
            getNotificationList();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void getNotificationList() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_NOTIFICATION_UNREAD,
                response -> {
                    Timber.e(response);

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                            jsonObject = jsonObject.getJSONObject("data");
                            if (jsonObject.has("unread_count") && !jsonObject.isNull("unread_count")) {
                                String countStr = jsonObject.getString("unread_count");
                                if (Integer.parseInt(countStr) > 0) {
                                    ivNotification.setImageResource(R.drawable.ic_notification_unread_24_28dp);
                                } else {
                                    ivNotification.setImageResource(R.drawable.ic_notification_bel_24_28dp);
                                }
                            }
                        } else {
                            ((ActivityBase) requireActivity()).showToast("something went wrong.", requireContext());
                            ivNotification.setImageResource(R.drawable.ic_notification_bel_24_28dp);
                        }

                    } catch (JSONException e) {
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                        ivNotification.setImageResource(R.drawable.ic_notification_bel_24_28dp);
                    }
                },
                error -> {
                    ivNotification.setImageResource(R.drawable.ic_notification_bel_24_28dp);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<>();
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("Authorization", "Bearer " + sessionManager.getAccessToken());
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
        Timber.e(stringRequest.getUrl());

    }
}
