package com.jobtick.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.FragmentActivity;

import com.jobtick.AppController;
import com.jobtick.fragments.AttachmentBottomSheet;
import com.jobtick.utils.NewCameraUtil;
import com.jobtick.utils.OnCropImage;
import com.jobtick.utils.OnUCropImageImpl;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import static android.app.Activity.RESULT_OK;
import static com.jobtick.fragments.AttachmentBottomSheet.CAMERA_REQUEST;
import static com.jobtick.fragments.AttachmentBottomSheet.GALLERY_REQUEST;

public abstract class AbstractUploadableImageImpl implements UploadableImage {

    private final FragmentActivity activity;
    private final AttachmentBottomSheet attachmentBottomSheet;
    private boolean isCircle;


    public AbstractUploadableImageImpl(FragmentActivity activity) {
        this.activity = activity;
        attachmentBottomSheet = new AttachmentBottomSheet();
    }

    @Override
    public void showAttachmentBottomSheet(boolean isCircle){
        this.isCircle = isCircle;
        attachmentBottomSheet.show(activity.getSupportFragmentManager(), "");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            File file = new File(resultUri.getPath());
            onImageReady(file);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            if(cropError != null)
                ((AppController) activity.getApplicationContext()).mCrashlytics.recordException(cropError);
        }


        if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri filePath = data.getData();
            OnCropImage onCropImage = new OnUCropImageImpl(activity);
            onCropImage.crop(filePath, isCircle);
        }

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {

            String imagePath = NewCameraUtil.getImagePath();
            if(imagePath == null){
                ((ActivityBase)activity).showToast("Can not find image.", activity);
                return;
            }
            Uri imageUri = Uri.fromFile(new File(imagePath));
            OnCropImage cropImage = new OnUCropImageImpl(activity);
            cropImage.crop(imageUri, isCircle);
        }
    }

    public abstract void onImageReady(File imageFile);

    public boolean isCircle() {
        return isCircle;
    }

    public void setCircle(boolean circle) {
        isCircle = circle;
    }
}
