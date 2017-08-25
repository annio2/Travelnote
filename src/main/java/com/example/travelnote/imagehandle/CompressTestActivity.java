package com.example.travelnote.imagehandle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import com.example.travelnote.R;
import com.orhanobut.logger.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class CompressTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compress_test);
        ImageView imageView = (ImageView) findViewById(R.id.compressTest);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.compress_test);
        int width = bitmap.getWidth();//单位：px
        int height = bitmap.getHeight();
        Log.e("image原宽度", "onCreate~~~~~~~~: " + width);
        Log.e("image原高度", "onCreate~~~~~~~~: " + height);
        Log.e("image原字节数", "onCreate~~~~~~~~: " + bitmap.getByteCount());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 90;
        while (baos.toByteArray().length/1024 > 100){
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
            if (options < 0){
                options = 90;
            }
        }
        ByteArrayInputStream is = new ByteArrayInputStream(baos.toByteArray());
        Log.e("将baos转为bitmap的大小", "onCreate: ~~~~~~" + BitmapFactory.decodeStream(is).getByteCount());
        Log.e("baos长度", "onCreate: ~~~~~~" + baos.toByteArray().length / 1024);
        BitmapCompress bitmapCompress = new BitmapCompress();
        Bitmap afterCompress = bitmapCompress.getSmallBitmap(imageView, baos.toByteArray());

        imageView.setImageBitmap(afterCompress);
        Log.e("image压缩后宽度", "onCreate: ~~~~~~" + afterCompress.getWidth());
        Log.e("image压缩后高度", "onCreate: ~~~~~~" + afterCompress.getHeight());
        Log.e("image压缩后字节数", "onCreate: ~~~~~~" + afterCompress.getByteCount());
    }
}
