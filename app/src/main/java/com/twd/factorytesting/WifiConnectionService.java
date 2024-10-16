package com.twd.factorytesting;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.twd.factorytesting.test.WifiTest;
import com.twd.factorytesting.util.ToastUtil;
import com.twd.factorytesting.util.USBUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 13:59 2024/10/15
 */
public class WifiConnectionService extends Service {

    private static final String TAG = "WifiConnectionService";
    private static WifiConnectionService instance;
    private String wifiSSID;
    private String password;
    private boolean onlyConnectWiFi;
    private WifiTest wifiTest;
    private USBUtil usbUtil;
    private WifiManager wifiManager;
    private Context context;
    boolean isSuccess = false;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        instance = this;
    }

    // 提供一个静态方法来获取服务实例
    public static WifiConnectionService getInstance() {
        return instance;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //showToast("服务启动");
        try {
            // 读取文件内容
            String usbpath = new USBUtil(this).getUsbFilePath();
            File file = new File(usbpath + "/testInfo.txt");
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                String line;
                wifiSSID = "";
                password = "";
                onlyConnectWiFi = false;
                while ((line = reader.readLine())!= null) {
                    if (line.contains("wifiSSID：")) {
                        wifiSSID = line.substring("wifiSSID：".length());
                    } else if (line.contains("password：")) {
                        password = line.substring("password：".length());
                    } else if (line.contains("Only_ConnectWiFi：")) {
                        onlyConnectWiFi = Integer.parseInt(line.substring("Only_ConnectWiFi：".length())) == 1;
                    }
                }
                reader.close();
                fis.close();
            }else { Log.i(TAG, "onStartCommand: 读取不存在");
                    stopSelf();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (onlyConnectWiFi) {
            connectToWifi(wifiSSID,password);
        }else {
            Log.i(TAG, "onStartCommand: 配置文件为 0 ，不连接");
            stopSelf();
        }
        return START_NOT_STICKY;
    }
    private void connectToWifi(String ssid,String password) {
        Log.i(TAG, "showToast: connectToWifi");
        // 这里是你的连接 Wi-Fi 的逻辑
        usbUtil = new USBUtil(context);
        usbUtil.usbFilePath = usbUtil.getUsbFilePath();
        wifiTest = new WifiTest(context);
        if (ssid!=null&&password!=null){
            Log.i(TAG, "connectToWifi: ssid和password都不是空");
            wifiTest.connectToWifi(ssid, password);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isSuccess = wifiTest.getCurrentWifiSsid(wifiManager).equals(ssid);
                    Log.i(TAG, "run: isSuccess === " + isSuccess);
                    if (isSuccess){
                        ToastUtil.showCustomToast(context,"Wifi连接成功",Toast.LENGTH_SHORT);
                    }else {
                        ToastUtil.showCustomToast(context,"Wifi连接错误，检查后重试",Toast.LENGTH_SHORT);
                    }
                    stopSelf();
                }
            },6000);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: 我滴任务完成了");
        super.onDestroy();
    }
}
