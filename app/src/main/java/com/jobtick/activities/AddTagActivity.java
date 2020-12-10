package com.jobtick.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.jobtick.edit_text.EditTextRegular;
import com.jobtick.R;
import com.jobtick.TextView.TextViewSemiBold;
import com.jobtick.adapers.AddTagAdapter;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.Tools;
import com.jobtick.widget.SpacingItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddTagActivity extends ActivityBase {

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    AddTagAdapter adapter;
    ArrayList<String> addTagList;
    @BindView(R.id.txt_title)
    TextViewSemiBold txtTitle;
    @BindView(R.id.edt_add_tag)
    EditTextRegular edtAddTag;
    @BindView(R.id.img_btn_add_tag)
    ImageView imgBtnAddTag;
    String action_bar_title;
    String title;
    private int tag_array_size = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tag);
        ButterKnife.bind(this);
        addTagList = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getStringArrayList(ConstantKey.TAG) != null) {
                addTagList = bundle.getStringArrayList(ConstantKey.TAG);
            }
            if (bundle.getString(ConstantKey.ACTIONBAR_TITLE) != null) {
                action_bar_title = bundle.getString(ConstantKey.ACTIONBAR_TITLE);
            }
            if (bundle.getString(ConstantKey.TITLE) != null) {
                title = bundle.getString(ConstantKey.TITLE);
            }
            if (bundle.getInt(ConstantKey.TAG_SIZE) != 0) {
                tag_array_size = bundle.getInt(ConstantKey.TAG_SIZE);
            }
        }
        init();
        initToolbar();
    }

    private void initToolbar() {
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.setTitle(action_bar_title);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(ConstantKey.TAG, addTagList);
        setResult(25, intent);
        super.onBackPressed();

    }


    private void init() {
        txtTitle.setText(title);
        recyclerView.setLayoutManager(new LinearLayoutManager(AddTagActivity.this));
        recyclerView.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(AddTagActivity.this, 5), true));
        recyclerView.setHasFixedSize(true);

        adapter = new AddTagAdapter(addTagList, data -> {
            addTagList.remove(data);
            adapter.updateItem(addTagList);
            recyclerView.swapAdapter(adapter, true);
        });
        recyclerView.setAdapter(adapter);
    }


    @OnClick(R.id.img_btn_add_tag)
    public void onViewClicked() {
        if (TextUtils.isEmpty(edtAddTag.getText().toString().trim())) {
            edtAddTag.setError("Text is empty");
            return;
        } else {
            if (tag_array_size > addTagList.size()) {
                if (recyclerView.getVisibility() != View.VISIBLE) {
                    recyclerView.setVisibility(View.VISIBLE);
                }
                addTagList.add(edtAddTag.getText().toString().trim());
                adapter.notifyItemInserted(adapter.getItemCount());
                edtAddTag.setText(null);
            } else {
                showToast("Max. 3 Tag you can add", AddTagActivity.this);
            }
        }
    }
}
