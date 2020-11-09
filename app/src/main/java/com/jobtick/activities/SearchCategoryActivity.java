package com.jobtick.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchCategoryActivity extends ActivityBase {

    @BindView(R.id.lyt_search_new)
    MaterialButton lytSearchNew;

    @BindView(R.id.iv_back)
    ImageView ivBack;

    @BindView(R.id.lyt_categories)
    MaterialButton lytCategories;

    @BindView(R.id.edt_search_categoreis)
    EditTextRegular edtSearchCategories;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_search);
        ButterKnife.bind(this);
    }

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
