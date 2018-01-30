package com.coderzhang.drysister.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.coderzhang.drysister.R;
import com.coderzhang.drysister.db.SisterDBHelper;
import com.coderzhang.drysister.entity.Sister;
import com.coderzhang.drysister.api.SisterApi;
import com.coderzhang.drysister.utils.Permissions;
import com.coderzhang.drysister.utils.Save2SD;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "NetWork";
    private static final int REQUEST_CODE = 1;
    private ImageView imageView;
    private Button btnPre, btnNext, btnSwitch;
    private ArrayList<Sister> data;
    private SisterApi sisterApi;
    private int curPos = 0; // 当前显示哪一张
    private int page = 1;//页数
    private GetSisterTask sisterTask;
    private SisterDBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sisterApi = new SisterApi();
        dbHelper = SisterDBHelper.getInstance(getApplicationContext());
        bindViews();
        checkPermissions();
        setData();
        saveImage();
    }

    private void checkPermissions() {
        Permissions.check(getApplicationContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE},
                this, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int grant :
                    grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "授权未完成！保存图片功能失效！", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    return;
                }
            }
        }
    }

    private void saveImage() {
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("保存吗?").setPositiveButton("好", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean state = Save2SD.save(data.get(curPos).getDesc(), imageView, getApplicationContext());
                        if (state) {
                            Toast.makeText(getApplicationContext(), "保存成功！", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getApplicationContext(), "保存失败！请检查读写权限！", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("不", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
                return true;
            }
        });
    }

    private void setData() {
        data = new ArrayList<>();
    }

    private void bindViews() {
        imageView = findViewById(R.id.image_view);
        btnNext = findViewById(R.id.btn_next);
        btnPre = findViewById(R.id.btn_previous);
        btnSwitch = findViewById(R.id.btn_switch);
        btnNext.setOnClickListener(this);
        btnPre.setOnClickListener(this);
        btnSwitch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_previous:
                if (data != null && !data.isEmpty()) {
                    if (curPos == 0) {
                        //当前图片索引为0时 隐藏btn_pre
                        btnPre.setVisibility(View.INVISIBLE);
                    }

                    if (curPos == data.size() - 1) {
                        //最后一个 重新new Task

                    } else if (curPos < data.size()) {
                        //当前图片位置在总索引内
                        //获得索引加载图片
                        Picasso.with(getApplicationContext()).load(data.get(curPos).getUrl()).into(imageView);
                    }
                    curPos--;
                } else {
                    Log.v(TAG, "集合为空");
                }
                break;
            case R.id.btn_next:
                btnPre.setVisibility(View.VISIBLE);
                if (data != null && !data.isEmpty()) {
                    if (curPos < data.size()) {
                        ////当前图片的位置在总索引内 索引++
                        curPos++;
                    }
                    if (curPos > data.size() - 1) {
                        //当前图片的位置大于最大大小时
                        curPos = 0;
                    } else if (curPos < data.size()) {
                        //当前图片的位置在总索引内
                        Picasso.with(getApplicationContext()).load(data.get(curPos).getUrl()).into(imageView);
                    }

                } else {
                    Log.v(TAG, "集合为空");
                }
                break;
            case R.id.btn_switch:
                Toast.makeText(getApplicationContext(), "已换新一批妹子！", Toast.LENGTH_SHORT).show();
                sisterTask = new GetSisterTask();
                sisterTask.execute();
                curPos = 0;
                break;
            default:
                break;
        }
    }

    class GetSisterTask extends AsyncTask<Void, Void, ArrayList<Sister>> {
        public GetSisterTask() {
        }

        @Override
        protected ArrayList<Sister> doInBackground(Void... voids) {
            return sisterApi.fetchSister(10, page);
        }

        @Override
        protected void onPostExecute(ArrayList<Sister> sisters) {
            super.onPostExecute(sisters);
            data.clear();
            data.addAll(sisters);
            page++;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            sisterTask = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sisterTask.cancel(true);
    }
}
