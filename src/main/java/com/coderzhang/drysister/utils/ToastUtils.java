package com.coderzhang.drysister.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by z on 2018/01/30.
 */

public class ToastUtils {
    public static void  toast(Context context, String msg, int time){
        Toast.makeText(context,msg,time);
    }
}
