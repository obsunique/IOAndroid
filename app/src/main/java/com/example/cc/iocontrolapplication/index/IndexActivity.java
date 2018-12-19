package com.example.cc.iocontrolapplication.index;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.cc.iocontrolapplication.FunctionService.FacepageActivity;
import com.example.cc.iocontrolapplication.FunctionService.PaymessageActivity;
import com.example.cc.iocontrolapplication.R;
import com.example.cc.iocontrolapplication.utils.ToastDiag;

/**
 * Created by cc on 2018/12/14.
 */

public class IndexActivity extends Activity implements View.OnClickListener{
    private LinearLayout editor;
    private LinearLayout subway,idcard,more;
    private LinearLayout paymessage,nopay,face;

    public void onCreate(Bundle onsave){
        super.onCreate(onsave);
        setContentView(R.layout.layout_index);
        initLayout();
        initLayoutLister();

    }
    public void initLayout(){
        subway=(LinearLayout)findViewById(R.id.subway_function);
        idcard=(LinearLayout)findViewById(R.id.idcard_function);
        more=(LinearLayout)findViewById(R.id.more_function);
        paymessage=(LinearLayout)findViewById(R.id.paymessage_function);
        nopay=(LinearLayout)findViewById(R.id.nopay_function);
        face=(LinearLayout)findViewById(R.id.face_function);
        editor=(LinearLayout)findViewById(R.id.editor);
    }
    public void initLayoutLister(){
        subway.setOnClickListener(this);
        idcard.setOnClickListener(this);
        more.setOnClickListener(this);
        paymessage.setOnClickListener(this);
        nopay.setOnClickListener(this);
        face.setOnClickListener(this);
        editor.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent=new Intent();
        switch (view.getId()){
            case R.id.subway_function:
                ToastDiag.warnDiag(IndexActivity.this,"敬请期待");
                break;
            case R.id.idcard_function:
                ToastDiag.warnDiag(IndexActivity.this,"敬请期待");
                break;
            case R.id.more_function:
                ToastDiag.warnDiag(IndexActivity.this,"等多应用正在开发中");
                break;
            case R.id.paymessage_function:
                intent.setClass(IndexActivity.this, PaymessageActivity.class);
                startActivity(intent);
                break;
            case R.id.nopay_function:
                ToastDiag.warnDiag(IndexActivity.this,"正在开发中");
                break;
            case R.id.face_function:
                intent.setClass(IndexActivity.this, FacepageActivity.class);
                startActivity(intent);
                break;
            case R.id.editor:
                Toast.makeText(IndexActivity.this,"欢迎您，兄得",Toast.LENGTH_SHORT).show();
                break;

        }
    }

}
