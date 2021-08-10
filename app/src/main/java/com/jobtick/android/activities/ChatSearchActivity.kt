package com.jobtick.android.activities

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.Volley
import com.jobtick.android.R
import com.jobtick.android.adapers.InboxListAdapter
import com.jobtick.android.models.ConversationModel
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.Helper
import com.jobtick.android.viewmodel.ChatSearchViewModel
import org.json.JSONException
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class ChatSearchActivity : ActivityBase(), InboxListAdapter.OnItemClickListener, OnEditorActionListener {

    private lateinit var etSearch: EditText
    private lateinit var pbLoading: ProgressBar
    private lateinit var ivBack: ImageView
    private var adapter: InboxListAdapter? = null
    private lateinit var noMessages: RelativeLayout
    private lateinit var chatList: RecyclerView
    private lateinit var chatSearchViewModel: ChatSearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_search)
        initIDS()
        initVM()
        initComponents()
    }

    private fun initVM() {
        chatSearchViewModel = ViewModelProvider(this).get(ChatSearchViewModel::class.java)
        var items: ArrayList<ConversationModel>
        chatSearchViewModel.getMessageResponse().observe(this, androidx.lifecycle.Observer {
            try {
                items = ArrayList()
                pbLoading.visibility = View.GONE
                chatList.visibility = View.VISIBLE
                val jsonObject = it
                Timber.e(jsonObject.toString())
                if (!jsonObject.has("data")) {
                    showToast("some went to wrong", this)
                    return@Observer
                }
                val jsonArrayData = jsonObject.getJSONArray("data")
                var i = 0
                while (jsonArrayData.length() > i) {
                    val jsonObjectConversation = jsonArrayData.getJSONObject(i)
                    val conversationModel = ConversationModel(this).getJsonToModel(jsonObjectConversation,
                            this)
                    items.add(conversationModel)
                    i++
                }
                adapter!!.clear()
                adapter!!.addItems(items)
                if (items.size <= 0) {
                    noMessages.visibility = View.VISIBLE
                    chatList.visibility = View.GONE
                } else {
                    noMessages.visibility = View.GONE
                    chatList.visibility = View.VISIBLE
                }
            } catch (e: JSONException) {
                hideProgressDialog()
                Timber.e(e.toString())
                e.printStackTrace()
            }
        })
    }

    private fun initIDS() {
        etSearch = findViewById(R.id.edt_search_categoreis)
        pbLoading = findViewById(R.id.pbLoading)
        ivBack = findViewById(R.id.iv_back)
        noMessages = findViewById(R.id.no_messages_container)
        chatList = findViewById(R.id.list)
    }

    override fun onItemClick(view: View, obj: ConversationModel, position: Int, action: String) {
        if (action.equals("parent_layout", ignoreCase = true)) {
            if (obj.unseenCount != 0) {
                adapter!!.setUnSeenCountZero(position)
            }
            val intent = Intent(this, ChatActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(ConstantKey.CONVERSATION, obj)
            intent.putExtras(bundle)
            startActivityForResult(intent, ConstantKey.RESULTCODE_PRIVATE_CHAT)
        }
    }

    private fun initComponents() {

        val layoutManager = LinearLayoutManager(this)
        chatList.layoutManager = layoutManager
        adapter = InboxListAdapter(this, ArrayList())
        chatList.adapter = adapter
        adapter!!.setOnItemClickListener(this)
        etSearch.setOnEditorActionListener(this)
        etSearch.onFocusChangeListener = OnFocusChangeListener { v: View?, hasFocus: Boolean -> if (hasFocus) noMessages.visibility = View.GONE }
        ivBack.setOnClickListener { v: View? -> super.onBackPressed() }
    }


    private fun fetchData(query: String) {
        pbLoading.visibility = View.VISIBLE
        noMessages.visibility = View.GONE
        chatList.visibility = View.GONE
        Helper.closeKeyboard(this)

        chatSearchViewModel.getMessages(sessionManager.accessToken, Volley.newRequestQueue(this), query)
        chatSearchViewModel.getError().observe(this, androidx.lifecycle.Observer { jsonObject ->
            pbLoading.visibility = View.GONE
            showToast("Something Went Wrong", this@ChatSearchActivity)
            pbLoading.visibility = View.GONE

        })

    }

    override fun onEditorAction(textView: TextView, actionId: Int, keyEvent: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            //set adapter to online mode (previewMode will be false)
            fetchData(etSearch.text.toString())
            etSearch.clearFocus()
            pbLoading.visibility = View.VISIBLE
            return true
        }
        return false
    }
}