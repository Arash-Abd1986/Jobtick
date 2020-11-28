package com.jobtick.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.R;
import com.jobtick.models.TaskModel;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.ImageUtil;
import com.jobtick.utils.SessionManager;
import com.jobtick.utils.TimeHelper;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JobReceiptActivity extends ActivityBase {

    private static final String TAG = JobReceiptActivity.class.getName();

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    @BindView(R.id.img_avatar)
    CircularImageView imgAvatar;
    @BindView(R.id.img_verified_account)
    ImageView imgVerifiedAccount;
    @BindView(R.id.txt_location)
    TextView txtLocation;
    @BindView(R.id.txt_full_name)
    TextView txtFullName;


    @BindView(R.id.job_title)
    TextView jobTitle;
    @BindView(R.id.txt_amount)
    TextView txtAmount;
    @BindView(R.id.receipt_number)
    TextView receiptNumber;

    @BindView(R.id.job_cast_value)
    TextView jobCostValue;
    @BindView(R.id.service_fee_value)
    TextView serviceFee;
    @BindView(R.id.total_cost_value)
    TextView totalCost;

    @BindView(R.id.paid)
    TextView paidOn;
    @BindView(R.id.payment_number)
    TextView paymentNumber;
    @BindView(R.id.card_logo)
    ImageView cardLogo;

    @BindView(R.id.abn_number)
    TextView abnNumber;

    @BindView(R.id.job_tick_service_fee_value)
    TextView jobTickServiceValue;
    @BindView(R.id.job_tick_gts_value)
    TextView jobTickGtsValue;
    @BindView(R.id.job_tick_total_value)
    TextView jobTickTotalValue;

    private SessionManager sessionManager;
    private TaskModel taskModel;
    private Boolean isMyTask = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_receipt);
        ButterKnife.bind(this);

        sessionManager = new SessionManager(JobReceiptActivity.this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isMyTask = bundle.getBoolean(ConstantKey.IS_MY_TASK);
            taskModel = TaskDetailsActivity.taskModel;
          //  taskModel = bundle.getParcelable(ConstantKey.TASK);
        }

        initToolbar();
        init();
        setData();
    }

    private void init(){
        if(isMyTask){

        }else{
            cardLogo.setVisibility(View.GONE);
            paidOn.setVisibility(View.GONE);
            paymentNumber.setVisibility(View.GONE);
        }
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Job receipt");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setData() {

        jobTitle.setText(taskModel.getTitle());
        txtAmount.setText(String.format(Locale.ENGLISH, "$ %d", taskModel.getAmount()));
        jobCostValue.setText(String.format(Locale.ENGLISH, "$ %d", taskModel.getAmount()));

        if (!isMyTask) {
            //worker
            if (taskModel.getPoster().getLocation() != null) {
                txtLocation.setText(taskModel.getPoster().getLocation());
            } else {
                txtLocation.setText(R.string.in_person);
            }
            if (taskModel.getPoster().getAvatar() != null && taskModel.getPoster().getAvatar().getThumbUrl() != null) {
                ImageUtil.displayImage(imgAvatar, taskModel.getPoster().getAvatar().getThumbUrl(), null);
            } else {
                //deafult image
            }
            if (taskModel.getPoster().getIsVerifiedAccount() == 1) {
                imgVerifiedAccount.setVisibility(View.VISIBLE);
            } else {
                imgVerifiedAccount.setVisibility(View.GONE);
            }

            txtFullName.setText(taskModel.getPoster().getName());

             //calcualation
//            float totalServiceFee = taskModel.getAmount() * 0.1f;
//            float gst = totalServiceFee * 1.1f;
//            float total = taskModel.getAmount() + totalServiceFee;
//            float earning = total
//            float jTServiceFee = totalServiceFee - gst;
//
//            serviceFee.setText(String.format(Locale.ENGLISH,"%.2f",totalServiceFee));
//            totalCost.setText(String.format(Locale.ENGLISH,"%.2f",total));
//            jobTickTotalValue.setText(String.format(Locale.ENGLISH,"%.2f",total));
//            jobTickGtsValue.setText(String.format(Locale.ENGLISH,"%.2f",gst));
//            jobTickServiceValue.setText(String.format(Locale.ENGLISH,"%.2f",jTServiceFee));

        } else {
            //poster
            if (taskModel.getWorker().getLocation() != null) {
                txtLocation.setText(taskModel.getWorker().getLocation());
            } else {
                txtLocation.setText(R.string.in_person);
            }
            if (taskModel.getWorker().getAvatar() != null && taskModel.getWorker().getAvatar().getThumbUrl() != null) {
                ImageUtil.displayImage(imgAvatar, taskModel.getWorker().getAvatar().getThumbUrl(), null);
            } else {
                //deafult image
            }
            if (taskModel.getWorker().getIsVerifiedAccount() == 1) {
                imgVerifiedAccount.setVisibility(View.VISIBLE);
            } else {
                imgVerifiedAccount.setVisibility(View.GONE);
            }
            txtFullName.setText(taskModel.getWorker().getName());

            if(taskModel.getConversation().getTask() != null && taskModel.getConversation().getTask().getClosedAt() != null){
            //    paymentNumber.setText();
                paidOn.setText(String.format(Locale.ENGLISH, "Paid On %s", TimeHelper.convertToShowTimeFormat(taskModel.getConversation().getTask().getClosedAt())));
            }

            //calcualation
            float totalServiceFee = taskModel.getAmount() * 0.1f;
            float total = taskModel.getAmount() + totalServiceFee;
            float gst = totalServiceFee - (totalServiceFee / 1.1f);
            float jTServiceFee = totalServiceFee - gst;

            serviceFee.setText(String.format(Locale.ENGLISH,"%.2f",totalServiceFee));
            totalCost.setText(String.format(Locale.ENGLISH,"%.2f",total));
            jobTickTotalValue.setText(String.format(Locale.ENGLISH,"%.2f",total));
            jobTickGtsValue.setText(String.format(Locale.ENGLISH,"%.2f",gst));
            jobTickServiceValue.setText(String.format(Locale.ENGLISH,"%.2f",jTServiceFee));

        }
    }
}