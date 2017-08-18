package com.example.travelnote.http;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MyHttp{

    public interface HttpCallBack{
        void onSuccess(byte[] response);
        void onFailure(Exception e);
    }
    /*使用HttpUrlConnection发送GET请求*/
    public static void sendGetRequest(final String urlString, final RequestBody requestBody, final HttpCallBack callBack){
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                String newurl;
                HttpURLConnection connection = null;
                String pageNo = requestBody.getPageNo();
                String orderBy = requestBody.getOrderType();
                Log.e("hahaha", "run: "+ pageNo + orderBy);
                if (!TextUtils.isEmpty(pageNo) && !TextUtils.isEmpty(orderBy)){
                    newurl = urlString + "?pageNo=" + pageNo + "&orderType=" + orderBy;
                }else if (TextUtils.isEmpty(pageNo) && !TextUtils.isEmpty(orderBy)){
                    newurl = urlString + "&orderType=" + orderBy;
                }else if (TextUtils.isEmpty(orderBy) && !TextUtils.isEmpty(pageNo)){
                    newurl = urlString + "?pageNo=" + pageNo;
                }else {
                    newurl = urlString;
                }
                try {
                    url = new URL(newurl);
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
                } catch (MalformedURLException e) {
                    if (callBack != null) {
                        callBack.onFailure(e);
                    }
                    e.printStackTrace();
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
    /*使用HttpUrlConnection发送POST请求*/
    public static void sendRequestPost(final String urlString, final RequestBody requestBody, final HttpCallBack callBack){
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                HttpURLConnection connection = null;
                try {
                    url = new URL(urlString.trim());
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(8000);
                    //设置运行时输入输出
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    String reqBody = "pageNo=" + requestBody.getPageNo() + "&orderBy="+requestBody.getOrderType();
                    OutputStream os = connection.getOutputStream();
                    BufferedOutputStream bos = new BufferedOutputStream(os);
                    bos.write(reqBody.getBytes());
                    bos.flush();
                    bos.close();
                    os.close();
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
                } catch (MalformedURLException e) {
                    if (callBack != null){
                        callBack.onFailure(e);
                    }
                    e.printStackTrace();
                } catch (IOException e) {
                    if (callBack != null){
                        callBack.onFailure(e);
                    }
                    e.printStackTrace();
                }finally {
                    if (connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public static void sendRequestGet(String urlString){

    }
}