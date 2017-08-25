package com.example.travelnote.utils;

import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.example.travelnote.bean.ImageViewSize;

/**
 * Created by wuguanglin on 2017/8/21.
 */

public class ImageViewSizeUtils {
    //获取ImageView的宽和高
    public static ImageViewSize getImageViewSize(ImageView imageView){
        ImageViewSize imageViewSize = new ImageViewSize();
        DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
        LayoutParams layoutParams = imageView.getLayoutParams();
        int width = imageView.getWidth();
        if (width < 0){
            width = layoutParams.width;//在layout中声明的宽度
        }
        if (width <= 0){
            width = displayMetrics.widthPixels;//屏幕宽度
        }
        int height = imageView.getHeight();
        if (height <= 0){
            height = layoutParams.height;//layout中声明的高度
        }
        if (height <= 0){
            height = 200;
        }
        imageViewSize.setImageWidth(width);
        imageViewSize.setImageHeight(height);
        return imageViewSize;
    }
}
