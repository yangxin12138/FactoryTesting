package com.twd.factorytesting.test;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 8:54 2024/5/11
 */
public class GsensorTest {
    private Context mContext;
    private SensorEventListener mListener;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Handler mHandler;
    public GsensorTest(Context mContext, Handler handler) {
        this.mContext = mContext;
        mHandler = handler;
    }

    public void doTest(){
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Log.i("yangxin", "doTest: Gensor doTest--------------");
        mListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[SensorManager.DATA_X];
                float y = event.values[SensorManager.DATA_Y];
                float z = event.values[SensorManager.DATA_Z];
                String formattedX = String.format("%.3f", x);
                String formattedY = String.format("%.3f", y);
                String formattedZ = String.format("%.3f", z);
                //Log.i("yangxin", "onSensorChanged: Gsensor坐标 x y z "+formattedX+formattedY+formattedZ);
                //TODO:
                Message message = mHandler.obtainMessage(5,formattedX+","+formattedY+","+formattedZ);
                mHandler.sendMessage(message);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        mSensorManager.registerListener(mListener,mSensor,SensorManager.SENSOR_DELAY_GAME);
    }

    public void doStop(){
        Log.i("yangxin", "doStop: Gsensor doStop");
        mSensorManager.unregisterListener(mListener);
    }
}
