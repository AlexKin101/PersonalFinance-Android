package com.example.personalfinancedemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectIconActivity extends AppCompatActivity {

    ListView listViewIcons;
    int iconResourceId=R.drawable.tx1;
    int[] icons={R.drawable.tx1,R.drawable.tx2,R.drawable.tx3,R.drawable.tx4,R.drawable.tx5,R.drawable.tx6};
    ImageView imageView2;

    ArrayList<Map<String,String>> getData(){
        ArrayList<Map<String,String>> list=new ArrayList<Map<String, String>>();
        for(int i=0;i<6;i++){
            Map<String,String> map=new HashMap<>();
            map.put("icon",String.valueOf(icons[i]));
            list.add(map);
        }
        return list;
    }

    @Override
    public void onBackPressed() {
        //数据是使用Intent返回
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra("iconResourceId",iconResourceId);
        //设置返回数据
        this.setResult(RESULT_OK, intent);
        //关闭Activity
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_icon);
        Intent intent=getIntent();
        intent.getIntExtra("iconRescourceId",R.drawable.tx1);
        imageView2=findViewById(R.id.imageView2);
        imageView2.setImageResource(iconResourceId);
        listViewIcons=findViewById(R.id.listviewicons);
        SimpleAdapter simpleAdapter=new SimpleAdapter(
                getApplicationContext(),
                getData(),
                R.layout.listtiemlayout,
                new String[]{"icon"},
                new int[]{R.id.icon}
                );

        listViewIcons.setAdapter(simpleAdapter);
        listViewIcons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                iconResourceId=icons[position];
                imageView2.setImageResource(iconResourceId);
            }
        });

        QMUIRoundButton btn_setIconOK=findViewById(R.id.btn_setIconOK);
        btn_setIconOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(getApplicationContext(),RegisterActivity.class);
                intent1.putExtra("iconResourceId",iconResourceId);
                setResult(RESULT_OK,intent1);
                finish();
            }
        });

        QMUIRoundButton btn_selectCancel=findViewById(R.id.btn_selectCancel);
        btn_selectCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(getApplicationContext(),RegisterActivity.class);
                setResult(RESULT_CANCELED);
                setResult(RESULT_OK,intent1);
                finish();
            }
        });
    }
}
