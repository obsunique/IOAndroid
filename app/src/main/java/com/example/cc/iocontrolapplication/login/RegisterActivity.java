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
import android.widget.Toast;

import com.example.cc.iocontrolapplication.R;
import com.example.cc.iocontrolapplication.main.IOIndex;
import com.example.cc.iocontrolapplication.utils.CountDownTimerUtils;
import com.example.cc.iocontrolapplication.utils.PayHttpUtils;
import com.example.cc.iocontrolapplication.utils.SharedPrefUtility;

import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     * 权限请求
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     *
     *包含已知用户名和密码的虚拟身份验证存储。
     * todo:连接到一个真正的认证系统后删除。

     private static final String[] DUMMY_CREDENTIALS = new String[]{
     "foo@example.com:hello", "bar@example.com:world"
     };
     */
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     * 跟踪登录任务，以确保如果需要，我们可以取消它。
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mNumberView;

    private TextInputLayout mInputPassword;
    private EditText mPasswordView;

    private View mProgressView;
    private View mRegisterFormView;

    private TextView mRegisterLoginText;

    private Button mRegisterSendButton;
    private TextView mRegisterCode;
    private String code;

    private Button mRegisterSignInButton;
    private CountDownTimerUtils countDownTimerUtils;

    private String number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);


        mNumberView = (AutoCompleteTextView) findViewById(R.id.register_number);

        mInputPassword=(TextInputLayout)findViewById(R.id.register_input_number);

        mPasswordView = (EditText) findViewById(R.id.register_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    return true;
                }
                return false;
            }
        });

        mRegisterCode=(TextView)findViewById(R.id.register_code);


        mRegisterSignInButton= (Button) findViewById(R.id.register_button);
        mRegisterSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mRegisterSignInButton.getText().toString().equals("完成")){
                    attemptLogin();
                }else{
                    View focusView = null;
                    if(TextUtils.isEmpty(mNumberView.getText().toString())){
                        mNumberView.setError("请输入账号");
                        focusView = mNumberView;
                        focusView.requestFocus();
                    }else if(TextUtils.isEmpty(code)){
                        mRegisterCode.setError("请获取验证码");
                        focusView = mRegisterCode;
                        focusView.requestFocus();
                    }else if(TextUtils.isEmpty(mRegisterCode.getText().toString())){
                        mRegisterCode.setError("请输入验证码");
                        focusView = mRegisterCode;
                        focusView.requestFocus();
                    }else if(code.equals(mRegisterCode.getText().toString())){
                        mNumberView.setVisibility(View.GONE);
                        mRegisterCode.setVisibility(View.GONE);
                        mRegisterSendButton.setVisibility(View.GONE);
                        code="";
                        mInputPassword.setVisibility(View.VISIBLE);
                        mRegisterSignInButton.setText("完成");
                    }
                }
            }
        });

        mRegisterSendButton=(Button)findViewById(R.id.register_send);

        countDownTimerUtils=new CountDownTimerUtils(mRegisterSendButton,30000,1000);
        mRegisterSendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                View focusView = null;
                number=mNumberView.getText().toString();
                Log.e("-------***2---result---",!TextUtils.isEmpty(number)+" "+isMobileNumber(number));
                if(isMobileNumber(number)&&!TextUtils.isEmpty(number)){

                    countDownTimerUtils.start();
                    JSONObject json = new JSONObject();
                    try {
                        json.put("userphone",number);
                    }catch (Exception e) {
                        mNumberView.setError(getString(R.string.error_invalid_number));
                        focusView = mNumberView;
                        focusView.requestFocus();
                    }
                    Log.e("-------***---json---",json.toString());
                    String url="http://47.107.248.227:8080/android/Login/sendCode";
                    Toast.makeText(RegisterActivity.this,"已发送验证码",Toast.LENGTH_SHORT);
                    mAuthTask = new UserLoginTask(url,json);
                    mAuthTask.execute((Void) null);

                }else{
                    mNumberView.setError(getString(R.string.error_field_required));
                    focusView = mNumberView;
                    focusView.requestFocus();
                }
            }
        });

        mRegisterLoginText=findViewById(R.id.register_login);
        mRegisterLoginText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        String password = mPasswordView.getText().toString();
        View focusView = null;
        if (TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            focusView.requestFocus();
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("userphone",number);
            json.put("userpassword",mPasswordView.getText().toString());
        }catch (Exception e) {
            mNumberView.setError(getString(R.string.error_invalid_number));
            focusView = mNumberView;
            focusView.requestFocus();
        }
        Log.e("-------***---json---",json.toString());
        String url="http://47.107.248.227:8080/android/Login/register";

        Intent intent=new Intent();
        intent.setClass(RegisterActivity.this,IOIndex.class);

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

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    /**
     * 转圈
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            //获得android定义的短片动画时间
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            //设置该VIEW为显示
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            //设置动画的渐变效果
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void setCode(String code){
        this.code=code;
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final JSONObject json;
        private final String url;
        private Intent intent;
        private  String userid;


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
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }
            //String users="[{\"username\":"+mNumberView.getText().toString()+",\"password\":"+mPasswordView.getText().toString()+"}]";

            JSONObject result = PayHttpUtils.post(url,json.toString(),null,null);
            Log.e("-------***1---result---",result+"");
            if(result!=null){
                Log.e("-------***2---result---",result.toString());
                try {
                    if(mRegisterSignInButton.getText().toString().equals("下一步"))
                        setCode(result.getString("code"));
                    else
                        userid=result.getString("flag");

                    return true;
                }catch (Exception e){
                    return false;
                }
            }
            return false;
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                if(intent!=null) {

                    //保存登录状态
                    SharedPrefUtility.setParam(RegisterActivity.this, SharedPrefUtility.IS_LOGIN, true);
                    //保存登录个人信息
                    SharedPrefUtility.setParam(RegisterActivity .this, SharedPrefUtility.UserId, userid);

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            } else {
                setCode("");
                mPasswordView.setError(getString(R.string.error_incorrect_password));
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