package com.example.cc.iocontrolapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by cc on 2018/12/16.
 */

public class SharedPrefUtility {
    private static final String FILE_NAME = "cc_date";

    public static final String INDEX="index";
    public static final String LOGIN_DATA="loginData";
    public static final String IS_LOGIN="IS_LOGIN";
    public static final String UserId="UserId";
    public static final String UserAvatar="UserAvatar";
    public static final String UserName="UserName";
    public static final String UserPhone="UserPhone";
    public static final String UserRealName="UserRealName";
    public static final String UserIdCard="UserIdCard";
    public static final String UserEmail="UserEmail";

    public static final String UserFace="UserFace";
    private static SharedPreferences.Editor editor;
    private static SharedPreferences sp;
    /**
     * save data into FILE_NAME ,this path is data/data/POCKET_NAME/shared_prefs
     */
    public static void oncreate(Context context) {
        sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();

    }
        public static void setParam(String key, Object object){
            String type = object.getClass().getSimpleName();
        if("String".equals(type)){
            editor.putString(key, (String)object);
        }
        else if("Integer".equals(type)||"int".equals(type)){
            editor.putInt(key, (Integer)object);
        }
        else if("Boolean".equals(type)){
            editor.putBoolean(key, (boolean)object);
        }
        else if("Float".equals(type)){
            editor.putFloat(key, (Float)object);
        }
        else if("Long".equals(type)){
            editor.putLong(key, (Long)object);
        }

        editor.apply();
    }


    public static Object getParam(Context context , String key, Object defaultObject){
        String type = defaultObject.getClass().getSimpleName();

        if("String".equals(type)){
            return sp.getString(key, (String)defaultObject);
        }
        else if("Integer".equals(type)||"int".equals(type)){
            return sp.getInt(key, (Integer)defaultObject);
        }
        else if("Boolean".equals(type)){
            return sp.getBoolean(key, (boolean)defaultObject);
        }
        else if("Float".equals(type)){
            return sp.getFloat(key, (Float)defaultObject);
        }
        else if("Long".equals(type)){
            return sp.getLong(key, (Long)defaultObject);
        }

        return null;
    }
    /*
    public static void removeParam(Context context,String key){

        editor.remove(key);
        editor.apply();
    }*/
    public static void removeAllParam(){

        setParam(SharedPrefUtility.IS_LOGIN, (String)"false");
        editor.remove(SharedPrefUtility.LOGIN_DATA);
        editor.apply();
        editor.remove(SharedPrefUtility.UserName);
        editor.apply();
        editor.remove(SharedPrefUtility.UserPhone);
        editor.apply();
        editor.remove(SharedPrefUtility.UserRealName);
        editor.apply();
        editor.remove(SharedPrefUtility.UserIdCard);
        editor.apply();
        editor.remove(SharedPrefUtility.UserEmail);
        editor.apply();
        editor.remove(SharedPrefUtility.UserFace);
        editor.apply();
    }
}
