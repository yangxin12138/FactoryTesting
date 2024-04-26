package com.twd.factorytesting;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.twd.factorytesting.test.BluetoothTest;
import com.twd.factorytesting.test.HeadsetTest;
import com.twd.factorytesting.test.USBTest;
import com.twd.factorytesting.test.WifiTest;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WifiTest wifiTest;
    private BluetoothTest bleTest;
    private USBTest usbTest;
    private HeadsetTest headsetTest;
    private static final String TAG = "MainActivity";
    private TextView tv_deviceName;
    private TextView tv_deviceVersion;
    private TextView tv_wifiIp;
    private TextView tv_wifiName;
    private TextView tv_wifiResult;
    private TextView tv_blMac;
    private TextView tv_blDevice;
    private TextView tv_blResult;
    private TextView tv_keyResult;
    private TextView tv_usbName;
    private TextView tv_usbResult;
    private TextView tv_headsetName;
    private TextView tv_headsetResult;
    WifiManager wifiManager;
    BluetoothAdapter bluetoothAdapter;
    IntentFilter wifiFilter;
    IntentFilter bleFilter;
    IntentFilter usbFilter;
    IntentFilter headsetFilter;
    private List<BluetoothDevice> deviceList;

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String message = (String) msg.obj;
            if (message.equals("In")){
                tv_usbName.setText("已插入");
                tv_usbResult.setText("成功");
                tv_usbResult.setTextColor(Color.GREEN);
            } else if (message.equals("Out")) {
                tv_usbName.setText("已拔出");
            } else if (message.equals("plug")) {
                Log.d(TAG, "handleMessage: 耳机插入");
                tv_headsetName.setText("已检测到耳机");
                tv_headsetResult.setText("成功");
                tv_headsetResult.setTextColor(Color.GREEN);
            } else if (message.equals("unplug")) {
                Log.d(TAG, "handleMessage: 耳机拔出");
                tv_headsetName.setText("未检测到耳机");
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        deviceInfo();
        wifiInit();
        bleInit();
        tv_keyResult = findViewById(R.id.key_result);
        usbInit();
        headsetInit();
    }

    /*
    * 耳机测试*/
    private void headsetInit(){
        headsetTest = new HeadsetTest(this,mHandler);
        tv_headsetName = findViewById(R.id.headset_name);
        tv_headsetResult = findViewById(R.id.headset_result);
        headsetFilter = new IntentFilter();
        headsetFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        if (headsetTest.isWired()){
            tv_headsetName.setText("未检测到耳机");
        }else {
            tv_headsetName.setText("已检测到耳机");
            tv_headsetResult.setText("成功");
            tv_headsetResult.setTextColor(Color.GREEN);

        }
    }

    /*
    * USB测试*/
    private void usbInit(){
        usbTest = new USBTest(this,mHandler);
        tv_usbName = findViewById(R.id.USB_name);
        tv_usbResult = findViewById(R.id.USB_result);
        usbFilter = new IntentFilter();
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        if (usbTest.isConnected()){
            tv_usbName.setText("已插入");
            tv_usbResult.setText("成功");
            tv_usbResult.setTextColor(Color.GREEN);
        }
    }

    /*
    * 设备信息*/
    private void deviceInfo(){
        String deviceName = Build.MANUFACTURER + " " + Build.MODEL;
        String androidVersion = Build.VERSION.RELEASE;
        tv_deviceName = findViewById(R.id.device_name);
        tv_deviceVersion = findViewById(R.id.device_version);
        tv_deviceName.setText("设备名称:" + deviceName);
        tv_deviceVersion.setText("Android版本:"+androidVersion);
    }

    /*
    * 按键测试*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
            tv_keyResult.setText("成功");
            tv_keyResult.setTextColor(Color.GREEN);
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    /*
    * 蓝牙测试*/
    private void bleInit() {
        deviceList = new ArrayList<>();
        bleTest = new BluetoothTest(this);
        tv_blMac = findViewById(R.id.bluetooth_mac);
        tv_blDevice = findViewById(R.id.bluetooth_device);
        tv_blResult = findViewById(R.id.bluetooth_result);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bleFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        if (!bleTest.isSupport()) {
            tv_blMac.setText("不支持蓝牙");
            tv_blResult.setText("失败");
            tv_blResult.setTextColor(Color.RED);
            return;
        } else {
            bleTest.startBluetoothScan();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //停止扫描
                    if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        return;}
                    bluetoothAdapter.cancelDiscovery();
                    unregisterReceiver(bleReceiver);
                    tv_blDevice.setText("搜索到"+deviceList.size()+"个设备");
                    tv_blResult.setText("成功");
                    tv_blResult.setTextColor(Color.GREEN);
                    tv_blMac.setText(bluetoothAdapter.getAddress());
                }
            },4000);
        }

    }

    /*
    * wifi测试*/
    private void wifiInit() {
        wifiTest = new WifiTest(this);
        tv_wifiIp = findViewById(R.id.ip_address);
        tv_wifiName = findViewById(R.id.wifi_name);
        tv_wifiResult = findViewById(R.id.wifi_result);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        tv_wifiIp.setText("IP:" + wifiTest.getIpAddress(wifiInfo));
        tv_wifiName.setText(wifiTest.getSSID(wifiInfo));
        wifiFilter = new IntentFilter();
        wifiFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        String ssid = "WiFi_2.4";
        String password = "kkkkkkkk";
        if (!wifiTest.isConnect()) {
            Log.d(TAG, "onCreate: 没连wifi，给连上");
            wifiTest.connectToWifi(ssid, password);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (wifiTest.getCurrentWifiSsid(wifiManager).equals(ssid)) {
                        tv_wifiResult.setText("成功");
                        tv_wifiResult.setTextColor(Color.GREEN);
                    } else {
                        tv_wifiResult.setText("失败");
                        tv_wifiResult.setTextColor(Color.RED);
                    }
                }
            }, 4000);
        } else {
            tv_wifiResult.setText("成功");
            tv_wifiResult.setTextColor(Color.GREEN);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(wifiReceiver);
        unregisterReceiver(bleReceiver);
        unregisterReceiver(usbTest.usbReceiver);
        unregisterReceiver(headsetTest.headsetReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(wifiReceiver, wifiFilter);
        registerReceiver(bleReceiver, bleFilter);
        registerReceiver(usbTest.usbReceiver, usbFilter);
        registerReceiver(headsetTest.headsetReceiver,headsetFilter);
    }

    private final BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (networkInfo != null && networkInfo.isConnected()) {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    //wifi已连接
                    tv_wifiIp.setText("IP:" + Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
                    tv_wifiName.setText(wifiTest.getSSID(wifiInfo));
                } else {
                    //wifi断开
                    tv_wifiIp.setText("IP:0.0.0.0");
                    tv_wifiName.setText("null");
                }
            }
        }
    };

    private final BroadcastReceiver bleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceList.add(device);
            }
        }
    };
}