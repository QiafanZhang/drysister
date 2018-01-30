package com.coderzhang.drysister.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.Toast;

import com.coderzhang.drysister.constant.ConstantValues;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by z on 2018/01/30.
 */

public class CrashLogCatchUtils implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "CrashLogCatchUtils";
    private Thread.UncaughtExceptionHandler mExceptionHandler;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM--dd HH--mm-ss");
    private Context mContext;
    private Map<String, String> infoMap = new HashMap<>();
    private static CrashLogCatchUtils instance;

    private CrashLogCatchUtils() {
    }

    public static CrashLogCatchUtils getInstance() {
        if (instance == null) {
            synchronized (CrashLogCatchUtils.class) {
                if (instance == null) {
                    instance = new CrashLogCatchUtils();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.mContext = context;
        mExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 返回SD卡路径
     *
     * @return
     */
    private static String getSavePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "drySister" + File.separator + "log-crash";
    }

    /**
     * 将StringBuilder拼接好的内容写入到文件中去
     *
     * @param sb StringBuilder字符串
     * @return 该文件的名字
     * @throws IOException
     */
    private String write2File(String sb) throws IOException {
        String now = formatter.format(new Date());
        String fileName = "-" + now + ".log";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = getSavePath();
            File file = new File(path,"log-crash");
            if (!file.exists()) file.mkdir();
            FileOutputStream outputStream = new FileOutputStream(file + fileName, true);
            outputStream.write(sb.getBytes());
            outputStream.flush();
            outputStream.close();
        }
        return fileName;
    }

    private void getInfo(Context context) {
        try {
            PackageManager packageManager = (PackageManager) context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                infoMap.put(ConstantValues.VERSION_NAME, packageInfo.versionName);
                infoMap.put(ConstantValues.VERSION_CODE, String.valueOf(packageInfo.versionCode));
            }
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e(TAG, "包名未找到！");
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field :
                fields) {
            field.setAccessible(true);
            try {
                infoMap.put(field.getName(), field.get(null).toString());
            } catch (IllegalAccessException e) {
                LogUtils.e(TAG, "获取设备信息错误！");
            }
        }
    }

    public String saveCrashInfo2File(Throwable throwable) throws IOException {
        StringBuilder sb = new StringBuilder();
        try {
            String now = formatter.format(new Date());
            sb.append("\r\n").append(now).append("\n");
            for (Map.Entry<String, String> entry : infoMap.entrySet()
                    ) {
                String key = entry.getKey();
                String value = entry.getValue();
                sb.append(key).append("=").append(value).append("\n");
            }
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            throwable.printStackTrace(printWriter);
            writer.flush();
            writer.close();
            String content = writer.toString();
            sb.append(content);
            return write2File(sb.toString());
        } catch (IOException e) {
            LogUtils.e(TAG, "把奔溃信息写入到日志文件错误!");
            sb.append("把奔溃信息写入到日志文件错误");
            write2File(sb.toString());
        }
        return null;
    }

    private boolean handleException(Throwable throwable) {
        if (throwable == null) return false;
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(mContext, "程序出现异常！5秒后退出", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }).start();
            getInfo(mContext);
            saveCrashInfo2File(throwable);
            SystemClock.sleep(3000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (!handleException(e) && mExceptionHandler != null) {
            mExceptionHandler.uncaughtException(t, e);
        } else {
            //退出应用
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
            System.gc();
        }
    }
}
