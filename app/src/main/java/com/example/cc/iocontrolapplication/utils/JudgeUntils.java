package com.example.cc.iocontrolapplication.utils;

import android.util.Log;

/**
 * Created by cc on 2018/12/20.
 */

public class JudgeUntils {

    //判断手机
    public static boolean isPhoneNumber(String number){
        String telRegex = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(147,145))\\d{8}$";
        Log.e("------***------手机账号",""+number.matches(telRegex));
        return number.matches(telRegex);//是返回true
    }
    //判断邮件
    public static boolean isEmailValid(String email) {
        return email.contains("@");
    }
    //密码大于6位
    public static boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }
}
