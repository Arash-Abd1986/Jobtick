package com.jobtick.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jobtick.android.R;
import android.annotation.SuppressLint;

import com.jobtick.android.activities.ChatActivity;
import com.jobtick.android.activities.ChatSearchActivity;
import com.jobtick.android.activities.DashboardActivity;
import com.jobtick.android.adapers.InboxListAdapter;
import com.jobtick.android.models.ConversationModel;
import com.jobtick.android.pagination.PaginationListener;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.ConstantKey;
import com.jobtick.android.utils.Helper;
import com.jobtick.android.utils.SessionManager;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

import static com.jobtick.android.pagination.PaginationListener.PAGE_START;
import static com.jobtick.android.utils.ConstantKey.PUSH_CONVERSATION_ID;

public class InboxFragment extends Fragment implements InboxListAdapter.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private DashboardActivity dashboardActivity;
    private RecyclerView chatList;
    private SwipeRefreshLayout swipeRefresh;
    private SessionManager sessionManager;
    private InboxListAdapter adapter;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;
    private Toolbar toolbar;
    private SearchView searchView;
    private String queryParameter = "";

    public InboxFragment() {
        // Required empty public constructor
    }

    private Pusher pusher;
    private int conversationId;

    private ImageView ivNotification;
    private TextView toolbar_title;
    private LinearLayout noMessages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        noMessages = view.findViewById(R.id.no_messages_container);
        chatList = view.findViewById(R.id.recycler_view);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        getExtras();
        return view;
    }

    private void getExtras() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            conversationId = bundle.getInt(PUSH_CONVERSATION_ID);
        }
    }

    @SuppressLint({"SetTextI18n", "RtlHardcoded"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(getContext());

        initToolbar();
        initPusher();
        initComponents();
        fetchData();
    }

    private void initComponents() {
        swipeRefresh.setOnRefreshListener(this);
        chatList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        chatList.setLayoutManager(layoutManager);
        adapter = new InboxListAdapter(dashboardActivity, new ArrayList<>());
        chatList.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        swipeRefresh.setRefreshing(true);
        chatList.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                fetchData();
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
    }

    private void initPusher() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("X-REQUESTED-WITH", "xmlhttprequest");
        headers.put("Accept", "application/json");
        HttpAuthorizer authorizer = new HttpAuthorizer(Constant.BASE_URL + "broadcasting/auth");

        authorizer.setHeaders(headers);
        PusherOptions options = new PusherOptions()
                //.setEncrypted(true)
                .setCluster("us2")
                .setAuthorizer(authorizer);

        pusher = new Pusher(getString(R.string.pusher_api_key), options);

        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                Timber.e("%s", change.getCurrentState());
                System.out.println("State changed to " + change.getCurrentState() +
                        " from " + change.getPreviousState());
                if (change.getCurrentState() == ConnectionState.CONNECTED) {
                    subscribeToChannel(); //run kro ok
                    subscribeToPresence();
                }
            }

            @Override
            public void onError(String message, String code, Exception e) {
                //  System.out.println("There was a problem connecting!");
            }
        }, ConnectionState.ALL);
    }

    private void initToolbar() {
        dashboardActivity = (DashboardActivity) requireActivity();
        if (dashboardActivity == null) return;
        toolbar = dashboardActivity.findViewById(R.id.toolbar);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_new_task);
        toolbar.getMenu().findItem(R.id.action_search).setVisible(false);
        toolbar.getMenu().findItem(R.id.action_search_chat).setVisible(true);
        toolbar.getMenu().findItem(R.id.action_search_chat).setOnMenuItemClickListener(null);
        toolbar.getMenu().findItem(R.id.action_search_chat).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getContext(), ChatSearchActivity.class);
                startActivity(intent);
                return false;
            }
        });
        ivNotification = dashboardActivity.findViewById(R.id.ivNotification);
        ivNotification.setVisibility(View.GONE);
        toolbar_title = dashboardActivity.findViewById(R.id.toolbar_title);
        toolbar_title.setVisibility(View.VISIBLE);
        toolbar_title.setText(R.string.chat);
        toolbar_title.setTextSize(20f);
        toolbar_title.setTypeface(ResourcesCompat.getFont(requireActivity(), R.font.roboto_semi_bold));
        toolbar.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.backgroundLightGrey));
        androidx.appcompat.widget.Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.START;
        toolbar_title.setLayoutParams(params);
    }

    private void subscribeToPresence() {
        try {
            PresenceChannel presenceChannel = pusher.subscribePresence("presence-userStatus",
                    new PresenceChannelEventListener() {
                        @Override
                        public void onUsersInformationReceived(String channelName, Set<User> users) {
                            ArrayList<Integer> integerArrayList = new ArrayList<>();
                            for (User user : users) {
                                integerArrayList.add(Integer.parseInt(user.getId()));
                            }
                            adapter.setOnlineStatus(integerArrayList);
                            Timber.e("%s", integerArrayList.size());
                        }

                        @Override
                        public void userSubscribed(String channelName, User user) {
                            adapter.addNewSubscribe(Integer.parseInt(user.getId()));
                            Timber.e(user.toString());
                        }

                        @Override
                        public void userUnsubscribed(String channelName, User user) {
                            adapter.addNewUnSubscribe(Integer.parseInt(user.getId()));
                            Timber.e(user.toString());
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

        } catch (Exception ignored) {

        }
    }

    protected void subscribeToChannel() {

        try {
            //its json response not string
            PrivateChannel channel = pusher.subscribePrivate("private-user." + sessionManager.getUserAccount().getId(),
                    new PrivateChannelEventListener() {
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
                                ConversationModel conversationModel = new ConversationModel(dashboardActivity).getJsonToModel(jsonObject,
                                        dashboardActivity);
                                adapter.getEventCall(conversationModel);
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

    private void fetchData() {

        ArrayList<ConversationModel> items = new ArrayList<>();
        Helper.closeKeyboard(dashboardActivity);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_CHAT +
                "/conversations" + "?page=" + currentPage + "&query=" + queryParameter,
                response -> {
                    Timber.e(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (!jsonObject.has("data")) {
                            dashboardActivity.showToast("some went to wrong", dashboardActivity);
                            return;
                        }
                        JSONArray jsonArray_data = jsonObject.getJSONArray("data");
                        for (int i = 0; jsonArray_data.length() > i; i++) {
                            JSONObject jsonObject_conversation = jsonArray_data.getJSONObject(i);
                            ConversationModel conversationModel = new ConversationModel(dashboardActivity).getJsonToModel(jsonObject_conversation,
                                    dashboardActivity);
                            items.add(conversationModel);
                        }

                        if (jsonObject.has("meta") && !jsonObject.isNull("meta")) {
                            JSONObject jsonObject_meta = jsonObject.getJSONObject("meta");
                            totalPage = jsonObject_meta.getInt("last_page");
                            Constant.PAGE_SIZE = jsonObject_meta.getInt("per_page");
                        }

                        /*
                         *manage progress view
                         */
                        if (currentPage != PAGE_START)

                            adapter.removeLoading();
                        if (items.size() <= 0) {
                            noMessages.setVisibility(View.VISIBLE);
                            chatList.setVisibility(View.GONE);
                        } else {
                            noMessages.setVisibility(View.GONE);
                            chatList.setVisibility(View.VISIBLE);

                        }

                        adapter.addItems(items);

                        for (int i = 0; i < items.size(); i++) {
                            if (items.get(i).getId() == conversationId) {
                                onItemClick(null
                                        , items.get(i), i, "parent_layout");
                                conversationId = 0;
                                break;
                            }
                        }
                        swipeRefresh.setRefreshing(false);
                        // check weather is last page or not
                        if (currentPage < totalPage) {
                            adapter.addLoading();
                        } else {
                            isLastPage = true;
                        }
                        isLoading = false;
                    } catch (JSONException e) {
                        dashboardActivity.hideProgressDialog();
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                    }
                },
                error -> {
                    swipeRefresh.setRefreshing(false);
                    dashboardActivity.errorHandle1(error.networkResponse);
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
        RequestQueue requestQueue = Volley.newRequestQueue(dashboardActivity);
        requestQueue.add(stringRequest);
        Timber.e(stringRequest.getUrl());
    }

    @Override
    public void onRefresh() {
        swipeRefresh.setRefreshing(true);
        currentPage = PAGE_START;
        isLastPage = false;
        adapter.clear();
        fetchData();
    }


    @Override
    public void onItemClick(View view, ConversationModel obj, int position, String action) {
        if (action.equalsIgnoreCase("parent_layout")) {
            if (obj.getUnseenCount() != 0) {
                adapter.setUnSeenCountZero(position);
            }
            Intent intent = new Intent(dashboardActivity, ChatActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable(ConstantKey.CONVERSATION, obj);
            intent.putExtras(bundle);
            startActivityForResult(intent, ConstantKey.RESULTCODE_PRIVATE_CHAT);
        }
    }

    @Override
    public void onResume() {
        pusher.connect();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        pusher.disconnect();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantKey.RESULTCODE_PRIVATE_CHAT) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    if (bundle.getBoolean(ConstantKey.PRIVATE_CHAT)) {
                        onRefresh();

                    }
                }
            }
        }
    }

    @Override
    public boolean onClose() {
        queryParameter = "";
        onRefresh();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        queryParameter = query;
        onRefresh();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
