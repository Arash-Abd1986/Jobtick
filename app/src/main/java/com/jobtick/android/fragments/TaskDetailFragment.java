package com.jobtick.android.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.jobtick.android.AppController;
import com.jobtick.android.R;
import com.jobtick.android.activities.AbstractUploadableImageImpl;
import com.jobtick.android.activities.UploadableImage;
import com.jobtick.android.activities.TaskCreateActivity;
import com.jobtick.android.adapers.AddTagAdapter;
import com.jobtick.android.adapers.AttachmentAdapter1;
import com.jobtick.android.models.AttachmentModel;
import com.jobtick.android.models.PositionModel;
import com.jobtick.android.models.TaskModel;
import com.jobtick.android.models.task.AttachmentModels;
import com.jobtick.android.retrofit.ApiClient;
import com.jobtick.android.text_view.TextViewMedium;
import com.jobtick.android.utils.SessionManager;
import com.jobtick.android.utils.SuburbAutoComplete;
import com.jobtick.android.utils.Tools;
import com.jobtick.android.widget.ExtendedCommentText;
import com.jobtick.android.widget.ExtendedCommentTextNewDesign;
import com.jobtick.android.widget.ExtendedEntryText;
import com.jobtick.android.widget.SpacingItemDecoration;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskDetailFragment extends Fragment implements AttachmentAdapter1.OnItemClickListener, TextWatcher {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_details)
    LinearLayout lytBtnDetails;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.card_details)
    CardView cardDetails;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.add_attach)
    FrameLayout addAttach;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_attach_title)
    MaterialTextView tvAttachTitle;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_require_title)
    MaterialTextView tvRequireTitle;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.add_attach_small)
    FrameLayout addAttachSmall;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_bnt_date_time)
    LinearLayout lytBntDateTime;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_budget)
    LinearLayout lytBtnBudget;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_title)
    ExtendedCommentTextNewDesign edtTitle;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_description)
    ExtendedCommentTextNewDesign edtDescription;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_add_must_have)
    RecyclerView recyclerAddMustHave;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rlt_add_must_have)
    FrameLayout rltAddMustHave;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rel_req_small)
    RelativeLayout relReqSmall;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.checkbox_online)
    SwitchCompat checkboxOnline;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_suburb)
    ExtendedCommentTextNewDesign txtSuburb;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_next)
    MaterialButton btnNext;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.bottom_sheet)
    FrameLayout bottomSheet;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rcAttachment)
    RecyclerView rcAttachment;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_details)
    ImageView imgDetails;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_details)
    TextView txtDetails;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_date_time)
    ImageView imgDateTime;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_date_time)
    TextView txtDateTime;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_budget)
    ImageView imgBudget;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_budget)
    TextView txtBudget;
    private int requiredCount = -1;


    private final String TAG = TaskDetailFragment.class.getName();
    private final int PLACE_SELECTION_REQUEST_CODE = 21;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private AttachmentAdapter1 attachmentAdapter;
    private BottomSheetDialog mBottomSheetDialog;
    private TaskCreateActivity taskCreateActivity;
    private AddTagAdapter tagAdapter;
    private AddTagAdapter tagAdapterBottomSheet;
    private ArrayList<String> addTagList;
    private OperationsListener operationsListener;
    private TaskModel task;
    private SessionManager sessionManager;
    private UploadableImage uploadableImage;
    boolean isEditTask = false;
    String taskSlug = null;
    private final ArrayList<AttachmentModel> attachmentArrayList = new ArrayList<>();

    public static TaskDetailFragment newInstance(String title, String description, ArrayList<String> musthave,
                                                 String task_type, String location, PositionModel positionModel, AttachmentModels attachmentModels, OperationsListener operationsListener, boolean isEditTask, String taskSlug) {
        TaskDetailFragment fragment = new TaskDetailFragment();
        fragment.operationsListener = operationsListener;
        Bundle args = new Bundle();
        args.putString("TITLE", title);
        args.putString("DESCRIPTION", description);
        args.putStringArrayList("MUSTHAVE", musthave);
        args.putString("TASK_TYPE", task_type);
        args.putString("LOCATION", location);
        args.putBoolean("isEditTask", isEditTask);
        args.putString("taskSlug", taskSlug);
        args.putParcelable("POSITION", positionModel);
        args.putParcelable("ATTACHMENT", attachmentModels);

        fragment.setArguments(args);
        return fragment;
    }

    public TaskDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_detail, container, false);
        ButterKnife.bind(this, view);

        uploadableImage = new AbstractUploadableImageImpl(requireActivity()) {
            @Override
            public void onImageReady(File imageFile) {
                if (!isEditTask)
                    uploadDataInTempApi(imageFile);
                else
                    uploadDataForEditTask(imageFile);
            }
        };
        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        taskCreateActivity = (TaskCreateActivity) requireActivity();
        sessionManager = new SessionManager(taskCreateActivity);

        task = new TaskModel();
        selectDetailsBtn();

        addTagList = new ArrayList<>();
        task.setTitle(getArguments().getString("TITLE"));
        task.setDescription(getArguments().getString("DESCRIPTION"));
        task.setMusthave(getArguments().getStringArrayList("MUSTHAVE"));
        task.setTaskType(getArguments().getString("TASK_TYPE"));
        task.setLocation(getArguments().getString("LOCATION"));
        task.setPosition(getArguments().getParcelable("POSITION"));
        task.setAttachments(new ArrayList<>(((AttachmentModels) getArguments().getParcelable("ATTACHMENT")).getAttachmentModelList()));
        isEditTask = getArguments().getBoolean("isEditTask", false);
        taskSlug = getArguments().getString("taskSlug", null);
        //lytBtnDetails.setBackgroundResource(R.drawable.rectangle_round_white_with_shadow);
        cardDetails.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        addAttach.setOnClickListener(v -> {
            edtDescription.clearFocus();
            edtTitle.clearFocus();
            uploadableImage.showAttachmentImageBottomSheet(false);
        });
        addAttachSmall.setOnClickListener(v -> {
            edtDescription.clearFocus();
            edtTitle.clearFocus();
            uploadableImage.showAttachmentImageBottomSheet(false);});


        taskCreateActivity.setActionDraftTaskDetails(taskModel -> {

            if (edtTitle.getText().trim().length() >= 10) {
                taskModel.setTitle(edtTitle.getText().trim());
            }
            if (edtDescription.getText().trim().length() >= 25) {
                taskModel.setDescription(edtDescription.getText().trim());
            }
            if (!TextUtils.isEmpty(txtSuburb.getText().trim())) {
                taskModel.setLocation(task.getLocation());
                taskModel.setPosition(task.getPosition());
            }
            if (addTagList != null)
                taskModel.setMusthave(addTagList);

            if (attachmentArrayList != null)
                taskModel.setAttachments(attachmentArrayList);


            taskModel.setTaskType(checkboxOnline.isChecked() ? "remote" : "physical");
            operationsListener.draftTaskDetails(taskModel, false);
        });

        txtSuburb.setOnClickListener(v -> {
            edtDescription.clearFocus();
            edtTitle.clearFocus();
            Intent intent = new SuburbAutoComplete(requireActivity()).getIntent();
            startActivityForResult(intent, PLACE_SELECTION_REQUEST_CODE);
        });

        setComponent();
        init();
        textChangeCheck();

    }


    private void setComponent() {
        edtTitle.setText(task.getTitle());
        edtDescription.setText(task.getDescription());
        if (task.getTaskType() != null) {
            if (task.getTaskType().equalsIgnoreCase("remote")) {
                checkboxOnline.setChecked(true);
                txtSuburb.setVisibility(View.GONE);
            } else {
                checkboxOnline.setChecked(false);
                txtSuburb.setVisibility(View.VISIBLE);
            }
        } else {
            checkboxOnline.setChecked(false);
            txtSuburb.setVisibility(View.VISIBLE);
        }
        if (task.getMusthave() != null && task.getMusthave().size() != 0) {
            addTagList.addAll(task.getMusthave());
        }
        txtSuburb.setText(task.getLocation());
    }

    private void init() {
        showBottomSheetAddMustHave(true);
        recyclerAddMustHave.setLayoutManager(new GridLayoutManager(taskCreateActivity, 1));
        recyclerAddMustHave.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(taskCreateActivity, 5), true));
        recyclerAddMustHave.setHasFixedSize(true);
        tagAdapter = new AddTagAdapter(addTagList, data -> {
            addTagList.remove(data);
            tagAdapter.updateItem(addTagList);
            tagAdapterBottomSheet.updateItem(addTagList);
            if (addTagList.size() == 0) {
                relReqSmall.setVisibility(View.GONE);
                tvRequireTitle.setTextColor(getResources().getColor(R.color.N100));
                rltAddMustHave.setVisibility(View.VISIBLE);
            }
        });

        recyclerAddMustHave.setAdapter(tagAdapter);
        rcAttachment.setLayoutManager(new GridLayoutManager(taskCreateActivity, 5));
        rcAttachment.setHasFixedSize(true);
        attachmentAdapter = new AttachmentAdapter1(attachmentArrayList, true);
        rcAttachment.setAdapter(attachmentAdapter);
        attachmentAdapter.setOnItemClickListener(this);

        if (task.getAttachments() != null && !task.getAttachments().isEmpty()) {
            for (AttachmentModel model : task.getAttachments()) {
                if (model.getId() != null) {
                    if (attachmentArrayList.size() != 0) {
                        attachmentArrayList.add(attachmentArrayList.size() - 1, model);
                    }
                    attachmentAdapter.notifyItemInserted(attachmentArrayList.size() - 1);
                }
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.rlt_add_must_have, R.id.lyt_btn_details, R.id.lyt_bnt_date_time, R.id.lyt_btn_budget, R.id.checkbox_online, R.id.btn_next, R.id.rel_req_small
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rlt_add_must_have:
                edtDescription.clearFocus();
                edtTitle.clearFocus();
                showBottomSheetAddMustHave(false);
                break;
            case R.id.rel_req_small:
                showBottomSheetAddMustHave(false);
                break;
            case R.id.lyt_btn_details:
                break;
            case R.id.lyt_bnt_date_time:
                break;
            case R.id.lyt_btn_budget:
                break;
            case R.id.checkbox_online:
                edtDescription.clearFocus();
                edtTitle.clearFocus();
                if (checkboxOnline.isChecked()) {
                    txtSuburb.setVisibility(View.GONE);
                } else {
                    txtSuburb.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_next:
                switch (getValidationCode()) {
                    case 0:
                        //success
                        operationsListener.onNextClick(
                                edtTitle.getText().trim(),
                                edtDescription.getText().trim(),
                                addTagList,
                                checkboxOnline.isChecked() ? "remote" : "physical",
                                txtSuburb.getText().trim(),
                                task.getPosition(),
                                attachmentArrayList
                        );
                        operationsListener.onValidDataFilled();
                        break;
                    case 1:
                        edtTitle.setError("Please enter the title");
                        break;
                    case 2:
                        edtDescription.setError("Please enter the description");
                        break;
                    case 3:
                        txtSuburb.setError("Please select your location");
                        break;
                }
                break;
        }
    }

    private int getValidationCode() {
        if (TextUtils.isEmpty(edtTitle.getText().trim()) || edtTitle.getText().trim().length() < 10) {
            return 1;
        } else if (TextUtils.isEmpty(edtDescription.getText().trim()) || edtDescription.getText().trim().length() < 25) {
            return 2;
        } else if (!checkboxOnline.isChecked()) {
            if (TextUtils.isEmpty(txtSuburb.getText().trim())) {
                return 3;
            }
        }
        return 0;
    }

    @Override
    public void onItemClick(View view, AttachmentModel obj, int position, String action) {
        if (action.equalsIgnoreCase("delete")) {
            if (isEditTask) {
                deleteEditTaskAttachment(obj.getId());
            }
            rcAttachment.removeViewAt(position);
            attachmentArrayList.remove(position);
            attachmentAdapter.notifyItemRemoved(position);
            attachmentAdapter.notifyItemRangeRemoved(position, attachmentArrayList.size());
            if (attachmentArrayList.size() == 0) {
                addAttach.setVisibility(View.VISIBLE);
                tvAttachTitle.setTextColor(getResources().getColor(R.color.N100));
                addAttachSmall.setVisibility(View.GONE);
            }
        } else if (action.equalsIgnoreCase("show")) {
            showBottomSheetDialogViewFullImage(obj.getModalUrl(), position);
        }
    }

    private void deleteEditTaskAttachment(int attachmentId) {
        if (taskSlug == null) return;
        taskCreateActivity.showProgressDialog();
        Call<String> call;
        call = ApiClient.getClient().deleteEditTaskAttachment(taskSlug, "XMLHttpRequest", sessionManager.getTokenType() + " " + sessionManager.getAccessToken(), attachmentId);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                taskCreateActivity.hideProgressDialog();
                if (response.code() == 422) {
                    return;
                }

                try {
                    String strResponse = response.body();
                    JSONObject jsonObject = new JSONObject(strResponse);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                ((AppController) getContext()).mCrashlytics.recordException(t);
            }
        });
    }

    private void textChangeCheck() {
        edtTitle.addTextChangedListener(this);
        edtDescription.addTextChangedListener(this);
        txtSuburb.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (getValidationCode() == 0) {
            btnNext.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.P300));
        } else {
            btnNext.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.P300_alpha));
        }
    }

    public interface OperationsListener {
        void onNextClick(String title, String description, ArrayList<String> musthave, String task_type, String location,
                         PositionModel positionModel, ArrayList<AttachmentModel> attachmentArrayList);

        void onValidDataFilled();

        void draftTaskDetails(TaskModel taskModel, boolean moveForword);
    }

    private void showBottomSheetDialogViewFullImage(String url, int currentPosition) {
        /*if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
*/
        @SuppressLint("InflateParams") final View view = getLayoutInflater().inflate(R.layout.sheet_full_image, null);

        mBottomSheetDialog = new BottomSheetDialog(taskCreateActivity);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        ImageView iv_attachment = view.findViewById(R.id.iv_attachment);
        if (url != null) {
            Glide.with(iv_attachment).load(url).into(iv_attachment);
        }


        LinearLayout lyt_btn_delete = view.findViewById(R.id.lyt_btn_delete);
        lyt_btn_delete.setOnClickListener(v -> {
            if (isEditTask)
                deleteEditTaskAttachment(attachmentArrayList.get(currentPosition).getId());
            rcAttachment.removeViewAt(currentPosition);
            attachmentArrayList.remove(currentPosition);
            attachmentAdapter.notifyItemRemoved(currentPosition);
            attachmentAdapter.notifyItemRangeRemoved(currentPosition, attachmentArrayList.size());

            mBottomSheetDialog.dismiss();
        });


        LinearLayout lyt_btn_back = view.findViewById(R.id.lyt_btn_back);
        lyt_btn_back.setOnClickListener(v -> {
            mBottomSheetDialog.dismiss();
        });

        // set background transparent
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(dialog -> mBottomSheetDialog = null);
    }

    @SuppressLint("SetTextI18n")
    private void showBottomSheetAddMustHave(boolean justInit) {
        @SuppressLint("InflateParams") final View view = getLayoutInflater().inflate(R.layout.sheet_add_must_have, null);

        mBottomSheetDialog = new BottomSheetDialog(taskCreateActivity);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        TextView txtCount = view.findViewById(R.id.txt_count);
        RecyclerView recyclerAddMustHaveBottomSheet = view.findViewById(R.id.recycler_add_must_have_bottom_sheet);
        TextView txtTotalCount = view.findViewById(R.id.txt_total_count);

        FrameLayout btnAdd = view.findViewById(R.id.btn_add);
        MaterialButton btnClose = view.findViewById(R.id.btn_close);
        RelativeLayout root = view.findViewById(R.id.root_bottom_sheet);
        RelativeLayout relRequire = view.findViewById(R.id.rel_require);
        EditText edtAddTag = view.findViewById(R.id.edtAddTag);
        Resources r = getResources();
        int px = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 600,r.getDisplayMetrics()));
        int pxMin = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 400,r.getDisplayMetrics()));
        edtAddTag.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                root.setMinimumHeight(px);
            else
                root.setMinimumHeight(pxMin);
        });
        relRequire.setOnClickListener(v->{
            edtAddTag.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtAddTag, InputMethodManager.SHOW_IMPLICIT);
        });
        edtAddTag.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEND) {
                btnAdd.performClick();
                return true;
            }
            return false;
        });
        btnClose.setOnClickListener(v -> {
            mBottomSheetDialog.dismiss();
        });
        recyclerAddMustHaveBottomSheet.setLayoutManager(new GridLayoutManager(taskCreateActivity, 1));
        recyclerAddMustHaveBottomSheet.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(taskCreateActivity, 5), true));
        recyclerAddMustHaveBottomSheet.setHasFixedSize(true);

        tagAdapterBottomSheet = new AddTagAdapter(addTagList, data -> {
            addTagList.remove(data);
            tagAdapterBottomSheet.updateItem(addTagList);
            tagAdapter.updateItem(addTagList);
            btnAdd.setEnabled(addTagList.size() < 3);
        });

        recyclerAddMustHaveBottomSheet.setAdapter(tagAdapterBottomSheet);
        txtCount.setText(addTagList.size() + "");

        btnAdd.setOnClickListener(v -> {
                    if (addTagList.size() == 0)
                        relReqSmall.setVisibility(View.INVISIBLE);
                    else if (addTagList.size() < 4)
                        relReqSmall.setVisibility(View.INVISIBLE);

                    if (TextUtils.isEmpty(edtAddTag.getText().toString().trim())) {
                        edtAddTag.setError("Text is empty");
                        return;
                    }

                    if (addTagList.size() < 3) {
                        btnAdd.setEnabled(addTagList.size() != 2);
                        txtCount.setText(addTagList.size() + 1 + "");
                        addTagList.add(edtAddTag.getText().toString().trim());
                        tagAdapterBottomSheet.updateItem(addTagList);
                        tagAdapter.updateItem(addTagList);
                        edtAddTag.setText("");
                    } else {
                        taskCreateActivity.showToast("you can add only 3 requirements", taskCreateActivity);
                    }

                    if (addTagList.size() == 3)
                        mBottomSheetDialog.dismiss();

                    if (addTagList.size() == 0) {
                        relReqSmall.setVisibility(View.GONE);
                        rltAddMustHave.setVisibility(View.VISIBLE);
                        tvRequireTitle.setTextColor(getResources().getColor(R.color.N100));
                    } else if (addTagList.size() < 4) {
                        tvRequireTitle.setTextColor(getResources().getColor(R.color.P300));
                        relReqSmall.setVisibility(View.VISIBLE);
                        rltAddMustHave.setVisibility(View.GONE);
                    }
                }
        );

        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        if (!justInit) {
            mBottomSheetDialog.show();
            mBottomSheetDialog.setOnDismissListener(dialog -> mBottomSheetDialog = null);
        }
    }

    private void uploadDataInTempApi(File pictureFile) {
        taskCreateActivity.showProgressDialog();
        Call<String> call;
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile);
        MultipartBody.Part imageFile = MultipartBody.Part.createFormData("media", pictureFile.getName(), requestFile);
        call = ApiClient.getClient().getTaskTempAttachmentMediaData(/*"application/x-www-form-urlencoded",*/ "XMLHttpRequest", sessionManager.getTokenType() + " " + sessionManager.getAccessToken(), imageFile);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                taskCreateActivity.hideProgressDialog();
                if (response.code() == 422) {
                    taskCreateActivity.showToast(response.message(), taskCreateActivity);
                    return;
                }

                try {
                    String strResponse = response.body();
                    JSONObject jsonObject = new JSONObject(strResponse);
                    if (jsonObject.has("data")) {
                        JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                        AttachmentModel attachment = new AttachmentModel().getJsonToModel(jsonObject_data);
                        if (attachmentArrayList.size() == 0) {
                            attachmentArrayList.add(attachment);
                        } else {
                            attachmentArrayList.size();
                            attachmentArrayList.add(attachmentArrayList.size(), attachment);
                        }
                        if (attachmentArrayList.size() > 0) {
                            addAttach.setVisibility(View.GONE);
                            tvAttachTitle.setTextColor(getResources().getColor(R.color.P300));
                            addAttachSmall.setVisibility(View.VISIBLE);
                        } else {
                            tvAttachTitle.setTextColor(getResources().getColor(R.color.N100));
                            addAttach.setVisibility(View.VISIBLE);
                            addAttachSmall.setVisibility(View.GONE);
                        }

                    }
                    attachmentAdapter.notifyItemInserted(attachmentArrayList.size() - 1);

                } catch (JSONException e) {
                    taskCreateActivity.showToast("Something went wrong", taskCreateActivity);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                ((AppController) getContext()).mCrashlytics.recordException(t);
                t.printStackTrace();
                taskCreateActivity.hideProgressDialog();
            }
        });
    }

    private void uploadDataForEditTask(File pictureFile) {
        if (taskSlug == null) return;
        taskCreateActivity.showProgressDialog();
        Call<String> call;
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile);
        MultipartBody.Part imageFile = MultipartBody.Part.createFormData("media", pictureFile.getName(), requestFile);
        call = ApiClient.getClient().getTasKAttachmentMediaUpload(taskSlug, "XMLHttpRequest", sessionManager.getTokenType() + " " + sessionManager.getAccessToken(), imageFile);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                taskCreateActivity.hideProgressDialog();
                if (response.code() == 422) {
                    taskCreateActivity.showToast(response.message(), taskCreateActivity);
                    return;
                }

                try {
                    String strResponse = response.body();
                    JSONObject jsonObject = new JSONObject(strResponse);
                    if (jsonObject.has("data")) {
                        JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                        AttachmentModel attachment = new AttachmentModel().getJsonToModel(jsonObject_data);
                        if (attachmentArrayList.size() != 0) {
                            attachmentArrayList.add(attachmentArrayList.size() - 1, attachment);
                        }
                    }
                    attachmentAdapter.notifyItemInserted(attachmentArrayList.size() - 1);

                } catch (JSONException e) {
                    taskCreateActivity.showToast("Something went wrong", taskCreateActivity);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                ((AppController) getContext()).mCrashlytics.recordException(t);
                t.printStackTrace();
                taskCreateActivity.hideProgressDialog();
            }
        });
    }


    private void selectDetailsBtn() {
        ColorStateList csl_primary = AppCompatResources.getColorStateList(getContext(), R.color.colorPrimary);
        imgDetails.setImageTintList(csl_primary);
        txtDetails.setTextColor(getResources().getColor(R.color.colorPrimary));
        Typeface face = ResourcesCompat.getFont(getActivity(), R.font.roboto_medium);
        txtDetails.setTypeface(face);
        ColorStateList csl_grey = AppCompatResources.getColorStateList(getContext(), R.color.greyC4C4C4);
        imgDateTime.setImageTintList(csl_grey);
        imgBudget.setImageTintList(csl_grey);
        txtDateTime.setTextColor(getResources().getColor(R.color.colorGrayC9C9C9));
        txtBudget.setTextColor(getResources().getColor(R.color.colorGrayC9C9C9));
        tabClickListener();
    }

    private void tabClickListener() {
        if (getActivity() != null && getValidationCode() == 0) {
            lytBtnDetails.setOnClickListener(v -> ((TaskCreateActivity) requireActivity()).onViewClicked(v));
            lytBntDateTime.setOnClickListener(v -> ((TaskCreateActivity) requireActivity()).onViewClicked(v));
            lytBtnBudget.setOnClickListener(v -> ((TaskCreateActivity) requireActivity()).onViewClicked(v));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        uploadableImage.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 25) {
            addTagList.clear();
            addTagList.addAll(data.getStringArrayListExtra("TAG"));
            tagAdapter.notifyDataSetChanged();
            tagAdapterBottomSheet.notifyDataSetChanged();
        }

        if (requestCode == PLACE_SELECTION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            txtSuburb.setText(SuburbAutoComplete.getSuburbName(data));
            PositionModel positionModel = new PositionModel();
            positionModel.setLatitude(SuburbAutoComplete.getLatitudeDouble(data));
            positionModel.setLongitude(SuburbAutoComplete.getLongitudeDouble(data));
            task.setLocation(SuburbAutoComplete.getSuburbName(data));
            task.setPosition(positionModel);
        }
    }
}
