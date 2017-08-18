package com.example.travelnote.utils;

import com.example.travelnote.jsonbean.Response;
import com.example.travelnote.jsonbean.TravelNote;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuguanglin on 2017/8/11.
 */

public class JsonUtils {

    /*从服务器获取json字符串*/
    public static String getJsonString(String urlString){
        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            if (200 == connection.getResponseCode()){
                InputStream is = connection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while (-1 != (len = is.read(buffer))){
                    baos.write(buffer, 0, len);
                    baos.flush();
                }
                return baos.toString();
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

    //将json字符串解析为Response对象的集合
    public static List<Response> getTravelResponse(String urlString){
        List<Response> list;
        String jsonString = getJsonString(urlString);
        Gson gson = new Gson();
        TravelNote travelNote = gson.fromJson(jsonString, TravelNote.class);
        if (travelNote != null){
            int result = travelNote.getResult();
            if (result == 0){
                list = travelNote.getResponse();
                return list;
            }
        }
        return null;
    }
}
