
package com.example.mem.alarms;



import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mem.R;

import java.util.Calendar;

public class alarm extends AppCompatActivity {
    TimePicker timePicker;
    Calendar calendar;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clock);
        //获取一个日历对象
        calendar = Calendar.getInstance();
        //获取时间提取器组件
        timePicker = findViewById(R.id.timePicker1);
        //设置使用二十四小时制
        timePicker.setIs24HourView(true);
        //获取“设置闹钟”按钮
        Button button = findViewById(R.id.button7);
        Button button1 = findViewById(R.id.button8);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建Intent
                Intent intent = new Intent(alarm.this, AalarmActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(alarm.this, 0, intent, 0);
                // 获取AlarmManager对象
                AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                //设置小时数
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                //设置分钟数
                calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                //AlarmManager
                calendar.set(Calendar.SECOND, 0);
                //创建闹钟
                alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                Toast.makeText(alarm.this, "已设置稍后提醒。", Toast.LENGTH_SHORT).show();
                finish();
            }

        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}