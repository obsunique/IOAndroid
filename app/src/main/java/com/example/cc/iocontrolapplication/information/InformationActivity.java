package com.example.cc.iocontrolapplication.information;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.cc.iocontrolapplication.R;

/**
 * Created by cc on 2018/12/14.
 */

public class InformationActivity extends Activity {

    private LinearLayout view1,view2;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_lifemessage);


        view1=(LinearLayout)findViewById(R.id.openpage_one);
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPage("https://47.148.248.227:8080/helloword");
            }
        });
        view2=(LinearLayout)findViewById(R.id.openpage_two);
        view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPage("https://baijiahao.baidu.com/s?id=1620376078176703226&wfr=spider&for=pc");
            }
        });

    }
    public void gotoPage(String webname){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClass(InformationActivity.this,OpenInformationPage.class);
        intent.putExtra("webname",webname);
        startActivity(intent);
    }

}
