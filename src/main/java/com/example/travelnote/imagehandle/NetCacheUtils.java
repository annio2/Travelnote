package com.example.travelnote.imagehandle;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.travelnote.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by wuguanglin on 2017/8/9.
 */

public class NetCacheUtils {
    private static final String TAG = "NetCacheUtils";
    private MemoryCacheUtils memoryCache;
    private BitmapCompress bitmapCompress;
    private LocalCacheUtils localCacheUtils;
    private Set<DownloadImageTask> tasks;
    private Context context;

    public NetCacheUtils(MemoryCacheUtils memoryCacheUtils, LocalCacheUtils localCacheUtils){
        this.memoryCache = memoryCacheUtils;
        this.localCacheUtils = localCacheUtils;
        bitmapCompress = new BitmapCompress();
        tasks = new HashSet<>();
    }
    //从网络直接下载图片并显示在ImageView，先判断当前ImageView是否还有未完成的任务
    public void displayImageFromNet(Context context, String url, ImageView imageView){
        if (cancleDownLoadTask(url, imageView)){
            DownloadImageTask task = new DownloadImageTask(imageView);
            AsyncDrawable asyncDrawable = new AsyncDrawable(context.getResources(), task);
            imageView.setImageDrawable(asyncDrawable);  //为ImageView设置一个AsyncDrawable，方便获取DownloadImageTask
            tasks.add(task);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        }
    }

    class DownloadImageTask extends AsyncTask<String, Void, Bitmap>{
        private String urlString;
        private ImageView imageView;
        private WeakReference<ImageView> imageViewWeakReference; //持有ImageView的弱引用
        public DownloadImageTask(ImageView imageView){
            this.imageView = imageView;
            imageViewWeakReference = new WeakReference<>(imageView);
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            while (true){
                if (isCancelled()){
                    break;
                }
                urlString = params[0];
                byte[] result = download(urlString);
                if (result != null){
                    Bitmap bitmap = bitmapCompress.getSmallBitmap(result);
                    if (bitmap != null){
                        memoryCache.setBitmap(urlString, bitmap);
                        localCacheUtils.addToLocalCache(urlString, bitmap);
                        return bitmap;
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView imageView = getAttachImageView();
            if (imageView != null){
                if (bitmap != null && !bitmap.isRecycled()){
                    imageView.setImageBitmap(bitmap);
                }else {
                    imageView.setImageResource(R.mipmap.default_cirmg);
                }
            }
        }

        //获取当前downloadImageTask关联的ImageView
        private ImageView getAttachImageView(){
            ImageView imageView = imageViewWeakReference.get();
            DownloadImageTask task = getBitmapDownLoadTask(imageView);
            if (this == task){
                return imageView;
            }
            return null;
        }
    }

    private byte[] download(String urlString){
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);//设置连接超时
            connection.setReadTimeout(8000);//设置读取超时
            if (200 == connection.getResponseCode()){
                InputStream is = connection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while (-1 != (len = is.read(buffer))){
                    baos.write(buffer, 0, len);
                    baos.flush();
                }
                return baos.toByteArray();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (connection != null){
                connection.disconnect();
            }
        }
        return null;
    }

    //先获取imageView的AsyncDrawable，再通过AsyncDrawable获取对应的downloadImageTask
    private DownloadImageTask getBitmapDownLoadTask(ImageView imageView){
        if (imageView != null){
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable){
                AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapDownLoadTask();
            }
        }
        return null;
    }


    //判断当前ImageView若存在其他的DownloadImageTask，就将其取消并返回true，否则false
    private boolean cancleDownLoadTask(String url, ImageView imageView){
        DownloadImageTask task = getBitmapDownLoadTask(imageView);
        if (task != null){
            String urlString = task.urlString;
            if (urlString == null || url.equals(urlString)){
                task.cancel(true);
            }else {
                return false;
            }
        }
        return true;
    }

    public void cancelAllTasks(){
        if (tasks != null){
            for (DownloadImageTask task : tasks){
                if (task != null && task.getStatus() == AsyncTask.Status.RUNNING){
                    task.cancel(true);
                }
            }
        }
    }

    /*自定义Drawable，持有downloadImageTask的弱引用，ImageView通过该类获取对应的task*/
    class AsyncDrawable extends BitmapDrawable{
        private WeakReference<DownloadImageTask> taskWeakReference;

        private AsyncDrawable(Resources rs, DownloadImageTask task){
            taskWeakReference = new WeakReference<>(task);
        }

        private DownloadImageTask getBitmapDownLoadTask(){
            return taskWeakReference.get();
        }
    }
}
