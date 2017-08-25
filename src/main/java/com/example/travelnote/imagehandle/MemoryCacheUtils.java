package com.example.travelnote.imagehandle;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.example.travelnote.R;

import javax.security.auth.login.LoginException;

/**
 * Created by wuguanglin on 2017/8/9.
 */
/*内存缓存类*/
public class MemoryCacheUtils {
    private Context context;
    private LruCache<String, Bitmap> lruCache;

    public MemoryCacheUtils(final Context context){
        /*maxMemory()当前堆可扩展的最大数目
        * totalMemory()当前堆栈的大小
        * freeMemory()返回当前堆中可用byte大小*/
        this.context = context;
        int maxMemory = (int) Runtime.getRuntime().maxMemory(); //最大内存
        int totalMemory = (int) Runtime.getRuntime().totalMemory();
        Log.e("最大可分配内存", "MemoryCacheUtils: " + maxMemory / (1024*1024));//256M
        Log.e("当前堆栈大小", "MemoryCacheUtils: " + totalMemory / (1024*1024));//20M
        int maxSize = 881024*1024; //缓存空间设为8M
        lruCache = new LruCache<String, Bitmap>(maxSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //返回bitmap所占内存，getRowBytes()为位图每一行所占字节，无API版本要求
                return value.getRowBytes()*value.getHeight();//=getByteCount()，要求API 12
            }

//            @Override
//            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
//                super.entryRemoved(evicted, key, oldValue, newValue);
//                if (evicted && oldValue != null & !oldValue.isRecycled()){
//                    oldValue.recycle();
//                    oldValue = null;
//                }
//            }

//            @Override
//            protected Bitmap create(String key) {
//                return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
//            }
        };
    }

    public Bitmap getBitmap(String url){
        return lruCache.get(url);
    }

    public void setBitmap(String url, Bitmap bitmap){
        lruCache.put(url, bitmap);
    }

    public boolean isInMemoryCache(Bitmap image){

        return false;
    }

}
