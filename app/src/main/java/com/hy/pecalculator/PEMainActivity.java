package com.hy.pecalculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
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
    private EditText targetMonthET;
    private Spinner spinner;
    private String selectedIndexType = "all";
    private RadioButton rbHaveWeight;
    private RadioButton rbNoWeight;
    private CheckBox cbUseOldData;
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
        targetMonthET = findViewById(R.id.target_month);
        spinner = findViewById(R.id.index_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,new String[]{IndexType.ALL.type,IndexType.HS300.type,IndexType.ZZ500.type,
                IndexType.ZZHB.type,IndexType.ZZCM.type,IndexType.ZZHL.type,IndexType.QZXF.type,IndexType.QZYY.type,IndexType.QZJR.type,IndexType.QZXX.type,IndexType.ZZ800.type,IndexType.ZZ1000.type});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:selectedIndexType = IndexType.ALL.value;break;
                    case 1:selectedIndexType = IndexType.HS300.value;break;
                    case 2:selectedIndexType = IndexType.ZZ500.value;break;
                    case 3:selectedIndexType = IndexType.ZZHB.value;break;
                    case 4:selectedIndexType = IndexType.ZZCM.value;break;
                    case 5:selectedIndexType = IndexType.ZZHL.value;break;
                    case 6:selectedIndexType = IndexType.QZXF.value;break;
                    case 7:selectedIndexType = IndexType.QZYY.value;break;
                    case 8:selectedIndexType = IndexType.QZJR.value;break;
                    case 9:selectedIndexType = IndexType.QZXX.value;break;
                    case 10:selectedIndexType = IndexType.ZZ800.value;break;
                    case 11:selectedIndexType = IndexType.ZZ1000.value;break;
                }
                Log.d("yue.huang",position+":"+selectedIndexType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rbHaveWeight = findViewById(R.id.have_weight);
        rbNoWeight = findViewById(R.id.no_weight);
        cbUseOldData = findViewById(R.id.use_old_data);
        cbUseOldData.setChecked(true);
        rbNoWeight.setChecked(true);

        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> targetList = new ArrayList<>();
                List<String> monthList = getRangeSet(startTv.getText().toString(),endTv.getText().toString());
                for(String month : monthList){
                    if(targetMonthET.getText().toString().contains(month.substring(month.length()-2))){
                        Log.d("yue.huang",getLastDayInMonth(month));
                        targetList.add(getLastDayInMonth(month));
                    }
                }
                if(targetList.size() == 0){
                    Toast.makeText(PEMainActivity.this,"时间范围有问题",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(PEMainActivity.this, PEChartActivity.class);
                    intent.putStringArrayListExtra("month_list",targetList);
                    intent.putExtra("index_type",selectedIndexType);
                    intent.putExtra("have_weight",rbHaveWeight.isChecked());
                    intent.putExtra("use_old_data", cbUseOldData.isChecked());
                    startActivity(intent);
                }
            }
        });
    }




    /**
     * 获取任意月中最后一天
     * @param month yyyy-MM格式的月份
     */
    private String getLastDayInMonth(String month){
        try {
            SimpleDateFormat sdfYM = new SimpleDateFormat("yyyy-MM");
            SimpleDateFormat sdfYMD = new SimpleDateFormat("yyyyMMdd");
            Date date = sdfYM.parse(month);

            Calendar instance = Calendar.getInstance();
            instance.setTime(date);
            if(Calendar.getInstance().get(Calendar.YEAR) == instance.get(Calendar.YEAR)
                    && Calendar.getInstance().get(Calendar.MONTH) == instance.get(Calendar.MONTH)){
                //只有传入的时间不是当前月份时才返回传入月份的最后一天，如果传入的月份是当前月分则返回当前日期
                return sdfYMD.format(Calendar.getInstance().getTime());
            }else {
                instance.add(Calendar.MONTH, 1);//月份+1
                instance.set(Calendar.DAY_OF_MONTH, 1);//天设为一个月的第一天
                instance.add(Calendar.DAY_OF_MONTH, -1);//本月最后一天
                return sdfYMD.format(instance.getTime());
            }
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
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
