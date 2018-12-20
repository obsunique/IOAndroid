package com.example.cc.iocontrolapplication.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cc on 2018/12/18.
 */

public class ImgUtil {
    //转码
    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    //上传
    public static void upload(final Context context, int userid, String userphone,final Bitmap image){
        final JSONObject json=new JSONObject();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");//获取当前时间
            Date date = new Date(System.currentTimeMillis());
            String adate=simpleDateFormat.format(date);
            final String path="/root/Turning/avatar/"+userphone+""+adate+".jpeg";
            String img=ImgUtil.bitmapToBase64(image);
        try{
            json.put("userid",userid);
            json.put("imgStr",img);//图片
            json.put("src","/root/Turning/avatar/");//路径
            json.put("imgname",userphone+""+adate+".jpeg");//名

        }catch (Exception e){
            ToastDiag.Toast(context,"系统错误");
        }
        //也可以进行一些保存、压缩等操作后上传
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject result = PayHttpUtils.post("http://47.107.248.227:8080/android/uploadPicture", json.toString(), null, null);
                if (result != null) {
                    try {
                        if (Integer.parseInt(result.getString("flag")) > 0) {
                            ImgUtil.savaImg(path, image);
                        }else {
                            ToastDiag.Toast(context, "保存失败");
                        }
                    } catch (Exception e) {
                        ToastDiag.Toast(context, "网络错误");
                    }
                }else {
                    ToastDiag.Toast(context, "网络错误");
                }
            }
        });
    }


    //图片保存到本地
    public static void savaImg(String path,Bitmap bitmap){
        File file=new File("/sdcard/",path+"");
        try {
            FileOutputStream out=new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,90,out);
            out.flush();
            out.close();
        }catch (Exception e){}

    }

    public static Bitmap readImg(String path){
        Bitmap bitmap=null;
        try {
            File file=new File(path);
            if (file.exists()){
                bitmap= BitmapFactory.decodeFile(path);
            }
        }catch (Exception e){}
        return bitmap;
    }
}
