package com.anber.multimediaexample.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.anber.multimediaexample.BR;
import com.anber.multimediaexample.ItemDecoration.GridItemDecoration;
import com.anber.multimediaexample.R;
import com.anber.multimediaexample.databinding.ActivityKitMainBinding;
import com.anber.multimediaexample.utils.HomePageAdapter;
import com.anber.sdkplayer.PlayerExActivity;
import com.anber.sdkplayer.SecActivity;
import com.goldze.base.base.BaseActivity;
import com.goldze.base.base.BaseViewModel;
import com.goldze.base.base.UtilBase;
import com.goldze.base.router.RouterActivityPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by anber on 2019/6/21
 */
@Route(path = RouterActivityPath.Main.PAGER_MAIN)
public class MainKitActivity extends BaseActivity<ActivityKitMainBinding, BaseViewModel> implements View.OnClickListener {
    public static final int REQUEST_CAMERA_PERMISSION_CODE = 200;
    public static final int REQUEST_RECORD_AUDIO_PERMISSION_CODE = 201;
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 202;
    public static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION_CODE = 203;
    private boolean mPermissionGranted = false;


    /**
     * 直播间
     */
    private LinearLayout llLiveRoom;

    /**
     * 直播推流
     */
    private LinearLayout llLivePlugFlow;

    /**
     * 录屏推流
     */
    private LinearLayout llLiveScreenRecord;

    /**
     * 直播播放器
     */
    private LinearLayout llLivePlayer;

    /**
     * 点播播放器
     */
    private LinearLayout llVodPlayer;

    private HomePageAdapter adapter;
    private RecyclerView rvShortVideo;
    private String[] type = {"视频拍摄", "视频编辑", "抖音特效", "粒子特效", "虚拟背景抠像", "画中画编辑"};
    private int[] img = {R.mipmap.icon_pic, R.mipmap.icon_video_editing, R.mipmap.icon_douyin, R.mipmap.icon_the_particle, R.mipmap.icon_cutout, R.mipmap.icon_pic_in_pic};
    private List<String> types = new ArrayList<>();
    private List<Integer> imgs = new ArrayList<>();

    private boolean mWaitFlag = false;

    private boolean arSceneFinished = false;// 人脸初始化完成的标识
    private boolean initARSceneing = true;// 记录人脸模块正在初始化
    private boolean isClickRepeat = false;// 防止页面重复点击标识

//    @Autowired(name = RouterServicePath.UPDATE.SERVICE_UPDATE)
//    public UpdateService updateService;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_kit_main;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {

        if (!mPermissionGranted) {
            checkAllPermission();
        }

        UtilBase.init(this);
//        VideoServiceManager.get().init(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);   //继承AppCompatActivity中使用
        super.onCreate(savedInstanceState);
        initView();

        //没有权限，则请求权限
        if (!mPermissionGranted) {
            checkAllPermission();
            doClick();
        } else {
            if (mWaitFlag) {
                return;
            }
            mWaitFlag = true;
            doClick();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWaitFlag = false;
        isClickRepeat = false;
    }

    private void initView() {
        rvShortVideo = findViewById(R.id.rv_short_video);
        llLiveRoom = findViewById(R.id.ll_live_room);
        llLivePlugFlow = findViewById(R.id.ll_live_plug_flow);
        llLiveScreenRecord = findViewById(R.id.ll_live_screen_record);
        llLivePlayer = findViewById(R.id.ll_live_player);
        llVodPlayer = findViewById(R.id.ll_vod_player);
        rvShortVideo = findViewById(R.id.rv_short_video);

        types.addAll(Arrays.asList(type));
        for (int i : img) {
            imgs.add(i);
        }
        adapter = new HomePageAdapter(this, types, imgs);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        rvShortVideo.setLayoutManager(manager);
        rvShortVideo.addItemDecoration(new GridItemDecoration(getResources().getColor(R.color.white_90), 2, 0));
        rvShortVideo.setAdapter(adapter);

        initListener();
    }

    private void initListener() {
        llLiveRoom.setOnClickListener(this);
        llVodPlayer.setOnClickListener(this);
        llLivePlayer.setOnClickListener(this);
        llLivePlugFlow.setOnClickListener(this);
        llLiveScreenRecord.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (isClickRepeat) {
            return;
        }
        isClickRepeat = true;

        int i = v.getId();//进入直播列表页面
        if (i == R.id.ll_live_room) {
//            ARouter.getInstance().build(RouterActivityPath.DrawBmp.PAGER_DRAWBMP).navigation();
            Intent intent;
            intent = new Intent(this, SecActivity.class);
            startActivity(intent);
            //进入推流页面
        } else if (i == R.id.ll_live_plug_flow) {
            ARouter.getInstance().build(RouterActivityPath.Push.PAGER_PUSH).navigation();
            //进入录屏推流页面
        } else if (i == R.id.ll_live_screen_record) {
            ARouter.getInstance().build(RouterActivityPath.WebRTC.PAGER_WEBRTC).navigation();
            //进入直播播放器
        } else if (i == R.id.ll_live_player) {
            ARouter.getInstance().build(RouterActivityPath.Weplayer.PAGER_PLAYER)
                    .withBoolean("isLive", true)
                    .navigation();
            //进入点播播放器
        } else if (i == R.id.ll_vod_player) {
            ARouter.getInstance().build(RouterActivityPath.Weplayer.PAGER_PLAYER)
                    .withBoolean("isLive", false)
                    .navigation();
        } else {
        }
    }

    private void doClick() {


        adapter.setOnItemClickListener(new HomePageAdapter.ItemClickListener() {
            @Override
            public void onClick(int position) {
                if (isClickRepeat) {
                    return;
                }
                isClickRepeat = true;
//                initNVSData();
                switch (types.get(position)) {
                    case "视频拍摄":
                        ARouter.getInstance().build(RouterActivityPath.Shortvideo.PAGER_KIT_SHORTVIDEO).navigation();
//                        if (initARSceneing){
//                            Bundle captureBundle = new Bundle();
//                            captureBundle.putBoolean("initArScene", arSceneFinished);
//                            ARouter.getInstance().build(RouterActivityPath.Shortvideo.PAGER_KIT_SHORTVIDEO)
//                                    .withBundle("initArScene",captureBundle)
//                                    .navigation();
//                        }else {
//                            isClickRepeat = false;
//                            ToastUtils.showShort(getResources().getString(R.string.initArsence));
//                        }

//                        AppManager.getInstance().jumpActivity(MainActivity.this, ShortVideoCaptureActivity.class, bundle);
                        break;
                    case "视频编辑":
//                        AppManager.getInstance().jumpActivity(MainActivity.this, SelectMediaActivity.class, editBundle);
                        break;
                    case "抖音特效":
                        ARouter.getInstance().build(RouterActivityPath.Shortvideo.PAGER_SHORTVIDEO).navigation();
//                        Bundle douyinBundle = new Bundle();
//                        douyinBundle.putBoolean(Constants.CAN_USE_ARFACE_FROM_MAIN, mCanUseARFace);
//                        AppManager.getInstance().jumpActivity(MainActivity.this, DouYinCaptureActivity.class, douyinBundle);
                        break;
                    case "粒子特效":
                        ARouter.getInstance().build(RouterActivityPath.Shortvideo.PAGER_SHORTVIDEO_PARTICLE).navigation();
//                        AppManager.getInstance().jumpActivity(MainActivity.this, ParticleCaptureActivity.class, null);
                        break;
                    case "虚拟背景抠像":
                        ARouter.getInstance().build(RouterActivityPath.Shortvideo.PAGER_SHORTVIDEO_SCENE).navigation();
//                        AppManager.getInstance().jumpActivity(MainActivity.this, CaptureSceneActivity.class, null);
                        break;
                    case "画中画编辑":
//                        AppManager.getInstance().jumpActivity(MainActivity.this, SelectMediaActivity.class, pipBundle);
                        break;
                    case "视频上传":
//                        Intent intent = new Intent(MainActivity.this, UploadActivity.class);
//                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void checkAllPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
                    if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            mPermissionGranted = true;
//                            if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE)) {
//                            } else {
//                                requestPermissions(new String[]{android.Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE_PERMISSION_CODE);
//                            }
                        } else {
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION_CODE);
                        }
                    } else {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_CODE);
                    }
                } else {
                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION_CODE);
                }
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_CODE);
            }
        } else {
            mPermissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION_CODE:
                if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
                    if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            mPermissionGranted = true;
                        } else {
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION_CODE);
                        }
                    } else {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_CODE);
                    }
                } else {
                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION_CODE);
                }
                break;
            case REQUEST_RECORD_AUDIO_PERMISSION_CODE:
                if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        mPermissionGranted = true;
                    } else {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION_CODE);
                    }
                } else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_CODE);
                }
                break;
            case REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_CODE:
                if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    mPermissionGranted = true;
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION_CODE);
                }
                break;
            case REQUEST_READ_EXTERNAL_STORAGE_PERMISSION_CODE:
                mPermissionGranted = true;
                break;
            default:
                break;
        }
    }

}
