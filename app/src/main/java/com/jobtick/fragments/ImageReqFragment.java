package com.jobtick.fragments;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jobtick.R;
import com.jobtick.activities.ActivityBase;
import com.jobtick.activities.EditProfileActivity;
import com.jobtick.activities.TaskDetailsActivity;
import com.jobtick.adapers.AttachmentAdapter;
import com.jobtick.adapers.AttachmentAdapterEditProfile;
import com.jobtick.models.AttachmentModel;
import com.jobtick.models.UserAccountModel;
import com.jobtick.retrofit.ApiClient;
import com.jobtick.utils.CameraUtils;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.ImageUtil;
import com.jobtick.utils.SessionManager;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.jobtick.activities.DashboardActivity.onProfileupdatelistenerSideMenu;
import static com.jobtick.fragments.ProfileFragment.onProfileupdatelistener;
import static com.jobtick.utils.ConstantKey.TAG;

public class ImageReqFragment extends Fragment implements AttachmentAdapterEditProfile.OnItemClickListener {

    private final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private final int GALLERY_PICKUP_IMAGE_REQUEST_CODE = 400;
    private CircularImageView imgAvatar;
    SessionManager sessionManager;

    private UserAccountModel userAccountModel;
    private static String imageStoragePath;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    boolean isUploadPortfolio = false;
    private FrameLayout btmSheet;

    public ImageReqFragment() {
    }

    public static ImageReqFragment newInstance() {
        return new ImageReqFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(getContext());
        btmSheet = view.findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(btmSheet);
        imgAvatar = view.findViewById(R.id.img_user_avatar);
        TextView btnNext = view.findViewById(R.id.txt_btn_nextI);
        btnNext.setOnClickListener(v -> ((RequirementsBottomSheet) getParentFragment()).changeFragment(1));
        userAccountModel = ((TaskDetailsActivity) getActivity()).userAccountModel;
        setUpAvatar(userAccountModel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_req, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private void setUpAvatar(UserAccountModel userAccountModel) {
        try {
            ImageUtil.displayImage(imgAvatar, userAccountModel.getAvatar().getUrl(), null);
        } catch (Exception ex) {
            Log.e(TAG, "EXCEPTION CAUGHT WHILE EXECUTING DATABASE TRANSACTION");
            ex.printStackTrace();
        }
    }

    @OnClick({R.id.img_user_avatar})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.img_user_avatar) {
            showBottomSheetDialog(false);
        }
    }

    private void showBottomSheetDialog(boolean isUploadPortfolioOrProfile) {
        isUploadPortfolio = isUploadPortfolioOrProfile;

        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        final View view = getLayoutInflater().inflate(R.layout.sheet_attachment, null);
        LinearLayout lytBtnCamera = view.findViewById(R.id.lyt_btn_camera);
        LinearLayout lytBtnImage = view.findViewById(R.id.lyt_btn_image);
        LinearLayout lytBtnVideo = view.findViewById(R.id.lyt_btn_video);
        LinearLayout lytBtnDoc = view.findViewById(R.id.lyt_btn_doc);
        LinearLayout lyrBtnVideoCamera = view.findViewById(R.id.lyt_btn_video_camera);
        lytBtnVideo.setVisibility(View.GONE);
        lytBtnDoc.setVisibility(View.INVISIBLE);
        lyrBtnVideoCamera.setVisibility(View.INVISIBLE);


        lytBtnCamera.setOnClickListener(view1 -> {
            if (!CameraUtils.isDeviceSupportCamera(getApplicationContext())) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! Your device doesn't support camera",
                        Toast.LENGTH_LONG).show();
                // will close the app if the device doesn't have camera
                return;
            }
            if (CameraUtils.checkPermissions(getApplicationContext())) {
                captureImage();
            } else {
                requestCameraPermission(ConstantKey.MEDIA_TYPE_IMAGE);
            }
            mBottomSheetDialog.hide();
        });

        lytBtnImage.setOnClickListener(v -> {
            Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(openGallery, "Open Gallery"), GALLERY_PICKUP_IMAGE_REQUEST_CODE);
            mBottomSheetDialog.hide();
        });
        if (getActivity() != null) {
            mBottomSheetDialog = new BottomSheetDialog(getActivity());
            mBottomSheetDialog.setContentView(view);
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        // set background transparent
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(dialog -> mBottomSheetDialog = null);
    }

    private void requestCameraPermission(final int type) {
        Dexter.withContext(getActivity())
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            if (type == ConstantKey.MEDIA_TYPE_IMAGE) {
                                // capture picture
                                captureImage();
                            }
                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showPermissionsAlert();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = CameraUtils.getOutputMediaFile(ConstantKey.MEDIA_TYPE_IMAGE);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }
        Uri fileUri = CameraUtils.getOutputMediaFileUri(getApplicationContext(), file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null & getContext() != null) {
            if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    Uri uri = Uri.parse("file://" + imageStoragePath);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    imgAvatar.setImageBitmap(bitmap);
                    uploadProfileAvatar(new File(uri.getPath()));

                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(),
                            "User cancelled image capture", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                            .show();
                }
            } else if (requestCode == GALLERY_PICKUP_IMAGE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    if (data.getData() != null && getActivity() != null) {
                        try {
                            imageStoragePath = CameraUtils.getPath(getActivity(), data.getData());
                            File file = new File(imageStoragePath);
                            Uri uri = Uri.parse("file://" + imageStoragePath);
                            Bitmap bitmap = null;
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);

                            imgAvatar.setImageBitmap(bitmap);
                            uploadProfileAvatar(new File(uri.getPath()));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    // failed to record video
                    Toast.makeText(getApplicationContext(),
                            "Sorry! Failed to Pickup Image", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }

    private void uploadProfileAvatar(File pictureFile) {
        Call<String> call;
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile);
        MultipartBody.Part imageFile = MultipartBody.Part.createFormData("media", pictureFile.getName(), requestFile);
        call = ApiClient.getClient().uploadProfilePicture("XMLHttpRequest", sessionManager.getTokenType() + " " + sessionManager.getAccessToken(), imageFile);
        System.out.println("aaaaaaaaaaaaaaaa");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                System.out.println("ccccccccccccccccccccccc");
                String res = response.body();
                System.out.println(res);
                if (response.code() == HttpStatus.HTTP_VALIDATION_ERROR) {
                    if (getActivity() != null) {
                        ((ActivityBase) getActivity()).showToast(response.message(), getActivity());
                    }
                    return;
                }
                try {
                    String strResponse = res;
                    if (response.code() == HttpStatus.NOT_FOUND) {
                        Toast.makeText(getActivity(), "not found", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (response.code() == HttpStatus.AUTH_FAILED) {
                        ((ActivityBase) getActivity()).unauthorizedUser();
                        return;
                    }
                    if (response.code() == HttpStatus.SUCCESS) {
                        JSONObject jsonObject = new JSONObject(strResponse);
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

                            sessionManager.getUserAccount().setAvatar(attachment);
                            ImageUtil.displayImage(imgAvatar, attachment.getUrl(), null);

                            if (onProfileupdatelistener != null) {
                                onProfileupdatelistener.updatedSuccesfully(attachment.getUrl());
                            }
                            if (onProfileupdatelistenerSideMenu != null) {
                                onProfileupdatelistenerSideMenu.updatedSuccesfully(attachment.getUrl());
                            }
                        }
                        //   adapter.notifyItemRangeInserted(0,attachmentArrayList.size());
                        ((ActivityBase) getActivity()).showToast("attachment added", getActivity());
                    } else {
                        ((ActivityBase) getActivity()).showToast("Something went wrong", getActivity());
                    }
                } catch (JSONException e) {
                    ((ActivityBase) getActivity()).showToast("Something went wrong", getActivity());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                System.out.println("bbbbbbbbbbbbbbbbb");
                System.out.println(t.toString());
                if (getActivity() != null) {
                    ((ActivityBase) getActivity()).hideProgressDialog();
                }
            }
        });


    }

    public void showPermissionsAlert() {
        if (getActivity() == null) {
            return;
        }
        new MaterialAlertDialogBuilder(getActivity())
                .setTitle("Permissions required!")
                .setMessage("Camera needs few permissions to work properly. Grant them in settings.")
                .setPositiveButton("GOTO SETTINGS", (dialog, which) -> CameraUtils.openSettings(getActivity()))
                .setNegativeButton("CANCEL", null)
                .show();
    }

    @Override
    public void onItemClick(View view, AttachmentModel obj, int position, String action) {
        if (action.equalsIgnoreCase("add")) {
           /* Intent intent = new Intent(EditProfileActivity.this, PortfolioActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(ConstantKey.ATTACHMENT, attachmentArrayList);
            intent.putExtras(bundle);
            startActivityForResult(intent, 234);*/

            showBottomSheetDialog(true);

        }
    }
}