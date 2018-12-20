package com.example.cc.iocontrolapplication.utils;



import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PayHttpUtils {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    static OkHttpClient client = new OkHttpClient();

    public static JSONObject post(String url, String json, String sign, String sn) {//post请求，返回String类型
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .addHeader("Content-Type","application/json")//添加头部
                //.addHeader("Authorization",sn + " " + sign)
                .url(url)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            JSONObject obj = new JSONObject(response.body().string());
            return obj;
        }catch(Exception e){
            return null;
        }
    }

    public static JSONObject tongpost(String url, String json, String sign, String sn) {//post请求，返回String类型
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .addHeader("Content-Type","application/json")//添加头部
                //.addHeader("Authorization",sn + " " + sign)
                .url(url)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            JSONObject obj = new JSONObject(response.body().string());
            return obj;
        }catch(Exception e){
            return null;
        }
    }

    public static JSONObject get(String neturl){
        Request request=new Request.Builder()
                .addHeader("Content-Type","application/x-www-form-urlencoded")//添加头部
                .url(neturl)
                .build();
        try {
            Response response = client.newCall(request).execute();
            JSONObject obj = new JSONObject(response.body().string());
            return obj;
        }catch(Exception e){
            return null;
        }
    }
}