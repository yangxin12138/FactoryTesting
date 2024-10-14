package com.twd.factorytesting.test;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.List;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 10:46 2024/4/23
 */
public class WifiTest {
    private Context mContext;
    private WifiManager wifiManager;
    private WifiInfo wifiInfo;
    private final static String TAG = "WifiTest";

    public WifiTest(Context context) {
        this.mContext = context;
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        Log.d(TAG, "WifiTest: 初始化");
        //判断wifi是否已经打开
        if (!wifiManager.isWifiEnabled()) {
            //如果wifi未打开则打开它
            wifiManager.setWifiEnabled(true);
        }
    }

    public String getSSID(WifiInfo wifiInfo) {
        this.wifiInfo = wifiInfo;
        return wifiInfo.getSSID().trim().replace("\"", "");
    }

    public int getRssi(WifiInfo wifiInfo) {
        this.wifiInfo = wifiInfo;
        return wifiInfo.getRssi();
    }

    public String getIpAddress(WifiInfo wifiInfo) {
        this.wifiInfo = wifiInfo;
        int ipAddress = wifiInfo.getIpAddress();
        return Formatter.formatIpAddress(ipAddress);
    }

    public void connectToWifi(String ssid, String password) {
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = "\"" + ssid + "\"";
        wifiConfiguration.preSharedKey = "\"" + password + "\"";

        //如果wifi已启用，请禁用它以确保连接新网络
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
        //添加并启用网络配置
        int networkId = wifiManager.addNetwork(wifiConfiguration);
        wifiManager.enableNetwork(networkId, true);
        //重新启用wifi
        wifiManager.setWifiEnabled(true);
    }

    public String getCurrentWifiSsid(WifiManager wifiManager) {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid;
        if (wifiInfo != null && wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            ssid = wifiInfo.getSSID().replace("\"", "");
        } else {
            ssid = "默认网络";
        }
        return ssid;
    }

    public boolean isConnect() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            Log.d(TAG, "isConnect: 网络已连接");
            return true;
        } else {
            Log.d(TAG, "isConnect: 网络未连接");
            return false;
        }
    }

    public int getWifiNum(WifiManager wifiManager) {
        if (!wifiManager.isWifiEnabled()) {
            // 如果 Wi-Fi 未开启，无法扫描热点
            Log.d(TAG, "getWifiNum: wifi未开启");
            return 0;
        }
        final int[] count = {0};
        mContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;}
                    List<ScanResult> scanResults = wifiManager.getScanResults();
                    count[0] = scanResults.size();
                    Log.i(TAG, "扫描到的 Wi-Fi 热点个数：" + count[0]);
                    context.unregisterReceiver(this);
                }
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wifiManager.startScan();
        return count[0];
    }

    public String convertMacToUpperCase() {
        wifiInfo = wifiManager.getConnectionInfo();
        String macAddress = wifiInfo.getMacAddress();
        if(macAddress!=null){
            return macAddress.toUpperCase();
        }else {
            return null;
        }
    }
}
