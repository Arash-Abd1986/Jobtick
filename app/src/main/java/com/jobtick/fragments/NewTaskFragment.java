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
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.jobtick.R;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.activities.CategroyListActivity;
import com.jobtick.activities.DashboardActivity;
import com.jobtick.activities.SearchCategoryActivity;
import com.jobtick.adapers.TaskCategoryAdapter;
import com.jobtick.models.TaskModel;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewTaskFragment extends Fragment {

    @BindView(R.id.edt_search_categories)
    TextViewRegular edtSearchCategories;
    @BindView(R.id.lty_btn_post)
    MaterialButton lytBtnPost;
    @BindView(R.id.txt_btn_category)
    TextViewRegular txtBtnCategory;

    private TaskCategoryAdapter adapter;
    private DashboardActivity dashboardActivity;
    private TaskModel taskModel;
    private Toolbar toolbar;

    //TODO: add this for implementing block user.
//    @BindView(R.id.card_cancel_background)
//    CardView cardCancelBackground;
//    @BindView(R.id.txtBlocked)
//    TextViewRegular txtBlocked;
//
//    @BindView(R.id.card_cancelled)
//    CardView cardCancelled;

    ImageView ivNotification;
    TextView toolbar_title;
    SessionManager sessionManager;

    @BindView(R.id.lyt_search_category)
    LinearLayout ltySearchCategory;

    public NewTaskFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_task, container, false);
        ButterKnife.bind(this, view);
        dashboardActivity = (DashboardActivity) getActivity();
        if (dashboardActivity != null) {
            toolbar = dashboardActivity.findViewById(R.id.toolbar);
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_new_task);
            toolbar.getMenu().findItem(R.id.action_search).setVisible(false);
            ivNotification = dashboardActivity.findViewById(R.id.ivNotification);
            ivNotification.setVisibility(View.VISIBLE);
            toolbar_title = dashboardActivity.findViewById(R.id.toolbar_title);
            toolbar_title.setVisibility(View.VISIBLE);

            toolbar.post(() -> {
                Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, null);
                toolbar.setNavigationIcon(d);
            });

            //linearCustomeHeader = dashboardActivity.findViewById(R.id.linearCustomeHeader);
            //  linearCustomeHeader.setVisibility(View.VISIBLE);
            //  customToolbar = dashboardActivity.findViewById(R.id.customToolbar);
            //  customToolbar.setVisibility(View.VISIBLE);


            toolbar_title.setText("JobTick");
            //  toolbar_title.setGravity(Gravity.RIGHT)
            //  toolbar_title.setGravity(Gravity.LEFT);

            toolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background));
            toolbar_title.setTypeface(ResourcesCompat.getFont(getContext(), R.font.poppins_bold));

            androidx.appcompat.widget.Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);

            params.gravity = Gravity.CENTER;
            toolbar_title.setLayoutParams(params);
           // Typeface font = Typeface.createFromAsset(getContext().getAssets(), "font/poppins_bold.otf");
          //  toolbar_title.setTypeface(font);
        }


        taskModel = new TaskModel();
        sessionManager = new SessionManager(getContext());


        lytBtnPost.setOnClickListener(v -> {
            Intent creating_task = new Intent(getActivity(), CategroyListActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("category", "");
            creating_task.putExtras(bundle);
          /*
            //taskModel.setCategory(1);
            bundle.putParcelable(ConstantKey.TASK, taskModel);
            creating_task.putExtras(bundle);
            startActivityForResult(creating_task, 12);*/
            getContext().startActivity(creating_task);
        });
     //   init();


        edtSearchCategories.setOnClickListener(v -> {
            Intent creating_task = new Intent(getActivity(), SearchCategoryActivity.class);
            startActivity(creating_task);
        });

        ltySearchCategory.setOnClickListener(v -> {
            Intent creating_task = new Intent(getActivity(), SearchCategoryActivity.class);
            startActivity(creating_task);
        });
        return view;
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

    //TODO: add this view for blocked user. I (Mohsen) comment views in the layout.
//    public void init() {
//        UserAccountModel userAccountModel = sessionManager.getUserAccount();
//        if (userAccountModel.getBlocked()) {
//            cardCancelBackground.setVisibility(View.VISIBLE);
//            cardCancelled.setVisibility(View.VISIBLE);
//            txtBlocked.setText("Your account has been blocked for " + compareTwoDate(sessionManager.getUserAccount().getBlockedUntil()) + " days.");
//        }
//
//    }
}
