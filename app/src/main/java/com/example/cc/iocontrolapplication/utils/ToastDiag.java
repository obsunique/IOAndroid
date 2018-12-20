package com.example.cc.iocontrolapplication.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cc.iocontrolapplication.R;

/**
 * Created by cc on 2018/12/18.
 */

public class ToastDiag{
    public static void warnDiag(final Context context, String message){
        AlertDialog.Builder alert = new AlertDialog.Builder(context)
                .setTitle("提示")//标题
                .setMessage(message)//内容
                .setIcon(R.mipmap.logo)//图标
                .setPositiveButton("entar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton("cencle", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        alert.show();
    }
    public static void warnMsgDialog(Context context, String message){
        AlertDialog.Builder alert = new AlertDialog.Builder(context)
                .setTitle("提示")
                .setIcon(R.mipmap.logo);
        TextView msg = new TextView(context);
        msg.setText(message);
        msg.setPadding(10, 10, 10, 10);
        msg.setGravity(Gravity.CENTER);
        msg.setTextSize(18);
        alert.setView(msg);
        alert.show();

    }

    public static void Toast(Context context, String message){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
