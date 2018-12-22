package com.example.cc.iocontrolapplication.FunctionService;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cc.iocontrolapplication.R;
import com.example.cc.iocontrolapplication.faceactivity.faceRegistActivity;
import com.example.cc.iocontrolapplication.utils.SharedPrefUtility;
import com.example.cc.iocontrolapplication.utils.ToastDiag;

/**
 * Created by cc on 2018/12/18.
 */

public class FacepageActivity extends AppCompatActivity implements View.OnClickListener{

    private ActionBar actionBar;
    private ImageView backimageView,icon;
    private TextView titleText,serveText,Face_value;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_face_page);
        setCustomActionBar();
        initView();
        initViewLister();
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

    private void initView() {

        backimageView=(ImageView) findViewById(R.id.actionbar_edit_button);
        titleText=(TextView) findViewById(R.id.actionbar_edit_title);
        serveText=(TextView)findViewById(R.id.actionbar_edit_serve);
        Face_value=(TextView)findViewById(R.id.Face_value);
        icon = (ImageView) findViewById(R.id.icon);
        serveText.setVisibility(View.GONE);
        titleText.setText("人脸识别");
    }

    public void initViewLister(){
        backimageView.setOnClickListener(this);
        Face_value.setOnClickListener(this);
        icon.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.actionbar_edit_button:
                onBackPressed();
                break;
            case R.id.Face_value:
                String userface=(String) SharedPrefUtility.getParam(FacepageActivity.this,SharedPrefUtility.UserFace,"");
                if(userface.equals("") || userface == null)
                {
                    ToastDiag.warnDiag(FacepageActivity.this,"您还未进行实名注册，请前往进行实名注册");
                }
                else if( userface.equals("") || userface == null)
                {
                    ToastDiag.warnDiag(FacepageActivity.this,"您还未进行实名注册，请前往进行实名注册");
                }
                else {
                    Intent intent = new Intent();
                    intent.setClass(FacepageActivity.this, faceRegistActivity.class);
                    intent.putExtra("userid", SharedPrefUtility.UserId.toString());
                    intent.putExtra("username", SharedPrefUtility.UserName.toString());
                    startActivityForResult(intent, 6);
                }
            default:
                break;
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
        setResult(0,intent);
        finish();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.e("----*1----",requestCode+"我来了");
        switch (requestCode) {
            case 6:
                if(resultCode ==8)
                ToastDiag.warnDiag(FacepageActivity.this,"恭喜您验证通过，给小主人请安了");
                else if (resultCode == 5)
                    ToastDiag.warnDiag(FacepageActivity.this,"验证并未通过");
                break;
        }
    }
}
