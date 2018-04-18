package com.freeme.jsonparse.pojo;

import java.util.List;

public class Origin {
    private String from;
    private String to;
    private List<Trans_result> trans_result;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public List<Trans_result> getTrans_result() {
        return trans_result;
    }

    public void setTrans_result(List<Trans_result> trans_result) {
        this.trans_result = trans_result;
    }


}
