package com.example.travelnote.imagehandle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;

/**
 * Created by wuguanglin on 2017/8/9.
 */

public class MyImageLoder {
    private MemoryCacheUtils memoryCacheUtils;//内存缓存
    private LocalCacheUtils localCacheUtils;//本地缓存
    private NetCacheUtils netCacheUtils;//网络“缓存”,直接从网络获取
    private Context context;

    public MyImageLoder(Context context){
        memoryCacheUtils = new MemoryCacheUtils(context);
        localCacheUtils = new LocalCacheUtils(context, memoryCacheUtils);
        netCacheUtils = new NetCacheUtils(memoryCacheUtils, localCacheUtils);
        this.context = context;
    }

    //将图片展示到ImageView中，依次查找内存缓存、本地缓存、网络
    public void display(String url, ImageView imageView){
        Bitmap bitmap;
//        recycle(imageView);
        bitmap = memoryCacheUtils.getBitmap(url);
        if (bitmap != null && !bitmap.isRecycled()){
            imageView.setImageBitmap(bitmap);
            return;
        }
        bitmap = localCacheUtils.getFromLocalCache(url);
        if (bitmap != null){
            imageView.setImageBitmap(bitmap);
            return;
        }
        //直接从网络加载
        netCacheUtils.displayImageFromNet(context, url, imageView);

    }

    //将本地缓存记录同步到journal文件中
    public void flushCache(){
        localCacheUtils.flushCache();
    }

    public void cancelAllTasks(){
        netCacheUtils.cancelAllTasks();
    }

    public void recycle(View view){
        if (view == null){
            return;
        }
        BitmapDrawable bd = (BitmapDrawable) view.getBackground();
        if (bd == null){
            return;
        }
        view.setBackgroundDrawable(null);
        Bitmap bitmap = bd.getBitmap();
        if (bitmap != null && !memoryCacheUtils.isInMemoryCache(bitmap)){
            bitmap.recycle();
        }
    }

}
