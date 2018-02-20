package com.n8sqrd.breadcrumbs.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.camera2.CameraCharacteristics;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.n8sqrd.breadcrumbs.persistence.Path;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ntackett on 1/8/2018.
 */

public class UtilityMethods {



    public static String createPathNameFromDate(Path path) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.PATH_NAME_DATE);
        Date date = path.getStartTime()!=null?path.getStartTime():new Date();

        return "Breadcrumbs starting on " + sdf.format(date);

    }

    public static void logStart(String what) {
        Log.i(Constants.TAG, "logStart: " + what);
    }
    public static void logEnd(String what) {
        Log.i(Constants.TAG, "logEnd: " + what);
    }

    public static void setScaledImage(ImageView imageView,InputStream image) {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(image,null,bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = targetW>0?photoW/targetW:1;

                //Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        //bmOptions.inJustDecodeBounds = false;
        //bmOptions.inSampleSize = scaleFactor;
       // bmOptions.inPurgeable = true;

        //Bitmap bitmap = BitmapFactory.decodeStream(image,null, bmOptions);
        Bitmap bitmap = BitmapFactory.decodeStream(image);
        imageView.setImageBitmap(bitmap);
    }

    public static Bitmap scaleAndRotateImage(String imagePath) {
        int degrees = 0;

        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degrees = 90;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    degrees = 180;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    degrees = 270;
                    break;
            }
        } catch (IOException e) {
            Log.e("ImageError", "Error in reading Exif data of " + imagePath, e);
        }

        BitmapFactory.Options decodeBounds = new BitmapFactory.Options();
        decodeBounds.inJustDecodeBounds = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, decodeBounds);
        int numPixels = decodeBounds.outWidth * decodeBounds.outHeight;
        int maxPixels = 2048 * 1536; // requires 12 MB heap

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = (numPixels > maxPixels) ? 2 : 1;

        bitmap = BitmapFactory.decodeFile(imagePath, options);

        if (bitmap == null) {
            return null;
        }

        Matrix matrix = new Matrix();
        matrix.setRotate(degrees);

        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);

        return bitmap;
    }

}
