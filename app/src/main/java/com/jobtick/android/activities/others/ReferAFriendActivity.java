package com.jobtick.android.activities.others;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jobtick.android.R;
import com.jobtick.android.activities.ActivityBase;
import com.jobtick.android.utils.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ReferAFriendActivity extends ActivityBase {
    String link = "https://www.jobtick.com/?auth=invite&refer=";
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.relCopy)
    RelativeLayout relCopy;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btnInviteFriend)
    Button btnInviteFriend;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tvLink)
    TextView tvLink;
    @SuppressLint("NonConstantResourceId")
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
        setContentView(R.layout.activity_refer_a_friend);
        ButterKnife.bind(this);
        initComponent();
    }

    SessionManager sessionManager;

    private void initComponent() {
        sessionManager = new SessionManager(this);
        link = link + sessionManager.getUserAccount().getId();
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        tvLink.setText(link);
        relCopy.setOnClickListener(v -> {
            copyLink();
        });
        btnInviteFriend.setOnClickListener(v -> {
            shareLink();
        });
        llBack.setOnClickListener(v -> {
            super.onBackPressed();
        });
    }

    private void shareLink() {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Say yes to "+capitalize(sessionManager.getUserAccount().getFname(),true) +" "+
                            capitalize(sessionManager.getUserAccount().getLname(),false)+ " VIP Invitation and receive $10 towards your first job completion. \n" +
                            "Sign-up using the link to join our professional and welcoming community and start your journey on JOBTICK. \n\n" + link);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (Exception e) {
            Timber.d(e.getMessage());
        }
    }

    public static String capitalize(String str,boolean isFname) {
        if(str == null || str.isEmpty()) {
            if(isFname)
                return "Jobtick";
            else
                return "user";
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    private ClipboardManager clipboardManager;

    private void copyLink() {
        ClipData clip = ClipData.newPlainText("link", link);
        clipboardManager.setPrimaryClip(clip);
        showSuccessToast("Link copied", this);
    }
}