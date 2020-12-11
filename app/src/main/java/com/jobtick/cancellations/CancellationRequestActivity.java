package com.jobtick.cancellations;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.text_view.TextViewBold;
import com.jobtick.text_view.TextViewRegular;
import com.jobtick.activities.ActivityBase;
import com.jobtick.activities.TaskDetailsActivity;
import com.jobtick.models.TaskModel;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.ImageUtil;
import com.jobtick.utils.SessionManager;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CancellationRequestActivity extends ActivityBase {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_username_date)
    TextViewRegular txtUsernameDate;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_user_avtar)
    CircularImageView imgUserAvtar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_post_title)
    TextViewBold txtPostTitle;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_name)
    TextViewRegular txtName;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_amount)
    TextViewBold txtAmount;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_cancellation_reason)
    TextViewBold txtCancellationReason;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_comment)
    TextViewRegular txtComment;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_comments)
    LinearLayout lytComments;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_cancellation_fee)
    TextViewBold txtCancellationFee;


    private SessionManager sessionManager;
    private TaskModel taskModel;
    private Boolean isMyTask = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancellation_request);
        ButterKnife.bind(this);

        sessionManager = new SessionManager(CancellationRequestActivity.this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isMyTask = bundle.getBoolean(ConstantKey.IS_MY_TASK);
        }
        taskModel = TaskDetailsActivity.taskModel;
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        setData();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setData() {
        if (isMyTask) {
            txtUsernameDate.setText(String.format("%s required to cancel this task on %s.", taskModel.getPoster().getName(), taskModel.getCancellation().getCreatedAt()));
            txtName.setText(taskModel.getPoster().getName());
            if (taskModel.getPoster().getAvatar() != null && taskModel.getPoster().getAvatar().getThumbUrl() != null) {
                ImageUtil.displayImage(imgUserAvtar, taskModel.getPoster().getAvatar().getThumbUrl(), null);
            } else {
                //set default image
            }
            txtPostTitle.setText(taskModel.getTitle());
            txtAmount.setText(String.format(Locale.ENGLISH, "%d", taskModel.getAmount()));
        } else {
            txtUsernameDate.setText(String.format("%s required to cancel this task on %s.", taskModel.getWorker().getName(), taskModel.getCancellation().getCreatedAt()));
            txtName.setText(taskModel.getWorker().getName());
            if (taskModel.getWorker().getAvatar() != null && taskModel.getWorker().getAvatar().getThumbUrl() != null) {
                ImageUtil.displayImage(imgUserAvtar, taskModel.getWorker().getAvatar().getThumbUrl(), null);
            } else {
                //set default image
            }
            txtPostTitle.setText(taskModel.getTitle());
            txtAmount.setText(String.format(Locale.ENGLISH, "$ %d", taskModel.getAmount()));
        }
        txtCancellationReason.setText(String.format("“%s“", taskModel.getCancellation().getReason()));
        if (taskModel.getCancellation().getComment() != null) {
            lytComments.setVisibility(View.VISIBLE);
            txtComment.setText(taskModel.getCancellation().getComment());
        } else {
            lytComments.setVisibility(View.GONE);
        }
    }


    @OnClick(R.id.img_user_avtar)
    public void onViewClicked() {
    }
}