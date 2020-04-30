package com.bignerdranch.watashinonikki;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * Image Handling. SInce the file when imported through bitmap is uncompresed, it's size is huge
 * Since the ImageView is small, we can scale it down so as to reduce the image's footprint
 */

public class PictureUtils {
    public static Bitmap getScaledBitmap(String photoPath, int destHeight, int destWidth ){
        //Read file's dimensions( pixel)
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, options);

        // Retrieve Source's Height and Width
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        // Figure out how much to scale down by
        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            float heightScale = srcHeight / destHeight;
            float widthScale = srcWidth / destWidth;
            inSampleSize = Math.round(heightScale > widthScale ? heightScale :
                    widthScale);
        }

        options = new BitmapFactory.Options();
        //Reduces height and width by n, where n is the value of inSampleSize (in int)
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(photoPath,options);

    }
}
