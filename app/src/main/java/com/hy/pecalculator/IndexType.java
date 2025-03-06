package com.hy.pecalculator;

/**
 * Created by yue.huang
 * on 2020-04-20
 */
public enum IndexType {
    ALL("全市场","all"),HS300("沪深300","hs300"),ZZ500("中证500","zhishu_000905"), SWHB("申万环保","sw_hb"), SWCM("申万传媒","sw_cm"),
    SZXF("上证消费","zhishu_000036"), SZYY("上证医药","zhishu_000037"), SZJR("上证金融","zhishu_000038"), SZXX("上证信息","zhishu_000039");

    public String value;
    public String type;
    IndexType(String t,String v){
         this.value = v;
         this.type = t;
    }

}
