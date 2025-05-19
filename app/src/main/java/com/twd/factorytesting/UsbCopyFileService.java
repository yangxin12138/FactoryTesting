package com.twd.factorytesting;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.twd.factorytesting.util.ToastUtil;
import com.twd.factorytesting.util.USBUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Objects;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 上午9:06 17/5/2025
 */
public class UsbCopyFileService extends Service {
    private static final String TAG = "UsbCopyFileService";
    private static UsbCopyFileService instance;
    private String cmd_copy_path;
    private Context context;
    public static UsbCopyFileService getInstance(){
        return instance;
    }
    private USBUtil usbUtil;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: 创建UsbCopyFileService");
        context = getApplicationContext();
        usbUtil = new USBUtil(context);
        instance = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //服务启动
        Log.i(TAG, "onCreate: onStartCommand服务启动");
        try{
            //读取文件内容
            String usbPath = usbUtil.getUsbFilePath();
            Log.i(TAG, "onStartCommand: usbPath = "+usbPath);
            File file = new File(usbPath+"/testInfo.txt");
            if (file.exists()){
                Log.i(TAG, "onStartCommand: file存在,"+file.getName());
                FileInputStream fis = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                String line;
                cmd_copy_path = "";
                while ((line = reader.readLine())!= null){
                    if (line.contains("cmd_copy_path：")){
                        cmd_copy_path = line.substring("cmd_copy_path：".length());
                        Log.i(TAG, "onStartCommand:cmd_copy_path找到了 =  "+cmd_copy_path);
                        break;
                    }
                }
                reader.close();
                fis.close();
            }else {
                Log.i(TAG, "onStartCommand: 读取不存在");
                stopSelf();
                return START_NOT_STICKY;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if (!Objects.equals(cmd_copy_path, "")){
        copyToDevice(cmd_copy_path);
        }else {
            Log.i(TAG, "onStartCommand: 未找到有效的复制路径");
            stopSelf();
        }
        return START_STICKY;
    }

    private void copyToDevice(String copyPath){
        Log.i(TAG, "copyToDevice: 开始复制");
        File sdcardDir = Environment.getExternalStorageDirectory();
        String targetPath = sdcardDir.getAbsolutePath();
        File destDir = new File(targetPath);

        new Thread(() -> {
            boolean success = false;
            try{
                if (usbUtil.checkUsbFolderExist(copyPath)){
                    File sourceDir = new File(usbUtil.getUsbFilePath()+File.separator+copyPath);
                    File newDestDir = new File(destDir, sourceDir.getName());
                    copyDirectory(sourceDir,newDestDir);
                    Log.i(TAG, "copyToDevice: 复制完成，目标路径: " + targetPath);
                    success = true;
                }else {
                    Log.i(TAG, "copyToDevice: 源目录不存在或不是目录: " + copyPath);
                }
            }catch (IOException e){
                Log.e(TAG, "copyToDevice: 复制过程中出错", e);
                e.printStackTrace();
            } finally {
                // 通过Handler切换到主线程显示Toast
                boolean finalSuccess = success;
                new Handler(Looper.getMainLooper()).post(() ->{
                    if (finalSuccess){
                        ToastUtil.showCustomToast(context, "文件复制成功", Toast.LENGTH_SHORT);
                    }else {
                        ToastUtil.showCustomToast(context, "复制失败，请检查路径或权限", Toast.LENGTH_SHORT);
                    }
                    stopSelf();
                });
            }
        }).start();

    }

    private void copyDirectory(File sourceDir,File destDir) throws IOException{
        Log.i(TAG, "copyDirectory: 进入copyDirectory");
        // 若目标目录不存在，则创建
        if (!destDir.exists()){
            Log.i(TAG, "copyDirectory: destDir 不存在");
            if (!destDir.mkdirs()){
                Log.i(TAG, "copyDirectory: 无法创建destDir");
                throw new IOException("无法创建目录："+destDir);
            }
        }

        Log.i(TAG, "copyDirectory: sourceDir = " + sourceDir.getName());
        //列出目录下的所有文件和子目录
        File[] files = sourceDir.listFiles();

        if(files == null){
            Log.i(TAG, "copyDirectory: 文件列表是空的");
        }

        for (File file: files){
            File targetFile = new File(destDir,file.getName());
            Log.i(TAG, "copyDirectory: targetFile = " +targetFile.getName());
            if (file.isDirectory()){
                Log.i(TAG, "copyDirectory: 递归复制子目录");
                //递归复制子目录
                copyDirectory(file,targetFile);
            }else {
                //复制单个文件
                Log.i(TAG, "copyDirectory: 复制单个文件");
                copyFile(file,targetFile);
            }
        }
    }

    private void copyFile(File sourceFile,File destFile) throws IOException{
        InputStream in = new FileInputStream(sourceFile);
        OutputStream out = new FileOutputStream(destFile);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) > 0 ){
            out.write(buffer,0,length);
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
