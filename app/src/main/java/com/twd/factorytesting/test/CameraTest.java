package com.twd.factorytesting.test;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.io.IOException;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 上午8:47 9/4/2025
 */
public class CameraTest implements SurfaceHolder.Callback {

    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private Handler handler;

    public CameraTest(SurfaceHolder surfaceHolder, Handler handler) {
        this.surfaceHolder = surfaceHolder;
        this.handler = handler;
        surfaceHolder.addCallback(this);
    }

    public void openCamera(){
        camera = Camera.open();
        if (camera != null){
            camera.setDisplayOrientation(90);
            try{
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
            }catch (IOException e){
                e.printStackTrace();
            }
        }else {

        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        if (camera != null){
            try{
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            }catch (IOException e){
                e.printStackTrace();
            }
        }else {
            Message message = handler.obtainMessage(7,"plug");
            handler.sendMessage(message);
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        if (surfaceHolder.getSurface() == null){
            return;
        }
        try{
            camera.stopPreview();
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        if (camera != null){
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public void stopCamera(){
        if (camera != null){
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
}
