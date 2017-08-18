package com.example.travelnote.imagehandle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by wuguanglin on 2017/8/9.
 */

public class BitmapCompress {
    //
    public Bitmap getSmallBitmap(byte[] byteBitmap) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = calculateInSampleSize(options, 1280, 960);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteBitmap, 0, byteBitmap.length, options);
        if (bitmap != null) {
            return compressBitmap(bitmap);
        }
        return null;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int inSampleSize = 1;
        int w = options.outWidth;
        int h = options.outHeight;
        if (reqWidth < w || reqHeight < h) {
            float wratio = w / reqWidth;
            float hratio = h / reqHeight;
            inSampleSize = wratio > hratio ? (int)wratio : (int)hratio;
        }
        return inSampleSize;
    }

    private Bitmap compressBitmap(Bitmap image) {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bao);
        int options = 90;
        while (bao.toByteArray().length / 1024 > 100) {
            bao.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, bao);
            options -= 10;
        }
        ByteArrayInputStream is = new ByteArrayInputStream(bao.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        return bitmap;
    }
}
