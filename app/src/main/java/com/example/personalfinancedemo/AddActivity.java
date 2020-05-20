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

public class AddActivity extends AppCompatActivity {

    EditText edt_recordDate_add,edt_recordAmount_add,edt_recordExplain_add;
    TextView tv_ownerId_add;
    Spinner spinner3;
    String[] record_types={"收入","支出"};

    String httpUrl_add="http://10.0.2.2:8080/PF2/add";
    String msg = "";
    Handler handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Intent intent1=getIntent();

        edt_recordDate_add = findViewById(R.id.edt_recordDate_add);
        edt_recordAmount_add = findViewById(R.id.edt_recordAmount_add);
        edt_recordExplain_add = findViewById(R.id.edt_recordExplain_add);
        tv_ownerId_add=findViewById(R.id.tv_ownerId_add);
        spinner3 = findViewById(R.id.spinner3);

        tv_ownerId_add.setText(intent1.getStringExtra("ownerId"));
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, record_types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(adapter);

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //TextView tv=(TextView)view;
                //tv.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        QMUIRoundButton btn_RecordAddsubmit = findViewById(R.id.btn_RecordAddsubmit);
        btn_RecordAddsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new AddThread());
                thread.start();
            }
        });

        QMUIRoundButton btn_RecordAddreset = findViewById(R.id.btn_RecordAddreset);
        btn_RecordAddreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_recordDate_add.setText("");
                edt_recordAmount_add.setText("");
                edt_recordExplain_add.setText("");
                spinner3.setSelection(0);
            }
        });
    }

    public class AddThread implements Runnable {

        @Override
        public void run() {
            URL url = null;
            try {
                url = new URL(httpUrl_add);
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
                    String recordDate = edt_recordDate_add.getText().toString();
                    String recordType = spinner3.getSelectedItem().toString();
                    String recordAmount = edt_recordAmount_add.getText().toString();
                    String recordExplain=edt_recordExplain_add.getText().toString();
                    String recordOwner=tv_ownerId_add.getText().toString();
                    String params = "recordDate=" + URLEncoder.encode(recordDate, "utf-8");
                    params += "&recordType=" + URLEncoder.encode(recordType, "utf-8");
                    params += "&recordAmount=" + URLEncoder.encode(recordAmount, "utf-8");
                    params += "&recordExplain=" + URLEncoder.encode(recordExplain, "utf-8");
                    params += "&recordOwner=" + URLEncoder.encode(recordOwner, "utf-8");
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
                        if (result.equals("新增失败")) {
                            msg = result;
                            handler.post(new updateUIThread());
                            setResult(RESULT_CANCELED);
                        } else {
                            msg = result;
                            handler.post(new updateUIThread());
                            Intent intent= new Intent(AddActivity.this,ManagerActivity.class);
                            setResult(RESULT_OK,intent);
                            finish();
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
}


