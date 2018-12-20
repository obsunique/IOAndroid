package com.example.cc.iocontrolapplication.usercenter;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cc.iocontrolapplication.R;
import com.example.cc.iocontrolapplication.utils.JudgeUntils;
import com.example.cc.iocontrolapplication.utils.PayHttpUtils;
import com.example.cc.iocontrolapplication.utils.SharedPrefUtility;
import com.example.cc.iocontrolapplication.utils.ToastDiag;

import org.json.JSONObject;

/**
 * Created by cc on 2018/12/14.
 */

public class EditMessageActivity extends AppCompatActivity implements View.OnClickListener,TextView.OnEditorActionListener {

    private EditText editText,editTextSecond,editCodeValue;
    private LinearLayout editTextView,editTextSecondView,editCodeView;
    private Button editButton;

    private View mProgressView;
    private View meditFormView;
    private PushTask pushTask=null;

    private String jsonname;
    private int userid;
    private String editValue;
    private int editId=0;
    private ActionBar actionBar;

    private ImageView backimageView;
    private TextView titleText;
    private TextView serveText;

    private String sendcode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_edit_message);
        setCustomActionBar();
/*
        actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.show();
*/
        initData();
        initLayout();
        initLayoutListener();

    }

    private void setCustomActionBar() {
        ActionBar.LayoutParams lp =new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        View mActionBarView = LayoutInflater.from(this).inflate(R.layout.actionbar_edit, null);
        actionBar = getSupportActionBar();
        actionBar.setCustomView(mActionBarView, lp);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
    }
    public void initData(){
        Intent intent=getIntent();
        userid=intent.getIntExtra("userid",-1);
        editId=intent.getIntExtra("editId",0);
        jsonname=intent.getStringExtra("editString");
        editValue=intent.getStringExtra("editValue");
    }
    public void initLayout(){
        if(editId<0||userid<0){
            ToastDiag.Toast(EditMessageActivity.this,"网络连接错误");
            return;
        }else {
            backimageView=(ImageView) findViewById(R.id.actionbar_edit_button);//返回
            titleText=(TextView) findViewById(R.id.actionbar_edit_title);//标题
            serveText=(TextView) findViewById(R.id.actionbar_edit_serve);//保存，下一步
            mProgressView=(View)findViewById(R.id.edit_progress);//加载
            meditFormView=(View)findViewById(R.id.edit_form);//表格

            editTextView=(LinearLayout)findViewById(R.id.edit_message_view);//第一列栏
            editText = (EditText) findViewById(R.id.edit_message);//第一列输入

            editCodeView=(LinearLayout)findViewById(R.id.edit_message_code_view);//验证码栏
            editCodeValue=(EditText)findViewById(R.id.edit_message_code_value);//验证码输入栏
            editButton=(Button)findViewById(R.id.edit_message_code_send);//发送验证码

            editTextSecondView=(LinearLayout)findViewById(R.id.edit_message_second_view);//第二列栏
            editTextSecond=(EditText) findViewById(R.id.edit_message_second);//第二列输入



            switch (editId){
                case 1:
                    titleText.setText("更改昵称");
                    editText.setHint("昵称");
                    break;
                case 2:
                    titleText.setText("身份认证");
                    editValue="";
                    idcardEvent();
                    break;
                case 3:
                    titleText.setText("更改手机号码");
                    phoneEvent();
                    break;
                case 4:
                    if(editValue.equals("前往设置")){
                        editValue="";
                    }
                    titleText.setText("注册邮箱");;
                    emailEvent();
                    break;
                default:break;
            }
            Log.e("-----*******----",editValue+"");
            editText.setText(editValue);
        }
    }
    public void initLayoutListener(){
        backimageView.setOnClickListener(this);
        serveText.setOnClickListener(this);
        editText.setOnEditorActionListener(this);
        editButton.setOnClickListener(this);
        editTextSecond.setOnEditorActionListener(this);
        editCodeValue.setOnEditorActionListener(this);

        initTouch();
    }
    public void initTouch(){
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(null != EditMessageActivity.this.getCurrentFocus()){
                    InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    return mInputMethodManager.hideSoftInputFromWindow(EditMessageActivity.this.getCurrentFocus().getWindowToken(), 0);
                }
                return false;
            }
        });
        editCodeValue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(null != EditMessageActivity.this.getCurrentFocus()){
                    InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    return mInputMethodManager.hideSoftInputFromWindow(EditMessageActivity.this.getCurrentFocus().getWindowToken(), 0);
                }
                return false;
            }
        });
        editTextSecond.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(null != EditMessageActivity.this.getCurrentFocus()){
                    InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    return mInputMethodManager.hideSoftInputFromWindow(EditMessageActivity.this.getCurrentFocus().getWindowToken(), 0);
                }
                return false;
            }
        });
    }
    public String checkUrl(int editId){
        String url="";
        switch (editId){
            case 1:
                url="http://47.107.248.227:8080/android/User/updataUserName";
                break;
            case 2:
                url="http://47.107.248.227:8080/android/User/updataUserPerfectMesaage";
                break;
            case 3:
                url="http://47.107.248.227:8080/android/User/updataUserPhone";
                break;
            case 4:
                url="http://47.107.248.227:8080/android/User/updataUserPerfectMesaage";
                break;
            default:
                break;
        }
        return url;
    }


    public void idcardEvent(){
        editText.setHint("真实姓名");
        editTextSecond.setHint("身份证");
        editTextSecondView.setVisibility(View.VISIBLE);
    }

    public void phoneEvent(){
        setEditTextEnable(editText,false);
        editText.setHint("手机号码");
        editCodeView.setVisibility(View.VISIBLE);
        serveText.setText("下一步");
    }
    public void phoneNewEvent(){
        editTextView.setVisibility(View.GONE);
        editCodeView.setVisibility(View.GONE);
        serveText.setText("保存");
        editTextSecondView.setVisibility(View.VISIBLE);
        editTextSecond.setHint("新手机号码");
    }
    public void emailEvent(){
        setEditTextEnable(editText,false);
        editText.setHint("邮箱:");
        editCodeView.setVisibility(View.VISIBLE);
        serveText.setText("下一步");
    }
    public void emailNewEvent(){
        editTextView.setVisibility(View.GONE);
        editCodeView.setVisibility(View.GONE);
        serveText.setText("保存");
        editTextSecondView.setVisibility(View.VISIBLE);
        editTextSecond.setHint("新邮箱号码");
    }
    //事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.actionbar_edit_button:
                onBackPressed();
                break;
            case R.id.actionbar_edit_serve:
                if (serveText.getText().toString().equals("保存"))
                    if((editId==1&&isContinue(editText))||(editId!=1&&isContinue(editTextSecond)))
                        push();
                    else if(serveText.getText().toString().equals("下一步")){
                        if(editId==3){
                            if(isContinue(editText)) {
                                if (isCode()) {
                                    phoneNewEvent();
                                }
                            }
                        } else if(editId==4)
                            if (JudgeUntils.isEmailValid(editTextSecond.getText().toString())){
                            if (isCode()) {
                                emailNewEvent();
                            }
                        }else{
                                View view=null;
                                editTextSecond.setError("邮箱格式错误");
                                view=editTextSecond;
                                view.requestFocus();
                            }
                    }
                break;
            case R.id.edit_message_code_send:
                send();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int id, KeyEvent event) {
        switch (v.getId()) {
            case R.id.edit_message:
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    return true;
                }
                return false;
            default:
                return false;
        }

    }

private void setEditTextEnable(EditText editText,boolean mode){
        editText.setFocusable(mode);
        editText.setFocusableInTouchMode(mode);
        editText.setLongClickable(mode);
        editText.setInputType(mode? InputType.TYPE_CLASS_TEXT:InputType.TYPE_NULL);
}


    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        Log.e("----***---","我返回了");
        setResult(4,intent);
        finish();
    }
    // 判断手机号码栏
    public boolean isContinue(EditText editText){
        String number=editText.getText().toString();
        View view=null;
        if(TextUtils.isEmpty(number)){
            editText.setError("请不要为空");
            view=editText;
            view.requestFocus();
        }else if (editId==3&&!JudgeUntils.isPhoneNumber(number)){
            editText.setError("请注意手机号码格式");
            view=editText;
            view.requestFocus();
        }else
            return true;
        return false;
    }
    //判断验证码栏
    public boolean isCode(){
        String code=editCodeValue.getText().toString();
        View view=null;
        if (TextUtils.isEmpty(sendcode)){
            editCodeValue.setError("请获取验证码");
            view=editCodeValue;
            view.requestFocus();
        }if (TextUtils.isEmpty(code)){
            editCodeValue.setError("请输入验证码");
            view=editCodeValue;
            view.requestFocus();
        }if (!code.equals(sendcode)){
            editCodeValue.setError("验证码不相等");
            view=editCodeValue;
            view.requestFocus();
        }else
            return true;
        return false;
    }


    //发送验证码
    public void send() {
        if (pushTask != null) {
            return;
        }
        String editOne = editText.getText().toString();
        if ((editId == 3 && isContinue(editText) || (editId == 4 && JudgeUntils.isEmailValid(editOne)))) {
            JSONObject json = new JSONObject();
            //手机密码登录
            String url = "";
            if (editId == 2)
                url = "http://47.107.248.227:8080/android/Login/sendCode";
            try {
                json.put(jsonname, editOne);
            } catch (Exception e) {
            }

            pushTask = new PushTask(url, json);
            pushTask.execute((Void) null);
        }
    }
    //提交
    public void push(){
        if (pushTask != null) {
            return;
        }
        String editOne=editText.getText().toString();
        String editsecond=editTextSecond.getText().toString();
        JSONObject json = new JSONObject();

        String url=checkUrl(editId);
        try {
            json.put("userid",userid);
            if(editId==1)
                json.put(jsonname,editOne);
            else if (editId==2) {
                json.put(jsonname,editOne);
                json.put("useridcard", editsecond);
            }else
                json.put(jsonname,editsecond);
        }catch (Exception e) {}

        pushTask = new PushTask(url,json);
        pushTask.execute((Void) null);

    }

    public void setcode(String sendcode){
        this.sendcode=sendcode;
    }

    //设置缓存
    public void setSP(int editId){
        String editOne=editText.getText().toString();
        String editsecond=editTextSecond.getText().toString();
        switch (editId){
            case 1:
                SharedPrefUtility.setParam(SharedPrefUtility.UserName,editOne);
                break;
            case 2:
                SharedPrefUtility.setParam(SharedPrefUtility.UserRealName,editOne);
                SharedPrefUtility.setParam(SharedPrefUtility.UserIdCard,editsecond);
                break;
            case 3:
                SharedPrefUtility.setParam(SharedPrefUtility.UserPhone,editsecond);
                break;
            case 4:
                SharedPrefUtility.setParam(SharedPrefUtility.UserEmail,editsecond);
                break;
        }
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
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }
            Log.e("-----*******----",json.toString());
            PayHttpUtils httpUtils = new PayHttpUtils();
            JSONObject result = httpUtils.post(url,json.toString(),null,null);
            if(result!=null) {
                Log.e("-----*******----",result.toString());
                try {
                    String flag="0";
                    if (serveText.getText().toString().equals("下一步")){
                        flag= result.getString("code");
                        setcode(flag);
                        return true;
                    }
                    Log.e("-----*******----",result.getString("flag"));
                    Log.e("-----*******----",flag.compareTo("0")+"");
                    flag= result.getString("flag");
                    if (Integer.parseInt(flag)>0) {
                        setSP(editId);
                        return true;
                    } else {
                        ToastDiag.Toast(EditMessageActivity.this, "保存失败");
                    }
                } catch (Exception e) {
                    return false;
                }
            }
            return false;
        }
        @Override
        protected void onPostExecute(final Boolean success) {

            pushTask = null;
            if (success) {
                if (serveText.getText().toString().equals("保存"))
                onBackPressed();
            } else {
                ToastDiag.Toast(EditMessageActivity.this,"保存失败");
            }
        }
        @Override
        protected void onCancelled() {
            pushTask = null;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            //获得android定义的短片动画时间
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            //设置该VIEW为显示
            meditFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            //设置动画的渐变效果
            meditFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    meditFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            meditFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
