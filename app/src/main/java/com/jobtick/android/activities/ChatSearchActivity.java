package com.jobtick.android.activities;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jobtick.android.R;
import android.annotation.SuppressLint;

import com.jobtick.android.adapers.InboxListAdapter;
import com.jobtick.android.models.ConversationModel;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.ConstantKey;
import com.jobtick.android.utils.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ChatSearchActivity extends ActivityBase implements InboxListAdapter.OnItemClickListener,TextView.OnEditorActionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_search);
        ButterKnife.bind(this);


        initComponents();
    }
    @Override
    public void onItemClick(View view, ConversationModel obj, int position, String action) {
        if (action.equalsIgnoreCase("parent_layout")) {
            if (obj.getUnseenCount() != 0) {
                adapter.setUnSeenCountZero(position);
            }
            Intent intent = new Intent(this, ChatActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable(ConstantKey.CONVERSATION, obj);
            intent.putExtras(bundle);
            startActivityForResult(intent, ConstantKey.RESULTCODE_PRIVATE_CHAT);
        }
    }

    private void initComponents() {
        noMessages = findViewById(R.id.no_messages_container);
        chatList = findViewById(R.id.list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        chatList.setLayoutManager(layoutManager);
        adapter = new InboxListAdapter(this, new ArrayList<>());
        chatList.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        etSearch.setOnEditorActionListener(this);
        etSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    noMessages.setVisibility(View.GONE);
            }
        });
        iv_back.setOnClickListener(v -> {
           super.onBackPressed();
        });
    }
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_search_categoreis)
    EditText etSearch;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.pbLoading)
    ProgressBar pbLoading;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.iv_back)
    ImageView iv_back;
    private InboxListAdapter adapter;
    private RelativeLayout noMessages;
    private RecyclerView chatList;

    private void fetchData(String query) {
        pbLoading.setVisibility(View.VISIBLE);
        noMessages.setVisibility(View.GONE);
        chatList.setVisibility(View.GONE);
        ArrayList<ConversationModel> items = new ArrayList<>();
        Helper.closeKeyboard(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_CHAT +
                "/conversations" + "?page=" + 1 + "&query=" +query,
                response -> {
                    Timber.e(response);
                    try {
                        pbLoading.setVisibility(View.GONE);
                        chatList.setVisibility(View.VISIBLE);
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (!jsonObject.has("data")) {
                            this.showToast("some went to wrong", this);
                            return;
                        }
                        JSONArray jsonArray_data = jsonObject.getJSONArray("data");
                        for (int i = 0; jsonArray_data.length() > i; i++) {
                            JSONObject jsonObject_conversation = jsonArray_data.getJSONObject(i);
                            ConversationModel conversationModel = new ConversationModel(this).getJsonToModel(jsonObject_conversation,
                                    this);
                            items.add(conversationModel);
                        }
                        adapter.clear();
                        adapter.addItems(items);
                        if (items.size() <= 0) {
                            noMessages.setVisibility(View.VISIBLE);
                            chatList.setVisibility(View.GONE);
                        } else {
                            noMessages.setVisibility(View.GONE);
                            chatList.setVisibility(View.VISIBLE);

                        }


                    } catch (JSONException e) {
                        this.hideProgressDialog();
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                    }
                },
                error -> {
                    pbLoading.setVisibility(View.GONE);
                    this.errorHandle1(error.networkResponse);
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        Timber.e(stringRequest.getUrl());
    }
    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            //set adapter to online mode (previewMode will be false)
            fetchData(etSearch.getText().toString());
            etSearch.clearFocus();
            pbLoading.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }
}