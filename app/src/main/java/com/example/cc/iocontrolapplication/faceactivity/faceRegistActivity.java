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
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
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
import com.example.cc.iocontrolapplication.utils.ImgUtil;
import com.example.cc.iocontrolapplication.utils.PayHttpUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/12/21.
 */

public class faceRegistActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener {
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

    private TextView textView;
    private boolean detectResult = false;
    private boolean personVerifyResult = false;
    private boolean addUserResult = false;
    private boolean detectstatus = false;

    private String userid;
    private String username;

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
        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");
        username = intent.getStringExtra("username");
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
        textView = findViewById(R.id.textView);
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
        faceEngine.active(faceRegistActivity.this, Constants.APP_ID,Constants.SDK_KEY);
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
                                Log.i(TAG, "--------onPreview: "+pauseFrame+"     detectstatus"+detectstatus);
                                if(detectstatus == false) {
                                    //图片转换

                                    pauseFrame = 1;
                                    detectstatus = true;
                                    Bitmap bitmap = ImageUtil.nv21ToBitmap(nv21,previewSize.width,previewSize.height,faceRegistActivity.this);
                                    bitmap = ImageUtil.rotaingImageView(270,bitmap);
                                    Log.e(TAG, "onPreview: 我进行了截图" );

                                    String base64= ImgUtil.bitmapToBase64(bitmap);
                                    //detect上传
                                    final JSONObject json = new JSONObject();
                                    try {
                                        json.put("imagein", base64);
                                    } catch (Exception e) {}

                                    final JSONObject addUser = new JSONObject();
                                    try{
                                        addUser.put("imagein",base64);
                                        addUser.put("group","default");
                                    }catch (Exception e){}

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            JSONObject detectJson = PayHttpUtils.post("http://47.107.248.227:8080/Aipface/detect", json.toString(),null,null);
                                            boolean flag = true;
                                            if (detectJson != null) {
                                                Log.e("妈个鸡有毒呀","进入detectJSON判断");
                                                try {
                                                    detectJson = detectJson.getJSONObject("result");

                                                    JSONArray array =  detectJson.getJSONArray("face_list");
                                                    detectJson = array.getJSONObject(0);

                                                    JSONObject angle = detectJson.getJSONObject("angle");

                                                    angle.getDouble("pitch"); //仰俯角
                                                    Log.e(TAG, "--------DetectOnPreview: 仰俯角"+angle.getDouble("pitch"));

                                                    angle.get("roll");  //平面旋转角
                                                    Log.e(TAG, "--------DetectOnPreview: 平面旋转角"+angle.get("roll"));

                                                    angle.get("yaw");  //左右旋转角
                                                    Log.e(TAG, "--------DetectOnPreview: 左右旋转角"+angle.get("yaw"));

                                                    JSONObject location = detectJson.getJSONObject("location");

                                                    location.getDouble("width"); //人脸宽度
                                                    Log.e(TAG, "--------DetectOnPreview: 人脸宽度"+location.getDouble("width"));

                                                    location.getDouble("height"); //人脸高度
                                                    Log.e(TAG, "--------DetectOnPreview: 人脸高度"+location.getDouble("height"));

                                                    JSONObject quality = detectJson.getJSONObject("quality");
                                                    quality.getDouble("blur");//模糊程度
                                                    Log.e(TAG,"--------DetectOnPreview:模糊程度"+quality.getDouble("blur")+"");

                                                    quality.getDouble("completeness");//人脸完整度
                                                    Log.e(TAG, "--------DetectOnPreview:人脸完整度 "+quality.getDouble("completeness"));

                                                    quality.getInt("illumination"); //光照程度
                                                    Log.e(TAG, "--------DetectOnPreview: 光照程度"+quality.getInt("illumination"));

                                                    JSONObject occlusion = quality.getJSONObject("occlusion");
                                                    Log.e(TAG, "--------run: "+occlusion.toString() );

                                                    occlusion.getDouble("left_eye"); //左眼被遮挡
                                                    Log.e(TAG, "--------DetectOnPreview: 左眼被遮挡"+occlusion.getDouble("left_eye"));

                                                    occlusion.getDouble("right_eye"); //右眼被遮挡
                                                    Log.e(TAG, "--------DetectOnPreview: 右眼被遮挡"+occlusion.getDouble("right_eye"));

                                                    occlusion.getDouble("nose"); //鼻子被遮挡
                                                    Log.e(TAG, "--------DetectOnPreview: 鼻子被遮挡"+occlusion.getDouble("nose"));

                                                    occlusion.getDouble("mouth"); //嘴巴被遮挡
                                                    Log.e(TAG, "--------DetectOnPreview: 嘴巴被遮挡"+occlusion.getDouble("mouth"));

                                                    occlusion.getDouble("left_cheek"); //左脸颊被遮挡
                                                    Log.e(TAG, "--------DetectOnPreview: 左脸颊被遮挡"+occlusion.getDouble("left_cheek"));

                                                    occlusion.getDouble("right_cheek"); //右脸颊被遮挡
                                                    Log.e(TAG, "--------DetectOnPreview: 右脸颊被遮挡"+occlusion.getDouble("right_cheek"));

                                                    occlusion.getDouble("chin_contour"); //下巴被遮挡
                                                    Log.e(TAG, "--------DetectOnPreview: 下巴被遮挡"+occlusion.getDouble("chin_contour"));

                                                    if(quality.getDouble("blur") > 0.4)
                                                    {
                                                        textView.setText("画面模糊");
                                                        flag = false;
                                                    }
                                                    else if(quality.getDouble("completeness") != 1)
                                                    {
                                                        textView.setText("人脸不完整");
                                                        flag = false;
                                                    }
                                                    else if(angle.getDouble("pitch") > 20 || angle.getDouble("roll") > 20 || angle.getDouble("yaw")>20)
                                                    {
                                                        textView.setText("请勿露出正脸");
                                                        flag = false;
                                                    }
                                                    else if(location.getDouble("width")<100 || location.getDouble("height") <100)
                                                    {
                                                        textView.setText("人脸面积过小");
                                                        flag = false;
                                                    }
                                                    else if(location.getDouble("width")>350 || location.getDouble("height") >350)
                                                    {
                                                        textView.setText("人脸面积过大");
                                                        flag = false;
                                                    }
                                                    else if(quality.getInt("illumination")<100)
                                                    {
                                                        textView.setText("环境过暗");
                                                        flag = false;
                                                    }
                                                    else if(occlusion.getDouble("left_eye")>0.6)
                                                    {
                                                        textView.setText("请勿遮挡左眼");
                                                        flag = false;
                                                    }
                                                    else if(occlusion.getDouble("right_eye")>0.6)
                                                    {
                                                        textView.setText("请勿遮挡右眼");
                                                        flag = false;
                                                    }
                                                    else if(occlusion.getDouble("nose")>0.7)
                                                    {
                                                        textView.setText("请勿遮挡鼻子");
                                                        flag = false;
                                                    }
                                                    else if(occlusion.getDouble("mouth")>0.7)
                                                    {
                                                        textView.setText("请勿遮挡嘴巴");
                                                        flag = false;
                                                    }
                                                    else if(occlusion.getDouble("left_check")>0.8)
                                                    {
                                                        textView.setText("请勿遮挡脸颊");
                                                        flag = false;
                                                    }
                                                    else if(occlusion.getDouble("right_check")>0.8)
                                                    {
                                                        textView.setText("请勿遮挡脸颊");
                                                        flag = false;
                                                    }
                                                    else if(occlusion.getDouble("chin_contour")>0.6)
                                                    {
                                                        textView.setText("请勿遮挡下巴");
                                                        flag = false;
                                                    }
                                                    else {

                                                    }
                                                }catch (Exception e){}
                                                Log.e(TAG, "----------run: 这是底部");
                                                if(flag == true)
                                                {
                                                    detectResult = true;
                                                    Log.e(TAG, "---------run: 检测通过");
                                                }

                                                //开始人脸识别过程
                                                if(detectResult == true)
                                                {
                                                    Log.e(TAG, "--------run: 开始正式进行人脸识别" );
                                                    JSONObject searchJson = PayHttpUtils.post("http://47.107.248.227:8080/Aipface/search",addUser.toString(),null,null);
                                                    Log.e(TAG,"--------searchJson"+searchJson.toString());
                                                    if(searchJson != null)
                                                    {
                                                        try {
                                                            searchJson.get("face_token");
                                                            if(searchJson.get("face_token").equals("") || searchJson.get("face_token")!=null){
                                                                addUserResult = true;
                                                                showToast("注册成功，稍后返回页面");
                                                                onBackPressed();
                                                            }
                                                        }catch (Exception e){}
                                                    }
                                                    else {
                                                        showToast("searchJson为空");
                                                    }
                                                }
                                            }//detect
                                            else{
                                                showToast("searchJson系统错误，请稍后再试");
                                                detectstatus = false;
                                            }
                                        }
                                    }).start();
                                }//图片转换
                                //卡帧判断
                                if(detectResult == false) {
                                    detectstatus = false;
                                    pauseFrame = 1;
                                }
                            }
                            else if(pauseFrame == 25){
                                pauseFrame = 0;
                            }
                            else{
                                pauseFrame = pauseFrame + 1;
                            }
                        }
                        else if (faceLivenessInfoList.get(i).getLiveness()==0 && detectstatus==false)
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
}
