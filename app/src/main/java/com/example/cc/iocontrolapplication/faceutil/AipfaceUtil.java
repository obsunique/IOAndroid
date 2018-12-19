package com.example.cc.iocontrolapplication.faceutil;

import com.example.cc.iocontrolapplication.utils.PayHttpUtils;

import org.json.JSONObject;

/**
 * Created by Administrator on 2018/12/19.
 */

public class AipfaceUtil {
    public final String baseurl = "http://47.107.248.227/Aipface/";
    public JSONObject detect(String image)
    {
        String baseimage64 = "{\"imagein\":" + image + "}";
        return PayHttpUtils.post(baseurl+"detect",baseimage64,null,null);
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
