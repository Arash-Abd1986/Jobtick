package com.jobtick.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.jobtick.R;
import com.jobtick.adapers.AttachmentAdapter;
import com.jobtick.models.AttachmentModel;
import com.jobtick.retrofit.ApiClient;
import com.jobtick.utils.CameraUtils;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.Tools;
import com.jobtick.widget.SpacingItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class  PortfolioActivity extends ActivityBase implements AttachmentAdapter.OnItemClickListener {

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.bottom_sheet)
    FrameLayout bottomSheet;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    AttachmentAdapter adapter;
    ArrayList<AttachmentModel> attachmentArrayList;

    public static final long MAX_VIDEO_DURATION = 30;
    public static final long MAX_VIDEO_SIZE = 20 * 1024 * 1024;

    private static String imageStoragePath;
    // key to store image path in savedInstance state
    public static final String KEY_IMAGE_STORAGE_PATH = "image_path";

    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int GALLERY_PICKUP_VIDEO_REQUEST_CODE = 300;
    private static final int GALLERY_PICKUP_IMAGE_REQUEST_CODE = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment);
        ButterKnife.bind(this);
        mBehavior = BottomSheetBehavior.from(bottomSheet);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        attachmentArrayList = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getParcelableArrayList(ConstantKey.ATTACHMENT) != null) {
            attachmentArrayList = bundle.getParcelableArrayList(ConstantKey.ATTACHMENT);
        }


        init();
        // restoring storage image path from saved instance state
        // otherwise the path will be null on device rotation
        restoreFromBundle(savedInstanceState);


    }

    @Override
    public void onBackPressed() {
        //attachmentArrayList.remove(attachmentArrayList.size() - 1);
        Intent intent = new Intent();
        intent.putExtra(ConstantKey.ATTACHMENT, attachmentArrayList);
        setResult(101, intent);
        super.onBackPressed();

    }

    private void restoreFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_IMAGE_STORAGE_PATH)) {
                imageStoragePath = savedInstanceState.getString(KEY_IMAGE_STORAGE_PATH);
                if (!TextUtils.isEmpty(imageStoragePath)) {
                    if (imageStoragePath.substring(imageStoragePath.lastIndexOf(".")).equals("." + ConstantKey.IMAGE_EXTENSION)) {
                        //  previewCapturedImage();
                    } else if (imageStoragePath.substring(imageStoragePath.lastIndexOf(".")).equals("." + ConstantKey.VIDEO_EXTENSION)) {
                        //  previewVideo();
                    }
                }
            }
        }
    }

    private void init() {
        recyclerView.setLayoutManager(new GridLayoutManager(PortfolioActivity.this, 3));
        recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(PortfolioActivity.this, 5), true));
        recyclerView.setHasFixedSize(true);
        //set data and list adapter
        adapter = new AttachmentAdapter(attachmentArrayList,true);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(this);
    }

    private void showBottomSheetDialog() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.sheet_attachment, null);
        LinearLayout lytBtnCamera = view.findViewById(R.id.lyt_btn_camera);
        LinearLayout lytBtnImage = view.findViewById(R.id.lyt_btn_image);
        LinearLayout lytBtnVideo = view.findViewById(R.id.lyt_btn_video);
        LinearLayout lytBtnDoc = view.findViewById(R.id.lyt_btn_doc);
        LinearLayout lyrBtnVideoCamera = view.findViewById(R.id.lyt_btn_video_camera);

        lytBtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
        lytBtnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intent.setType("video/*");
                startActivityForResult(intent, GALLERY_PICKUP_VIDEO_REQUEST_CODE);
                mBottomSheetDialog.hide();
            }
        });
        lyrBtnVideoCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Checking availability of the camera
                if (!CameraUtils.isDeviceSupportCamera(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(),
                            "Sorry! Your device doesn't support camera",
                            Toast.LENGTH_LONG).show();
                    // will close the app if the device doesn't have camera
                    return;
                }
                if (CameraUtils.checkPermissions(getApplicationContext())) {
                    captureVideo();
                } else {
                    requestCameraPermission(ConstantKey.MEDIA_TYPE_VIDEO);
                }
                mBottomSheetDialog.hide();
            }
        });
        lytBtnDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select PDF File"), 1001);
                mBottomSheetDialog.hide();
            }
        });

        lytBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent opengallary = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(opengallary, "Open Gallary"), GALLERY_PICKUP_IMAGE_REQUEST_CODE);
                mBottomSheetDialog.hide();
            }
        });

        mBottomSheetDialog = new BottomSheetDialog(this);
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
        Dexter.withActivity(this)
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
                                captureVideo();
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

    /**
     * Saving stored image path to saved instance state
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putString(KEY_IMAGE_STORAGE_PATH, imageStoragePath);
    }

    /**
     * Restoring image path from saved instance state
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // get the file url
        imageStoragePath = savedInstanceState.getString(KEY_IMAGE_STORAGE_PATH);
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
        Uri fileUri = CameraUtils.getOutputMediaFileUri(getApplicationContext(), file);
        // set video quality
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAX_VIDEO_DURATION);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, MAX_VIDEO_SIZE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
        // start the video capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    @Override
    public void onItemClick(View view, AttachmentModel obj, int position, String action) {
        if (action.equalsIgnoreCase("add")) {
            showBottomSheetDialog();
        } else if (action.equalsIgnoreCase("delete")) {
            deleteMediaInAttachment(position, obj);
        }
    }

    private void uploadDataInPortfolioMediaApi(File pictureFile) {
        showProgressDialog();
        Call<String> call;
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile);
        MultipartBody.Part imageFile = MultipartBody.Part.createFormData("media", pictureFile.getName(), requestFile);
        call = ApiClient.getClient().getPortfolioMediaUpload("XMLHttpRequest", sessionManager.getTokenType() + " " + sessionManager.getAccessToken(), imageFile);


        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                hideProgressDialog();
                Log.e("Response", response.toString());
                if (response.code() == HttpStatus.HTTP_VALIDATION_ERROR) {
                    showToast(response.message(), PortfolioActivity.this);
                    return;
                }
                try {
                    String strResponse = response.body();
                    if (response.code() == HttpStatus.NOT_FOUND) {
                        Toast.makeText(PortfolioActivity.this, "not found", Toast.LENGTH_SHORT).show();
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
                            if (attachmentArrayList.size() != 0) {
                                attachmentArrayList.add(attachmentArrayList.size() - 1, attachment);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        //  adapter.notifyItemRangeInserted(0,attachmentArrayList.size());
                        // showToast("attachment added", AttachmentActivity.this);
                    } else {
                        showToast("Something went wrong", PortfolioActivity.this);
                    }
                } catch (JSONException e) {
                    showToast("Something went wrong", PortfolioActivity.this);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                hideProgressDialog();
                Log.e("Response", call.toString());
            }
        });

    }


    private void deleteMediaInAttachment(int position, AttachmentModel obj) {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, Constant.URL_PROFILE + "/portfolio/" + attachmentArrayList.get(position).getId(),
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Timber.e(response);
                        hideProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Timber.e(jsonObject.toString());
                            if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                                if (jsonObject.getBoolean("success")) {
                                    recyclerView.removeViewAt(position);
                                    attachmentArrayList.remove(position);
                                    adapter.notifyItemRemoved(position);
                                    adapter.notifyItemRangeRemoved(position, attachmentArrayList.size());
                                    showToast("Portfolio Deleted", PortfolioActivity.this);

                                } else {
                                    showToast("Something went Wrong", PortfolioActivity.this);
                                }
                            }
                        } catch (JSONException e) {
                            Timber.e(String.valueOf(e));
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
                                    Toast.makeText(PortfolioActivity.this, jsonObject_error.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                                if (jsonObject_error.has("errors")) {
                                    JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");
                                }
                                //  ((CredentialActivity)getActivity()).showToast(message,getActivity());


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            showToast("Something Went Wrong", PortfolioActivity.this);
                        }
                        Timber.e(error.toString());
                        hideProgressDialog();
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
        RequestQueue requestQueue = Volley.newRequestQueue(PortfolioActivity.this);
        requestQueue.add(stringRequest);
        Log.e("AttachmentActivity", stringRequest.getUrl());
    }


    /**
     * Activity result method will be called after closing the camera
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);
                Uri uri = Uri.parse("file://" + imageStoragePath);
                uploadDataInPortfolioMediaApi(new File(uri.getPath()));
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
        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

                // video successfully recorded
                // preview the recorded video
                Uri uri = Uri.parse("file://" + imageStoragePath);
                uploadDataInPortfolioMediaApi(new File(uri.getPath()));
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
        } else if (requestCode == GALLERY_PICKUP_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                imageStoragePath = CameraUtils.getImagePath(PortfolioActivity.this, data.getData());
                Log.e("path", imageStoragePath);
                if(imageStoragePath != null) {
                    MediaPlayer mpl = MediaPlayer.create(PortfolioActivity.this, Uri.parse(imageStoragePath));
                    int si = mpl.getDuration();
                    long duration = TimeUnit.MILLISECONDS.toSeconds(si);
                    File file = new File(imageStoragePath);
                    long file_size = Integer.parseInt(String.valueOf(file.length()));
                    if (duration > MAX_VIDEO_DURATION) {
                        CameraUtils.showLimitDialog(PortfolioActivity.this);
                        imageStoragePath = null;
                    } else if (file_size > MAX_VIDEO_SIZE) {
                        showToast("Maximum video size exceeds(20 MB)", PortfolioActivity.this);
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
        } else if (requestCode == GALLERY_PICKUP_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
              /*  Uri filePath = data.getData();
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                if (data.getData() != null) {
                    imageStoragePath = CameraUtils.getPath(PortfolioActivity.this, data.getData());
                    File file = new File(imageStoragePath);
                    uploadDataInPortfolioMediaApi(file);
                }
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled Pickup Image", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to Pickup Image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }


}
