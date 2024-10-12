package com.twd.factorytesting.util;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.twd.factorytesting.MainActivity;

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
        if (action != null && action.equals(Intent.ACTION_MEDIA_MOUNTED)){
            String usbpath  = new USBUtil(context).getUsbFilePath();
            Log.i("StartReceiver", "run: usbpath = " + usbpath);
            if (!usbpath.isEmpty()){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("StartReceiver", "run: ---重启咯---");
                        Intent restartIntent = new Intent();
                        String packageName = "com.twd.factorytesting";
                        String className = "com.twd.factorytesting.MainActivity";
                        restartIntent.setClassName(packageName, className);
                        restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(1);
                        if (runningTasks!= null && runningTasks.size() > 0) {
                            Log.i("StartReceiver", "run: activityManager h和 runningTasks 不是空，且有运行程序");
                            ActivityManager.RunningTaskInfo topTask = runningTasks.get(0);
                            if (!topTask.topActivity.getClassName().equals(className)) {
                                Log.i("StartReceiver", "顶层程序不是我");
                                try {
                                    context.startActivity(restartIntent);
                                    Log.i("StartReceiver", "启动成功");
                                } catch (Exception e) {
                                    Log.e("StartReceiver", "启动失败：" + e.getMessage());
                                }
                            } else {
                                Log.i("StartReceiver", "目标 Activity 已在前台，无需启动");
                            }
                        } else {
                            try {
                                context.startActivity(restartIntent);
                                Log.i("StartReceiver", "启动成功");
                            } catch (Exception e) {
                                Log.e("StartReceiver", "启动失败：" + e.getMessage());
                            }
                        }
                    }
                },1000);
            }
        }
    }
}
