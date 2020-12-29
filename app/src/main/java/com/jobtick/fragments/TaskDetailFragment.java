package com.jobtick.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.jobtick.BuildConfig;
import com.jobtick.R;
import com.jobtick.activities.TaskCreateActivity;
import com.jobtick.adapers.AddTagAdapter;
import com.jobtick.adapers.AttachmentAdapter1;
import com.jobtick.models.AttachmentModel;
import com.jobtick.models.PositionModel;
import com.jobtick.models.TaskModel;
import com.jobtick.models.task.AttachmentModels;
import com.jobtick.retrofit.ApiClient;
import com.jobtick.utils.ImageUtil;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

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

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public static final String VIDEO_FORMAT = ".mp4";
    public static final String VIDEO_SIGN = "VID_";
    private static final int VIDEO_CAPTURE = 101;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    public static final long MAX_VIDEO_DURATION = 30;
    public static final long MAX_VIDEO_SIZE = 20 * 1024 * 1024;
    public static final String MEDIA_DIRECTORY_NAME = "Jobtick";
    public static final String TIME_STAMP_FORMAT = "yyyyMMdd_HHmmss";
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int PICK_VIDEO = 107;

    private AttachmentAdapter1 attachmentAdapter;
    private BottomSheetDialog mBottomSheetDialog;
    private TaskCreateActivity taskCreateActivity;
    private AddTagAdapter tagAdapter;
    private AddTagAdapter tagAdapterBottomSheet;
    private ArrayList<String> addTagList;
    private OperationsListener operationsListener;
    private TaskModel task;
    private SessionManager sessionManager;

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

            if(attachmentArrayList != null)
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

        if (task.getAttachments() != null && !task.getAttachments().isEmpty()) {
            attachmentArrayList.addAll(task.getAttachments());
        }
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
            showBottomSheetDialog();
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
            ImageUtil.displayImage(iv_attachment, url, null);
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

    private void showBottomSheetDialog() {
        @SuppressLint("InflateParams") final View view = getLayoutInflater().inflate(R.layout.sheet_attachment, null);
        LinearLayout lytBtnCamera = view.findViewById(R.id.lyt_btn_camera);
        LinearLayout lytBtnImage = view.findViewById(R.id.lyt_btn_image);
        LinearLayout lytBtnVideo = view.findViewById(R.id.lyt_btn_video);
        LinearLayout lytBtnDoc = view.findViewById(R.id.lyt_btn_doc);
        LinearLayout lyrBtnVideoCamera = view.findViewById(R.id.lyt_btn_video_camera);

        lytBtnCamera.setOnClickListener(view1 -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (getContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
            mBottomSheetDialog.hide();
        });
        lytBtnVideo.setOnClickListener(view12 -> {
            getGalleryVideo();
            mBottomSheetDialog.hide();
        });
        lyrBtnVideoCamera.setOnClickListener(view13 -> {
            recordVideo();
            mBottomSheetDialog.hide();
        });
        lytBtnDoc.setOnClickListener(view14 -> {
            Intent intent = new Intent();
            intent.setType("application/pdf");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select PDF File"), 1001);
            mBottomSheetDialog.hide();
        });

        lytBtnImage.setOnClickListener(v -> {
            if (checkPermissionREAD_EXTERNAL_STORAGE(taskCreateActivity)) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
            mBottomSheetDialog.hide();
        });

        mBottomSheetDialog = new BottomSheetDialog(taskCreateActivity);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        // set background transparent
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(dialog -> mBottomSheetDialog = null);

    }

    private void getGalleryVideo() {
        @SuppressLint("IntentReset") Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_VIDEO);

    }

    private void recordVideo() {

        Uri fileUri;
        try {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            fileUri = getOutputMediaFileUri();
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAX_VIDEO_DURATION);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, MAX_VIDEO_SIZE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
            // start the video capture Intent
            startActivityForResult(intent, VIDEO_CAPTURE);

        } catch (Exception e) {

            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

            fileUri = getOutputMediaFileUri1();

            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAX_VIDEO_DURATION);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, MAX_VIDEO_SIZE);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file

            // start the video capture Intent
            startActivityForResult(intent, VIDEO_CAPTURE);
        }
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            READ_EXTERNAL_STORAGE);
                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[]{READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                (dialog, which) -> ActivityCompat.requestPermissions((Activity) context,
                        new String[]{permission},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE));
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private Uri getOutputMediaFileUri1() {
        return FileProvider.getUriForFile(taskCreateActivity, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile());
    }

    private static File getOutputMediaFile() {

        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                MEDIA_DIRECTORY_NAME);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }


        File mediaFile;
        if (TaskDetailFragment.MEDIA_TYPE_VIDEO == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + VIDEO_SIGN + getTimeStamp() + VIDEO_FORMAT);
        } else {
            return null;
        }
        return mediaFile;
    }

    private static String getTimeStamp() {
        return new SimpleDateFormat(TIME_STAMP_FORMAT,
                Locale.getDefault()).format(new Date());
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
                taskCreateActivity.hideProgressDialog();
            }
        });
    }

    public String getPath(Uri uri) {
        Cursor cursor = taskCreateActivity.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = taskCreateActivity.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();


        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

        Timber.e(path);
        cursor.close();

        return path;
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
        if (requestCode == 25) {
            addTagList.clear();
            addTagList.addAll(data.getStringArrayListExtra("TAG"));
            tagAdapter.notifyDataSetChanged();
            tagAdapterBottomSheet.notifyDataSetChanged();
        }

        if (requestCode == PLACE_SELECTION_REQUEST_CODE && resultCode == requireActivity().RESULT_OK) {

            txtSuburb.setText(SuburbAutoComplete.getSuburbName(data));
            PositionModel positionModel = new PositionModel();
            positionModel.setLatitude(SuburbAutoComplete.getLatitudeDouble(data));
            positionModel.setLongitude(SuburbAutoComplete.getLongitudeDouble(data));
            task.setLocation(SuburbAutoComplete.getSuburbName(data));
            task.setPosition(positionModel);
        }

        if (requestCode == 1 && resultCode == requireActivity().RESULT_OK) {
            Uri filePath = data.getData();
            String imagePath = getPath(data.getData());
            File file = new File(imagePath);
            uploadDataInTempApi(file);
        }

        if (requestCode == CAMERA_REQUEST && resultCode == requireActivity().RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            File pictureFile = new File(taskCreateActivity.getExternalCacheDir(), "jobtick");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(pictureFile);
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                }
                fos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            uploadDataInTempApi(pictureFile);
        }
    }
}
