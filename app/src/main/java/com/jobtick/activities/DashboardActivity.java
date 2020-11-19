package com.jobtick.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.jobtick.R;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.fragments.CashOutBottomSheet;
import com.jobtick.fragments.LogOutBottomSheet;
import com.jobtick.interfaces.onProfileUpdateListener;
import com.jobtick.models.PushNotificationModel;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.ImageUtil;
import com.jobtick.utils.SessionManager;
import com.onesignal.OneSignal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import static com.jobtick.utils.ConstantKey.PUSH_COMMENT;
import static com.jobtick.utils.ConstantKey.PUSH_CONVERSATION;
import static com.jobtick.utils.ConstantKey.PUSH_CONVERSATION_ID;
import static com.jobtick.utils.ConstantKey.PUSH_NOTIFICATION_MODEL;
import static com.jobtick.utils.ConstantKey.PUSH_TASK;

public class DashboardActivity extends ActivityBase implements NavigationView.OnNavigationItemSelectedListener, onProfileUpdateListener {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    AppBarConfiguration appBarConfiguration;
    SessionManager sessionManager;
    ImageView imgUserAvatar;
    ImageView imgVerifiedAccount;
    TextView txtUserName;
    TextView txtAccountLevel;
    CardView btnCashOut;
    public static onProfileUpdateListener onProfileupdatelistenerSideMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        toolbar = findViewById(R.id.toolbar);
        // Tools.clearSystemBarLight(this);
        toolbar.setElevation(0);
        sessionManager = new SessionManager(this);
        onProfileupdatelistenerSideMenu = this;

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);

        imgUserAvatar = headerView.findViewById(R.id.img_user_avatar);
        imgVerifiedAccount = headerView.findViewById(R.id.img_verified_account);
        txtUserName = headerView.findViewById(R.id.txt_user_name);
        txtAccountLevel = headerView.findViewById(R.id.txt_account_level);
        btnCashOut = headerView.findViewById(R.id.btn_cashout);

        setHeaderLayout();
        navigationView.setNavigationItemSelectedListener(this);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        btnCashOut.setOnClickListener( v -> {
            drawerLayout.close();
            CashOutBottomSheet cashOutBottomSheet = CashOutBottomSheet.newInstance();
            cashOutBottomSheet.show(getSupportFragmentManager(), "");
        });



       /* OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                Log.d("debug", "User:" + userId);
                if (registrationId != null)


                    Log.d("debug", "registrationId:" + registrationId);

            }
        });*/


        drawerLayout = findViewById(R.id.drawer_layout);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_new_task, R.id.navigation_my_tasks, R.id.navigation_browse
                , R.id.navigation_inbox, R.id.navigation_profile)
                .setDrawerLayout(drawerLayout)
                .build();


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
        //    NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                updateCounter(navigationView);
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getParcelable(PUSH_NOTIFICATION_MODEL) != null) {
                PushNotificationModel pushNotificationModel = bundle.getParcelable(PUSH_NOTIFICATION_MODEL);
                if (pushNotificationModel != null) {
                    if (pushNotificationModel.getTrigger() != null) {
                        Intent intent = new Intent(DashboardActivity.this, TaskDetailsActivity.class);
                        Bundle bundleintent = new Bundle();
                        if (pushNotificationModel.getTrigger().equals(PUSH_TASK)) {
                            bundleintent.putString(ConstantKey.SLUG, pushNotificationModel.getModel_slug());
                            intent.putExtras(bundleintent);
                            startActivity(intent);
                        }
                        if (pushNotificationModel.getTrigger().equals(PUSH_COMMENT)) {

                            if (pushNotificationModel.getOffer_id() != 0) {
                                bundleintent.putString(ConstantKey.SLUG, pushNotificationModel.getModel_slug());
                                bundleintent.putInt(ConstantKey.PUSH_OFFER_ID, pushNotificationModel.getOffer_id());
                                intent.putExtras(bundleintent);
                                startActivity(intent);
                            }
                            if (pushNotificationModel.getQuestion_id() != 0) {
                                bundleintent.putString(ConstantKey.SLUG, pushNotificationModel.getModel_slug());
                                bundleintent.putInt(ConstantKey.PUSH_QUESTION_ID, pushNotificationModel.getQuestion_id());
                                intent.putExtras(bundleintent);
                                startActivity(intent);
                            }


                        }
                        if (pushNotificationModel.getTrigger().equals(PUSH_CONVERSATION)) {

                            Bundle bundle1 = new Bundle();
                            bundle1.putInt(PUSH_CONVERSATION_ID, pushNotificationModel.getConversation_id());
                            NavGraph graph = navController.getGraph();
                            graph.setStartDestination(R.id.navigation_inbox);
                            navController.setGraph(graph, bundle1);


                        }

                    }
                }
            }
        }


    }

    private void setHeaderLayout() {

        if (sessionManager.getUserAccount().getAvatar() != null && sessionManager.getUserAccount().getAvatar().getThumbUrl() != null) {
            ImageUtil.displayImage(imgUserAvatar, sessionManager.getUserAccount().getAvatar().getThumbUrl(), null);
        } else {
            //default image
        }

        OneSignal.setExternalUserId(String.valueOf(sessionManager.getUserAccount().getId()), (OneSignal.OSExternalUserIdUpdateCompletionHandler) results -> {

        });

        if (sessionManager.getUserAccount().getIsVerifiedAccount() == 1) {
            imgVerifiedAccount.setVisibility(View.VISIBLE);
        } else {
            imgVerifiedAccount.setVisibility(View.GONE);
        }

        if (sessionManager.getUserAccount().getPosterTier() != null) {
            if (sessionManager.getUserAccount().getPosterTier().getId() == 1) {
                txtAccountLevel.setText("Level 1");
            } else if (sessionManager.getUserAccount().getPosterTier().getId() == 2) {
                txtAccountLevel.setText("Level 2");
            } else if (sessionManager.getUserAccount().getPosterTier().getId() == 3) {
                txtAccountLevel.setText("Level 3");
            }
        } else {
            txtAccountLevel.setText("Level 0");
        }

        if (sessionManager.getUserAccount().getName() != null) {
            txtUserName.setText(sessionManager.getUserAccount().getName());
        } else {
            txtUserName.setText("Profile not updated");
        }
    }

    private void updateCounter(NavigationView navigationView) {
        Menu m = navigationView.getMenu();
        MenuItem menuItem = m.findItem(R.id.nav_dashboard);
        LinearLayout linearLayout = (LinearLayout) menuItem.getActionView();
        TextViewRegular textViewRegular = (TextViewRegular) linearLayout.findViewById(R.id.txt_bedge);
        textViewRegular.setVisibility(View.VISIBLE);

    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_profile:
                startActivity(new Intent(DashboardActivity.this, EditProfileActivity.class));
                return true;
            case R.id.action_logout:
                showLogoutBottomSheet();
                return true;
            case R.id.action_rate_us:
                String rating_link = "https://play.google.com/store/apps/details?id=" + getPackageName();
                Intent rate_us = new Intent(Intent.ACTION_VIEW);
                rate_us.setData(Uri.parse(rating_link));
                startActivity(rate_us);
                return true;
            case R.id.action_share:
                referAFriend();
                return true;
            case R.id.action_privacy_policy:
                String privacy_policy_link = "https://sites.google.com/view/_/home";
                Intent privacy_policy = new Intent(Intent.ACTION_VIEW);
                privacy_policy.setData(Uri.parse(privacy_policy_link));
                startActivity(privacy_policy);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showLogoutBottomSheet() {
        drawerLayout.close();
        LogOutBottomSheet logOutBottomSheet = LogOutBottomSheet.newInstance();
        logOutBottomSheet.show(getSupportFragmentManager(), "");
    }

    private void referAFriend(){
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "\n\nhttps://play.google.com/store/apps/details?id=" + getPackageName() +
                            " Sponsor code : " + sessionManager.getUserAccount().getFname() +
                            "\n\nThankYou\n" +
                            "Team " + getResources().getString(R.string.app_name));
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (Exception e) {

        }
    }


    private void logout_dialog_box() {

        new MaterialAlertDialogBuilder(DashboardActivity.this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        sessionManager.setUserAccount(null);
                        sessionManager.setLogin(false);
                        sessionManager.setTokenType(null);
                        sessionManager.setAccessToken(null);
                        Intent i;
                        i = new Intent(DashboardActivity.this, AuthActivity.class);
                        openActivity(i);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();

                    }
                }).show();
    }

    private void openActivity(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.post(() -> {
            Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, null);
            toolbar.setNavigationIcon(d);
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (menuItem.getItemId()) {

            case R.id.nav_dashboard:
                Intent dashboard = new Intent(DashboardActivity.this, Dashboard2Activity.class);
                startActivity(dashboard);
                return true;

            case R.id.nav_payment:
                startActivity(new Intent(DashboardActivity.this, PaymentHistoryActivity.class));
                return true;

            case R.id.nav_saved_tasks:
                //  Toast.makeText(this, "saved tasks", Toast.LENGTH_SHORT).show();
                Intent savedTask = new Intent(DashboardActivity.this, SavedTaskActivity.class);
                startActivity(savedTask);
                return true;

            case R.id.nav_notifications:
                Intent intent = new Intent(DashboardActivity.this, NotificationActivity.class);
                startActivity(intent);
                return true;

            case R.id.nav_task_alerts:
                Intent task_alerts = new Intent(DashboardActivity.this, TaskAlertsActivity.class);
                startActivity(task_alerts);
                return true;

            case R.id.nav_refer_a_friend:
                Toast.makeText(this, "refer a friend", Toast.LENGTH_SHORT).show();
                //TODO: after implementing app in store active this function
                //referAFriend();
                return true;

            case R.id.nav_settings:
                Intent settings = new Intent(DashboardActivity.this, SettingActivity.class);
                startActivity(settings);
                return true;


            case R.id.nav_help_topics:
                Intent help_topics = new Intent(DashboardActivity.this, HelpActivity.class);
                startActivity(help_topics);
                return true;

            case R.id.nav_logout:
                showLogoutBottomSheet();
                return true;
        }
        return false;
    }


    @Override
    public void updatedSuccesfully(String path) {
        if (path != null) {
            ImageUtil.displayImage(imgUserAvatar, path, null);
        } else {
            //default image
        }
    }

    @Override
    public void updateProfile() {
    }
}
