package com.jobtick.android.activities.others;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.jobtick.android.R;
import com.jobtick.android.activities.ActivityBase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReferAFriendActivity extends ActivityBase {
    @BindView(R.id.llBack)
    LinearLayout llBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = this.getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_a_friend_v2);
        ButterKnife.bind(this);
        initComponent();
    }


    private void initComponent() {
        llBack.setOnClickListener(v -> {
            super.onBackPressed();
        });
    }


}