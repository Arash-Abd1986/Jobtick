package com.jobtick.activities;

import android.content.Intent;

public interface UploadableImage {

    void showAttachmentBottomSheet();
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
