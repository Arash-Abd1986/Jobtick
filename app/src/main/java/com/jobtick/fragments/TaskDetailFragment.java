package com.jobtick.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.jobtick.AppController;
import com.jobtick.R;
import com.jobtick.activities.AbstractUploadableImageImpl;
import com.jobtick.activities.UploadableImage;
import com.jobtick.activities.TaskCreateActivity;
import com.jobtick.adapers.AddTagAdapter;
import com.jobtick.adapers.AttachmentAdapter1;
import com.jobtick.models.AttachmentModel;
import com.jobtick.models.PositionModel;
import com.jobtick.models.TaskModel;
import com.jobtick.models.task.AttachmentModels;
import com.jobtick.retrofit.ApiClient;
import com.jobtick.utils.SessionManager;
import com.jobtick.utils.SuburbAutoComplete;
import com.jobtick.utils.Tools;
import com.jobtick.widget.ExtendedCommentText;
import com.jobtick.widget.ExtendedEntryText;
import com.jobtick.widget.SpacingItemDecoration;

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

public class TaskDetailFragment extends Fragment implements AttachmentAdapter1.OnItemClickListener {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_details)
    LinearLayout lytBtnDetails;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_bnt_date_time)
    LinearLayout lytBntDateTime;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_budget)
    LinearLayout lytBtnBudget;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_title)
    ExtendedCommentText edtTitle;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_description)
    ExtendedCommentText edtDescription;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_add_must_have)
    RecyclerView recyclerAddMustHave;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.img_add_white)
    ImageView imgAddWhite;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.rlt_add_must_have)
    RelativeLayout rltAddMustHave;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.checkbox_online)
    CheckBox checkboxOnline;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_suburb)
    ExtendedEntryText txtSuburb;
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

    private final ArrayList<AttachmentModel> attachmentArrayList = new ArrayList<>();

    public static TaskDetailFragment newInstance(String title, String description, ArrayList<String> musthave,
                                                 String task_type, String location, PositionModel positionModel, AttachmentModels attachmentModels, OperationsListener operationsListener) {
        TaskDetailFragment fragment = new TaskDetailFragment();
        fragment.operationsListener = operationsListener;
        Bundle args = new Bundle();
        args.putString("TITLE", title);
        args.putString("DESCRIPTION", description);
        args.putStringArrayList("MUSTHAVE", musthave);
        args.putString("TASK_TYPE", task_type);
        args.putString("LOCATION", location);
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
                uploadDataInTempApi(imageFile);
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
        attachmentArrayList.add(new AttachmentModel());
        selectDetailsBtn();

        addTagList = new ArrayList<>();
        task.setTitle(getArguments().getString("TITLE"));
        task.setDescription(getArguments().getString("DESCRIPTION"));
        task.setMusthave(getArguments().getStringArrayList("MUSTHAVE"));
        task.setTaskType(getArguments().getString("TASK_TYPE"));
        task.setLocation(getArguments().getString("LOCATION"));
        task.setPosition(getArguments().getParcelable("POSITION"));
        task.setAttachments(new ArrayList<>(((AttachmentModels) getArguments().getParcelable("ATTACHMENT")).getAttachmentModelList()));


        taskCreateActivity.setActionDraftTaskDetails(taskModel -> {

            if (edtTitle.getText().toString().trim().length() >= 10) {
                taskModel.setTitle(edtTitle.getText().toString().trim());
            }
            if (edtDescription.getText().toString().trim().length() >= 25) {
                taskModel.setDescription(edtDescription.getText().toString().trim());
            }
            if (!TextUtils.isEmpty(txtSuburb.getText().toString().trim())) {
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

        txtSuburb.setExtendedViewOnClickListener(() -> {
            Intent intent = new SuburbAutoComplete(requireActivity()).getIntent();
            startActivityForResult(intent, PLACE_SELECTION_REQUEST_CODE);
        });

        setComponent();
        init();

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
        recyclerAddMustHave.setLayoutManager(new GridLayoutManager(taskCreateActivity, 1));
        recyclerAddMustHave.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(taskCreateActivity, 5), true));
        recyclerAddMustHave.setHasFixedSize(true);
        tagAdapter = new AddTagAdapter(addTagList, data -> {
            addTagList.remove(data);
            tagAdapter.updateItem(addTagList);
            tagAdapterBottomSheet.updateItem(addTagList);
        });

        recyclerAddMustHave.setAdapter(tagAdapter);
        rcAttachment.setLayoutManager(new GridLayoutManager(taskCreateActivity, 4));
        rcAttachment.addItemDecoration(new SpacingItemDecoration(4, Tools.dpToPx(taskCreateActivity, 5), true));
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
    @OnClick({R.id.rlt_add_must_have, R.id.lyt_btn_details, R.id.lyt_bnt_date_time, R.id.lyt_btn_budget, R.id.checkbox_online, R.id.btn_next
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rlt_add_must_have:
                showBottomSheetAddMustHave();
                break;
            case R.id.lyt_btn_details:
                break;
            case R.id.lyt_bnt_date_time:
                break;
            case R.id.lyt_btn_budget:
                break;
            case R.id.checkbox_online:
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
                                edtTitle.getText().toString().trim(),
                                edtDescription.getText().toString().trim(),
                                addTagList,
                                checkboxOnline.isChecked() ? "remote" : "physical",
                                txtSuburb.getText().toString().trim(),
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
        if (TextUtils.isEmpty(edtTitle.getText().toString().trim()) || edtTitle.getText().toString().trim().length() < 10) {
            return 1;
        } else if (TextUtils.isEmpty(edtDescription.getText().toString().trim()) || edtDescription.getText().toString().trim().length() < 25) {
            return 2;
        } else if (!checkboxOnline.isChecked()) {
            if (TextUtils.isEmpty(txtSuburb.getText().toString().trim())) {
                return 3;
            }
        }
        return 0;
    }

    @Override
    public void onItemClick(View view, AttachmentModel obj, int position, String action) {
        if (action.equalsIgnoreCase("add")) {
            uploadableImage.showAttachmentImageBottomSheet(false);

        } else if (action.equalsIgnoreCase("delete")) {
            rcAttachment.removeViewAt(position);
            attachmentArrayList.remove(position);
            attachmentAdapter.notifyItemRemoved(position);
            attachmentAdapter.notifyItemRangeRemoved(position, attachmentArrayList.size());
        } else if (action.equalsIgnoreCase("show")) {
            showBottomSheetDialogViewFullImage(obj.getModalUrl(), position);
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
    private void showBottomSheetAddMustHave() {
        @SuppressLint("InflateParams") final View view = getLayoutInflater().inflate(R.layout.sheet_add_must_have, null);

        mBottomSheetDialog = new BottomSheetDialog(taskCreateActivity);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        TextView txtCount = view.findViewById(R.id.txt_count);
        RecyclerView recyclerAddMustHaveBottomSheet = view.findViewById(R.id.recycler_add_must_have_bottom_sheet);
        TextView txtTotalCount = view.findViewById(R.id.txt_total_count);

        MaterialButton btnAdd = view.findViewById(R.id.btn_add);
        EditText edtAddTag = view.findViewById(R.id.edtAddTag);
        edtAddTag.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND) {
                    btnAdd.performClick();
                    return true;
                }
                return false;
            }
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
                        taskCreateActivity.showToast("Max. 3 Tag you can add", taskCreateActivity);
                    }
                }
        );

        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(dialog -> mBottomSheetDialog = null);
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
