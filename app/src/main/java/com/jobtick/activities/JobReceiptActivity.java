package com.jobtick.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.jobtick.R;
import com.jobtick.models.TaskModel;
import com.jobtick.models.receipt.Item;
import com.jobtick.models.receipt.JobReceiptModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.ImageUtil;
import com.jobtick.utils.SessionManager;
import com.jobtick.utils.TimeHelper;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.jobtick.pagination.PaginationListener.PAGE_START;

public class JobReceiptActivity extends ActivityBase {

    private static final String TAG = JobReceiptActivity.class.getName();

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_avatar)
    CircularImageView imgAvatar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_verified_account)
    ImageView imgVerifiedAccount;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_location)
    TextView txtLocation;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_full_name)
    TextView txtFullName;


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.job_title)
    TextView jobTitle;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_amount)
    TextView txtAmount;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.receipt_number)
    TextView receiptNumber;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.service_fee_title)
    TextView serviceFeeTitle;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.job_cast_value)
    TextView jobCostValue;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.service_fee_value)
    TextView serviceFee;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.total_cost_value)
    TextView totalCost;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.total_cost_title)
    TextView totalCostTitle;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.paid)
    TextView paidOn;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.payment_number)
    TextView paymentNumber;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.card_logo)
    CardView cardLogo;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.abn_number)
    TextView abnNumber;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.invoice_number)
    TextView invoiceNumber;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.job_tick_service_fee_value)
    TextView jobTickServiceValue;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.job_tick_service_fee_title)
    TextView jobTickServiceTitle;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.job_tick_gts_value)
    TextView jobTickGtsValue;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.job_tick_total_value)
    TextView jobTickTotalValue;

    private SessionManager sessionManager;
    private Boolean isMyTask = false;
    private String taskSlug;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_receipt);
        ButterKnife.bind(this);

        sessionManager = new SessionManager(JobReceiptActivity.this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isMyTask = bundle.getBoolean(ConstantKey.IS_MY_TASK);
            taskSlug = bundle.getString(ConstantKey.TASK_SLUG);
        }

        if (taskSlug == null)
            throw new IllegalStateException("need to send taskslug on bundle");
        initToolbar();
        init();
        getData(taskSlug);
    }

    private void init() {
        if (!isMyTask) {
            cardLogo.setVisibility(View.GONE);
            paidOn.setVisibility(View.GONE);
            paymentNumber.setVisibility(View.GONE);
            totalCostTitle.setText(R.string.total);
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

    private void setData(JobReceiptModel model) {

        TaskModel taskModel = model.getTask();
        txtAmount.setText(String.format(Locale.ENGLISH, "$%.0f", model.getReceipt().getReceiptAmount()));
        receiptNumber.setText(model.getReceipt().getReceiptNumber());

        jobTitle.setText(taskModel.getTitle());

        if (taskModel.getPoster().getLocation() != null) {
            txtLocation.setText(taskModel.getPoster().getLocation());
        } else {
            txtLocation.setText(R.string.in_person);
        }
        if (taskModel.getPoster().getAvatar() != null && taskModel.getPoster().getAvatar().getThumbUrl() != null) {
            Glide.with(imgAvatar).load(taskModel.getPoster().getAvatar().getThumbUrl()).into(imgAvatar);
        } else {
            //deafult image
        }
        if (taskModel.getPoster().getIsVerifiedAccount() == 1) {
            imgVerifiedAccount.setVisibility(View.VISIBLE);
        } else {
            imgVerifiedAccount.setVisibility(View.GONE);
        }

        txtFullName.setText(taskModel.getPoster().getName());

        jobCostValue.setText(String.format(Locale.ENGLISH, "$%.2f", model.getReceipt().getTaskCost()));
        serviceFee.setText(String.format(Locale.ENGLISH, "$%s", model.getReceipt().getFee()));
        totalCost.setText(String.format(Locale.ENGLISH, "$%.2f", model.getReceipt().getNetAmount()));

        invoiceNumber.setText(model.getInvoice().getInvoiceNumber());
        abnNumber.setText(String.format(Locale.ENGLISH, "ABN: %s", model.getInvoice().getAbn()));

        Item item = model.getInvoice().getItems().get(0);
        jobTickServiceTitle.setText(item.getItemName());
        jobTickServiceValue.setText(String.format(Locale.ENGLISH, "$%s", item.getAmount()));
        jobTickGtsValue.setText(String.format(Locale.ENGLISH, "$%s", item.getTaxAmount()));
        jobTickTotalValue.setText(String.format(Locale.ENGLISH, "$%s", item.getFinalAmount()));

        if(isMyTask){
            paidOn.setText(String.format(Locale.ENGLISH, "Paid On %s",
                    TimeHelper.convertToShowTimeFormat(model.getInvoice().getCreatedAt())));

            paymentNumber.setText(String.format(Locale.ENGLISH, "%s *******%s",
                    model.getReceipt().getPaymentMethod().getBrand(),
                    model.getReceipt().getPaymentMethod().getLast4()));
        }

    }


    private void getData(String taskSlug) {

        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_TASKS + "/" + taskSlug + "/invoice",
                response -> {
                    Timber.e(response);
                    hideProgressDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                            String jsonString = jsonObject.getString("data");
                            Gson gson = new Gson();
                            JobReceiptModel model = gson.fromJson(jsonString, JobReceiptModel.class);
                            setData(model);
                        } else {
                            showToast("something went wrong.", this);
                            return;
                        }

                    } catch (JSONException e) {
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                    }
                    hideProgressDialog();
                },
                error -> {
                    errorHandle1(error.networkResponse);
                    hideProgressDialog();
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
        RequestQueue requestQueue = Volley.newRequestQueue(JobReceiptActivity.this);
        requestQueue.add(stringRequest);
        Timber.e(stringRequest.getUrl());
    }
}