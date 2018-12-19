package com.example.cc.iocontrolapplication.usercenter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
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

/**
 * Created by cc on 2018/12/18.
 */

public class AvatarChoose extends AppCompatActivity implements View.OnClickListener {

        private ImageView mHeader_iv;

        //相册请求码
        private static final int ALBUM_REQUEST_CODE = 1;

        //剪裁请求码
        private static final int CROP_REQUEST_CODE = 3;

        private ActionBar actionBar;
        private ImageView backimageView;
        private TextView titleText;
        private TextView serveText;

        private String jsonname;
        private int userid;
        private String editValue;
        private int editId=0;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.layout_avatar);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
            }
            setCustomActionBar();
            initData();
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

    public void initData(){
        Intent intent=getIntent();
        userid=intent.getIntExtra("userid",-1);
        editId=intent.getIntExtra("editId",0);
        jsonname=intent.getStringExtra("editString");
        editValue=intent.getStringExtra("editV-alue");
    }
        private void initView() {
            mHeader_iv = (ImageView) findViewById(R.id.mHeader_iv);
            backimageView=(ImageView) findViewById(R.id.actionbar_edit_button);
            titleText=(TextView) findViewById(R.id.actionbar_edit_title);
            serveText=(TextView) findViewById(R.id.actionbar_edit_serve);

            titleText.setText("头像");
            serveText.setText("更换");
        }

        public void initViewLister(){
            backimageView.setOnClickListener(this);
            serveText.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.actionbar_edit_button:
                    onBackPressed();
                    break;
                case R.id.actionbar_edit_serve:
                    getPicFromAlbm();
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
    /**
     * 从相册获取图片
     */
    private void getPicFromAlbm() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        Log.e("----*-1----","我来了");
        startActivityForResult(photoPickerIntent, ALBUM_REQUEST_CODE);
        Log.e("----*0----","我来了");
    }

    /**
     * 裁剪图片
     */
    private void cropPhoto(Uri uri) {
        Log.e("----*3----","我来了");
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, CROP_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.e("----*1----",requestCode+"我来了");
        switch (requestCode) {
            case ALBUM_REQUEST_CODE:    //调用相册后返回
                Log.e("----*1----",resultCode+"我来了"+RESULT_OK);
                if (resultCode == RESULT_OK) {
                    Uri uri = intent.getData();
                    Log.e("----*----",uri+"");
                    cropPhoto(uri);
                }
                break;
            case CROP_REQUEST_CODE:     //调用剪裁后返回
                Log.e("----*2----","我来了");
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    //在这里获得了剪裁后的Bitmap对象，可以用于上传
                    Bitmap image = bundle.getParcelable("data");
                    //设置到ImageView上
                    mHeader_iv.setImageBitmap(image);
                    //也可以进行一些保存、压缩等操作后上传
//                    String path = saveImage("crop", image);
                }
                break;
        }
    }

    }