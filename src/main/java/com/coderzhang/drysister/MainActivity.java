package com.coderzhang.drysister;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "NetWork";
    private ImageView imageView;
    private Button btnNext, btnSwitch;
    private ArrayList<Sister> data;
    private PictureLoader loader;
    private SisterApi sisterApi;
    private int curPos = 0; // 当前显示哪一张
    private int page = 1;//页数
    private GetSisterTask sisterTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loader = new PictureLoader();
        sisterApi = new SisterApi();
        bindViews();
        setData();
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }

    private void setData() {
        data = new ArrayList<>();
    }

    private void bindViews() {
        imageView = findViewById(R.id.image_view);
        btnNext = findViewById(R.id.btn_next);
        btnSwitch = findViewById(R.id.btn_switch);
        btnNext.setOnClickListener(this);
        btnSwitch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                if (data != null && !data.isEmpty()) {
                    if (curPos > 9) {
                        curPos = 0;
                    }
                    loader.loadImage(imageView, data.get(curPos).getUrl());
                    curPos++;
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
