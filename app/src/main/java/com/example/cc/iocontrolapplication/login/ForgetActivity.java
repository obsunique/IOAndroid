package com.example.cc.iocontrolapplication.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cc.iocontrolapplication.R;
import com.example.cc.iocontrolapplication.main.IOIndex;
import com.example.cc.iocontrolapplication.utils.CountDownTimerUtils;
import com.example.cc.iocontrolapplication.utils.PayHttpUtils;

import org.json.JSONObject;


public class ForgetActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;

    private TextInputLayout mforgetNumberView;
    private AutoCompleteTextView mNumberView;
    private View mInputPassword;
    private EditText mPasswordView;
    private EditText mAgainPasswordView;
    private View mProgressView;
    private View mForgetFormView;

    private TextView mForgetLastText;
    private TextView mForgetLoginText;

    private Button mForgetSendButton;
    private TextView mForgetCode;
    private String code;

    private String number;

    private Button mForginSignInButton;

    private CountDownTimerUtils countDownTimerUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        mNumberView = (AutoCompleteTextView) findViewById(R.id.forget_number);
        mInputPassword=(View)findViewById(R.id.input_password);

        mPasswordView = (EditText) findViewById(R.id.forget_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    return true;
                }
                return false;
            }
        });

        mAgainPasswordView = (EditText) findViewById(R.id.forget_again_password);
        mAgainPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    if(mAgainPasswordView.getText().toString().equals(mPasswordView.getText().toString())){
                        attemptLogin();
                        return true;
                    }
                }
                return false;
            }
        });
        mForgetCode=(TextView)findViewById(R.id.forget_code);
        mforgetNumberView=(TextInputLayout)findViewById(R.id.forget_number_view);

        mForginSignInButton= (Button) findViewById(R.id.forget_button);
        mForginSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                View focusView = null;
                if(mForginSignInButton.getText().toString().equals("完成")){
                    if(TextUtils.isEmpty(mPasswordView.getText().toString())&&TextUtils.isEmpty(mAgainPasswordView.getText().toString())){
                        mPasswordView.setError("请输入密码");
                        focusView = mPasswordView;
                        focusView.requestFocus();
                    }else if(mPasswordView.getText().toString().equals(mAgainPasswordView.getText().toString()))
                        attemptLogin();
                    else{
                        mAgainPasswordView.setError("俩次密码不一样");
                        focusView = mAgainPasswordView;
                        focusView.requestFocus();
                }
            }else{
                    if(TextUtils.isEmpty(mNumberView.getText().toString())){
                        mNumberView.setError("请输入账号");
                        focusView = mNumberView;
                        focusView.requestFocus();
                    }else if(TextUtils.isEmpty(code)){
                        mForgetCode.setError("请获取验证码");
                        focusView = mForgetCode;
                        focusView.requestFocus();
                    }else if(TextUtils.isEmpty(mForgetCode.getText().toString())){
                        mForgetCode.setError("请输入验证码");
                        focusView = mForgetCode;
                        focusView.requestFocus();
                    }else if(code.equals(mForgetCode.getText().toString())){
                        mforgetNumberView.setVisibility(View.GONE);
                        mNumberView.setVisibility(View.GONE);
                        mForgetCode.setVisibility(View.GONE);
                        mForgetSendButton.setVisibility(View.GONE);
                        code="";
                        mInputPassword.setVisibility(View.VISIBLE);
                        mForgetLastText.setVisibility(View.VISIBLE);
                        mForginSignInButton.setText("完成");
                    }
                }
            }
        });

        mForgetSendButton=(Button)findViewById(R.id.forget_send);

        countDownTimerUtils=new CountDownTimerUtils(mForgetSendButton,30000,1000);
        mForgetSendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                View focusView = null;
                number=mNumberView.getText().toString();
                if(isMobileNumber(number)&&!TextUtils.isEmpty(number)){
                    countDownTimerUtils.start();
                    JSONObject json = new JSONObject();
                    try {
                        json.put("userphone",mNumberView.getText().toString());
                    }catch (Exception e) {
                        mNumberView.setError(getString(R.string.error_invalid_number));
                        focusView = mNumberView;
                        focusView.requestFocus();
                    }
                    Log.e("-------***---json---",json.toString());
                    String url="http://47.107.248.227:8080/android/Login/sendCode";

                    mAuthTask = new UserLoginTask(url,json);
                    mAuthTask.execute((Void) null);

                }else{
                    mNumberView.setError(getString(R.string.error_field_required));
                    focusView = mNumberView;
                    focusView.requestFocus();
                }

            }
        });

        mForgetFormView = findViewById(R.id.forget_form);
        mProgressView = findViewById(R.id.forget_progress);

        mForgetLoginText=findViewById(R.id.forget_login);
        mForgetLoginText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(ForgetActivity.this,IOIndex.class);
                startActivity(intent);
            }
        });

        mForgetLastText=(TextView)findViewById(R.id.forget_last);
        mForgetLastText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mforgetNumberView.setVisibility(View.VISIBLE);
                mNumberView.setVisibility(View.VISIBLE);
                mForgetCode.setVisibility(View.VISIBLE);
                mForgetSendButton.setVisibility(View.VISIBLE);
                code="";
                mInputPassword.setVisibility(View.GONE);
                mForgetLastText.setVisibility(View.GONE);
                mForginSignInButton.setText("下一步");
            }
        });
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        String password = mPasswordView.getText().toString();
        View focusView = null;
            JSONObject json = new JSONObject();
            try {
                json.put("userphone",number);
                json.put("userpassword",password);
            }catch (Exception e) {
                mNumberView.setError(getString(R.string.error_invalid_number));
                focusView = mNumberView;
                focusView.requestFocus();
            }
            Log.e("-------***---json---",json.toString());
            String url="http://47.107.248.227:8080/android/Login/forget";

            Intent intent=new Intent();
            intent.setClass(ForgetActivity.this,LoginActivity.class);

            showProgress(true);
            mAuthTask = new UserLoginTask(url,json,intent);
            mAuthTask.execute((Void) null);

    }
    //判断手机
    public boolean isMobileNumber(String mobiles) {
        String telRegex = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(147,145))\\d{8}$";
        Log.e("------***------手机账号",""+mobiles.matches(telRegex));
        return mobiles.matches(telRegex);
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    /**
     * 转圈
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            //获得android定义的短片动画时间
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            //设置该VIEW为显示
            mForgetFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            //设置动画的渐变效果
            mForgetFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mForgetFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mForgetFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void setCode(String code){
        this.code=code;
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final JSONObject json;
        private final String url;
        private Intent intent;
        private JSONObject result;

        UserLoginTask(String url,JSONObject json) {
            this.url=url;
            this.json=json;
        }

        UserLoginTask(String url,JSONObject json,Intent intent) {
            this.url=url;
            this.json=json;
            this.intent=intent;
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
            result = PayHttpUtils.post(url,json.toString(),null,null);
            Log.e("-------***1---result---",result+"");
            if(result!=null){
                Log.e("-------***2---result---",result.toString());
                try {
                    if(mForginSignInButton.getText().toString().equals("下一步"))
                        setCode(result.getString("code"));
                    Log.e("-------***3---result---", result.getString("flag"));
                    return true;
                }catch (Exception e){
                    return false;
                }
            }
            // TODO: register the new account here.
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            if (success) {
                if(intent!=null){
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            } else {
                mPasswordView.setError("系统错误1");
                mAgainPasswordView.setError("系统错误2");
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

}

