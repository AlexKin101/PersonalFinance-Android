package com.example.personalfinancedemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {

    EditText edt_loginuserid, edt_loginpwd;
    QMUIRoundButton btn_login;

    String httpUrl = "http://10.0.2.2:8080/PF2/login";
    String msg = "";
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent intent=getIntent();
        String userId=intent.getStringExtra("userId");

        edt_loginuserid = findViewById(R.id.edt_loginuserid);
        edt_loginpwd = findViewById(R.id.edt_loginpwd);

//        if(userId.length()>0){
//            edt_loginuserid.setText(userId);
//        }

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new LoginThread());
                thread.start();
            }
        });

        QMUIRoundButton btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent1);
            }
        });
    }

    public class LoginThread implements Runnable {

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
                    httpURLConnection.setConnectTimeout(2000);
                    httpURLConnection.connect();
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    String userId = edt_loginuserid.getText().toString();
                    String userPwd = edt_loginpwd.getText().toString();
                    String params = "userId=" + URLEncoder.encode(userId, "utf-8");
                    params += "&userPwd=" + URLEncoder.encode(userPwd, "utf-8");
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
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getInt("isLoginOK") == 0) {
                            msg = "登录失败";
                            handler.post(new updateUIThread());

                        } else {
                            msg = "登录成功";
                            handler.post(new updateUIThread());
                            String ownerId=jsonObject.getString("userId");
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("ownerId",ownerId);
                            startActivity(intent);
                        }
                    }
                } catch (SocketTimeoutException e) {
                    msg = "网络连接超时";
                    handler.post(new updateUIThread());
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

