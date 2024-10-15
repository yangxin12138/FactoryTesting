package com.twd.factorytesting;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 16:37 2024/10/14
 */
public class MacErrorDialog extends Dialog {

    private static MacErrorDialog instance;
    private Context mContext;

    public MacErrorDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public static MacErrorDialog getInstance(Context context) {
        if (instance == null){
            instance = new MacErrorDialog(context);
        }
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("yangxin", "onCreate: dialog创建了");
        setContentView(R.layout.dialog_layout);
    }

    public static void resetInstance() {
        instance = null;
    }
}
