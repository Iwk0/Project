package com.tictactoe.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by imishev on 31.7.2014 г..
 */
public class ImageResize {

    public static Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int HEIGHT = options.outHeight;
        final int WIDTH = options.outWidth;
        int inSampleSize = 1;

        if (HEIGHT > reqHeight || WIDTH > reqWidth) {
            if (WIDTH > HEIGHT) {
                inSampleSize = Math.round((float) HEIGHT / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) WIDTH / (float) reqWidth);
            }
        }

        return inSampleSize;
    }
}