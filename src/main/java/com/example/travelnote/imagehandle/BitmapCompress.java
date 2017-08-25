package com.example.travelnote.imagehandle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.widget.ImageView;

import com.example.travelnote.bean.ImageViewSize;
import com.example.travelnote.utils.ImageViewSizeUtils;
import com.orhanobut.logger.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by wuguanglin on 2017/8/9.
 */

public class BitmapCompress {
    //bitmap占用内存 = bitmap宽 * bitmap高 * 单个像素点所占字节
    private ImageViewSize imageViewSize;

    //
    public Bitmap getSmallBitmap(ImageView imageView, byte[] byteBitmap) {
        imageViewSize = ImageViewSizeUtils.getImageViewSize(imageView);
        Logger.e("imageview宽高", imageViewSize.getImageHeight());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(byteBitmap, 0, byteBitmap.length, options);
        options.inSampleSize = calculateInSampleSize(options, imageViewSize.getImageWidth(), imageViewSize.getImageHeight());
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;//一个像素点占用2个字节
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteBitmap, 0, byteBitmap.length, options);
        if (bitmap != null) {
            return bitmap;
        }
        return null;
    }

    public Bitmap getMartixScaleBitmap(ImageView imageView, byte[] byteBitmap){
        imageViewSize = ImageViewSizeUtils.getImageViewSize(imageView);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(byteBitmap, 0, byteBitmap.length, options);
        float zootSize = calculateFloatZoomSize(options, imageViewSize.getImageWidth(), imageViewSize.getImageHeight());
        options.inJustDecodeBounds = false;
        Matrix matrix = new Matrix();
        matrix.setScale(zootSize, zootSize);
        Bitmap bitmapSource = BitmapFactory.decodeByteArray(byteBitmap, 0, byteBitmap.length);
        Bitmap bitmap = Bitmap.createBitmap(bitmapSource, 0, 0, bitmapSource.getWidth(), bitmapSource.getHeight(), matrix, true);
        if (bitmap != null){
            return bitmap;
        }
        return null;
    }

    //根据ImageView的宽和高，网络下载的图片的宽和高计算压缩比例
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int inSampleSize = 1;
        int w = options.outWidth;//图片宽
        int h = options.outHeight;//图片高
        if (reqWidth < w || reqHeight < h) {
            int wratio = Math.round((w * 1.0f) / reqWidth);
            int hratio = Math.round((h * 1.0f) / reqWidth);
            inSampleSize = Math.max(wratio, hratio);
        }
        return inSampleSize;
    }

    //计算缩放比例，返回浮点数，设置Matrix的缩放
    private float calculateFloatZoomSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
        float rootSize = 1.0f;
        int w = options.outWidth;
        int h = options.outHeight;//bitmap的宽和高
        if (reqWidth < w || reqHeight < h){
            float wratio = (float) w /  reqWidth;
            float hratio = (float) h / reqHeight;
            rootSize = Math.max(wratio, hratio);
        }
        return rootSize;
    }
    //质量压缩,不改变内存占用大小,存到文件系统会变小
    private Bitmap compressBitmap(Bitmap image) {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bao);
        int options = 90;
        while (bao.toByteArray().length / 1024 > 100) {
            bao.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, bao);
            options -= 10;
            if (options < 0){
                options = 90;
            }
        }
        ByteArrayInputStream is = new ByteArrayInputStream(bao.toByteArray());
        return BitmapFactory.decodeStream(is);//返回bitmap相当于什么都没做
    }
}
