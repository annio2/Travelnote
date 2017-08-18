package com.example.travelnote.http;

/**
 * Created by wuguanglin on 2017/8/8.
 */

public class RequestBody {
    private String pageNo;
    private String orderType;

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
}
