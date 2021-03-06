package com.anber.sdkplayer;


import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.goldze.base.router.RouterActivityPath;
import com.goldze.base.utils.LogUtils;

import java.io.File;

@Route(path = RouterActivityPath.DrawBmp.PAGER_DRAWBMP)

public class PlayerActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    static {
        System.loadLibrary("native-lib");
    }

    private Button mbutton;
    private SeekBar mSeekBar;
    private Thread mThread;
    private Runnable mRunnable;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //去掉标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏，隐藏状态
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_player);

        initView();
        Intent intent = getIntent();
        if (intent != null) {
            url = intent.getStringExtra("url");
        }
        initData();
        initPermission();


    }

    private void initView() {
        mbutton = findViewById(R.id.btn_play);
        mSeekBar = findViewById(R.id.seek_progress);
//        if(mbutton != null){
            mbutton.setOnClickListener(this);
//        }
//        if(mSeekBar != null){
            mSeekBar.setOnSeekBarChangeListener(this);
//        }
    }


    private void initData() {
        mRunnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    int progress = (int) (getPlayPos() * 100);

                    //Log.i("===TAG===", "progress = " + progress * 100);
                    if(mSeekBar != null){
                        mSeekBar.setProgress(progress);

                    }
                    try {
                        Thread.currentThread().sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        mThread = new Thread(mRunnable);
        mThread.start();
    }

    //在onPause中暂停会有个问题，直接卡主了
    /*@Override
    protected void onResume() {
        super.onResume();

        mVideoView.playOnPause();
        Log.i("===TAG===", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("===TAG===", "onPause");
        mVideoView.playOnPause();
    }*/

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i("===TAG===", "onConfigurationChanged");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        double pos = (double) seekBar.getProgress() / (double) seekBar.getMax();
        Log.i("===TAG===", "Seek pos = " + pos);
        seekTo(pos);
    }

    //获取播放位置
    public native double getPlayPos();

    //滑动到
    public native double seekTo(double pos);

    //播放
    public native void play(String url);


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_play) {//play("rtmp://10.2.3.222:1935/live");
            play( url);
        }
    }

    /*权限相关*/
    private void initPermission() {
        int result = PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == -1) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
