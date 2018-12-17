package com.example.cc.iocontrolapplication.information;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.cc.iocontrolapplication.R;

/**
 * Created by cc on 2018/12/14.
 */

public class InformationActivity extends Activity {

    private LinearLayout view;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_lifemessage);


        view=(LinearLayout)findViewById(R.id.lifemessage);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InformationActivity.this,"成功打开此页面",Toast.LENGTH_SHORT).show();
            }
        });
    }

}
