package com.jobtick.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jobtick.R;
import com.jobtick.adapers.AddTagAdapter;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.Tools;
import com.jobtick.widget.SpacingItemDecoration;

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


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    AddTagAdapter adapter;
    ArrayList<String> addTagList;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.edt_add_tag)
    EditText edtAddTag;
    @BindView(R.id.img_btn_add_tag)
    ImageView imgBtnAddTag;
    private String action_bat_title;
    private String title;

    private boolean hasUpdate = false;
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;

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
        } catch (Exception e) {

        }
        init();

        toolbar_title.setText(action_bat_title);

       /* toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void updateSkillsTag() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_PROFILE + "/skills",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        hideProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Timber.e(jsonObject.toString());
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
                        } catch (JSONException e) {
                            Timber.e(String.valueOf(e));
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            String jsonError = new String(networkResponse.data);
                            // Print Error!
                            Timber.e(jsonError);
                            if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                                unauthorizedUser();
                                hideProgressDialog();
                                return;
                            }
                            try {
                                JSONObject jsonObject = new JSONObject(jsonError);

                                JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                                if (jsonObject_error.has("message")) {
                                    Toast.makeText(SkillsTagActivity.this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            showToast("Something Went Wrong", SkillsTagActivity.this);
                        }
                        Timber.e(error.toString());
                        hideProgressDialog();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                return map1;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<>();
                if (addTagList != null && addTagList.size() != 0) {
                    for (int i = 0; addTagList.size() > i; i++) {
                        map1.put(action_bat_title.toLowerCase() + "[" + i + "]", addTagList.get(i));
                    }
                } else {
                    map1.put(action_bat_title.toLowerCase() + "[]", "");
                }
                Log.e("MAP", String.valueOf(map1.size()));
                Timber.e(map1.toString());
                return map1;

            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(SkillsTagActivity.this);
        requestQueue.add(stringRequest);
        Log.e("OfferChatActivity", stringRequest.getUrl());
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

        adapter = new AddTagAdapter(addTagList, new AddTagAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String data) {
                addTagList.remove(data);
                adapter.updateItem(addTagList);
                hasUpdate = true;
                recyclerView.swapAdapter(adapter, true);
                if (addTagList.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });

        if (addTagList != null && addTagList.size() != 0) {
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    @OnClick(R.id.img_btn_add_tag)
    public void onViewClicked() {
        if (TextUtils.isEmpty(edtAddTag.getText().toString().trim())) {
            edtAddTag.setError("Text is empty");
            return;
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
