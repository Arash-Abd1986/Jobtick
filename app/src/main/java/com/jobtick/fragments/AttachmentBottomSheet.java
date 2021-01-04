package com.jobtick.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jobtick.BuildConfig;
import com.jobtick.R;
import com.jobtick.activities.ActivityBase;
import com.jobtick.utils.NewCameraUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class AttachmentBottomSheet extends BottomSheetDialogFragment {


    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public static final String VIDEO_FORMAT = ".mp4";
    public static final String VIDEO_SIGN = "VID_";
    private static final int VIDEO_CAPTURE = 101;
    public static final int CAMERA_REQUEST = 1001;
    public static final int GALLERY_REQUEST = 1002;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    public static final long MAX_VIDEO_DURATION = 30;
    public static final long MAX_VIDEO_SIZE = 20 * 1024 * 1024;
    public static final String MEDIA_DIRECTORY_NAME = "Jobtick";
    public static final String TIME_STAMP_FORMAT = "yyyyMMdd_HHmmss";
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int PICK_VIDEO = 107;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.sheet_attachment, container, false);

        LinearLayout cameraBtn = view.findViewById(R.id.lyt_btn_camera);
        LinearLayout galleryBtn = view.findViewById(R.id.lyt_btn_image);
        LinearLayout lytBtnVideo = view.findViewById(R.id.lyt_btn_video);
        LinearLayout lyrBtnVideoCamera = view.findViewById(R.id.lyt_btn_video_camera);

        cameraBtn.setOnClickListener(view1 -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (getContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = NewCameraUtil.getTakePictureIntent(requireContext());
                    if (cameraIntent == null) {
                        ((ActivityBase) requireActivity()).showToast("can not write to your files to save picture.", requireContext());
                    }
                    try {
                        requireActivity().startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        dismiss();
                    } catch (ActivityNotFoundException e) {
                        ((ActivityBase) requireActivity()).showToast("Can not find your camera.", requireContext());
                    }
                }
            }
        });

        galleryBtn.setOnClickListener(v -> {
            if (checkPermissionREAD_EXTERNAL_STORAGE(requireContext())) {

                //TODO: when API support pdf, use these commented lines
//                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.setType("*/*");
//                String[] mimetypes = {"image/*", "application/pdf/*"};
//                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                requireActivity().startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST);
            }
            dismiss();
        });

        lytBtnVideo.setOnClickListener(view12 -> {
            getGalleryVideo();
            dismiss();
        });
        lyrBtnVideoCamera.setOnClickListener(view13 -> {
            recordVideo();
            dismiss();
        });

        return view;
    }



    public boolean checkPermissionREAD_EXTERNAL_STORAGE(final Context context) {
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

    private Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
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

    private Uri getOutputMediaFileUri1() {
        return FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile());
    }

    private static String getTimeStamp() {
        return new SimpleDateFormat(TIME_STAMP_FORMAT,
                Locale.getDefault()).format(new Date());
    }

}
