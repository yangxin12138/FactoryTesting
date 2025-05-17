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
    String verify;
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

    public void getMacVerify(){
        verify = "";
        try {
            File file = new File(usbFilePath + "/testInfo.txt");
            if (!file.exists()) {
                Log.e("yangxin", "File does not exist.");
                return;
            }
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = br.readLine())!= null) {
                if (line.contains("mac_verify")) {
                    String[] parts = line.split("：");
                    if (parts.length == 2) {
                        verify = parts[1].trim();
                    }
                }
            }
            br.close();
        } catch (Exception e) {
            Log.e("yangxin", "Failed to read file: " + e.getMessage());
            e.printStackTrace();
        }
    }


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

    public boolean checkUsbFolderExist(String folderName){//“COPY_FILE”
        if (TextUtils.isEmpty(folderName)){
            return false;
        }
        // 获取U盘根路径
        String usbRootPath = getUsbFilePath();
        if (TextUtils.isEmpty(usbRootPath)) {
            Log.e("UsbCopyFileService", "未找到可移动存储设备");
            return false;
        }
        // 构建完整路径
        String targetPath = usbRootPath + File.separator + folderName;
        Log.d("UsbCopyFileService", "检查路径: " + targetPath);
        // 检查文件夹是否存在
        File folder = new File(targetPath);
        return folder.exists() && folder.isDirectory();
    }


    public String getWifiSSID() {
        return wifiSSID;
    }

    public String getPassWord() {
        return passWord;
    }

    public String getVerify() {
        return verify;
    }

}
