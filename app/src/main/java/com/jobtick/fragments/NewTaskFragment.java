package com.jobtick.fragments;

import android.annotation.SuppressLint;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jobtick.R;
import com.jobtick.activities.ActivityBase;
import com.jobtick.activities.CategoryListActivity;
import com.jobtick.activities.DashboardActivity;
import com.jobtick.activities.NotificationActivity;
import com.jobtick.utils.Constant;
import com.jobtick.utils.SessionManager;

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
    @BindView(R.id.lty_btn_post)
    FloatingActionButton lytBtnPost;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.scrollView)
    NestedScrollView scrollView;

    View root;
    private Toolbar toolbar;

    private ImageView ivNotification;

    public NewTaskFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_new_task_rev2, container, false);
        ButterKnife.bind(this, root);

        lytBtnPost.setOnClickListener(v -> {
            Intent creating_task = new Intent(getActivity(), CategoryListActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("category", "");
            creating_task.putExtras(bundle);
            getContext().startActivity(creating_task);
        });

        posterCard.setOnClickListener(v -> {
            lytBtnPost.performClick();
        });
        tickerCard.setOnClickListener(v -> {
            ((DashboardActivity) requireActivity()).goToFragment(DashboardActivity.Fragment.EXPLORE);
        });

        sessionManager = new SessionManager(getContext());
        name.setText(sessionManager.getUserAccount().getName());

        initToolbar();
        return root;
    }

    private void initToolbar() {
        DashboardActivity dashboardActivity = (DashboardActivity) getActivity();
        if (dashboardActivity == null) return;
        toolbar = dashboardActivity.findViewById(R.id.toolbar);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_new_task);
        toolbar.getMenu().findItem(R.id.action_search).setVisible(false);

        TextView toolbar_title = dashboardActivity.findViewById(R.id.toolbar_title);
        toolbar_title.setVisibility(View.VISIBLE);
        toolbar_title.setText(R.string.jobTick);
        toolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.backgroundLightGrey));
        toolbar_title.setTypeface(ResourcesCompat.getFont(getContext(), R.font.poppins_bold));
        toolbar_title.setTextSize(22f);
        androidx.appcompat.widget.Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        toolbar_title.setLayoutParams(params);

        ivNotification = dashboardActivity.findViewById(R.id.ivNotification);
        ivNotification.setVisibility(View.VISIBLE);
        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), NotificationActivity.class);
            startActivity(intent);
        });

        getNotificationList();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setOnMenuItemClickListener(item -> false);
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
