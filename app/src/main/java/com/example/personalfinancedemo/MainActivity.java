package com.example.personalfinancedemo;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

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
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    CircleStatisticalView csv, csv_month;
    Calendar cal = Calendar.getInstance();

    String Month = String.valueOf(cal.get(Calendar.MONTH) + 1);
    private static String REGEX_CHINESE = "[\u4e00-\u9fa5]";
    List<StatisticalItem> list;
    TextView tv_ownerId;
    String httpUrl_total = "http://10.0.2.2:8080/PF2/totalcount";
    String httpUrl_month = "http://10.0.2.2:8080/PF2/monthcount";
    String msg = "";
    Handler handler = new Handler();
    ArrayList<Map<Integer, Float>> amountList = new ArrayList<>();
    private AlphaAnimation alphaAniShow, alphaAniHide;
    TextView tv_tatol, tv_month, tv_month_all,tv_count_all;
    QMUIRoundButton btn_query, btn_manager, btn_count_other,btn_count_total,btn_count_month;
    Spinner spinner4;
    String[] select_months = {"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
    String select_month = "";
    LinearLayout LinearLayout_btn_count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread thread = new Thread(new CountTotalThread());
        Thread thread1 = new Thread(new CountMonthThread());

        thread.start();
        thread1.start();

        final Intent intent = getIntent();
        tv_ownerId = findViewById(R.id.tv_ownerId);
        tv_ownerId.setText(intent.getStringExtra("ownerId"));
        tv_tatol = findViewById(R.id.tv_tatol);
        tv_month = findViewById(R.id.tv_month);
        tv_month_all = findViewById(R.id.tv_month_all);
        tv_count_all=findViewById(R.id.tv_count_all);
        LinearLayout_btn_count=findViewById(R.id.LinearLayout_btn_count);


        tv_month_all.setText(Month + "月");

        btn_manager = findViewById(R.id.btn_manager);
        btn_manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getApplicationContext(), ManagerActivity.class);
                intent2.putExtra("ownerId", tv_ownerId.getText().toString());
                startActivity(intent2);
            }
        });

        initView();
        csv.setVisibility(View.VISIBLE);

        spinner4 = findViewById(R.id.spinner4);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, select_months);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner4.setAdapter(adapter);
        spinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                tv.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_count_other = findViewById(R.id.btn_count_other);
        btn_count_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Month = spinner4.getSelectedItem().toString();
                Pattern pat = Pattern.compile(REGEX_CHINESE);
                Matcher mat = pat.matcher(Month);
                Month = mat.replaceAll("");
                Intent intent3 = new Intent(getApplicationContext(), CountMonthActivity.class);
                intent3.putExtra("month", Month);
                intent3.putExtra("ownerId", tv_ownerId.getText().toString());
                startActivity(intent3);
            }
        });


    }

    private void initView() {
        csv = findViewById(R.id.csv);
        csv_month = findViewById(R.id.csv_moth);
        btn_count_total = findViewById(R.id.btn_count_total);
        btn_count_month = findViewById(R.id.btn_count_month);
        alphaAnimation();
        btn_count_total.setOnClickListener(this);
        btn_count_month.setOnClickListener(this);
    }

    private void alphaAnimation() {
        //显示
        alphaAniShow = new AlphaAnimation(0, 1);//百分比透明度，从0%到100%显示
        alphaAniShow.setDuration(1000);//一秒

        //隐藏
        alphaAniHide = new AlphaAnimation(1, 0);
        alphaAniHide.setDuration(1000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_count_month:
                if (csv_month.getVisibility() == View.VISIBLE) break;
                csv_month.startAnimation(alphaAniShow);
                tv_month_all.startAnimation(alphaAniShow);
                csv.startAnimation(alphaAniHide);
                alphaAniHide.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        tv_tatol.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        csv.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                csv_month.startAnimation(alphaAniShow);
                csv_month.setVisibility(View.VISIBLE);
                tv_month.setVisibility(View.VISIBLE);
                tv_month_all.startAnimation(alphaAniShow);
                tv_month_all.setVisibility(View.VISIBLE);
                break;

            case R.id.btn_count_total:
                if (csv.getVisibility() == View.VISIBLE) break;
                csv.startAnimation(alphaAniShow);
                csv.setVisibility(View.VISIBLE);
                csv_month.startAnimation(alphaAniHide);
                tv_month_all.startAnimation(alphaAniHide);
                tv_tatol.setVisibility(View.VISIBLE);

                alphaAniHide.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        tv_month.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        csv_month.setVisibility(View.INVISIBLE);
                        tv_month_all.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                break;
        }
    }

    public class CountTotalThread implements Runnable {
        @Override
        public void run() {
            URL url = null;
            try {
                url = new URL(httpUrl_total);
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
                    String recordOwner = tv_ownerId.getText().toString();
                    String params = "recordOwner_total=" + URLEncoder.encode(recordOwner, "utf-8");
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
                        double amount_total = jsonObject.getDouble("amount_total");
                        double amount_in = jsonObject.getDouble("amount_in");
                        double amount_out = jsonObject.getDouble("amount_out");
                        tv_tatol.setText(String.valueOf(amount_total) + "元");
                        if (amount_in == 0 && amount_out == 0 && amount_total == 0) {
                            msg = "统计无数据";
                            handler.post(new updateUIThread());
                            tv_count_all.setText("目前没有任何记录");
                            tv_tatol.setText("");
                            csv.setVisibility(View.INVISIBLE);
                            LinearLayout_btn_count.setVisibility(View.INVISIBLE);
                        }
                        float[] percent = {(float) (amount_in / amount_total), (float) (amount_out / amount_total)};
                        int[] color = {Color.parseColor("#12B166"), Color.parseColor("#1DA02B"), Color.GREEN};
                        String[] markTop = {String.valueOf(amount_in), String.valueOf(amount_out)};
                        String[] markBottom = {"收入", "支出"};

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
                        csv.setStatisticalItems(list);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                    msg = "收支统计失败";
                    handler.post(new updateUIThread());
                }
            }
        }
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
                    String recordOwner = tv_ownerId.getText().toString();
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

                            float[] percent = {(float) (amount_in / amount_total), (float) (amount_out / amount_total)};
                            int[] color = {Color.parseColor("#12B166"), Color.parseColor("#1DA02B"), Color.GREEN};
                            String[] markTop = {String.valueOf(amount_in), String.valueOf(amount_out)};
                            String[] markBottom = {"收入", "支出"};
                            tv_month.setText(String.valueOf(amount_total) + "元");
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
                            csv_month.setStatisticalItems(list);
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

