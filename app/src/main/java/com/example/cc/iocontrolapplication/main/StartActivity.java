package com.example.cc.iocontrolapplication.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.example.cc.iocontrolapplication.R;
import com.example.cc.iocontrolapplication.login.LoginActivity;
import com.example.cc.iocontrolapplication.login.RegisterActivity;

/**
 * Created by cc on 2018/12/19.
 */

public class StartActivity extends Activity {


    private Button Register,Login;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_start);

        Register=(Button)findViewById(R.id.gotoRegister);
        Login=(Button)findViewById(R.id.gotoLogin);

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(StartActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(StartActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}