package com.example.cc.iocontrolapplication.FunctionService;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cc.iocontrolapplication.R;

/**
 * Created by cc on 2018/12/18.
 */

public class FacepageActivity extends AppCompatActivity implements View.OnClickListener{

    private ActionBar actionBar;
    private ImageView backimageView;
    private TextView titleText;

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

        titleText.setText("人脸识别");
    }

    public void initViewLister(){
        backimageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.actionbar_edit_button:
                onBackPressed();
                break;
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
}
