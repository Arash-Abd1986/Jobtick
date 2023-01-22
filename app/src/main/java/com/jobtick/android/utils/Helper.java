package com.jobtick.android.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;

import androidx.core.content.ContextCompat;
import timber.log.Timber;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.jobtick.android.BuildConfig;
import com.jobtick.android.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A class that contains various functions for help in progaramming
 */
public class Helper {

    private static final String lastFileName = "";
    private static int lastFileNameIndex;

    /**
     * A function that takes {@link Activity} as a parameter and closes the keyboard
     *
     * @param activity The current activity
     */
    public static void closeKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * A function that takes {@link Activity} as a parameter and opens the keyboard
     *
     * @param activity The current activity
     */
    public static void openKeyboard(final Activity activity) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {

                View view = activity.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(view, 0);
                }
            }
        });
    }


    public static boolean isConnected(final Activity activity) {

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        //we are connected to a network
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

        return connected;
    }

    /**
     * Removes the toolbar scroll flags
     *
     * @param activity The {@link Activity} comprising the toolbar
     * @param toolbar  The {@link Toolbar} for which scroll flags have to be removed
     */
    public static void removeToolbarFlags(Activity activity, Toolbar toolbar) {
        if (toolbar != null) {
            final AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
            params.setScrollFlags(0);
            toolbar.setLayoutParams(params);
            ((AppCompatActivity) activity).setSupportActionBar(toolbar);
        }
    }

    /**
     * Sets the toolbar scroll flags
     *
     * @param activity The {@link Activity} comprising the toolbar
     * @param toolbar  The {@link Toolbar} for which scroll flags have to be set
     */
    public static void setToolbarFlags(Activity activity, Toolbar toolbar) {
        if (toolbar != null) {
            final AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                    | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
            ((AppCompatActivity) activity).setSupportActionBar(toolbar);
        }
    }

    /**
     * A function to convert dp dimension to px
     *
     * @param context
     * @param dp      The value in dp
     * @return int The value in px
     */
    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /**
     * A function to convert px dimension to dp
     *
     * @param context
     * @param px      The value in px
     * @return int The value in dp
     */
    public static int pxToDp(Context context, int px) {
        return (int) (px / context.getResources().getDisplayMetrics().density);
    }

    /**
     * Creates and returns the Facebook {@link Intent} object
     *
     * @param pm  The {@link PackageManager}
     * @param url The facebook url
     * @return Facebook {@link Intent}
     */
    public static Intent newFacebookIntent(PackageManager pm, String url) {
        Uri uri = Uri.parse(url);
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                uri = Uri.parse("fb://facewebmodal/f?href=" + url);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return new Intent(Intent.ACTION_VIEW, uri);
    }

    /**
     * Opens a share intent with text and image
     *
     * @param context
     * @param itemview  View for which image has to be created
     * @param shareText Text to be shared
     */
    public static void openShareIntent(Context context, @Nullable View itemview, String shareText) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (itemview != null) {
            try {
                File imageUri = getImageFile(context, itemview, "postBitmap.jpeg");
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", imageUri));
                //  intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (IOException e) {
                intent.setType("text/plain");
                e.printStackTrace();
            }
        } else {
            intent.setType("text/plain");
        }
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        context.startActivity(Intent.createChooser(intent, "Share Via:"));
    }

    /**
     * Creates image for the view passed, saves it to a temporary file and returns the {@link Uri}
     *
     * @param context
     * @param view     View for which image has to be created
     * @param fileName Name of the file to save the image
     * @return Image {@link Uri}
     * @throws IOException
     */
    private static Uri getImageUri(Context context, View view, String fileName) throws IOException {
        Bitmap bitmap = loadBitmapFromView(view);
        File pictureFile = new File(context.getExternalCacheDir(), fileName);
        FileOutputStream fos = new FileOutputStream(pictureFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
        fos.close();
        return Uri.parse("file://" + pictureFile.getAbsolutePath());
    }


    private static File getImageFile(Context context, View view, String fileName) throws IOException {
        Bitmap bitmap = loadBitmapFromView(view);
        File pictureFile = new File(context.getExternalCacheDir(), fileName);
        FileOutputStream fos = new FileOutputStream(pictureFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
        fos.close();
        return pictureFile;
    }


    /**
     * Creates bitmap from the view supplied
     *
     * @param v View
     * @return {@link Bitmap}
     */
    private static Bitmap loadBitmapFromView(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            v.setDrawingCacheEnabled(true);
            return v.getDrawingCache();
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }

    /**
     * Opens the play store for this app
     *
     * @param context
     */
    public static void openPlayStoreIntent(Context context) {
        Uri uri = Uri.parse("market://details?id=shoxrux" + context.getApplicationContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        } else {
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        }
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    /**
     * This function copies file from source to destination
     *
     * @param src Source file
     * @param dst Destination file
     * @throws IOException
     */
    public static void copyFile(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    /**
     * Saves the bitmap to a file with the supplied filename if no such file exists or creates a new file by modifying the filename
     *
     * @param context
     * @param bitmap   The {@link Bitmap} to be saved_icon_3x
     * @param fileName The name of the file
     * @throws IOException
     */
    public static void saveBitmap(Context context, @NonNull Bitmap bitmap, @NonNull String fileName) throws IOException {
        File pictureFile = new File(Helper.getRootDir(), fileName);
        saveBitmapToFile(bitmap, pictureFile);
        MediaScannerConnection.scanFile(context, new String[]{pictureFile.getAbsolutePath()}, new String[]{"image/jpeg"}, null);
    }

    /**
     * Saves the bitmap to a temporary file in private directory with the supplied filename
     *
     * @param activity
     * @param bitmap   The {@link Bitmap} to be saved_icon_3x
     * @param fileName The name of the file
     * @throws IOException
     */
    public static void saveBitmapTemporary(Activity activity, Bitmap bitmap, String fileName) throws IOException {
        File pictureFile = new File(activity.getExternalFilesDir(null), fileName);
        saveBitmapToFile(bitmap, pictureFile);
        pictureFile.deleteOnExit();
    }

    /**
     * Saves the bitmap to the filename supplied
     *
     * @param bitmap      {@link Bitmap} to be saved_icon_3x
     * @param pictureFile The name of the file
     * @throws IOException
     */
    public static void saveBitmapToFile(Bitmap bitmap, File pictureFile) throws IOException {
        FileOutputStream fos = new FileOutputStream(pictureFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.close();
    }

    /**
     * Returns the date in relative time like "5 mins ago"
     *
     * @param date The date
     * @return {@link CharSequence} relative time
     */
    public static CharSequence timeDiff(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date startDate = new Date();
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            startDate = simpleDateFormat.parse(date);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return DateUtils.getRelativeTimeSpanString(startDate.getTime(), calendar.getTimeInMillis(), DateUtils.SECOND_IN_MILLIS);
    }

    /**
     * Gets the root external public directory
     *
     * @return root directory
     */
    public static File getRootDir() {
        /*File mydir = context.getDir("users", Context.MODE_PRIVATE); //Creating an internal dir;
        if (!mydir.exists())
        {
            mydir.mkdirs();
        }*/

        File root = new File(Environment.getExternalStorageDirectory() +
                File.separator + "Jobtick");
        root.mkdirs();
        return root;
    }

    /**
     * A function used to enable {@link android.content.BroadcastReceiver}
     *
     * @param context
     * @param mClass  BroadcastReceiver class
     */
    public static void enableBroadcastReceiver(Context context, Class mClass) {
        Timber.d("enable");
        ComponentName receiver = new ComponentName(context, mClass);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    /**
     * A function used to disable {@link android.content.BroadcastReceiver}
     *
     * @param context
     * @param mClass  BroadcastReceiver class
     */
    public static void disableBroadcastReceiver(Context context, Class mClass) {
        Timber.tag("receiver").d("disable");
        ComponentName receiver = new ComponentName(context, mClass);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    /**
     * Returns the current fragment from the {@link FragmentManager}
     *
     * @param activity The current {@link Activity}
     * @return Current {@link Fragment}
     */
    public static Fragment getCurrentFragment(AppCompatActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() == 0) {
            return null;
        }
        String tag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
        return fragmentManager.findFragmentByTag(tag);
    }


    /**
     * <p>It is used to get Color from Attribute</p>
     *
     * @param context
     * @param attr
     * @return
     */
    public static int getAttrColor(Context context, int attr) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(attr, typedValue, true);
        @ColorInt int color = typedValue.data;
        return color;
    }


    /**
     * <p>Show the Message in Logcat</p>
     *
     * @param tag     tag you want to use in your logger
     * @param message message you want to show in logcat
     */
    public static void Logger(String tag, String message) {
        Timber.tag(tag).e(message);
    }



    public static void shareTask(Context context
            , String taskMessage) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = taskMessage;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Jobtick");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }

    public static String getDateWithDesignedFormat(String date)
    {
        String formattedDate ="";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = sdf.parse(date.substring(0, Math.min(date.length(), 10)));
            SimpleDateFormat format = new SimpleDateFormat("EE, MMM d, yyyy");
            formattedDate = format.format(date1);
        }catch (Exception e)
        {}
        return formattedDate;
    }

    public static String getTimeWithDesignedFormat(String date)
    {
        try {
            return date.substring(11, Math.min(date.length(), 19));
        }catch (Exception e)
        {return "";}

    }

    public static void setError(Activity activity, String error, TextInputLayout txtInput) {
        Drawable errorDrawable = ContextCompat.getDrawable(activity, R.drawable.ic_error);
        SpannableString ss = new SpannableString("    " + error + "\n");
        assert errorDrawable != null;
        errorDrawable.setBounds(0, 0, errorDrawable.getIntrinsicWidth(), errorDrawable.getIntrinsicHeight());
        ImageSpan span ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            span = new ImageSpan(errorDrawable, ImageSpan.ALIGN_CENTER);
        } else {
            span = new ImageSpan(errorDrawable, ImageSpan.ALIGN_BOTTOM);
        }
        ss.setSpan(span, 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        txtInput.setError(ss);
    }


}
