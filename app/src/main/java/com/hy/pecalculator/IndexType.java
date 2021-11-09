package com.hy.pecalculator;

/**
 * Created by yue.huang
 * on 2020-04-20
 */
public enum IndexType {
    ALL("全市场","all"),HS300("沪深300","000300.SH"),ZZ500("中证500","000905.SH"),ZZ800("中证800","000906.SH"),ZZ1000("中证1000","000852.SH")
    ,ZZHB("中证环保","000827.SH"),ZZCM("中证传媒","399971.CSI"),ZZHL("中证红利","000922.CSI"),QZXF("全指消费","000990.SH"),QZYY("全指医药","000991.SH"),QZJR("全指金融","000992.SH"),QZXX("全指信息","000993.SH");

    public String value;
    public String type;
    IndexType(String t,String v){
         this.value = v;
         this.type = t;
    }

}
