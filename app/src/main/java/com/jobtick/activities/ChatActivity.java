package com.jobtick.activities;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jobtick.TextView.TextViewMedium;
import com.jobtick.TextView.TextViewSemiBold;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
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
import com.jobtick.EditText.EditTextRegular;
import com.jobtick.R;
import com.jobtick.TextView.TextViewBold;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.adapers.ChatAdapter;
import com.jobtick.models.AttachmentModel;
import com.jobtick.models.ChatModel;
import com.jobtick.models.ConversationModel;
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
import static com.jobtick.utils.ConstantKey.KEY_USER_REPORT;

public class ChatActivity extends ActivityBase implements SwipeRefreshLayout.OnRefreshListener {

    Pusher pusher;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txt_status)
    TextView txtStatus;
    @BindView(R.id.card_status)
    CardView cardStatus;
    @BindView(R.id.img_btn_task_action)
    ImageView imgBtnTaskAction;
    @BindView(R.id.card_task_action)
    CardView cardTaskAction;
    @BindView(R.id.lyt_task_details)
    LinearLayout lytTaskDetails;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.img_btn_image_select)
    ImageView imgBtnImageSelect;
    @BindView(R.id.edt_comment_message)
    EditTextRegular edtCommentMessage;
    @BindView(R.id.img_btn_send)
    ImageView imgBtnSend;
    @BindView(R.id.card_send)
    CardView cardSend;
    @BindView(R.id.rlt_layout_action_data)
    RelativeLayout rltLayoutActionData;
    @BindView(R.id.img_avatar)
    CircularImageView imgAvatar;
    @BindView(R.id.txt_title)
    TextViewRegular txtTitle;
    @BindView(R.id.txt_subtitle)
    TextViewRegular txtSubtitle;
    @BindView(R.id.txt_job_title)
    TextView txtJobTitle;
    @BindView(R.id.lyt_scroll_down)
    LinearLayout lytScrollDown;

    @BindView(R.id.txtCount)
    TextViewMedium txtCount;
    private ConversationModel conversationModel;
    private ChatAdapter adapter;
    private ArrayList<ChatModel> chatModelArrayList;


    AttachmentModel attachment;

    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int GALLERY_PICKUP_IMAGE_REQUEST_CODE = 400;
    private static String imageStoragePath;

    PrivateChannel channel;
    PresenceChannel presenceChannel;

    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;
    private int unreadCount = 0;
    private boolean isLastPosition=false;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        ConstantKey.IS_CHAT_SCREEN = true;
        //  setSupportActionBar(toolbar);
        toolbar.setSubtitle("Offline");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("X-REQUESTED-WITH", "xmlhttprequest");
        headers.put("Accept", "application/json");
        HttpAuthorizer authorizer = new HttpAuthorizer(Constant.BASE_URL + "broadcasting/auth");

        authorizer.setHeaders(headers);
        PusherOptions options = new PusherOptions()
                .setEncrypted(true)
                .setCluster("us2")
                .setAuthorizer(authorizer);

        //wait
        attachment = new AttachmentModel();
        conversationModel = new ConversationModel(ChatActivity.this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getParcelable(ConstantKey.CONVERSATION) != null) {
                conversationModel = bundle.getParcelable(ConstantKey.CONVERSATION);
            }
        }

        if (conversationModel != null) {
            setToolbar(conversationModel);
        }
        pusher = new Pusher("31c5e7256697a01d331a", options);

        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                Log.e("connection", change.getCurrentState() + "");
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
        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<>(), conversationModel.getReceiver().getName(), conversationModel.getSender().getId());
        recyclerView.setAdapter(adapter);
        // publicChatListAdapter.setOnItemClickListener(this);
        doApiCall();

       /* recyclerView.addOnScrollListener(new PaginationListener(layoutManager) {
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
*/

        initComponentScroll();

        lytScrollDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab(true);
                unreadCount = 0;
                txtCount.setText(String.valueOf(unreadCount));
                recyclerView.smoothScrollToPosition(adapter.getItemCount());
            }
        });


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
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    //End of list
                    isLastPosition=true;
                }else
                {
                    isLastPosition=false;
                }


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


    private void setToolbar(ConversationModel conversationModel) {
        if (conversationModel != null && conversationModel.getReceiver() != null) {
            if (conversationModel.getReceiver().getName() != null) {
                txtTitle.setText(conversationModel.getReceiver().getName());
            }
        }
        if (conversationModel.getReceiver().getAvatar() != null && conversationModel.getReceiver().getAvatar().getThumbUrl() != null) {
            imgAvatar.setImageDrawable(getResources().getDrawable(R.drawable.avatar));

            //   ImageUtil.displayRoundImage(imgAvatar, "https://dev.jobtick.com/storage/uploads/16/resized/image1596373636763-thumb.jpg", null);
        } else {
            imgAvatar.setImageDrawable(getResources().getDrawable(R.drawable.avatar));
        }
        txtJobTitle.setText(conversationModel.getName());
        txtStatus.setText(conversationModel.getStatus());

        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {

                case R.id.action_flag:

                    Bundle bundleReport = new Bundle();
                    Intent intentReport = new Intent(ChatActivity.this, ReportActivity.class);
                    bundleReport.putString("key", KEY_USER_REPORT);
                    bundleReport.putInt(Constant.userID, conversationModel.getReceiver().getId());

                    intentReport.putExtras(bundleReport);
                    startActivity(intentReport);
                    break;
            }
            return false;
        });

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
                Log.e("user_sub", integerArrayList.size() + "");
            }

            @Override
            public void userSubscribed(String channelName, User user) {
                //   adapter.addNewSubscribe(Integer.parseInt(user.getId()));
                Log.e("new_user_sub", user.toString());
                if (Integer.parseInt(user.getId()) == conversationModel.getReceiver().getId()) {
                    setToolbarSubTitle(true);
                }
            }

            @Override
            public void userUnsubscribed(String channelName, User user) {
                // adapter.addNewUnSubscribe(Integer.parseInt(user.getId()));
                Log.e("old_user_unsub", user.toString());
                if (Integer.parseInt(user.getId()) == conversationModel.getReceiver().getId()) {
                    setToolbarSubTitle(false);
                }
            }

            @Override
            public void onAuthenticationFailure(String message, Exception e) {
                Log.e("presence_auth", message);
            }

            @Override
            public void onSubscriptionSucceeded(String channelName) {
                Log.e("succeeded", channelName);
            }

            @Override
            public void onEvent(PusherEvent event) {
                Log.e("Call", event.toString());
            }
        });
    }

    private void setToolbarSubTitle(boolean status) {

        if (status) {

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    txtSubtitle.setText("Online");
                    txtSubtitle.setTextColor(getResources().getColor(R.color.green));

                    // Stuff that updates the UI

                }
            });


        } else {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    txtSubtitle.setText("Offline");
                    txtSubtitle.setTextColor(getResources().getColor(R.color.red_600));
                    // Stuff that updates the UI

                }
            });

        }

    }

    protected void subscribeToChannel() {

        try {
            channel = pusher.subscribePrivate("private-conversation." + conversationModel.getId(), new PrivateChannelEventListener() {
                @Override
                public void onAuthenticationFailure(String message, Exception e) {
                    Connection cpm = pusher.getConnection();
                    Log.e("auth", message);
                    Log.e("auth", e.toString());
                    Log.e("SS : ", cpm.getSocketId());
                }

                @Override
                public void onSubscriptionSucceeded(String channelName) {
                    Log.e("succeeded", channelName); //its json response not string
                }

                @Override
                public void onEvent(PusherEvent event) {
                    Log.e("Call", event.toString());
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

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {


                                    // Stuff that updates the UI
                                    //chatModelArrayList.add(chatModel);
                                    adapter.addItems(chatModel);
                                    if(isLastPosition)
                                    {
                                        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                                    }else
                                    {
                                        unreadCount = unreadCount + 1;
                                        txtCount.setVisibility(View.VISIBLE);
                                        txtCount.setText(String.valueOf(unreadCount));
                                    }
                                   /* // adapter.notifyItemChanged(adapter.getItemCount() - 1);
                                        if (chatModel.getSenderId() == sessionManager.getUserAccount().getId()) {
                                        }else
                                        {

                                        }*/


                                }
                            });


                        }
                        Log.e("json", jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    System.out.println("Received event with data: " + event.toString());

                }
            }, new String[]{"message.sent"});

        } catch (Exception e) {

        }

    }

    private void doApiCall() {
        ArrayList<ChatModel> items = new ArrayList<>();
        Helper.closeKeyboard(ChatActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_CHAT + "/" + conversationModel.getId() + "/messages" + "?page=" + currentPage,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("responce_url", response);
                        // categoryArrayList.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.e("json", jsonObject.toString());
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
                                totalPage = jsonObject_meta.getInt("last_page");
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
                            if (currentPage < totalPage) {
                                // adapter.addLoading();
                            } else {
                                isLastPage = true;
                            }
                            isLoading = false;


                        } catch (JSONException e) {
                            hidepDialog();
                            Log.e("EXCEPTION", String.valueOf(e));
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        swipeRefresh.setRefreshing(false);
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
        RequestQueue requestQueue = Volley.newRequestQueue(ChatActivity.this);
        requestQueue.add(stringRequest);
        Log.e("url", stringRequest.getUrl());
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
    }

    @OnClick({R.id.img_btn_task_action, R.id.img_btn_image_select, R.id.img_btn_send, R.id.lyt_task_details})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_btn_task_action:
                break;
            case R.id.img_btn_image_select:
                PopupMenu popupMenu = new PopupMenu(ChatActivity.this, imgBtnImageSelect);
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
                            case R.id.action_doc:

                                break;
                        }
                        return true;
                    }
                });
                popupMenu.inflate(R.menu.menu_private_chat);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    popupMenu.getMenu().setGroupDividerEnabled(true);
                }
                popupMenu.show();
                break;
            case R.id.img_btn_send:
                if (validation()) {
                    String str_message = edtCommentMessage.getText().toString().trim();

                    addCommentIntoServer(str_message);
                    edtCommentMessage.setText(null);
                }
                break;
            case R.id.lyt_task_details:
                Intent intent = new Intent(ChatActivity.this, TaskDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(ConstantKey.SLUG, conversationModel.getSlug());
                bundle.putInt(ConstantKey.USER_ID, sessionManager.getUserAccount().getId());
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }

    private void addCommentIntoServer(String str_message) {
        // showpDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_CHAT + "/send",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
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
                    }
                },
                error -> {
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
                                Toast.makeText(ChatActivity.this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
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
                    hidepDialog();
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

                map1.put("conversation_id", String.valueOf(conversationModel.getId()));
                map1.put("task_id", String.valueOf(conversationModel.getTaskId()));
                if (str_message != null) {
                    map1.put("message", str_message);
                }
                Log.e("MAP", String.valueOf(map1.size()));
                Timber.e(map1.toString());
                return map1;

            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(ChatActivity.this);
        requestQueue.add(stringRequest);
        Log.e("OfferChatActivity", stringRequest.getUrl());
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


    private void uploadDataInPortfolioMediaApi(File pictureFile) {
        showpDialog();
        Call<String> call;
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile);
        RequestBody requestConversationid = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(conversationModel.getId()));

        MultipartBody.Part imageFile = MultipartBody.Part.createFormData("attachment", pictureFile.getName(), requestFile);
        call = ApiClient.getClient().getChatMediaUpload("XMLHttpRequest", sessionManager.getTokenType() + " " + sessionManager.getAccessToken(), imageFile, requestConversationid);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                hidepDialog();
                Log.e("Response", response.toString());
                if (response.code() == HttpStatus.HTTP_VALIDATION_ERROR) {
                    showToast(response.message(), ChatActivity.this);
                    return;
                }
                try {
                    String strResponse = response.body();
                    if (response.code() == HttpStatus.NOT_FOUND) {
                        Toast.makeText(ChatActivity.this, "not found", Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<String> call, Throwable t) {
                hidepDialog();
                Log.e("Response", call.toString());
            }
        });

    }


    private boolean validation() {
        if (TextUtils.isEmpty(edtCommentMessage.getText().toString().trim())) {
            edtCommentMessage.setError("?");
            return false;
        }
        return true;
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
                uploadDataInPortfolioMediaApi(new File(uri.getPath()));
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
                    imageStoragePath = CameraUtils.getPath(ChatActivity.this, data.getData());
                    File file = new File(imageStoragePath);
                    uploadDataInPortfolioMediaApi(file);
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


    /**
     * Called when a swipe gesture triggers a refresh.
     */
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