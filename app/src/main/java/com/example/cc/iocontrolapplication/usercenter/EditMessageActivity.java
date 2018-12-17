package com.example.cc.iocontrolapplication.usercenter;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cc.iocontrolapplication.R;
import com.example.cc.iocontrolapplication.utils.PayHttpUtils;

import org.json.JSONObject;

/**
 * Created by cc on 2018/12/14.
 */

public class EditMessageActivity extends AppCompatActivity implements View.OnClickListener,TextView.OnEditorActionListener {

    private EditText editText;
    private PushTask pushTask=null;

    private String jsonname;
    private int userid;
    private String editValue;
    private int editId=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_edit_message);

        ActionBar actionBar=getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.show();

        initData();
        initLayout();
        initLayoutListener();

    }
    public void initData(){
        Intent intent=getIntent();
        userid=intent.getIntExtra("userid",-1);
        editId=intent.getIntExtra("editId",0);
        jsonname=intent.getStringExtra("editString");
        editValue=intent.getStringExtra("editValue");
    }
    public void initLayout(){
        editText=(EditText)findViewById(R.id.edit_message);
        editText.setText(editValue);
    }
    public void initLayoutListener(){
        editText.setOnEditorActionListener(this);
    }
    @Override
    public void onClick(View v) {

    }
    @Override
    public boolean onEditorAction(TextView v, int id, KeyEvent event) {
        switch (v.getId()) {
            case R.id.edit_message:
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    push();
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
        intent.putExtra("editString",jsonname);
        intent.putExtra("editValue",editValue);
        setResult(editId,intent);
        finish();
    }

    public void push(){
        if (pushTask != null) {
            return;
        }
        // Store values at the time of the login attempt.
        String jsonValue = editText.getText().toString();
        String roastString="";
        boolean cancel = false;
        if (TextUtils.isEmpty(jsonValue)) {
            cancel = true;
            roastString="请输入姓名";
        }
        if (cancel) {
            Toast.makeText(EditMessageActivity.this,roastString,Toast.LENGTH_SHORT);
        } else {
            editValue=jsonValue;
            String url = null;
            JSONObject json = new JSONObject();
            url = "http://47.107.248.227:8080/android/Login/usertest";
            try {
                json.put("username",userid);
                json.put("password", jsonValue);
            } catch (Exception e) {
            }
            if (json == null) {
                Toast.makeText(EditMessageActivity.this, "系统错误", Toast.LENGTH_SHORT).show();
            }
            /*
            pushTask = new PushTask(json, url);
            pushTask.execute((Void) null);
            */
        }
    }



    public class PushTask extends AsyncTask<Void, Void, Boolean> {
        private final JSONObject json;
        private final String url;
        PushTask(JSONObject json,String url) {
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
            pushTask = null;
            if (success) {
                Toast.makeText(EditMessageActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EditMessageActivity.this, "系统错误", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected void onCancelled() {
            pushTask = null;
        }
    }
}
