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

import androidx.annotation.Nullable;

import com.bigkoo.pickerview.TimePickerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by hy on 2025-03-01
 * Describe:
 */
public class PEMonthActivity extends Activity {
    private TimePickerView pvTime;
    private TextView monthTv;
    private String selectedIndexType = "all";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pemonth);
        Calendar startDate = Calendar.getInstance();
        startDate.set(1995,0,1);
        pvTime = new TimePickerView.Builder(PEMonthActivity.this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                monthTv.setText(new SimpleDateFormat("yyyy-MM").format(date));
            }
        }).setType(new boolean[]{true,true,false,false,false,false}).setRangDate(startDate, Calendar.getInstance()).setDate(Calendar.getInstance()).build();
        monthTv = findViewById(R.id.month_tv);
        monthTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvTime.show();
            }
        });

        Spinner spinner = findViewById(R.id.index_type);
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
                Intent intent = new Intent(PEMonthActivity.this,PEChartMonthActivity.class);
                intent.putExtra("month",monthTv.getText());
                intent.putExtra("index_type",selectedIndexType);
                startActivity(intent);
            }
        });
    }
}
