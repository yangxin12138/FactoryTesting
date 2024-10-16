package com.twd.factorytesting.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.twd.factorytesting.R;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 15:58 2024/10/15
 */
public class ToastUtil {
    public static void showCustomToast(Context context,String text,int duration){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_toast,null);
        TextView result = (TextView) layout.findViewById(R.id.connect_result);
        result.setText(text);

        // 创建Toast并设置参数
        Toast toast = new Toast(context);
        toast.setDuration(duration);
        toast.setView(layout);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
