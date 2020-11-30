package com.jobtick.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.jobtick.R;
import com.jobtick.fragments.ProfileFragment;
import com.jobtick.fragments.ProfileViewFragment;

public class ProfileActivity extends ActivityBase {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_porfile);


        if(getIntent().getIntExtra("id",1)==1){
            Bundle b = new Bundle();
            b.putInt("userId",getIntent().getIntExtra("id",-1));
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ProfileViewFragment profileFragment = new ProfileViewFragment();
            profileFragment.setArguments(b);
            ft.replace(R.id.profile, profileFragment);
            ft.commit();
        }
    }
}