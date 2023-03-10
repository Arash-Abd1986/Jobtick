package com.jobtick.android.cancellations;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.jobtick.android.R;
import android.annotation.SuppressLint;

import com.jobtick.android.utils.ConstantKey;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CancellationSubmittedActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_done)
    MaterialButton btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancellation_submited);
        ButterKnife.bind(this);
        initToolbar();

        Bundle bundle = getIntent().getExtras();
        if(bundle == null)
            throw new IllegalStateException("there is no bundle attached.");

        String desc = bundle.getString(ConstantKey.CANCELLATION_SUBMITTED);
        if(desc != null && !desc.equals(""))
            txtTitle.setText(desc);

        btnDone.setOnClickListener(v -> finishActivity());
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_cancel);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cancellation Request");
        toolbar.setNavigationOnClickListener(v -> finishActivity());
    }

    private void finishActivity(){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ConstantKey.CANCELLATION, true);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}