package com.goldze.base.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by baoqingyang on 2018/3/8.
 */

public class RequestPermission {


    public static final String PERMISSION_FILE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String PERMISSION_RECORD = Manifest.permission.RECORD_AUDIO;
    public static final String PERMISSION_READ = Manifest.permission.READ_EXTERNAL_STORAGE;


    private static final String[] requestPermissions = {

            PERMISSION_FILE,
            PERMISSION_RECORD,
            PERMISSION_READ
    };

    public RequestPermission(){

    }
    interface PermissionGrant {
        void onPermissionGranted(int requestCode);
    }

    public void  RequestPermission(Activity activity){
        String[] permissions;
        List<String> mPermissionList = new ArrayList<>();

        mPermissionList.clear();
        for (int i = 0; i < requestPermissions.length; i++) {
            if (ContextCompat.checkSelfPermission(activity, requestPermissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(requestPermissions[i]);
            }
        }

        if (mPermissionList.isEmpty()) {//未授予的权限为空，表示都授予了

        } else {//请求权限方法
            permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
            ActivityCompat.requestPermissions(activity, permissions, 1);
        }
        String res = getProperty("CAPTURE_AUDIO_OUTPUT","1111");
        Log.d("show",res);

    }
    public  String getProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String)(get.invoke(c, key, "unknown" ));
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return value;
        }
    }
    public static  void setprop(String key, String defaultValue) {

        String value = defaultValue;

        try {

            Class<?> c = Class.forName("android.os.SystemProperties");

            Method get = c.getMethod("set", String.class, String.class );

            get.invoke(c, key, value );

        } catch (Exception e) {

            e.printStackTrace();

        }
    }


}