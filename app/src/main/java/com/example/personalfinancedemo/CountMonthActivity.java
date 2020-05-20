package com.example.personalfinancedemo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class CountMonthActivity extends AppCompatActivity {

    String httpUrl_month = "http://10.0.2.2:8080/PF2/monthcount";
    String msg = "";
    Handler handler = new Handler();

    TextView tv_ownerId_other_count,tv_count_month_all,tv_month_count_in,tv_month_count_out,tv_count_info;
    String Month;
    CircleStatisticalView csv_other_month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_month);
        Intent intent=getIntent();
        Month=intent.getStringExtra("month");
        tv_ownerId_other_count=findViewById(R.id.tv_ownerId_other_count);
        tv_ownerId_other_count.setText(intent.getStringExtra("ownerId"));
        tv_count_month_all=findViewById(R.id.tv_month_count_all);
        tv_month_count_in=findViewById(R.id.tv_month_count_in);
        tv_month_count_out=findViewById(R.id.tv_month_count_out);
        tv_count_info=findViewById(R.id.tv_count_info);
        csv_other_month=findViewById(R.id.csv_other_month);
        tv_count_info.setText(Month+"月");

        Thread thread=new Thread(new CountMonthThread());
        thread.start();
    }


    public class CountMonthThread implements Runnable {
        @Override
        public void run() {
            URL url = null;
            try {
                url = new URL(httpUrl_month);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if (url != null) {
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.connect();
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    String recordOwner = tv_ownerId_other_count.getText().toString();
                    String params = "recordOwner_month=" + URLEncoder.encode(recordOwner, "utf-8");
                    params += "&Month=" + URLEncoder.encode(Month, "utf-8");
                    outputStream.write(params.getBytes());
                    outputStream.flush();
                    outputStream.close();
                    int code = httpURLConnection.getResponseCode();
                    if (code == 200) {
                        InputStream inputStream = httpURLConnection.getInputStream();
                        byte[] buffer = new byte[1024];
                        int count = inputStream.read(buffer);
                        String result = new String(buffer, 0, count);
                        JSONObject jsonObject = new JSONObject(result);
                        Log.i("Test", result);
                        if (result.equals("统计失败")) {
                            //msg = "该月总收支" + result;
                            handler.post(new updateUIThread());
                        } else {
                            //msg = "该月总收支统计成功";
                            handler.post(new updateUIThread());
                            double amount_total = jsonObject.getDouble("amount_total");
                            double amount_in = jsonObject.getDouble("amount_in");
                            double amount_out = jsonObject.getDouble("amount_out");
                            if(amount_in==0&&amount_out==0&&amount_total==0){
                                msg="该月无数据";
                                handler.post(new updateUIThread());
                                finish();
                            }
                            float[] percent = {(float) (amount_in / amount_total), (float) (amount_out / amount_total)};
                            int[] color = {Color.parseColor("#12B166"), Color.parseColor("#1DA02B"), Color.GREEN};
                            String[] markTop = {String.valueOf(amount_in), String.valueOf(amount_out)};
                            String[] markBottom = {"收入", "支出"};
                            tv_count_month_all.setText(String.valueOf(amount_total)+"元");
                            tv_month_count_in.setText(String.valueOf(amount_in)+"元");
                            tv_month_count_out.setText(String.valueOf(amount_out)+"元");
                            final ArrayList list = new ArrayList<>();
                            for (int i = 0; i < percent.length; i++) {
                                StatisticalItem item = new StatisticalItem();
                                item.setPercent(percent[i]);
                                item.setColor(color[i]);
                                item.setTopMarkText(markTop[i]);
                                item.setBottomMarkText(markBottom[i]);
                                list.add(item);
                            }
                            //设置数据方法
                            csv_other_month.setStatisticalItems(list);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class updateUIThread implements Runnable {
        @Override
        public void run() {
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }
}
