package com.jobtick.fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.jobtick.R;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.activities.CategoryListActivity;
import com.jobtick.activities.DashboardActivity;
import com.jobtick.activities.SearchCategoryActivity;
import com.jobtick.adapers.TaskCategoryAdapter;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewTaskFragment extends Fragment {

    SessionManager sessionManager;

    @BindView(R.id.ticker_card)
    CardView tickerCard;
    @BindView(R.id.poster_card)
    CardView posterCard;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.lty_btn_post)
    MaterialButton lytBtnPost;

    private TaskCategoryAdapter adapter;
    private Toolbar toolbar;

    public NewTaskFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_task_rev2, container, false);
        ButterKnife.bind(this, view);
        sessionManager = new SessionManager(getContext());
        DashboardActivity dashboardActivity = (DashboardActivity) getActivity();
        if (dashboardActivity != null) {
            toolbar = dashboardActivity.findViewById(R.id.toolbar);
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_new_task);
            toolbar.getMenu().findItem(R.id.action_search).setVisible(false);
            ImageView ivNotification = dashboardActivity.findViewById(R.id.ivNotification);
            ivNotification.setVisibility(View.VISIBLE);
            TextView toolbar_title = dashboardActivity.findViewById(R.id.toolbar_title);
            toolbar_title.setVisibility(View.VISIBLE);

            toolbar.post(() -> {
                Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, null);
                toolbar.setNavigationIcon(d);
            });

            toolbar_title.setText(R.string.jobTick);

            if (getContext() != null) {
                toolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.backgroundLightGrey));
                toolbar_title.setTypeface(ResourcesCompat.getFont(getContext(), R.font.poppins_bold));

            }
            androidx.appcompat.widget.Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
        }

        lytBtnPost.setOnClickListener(v -> {
            openPostAJob();
        });

        posterCard.setOnClickListener(v -> {
            openPostAJob();
        });


        tickerCard.setOnClickListener(v -> {
            Intent creating_task = new Intent(getActivity(), SearchCategoryActivity.class);
            startActivity(creating_task);
        });

        name.setText(sessionManager.getUserAccount().getName());
        return view;
    }

    private void openPostAJob(){
        Intent creating_task = new Intent(getActivity(), CategoryListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("category", "");
        creating_task.putExtras(bundle);
        getContext().startActivity(creating_task);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setOnMenuItemClickListener(item -> false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == ConstantKey.RESULTCODE_CATEGORY && data != null) {
            boolean deSelectAllCategory = data.getBooleanExtra(ConstantKey.CATEGORY, false);
            if (deSelectAllCategory) {
                adapter.allUnselect();
                adapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
