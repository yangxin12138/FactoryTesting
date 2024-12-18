package com.twd.factorytesting.test;

import android.content.Context;
import android.util.Log;

import com.twd.factorytesting.util.Utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 上午9:44 18/12/2024
 */
public class MotorTest {
    private Context mContext;
    private static String motor_port = "/sys/devices/platform/motor0/motor_ctr";
    private static boolean isRunning = true;

    public static void startMotorLoop(){
        Timer timer = new Timer();
        //定义定时任务
        TimerTask task = new TimerTask() {
            boolean writeOne = true;
            @Override
            public void run() {
                if (!isRunning) {
                    timer.cancel();
                    Log.i("MotorTest","------------motor任务结束-----------");
                    return;
                }
                String writeContent;
                if (writeOne){
                    writeContent = "1,99999999";
                }else {
                    writeContent = "2,99999999";
                }
                Utils.writeSystemFile(writeContent,motor_port);
                Log.i("MotorTest","写入内容: " + writeContent);
                String readContent = Utils.readSystemFile(motor_port);
                Log.i("MotorTest","读取内容: " + readContent);
                writeOne = !writeOne;
            }
        };
        //首次执行任务延迟0毫秒，之后每隔2秒执行一次任务
        timer.schedule(task,0,2000);
    }

    public static void stopMotorLoop(){
        isRunning = false;
    }
}
