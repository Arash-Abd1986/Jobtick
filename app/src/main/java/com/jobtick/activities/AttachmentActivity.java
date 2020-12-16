package com.jobtick.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jobtick.BuildConfig;
import com.jobtick.R;
import android.annotation.SuppressLint;

import com.jobtick.adapers.AttachmentAdapter;
import com.jobtick.models.AttachmentModel;
import com.jobtick.retrofit.ApiClient;
import com.jobtick.utils.Constant;
import com.jobtick.utils.ConstantKey;
import com.jobtick.utils.HttpStatus;
import com.jobtick.utils.Tools;
import com.jobtick.widget.SpacingItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
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

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class AttachmentActivity extends ActivityBase implements AttachmentAdapter.OnItemClickListener {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    ArrayList<AttachmentModel> attachmentArrayList;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.bottom_sheet)
    FrameLayout bottomSheet;
    public static final String VIDEO_FORMAT = ".mp4";
    public static final String VIDEO_SIGN = "VID_";
    private static final int VIDEO_CAPTURE = 101;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    public static final long MAX_VIDEO_DURATION = 30;
    public static final long MAX_VIDEO_SIZE = 20 * 1024 * 1024;
    public static final String MEDIA_DIRECTORY_NAME = "Jobtick";
    public static final String VIDEO_NAME = "video_name";
    public static final String AD_THUMB = "thumb_name";
    public static final String TIME_STAMP_FORMAT = "yyyyMMdd_HHmmss";
    public static final int MEDIA_TYPE_VIDEO = 2;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private static final int PICK_VIDEO = 107;
    private Uri fileUri;
    private String imagePath;
    private Uri filePath;
    private Bitmap bitmap;
    AttachmentAdapter adapter;
    private String strVideoPath;
    private String title;
    private String slug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment);
        ButterKnife.bind(this);
        mBehavior = BottomSheetBehavior.from(bottomSheet);
        attachmentArrayList = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString(ConstantKey.TITLE) != null) {
                title = bundle.getString(ConstantKey.TITLE);
            }
            if (bundle.getString(ConstantKey.SLUG) != null) {
                slug = bundle.getString(ConstantKey.SLUG);
            }
            if (bundle.getParcelableArrayList(ConstantKey.ATTACHMENT) != null) {
                attachmentArrayList = bundle.getParcelableArrayList(ConstantKey.ATTACHMENT);
                if (attachmentArrayList != null) {
                    attachmentArrayList.add(new AttachmentModel());
                }
            } else {
                attachmentArrayList.add(new AttachmentModel());
            }
        } else {
            attachmentArrayList.add(new AttachmentModel());
        }

        init();
        initToolbar();
    }

    private void initToolbar() {
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_cancel);
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Attachment");
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        attachmentArrayList.remove(attachmentArrayList.size() - 1);
        Intent intent = new Intent();
        intent.putExtra(ConstantKey.ATTACHMENT, attachmentArrayList);
        setResult(ConstantKey.RESULTCODE_ATTACHMENT, intent);
        super.onBackPressed();
    }


    private void init() {
        recyclerView.setLayoutManager(new GridLayoutManager(AttachmentActivity.this, 3));
        recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(AttachmentActivity.this, 5), true));
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

        lytBtnCamera.setOnClickListener(view1 -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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

        lytBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissionREAD_EXTERNAL_STORAGE(AttachmentActivity.this)) {
                    Intent opengallary = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(opengallary, "Open Gallary"), 1);
                }
                mBottomSheetDialog.hide();
            }
        });

        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        // set background transparent
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(dialog -> mBottomSheetDialog = null);

    }


    private void getGalleryVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_VIDEO);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast("camera permission granted", AttachmentActivity.this);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //   uri_cameraimage = getOutputMediaFileUrir(MEDIA_TYPE_IMAGE);
                //   cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri_cameraimage); // set the image file
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                showToast("camera permission denied", AttachmentActivity.this);
            }
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

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();

        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

        Timber.e(path);
        cursor.close();

        return path;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1 && resultCode == RESULT_OK) {
                filePath = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imagePath = getPath(data.getData());
                //imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
                //  uploadImageWithAmount();
                File file = new File(imagePath);
                if (title.equalsIgnoreCase(ConstantKey.CREATE_A_JOB)) {
                    uploadDataInTempApi(file);
                } else {
                    uploadDataInTaskMediaApi(file);
                }
                Timber.e(imagePath);

            } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                //bitmap to convert into file
                File pictureFile = new File(getExternalCacheDir(), "jobtick");
                FileOutputStream fos = new FileOutputStream(pictureFile);
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                }
                fos.close();
                if (title.equalsIgnoreCase(ConstantKey.CREATE_A_JOB)) {
                    uploadDataInTempApi(pictureFile);
                } else {
                    uploadDataInTaskMediaApi(pictureFile);
                }

            } else if (requestCode == PICK_VIDEO && resultCode == RESULT_OK) {

                strVideoPath = getImagePath(data.getData());
                Timber.e(strVideoPath);
                MediaPlayer mpl = MediaPlayer.create(AttachmentActivity.this, Uri.parse(strVideoPath));
                int si = mpl.getDuration();
                long duration = TimeUnit.MILLISECONDS.toSeconds(si);
                File file = new File(strVideoPath);
                long file_size = Integer.parseInt(String.valueOf(file.length()));
                if (duration > MAX_VIDEO_DURATION) {
                    showLimitDialog();
                    strVideoPath = null;
                } else if (file_size > MAX_VIDEO_SIZE) {
                    showToast("Maximum video size exceeds(20 MB)", AttachmentActivity.this);
                    strVideoPath = null;
                } else {
                    Timber.e(duration + "");
                    Timber.e(file_size + "");
                    // uploadUrl = strVideoPath;
                    Timber.e("video");
                    if (title.equalsIgnoreCase(ConstantKey.CREATE_A_JOB)) {
                        uploadDataInTempApi(file);
                    } else {
                        uploadDataInTaskMediaApi(file);
                    }
                }

            } else if (requestCode == VIDEO_CAPTURE && resultCode == RESULT_OK) {

                strVideoPath = fileUri.getPath();
                MediaPlayer mpl = MediaPlayer.create(AttachmentActivity.this, Uri.parse(strVideoPath));
//                    MediaPlayer mpl = MediaPlayer.create(getActivity(), data.getData());
                int si = mpl.getDuration();
                long duration = TimeUnit.MILLISECONDS.toSeconds(si);
                File file = new File(strVideoPath);
                long file_size = Integer.parseInt(String.valueOf(file.length()));
                if (duration > MAX_VIDEO_DURATION) {
                    showLimitDialog();
                    strVideoPath = null;
                } else if (file_size > MAX_VIDEO_SIZE) {
                    showToast("Maximum video size exceeds(20 MB)", AttachmentActivity.this);
                    strVideoPath = null;
                } else {
                    Timber.e(duration + "");
                    Timber.e(file_size + "");
                    Timber.e("video");
                    if (title.equalsIgnoreCase(ConstantKey.CREATE_A_JOB)) {
                        uploadDataInTempApi(file);
                    } else {
                        uploadDataInTaskMediaApi(file);
                    }
                }
            } else if (requestCode == 1001 && resultCode == RESULT_OK) {
                if (data != null && data.getData() != null) {
                    File file = null;
                    if (data.getData().getPath() != null) {
                        file = new File(data.getData().getPath());
                    }
                    if (file != null) {
                        if (title.equalsIgnoreCase(ConstantKey.CREATE_A_JOB)) {
                            uploadDataInTempApi(file);
                        } else {
                            uploadDataInTaskMediaApi(file);
                        }
                    } else {
                       showToast("Try Again", this);
                    }

                }
            } else if (resultCode == RESULT_CANCELED) {
                showToast(getString(R.string.msg_image_select), AttachmentActivity.this);
            } else {
                showToast(getString(R.string.error_image_select), AttachmentActivity.this);
            }

        } catch (Exception e) {
            Timber.e(e.toString());
        }

    }


    private void recordVideo() {

        try {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAX_VIDEO_DURATION);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, MAX_VIDEO_SIZE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
            // start the video capture Intent
            startActivityForResult(intent, VIDEO_CAPTURE);

        } catch (Exception e) {

            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

            fileUri = getOutputMediaFileUri1(MEDIA_TYPE_VIDEO);

            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAX_VIDEO_DURATION);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, MAX_VIDEO_SIZE);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file

            // start the video capture Intent
            startActivityForResult(intent, VIDEO_CAPTURE);
        }
    }

    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private Uri getOutputMediaFileUri1(int type) {
        return FileProvider.getUriForFile(AttachmentActivity.this, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

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
        if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + VIDEO_SIGN + getTimeStamp() + VIDEO_FORMAT);
        } else {
            return null;
        }


        Timber.e(String.valueOf(mediaFile));

        return mediaFile;
    }

    private static String getTimeStamp() {
        return new SimpleDateFormat(TIME_STAMP_FORMAT,
                Locale.getDefault()).format(new Date());
    }

    private void showLimitDialog() {

        androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(AttachmentActivity.this);
        builder1.setMessage("Maximum video duration exceeds 30 sec");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();

                    }
                });


        androidx.appcompat.app.AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    public String getImagePath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Video.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
    }

    private void uploadDataInTempApi(File pictureFile) {
        showProgressDialog();
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
            public void onResponse(Call<String> call, Response<String> response) {
                hideProgressDialog();
                Timber.e(response.toString());
                if (response.code() == 422) {
                    showToast(response.message(), AttachmentActivity.this);
                    return;
                }

                try {
                    String strResponse = response.body();
                    Timber.e(strResponse);
                    JSONObject jsonObject = new JSONObject(strResponse);
                    Timber.e(jsonObject.toString());
                    if (jsonObject.has("data")) {
                        JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                        AttachmentModel attachment = new AttachmentModel().getJsonToModel(jsonObject_data);
                        if (attachmentArrayList.size() != 0) {
                            attachmentArrayList.add(attachmentArrayList.size() - 1, attachment);
                        }
                    }

                    adapter.notifyItemInserted(0);
                    //  adapter.notifyItemRangeInserted(0,attachmentArrayList.size());

                } catch (JSONException e) {
                    showToast("Something went wrong", AttachmentActivity.this);

                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                hideProgressDialog();
                Timber.e(call.toString());
            }
        });

    }

    private void uploadDataInTaskMediaApi(File pictureFile) {
        showProgressDialog();
        Call<String> call;

        //    File file = new File(imagePath);
        //    Log.e("AttachmentApi: ", file.getName());
        //   Log.e("uploadProfile++:11 ", imagePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), pictureFile);
        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part imageFile = MultipartBody.Part.createFormData("media", pictureFile.getName(), requestFile);
        call = ApiClient.getClient().getTasKAttachmentMediaUpload(slug, "XMLHttpRequest", sessionManager.getTokenType() + " " + sessionManager.getAccessToken(), imageFile);


        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                hideProgressDialog();
                Timber.e(response.toString());
                if (response.code() == 422) {
                    showToast(response.message(), AttachmentActivity.this);
                    return;
                }

                try {
                    String strResponse = response.body();
                    if (response.code() == HttpStatus.NOT_FOUND) {
                        showToast("not found", AttachmentActivity.this);
                        return;
                    }
                    if (response.code() == HttpStatus.AUTH_FAILED) {
                        unauthorizedUser();
                        return;
                    }
                    if (response.code() == HttpStatus.SUCCESS) {
                        Timber.e(strResponse);
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
                            if (attachmentArrayList.size() != 0) {
                                attachmentArrayList.add(attachmentArrayList.size() - 1, attachment);
                            }
                        }

                        adapter.notifyItemInserted(0);
                        //  adapter.notifyItemRangeInserted(0,attachmentArrayList.size());
                        // showToast("attachment added", AttachmentActivity.this);

                    } else {
                        showToast("Something went wrong", AttachmentActivity.this);

                    }
                } catch (JSONException e) {
                    showToast("Something went wrong", AttachmentActivity.this);

                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                hideProgressDialog();
                Timber.e(call.toString());
            }
        });

    }


    @Override
    public void onItemClick(View view, AttachmentModel obj, int position, String action) {
        if (action.equalsIgnoreCase("add")) {
            showBottomSheetDialog();
        } else if (action.equalsIgnoreCase("delete")) {
            if (title.equalsIgnoreCase(ConstantKey.CREATE_A_JOB)) {
                recyclerView.removeViewAt(position);
                attachmentArrayList.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeRemoved(position, attachmentArrayList.size());
                showToast("Delete this attachment", AttachmentActivity.this);
            } else {
                deleteMediaInAttachment(position, obj);
            }
        }
    }

    private void deleteMediaInAttachment(int position, AttachmentModel obj) {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, Constant.URL_TASKS + "/" + slug + "/attachment?media=" + attachmentArrayList.get(position).getId(),
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
                                    showToast("Delete this attachment", AttachmentActivity.this);

                                } else {
                                    showToast("Something went Wrong", AttachmentActivity.this);
                                }
                            }


                        } catch (JSONException e) {
                            Timber.e(String.valueOf(e));
                            e.printStackTrace();

                        }


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
                                showToast(jsonObject_error.getString("message"), this);
                            }

                            //  ((CredentialActivity)getActivity()).showToast(message,getActivity());


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Something Went Wrong", AttachmentActivity.this);
                    }
                    Timber.e(error.toString());
                    hideProgressDialog();
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
        RequestQueue requestQueue = Volley.newRequestQueue(AttachmentActivity.this);
        requestQueue.add(stringRequest);
        Timber.e(stringRequest.getUrl());
    }
}