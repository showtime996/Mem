package com.example.mem;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.example.mem.database.MyDB;
import com.example.mem.module.Record;
public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener{
//实现多个接口 主界面的列表 适配器
    private final static String TAG = "MainActivity";
    MyDB myDB;
    private ListView myListView;
    private Button createButton;
    private MyBaseAdapter myBaseAdapter;
    private Button group;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();
    }
    //初始化控件
    private void init(){
        createButton = findViewById(R.id.createButton);
        createButton.setOnClickListener(this);
        group=findViewById(R.id.group);
        group.setOnClickListener(this);
        myListView = findViewById(R.id.list_view);
        //获取arrayList中的元素个数 get(); 获取arrayList中的下标对象
        List<Record> recordList = new ArrayList<>();
        myDB = new MyDB(this);
//        SQLiteDatabase既代表与数据库的连接，又只能用于执行sql语句操作
        SQLiteDatabase db = myDB.getReadableDatabase();
//        创建游标进行数据的查询
        Cursor cursor = db.query(MyDB.TABLE_NAME_RECORD,null,
                null,null,null,
                null,MyDB.RECORD_TIME+" DESC");
//        使用moveToNext方式
        if(cursor.moveToFirst()){
//            构建数据库的模型
            Record record;
            while (!cursor.isAfterLast()){
                record = new Record();
                record.setId(
                        Integer.valueOf(cursor.getString(cursor.getColumnIndex(MyDB.RECORD_ID))));
                record.setTitleName(
                        cursor.getString(cursor.getColumnIndex(MyDB.RECORD_TITLE))
                );
                record.setTextBody(
                        cursor.getString(cursor.getColumnIndex(MyDB.RECORD_BODY))
                );
                record.setCreateTime(
                        cursor.getString(cursor.getColumnIndex(MyDB.RECORD_TIME)));
                recordList.add(record);
                cursor.moveToNext();
            }
        }
//        查询结束后需要close掉db，避免内存持续消耗
        cursor.close();
        db.close();
        // 创建一个Adapter的实例 适配器创建：通过List集合将数据传入，这里的record是自定义的数据实体，封装了id，titleName，textVBody，createTime四个成员属性。
        myBaseAdapter = new MyBaseAdapter(this,recordList,R.layout.list);
//        ListView
        myListView.setAdapter(myBaseAdapter);
        // 设置点击监听 长点击 短点击
        myListView.setOnItemClickListener(this);
        myListView.setOnItemLongClickListener(this);
    }
//    先创建一个Intent对象，第一个参数Context表示当前Activity，传入当前this即可；
//    第二个参数是要跳转的目标Activity的class，然后调用startActivity()方法，启动跳转；最后finish()掉当前Activity；
//    主界面的新建按钮
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.createButton:
                Intent intent = new Intent(MainActivity.this, Edit.class);
                startActivity(intent);
                MainActivity.this.finish();
                break;
            case R.id.group:
                Intent in = new Intent(MainActivity.this, group_1.class);
                startActivity(in);
                MainActivity.this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int d, long id) {
        //    点击条目，跳转的编辑页面进行更改,将原始数据到目标页面 方法实现是找到目标记录
        Intent intent = new Intent(MainActivity.this,xiugai.class);
//        利用实例模型将数据库的信息输出到修改页面 获取当前位置的id
        Record record = (Record) myListView.getItemAtPosition(d);
        System.out.println("main"+d);
//        传送参数，通过点击的id，输出所相应的参数内容
        intent.putExtra(MyDB.RECORD_TITLE,record.getTitleName().trim());
        intent.putExtra(MyDB.RECORD_BODY,record.getTextBody().trim());
        intent.putExtra(MyDB.RECORD_TIME,record.getCreateTime().trim());
        intent.putExtra(MyDB.RECORD_ID,record.getId().toString().trim());
        this.startActivity(intent);
        MainActivity.this.finish();
    }
//    长按某一条记录，然后弹出询问框进行删除 方法实现是找到目标记录，然后通过一个dialog弹窗
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int d, long id) {
        Record record = (Record) myListView.getItemAtPosition(d);
        showDialog(record,d);
        return true;
    }
    void showDialog(final Record record,final int d){
//    确认删除的时候，根据Id删除指定数据，然后需要对当前的主页面里的数据进行刷新
        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("是否删除？");
        String textBody = record.getTextBody();
//        取出内容 内容长度是否大于150 是的话。。。代替 否则输出内容
        dialog.setMessage(
                textBody.length()>150?textBody.substring(0,150)+"...":textBody);
//        删除确定按钮
        dialog.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = myDB.getWritableDatabase();
//                        根据点击的id进行删除操作
                            db.delete(MyDB.TABLE_NAME_RECORD,
                                MyDB.RECORD_ID +"=?",
                                new String[]{String.valueOf(record.getId())});
                        db.close();
                        Toast.makeText(MainActivity.this,"删除成功!",Toast.LENGTH_SHORT).show();
//                        刷新主页面
                        myBaseAdapter.removeItem(d);
                        myListView.post(new Runnable() {
                            @Override
                            public void run() {
//                                对ListView的中的数据集合中的每一个索引进行检测，如果有修改就会刷新，反之不会刷新；
                                myBaseAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
//        取消按钮
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        dialog.show();
    }
//      ListView展示的适配器类
//      MyBaseAdapter是ListView展示数据所需要的适配器类，继承于BaseAdapter类
    class MyBaseAdapter extends BaseAdapter{
        private List<Record> recordList;//数据集合
        private Context context;
        private int layoutId;
//        初始化
        public MyBaseAdapter(Context context,List<Record> recordList,int layoutId){
            this.context = context;
            this.recordList = recordList;
            this.layoutId = layoutId;
        }

        @Override
        public int getCount() {
            if (recordList!=null&&recordList.size()>0)
                return recordList.size();
            else
                return 0;
        }
//        当前位置
        @Override
        public Object getItem(int d) {
            if (recordList!=null&&recordList.size()>0)
                return recordList.get(d);

            else
                return null;
        }

        public void removeItem(int d){
            this.recordList.remove(d);
        }
//        获取id
        @Override
        public long getItemId(int d) {

            return d;

        }

//        视图设置
        @Override
        public View getView(int d, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
//            缓存的视图
            if (convertView == null) {
                convertView = LayoutInflater.from(
                        getApplicationContext()).inflate(R.layout.list, parent,
                        false);
//                封装的类获取id元素 用封装好的viewHolder类调用文本获取id
                viewHolder  = new ViewHolder();
                viewHolder.titleView = convertView.findViewById(R.id.list_item_title);
                viewHolder.bodyView = convertView.findViewById(R.id.list_item_body);
                viewHolder.timeView = convertView.findViewById(R.id.list_item_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
//            对时间，标题，内容的获取显示在list中
            Record record = recordList.get(d);
            System.out.println("p="+d);
            String tile = record.getTitleName();
//            标题7个 内容13个字后面省略号
            viewHolder.titleView.setText((tile.length()>7?tile.substring(0,7)+"...":tile));
//           viewHolder.titleView.setText(tile);
            String body = record.getTextBody();
            viewHolder.bodyView.setText(body.length()>13?body.substring(0,12)+"...":body);
//           viewHolder.bodyView.setText(body);
            viewHolder.timeView.setText(record.getCreateTime().trim());
            return convertView;
        }
    }


    //ListView里的组件包装类
    class ViewHolder{
        TextView titleView;
        TextView bodyView;
        TextView timeView;
    }


}

