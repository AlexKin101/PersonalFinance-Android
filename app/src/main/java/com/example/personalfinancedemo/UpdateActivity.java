package com.example.personalfinancedemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class UpdateActivity extends AppCompatActivity {

    EditText edt_recordDate, edt_recordAmount, edt_recordExplain;
    TextView tv_update_id;
    Spinner spinner2;
    String[] record_types = {"收入", "支出"};

    String httpUrl_Update = "http://10.0.2.2:8080/PF2/modify";
    String msg = "";
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        Intent intent = getIntent();
        ArrayList<String> list = new ArrayList<String>();
        list = intent.getStringArrayListExtra("list1");
        String recordId = list.get(0);
        final String recordDate = list.get(1);
        final String recordtype = list.get(2);
        final String recordamount = list.get(3);
        final String recordExplain = list.get(4);

        edt_recordDate = findViewById(R.id.edt_recordDate);
        edt_recordAmount = findViewById(R.id.edt_recordAmount);
        edt_recordExplain = findViewById(R.id.edt_recordExplain);
        tv_update_id = findViewById(R.id.tv_update_id);

        edt_recordDate.setText(recordDate);
        edt_recordAmount.setText(recordamount);
        edt_recordExplain.setText(recordExplain);
        tv_update_id.setText(recordId);

        spinner2 = findViewById(R.id.spinner2);


        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, record_types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter);

        if (recordtype.equals("收入")) {
            adapter.notifyDataSetChanged();
            spinner2.setSelection(0);
        } else {
            adapter.notifyDataSetChanged();
            spinner2.setSelection(1);
        }

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //TextView tv=(TextView)view;
                //tv.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        QMUIRoundButton btn_RecordUpdatesubmit = findViewById(R.id.btn_RecordUpdatesubmit);
        btn_RecordUpdatesubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new ModifyThread());
                thread.start();
            }
        });

        QMUIRoundButton btn_RecordUpdatereset = findViewById(R.id.btn_RecordUpdatereset);
        btn_RecordUpdatereset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_recordDate.setText(recordDate);
                edt_recordAmount.setText(recordamount);
                edt_recordExplain.setText(recordExplain);

                if (recordtype.equals("收入")) {
                    adapter.notifyDataSetChanged();
                    spinner2.setSelection(0);
                } else {
                    adapter.notifyDataSetChanged();
                    spinner2.setSelection(1);
                }
            }
        });
    }

    public class ModifyThread implements Runnable {
        @Override
        public void run() {
            URL url = null;
            try {
                url = new URL(httpUrl_Update);
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
                    String recordId = tv_update_id.getText().toString();
                    String recordDate = edt_recordDate.getText().toString();
                    String recordtype = spinner2.getSelectedItem().toString();
                    String recordamount = edt_recordAmount.getText().toString();
                    String recordExplain = edt_recordExplain.getText().toString();
                    String params = "recordId=" + URLEncoder.encode(recordId, "utf-8");
                    params += "&recordDate=" + URLEncoder.encode(recordDate, "utf-8");
                    params += "&recordType=" + URLEncoder.encode(recordtype, "utf-8");
                    params += "&recordAmount=" + URLEncoder.encode(String.valueOf(recordamount), "utf-8");
                    params += "&recordExplain=" + URLEncoder.encode(recordExplain, "utf-8");
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
                        msg = result;
                        handler.post(new updateUIThread());
                        ArrayList<String> list = new ArrayList<>();
                        list.add(tv_update_id.getText().toString());
                        list.add(edt_recordDate.getText().toString());
                        list.add(spinner2.getSelectedItem().toString());
                        list.add(edt_recordAmount.getText().toString());
                        list.add(edt_recordExplain.getText().toString());
                        Intent intent1 = new Intent(UpdateActivity.this, QueryActivity.class);
                        Intent intent2 = new Intent(UpdateActivity.this, ManagerActivity.class);
                        intent1.putStringArrayListExtra("list2", list);
                        intent2.putStringArrayListExtra("list2", list);
                        setResult(RESULT_OK, intent1);
                        setResult(RESULT_OK, intent2);
                        finish();
                    }
                } catch (IOException e) {
                    msg = "修改失败";
                    handler.post(new updateUIThread());
                    e.printStackTrace();
                    setResult(RESULT_CANCELED);
                    finish();
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

