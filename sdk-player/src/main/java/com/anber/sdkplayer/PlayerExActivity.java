package com.anber.sdkplayer;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.goldze.base.utils.MyPermissionUtils;
import com.goldze.base.filechooser.FileChooser;

public class PlayerExActivity extends AppCompatActivity {

    private EditText editText;
    private boolean isLive;
    private static final int REQUEST_CODE_CHOOSE = 23;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playerex);
        requestPermissins(new MyPermissionUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied(String[] deniedPermissions) {
                Toast.makeText(PlayerExActivity.this, "未获取到存储权限", Toast.LENGTH_SHORT).show();
            }
        });
        editText = findViewById(R.id.et);
        ((RadioGroup) findViewById(R.id.rg)).setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.vod) {
                isLive = false;
            } else if (checkedId == R.id.live) {
                isLive = true;
            }
        });

        findViewById(R.id.secfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requestPermissins(new MyPermissionUtils.OnPermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        FileChooser fileChooser = new FileChooser(PlayerExActivity.this, new FileChooser.FileChoosenListener() {
                            @Override
                            public void onFileChoosen(String filePath) {
                                editText.setText(filePath);
                            }
                        });
                        fileChooser.setBackIconRes(R.drawable.back_white);
                        fileChooser.setTitle("选择文件路径");
                        fileChooser.setDoneText("确定");
                        fileChooser.setThemeColor(R.color.colorAccent);
                        fileChooser.open();
                    }

                    @Override
                    public void onPermissionDenied(String[] deniedPermissions) {
                        Toast.makeText(PlayerExActivity.this, "未获取到存储权限", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.close_float_window) {
//            PIPManager.getInstance().stopFloatWindow();
//                PIPManager.getInstance().reset();
        } else if (itemId == R.id.clear_cache) {
//                if (VideoCacheManager.clearAllCache(this)) {
//                    Toast.makeText(this, "清除缓存成功", Toast.LENGTH_SHORT).show();
//                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void playOther(View view) {
        String url = editText.getText().toString();
        if (TextUtils.isEmpty(url)) return;
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("isLive", isLive);
        startActivity(intent);
    }

    private void requestPermissins(MyPermissionUtils.OnPermissionListener mOnPermissionListener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mOnPermissionListener.onPermissionGranted();
            return;
        }
        String[] permissions = { "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
        MyPermissionUtils.requestPermissions(this, 0
                , permissions, mOnPermissionListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MyPermissionUtils.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    public void clearUrl(View view) {
        editText.setText("");
    }
}