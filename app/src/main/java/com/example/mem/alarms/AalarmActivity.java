package com.example.mem.alarms;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mem.R;

public class AalarmActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        final AlertDialog alertDialog=new AlertDialog.Builder(this).create();
        //标题
        alertDialog.setTitle("闹钟");
        //显示内容
        alertDialog.setMessage("闹钟响啦！！！！");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "我知道了",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Intent intent=new Intent();
////                  设置行为应用程序入口
//                intent.setAction(Intent.ACTION_MAIN);
////                  Category返回桌面
//                intent.addCategory(Intent.CATEGORY_HOME);
//                startActivity(intent);
                finish();
            }
        });
        //对话框显示
        alertDialog.show();
    }
}

