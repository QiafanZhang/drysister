package com.coderzhang.drysister.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by z on 2018/01/28.
 */

public class Save2SD {
    public static boolean save(String fileName, ImageView imageView, Context context) {
        Drawable drawable = imageView.getDrawable();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || drawable != null) {
            String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "drySister";

            File appDir = new File(absolutePath);
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            File file = new File(appDir, fileName + ".jpg");
//            Log.v(TAG, file + "");
            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                boolean state = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                Uri uri = Uri.fromFile(file);
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                if (state) {
                    return true;
                } else {
                    return false;
                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
//                Log.v(TAG, "IO异常");
            }
        } else {
//            Log.v(TAG, "SD卡未挂载或者image为NULL");
            return false;
        }
        return false;
    }
}
