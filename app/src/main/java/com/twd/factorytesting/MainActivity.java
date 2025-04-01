package com.twd.factorytesting;


import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.media.tv.TvContract;
import android.media.tv.TvView;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.twd.factorytesting.test.BluetoothTest;
import com.twd.factorytesting.test.GsensorTest;
import com.twd.factorytesting.test.HeadsetTest;
import com.twd.factorytesting.test.MotorTest;
import com.twd.factorytesting.test.SpeakTest;
import com.twd.factorytesting.test.USBTest;
import com.twd.factorytesting.test.WifiTest;
import com.twd.factorytesting.util.StorageUtils;
import com.twd.factorytesting.util.USBUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WifiTest wifiTest;
    private BluetoothTest bleTest;
    private USBTest usbTest;
    private HeadsetTest headsetTest;
    private SpeakTest speakTest;
    private static final String TAG = "MainActivity";
    private TextView tv_deviceName;
    private TextView tv_deviceVersion;
    private TextView tv_wifiIp;
    private TextView tv_wifiName;
    private TextView tv_wifiResult;
    private TextView tv_wifiMac;
    private TextView tv_blMac;
    private TextView tv_blDevice;
    private TextView tv_blResult;
    private TextView tv_keyResult;
    private TextView tv_usbName;
    private TextView tv_usbResult;
    private TextView tv_headsetName;
    private TextView tv_headsetResult;
    private TvView hdmiView;
    private TextView tv_softwareNo;
    private TextView tv_gsensor;
    private TextView tv_gsensor_result;
    private TextView wifi_num;
    private TextView rom_size;
    private TextView ram_size;
    private TextView softwareTime_text;
    private ImageView picView;
    WifiManager wifiManager;
    BluetoothAdapter bluetoothAdapter;
    IntentFilter wifiFilter;
    IntentFilter bleFilter;
    IntentFilter usbFilter;
    IntentFilter diskFilter;
    IntentFilter headsetFilter;
    private List<BluetoothDevice> deviceList;
    USBUtil usbUtil;
    GsensorTest gsensorTest;
    private boolean isImageViewVisible = false;
    private int currentImageIndex = 0;
    private int[] imageResources = {R.drawable.white_1080,R.drawable.black_1080,R.drawable.text_1080,R.drawable.rainbow,R.drawable.color_1080};

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String message = (String) msg.obj;
            if (msg.what == 1){
                tv_usbName.setText("已插入");
                tv_usbResult.setText("成功");
                tv_usbResult.setTextColor(Color.GREEN);
            } else if (msg.what == 2) {
                tv_usbName.setText("已拔出");
            } else if (msg.what == 3) {
                Log.d(TAG, "handleMessage: 耳机拔出");
                tv_headsetName.setText("未检测到耳机");
            } else if (msg.what == 4) {
                Log.d(TAG, "handleMessage: 耳机插入");
                tv_headsetName.setText("已检测到耳机");
                tv_headsetResult.setText("成功");
                tv_headsetResult.setTextColor(Color.GREEN);
            } else if (msg.what == 5) {
                //Log.i(TAG, "handleMessage: Gsensor测试回调");
                tv_gsensor.setText(message);
                tv_gsensor_result.setText("成功");
                tv_gsensor_result.setTextColor(Color.GREEN);
            } else if (msg.what == 6) {
                Log.d(TAG, "handleMessage: u盘文件连接");
                isMacVerify();
                connectWifi();
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        picView = findViewById(R.id.im_imageTest);
        deviceInfo();
        bleInit();
        tv_keyResult = findViewById(R.id.key_result);
        usbInit();
        headsetInit();
        //hdmiInit();
        usbUtil = new USBUtil(this);
        wifiInit();
        speakTest= new SpeakTest(this);
        gsensorInit();
        MotorTest.startMotorLoop();
    }

    /*
    * Gsensor测试*/
    private void gsensorInit(){
        tv_gsensor = findViewById(R.id.gsensor_value);
        tv_gsensor_result = findViewById(R.id.gsensor_result);
        gsensorTest = new GsensorTest(this,mHandler);
        gsensorTest.doTest();
    }

    /*
    * speaker测试*/
    private void speakerInit(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                speakTest.playMusic();
            }
        },1000);
    }

    /*
    * hdmi测试*/
    private void hdmiInit(){
        String input = "com.softwinner.vis/.HdmiInputService/HW1";
        hdmiView = findViewById(R.id.hdmi_view);
        hdmiView.reset();
        Uri uri = TvContract.buildChannelUriForPassthroughInput(input);
        hdmiView.tune(input,uri);
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
            Log.i(TAG, "headsetInit: 初始化这里检测到");
            //tv_headsetName.setText("未检测到耳机");
            tv_headsetName.setText("已检测到耳机");
            tv_headsetResult.setText("成功");
            tv_headsetResult.setTextColor(Color.GREEN);
        }else {
            Log.i(TAG, "headsetInit: 初始化这里未检测");
            /*tv_headsetName.setText("已检测到耳机");
            tv_headsetResult.setText("成功");
            tv_headsetResult.setTextColor(Color.GREEN);*/
            tv_headsetName.setText("未检测到耳机");
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
        diskFilter = new IntentFilter();
        diskFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        diskFilter.addDataScheme("file");
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
        String softwareVersion = Build.VERSION.INCREMENTAL;
        tv_deviceName = findViewById(R.id.device_name);
        tv_deviceVersion = findViewById(R.id.device_version);
        tv_softwareNo = findViewById(R.id.software_version);
        tv_deviceName.setText("设备名称:" + deviceName);
        tv_deviceVersion.setText("Android版本:"+androidVersion);
        tv_softwareNo.setText(softwareVersion);
        rom_size = findViewById(R.id.rom_size);
        ram_size = findViewById(R.id.ram_size);
        rom_size.setText("存储空间："+ StorageUtils.getTotalStorageSize());
        ram_size.setText("运行内存："+StorageUtils.getTotalMemorySize());
        softwareTime_text = findViewById(R.id.software_time_textView);
        softwareTime_text.setText(processVersion(softwareVersion));
    }

    public static String processVersion(String version) {
        String[] parts = version.split("\\.");
        if (parts.length > 2) {
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < parts.length; i++) {
                if (i > 2) {
                    sb.append(".");
                }
                sb.append(parts[i]);
            }
            return sb.toString();
        }
        return "";
    }

    /*
    * 按键测试 + 一键进入老化界面*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
            tv_keyResult.setText("成功");
            tv_keyResult.setTextColor(Color.GREEN);
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_MENU){
            Intent agingIntent = new Intent();
            agingIntent.setComponent(new ComponentName("com.twd.agingtest","com.twd.agingtest.MainActivity"));
            agingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            try {
                startActivity(agingIntent);
            }catch (Exception e){
                Log.i(TAG,"找不到老化界面");
                e.printStackTrace();
            }
        } else if ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) &&!isImageViewVisible) {
            picView.setVisibility(View.VISIBLE);
            Log.i(TAG, "onKeyDown: 第一次显示图片");
            isImageViewVisible = true;
            currentImageIndex = 2;
            picView.setImageResource(imageResources[currentImageIndex]);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            currentImageIndex = (currentImageIndex -1 + imageResources.length) % imageResources.length;
            picView.setImageResource(imageResources[currentImageIndex]);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            currentImageIndex = (currentImageIndex + 1) % imageResources.length;
            picView.setImageResource(imageResources[currentImageIndex]);
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    @Override
    public void onBackPressed() {
        if (isImageViewVisible){
            isImageViewVisible = false;
            picView.setVisibility(View.GONE);
        }else {
            super.onBackPressed();
        }
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
        registerReceiver(bleReceiver, bleFilter);
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
                   // unregisterReceiver(bleReceiver);
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
        Log.i(TAG, "wifiInit: ------初始化网络--------");
        wifiTest = new WifiTest(this);
        tv_wifiIp = findViewById(R.id.ip_address);
        tv_wifiName = findViewById(R.id.wifi_name);
        tv_wifiResult = findViewById(R.id.wifi_result);
        tv_wifiMac = findViewById(R.id.wifi_mac_value);
        wifi_num = findViewById(R.id.wifi_num);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        tv_wifiIp.setText("IP:" + wifiTest.getIpAddress(wifiInfo));
        String macAddress = wifiTest.convertMacToUpperCase();
        if (!wifiManager.isWifiEnabled()){
            // 如果 Wi-Fi 未开启，无法扫描热点
            Log.d(TAG, "getWifiNum: wifi未开启");
            return;
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final int[] count = {0};
                    registerReceiver(new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                                if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    return;}
                                List<ScanResult> scanResults = wifiManager.getScanResults();
                                count[0] = scanResults.size();
                                Log.i(TAG, "扫描到的 Wi-Fi 热点个数：" + count[0]);
                                context.unregisterReceiver(this);
                                wifi_num.setText("搜索到"+count[0]+"个热点");
                                tv_wifiResult.setText("成功");
                                tv_wifiResult.setTextColor(Color.GREEN);
                            }
                        }
                    }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

                    wifiManager.startScan();
                }
            },2000);
        }
        wifiFilter = new IntentFilter();
        wifiFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        String usbPath = new USBUtil(this).getUsbFilePath();
        //主动检查u盘
        if (!usbPath.isEmpty()) {
            Log.i(TAG, "应用启动时检测到 U 盘：" + usbPath);
            connectWifi();
            isMacVerify();
        }else {
            Log.i(TAG, "应用启动时没有检测到 U 盘：");
            isMacVerify();
        }
    }
    private void isMacVerify(){
        String macAddress = wifiTest.convertMacToUpperCase();
        if (macAddress != null){
            String[] parts = macAddress.split(":");
            if (parts.length >= 3 && parts[0].equals("74") && parts[1].equals("AF") && parts[2].equals("F7")){
                tv_wifiMac.setText(macAddress);
            }else {
                usbUtil.usbFilePath = usbUtil.getUsbFilePath();
                usbUtil.getMacVerify();
                String verify = usbUtil.getVerify();
                Log.i("yangxin", "isMacVerify: verify = "+ verify);
                //TODO:  mac错误对话框
                if (verify.equals("1")){
                    showMacErrorDialog();
                }
                tv_wifiMac.setText(macAddress);
            }
        }
    }


    private void connectWifi(){
        if (!usbUtil.getUsbFilePath().equals("")){
            Log.i(TAG, "wifiInit: U盘路径："+usbUtil.getUsbFilePath());
            usbUtil.usbFilePath = usbUtil.getUsbFilePath();
            usbUtil.getWifiInfo();
            String ssid = usbUtil.getWifiSSID();
            String password = usbUtil.getPassWord();
            Log.i(TAG, "connectWifi: ssid = "+ssid+",password = "+password);
            //可以做成就算现在已经连着一个WiFi了，也强制换成U盘中的这个网络
            if (!wifiTest.isConnect() && ssid!=null && password!=null) {
                Log.i(TAG, "connectWifi: 进入连接");
                Log.d(TAG, "onCreate: 没连wifi，给连上");
                wifiTest.connectToWifi(ssid, password);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (wifiTest.getCurrentWifiSsid(wifiManager).equals(ssid)) {
                            tv_wifiName.setText(ssid);
                        }else {
                            tv_wifiName.setText("");
                        }
                    }
                }, 2000);
            }
        }else {
            Log.i(TAG, "wifiInit: U盘未挂载");
        }
    }
    private  void  showMacErrorDialog(){
        MacErrorDialog dialog = MacErrorDialog.getInstance(this);
        dialog.show();
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d("yangxin", "onPause: 暂停了");
        hdmiView.reset();
        speakTest.stop();
        gsensorTest.doStop();
        MotorTest.stopMotorLoop();
        unregisterReceiver(wifiReceiver);
        unregisterReceiver(bleReceiver);
        unregisterReceiver(usbTest.usbReceiver);
        unregisterReceiver(usbTest.uDiskReceiver);
        unregisterReceiver(headsetTest.headsetReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hdmiView.reset();
        Log.d("yangxin", "onPause: 暂停了");
        MacErrorDialog.resetInstance();
        speakTest.stop();
        gsensorTest.doStop();
        MotorTest.stopMotorLoop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("yangxin", "onRestart: 重新播放");
        hdmiInit();
        speakerInit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("yangxin", "onResume: 继续播放");
        hdmiInit();
        speakerInit();
        wifiInit();
        bleInit();
        gsensorInit();
        MotorTest.startMotorLoop();
        registerReceiver(wifiReceiver, wifiFilter);
        registerReceiver(bleReceiver, bleFilter);
        registerReceiver(usbTest.usbReceiver, usbFilter);
        registerReceiver(usbTest.uDiskReceiver, diskFilter);
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