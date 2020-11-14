package com.jobtick.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.jobtick.R;
import com.jobtick.adapers.AttachmentAdapter;
import com.jobtick.adapers.PublicChatListAdapter;
import com.jobtick.models.AttachmentModel;
import com.jobtick.models.CommentModel;
import com.jobtick.models.OfferModel;
import com.jobtick.models.QuestionModel;
import com.jobtick.retrofit.ApiClient;
import com.jobtick.utils.CameraUtils;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.Helper;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.ImageUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;

import static com.jobtick.activities.TaskDetailsActivity.isOfferQuestion;
import static com.jobtick.pagination.PaginationListener.PAGE_START;

public class PublicChatActivity extends ActivityBase implements View.OnClickListener, AttachmentAdapter.OnItemClickListener {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_avatar)
    CircularImageView imgAvatar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rlt_profile)
    RelativeLayout rltProfile;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_name)
    TextView txtName;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.ratingbar_worker)
    AppCompatRatingBar ratingbarWorker;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_rating_value)
    TextView txtRatingValue;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_completion_rate)
    TextView txtCompletionRate;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_budget)
    TextView txtBudget;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_budget_status)
    LinearLayout lytBudgetStatus;
    //@SuppressLint("NonConstantResourceId")
    //@BindView(R.id.txt_type)
   // TextViewBold txtType;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_created_date)
    TextView txtCreatedDate;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_message)
    TextView txtMessage;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_offer_on_task)
    ImageView imgOfferOnTask;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_btn_play)
    ImageView imgBtnPlay;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.card_live_video)
    CardView cardLiveVideo;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_reply)
    LinearLayout lytBtnReply;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_more)
    LinearLayout lytBtnMore;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_btn_image_select)
    ImageView imgBtnImageSelect;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_comment_message)
    EditText edtCommentMessage;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_btn_send)
    ImageView imgBtnSend;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.card_send)
    CardView cardSend;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.layout_offer)
    LinearLayout layoutOffer;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_avatar_question)
   CircularImageView imgAvatarQuestion;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_name_question)
    TextView txtNameQuestion;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_created_date_question)
    TextView txtCreatedDateQuestion;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_message_question)
    TextView txtMessageQuestion;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_reply_question)
    LinearLayout lytBtnReplyQuestion;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_more_less_arrow)
    ImageView imgMoreLessArrow;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_more_less)
    TextView txtMoreLess;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_more_question)
    LinearLayout lytBtnMoreQuestion;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.layout_question)
    LinearLayout layoutQuestion;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_create_message)
    LinearLayout lytCreateMessage;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_view_question)
    RecyclerView recyclerViewQuestion;

    private OfferModel offerModel;
    private QuestionModel questionModel;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_view_offer_chat)
    RecyclerView recyclerViewOfferChat;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rlt_layout_action_data)
    RelativeLayout rltLayoutActionData;

    AttachmentModel attachment;

    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int GALLERY_PICKUP_IMAGE_REQUEST_CODE = 400;
    private static String imageStoragePath;

    private PublicChatListAdapter publicChatListAdapter;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;

    @BindView(R.id.iv_verified_account)
    ImageView ivVerifiedAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_chat);
        ButterKnife.bind(this);

        attachment = new AttachmentModel();
        txtBudget.setVisibility(View.GONE);
       // txtAccept.setVisibility(View.GONE);
        txtMessage.setVisibility(View.GONE);
        cardLiveVideo.setVisibility(View.GONE);
        //txtType.setVisibility(View.GONE);
        lytBtnReply.setVisibility(View.GONE);
        lytBtnMore.setVisibility(View.GONE);
        lytBtnReplyQuestion.setVisibility(View.GONE);
        lytBtnMoreQuestion.setVisibility(View.GONE);
        layoutOffer.setVisibility(View.GONE);
        layoutQuestion.setVisibility(View.GONE);

        Bundle bundle = getIntent().getExtras();
        offerModel = new OfferModel();
        questionModel = new QuestionModel();

        if (isOfferQuestion.equals("offer")) {
            offerModel = TaskDetailsActivity.offerModel;
            toolbar.setTitle("Offer Chat");
        } else {
            questionModel = TaskDetailsActivity.questionModel;
            toolbar.setTitle("Question Chat");

        }

       /* if (bundle != null && bundle.getParcelable(ConstantKey.OFFER_LIST_MODEL) != null) {
            offerModel = bundle.getParcelable(ConstantKey.OFFER_LIST_MODEL);
            toolbar.setTitle("Offer Chat");
        } else if (bundle != null && bundle.getParcelable(ConstantKey.QUESTION_LIST_MODEL) != null) {
            questionModel = bundle.getParcelable(ConstantKey.QUESTION_LIST_MODEL);
            toolbar.setTitle("Question Chat");
        }*/

        initLayout();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imgBtnImageSelect.setOnClickListener(this);
        imgBtnPlay.setOnClickListener(this);
        imgBtnSend.setOnClickListener(this);
        lytBtnMore.setOnClickListener(this);


        //recyclerViewOfferChat.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(PublicChatActivity.this);
        recyclerViewOfferChat.setLayoutManager(layoutManager);
        publicChatListAdapter = new PublicChatListAdapter(PublicChatActivity.this, new ArrayList<>());
        recyclerViewOfferChat.setAdapter(publicChatListAdapter);
        // publicChatListAdapter.setOnItemClickListener(this);
        if (offerModel.getTaskId() != null) {
            doApiCall(Constant.URL_OFFERS + "/" + offerModel.getId());
        } else {
            doApiCall(Constant.URL_QUESTIONS + "/" + questionModel.getId());
        }


    }

    private void initLayout() {
        if (offerModel.getTaskId() != null) {
            layoutOffer.setVisibility(View.VISIBLE);
            layoutQuestion.setVisibility(View.GONE);
            if (offerModel.getWorker().getAvatar() != null)
                ImageUtil.displayImage(imgAvatar, offerModel.getWorker().getAvatar().getThumbUrl(), null);
            txtName.setText(offerModel.getWorker().getName());
            if (offerModel.getWorker() != null && offerModel.getWorker().getWorkerRatings() != null && offerModel.getWorker().getWorkerRatings().getAvgRating() != null) {
                txtRatingValue.setText("(" + offerModel.getWorker().getWorkerRatings().getAvgRating() + ")");
                ratingbarWorker.setProgress(offerModel.getWorker().getWorkerRatings().getAvgRating());
            }
            if (offerModel.getWorker().getIsVerifiedAccount() == 1) {
                ivVerifiedAccount.setVisibility(View.VISIBLE);
            } else {
                ivVerifiedAccount.setVisibility(View.GONE);
            }
            txtCompletionRate.setText(offerModel.getWorker().getWorkTaskStatistics().getCompletionRate() + "% Completion Rate");
            txtCreatedDate.setText(offerModel.getCreatedAt());
            if (offerModel.getAttachments() != null && offerModel.getAttachments().size() != 0) {
                cardLiveVideo.setVisibility(View.VISIBLE);
             //   txtType.setText("Video Offer");
                ImageUtil.displayImage(imgOfferOnTask, offerModel.getAttachments().get(0).getModalUrl(), null);

            } else {
                txtMessage.setVisibility(View.VISIBLE);
               // txtType.setText("Message");
                txtMessage.setText(offerModel.getMessage());
            }
          //  txtType.setVisibility(View.VISIBLE);
        } else {
            layoutOffer.setVisibility(View.GONE);
            layoutQuestion.setVisibility(View.VISIBLE);
            if (questionModel.getUser().getAvatar() != null)
                ImageUtil.displayImage(imgAvatarQuestion, questionModel.getUser().getAvatar().getThumbUrl(), null);
            txtNameQuestion.setText(questionModel.getUser().getName());
            txtMessageQuestion.setVisibility(View.VISIBLE);
            txtMessageQuestion.setText(questionModel.getQuestionText());
            if (questionModel.getAttachments() != null && questionModel.getAttachments().size() != 0) {
                recyclerViewQuestion.setVisibility(View.VISIBLE);
                AttachmentAdapter attachmentAdapter = new AttachmentAdapter(questionModel.getAttachments(), false);
                recyclerViewQuestion.setHasFixedSize(true);
                recyclerViewQuestion.setLayoutManager(new LinearLayoutManager(PublicChatActivity.this, LinearLayoutManager.HORIZONTAL, false));
                recyclerViewQuestion.setAdapter(attachmentAdapter);
                recyclerViewQuestion.setNestedScrollingEnabled(true);
                attachmentAdapter.setOnItemClickListener(this);
            } else {
                recyclerViewQuestion.setVisibility(View.GONE);
            }
            txtCreatedDateQuestion.setText(questionModel.getCreatedAt());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_btn_play:

                break;
            case R.id.img_btn_send:
                if (validation()) {
                    String str_message = edtCommentMessage.getText().toString().trim();
                    if (offerModel.getTaskId() != null) {
                        if (attachment.getThumbUrl() != null) {
                            addCommentIntoServer(str_message, attachment.getId(), Constant.URL_OFFERS + "/" + offerModel.getId());
                            edtCommentMessage.setText(null);
                            imgBtnImageSelect.setImageDrawable(getResources().getDrawable(R.drawable.ic_paperclip));
                        } else {
                            addCommentIntoServer(str_message, null, Constant.URL_OFFERS + "/" + offerModel.getId());
                            edtCommentMessage.setText(null);
                            imgBtnImageSelect.setImageDrawable(getResources().getDrawable(R.drawable.ic_paperclip));
                        }
                    } else {
                        if (attachment.getThumbUrl() != null) {
                            addCommentIntoServer(str_message, attachment.getId(), Constant.URL_QUESTIONS + "/" + questionModel.getId());
                            edtCommentMessage.setText(null);
                            imgBtnImageSelect.setImageDrawable(getResources().getDrawable(R.drawable.ic_paperclip));
                        } else {
                            addCommentIntoServer(str_message, null, Constant.URL_QUESTIONS + "/" + questionModel.getId());
                            edtCommentMessage.setText(null);
                            imgBtnImageSelect.setImageDrawable(getResources().getDrawable(R.drawable.ic_paperclip));
                        }
                    }
                }
                break;
            case R.id.lyt_btn_more:

                break;
            case R.id.img_btn_image_select:
                PopupMenu popupMenu = new PopupMenu(PublicChatActivity.this, imgBtnImageSelect);
                if (attachment.getThumbUrl() == null) {
                    popupMenu.getMenu().removeItem(R.id.action_remove);
                    //   menu.setGroupVisible(R.id.grp_3,false);
                } else {
                    Menu menu = popupMenu.getMenu();
                    menu.setGroupVisible(R.id.grp_3, true);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_camera:
                                // Checking availability of the camera
                                if (!CameraUtils.isDeviceSupportCamera(getApplicationContext())) {
                                    Toast.makeText(getApplicationContext(),
                                            "Sorry! Your device doesn't support camera",
                                            Toast.LENGTH_LONG).show();
                                    // will close the app if the device doesn't have camera

                                } else {
                                    if (CameraUtils.checkPermissions(getApplicationContext())) {
                                        captureImage();
                                    } else {
                                        requestCameraPermission(ConstantKey.MEDIA_TYPE_IMAGE);
                                    }
                                }
                                break;
                            case R.id.action_gallery:
                                Intent opengallary = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(Intent.createChooser(opengallary, "Open Gallary"), GALLERY_PICKUP_IMAGE_REQUEST_CODE);
                                break;
                            case R.id.action_remove:
                                if (attachment.getThumbUrl() != null) {
                                    attachment = new AttachmentModel();
                                    imgBtnImageSelect.setImageDrawable(getResources().getDrawable(R.drawable.ic_paperclip));
                                }
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.inflate(R.menu.menu_public_chat);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    popupMenu.getMenu().setGroupDividerEnabled(true);
                }
                popupMenu.show();
                break;

        }
    }


    /**
     * Requesting permissions using Dexter library
     */
    private void requestCameraPermission(final int type) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            if (type == ConstantKey.MEDIA_TYPE_IMAGE) {
                                // capture picture
                                captureImage();
                            }

                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showPermissionsAlert();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    /**
     * Capturing Camera Image will launch camera app requested image capture
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = CameraUtils.getOutputMediaFile(ConstantKey.MEDIA_TYPE_IMAGE);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }
        Uri fileUri = CameraUtils.getOutputMediaFileUri(getApplicationContext(), file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }


    private void addCommentIntoServer(String str_message, Integer id, String url) {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + "/comments",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        hideProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Timber.e(jsonObject.toString());
                            if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                                if (jsonObject.getBoolean("success")) {
                                    //TODO update recycler view
                                    JSONObject jsonObject_offer_chat = jsonObject.getJSONObject("data");
                                    CommentModel commentModel = new CommentModel().getJsonToModel(jsonObject_offer_chat);
                                    publicChatListAdapter.addItem(commentModel);

                                } else {
                                    showToast("Something went Wrong", PublicChatActivity.this);
                                }
                            }
                        } catch (JSONException e) {
                            Timber.e(String.valueOf(e));
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
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
                                    Toast.makeText(PublicChatActivity.this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                                if (jsonObject_error.has("errors")) {
                                    JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                                    if (jsonObject_errors.has("comment_text") && !jsonObject_errors.has("comment_text")) {
                                        String message = jsonObject_errors.getString("comment_text");
                                        edtCommentMessage.setError(message);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            showToast("Something Went Wrong", PublicChatActivity.this);
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
                map1.put("comment_text", str_message);
                if (attachment.getThumbUrl() != null) {
                    map1.put("attachments", String.valueOf(id));
                }
                Log.e("MAP", String.valueOf(map1.size()));
                Timber.e(map1.toString());
                return map1;

            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(PublicChatActivity.this);
        requestQueue.add(stringRequest);
        Log.e("PublicChatActivity", stringRequest.getUrl());
    }

    private boolean validation() {
        if (TextUtils.isEmpty(edtCommentMessage.getText().toString().trim())) {
            edtCommentMessage.setError("?");
            return false;
        }
        return true;
    }


    private void doApiCall(String url) {

        ArrayList<CommentModel> items = new ArrayList<>();
        Helper.closeKeyboard(PublicChatActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "/comments",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("responce_url", response);
                        // categoryArrayList.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.e("json", jsonObject.toString());
                            JSONArray jsonArray_data = jsonObject.getJSONArray("data");
                            for (int i = 0; jsonArray_data.length() > i; i++) {
                                JSONObject jsonObject_offer_chat = jsonArray_data.getJSONObject(i);
                                CommentModel commentModel = new CommentModel().getJsonToModel(jsonObject_offer_chat);
                                items.add(commentModel);
                            }

                            Collections.reverse(items);
                            publicChatListAdapter.addItems(items);

                            recyclerViewOfferChat.scrollToPosition(items.size() - 1);
                            recyclerViewQuestion.scrollToPosition(items.size() - 1);

                        } catch (JSONException e) {
                            hideProgressDialog();
                            Log.e("EXCEPTION", String.valueOf(e));
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  swipeRefresh.setRefreshing(false);
                        errorHandle1(error.networkResponse);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("Authorization", "Bearer " + sessionManager.getAccessToken());
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(PublicChatActivity.this);
        requestQueue.add(stringRequest);
        Log.e("url", stringRequest.getUrl());
    }


    private void uploadDataInTempAttachmentMediaApi(File pictureFile) {
        showProgressDialog();
        Call<String> call;
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile);
        MultipartBody.Part imageFile = MultipartBody.Part.createFormData("media", pictureFile.getName(), requestFile);
        call = ApiClient.getClient().getTaskTempAttachmentMediaData("XMLHttpRequest", sessionManager.getTokenType() + " " + sessionManager.getAccessToken(), imageFile);


        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                hideProgressDialog();
                Log.e("Response", response.toString());
                if (response.code() == HttpStatus.HTTP_VALIDATION_ERROR) {
                    showToast(response.message(), PublicChatActivity.this);
                    return;
                }
                try {
                    String strResponse = response.body();
                    if (response.code() == HttpStatus.NOT_FOUND) {
                        Toast.makeText(PublicChatActivity.this, "not found", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (response.code() == HttpStatus.AUTH_FAILED) {
                        unauthorizedUser();
                        return;
                    }
                    if (response.code() == HttpStatus.SUCCESS) {
                        Log.e("body", strResponse);
                        JSONObject jsonObject = new JSONObject(strResponse);
                        Log.e("json", jsonObject.toString());
                        if (jsonObject.has("data")) {
                            JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                            attachment = new AttachmentModel().getJsonToModel(jsonObject_data);
                        }

                        ImageUtil.displayRoundImage(imgBtnImageSelect, attachment.getThumbUrl(), null);
                    } else {
                        showToast("Something went wrong", PublicChatActivity.this);
                    }
                } catch (JSONException e) {
                    showToast("Something went wrong", PublicChatActivity.this);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                hideProgressDialog();
                Log.e("Response", call.toString());
            }
        });

    }


    /**
     * Activity result method will be called after closing the camera
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);
                Uri uri = Uri.parse("file://" + imageStoragePath);
                uploadDataInTempAttachmentMediaApi(new File(uri.getPath()));
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == GALLERY_PICKUP_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
              /*  Uri filePath = data.getData();
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                if (data.getData() != null) {
                    imageStoragePath = CameraUtils.getPath(PublicChatActivity.this, data.getData());
                    File file = new File(imageStoragePath);
                    uploadDataInTempAttachmentMediaApi(file);
                }
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled Pickup Image", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to Pickup Image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    public void onItemClick(View view, AttachmentModel obj, int position, String action) {

    }
}
