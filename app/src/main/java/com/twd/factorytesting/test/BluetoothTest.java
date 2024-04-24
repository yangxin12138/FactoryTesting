package com.twd.factorytesting.test;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 9:50 2024/4/24
 */
public class BluetoothTest {
    private Context mContext;
    BluetoothAdapter bluetoothAdapter;


    public BluetoothTest(Context mContext) {
        this.mContext = mContext;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean isSupport(){
       return bluetoothAdapter != null;
    }


    public void startBluetoothScan(){
        if (!bluetoothAdapter.isEnabled()){
            //蓝牙未打开
            bluetoothAdapter.enable();//打开蓝牙
        }
        while (!bluetoothAdapter.isEnabled()){
            try{
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        //检查是否已经在扫描
        checkPermission(Manifest.permission.BLUETOOTH_SCAN);
        if (bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
        }
        //开始扫描
        bluetoothAdapter.startDiscovery();

    }

    public void stopScan(){
        checkPermission(Manifest.permission.BLUETOOTH_SCAN);
        if (bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
        }
    }

    private void checkPermission(String Permission) {
        if (ActivityCompat.checkSelfPermission(mContext, Permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Permission}, 1);
        }
    }


}
