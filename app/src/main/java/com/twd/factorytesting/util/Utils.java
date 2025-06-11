package com.twd.factorytesting.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 上午9:51 18/12/2024
 */
public class Utils {
    private Context mContext;

    public Utils(Context mContext) {
        this.mContext = mContext;
    }

    public static String readSystemFile(String path){
        StringBuilder content = new StringBuilder();
        File file = new File(path);
        try{
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null){
                content.append(line).append("\n");
            }
            reader.close();
            fis.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return content.toString();
    }

    public static void writeSystemFile(String content,String path) {
        File file = new File(path);
        try{
            OutputStream os = new FileOutputStream(file);
            os.write(content.getBytes());
            os.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static String readSystemProp(String search_line) {
        String line = "";
        try {
            File file = new File("/system/etc/settings.ini");
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            while ((line = reader.readLine()) != null) {
                if (line.contains(search_line)) {
                    // 这里可以进一步解析line来获取STORAGE_SIMPLE_SYSDATA的值
                    String value = line.split("=")[1].trim(); // 获取等号后面的值
                    reader.close();
                    fis.close();
                    return value;
                }
            }
            reader.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Standard";
    }
}
