package com.jobtick.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.jobtick.R;
import com.jobtick.TextView.TextViewRegular;
import com.jobtick.adapers.AttachmentAdapter;
import com.jobtick.adapers.MustHaveListAdapter;
import com.jobtick.adapers.OfferListAdapter;
import com.jobtick.adapers.QuestionListAdapter;
import com.jobtick.adapers.ShowMustHaveListAdapter;
import com.jobtick.cancellations.CancellationPosterActivity;
import com.jobtick.cancellations.CancellationRequestActivity;
import com.jobtick.cancellations.CancellationRequestSubmittedActivity;
import com.jobtick.cancellations.CancellationWorkerActivity;
import com.jobtick.fragments.ConfirmAskToReleaseBottomSheet;
import com.jobtick.fragments.ConfirmReleaseBottomSheet;
import com.jobtick.fragments.IncreaseBudgetBottomSheet;
import com.jobtick.fragments.IncreaseBudgetDeclineBottomSheet;
import com.jobtick.fragments.IncreaseBudgetNoticeBottomSheet;
import com.jobtick.fragments.RescheduleNoticeBottomSheetState;
import com.jobtick.fragments.TickerRequirementsBottomSheet;
import com.jobtick.interfaces.OnRequestAcceptListener;
import com.jobtick.interfaces.OnWidthDrawListener;
import com.jobtick.models.AttachmentModel;
import com.jobtick.models.BankAccountModel;
import com.jobtick.models.BillingAdreessModel;
import com.jobtick.models.ConversationModel;
import com.jobtick.models.DueTimeModel;
import com.jobtick.models.MustHaveModel;
import com.jobtick.models.OfferDeleteModel;
import com.jobtick.models.OfferModel;
import com.jobtick.models.QuestionModel;
import com.jobtick.models.TaskModel;
import com.jobtick.models.UserAccountModel;
import com.jobtick.models.review.ReviewModel;
import com.jobtick.retrofit.ApiClient;
import com.jobtick.utils.CameraUtils;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.Helper;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.ImageUtil;
import com.jobtick.utils.SessionManager;
import com.jobtick.utils.TimeHelper;
import com.jobtick.utils.Tools;
import com.jobtick.widget.ExtendedAlertBox;
import com.jobtick.widget.SpacingItemDecoration;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;

import static com.jobtick.activities.SavedTaskActivity.onRemoveSavedtasklistener;
import static com.jobtick.fragments.TickerRequirementsBottomSheet.Requirement;
import static com.jobtick.utils.Constant.ADD_ACCOUNT_DETAILS;
import static com.jobtick.utils.Constant.ADD_BILLING;
import static com.jobtick.utils.Constant.BASE_URL;
import static com.jobtick.utils.Constant.TASK_ASSIGNED;
import static com.jobtick.utils.Constant.TASK_CANCELLED;
import static com.jobtick.utils.Constant.TASK_CLOSED;
import static com.jobtick.utils.Constant.TASK_COMPLETE;
import static com.jobtick.utils.Constant.TASK_DRAFT;
import static com.jobtick.utils.Constant.TASK_OPEN;
import static com.jobtick.utils.Constant.TASK_PENDING;
import static com.jobtick.utils.Constant.URL_TASKS;

public class TaskDetailsActivity extends ActivityBase implements OfferListAdapter.OnItemClickListener,
        QuestionListAdapter.OnItemClickListener, AttachmentAdapter.OnItemClickListener, OnRequestAcceptListener, OnWidthDrawListener, ExtendedAlertBox.OnExtendedAlertButtonClickListener,
        RescheduleNoticeBottomSheetState.NoticeListener, IncreaseBudgetBottomSheet.NoticeListener,
        IncreaseBudgetNoticeBottomSheet.NoticeListener, IncreaseBudgetDeclineBottomSheet.NoticeListener,
        ConfirmAskToReleaseBottomSheet.NoticeListener, ConfirmReleaseBottomSheet.NoticeListener {
    @BindView(R.id.alert_box)
    ExtendedAlertBox alertBox;


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_completion_rate)
    TextView txtCompletionRate;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_status_open)
    TextView txtStatusOpen;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_status_assigned)
    TextView txtStatusAssigned;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_status_completed)
    TextView txtStatusCompleted;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_due_date)
    TextView txtDueDate;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_rating_value)
    TextView txtRatingValue;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_due_time)
    TextView txtDueTime;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_created_date)
    TextView txtCreatedDate;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_location)
    TextView txtLocation;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_map_pin)
    ImageView imgMapPin;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_description)
    TextView txtDescription;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_offer_count)
    TextView txtOfferCount;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.first_offer)
    TextView firstOffer;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.first_offer_lyt)
    LinearLayout firstOfferLyt;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_budget)
    TextView txtBudget;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_message)
    LinearLayout lytBtnMessage;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.llLocation)
    LinearLayout llLocation;
    //    @BindView(R.id.card_message)
//    CardView cardMessage;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.postedByLyt)
    RelativeLayout postedByLyt;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_make_an_offer)
    LinearLayout lytBtnMakeAnOffer;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.card_make_an_offer)
    CardView cardMakeAnOffer;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_view_offers)
    RecyclerView recyclerViewOffers;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.nested_scroll_view)
    NestedScrollView nestedScrollView;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_btn_text)
    TextView txtBtnText;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_offers_count)
    TextView txtOffersCount;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_view_all_offers)
    LinearLayout lytBtnViewAllOffers;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.card_view_all_offers)
    CardView cardViewAllOffers;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.card_offer_layout)
    CardView cardOfferLayout;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_waiting_for_offer)
    FrameLayout txtWaitingForOffer;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_avtar_worker)
    CircularImageView imgAvtarWorker;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_worker_name)
    TextView txtWorkerName;
    @BindView(R.id.lineOpen)
    View lineOpenState;
    //    @SuppressLint("NonConstantResourceId")
//    @BindView(R.id.card_private_chat)
//    CardView cardPrivateChat;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.card_assignee_layout)
    CardView cardAssigneeLayout;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_avtar_poster)
    CircularImageView imgAvtarPoster;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_poster_name)
    TextView txtPosterName;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_poster_location)
    TextView txtPosterLocation;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_poster_last_online)
    TextView txtPosterLastOnline;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_questions_count)
    TextView txtQuestionsCount;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_view_questions)
    RecyclerView recyclerViewQuestions;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_view_all_questions)
    LinearLayout lytBtnViewAllQuestions;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_view_all_questions)
    LinearLayout lytViewAllQuestions;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.card_questions_layout)
    LinearLayout cardQuestionsLayout;
    //   @BindView(R.id.img_avatar)
    //   CircularImageView imgAvatar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_comment)
    EditText edtComment;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_comment_send)
    ImageView lytBtnCommentSend;
    //   @BindView(R.id.lyt_comment)
    //  LinearLayout lytComment;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_view_question_attachment)
    RecyclerView recyclerViewQuestionAttachment;
    //   @BindView(R.id.card_comment_send)
    //   CardView cardCommentSend;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rlt_layout_action_data)
    RelativeLayout rltQuestionAdd;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_status_cancelled)
    TextView txtStatusCancelled;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_status_overdue)
    TextView txtStatusOverdue;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_status_reviewed)
    TextView txtStatusReviewed;
    private AdapterImageSlider adapterImageSlider;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.pager)
    ViewPager viewPager;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.layout_dots)
    LinearLayout layoutDots;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_budgets)
    TextView budget;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.card_cancel_background)
    CardView cardCancelBackground;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.card_cancelled)
    CardView cardCancelled;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.liAssign)
    LinearLayout liAssign;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.linearUserProfile)
    LinearLayout linearUserProfile;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.mustHaveLyt)
    CardView mustHaveLyt;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.mustHaveList)
    RecyclerView mustHaveList;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.bottom_sheet)
    FrameLayout bottom_sheet;

    public static TaskModel taskModel = new TaskModel();
    public static String isOfferQuestion = "";
    public static OfferModel offerModel;
    public static QuestionModel questionModel;
    public static OnRequestAcceptListener requestAcceptListener;
    public static OnWidthDrawListener widthDrawListener;


    public UserAccountModel userAccountModel;
    public BankAccountModel bankAccountModel;
    public BillingAdreessModel billingAdreessModel;

    private final ArrayList<AttachmentModel> dataList = new ArrayList<>();
    private final ArrayList<AttachmentModel> attachmentArrayList_question = new ArrayList<>();

    private String str_slug = "";
    private boolean isMyTask = false;
    boolean isFabHide = false;
    boolean isShowBookmarked = false;
    private OfferListAdapter offerListAdapter;
    private QuestionListAdapter questionListAdapter;
    private boolean noActionAvailable = false;
    private static final int GALLERY_PICKUP_IMAGE_REQUEST_CODE = 400;
    private AttachmentAdapter adapter;
    public int pushOfferID;
    public int pushQuestionID;
    private BottomSheetDialog mBottomSheetDialog;
    private BottomSheetBehavior mBehavior;
    private final HashMap<Requirement, Boolean> requirementState = new HashMap<>();

    private AlertType alertType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        ButterKnife.bind(this);
        alertBox.setOnExtendedAlertButtonClickListener(this);

        mBehavior = BottomSheetBehavior.from(bottom_sheet);
        requestAcceptListener = this;
        widthDrawListener = this;
        userAccountModel = new SessionManager(this).getUserAccount();

        initialStage();
    }

    @Override
    protected void getExtras() {
        super.getExtras();

        if (getIntent() == null || getIntent().getExtras() == null) {
            return;
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle.getString(ConstantKey.SLUG) != null) {
            str_slug = bundle.getString(ConstantKey.SLUG);
        }
        if (bundle.getInt(ConstantKey.USER_ID) != 0) {
            int userId = bundle.getInt(ConstantKey.USER_ID);
            SessionManager sessionManager = new SessionManager(this);
            isMyTask = userId == sessionManager.getUserAccount().getId();
        }
        if (bundle.getInt(ConstantKey.PUSH_OFFER_ID) != 0) {
            pushOfferID = bundle.getInt(ConstantKey.PUSH_OFFER_ID);
        }
        if (bundle.getInt(ConstantKey.PUSH_QUESTION_ID) != 0) {
            pushQuestionID = bundle.getInt(ConstantKey.PUSH_QUESTION_ID);
        }
    }

    private void initialStage() {
        //fl_task_details.setVisibility(View.VISIBLE);

        recyclerViewOffers.setVisibility(View.GONE);
        cardViewAllOffers.setVisibility(View.GONE);

        initToolbar();
        initComponentScroll();
        initOfferList();
        initQuestionList();
        getData();
    }

    private void initQuestionList() {
        recyclerViewQuestions.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(TaskDetailsActivity.this);
        recyclerViewQuestions.setLayoutManager(layoutManager);
        questionListAdapter = new QuestionListAdapter(TaskDetailsActivity.this, new ArrayList<>());
        recyclerViewQuestions.setAdapter(questionListAdapter);
        questionListAdapter.setOnItemClickListener(this);
    }

    private void initOfferList() {
        recyclerViewOffers.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(TaskDetailsActivity.this);
        recyclerViewOffers.setLayoutManager(layoutManager);

        offerListAdapter = new OfferListAdapter(TaskDetailsActivity.this, isMyTask, new ArrayList<>());
        recyclerViewOffers.setAdapter(offerListAdapter);
        offerListAdapter.setOnItemClickListener(this);
    }

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    private void initStatusTask(String status) {
        switch (status) {
            case Constant.TASK_ASSIGNED:
                txtStatusOpen.setSelected(false);
                txtStatusAssigned.setSelected(true);
                txtStatusCompleted.setSelected(false);
                txtStatusCancelled.setSelected(false);
                txtStatusOverdue.setSelected(false);
                txtStatusReviewed.setSelected(false);
                txtStatusOpen.setVisibility(View.VISIBLE);
                cardMakeAnOffer.setBackgroundTintList(ContextCompat.getColorStateList(TaskDetailsActivity.this,
                        R.color.colorTaskAssigned));
                txtStatusAssigned.setVisibility(View.VISIBLE);
                txtStatusCompleted.setVisibility(View.VISIBLE);
                txtStatusCancelled.setVisibility(View.GONE);
                txtStatusOverdue.setVisibility(View.GONE);
                txtStatusReviewed.setVisibility(View.GONE);
                txtStatusOpen.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_tab_primary_2dp));
                if (isMyTask) {
                    cardMakeAnOffer.setVisibility(View.VISIBLE);
                    txtBtnText.setText(ConstantKey.BTN_ASSIGNED);
                    toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_cancellation, true);
                    toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_report, false);
                    toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_increase_budget, true);
                    toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_reschedule, true);

//                    cardPrivateChat.setVisibility(View.VISIBLE);
                    //Cancellation

                } else {
                    lineOpenState.setVisibility(View.GONE);

                    // worker task
                    if (noActionAvailable) {
                        cardMakeAnOffer.setVisibility(View.GONE);
                        toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_report, false);
                        toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_cancellation, false);
                        toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_increase_budget, false);
                        toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_reschedule, false);

//                        cardPrivateChat.setVisibility(View.GONE);
                    } else {
                        cardMakeAnOffer.setVisibility(View.VISIBLE);
                        txtBtnText.setText(ConstantKey.BTN_ASK_TO_RELEASE_MONEY);
                        toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_copy, true);
                        toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_cancellation, true);
                        toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_increase_budget, true);
//                        cardPrivateChat.setVisibility(View.VISIBLE);
                        //Cancellation
                    }
                }
                toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_edit, false);
                toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_delete, false);

                cardOfferLayout.setVisibility(View.GONE);
//                cardMessage.setVisibility(View.VISIBLE);
                cardAssigneeLayout.setVisibility(View.VISIBLE);
                if (taskModel.getQuestions() != null && taskModel.getQuestions().size() != 0) {
                    //   rltQuestionAdd.setVisibility(View.GONE);
                } else {
                    cardQuestionsLayout.setVisibility(View.GONE);
                }
                txtBudget.setText("$" + taskModel.getAmount());
                break;
            case Constant.TASK_OPEN:
                txtStatusOpen.setSelected(true);
                txtStatusOpen.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_tab_primary_2dp));
                txtStatusOpen.setTextColor(ContextCompat.getColor(this, R.color.white));
                txtStatusAssigned.setSelected(false);
                txtStatusCompleted.setSelected(false);
                txtStatusCancelled.setSelected(false);
                txtStatusOverdue.setSelected(false);
                txtStatusReviewed.setSelected(false);
                txtStatusOpen.setVisibility(View.VISIBLE);
                txtStatusAssigned.setVisibility(View.VISIBLE);
                txtStatusCompleted.setVisibility(View.VISIBLE);
                txtStatusCancelled.setVisibility(View.GONE);
                txtStatusOverdue.setVisibility(View.GONE);
                txtStatusReviewed.setVisibility(View.GONE);
                lineOpenState.setVisibility(View.GONE);
                cardMakeAnOffer.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
                cardMakeAnOffer.setBackgroundTintList(ContextCompat.getColorStateList(TaskDetailsActivity.this,
                        R.color.colorPrimary));

                if (isMyTask) {
                    //poster task
                    toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_edit, true);
                    toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_delete, true);
                    toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_report, false);

                    cardMakeAnOffer.setVisibility(View.GONE);
                } else {
                    //worker task
                    toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_edit, false);
                    toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_delete, false);
                    toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_report, true);

                    cardMakeAnOffer.setVisibility(View.VISIBLE);
                    if (taskModel.getOfferSent()) {
                        txtBtnText.setText(ConstantKey.BTN_OFFER_PENDING);
                    } else {
                        txtBtnText.setText(ConstantKey.BTN_MAKE_AN_OFFER);
                    }
                }
                //toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_report, false);
                toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_cancellation, false);
                toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_reschedule, false);
                toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_increase_budget, false);
//                cardMessage.setVisibility(View.VISIBLE);
                cardAssigneeLayout.setVisibility(View.GONE);
//                cardPrivateChat.setVisibility(View.GONE);
                if (taskModel.getOffers().size() != 0) {
                    recyclerViewOffers.setVisibility(View.VISIBLE);
                    if(offerListAdapter!=null)
                        offerListAdapter.clear();
                    offerListAdapter.addItems(taskModel.getOffers());
                    for (int i = 0; i < taskModel.getOffers().size(); i++) {
                        if (taskModel.getOffers().get(i).getId() == pushOfferID) {
                            onItemOfferClick(null, taskModel.getOffers().get(i), i, "reply");
                            break;
                        }
                    }
                } else {
                    recyclerViewOffers.setVisibility(View.GONE);
                }
                cardOfferLayout.setVisibility(View.VISIBLE);
                cardQuestionsLayout.setVisibility(View.VISIBLE);
                txtBudget.setText("$" + taskModel.getBudget());
                break;
            case TASK_CANCELLED:
                cardMakeAnOffer.setVisibility(View.GONE);
                lineOpenState.setVisibility(View.GONE);
                cardMakeAnOffer.setBackgroundTintList(ContextCompat.getColorStateList(TaskDetailsActivity.this,
                        R.color.colorPrimary));
                txtStatusOpen.setSelected(false);
                txtStatusAssigned.setSelected(false);
                txtStatusCompleted.setSelected(false);
                txtStatusCancelled.setSelected(true);
                if (isMyTask) {
                    cardCancelled.setVisibility(View.VISIBLE);
                    cardCancelBackground.setVisibility(View.VISIBLE);
                }
                txtStatusOverdue.setSelected(false);
                txtStatusReviewed.setSelected(false);
                txtStatusOpen.setVisibility(View.VISIBLE);
                txtStatusAssigned.setVisibility(View.GONE);
                txtStatusCompleted.setVisibility(View.GONE);
                txtStatusCancelled.setVisibility(View.VISIBLE);
                txtStatusOverdue.setVisibility(View.GONE);
                txtStatusReviewed.setVisibility(View.GONE);

//                cardMessage.setVisibility(View.VISIBLE);
                cardAssigneeLayout.setVisibility(View.VISIBLE);
//                cardPrivateChat.setVisibility(View.VISIBLE);
                cardOfferLayout.setVisibility(View.GONE);
                cardQuestionsLayout.setVisibility(View.GONE);
                toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_edit, false);
                toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_delete, false);
                toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_cancellation, false);
                toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_increase_budget, false);
                toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_reschedule, false);
                toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_report, false);


                txtBudget.setText("$" + taskModel.getAmount());
                break;
            case Constant.TASK_OVERDUE:
            case Constant.TASK_COMPLETED:
                lineOpenState.setVisibility(View.VISIBLE);
                txtStatusOpen.setSelected(false);
                txtStatusAssigned.setSelected(false);
                txtStatusCompleted.setSelected(true);
                txtStatusCancelled.setSelected(false);
                txtStatusOverdue.setSelected(true);
                txtStatusReviewed.setSelected(false);
                txtStatusOpen.setVisibility(View.VISIBLE);
                txtStatusAssigned.setVisibility(View.VISIBLE);
                txtStatusCompleted.setVisibility(View.GONE);
                txtStatusCancelled.setVisibility(View.GONE);
                txtStatusOverdue.setVisibility(View.GONE);
                txtStatusReviewed.setVisibility(View.GONE);
                cardMakeAnOffer.setBackgroundTintList(ContextCompat.getColorStateList(TaskDetailsActivity.this,
                        R.color.colorPrimary));
                if (isMyTask) {
                    txtStatusOverdue.setVisibility(View.VISIBLE);
                    // poster task
                    if (noActionAvailable) {
                        cardMakeAnOffer.setVisibility(View.GONE);
                    } else {
                        cardMakeAnOffer.setVisibility(View.VISIBLE);
                        txtBtnText.setText(ConstantKey.BTN_RELEASE_MONEY);
                    }
                    toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_report, false);
                } else {
                    txtStatusCompleted.setVisibility(View.VISIBLE);
                    // worker task
                    if (noActionAvailable) {
                        cardMakeAnOffer.setVisibility(View.GONE);
                        toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_report, false);
                    } else {
                        cardMakeAnOffer.setVisibility(View.VISIBLE);
                        txtBtnText.setText(ConstantKey.BTN_WAIT_TO_RELEASE_MONEY);
                        toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_report, false);
                    }
                }

                toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_edit, false);
                toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_delete, false);
                toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_increase_budget, false);
                toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_reschedule, false);
                toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_cancellation, false);

//                cardMessage.setVisibility(View.VISIBLE);
                cardAssigneeLayout.setVisibility(View.VISIBLE);
//                cardPrivateChat.setVisibility(View.VISIBLE);
                cardOfferLayout.setVisibility(View.GONE);
                cardQuestionsLayout.setVisibility(View.GONE);
                txtBudget.setText("$" + taskModel.getAmount());
                break;
            case Constant.TASK_DRAFT:
                break;
            case Constant.TASK_CLOSED:
                lineOpenState.setVisibility(View.VISIBLE);
                txtStatusOpen.setSelected(false);
                txtStatusAssigned.setSelected(false);
                txtStatusCompleted.setSelected(false);
                txtStatusCancelled.setSelected(false);
                txtStatusOverdue.setSelected(false);
                txtStatusReviewed.setSelected(true);
                txtStatusOpen.setVisibility(View.VISIBLE);
                txtStatusAssigned.setVisibility(View.VISIBLE);
                txtStatusCompleted.setVisibility(View.VISIBLE);
                txtStatusCancelled.setVisibility(View.GONE);
                txtStatusOverdue.setVisibility(View.GONE);
                txtStatusReviewed.setVisibility(View.VISIBLE);
                if (isMyTask) {
                    cardMakeAnOffer.setVisibility(View.GONE);
                    txtBtnText.setText(ConstantKey.BTN_WRITE_A_REVIEW);

                    toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_report, false);
                } else {
                    // worker task
                    if (noActionAvailable) {
                        cardMakeAnOffer.setVisibility(View.GONE);
                        toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_report, false);
                    } else {
                        cardMakeAnOffer.setVisibility(View.GONE);
                        txtBtnText.setText(ConstantKey.BTN_WRITE_A_REVIEW);
                        toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_report, false);

                    }
                }
                toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_edit, false);
                toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_delete, false);
                toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_cancellation, false);
                toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_increase_budget, false);
                toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_reschedule, false);


//                cardMessage.setVisibility(View.VISIBLE);
                cardAssigneeLayout.setVisibility(View.VISIBLE);
//                cardPrivateChat.setVisibility(View.VISIBLE);
                cardOfferLayout.setVisibility(View.GONE);
                cardQuestionsLayout.setVisibility(View.GONE);
                txtBudget.setText("$" + taskModel.getAmount());
                break;
        }

        initCancelled();
        initCancellation();
        initJobReceipt();
        initIncreaseBudget();
        initRescheduleTime();
        initReview();
        initReleaseMoney();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            toolbar.getMenu().setGroupDividerEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_share:
                    Helper.shareTask(TaskDetailsActivity.this,
                            "Hey ! Checkout this task. \n " + "https://dev.jobtick.com/tasks/" + taskModel.getSlug());
                    break;
                case R.id.menu_bookmark:
                    if (taskModel.getBookmarkID() != null) {
                        removeBookmark();
                    } else {
                        addToBookmark();
                    }
                    break;
                case R.id.action_report:

                    Bundle bundleReport = new Bundle();
                    Intent intentReport = new Intent(TaskDetailsActivity.this, ReportActivity.class);
                    bundleReport.putString(ConstantKey.SLUG, taskModel.getSlug());
                    bundleReport.putString("key", ConstantKey.KEY_TASK_REPORT);

                    intentReport.putExtras(bundleReport);
                    startActivity(intentReport);
                    break;
                case R.id.action_edit:
                    new MaterialAlertDialogBuilder(TaskDetailsActivity.this)
                            .setTitle(getResources().getString(R.string.title_edit))
                            .setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss())
                            .setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                                dialog.dismiss();
                                EditTask(taskModel);
                            }).show();
                    break;
                case R.id.action_delete:
                    new MaterialAlertDialogBuilder(TaskDetailsActivity.this)
                            .setTitle(getResources().getString(R.string.title_delete))
                            .setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss())
                            .setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                                dialog.dismiss();
                                deleteTaskPermanent(taskModel.getSlug());
                            }).show();
                    break;
                case R.id.action_copy:
                    new MaterialAlertDialogBuilder(TaskDetailsActivity.this)
                            .setTitle(getResources().getString(R.string.title_copy))
                            .setNegativeButton(getResources().getString(R.string.no), null)
                            .setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                                dialog.dismiss();
                                // deleteTaskPermanent(taskDetails.getSlug());
                                copyTask(taskModel);
                            }).show();
                    break;
                case R.id.action_cancellation:
                    Intent intent;
                    Bundle bundle;
                    if (isMyTask) {
                        intent = new Intent(TaskDetailsActivity.this, CancellationPosterActivity.class);
                        // bundle.putParcelable(ConstantKey.TASK, taskModel);
                    } else {
                        intent = new Intent(TaskDetailsActivity.this, CancellationWorkerActivity.class);
                        //   bundle.putParcelable(ConstantKey.TASK, taskModel);
                    }
                    bundle = new Bundle();
                    bundle.putString(ConstantKey.SLUG, taskModel.getSlug());
                    intent.putExtras(bundle);
                    startActivityForResult(intent, ConstantKey.RESULTCODE_CANCELLATION);
                    break;
                case R.id.action_increase_budget:
                    if (isMyTask) {
                        //TODO: we should not have the menu if it is user post, So I comment these lines and add this toast
                        showToast("You can not increase budget of your job!", this);
//                        intent = new Intent(TaskDetailsActivity.this, IncreaseBudgetFromPosterActivity.class);
//                        bundle = new Bundle();
//                        //  bundle.putParcelable(ConstantKey.TASK, taskModel);
//                        intent.putExtras(bundle);
//                        startActivityForResult(intent, ConstantKey.RESULTCODE_INCREASE_BUDGET);
                    } else {
                        showDialogIncreaseBudgetRequest();
                        //   startActivityForResult(intent, ConstantKey.RESULTCODE_INCREASE_BUDGET);
                    }
                    break;
                case R.id.action_rechedule:
                    intent = new Intent(TaskDetailsActivity.this, RescheduleTimeRequestActivity.class);
                    bundle = new Bundle();
                    //   bundle.putParcelable(ConstantKey.TASK, taskModel);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, ConstantKey.RESULTCODE_INCREASE_BUDGET);
                    break;
                case R.id.action_job_receipt:
                    intent = new Intent(TaskDetailsActivity.this, JobReceiptActivity.class);
                    bundle = new Bundle();
                    //    bundle.putParcelable(ConstantKey.TASK, taskModel);
                    bundle.putBoolean(ConstantKey.IS_MY_TASK, isMyTask);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
            }
            return false;
        });
    }

    private void initComponentScroll() {
        NestedScrollView nested_content = findViewById(R.id.nested_scroll_view);
        nested_content.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY < oldScrollY) { // up
                animateFab(false);
            }
            if (scrollY > oldScrollY) { // down
                animateFab(true);
            }
        });
    }

    private void animateFab(final boolean hide) {
        if (isFabHide && hide || !isFabHide && !hide) return;
        isFabHide = hide;
        int moveY = hide ? (2 * cardMakeAnOffer.getHeight()) : 0;
        cardMakeAnOffer.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    @BindView(R.id.content)
    LinearLayout content;
    @BindView(R.id.llLoading)
    LinearLayout llLoading;
    private void getData() {
        getAllUserProfileDetails();
        getBankAccountAddress();
        getBillingAddress();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_TASKS + "/" + str_slug,
                response -> {
                    try {
                        llLoading.setVisibility(View.GONE);
                        content.setVisibility(View.VISIBLE);
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        System.out.println(jsonObject.toString());
                        if (jsonObject.has("success") &&
                                !jsonObject.isNull("success") &&
                                jsonObject.getBoolean("success")) {

                            if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                                String ss = jsonObject.getString("data");
                                taskModel = new TaskModel().getJsonToModel(jsonObject_data, TaskDetailsActivity.this);

                                setOwnerTask();
//                                initOfferList();
                                initStatusTask(taskModel.getStatus().toLowerCase());
                                initComponent();
                                setDataInLayout(taskModel);
                                initQuestion();
                                setChatButton(taskModel.getStatus().toLowerCase(),jsonObject_data);
                                setPosterChatButton(taskModel.getStatus().toLowerCase(),jsonObject_data);



                                if (taskModel.getTaskType().equals("physical")) {
                                    llLocation.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<" + taskModel.getPosition().getLatitude() + ">,<" + taskModel.getPosition().getLongitude() + ">?q=<" + taskModel.getPosition().getLatitude() + ">,<" + taskModel.getPosition().getLongitude() + ">(" + "job address" + ")"));
                                            startActivity(intent);
                                        }
                                    });
                                }
                                questionListAdapter.clear();
                                questionListAdapter.addItems(taskModel.getQuestions());

                                for (int i = 0; i < taskModel.getQuestions().size(); i++) {
                                    if (taskModel.getQuestions().get(i).getId() == pushQuestionID) {
                                        onItemQuestionClick(null, taskModel.getQuestions().get(i), i, "reply");
                                        break;
                                    }
                                }
                            }
                        } else {
                            showToast("Something went wrong", TaskDetailsActivity.this);
                        }

                    } catch (JSONException e) {

                        showToast("JSONException", TaskDetailsActivity.this);
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                    }
                },
                error -> {
                    //    fl_task_details.setVisibility(View.GONE);

                    errorHandle1(error.networkResponse);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                // map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(TaskDetailsActivity.this);
        requestQueue.add(stringRequest);

    }
    private void getDataOnlyQuestions() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_TASKS + "/" + str_slug,
                response -> {
                    try {
                        llLoading.setVisibility(View.GONE);
                        content.setVisibility(View.VISIBLE);
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        System.out.println(jsonObject.toString());
                        if (jsonObject.has("success") &&
                                !jsonObject.isNull("success") &&
                                jsonObject.getBoolean("success")) {

                            if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                                String ss = jsonObject.getString("data");
                                taskModel = new TaskModel().getJsonToModel(jsonObject_data, TaskDetailsActivity.this);

                                setOwnerTask();
//                                initOfferList();
                                initStatusTask(taskModel.getStatus().toLowerCase());
                                initComponent();
                                setDataInLayout(taskModel);
                                initQuestion();
                                setChatButton(taskModel.getStatus().toLowerCase(),jsonObject_data);
                                setPosterChatButton(taskModel.getStatus().toLowerCase(),jsonObject_data);



                                if (taskModel.getTaskType().equals("physical")) {
                                    llLocation.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<" + taskModel.getPosition().getLatitude() + ">,<" + taskModel.getPosition().getLongitude() + ">?q=<" + taskModel.getPosition().getLatitude() + ">,<" + taskModel.getPosition().getLongitude() + ">(" + "job address" + ")"));
                                            startActivity(intent);
                                        }
                                    });
                                }
                                questionListAdapter.clear();
                                questionListAdapter.addItems(taskModel.getQuestions());

                                for (int i = 0; i < taskModel.getQuestions().size(); i++) {
                                    if (taskModel.getQuestions().get(i).getId() == pushQuestionID) {
                                        onItemQuestionClick(null, taskModel.getQuestions().get(i), i, "reply");
                                        break;
                                    }
                                }
                            }
                        } else {
                            showToast("Something went wrong", TaskDetailsActivity.this);
                        }

                    } catch (JSONException e) {

                        showToast("JSONException", TaskDetailsActivity.this);
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                    }
                },
                error -> {
                    //    fl_task_details.setVisibility(View.GONE);

                    errorHandle1(error.networkResponse);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                // map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(TaskDetailsActivity.this);
        requestQueue.add(stringRequest);

    }

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tvPrivateChatName)
    TextView tvPrivateChatName;
    @BindView(R.id.tvPrivateChatLocation)
    TextView tvPrivateChatLocation;
    @BindView(R.id.tvPrivateChatLastOnline)
    TextView tvPrivateChatLastOnline;
    @BindView(R.id.imgAvatarChat)
    CircularImageView imgAvatarChat;
    @BindView(R.id.llBtnPrivateMessageDisable)
    LinearLayout llBtnPrivateMessageDisable;
    @BindView(R.id.llBtnPrivateMessageEnable)
    LinearLayout llBtnPrivateMessageEnable;
    @BindView(R.id.llBtnPosterMessageEnable)
    LinearLayout llBtnPosterMessageEnable;

    private void setChatButton(String state,JSONObject jsonObject) {
        if(!isMyTask || taskModel.getWorker()==null)
            return;

        relPrivateChat.setVisibility(View.VISIBLE);
        if (taskModel.getWorker().getAvatar() != null && taskModel.getWorker().getAvatar().getThumbUrl() != null) {
            ImageUtil.displayImage(imgAvatarChat, taskModel.getWorker().getAvatar().getThumbUrl(), null);
        }

        imgAvatarChat.setOnClickListener(v -> {
            if (taskModel.getWorker().getId() != null) {
                Intent intent = new Intent(TaskDetailsActivity.this, ProfileActivity.class);
                intent.putExtra("id", taskModel.getWorker().getId());
                startActivity(intent);
            }
        });
        tvPrivateChatName.setText(taskModel.getWorker().getName());
        if (taskModel.getLocation() != null && !taskModel.getLocation().isEmpty()) {
            tvPrivateChatLocation.setText(taskModel.getLocation());
        } else {
            tvPrivateChatLocation.setText("Remote Task");
        }
        if (taskModel.getWorker().getLocation() != null && taskModel.getWorker().getLocation().length() > 0) {
            tvPrivateChatLocation.setVisibility(View.VISIBLE);
//            txtPosterLocation.setText(taskModel.getLocation());
        } else {
            tvPrivateChatLocation.setVisibility(View.VISIBLE);
        }

        tvPrivateChatLastOnline.setText("Active " + taskModel.getWorker().getLastOnline());

        llBtnPrivateMessageEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject conversation = jsonObject.getJSONObject("conversation");
                    ConversationModel conversationModel = new ConversationModel(TaskDetailsActivity.this).getJsonToModel(conversation,
                            TaskDetailsActivity.this);
                    Intent intent = new Intent(TaskDetailsActivity.this, ChatActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(ConstantKey.CONVERSATION, conversationModel);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, ConstantKey.RESULTCODE_PRIVATE_CHAT);
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        });
            switch (state){
                case TASK_DRAFT:
                case TASK_PENDING:
                case TASK_OPEN:
                    llBtnPrivateMessageDisable.setVisibility(View.VISIBLE);
                    llBtnPrivateMessageEnable.setVisibility(View.GONE);
                    break;
                 default:
                     llBtnPrivateMessageDisable.setVisibility(View.GONE);
                     llBtnPrivateMessageEnable.setVisibility(View.VISIBLE);
                    break;
            }

    }
    private void setPosterChatButton(String state,JSONObject jsonObject) {
        if(taskModel.getWorker()==null)
            return;


        if (!taskModel.getWorker().getId().equals(sessionManager.getUserAccount().getId()))
            return;


        llBtnPosterMessageEnable.setOnClickListener(v -> {
            try {
                JSONObject conversation = jsonObject.getJSONObject("conversation");
                ConversationModel conversationModel = new ConversationModel(TaskDetailsActivity.this).getJsonToModel(conversation,
                        TaskDetailsActivity.this);
                Intent intent = new Intent(TaskDetailsActivity.this, ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(ConstantKey.CONVERSATION, conversationModel);
                intent.putExtras(bundle);
                startActivityForResult(intent, ConstantKey.RESULTCODE_PRIVATE_CHAT);
            } catch (JSONException e) {
                e.printStackTrace();
            }



        });
            switch (state){
                case TASK_DRAFT:
                case TASK_PENDING:
                case TASK_OPEN:
                    lytBtnMessage.setVisibility(View.VISIBLE);
                    llBtnPosterMessageEnable.setVisibility(View.GONE);
                    break;
                 default:
                     lytBtnMessage.setVisibility(View.GONE);
                     llBtnPosterMessageEnable.setVisibility(View.VISIBLE);
                    break;
            }

    }

    public void getAllUserProfileDetails() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_PROFILE + "/" + sessionManager.getUserAccount().getId(),
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());

                        if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                            userAccountModel = new UserAccountModel().getJsonToModel(jsonObject.getJSONObject("data"));
                        } else {
                        }

                    } catch (JSONException e) {
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                    }
                },
                error -> {
                    errorHandle1(error.networkResponse);
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                // map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }
        };


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void getBankAccountAddress() {
        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, BASE_URL + ADD_ACCOUNT_DETAILS,
                response -> {
                    Timber.e(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {

                                String jsonString = jsonObject.toString(); //http request
                                BankAccountModel data = new BankAccountModel();
                                Gson gson = new Gson();
                                data = gson.fromJson(jsonString, BankAccountModel.class);

                                if (data != null) {
                                    if (data.isSuccess()) {
                                        if (data.getData() != null && data.getData().getAccount_number() != null) {
                                            bankAccountModel = data;
                                        }
                                    }
                                }
                            } else {
                                showToast("Something went Wrong", this);
                            }
                        }
                    } catch (JSONException e) {
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
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
                            hideProgressDialog();
                            return;
                        }
                        try {
                            hideProgressDialog();
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                            //  showCustomDialog(jsonObject_error.getString("message"));
                            if (jsonObject_error.has("message")) {
                                Toast.makeText(this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", this);
                    }
                    Timber.e(error.toString());
                    hideProgressDialog();
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void getBillingAddress() {

        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, BASE_URL + ADD_BILLING,
                response -> {
                    Timber.e(response);
                    try {
                        hideProgressDialog();
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                String jsonString = jsonObject.toString(); //http request
                                BillingAdreessModel data = new BillingAdreessModel();
                                Gson gson = new Gson();
                                data = gson.fromJson(jsonString, BillingAdreessModel.class);

                                if (data != null) {
                                    if (data.isSuccess()) {
                                        if (data.getData() != null && data.getData().getLine1() != null) {
                                            billingAdreessModel = data;
                                        }
                                    }
                                }
                            } else {
                                showToast("Something went Wrong", this);
                            }
                        }
                    } catch (JSONException e) {
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
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
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                            //  showCustomDialog(jsonObject_error.getString("message"));
                            if (jsonObject_error.has("message")) {
                                Toast.makeText(this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", this);
                    }
                    Timber.e(error.toString());
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.relPrivateChat)
    RelativeLayout relPrivateChat;

    private void setOwnerTask() {
        if (taskModel.getPoster().getId().equals(sessionManager.getUserAccount().getId())) {
            //this is self task
            isMyTask = true;
            postedByLyt.setVisibility(View.GONE);
        } else {
            //this is another tasks
            postedByLyt.setVisibility(View.VISIBLE);
            if (taskModel.getWorker() != null) {
                if (taskModel.getWorker().getId().equals(sessionManager.getUserAccount().getId())) {
                    isMyTask = false;
                    noActionAvailable = false;
                } else {
                    noActionAvailable = true;
                }
            } else {
                isMyTask = false;
                noActionAvailable = false;
            }
        }
    }

    private void initQuestion() {
        attachmentArrayList_question.clear();
        attachmentArrayList_question.add(new AttachmentModel());
        recyclerViewQuestionAttachment.setLayoutManager(new LinearLayoutManager(TaskDetailsActivity.this, RecyclerView.HORIZONTAL, false));
        recyclerViewQuestionAttachment.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(TaskDetailsActivity.this, 5), true));
        recyclerViewQuestionAttachment.setHasFixedSize(true);
        //set data and list adapter
        adapter = new AttachmentAdapter(attachmentArrayList_question, true);
        recyclerViewQuestionAttachment.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(this);

    }

    @SuppressLint("SetTextI18n")
    private void setDataInLayout(TaskModel taskModel) {
        txtTitle.setText(taskModel.getTitle());
        txtCreatedDate.setText("Posted " + taskModel.getCreatedAt());

        if (taskModel.getBookmarkID() != null) {
            toolbar.getMenu().findItem(R.id.menu_bookmark).setIcon(R.drawable.ic_bookmark_filled_white);
        }

        txtDescription.setText(taskModel.getDescription());
        if (taskModel.getPoster().getAvatar() != null && taskModel.getPoster().getAvatar().getThumbUrl() != null) {
            ImageUtil.displayImage(imgAvtarPoster, taskModel.getPoster().getAvatar().getThumbUrl(), null);
        }  //TODO DUMMY IMAGE

        imgAvtarPoster.setOnClickListener(v -> {
            if (taskModel.getPoster().getId() != null) {
                Intent intent = new Intent(TaskDetailsActivity.this, ProfileActivity.class);
                intent.putExtra("id", taskModel.getPoster().getId());
                startActivity(intent);
            }
        });
        txtPosterName.setText(taskModel.getPoster().getName());
        if (taskModel.getLocation() != null && !taskModel.getLocation().isEmpty()) {
            txtLocation.setText(taskModel.getLocation());
        } else {
            txtLocation.setText("Remote Task");
        }
        if (taskModel.getPoster().getLocation() != null && taskModel.getPoster().getLocation().length() > 0) {
            txtPosterLocation.setVisibility(View.VISIBLE);
//            txtPosterLocation.setText(taskModel.getLocation());
        } else {
            txtPosterLocation.setVisibility(View.VISIBLE);
        }

        txtPosterLastOnline.setText("Active " + taskModel.getPoster().getLastOnline());


        if (taskModel.getDueTime() != null) {

            String dueTime = convertObjectToString(taskModel.getDueTime(), "");
            txtDueTime.setText(dueTime);
        }
        if (taskModel.getDueTime() != null) {
            txtDueDate.setText(taskModel.getDueDate() + " - ");
        }


        if (taskModel.getMusthave() != null && taskModel.getMusthave().size() > 0) {
            mustHaveLyt.setVisibility(View.VISIBLE);
            ShowMustHaveListAdapter showMustHaveListAdapter = new ShowMustHaveListAdapter(taskModel.getMusthave());
            mustHaveList.setLayoutManager(new LinearLayoutManager(this));
            mustHaveList.setAdapter(showMustHaveListAdapter);

        } else {
            mustHaveLyt.setVisibility(View.GONE);
        }
    }

    private String convertObjectToString(DueTimeModel time, String dueTime) {
        if (time.getMorning()) {
            dueTime = "Morning";
        }
        if (time.getAfternoon()) {
            if (dueTime.length() != 0) {
                dueTime = dueTime + ",Afternoon";
            } else {
                dueTime = dueTime + "Afternoon";
            }
        }
        if (time.getEvening()) {
            if (dueTime.length() != 0) {
                dueTime = dueTime + ",Evening";
            } else {
                dueTime = dueTime + "Evening";
            }
        }
        if (time.getMidday()) {
            if (dueTime.length() != 0) {
                dueTime = dueTime + ",Midday";
            } else {
                dueTime = dueTime + "Midday";
            }
        }
        return dueTime;
    }

    private void initToolbar() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            final boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle("Job Details");
                    collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(getApplication(), R.color.black));
                    toolbar.getMenu().findItem(R.id.menu_share).setIcon(R.drawable.ic_share);
                    Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_back_black, null);
                    toolbar.setNavigationIcon(d);
                    toolbar.setTitleTextColor(Color.BLACK);
                    toolbar.getMenu().findItem(R.id.item_three_dot).setIcon(R.drawable.ic_three_dot);
                    if (taskModel.getBookmarkID() != null) {
                        toolbar.getMenu().findItem(R.id.menu_bookmark).setIcon(R.drawable.ic_bookmark_filled_black);
                    } else {
                        toolbar.getMenu().findItem(R.id.menu_bookmark).setIcon(R.drawable.ic_bookmark_black);
                    }
                    isShowBookmarked = true;
                } else if (isShowBookmarked) {
                    collapsingToolbar.setTitle("");
                    collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(getApplication(), R.color.transparent));
                    toolbar.getMenu().findItem(R.id.menu_share).setIcon(R.drawable.ic_share_white);
                    toolbar.getMenu().findItem(R.id.item_three_dot).setIcon(R.drawable.ic_three_dot_white);
                    Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_back_white, null);
                    toolbar.setNavigationIcon(d);
                    toolbar.setTitleTextColor(Color.WHITE);
                    if (taskModel.getBookmarkID() != null) {
                        toolbar.getMenu().findItem(R.id.menu_bookmark).setIcon(R.drawable.ic_bookmark_filled_white);
                    } else {
                        toolbar.getMenu().findItem(R.id.menu_bookmark).setIcon(R.drawable.ic_bookmark_white);
                    }
                    isShowBookmarked = false;
                }
            }
        });

    }

    private void copyTask(TaskModel taskModel) {


        Intent update_task = new Intent(TaskDetailsActivity.this, TaskCreateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(ConstantKey.TASK, taskModel);
        bundle.putString(ConstantKey.TITLE, "Create Task");
        update_task.putExtras(bundle);
        startActivity(update_task);
    }

    private void EditTask(TaskModel taskModel) {

        Intent update_task = new Intent(TaskDetailsActivity.this, TaskCreateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(ConstantKey.TASK, taskModel);
        bundle.putString(ConstantKey.TITLE, ConstantKey.UPDATE_TASK);
        update_task.putExtras(bundle);
        startActivity(update_task);
        //startActivityForResult(creating_task,12);
    }

    private void deleteTaskPermanent(String slug) {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, URL_TASKS + "/" + slug,
                response -> {
                    Timber.e(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                showToast("Task Deleted Successfully", TaskDetailsActivity.this);
                            } else {
                                showToast("Task not deleted", TaskDetailsActivity.this);
                            }
                        } else {
                            showToast(getString(R.string.server_went_wrong), TaskDetailsActivity.this);
                        }
                        hideProgressDialog();
                    } catch (JSONException e) {
                        hideProgressDialog();
                        e.printStackTrace();
                    }

                },
                error -> {
                    hideProgressDialog();

                    //  swipeRefresh.setRefreshing(false);
                    errorHandle1(error.networkResponse);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                return map1;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(TaskDetailsActivity.this);
        requestQueue.add(stringRequest);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @SuppressLint("SetTextI18n")
    private void initComponent() {
        setOfferView(taskModel.getOfferCount());
        setQuestionView(taskModel.getQuestionCount());
        initIncreaseBudget();
        budget.setText("$" + taskModel.getBudget());
        setAttachmentAndSlider();
        setWorker();
    }

    @SuppressLint("SetTextI18n")
    private void setWorker() {
        //worker
        if (taskModel.getWorker() != null) {
            if (taskModel.getWorker().getAvatar() != null && taskModel.getWorker().getAvatar().getThumbUrl() != null) {
                ImageUtil.displayImage(imgAvtarWorker, taskModel.getWorker().getAvatar().getThumbUrl(), null);
            }  //TODO DUMMY IMAGE

            txtWorkerName.setText(taskModel.getWorker().getName());
            if (taskModel.getWorker() != null && taskModel.getWorker().getWorkerRatings() != null && taskModel.getWorker().getWorkerRatings().getAvgRating() != null) {
                txtRatingValue.setText("(" + taskModel.getWorker().getWorkerRatings().getAvgRating() + ")");
            }
            if (taskModel.getWorker().getWorkTaskStatistics() != null)
                txtCompletionRate.setText(taskModel.getWorker().getWorkTaskStatistics().getCompletionRate().toString() + "%");
        }
    }

    private void setAttachmentAndSlider() {
        Timber.e("%s", taskModel.getAttachments().size());
        adapterImageSlider = new AdapterImageSlider(this, new ArrayList<>());

        if (taskModel.getAttachments() == null || taskModel.getAttachments().size() == 0) {
            AttachmentModel attachment = new AttachmentModel();
          /*  if (taskModel.getTaskType().equalsIgnoreCase(ConstantKey.PHYSICAL)) {
                attachment.setDrawable(R.drawable.banner_red);
            } else {
                attachment.setDrawable(R.drawable.banner_green);
            }*/
            ArrayList<AttachmentModel> attachments = new ArrayList<>();
            attachments.add(attachment);
            taskModel.setAttachments(attachments);

        }
        adapterImageSlider.setItems(taskModel.getAttachments());
        dataList.addAll(taskModel.getAttachments());

        viewPager.setAdapter(adapterImageSlider);
        // displaying selected image first
        viewPager.setCurrentItem(0);
        if (taskModel.getAttachments() != null && taskModel.getAttachments().size() > 1) {
            addBottomDots(layoutDots, adapterImageSlider.getCount(), 0);
        }

        //  ((TextView) findViewById(R.id.title)).setText(items.get(0).name);
        // ((TextView) findViewById(R.id.brief)).setText(items.get(0).brief);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int pos) {
                //   ((TextView) findViewById(R.id.title)).setText(items.get(pos).name);
                //   ((TextView) findViewById(R.id.brief)).setText(items.get(pos).brief);
                addBottomDots(layoutDots, adapterImageSlider.getCount(), pos);


            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // startAutoSlider(adapterImageSlider.getCount());
    }

    @SuppressLint("SetTextI18n")
    private void setQuestionView(int questionCount) {
        if (isMyTask) {
            rltQuestionAdd.setVisibility(View.GONE);
        } else {
            rltQuestionAdd.setVisibility(View.VISIBLE);
        }

        if (questionCount == 0) {
            txtQuestionsCount.setText("Question");
        } else {
            txtQuestionsCount.setText("Question (" + questionCount + ")");
        }

        //TODO taskModel.getQuestionCount() > 5
        if (taskModel.getQuestionCount() > 5) {
            lytViewAllQuestions.setVisibility(View.VISIBLE);
        } else {
            lytViewAllQuestions.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setOfferView(int offerCount) {
        if (offerCount == 0) {
            txtOffersCount.setText(getString(R.string.offer));
            txtWaitingForOffer.setVisibility(View.VISIBLE);
            if (isMyTask) {
                firstOfferLyt.setVisibility(View.GONE);
            } else {
                firstOfferLyt.setVisibility(View.VISIBLE);
            }
        } else {
            txtOffersCount.setText("Offers (" + offerCount + ")");
            txtWaitingForOffer.setVisibility(View.GONE);
        }

        //TODO taskModel.getOfferCount() > 5
//        if (offerCount > 5) {
//            cardViewAllOffers.setVisibility(View.VISIBLE);
//        } else {
//            cardViewAllOffers.setVisibility(View.GONE);
//        }
    }

    private void addBottomDots(LinearLayout layout_dots, int size, int current) {
        ImageView[] dots = new ImageView[size];

        layout_dots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            int width_height = 15;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(width_height, width_height));
            params.setMargins(10, 10, 10, 10);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(R.drawable.shape_circle_outline);
            layout_dots.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[current].setImageResource(R.drawable.shape_circle);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.lyt_btn_view_all_questions, R.id.lyt_btn_comment_send, R.id.lyt_btn_message, R.id.first_offer,
            R.id.lyt_btn_view_all_offers, R.id.txt_btn_text, R.id.lyt_btn_make_an_offer,
            R.id.card_cancelled, R.id.li_repost, R.id.liAssign, R.id.linearUserProfile})
    public void onViewClicked(View view) {
        Intent intent;
        Bundle bundle;
        switch (view.getId()) {
            case R.id.lyt_btn_comment_send:
                if (TextUtils.isEmpty(edtComment.getText().toString().trim())) {
                    edtComment.setError("?");
                    return;
                } else {
                    if (attachmentArrayList_question.size() > 0)
                        attachmentArrayList_question.remove(attachmentArrayList_question.size() - 1);
                    if (attachmentArrayList_question.size() == 0) {
                        postComment(
                                edtComment.getText().toString().trim(),
                                null
                        );
                    } else {
                        postComment(
                                edtComment.getText().toString().trim(),
                                attachmentArrayList_question
                        );
                    }
                }
                break;
            case R.id.lyt_btn_message:
//                intent = new Intent(TaskDetailsActivity.this, ChatActivity.class);
//                bundle = new Bundle();
//                bundle.putParcelable(ConstantKey.CONVERSATION, taskModel.getConversation());
//                intent.putExtras(bundle);
//                startActivity(intent);
                break;

//            case R.id.lyt_btn_private_chat:
//                intent = new Intent(TaskDetailsActivity.this, ChatActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putParcelable(ConstantKey.CONVERSATION, taskModel.getConversation());
//                intent.putExtras(bundle);
//                startActivity(intent);
//                break;
            case R.id.lyt_btn_view_all_offers:
                intent = new Intent(TaskDetailsActivity.this, ViewAllOffersActivity.class);
                bundle = new Bundle();
                bundle.putString(ConstantKey.SLUG, taskModel.getSlug());
                intent.putExtras(bundle);
                startActivityForResult(intent, 121);
                break;
            case R.id.lyt_btn_view_all_questions:
                intent = new Intent(TaskDetailsActivity.this, ViewAllQuestionsActivity.class);
                bundle = new Bundle();
                bundle.putString(ConstantKey.SLUG, taskModel.getSlug());
                intent.putExtras(bundle);
                startActivityForResult(intent, 121);
                break;

            case R.id.txt_btn_text:
            case R.id.first_offer:
            case R.id.lyt_btn_make_an_offer:
                switch (txtBtnText.getText().toString().trim()) {
                    case ConstantKey.BTN_ASK_TO_RELEASE_MONEY:
                        showCustomDialogAskToReleaseMoney();
                        break;
                    case ConstantKey.BTN_RELEASE_MONEY:
                        showCustomDialogReleaseMoney();
                        break;
                    case ConstantKey.BTN_MAKE_AN_OFFER:
                        if (!needRequirementSheet()) {
                            if (taskModel.getMusthave().size() == 0) {
                                intent = new Intent(TaskDetailsActivity.this, MakeAnOfferActivity.class);
                                bundle = new Bundle();
                                bundle.putInt("id", taskModel.getId());
                                bundle.putInt("budget", taskModel.getBudget());
                                intent.putExtras(bundle);
                                startActivity(intent);

                            } else {
                                showRequirementDialog();
                            }
                        } else {
                            TickerRequirementsBottomSheet tickerRequirementsBottomSheet = TickerRequirementsBottomSheet.newInstance(requirementState);
                            tickerRequirementsBottomSheet.show(getSupportFragmentManager(), "");
                        }
                        break;
                    case ConstantKey.BTN_OFFER_PENDING:
                        Toast.makeText(TaskDetailsActivity.this, "offer pending", Toast.LENGTH_SHORT).show();
                        break;
                    case ConstantKey.BTN_CANCELLATION_REQUEST_RECEIVED:
                        intent = new Intent(TaskDetailsActivity.this, CancellationRequestSubmittedActivity.class);
                        bundle = new Bundle();
                        // bundle.putParcelable(ConstantKey.TASK, taskModel);
                        bundle.putBoolean(ConstantKey.IS_MY_TASK, isMyTask);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, ConstantKey.RESULTCODE_CANCELLATION_DONE);
                        break;
                    case ConstantKey.BTN_CANCELLATION_REQUEST_SENT:
                        intent = new Intent(TaskDetailsActivity.this, CancellationRequestActivity.class);
                        bundle = new Bundle();
                        //  bundle.putParcelable(ConstantKey.TASK, taskModel);
                        bundle.putBoolean(ConstantKey.IS_MY_TASK, isMyTask);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case ConstantKey.BTN_WRITE_A_REVIEW:
                        //TODO write a review
                        //button should not exist
                        break;

                }
                break;
            case R.id.card_cancelled:
            case R.id.li_repost:
                copyTask(taskModel);
                break;
            case R.id.liAssign:
                Intent workerIntent = new Intent(TaskDetailsActivity.this, ProfileActivity.class);
                workerIntent.putExtra("id", taskModel.getWorker().getId());
                startActivity(workerIntent);
            case R.id.linearUserProfile:
                Intent posterIntent = new Intent(TaskDetailsActivity.this, ProfileActivity.class);
                posterIntent.putExtra("id", taskModel.getPoster().getId());
                startActivity(posterIntent);
                break;
        }
    }

    private void handleState() {
        requirementState.put(Requirement.Profile, false);
        requirementState.put(Requirement.BankAccount, false);
        requirementState.put(Requirement.BillingAddress, false);
        requirementState.put(Requirement.BirthDate, false);
        requirementState.put(Requirement.PhoneNumber, false);

        if (userAccountModel != null && userAccountModel.getAvatar() != null &&
                !userAccountModel.getAvatar().getUrl().equals("")) {
            requirementState.put(Requirement.Profile, true);
        }
        //checking one field is enough
        if (bankAccountModel != null && bankAccountModel.getData() != null &&
                bankAccountModel.getData().getAccount_number() != null &&
                !bankAccountModel.getData().getAccount_number().equals("")) {
            requirementState.put(Requirement.BankAccount, true);
        }
        //checking one filed is enough
        if (billingAdreessModel != null && billingAdreessModel.getData() != null &&
                billingAdreessModel.getData().getPost_code() != null &&
                !billingAdreessModel.getData().getPost_code().equals("")) {

            requirementState.put(Requirement.BillingAddress, true);
        }
        if (userAccountModel != null && userAccountModel.getDob() != null &&
                !userAccountModel.getDob().equals("")) {
            requirementState.put(Requirement.BirthDate, true);
        }
        if (userAccountModel != null && userAccountModel.getMobile() != null &&
                !userAccountModel.getMobile().equals("") &&
                userAccountModel.getMobileVerifiedAt() != null &&
                !userAccountModel.getMobileVerifiedAt().equals("")) {
            requirementState.put(Requirement.PhoneNumber, true);
        }
    }

    private boolean needRequirementSheet() {
        handleState();
        return !(requirementState.get(Requirement.Profile) &&
                requirementState.get(Requirement.BankAccount) &&
                requirementState.get(Requirement.BillingAddress) &&
                requirementState.get(Requirement.BirthDate) &&
                requirementState.get(Requirement.PhoneNumber));
    }

    private void submitAskToReleaseMoney() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_TASKS + "/" + taskModel.getSlug() + "/complete",
                response -> {
                    Timber.e(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {

                            initialStage();
                        } else {
                            hideProgressDialog();
                            showToast(getString(R.string.server_went_wrong), TaskDetailsActivity.this);
                        }

                    } catch (JSONException e) {
                        hideProgressDialog();
                        e.printStackTrace();
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
                            hideProgressDialog();
                            return;
                        }
                        hideProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);

                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                            if (jsonObject_error.has("message")) {
                                showCustomDialog("You have sent your request before");
                                // Toast.makeText(TaskDetailsActivity.this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", TaskDetailsActivity.this);
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
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(TaskDetailsActivity.this);
        requestQueue.add(stringRequest);
    }

    private void submitReleaseMoney() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_TASKS + "/" + taskModel.getSlug() + "/close",
                response -> {
                    Timber.e(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {

                            initialStage();
                        } else {
                            hideProgressDialog();
                            showToast(getString(R.string.server_went_wrong), TaskDetailsActivity.this);
                        }

                    } catch (JSONException e) {
                        hideProgressDialog();
                        e.printStackTrace();
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
                            hideProgressDialog();
                            return;
                        }
                        hideProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);

                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                            if (jsonObject_error.has("message")) {
                                Toast.makeText(TaskDetailsActivity.this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", TaskDetailsActivity.this);
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
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(TaskDetailsActivity.this);
        requestQueue.add(stringRequest);
    }

    private void showCustomDialog(String message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_show_warning);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        TextViewRegular txtMessage = dialog.findViewById(R.id.txt_message);
        txtMessage.setText(message);

        dialog.findViewById(R.id.btn_ok).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void postComment(String str_comment, ArrayList<AttachmentModel> attachmentModels) {
        if (str_comment.length() < 5) {
            showToast("The question text must be at least 5 characters", this);
            return;
        }
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_QUESTIONS + "/" + taskModel.getId() + "/create",
                response -> {
                    Timber.e(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Timber.e(jsonObject.toString());
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            edtComment.setText("");
                            attachmentArrayList_question.clear();
                            attachmentArrayList_question.add(new AttachmentModel());
                            adapter.notifyDataSetChanged();
                            if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                                if (recyclerViewQuestions.getVisibility() != View.VISIBLE) {
                                    recyclerViewQuestions.setVisibility(View.VISIBLE);
                                }
                                QuestionModel questionModel = new QuestionModel().getJsonToModel(jsonObject.getJSONObject("data"));
                                questionListAdapter.addItem(questionModel);

                            }
                        } else {
                            showToast(getString(R.string.server_went_wrong), TaskDetailsActivity.this);
                        }
                        getDataOnlyQuestions();
                        hideProgressDialog();
                    } catch (JSONException e) {
                        hideProgressDialog();
                        e.printStackTrace();
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
                            hideProgressDialog();
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);

                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                            if (jsonObject_error.has("message")) {
                                Toast.makeText(TaskDetailsActivity.this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            if (jsonObject_error.has("errors")) {
                                JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                            }
                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", TaskDetailsActivity.this);
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
                map1.put("question_text", str_comment);
                if (attachmentModels != null && attachmentModels.size() != 0) {
                    for (int i = 0; attachmentModels.size() > i; i++) {
                        map1.put("attachments[" + i + "]", String.valueOf(attachmentModels.get(i).getId()));
                    }
                }
                return map1;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(TaskDetailsActivity.this);
        requestQueue.add(stringRequest);

    }

    @Override
    public void onMakeAnOffer() {
        initialStage();
    }

    @Override
    public void onRequestAccept() {
        getData();
    }

    @Override
    public void onWidthdraw(int id) {
        doApiCall(Constant.URL_OFFERS + "/" + id);
    }


    private static class AdapterImageSlider extends PagerAdapter {
        private final Activity act;
        private ArrayList<AttachmentModel> items;

        public void setOnItemClickListener() {
        }

        // constructor
        private AdapterImageSlider(Activity activity, ArrayList<AttachmentModel> items) {
            this.act = activity;
            this.items = items;
        }


        @Override
        public int getCount() {
            return this.items.size();
        }

        public AttachmentModel getItem(int pos) {
            return items.get(pos);
        }

        public void setItems(ArrayList<AttachmentModel> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @Override
        public boolean isViewFromObject(@NotNull View view, @NotNull Object object) {
            return view == object;
        }

        @Override
        public @NotNull Object instantiateItem(@NotNull ViewGroup container, int position) {
            final AttachmentModel attachment = items.get(position);
            LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.item_slider_image, container, false);

            ImageView image = v.findViewById(R.id.image);
            image.setAdjustViewBounds(true);
            if (attachment.getUrl() != null) {
                ImageUtil.displayImage(image, attachment.getUrl(), null);
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                if (taskModel.getLocation() != null && !taskModel.getLocation().isEmpty()) {
                    Tools.displayImageOriginal(act, image, attachment.getDrawable());
                    image.setAdjustViewBounds(true);
                    image.setBackgroundResource(R.drawable.banner_green);
                } else {
                    Tools.displayImageOriginal(act, image, attachment.getDrawable());
                    image.setAdjustViewBounds(true);
                    image.setBackgroundResource(R.drawable.banner_red);
                }

                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            image.setOnClickListener(v1 -> {
             /*   if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, attachment);
                }*/
                Intent intent = new Intent(act, ZoomImageActivity.class);
                intent.putExtra("url", items);
                intent.putExtra("title", "");
                intent.putExtra("pos", position);
                act.startActivity(intent);
            });

            container.addView(v);

            return v;
        }

        @Override
        public void destroyItem(@NotNull ViewGroup container, int position, @NotNull Object object) {
            container.removeView((RelativeLayout) object);

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantKey.RESULTCODE_MAKEANOFFER) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    if (bundle.getBoolean(ConstantKey.MAKE_AN_OFFER)) {
                        initialStage();
                        // txtBtnText.setText(ConstantKey.BTN_OFFER_PENDING);
                    }
                }
            }
        }
        if (requestCode == ConstantKey.RESULTCODE_PAYMENTOVERVIEW) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    if (bundle.getBoolean(ConstantKey.PAYMENT_OVERVIEW)) {
                        initialStage();
                    }
                }
            }
        }
        if (requestCode == ConstantKey.RESULTCODE_WRITE_REVIEW) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    if (bundle.getBoolean(ConstantKey.WRITE_REVIEW)) {
                        initialStage();
                    }
                }
            }
        }
        if (requestCode == ConstantKey.RESULTCODE_CANCELLATION) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    if (bundle.getBoolean(ConstantKey.CANCELLATION)) {
                        initialStage();
                    }
                }
            }
        }
        if (requestCode == ConstantKey.RESULTCODE_CANCELLATION_DONE) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    if (bundle.getBoolean(ConstantKey.DONE)) {
                        initialStage();
                    }
                }
            }
        }

        if (requestCode == GALLERY_PICKUP_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data.getData() != null) {
                    String imageStoragePath = CameraUtils.getPath(TaskDetailsActivity.this, data.getData());
                    File file = new File(imageStoragePath);
                    uploadDataQuestionMediaApi(file);
                }
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording

            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to Pickup Image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    //Adapter override method
    @Override
    public void onItemOfferClick(View v, OfferModel obj, int position, String action) {
        if (action.equalsIgnoreCase("reply")) {
            Intent intent = new Intent(TaskDetailsActivity.this, PublicChatActivity.class);
            Bundle bundle = new Bundle();
            ///bundle.putParcelable(ConstantKey.OFFER_LIST_MODEL, obj);
            offerModel = obj;
            isOfferQuestion = "offer";
            intent.putExtras(bundle);
            startActivityForResult(intent, 20);
        } else if (action.equalsIgnoreCase("accept")) {
            Intent intent = new Intent(TaskDetailsActivity.this, PaymentOverviewActivity.class);
            Bundle bundle = new Bundle();
            offerModel = obj;
            //    bundle.putParcelable(ConstantKey.TASK, taskModel);
            //     bundle.putParcelable(ConstantKey.OFFER_LIST_MODEL, obj);
            intent.putExtras(bundle);
            startActivityForResult(intent, ConstantKey.RESULTCODE_PAYMENTOVERVIEW);
        }else if(action.equalsIgnoreCase("report")){
            Intent intent = new Intent(TaskDetailsActivity.this, ReportActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(ConstantKey.SLUG, taskModel.getSlug());
            bundle.putString("key", ConstantKey.KEY_OFFER_REPORT);

            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public void onItemQuestionClick(View view, QuestionModel obj, int position, String action) {
        if (action.equalsIgnoreCase("reply")) {
            Intent intent = new Intent(TaskDetailsActivity.this, PublicChatActivity.class);
            Bundle bundle = new Bundle();
            questionModel = obj;
            isOfferQuestion = "question";
            //bundle.putParcelable(ConstantKey.QUESTION_LIST_MODEL, obj);
            intent.putExtras(bundle);
            startActivityForResult(intent, 21);
        }else if(action.equalsIgnoreCase("report")){
            Intent intent = new Intent(TaskDetailsActivity.this, ReportActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(ConstantKey.SLUG, taskModel.getSlug());
            bundle.putString("key", ConstantKey.KEY_COMMENT_REPORT);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public void onItemClick(View view, AttachmentModel obj, int position, String action) {
        if (action.equalsIgnoreCase("add")) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_PICKUP_IMAGE_REQUEST_CODE);
        } else if (action.equalsIgnoreCase("delete")) {
            recyclerViewQuestionAttachment.removeViewAt(position);
            attachmentArrayList_question.remove(position);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeRemoved(position, attachmentArrayList_question.size());
            showToast("Delete this attachment", TaskDetailsActivity.this);
        }
    }

    private void uploadDataQuestionMediaApi(File pictureFile) {
        showProgressDialog();
        Call<String> call;
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile);
        MultipartBody.Part imageFile = MultipartBody.Part.createFormData("media", pictureFile.getName(), requestFile);
        call = ApiClient.getClient().getTaskTempAttachmentMediaData("XMLHttpRequest", sessionManager.getTokenType() + " " + sessionManager.getAccessToken(), imageFile);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, retrofit2.@NotNull Response<String> response) {
                hideProgressDialog();
                Timber.e(response.toString());
                if (response.code() == HttpStatus.HTTP_VALIDATION_ERROR) {
                    showToast(response.message(), TaskDetailsActivity.this);
                    return;
                }
                try {
                    String strResponse = response.body();
                    if (response.code() == HttpStatus.NOT_FOUND) {
                        Toast.makeText(TaskDetailsActivity.this, "not found", Toast.LENGTH_SHORT).show();
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
                            JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                            AttachmentModel attachment = new AttachmentModel().getJsonToModel(jsonObject_data);
                            if (attachmentArrayList_question.size() != 0) {
                                attachmentArrayList_question.add(attachmentArrayList_question.size() - 1, attachment);
                            }
                        }

                        adapter.notifyItemInserted(0);
                        //  adapter.notifyItemRangeInserted(0,attachmentArrayList.size());


                    } else {
                        showToast("Something went wrong", TaskDetailsActivity.this);
                    }
                } catch (JSONException e) {
                    showToast("Something went wrong", TaskDetailsActivity.this);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                hideProgressDialog();
            }
        });

    }

    private void doApiCall(String url) {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    hideProgressDialog();
                    Timber.e(response);
                    // categoryArrayList.clear();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        OfferDeleteModel data = new OfferDeleteModel();
                        Gson gson = new Gson();
                        data = gson.fromJson(jsonObject.toString(), OfferDeleteModel.class);
                        initialStage();
                        showCustomDialog(data.getMessage());
                    } catch (JSONException e) {
                        hideProgressDialog();
                        Timber.e(String.valueOf(e));
                        e.printStackTrace();
                    }
                },
                error -> {
                    //  swipeRefresh.setRefreshing(false);
                    hideProgressDialog();

                    errorHandle1(error.networkResponse);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                //    map1.put("X-Requested-With", "XMLHttpRequest");

                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(TaskDetailsActivity.this);
        requestQueue.add(stringRequest);
        Timber.e(stringRequest.getUrl());
    }

    public void addToBookmark() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_TASKS + "/" + taskModel.getSlug() + "/bookmark",
                response -> {
                    if (isShowBookmarked) {
                        toolbar.getMenu().findItem(R.id.menu_bookmark).setIcon(R.drawable.ic_bookmark_filled_black);
                    } else {
                        toolbar.getMenu().findItem(R.id.menu_bookmark).setIcon(R.drawable.ic_bookmark_filled_white);
                    }
                    getData();
                },
                error -> {
                    errorHandle1(error.networkResponse);
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map1 = new HashMap<>();
                map1.put("is_important", "0");
                return map1;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");

                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(TaskDetailsActivity.this);
        requestQueue.add(stringRequest);
        Timber.e(stringRequest.getUrl());
    }

    private void removeBookmark() {
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, BASE_URL + "bookmarks/" + taskModel.getBookmarkID(),
                response -> {
                    Timber.e(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                if (isShowBookmarked) {
                                    toolbar.getMenu().findItem(R.id.menu_bookmark).setIcon(R.drawable.ic_bookmark_black);
                                } else {
                                    toolbar.getMenu().findItem(R.id.menu_bookmark).setIcon(R.drawable.ic_bookmark_white);
                                }

                                getData();
                                if (onRemoveSavedtasklistener != null) {
                                    onRemoveSavedtasklistener.onRemoveSavedTask();
                                }

                            } else {
                                showToast("Task not deleted", TaskDetailsActivity.this);
                            }
                        } else {
                            showToast(getString(R.string.server_went_wrong), TaskDetailsActivity.this);
                        }
                    } catch (JSONException e) {
                        hideProgressDialog();
                        e.printStackTrace();
                    }

                },
                error -> {
                    //  swipeRefresh.setRefreshing(false);
                    errorHandle1(error.networkResponse);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map1 = new HashMap<>();
                map1.put("is_important", "1");
                return map1;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map1 = new HashMap<>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");

                return map1;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(TaskDetailsActivity.this);
        requestQueue.add(stringRequest);
    }

    public void showRequirementDialog() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        @SuppressLint("InflateParams") final View view = getLayoutInflater().inflate(R.layout.sheet_requirement, null);

        LinearLayout lyt_btn_make_an_offer = view.findViewById(R.id.lyt_btn_make_an_offer);
        CardView card_make_an_offer = view.findViewById(R.id.card_make_an_offer);

        MustHaveListAdapter adapter;
        ArrayList<MustHaveModel> addTagList = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(this, 5), true));
        recyclerView.setHasFixedSize(true);


        for (int i = 0; i < taskModel.getMusthave().size(); i++) {
            MustHaveModel mustHaveModel = new MustHaveModel();
            mustHaveModel.setMustHaveTitle(taskModel.getMusthave().get(i));
            mustHaveModel.setChecked(false);
            addTagList.add(mustHaveModel);
        }
        adapter = new MustHaveListAdapter(this, addTagList);
        adapter.onCheckedAllItems(() -> {
            if (adapter.isAllSelected()) {
                card_make_an_offer.setBackgroundTintList(ContextCompat.getColorStateList(TaskDetailsActivity.this,
                        R.color.colorPrimary));
            } else {
                card_make_an_offer.setBackgroundTintList(ContextCompat.getColorStateList(TaskDetailsActivity.this,
                        R.color.colorAccent));
            }
        });


        lyt_btn_make_an_offer.setOnClickListener(v -> {

            if (adapter.isAllSelected()) {

                Intent intent = new Intent(TaskDetailsActivity.this, MakeAnOfferActivity.class);

                Bundle bundle = new Bundle();
                bundle.putInt("id", taskModel.getId());
                bundle.putInt("budget", taskModel.getBudget());
                intent.putExtras(bundle);
                startActivity(intent);
                mBottomSheetDialog.dismiss();
            } else {
                Toast.makeText(TaskDetailsActivity.this, "Please select all must-have requirement", Toast.LENGTH_LONG).show();
            }
        });

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // set background transparent
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(dialog -> mBottomSheetDialog = null);
    }

    private void initCancelled() {
        if (alertType == AlertType.CANCELLED) {
            hideAlertBox();
        }

        if (taskModel.getStatus().toLowerCase().equals("cancelled")) {
            showCancelledCard();
        }
    }

    private void initCancellation() {
        if (alertType == AlertType.CANCELLATION) {
            hideAlertBox();
        }
        if (taskModel.getCancellation() == null) return;
        if (isMyTask) {
            if (taskModel.getCancellation().getStatus().equalsIgnoreCase(ConstantKey.CANCELLATION_PENDING)) {
                if (taskModel.getCancellation().getRequesterId().equals(sessionManager.getUserAccount().getId())) {
                    //poster is cancelling, user is poster too
                    showCancellationCard(false, false);
                } else {
                    //ticker is cancelling, user is poster
                    showCancellationCard(false, true);
                }
                txtBtnText.setVisibility(View.GONE);
                toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_cancellation, false);
            } else if (taskModel.getCancellation().getStatus().equalsIgnoreCase(ConstantKey.CANCELLATION_DECLINED)) {

            } else if (taskModel.getCancellation().getStatus().equalsIgnoreCase(ConstantKey.CANCELLATION_ACCEPTED)) {

            }
        } else {
            if (taskModel.getCancellation().getStatus().equalsIgnoreCase(ConstantKey.CANCELLATION_PENDING)) {
                if (taskModel.getCancellation().getRequesterId().equals(sessionManager.getUserAccount().getId())) {
                    //ticker is cancelling, user is ticker too
                    showCancellationCard(true, true);
                } else {
                    //poster is cancelling, user is ticker
                    showCancellationCard(true, false);
                }
                txtBtnText.setVisibility(View.GONE);
                toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_cancellation, false);
            }
        }
    }

    private void initReleaseMoney() {

        if (alertType == AlertType.ASK_TO_RELEASE && !isMyTask) {
            hideAlertBox();
        }
        else if (alertType == AlertType.CONFIRM_RELEASE && isMyTask) {
            hideAlertBox();
        }
        if (taskModel.getStatus().toLowerCase().equals("completed") && taskModel.getWorker() != null &&
                taskModel.getWorker().getId().equals(userAccountModel.getId())) {
            showAskToReleaseCard();
        } else if ((taskModel.getStatus().toLowerCase().equals("overdue") && taskModel.getPoster() != null &&
                taskModel.getPoster().getId().equals(userAccountModel.getId()))) {
            showConfirmReleaseCard();
        }
    }

    private void initReview() {
        if (alertType == AlertType.REVIEW) {
            hideAlertBox();
        }
        if (!taskModel.getStatus().toLowerCase().equals("closed")) return;
        if (taskModel.getReviewModels() == null) {
            showReviewCard();
            return;
        }
        boolean showReview = true;
        List<ReviewModel> reviewModels = taskModel.getReviewModels();
        //for poster
        if (isMyTask) {
            for (ReviewModel reviewModel : reviewModels) {
                if (reviewModel.getRateeType().equals("worker")) {
                    showReview = false;
                    break;
                }
            }
        } //for worker
        else {
            for (ReviewModel reviewModel : reviewModels) {
                if (reviewModel.getRateeType().equals("poster")) {
                    showReview = false;
                    break;
                }
            }
        }
        if (showReview)
            showReviewCard();
    }

    private void initRescheduleTime() {
        if (alertType == AlertType.RESCHEDULE) {
            hideAlertBox();
        }
        if (taskModel.getRescheduleReqeust() != null && taskModel.getRescheduleReqeust().size() > 0) {

            for (int i = 0; i < taskModel.getRescheduleReqeust().size(); i++) {
                if (taskModel.getRescheduleReqeust().get(i).getStatus().equals("pending") &&
                        !taskModel.getRescheduleReqeust().get(i).getRequester_id().equals(sessionManager.getUserAccount().getId())) {
                    if (!taskModel.getStatus().toLowerCase().equals(TASK_CANCELLED) && !taskModel.getStatus().toLowerCase().equals(TASK_CLOSED)) {
                        pos = i;
                        showRescheduleTimeCard(i);
                        break;
                    }
                }
            }
        }
    }

    private void initJobReceipt() {
        toolbar.getMenu().findItem(R.id.item_three_dot).getSubMenu().setGroupVisible(R.id.grp_job_receipt, taskModel.getStatus().toLowerCase().equals(TASK_CLOSED));
    }

    private void initIncreaseBudget() {
        if (alertType == AlertType.INCREASE_BUDGET) {
            hideAlertBox();
        }
        if (taskModel.getAdditionalFund() != null && taskModel.getAdditionalFund().getStatus().equals("pending")) {
            if (taskModel.getPoster().getId().equals(sessionManager.getUserAccount().getId())) {
                showIncreaseBudgetCard();
            }
        }
    }

    private int pos = 0;

    private void showDialogRescheduleRequest(int pos) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        RescheduleNoticeBottomSheetState dialog = RescheduleNoticeBottomSheetState.newInstance(taskModel, pos);
        dialog.show(fragmentManager, "");
    }

    private void showDialogIncreaseBudgetRequest() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        IncreaseBudgetBottomSheet dialog = IncreaseBudgetBottomSheet.newInstance(taskModel, 0);
        dialog.show(fragmentManager, "");
    }

    private void showDialogIncreaseBudgetNoticeRequest() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        IncreaseBudgetNoticeBottomSheet dialog = IncreaseBudgetNoticeBottomSheet.newInstance(taskModel);
        dialog.show(fragmentManager, "");
    }

    private void showDialogIncreaseBudgetDeclineRequest() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        IncreaseBudgetDeclineBottomSheet dialog = IncreaseBudgetDeclineBottomSheet.newInstance(taskModel);
        dialog.show(fragmentManager, "");
    }

    private void showCustomDialogAskToReleaseMoney() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ConfirmAskToReleaseBottomSheet dialog = new ConfirmAskToReleaseBottomSheet(getBaseContext());
        dialog.show(fragmentManager, "");

    }

    private void showCustomDialogReleaseMoney() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ConfirmReleaseBottomSheet dialog = new ConfirmReleaseBottomSheet(getBaseContext());
        dialog.show(fragmentManager, "");
    }

    private void showCancelledCard() {
        showAlertBox(Html.fromHtml("This job has been canceled"),
                ConstantKey.BTN_POST_NEW_JOB, AlertType.CANCELLED, true);
    }

    // we have 4 possibility to support,
    // ticker cancels, user is ticker
    // ticker cancels, user is poster
    // poster cancels, user is poster
    // poster cancels, user is ticker
    private boolean isTicker = false;
    private boolean tickerCancels = false;
    private String cancellationTitle;

    private void showCancellationCard(boolean isTicker, boolean tickerCancels) {
        this.isTicker = isTicker;
        this.tickerCancels = tickerCancels;

        if (isTicker && tickerCancels) {
            cancellationTitle = "<b>You</b> have requested to cancel this job on <b>" +
                    TimeHelper.convertToShowTimeFormat(taskModel.getCancellation().getCreatedAt()) + "</b>";

            showAlertBox(Html.fromHtml(cancellationTitle), ConstantKey.BTN_VIEW_CANCELLATION_REQUEST,
                    AlertType.CANCELLATION, true);
        }
        if (!isTicker && !tickerCancels) {
            cancellationTitle = "<b>You</b> have requested to cancel this job on <b>" +
                    TimeHelper.convertToShowTimeFormat(taskModel.getCancellation().getCreatedAt()) + "</b>";

            showAlertBox(Html.fromHtml(cancellationTitle), ConstantKey.BTN_VIEW_CANCELLATION_REQUEST,
                    AlertType.CANCELLATION, true);
        }
        if (isTicker && !tickerCancels) {
            String cancelledByWho = taskModel.getPoster().getName();

            cancellationTitle = "<b>" + cancelledByWho + "</b> " +
                    "has requested to cancel this job on <b>" +
                    TimeHelper.convertToShowTimeFormat(taskModel.getCancellation().getCreatedAt()) + "</b>";

            showAlertBox(Html.fromHtml(cancellationTitle), ConstantKey.BTN_VIEW_CANCELLATION_REQUEST,
                    AlertType.CANCELLATION, true);
        }
        if (!isTicker && tickerCancels) {
            String cancelledByWho = taskModel.getWorker().getName();

            cancellationTitle = "<b>" + cancelledByWho + "</b> " +
                    "has requested to cancel this job on <b>" +
                    TimeHelper.convertToShowTimeFormat(taskModel.getCancellation().getCreatedAt()) + "</b>";

            showAlertBox(Html.fromHtml(cancellationTitle), ConstantKey.BTN_VIEW_CANCELLATION_REQUEST,
                    AlertType.CANCELLATION, true);
        }
    }

    private void showRescheduleTimeCard(int pos) {
        String rescheduledByWho;
        if (isMyTask)
            rescheduledByWho = taskModel.getWorker().getName();
        else
            rescheduledByWho = taskModel.getPoster().getName();

        showAlertBox(Html.fromHtml("<b>" + rescheduledByWho + "</b> " +
                        "has requested to reschedule time for this job on <b>" +
                        TimeHelper.convertToShowTimeFormat(taskModel.getRescheduleReqeust().get(pos).getCreated_at()) + "</b>"),
                ConstantKey.BTN_RESCHEDULE_REQUEST_SENT, AlertType.RESCHEDULE, true);
    }

    private void showIncreaseBudgetCard() {
        String increaseRequestByWho;
        if (isMyTask)
            increaseRequestByWho = taskModel.getWorker().getName();
        else
            increaseRequestByWho = taskModel.getPoster().getName();

        showAlertBox(Html.fromHtml("<b>" + increaseRequestByWho + "</b> " +
                        "has requested to increase price on this job on <b>" +
                        TimeHelper.convertToShowTimeFormat(taskModel.getAdditionalFund().getCreatedAt()) + "</b>"),
                ConstantKey.BTN_INCREASE_BUDGET_REQUEST_SENT, AlertType.INCREASE_BUDGET, true);
    }

    private void showReviewCard() {
        String writeAReviewForWho;
        if (isMyTask)
            writeAReviewForWho = taskModel.getWorker().getName();
        else
            writeAReviewForWho = taskModel.getPoster().getName();

        showAlertBox(Html.fromHtml(
                "Make our community more trusted by leaving a review for <b>" + writeAReviewForWho + "</b>"),
                ConstantKey.BTN_LEAVE_A_REVIEW, AlertType.REVIEW, true);
    }

    private void showAskToReleaseCard() {
        if (isMyTask) return;

        showAlertBox(Html.fromHtml(
                "You have requested to release money this job on <b>" +
                        TimeHelper.convertToShowTimeFormat(taskModel.getConversation().getTask().getCompletedAt()) + "</b>"),
                null, AlertType.ASK_TO_RELEASE, false);
    }

    private void showConfirmReleaseCard() {
        if (!isMyTask) return;

        String whoRequestToReleaseMoney = taskModel.getWorker().getName();

        showAlertBox(Html.fromHtml("<b>" + whoRequestToReleaseMoney + "</b> " +
                        " have requested to release money this job on <b>" +
                        TimeHelper.convertToShowTimeFormat(taskModel.getConversation().getTask().getCompletedAt()) + "</b>"),
                ConstantKey.BTN_CONFIRM_RELEASE_MONEY, AlertType.CONFIRM_RELEASE, true);
    }

    //we use spanned to support middle bolds
    private void showAlertBox(Spanned title, String buttonText, AlertType alertType,
                              boolean hasButton) {
        alertBox.setVisibility(View.VISIBLE);
        this.alertType = alertType;
        alertBox.setTitle(title);
        alertBox.setHasButton(hasButton);
        alertBox.setButtonText(buttonText);
    }

    private void hideAlertBox() {
        alertBox.setVisibility(View.GONE);
        this.alertType = null;
    }

    // we have 4 possibility to support,
    // ticker cancels, user is ticker
    // ticker cancels, user is poster
    // poster cancels, user is poster
    // poster cancels, user is ticker
    @Override
    public void onExtendedAlertButtonClick() {
        if (alertType == null) return;
        if (alertType == AlertType.CANCELLATION) {
            Intent intent;
            if (tickerCancels && isTicker) {
                intent = new Intent(this, TTCancellationSummaryActivity.class);
            } else if (tickerCancels && !isTicker) {
                intent = new Intent(this, TPCancellationSummaryActivity.class);
            } else if (!tickerCancels && isTicker) {
                intent = new Intent(this, PTCancellationSummaryActivity.class);
            } else {
                intent = new Intent(this, PPCancellationSummaryActivity.class);
            }
            Bundle bundle = new Bundle();
            bundle.putString(ConstantKey.CANCELLATION_TITLE, cancellationTitle);
            intent.putExtras(bundle);
            startActivityForResult(intent, ConstantKey.RESULTCODE_CANCELLATION);
        } else if (alertType == AlertType.RESCHEDULE) {
            showDialogRescheduleRequest(pos);
        } else if (alertType == AlertType.INCREASE_BUDGET) {
            showDialogIncreaseBudgetNoticeRequest();
        } else if (alertType == AlertType.REVIEW) {
            Intent intent = new Intent(TaskDetailsActivity.this, LeaveReviewActivity.class);
            Bundle bundle = new Bundle();
            //    bundle.putParcelable(ConstantKey.TASK, taskModel);
            bundle.putBoolean(ConstantKey.IS_MY_TASK, isMyTask);
            intent.putExtras(bundle);
            startActivityForResult(intent, ConstantKey.RESULTCODE_WRITE_REVIEW);
        } else if (alertType == AlertType.CANCELLED) {
            Intent intent = new Intent(this, CategoryListActivity.class);
            startActivity(intent);
        }
        else if(alertType == AlertType.CONFIRM_RELEASE){
            showCustomDialogReleaseMoney();
        }
    }

    @Override
    public void onAskToReleaseConfirmClick() {
        submitAskToReleaseMoney();
    }

    @Override
    public void onReleaseConfirmClick() {
        submitReleaseMoney();
    }

    @Override
    public void onIncreaseBudgetAcceptClick() {
        getData();
    }

    @Override
    public void onIncreaseBudgetRejectClick() {
        showDialogIncreaseBudgetDeclineRequest();
    }

    @Override
    public void onIncreaseBudgetSubmitClick() {
        getData();
    }

    @Override
    public void onRescheduleTimeAcceptDeclineClick() {
        getData();
    }

    @Override
    public void onSubmitIncreasePrice() {
        getData();
    }

    public enum AlertType {
        CANCELLATION,
        RESCHEDULE,
        INCREASE_BUDGET,
        ASK_TO_RELEASE,
        CONFIRM_RELEASE,
        REVIEW,
        CANCELLED
        //more we add later
    }
}