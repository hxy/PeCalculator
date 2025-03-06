package com.hy.pecalculator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hy on 2025-02-28
 * Describe:每年、每季度的盈利数据
 */
public class GpProfitBean {
    public int year;
    public List<Profit> seasonOne = new ArrayList<>();
    public List<Profit> seasonTwo = new ArrayList<>();
    public List<Profit> seasonThree = new ArrayList<>();
    public List<Profit> seasonFour = new ArrayList<>();



    public static class Profit{
        public String dm;
        public String mc;
        public double jzcsy;
        public double jll;
        public double mll;
        public double jlr;
        public double mgsy;
        public double yysr;
        public double mgzysr;
        public int y;
        public int q;
        public String yq;
    }
}
