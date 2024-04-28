package com.twd.factorytesting.test;

import android.content.Context;
import android.media.SoundPool;
import android.util.Log;

import com.twd.factorytesting.R;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 16:24 2024/4/26
 */
public class SpeakTest {

    private Context mContext;
    SoundPool soundPool;
    int soundId;
    public SpeakTest(Context mContext) {
        this.mContext = mContext;
    }

    public void playMusic(){
        //创建SoundPool对象
        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)
                .build();
        //加载音频
        soundId = soundPool.load(mContext, R.raw.ringtone001,1);

        //在加载完成后播放音频，并设置循环播放
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                // 第一个参数是soundID
                // 第二个参数和第三个参数分别是左右声道的音量
                // 第四个参数是优先级，目前没有作用，传入0即可
                // 第五个参数是循环次数，-1表示无限循环
                // 第六个参数是播放速率，1.0是正常播放，范围0.5到2.0
                soundPool.play(soundId,0.3f,0.3f,0,-1,1f);
            }
        });
    }

    public void stop(){
        Log.d("yangxin", "stop: 音频停止了");
        soundPool.stop(soundId);
        soundPool.release();
    }
}
