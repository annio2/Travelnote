package com.example.travelnote.http;

import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wuguanglin on 2017/8/8.
 */
public class MyHttpTest {
    public interface HttpCallBack{
        void onSuccess(byte[] response);
        void onFailure(Exception e);
    }
    @Test
    public static void sendGetRequest(final String urlString, final MyHttp.HttpCallBack callBack){
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                HttpURLConnection connection = null;
                try {
                    url = new URL(urlString.trim());
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(8000);
                    if (200 == connection.getResponseCode()){
                        InputStream is = connection.getInputStream();
                        BufferedInputStream bis = new BufferedInputStream(is);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] bytes = new byte[1024];
                        int len = -1;
                        while ((len = bis.read(bytes)) != -1) {
                            baos.write(bytes, 0, len);
                        }
                        bis.close();
                        is.close();
                        byte[] response = baos.toByteArray();
                        if (callBack != null){
                            callBack.onSuccess(response);
                        }
                    }
                }catch (IOException e){
                    if (callBack != null){
                        callBack.onFailure(e);
                    }
                }finally {
                    if (connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}