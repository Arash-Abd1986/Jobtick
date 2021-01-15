package com.jobtick.android.cancellations;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.jobtick.android.R;

public abstract class AbstractDoneMessageActivity extends AppCompatActivity {

    MaterialToolbar toolbar;
    TextView txtTitle;
    MaterialButton done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done_message);

        txtTitle = findViewById(R.id.txt_title);
        toolbar = findViewById(R.id.toolbar);
        done = findViewById(R.id.done);

        txtTitle.setText(getTextTitle());
        initToolbar();
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getToolbarTitle());

        done.setOnClickListener(v -> {
            finish();
        });
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    abstract String getToolbarTitle();
    abstract String getTextTitle();
}