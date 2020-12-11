package com.jobtick.cancellations;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import com.jobtick.R;
import com.jobtick.activities.TTCancellationSummaryActivity;
import com.jobtick.models.cancellation.reason.CancellationReasonModel;
import com.jobtick.models.cancellation.reason.Worker;
import com.jobtick.utils.ConstantKey;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

public class CancellationWorkerActivity extends AbstractCancellationReasonsActivity
        implements RadioGroup.OnCheckedChangeListener{


    @BindView(R.id.rb_reason_1)
    RadioButton rbReason1;
    @BindView(R.id.rb_reason_2)
    RadioButton rbReason2;
    @BindView(R.id.rb_reason_3)
    RadioButton rbReason3;
    @BindView(R.id.rb_reason_4)
    RadioButton rbReason4;


    @BindView(R.id.btn_submit)
    MaterialButton btnSubmit;

    @BindView(R.id.cancellation_fee_container)
    RelativeLayout cancellationFeeContainer;

    @BindView(R.id.txt_cancellation_fee)
    TextView cancellationFee;

    @BindView(R.id.rg_reason_message)
    RadioGroup rgReasonMessage;

    private int reasonId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rgReasonMessage.setOnCheckedChangeListener(this);
    }

    @Override
    public void setReasons(CancellationReasonModel cancellationReasonModel) {

        List<Worker> reasons = cancellationReasonModel.getWorker();

        for (int i = 0; reasons.size() > i; i++) {
            String reason = reasons.get(i).getReason();
            if(reason.contains("{poster}"))
                reason = reason.replace("{poster}", "<b>" + taskModel.getPoster().getName() + "</b>");
            if(reason.contains("{worker}"))
                reason = reason.replace("{worker}", "<b>" + taskModel.getWorker().getName() + "</b>");

            switch (i) {
                case 0:
                    rbReason1.setText(Html.fromHtml(reason));
                    rbReason1.setTag(reasons.get(i));
                    break;
                case 1:
                    rbReason2.setText(Html.fromHtml(reason));
                    rbReason2.setTag(reasons.get(i));
                    break;
                case 2:
                    rbReason3.setText(Html.fromHtml(reason));
                    rbReason3.setTag(reasons.get(i));
                    break;
                case 3:
                    rbReason4.setText(Html.fromHtml(reason));
                    rbReason4.setTag(reasons.get(i));
                    break;
                case 4:
                    throw new IllegalStateException("cancellation reason worker list is more than expected.");
            }
        }
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int id) {
        RadioButton radioButton = (RadioButton) group.findViewById(id);
        Worker selectedReason = ((Worker)radioButton.getTag());
        if (radioButton.isChecked()) {
            reason = radioButton.getText().toString().trim();
            if(selectedReason.getResponsiblePersonType().equals("worker")){
                cancellationFee.setText(String.format(Locale.ENGLISH, "-$%s", cancellationFeeValue));
                cancellationFeeContainer.setVisibility(View.VISIBLE);
            }else{
                cancellationFeeContainer.setVisibility(View.GONE);
            }

            btnSubmit.setEnabled(true);
            reasonId = selectedReason.getId();
        }
    }

    @OnClick(R.id.btn_submit)
    public void onViewClicked() {
        String str_comment = null;
        if (!TextUtils.isEmpty(commentText.getText().toString().trim())) {
            str_comment = commentText.getText().toString().trim();
        }

        Bundle bundle = new Bundle();
        bundle.putInt(CANCELLATION_ID, reasonId);
        bundle.putString(CANCELLATION_REASON, reason);
        bundle.putString(CANCELLATION_COMMENT, str_comment);
        if(cancellationFeeContainer.getVisibility() == View.VISIBLE)
            bundle.putFloat(CANCELLATION_VALUE, cancellationFeeValue);
        bundle.putString(ConstantKey.CANCELLATION_TITLE, generateTitle());

        //we sure this is ticker and ticker is cancelling, so we go to TT
        Intent intent = new Intent(this, TTCancellationSummaryActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, ConstantKey.RESULTCODE_CANCELLATION);
    }
}