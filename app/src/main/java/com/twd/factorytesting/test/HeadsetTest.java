package com.twd.factorytesting.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 17:03 2024/4/25
 */
public class HeadsetTest {
    private Context mContext;
    private Handler mHandler;

    public HeadsetTest(Context mContext,Handler handler) {
        this.mContext = mContext;
        mHandler = handler;
    }

    public BroadcastReceiver headsetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_HEADSET_PLUG)){
                int state = intent.getIntExtra("state",-1);
                switch (state){
                    case 0:
                       //耳机未插入
                        Log.i("HeadsetTest", "onReceive: HeadsetTest --- 耳机未插入");
                       Message message = mHandler.obtainMessage(3,"plug");
                       mHandler.sendMessage(message);
                       break;
                    case 1:
                       //耳机插入
                        Log.i("HeadsetTest", "onReceive: HeadsetTest --- 耳机插入");
                       Message message2 = mHandler.obtainMessage(4,"unplug");
                       mHandler.sendMessage(message2);
                       break;
                    default:
                       break;
                }
            }
        }
    };

    public boolean isWired(){
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        return audioManager.isWiredHeadsetOn();
    }
}
