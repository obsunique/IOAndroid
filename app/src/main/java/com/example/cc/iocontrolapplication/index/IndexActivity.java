package com.example.cc.iocontrolapplication.index;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.cc.iocontrolapplication.R;

/**
 * Created by cc on 2018/12/14.
 */

public class IndexActivity extends Activity{
    private LinearLayout editor;
    public void onCreate(Bundle onsave){
        super.onCreate(onsave);
        setContentView(R.layout.layout_index);
        editor=(LinearLayout)findViewById(R.id.editor);
        editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(IndexActivity.this,"欢迎您，兄得",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
