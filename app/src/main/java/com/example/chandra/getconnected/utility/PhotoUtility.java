package com.example.chandra.getconnected.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.parse.ParseFile;

import java.io.ByteArrayOutputStream;

/**
 * Created by chandra on 11/29/2015.
 */
public class PhotoUtility {

    public static Bitmap decodeSampledBitmap(byte[] data) {
        int width = 500;
        int height = 500;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        return compressBitmap(BitmapFactory.decodeByteArray(data, 0, data.length, options));
    }

    public static Bitmap compressBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return bitmap;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }

            long totalPixels = width * height / inSampleSize;
            final long totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels > totalReqPixelsCap) {
                inSampleSize *= 2;
                totalPixels /= 2;
            }
        }
        return inSampleSize;
    }


    public static String convertImageToString(Bitmap image) {

        ByteArrayOutputStream arrayStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, arrayStream);
        byte[] b = arrayStream.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }


    public static Bitmap convertStringToImage(String imageString) {

        byte[] b = Base64.decode(imageString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }


    public static ParseFile getParseFileFromBitmap(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 0, stream);
        byte[] temp = stream.toByteArray();
        return new ParseFile("thumbnail.png", temp);
    }

}