package com.coderzhang.drysister;

import android.app.Application;
import android.content.Context;

import com.coderzhang.drysister.utils.CrashLogCatchUtils;

/**
 * Created by z on 2018/01/30.
 */

public class DrySisterApp extends Application {
    private Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        CrashLogCatchUtils.getInstance().init(context);
    }
}
