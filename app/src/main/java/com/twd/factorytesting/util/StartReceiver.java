package com.twd.factorytesting.util;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.twd.factorytesting.MainActivity;
import com.twd.factorytesting.WifiConnectionService;
import com.twd.factorytesting.test.WifiTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 11:33 2024/5/9
 */
public class StartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i("WifiConnectionService", "onReceive: 接收到广播");
        if (action!= null && action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
            Log.i("WifiConnectionService", "onReceive: 广播类型正确");
            if (WifiConnectionService.getInstance() == null) {
                Log.i("WifiConnectionService", "onReceive: 服务没有实例");
                Intent serviceIntent = new Intent(context, WifiConnectionService.class);
                context.startService(serviceIntent);
            }else {
                Log.i("WifiConnectionService", "onReceive: 服务有实例所以不启动服务");
            }
            /*Intent serviceIntent = new Intent(context, WifiConnectionService.class);
            context.startService(serviceIntent);*/
        }
    }
}
