package com.example.mem;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.example.mem.database.MyDB;
import com.example.mem.alarms.alarm;
public class Edit extends AppCompatActivity implements View.OnClickListener{
    private final static String TAG = "Edit";
    MyDB myDB;
    private Button btnSave;
    private Button btnBack;
    private TextView editTime;
    private EditText editTitle;
    private EditText editBody;
    private AlertDialog.Builder dialog;
    private Button btnNotice;
    private String dispCreateDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);
        init();
        if (editTime.getText().length()==0)
            editTime.setText(dispCreateDate);
    }
     //初始化函数
    void init(){
        myDB = new MyDB(this);
        btnBack = findViewById(R.id.button_back);
        btnSave = findViewById(R.id.button_save);
        editTitle = findViewById(R.id.edit_title);
        editBody = findViewById(R.id.edit_body);
        editTime = findViewById(R.id.edit_title_time);

        btnNotice = findViewById(R.id.btn_edit_menu_notice);

        btnSave.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnNotice.setOnClickListener(this);
//        获取当前的时间
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dispCreateDate =simpleDateFormat.format(date);
        editTime.setText(dispCreateDate);
    }
     //按钮点击事件监听
    @Override
    public void onClick(View v) {
        String title;
        String body;
        title = editTitle.getText().toString();
        body = editBody.getText().toString();
        switch (v.getId()){
            case R.id.button_save:
//                保存信息
                if (saveFunction(title,body, dispCreateDate)){
                    intentStart();
                }
                break;
//                返回按钮
            case R.id.button_back:
                if (!"".equals(title)||!"".equals(body)){
                    showDialog(title,body, dispCreateDate);
//                    clearDialog();
                } else {
                    intentStart();
                }
                break;

            case R.id.btn_edit_menu_notice:
                setNoticeDate();
                break;
            default:
                break;
        }
    }
    //返回主界面
    void intentStart(){
        Intent intent = new Intent(Edit.this,MainActivity.class);
        startActivity(intent);
        this.finish();
    }


     //备忘录保存函数

    boolean saveFunction(String title,String body,String dispCreateDate){

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
            SQLiteDatabase db;
            ContentValues values;
            //  存储备忘录信息
            db = myDB.getWritableDatabase();
            values = new ContentValues();
            values.put(MyDB.RECORD_TITLE,title);
            values.put(MyDB.RECORD_BODY,body);
            values.put(MyDB.RECORD_TIME,dispCreateDate);
//            闹钟
            db.insert(MyDB.TABLE_NAME_RECORD,null,values);
            Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();
            db.close();
        }
        return flag;
    }
    //弹窗函数
    void showDialog(final String title, final String body, final String dispCreateDate){
        dialog = new AlertDialog.Builder(Edit.this);
        dialog.setTitle("提示");
        dialog.setMessage("是否保存当前编辑内容");
        dialog.setPositiveButton("保存",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveFunction(title, body, dispCreateDate);
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
    /*
     *  设置闹钟时间函数
     */
    void setNoticeDate(){
        Intent intent=new Intent();
        intent.setClass(Edit.this,alarm.class);
        Edit.this.startActivity(intent);
    }



}
