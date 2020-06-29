package com.anber.work.ui.activity.audio;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.anber.work.R;
import com.goldze.base.router.RouterActivityPath;

/**
 * OpenSL录制音频为pcm，AudioTrack播放音频
 */
//@Route(path = RouterActivityPath.DrawBmp.PAGER_DRAWBMP)
public class OpenSLActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_sl);
    }
}
