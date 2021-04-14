package com.jobtick.android.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.jobtick.android.BuildConfig;
import com.jobtick.android.R;
import android.annotation.SuppressLint;
import android.widget.Toast;

import com.jobtick.android.adapers.PreviewTaskAdapter;
import com.jobtick.android.adapers.TaskListAdapter;
import com.jobtick.android.models.PreviewTaskModel;
import com.jobtick.android.models.PreviewTaskSetModel;
import com.jobtick.android.models.TaskModel;
import com.jobtick.android.pagination.PaginationListener;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.ConstantKey;
import com.jobtick.android.utils.Helper;
import com.jobtick.android.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static com.jobtick.android.pagination.PaginationListener.PAGE_START;

public class SearchTaskActivity extends ActivityBase implements TextView.OnEditorActionListener,
        TaskListAdapter.OnItemClickListener, PreviewTaskAdapter.OnItemClickListener<PreviewTaskModel> {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_search_new)
    MaterialButton lytSearchNew;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.iv_back)
    ImageView ivBack;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.list)
    RecyclerView recyclerView;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.empty_search)
    RelativeLayout emptySearch;


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_search_categoreis)
    EditText edtSearch;

    private SessionManager sessionManager;
    private TaskListAdapter adapter;
    private PreviewTaskSetModel previewTaskSetModel;
    private PreviewTaskAdapter previewTaskAdapter;

    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private int totalItem = 10;
    private boolean isLoading = false;

    private boolean isFromMyJobs = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_all);
        ButterKnife.bind(this);
        isFromMyJobs = getIntent().getBooleanExtra(ConstantKey.FROM_MY_JOBS_WITH_LOVE, false);
        //  RelativeLayout emptySearch = findViewById(R.id.empty_search);
        sessionManager = new SessionManager(this);
        if(isFromMyJobs){
            edtSearch.setHint(R.string.search_your_jobs);
        }
        else{
            edtSearch.setHint(R.string.search_jobs);
        }
        edtSearch.requestFocus();
        edtSearch.performClick();
        edtSearch.setOnEditorActionListener(this);

        setPreviewAdapter();
        searchWithVoice();
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.lyt_search_new, R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lyt_search_new:
                recyclerView.setVisibility(View.VISIBLE);
                emptySearch.setVisibility(View.GONE);
                edtSearch.setText("");
                edtSearch.requestFocus();
                edtSearch.performClick();
                showKeyboard(edtSearch);
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
        }

    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            //set adapter to online mode (previewMode will be false)
            setOnlineAdapter();
            setLoadMoreListener();
            doApiCall();
            return true;
        }
        return false;
    }


    private void doApiCall() {
        String queryParameter = "";

        queryParameter = edtSearch.getText().toString();

        ArrayList<TaskModel> items = new ArrayList<>();
        Helper.closeKeyboard(this);
        String url = Constant.URL_TASKS + "?search_query=" +  queryParameter + "&page=" + currentPage;
        if(isFromMyJobs)
            url = Constant.URL_TASKS + ConstantKey.ALL_MY_JOBS_URL_FILTER + "&search_query=" +  queryParameter + "&page=" + currentPage;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Timber.e(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                            JSONArray jsonArray_data = jsonObject.getJSONArray("data");
                            for (int i = 0; jsonArray_data.length() > i; i++) {
                                JSONObject jsonObject_taskModel_list = jsonArray_data.getJSONObject(i);
                                TaskModel taskModel = new TaskModel().getJsonToModel(jsonObject_taskModel_list, this);
                                items.add(taskModel);
                            }
                        } else {
                            showToast("some went to wrong", this);
                            return;
                        }
                        if (jsonObject.has("meta") && !jsonObject.isNull("meta")) {
                            JSONObject jsonObject_meta = jsonObject.getJSONObject("meta");
                            totalPage = jsonObject_meta.getInt("last_page");
                            totalItem = jsonObject_meta.getInt("total");
                            Constant.PAGE_SIZE = jsonObject_meta.getInt("per_page");
                        }

                        if (items.size() <= 0) {
                            emptySearch.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            emptySearch.setVisibility(View.GONE);
                        }
                        adapter.addItems(items, totalItem);

                    } catch (JSONException e) {
                        hideProgressDialog();
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                    }
                },
                error -> {
                    errorHandle1(error.networkResponse);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("Authorization", "Bearer " + sessionManager.getAccessToken());
                map1.put("Version", String.valueOf(BuildConfig.VERSION_CODE));
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        Timber.e(stringRequest.getUrl());
    }


    private void setPreviewAdapter() {
        previewTaskSetModel = sessionManager.getPreviewTaskModel(SearchTaskActivity.class, isFromMyJobs);
        if (previewTaskSetModel == null)
            previewTaskSetModel = new PreviewTaskSetModel();

        previewTaskAdapter = new PreviewTaskAdapter(new ArrayList<>(previewTaskSetModel.getPreviewSet()));
        recyclerView.setAdapter(previewTaskAdapter);

        recyclerView.setHasFixedSize(true);
        previewTaskAdapter.setOnItemClickListener(this);
    }

    private void setOnlineAdapter(){
        adapter = new TaskListAdapter(new ArrayList<>(), null);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    private boolean previewMode = true;
    private void setLoadMoreListener() {
        if (!previewMode) return;
        recyclerView.addOnScrollListener(new PaginationListener((LinearLayoutManager) recyclerView.getLayoutManager()) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                doApiCall();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
        previewMode = false;
    }

    private void showKeyboard(EditText editText) {
        editText.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getBaseContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, 0);
            }
        });
    }

    @Override
    public void onItemClick(View view, TaskModel obj, int position, String action) {
        onItemClick(view, obj.getPreviewTaskModel(), position);
    }

    @Override
    public void onItemClick(View view, PreviewTaskModel obj, int position) {
        previewTaskSetModel.addItem(obj);
        Intent intent = new Intent(this, TaskDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ConstantKey.SLUG, obj.getSlug());
     //   bundle.putInt(ConstantKey.USER_ID, obj.getUserId());
        intent.putExtras(bundle);
        startActivity(intent);
        sessionManager.setPreviewTaskModel(previewTaskSetModel, SearchTaskActivity.class, isFromMyJobs);
    }

    //voice search
    private int MY_PERMISSIONS_RECORD_AUDIO=123;

    @BindView(R.id.btnVoice)
    ImageView btnVoice;

    private void searchWithVoice() {
        btnVoice.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ContextCompat.checkSelfPermission(SearchTaskActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(SearchTaskActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
                        }
                        else {
                            speechToText();
                        }

                    }
                });
    }

    private void speechToText() {
        btnVoice.startAnimation(AnimationUtils.loadAnimation(SearchTaskActivity.this, R.anim.video_icon_animate));

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                "com.domain.app");

        SpeechRecognizer recognizer = SpeechRecognizer
                .createSpeechRecognizer(SearchTaskActivity.this.getApplicationContext());
        RecognitionListener listener = new RecognitionListener() {
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> voiceResults = results
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (voiceResults == null) {
                    btnVoice.clearAnimation();
                    System.out.println("No voice results");
                } else {
                    edtSearch.setText(voiceResults.get(0));
                    setOnlineAdapter();
                    setLoadMoreListener();
                    doApiCall();
                    btnVoice.clearAnimation();
                }
            }

            @Override
            public void onReadyForSpeech(Bundle params) {
                System.out.println("Ready for speech");
            }

            /**
             *  ERROR_NETWORK_TIMEOUT = 1;
             *  ERROR_NETWORK = 2;
             *  ERROR_AUDIO = 3;
             *  ERROR_SERVER = 4;
             *  ERROR_CLIENT = 5;
             *  ERROR_SPEECH_TIMEOUT = 6;
             *  ERROR_NO_MATCH = 7;
             *  ERROR_RECOGNIZER_BUSY = 8;
             *  ERROR_INSUFFICIENT_PERMISSIONS = 9;
             *
             * @param error code is defined in SpeechRecognizer
             */
            @Override
            public void onError(int error) {
                btnVoice.clearAnimation();
                System.err.println("Error listening for speech: " + error);
            }

            @Override
            public void onBeginningOfSpeech() {
                System.out.println("Speech starting");
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onEndOfSpeech() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onRmsChanged(float rmsdB) {
                // TODO Auto-generated method stub

            }
        };
        recognizer.setRecognitionListener(listener);
        recognizer.startListening(intent);
    }

}

