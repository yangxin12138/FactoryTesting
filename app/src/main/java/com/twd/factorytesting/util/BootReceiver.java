package com.twd.factorytesting.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
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

                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                boolean isWiFiEnabled = wifiManager.isWifiEnabled();
                boolean verify = Boolean.parseBoolean(Utils.readSystemProp("MAC_TEST_LAUNCHER").trim());
                if (verify){
                    if(isWiFiEnabled) {
                        Log.i(TAG, "onReceive: WiFi已启用，启动服务");
                        Intent serviceIntent = new Intent(context, MacTestService.class);
                        context.startService(serviceIntent);
                    }else {
                        Log.i(TAG, "onReceive: WiFi未开启，不启动服务");
                    }
                }
        }
    }
}
