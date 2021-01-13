package com.jobtick.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.camera2.CameraCharacteristics;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jobtick.R;
import com.jobtick.activities.ActivityBase;
import com.jobtick.activities.MakeAnOfferActivity;
import com.jobtick.activities.TaskDetailsActivity;
import com.jobtick.activities.VideoPlayerActivity;
import com.jobtick.adapers.AttachmentAdapter;
import com.jobtick.models.AttachmentModel;
import com.jobtick.models.MakeAnOfferModel;
import com.jobtick.retrofit.ApiClient;
import com.jobtick.utils.CameraUtils;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.ImageUtil;
import com.jobtick.utils.SessionManager;
import com.jobtick.utils.Tools;
import com.jobtick.widget.ExtendedCommentText;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
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

import static android.app.Activity.RESULT_OK;
import static com.jobtick.utils.Constant.URL_VIDEO_GUIDELINE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MakeAnOfferAboutFragment extends Fragment implements View.OnClickListener {


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_description)
    ExtendedCommentText edtDescription;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.checkbox_save_as_template)
    CheckBox checkboxSaveAsTemplate;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.saveQuickOfferTxt)
    TextView saveQuickOfferTxt;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.quickOfferDesc)
    TextView quickOfferDesc;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_make_a_live_video)
    LinearLayout lytBtnMakeALiveVideo;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lytRecord2)
    LinearLayout lytRecord2;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_continue)
    MaterialButton lytBtnContinue;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.guideline_video)
    TextView gxtGuideLineVideo;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.card_live_video)
    CardView cardLiveVideo;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.ivBack)
    ImageView ivBack;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.imgThumbnail)
    ImageView imgThumbnail;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.LytVideoPlay)
    RelativeLayout LytVideoPlay;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.llPlayVideo)
    LinearLayout llPlayVideo;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.bottom_sheet)
    FrameLayout bottomSheet;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.llJobDetails)
    LinearLayout llJobDetails;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.llCancelVideo)
    LinearLayout llCancelVideo;


    public static final long MAX_VIDEO_DURATION = 30;
    public static final long MAX_VIDEO_SIZE = 20 * 1024 * 1024;

    private MakeAnOfferModel makeAnOfferModel;
    private MakeAnOfferActivity makeAnOfferActivity;
    private AboutCallbackFunction aboutCallbackFunction;
    private String quickOffer;


    // key to store image path in savedInstance state
    private static final String KEY_IMAGE_STORAGE_PATH = "image_path";
    private static String imageStoragePath;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private SessionManager sessionManager;

    private BottomSheetDialog mBottomSheetDialog;
    private BottomSheetBehavior mBehavior;

    public static MakeAnOfferAboutFragment newInstance(MakeAnOfferModel makeAnOfferModel, AboutCallbackFunction aboutCallbackFunction) {

        Bundle args = new Bundle();
        args.putParcelable(ConstantKey.MAKE_AN_OFFER_MODEL, makeAnOfferModel);
        MakeAnOfferAboutFragment fragment = new MakeAnOfferAboutFragment();
        fragment.aboutCallbackFunction = aboutCallbackFunction;
        fragment.setArguments(args);
        return fragment;
    }

    public MakeAnOfferAboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_make_an_offer_about, container, false);
        ButterKnife.bind(this, view);
        mBehavior = BottomSheetBehavior.from(bottomSheet);
        sessionManager = new SessionManager(getContext());

        quickOffer = sessionManager.getQuickOffer();
        setQuickOffer(quickOffer, "");

        edtDescription.setTextWatcher(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                    @Override
                    public void afterTextChanged(Editable editable) {
                        setQuickOffer(quickOffer, editable.toString());
                    }
                });

        LytVideoPlay.setVisibility(View.GONE);
        return view;
    }

    private void setQuickOffer(String quickOffer, String currentText) {
        if (!quickOffer.equals("")) {
            if (currentText.equals(quickOffer)) {
                saveQuickOfferTxt.setEnabled(false);
            } else if (currentText.length() < edtDescription.geteMinSize()) {
                saveQuickOfferTxt.setEnabled(true);
                saveQuickOfferTxt.setText(R.string.use_quick_offer);
                loadQuickOffer();
                saveQuickOfferTxt.setOnClickListener(v -> {
                    edtDescription.setText(quickOffer);
                    edtDescription.setSelection(edtDescription.getText().length());
                });
            } else {
                saveQuickOfferTxt.setOnClickListener(v -> {
                    saveQuickOffer(currentText);
                });
                saveQuickOfferTxt.setEnabled(true);
                saveQuickOfferTxt.setText(R.string.update_quick_offer);
            }
        } else {
            saveQuickOfferTxt.setText(R.string.save_as_a_Quick_offer);

            saveQuickOfferTxt.setOnClickListener(v -> {
                saveQuickOffer(currentText);
            });

            if (currentText.length() < edtDescription.geteMinSize()) {
                saveQuickOfferTxt.setEnabled(false);
                saveQuickOfferTxt.setAlpha(0.5f);
            } else {
                saveQuickOfferTxt.setEnabled(true);
                saveQuickOfferTxt.setAlpha(1f);
            }
        }
    }

    private void saveQuickOffer(String currentQuickOffer) {
        ((ActivityBase) requireActivity()).showSuccessToast("Quick offer saved", getContext());
        quickOfferDesc.setText(String.format(Locale.ENGLISH, "%s...", currentQuickOffer.trim().substring(0, Math.min(currentQuickOffer.trim().length() - 1, 19))));
        sessionManager.setQuickOffer(currentQuickOffer);
    }

    private void loadQuickOffer() {
        String quickOffer = sessionManager.getQuickOffer();
        quickOfferDesc.setText(String.format(Locale.ENGLISH, "%s...", quickOffer.trim().substring(0, Math.min(quickOffer.trim().length() - 1, 19))));
    }

    private void setLayoout() {
        // cardLiveVideo.setVisibility(View.GONE);
        // cardLiveVideo.setVisibility(View.VISIBLE);
        checkboxSaveAsTemplate.setChecked(makeAnOfferModel.isCheckbok());
        if (makeAnOfferModel.getMessage() != null) {
            edtDescription.setText(makeAnOfferModel.getMessage());

        }
    }

    private void showJobDetailDialog() {


        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.sheet_job_details, null);

        TextView tvJobTitle = (TextView) view.findViewById(R.id.tvJobTitle);
        TextView tvPosterName = (TextView) view.findViewById(R.id.tvPosterName);
        TextView tvLocation = (TextView) view.findViewById(R.id.tvLocation);
        TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
        TextView tvDesc = (TextView) view.findViewById(R.id.tvDesc);

        tvJobTitle.setText(TaskDetailsActivity.taskModel.getTitle());
        tvPosterName.setText(TaskDetailsActivity.taskModel.getPoster().getName());
        if(TaskDetailsActivity.taskModel.getLocation() != null)
            tvLocation.setText(TaskDetailsActivity.taskModel.getLocation());
        else
            tvLocation.setText(R.string.remotely);

        tvDesc.setText(TaskDetailsActivity.taskModel.getDescription());

        //
        tvDate.setText(Tools.getDayMonthDateTimeFormat2(TaskDetailsActivity.taskModel.getDueDate()));

        CircularImageView imgAvtarPoster = (CircularImageView) view.findViewById(R.id.ivAvatar);
        if (TaskDetailsActivity.taskModel.getPoster().getAvatar() != null && TaskDetailsActivity.taskModel.getPoster().getAvatar().getThumbUrl() != null) {
            ImageUtil.displayImage(imgAvtarPoster, TaskDetailsActivity.taskModel.getPoster().getAvatar().getThumbUrl(), null);
        } else {
            //TODO DUMMY IMAGE
        }

        mBottomSheetDialog = new BottomSheetDialog(requireActivity());
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        // set background transparent
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        makeAnOfferActivity = (MakeAnOfferActivity) requireActivity();


        if (makeAnOfferActivity != null) {
            sessionManager = new SessionManager(makeAnOfferActivity);
        }
        makeAnOfferModel = new MakeAnOfferModel();
        if (getArguments() != null && getArguments().getParcelable(ConstantKey.MAKE_AN_OFFER_MODEL) != null) {
            makeAnOfferModel = (MakeAnOfferModel) getArguments().getParcelable(ConstantKey.MAKE_AN_OFFER_MODEL);
        }
        if (makeAnOfferModel != null) {
            setLayoout();
        }
        // toolbar.setNavigationOnClickListener(MakeAnOfferAboutFragment.this);
        checkboxSaveAsTemplate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
              /*  if(isChecked){
                    cardLiveVideo.setVisibility(View.GONE);
                }else{
                    cardLiveVideo.setVisibility(View.VISIBLE);
                }*/
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeAnOfferActivity.onBackPressed();
            }
        });
    }


    @OnClick({R.id.lyt_btn_make_a_live_video, R.id.lyt_btn_continue, R.id.lytRecord2, R.id.llJobDetails, R.id.llCancelVideo, R.id.guideline_video})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lytRecord2:

            case R.id.lyt_btn_make_a_live_video:
                // Checking availability of the camera
                if (!CameraUtils.isDeviceSupportCamera(makeAnOfferActivity)) {
                    ((ActivityBase) requireActivity()).showToast("Sorry! Your device doesn't support camera", requireContext());
                    // will close the app if the device doesn't have camera
                    //finish();
                    return;
                }

                if (CameraUtils.checkPermissions(makeAnOfferActivity)) {
                    captureVideo();
                } else {
                    requestCameraPermission(ConstantKey.MEDIA_TYPE_VIDEO);
                }
                break;

            case R.id.lyt_btn_continue:
                switch (validation()) {
                    case 1:
                        edtDescription.setError("Please enter your description in video or text");
                        break;
                    case 2:
                        checkboxSaveAsTemplate.setError("Please enter only video or text");
                        break;
                    case 0:
                        if (aboutCallbackFunction != null) {
                            makeAnOfferModel.setMessage(edtDescription.getText().toString().trim());
                            makeAnOfferModel.setCheckbok(checkboxSaveAsTemplate.isChecked());
                            aboutCallbackFunction.continueButtonAbout(makeAnOfferModel);
                        }
                        break;
                }
                break;
            case R.id.llJobDetails:
                showJobDetailDialog();
                break;

            case R.id.llCancelVideo:
                lytBtnMakeALiveVideo.setVisibility(View.VISIBLE);
                LytVideoPlay.setVisibility(View.GONE);
                edtDescription.setText("");
                imageStoragePath = "";
                break;

            case R.id.guideline_video:
                Intent intent = new Intent(requireActivity(), VideoPlayerActivity.class);
                intent.putExtra(ConstantKey.VIDEO_PATH, URL_VIDEO_GUIDELINE);
                requireActivity().startActivity(intent);
                break;
        }
    }

    private int validation() {
        if (edtDescription.getText().length() < edtDescription.geteMinSize() && makeAnOfferModel.getAttachment() == null) {
            return 1;
        } else if (!TextUtils.isEmpty(edtDescription.getText().toString().trim()) && makeAnOfferModel.getAttachment() != null) {
            return 2;
        }
        return 0;
    }

    /**
     * Requesting permissions using Dexter library
     */
    private void requestCameraPermission(final int type) {
        Dexter.withContext(makeAnOfferActivity)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            if (type == ConstantKey.MEDIA_TYPE_IMAGE) {
                                // capture picture
                                //captureImage();
                            } else {
                                captureVideo();
                            }

                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            makeAnOfferActivity.showPermissionsAlert();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }


    /**
     * Launching camera app to record video
     */
    private void captureVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        File file = CameraUtils.getOutputMediaFile(ConstantKey.MEDIA_TYPE_VIDEO);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }
        Uri fileUri = CameraUtils.getOutputMediaFileUri(makeAnOfferActivity, file);
        // set video quality
        // intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAX_VIDEO_DURATION);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, MAX_VIDEO_SIZE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            intent.putExtra("android.intent.extras.CAMERA_FACING", CameraCharacteristics.LENS_FACING_FRONT);
            intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);// Tested on API 24 Android version 7.0(Samsung S6)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra("android.intent.extras.CAMERA_FACING", CameraCharacteristics.LENS_FACING_FRONT); // Tested on API 27 Android version 8.0(Nexus 6P)
            intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
        } else {
            intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        }
        // Tested API 21 Android version 5.0.1(Samsung S4)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
        // start the video capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    /**
     * Activity result method will be called after closing the camera
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(makeAnOfferActivity, imageStoragePath);
                uploadDataInTempApi(new File(Uri.parse("file://" + imageStoragePath).getPath()));

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // user cancelled recording
                ((ActivityBase) requireActivity()).showToast(
                        "User cancelled video recording", requireContext());
            } else {
                // failed to record video
                ((ActivityBase) requireActivity()).showToast(
                        "Sorry! Failed to record video", requireContext());
            }
        }
    }


    private void uploadDataInTempApi(File pictureFile) {
        makeAnOfferActivity.showProgressDialog();
        Call<String> call;

        //    File file = new File(imagePath);
        //    Log.e("AttachmentApi: ", file.getName());
        //   Log.e("uploadProfile++:11 ", imagePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile);
        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part imageFile = MultipartBody.Part.createFormData("media", pictureFile.getName(), requestFile);
        call = ApiClient.getClient().getTaskTempAttachmentMediaData(/*"application/x-www-form-urlencoded",*/ "XMLHttpRequest", sessionManager.getTokenType() + " " + sessionManager.getAccessToken(), imageFile);


        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                makeAnOfferActivity.hideProgressDialog();
                Timber.tag("Response").e(response.toString());
                if (response.code() == 422) {
                    makeAnOfferActivity.showToast(response.message(), makeAnOfferActivity);
                    return;
                }

                try {
                    String strResponse = response.body();
                    Timber.tag("body").e(strResponse);
                    JSONObject jsonObject = new JSONObject(strResponse);
                    Timber.e(jsonObject.toString());
                    if (jsonObject.has("data")) {
                        AttachmentModel attachment = new AttachmentModel();
                        JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                        if (jsonObject_data.has("id") && !jsonObject_data.isNull("id"))
                            attachment.setId(jsonObject_data.getInt("id"));
                        if (jsonObject_data.has("name") && !jsonObject_data.isNull("name"))
                            attachment.setName(jsonObject_data.getString("name"));
                        if (jsonObject_data.has("file_name") && !jsonObject_data.isNull("file_name"))
                            attachment.setFileName(jsonObject_data.getString("file_name"));
                        if (jsonObject_data.has("mime") && !jsonObject_data.isNull("mime"))
                            attachment.setMime(jsonObject_data.getString("mime"));
                        if (jsonObject_data.has("url") && !jsonObject_data.isNull("url"))
                            attachment.setUrl(jsonObject_data.getString("url"));
                        if (jsonObject_data.has("thumb_url") && !jsonObject_data.isNull("thumb_url"))
                            attachment.setThumbUrl(jsonObject_data.getString("thumb_url"));
                        if (jsonObject_data.has("modal_url") && !jsonObject_data.isNull("modal_url"))
                            attachment.setModalUrl(jsonObject_data.getString("modal_url"));
                        if (jsonObject_data.has("created_at") && !jsonObject_data.isNull("created_at"))
                            attachment.setCreatedAt(jsonObject_data.getString("created_at"));
                        attachment.setType(AttachmentAdapter.VIEW_TYPE_IMAGE);
                        makeAnOfferModel.setAttachment(attachment);
                    }

                    // aboutCallbackFunction.continueButtonAbout(makeAnOfferModel);

                    lytBtnMakeALiveVideo.setVisibility(View.GONE);
                    LytVideoPlay.setVisibility(View.VISIBLE);
                    ImageUtil.displayImage(imgThumbnail, makeAnOfferModel.getAttachment().getModalUrl(), null);
                    //ImageUtil.displayImage(imgThumbnail, "https://images.wallpaperscraft.com/image/road_asphalt_marking_130996_1280x720.jpg", null);


                    llPlayVideo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(requireActivity(), VideoPlayerActivity.class);
                            intent.putExtra(ConstantKey.VIDEO_PATH, "" + makeAnOfferModel.getAttachment().getUrl());
                            requireActivity().startActivity(intent);
                        }
                    });

                    //https://images.wallpaperscraft.com/image/road_asphalt_marking_130996_1280x720.jpg

                    //  adapter.notifyItemRangeInserted(0,attachmentArrayList.size());
                } catch (JSONException e) {
                    makeAnOfferActivity.showToast("Something went wrong", makeAnOfferActivity);

                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                makeAnOfferActivity.hideProgressDialog();
                Timber.tag("Response").e(call.toString());
            }
        });

    }


    @Override
    public void onClick(View v) {
        if (aboutCallbackFunction != null) {
            aboutCallbackFunction.backButtonAbout();
        }
    }

    public interface AboutCallbackFunction {
        void backButtonAbout();

        void continueButtonAbout(MakeAnOfferModel makeAnOfferModel);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // get the file url
        if (savedInstanceState != null) {
            imageStoragePath = savedInstanceState.getString(KEY_IMAGE_STORAGE_PATH);
        }
    }
}
