package com.jobtick.android.utils.CameraImagePick;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.jobtick.android.BuildConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.activity.result.ActivityResult;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

public class CameraGalleryImagePick extends AppCompatActivity {

    private final Activity activity;
    private Bitmap selectedImage;
    private final ImageView imageView;
//    public final static String ImagePath = Environment.getExternalStorageDirectory() + File.separator + ".welfareassistantyariresan" + File.separator;
    public static String ImagePath;
    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);
    File file;
    public CameraGalleryImagePick(Activity activity, ImageView view)
    {
        File[] externalStorageVolumes =
                ContextCompat.getExternalFilesDirs(activity, null);
        ImagePath = externalStorageVolumes[0] + File.separator + ".welfareassistantyariresan" + File.separator;
        file = new File(ImagePath);
        this.activity = activity;
        imageView = view ;
    }
    public Bitmap openSomeActivityForResult(ImagePickedStatus mode) {
        selectedImage = null;
        Intent intent;
        switch (mode)
        {
            case Gallery:
                intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                break;

            case Camera:
            default:
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
                break;

        }
        String rowPhoto = ImagePath + "picture.jpg";
        int compressLevel = 40;
        activityLauncher.launch(intent, result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {

                switch (mode) {

                    case Gallery: {
                            final Uri imageUri = intent.getData();
                            final InputStream imageStream1;
                            try {
                                imageStream1 = activity.getContentResolver().openInputStream(imageUri);
                                selectedImage = BitmapFactory.decodeStream(imageStream1);
                                if (selectedImage == null) {
                                    Toast.makeText(activity, "خطا در دریافت عکس!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                long time = System.currentTimeMillis();
                                File f = new File(file, time + ".jpg");
                                FileOutputStream out = new FileOutputStream(f);


                                /* rotating the photo*/
                                Matrix matrix = new Matrix();
                                matrix.postRotate(0);

                                int targetWidth = 1200;
                                int targetHeight = (int) (selectedImage.getHeight() * targetWidth / (double) selectedImage.getWidth());
                                selectedImage = Bitmap.createScaledBitmap(selectedImage, targetWidth, targetHeight, false);

                                /* compress the photo and save it as new*/
                                selectedImage.compress(Bitmap.CompressFormat.JPEG, compressLevel, out);
                                imageView.setImageBitmap(selectedImage);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                        break;

                    case Camera: {
                            try {
                                /* get the image*/
                                BitmapFactory.Options options;
                                options = new BitmapFactory.Options();
                                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                                Bitmap photo = BitmapFactory.decodeFile(rowPhoto, options);

                                if (photo == null) {
                                    Toast.makeText(activity, "error on recieving image", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                /* name the photo*/
                                long time = System.currentTimeMillis();
                                File f = new File(file, time + ".jpg");
                                FileOutputStream out = new FileOutputStream(f);

                                /* rotating the photo*/
                                Matrix matrix = new Matrix();
                                matrix.postRotate(90);

                                int targetWidth = 1200;
                                int targetHeight = (int) (photo.getHeight() * targetWidth / (double) photo.getWidth());
                                photo = Bitmap.createScaledBitmap(photo, targetWidth, targetHeight, false);
                                photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);
                                photo.compress(Bitmap.CompressFormat.JPEG, compressLevel, out);
                                imageView.setImageBitmap(selectedImage);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        break;
                    }
                }

            }
        });
        return selectedImage;
    }

    public Uri setImageUri() {
        File file = new File(ImagePath + "picture.jpg");
        Uri uri = Uri.fromFile(file);
        Uri imgUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", file);
        if (Build.VERSION.SDK_INT >= 24)
            return imgUri;
        else
            return uri;
    }
}
