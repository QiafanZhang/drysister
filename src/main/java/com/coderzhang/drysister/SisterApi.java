package com.coderzhang.drysister;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by z on 2018/01/27.
 */

public class SisterApi {
    private static final String TAG = "NetWork";
    private static final String url = "http://gank.io/api/data/福利/";

    public ArrayList<Sister> fetchSister(int count, int page) {
        String newUrl = url + count + "/" + page;
        ArrayList<Sister> sisters = new ArrayList<>();
        try {
            URL url = new URL(newUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(10000);
            conn.connect();
            int responseCode = conn.getResponseCode();
            Log.v(TAG, "服务器响应码" + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = conn.getInputStream();
                byte[] outBytes = readStream(inputStream);
                //将字节数组转成字符串
                String result = new String(outBytes, "UTF-8");
                Log.v(TAG, "result=" + result);
                sisters = parseLists(result);
            } else {
                Log.v(TAG, "请求失败：" + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v(TAG, "总的数组大小：" + sisters.size());
        return sisters;
    }

    /**
     * 读取流中数据的方法
     *
     * @param inputStream 输入流
     * @return 输出流字节数组
     * @throws IOException
     */
    public byte[] readStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        inputStream.close();
        Log.v(TAG, "字节数组的长度：" + outputStream.toString().length());
        return outputStream.toByteArray();
    }

    /**
     * 用自带JSON解析
     *
     * @param content
     * @return 泛型为Sister的集合
     */
    public ArrayList<Sister> parseLists(String content) throws Exception {
        ArrayList<Sister> sisters = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(content);
        JSONArray jsonArray = jsonObject.getJSONArray("results");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject results = (JSONObject) jsonArray.get(i);
            Sister sister = new Sister();
            sister.set_id(results.getString("_id"));
            sister.setCreatedAt(results.getString("createdAt"));
            sister.setDesc(results.getString("desc"));
            sister.setPublishedAt(results.getString("publishedAt"));
            sister.setSource(results.getString("source"));
            sister.setType(results.getString("type"));
            sister.setUrl(results.getString("url"));
            sister.setWho(results.getString("who"));
            sister.setUsed(results.getBoolean("used"));
            sisters.add(sister);
        }
        Log.v(TAG, "json解析返回的集合大小：" + sisters.size() + "");
        return sisters;
    }

}
