package com.twd.factorytesting.test;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

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
        if (!wifiManager.isWifiEnabled()){
            //如果wifi未打开则打开它
            wifiManager.setWifiEnabled(true);
        }
    }

    public String getSSID(WifiInfo wifiInfo){
        this.wifiInfo = wifiInfo;
        return wifiInfo.getSSID().trim().replace("\"","");
    }

    public int getRssi(WifiInfo wifiInfo){
        this.wifiInfo = wifiInfo;
        return wifiInfo.getRssi();
    }

    public String getIpAddress(WifiInfo wifiInfo){
        this.wifiInfo = wifiInfo;
        int ipAddress = wifiInfo.getIpAddress();
        return Formatter.formatIpAddress(ipAddress);
    }

    public void connectToWifi(String ssid,String password){
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = "\""+ssid+"\"";
        wifiConfiguration.preSharedKey = "\""+password+"\"";

        //如果wifi已启用，请禁用它以确保连接新网络
        if (wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(false);
        }
        //添加并启用网络配置
        int networkId = wifiManager.addNetwork(wifiConfiguration);
        wifiManager.enableNetwork(networkId,true);
        //重新启用wifi
        wifiManager.setWifiEnabled(true);
    }
    public String getCurrentWifiSsid(WifiManager wifiManager){
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid;
        if (wifiInfo != null && wifiInfo.getSupplicantState() == SupplicantState.COMPLETED){
            ssid = wifiInfo.getSSID().replace("\"","");
        }else {
            ssid = "默认网络";
        }
        return ssid;
    }
    public boolean isConnect(){
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo!=null && networkInfo.isConnected() && networkInfo.getType()== ConnectivityManager.TYPE_WIFI){
            Log.d(TAG, "isConnect: 网络已连接");
            return true;
        }else {
            Log.d(TAG, "isConnect: 网络未连接");
            return false;
        }
    }
}
