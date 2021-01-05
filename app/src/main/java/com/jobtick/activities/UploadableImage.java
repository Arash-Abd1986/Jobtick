package com.jobtick.activities;

import android.content.Intent;

public interface UploadableImage {

    void showAttachmentImageBottomSheet(boolean isImageCircle);
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
