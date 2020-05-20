package com.example.personalfinancedemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class RegisterActivity extends AppCompatActivity {

    EditText edt_reguserid, edt_reguserpwd, edt_regemail;
    int iconResourceId = R.drawable.tx1;
    RoundImageView imageView;
    int defaultIconId = R.drawable.tx1;

    String httpUrl = "http://10.0.2.2:8080/PF2/register";
    String msg = "";
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        edt_reguserid = findViewById(R.id.edt_reguserid);
        edt_reguserpwd = findViewById(R.id.edt_reguserpwd);
        edt_regemail = findViewById(R.id.edt_regemail);
        imageView = findViewById(R.id.imageView);

        QMUIRoundButton btn_registersubmit = findViewById(R.id.btn_registersubmit);
        btn_registersubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new RegisterThread());
                thread.start();
            }
        });

        QMUIRoundButton btn_registerreset = findViewById(R.id.btn_registerreset);
        btn_registerreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_reguserid.setText("");
                edt_reguserpwd.setText("");
                edt_regemail.setText("");
            }
        });

        Button btn_changeIcon = findViewById(R.id.btn_changeIcon);
        btn_changeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectIconActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            int id = data.getIntExtra("iconResourceId", R.drawable.tx1);
            imageView.setImageResource(id);
            defaultIconId = id;
        }
    }


    public class RegisterThread implements Runnable {

        @Override
        public void run() {
            URL url = null;
            try {
                url = new URL(httpUrl);
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
                    String userId = edt_reguserid.getText().toString();
                    String userPwd = edt_reguserpwd.getText().toString();
                    String userEmail = edt_regemail.getText().toString();
                    String params = "userId=" + URLEncoder.encode(userId, "utf-8");
                    params += "&userPwd=" + URLEncoder.encode(userPwd, "utf-8");
                    params += "&userEmail=" + URLEncoder.encode(userEmail, "utf-8");
                    params += "&userIcon=" + URLEncoder.encode(String.valueOf(defaultIconId), "utf-8");
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
                            Intent intent= new Intent(getApplicationContext(),LoginActivity.class);
                            intent.putExtra("userId", userId);
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                } catch (IOException e) {
                    e.printStackTrace();
                    msg="注册失败";
                    handler.post(new updateUIThread());
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
