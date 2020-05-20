package com.example.personalfinancedemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import org.json.JSONArray;
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
import java.util.HashMap;
import java.util.Map;

public class ManagerActivity extends AppCompatActivity {

    String httpUrl_ALL = "http://10.0.2.2:8080/PF2/query";
    String httpUrl_TYPE = "http://10.0.2.2:8080/PF2/query";
    String httpUrl_DATE="http://10.0.2.2:8080/PF2/datequery";
    String httpUrl_Delete="http://10.0.2.2:8080/PF2/delete";
    String msg = "";
    Handler handler=new Handler();
    String query_type = "";
    int[] record_icons = {R.drawable.icon1, R.drawable.icon2};
    String[] quert_types = {"时间段", "类别"};
    String query_type_in_out="";
    String date_start="",date_end="";

    TextView recordid,recorddate,recordtype,recordamount,recordExplain;

    Record record = new Record();

    QMUIRoundButton btn_record_query,btn_record_query_in,btn_record_query_out,btn_record_add;
    Spinner spinner;
    TextView tv_ownerId;
    EditText edt_query_start,edt_query_end;
    SimpleAdapter simpleAdapter;
    ListView query_list;
    LinearLayout lay_query_type;
    LinearLayout lay_query_date;

    final Thread thread = new Thread(new QueryAllThread());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        final Intent intent = getIntent();

        thread.start();

        tv_ownerId = findViewById(R.id.tv_ownerId);
        tv_ownerId.setText(intent.getStringExtra("ownerId"));
        spinner = findViewById(R.id.spinner);

        lay_query_type=findViewById(R.id.layout_query_type);
        lay_query_date=findViewById(R.id.layout_query_date);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, quert_types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv=(TextView)view;
                tv.setGravity(android.view.Gravity.CENTER_HORIZONTAL);

                query_type = quert_types[position];
                if(query_type.equals("类别")){
                    lay_query_date.setVisibility(View.INVISIBLE);
                    lay_query_type.setVisibility(View.VISIBLE);
                }
                else{
                    lay_query_date.setVisibility(View.VISIBLE);
                    lay_query_type.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        btn_record_query_in=findViewById(R.id.btn_record_query_in);
        btn_record_query_out=findViewById(R.id.btn_record_query_out);

        edt_query_start = findViewById(R.id.edt_query_start);
        edt_query_end=findViewById(R.id.edt_query_end);
        btn_record_query = findViewById(R.id.btn_record_query);
        btn_record_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_start=edt_query_start.getText().toString();
                date_end=edt_query_end.getText().toString();
                Thread thread2 = new Thread(new QueryDateThread());
                thread2.start();
            }
        });

        btn_record_query_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query_type_in_out="收入";
                Thread thread1 = new Thread(new QueryTypeThread());
                thread1.start();
            }
        });
        btn_record_query_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query_type_in_out="支出";
                Thread thread1 = new Thread(new QueryTypeThread());
                thread1.start();
            }
        });

        btn_record_add=findViewById(R.id.btn_record_add);
        btn_record_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2=new Intent(ManagerActivity.this,AddActivity.class);
                intent2.putExtra("ownerId",tv_ownerId.getText().toString());
                startActivityForResult(intent2,2);
                finish();
            }
        });
    }


    public class QueryAllThread implements Runnable {
        @Override
        public void run() {
            URL url = null;
            try {
                url = new URL(httpUrl_ALL);
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
                    String params = "recordOwner=" + URLEncoder.encode(recordOwner, "utf-8");
                    outputStream.write(params.getBytes());
                    outputStream.flush();
                    outputStream.close();
                    int code = httpURLConnection.getResponseCode();
                    if (code == 200) {
                        InputStream inputStream = httpURLConnection.getInputStream();
                        byte[] buffer = new byte[1024];
                        int count = inputStream.read(buffer);
                        String result = new String(buffer, 0, count);
                        JSONArray jsonArray = new JSONArray(result);
                        Log.i("Test", result);
                        if (result.equals("查询失败")) {
                            msg = result;
                            handler.post(new updateUIThread());
                        } else {
                            //Record record=new Record();
                            ArrayList<Map<String, String>> list = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Map<String, String> map = new HashMap<>();
                                map.put("recordId",String.valueOf(jsonObject.getInt("recordId")));
                                if (jsonObject.getString("recordType").equals("收入")) {
                                    map.put("recordIcon", String.valueOf(record_icons[0]));
                                }else{
                                    map.put("recordIcon", String.valueOf(record_icons[1]));
                                }
                                map.put("recordDate", (jsonObject.getString("recordDate")));
                                map.put("recordType", (jsonObject.getString("recordType")));
                                map.put("recordAmount", String.valueOf(jsonObject.getDouble("recordAmount")));
                                map.put("recordExplain", jsonObject.getString("recordExplain"));
                                list.add(map);
                            }

                            query_list = findViewById(R.id.query_list);
                            simpleAdapter = new SimpleAdapter(
                                    getApplicationContext(),
                                    list,
                                    R.layout.listitemlayout_manager,
                                    new String[]{"recordId","recordIcon", "recordDate", "recordType", "recordAmount", "recordExplain"},
                                    new int[]{R.id.record_id,R.id.record_icon, R.id.record_date, R.id.record_type, R.id.record_amount, R.id.record_explain}
                            );
                            query_list.setAdapter(simpleAdapter);

                            query_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    ListView tempList=(ListView)parent;
                                    View mView=tempList.getChildAt(position);

                                    recordid=mView.findViewById(R.id.record_id);
                                    recorddate=mView.findViewById(R.id.record_date);
                                    recordtype=mView.findViewById(R.id.record_type);
                                    recordamount=mView.findViewById(R.id.record_amount);
                                    recordExplain=mView.findViewById(R.id.record_explain);

                                    final ArrayList<String> list1=new ArrayList<>();
                                    list1.add(recordid.getText().toString());
                                    list1.add(recorddate.getText().toString());
                                    list1.add(recordtype.getText().toString());
                                    list1.add(recordamount.getText().toString());
                                    list1.add(recordExplain.getText().toString());

                                    AlertDialog.Builder builder= new AlertDialog.Builder(ManagerActivity.this);
                                    builder.setIcon(R.drawable.ic_launcher_background);
                                    builder.setTitle("操作");
                                    builder.setMessage("请点击所需要的操作");
                                    builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Thread thread=new Thread(new DeleteThread());
                                            thread.start();
                                        }
                                    });
                                    builder.setNegativeButton("修改", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent1=new Intent(getApplicationContext(),UpdateActivity.class);
                                            intent1.putStringArrayListExtra("list1",list1);
                                            startActivityForResult(intent1,1);
                                        }
                                    });
                                    AlertDialog b=builder.create();
                                    b.show();
                                }
                            });
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

    public class QueryTypeThread implements Runnable {
        @Override
        public void run() {

            URL url = null;
            try {
                url = new URL(httpUrl_TYPE);
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
                    String params = "recordOwner=" + URLEncoder.encode(recordOwner, "utf-8");
                    //params += "&queryType=" + URLEncoder.encode(edt_query.getText().toString(), "utf-8");
                    outputStream.write(params.getBytes());
                    outputStream.flush();
                    outputStream.close();
                    int code = httpURLConnection.getResponseCode();
                    if (code == 200) {
                        InputStream inputStream = httpURLConnection.getInputStream();
                        byte[] buffer = new byte[1024];
                        int count = inputStream.read(buffer);
                        String result = new String(buffer, 0, count);
                        JSONArray jsonArray = new JSONArray(result);
                        Log.i("Test", result);
                        if (result.equals("查询失败")) {
                            msg = result;
                            handler.post(new updateUIThread());
                        } else {
                            //Record record=new Record();
                            ArrayList<Record> list = new ArrayList<Record>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Record record=new Record();
                                if (jsonObject.getString("recordType").equals(query_type_in_out)){
                                    if (jsonObject.getString("recordType").equals("收入")) {
                                        record.setRecordIcon(record_icons[0]);
                                    } else {
                                        record.setRecordIcon(record_icons[1]);
                                    }
                                    record.setRecordId(jsonObject.getInt("recordId"));
                                    record.setRecordDate(jsonObject.getString("recordDate"));
                                    record.setRecordType(jsonObject.getString("recordType"));
                                    record.setRecordAmount(jsonObject.getDouble("recordAmount"));
                                    record.setRecordExplain(jsonObject.getString("recordExplain"));
                                    list.add(record);
                                }
                            }
                            Intent intent=new Intent(getApplicationContext(),QueryActivity.class);
                            intent.putExtra("ownerId",tv_ownerId.getText().toString());
                            intent.putExtra("query_info",query_type_in_out);
                            intent.putParcelableArrayListExtra("list", list);
                            startActivity(intent);
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

    public class QueryDateThread implements Runnable {
        @Override
        public void run() {

            URL url = null;
            try {
                url = new URL(httpUrl_DATE);
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
                    String params = "recordOwner=" + URLEncoder.encode(recordOwner, "utf-8");
                    params += "&Date_start=" + URLEncoder.encode(edt_query_start.getText().toString(), "utf-8");
                    params += "&Date_end=" + URLEncoder.encode(edt_query_end.getText().toString(), "utf-8");
                    outputStream.write(params.getBytes());
                    outputStream.flush();
                    outputStream.close();
                    int code = httpURLConnection.getResponseCode();
                    if (code == 200) {
                        InputStream inputStream = httpURLConnection.getInputStream();
                        byte[] buffer = new byte[1024];
                        int count = inputStream.read(buffer);
                        String result = new String(buffer, 0, count);
                        JSONArray jsonArray = new JSONArray(result);
                        Log.i("Test", result);
                        if (result.equals("查询失败")) {
                            msg = result;
                            handler.post(new updateUIThread());
                        } else {
                            ArrayList<Record> list = new ArrayList<Record>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Record record=new Record();
                                if (jsonObject.getString("recordType").equals("收入")) {
                                    record.setRecordIcon(record_icons[0]);
                                } else {
                                    record.setRecordIcon(record_icons[1]);
                                }
                                    record.setRecordId(jsonObject.getInt("recordId"));
                                    record.setRecordDate(jsonObject.getString("recordDate"));
                                    record.setRecordType(jsonObject.getString("recordType"));
                                    record.setRecordAmount(jsonObject.getDouble("recordAmount"));
                                    record.setRecordExplain(jsonObject.getString("recordExplain"));
                                    list.add(record);
                            }
                            Intent intent=new Intent(getApplicationContext(),QueryActivity.class);
                            intent.putExtra("ownerId",tv_ownerId.getText().toString());
                            intent.putExtra("query_info","时间段");
                            intent.putParcelableArrayListExtra("list", list);
                            startActivity(intent);
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

    public class DeleteThread implements Runnable {
        @Override
        public void run() {
            URL url = null;
            try {
                url = new URL(httpUrl_Delete);
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
                    String recordId=recordid.getText().toString();
                    String params="recordId="+ URLEncoder.encode(recordId,"utf-8");
                    outputStream.write(params.getBytes());
                    outputStream.flush();
                    outputStream.close();
                    int code = httpURLConnection.getResponseCode();
                    if (code == 200) {
                        InputStream inputStream = httpURLConnection.getInputStream();
                        byte[] buffer = new byte[1024];
                        int count = inputStream.read(buffer);
                        String result = new String(buffer, 0, count);
                        Log.i("Test", result);
                        if (result.equals("删除成功")) {
                            msg = result;
                            handler.post(new updateUIThread());
                        } else {
                            msg = result;
                            handler.post(new updateUIThread());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1){
            if(resultCode==RESULT_OK){
                ArrayList<String> result=data.getStringArrayListExtra("list2");
                recordid.setText(result.get(0));
                recorddate.setText(result.get(1));
                recordtype.setText(result.get(2));
                recordamount.setText(result.get(3));
                recordExplain.setText(result.get(4));
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
