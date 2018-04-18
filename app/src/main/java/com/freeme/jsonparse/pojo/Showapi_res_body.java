package com.freeme.jsonparse.pojo;

import java.util.List;

public class Showapi_res_body {
    private int allNum;

    private int allPages;

    private List<Contentlist> contentlist;

    private int currentPage;

    private int maxResult;

    private int ret_code;

    public int getAllNum() {
        return allNum;
    }

    public void setAllNum(int allNum) {
        this.allNum = allNum;
    }

    public int getAllPages() {
        return allPages;
    }

    public void setAllPages(int allPages) {
        this.allPages = allPages;
    }

    public List<Contentlist> getContentlist() {
        return contentlist;
    }

    public void setContentlist(List<Contentlist> contentlist) {
        this.contentlist = contentlist;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getMaxResult() {
        return maxResult;
    }

    public void setMaxResult(int maxResult) {
        this.maxResult = maxResult;
    }

    public int getRet_code() {
        return ret_code;
    }

    public void setRet_code(int ret_code) {
        this.ret_code = ret_code;
    }


}
