package com.example.travelnote.bean;

/**
 * Created by wuguanglin on 2017/8/21.
 */

public class ImageViewSize {
    //ImageView的宽度，用于options.insampleSize计算缩放比例
    private int imageWidth;
    //ImageView高度
    private int imageHeight;


    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }
}
