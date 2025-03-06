package com.hy.pecalculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PEMainActivity extends Activity {
    private TextView clickedView;
    private TextView startTv;
    private TextView endTv;
    private TimePickerView pvTime;
    private Spinner spinner;
    private String selectedIndexType = "all";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pemain);
        Calendar startDate = Calendar.getInstance();
        startDate.set(1995,0,1);
        pvTime = new TimePickerView.Builder(PEMainActivity.this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                clickedView.setText(new SimpleDateFormat("yyyy-MM").format(date));
            }
        }).setType(new boolean[]{true,true,false,false,false,false}).setRangDate(startDate,Calendar.getInstance()).setDate(Calendar.getInstance()).build();
        startTv = findViewById(R.id.start_month);
        endTv = findViewById(R.id.end_month);
        startTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedView = startTv;
                pvTime.show();
            }
        });
        endTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedView = endTv;
                pvTime.show();
            }
        });
        spinner = findViewById(R.id.index_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,new String[]{IndexType.ALL.type,IndexType.HS300.type,IndexType.ZZ500.type,
                IndexType.SWHB.type,IndexType.SWCM.type,IndexType.SZXF.type,IndexType.SZYY.type,IndexType.SZJR.type,IndexType.SZXX.type});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:selectedIndexType = IndexType.ALL.value;break;
                    case 1:selectedIndexType = IndexType.HS300.value;break;
                    case 2:selectedIndexType = IndexType.ZZ500.value;break;
                    case 3:selectedIndexType = IndexType.SWHB.value;break;
                    case 4:selectedIndexType = IndexType.SWCM.value;break;
                    case 6:selectedIndexType = IndexType.SZXF.value;break;
                    case 7:selectedIndexType = IndexType.SZYY.value;break;
                    case 8:selectedIndexType = IndexType.SZJR.value;break;
                    case 9:selectedIndexType = IndexType.SZXX.value;break;
                }
                Log.d("yue.huang",position+":"+selectedIndexType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> monthList = getRangeSet(startTv.getText().toString(),endTv.getText().toString());
                if(monthList.size() == 0){
                    Toast.makeText(PEMainActivity.this,"时间范围有问题",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(PEMainActivity.this, PEChartActivity.class);
                    intent.putStringArrayListExtra("month_list",(ArrayList<String>) monthList);
                    intent.putExtra("index_type",selectedIndexType);
                    startActivity(intent);
                }
            }
        });
    }









    /**
     * 获取时间范围内月份集合
     * @param beginDate
     * @param endDate
     * @return
     */
    public List<String> getRangeSet(String beginDate,String endDate){
        /*Date1.after(Date2),当Date1大于Date2时，返回TRUE，当小于等于时，返回false；
          Date1.before(Date2)，当Date1小于Date2时，返回TRUE，当大于等于时，返回false；*/
        List<String> rangeSet = new java.util.ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date begin_date = null;
        Date end_date = null;
        try {
            begin_date = sdf.parse(beginDate);//定义起始日期
            end_date = sdf.parse(endDate);//定义结束日期
            Calendar dd = Calendar.getInstance();//定义日期实例
            dd.setTime(begin_date);//设置日期起始时间
            while(!dd.getTime().after(end_date)){//判断是否到结束日期
                rangeSet.add(sdf.format(dd.getTime()));
                dd.add(Calendar.MONTH, 1);//进行当前日期月份加1
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return rangeSet;
    }






    /**
     * 找出任意月中最后一个周五
     * @param month yyyy-MM格式的月份
     */
    private void getFive(String month){
        try {
            SimpleDateFormat sdfYM = new SimpleDateFormat("yyyy-MM");
            SimpleDateFormat sdfYMD = new SimpleDateFormat("yyyy-MM-dd");

            Date date = sdfYM.parse(month);
            Calendar instance = Calendar.getInstance();
            instance.setTime(date);
            instance.add(Calendar.MONTH, 1);//月份+1
            instance.set(Calendar.DAY_OF_MONTH, 1);//天设为一个月的第一天
            instance.add(Calendar.DAY_OF_MONTH, -1);//本月最后一天
            int offset = 0;
            int dayOfWeek = instance.get(Calendar.DAY_OF_WEEK);
            if(dayOfWeek<6){offset = -7+(6-dayOfWeek);}
            else {offset = 6-dayOfWeek;}
            instance.add(Calendar.DAY_OF_MONTH, offset);//根据月末最后一天是星期几，向前偏移至最近的周五
            Log.d("yue.huang",sdfYMD.format(instance.getTime()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
