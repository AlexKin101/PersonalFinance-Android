package com.example.personalfinancedemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class QueryActivity extends AppCompatActivity {

    SimpleAdapter simpleAdapter;

    TextView tv_ownerId_query;
    TextView tv_query_info;

    TextView recordid,recorddate,recordtype,recordamount,recordExplain;

    String httpUrl_Delete="http://10.0.2.2:8080/PF2/delete";
    String msg = "";
    Handler handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        final Intent intent=getIntent();

        tv_ownerId_query = findViewById(R.id.tv_ownerId_query);
        tv_query_info=findViewById(R.id.tv_query_info);
        tv_ownerId_query.setText(intent.getStringExtra("ownerId"));
        tv_query_info.setText(intent.getStringExtra("query_info"));
        ArrayList<Record> recordsList=intent.getParcelableArrayListExtra("list");
        ArrayList<Map<String,String>> list=new ArrayList<>();

        for(int i=0;i<recordsList.size();i++){
            Record record=recordsList.get(i);
            Map<String,String> map=new HashMap<>();
            map.put("recordId",String.valueOf(record.getRecordId()));
            map.put("recordIcon",String.valueOf(record.getRecordIcon()));
            map.put("recordDate",record.getRecordDate());
            map.put("recordType",record.getRecordType());
            map.put("recordAmount",String.valueOf(record.getRecordAmount()));
            map.put("recordExplain",record.getRecordExplain());
            list.add(map);
        }

        ListView spquery_list = findViewById(R.id.spquery_list);
        simpleAdapter = new SimpleAdapter(
                getApplicationContext(),
                list,
                R.layout.listitemlayout_manager,
                new String[]{"recordId","recordIcon", "recordDate", "recordType", "recordAmount", "recordExplain"},
                new int[]{R.id.record_id,R.id.record_icon, R.id.record_date, R.id.record_type, R.id.record_amount, R.id.record_explain}
        );
        spquery_list.setAdapter(simpleAdapter);

        spquery_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ListView tempList=(ListView)parent;
                View mView = tempList.getChildAt(position - tempList.getFirstVisiblePosition());
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

                AlertDialog.Builder builder= new AlertDialog.Builder(QueryActivity.this);
                builder.setMessage("请点击所需要的操作");
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(QueryActivity.this);
                        builder.setMessage("确定删除吗");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Thread thread=new Thread(new QueryActivity.DeleteThread());
                                thread.start();
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        AlertDialog ad = builder.create();
                        ad.show();
                    }
                });
                builder.setNegativeButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent1=new Intent(QueryActivity.this,UpdateActivity.class);
                        intent1.putStringArrayListExtra("list1",list1);
                        startActivityForResult(intent1,1);
                    }
                });
                AlertDialog b=builder.create();
                b.show();
            }
        });
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

    public class updateUIThread implements Runnable {
        @Override
        public void run() {
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
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
}
