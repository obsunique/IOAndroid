package com.example.cc.iocontrolapplication.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.cc.iocontrolapplication.utils.SharedPrefUtility;

/**
 * Created by cc on 2018/12/16.
 */

public class LoadActivity extends Activity {

    private String isLogin;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPrefUtility.oncreate(LoadActivity.this);
        try {
            isLogin = (String) SharedPrefUtility.getParam(LoadActivity.this, SharedPrefUtility.IS_LOGIN, (String) "false");
            Log.e("----****----",isLogin);
        }catch (Exception e){
            isLogin="false";
        }
        Log.e("----****----",isLogin);
        Intent intent=new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        if(isLogin.equals("true")){
            intent.setClass(LoadActivity.this,IOIndex.class);
        }else{
            SharedPrefUtility.removeAllParam();
            intent.setClass(LoadActivity.this, StartActivity.class);
        }

        startActivity(intent);
    }
}
