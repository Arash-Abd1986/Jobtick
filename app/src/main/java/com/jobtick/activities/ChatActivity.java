package com.jobtick.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.adapers.ChatAdapter;
import com.jobtick.models.AttachmentModel;
import com.jobtick.models.ChatModel;
import com.jobtick.models.ConversationModel;
import com.jobtick.retrofit.ApiClient;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.Helper;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.ImageUtil;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PresenceChannel;
import com.pusher.client.channel.PresenceChannelEventListener;
import com.pusher.client.channel.PrivateChannel;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.User;
import com.pusher.client.connection.Connection;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;

import static com.jobtick.pagination.PaginationListener.PAGE_START;

public class ChatActivity extends ActivityBase implements SwipeRefreshLayout.OnRefreshListener {

    Pusher pusher;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_status)
    TextView txtStatus;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.card_status)
    CardView cardStatus;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_btn_task_action)
    ImageView imgBtnTaskAction;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_task_details)
    LinearLayout lytTaskDetails;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
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
    @BindView(R.id.rlt_layout_action_data)
    RelativeLayout rltLayoutActionData;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_avatar)
    CircularImageView imgAvatar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_subtitle)
    TextView txtSubtitle;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_job_title)
    TextView txtJobTitle;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_scroll_down)
    LinearLayout lytScrollDown;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.bottom_sheet)
    FrameLayout bottomSheet;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txtCount)
    TextView txtCount;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.pbLoading)
    ProgressBar pbLoading;


    public static ConversationModel conversationModel;
    private ChatAdapter adapter;
    private ArrayList<ChatModel> chatModelArrayList;

    AttachmentModel attachment;

    PrivateChannel channel;
    PresenceChannel presenceChannel;
    private int currentPage = PAGE_START;
    private final boolean isLastPage = false;
    private int unreadCount = 0;
    private boolean isLastPosition = false;
    LinearLayoutManager layoutManager;

    private UploadableImage uploadableImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        ConstantKey.IS_CHAT_SCREEN = true;
        //  setSupportActionBar(toolbar);
        toolbar.setSubtitle("Offline");
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("X-REQUESTED-WITH", "xmlhttprequest");
        headers.put("Accept", "application/json");
        HttpAuthorizer authorizer = new HttpAuthorizer(Constant.BASE_URL + "broadcasting/auth");
        authorizer.setHeaders(headers);
        PusherOptions options = new PusherOptions()
                //   .setEncrypted(true)
                .setCluster("ap4")
                .setAuthorizer(authorizer);

        //wait
        attachment = new AttachmentModel();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getParcelable(ConstantKey.CONVERSATION) != null) {
                try {
                    conversationModel = bundle.getParcelable(ConstantKey.CONVERSATION);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        if (conversationModel != null) {
            setToolbar(conversationModel);
            if(conversationModel.getChatClosed() != null &&
                    conversationModel.getChatClosed())
                rltLayoutActionData.setVisibility(View.GONE);

        }
        pusher = new Pusher(getString(R.string.pusher_api_key), options);

        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                Timber.e("%s", change.getCurrentState());
                System.out.println("State changed to " + change.getCurrentState() +
                        " from " + change.getPreviousState());
                if (change.getCurrentState() == ConnectionState.CONNECTED) {
                    subscribeToChannel(); //run kro ok
                    subscribeToPresenceChannel();
                }
            }

            @Override
            public void onError(String message, String code, Exception e) {
                System.out.println("There was a problem connecting!");
            }
        }, ConnectionState.ALL);


        recyclerView.setHasFixedSize(true);
        chatModelArrayList = new ArrayList<>();
        swipeRefresh.setOnRefreshListener(this);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(ChatActivity.this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<>(), conversationModel.getSender().getId());
        recyclerView.setAdapter(adapter);
        doApiCall();

        initComponentScroll();

        lytScrollDown.setOnClickListener(v -> {
            animateFab(true);
            unreadCount = 0;
            txtCount.setText(String.valueOf(unreadCount));
            recyclerView.smoothScrollToPosition(adapter.getItemCount());
        });

        uploadableImage = new AbstractUploadableImageImpl(this) {
            @Override
            public void onImageReady(File imageFile) {
                uploadDataInPortfolioMediaApi(imageFile);
            }
        };
    }



    @Override
    public void onBackPressed() {
        ConstantKey.IS_CHAT_SCREEN = false;

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ConstantKey.PRIVATE_CHAT, true);
        intent.putExtras(bundle);
        setResult(ConstantKey.RESULTCODE_PRIVATE_CHAT, intent);
        super.onBackPressed();
    }

    private void initComponentScroll() {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            /**
             * Callback method to be invoked when the RecyclerView has been scrolled. This will be
             * called after the scroll has completed.
             * <p>
             * This callback will also be called if visible item range changes after a layout
             * calculation. In that case, dx and dy will be 0.
             *
             * @param recyclerView The RecyclerView which scrolled.
             * @param dx           The amount of horizontal scroll.
             * @param dy           The amount of vertical scroll.
             */
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //TODO this is due
                if (dy > 0) {
                    animateFab(true);
                } else {
                    unreadCount = 0;
                    animateFab(false);
                }

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
                //End of list
                isLastPosition = pastVisibleItems + visibleItemCount >= totalItemCount;
            }
        });

    }

    boolean isFabHide = false;

    private void animateFab(final boolean hide) {
        if (isFabHide && hide || !isFabHide && !hide) return;
        isFabHide = hide;
        int moveY = hide ? (2 * lytScrollDown.getHeight()) : 0;
        lytScrollDown.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
        if (unreadCount == 0) {
            txtCount.setVisibility(View.GONE);
        } else {
            txtCount.setVisibility(View.VISIBLE);

        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setToolbar(ConversationModel conversationModel) {
        if (conversationModel != null && conversationModel.getReceiver() != null) {
            if (conversationModel.getReceiver().getName() != null) {
                txtTitle.setText(conversationModel.getReceiver().getName());
                imgAvatar.setOnClickListener(v -> {
                    Intent intent = new Intent(ChatActivity.this, ProfileActivity.class);
                    intent.putExtra("id",conversationModel.getReceiver().getId());
                    startActivity(intent);
                });
                txtTitle.setOnClickListener(v -> {
                    imgAvatar.performClick();
                });
            }
        }
        assert conversationModel != null;
        assert conversationModel.getReceiver() != null;
        if (conversationModel.getReceiver().getAvatar() != null && conversationModel.getReceiver().getAvatar().getThumbUrl() != null) {
//            imgAvatar.setImageDrawable(getResources().getDrawable(R.drawable.avatar));

            ImageUtil.displayImage(imgAvatar, conversationModel.getReceiver().getAvatar().getThumbUrl(), null);
        } else {
            imgAvatar.setImageDrawable(getResources().getDrawable(R.drawable.avatar));
        }
        txtJobTitle.setText(conversationModel.getName());
        txtStatus.setText(conversationModel.getStatus());



    }

    private void subscribeToPresenceChannel() {
        presenceChannel = pusher.subscribePresence("presence-userStatus", new PresenceChannelEventListener() {
            @Override
            public void onUsersInformationReceived(String channelName, Set<User> users) {
                ArrayList<Integer> integerArrayList = new ArrayList<>();
                for (User user : users) {
                    integerArrayList.add(Integer.parseInt(user.getId()));
                }
                setToolbarSubTitle(false);
                for (int i = 0; integerArrayList.size() > i; i++) {
                    int id = integerArrayList.get(i);
                    if (id == conversationModel.getReceiver().getId()) {
                        setToolbarSubTitle(true);
                    }
                }
                Timber.e("%s", integerArrayList.size());
            }

            @Override
            public void userSubscribed(String channelName, User user) {
                //   adapter.addNewSubscribe(Integer.parseInt(user.getId()));
                Timber.e(user.toString());
                if (Integer.parseInt(user.getId()) == conversationModel.getReceiver().getId()) {
                    setToolbarSubTitle(true);
                }
            }

            @Override
            public void userUnsubscribed(String channelName, User user) {
                // adapter.addNewUnSubscribe(Integer.parseInt(user.getId()));
                Timber.e(user.toString());
                if (Integer.parseInt(user.getId()) == conversationModel.getReceiver().getId()) {
                    setToolbarSubTitle(false);
                }
            }

            @Override
            public void onAuthenticationFailure(String message, Exception e) {
                Timber.e(message);
            }

            @Override
            public void onSubscriptionSucceeded(String channelName) {
                Timber.e(channelName);
            }

            @Override
            public void onEvent(PusherEvent event) {
                Timber.e(event.toString());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setToolbarSubTitle(boolean status) {

        if (status) {

            runOnUiThread(() -> {
                txtSubtitle.setText("Online");
                txtSubtitle.setTextColor(getResources().getColor(R.color.colorPrimary));

                // Stuff that updates the UI

            });


        } else {
            runOnUiThread(() -> {
                txtSubtitle.setText("Offline");
                txtSubtitle.setTextColor(getResources().getColor(R.color.colorPrimary));
                // Stuff that updates the UI

            });

        }

    }

    protected void subscribeToChannel() {

        try {
            channel = pusher.subscribePrivate("private-conversation." + conversationModel.getId(), new PrivateChannelEventListener() {
                @Override
                public void onAuthenticationFailure(String message, Exception e) {
                    Connection cpm = pusher.getConnection();
                    Timber.e(message);
                    Timber.e(e.toString());
                    Timber.e(cpm.getSocketId());
                }

                @Override
                public void onSubscriptionSucceeded(String channelName) {
                    Timber.e(channelName); //its json response not string
                }

                @Override
                public void onEvent(PusherEvent event) {
                    Timber.e(event.toString());
                    try {
                        JSONObject jsonObject = new JSONObject(event.getData());
                        ChatModel chatModel = new ChatModel().getJsonToModel(jsonObject);
                        boolean isAvailable = false;
                        ArrayList<ChatModel> chatModelArrayList1 = chatModelArrayList;
                        Collections.reverse(chatModelArrayList1);
                        for (ChatModel chatModel1 : chatModelArrayList1) {
                            if (chatModel.getId().equals(chatModel1.getId())) {
                                isAvailable = true;
                                break;
                            }
                        }
                        if (!isAvailable) {

                            runOnUiThread(() -> {


                                // Stuff that updates the UI
                                //chatModelArrayList.add(chatModel);
                                adapter.addItems(chatModel);
                                if (isLastPosition) {
                                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                                } else {
                                    unreadCount = unreadCount + 1;
                                    txtCount.setVisibility(View.VISIBLE);
                                    txtCount.setText(String.valueOf(unreadCount));
                                }
                               /* // adapter.notifyItemChanged(adapter.getItemCount() - 1);
                                    if (chatModel.getSenderId() == sessionManager.getUserAccount().getId()) {
                                    }else
                                    {

                                    }*/


                            });


                        }
                        Timber.e(jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    System.out.println("Received event with data: " + event.toString());

                }
            }, "message.sent");

        } catch (Exception ignored) {

        }

    }

    private void doApiCall() {

        ArrayList<ChatModel> items = new ArrayList<>();
        Helper.closeKeyboard(ChatActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_CHAT + "/" + conversationModel.getId() + "/messages" + "?page=" + currentPage,
                response -> {
                    Timber.e(response);
                    // categoryArrayList.clear();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (!jsonObject.has("data")) {
                            showToast("SomeThing to wrong", ChatActivity.this);
                            return;
                        }
                        JSONArray jsonArray_data = jsonObject.getJSONArray("data");
                        for (int i = 0; jsonArray_data.length() > i; i++) {
                            JSONObject jsonObject_chat = jsonArray_data.getJSONObject(i);
                            ChatModel chatModel = new ChatModel().getJsonToModel(jsonObject_chat);
                            items.add(chatModel);
                        }

                        Collections.reverse(items);
                        if (jsonObject.has("meta") && !jsonObject.isNull("meta")) {
                            JSONObject jsonObject_meta = jsonObject.getJSONObject("meta");
                            Constant.PAGE_SIZE = jsonObject_meta.getInt("per_page");
                        }

                        /*
                         *manage progress view
                         */
                        //  if (currentPage != PAGE_START)
                        //      adapter.removeLoading();
                        adapter.addItems(items);


                        //   conversationModelArrayList = items;

                        swipeRefresh.setRefreshing(false);
                        // check weather is last page or not
//                        if (currentPage < totalPage) {
//                            adapter.addLoading();
//                        } else {
//                            isLastPage = true;
//                        }


                    } catch (JSONException e) {
                        hideProgressDialog();
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                    }
                },
                error -> {
                    swipeRefresh.setRefreshing(false);
                    errorHandle1(error.networkResponse);
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
        RequestQueue requestQueue = Volley.newRequestQueue(ChatActivity.this);
        requestQueue.add(stringRequest);
        Timber.e(stringRequest.getUrl());
    }

  /*  @Override
    public void onRefresh() {
        currentPage = PAGE_START;
        isLastPage = false;
       // adapter.clear();
        doApiCall();
    }*/


    @Override
    protected void onRestart() {
        super.onRestart();
        // Reconnect, with all channel subscriptions and event bindings automatically recreated
        pusher.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Disconnect from the service
        pusher.disconnect();
        conversationModel = null;
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.img_btn_task_action, R.id.img_btn_image_select, R.id.img_btn_send, R.id.lyt_task_details})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_btn_task_action:
                break;
            case R.id.img_btn_image_select:
               uploadableImage.showAttachmentBottomSheet(false);
             //   showBottomSheetDialog(true);
                break;
            case R.id.img_btn_send:
                if (validation()) {
                    String str_message = Objects.requireNonNull(edtCommentMessage.getText()).toString().trim();

                    addCommentIntoServer(str_message);
                    edtCommentMessage.setText(null);
                }
                break;
            case R.id.lyt_task_details:
                Intent intent = new Intent(ChatActivity.this, TaskDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(ConstantKey.SLUG, conversationModel.getSlug());
            //    bundle.putInt(ConstantKey.USER_ID, sessionManager.getUserAccount().getId());
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }

    private void addCommentIntoServer(String str_message) {
        // showpDialog();
        pbLoading.setVisibility(View.VISIBLE);
        imgBtnSend.setVisibility(View.GONE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_CHAT + "/send",
                response -> {
                    pbLoading.setVisibility(View.GONE);
                    imgBtnSend.setVisibility(View.VISIBLE);
                    Timber.e(response);
                    // hidepDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                //TODO update recycler view
                                JSONObject jsonObject_chat = jsonObject.getJSONObject("data");
                                ChatModel chatModel = new ChatModel().getJsonToModel(jsonObject_chat);
                                // chatModelArrayList.add(chatModel);
                                //adapter.addItems(chatModel);

                                // showToast("");
                            } else {
                                showToast("Something went Wrong", ChatActivity.this);
                            }
                        }
                        //  adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(adapter.getItemCount() - 1);

                    } catch (JSONException e) {
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                    }
                },
                error -> {
                    pbLoading.setVisibility(View.GONE);
                    imgBtnSend.setVisibility(View.VISIBLE);
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        // Print Error!
                        Timber.e(jsonError);
                        if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                            unauthorizedUser();
                            //   hidepDialog();
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);

                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                            if (jsonObject_error.has("message")) {
                               showToast(jsonObject_error.getString("message"), this);
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
                        showToast("Something Went Wrong", ChatActivity.this);
                    }
                    Timber.e(error.toString());
                    hideProgressDialog();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                return map1;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map1 = new HashMap<>();

                map1.put("conversation_id", String.valueOf(conversationModel.getId()));
                map1.put("task_id", String.valueOf(conversationModel.getTaskId()));
                if (str_message != null) {
                    map1.put("message", str_message);
                }
                Timber.e(String.valueOf(map1.size()));
                Timber.e(map1.toString());
                return map1;

            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(ChatActivity.this);
        requestQueue.add(stringRequest);
        Timber.e(stringRequest.getUrl());
    }

    private void uploadDataInPortfolioMediaApi(File pictureFile) {
        showProgressDialog();
        Call<String> call;
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile);
        RequestBody requestConversationid = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(conversationModel.getId()));

        MultipartBody.Part imageFile = MultipartBody.Part.createFormData("attachment", pictureFile.getName(), requestFile);
        call = ApiClient.getClient().getChatMediaUpload("XMLHttpRequest", sessionManager.getTokenType() + " " + sessionManager.getAccessToken(), imageFile, requestConversationid);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, retrofit2.@NotNull Response<String> response) {
                hideProgressDialog();
                Timber.d(response.toString());
                if (response.code() == HttpStatus.HTTP_VALIDATION_ERROR) {
                    showToast(response.message(), ChatActivity.this);
                    return;
                }
                try {
                    String strResponse = response.body();
                    if (response.code() == HttpStatus.NOT_FOUND) {
                       showToast("not found", ChatActivity.this);
                        return;
                    }
                    if (response.code() == HttpStatus.AUTH_FAILED) {
                        unauthorizedUser();
                        return;
                    }
                    if (response.code() == HttpStatus.SUCCESS) {
                        Timber.e(strResponse);
                        assert strResponse != null;
                        JSONObject jsonObject = new JSONObject(strResponse);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("data")) {
                            JSONObject jsonObject_chat = jsonObject.getJSONObject("data");
                            ChatModel chatModel = new ChatModel().getJsonToModel(jsonObject_chat);
                            //chatModelArrayList.add(chatModel);
                            //  adapter.addItems(chatModel);
                            // attachment = new Attachment().getJsonToModel(jsonObject_data);
                        }
                        // recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    } else {
                        showToast("Something went wrong", ChatActivity.this);
                    }
                } catch (JSONException e) {
                    showToast("Something went wrong", ChatActivity.this);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                hideProgressDialog();
                Timber.tag("FileUpload").d(t.toString());

            }
        });

    }

    private boolean validation() {
        if (TextUtils.isEmpty(Objects.requireNonNull(edtCommentMessage.getText()).toString().trim())) {
            edtCommentMessage.setError("?");
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uploadableImage.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onRefresh() {

        swipeRefresh.setRefreshing(true);
        if (!isLastPage) {
            currentPage++;
            doApiCall();
        } else {
            swipeRefresh.setRefreshing(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ConstantKey.IS_CHAT_SCREEN = false;
    }

}