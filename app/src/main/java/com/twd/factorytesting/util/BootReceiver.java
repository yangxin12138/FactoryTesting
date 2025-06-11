package com.twd.factorytesting.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.twd.factorytesting.MacTestService;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 下午3:22 11/6/2025
 */
public class BootReceiver extends BroadcastReceiver {
    String TAG = "BootReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: 接收到广播");
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            Log.i(TAG, "onReceive: 是开机完成广播");
            Intent serviceIntent = new Intent(context, MacTestService.class);
            context.startService(serviceIntent);
        }
    }
}
