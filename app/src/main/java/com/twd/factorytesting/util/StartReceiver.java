package com.twd.factorytesting.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.twd.factorytesting.MainActivity;

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
            /*String usbPath = intent.getData().getPath();
            if (!TextUtils.isEmpty(usbPath)){
                Intent localIntent = new Intent("connect_internet");
                localIntent.putExtra("path",usbPath);
                context.sendBroadcast(localIntent);
            }*/

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.i("StartReceiver", "run: ---重启咯---");
                    Intent restartIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                    if (restartIntent != null){
                        restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(restartIntent);
                    }
                }
            },4000);
        }
    }
}
