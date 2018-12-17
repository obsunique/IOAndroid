package com.example.cc.iocontrolapplication.usercenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cc.iocontrolapplication.FunctionService.FunctionServiceActivity;
import com.example.cc.iocontrolapplication.R;
import com.example.cc.iocontrolapplication.login.LoginActivity;
import com.example.cc.iocontrolapplication.utils.SharedPrefUtility;

/**
 * Created by cc on 2018/12/13.
 */

public class UserActivity extends Activity implements View.OnClickListener {

    private int userid;
    private ImageView userimageValue;
    private LinearLayout usernameView,useridcardView,userphonenumberView,useremailnumberView,userisAutoPayView,userFaceView,userOpenServiceView;
    private TextView usernameString,useridcardString,userphonenumberString,useremailnumberString,userisAutoPayString,userFaceString,userOpenServiceString;
    private TextView  usernameValue,useridcardValue,userphonenumberValue,useremailnumberValue,userisAutoPayValue,userFaceValue;
    private LinearLayout exitLogin;
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if(resultCode==1){
                    Log.e("----****-----",data.getStringExtra("editValue"));
                    usernameValue.setText(data.getStringExtra("editValue"));
                }
                break;
            default:
                break;
        }
    }
*/

    public void refreshData(){
        userid=(Integer) SharedPrefUtility.getParam(UserActivity.this, SharedPrefUtility.UserId, 0);
        if(userid==0){
            Toast.makeText(UserActivity.this,"请连接网络",Toast.LENGTH_LONG);
        }else{

        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        //refreshData();//刷新数据
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        initLayout();
        initLayoutListener();

    }
    public void initLayout(){

        userimageValue=(ImageView)findViewById(R.id.userimage_value);

        usernameView=(LinearLayout)findViewById(R.id.username_button);
        usernameString=(TextView)findViewById(R.id.username_string);
        usernameValue=(TextView)findViewById(R.id.username_value);

        useridcardView=(LinearLayout)findViewById(R.id.IdCard_button);
        useridcardString=(TextView)findViewById(R.id.IdCard_string);
        useridcardValue=(TextView)findViewById(R.id.IdCard_value);

        userphonenumberView=(LinearLayout)findViewById(R.id.userphonenumber_button);
        userphonenumberString=(TextView)findViewById(R.id.userphonenumber_string);
        userphonenumberValue=(TextView)findViewById(R.id.userphonenumber_value);

        useremailnumberView=(LinearLayout)findViewById(R.id.useremailnumber_button);
        useremailnumberString=(TextView)findViewById(R.id.useremailnumber_string);
        useremailnumberValue=(TextView)findViewById(R.id.useremailnumber_value);

        userisAutoPayView=(LinearLayout)findViewById(R.id.isAutoPay_button);
        userisAutoPayString=(TextView)findViewById(R.id.isAutoPay_string);
        userisAutoPayValue=(TextView)findViewById(R.id.isAutoPay_value);

        userFaceView=(LinearLayout)findViewById(R.id.Face_button);
        userFaceString=(TextView)findViewById(R.id.Face_string);
        userFaceValue=(TextView)findViewById(R.id.Face_value);

        userOpenServiceView=(LinearLayout)findViewById(R.id.OpenService);
        userOpenServiceString=(TextView)findViewById(R.id.Face_string);

        exitLogin=(LinearLayout)findViewById(R.id.exit_login);

    }
    public void initLayoutListener(){
        userimageValue.setOnClickListener(this);
        usernameView.setOnClickListener(this);
        useridcardView.setOnClickListener(this);
        userphonenumberView.setOnClickListener(this);
        useremailnumberView.setOnClickListener(this);
        userisAutoPayView.setOnClickListener(this);
        userFaceView.setOnClickListener(this);
        userOpenServiceView.setOnClickListener(this);
        exitLogin.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userimage_value:
                //打开图片
                break;
            case R.id.username_button:
                intoEditPage(1,"username",usernameValue.getText().toString());
                break;
            case R.id.IdCard_button:
                intoEditPage(2,"useridcard",useridcardValue.getText().toString());
                break;
            case R.id.userphonenumber_button:
                intoEditPage(3,"userphone",userphonenumberValue.getText().toString());
                break;
            case R.id.useremailnumber_button:
                intoEditPage(4,"useremail",useremailnumberValue.getText().toString());
                break;
            case R.id.isAutoPay_button:
                intoEditPage(5,"isautopay",userisAutoPayValue.getText().toString());
                break;
            case R.id.Face_button:
                //打开人脸识别
                break;
            case R.id.OpenService:
                openFunctionService();
                break;
            case R.id.exit_login:
                exitLogin();
                break;
            default:
                break;
        }
    }

    public void intoEditPage(int editId,String editString,String editValue){
        Intent intent=new Intent();
        intent.setClass(UserActivity.this,EditMessageActivity.class);
        intent.putExtra("userid",userid);
        intent.putExtra("editId",editId);
        intent.putExtra("editString",editString);
        intent.putExtra("editValue",editValue);
        startActivity(intent);
    }

    //打开图册选照片

    //打开人脸识别

    //打开服务
    public void openFunctionService(){
        Intent intent=new Intent();
        //服务应用界面
        intent.setClass(UserActivity.this, FunctionServiceActivity.class);
        startActivity(intent);
    }
    //退出登录
    public void exitLogin(){
        SharedPrefUtility.setParam(UserActivity.this, SharedPrefUtility.IS_LOGIN, false);
        SharedPrefUtility.removeParam(UserActivity.this, SharedPrefUtility.LOGIN_DATA);
        SharedPrefUtility.removeParam(UserActivity.this, SharedPrefUtility.UserId);
        Intent intent=new Intent();
        intent.setClass(UserActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
