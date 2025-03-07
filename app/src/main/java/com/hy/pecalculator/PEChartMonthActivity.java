package com.hy.pecalculator;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yue.huang
 * on 2020-04-15
 * 计算某一月内数据
 * 接口地址：https://www.mairui.club/hsdata
 * licence微信支付交易单号：4200002593202503071646671367
 */
public class PEChartMonthActivity extends Activity {

    private String targetMonth;
    private ArrayList<String> realDays = new ArrayList<>();
    private ArrayList peList = new ArrayList<Integer>();
    private LineChartView chartView;
    private ProgressBar progressBar;
    private TextView tvPrint;
    //成分股列表
    private String indexType = IndexType.ALL.value;
    private float maxPe = 0;
    private float minPe = 0;
    private int chatMinNum = 15;
    private int chatMaxNum = 200;
    private int chatIntervalNum = 5;

    private static final String TEST_LICENCE = "b997d4403688d5e66a";//测试licence，不限时不限次数，请求接口的数据不全
    private static final String REAL_LICENCE = "B5EFEAC7-076D-44D2-B211-503B3E37DE0F";//真正licence,共200w次，每分钟20次
    private static final String FREE_LICENCE = "110CC0CE-4FFF-4F3B-BE85-AEDA71F2CAC6";//测试licence，每天50次，超过不给数据
    private static int MAX_PE = 200;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        initAdapt();
        targetMonth = getIntent().getStringExtra("month");
        progressBar = findViewById(R.id.progress_bar);
        tvPrint = findViewById(R.id.tv_print);
        indexType = getIntent().getStringExtra("index_type");
        if (IndexType.SWCM.value.equals(indexType)) {
            chatMinNum = 40;
            chatMaxNum = 240;
            chatIntervalNum = 10;
        } else if (IndexType.ZZ500.value.equals(indexType)) {
            chatMinNum = 4;
            chatMaxNum = 20;
            chatIntervalNum = 2;
        } else if (IndexType.SWHB.value.equals(indexType)) {
            chatMinNum = 20;
            chatMaxNum = 130;
            chatIntervalNum = 10;
        } else if (IndexType.SZYY.value.equals(indexType)) {
            chatMinNum = 10;
            chatMaxNum = 80;
        } else if (IndexType.SZJR.value.equals(indexType)) {
            chatMinNum = 5;
            chatMaxNum = 35;
        } else if (IndexType.SZXX.value.equals(indexType)) {
            chatMinNum = 5;
            chatMaxNum = 50;
        }
        start(indexType);
    }

    private void start(final String type){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //请求所有股票列表
                if(type.equals(IndexType.ALL.value)){
                    getAllGPList();
                }else {
                    getIndexGPList(type);
                }
                //请求所有股票在计算每月内每天的收盘价格
                getAllGPDailyPrice(targetMonth+"-01",getLastDayInMonth(targetMonth));
                //请求所有股票在计算时间段内年报每股收益
                getProfitData(targetMonth+"-01",getLastDayInMonth(targetMonth));
                //计算pe
                startCalculatorAveragePe();
                //显示表格
                fullDataToChartAndShow();

            }
        }).start();
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
            values.add(new PointValue(j, (int)peList.get(j)));//添加数据点
        }
        line.setValues(values);
        lines.add(line);
        //图表数据
        LineChartData data = new LineChartData(lines);
        //y轴
        Axis axisY = new Axis().setHasLines(true);
        List<AxisValue> axisYValues = new ArrayList<AxisValue>();
        for(int m = chatMinNum;m<chatMaxNum;m+=chatIntervalNum){
            axisYValues.add(new AxisValue(m).setLabel(""+m));
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
        chartView.setMaxZoom((float) realDays.size());//最大方法比例
        chartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        chartView.setValueSelectionEnabled(true);//设置节点点击后动画
        Viewport v = new Viewport(chartView.getMaximumViewport());
        v.bottom = chatMinNum;
        v.top = maxPe;
        //固定Y轴的范围,如果没有这个,Y轴的范围会根据数据的最大值和最小值决定,
        chartView.setMaximumViewport(v);
        //这2个属性的设置一定要在lineChart.setMaximumViewport(v);这个方法之后,不然显示的坐标数据是不能左右滑动查看更多数据的
        v.left = 0f;
        v.right = realDays.size();
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


    private List<GpBean> allGPList;
    /**
     * 获取沪深所有股票列表
     */
    public void getAllGPList(){
        try {
            OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
            Request request = new Request.Builder()
                    .url("http://api.mairui.club/hslt/list/"+REAL_LICENCE)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();//发送请求获取返回数据
            String responseData = response.body().string();//处理返回的数据
            Type type = new TypeToken<List<GpBean>>(){}.getType();
            allGPList = new Gson().fromJson(responseData,type);
            print("获取所有股票列表:"+allGPList.size());
            Log.d("yue.huang","获取所有股票列表:"+allGPList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取指数所有股票列表
     */
    public void getIndexGPList(String indexType){
        try {
            OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
            Request request = new Request.Builder()
                    .url("http://api.mairui.club/hszg/gg/"+indexType+"/"+REAL_LICENCE)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();//发送请求获取返回数据
            String responseData = response.body().string();//处理返回的数据
            Type type = new TypeToken<List<GpBean>>(){}.getType();
            allGPList = new Gson().fromJson(responseData,type);
            print("获取指数"+indexType+"股票列表:"+allGPList.size());
            Log.d("yue.huang","获取指数"+indexType+"股票列表:"+allGPList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存每个股票从起始日期到结束日期的每天的价格
     */
    private List<GpDailyPriceListBean> gpDailyPriceListBeanList = new ArrayList<>();
    public void getAllGPDailyPrice(String startDay, String endDay){
        for(GpBean gpBean : allGPList){
            //循环请求每个股票，在时间段内每月底的价格
            List<GpDailyPriceListBean.DailyPriceBean> dailyPriceBeanList = getGPDailyPrice(gpBean.dm,startDay,endDay);
            if(dailyPriceBeanList!=null){
                //保存起来
                GpDailyPriceListBean allMonthPriceListBean = new GpDailyPriceListBean();
                allMonthPriceListBean.code = gpBean.dm;
                allMonthPriceListBean.name = gpBean.mc;
                allMonthPriceListBean.priceBeanList.addAll(dailyPriceBeanList);
                gpDailyPriceListBeanList.add(allMonthPriceListBean);
                if(dailyPriceBeanList.size()>realDays.size()){
                    realDays.clear();
                    //将真实的当月的交易日期保存起来
                    for (GpDailyPriceListBean.DailyPriceBean priceBean:dailyPriceBeanList){
                        realDays.add(priceBean.d);
                    }
                }
                print("请求股票"+startDay+"月每天价格集合："+gpBean.dm+","+gpBean.mc+",共"+dailyPriceBeanList.size()+"天");
                Log.d("yue.huang","请求股票"+startDay+"月每天价格集合："+gpBean.dm+","+gpBean.mc+",共"+dailyPriceBeanList.size()+"天");
            }
        }
    }

    private List<GpDailyPriceListBean.DailyPriceBean> getGPDailyPrice(String code, String startDay, String endDay){
        try {
            //一分钟300次限制
            Thread.sleep(250);
            OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
            Request request = new Request.Builder()
                    .url("http://api.mairui.club/hszbc/fsjy/"+code+"/dq/"+startDay+"/"+endDay+"/"+REAL_LICENCE)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();//发送请求获取返回数据
            String responseData = response.body().string();//处理返回的数据
            Type type = new TypeToken<List<GpDailyPriceListBean.DailyPriceBean>>(){}.getType();
            List<GpDailyPriceListBean.DailyPriceBean> dailyPriceBeanList = new Gson().fromJson(responseData,type);
            return dailyPriceBeanList;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("yue.huang","获取股票每天价格集合出错:code"+code+",startDay:"+startDay+",endDay:"+endDay);
            print("获取股票每天价格集合出错:code"+code+",startDay:"+startDay+",endDay:"+endDay);
            return null;
        }
    }


    /**
     * 保存每年每季度股票从起始日期到结束日期的盈利情况，用里面的获取每股收益
     */
    private List<GpProfitBean> gpProfitBeanList = new ArrayList<>();
    /**
     * 获取时间段内每季度盈利情况
     * @param startDay
     * @param endDay
     */
    private void getProfitData(String startDay,String endDay){
        String[] starts = startDay.split("-");
        String[] ends = endDay.split("-");
        int startYear = Integer.parseInt(starts[0]);
        int endYear = Integer.parseInt(ends[0]);
        //多获取一年的年报数据
        startYear -= 1;
        while (startYear<endYear){
            String log = "获取时间段内每年年度盈利情况";
            List<GpProfitBean.Profit> profitSeasonFour = getProfitDataReal(startYear,4);
            if(profitSeasonFour!=null){
                log += "，"+startYear+"，年报"+profitSeasonFour.size();
                GpProfitBean gpProfitBean = new GpProfitBean();
                gpProfitBean.year = startYear;
                gpProfitBean.profitList.addAll(profitSeasonFour);
                gpProfitBeanList.add(gpProfitBean);
                print(log);
                Log.d("yue.huang",log);
            }
            startYear++;
        }
    }


    /**
     * 获取某年某季度的所有股票盈利情况
     * @param year
     * @param season
     * @return
     */
    private List<GpProfitBean.Profit> getProfitDataReal(int year,int season){
        try {
            //一分钟20次限制
            Thread.sleep(3050);
            OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
            Request request = new Request.Builder()
                    .url("http://api.mairui.club/hicw/yl/"+year+"/"+season+"/"+REAL_LICENCE)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();//发送请求获取返回数据
            String responseData = response.body().string();//处理返回的数据
            Type type = new TypeToken<List<GpProfitBean.Profit>>(){}.getType();
            List<GpProfitBean.Profit> gpProfitBeanList = new Gson().fromJson(responseData,type);
            return gpProfitBeanList;
        } catch (Exception e) {
            print("获取股票股票盈利接口出错，year："+year);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 开始计算查询时间段内每天所有股票的平均pe
     */
    private void startCalculatorAveragePe(){
        print("计算pe中。。。");
        peList.clear();
        for (String day :realDays){
            String log1="计算当天股票平均pe："+day;
            //循环计算范围内的每一天
            int totalPe=0;
            int totalSize=0;
            for (GpDailyPriceListBean dailyPriceListBean : gpDailyPriceListBeanList){
                double price = 0;//收盘价
                double mgsy = 10000;//每股收益，设置一个特殊的初始值用来区分一般情况
                String log="计算每个股票pe："+day+",code:"+dailyPriceListBean.code+",name:"+dailyPriceListBean.name;
                //循环所有有价格数据的股票
                //------下面获取股票当天的收盘价-------
                for (GpDailyPriceListBean.DailyPriceBean dailyPriceBean : dailyPriceListBean.priceBeanList){
                    //循环查找当天的价格
                    if(day.equals(dailyPriceBean.d)){
                        //获取收盘价
                        price = dailyPriceBean.c;
                        log+=",price:"+price;
                        break;
                    }
                }
                if(price==0){
                    //这个股票没计算这天的价格，跳过
                    log+=",此股票没价格数据，跳过";
                    Log.d("yue.huang",log);
                    continue;
                }

                //-----下面获取股票上一年报的每股收益------
                String[] days = day.split("-");
                int year = Integer.parseInt(days[0]);
                for (GpProfitBean gpProfitBean : gpProfitBeanList){
                    //查找对应年份和季度的盈利数据
                    if((year-1) == gpProfitBean.year){
                        for (GpProfitBean.Profit profit:gpProfitBean.profitList){
                            //遍历盈利列表，找到对应股票数据
                            if(profit.dm.equals(dailyPriceListBean.code)){
                                mgsy = profit.mgsy;
                                log+=",mgsy:"+mgsy;
                                break;
                            }
                        }
                        if(mgsy==10000){
                            log+="，上年年报没有数据,不统计该股票数据";
                        }
                        break;
                    }
                }

                if(mgsy!=10000){
                    //每股收益不是初始值才算数
                    int pe=0;
                    if(mgsy > 0){
                        pe = (int)(price/mgsy);
                        log+=",真实pe:"+pe;
                    }else {
                        //处理每股收益等于0和小于0的情况，设置为最大pe处理
                        pe = MAX_PE;
                    }
                    if(pe>=MAX_PE){
                        log+=",pe太大，改为最大值";
                        pe=MAX_PE;
                    }
                    log+=",最后pe:"+pe;
                    totalPe+=pe;
                    totalSize++;
                }
                Log.d("yue.huang",log);
            }
            if(totalSize==0){
                //这一天所有股票都没有数据，跳过
                continue;
            }
            int averagePe = totalPe/totalSize;
            log1+=",averagePe:"+averagePe;
            peList.add(averagePe);
            if(averagePe>maxPe){
                maxPe = averagePe;
            }
            if(minPe>averagePe){
                minPe = averagePe;
            }
            Log.d("yue.huang",log1);
        }
    }

    /**
     * 获取任意月中最后一天
     * @param month yyyy-MM格式的月份
     */
    private String getLastDayInMonth(String month){
        try {
            SimpleDateFormat sdfYM = new SimpleDateFormat("yyyy-MM");
            SimpleDateFormat sdfYMD = new SimpleDateFormat("yyyy-MM-dd");
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

    private void print(final String msg){
        tvPrint.post(new Runnable() {
            @Override
            public void run() {
                tvPrint.setText(msg);
            }
        });
    }



    private void initAdapt(){
        final LinearLayout layoutAdapt = findViewById(R.id.layout_adapt);
        final EditText maxEt = findViewById(R.id.max_et);
        final EditText minEt = findViewById(R.id.min_et);
        final EditText invEt = findViewById(R.id.inv_et);
        TextView updateBtn = findViewById(R.id.update_btn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int max = Integer.parseInt(maxEt.getText().toString());
                int min = Integer.parseInt(minEt.getText().toString());
                int inv = Integer.parseInt(invEt.getText().toString());
                chatMaxNum = max;
                chatMinNum = min;
                chatIntervalNum = inv;
                fullDataToChartAndShow();
            }
        });

        final EditText maxpeEt = findViewById(R.id.maxpe_et);
        findViewById(R.id.recalculate_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MAX_PE = Integer.parseInt(maxpeEt.getText().toString());
                startCalculatorAveragePe();
                fullDataToChartAndShow();
            }
        });

        findViewById(R.id.adapt_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layoutAdapt.getVisibility()!=View.VISIBLE){
                    layoutAdapt.setVisibility(View.VISIBLE);
                }else {
                    layoutAdapt.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
