package com.jobtick.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;

import com.jobtick.R;
import com.yalantis.ucrop.UCrop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import timber.log.Timber;

public class OnUCropImageImpl implements OnCropImage{

    private final UriPathHelper uriPathHelper;
    private final Activity activity;

    public OnUCropImageImpl(Activity activity) {
        this.activity = activity;
        uriPathHelper = new UriPathHelper(activity);
    }


    @Override
    public void crop(Uri uri, boolean isCircle) {

        String imageCachePath = uriPathHelper.getCachePath(uri);
        Uri cacheCopyUri =  Uri.fromFile(new File(imageCachePath));
        File cacheCopyFile = new File(imageCachePath);
        Uri destinationUri = Uri.fromFile(new File(activity.getCacheDir(), "crop_" + cacheCopyFile.getName()));

        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCircleDimmedLayer(isCircle);

        options.setActiveControlsWidgetColor(activity.getColor(R.color.colorPrimary));
        options.setStatusBarColor(activity.getColor(R.color.backgroundLightGrey));
        options.setToolbarColor(activity.getColor(R.color.backgroundLightGrey));
        options.setDimmedLayerColor(activity.getColor(R.color.backgroundLightGrey));
        options.setRootViewBackgroundColor(activity.getColor(R.color.backgroundLightGrey));
        if(isCircle) {
            options.setCropFrameColor(activity.getColor(R.color.transparent));
            options.setCropGridColor(activity.getColor(R.color.transparent));

            UCrop.of(cacheCopyUri, destinationUri)
                    .withAspectRatio(3, 3)
                    .withMaxResultSize(768, 768)
                    .withOptions(options)
                    .start(activity);
        }  else{

            options.setCropFrameColor(activity.getColor(R.color.N070));
            options.setCropGridColor(activity.getColor(R.color.N050));

            UCrop.of(cacheCopyUri, destinationUri)
                    .withAspectRatio(4, 3)
                    .withMaxResultSize(1024, 768)
                    .withOptions(options)
                    .start(activity);
            }
    }


    private class UriPathHelper {

        private Uri contentUri = null;
        private final Context context;

        public UriPathHelper(Context context) {
            this.context=context;
        }

        @SuppressLint("NewApi")
        public String getCachePath(final Uri uri) {
            // check here to KITKAT or new version
            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
            String selection = null;
            String[] selectionArgs = null;
            // DocumentProvider
            if (isKitKat ) {
                // ExternalStorageProvider

                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    String fullPath = getPathFromExtSD(split);
                    if (!fullPath.equals("")) {
                        return fullPath;
                    } else {
                        return null;
                    }
                }


                // DownloadsProvider

                if (isDownloadsDocument(uri)) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        final String id;
                        try (Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null)) {
                            if (cursor != null && cursor.moveToFirst()) {
                                String fileName = cursor.getString(0);
                                String path = Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
                                if (!TextUtils.isEmpty(path)) {
                                    return path;
                                }
                            }
                        }
                        id = DocumentsContract.getDocumentId(uri);
                        if (!TextUtils.isEmpty(id)) {
                            if (id.startsWith("raw:")) {
                                return id.replaceFirst("raw:", "");
                            }
                            String[] contentUriPrefixesToTry = new String[]{
                                    "content://downloads/public_downloads",
                                    "content://downloads/my_downloads"
                            };
                            for (String contentUriPrefix : contentUriPrefixesToTry) {
                                try {
                                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.valueOf(id));


                                    return getDataColumn(context, contentUri, null, null);
                                } catch (NumberFormatException e) {
                                    //In Android 8 and Android P the id is not a number
                                    return uri.getPath().replaceFirst("^/document/raw:", "").replaceFirst("^raw:", "");
                                }
                            }
                        }
                    }
                    else {
                        final String id = DocumentsContract.getDocumentId(uri);

                        if (id.startsWith("raw:")) {
                            return id.replaceFirst("raw:", "");
                        }
                        try {
                            contentUri = ContentUris.withAppendedId(
                                    Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                        }
                        catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        if (contentUri != null) {

                            return getDataColumn(context, contentUri, null, null);
                        }
                    }
                }


                // MediaProvider
                if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;

                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    selection = "_id=?";
                    selectionArgs = new String[]{split[1]};


                    return getDataColumn(context, contentUri, selection,
                            selectionArgs);
                }

                if (isGoogleDriveUri(uri)) {
                    return getDriveFilePath(uri);
                }

                if(isWhatsAppFile(uri)){
                    return getFilePathForWhatsApp(uri);
                }


                if ("content".equalsIgnoreCase(uri.getScheme())) {

                    if (isGooglePhotosUri(uri)) {
                        return uri.getLastPathSegment();
                    }
                    if (isGoogleDriveUri(uri)) {
                        return getDriveFilePath(uri);
                    }
                    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    {

                        // return getFilePathFromURI(context,uri);
                        return copyFileToInternalStorage(uri,"userfiles");
                        // return getRealPathFromURI(context,uri);
                    }
                    else
                    {
                        return getDataColumn(context, uri, null, null);
                    }

                }
                if ("file".equalsIgnoreCase(uri.getScheme())) {
                    return uri.getPath();
                }
            }
            else {

                if(isWhatsAppFile(uri)){
                    return getFilePathForWhatsApp(uri);
                }

                if ("content".equalsIgnoreCase(uri.getScheme())) {
                    String[] projection = {
                            MediaStore.Images.Media.DATA
                    };
                    Cursor cursor = null;
                    try {
                        cursor = context.getContentResolver()
                                .query(uri, projection, selection, selectionArgs, null);
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        if (cursor.moveToFirst()) {
                            return cursor.getString(column_index);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }




            return null;
        }

        private  boolean fileExists(String filePath) {
            File file = new File(filePath);

            return file.exists();
        }

        private String getPathFromExtSD(String[] pathData) {
            final String type = pathData[0];
            final String relativePath = "/" + pathData[1];
            String fullPath = "";

            // on my Sony devices (4.4.4 & 5.1.1), `type` is a dynamic string
            // something like "71F8-2C0A", some kind of unique id per storage
            // don't know any API that can get the root path of that storage based on its id.
            //
            // so no "primary" type, but let the check here for other devices
            if ("primary".equalsIgnoreCase(type)) {
                fullPath = Environment.getExternalStorageDirectory() + relativePath;
                if (fileExists(fullPath)) {
                    return fullPath;
                }
            }

            // Environment.isExternalStorageRemovable() is `true` for external and internal storage
            // so we cannot relay on it.
            //
            // instead, for each possible path, check if file exists
            // we'll start with secondary storage as this could be our (physically) removable sd card
            fullPath = System.getenv("SECONDARY_STORAGE") + relativePath;
            if (fileExists(fullPath)) {
                return fullPath;
            }

            fullPath = System.getenv("EXTERNAL_STORAGE") + relativePath;
            if (fileExists(fullPath)) {
                return fullPath;
            }

            return fullPath;
        }

        private String getDriveFilePath(Uri uri) {
            Uri returnUri = uri;
            Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
            /*
             * Get the column indexes of the data in the Cursor,
             *     * move to the first row in the Cursor, get the data,
             *     * and display it.
             * */
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            String name = (returnCursor.getString(nameIndex));
            String size = (Long.toString(returnCursor.getLong(sizeIndex)));
            File file = new File(context.getCacheDir(), name);
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                FileOutputStream outputStream = new FileOutputStream(file);
                int read = 0;
                int maxBufferSize = 1 * 1024 * 1024;
                int bytesAvailable = inputStream.available();

                //int bufferSize = 1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);

                final byte[] buffers = new byte[bufferSize];
                while ((read = inputStream.read(buffers)) != -1) {
                    outputStream.write(buffers, 0, read);
                }
                Timber.e("Size %s", file.length());
                inputStream.close();
                outputStream.close();
                Timber.e("Path %s", file.getPath());
                Timber.e("Size %s", file.length());
            } catch (Exception e) {
                Timber.e(e);
            }
            return file.getPath();
        }

        /***
         * Used for Android Q+
         * @param uri
         * @param newDirName if you want to create a directory, you can set this variable
         * @return
         */
        public String copyFileToInternalStorage(Uri uri,String newDirName) {
            Uri returnUri = uri;

            Cursor returnCursor = context.getContentResolver().query(returnUri, new String[]{
                    OpenableColumns.DISPLAY_NAME,OpenableColumns.SIZE
            }, null, null, null);


            /*
             * Get the column indexes of the data in the Cursor,
             *     * move to the first row in the Cursor, get the data,
             *     * and display it.
             * */
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            String name = (returnCursor.getString(nameIndex));
            String size = (Long.toString(returnCursor.getLong(sizeIndex)));

            File output;
            if(!newDirName.equals("")) {
                File dir = new File(context.getCacheDir() + "/" + newDirName);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                output = new File(context.getCacheDir() + "/" + newDirName + "/" + name);
            }
            else{
                output = new File(context.getCacheDir() + "/" + name);
            }
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                FileOutputStream outputStream = new FileOutputStream(output);
                int read = 0;
                int bufferSize = 1024;
                final byte[] buffers = new byte[bufferSize];
                while ((read = inputStream.read(buffers)) != -1) {
                    outputStream.write(buffers, 0, read);
                }

                inputStream.close();
                outputStream.close();

            }
            catch (Exception e) {

                Timber.e(e);
            }

            return output.getPath();
        }

        private String getFilePathForWhatsApp(Uri uri){
            return  copyFileToInternalStorage(uri,"whatsapp");
        }

        private String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
            Cursor cursor = null;
            final String column = "_data";
            final String[] projection = {column};

            try {
                cursor = context.getContentResolver().query(uri, projection,
                        selection, selectionArgs, null);

                if (cursor != null && cursor.moveToFirst()) {
                    final int index = cursor.getColumnIndexOrThrow(column);
                    return cursor.getString(index);
                }
            }
            finally {
                if (cursor != null)
                    cursor.close();
            }

            return null;
        }

        private boolean isExternalStorageDocument(Uri uri) {
            return "com.android.externalstorage.documents".equals(uri.getAuthority());
        }

        private boolean isDownloadsDocument(Uri uri) {
            return "com.android.providers.downloads.documents".equals(uri.getAuthority());
        }

        private boolean isMediaDocument(Uri uri) {
            return "com.android.providers.media.documents".equals(uri.getAuthority());
        }

        private boolean isGooglePhotosUri(Uri uri) {
            return "com.google.android.apps.photos.content".equals(uri.getAuthority());
        }

        private boolean isWhatsAppFile(Uri uri){
            return "com.whatsapp.provider.media".equals(uri.getAuthority());
        }

        private boolean isGoogleDriveUri(Uri uri) {
            return "com.google.android.apps.docs.storage".equals(uri.getAuthority()) || "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
        }


    }
}
