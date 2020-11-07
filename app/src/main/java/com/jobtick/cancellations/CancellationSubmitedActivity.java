package com.jobtick.cancellations;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CancellationSubmitedActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.lyt_btn_finish)
    LinearLayout lytBtnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancellation_submited);
        ButterKnife.bind(this);
        initToolbar();

        txtTitle.setText(getIntent().getExtras().getString(CANCELLATION));

        lytBtnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_cancel);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cancellation");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public static final String CANCELLATION = "cancellation";
}