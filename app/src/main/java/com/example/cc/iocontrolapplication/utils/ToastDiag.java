package com.example.cc.iocontrolapplication.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.example.cc.iocontrolapplication.R;

/**
 * Created by cc on 2018/12/18.
 */

public class ToastDiag{
    public static void warnDiag(Context context, String message){
        AlertDialog alertDialog1 = new AlertDialog.Builder(context)
                .setTitle("提示")//标题
                .setMessage(message)//内容
                .setIcon(R.mipmap.ic_launcher)//图标
                .create();
        alertDialog1.show();
    }
}
