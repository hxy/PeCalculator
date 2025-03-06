package com.hy.pecalculator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hy on 2025-02-28
 * Describe:每个股票在一段时间内所有的价格数据
 */
public class GpDailyPriceListBean {
    /**
     * 股票代码
     */
    public String code;
    /**
     * 股票名称
     */
    public String name;
    /**
     * 价格列表
     */
    public List<DailyPriceBean> priceBeanList = new ArrayList<>();

    /**
     * 股票某一天的价格数据
     */
    public static class DailyPriceBean {
        public String d;
        public double o;
        public double h;
        public double l;
        public double c;
        public int v;
        public double e;
        public double zf;
        public double hs;
        public double zd;
        public double zde;
        public String ud;
    }
}
