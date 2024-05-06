package com.twd.factorytesting.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.twd.factorytesting.test.USBTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 10:18 2024/4/30
 */
public class USBUtil {

    String wifiSSID;
    String passWord;
    public String usbFilePath;

    private Context mContext;

    public USBUtil(Context context){
        this.mContext = context;
    }

    public void getWifiInfo(){
        wifiSSID = "";
        passWord = "";

        try{
            File file = new File(usbFilePath+"/testInfo.txt");
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            String line;
            while ((line = br.readLine()) != null){
                if (line.contains("wifiSSID")){
                    wifiSSID = line.split("：")[1].trim();
                } else if (line.contains("password")) {
                    passWord = line.split("：")[1].trim();
                }
            }
            br.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.d("yangxin", "getWifiInfo: wifiSSID = " + wifiSSID + ",password = " + passWord);
    }

    /*public BroadcastReceiver UsbPathReceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)){
                //U盘根目录
                usbFilePath = intent.getData().getPath();
                if (!TextUtils.isEmpty(usbFilePath)){
                    Log.d("yangxin", "onReceive: U盘挂载："+usbFilePath);
                    getWifiInfo();
                    Message message = mHandler.obtainMessage(1,"Mounted");
                    mHandler.sendMessage(message);
                }
            }
        }
    };*/

    public String getUsbFilePath(){
        StorageManager storageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        if (storageManager != null){
            StorageVolume[] storageVolumes = storageManager.getStorageVolumes().toArray(new StorageVolume[0]);
            for (StorageVolume volume : storageVolumes){
                if (volume.isRemovable() && !volume.isPrimary()){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {//Android11以下的用反射
                        return volume.getDirectory().getAbsolutePath();
                    }
                }
            }
        }
        return "";//未挂载
    }


    public String getWifiSSID() {
        return wifiSSID;
    }

    public String getPassWord() {
        return passWord;
    }

}
