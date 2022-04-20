package com.jobtick.android.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.adapers.PreviewTaskAdapter
import com.jobtick.android.adapers.TaskListAdapterV2
import com.jobtick.android.fragments.VoiceSearchBottomSheet
import com.jobtick.android.models.PreviewTaskModel
import com.jobtick.android.models.PreviewTaskSetModel
import com.jobtick.android.models.response.myjobs.Data
import com.jobtick.android.models.response.myjobs.MyJobsResponse
import com.jobtick.android.pagination.PaginationListener
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.Helper
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.voicerecorder.RippleView
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

class SearchTaskActivity :
    ActivityBase(),
    OnEditorActionListener,
    VoiceSearchBottomSheet.VoiceRecorderInterface,
    TaskListAdapterV2.OnItemClickListener,
    PreviewTaskAdapter.OnItemClickListener<PreviewTaskModel?> {
    var ivBack: ImageView? = null
    var recyclerView: RecyclerView? = null
    var emptySearch: RelativeLayout? = null
    var edtSearch: EditText? = null
    var rvMic: RippleView? = null
    private val MY_PERMISSIONS_RECORD_AUDIO = 123
    var btnVoice: ImageView? = null
    val voiceSearchBottomSheet = VoiceSearchBottomSheet()

    private lateinit var sessionManagerT: SessionManager
    private var adapter: TaskListAdapterV2? = null
    private var previewTaskSetModel: PreviewTaskSetModel? = null
    private var previewTaskAdapter: PreviewTaskAdapter? = null
    private var currentPage = PaginationListener.PAGE_START
    private val isLastPageItem = false
    private var totalPage = 10
    private var totalItem = 10
    private var isLoadingItem = false
    private var isFromMyJobs = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_all)
        initIDS()
        voiceSearchBottomSheet.voiceRecorderInterface = this
        isFromMyJobs = intent.getBooleanExtra(ConstantKey.FROM_MY_JOBS_WITH_LOVE, false)
        //  RelativeLayout emptySearch = findViewById(R.id.empty_search);
        sessionManagerT = SessionManager(this)
        if (isFromMyJobs) {
            edtSearch!!.setHint(R.string.search_your_jobs)
        } else {
            edtSearch!!.setHint(R.string.search_jobs)
        }
        edtSearch!!.requestFocus()
        edtSearch!!.performClick()
        edtSearch!!.setOnEditorActionListener(this)
        setPreviewAdapter()
        searchWithVoice()
        if (intent.hasExtra("IsVoice")) {
            btnVoice!!.performClick()
        }
    }

    private fun initIDS() {
        ivBack = findViewById(R.id.iv_back)
        recyclerView = findViewById(R.id.list)
        emptySearch = findViewById(R.id.empty_search)
        edtSearch = findViewById(R.id.edt_search_categoreis)
        btnVoice = findViewById(R.id.btnVoice)
        rvMic = findViewById(R.id.rv_mic)
        ivBack!!.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onEditorAction(textView: TextView?, actionId: Int, keyEvent: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            // set adapter to online mode (previewMode will be false)
            setOnlineAdapter()
            setLoadMoreListener()
            doApiCall()
            return true
        }
        return false
    }

    private fun doApiCall() {
        var queryParameter = ""
        queryParameter = edtSearch!!.text.toString()
        Helper.closeKeyboard(this)
        var url = Constant.URL_TASKS_v2 + "?search_query=" + queryParameter + "&page=" + currentPage
        if (isFromMyJobs) url =
            Constant.URL_TASKS_v2 + "&search_query=" + queryParameter + "&page=" + currentPage
        val stringRequest: StringRequest = object : StringRequest(
            Method.GET, url,
            Response.Listener { response: String? ->
                Timber.e(response)
                try {
                    val jsonObject = JSONObject(response!!)
                    Timber.e(jsonObject.toString())
                    val gson = Gson()
                    val (_, data, _, _, _, _, _, _, _, per_page, _, _, total) = gson.fromJson(
                        jsonObject.toString(),
                        MyJobsResponse::class.java
                    )
                    if (data == null) {
                        showToast("some went to wrong", this)
                        return@Listener
                    }
                    totalItem = total!!
                    Constant.PAGE_SIZE = per_page!!
                    totalPage = total
                    if (data.size <= 0) {
                        emptySearch!!.visibility = View.VISIBLE
                        recyclerView!!.visibility = View.GONE
                    } else {
                        recyclerView!!.visibility = View.VISIBLE
                        emptySearch!!.visibility = View.GONE
                    }
                    adapter!!.addItems(data, totalItem)
                } catch (e: JSONException) {
                    hideProgressDialog()
                    Timber.e(e.toString())
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error: VolleyError -> errorHandle1(error.networkResponse) }
        ) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["Authorization"] = "Bearer " + sessionManagerT.accessToken
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            0, -1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
    }

    private fun setPreviewAdapter() {
        previewTaskSetModel =
            sessionManagerT.getPreviewTaskModel(SearchTaskActivity::class.java, isFromMyJobs)
        if (previewTaskSetModel == null) previewTaskSetModel = PreviewTaskSetModel()
        previewTaskAdapter = PreviewTaskAdapter(ArrayList(previewTaskSetModel!!.previewSet))
        recyclerView!!.adapter = previewTaskAdapter
        recyclerView!!.setHasFixedSize(true)
        previewTaskAdapter!!.setOnItemClickListener(this)
    }

    private fun setOnlineAdapter() {
        adapter = TaskListAdapterV2(ArrayList(), sessionManager!!.userAccount, false)
        recyclerView!!.adapter = adapter
        adapter!!.setOnItemClickListener(this)
    }

    private var previewMode = true
    private fun setLoadMoreListener() {
        if (!previewMode) return
        recyclerView!!.addOnScrollListener(object :
                PaginationListener((recyclerView!!.layoutManager as LinearLayoutManager?)!!) {
                override fun loadMoreItems() {
                    isLoadingItem = true
                    currentPage++
                    doApiCall()
                }

                override val isLastPage: Boolean
                    get() = isLastPageItem
                override val isLoading: Boolean
                    get() = isLoadingItem
            })
        previewMode = false
    }

    private fun showKeyboard(editText: EditText) {
        editText.post {
            val imm = baseContext
                .getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, 0)
        }
    }

    override fun onItemClick(view: View?, obj: Data?, position: Int, action: String?) {
        val previewTaskModel =
            PreviewTaskModel(obj!!.id!!, obj.title, sessionManagerT.userAccount.id, obj.slug)
        onItemClick(view, previewTaskModel, position)
    }

    override fun onItemClick(view: View?, obj: PreviewTaskModel?, position: Int) {
        previewTaskSetModel!!.addItem(obj)
        val intent = Intent(this, TaskDetailsActivity::class.java)
        val bundle = Bundle()
        bundle.putString(ConstantKey.SLUG, obj!!.slug)
        bundle.putBoolean(ConstantKey.IS_FROM_SEARCH, true)
        //   bundle.putInt(ConstantKey.USER_ID, obj.getUserId());
        intent.putExtras(bundle)
        startActivity(intent)
        sessionManagerT.setPreviewTaskModel(
            previewTaskSetModel,
            SearchTaskActivity::class.java,
            isFromMyJobs
        )
    }

    private fun searchWithVoice() {
        btnVoice!!.setOnClickListener { view: View? ->
            if (ContextCompat.checkSelfPermission(
                    this@SearchTaskActivity,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@SearchTaskActivity,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    MY_PERMISSIONS_RECORD_AUDIO
                )
            } else {
                speechToText()
            }
        }
    }

    private fun speechToText() {
        voiceSearchBottomSheet.show(supportFragmentManager, null)
        //        btnVoice!!.startAnimation(AnimationUtils.loadAnimation(this@SearchTaskActivity, R.anim.video_icon_animate))
    }

    override fun onVoiceRecordResult(status: Boolean, result: String) {
        if (status) {
            edtSearch!!.setText(result)
            setOnlineAdapter()
            setLoadMoreListener()
            doApiCall()
            btnVoice!!.clearAnimation()
        } else {
            btnVoice!!.clearAnimation()
        }
    }
}
