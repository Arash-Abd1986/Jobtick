package com.jobtick.android.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.activities.ChatActivity
import com.jobtick.android.activities.ChatSearchActivity
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.adapers.InboxListAdapter
import com.jobtick.android.models.ConversationModel
import com.jobtick.android.pagination.PaginationListener
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.Helper
import com.jobtick.android.utils.SessionManager
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.*
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import com.pusher.client.util.HttpAuthorizer
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class InboxFragmentOpens : Fragment(), InboxListAdapter.OnItemClickListener, OnRefreshListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener {
    private var dashboardActivity: DashboardActivity? = null
    private var chatList: RecyclerView? = null
    private var swipeRefresh: SwipeRefreshLayout? = null
    private var sessionManager: SessionManager? = null
    private var adapter: InboxListAdapter? = null
    private var currentPage = PaginationListener.PAGE_START
    private var isLastPageItems = false
    private var totalPage = 0
    private var isLoadingItems = false
    private var toolbar: Toolbar? = null
    private var queryParameter = ""
    private var pusher: Pusher? = null
    private var conversationId = 0
    private var ivNotification: ImageView? = null
    private var toolbarTitle: TextView? = null
    private var noMessages: LinearLayout? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_inbox, container, false)
        noMessages = view.findViewById(R.id.no_messages_container)
        chatList = view.findViewById(R.id.recycler_view)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        extras
        return view
    }

    private val extras: Unit
        get() {
            val bundle = arguments
            if (bundle != null) {
                conversationId = bundle.getInt(ConstantKey.PUSH_CONVERSATION_ID)
            }
        }

    @SuppressLint("SetTextI18n", "RtlHardcoded")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(context)
        initToolbar()
        initPusher()
        initComponents()
        fetchData()
    }

    private fun initComponents() {
        swipeRefresh!!.setOnRefreshListener(this)
        chatList!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        chatList!!.layoutManager = layoutManager
        adapter = InboxListAdapter(dashboardActivity, ArrayList())
        chatList!!.adapter = adapter
        adapter!!.setOnItemClickListener(this)
        swipeRefresh!!.isRefreshing = true
        chatList!!.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                isLoadingItems = true
                currentPage++
                fetchData()
            }

            override val isLastPage: Boolean
                get() = isLastPageItems
            override val isLoading: Boolean
                get() = isLoadingItems
        })
    }

    private fun initPusher() {
        val headers = HashMap<String, String>()
        headers["Authorization"] = sessionManager!!.tokenType + " " + sessionManager!!.accessToken
        headers["Content-Type"] = "application/x-www-form-urlencoded"
        headers["X-REQUESTED-WITH"] = "xmlhttprequest"
        headers["Accept"] = "application/json"
        val authorizer = HttpAuthorizer(Constant.BASE_URL + "broadcasting/auth")
        authorizer.setHeaders(headers)
        val options = PusherOptions() //.setEncrypted(true)
                .setCluster("us2")
                .setAuthorizer(authorizer)
        pusher = Pusher(getString(R.string.pusher_api_key), options)
        pusher!!.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange) {
                Timber.e("%s", change.currentState)
                println("State changed to " + change.currentState +
                        " from " + change.previousState)
                if (change.currentState == ConnectionState.CONNECTED) {
                    subscribeToChannel() //run kro ok
                    subscribeToPresence()
                }
            }

            override fun onError(message: String?, code: String?, e: Exception?) {
                //  System.out.println("There was a problem connecting!");
            }
        }, ConnectionState.ALL)
    }

    private fun initToolbar() {
        dashboardActivity = requireActivity() as DashboardActivity
        if (dashboardActivity == null) return
        toolbar = dashboardActivity!!.findViewById(R.id.toolbar)
        toolbar!!.menu.clear()
        toolbar!!.inflateMenu(R.menu.menu_new_task)
        toolbar!!.menu.findItem(R.id.action_search).isVisible = false
        toolbar!!.menu.findItem(R.id.action_search_chat).isVisible = true
        toolbar!!.menu.findItem(R.id.action_search_chat).setOnMenuItemClickListener(null)
        toolbar!!.menu.findItem(R.id.action_search_chat).setOnMenuItemClickListener {
            val intent = Intent(context, ChatSearchActivity::class.java)
            startActivity(intent)
            false
        }
        ivNotification = dashboardActivity!!.findViewById(R.id.ivNotification)
        ivNotification!!.visibility = View.GONE
        toolbarTitle = dashboardActivity!!.findViewById(R.id.toolbar_title)
        toolbarTitle!!.visibility = View.VISIBLE
        toolbarTitle!!.setText(R.string.chat)
        toolbarTitle!!.textSize = 20f
        toolbarTitle!!.typeface = ResourcesCompat.getFont(requireActivity(), R.font.roboto_medium)
        toolbar!!.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.backgroundLightGrey))
        val params = Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.START
        toolbarTitle!!.layoutParams = params
        toolbar!!.navigationIcon = null
    }

    private fun subscribeToPresence() {
        try {
            pusher!!.subscribePresence("presence-userStatus",
                    object : PresenceChannelEventListener {
                        override fun onUsersInformationReceived(channelName: String, users: Set<User>) {
                            val integerArrayList = ArrayList<Int>()
                            for (user in users) {
                                integerArrayList.add(user.id.toInt())
                            }
                            adapter!!.setOnlineStatus(integerArrayList)
                            Timber.e("%s", integerArrayList.size)
                        }

                        override fun userSubscribed(channelName: String, user: User) {
                            adapter!!.addNewSubscribe(user.id.toInt())
                            Timber.e(user.toString())
                        }

                        override fun userUnsubscribed(channelName: String, user: User) {
                            adapter!!.addNewUnSubscribe(user.id.toInt())
                            Timber.e(user.toString())
                        }

                        override fun onAuthenticationFailure(message: String?, e: Exception) {
                            Timber.e(message)
                        }

                        override fun onSubscriptionSucceeded(channelName: String) {
                            Timber.e(channelName)
                        }

                        override fun onEvent(event: PusherEvent?) {
                            Timber.e(event.toString())
                        }
                    })
        } catch (ignored: Exception) {
        }
    }

    private fun subscribeToChannel() {
        try {
            //its json response not string
            pusher!!.subscribePrivate("private-user." + sessionManager!!.userAccount.id,
                    object : PrivateChannelEventListener {
                        override fun onAuthenticationFailure(message: String?, e: Exception) {
                            val cpm = pusher!!.connection
                            Timber.e(message)
                            Timber.e(e.toString())
                            Timber.e(cpm.socketId)
                        }

                        override fun onSubscriptionSucceeded(channelName: String?) {
                            Timber.e(channelName) //its json response not string
                        }

                        override fun onEvent(event: PusherEvent?) {
                            Timber.e(event.toString())
                            try {
                                val jsonObject = JSONObject(event!!.data)
                                val conversationModel = ConversationModel(dashboardActivity).getJsonToModel(jsonObject,
                                        dashboardActivity)
                                adapter!!.getEventCall(conversationModel)
                                Timber.e(jsonObject.toString())
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            println("Received event with data: $event")
                        }
                    }, "message.sent")
        } catch (ignored: Exception) {
        }
    }

    private fun fetchData() {
        val items = ArrayList<ConversationModel>()
        Helper.closeKeyboard(dashboardActivity)
        val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.URL_CHAT +
                "/conversations/open" + "?page=" + currentPage + "&query=" + queryParameter,
        Response.Listener { response: String? ->
            Timber.e(response)
            try {
                Log.d("inboxData", response.toString())
                val jsonObject = JSONObject(response!!)
                Timber.e(jsonObject.toString())
                if (!jsonObject.has("data")) {
                    dashboardActivity!!.showToast("some went to wrong", dashboardActivity)
                    return@Listener
                }
                val jsonArrayData = jsonObject.getJSONArray("data")
                run {
                    var i = 0
                    while (jsonArrayData.length() > i) {
                        val jsonObjectConversation = jsonArrayData.getJSONObject(i)
                        val conversationModel = ConversationModel(dashboardActivity).getJsonToModel(jsonObjectConversation,
                                dashboardActivity)
                        items.add(conversationModel)
                        i++
                    }
                }
                if (jsonObject.has("meta") && !jsonObject.isNull("meta")) {
                    val jsonObjectMeta = jsonObject.getJSONObject("meta")
                    totalPage = jsonObjectMeta.getInt("last_page")
                    Constant.PAGE_SIZE = jsonObjectMeta.getInt("per_page")
                }

                /*
                         *manage progress view
                         */if (currentPage != PaginationListener.PAGE_START) adapter!!.removeLoading()
                if (items.size <= 0) {
                    noMessages!!.visibility = View.VISIBLE
                    chatList!!.visibility = View.GONE
                } else {
                    noMessages!!.visibility = View.GONE
                    chatList!!.visibility = View.VISIBLE
                }
                adapter!!.addItems(items)
                var i = 0
                while (i < items.size) {
                    if (items[i].id == conversationId) {
                        swipeRefresh!!.isRefreshing = false
                        dashboardActivity!!.hideProgressDialog()
                        onItemClick(requireView(), items[i], i, "parent_layout")
                        conversationId = 0
                        break
                    }
                    i++
                }
                swipeRefresh!!.isRefreshing = false
                // check weather is last page or not
                if (currentPage < totalPage) {
                    adapter!!.addLoading()
                } else {
                    isLastPageItems = true
                }
                isLoadingItems = false
            } catch (e: JSONException) {
                dashboardActivity!!.hideProgressDialog()
                Timber.e(e.toString())
                e.printStackTrace()
            }
        },
                Response.ErrorListener { error: VolleyError ->
                    swipeRefresh!!.isRefreshing = false
                    dashboardActivity!!.errorHandle1(error.networkResponse)
                }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["Authorization"] = "Bearer " + sessionManager!!.accessToken
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(dashboardActivity)
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
    }

    override fun onRefresh() {
        swipeRefresh!!.isRefreshing = true
        currentPage = PaginationListener.PAGE_START
        isLastPageItems = false
        adapter!!.clear()
        fetchData()
    }

    override fun onItemClick(view: View, obj: ConversationModel, position: Int, action: String) {
        if (action.equals("parent_layout", ignoreCase = true)) {
            if (obj.unseenCount != 0) {
                adapter!!.setUnSeenCountZero(position)
            }
            val intent = Intent(dashboardActivity, ChatActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(ConstantKey.CONVERSATION, obj)
            intent.putExtras(bundle)
            startActivityForResult(intent, ConstantKey.RESULTCODE_PRIVATE_CHAT)
        }
    }

    override fun onResume() {
        pusher!!.connect()
        super.onResume()
    }

    override fun onDestroy() {
        try {
            pusher!!.disconnect()
        }catch (e:Exception){
            e.printStackTrace()
        }
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ConstantKey.RESULTCODE_PRIVATE_CHAT) {
            if (data != null) {
                val bundle = data.extras
                if (bundle != null) {
                    if (bundle.getBoolean(ConstantKey.PRIVATE_CHAT)) {
                        onRefresh()
                    }
                }
            }
        }
    }

    override fun onClose(): Boolean {
        queryParameter = ""
        onRefresh()
        return true
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        queryParameter = query
        onRefresh()
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        return false
    }
}