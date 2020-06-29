package com.anber.work.ui.activity.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.anber.work.R;
import com.goldze.base.router.RouterActivityPath;
import com.goldze.base.utils.FileUtils;
import com.goldze.base.utils.MediaUtils;
import com.goldze.base.utils.ToastUtils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 使用AudioRecord录制音频
 * 1. AudioRecord通过read()可以获取音频，对音频处理后，保存，弥补了MediaRecord不能对音频数据进行处理的不足
 * 2. AudioRecord录制的为PCM数据，PCM封装为mp3
 */
//@Route(path = RouterActivityPath.DrawBmp.PAGER_DRAWBMP)
public class AudioRecorderActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mBtnRecord;
    private Button mBtnAudioTrack;
    private Button mBtnMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);
        mBtnRecord = (Button) findViewById(R.id.btn_audio_record_record);
        mBtnAudioTrack = (Button) findViewById(R.id.btn_audio_record_audio_track_play);
        mBtnMediaPlayer = (Button) findViewById(R.id.btn_audio_record_media_player_play);
        mBtnRecord.setOnClickListener(this);
        mBtnAudioTrack.setOnClickListener(this);
        mBtnMediaPlayer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Button btn = (Button) v;
        if (id == R.id.btn_audio_record_record) {
            if (TextUtils.equals(btn.getText(), "录制")) {
                btn.setText("停止");
                startRecord();
            } else {
                btn.setText("录制");
                stopRecord();
            }
        } else if (id == R.id.btn_audio_record_audio_track_play) {
            playWithAudioTrack();
        } else if (id == R.id.btn_audio_record_media_player_play) {
            playWithMediaPlayer();
        }
    }

    /*****************AudioRecord使用的参数**************************/
    enum Status { //录音的状态
        RUNNING, //正在录音
        END //录音停止
    }

    private AudioRecord mAudioRecord;
    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
    private final static int AUDIO_SAMPLE_RATE = 44100;    // 采样率44100是目前的标准，但是某些设备仍然支持22050，16000，11025， 采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
    private final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_STEREO;// 音频通道，双通道
    private final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT; // 音频格式：PCM编码，16位，这里的值是编号，写入wav不要搞混了
    private int mBufferSizeInBytes = 0;// 缓冲区大小：缓冲区字节大小，使用AudioRecord.getMinBufferSize，获取
    private File mPCMFile;
    private String mPCMPath; //pcm存储地址
    private String mWavString;
    private Status mStatus = Status.END;
    FileOutputStream fos;

    /**
     * 使用AudioRecord录制音频
     * 1. 设置参数
     * 2. 开始录制
     * 3. 停止录制
     * 4. 转换为wav格式，同时存在pcm，wav两种文件
     * blog:
     * http://www.jianshu.com/p/90c4071c7768 AudioRecord基本使用
     * http://www.jianshu.com/p/af7787b409a2，声音中采样位数，采样频率，回声，降噪的概念，回声消除
     * http://www.jianshu.com/p/bee958826a9e，AudioTrack源码分析
     */
    private void startRecord() {
        mBufferSizeInBytes = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING); //获取默认最小缓冲区大小
        mAudioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING, mBufferSizeInBytes); //初始化
        mAudioRecord.startRecording(); //开始录音
        new Thread(new Runnable() { //录音转存为pcm文件需要在子线程
            @Override
            public void run() { //需要加入Status，控制状态，处理
                byte[] audioData = new byte[mBufferSizeInBytes]; //可以暂停录音，中间文件保存在这里，生成多个文件，最后再合成所有录音：http://blog.csdn.net/imhxl/article/details/52190451
                mPCMPath = FileUtils.createFilePath("pcm", 1);
                mPCMFile = new File(mPCMPath);
                try {
                    fos = new FileOutputStream(mPCMFile); //创建输出文件流
                    int readSize = 0;
                    mStatus = Status.RUNNING;
                    while (mStatus == Status.RUNNING) { //正在录音
                        readSize = mAudioRecord.read(audioData, 0, mBufferSizeInBytes); //从缓冲区中读数据
                        if (AudioRecord.ERROR_INVALID_OPERATION != readSize && fos != null) {
                            fos.write(audioData); //数据保存到文件，同时这里可以对数据进行处理，例如推流，变声等
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close(); //关闭流
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    private String mOutputPath;

    /**
     * 停止录制音频
     * 1. 可以对录音文件进行转换，pcm->wav
     * 2. 如果有暂停录音，对多份录音文件进行合并
     */
    private void stopRecord() {
        mAudioRecord.stop();
        mStatus = Status.END; //设置状态，正在录音的循环会终止
        if (mAudioRecord != null) {
            mAudioRecord.release();
            mAudioRecord = null;
        }
        new Thread(new Runnable() {  //pcm->wav
            @Override
            public void run() {
                mOutputPath = FileUtils.createCommonDir() + "AUD_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".wav";
                MediaUtils.pcm2wav(
                        44100,
                        16,
                        2,
                        mBufferSizeInBytes,
                        mPCMFile.getAbsolutePath(),
                        mOutputPath);
            }
        }).start();
    }

    /**
     * 使用AudioTrack播放pcm音频
     */
    private void playWithAudioTrack() {
        if (mBufferSizeInBytes == 0) {
            ToastUtils.show(this, "先录制，才能播放");
            return;
        }
        final AudioTrack audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                44100,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                mBufferSizeInBytes,
                AudioTrack.MODE_STREAM);
        //读取pcm文件
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataInputStream dis = null;
                try {
                    dis = new DataInputStream(new FileInputStream(mPCMPath));
                    byte[] buffer = new byte[mBufferSizeInBytes]; //设置读取缓冲区
                    int length;
                    while ((length = dis.read(buffer, 0, buffer.length)) > 0) {
                        audioTrack.write(buffer, 0, length);
                        audioTrack.play();
                    }
                    audioTrack.stop();
                    audioTrack.release();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (dis != null) {
                        try {
                            dis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * 使用MediaPlayer播放wav音频
     */
    private void playWithMediaPlayer() {
        if (!TextUtils.isEmpty(mOutputPath)) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(mOutputPath);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare(); //准备播放
                mediaPlayer.start();//开始播放
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
