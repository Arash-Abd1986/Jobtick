package com.jobtick.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jobtick.R;
import com.jobtick.activities.CategoryListActivity;
import com.jobtick.activities.DashboardActivity;
import com.jobtick.adapers.TaskCategoryAdapter;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewTaskFragment extends Fragment {

    @SuppressLint("NonConstantResourceId")
    SessionManager sessionManager;

    @BindView(R.id.ticker_card)
    CardView tickerCard;
    @BindView(R.id.poster_card)
    CardView posterCard;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.lty_btn_post)
    FloatingActionButton lytBtnPost;

    @BindView(R.id.scrollView)
    NestedScrollView scrollView;

    @BindView(R.id.dynamic_space)
    LinearLayout dynamicSpace;

    @BindView(R.id.button_container)
    View buttonContainer;

    View root;
    TextView toolbar_title;

    private TaskCategoryAdapter adapter;
    private Toolbar toolbar;

    public NewTaskFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_new_task_rev2, container, false);
//        View root = inflater.inflate(R.layout.fragment_new_task, container, false);
        ButterKnife.bind(this, root);
        initToolbar();

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
        sessionManager = new SessionManager(getContext());
        name.setText(sessionManager.getUserAccount().getName());
        final ViewTreeObserver observer = scrollView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                calculateSpaceHeight();
                scrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        return root;
    }

    private void initToolbar() {
        DashboardActivity dashboardActivity = (DashboardActivity) getActivity();
        if (dashboardActivity == null) return;
        toolbar = dashboardActivity.findViewById(R.id.toolbar);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_new_task);
        toolbar.getMenu().findItem(R.id.action_search).setVisible(false);
        ImageView ivNotification = dashboardActivity.findViewById(R.id.ivNotification);
        ivNotification.setVisibility(View.VISIBLE);
        TextView toolbar_title = dashboardActivity.findViewById(R.id.toolbar_title);
        toolbar_title.setVisibility(View.VISIBLE);
        toolbar_title.setText(R.string.jobTick);
        toolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.backgroundLightGrey));
        toolbar_title.setTypeface(ResourcesCompat.getFont(getContext(), R.font.poppins_bold));
        toolbar_title.setTextSize(22f);
        androidx.appcompat.widget.Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        toolbar_title.setLayoutParams(params);
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

    private void calculateSpaceHeight() {

        int scrollHeight = scrollView.getChildAt(0).getHeight();
        int totalHeight = root.getHeight();
        int bottomHeight = buttonContainer.getHeight();

        //24 for shadow and ...
        int space = (int) (((scrollHeight + bottomHeight) - totalHeight) + 24);

        if (space > 0) {
            LinearLayout.LayoutParams dynamicParams = new LinearLayout.LayoutParams(root.getWidth(), space);
            dynamicParams.setMargins(0, 0, 0, 0);
            dynamicSpace.setLayoutParams(dynamicParams);
        }
    }
}
