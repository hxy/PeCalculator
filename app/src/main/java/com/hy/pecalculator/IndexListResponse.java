package com.hy.pecalculator;

import java.util.List;

/**
 * Created by yue.huang
 * on 2020-04-17
 */
public class IndexListResponse {

    /**
     * request_id : 83c22120807511ea97c8c537914297101587105279828838
     * code : 0
     * msg :
     * data : {"fields":["con_code","weight"],"items":[["600837.SH",1.78],["000060.SZ",0.23],["600267.SH",0.1]],"has_more":false}
     */

    private String request_id;
    private int code;
    private String msg;
    private DataBean data;

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * fields : ["con_code","weight"]
         * items : [["600837.SH",1.78],["000060.SZ",0.23],["600267.SH",0.1]]
         * has_more : false
         */

        private boolean has_more;
        private List<String> fields;
        private List<List<String>> items;

        public boolean isHas_more() {
            return has_more;
        }

        public void setHas_more(boolean has_more) {
            this.has_more = has_more;
        }

        public List<String> getFields() {
            return fields;
        }

        public void setFields(List<String> fields) {
            this.fields = fields;
        }

        public List<List<String>> getItems() {
            return items;
        }

        public void setItems(List<List<String>> items) {
            this.items = items;
        }
    }
}
