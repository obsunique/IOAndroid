package com.example.cc.iocontrolapplication.utils;

import android.os.CountDownTimer;
import android.widget.Button;

/**
 * Created by cc on 2018/12/16.
 */

public class CountDownTimerUtils extends CountDownTimer {
    private Button mButton;

    public CountDownTimerUtils(Button button,long millisInFuture, long countDownInterval) {//控件，定时总时间,间隔时间
        super(millisInFuture, countDownInterval);
        this.mButton=button;

    }

    @Override
    public void onTick(long millisUntilFinished) {
        mButton.setClickable(false);//设置不可点击
        mButton.setText(millisUntilFinished/1000+"");//设置倒计时时间
        /* SpannableString spannableString=new SpannableString(bt_getcord.getText().toString());//获取按钮上的文字
        ForegroundColorSpan span=new ForegroundColorSpan(Color.RED);//设置文字颜色
        bt_getcord.setAllCaps(false);
        spannableString.setSpan(span,0,2,Spannable.SPAN_INCLUSIVE_EXCLUSIVE);////将倒计时的时间设置为红色
        bt_getcord.setText(spannableString);*/

    }

    @Override
    public void onFinish() {
        mButton.setClickable(true);//重新获得点击
        mButton.setText("重新获取验证码");

    }
}
