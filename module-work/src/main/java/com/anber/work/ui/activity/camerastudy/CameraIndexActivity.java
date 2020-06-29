package com.anber.work.ui.activity.camerastudy;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.anber.work.R;
import com.goldze.base.router.RouterActivityPath;
import com.goldze.base.utils.MainAdapter;
import com.goldze.base.utils.TabModel;

import java.util.ArrayList;
import java.util.List;
//@Route(path = RouterActivityPath.DrawBmp.PAGER_DRAWBMP)
public class CameraIndexActivity extends AppCompatActivity {

    private RecyclerView mRecContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_index);
        mRecContent = (RecyclerView) findViewById(R.id.rec_camera_index);
        mRecContent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        List<TabModel> list = new ArrayList<>();
        list.add(new TabModel("1. Camera使用", "Camera结合SurfaceView，GLSurfaceView，TextureView使用", CameraUseActivity.class));
        list.add(new TabModel("2. Camera数据共享", "Camera数据yuv->rgb，SurfaceView，GLSurfaceView，TextureView共享", CameraShareActivity.class));
        list.add(new TabModel("3. 多滤镜同时预览", "Camera数据结合GLSurfaceView实现同时预览多重滤镜", FourGLSurfaceViewActivity.class));
        mRecContent.setAdapter(new MainAdapter(list));
    }
}
