package com.jobtick.activities;

import android.content.Intent;

public interface UploadableImage {

    void showAttachmentBottomSheet(boolean isImageCircle);
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
