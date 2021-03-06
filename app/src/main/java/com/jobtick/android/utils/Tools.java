package com.jobtick.android.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;

import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.jobtick.android.R;
import android.annotation.SuppressLint;
import timber.log.Timber;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Tools {

    public static String formatFullDate(String timeString) {
        long timeInMillis = 0;
        try {
            timeInMillis = dateToMillis(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        return dateFormat.format(timeInMillis).toUpperCase();
    }
    public static String formatChatFullDate(String timeString) {
        long timeInMillis = 0;
        try {
            timeInMillis = chatDateToMillis(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        return dateFormat.format(timeInMillis).toUpperCase();
    }

    public static String formatLocalTime(long timeInMillis) {
        SimpleDateFormat dateFormatUTC = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        dateFormatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = dateFormatUTC.parse(formatTime(timeInMillis));
        } catch (ParseException e) {

        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getDefault());
        if (date == null) {
            return dateFormat.format(timeInMillis);
        }
        return dateFormat.format(date);
    }

    /**
     * Gets timestamp in millis and converts it to HH:mm (e.g. 16:44).
     */
    public static String formatTime(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }
    public static String formatJobDetailsDate(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }
    public static boolean hasSameDate(long millisFirst, long millisSecond) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return dateFormat.format(millisFirst).equals(dateFormat.format(millisSecond));
    }

    public static long dateToMillis(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = sdf.parse(dateString);
        return date.getTime();
    }
    public static long chatDateToMillis(String dateString) throws ParseException {
        dateString =dateString.replace("T"," ");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss+00:00", Locale.getDefault());
        Date date = sdf.parse(dateString);
        return date.getTime();
    }

    public static long jobDetailsDate(String dateString) throws ParseException {
        dateString =dateString.replace("T"," ");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = sdf.parse(dateString);
        return date.getTime();
    }

    public static long compareTwoDate(String untilDate) {
        Date userDob = null;
        try {
            userDob = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SSSZZZZZ").parse(untilDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date today = new Date();
        long diff =  userDob.getTime()-today.getTime();
        int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
        return numOfDays;
    }

    public static void setSystemBarColor(Activity act) {
        Window window = act.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(act.getResources().getColor(R.color.colorPrimaryDark));

    }

    public static void setSystemBarColor(Activity act, @ColorRes int color) {
        Window window = act.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(act.getResources().getColor(color));

    }

    /**
     * <p>It is used to get String values from Resource file</p>
     *
     * @param context
     * @param label
     * @return
     */
    public static String getStringFromRes(Context context, int label) {
        if (context == null || label == 0)
            return null;
        return context.getResources().getString(label);
    }

    public static void setSystemBarLight(Activity act) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View view = act.findViewById(android.R.id.content);
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
        }
    }

    public static void clearSystemBarLight(Activity act) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = act.getWindow();
            window.setStatusBarColor(ContextCompat.getColor(act, R.color.background));
        }
    }

    /**
     * Making notification bar transparent
     */
    public static void setSystemBarTransparent(Activity act) {
        Window window = act.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

    }

    public static void displayImageOriginal(Context ctx, ImageView img, @DrawableRes int drawable) {
        try {
            Glide.with(ctx).load(drawable)
                    //      .crossFade()
                    //       .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(img);
        } catch (Exception e) {
        }
    }

    public static String getApplicationFromatToServerFormat(String dateTime) {
        if (dateTime != null && !dateTime.equalsIgnoreCase("")) {
            SimpleDateFormat oldFormat = new SimpleDateFormat("MMMM dd, yyyy");
            Date old_date = new Date();
            try {
                old_date = oldFormat.parse(dateTime);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");

            return newFormat.format(new Date(String.valueOf(old_date)));
        } else {
            return null;
        }
    }

    public static String getDayMonthDateTimeFormat(String dateTime) {
        if (dateTime == null) {
            return "";
        }
        SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date old_date = new Date();
        try {
            old_date = utcFormat.parse(dateTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat newFormat = new SimpleDateFormat("MMMM dd, yyyy");

        return newFormat.format(new Date(String.valueOf(old_date)));

    }

    public static String getDayDateTimeFormat1(String dateTime) {
        SimpleDateFormat utcFormat;
        try {
            utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");
        } catch (Exception e) {
            utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        }
        Date old_date = new Date();
        try {
            old_date = utcFormat.parse(dateTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat newFormat = new SimpleDateFormat("EEE, MMM dd yyyy HH:mm:ss");

        return newFormat.format(new Date(String.valueOf(old_date)));
        /*SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date old_date = new Date();
        try {
            old_date = oldFormat.parse(dateTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat newFormat = new SimpleDateFormat("MMMM dd, yyyy");

        return newFormat.format(new Date(String.valueOf(old_date)));*/
    }

    public static String getGlobalDateTimeFormat(String dateTime) {
        SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");
        Date old_date = new Date();
        try {
            old_date = utcFormat.parse(dateTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return newFormat.format(new Date(String.valueOf(old_date)));
    }


    public static String getGlobalFormat(String dateTime) {
        SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date old_date = new Date();
        try {
            old_date = utcFormat.parse(dateTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat newFormat = new SimpleDateFormat("MMMM dd, yyyy");

        return newFormat.format(new Date(String.valueOf(old_date)));
    }

    public static String getExpireDateFormat(String dateTime) {

        SimpleDateFormat oldFormat = new SimpleDateFormat("MM/yyyy");
        Date old_date = new Date();
        try {
            old_date = oldFormat.parse(dateTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat newFormat = new SimpleDateFormat("MMMM, yyyy");

        return newFormat.format(new Date(String.valueOf(old_date)));
    }

    public static String getDayMonthDateTimeFormat2(String dateTime) {
        if (dateTime == null) {
            return "";
        }
        SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date old_date = new Date();
        try {
            old_date = utcFormat.parse(dateTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat newFormat = new SimpleDateFormat("dd MMM yyyy");

        return newFormat.format(new Date(String.valueOf(old_date)));

    }

/*
    public static void displayImageRound(final Context ctx, final ImageView img, @DrawableRes int drawable) {
        try {
          */
/*  Glide.with(ctx).load(drawable)

                 //   .centerCrop()
                    .into(new BitmapImageViewTarget(img) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(ctx.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    img.setImageDrawable(circularBitmapDrawable);
                }
            });*//*


            Glide.with(ctx)
                    .asBitmap()
                    .load(drawable)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            img.setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });


        } catch (Exception e) {
        }
    }
*/

/*    public static void displayImageOriginal(Context ctx, ImageView img, String url) {
        try {
            Glide.with(ctx).load(url)
                 //   .crossFade()
                //    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(img);
        } catch (Exception e) {
        }
    }*/

    public static String getFormattedDateSimple(Long dateTime) {
        SimpleDateFormat newFormat = new SimpleDateFormat("MMMM dd, yyyy");
        return newFormat.format(new Date(dateTime));
    }

    public static String getFormattedDateEvent(Long dateTime) {
        SimpleDateFormat newFormat = new SimpleDateFormat("EEE, MMM dd yyyy");
        return newFormat.format(new Date(dateTime));
    }

    public static Long getTimeStamp(String dateTime) {
        if (dateTime != null) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat utcFormat = new SimpleDateFormat("MMMM dd, yyyy");
            Date old_date = new Date();
            try {
                old_date = utcFormat.parse(dateTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (old_date != null) {
                return old_date.getTime();
            } else {
                return new Date().getTime();
            }
        } else {
            return new Date().getTime();
        }
    }


    public static String getFormattedTimeEvent(Long time) {
        SimpleDateFormat newFormat = new SimpleDateFormat("h:mm a");
        return newFormat.format(new Date(time));
    }

    public static String getEmailFromName(String name) {
        if (name != null && !name.equals("")) {
            String email = name.replaceAll(" ", ".").toLowerCase().concat("@mail.com");
            return email;
        }
        return name;
    }

    public static int dpToPx(Context c, int dp) {
        Resources r = c.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public static GoogleMap configActivityMaps(GoogleMap googleMap) {
        // set map type
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Enable / Disable zooming controls
        googleMap.getUiSettings().setZoomControlsEnabled(false);

        // Enable / Disable Compass icon
        googleMap.getUiSettings().setCompassEnabled(true);
        // Enable / Disable Rotate gesture
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        // Enable / Disable zooming functionality
        googleMap.getUiSettings().setZoomGesturesEnabled(true);

        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);

        return googleMap;
    }

    public static void copyToClipboard(Context context, String data) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("clipboard", data);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    public static void nestedScrollTo(final NestedScrollView nested, final View targetView) {
        nested.post(new Runnable() {
            @Override
            public void run() {
                nested.scrollTo(500, targetView.getBottom());
            }
        });
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static boolean toggleArrow(View view) {
        if (view.getRotation() == 0) {
            view.animate().setDuration(200).rotation(180);
            return true;
        } else {
            view.animate().setDuration(200).rotation(0);
            return false;
        }
    }

    public static boolean toggleArrow(boolean show, View view) {
        return toggleArrow(show, view, true);
    }

    public static boolean toggleArrow(boolean show, View view, boolean delay) {
        if (show) {
            view.animate().setDuration(delay ? 200 : 0).rotation(180);
            return true;
        } else {
            view.animate().setDuration(delay ? 200 : 0).rotation(0);
            return false;
        }
    }

    public static void changeNavigateionIconColor(Toolbar toolbar, @ColorInt int color) {
        Drawable drawable = toolbar.getNavigationIcon();
        drawable.mutate();
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    public static void changeMenuIconColor(Menu menu, @ColorInt int color) {
        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable == null) continue;
            drawable.mutate();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }

    public static void changeOverflowMenuIconColor(Toolbar toolbar, @ColorInt int color) {
        try {
            Drawable drawable = toolbar.getOverflowIcon();
            drawable.mutate();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        } catch (Exception e) {
        }
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static String toCamelCase(String input) {
        input = input.toLowerCase();
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }

        return titleCase.toString();
    }

    public static String insertPeriodically(String text, String insert, int period) {
        StringBuilder builder = new StringBuilder(text.length() + insert.length() * (text.length() / period) + 1);
        int index = 0;
        String prefix = "";
        while (index < text.length()) {
            builder.append(prefix);
            prefix = insert;
            builder.append(text.substring(index, Math.min(index + period, text.length())));
            index += period;
        }
        return builder.toString();
    }


    public static DatePickerDialog createDialogWithoutDateField(Activity context, int dialog, DatePickerDialog.OnDateSetListener mDateSetListener, int year, int month, int day) {
        DatePickerDialog dpd = new DatePickerDialog(context, dialog, mDateSetListener, year, month, day);
        try {
            java.lang.reflect.Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
                    java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                    for (java.lang.reflect.Field datePickerField : datePickerFields) {
                        if ("mDaySpinner".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Timber.tag("EX").e(ex);
        }
        return dpd;
    }


}
