package com.example.cc.iocontrolapplication.faceutil;

import android.util.Log;

import com.example.cc.iocontrolapplication.utils.PayHttpUtils;

import org.json.JSONObject;

/**
 * Created by Administrator on 2018/12/19.
 */

public class AipfaceUtil {
    public final String baseurl = "http://47.107.248.227:8080/Aipface/";
    public JSONObject detect(String image)
    {
        JSONObject json = new JSONObject();
        try {
            json.put("imagein", image);
        }catch (Exception e){}

        JSONObject res = PayHttpUtils.post("http://47.107.248.227:8080/Aipface/detect",json.toString(),null,null);
        return res;
    }
    public JSONObject addUser(String image,String group,String user,String userinfo)
    {
        String baseimage64 = "{\"imagein\":" + image + "}";
        String grouptemp = "{\"group\":" + group + "}";
        String usertemp = "{\"user\":" + user + "}";
        String userinfotemp = "{\"userinfo\":" + userinfo + "}";
        String json = baseimage64+grouptemp+usertemp+userinfotemp;
        return PayHttpUtils.post(baseurl+"addUser",json,null,null);
    }
    public JSONObject search(String image,String group)
    {
        String baseimage64 = "{\"imagein\":" + image + "}";
        String grouptemp = "{\"group\":" + group + "}";
        String json = baseimage64+grouptemp;
        return PayHttpUtils.post(baseurl+"search",json,null,null);
    }
    public JSONObject userCopy(String fromgroup,String togroup,String user)
    {
        String fromgrouptemp = "{\"fromgroup\":" + fromgroup + "}";
        String togrouptemp = "{\"togroup\":" + togroup + "}";
        String usertemp = "{\"user\":" + user + "}";
        String json = fromgrouptemp+togrouptemp+usertemp;
        return PayHttpUtils.post(baseurl+"userCopy",json,null,null);
    }
    public JSONObject groupAdd(String group)
    {
        String grouptemp = "{\"group\":" + group + "}";
        return PayHttpUtils.post(baseurl+"groupAdd",grouptemp,null,null);
    }
    public JSONObject groupDelect(String group)
    {
        String grouptemp = "{\"group\":" + group + "}";
        return PayHttpUtils.post(baseurl+"groupDelect",grouptemp,null,null);
    }
}
