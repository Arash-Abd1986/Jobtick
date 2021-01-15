package com.jobtick.android.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.jobtick.android.R;

public class ExternalIntentHelper {

    public static void openLink(Activity activity, String url){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        String title = activity.getResources().getText(R.string.chooser_title).toString();
        Intent chooser = Intent.createChooser(intent, title);
        activity.startActivity(chooser);
    }
}
