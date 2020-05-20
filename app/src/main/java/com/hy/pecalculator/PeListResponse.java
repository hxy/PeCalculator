package com.hy.pecalculator;

import java.util.List;

/**
 * Created by yue.huang
 * on 2020-04-13
 */
public class PeListResponse {

    /**
     * request_id : 2d91acea7d4f11ea81acb18692e1ea211586758960863172
     * code : 0
     * msg :
     * data : {"fields":["ts_code","trade_date","pe","pb"],"items":[["300629.SZ","20170731",114.6848,9.3799],["300382.SZ","20170731",49.3727,5.5042],["600774.SH","20170731",249.1867,5.049]],"has_more":false}
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
         * fields : ["ts_code","trade_date","pe","pb"]
         * items : [["300629.SZ","20170731",114.6848,9.3799],["300382.SZ","20170731",49.3727,5.5042],["600774.SH","20170731",249.1867,5.049]]
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
