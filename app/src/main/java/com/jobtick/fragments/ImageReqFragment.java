package com.jobtick.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jobtick.R;
import com.jobtick.activities.ActivityBase;
import com.jobtick.activities.EditProfileActivity;
import com.jobtick.activities.SkillsTagActivity;
import com.jobtick.adapers.AttachmentAdapter;
import com.jobtick.adapers.AttachmentAdapterEditProfile;
import com.jobtick.models.AttachmentModel;
import com.jobtick.models.GeocodeObject;
import com.jobtick.models.UserAccountModel;
import com.jobtick.retrofit.ApiClient;
import com.jobtick.utils.CameraUtils;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.Helper;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.ImageUtil;
import com.jobtick.utils.SessionManager;
import com.jobtick.utils.Tools;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.jobtick.activities.DashboardActivity.onProfileupdatelistenerSideMenu;
import static com.jobtick.fragments.ProfileFragment.onProfileupdatelistener;
import static com.jobtick.utils.Constant.URL_REMOVE_AVTAR;


public class ImageReqFragment extends Fragment {


    private static final String TAG = EditProfileActivity.class.getName();
    private int PLACE_SELECTION_REQUEST_CODE = 1;
    private static final int GALLERY_PICKUP_VIDEO_REQUEST_CODE = 300;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int GALLERY_PICKUP_IMAGE_REQUEST_CODE = 400;
    private TextView btnNext;
    private CircularImageView imgAvatar;
    SessionManager sessionManager;

    private ArrayList<AttachmentModel> attachmentArrayList;
    private UserAccountModel userAccountModel;
    AttachmentAdapterEditProfile adapter;
    private static String imageStoragePath;
    private FrameLayout bottomSheet;
    boolean isUploadPortfolio=false;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    public ImageReqFragment() {
    }

    public static ImageReqFragment newInstance() {
        ImageReqFragment fragment = new ImageReqFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imgAvatar=view.findViewById(R.id.img_user_avatar);
        btnNext = view.findViewById(R.id.txt_btn_nextI);
        bottomSheet=view.findViewById(R.id.bottom_sheet);
        btnNext.setOnClickListener(v -> {
            ((RequirementsBottomSheet) getParentFragment()).changeFragment(1); });
        getAllUserProfileDetails();

        mBehavior = BottomSheetBehavior.from(bottomSheet);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_image_req, container, false);

        return view;

    }


    private void getAllUserProfileDetails() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_PROFILE + "/" + sessionManager.getUserAccount().getId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                       // hideProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Timber.e(jsonObject.toString());


                            if (jsonObject.has("data") && !jsonObject.isNull("data")) {

                                userAccountModel = new UserAccountModel().getJsonToModel(jsonObject.getJSONObject("data"));
                                /*
                                 * Add Button for empty attachment
                                 * */
                                setUpAllEditFields(userAccountModel);
                                attachmentArrayList = userAccountModel.getPortfolio();
                                attachmentArrayList.add(new AttachmentModel());
                                Log.e(TAG, attachmentArrayList.size() + "");
                                if (attachmentArrayList.size() != 0) {
                                    adapter.addItems(attachmentArrayList);
                                }
                            } else {
                                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "JSONException", Toast.LENGTH_SHORT).show();

                            Timber.e(String.valueOf(e));
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    //    errorHandle1(error.networkResponse);
                     //   hideProgressDialog();
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                // map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
        Log.e(TAG, stringRequest.getUrl());
    }
    private void setUpAllEditFields(UserAccountModel userAccountModel)
    {

            ImageUtil.displayImage(imgAvatar, userAccountModel.getAvatar().getThumbUrl(), null);


    }


    @OnClick({ R.id.img_user_avatar})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.img_user_avatar:
                showBottomSheetDialog(false);

                break;

        }
    }


    private void showBottomSheetDialog(boolean isUploadPortfolioOrPrfile)
    {
        isUploadPortfolio=isUploadPortfolioOrPrfile;

        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.sheet_attachment, null);
        LinearLayout lytBtnCamera = view.findViewById(R.id.lyt_btn_camera);
        LinearLayout lytBtnImage = view.findViewById(R.id.lyt_btn_image);
        LinearLayout lytBtnVideo = view.findViewById(R.id.lyt_btn_video);
        LinearLayout lytBtnDoc = view.findViewById(R.id.lyt_btn_doc);
        LinearLayout lyrBtnVideoCamera = view.findViewById(R.id.lyt_btn_video_camera);

        if(isUploadPortfolioOrPrfile)
        {
            lytBtnVideo.setVisibility(View.VISIBLE);
            lytBtnDoc.setVisibility(View.VISIBLE);
            lyrBtnVideoCamera.setVisibility(View.VISIBLE);
        }else
        {
            lytBtnVideo.setVisibility(View.GONE);
            lytBtnDoc.setVisibility(View.INVISIBLE);
            lyrBtnVideoCamera.setVisibility(View.INVISIBLE);
        }


        lytBtnCamera.setOnClickListener(view1 -> {
            // Checking availability of the camera
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


        lytBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent opengallary = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(opengallary, "Open Gallary"), GALLERY_PICKUP_IMAGE_REQUEST_CODE);
                mBottomSheetDialog.hide();
            }
        });

        mBottomSheetDialog = new BottomSheetDialog(getActivity());
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



    /**
     * Requesting permissions using Dexter library
     */
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
                            } else {
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


    /**
     * Capturing Camera Image will launch camera app requested image capture
     */
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

 /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    // Refreshing the gallery
                    // CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);
                    Uri uri = Uri.parse("file://" + imageStoragePath);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    imgAvatar.setImageBitmap(bitmap);

                    if(isUploadPortfolio)
                    {
                        uploadDataInPortfolioMediaApi(new File(uri.getPath()));
                    }else
                    {
                        uploadProfileAvtar(new File(uri.getPath()));
                    }


                } else if (resultCode == RESULT_CANCELED) {
                    // user cancelled Image capture
                    Toast.makeText(getApplicationContext(),
                            "User cancelled image capture", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // failed to capture image
                    Toast.makeText(getApplicationContext(),
                            "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                            .show();
                }
            } else if (requestCode == GALLERY_PICKUP_IMAGE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    if (data.getData() != null) {
                        imageStoragePath = CameraUtils.getPath(EditProfileActivity.this, data.getData());
                        File file = new File(imageStoragePath);
                        Uri uri = Uri.parse("file://" + imageStoragePath);
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        imgAvatar.setImageBitmap(bitmap);

                        if(isUploadPortfolio)
                        {
                            uploadDataInPortfolioMediaApi(new File(uri.getPath()));

                        }else
                        {
                            uploadProfileAvtar(new File(uri.getPath()));
                        }


                        //// uploadDataInPortfolioMediaApi(file);
                    }
                }
                else if (requestCode == GALLERY_PICKUP_VIDEO_REQUEST_CODE)
                {
                    if (resultCode == RESULT_OK) {
                        imageStoragePath = CameraUtils.getImagePath(EditProfileActivity.this, data.getData());
                        Log.e("path", imageStoragePath);
                        if(imageStoragePath != null) {
                            MediaPlayer mpl = MediaPlayer.create(EditProfileActivity.this, Uri.parse(imageStoragePath));
                            int si = mpl.getDuration();
                            long duration = TimeUnit.MILLISECONDS.toSeconds(si);
                            File file = new File(imageStoragePath);
                            long file_size = Integer.parseInt(String.valueOf(file.length()));
                            if (duration > MAX_VIDEO_DURATION) {
                                CameraUtils.showLimitDialog(EditProfileActivity.this);
                                imageStoragePath = null;
                            } else if (file_size > MAX_VIDEO_SIZE) {
                                showToast("Maximum video size exceeds(20 MB)", EditProfileActivity.this);
                                imageStoragePath = null;
                            } else {
                                Log.e("Duration: ", String.valueOf(duration) + "");
                                Log.e("Size: ", String.valueOf(file_size) + "");
                                // uploadUrl = strVideoPath;
                                Log.e("VIDEO", "video");
                                uploadDataInPortfolioMediaApi(file);
                            }
                        }
                    } else if (resultCode == RESULT_CANCELED) {
                        // user cancelled recording
                        Toast.makeText(getApplicationContext(),
                                "User cancelled video recording", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        // failed to record video
                        Toast.makeText(getApplicationContext(),
                                "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                else {
                    // failed to record video
                    Toast.makeText(getApplicationContext(),
                            "Sorry! Failed to Pickup Image", Toast.LENGTH_SHORT)
                            .show();
                }
            }


            Bundle bundle = data.getExtras();
            if (bundle != null) {
                if (requestCode == 1) {
                    userAccountModel.getSkills().setTransportation(bundle.getStringArrayList(ConstantKey.SKILLS));
                    transportationSetUp(userAccountModel);
                } else if (requestCode == 2) {
                    userAccountModel.getSkills().setLanguage(bundle.getStringArrayList(ConstantKey.SKILLS));
                    languagesSetUp(userAccountModel);
                } else if (requestCode == 3) {
                    userAccountModel.getSkills().setEducation(bundle.getStringArrayList(ConstantKey.SKILLS));
                    educationSetUp(userAccountModel);
                } else if (requestCode == 4) {
                    userAccountModel.getSkills().setExperience(bundle.getStringArrayList(ConstantKey.SKILLS));
                    experienceSetUp(userAccountModel);
                } else if (requestCode == 5) {
                    userAccountModel.getSkills().setSpecialities(bundle.getStringArrayList(ConstantKey.SKILLS));
                    specialitiesSetUp(userAccountModel);
                } else if (requestCode == 234) {
                    userAccountModel.setPortfolio(bundle.getParcelableArrayList(ConstantKey.ATTACHMENT));
                    adapter.clearAll();
                    attachmentArrayList.clear();
                    attachmentArrayList = userAccountModel.getPortfolio();
                    *//*
                     * include add button *//*
                    adapter.addItems(attachmentArrayList);
                }
            }

        }


    }

    private void uploadProfileAvtar(File pictureFile) {
        showProgressDialog();
        Call<String> call;
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile);
        MultipartBody.Part imageFile = MultipartBody.Part.createFormData("media", pictureFile.getName(), requestFile);
        call = ApiClient.getClient().uploadProfilePicture("XMLHttpRequest", sessionManager.getTokenType() + " " + sessionManager.getAccessToken(), imageFile);


        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                hideProgressDialog();
                Log.e("Response", response.toString());
                if (response.code() == HttpStatus.HTTP_VALIDATION_ERROR) {
                    showToast(response.message(), EditProfileActivity.this);
                    return;
                }
                try {
                    String strResponse = response.body();
                    if (response.code() == HttpStatus.NOT_FOUND) {
                        Toast.makeText(EditProfileActivity.this, "not found", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (response.code() == HttpStatus.AUTH_FAILED) {
                        unauthorizedUser();
                        return;
                    }
                    if (response.code() == HttpStatus.SUCCESS) {
                        Log.e("body", strResponse);
                        JSONObject jsonObject = new JSONObject(strResponse);
                        Log.e("json", jsonObject.toString());
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
                            lytDeletePicture.setVisibility(View.VISIBLE);
                            if (onProfileupdatelistener != null) {
                                onProfileupdatelistener.updatedSuccesfully(attachment.getThumbUrl());
                            }
                            if (onProfileupdatelistenerSideMenu != null) {
                                onProfileupdatelistenerSideMenu.updatedSuccesfully(attachment.getThumbUrl());
                            }
                        }
                        //  adapter.notifyItemRangeInserted(0,attachmentArrayList.size());
                        // showToast("attachment added", AttachmentActivity.this);
                    } else {
                        showToast("Something went wrong", EditProfileActivity.this);
                    }
                } catch (JSONException e) {
                    showToast("Something went wrong", EditProfileActivity.this);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                hideProgressDialog();
                Log.e("Response", call.toString());
            }
        });

    }*/
    public void showPermissionsAlert() {
        new MaterialAlertDialogBuilder(getActivity())
                .setTitle("Permissions required!")
                .setMessage("Camera needs few permissions to work properly. Grant them in settings.")
                .setPositiveButton("GOTO SETTINGS", (dialog, which) -> CameraUtils.openSettings(getActivity()))
                .setNegativeButton("CANCEL", null)
                .show();
    }


}