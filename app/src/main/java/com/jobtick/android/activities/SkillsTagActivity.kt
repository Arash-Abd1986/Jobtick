package com.jobtick.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jobtick.android.BuildConfig;
import com.jobtick.android.R;
import android.annotation.SuppressLint;

import com.jobtick.android.adapers.AddTagAdapter;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.ConstantKey;
import com.jobtick.android.utils.HttpStatus;
import com.jobtick.android.utils.Tools;
import com.jobtick.android.widget.SpacingItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class SkillsTagActivity extends ActivityBase {


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    AddTagAdapter adapter;
    ArrayList<String> addTagList;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_add_tag)
    EditText edtAddTag;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_btn_add_tag)
    ImageView imgBtnAddTag;
    private String action_bat_title;
    private String title;

    private boolean hasUpdate = false;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.ivBack)
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skills_tag);

        ButterKnife.bind(this);
        addTagList = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        try {
            if (bundle != null) {
                addTagList = bundle.getStringArrayList(ConstantKey.SKILLS);
                action_bat_title = bundle.getString(ConstantKey.TOOLBAR_TITLE);
                title = bundle.getString(ConstantKey.TITLE);
            }
        } catch (Exception ignored) {

        }
        init();

        toolbar_title.setText(action_bat_title);
        toolbar_title.setOnClickListener(v ->
        {
            super.onBackPressed();
        });

       /* toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/


        ivBack.setOnClickListener(v -> onBackPressed());

    }

    private void updateSkillsTag() {
        Intent intent = new Intent();
        intent.putExtra(ConstantKey.SKILLS, addTagList);
        if (action_bat_title.equalsIgnoreCase(ConstantKey.TRANSPORTATION))
            setResult(1, intent);
        if (action_bat_title.equalsIgnoreCase(ConstantKey.LANGUAGE))
            setResult(2, intent);
        if (action_bat_title.equalsIgnoreCase(ConstantKey.EDUCATION))
            setResult(3, intent);
        if (action_bat_title.equalsIgnoreCase(ConstantKey.EXPERIENCE))
            setResult(4, intent);
        if (action_bat_title.equalsIgnoreCase(ConstantKey.SPECIALITIES))
            setResult(5, intent);
        SkillsTagActivity.super.onBackPressed();
    }


    @Override
    public void onBackPressed() {
        if (hasUpdate) {
            updateSkillsTag();
        } else {
            super.onBackPressed();
        }
    }


    private void init() {
        txtTitle.setText(title);
        recyclerView.setLayoutManager(new LinearLayoutManager(SkillsTagActivity.this));
        recyclerView.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(SkillsTagActivity.this, 5), true));
        recyclerView.setHasFixedSize(true);

        adapter = new AddTagAdapter(addTagList, data -> {
            addTagList.remove(data);
            adapter.updateItem(addTagList);
            hasUpdate = true;
            recyclerView.swapAdapter(adapter, true);
            if (addTagList.size() == 0) {
                recyclerView.setVisibility(View.GONE);
            }
        });

        if (addTagList != null && addTagList.size() != 0) {
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.img_btn_add_tag)
    public void onViewClicked() {
        if (TextUtils.isEmpty(edtAddTag.getText().toString().trim())) {
            edtAddTag.setError("Text is empty");
        } else {
            addTagList.add(edtAddTag.getText().toString().trim());
            // updateSkillsTag();
            hasUpdate = true;
            if (recyclerView.getVisibility() != View.VISIBLE) {
                recyclerView.setVisibility(View.VISIBLE);
            }
            if (addTagList.size() == 1) {
                recyclerView.setAdapter(adapter);
            }
            adapter.notifyItemInserted(adapter.getItemCount());
            edtAddTag.setText(null);

        }
    }
}
