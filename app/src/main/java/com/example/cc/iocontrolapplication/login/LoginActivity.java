package com.example.cc.iocontrolapplication.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
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
import com.example.cc.iocontrolapplication.utils.PayHttpUtils;
import com.example.cc.iocontrolapplication.utils.SharedPrefUtility;

import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

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
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mForgetText;
    private TextView mRegisterText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mNumberView = (AutoCompleteTextView) findViewById(R.id.login_number);
        mNumberView.clearFocus();
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();


        mPasswordView = (EditText) findViewById(R.id.login_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);

        mProgressView = findViewById(R.id.login_progress);

        mForgetText=findViewById(R.id.login_forget);
        mForgetText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(LoginActivity.this,ForgetActivity.class);
                startActivity(intent);
            }
        });

        mRegisterText=findViewById(R.id.login_register);
        mRegisterText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Callback received when a permissions request has been completed.

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }
     */

    /**
     * 尝试登陆
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        // Reset errors.
        mNumberView.setError(null);
        mPasswordView.setError(null);
        // Store values at the time of the login attempt.
        String number = mNumberView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(number)) {
            mNumberView.setError(getString(R.string.error_field_required));
            focusView = mNumberView;
            cancel = true;
        } else if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }else if (!isMobileNumber(number)) {
            mNumberView.setError(getString(R.string.error_invalid_number));
            focusView = mNumberView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.聚焦错误的地方
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            String url=null;

            JSONObject json = new JSONObject();
                //手机密码登录
                url="http://47.107.248.227:8080/android/Login/login";
                try {
                    json.put("userphone",mNumberView.getText().toString());
                    json.put("userpassword",mPasswordView.getText().toString());
                }catch (Exception e) {}

            if(json==null){
                Toast.makeText(LoginActivity.this,"系统错误1",Toast.LENGTH_SHORT).show();
            }
            showProgress(true);
            mAuthTask = new UserLoginTask(json,url);
            mAuthTask.execute((Void) null);
        }
    }

    //判断手机
    public boolean isMobileNumber(String mobiles) {
        String telRegex = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(147,145))\\d{8}$";
        Log.e("------***------手机账号",""+mobiles.matches(telRegex));
        return mobiles.matches(telRegex);
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }
    //密码大于6位
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 6;
    }

    private void setToastEorrer(View view){
        view.requestFocus();
    }
    /**
     * Shows the progress UI and hides the login form.
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            //设置动画的渐变效果
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final JSONObject json;
        private final String url;

        private String userid="";

        UserLoginTask(JSONObject json,String url) {
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
            if(result!=null){
                try {
                    userid=result.getString("flag");
                    if(userid.compareTo("0")>0){
                    Log.e("-------***3---result---", result.getString("flag"));
                    return true;
                    }
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

                //保存登录状态
                SharedPrefUtility.setParam(LoginActivity.this, SharedPrefUtility.IS_LOGIN, true);
                //保存登录个人信息
                SharedPrefUtility.setParam(LoginActivity .this, SharedPrefUtility.UserId, userid);

                Intent intent=new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(LoginActivity.this,IOIndex.class);
                //用Bundle携带数据
                //Bundle bundle=new Bundle();
                //传递name参数为tinyphp
                //bundle.putString("userid", 值);
                //intent.putExtras(bundle);

                startActivity(intent);
            } else {
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

