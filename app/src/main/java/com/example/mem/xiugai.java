package com.example.mem;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.example.mem.alarms.alarm;
import com.example.mem.database.MyDB;
import com.example.mem.module.Record;

public class xiugai extends AppCompatActivity implements View.OnClickListener{

    MyDB myDB;
    private Button btnSave;
    private Button btnBack;
    private TextView amendTime;
    private EditText amendTitle;
    private EditText amendBody;
    private Record record;
    private AlertDialog.Builder dialog;
    private Button btnNotice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xiugai);
        init();

    }


    @Override
    public void onClick(View v) {
        String body;
        String group;
        group= amendTitle.getText().toString();
        body = amendBody.getText().toString();
        switch (v.getId()){
//            保存按钮
            case R.id.button_save:
                if (updateFunction(group,body)){
                    intentStart();
                }
                break;
//                返回按钮
            case R.id.button_back:
                showDialog(group,body);

                break;
            case R.id.btn_amend_menu_notice:
                setNoticeDate();
                break;
            default:
                break;
        }
    }

    //初始化函数

    void init(){
        myDB = new MyDB(this);
        btnBack = findViewById(R.id.button_back);
        btnSave = findViewById(R.id.button_save);
        amendTitle = findViewById(R.id.amend_title);
        amendBody = findViewById(R.id.amend_body);
        amendTime = findViewById(R.id.amend_title_time);
        
        btnNotice = findViewById(R.id.btn_amend_menu_notice);

        btnSave.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        btnNotice.setOnClickListener(this);

        Intent intent = this.getIntent();
        if (intent!=null){

            record = new Record();

            record.setId(Integer.valueOf(intent.getStringExtra(MyDB.RECORD_ID)));
            record.setTitleName(intent.getStringExtra(MyDB.RECORD_TITLE));
            record.setTextBody(intent.getStringExtra(MyDB.RECORD_BODY));
            record.setCreateTime(intent.getStringExtra(MyDB.RECORD_TIME));
            amendTitle.setText(record.getTitleName());
            amendTime.setText(record.getCreateTime());
            amendBody.setText(record.getTextBody());
        }
    }
    void setNoticeDate(){
        Intent intent=new Intent();
        intent.setClass(xiugai.this, alarm.class);
        xiugai.this.startActivity(intent);
    }

     //返回主界面

    void intentStart(){
        Intent intent = new Intent(xiugai.this,MainActivity.class);
        startActivity(intent);
        this.finish();
    }


    //保存函数

    boolean updateFunction(String title,String body){

        SQLiteDatabase db;
        ContentValues values;
        boolean flag = true;
        if ("".equals(title)){
            Toast.makeText(this,"标题不能为空",Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if (title.length()>10){
            Toast.makeText(this,"标题过长",Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if (body.length()>200){
            Toast.makeText(this,"内容过长",Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if(flag){
            // update
            db = myDB.getWritableDatabase();
            values = new ContentValues();
            values.put(MyDB.RECORD_BODY,body);
            values.put(MyDB.RECORD_TIME,getNowTime());
            values.put(MyDB.RECORD_TITLE,title);
            db.update(MyDB.TABLE_NAME_RECORD,values,MyDB.RECORD_ID +"=?",
                    new String[]{record.getId().toString()});
            Toast.makeText(this,"修改成功",Toast.LENGTH_SHORT).show();
            db.close();
        }
        return flag;
    }


     // 弹窗函数

    void showDialog(final String title,final String body){
        dialog = new AlertDialog.Builder(xiugai.this);
        dialog.setTitle("提示");
        dialog.setMessage("是否保存当前编辑内容");
        dialog.setPositiveButton("保存",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateFunction(title,body);
                intentStart();
                    }
                });

        dialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intentStart();
                    }
                });
        dialog.show();
    }

    // 得到当前时间

    String getNowTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

}
