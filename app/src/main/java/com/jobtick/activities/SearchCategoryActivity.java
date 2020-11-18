package com.jobtick.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.jobtick.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchCategoryActivity extends ActivityBase {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_search_new)
    MaterialButton lytSearchNew;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.iv_back)
    ImageView ivBack;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_categories)
    MaterialButton lytCategories;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_search_categoreis)
    EditText edtSearchCategories;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_search);
        ButterKnife.bind(this);
      //  RelativeLayout emptySearch = findViewById(R.id.empty_search);
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.lyt_search_new, R.id.lyt_categories, R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lyt_search_new:

                edtSearchCategories.requestFocus();

                break;
            case R.id.lyt_categories:

                Intent categoryActivity = new Intent(SearchCategoryActivity.this, CategoryListActivity.class);
                startActivity(categoryActivity);
                break;
            case R.id.iv_back:
                finish();
                break;

        }

    }
}
