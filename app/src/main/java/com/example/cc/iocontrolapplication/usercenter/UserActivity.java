package com.example.cc.iocontrolapplication.usercenter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cc.iocontrolapplication.FunctionService.FunctionServiceActivity;
import com.example.cc.iocontrolapplication.R;
import com.example.cc.iocontrolapplication.faceactivity.PreviewActivity;
import com.example.cc.iocontrolapplication.login.LoginActivity;
import com.example.cc.iocontrolapplication.utils.PayHttpUtils;
import com.example.cc.iocontrolapplication.utils.SharedPrefUtility;
import com.example.cc.iocontrolapplication.utils.ToastDiag;

import org.json.JSONObject;

/**
 * Created by cc on 2018/12/13.
 */

public class UserActivity extends Activity implements View.OnClickListener {

    private Integer userid=12;
    private String username,userphone,userrealname,useridcard,useremail;

    private LinearLayout userimageButton;
    private ImageView userimageValue;
    private LinearLayout usernameView,useridcardView,userphonenumberView,useremailnumberView,userisAutoPayView,userFaceView,userOpenServiceView;
    private TextView usernameString,useridcardString,userphonenumberString,useremailnumberString,userisAutoPayString,userFaceString,userOpenServiceString;
    private TextView  usernameValue,useridcardValue,userphonenumberValue,useremailnumberValue,userisAutoPayValue,userFaceValue;
    private LinearLayout exitLogin;

    private JSONObject reultJson;
    private PushTask pushTask;

    public void refreshData(){
        Integer userid=(Integer) SharedPrefUtility.getParam(UserActivity.this, SharedPrefUtility.UserId, 0);

        Log.e("-----userid----",userid+"");
        /*
        username=(String) SharedPrefUtility.getParam(UserActivity.this, SharedPrefUtility.UserName, "");
        userphone=(String) SharedPrefUtility.getParam(UserActivity.this, SharedPrefUtility.UserPhone, "");
        userrealname=(String) SharedPrefUtility.getParam(UserActivity.this, SharedPrefUtility.UserRealName, "");
        useridcard=(String) SharedPrefUtility.getParam(UserActivity.this, SharedPrefUtility.UserIdCard, "");
        useremail=(String) SharedPrefUtility.getParam(UserActivity.this, SharedPrefUtility.UserEmail, "");
*/
        Log.e("-----userphone----",userphone+"");
        if(this.userid<1){
            ToastDiag.Toast(UserActivity.this,"请连接网络");
        }else if(userphone==null){
            JSONObject json = new JSONObject();
            //手机密码登录
            String url="http://47.107.248.227:8080/android/User/checkUserPerfectMesaage";
            try {
                json.put("userid",userid);
            }catch (Exception e) {}
            pushTask = new PushTask(url,json);
            pushTask.execute((Void) null);
        }else{
            setData();
        }

    }
    /*@Override
    protected void onResume() {
        super.onResume();
        //refreshData();//刷新数据
    }
*/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        initLayout();
        initLayoutListener();
        refreshData();
    }

//导入数据到SP
    public void inDate(){
        try{
            JSONObject userperfectWithBLOBs=reultJson.getJSONObject("userperfectWithBLOBs");
            JSONObject user=reultJson.getJSONObject("user");
            Log.e("----******---eeee---",userperfectWithBLOBs.getString("userfaceimage").equals("null")+"");

            if(!user.getString("username").equals("null")) {
                SharedPrefUtility.setParam(UserActivity.this,SharedPrefUtility.UserName,user.getString("username"));
                usernameValue.setText(user.getString("username"));
            }

            if(userperfectWithBLOBs.getString("userrealname").equals("null")) {
                useridcardValue.setText("未实名");
            }else{
                SharedPrefUtility.setParam(UserActivity.this,SharedPrefUtility.UserRealName,userperfectWithBLOBs.getString("userrealname"));
                SharedPrefUtility.setParam(UserActivity.this,SharedPrefUtility.UserIdCard,userperfectWithBLOBs.getString("useridcard"));
                useridcardValue.setText("已实名");
            }

            if(!user.getString("userphone").equals("null")){
                SharedPrefUtility.setParam(UserActivity.this,SharedPrefUtility.UserPhone,user.getString("userphone"));
                userphonenumberValue.setText(user.getString("userphone"));
            }


            if(userperfectWithBLOBs.getString("useremail").equals("null"))
                useremailnumberValue.setText("前往设置");
            else {
                SharedPrefUtility.setParam(UserActivity.this,SharedPrefUtility.UserEmail,user.getString("useremail"));
                useremailnumberValue.setText(userperfectWithBLOBs.getString("useremail"));
            }
            if(userperfectWithBLOBs.getInt("isautopay")!=0)
                userisAutoPayValue.setText("已开启");

            if(userperfectWithBLOBs.getString("userfaceimage").equals("null"))
                userFaceValue.setText("未开启");
            else
                userFaceValue.setText("已开启");
        }catch (Exception e){
            Log.e("----******---eeee---",e.toString());
        }
    }
    //从SP导入数据
    public void setData(){
        usernameValue.setText(username);
        if(userrealname!=null)
            useridcardValue.setText("已实名");
        userphonenumberValue.setText(userphone);
        useremailnumberValue.setText(useremail);
    }

    public void initLayout(){

        userimageButton=(LinearLayout)findViewById(R.id.userimage_button);
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
        userimageButton.setOnClickListener(this);
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
        if(usernameValue.getText().toString().equals("正在加载")) {
            ToastDiag.Toast(UserActivity.this, "请连接网络");
            return;
        }
        Intent intent=new Intent();
        switch (v.getId()) {
            case R.id.userimage_value:
                intoImgPage(9,"userimg","");
                break;
            case R.id.userimage_button:
                getPicFromAlbm();
                break;
            case R.id.username_button:
                    intoEditPage(1,"username",usernameValue.getText().toString());
                break;
            case R.id.IdCard_button:
                if(useridcardValue.getText().toString().equals("未实名"))
                        intoEditPage(2,"userrealname",useridcardValue.getText().toString());
                if(useridcardValue.getText().toString().equals(""))
                    ToastDiag.Toast(UserActivity.this,"请连接网络");
                break;
            case R.id.userphonenumber_button:
                intoEditPage(3,"userphone",userphonenumberValue.getText().toString());
                break;
            case R.id.useremailnumber_button:
                intoEditPage(4,"useremail",useremailnumberValue.getText().toString());
                break;
            case R.id.isAutoPay_button:
                intent.setClass(UserActivity.this, AvatarChoose.class);
                startActivity(intent);
                break;
            case R.id.Face_button:
                //打开人脸识别
                if (userFaceValue.getText().toString().equals("已开启")) {
                    ToastDiag.Toast(UserActivity.this,"你已开启人脸注册");
                }else
                    if (useridcardValue.getText().toString().equals("已实名")) {
                        intent.setClass(UserActivity.this, PreviewActivity.class);
                        intent.putExtra("userid",userid.toString());
                        intent.putExtra("username",usernameValue.getText().toString());
                        startActivity(intent);
                    } else {
                        ToastDiag.Toast(UserActivity.this, "请前往实名认证");
                    }

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
    public void intoImgPage(int editId,String editString,String editValue){
        Intent intent=new Intent();
        intent.setClass(UserActivity.this,AvatarChoose.class);
        intent.putExtra("userid",userid);
        intent.putExtra("editId",editId);
        intent.putExtra("editString",editString);
        intent.putExtra("editValue",editValue);
        startActivity(intent);
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
        SharedPrefUtility.removeParam(UserActivity.this, SharedPrefUtility.UserName);
        SharedPrefUtility.removeParam(UserActivity.this, SharedPrefUtility.UserPhone);
        SharedPrefUtility.removeParam(UserActivity.this, SharedPrefUtility.UserRealName);
        SharedPrefUtility.removeParam(UserActivity.this, SharedPrefUtility.UserIdCard);
        SharedPrefUtility.removeParam(UserActivity.this, SharedPrefUtility.UserEmail);
        Intent intent=new Intent();
        intent.setClass(UserActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void setReultJson(JSONObject reultJson){
        this.reultJson=reultJson;
    }
    public class PushTask extends AsyncTask<Void, Void, Boolean> {
        private final JSONObject json;
        private final String url;
        PushTask(String url,JSONObject json) {
            this.json = json;
            this.url=url;
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }
            //String users="[{\"username\":"+mNumberView.getText().toString()+",\"password\":"+mPasswordView.getText().toString()+"}]";
            Log.e("-------***---json---",json.toString());
            PayHttpUtils httpUtils = new PayHttpUtils();
            JSONObject result = httpUtils.post(url,json.toString(),null,null);
            Log.e("-------***1---result---",result+"");
            if(result!=null){


                try {
                    JSONObject mJsonArray=result.getJSONObject("userperfectWithBLOBs");
                    Log.e("-------***3---result---", mJsonArray.getString("userid"));
                    setReultJson(result);
                    return true;
                }catch (Exception e){
                    Log.e("----******---eeee---",e.toString());
                }

            }
            // TODO: register the new account here.
            return false;
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            pushTask = null;
            if (success) {
                inDate();
            } else {
                ToastDiag.Toast(UserActivity.this,"系统错误");
                return;
            }
        }
        @Override
        protected void onCancelled() {
            pushTask = null;
        }
    }

    //相册请求码
    private static final int ALBUM_REQUEST_CODE = 1;
    //剪裁请求码
    private static final int CROP_REQUEST_CODE = 3;
    /**
     * 从相册获取图片
     */
    private void getPicFromAlbm() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        Log.e("----*-1----","我来了");
        getParent().startActivityForResult(photoPickerIntent, ALBUM_REQUEST_CODE);

        Log.e("----*0----","我来了");
    }

    /**
     * 裁剪图片
     */
    private void cropPhoto(Uri uri) {
        Log.e("----*3----", "我来了");
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        getParent().startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.e("----*1----",requestCode+"我来了");
        switch (requestCode) {
            case ALBUM_REQUEST_CODE:    //调用相册后返回
                Log.e("----*1----",resultCode+"我来了"+RESULT_OK);
                if (resultCode == RESULT_OK) {
                    Uri uri = intent.getData();
                    Log.e("----*----",uri+"");
                    cropPhoto(uri);
                }
                break;
            case CROP_REQUEST_CODE:     //调用剪裁后返回
                Log.e("----*2----","我来了");
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    //在这里获得了剪裁后的Bitmap对象，可以用于上传
                    Bitmap image = bundle.getParcelable("data");
                    //设置到ImageView上
                    userimageValue.setImageBitmap(image);
                    //也可以进行一些保存、压缩等操作后上传
                    // String path = saveImage("crop", image);
                }
                break;
            case 4:
                refreshData();
                break;
        }
    }

}
