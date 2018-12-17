package com.example.cc.iocontrolapplication.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.cc.iocontrolapplication.login.LoginActivity;
import com.example.cc.iocontrolapplication.utils.SharedPrefUtility;

/**
 * Created by cc on 2018/12/16.
 */

public class LoadActivity extends Activity {

    private boolean isLogin;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLogin=(Boolean) SharedPrefUtility.getParam(LoadActivity.this, SharedPrefUtility.IS_LOGIN, false);
        Intent intent=new Intent();
        if(isLogin){
            intent.setClass(LoadActivity.this,IOIndex.class);
        }else
            intent.setClass(LoadActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
