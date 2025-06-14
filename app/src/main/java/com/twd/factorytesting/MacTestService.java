package com.twd.factorytesting;

import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.twd.factorytesting.test.WifiTest;
import com.twd.factorytesting.util.USBUtil;
import com.twd.factorytesting.util.Utils;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 下午3:08 27/5/2025
 */
public class MacTestService extends Service {
    private final static String TAG = MainActivity.class.getSimpleName();
    private Context context;
    private Handler mainHandler;
    private WindowManager windowManager;
    private View macErrorView;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        mainHandler = new Handler(Looper.getMainLooper());
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("MacTestService", "onStartCommand: MAC检测服务启动");
        new Handler(Looper.getMainLooper()).postDelayed(this::isMacVerify, 5000);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void isMacVerify(){
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String macAddress = wifiInfo.getMacAddress();
        if(macAddress!=null){
            macAddress =macAddress.toUpperCase();
            Log.i("MacTestService", "isMacVerify: MAC ="+macAddress);
            String[] parts = macAddress.split(":");
            String[] macAddr = Utils.readSystemProp("MAC_VALID_ADDR").split(":");
            if (parts.length >= 3 && parts[0].equals(macAddr[0]) && parts[1].equals(macAddr[1]) && parts[2].equals(macAddr[2])){
                Log.i("MacTestService", "isMacVerify: MAC合法" + macAddress);
            }else {
                String verify = Utils.readSystemProp("MAC_VALID_CHECK");
                Log.i("MacTestService", "isMacVerify: verify = "+ verify + ",macAddress = " + macAddress);
                if (verify.equals("true")){
                    Log.i("MacTestService", "isMacVerify: 显示错误窗口");
                    showMacErrorDialog("MAC ERROR");
                }
            }
        }
    }

    private  void  showMacErrorDialog(String message){
        // 移除已存在的窗口（如果有）
        removeMacErrorWindow();
        // 加载布局
        macErrorView = LayoutInflater.from(context).inflate(R.layout.dialog_layout, null);
        TextView tvMessage = macErrorView.findViewById(R.id.error_message);
        tvMessage.setText(message);

        // 添加返回键监听
        macErrorView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    removeMacErrorWindow();
                    return true;
                }
                return false;
            }
        });

        // 让视图可以接收按键事件
        macErrorView.setFocusableInTouchMode(true);
        macErrorView.requestFocus();
        // 设置窗口参数
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, // 移除FLAG_NOT_FOCUSABLE
                PixelFormat.TRANSLUCENT
        );

        // 设置窗口位置
        params.gravity = Gravity.CENTER;
        params.y = 0; // 垂直居中
        params.x = 0; // 水平居中

        // 添加窗口
        windowManager.addView(macErrorView, params);
    }

    private void removeMacErrorWindow() {
        if (macErrorView != null && windowManager != null) {
            try {
                windowManager.removeView(macErrorView);
            } catch (Exception e) {
                Log.e(TAG, "移除窗口失败: " + e.getMessage());
            }
            macErrorView = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeMacErrorWindow();
        Log.i(TAG, "服务已停止");
    }
}
