package com.hy.pecalculator;

/**
 * Created by yue.huang
 * on 2020-04-20
 */
public enum IndexType {
    ALL("ALL","all"),HS300("HS300","399300.SZ"),ZZ500("ZZ500","000905.SH"),ZZ800("ZZ800","000906.SH"),ZZ1000("ZZ1000","000852.SH");

    public String value;
    public String type;
    IndexType(String t,String v){
         this.value = v;
         this.type = t;
    }

}
