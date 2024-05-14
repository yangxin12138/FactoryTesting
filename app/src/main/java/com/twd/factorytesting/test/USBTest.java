package com.twd.factorytesting.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 10:14 2024/4/25
 */
public class USBTest {

    private Context mContext;
    private Handler mHandler;

    public USBTest(Context mContext, Handler handler) {
        this.mContext = mContext;
        mHandler = handler;
    }

    public boolean isConnected(){
        UsbManager usbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        for (Map.Entry<String,UsbDevice> entry:deviceList.entrySet()){
            UsbDevice device = entry.getValue();
            //检测设备是否已经挂载到USB设备
            return usbManager.hasPermission(device);
        }
        return false;
    }

    public BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)){
                //USB插入
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                Log.d("USB", "USB device attached: " + device.getDeviceName());
                //TODO:更新MainActivity中的UI
                Message message = mHandler.obtainMessage(1,"In");
                mHandler.sendMessage(message);
            }else {
                // USB设备拔出
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                Log.d("USB", "USB device detached: " + device.getDeviceName());
                //TODO:更新MainActivity中的UI
                Message message = mHandler.obtainMessage(2,"Out");
                mHandler.sendMessage(message);
            }
        }
    };
}
