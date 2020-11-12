package com.jobtick.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
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

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jobtick.EditText.EditTextMedium;
import com.jobtick.R;
import com.jobtick.activities.ChatActivity;
import com.jobtick.activities.DashboardActivity;
import com.jobtick.adapers.InboxListAdapter;
import com.jobtick.models.ConversationModel;
import com.jobtick.pagination.PaginationListener;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.Helper;
import com.jobtick.utils.SessionManager;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.jobtick.pagination.PaginationListener.PAGE_START;
import static com.jobtick.utils.ConstantKey.PUSH_CONVERSATION_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class InboxFragment extends Fragment implements InboxListAdapter.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    DashboardActivity dashboardActivity;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_search_categories)
    EditTextMedium edtSearchCategories;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
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

    ImageView ivNotification;
    TextView toolbar_title;
    private LottieAnimationView lottieAnim;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        ButterKnife.bind(this, view);
        lottieAnim=view.findViewById(R.id.lottieAnimationView);
        dashboardActivity = (DashboardActivity) getActivity();
        Bundle bundle = getArguments();
        if (bundle != null) {
            conversationId = bundle.getInt(PUSH_CONVERSATION_ID);
        }
        return view;
    }

    @SuppressLint({"SetTextI18n", "RtlHardcoded"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(dashboardActivity);


        dashboardActivity = (DashboardActivity) getActivity();
        if (dashboardActivity != null) {
            toolbar = dashboardActivity.findViewById(R.id.toolbar);
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_new_task);
            toolbar.getMenu().findItem(R.id.action_search).setVisible(true);
            ivNotification = dashboardActivity.findViewById(R.id.ivNotification);
            ivNotification.setVisibility(View.GONE);
            toolbar_title = dashboardActivity.findViewById(R.id.toolbar_title);
            toolbar_title.setVisibility(View.VISIBLE);
            toolbar_title.setText("Chat");

            toolbar_title.setTypeface(ResourcesCompat.getFont(getContext(), R.font.poppins_medium));
            toolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey_100));
            androidx.appcompat.widget.Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT,
                    Toolbar.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.LEFT;
            toolbar_title.setLayoutParams(params);

            toolbar.post(() -> {
                Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, null);
                toolbar.setNavigationIcon(d);
            });

        }

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_search) {
                Menu menu = toolbar.getMenu();
                searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
                searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
                searchView.setOnQueryTextListener(InboxFragment.this);
                searchView.setOnCloseListener(InboxFragment.this);

                /*    searchView.findViewById(R.id.search_close_btn)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d("called","this is called.");
                                    queryParameter="";
                                    onRefresh();
                                    searchView.setQuery("",false);
                                    searchView.setIconified(true);

                                }
                            });*/

                if (item.collapseActionView()) {
                    Timber.e("Called");
                }
            }
            return false;
        });

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


        swipeRefresh.setOnRefreshListener(this);
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(dashboardActivity);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new InboxListAdapter(dashboardActivity, new ArrayList<>());
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        swipeRefresh.setRefreshing(true);
        doApiCall();
        /*
         * add scroll listener while user reach in bottom load more will call
         */
        recyclerView.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                doApiCall(); }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        pusher = new Pusher("31c5e7256697a01d331a", options);

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
                System.out.println("There was a problem connecting!");
            }
        }, ConnectionState.ALL);
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


    private void doApiCall() {

        ArrayList<ConversationModel> items = new ArrayList<>();
        Helper.closeKeyboard(dashboardActivity);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_CHAT +
                "/conversations" + "?page=" + currentPage + "&query=" + queryParameter,
                response -> {
                    Timber.e(response);
                    // categoryArrayList.clear();
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
                            lottieAnim.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            lottieAnim.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

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
        doApiCall();
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
