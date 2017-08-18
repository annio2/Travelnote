package com.example.travelnote.imagehandle;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.example.travelnote.libcore.io.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by wuguanglin on 2017/8/10.
 */

public class LocalCacheUtils {
    private Context context;
    private File cache_dir;//图片存储路径
    private MemoryCacheUtils memoryCacheUtils;
    private DiskLruCache mDisLruCache;

    public LocalCacheUtils(Context context, MemoryCacheUtils memoryCacheUtils){
        this.context = context;
        this.memoryCacheUtils = memoryCacheUtils;
        cache_dir = getDiskCacheDir(context, "image");
        if (!cache_dir.exists()){
            boolean isSuccessful = cache_dir.mkdirs();
            if (!isSuccessful){
                Log.e("建立缓存文件夹失败", "LocalCacheUtils: ");
            }
        }
        try {
            mDisLruCache = DiskLruCache.open(cache_dir, getAppVersion(context), 1, 10*1024*1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //存入本地缓存
    public void addToLocalCache(String urlString, Bitmap bitmap){
        String fileName = encodeToMD5(urlString);
        try {
            DiskLruCache.Editor editor = mDisLruCache.edit(fileName);
            OutputStream outputStream = editor.newOutputStream(0);
            if (bitmapToStream(bitmap, outputStream)){
                editor.commit();
            }else {
                editor.abort();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //将缓存记录同步到journal文件中
    public void flushCache(){
        if (mDisLruCache != null){
            try {
                mDisLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //从本地缓存中读取并存入内存缓存
    public Bitmap getFromLocalCache(String url){
        String key = encodeToMD5(url);
        try {
            DiskLruCache.Snapshot snapshot = mDisLruCache.get(key);
            if (snapshot != null){
                InputStream inputStream = snapshot.getInputStream(0);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (bitmap != null){
                    memoryCacheUtils.setBitmap(url, bitmap);
                    return bitmap;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean bitmapToStream(Bitmap bitmap, OutputStream outputStream){
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        //将bitmap转为InputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
        try {
            in = new BufferedInputStream(inputStream, 8*1024);
            out = new BufferedOutputStream(outputStream, 8*1024);
            int b;
            //in.read()读取下一个字节
            while ((b = in.read()) != -1){
                //out.write(int)将指定字节写入输出流
                out.write(b);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (out != null){
                    out.close();
                }
                if (in != null){
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //从文件中获取bitmap，并且存入内存
    public Bitmap getBitmap(String url){
        String fileName = encodeToMD5(url);
        File file = new File(cache_dir, fileName);
        if (file.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            memoryCacheUtils.setBitmap(url, bitmap);
            return bitmap;
        }
        return null;
    }

    //网络下载之后存入本地
    public void setBitmap(String url, Bitmap bitmap){
        String fileName = encodeToMD5(url);
        File file = new File(cache_dir, fileName);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()){
            parentFile.mkdirs();
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //获取应用程序版本号
    private int getAppVersion(Context context){
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    //获取缓存路径
    private File getDiskCacheDir(Context context, String uniquName){
        String cache_path;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                ! Environment.isExternalStorageRemovable()){
            //如果SD卡存在或不可移除,使用getExternalCacheDir()，获取到/sdcard/Android/data/<application package>/cache
            cache_path = context.getExternalCacheDir().getPath();
        }else {
            //否则调用getCacheDir，获取到/data/data/<application package>/cache
            cache_path = context.getCacheDir().getPath();
        }
        Log.e("cache_path", "getDiskCacheDir: "+ cache_path);
        //File.separator是文件路径分隔符
        return new File(cache_path + File.separator + uniquName);
    }

    //将URL进行MD5编码，作为缓存文件的文件名
    private String encodeToMD5(String key){
        String cache_key;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(key.getBytes());
            cache_key = bytesToHexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            cache_key = String.valueOf(key.hashCode());
        }
        return cache_key;
    }

    private String bytesToHexString(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1){
                sb.append("0");
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
