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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cc.iocontrolapplication.R;
import com.example.cc.iocontrolapplication.utils.PayHttpUtils;

import org.json.JSONObject;

/**
 * Created by cc on 2018/12/14.
 */

public class EditMessageActivity extends AppCompatActivity implements View.OnClickListener,TextView.OnEditorActionListener {

    private EditText editText,editTextSecond;
    private LinearLayout editTextView,editTextSecondView;
    private TextView editTextName,editTextSecondName;

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
            AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                    .setTitle("提示")//标题
                    .setMessage("网络连接错误")//内容
                    .setIcon(R.mipmap.ic_launcher)//图标
                    .create();
            alertDialog1.show();
        }else {
            backimageView=(ImageView) findViewById(R.id.actionbar_edit_button);
            titleText=(TextView) findViewById(R.id.actionbar_edit_title);
            serveText=(TextView) findViewById(R.id.actionbar_edit_serve);

            switch (editId){
                case 1:
                    titleText.setText("更改昵称");;
                    break;
                case 2:
                    titleText.setText("身份认证");;
                    break;
                case 3:
                    titleText.setText("更改手机号码");;
                    break;
                case 4:
                    titleText.setText("注册邮箱");;
                    break;
                default:break;
            }

            editText = (EditText) findViewById(R.id.edit_message);
            if(editValue.equals("前往设置")){
                editValue="";
            }
            if(editId==2){
                editValue="";
                mProgressView=(View)findViewById(R.id.edit_progress);
                meditFormView=(View)findViewById(R.id.edit_form);
                editTextName=(TextView)findViewById(R.id.edit_message_name);
                editTextName.setVisibility(View.VISIBLE);
                editTextSecondView=(LinearLayout)findViewById(R.id.edit_message_second_view);
                editTextSecondView.setVisibility(View.VISIBLE);
                editTextSecond=(EditText) findViewById(R.id.edit_message_second);
            }
            editText.setText(editValue);
        }
    }
    public void initLayoutListener(){
        editText.setOnEditorActionListener(this);
        if(editId==2){
            editTextSecond.setOnEditorActionListener(this);
        }
        backimageView.setOnClickListener(this);
        serveText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.actionbar_edit_button:
                onBackPressed();
                break;
            case R.id.actionbar_edit_serve:
                push();
                break;
            default:
                break;
        }
    }
    public String checkUrl(int editId){
        String url="";
        switch (editId){
            case 1:
                url="http://47.107.248.227:8080/android/User/updataUserPerfectMesaage";
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
    public boolean  onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        if(editId!=2){
            intent.putExtra("editString",jsonname);
            intent.putExtra("editValue",editValue);
        }
        setResult(editId,intent);
        finish();
    }

    public void push(){
        String editOne=editText.getText().toString();
        String editsecond="";
        if(editId==0||userid==0){
            AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                    .setTitle("提示")//标题
                    .setMessage("网络连接错误")//内容
                    .setIcon(R.mipmap.ic_launcher)//图标
                    .create();
            alertDialog1.show();
            return;
        }
        if(editId==2)
            editsecond=editTextSecond.getText().toString();
        if(TextUtils.isEmpty(editOne)){
            editText.setError("请输入");
            View view=editText;
            view.requestFocus();
        }else if(TextUtils.isEmpty(editsecond)&&editId==2){
            editTextSecond.setError("请输入");
            View view=editTextSecond;
            view.requestFocus();
        }else if(editsecond.length()==18&&editId==2){
            editTextSecond.setError("身份证位数不够");
            View view=editTextSecond;
            view.requestFocus();
        } else {
            JSONObject json = new JSONObject();
            try {
                json.put("userid",userid);
                json.put(jsonname,editOne);
                if(userid==2) {
                    json.put("useridcard", editsecond);
                    showProgress(true);
                }
            }catch (Exception e) {
                AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                        .setTitle("提示")//标题
                        .setMessage("系统错误")//内容
                        .setIcon(R.mipmap.ic_launcher)//图标
                        .create();
                alertDialog1.show();
            }
            Log.e("-------***---json---",json.toString());
            String url=checkUrl(editId);
            if(url.equals("")){
                AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                        .setTitle("提示")//标题
                        .setMessage("系统错误")//内容
                        .setIcon(R.mipmap.ic_launcher)//图标
                        .create();
                alertDialog1.show();
                return;
            }
            pushTask = new PushTask(url,json);
            pushTask.execute((Void) null);
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
            //String users="[{\"username\":"+mNumberView.getText().toString()+",\"password\":"+mPasswordView.getText().toString()+"}]";
            Log.e("-------***---json---",json.toString());
            PayHttpUtils httpUtils = new PayHttpUtils();
            JSONObject result = httpUtils.post(url,json.toString(),null,null);
            Log.e("-------***1---result---",result+"");
            if(result!=null){
                Log.e("-------***2---result---",result.toString());
                try {
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
            showProgress(false);
            pushTask = null;
            if (success) {
                onBackPressed();
            } else {
                editText.setError("已存在");
                View view=editText;
                view.requestFocus();
            }
        }
        @Override
        protected void onCancelled() {
            pushTask = null;
            showProgress(false);
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
