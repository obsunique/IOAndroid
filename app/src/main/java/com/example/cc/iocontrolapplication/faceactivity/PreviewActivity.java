package com.example.cc.iocontrolapplication.faceactivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.LivenessInfo;
import com.arcsoft.face.VersionInfo;
import com.example.cc.iocontrolapplication.R;
import com.example.cc.iocontrolapplication.common.Constants;
import com.example.cc.iocontrolapplication.facemodel.DrawInfo;
import com.example.cc.iocontrolapplication.faceutil.AipfaceUtil;
import com.example.cc.iocontrolapplication.faceutil.ConfigUtil;
import com.example.cc.iocontrolapplication.faceutil.DrawHelper;
import com.example.cc.iocontrolapplication.faceutil.ImageUtil;
import com.example.cc.iocontrolapplication.faceutil.camera.CameraHelper;
import com.example.cc.iocontrolapplication.faceutil.camera.CameraListener;
import com.example.cc.iocontrolapplication.facewidget.FaceRectView;
import com.example.cc.iocontrolapplication.login.ForgetActivity;
import com.example.cc.iocontrolapplication.main.IOIndex;
import com.example.cc.iocontrolapplication.usercenter.UserActivity;
import com.example.cc.iocontrolapplication.utils.ImgUtil;
import com.example.cc.iocontrolapplication.utils.PayHttpUtils;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class PreviewActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener{
    private static final String TAG = "PreviewActivity";
    private CameraHelper cameraHelper;
    private DrawHelper drawHelper;
    private Camera.Size previewSize;
    private Integer cameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private FaceEngine faceEngine;
    private int afCode = -1;
    private int processMask =  FaceEngine.ASF_LIVENESS;
    private Toast toast = null;
    /**
     * 相机预览显示的控件，可为SurfaceView或TextureView
     */
    private View previewView;
    private FaceRectView faceRectView;
    private ImageView face_back;
    private ImageView face_list;
    private TextView autoCompleteTextView;
    private Integer pauseFrame = 0;
    private ImageView pauseFrameView ;
    private boolean detectResult = false;
    private JSONObject detectJson ;
    private JSONObject addUserJson ;
    private JSONObject searchJson;
    private JSONObject userCopyJson;
    private JSONObject groupAddJson;
    private JSONObject groupDelectJson;
    private boolean detectstatus = false;

    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    /**
     * 所需的所有权限信息
     */
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null) actionBar.hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
            attributes.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            getWindow().setAttributes(attributes);
        }

        // Activity启动后就锁定为启动时的方向
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            default:
                break;
        }

        previewView = findViewById(R.id.texture_preview);
        faceRectView = findViewById(R.id.face_rect_view);
        face_back = findViewById(R.id.face_back);

        face_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });  //返回用户界面

        face_list = findViewById(R.id.face_list);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        pauseFrameView = findViewById(R.id.imageView);
        //在布局结束后才做初始化操作
        previewView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        setResult(0,intent);
        if (cameraHelper != null) {
            cameraHelper.release();
            cameraHelper = null;
        }
        unInitEngine();
        finish();
    }  //返回按钮方法

    private void initEngine() {

        faceEngine = new FaceEngine();
        faceEngine.active(PreviewActivity.this, Constants.APP_ID,Constants.SDK_KEY);
        afCode = faceEngine.init(this.getApplicationContext(), FaceEngine.ASF_DETECT_MODE_VIDEO, ConfigUtil.getFtOrient(this),
                2, 1, FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_AGE | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_GENDER | FaceEngine.ASF_LIVENESS);
        VersionInfo versionInfo = new VersionInfo();
        faceEngine.getVersion(versionInfo);
        Log.i(TAG, "initEngine:  init: " + afCode + "  version:" + versionInfo);
        if (afCode != ErrorInfo.MOK) {
            Toast.makeText(this, "引擎加载失败", Toast.LENGTH_SHORT).show();
        }
    }  //引擎初始化

    private void unInitEngine() {
        if (afCode == 0) {
            afCode = faceEngine.unInit();
            Log.i(TAG, "unInitEngine: " + afCode);
        }
    }  //去除引擎初始化


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraHelper != null) {
            cameraHelper.release();
            cameraHelper = null;
        }
        unInitEngine();
    }  //销毁

    private boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this.getApplicationContext(), neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }  //检查权限

    private void initCamera() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final AipfaceUtil aipfaceUtil = new AipfaceUtil();
        CameraListener cameraListener = new CameraListener() {
            @Override
            public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
                Log.i(TAG, "onCameraOpened: " + cameraId + "  " + displayOrientation + " " + isMirror);
                previewSize = camera.getParameters().getPreviewSize();
                drawHelper = new DrawHelper(previewSize.width, previewSize.height, previewView.getWidth(), previewView.getHeight(), displayOrientation
                        , cameraId, isMirror);
                Log.e("faceInfoList",previewView.getWidth()+"   "+previewView.getHeight());
            } //打开摄像机


            @Override
            public void onPreview(byte[] nv21, Camera camera) {

                if (faceRectView != null) {
                    faceRectView.clearFaceInfo();
                }
                List<FaceInfo> faceInfoList = new ArrayList<>();
                int code = faceEngine.detectFaces(nv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList);
                if (code == ErrorInfo.MOK && faceInfoList.size() > 0) {
                    code = faceEngine.process(nv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList, processMask);
                    if (code != ErrorInfo.MOK) {
                        return;
                    }
                }else {
                    autoCompleteTextView.setText("未检测到人脸");
                    autoCompleteTextView.setTextColor(Color.parseColor("#ffff00"));
                    pauseFrame = 1;
                    return;
                }

                List<LivenessInfo> faceLivenessInfoList = new ArrayList<>();

                int livenessCode = faceEngine.getLiveness(faceLivenessInfoList);

                //有其中一个的错误码不为0，return
                if (( livenessCode) != ErrorInfo.MOK) {
                    Log.e(TAG, "onPreview: false" );
                    return;
                }
                if (faceRectView != null && drawHelper != null) {
                    List<DrawInfo> drawInfoList = new ArrayList<>();
                    for (int i = 0; i < faceInfoList.size(); i++) {
                        drawInfoList.add(new DrawInfo(faceInfoList.get(i).getRect(), faceLivenessInfoList.get(i).getLiveness(), null));
                        if(faceLivenessInfoList.get(i).getLiveness()==1)
                        {
                            autoCompleteTextView.setText("活体检测通过");
                            autoCompleteTextView.setTextColor(Color.parseColor("#008000"));
                            if(pauseFrame == 0)
                            {
                                if(!detectstatus) {
                                //图片转换
                                Bitmap bitmap = ImageUtil.nv21ToBitmap(nv21,previewSize.width,previewSize.height,PreviewActivity.this);
                                bitmap = ImageUtil.rotaingImageView(270,bitmap);
                                pauseFrameView.setImageBitmap(bitmap);
                                String base64=ImgUtil.bitmapToBase64(bitmap);
                                        //detect上传
                                        detectstatus = true;
                                        JSONObject json = new JSONObject();
                                        try {
                                            json.put("imagein", base64);
                                        } catch (Exception e) {
                                        }
                                        AipfaceTask aipfaceTask = new AipfaceTask("http://47.107.248.227:8080/Aipface/detect", json, "detect");
                                        aipfaceTask.execute((Void) null);
                                        if (detectJson != null) {
                                            try {
                                                detectJson = detectJson.getJSONObject("result");
                                                JSONArray array =  detectJson.getJSONArray("face_list");
                                                detectJson = array.getJSONObject(0);
                                                detectJson.get("blur");//模糊程度
                                                Log.e(TAG, "--------DetectOnPreview:模糊程度 "+detectJson.get("blur"));
                                                detectJson.get("completeness");//人脸完整度
                                                Log.e(TAG, "--------DetectOnPreview:人脸完整度 "+detectJson.get("completeness"));
                                                JSONObject angle = detectJson.getJSONObject("angle");
                                                angle.get("pitch"); //仰俯角
                                                Log.e(TAG, "--------DetectOnPreview: 仰俯角"+detectJson.get("pitch"));
                                                angle.get("roll");  //平面旋转角
                                                Log.e(TAG, "--------DetectOnPreview: 平面旋转角"+detectJson.get("roll"));
                                                angle.get("yaw");  //左右旋转角
                                                Log.e(TAG, "--------DetectOnPreview: 左右旋转角"+detectJson.get("yaw"));
                                                JSONObject location = detectJson.getJSONObject("location");
                                                location.get("width"); //人脸宽度
                                                Log.e(TAG, "--------DetectOnPreview: 人脸宽度"+detectJson.get("width"));
                                                location.get("height"); //人脸高度
                                                Log.e(TAG, "--------DetectOnPreview: 人脸高度"+detectJson.get("height"));
                                                JSONObject quality = detectJson.getJSONObject("quality");
                                                quality.get("illumination"); //光照程度
                                                Log.e(TAG, "--------DetectOnPreview: 光照程度"+detectJson.get("illumination"));
                                                JSONObject occlusion = quality.getJSONObject("occlusion");
                                                occlusion.get("left_eye"); //左眼被遮挡
                                                Log.e(TAG, "--------DetectOnPreview: 左眼被遮挡"+detectJson.get("yaw"));
                                                occlusion.get("right_eye"); //右眼被遮挡
                                                Log.e(TAG, "--------DetectOnPreview: 右眼被遮挡"+detectJson.get("right_eye"));
                                                occlusion.get("nose"); //鼻子被遮挡
                                                Log.e(TAG, "--------DetectOnPreview: 鼻子被遮挡"+detectJson.get("nose"));
                                                occlusion.get("mouth"); //嘴巴被遮挡
                                                Log.e(TAG, "--------DetectOnPreview: 嘴巴被遮挡"+detectJson.get("mouth"));
                                                occlusion.get("left_check"); //左脸颊被遮挡
                                                Log.e(TAG, "--------DetectOnPreview: 左脸颊被遮挡"+detectJson.get("left_check"));
                                                occlusion.get("right_check"); //右脸颊被遮挡
                                                Log.e(TAG, "--------DetectOnPreview: 右脸颊被遮挡"+detectJson.get("right_check"));
                                                occlusion.get("chin_contour"); //下巴被遮挡
                                                Log.e(TAG, "--------DetectOnPreview: 下巴被遮挡"+detectJson.get("chin_contour"));

                                                if((double)detectJson.get("blur") > 0.4)
                                                {
                                                    autoCompleteTextView.setText("请保持手机不要晃动");
                                                    autoCompleteTextView.setTextColor(Color.parseColor("#ffff00"));
                                                    detectstatus = false;
                                                }
                                                else if((Integer)detectJson.get("completeness") != 1)
                                                {
                                                    autoCompleteTextView.setText("请把脸部全部移入相框内");
                                                    autoCompleteTextView.setTextColor(Color.parseColor("#ffff00"));
                                                    detectstatus = false;
                                                }
                                                else if((Integer)angle.get("pitch") > 20 || (Integer)angle.get("roll") > 20 || (Integer)angle.get("yaw")>20)
                                                {
                                                    autoCompleteTextView.setText("请将正脸移入框内");
                                                    autoCompleteTextView.setTextColor(Color.parseColor("#ffff00"));
                                                    detectstatus = false;
                                                }
                                                else if((Integer)location.get("width")<100 || (Integer)location.get("height") <100)
                                                {
                                                    autoCompleteTextView.setText("请将脸靠近相机");
                                                    autoCompleteTextView.setTextColor(Color.parseColor("#ffff00"));
                                                    detectstatus = false;
                                                }
                                                else if((Integer)location.get("width")>200 || (Integer)location.get("height") >200)
                                                {
                                                    autoCompleteTextView.setText("请不要离相机这么近");
                                                    autoCompleteTextView.setTextColor(Color.parseColor("#ffff00"));
                                                    detectstatus = false;
                                                }
                                                else if((Integer)quality.get("illumination")<100)
                                                {
                                                    autoCompleteTextView.setText("请在光源充足的地方进行");
                                                    autoCompleteTextView.setTextColor(Color.parseColor("#ffff00"));
                                                    detectstatus = false;
                                                }
                                                else if((Integer)occlusion.get("left_eye")>0.6)
                                                {
                                                    autoCompleteTextView.setText("请勿遮挡左眼");
                                                    autoCompleteTextView.setTextColor(Color.parseColor("#ffff00"));
                                                    detectstatus = false;
                                                }
                                                else if((Integer)occlusion.get("right_eye")>0.6)
                                                {
                                                    autoCompleteTextView.setText("请勿遮挡右眼");
                                                    autoCompleteTextView.setTextColor(Color.parseColor("#ffff00"));
                                                    detectstatus = false;
                                                }
                                                else if((Integer)occlusion.get("nose")>0.7)
                                                {
                                                    autoCompleteTextView.setText("请勿遮挡鼻子");
                                                    autoCompleteTextView.setTextColor(Color.parseColor("#ffff00"));
                                                    detectstatus = false;
                                                }
                                                else if((Integer)occlusion.get("mouth")>0.7)
                                                {
                                                    autoCompleteTextView.setText("请勿遮挡嘴巴");
                                                    autoCompleteTextView.setTextColor(Color.parseColor("#ffff00"));
                                                    detectstatus = false;
                                                }
                                                else if((Integer)occlusion.get("left_check")>0.8)
                                                {
                                                    autoCompleteTextView.setText("请勿遮挡左脸颊");
                                                    autoCompleteTextView.setTextColor(Color.parseColor("#ffff00"));
                                                    detectstatus = false;
                                                }
                                                else if((Integer)occlusion.get("right_check")>0.8)
                                                {
                                                    autoCompleteTextView.setText("请勿遮挡右脸颊");
                                                    autoCompleteTextView.setTextColor(Color.parseColor("#ffff00"));
                                                    detectstatus = false;
                                                }
                                                else if((Integer)occlusion.get("chin_contour")>0.6)
                                                {
                                                    autoCompleteTextView.setText("请勿遮挡下巴");
                                                    autoCompleteTextView.setTextColor(Color.parseColor("#ffff00"));
                                                    detectstatus = false;
                                                }
                                                else {
                                                    detectResult = true;
                                                }
                                            }catch (Exception e){}
                                        }//detect
                                }//图片转换
                                //卡帧判断
                                if(detectstatus) {
                                    pauseFrame = 1;
                                }
                                else {
                                    pauseFrame = pauseFrame + 1;
                                }
                            }
                            else if(pauseFrame == 25){
                                pauseFrame = 0;
                            }
                            else{
                                pauseFrame = pauseFrame + 1;
                            }
                        }
                        else if (faceLivenessInfoList.get(i).getLiveness()==0)
                        {
                            autoCompleteTextView.setText("活体检测未通过");
                            autoCompleteTextView.setTextColor(Color.parseColor("#ff0000"));
                        }
                    }
                    drawHelper.draw(faceRectView, drawInfoList);
                }
            }

            @Override
            public void onCameraClosed() {
                Log.i(TAG, "onCameraClosed: ");
            }

            @Override
            public void onCameraError(Exception e) {
                Log.i(TAG, "onCameraError: " + e.getMessage());
            }

            @Override
            public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {
                if (drawHelper != null) {
                    drawHelper.setCameraDisplayOrientation(displayOrientation);
                }
                Log.i(TAG, "onCameraConfigurationChanged: " + cameraID + "  " + displayOrientation);
            }

        };
        cameraHelper = new CameraHelper.Builder()
                .previewViewSize(new Point(previewView.getMeasuredWidth(),previewView.getMeasuredHeight()))
                .rotation(getWindowManager().getDefaultDisplay().getRotation())
                .specificCameraId(cameraID != null ? cameraID : Camera.CameraInfo.CAMERA_FACING_FRONT)
                .isMirror(false)
                .previewOn(previewView)
                .cameraListener(cameraListener)
                .build();
        cameraHelper.init();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            boolean isAllGranted = true;
            for (int grantResult : grantResults) {
                isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
            }
            if (isAllGranted) {
                initEngine();
                initCamera();
                if (cameraHelper != null) {
                    cameraHelper.start();
                }
            } else {
                Toast.makeText(this.getApplicationContext(), "引擎加载失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 在{@link #previewView}第一次布局完成后，去除该监听，并且进行引擎和相机的初始化
     */
    @Override
    public void onGlobalLayout() {
        previewView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
        } else {
            initEngine();
            initCamera();
        }
    }

    private void showToast(String s) {
        if (toast == null) {
            toast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            toast.setText(s);
            toast.show();
        }
    }



    public class AipfaceTask extends AsyncTask<Void, Void, Boolean>{

        private final JSONObject json;
        private final String url;
        private final String classname;

        AipfaceTask(String url,JSONObject json,String classname) {
            this.json = json;
            this.url=url;
            this.classname = classname;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
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
            if(classname == "detect")
            {
                detectJson = result;
            }
            else if(classname == ""){

            }
            // TODO: register the new account here.
            return false;
        }


    }
}
