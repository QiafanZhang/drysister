package com.coderzhang.drysister.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by z on 2018/01/29.
 */

public class NetWorkUtils {
    public static NetworkInfo getNetWorkInfo(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo();
    }

    public static boolean isAvailable(Context context) {
        NetworkInfo workInfo = getNetWorkInfo(context);
        return workInfo != null && workInfo.isAvailable();
    }
}
