package com.example.cc.iocontrolapplication.main;

/**
 * Created by cc on 2018/12/9.
 */

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cc.iocontrolapplication.R;
import com.example.cc.iocontrolapplication.index.IndexActivity;
import com.example.cc.iocontrolapplication.information.InformationActivity;
import com.example.cc.iocontrolapplication.usercenter.UserActivity;

import java.util.ArrayList;
import java.util.List;

public class IOIndex extends Activity implements
        android.view.View.OnClickListener {


    private ViewPager mViewPager;// 用来放置界面切换
    private PagerAdapter mPagerAdapter;// 初始化View适配器

    private List<View> mViews = new ArrayList<View>();// 用来存放Tab01-03
    // 3个Tab，每个Tab包含一个按钮
    private LinearLayout mTabInformation;
    private LinearLayout mTabIndex;
    private LinearLayout mTabMysetting;

    // 3个按钮
    private ImageView mInformationImg;
    private ImageView mIndexImg;
    private ImageView mMysettingImg;

    private LinearLayout usernameView;
    private TextView username;

    private LocalActivityManager manager;
    private Intent intentIndex,intentInformation,intentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_page);

        manager = new LocalActivityManager(this, true);
        manager.dispatchCreate(savedInstanceState);
        //新页面接收数据
        //Bundle bundle = this.getIntent().getExtras();
        //接收name值
        //String name = bundle.getString("userid");
        initView();
        initViewPage();
        initEvent();
    }

    private void initEvent() {
        mTabIndex.setOnClickListener(this);
        mTabInformation.setOnClickListener(this);
        mTabMysetting.setOnClickListener(this);

        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            /**
             *ViewPage左右滑动时
             */
            @Override
            public void onPageSelected(int arg0) {
                int currentItem = mViewPager.getCurrentItem();
                switch (currentItem) {
                    case 0:
                        resetImg();
                        mIndexImg.setImageResource(R.drawable.index);
                        break;
                    case 1:
                        resetImg();
                        mInformationImg.setImageResource(R.drawable.information);
                        break;
                    case 2:
                        resetImg();
                        mMysettingImg.setImageResource(R.drawable.mysetting);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    /**
     * 初始化设置
     */
    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.id_viewpage);
        // 初始化3个LinearLayout
        mTabIndex = (LinearLayout) findViewById(R.id.index);
        mTabInformation = (LinearLayout) findViewById(R.id.information);
        mTabMysetting = (LinearLayout) findViewById(R.id.my_settings);

        // 初始化3个按钮
        mIndexImg = (ImageView) findViewById(R.id.index_img);
        mInformationImg = (ImageView) findViewById(R.id.information_img);
        mMysettingImg = (ImageView) findViewById(R.id.my_settings_img);

    }
    /**
     * 初始化ViewPage
     */
    private void initViewPage() {

        // 3个布局
        LayoutInflater mLayoutInflater = LayoutInflater.from(this);
        //View tab01 = mLayoutInflater.inflate(R.layout.layout_index, null);
        //View tab02 = mLayoutInflater.inflate(R.layout.layout_lifemessage, null);
        //View tab03 = mLayoutInflater.inflate(R.layout.activity_user, null);

        intentIndex=new Intent(IOIndex.this,IndexActivity.class);
        View tab01=manager.startActivity("first",intentIndex).getDecorView();

        intentInformation=new Intent(IOIndex.this,InformationActivity.class);
        View tab02=manager.startActivity("second",intentInformation).getDecorView();

        intentUser=new Intent(IOIndex.this,UserActivity.class);
        View tab03=manager.startActivity("third",intentUser).getDecorView();


        mViews.add(tab01);
        mViews.add(tab02);
        mViews.add(tab03);


        // 适配器初始化并设置
        mPagerAdapter = new PagerAdapter() {

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView(mViews.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = mViews.get(position);
                container.addView(view);
                return view;
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {

                return arg0 == arg1;
            }

            @Override
            public int getCount() {

                return mViews.size();
            }
        };
        mViewPager.setAdapter(mPagerAdapter);
    }

    /**
     * 判断哪个要显示，及设置按钮图片
     */
    @Override
    public void onClick(View arg0) {

        switch (arg0.getId()) {
            case R.id.index:
                mViewPager.setCurrentItem(0);
                resetImg();
                mIndexImg.setImageResource(R.drawable.index);
                break;
            case R.id.information:
                mViewPager.setCurrentItem(1);
                resetImg();
                mInformationImg.setImageResource(R.drawable.information);
                break;
            case R.id.my_settings:
                mViewPager.setCurrentItem(2);
                resetImg();
                mMysettingImg.setImageResource(R.drawable.mysetting);
                break;
            default:
                break;
        }
    }

    /**
     * 把所有图片变暗
     */
    private void resetImg() {
        mInformationImg.setImageResource(R.drawable.information_b);
        mIndexImg.setImageResource(R.drawable.index_b);
        mMysettingImg.setImageResource(R.drawable.mysetting_b);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UserActivity sActivity = (UserActivity) manager.getActivity("third");
        Log.e("----******----",requestCode+"我是谁"+resultCode);
        sActivity.handleActivityResult(requestCode, resultCode, data);

    }

}