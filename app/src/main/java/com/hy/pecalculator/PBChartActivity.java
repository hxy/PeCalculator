package com.hy.pecalculator;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by yue.huang
 * on 2020-04-1！
 */
public class PBChartActivity extends Activity {
    private String[] targetMonths;
    private ArrayList<String> realDays = new ArrayList<>();
    private ArrayList peList = new ArrayList<Integer>();
    private LineChartView chartView;
    private ProgressBar progressBar;
    private TextView tvPrint;
    //成分股列表
    private StringBuilder indexListString = new StringBuilder();
    private String indexType = IndexType.ALL.value;
    private Map<String,Double> codeWightMap = new HashMap();
    private float maxPe = 0;
    private DecimalFormat df =new DecimalFormat("#.00");
    private boolean useOldData = true;
    private int chatMinNum = 15;
    private int chatMaxNum = 80;
    private int chatIntervalNum = 5;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        targetMonths = getIntent().getStringArrayListExtra("month_list").toArray(new String[]{});
        progressBar = findViewById(R.id.progress_bar);
        tvPrint = findViewById(R.id.tv_print);
        indexType = getIntent().getStringExtra("index_type");
        useOldData = getIntent().getBooleanExtra("use_old_data",true);
        if(IndexType.ZZHL.value.equals(indexType)){
            chatMinNum = 15;
            chatMaxNum = 25;
            chatIntervalNum = 1;
        }else if(IndexType.QZJR.value.equals(indexType)) {
            chatMinNum = 5;
        }
        start(indexType);
    }

    private void start(final String type){
        new Thread(new Runnable() {
            @Override
            public void run() {
                requestAndCalculator(type);
            }
        }).start();

    }

    private void requestAndCalculator(String type){
        if(type.equals(IndexType.ALL.value)){
            for(String month:targetMonths){
               requestPBAndCalculator(month);
            }
        }else {
            for(String month:targetMonths){
                requestIndexCompositionList(type,month);
                if(indexListString.toString().isEmpty()){Log.d("yue.huang","跳过:"+month);continue;}
                else {
                    requestPBAndCalculator(month);}
            }
        }
        fullDataToChartAndShow();
    }


    /**
     * 获取特定指数的成分股和权重
     * @param code 指数代码
     */
    private void requestIndexCompositionList(String code,String day){
        if(code == null){return;}
        try {
            Thread.sleep(6000);
            OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
            MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("api_name", "index_weight");
            jsonObject.put("token", "38b16e9e9a798416bcf0946e9fa36aa77281c3adbcfdc33f1d2cddb8");
            JSONObject params = new JSONObject();
            String startDay = day.substring(0,day.length()-2)+"01";
            params.put("start_date", startDay);
            params.put("end_date", day);
            params.put("index_code",code);
            jsonObject.put("params", params);
            JSONArray fields = new JSONArray();
            fields.put("con_code");
            fields.put("weight");
            jsonObject.put("fields", fields);
            RequestBody requestBody = RequestBody.create(mediaType, jsonObject.toString());
            Request request = new Request.Builder()
                    .url("http://api.waditu.com")
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();//发送请求获取返回数据
            String responseData = response.body().string();//处理返回的数据
            IndexListResponse indexListResponse = com.alibaba.fastjson.JSONObject.parseObject(responseData, IndexListResponse.class);
            if (indexListResponse.getData() ==null || indexListResponse.getData().getItems().size() == 0) {
                if (useOldData){
                    Log.d("yue.huang",day+":当月指数成分数据为空，使用上月数据");
                }else {
                    Log.d("yue.huang",day+":当月指数成分数据为空，跳过");
                    indexListString.delete(0,indexListString.length());
                    codeWightMap.clear();
                }
            }else {
                indexListString.delete(0,indexListString.length());
                codeWightMap.clear();
                for (List<String> item:indexListResponse.getData().getItems()){
                    codeWightMap.put(item.get(0),Double.parseDouble(item.get(1)));
                    indexListString.append(item.get(0));
                }
            }
            Log.d("yue.huang",day+":"+indexListString.toString());
        }catch (Exception e){
            e.printStackTrace();
            Log.d("yue.huang",e.toString());
        }
    }

    private void requestPBAndCalculator(final String day){
        if(day == null){return;}
        try {
            Thread.sleep(500);
            OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
            MediaType mediaType = MediaType.parse("application/json");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("api_name", "daily_basic");
            jsonObject.put("token", "38b16e9e9a798416bcf0946e9fa36aa77281c3adbcfdc33f1d2cddb8");
            JSONObject params = new JSONObject();
            params.put("trade_date", day);
            jsonObject.put("params", params);
            JSONArray fields = new JSONArray();
            fields.put("pb");
            fields.put("ts_code");
            jsonObject.put("fields", fields);
            RequestBody requestBody = RequestBody.create(mediaType, jsonObject.toString());
            Request request = new Request.Builder()
                    .url("http://api.waditu.com")
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();//发送请求获取返回数据
            String responseData = response.body().string();//处理返回的数据
            PeListResponse peListResponse = com.alibaba.fastjson.JSONObject.parseObject(responseData, PeListResponse.class);
            float averagePb = calculatorAveragePb(peListResponse.getData().getItems());
            if (averagePb == 0) {
                //如果当天没有数据则去请求前一天的数据
                Log.d("yue.huang",day+":当天pb数据为空，获取前一天数据");
                requestPBAndCalculator(moveForwardOneDay(day));
            } else {
                realDays.add(day);
                peList.add(averagePb);
                if(averagePb>maxPe){
                    maxPe = averagePb;
                }
                tvPrint.post(new Runnable() {
                    @Override
                    public void run() {
                        tvPrint.setText(day);
                    }
                });
                Log.d("yue.huang", day + ":averagePb:" + averagePb);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private float calculatorAveragePb(List<List<String>> list){
        if(list.size() == 0){return 0;}
        int size = 0;
        float total = 0;
        if(!indexListString.toString().isEmpty()){
            for (List<String> item : list){
                if(indexListString.toString().contains(item.get(0)) && item.get(1)!=null && Float.parseFloat(item.get(1))<13){
                    size++;
                    total+=Float.parseFloat(item.get(1));
                }
            }
        }else {
            for (List<String> item : list){
                if(item.get(1)!=null && Float.parseFloat(item.get(1))<13){
                    size++;
                    total+=Float.parseFloat(item.get(1));
                }
            }
        }
        return total/size;
    }


    /**
     * 获取入参日期前一天
     * @param currentDay
     * @return
     */
    private String moveForwardOneDay(String currentDay){
        try {
            SimpleDateFormat sdfYMD = new SimpleDateFormat("yyyyMMdd");
            Date date = sdfYMD.parse(currentDay);
            Calendar instance = Calendar.getInstance();
            instance.setTime(date);
            instance.add(Calendar.DAY_OF_MONTH, -1);//本月最后一天
            return sdfYMD.format(instance.getTime());
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    private void fullDataToChartAndShow(){
        //线
        List<Line> lines = new ArrayList<Line>();
        Line line = new Line();
        line.setColor(Color.parseColor("#008577"));//设置折线的颜色
        line.setShape(ValueShape.CIRCLE);//节点图形样式 DIAMOND菱形、SQUARE方形、CIRCLE圆形
        line.setFilled(false);//是否填充曲线的面积
        line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        line.setPointRadius(2);//设置节点半径
        line.setStrokeWidth(2);
        line.setPointColor(Color.parseColor("#FF8F59"));//设置节点颜色
        List<PointValue> values = new ArrayList<PointValue>();
        for (int j = 0; j< peList.size(); ++j) {
            values.add(new PointValue(j, (float)peList.get(j)));//添加数据点
        }
        line.setValues(values);
        lines.add(line);
        //图表数据
        LineChartData data = new LineChartData(lines);
        //y轴
        Axis axisY = new Axis().setHasLines(true);
        List<AxisValue> axisYValues = new ArrayList<AxisValue>();
        for(int m = chatMinNum;m<chatMaxNum;m+=chatIntervalNum){
            axisYValues.add(new AxisValue(m/10f).setLabel(""+m/10f));
        }
        axisY.setValues(axisYValues);
        //x轴
        Axis axisX = new Axis();
        axisX.setHasTiltedLabels(true);//X坐标轴字体是斜的显示还是直的，true是斜的显示
        //axisX.setTextColor(Color.BLACK);//设置字体颜色
        axisX.setName(" ");//坐标轴名称
        axisX.setHasLines(true);//x 轴分割线
        axisX.setTextSize(12);//设置字体大小
        //axisX.setMaxLabelChars(2);//x轴每隔几个元素显示一个
        List<AxisValue> axisXValues = new ArrayList<AxisValue>();
        for (int i = 0; i<realDays.size();i++){
            axisXValues.add(new AxisValue(i).setLabel(realDays.get(i)));
        }
        axisX.setValues(axisXValues); //填充X轴的坐标名称
        //设置图表数据
        data.setAxisXBottom(axisX);//x 轴在底部
        data.setAxisYLeft(axisY);//Y轴设置在左边
        data.setBaseValue(Float.NEGATIVE_INFINITY);

        //设置图表属性
        //设置行为属性，支持缩放、滑动以及平移
        chartView = findViewById(R.id.chart);
        chartView.setLineChartData(data);
        chartView.setInteractive(true);
        chartView.setZoomType(ZoomType.HORIZONTAL);
        chartView.setMaxZoom((float) targetMonths.length);//最大方法比例
        chartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        chartView.setValueSelectionEnabled(true);//设置节点点击后动画
        Viewport v = new Viewport(chartView.getMaximumViewport());
        v.bottom = chatMinNum/10f;
        v.top = maxPe;
        //固定Y轴的范围,如果没有这个,Y轴的范围会根据数据的最大值和最小值决定,
        chartView.setMaximumViewport(v);
        //这2个属性的设置一定要在lineChart.setMaximumViewport(v);这个方法之后,不然显示的坐标数据是不能左右滑动查看更多数据的
        v.left = 0f;
        v.right = targetMonths.length;
        chartView.setCurrentViewport(v);
        chartView.post(new Runnable() {
            @Override
            public void run() {
                chartView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                tvPrint.setVisibility(View.GONE);
            }
        });
    }
}
